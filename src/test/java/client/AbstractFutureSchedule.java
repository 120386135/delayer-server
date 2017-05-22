package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by zp on 17/5/18.
 */
public abstract class AbstractFutureSchedule {
    DelayService service = new DelayService();
    protected final String topic = "timer";
    protected final String group = "timer-server";
    protected final String registerTag = "register";
    protected final String notifyTag = "notify";

    private static final Logger logger = LoggerFactory.getLogger(RocketMqSchedule.class);


    public abstract boolean register(long futureTime,byte[] body);

    public abstract void start();

    protected boolean handleNotify(Stub stub){
        try {
            Method method = DelayService.class.getDeclaredMethod(stub.getMethod(),String.class);
            return (boolean)method.invoke(service,stub.getParam());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
