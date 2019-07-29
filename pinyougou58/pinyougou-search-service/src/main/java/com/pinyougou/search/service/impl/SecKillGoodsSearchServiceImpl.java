package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.search.dao.SecKillGoodsSearchDao;
import com.pinyougou.search.service.SecKillGoodsSearchService;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

@Service
public class SecKillGoodsSearchServiceImpl implements SecKillGoodsSearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SecKillGoodsSearchDao secKillGoodsSearchDao;

    @Autowired
    private TbSeckillGoodsMapper secKillGoodsMapper;

    /***
     * 更新elasticSearch
     * @param ids
     */
    @Override
    public void updateIndex(Long[] ids) {
        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        List<TbSeckillGoods> tbSecKillGoodsList = secKillGoodsMapper.selectByExample(example);
        secKillGoodsSearchDao.saveAll(tbSecKillGoodsList);
    }

    /***
     * 删除elasticSearch
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(QueryBuilders.termsQuery("id", ids));
        elasticsearchTemplate.delete(deleteQuery, TbSeckillGoods.class);
    }
}
