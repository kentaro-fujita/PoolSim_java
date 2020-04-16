package miner.share_handler;

import mining_pool.Share;

public class BWHShareHandler extends DefaultShareHandler {
    @Override
    public String getName() {
        return "withholding_share";
    }

    @Override
    public void handleShare(Share share) {
        if (!share.isValid_block()) {
            getPool().submitShare(getAddress(), share);
        }
    }
}
