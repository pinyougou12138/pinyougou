package com.pinyougou.search.service;

public interface SecKillGoodsSearchService {
    /***
     * 更新索引
     * @param ids
     */
    void updateIndex(Long[] ids);

    /***
     * 删除索引
     * @param ids
     */
    void deleteByIds(Long[] ids);
}
