package network;

import java.util.ArrayList;

import mining_pool.MiningPool;

public class Network {
    private long difficulty;
    private long current_time;
    private long current_block;
    private ArrayList<MiningPool> pools = new ArrayList<MiningPool>();

    public void registerPool(MiningPool pool) {
        this.pools.add(pool);
    }

    public long getDifficulty() {
        return this.difficulty;
    }

    public long getCurrent_time() {
        return this.current_time;
    }

    public long getCurrent_block() {
        return this.current_block;
    }

    public void setDifficulty(long difficulty) {
        this.difficulty=difficulty;
    }

    public void setCurrent_time(long current_time) {
        this.current_time = current_time;
    }

    public void setCurrent_block(long current_block) {
        this.current_block = current_block;
    }

    public void incCurrent_block() {
        this.current_block++;
    }
}
