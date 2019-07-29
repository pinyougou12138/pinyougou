package com.pinyougou.pay.service;

import com.pinyougou.pojo.TbSeckillOrder;

import java.util.Map;

public interface AliPayService {




    /***
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    Map<String,String> queryPayStatus(String out_trade_no);

    /***
     * 关闭订单
     * @param out_trade_no
     * @return
     */
    Map closePay(String out_trade_no);


    /**
     * 生成阿里支付
     * @param secKillOrder
     * @return
     */
    Map<String, String> goAliPay(TbSeckillOrder secKillOrder);
}
