package com.menkor;

import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.*;
import org.slf4j.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zp on 17/5/16.
 */

public class LevelDBService implements IStoreService {
    private static DB db;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LevelDBService.class);
    static {
        DBFactory factory = JniDBFactory.factory;
        File file = new File("timer");
        Options options = new Options().createIfMissing(true);
        options.comparator(new DBComparator() {
            public String name() {
                return "timer";
            }

            public byte[] findShortestSeparator(byte[] bytes, byte[] bytes1) {
                return bytes;
            }

            public byte[] findShortSuccessor(byte[] bytes) {
                return bytes;
            }

            public int compare(byte[] o1, byte[] o2) {//desc
                long res = BinaryUtil.toLongArray(o1)[0]- BinaryUtil.toLongArray(o2)[0];//future time
                if(res>0){
                    return -1;
                }
                if(res<0){
                    return 1;
                }
                long res1 = BinaryUtil.toLongArray(o1)[1]- BinaryUtil.toLongArray(o2)[1];//prevent repeat code
                if(res1>0){
                    return 1;
                }
                if(res1<0){
                    return -1;
                }
                return 0;
            }
        });
        try {
            db = factory.open(file, options);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


    @Override
    public void put(long time,byte[] task){
        long[] key = {time,System.nanoTime()};// prevent repeat future time
        db.put(BinaryUtil.toBytes(key), task);
    }

    @Override
    public void putBatch(long[] times,byte[][] tasks){
        if(times.length==0){
            return;
        }
        long now  = System.nanoTime();
        WriteBatch batch = db.createWriteBatch();
        for(int i=0;i<times.length;i++){
            long[] key = {times[i],now+i};
            batch.put(BinaryUtil.toBytes(key), tasks[i]);
        }
        db.write(batch);
        try {
            batch.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


    @Override
    public  List<Map.Entry<byte[], byte[]>> findNeedNotify(long now){
        DBIterator iterator = db.iterator();
        Map.Entry<byte[], byte[]> entry;
        List<Map.Entry<byte[], byte[]>> bytes = new ArrayList<>();
        long[] position = {now,0};
        iterator.seek(BinaryUtil.getBytes(position));
        while (iterator.hasNext()){
            entry = iterator.next();
            bytes.add(entry);
        }
        return bytes;
    }

    @Override
    public void delete(byte[] key) {
        db.delete(key);
    }

    public void close(){
        try {
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
