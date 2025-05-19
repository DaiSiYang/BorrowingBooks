package com.example.borrowingbooks.utils;

public class SnowflakeUtil {

    // 起始时间戳（随便定，保持固定即可）
    private static final long START_TIMESTAMP = 1704067200000L; // 2024-01-01

    private static final long DATA_CENTER_ID_BITS = 5L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private long dataCenterId;
    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static final SnowflakeUtil INSTANCE = new SnowflakeUtil(1, 1); // 可传自定义值

    public static long nextId() {
        return INSTANCE.generateId();
    }

    private SnowflakeUtil(long dataCenterId, long workerId) {
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0)
            throw new IllegalArgumentException("dataCenterId不合法");
        if (workerId > MAX_WORKER_ID || workerId < 0)
            throw new IllegalArgumentException("workerId不合法");
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    private synchronized long generateId() {
        long current = System.currentTimeMillis();
        if (current < lastTimestamp) throw new RuntimeException("时钟回拨！");

        if (current == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                current = waitNextMillis(current);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = current;

        return ((current - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long current) {
        while (current <= lastTimestamp) {
            current = System.currentTimeMillis();
        }
        return current;
    }
}
