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
package org.springblade.modules.order.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.order.entity.Order;
import org.springblade.modules.order.vo.OrderVO;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author xianboss
 * @since 2019-10-22
 */
public class OrderWrapper extends BaseEntityWrapper<Order, OrderVO>  {

	public static OrderWrapper build() {
		return new OrderWrapper();
 	}

	@Override
	public OrderVO entityVO(Order order) {
		OrderVO orderVO = BeanUtil.copy(order, OrderVO.class);

		return orderVO;
	}

}
