package org.example.constants;

public final class AppConstants {

    // Prevent instantiation
    private AppConstants() {
        throw new UnsupportedOperationException("Cannot instantiate constants class");
    }

    public static final long TimeToLive = 1000;
    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "3";
    public static final int RETRY_ATTEMPTS = 3;
    public static final long RETRY_DELAY = 2000;
    public static final double RETRY_DELAY_MULTIPLIER = 2;
    public static final String KAFKA_TOPIC = "my_topic";
    public static final String KAFKA_GROUP_ID = "group_id";


}
