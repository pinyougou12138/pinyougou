package com.pinyougou.order.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbOrder;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbPayLog;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {

	/**
	 * 添加
	 * @param order
	 */
	void add(TbOrder order);

	/***
	 * redis中获取支付日志
	 * @param userId
	 * @return
	 */
    TbPayLog searchPayLogFromRedis(String userId);

	/**
	 * 更新订单状态
	 * @param out_trade_no
	 * @param transaction_id
	 */
	void updateOrderStatus(String out_trade_no, String transaction_id);

	/**wql
	 * 生成折线图的方法
	 * @param startTime
	 * @param endTime
	 * @param sellerId
	 * @return
	 */
	Map findxl(String startTime, String endTime, String sellerId);


	/** wql
	 * 点击订单管理 查询所有订单号
	 * @param pageNo
	 * @param pageSize
	 * @param sellerId
	 * @return
	 */
    PageInfo<TbOrder> findBySellerId(Integer pageNo, Integer pageSize, String sellerId);

	/**
	 * wql
	 * @param pageNo
	 * @param pageSize
	 * @param time1
	 * @param time2
	 * @return
	 */
	PageInfo<TbOrder> findByTime(Integer pageNo, Integer pageSize, String time1, String time2);

	/**wql
	 * 发货 时修改status
	 * @param ids
	 */
	void shipments(Long[] ids);
}
