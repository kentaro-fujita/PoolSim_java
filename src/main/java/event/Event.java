package event;

public class Event {
    private String miner_address;
    private double time;

    public Event(String miner_address, double time) {
        this.miner_address = miner_address;
        this.time = time;
    }

    public double getTime() {
        return this.time;
    }

    public String getMiner_address() {
        return this.miner_address;
    }
}
