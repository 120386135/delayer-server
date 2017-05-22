package com.menkor;

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
import java.util.Map;

/**
 * Created by zp on 17/5/18.
 */
public class RocketMqSchedule extends AbstractSchedule {
    private static final Logger logger = LoggerFactory.getLogger(RocketMqSchedule.class);
    private static final String addr = "192.168.1.100:9876";

    @Override
    public  void listen()  {
        try {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.setNamesrvAddr(addr);
            consumer.setMessageModel(MessageModel.CLUSTERING);
            consumer.subscribe(topic,consumeTag);
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    long[] times = new long[msgs.size()];
                    byte[][] params = new byte[msgs.size()][];
                    int i=0;
                    for(MessageExt msg:msgs){
                        String tmp = msg.getProperty("time");
                        if(tmp==null){
                            continue;
                        }
                        long time = Long.parseLong(tmp);
                        times[i] = time;
                        params[i] = msg.getBody();
                        i++;
                    }
                    storeService.putBatch(times,params);
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
        } catch (MQClientException e) {
            logger.error(e.getMessage());
        }
    }

    private DefaultMQProducer producer;
    @Override
    public  void send(Map.Entry<byte[], byte[]> entry){
        try {
            if(producer==null){
                producer = new DefaultMQProducer(group);
                producer.setNamesrvAddr(addr);
                producer.start();
            }
            Message msg = new Message(topic, produceTag, entry.getValue());
            SendResult sendResult = producer.send(msg);
            if(sendResult.getSendStatus()== SendStatus.SEND_OK){//成功发送之后才删除,否则一直存储
                storeService.delete(entry.getKey());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }




}
