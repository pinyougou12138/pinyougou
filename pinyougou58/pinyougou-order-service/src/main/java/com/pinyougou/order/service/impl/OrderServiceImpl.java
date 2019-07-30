package com.pinyougou.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbPayLogMapper payLogMapper;

	/**
	 * 添加
	 * @param order
	 */
	@Override
	public void add(TbOrder order) {
		//获取购物车数据
		List<Cart> redisCarList = (List<Cart>) redisTemplate.boundHashOps("redisCarList").get(order.getUserId());
		//支付日志参数
		double total_fee = 0;
		List<String> orderList = new ArrayList<>();
		//循环 cart--orderItemList
		for (Cart cart : redisCarList) {
			//设置订单id
			long orderId = new IdWorker(0, 1).nextId();
			orderList.add(orderId+"");

			//补全order的信息
			TbOrder newOrder = new TbOrder();
			newOrder.setOrderId(orderId);
			newOrder.setUserId(order.getUserId());
			newOrder.setPaymentType(order.getPaymentType());
			newOrder.setStatus("1"); //未支付
			newOrder.setSourceType("2"); //订单来源pc
			newOrder.setCreateTime(new Date());
			newOrder.setUpdateTime(newOrder.getCreateTime());
			newOrder.setReceiverAreaName(order.getReceiverAreaName());
			newOrder.setReceiverMobile(order.getReceiverMobile());
			newOrder.setReceiver(order.getReceiver());
			newOrder.setSellerId(cart.getSellerId());

			//支付金额
			double payment = 0;
			List<TbOrderItem> orderItemList = cart.getOrderItemList();
			for (TbOrderItem orderItem : orderItemList) {
				//TbOrderItem插入数据库
				long id = new IdWorker(0, 1).nextId();
				orderItem.setId(id);
				orderItem.setOrderId(orderId);
				orderItemMapper.insert(orderItem);

				//payment += 每张订单的总金额
				payment+=orderItem.getTotalFee().doubleValue();
			}
			newOrder.setPayment(BigDecimal.valueOf(payment));
			//购物车的总金额
			total_fee+=payment;

			//order 表中添加数据
			orderMapper.insert(newOrder);
		}

		//创建支付日志
		TbPayLog payLog = new TbPayLog();
		long outTradeNo = new IdWorker(0, 1).nextId();
		payLog.setOutTradeNo(outTradeNo+"");
		payLog.setCreateTime(new Date());

		payLog.setTotalFee((long) (total_fee*100));
		payLog.setUserId(order.getUserId());
		payLog.setTradeState("0");
		payLog.setOrderList(orderList.toString().replace("[","").replace("]",""));
		payLog.setPayType("1");
		//插入支付日志
		payLogMapper.insert(payLog);
		//存入redis
		System.out.println("TbPayLog.class.getSimpleName():"+TbPayLog.class.getSimpleName());
		redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).put(order.getUserId(),payLog);

		//清空redis中的购物车
		redisTemplate.boundHashOps("redisCarList").delete(order.getUserId());
	}

	/***
	 * redis中取出支付日志
	 * @param userId
	 * @return
	 */
	@Override
	public TbPayLog searchPayLogFromRedis(String userId) {
		TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).get(userId);
		return payLog;
	}

	/***
	 * 更新订单状态
	 * @param out_trade_no
	 * @param transaction_id
	 */
	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		//获取支付日志中的商家订单号列表
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		//更新数据库支付日志的支付状态 支付时间 微信订单号
		payLog.setTradeState("1");
		payLog.setPayTime(new Date());
		payLog.setTransactionId(transaction_id);
		payLogMapper.updateByPrimaryKey(payLog);

		//更新商家订单的支付状态
		String orderIdListStr = payLog.getOrderList();
		String[] orderIdList = orderIdListStr.split(",");
		for (String orderId : orderIdList) {
			TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
			order.setPaymentTime(payLog.getPayTime());
			order.setStatus("2");
			orderMapper.updateByPrimaryKey(order);
		}
		//清空redis中的支付日志
		redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).delete(payLog.getUserId());
	}


	/**wql
	 * 生成折线图的方法
	 * @param startTime
	 * @param endTime
	 * @param sellerId
	 * @return
	 */
	@Override
	public Map findxl(String startTime, String endTime, String sellerId) {
		//{"time":[2017/08/09,2017/08/10],"money":[3,4]}

		System.out.println(startTime);
		System.out.println(endTime);

		//从起始时间开始递增 月数 到endTime为止
		//递增的数据 去数据库查询 数据
		//存入map中 返回前端渲染

		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date time1 = simpleDateFormat.parse(startTime);
			Date time2 = simpleDateFormat.parse(endTime);
			System.out.println(time1+"----"+time2);

			Map<String,Object> map =new HashMap<>();
			ArrayList<String> time = new ArrayList<>();
			ArrayList<String> qian = new ArrayList<>();
			while (true){
				Calendar c = Calendar.getInstance();
				c.setTime(time1);


				Date t = c.getTime();

				String s = simpleDateFormat.format(t);
				System.out.println("明天是:" + s);
				time1=t;
				c.setTime(time2);
				Date tt = c.getTime();
				String ss = simpleDateFormat.format(tt);

				Example example = new Example(TbOrder.class);
				Example.Criteria criteria = example.createCriteria();

				System.out.println(ss+"------"+s);


				criteria.andLessThanOrEqualTo("endTime",s+" 23:59:59");
				criteria.andGreaterThanOrEqualTo("endTime",s+" 00:00:00");
				criteria.andEqualTo("sellerId",sellerId);

				List<TbOrder> tbOrders = orderMapper.selectByExample(example);
				System.out.println(tbOrders);


				String f = simpleDateFormat.format(time1)+"";
				BigDecimal money = new BigDecimal("0.00");



				for (TbOrder tbOrder : tbOrders) {

					Date endTime1 = tbOrder.getEndTime();
					f = simpleDateFormat.format(endTime1);
					BigDecimal payment = tbOrder.getPayment();
					money=money.add(payment);
					System.out.println(money);
				}
				qian.add(String.valueOf(money));
				time.add(f);
				map.put("time",time);
				map.put("money",qian);
				for (String key : map.keySet()) {
					Object value = map.get(key);
					System.out.println(value);
				}
				System.out.println(map);
				if (s.equals(ss)){

					break;
				}
				c.setTime(time1);
				c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
				Date ttt = c.getTime();
				String sss = simpleDateFormat.format(t);
				System.out.println("明天是:" + sss);
				time1=ttt;
			}
			return map;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 点击订单管理 查询所有订单号
	 * @param pageNo
	 * @param pageSize
	 * @param sellerId
	 * @return
	 */
    @Override
    public PageInfo<TbOrder> findBySellerId(Integer pageNo, Integer pageSize, String sellerId) {
		PageHelper.startPage(pageNo,pageSize);
		Example example = new Example(TbOrder.class);
		Example.Criteria criteria = example.createCriteria();

		TbOrder tbOrder = new TbOrder();
		tbOrder.setSellerId(sellerId);
		criteria.andEqualTo(tbOrder);
		List<TbOrder> tbOrders = orderMapper.selectByExample(example);
		System.out.println(tbOrders);
		PageInfo<TbOrder> info = new PageInfo<TbOrder>(tbOrders);
		System.out.println(info);
		//序列化再反序列化
		String s = JSON.toJSONString(info);
		PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
		System.out.println(pageInfo);
		return pageInfo;
    }

	/**
	 * wql
	 * @param pageNo
	 * @param pageSize
	 * @param time2
	 * @param time11
	 * @return
	 */
	@Override
	public PageInfo<TbOrder> findByTime(Integer pageNo, Integer pageSize, String time1, String time2) {


		PageHelper.startPage(pageNo,pageSize);
		Example example = new Example(TbOrder.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andLessThanOrEqualTo("endTime",time2);
		criteria.andGreaterThanOrEqualTo("createTime",time1);
		System.out.println(time2+"++"+time1);
		List<TbOrder> tbOrders = orderMapper.selectByExample(example);

		System.out.println("======"+tbOrders);
		PageInfo<TbOrder> info = new PageInfo<TbOrder>(tbOrders);

		//序列化再反序列化
		String s = JSON.toJSONString(info);
		PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
		return pageInfo;
	}

	/**
	 * wql 发货
	 * @param ids
	 */
	@Override
	public void shipments(Long[] ids) {
		TbOrder tbOrder = new TbOrder();
		tbOrder.setStatus("3");
		tbOrder.setConsignTime(new Date());
		Example example = new Example(TbOrder.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("orderId",Arrays.asList(ids));

		orderMapper.updateByExampleSelective(tbOrder,example);
	}


}
