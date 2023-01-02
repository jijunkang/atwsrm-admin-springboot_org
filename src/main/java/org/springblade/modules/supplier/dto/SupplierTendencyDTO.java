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
package org.springblade.modules.supplier.dto;

import org.springblade.modules.supplier.entity.SupplierTendency;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商动态信息数据传输对象实体类
 *
 * @author Blade
 * @since 2019-11-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierTendencyDTO extends SupplierTendency {
	private static final long serialVersionUID = 1L;

}
