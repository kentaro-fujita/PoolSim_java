package random;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Random;


public class MyRandom {
    private Random random = new Random();

    public void setSeed(long seed) {
        this.random.setSeed(seed);
    }

    public long randInt() {
        return this.random.nextInt();
    }

    public double randDouble() {
        return this.random.nextDouble();
    }

    public String getAddress() {
        String address = "";

        for (int i = 0; i < 40; i++) {
            int value = this.random.nextInt(15);
            address += Integer.toHexString(value);
        }

        return "0x" + address;
    }

    public double getGaussian(JsonNode params) {
        double value = this.random.nextGaussian();

        double mean = params.get("mean").doubleValue();
        double stddev = params.get("stddev").doubleValue();
        double maximum = params.get("maximum").doubleValue();
        double minimum = params.get("minimum").doubleValue();

        value = value * stddev + mean;

        if (value < minimum) {
            return minimum;
        } else if (value > maximum) {
            return maximum;
        } else {
            return value;
        }
    }
}
