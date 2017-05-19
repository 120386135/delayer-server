import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zp on 17/5/18.
 */
public abstract class AbstractSchedule {

    protected IStoreService storeService;
    protected final String topic = "timer";
    protected final String group = "timer-server";
    protected final String consumeTag = "register";
    protected final String produceTag = "notify";


    public abstract void send(Map.Entry<byte[], byte[]> entry);

    public abstract void listen();

    public void schedule(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<Map.Entry<byte[], byte[]>> values = storeService.findNeedNotify(System.currentTimeMillis());
                for(int i=values.size()-1;i>=0;i--){
                    send(values.get(i));
                }
            }
        },0,1000);
    }

    public void setStoreService(IStoreService storeService) {
        this.storeService = storeService;
    }
}
