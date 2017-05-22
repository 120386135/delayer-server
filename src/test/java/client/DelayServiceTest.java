package client;

import org.junit.Test;

/**
 * Created by zp on 17/5/18.
 */
public class DelayServiceTest {
    DelayService service = new DelayService();
    @Test
    public void delay() {
        int i =100;
        while (true){
            service.registerDelayAnnouncement(i++, System.currentTimeMillis()+20*1000);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
