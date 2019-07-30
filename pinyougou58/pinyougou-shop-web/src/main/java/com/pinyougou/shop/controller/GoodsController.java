package com.pinyougou.shop.controller;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.pojo.TbItem;
import entity.Goods;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import com.github.pagehelper.PageInfo;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    /***
     * 商品上架 下架
     * @param status
     * @param ids
     * @return
     */
    @RequestMapping("/isMarketable/{status}")
    public Result isMarketable(@PathVariable("status") String status, @RequestBody Long[] ids){
        try {
            //修改数据库上下架状态
            goodsService.updateIsMarketable(status,ids);
            //上架
            if ("1".equals(status)) {
                //通过spu goodsId查询sku
				/*List<TbItem> tbItemList = goodsService.findTbItemListByIds(ids);
				//加入es服务器数据
				itemSearchService.updateIndex(tbItemList);*/

                //创建sku静态文件
				/*for (Long id : ids) {
					itemPageService.genItemHtml(id);
				}*/

                //获取存入索引对象
                List<TbItem> tbItemList = goodsService.findTbItemListByIds(ids);
                //设置message-- topic tag key body methods
                MessageInfo messageInfo = new MessageInfo(tbItemList,"topic_goods",
                        "tag_goods","updateIsMarketable",MessageInfo.METHOD_UPDATE);

                Message message = new Message(messageInfo.getTopic(),messageInfo.getTags(),
                        messageInfo.getKeys(),JSON.toJSONString(messageInfo).getBytes());

                //发送消息
                SendResult send = defaultMQProducer.send(message);

            }

            //下架
            if ("2".equals(status)) {
                //设置message
                MessageInfo messageInfo = new MessageInfo(ids,"topic_goods","tag_goods",
                        "deleteIsMarketable",MessageInfo.METHOD_DELETE);

                Message message = new Message(messageInfo.getTopic(),messageInfo.getTags(),
                        messageInfo.getKeys(),JSON.toJSONString(messageInfo).getBytes());
                //发送message
                defaultMQProducer.send(message);
            }

            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return goodsService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			//获取到商家的ID
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getGoods().setSellerId(sellerId);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
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
	public Goods findOne(@PathVariable(value = "id") Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @returnu
	 */
	@RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        try {
            //删除数据库
            goodsService.delete(ids);
            //删除es服务器索引
            //itemSearchService.deleteByIds(ids);

            //消息队列
            //设置message
            MessageInfo messageInfo = new MessageInfo(ids,"topic_goods","tag_goods",
                    "deleteIsMarketable",MessageInfo.METHOD_DELETE);

            Message message = new Message(messageInfo.getTopic(),messageInfo.getTags(),
                    messageInfo.getKeys(),JSON.toJSONString(messageInfo).getBytes());
            //发送message
            defaultMQProducer.send(message);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }
	
	

	@RequestMapping("/search")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbGoods goods) {
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(sellerId);
		return goodsService.findPage(pageNo, pageSize, goods);
    }

	/**
	 * wql根据id 查找一个数据 用于秒杀申请
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOneSKU")
	public TbItem findOneSKU(Long id){

		return goodsService.findOneSKU(id);
	}
	
}
