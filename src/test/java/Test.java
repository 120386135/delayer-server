import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by zp on 17/5/15.
 */

public class Test {
   static LevelDBService helper = new LevelDBService();
    private static final Logger logger = LoggerFactory.getLogger(Test.class);
    public static void main(String[] args){
        String log4jConfPath = System.getProperty("user.dir")+"/src/main/resources/log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
        helper.put(1000L,"test1".getBytes());
        helper.put(1000L,"test2".getBytes());
        helper.put(1000L,"test3".getBytes());
        List<Map.Entry<byte[],byte[]>> values = helper.findNeedNotify(1000L);
        for(Map.Entry<byte[],byte[]> entry:values){
            logger.error(BinaryUtil.toString(entry.getValue()));
        }
        while (true);
    }
}
