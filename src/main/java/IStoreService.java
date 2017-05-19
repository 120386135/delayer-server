import java.util.List;
import java.util.Map;

/**
 * Created by zp on 17/5/18.
 */
public interface IStoreService {

    void put(long time,byte[] params);

    void putBatch(long[] times,byte[][] params);

    List<Map.Entry<byte[], byte[]>> findNeedNotify(long now);

    void delete(byte[] key);

}
