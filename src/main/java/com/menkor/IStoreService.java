package com.menkor;

import java.util.List;
import java.util.Map;

/**
 * Created by zp on 17/5/18.
 */
public interface IStoreService {
    /**
     * save the future task
     * @param time
     * @param task
     */
    void put(long time,byte[] task);

    /**
     * save batch
     * @param times
     * @param tasks
     */
    void putBatch(long[] times,byte[][] tasks);

    /**
     * find task where the future time < now
     * @param now
     * @return
     */
    List<Map.Entry<byte[], byte[]>> findNeedNotify(long now);

    /**
     * delete key when notify the consumer
     * @param key
     */
    void delete(byte[] key);

}
