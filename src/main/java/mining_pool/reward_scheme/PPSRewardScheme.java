package mining_pool.reward_scheme;

import miner.MinerRecord;
import mining_pool.Share;

public class PPSRewardScheme extends BaseRewardScheme {
    private double reward_per_round = 0.0;

    public String getSchemeName() {
        return "PPS";
    }

    public void handleShare(String miner_address, Share share, long total_shares_per_round) {
        shares_per_block++;
        MinerRecord record = findRecord(miner_address);
        updateRecord(record, share);

        if (!share.isValid_block()) {
            return;
        }

        block_meta_data.put("pool_luck", getPoolLuck());
        block_meta_data.put("shares_per_block", shares_per_block);

        if (!share.isUncle()) {
            record.incBlocks_mined();
            for (MinerRecord miner_record : records.values()) {
                double reward_per_round = 12.5 * (1-getPool_fee()) * miner_record.getShares_per_round() / shares_per_block;
                miner_record.setReward_per_round(reward_per_round);
                miner_record.incTotal_reward(reward_per_round);
                miner_record.resetShares_per_round();
            }
            shares_per_block = 0;
            return;
        }
        handleUncle(miner_address);
        record.incUncles_mined();
    }

    public void handleUncle(String miner_address) {

    }

    public void updateRecord(MinerRecord record, Share share) {
        record.incShares_count();
        record.incShares_per_round();
        long network_difficulty = getMiningPool().getNetwork().getDifficulty();
        double p = getMiningPool().getDifficulty()/(double)network_difficulty;
        record.incBlocks_received((1-getPool_fee())*p);
    }
}
