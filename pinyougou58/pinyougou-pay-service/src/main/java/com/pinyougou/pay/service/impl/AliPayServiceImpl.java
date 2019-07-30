package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.pinyougou.common.util.AlipayConfig;
import com.pinyougou.pay.service.AliPayService;
import com.pinyougou.pojo.TbSeckillOrder;

import java.util.HashMap;
import java.util.Map;

@Service
public class AliPayServiceImpl implements AliPayService {
    @Override
    public Map<String, String> goAliPay(TbSeckillOrder secKillOrder) {
        try {
            //获得初始化的AlipayClient
            AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
            //设置请求参数
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setReturnUrl(AlipayConfig.return_url);
            alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
            //商户订单号，商户网站订单系统中唯一订单号，必填
            String out_trade_no = secKillOrder.getId()+"";
            //付款金额，必填
            String total_amount =  secKillOrder.getMoney()+"";
            //订单名称，必填
            String subject = "品优购商品";
            //商品描述，可空
            String body = "用户"+secKillOrder.getId()+"订购商品";
            // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
            String timeout_express = "1c";
            alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                    + "\"total_amount\":\""+ total_amount +"\","
                    + "\"subject\":\""+ subject +"\","
                    + "\"body\":\""+ body +"\","
                    + "\"timeout_express\":\""+ timeout_express +"\","
                    + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
            //请求
            String result = alipayClient.pageExecute(alipayRequest).getBody();
            //自定义返回前端结果
            Map<String, String> map = new HashMap<>();
            map.put("out_trade_no",out_trade_no);
            map.put("total_fee",total_amount);
            return map;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return new HashMap<>();
        }

    }

    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        return null;
    }

    @Override
    public Map closePay(String out_trade_no) {
        return null;
    }


}
