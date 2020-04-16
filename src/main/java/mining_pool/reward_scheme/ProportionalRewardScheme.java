package mining_pool.reward_scheme;

import miner.MinerRecord;
import mining_pool.Share;

public class ProportionalRewardScheme extends BaseRewardScheme {
    public String getSchemeName() {
        return "proportional";
    }

    public void handleShare(String miner_address, Share share, long total_shares_per_round) {
        MinerRecord record = findRecord(miner_address);
        updateRecord(record, share);

        if (!share.isValid_block()) {
            return;
        }
        
        if (!share.isUncle()) {
            record.incBlocks_mined();
            for (String address : getMiningPool().getMiners()) {
                MinerRecord miner_record = findRecord(address);
                double reward = 12.5*(1-getPool_fee())*miner_record.getShares_per_round()/shares_per_block;
                miner_record.setReward_per_round(reward);
                miner_record.incTotal_reward(reward);
                miner_record.resetShares_per_round();
            }
            return;
        }
        handleUncle(miner_address);
        record.incUncles_mined();
    }

    public void handleUncle(String miner_address) {

    }

    public void updateRecord(MinerRecord record, Share share) {
        shares_per_block++;
        record.incShares_count();
        record.incShares_per_round();
        long network_difficulty = getMiningPool().getNetwork().getDifficulty();
        double p = getMiningPool().getDifficulty()/(double)network_difficulty;
        record.incBlocks_received((1-getPool_fee())*p);
    }
}