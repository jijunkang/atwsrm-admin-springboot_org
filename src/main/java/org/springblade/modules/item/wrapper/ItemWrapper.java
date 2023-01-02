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
package org.springblade.modules.item.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.vo.ItemVO;

/**
 * 物料表包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2019-11-27
 */
public class ItemWrapper extends BaseEntityWrapper<Item, ItemVO>  {

	public static ItemWrapper build() {
		return new ItemWrapper();
 	}

	@Override
	public ItemVO entityVO(Item item) {
		ItemVO itemVO = BeanUtil.copy(item, ItemVO.class);

		return itemVO;
	}

}
