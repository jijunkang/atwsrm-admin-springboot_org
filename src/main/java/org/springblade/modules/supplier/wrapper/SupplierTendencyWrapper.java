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
package org.springblade.modules.supplier.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.supplier.entity.SupplierTendency;
import org.springblade.modules.supplier.vo.SupplierTendencyVO;

/**
 * 供应商动态信息包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2019-11-23
 */
public class SupplierTendencyWrapper extends BaseEntityWrapper<SupplierTendency, SupplierTendencyVO>  {

	public static SupplierTendencyWrapper build() {
		return new SupplierTendencyWrapper();
 	}

	@Override
	public SupplierTendencyVO entityVO(SupplierTendency supplierTendency) {
		SupplierTendencyVO supplierTendencyVO = BeanUtil.copy(supplierTendency, SupplierTendencyVO.class);

		return supplierTendencyVO;
	}

}
