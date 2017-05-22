package client;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Created by xiaofengxu on 17/5/18.
 */
public class RocketMqSchedule extends AbstractFutureSchedule{
    private static final Logger logger = LoggerFactory.getLogger(RocketMqSchedule.class);
    private DefaultMQProducer producer;
    private static final String nameAddr = "192.168.1.100:9876";
    private static final String ServerGroup = "java_server";
    @Override
    public boolean register(long futureTime, byte[] body) {
        try {
            if(producer==null){
                producer = new DefaultMQProducer(ServerGroup);
                producer.setNamesrvAddr(nameAddr);
                producer.start();
            }
            Message msg = new Message(topic,registerTag,body);
            msg.putUserProperty("time", String.valueOf(futureTime));
            SendResult sendResult = producer.send(msg);
            return sendResult.getSendStatus()== SendStatus.SEND_OK;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void start() {
        try {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(ServerGroup);
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.setNamesrvAddr(nameAddr);
            consumer.setMessageModel(MessageModel.CLUSTERING);
            consumer.subscribe(topic,notifyTag);
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    for(MessageExt m:msgs){
                        try {
                            handleNotify(Stub.fromBytes(m.getBody()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
        } catch (MQClientException e) {
            logger.error(e.getMessage());
        }
    }
}
