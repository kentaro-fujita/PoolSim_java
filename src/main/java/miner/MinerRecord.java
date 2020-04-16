package miner;

public class MinerRecord {
    private String miner_address;
    private double hashrate = 0.0;
    private long blocks_mined = 0;
    private long uncles_mined = 0;
    private long shares_count = 0;
    private long shares_per_round =0;
    private double blocks_received = 0.0;
    private double uncles_received = 0.0;
    private double total_reward = 0.0;
    private double reward_per_round = 0.0;

    public MinerRecord(String miner_address) {
        this.miner_address = miner_address;
    }

    public void setHashrate(double hashrate) {
        this.hashrate = hashrate;
    }

    public void incBlocks_mined() {
        this.blocks_mined++;
    }

    public void incBlocks_received() {
        this.blocks_received++;
    }

    public void incBlocks_received(double blocks) {
        this.blocks_received += blocks;
    }

    public void incUncles_mined() {
        this.uncles_mined++;
    }

    public void incUncle_received() {
        this.uncles_received++;
    }

    public void incUncle_received(double uncles) {
        this.uncles_received += uncles;
    }

    public void incShares_count() {
        this.shares_count++;
    }

    public void incShares_per_round() {
        this.shares_per_round++;
    }

    // public void setShares_per_round(long shares_per_round) {
    //     this.shares_per_round = shares_per_round;
    // }
    
    public void resetShares_per_round() {
        this.shares_per_round = 0;
    }

    public void incTotal_reward(double reward) {
        this.total_reward += reward;
    }

    public void setReward_per_round(double reward) {
        this.reward_per_round = reward;
    }

    public String getMiner_address() {
        return this.miner_address;
    }

    public double getHashrate() {
        return this.hashrate;
    }

    public long getShares_per_round() {
        return this.shares_per_round;
    }

    public double getUncles_received() {
        return this.uncles_received;
    }

    public long getUncles_mined() {
        return this.uncles_mined;
    }

    public long getBlocks_mined() {
        return this.blocks_mined;
    }

    public double getBlocks_received() {
        return this.blocks_received;
    }

    public long getShares_count() {
        return this.shares_count;
    }

    public double getTotal_reward() {
        return this.total_reward;
    }

    public double getReward_per_round() {
        return this.reward_per_round;
    }
}
