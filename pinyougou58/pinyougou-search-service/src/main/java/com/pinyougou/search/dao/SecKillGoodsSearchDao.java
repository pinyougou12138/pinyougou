package com.pinyougou.search.dao;

import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface SecKillGoodsSearchDao extends ElasticsearchCrudRepository<TbSeckillGoods,Long> {
}
