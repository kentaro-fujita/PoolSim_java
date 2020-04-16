package miner.share_handler;

import miner.Miner;
import mining_pool.MiningPool;
import mining_pool.Share;
import network.Network;

public class DefaultShareHandler {
    private Miner miner;

    public String getName() {
        return "default";
    }

    public void setMiner(Miner miner) {
        this.miner = miner;
    }

    public Miner getMiner() {
        return this.miner;
    }

    public Network getNetwork() {
        return getMiner().getNetwork();
    }

    public MiningPool getPool() {
        return getMiner().getPool();
    }

    public String getAddress() {
        return getMiner().getAddress();
    }

    public void handleShare(Share share) {
        getPool().submitShare(getAddress(), share);
    }
}
