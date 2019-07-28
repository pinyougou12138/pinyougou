package com.pinyougou.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbOrder;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import entity.BigOrder;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService extends CoreService<TbOrder> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder Order);

	List<BigOrder> findOrderByTime(String startTime, String endTime);
}