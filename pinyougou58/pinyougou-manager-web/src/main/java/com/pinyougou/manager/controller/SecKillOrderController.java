package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class SecKillOrderController {

    @Reference
    private SeckillOrderService secKillOrderService;

    /***
     * 分页查询
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("/findPage")
    public PageInfo<TbSeckillOrder> findPage(@RequestParam(value = "pageNo",defaultValue = "1",required = true) Integer pageNo ,
                                             @RequestParam(value = "pageSize",defaultValue = "10",required = true) Integer pageSize){
        PageInfo<TbSeckillOrder> info = secKillOrderService.findPage(pageNo, pageSize);
        return info;
    }

    /***
     * 分页查询
     * @param pageNo
     * @param pageSize
     * @param secKillOrder
     * @return
     */
    @RequestMapping("/search")
    public PageInfo<TbSeckillOrder> findPage(@RequestParam(value = "pageNo",defaultValue = "1",required = true)Integer pageNo,
                                             @RequestParam(value = "pageSize",defaultValue = "10",required = true)Integer pageSize,
                                             @RequestBody TbSeckillOrder secKillOrder){

        PageInfo<TbSeckillOrder> info = secKillOrderService.findPage(pageNo, pageSize, secKillOrder);
        return info;
    }
}
