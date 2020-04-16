package mining_pool.reward_scheme;

import miner.MinerRecord;
import mining_pool.Share;

import java.util.ArrayList;

public class PPLNSRewardScheme extends BaseRewardScheme {
    private long n;
    
    private ArrayList<String> last_n_shares = new ArrayList<String>();

    public PPLNSRewardScheme(long n) {
        this.n = n;
    }

    public String getSchemeName() {
        return "PPLNS";
    }

    public void insertShare(String miner_address) {
        last_n_shares.add(miner_address);
        while (last_n_shares.size() > n) {
            last_n_shares.remove(last_n_shares.size()-1);
        }
    }

    public void handleShare(String miner_address, Share share, long total_shares_per_round) {
        MinerRecord miner_record = findRecord(miner_address);
        updateRecord(miner_record, share);
        shares_per_block++;
        insertShare(miner_address);

        if (!share.isValid_block()) {
            return;
        }
        block_meta_data.put("pool_luck", getPoolLuck());
        block_meta_data.put("shares_per_block", shares_per_block);

        if (!share.isUncle()) {
            for (String address : last_n_shares) {
                MinerRecord record = findRecord(address);
                record.incBlocks_received((1.0/last_n_shares.size()));
                record.resetShares_per_round();
            }
            shares_per_block = 0;
            return;
        }
        handleUncle(miner_address);
    }

    public void updateRecord(MinerRecord record, Share share) {
        record.incShares_count();
        record.incShares_per_round();
        if (share.isNetworkShare()) {
            record.incBlocks_mined();
        } else if (share.isUncle()) {
            record.incUncles_mined();
        }

    }

    public void handleUncle(String miner_address) {
        for (String address : last_n_shares) {
            MinerRecord record = findRecord(address);
            record.incUncle_received(1.0/last_n_shares.size());
        }
    }
}
