package com.menkor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zp on 17/5/15.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws Exception{
        AbstractSchedule schedule = new RocketMqSchedule();
        schedule.setStoreService(new LevelDBService());
        schedule.listen();
        schedule.schedule();
        logger.info("Timer server start");
    }
}
