package mining_pool;

import event.BlockEvent;
import random.MyRandom;
import network.Network;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;


public class MiningPool {
    private String pool_name;
    private long difficulty;
    private Network network;
    private MyRandom random;

    private double uncle_prob = 0;

    private ArrayList<String> miners = new ArrayList<String>();
    private RewardScheme reward_scheme;
    private long total_shares_per_round = 0;
    private long blocks_mined = 0;

    private static ObjectMapper mapper = new ObjectMapper();

    public MiningPool(String name, long difficulty, Network network, MyRandom random) {
        this.pool_name = name;
        this.difficulty = difficulty;
        this.network = network;
        this.random = random;
    }

    public ArrayList<String> getMiners() {
        return this.miners;
    }

    public RewardScheme getRewardScheme() {
        return this.reward_scheme;
    }

    public void setRewardScheme(RewardScheme reward_scheme) {
        this.reward_scheme = reward_scheme;
    }

    public void join(String miner_address) {
        this.miners.add(miner_address);
    }

    public void leave(String miner_address) {
        this.miners.remove(miner_address);
    }

    public String getPool_name() {
        return this.pool_name;
    }

   public double getLuck() {
       return this.reward_scheme.getPoolLuck();
   }

    public long getDifficulty() {
        return this.difficulty;
    }

    public Network getNetwork() {
        return this.network;
    }

    public void incTotal_shares_per_round() {
        this.total_shares_per_round++;
    }

    public void resetTotal_shares_per_round() {
        this.total_shares_per_round = 0;
    }

    public void submitShare(String miner_address, Share submitted_share) {
        Share share = submitted_share;
        if (share.isValid_block() & this.random.randDouble() < this.uncle_prob) {
            share = new Share(share.getProperties() | Share.Property.uncle);
        }

        if (share.isNetworkShare()) {
            this.blocks_mined++;
        }

        this.incTotal_shares_per_round();
        this.reward_scheme.handleShare(miner_address, share, this.total_shares_per_round);

        if (share.isValid_block()) {
            resetTotal_shares_per_round();
        }
    }

    public ArrayNode get_miners_metadata() {
        ArrayNode result = mapper.createArrayNode();
        for (String address : miners) {
            ObjectNode miner = mapper.createObjectNode();
            miner.put("address", address);
            miner.put("metadata", this.reward_scheme.get_miner_metadata(address));
            result.add(miner);
        }
        return result;
    }

    public ObjectNode getMetaData() {
        ObjectNode pool_meta_data = mapper.createObjectNode();
        pool_meta_data.put("name", getPool_name());
        pool_meta_data.put("difficulty", getDifficulty());
        pool_meta_data.put("reward_scheme", this.reward_scheme.getSchemeName());
        pool_meta_data.put("miners", get_miners_metadata());
        return pool_meta_data;
    }
}
