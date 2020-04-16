package mining_pool;

import event.BlockEvent;
import random.MyRandom;
import network.Network;

import java.util.ArrayList;


public class MiningPool {
    private String pool_name;
    private long difficulty;
    private Network network;
    private MyRandom random;

    private double uncle_prob = 0;

    private ArrayList<String> miners = new ArrayList<String>();
    private RewardScheme reward_scheme = new RewardScheme();
    private long total_shares_per_round = 0;
    private long blocks_mined = 0;

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
}
