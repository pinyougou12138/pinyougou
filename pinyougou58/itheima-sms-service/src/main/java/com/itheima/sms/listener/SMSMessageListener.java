package com.itheima.sms.listener;

import com.alibaba.fastjson.JSON;
import com.itheima.sms.util.SmsUtil;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Map;

public class SMSMessageListener implements MessageListenerConcurrently{
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            if (list!=null&&list.size()>0) {
                for (MessageExt messageExt : list) {
                    String s = new String(messageExt.getBody());
                    Map<String,String> map = JSON.parseObject(s, Map.class);
                    SmsUtil.sendSms(map);
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
