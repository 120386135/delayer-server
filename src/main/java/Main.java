
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by zp on 17/5/15.
 */
public class Main {
    public static void main(String[] args){
        String log4jConfPath = System.getProperty("user.dir")+"/src/main/resources/log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
        AbstractSchedule schedule = new RocketMqSchedule();
        schedule.setStoreService(new LevelDBService());
        schedule.listen();
        schedule.schedule();
        System.out.println("Timer Server Start!");
    }
}
