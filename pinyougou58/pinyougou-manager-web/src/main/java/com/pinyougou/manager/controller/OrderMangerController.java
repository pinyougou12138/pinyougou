package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;


import com.pinyougou.pojo.TbOrder;
import com.pinyougou.sellergoods.service.OrderService;
import entity.BigOrder;
import entity.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author:Drever
 * @Date: 2019/7/24  下午 10:14
 * Created by:Prettydongdong@outlook.com
 */
@RestController
@RequestMapping("/orderManger")
public class OrderMangerController {

    @Reference
    private OrderService orderService;

    /**
     *查询某时间段，各个商品的销售额，根据订单表， 每个订单找到旗下对应的各个商品
     * @return
     */
    @RequestMapping("/findOderByTime")
        public List<BigOrder> findOrderByTime(@RequestParam(value = "startTime") String startTime, @RequestParam(value = "endTime") String endTime){
        return orderService.findOrderByTime(startTime,endTime);

    }

    @RequestMapping("/findAll")
    public List<TbOrder> findAll(){
        return orderService.findAll();
    }

    @RequestMapping("/findPage")
    public PageInfo<TbOrder> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return orderService.findPage(pageNo, pageSize);
    }

    /**
     *查询订单数量
     */


    /**
     * 修改
     * @param order
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbOrder order){
        try {
            orderService.update(order);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public TbOrder findOne(@PathVariable(value = "id") Long id){
        return orderService.findOne(id);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        try {
            orderService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    @RequestMapping("/search")
    public PageInfo<TbOrder> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbOrder order) {
        return orderService.findPage(pageNo, pageSize, order);
    }
}
