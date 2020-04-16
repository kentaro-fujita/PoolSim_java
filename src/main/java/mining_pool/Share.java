package mining_pool;

public class Share {
    private long properties;

    public static class Property {
        public static int none = 0;
        public static int valid_block = 1 << 0;
        public static int uncle = 1 << 1;
    }

    public Share(long properties) {
        this.properties = properties;
    }

    public long getProperties() {
        return this.properties;
    }

    public boolean isValid_block() {
        return (this.properties & Property.valid_block) != 0;
    }

    public boolean isUncle() {
        return (this.properties & Property.uncle) != 0;
    }

    public boolean isNetworkShare() {
        return isValid_block();
    }


}