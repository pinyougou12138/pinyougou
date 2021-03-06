package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;

import com.pinyougou.sellergoods.service.ItemCatService;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class ItemCatServiceImpl extends CoreServiceImpl<TbItemCat> implements ItemCatService {


    private TbItemCatMapper itemCatMapper;

    @Autowired
    public ItemCatServiceImpl(TbItemCatMapper itemCatMapper) {
        super(itemCatMapper, TbItemCat.class);
        this.itemCatMapper = itemCatMapper;
    }


    @Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbItemCat> all = itemCatMapper.selectAll();
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItemCat> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize, TbItemCat itemCat) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();

        if (itemCat != null) {
            if (StringUtils.isNotBlank(itemCat.getName())) {
                criteria.andLike("name", "%" + itemCat.getName() + "%");
                //criteria.andNameLike("%"+itemCat.getName()+"%");
            }

        }
        List<TbItemCat> all = itemCatMapper.selectByExample(example);
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItemCat> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /***
     * 获取一级分类
     * @param parentId
     * @return
     */
    @Override
    public List<TbItemCat> findByParentId(Long parentId) {
        TbItemCat tbitemcat = new TbItemCat();
        tbitemcat.setParentId(parentId);
        List<TbItemCat> itemCatList = itemCatMapper.select(tbitemcat);

        //加入缓存
        List<TbItemCat> all = super.findAll();
        for (TbItemCat tbItemCat : all) {
            redisTemplate.boundHashOps("itemCat").put(tbItemCat.getName(),tbItemCat.getTypeId());
        }
        return itemCatList;
    }

    /***
     * 获取二三级分类
     * @param parentId
     * @return
     */
    @Override
    public Map<Long,List<TbItemCat>> findByParentId23(Long parentId) {
        Map map = new HashMap<Long,List<TbItemCat>>();

        //二级分类
        TbItemCat tbitemcat = new TbItemCat();
        tbitemcat.setParentId(parentId);
        List<TbItemCat> itemCatList2 = itemCatMapper.select(tbitemcat);
        map.put(parentId,itemCatList2);
        //三级分类
        for (TbItemCat tbItemCat : itemCatList2) {
            TbItemCat itemCat = new TbItemCat();
            itemCat.setParentId(tbItemCat.getId());
            List<TbItemCat> itemCatList3 = itemCatMapper.select(itemCat);
            map.put(tbItemCat.getId(),itemCatList3);
        }
        return map;
    }


    @Override
    public void updateStatus(Long[] ids, String status) {
        TbItemCat record= new TbItemCat();
        record.setAuditStatus(status);
        Example example = new Example(TbItemCat.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        itemCatMapper.updateByExampleSelective(record,example);//update set status=1 where id in (12,3)
    }

}
