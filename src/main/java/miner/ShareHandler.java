package miner;

import miner.share_handler.BWHShareHandler;
import miner.share_handler.DefaultShareHandler;
import mining_pool.Share;

public class ShareHandler {
    private String handler_type;
    private DefaultShareHandler default_share_handler;
    private BWHShareHandler bwh_share_handler;

    public void create(String handler_type) {
        this.handler_type = handler_type;

        if (this.handler_type.equals("default")) {
            default_share_handler = new DefaultShareHandler();
        } else if (this.handler_type.equals("withholding")) {
            bwh_share_handler = new BWHShareHandler();
        }
    }

    public String getName() {
        return this.handler_type;
    }

    public void setMiner(Miner miner) {
        if (this.handler_type.equals("default")) {
            default_share_handler.setMiner(miner);
        } else if (this.handler_type.equals("withholding")) {
            bwh_share_handler.setMiner(miner);
        }
    }

    public void handleShare(Share share) {
        if (this.handler_type.equals("default")) {
            default_share_handler.handleShare(share);
        } else if (this.handler_type.equals("withholding_share")) {
            bwh_share_handler.handleShare(share);
        }
    }
}
