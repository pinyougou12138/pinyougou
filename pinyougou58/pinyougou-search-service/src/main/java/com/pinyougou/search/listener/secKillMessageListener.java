package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.search.service.impl.SecKillGoodsSearchServiceImpl;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class secKillMessageListener implements MessageListenerConcurrently {

    @Autowired
    private SecKillGoodsSearchServiceImpl secKillGoodsSearchService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            if (list != null && list.size() > 0) {
                for (MessageExt messageExt : list) {
                    //String s = JSON.toJSONString(messageExt.getBody());
                    byte[] body = messageExt.getBody();
                    String s = new String(body);
                    MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);
                    if (messageInfo != null) {
                        Long[] ids = JSON.parseObject(messageInfo.getContext().toString(), Long[].class);
                        int method = messageInfo.getMethod();
                        switch (method) {
                            case 1: {  //增加索引
                                secKillGoodsSearchService.updateIndex(ids);
                                break;
                            }
                            case 2: { //更新索引
                                secKillGoodsSearchService.updateIndex(ids);
                                break;
                            }
                            case 3: { //刪除索引
                                secKillGoodsSearchService.deleteByIds(ids);
                                break;
                            }
                            default: { //未定义
                                throw new RuntimeException("方法不存在");
                            }
                        }
                    }
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
