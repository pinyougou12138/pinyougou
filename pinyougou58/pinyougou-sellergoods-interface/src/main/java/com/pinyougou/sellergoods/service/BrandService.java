package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbBrand;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface BrandService extends CoreService<TbBrand> {
	/**
	 * 从excel表中导入到数据库
	 */
	void importBrandList();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize, TbBrand Brand);

	/**
	 * 审核品牌
	 * @param ids
	 * @param status
	 */
    void updateStatus(Long[] ids, String status);
}
