package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/order")
public class OrderController {

	@Reference
	private OrderService orderService;


	/**
	 * 订单的查询
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
    @RequestMapping("/findBySellerId")
	public PageInfo<TbOrder> findBySellerId(
			@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize

	){


		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();


		return orderService.findBySellerId(pageNo,pageSize,sellerId);

	}

	/**
	 * 查询订单 指定时间 统计
	 * @param pageNo
	 * @param pageSize
	 * @param time1
	 * @param time2
	 * @return
	 */
	@RequestMapping("/findByTime")
	public PageInfo<TbOrder> findBySellerId(
			@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
			@RequestParam(value = "time1") String time1, @RequestParam(value = "time2") String time2

	){





		return orderService.findByTime(pageNo,pageSize,time1,time2);

	}

	/**
	 * 四u该发货信息
	 * @param ids
	 * @return
	 */
	@RequestMapping("/shipments")
	public Result shipments(@RequestBody Long[] ids){

		try {
			orderService.shipments(ids);
			return new Result(true,"成功");
		} catch (Exception e) {
			return new Result(false,"失败");
		}

	}
/*wql*/
	@RequestMapping("/findxl")
	public Map findxl( @RequestParam(value = "startTime") String startTime,@RequestParam(value = "endTime") String endTime) {

		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();



          //两个数据 一个是日期 一个 是销售额
		//{"time":[2017-08-09,2017-08-10],"money":[3,4]}
			Map map = orderService.findxl(startTime,endTime,sellerId);


              return map;

	}
	@RequestMapping("/kk")
	public BigDecimal kk(
			@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
			@RequestParam(value = "startTime") String startTime,@RequestParam(value = "endTime") String endTime,@RequestParam(value = "id") Long id

	){


		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(sellerId);

		System.out.println(endTime+"---"+startTime+"----"+id);


	return 	null;



	}

}
