/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author:  William Wang (wxx@idwsoft.com)
 */
package org.springblade.modules.item.mapper;

import org.apache.ibatis.annotations.Param;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.vo.ItemExcel;
import org.springblade.modules.item.vo.ItemVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 物料表 Mapper 接口
 *
 * @author Blade
 * @since 2019-11-27
 */
public interface ItemMapper extends BaseMapper<Item> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param item
	 * @return
	 */
	List<ItemVO> selectItemPage(IPage page, Item item);

    /**
     * 自定义分页,批量搜索
     *
     * @param page
     * @param item
     * @return
     */
    List<ItemVO> selectItemPageOfList(IPage page, Item item);

	/**
	 *  未完善的物料
	 * @param page
	 * @param item
	 * @return
	 */
	List<ItemVO> incompletePage(IPage<ItemVO> page, Item item);

    List<ItemVO> incompleteList(@Param("item")Item item);

	/**
	 *  未完善的物料数量
	 * @return
	 */
	int incompleteCount(@Param("req") Item item);

	/**
	 *  呆滞物料数量
	 * @return
	 */
	int dullCount();

    List<Item> updateByIds(String[] id);
}
