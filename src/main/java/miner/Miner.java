package miner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import miner.share_handler.DefaultShareHandler;
import network.Network;
import mining_pool.MiningPool;
import mining_pool.Share;

public class Miner {
    private String address;
    private double hashrate;
    private Network network;

    private long blocks_found = 0;
    private long total_work = 0;
    private MiningPool pool;
    private ShareHandler share_handler = new ShareHandler();

    private ObjectMapper mapper = new ObjectMapper();
    private ObjectNode miner_meta_data = mapper.createObjectNode();

    public Miner(String address, double hash_rate, Network network) {
        this.address = address;
        this.hashrate = hash_rate;
        this.network = network;
        this.share_handler.create("default");
        this.share_handler.setMiner(this);
    }

    public void setWithholdingShare(){
        this.share_handler.create("withholding");
        this.share_handler.setMiner(this);
    }

    public String getAddress() {
        return this.address;
    }

    public double getHashrate() {
        return this.hashrate;
    }

    public MiningPool getPool() {
        return pool;
    }

    public Network getNetwork() {
        return this.network;
    }

    public long getBlocks_found() {
        return this.blocks_found;
    }

    public void joinPool(MiningPool pool) {
        if (getPool() != null) {
            getPool().leave(getAddress());
        }

        this.pool = pool;
        getPool().join(getAddress());
    }

    public void processShare(Share share) {
        this.total_work += getPool().getDifficulty();
        if (share.isNetworkShare()) {
            this.blocks_found++;
        }
        this.share_handler.handleShare(share);
    }

    public String getName() {
        return "default";
    }

    public ObjectNode getMetaData() {
        miner_meta_data.put("address", getAddress());
        miner_meta_data.put("behavior", getName());
        miner_meta_data.put("hashrate", getHashrate());
        miner_meta_data.put("blocks_found", blocks_found);
        miner_meta_data.put("total_work", total_work);
        miner_meta_data.put("handler_meta_data", share_handler.getName());
        
        return miner_meta_data;
    }
}
