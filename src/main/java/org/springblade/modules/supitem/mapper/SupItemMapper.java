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
package org.springblade.modules.supitem.mapper;

import org.springblade.modules.supitem.entity.SupItem;
import org.springblade.modules.supitem.vo.SupItemVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Blade
 * @since 2019-12-04
 */
public interface SupItemMapper extends BaseMapper<SupItem> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param supItem
	 * @return
	 */
	List<SupItemVO> selectSupItemPage(IPage page, SupItemVO supItem);

    boolean deleteByItemCode(String itemCode);

    SupItem selectByCode(String itemCode, String supCode);
}
