package simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.String;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import event.BlockEvent;
import event.Event;
import event.EventQueue;
import miner.Miner;
import miner.MinerRecord;
import mining_pool.MiningPool;
import mining_pool.RewardScheme;
import mining_pool.Share;
import network.Network;
import random.MyRandom;

public class Simulator {
    private static HashMap<String, Miner> miners = new HashMap<String, Miner>();
    private static ArrayList<MiningPool> pools = new ArrayList<MiningPool>();
    private static Network network = new Network();
    private static MyRandom random = new MyRandom();

    private static long blocks;
    private static EventQueue queue = new EventQueue();
    private static ArrayList<BlockEvent> block_events = new ArrayList<BlockEvent>();

    private static ObjectMapper mapper = new ObjectMapper();
    private static Logger logger = LogManager.getLogger(Simulator.class);

    public static String initialize() {
        // mapper setting
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        // load config from config.json
        String config_json = readJsonAsString("dist/conf/config.json");
        JsonNode simulation_config = loadConfigFromJson(config_json);

        for (int i = 0; i < simulation_config.get("pools").size(); i++) {
            JsonNode pool_config = simulation_config.get("pools").get(i);
            
            long pool_difficulty = pool_config.get("difficulty").longValue();
            if (pool_difficulty == 0) {
                throw new IllegalArgumentException("pool difficulty should br greater than 0");
            }
            
            // Get or generate pool name
            String pool_name = null;
            try {
                pool_name = pool_config.get("name").toString();
            } catch (Exception e) {
                pool_name = "pool-" + (i+1);
            }

            // Create pool and initialize pool
            MiningPool mining_pool = new MiningPool(pool_name, pool_difficulty, network, random);

            // Create all the miners in the configuration
            HashMap<String, Miner> pool_miners = setMiners(pool_config.get("miners"));

            // Create pool reward scheme
            String reward_scheme_type = pool_config.get("reward_scheme").get("type").toString().replaceAll("\"","");
            double pool_fee =  pool_config.get("reward_scheme").get("params").get("pool_fee").doubleValue();
            RewardScheme reward_scheme = new RewardScheme();
            reward_scheme.create(reward_scheme_type, 10, mining_pool, pool_fee);
            mining_pool.setRewardScheme(reward_scheme);
            addPool(mining_pool);

            // add all miners to pool and simulator
            for (Miner miner : pool_miners.values()) {
                miner.joinPool(mining_pool);
                addMiner(miner);
            }
        }

        String result_filename = simulation_config.get("output").toString().replace("\"", "");
        return result_filename;
    }

    public static void run() {
        String result_filename = initialize();

        logger.debug("loaded {} pools with a total of {} miners", pools.size(), miners.size());

        if (pools.isEmpty() || miners.isEmpty()) {
            throw new IllegalArgumentException("simulation must have at one miner and one pool");
        }

        scheduleAll();

        logger.info("running {} blocks", blocks);

        while (network.getCurrent_block() < blocks) {
            Event event = queue.pop();
            processEvent(event);
        }

        saveSimulatorData(result_filename);
    }

    public static HashMap<String, Miner> setMiners(JsonNode miner_config) {
        HashMap<String, Miner> pool_miners = new HashMap<String, Miner>();

        JsonNode miner_behavior = miner_config.get("behavior");
        boolean isWithholding = false;
        double behavior_rate = 0;
        String behavior_type = miner_behavior.get("name").toString().replaceAll("\"","");
        if (behavior_type.equals("withholding")) {
            isWithholding = true;
            behavior_rate = miner_behavior.get("params").get("rate").doubleValue();
        }

        JsonNode hashrate_config = miner_config.get("hashrate");
        JsonNode hashrate_params = hashrate_config.get("params");

        double total_population = 0;
        double total_hashrate = 0;

        JsonNode stop_condition = miner_config.get("stop_condition");
        String stop_type = stop_condition.get("type").toString().replaceAll("\"","");
        double stop_value = stop_condition.get("params").get("value").doubleValue();


        while (true) {
            String miner_address = random.getAddress();
            double miner_hashrate = random.getGaussian(hashrate_params);

            Miner miner = new Miner(miner_address, miner_hashrate, network);
            if (isWithholding) {
                if (random.randDouble() < behavior_rate) {
                    miner.setWithholdingShare();
                }
            }
            pool_miners.put(miner_address, miner);

            if (stop_type.equals("total_population")) {
                if (total_population >= stop_value - 1 ) {
                    break;
                }
            }

            if (stop_type.equals("total_hashrate")) {
                if (total_hashrate >= stop_value) {
                    break;
                }
            }

            total_hashrate += miner_hashrate;
            total_population += 1;
        }

        return pool_miners;
    }

