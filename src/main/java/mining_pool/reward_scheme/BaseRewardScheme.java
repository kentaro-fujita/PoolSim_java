package mining_pool.reward_scheme;

import miner.MinerRecord;
import mining_pool.MiningPool;

import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BaseRewardScheme {
    private double pool_fee;
    private MiningPool mining_pool;

    protected long shares_per_block = 0;
    protected HashMap<String, MinerRecord> records = new HashMap<String, MinerRecord>();

    private ObjectMapper mapper = new ObjectMapper();
    protected ObjectNode block_meta_data = mapper.createObjectNode();

    public void setPool_fee(double pool_fee) {
        this.pool_fee = pool_fee;
    }

    public void setMiningPool(MiningPool mining_pool) {
        this.mining_pool = mining_pool;
    }

    public double getPool_fee() {
        return this.pool_fee;
    }

    public MiningPool getMiningPool() {
        return this.mining_pool;
    }

    public HashMap<String, MinerRecord> getRecords() {
        return this.records;
    }

    public MinerRecord findRecord(String miner_address) {
        MinerRecord record = new MinerRecord(miner_address);
        if (this.records.containsKey(miner_address)) {
            record = this.records.get(miner_address);
        } else {
            this.records.put(miner_address, record);
        }
        return record;
    }

    public double getPoolLuck() {
        if (shares_per_block == 0) {
            return 0.0;
        }
        MiningPool pool = getMiningPool();
        long network_difficulty = getMiningPool().getNetwork().getDifficulty();
        double exp_shares_per_block = (double)network_difficulty/pool.getDifficulty();
        double pool_luck = (exp_shares_per_block / shares_per_block) * 100.0;

        return pool_luck;
    }

    public ObjectNode getMeta_data() {
        return block_meta_data;
    }

    public ObjectNode get_miner_metadata(String miner_address) {
        ObjectNode json = mapper.createObjectNode();
        MinerRecord record = findRecord(miner_address);
        json = mapper.valueToTree(record);
        
        return json;
    }
}
