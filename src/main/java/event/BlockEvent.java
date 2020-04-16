package event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BlockEvent {
    private double time;
    private boolean is_uncle;
    private String pool_name;
    private String miner_address;
    private ObjectNode reward_scheme_data;

    public double getTime() {
        return this.time;
    }

    public boolean getIs_uncle() {
        return this.is_uncle;
    }

    public String getPool_name() {
        return this.pool_name;
    }

    public String getMiner_address() {
        return this.miner_address;
    }

    public ObjectNode getReward_scheme_data() {
        return this.reward_scheme_data;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setIs_uncle(boolean is_uncle) {
        this.is_uncle = is_uncle;
    }

    public void setPool_name(String pool_name) {
        this.pool_name = pool_name;
    }

    public void setMiner_address(String miner_address) {
        this.miner_address = miner_address;
    }

    public void setReward_scheme_data(ObjectNode reward_scheme_data) {
        this.reward_scheme_data = reward_scheme_data;
    }

    public ObjectNode to_json() {
        ObjectNode json = new ObjectMapper().createObjectNode();
        json.put("time", time);
        json.put("is_uncle", is_uncle);
        json.put("pool_name", pool_name);
        json.put("miner_address", miner_address);
        json.set("reward_scheme_data", reward_scheme_data);

        return json;
    }
}
