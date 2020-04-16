package mining_pool;

import miner.MinerRecord;
import mining_pool.reward_scheme.PPLNSRewardScheme;
import mining_pool.reward_scheme.PPSRewardScheme;
import mining_pool.reward_scheme.ProportionalRewardScheme;

import java.util.HashMap;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class RewardScheme {
    private String scheme_type;
    private ProportionalRewardScheme pp_reward_scheme;
    private PPSRewardScheme pps_reward_scheme;
    private PPLNSRewardScheme pplns_reward_scheme;

    public void create(String scheme_type, long param, MiningPool mining_pool, double pool_fee) {
        this.scheme_type = scheme_type;

        if (this.scheme_type.equals("proportional")) {
            this.pp_reward_scheme = new ProportionalRewardScheme();
            this.pp_reward_scheme.setMiningPool(mining_pool);
            this.pp_reward_scheme.setPool_fee(pool_fee);
        } else if (this.scheme_type.equals("pps")) {
            this.pps_reward_scheme = new PPSRewardScheme();
            this.pps_reward_scheme.setMiningPool(mining_pool);
            this.pps_reward_scheme.setPool_fee(pool_fee);
        } else if (this.scheme_type.equals("pplns")) {
            this.pplns_reward_scheme = new PPLNSRewardScheme(param);
            this.pplns_reward_scheme.setMiningPool(mining_pool);
            this.pplns_reward_scheme.setPool_fee(pool_fee);
        }
    }

    public void handleShare(String miner_address, Share share, long total_shares_per_round) {
        if (this.scheme_type.equals("proportional")) {
            this.pp_reward_scheme.handleShare(miner_address, share, total_shares_per_round);
        } else if (this.scheme_type.equals("pps")) {
            this.pps_reward_scheme.handleShare(miner_address, share, total_shares_per_round);
        } else if (this.scheme_type.equals("pplns")) {
            this.pplns_reward_scheme.handleShare(miner_address, share, total_shares_per_round);
        }
    }

    public HashMap<String, MinerRecord> getRecords() {
        if (this.scheme_type.equals("proportional")) {
            return this.pp_reward_scheme.getRecords();
        } else if (this.scheme_type.equals("pps")) {
            return this.pps_reward_scheme.getRecords();
        } else if (this.scheme_type.equals("pplns")) {
            return this.pplns_reward_scheme.getRecords();
        }

        return null;
    }

    public double getPoolLuck() {
        if (this.scheme_type.equals("pps")) {
            return this.pps_reward_scheme.getPoolLuck();
        } else if (this.scheme_type.equals("pplns")) {
             return this.pplns_reward_scheme.getPoolLuck();
        }

        return 0.0;
    }

    public ObjectNode getMetaData() {
        if (this.scheme_type.equals("pps")) {
            return this.pps_reward_scheme.getMeta_data();
        } else if (this.scheme_type.equals("pplns")) {
             return this.pplns_reward_scheme.getMeta_data();
        }

        return null;
    }
}
