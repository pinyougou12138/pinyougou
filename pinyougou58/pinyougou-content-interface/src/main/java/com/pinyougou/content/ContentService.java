package com.pinyougou.content;
import java.util.List;
import com.pinyougou.pojo.TbContent;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ContentService extends CoreService<TbContent> {

	/***
	 *
	 * @param categoryId
	 * @return
	 */
	public List<TbContent> findByCategoryId(Long categoryId);
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize, TbContent Content);
	
}