    public static void addMiner(Miner miner) {
        miners.put(miner.getAddress(), miner);
    }

    public static Miner getMiner(String miner_address) {
        return miners.get(miner_address);
    }

    public static void addPool(MiningPool pool) {
        pools.add(pool);
    }

    public static void scheduleAll() {
        for (Miner miner : miners.values()) {
            scheduleMiner(miner);
        }
    }

    public static void processEvent(Event event) {
        network.setCurrent_time((long)event.getTime());
        Miner miner = getMiner(event.getMiner_address());

        MiningPool pool = miner.getPool();
        double p = (double) pool.getDifficulty()/network.getDifficulty();
        boolean isNetworkShare = random.randDouble() < p;
        long share_flags = Share.Property.none;
        if (isNetworkShare) {
            network.incCurrent_block();
            long currentBlock = network.getCurrent_block();
            if (currentBlock % 10000 == 0) {
                logger.info("progress : {} / {}", currentBlock, blocks);
            } 
            else if (currentBlock % 100 == 0) {
                logger.debug("progress : {} / {}", currentBlock, blocks);
            }
            share_flags |= Share.Property.valid_block;
        }
        Share share = new Share(share_flags);
        scheduleMiner(miner);
        miner.processShare(share);
        if (isNetworkShare) {
            process(miner, pool);
        }
    }

    public static void scheduleMiner(Miner miner) {
        MiningPool pool = miner.getPool();
        double lambda = miner.getHashrate() / pool.getDifficulty();
        double t = -Math.log(random.randDouble()) / lambda;

        Event minerNextEvent = new Event(miner.getAddress(), network.getCurrent_time() + t);
        queue.schedule(minerNextEvent);
    }

    public static void process(Miner miner, MiningPool pool) {
        BlockEvent block_event = new BlockEvent();
        block_event.setTime(network.getCurrent_time());
        block_event.setIs_uncle(false);
        block_event.setMiner_address(miner.getAddress());
        block_event.setPool_name(pool.getPool_name());
        block_event.setReward_scheme_data(pool.getRewardScheme().getMetaData().deepCopy());
        block_events.add(block_event);
    }

    private static JsonNode loadConfigFromJson(String config_json) {
        if (config_json != null) {
            try {
                JsonNode root = mapper.readTree(config_json);

                blocks = root.get("blocks").longValue();
                network.setDifficulty(root.get("network_difficulty").longValue());
                random.setSeed(root.get("seed").longValue());
                logger.debug("initialized random with seed {}", root.get("seed").toString());

                return root;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String readJsonAsString(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void saveSimulatorData(String filename) {
        ObjectNode result_json = mapper.createObjectNode();

        try {
            FileWriter fw = new FileWriter("dist/output/" + filename);
            ArrayNode block_data = mapper.valueToTree(block_events);

            ArrayList<ObjectNode> miner_records = new ArrayList<ObjectNode>();
            for (Miner miner : miners.values()) {
                miner_records.add(miner.getMetaData());
            }
            ArrayNode miner_data = mapper.valueToTree(miner_records);
            ArrayNode pool_data = mapper.valueToTree(pools);

            try {
                result_json.set("blocks", block_data);
                result_json.set("miners", miner_data);
                result_json.set("pools", pool_data);

                fw.write(mapper.writeValueAsString(result_json));
            } catch (Exception e) {
                e.printStackTrace();
            }

            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}