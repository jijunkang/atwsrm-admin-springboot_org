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
package org.springblade.modules.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import org.springblade.core.mp.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 供应商动态信息实体类
 *
 * @author Blade
 * @since 2019-11-23
 */
@Data
@TableName("atw_supplier_tendency")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SupplierTendency对象", description = "供应商动态信息")
public class SupplierTendency extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	* ID
	*/
		@ApiModelProperty(value = "ID")
		@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	* 供应商编码
	*/
		@ApiModelProperty(value = "供应商编码")
		private String supCode;
	/**
	* 供应商名称
	*/
		@ApiModelProperty(value = "供应商名称")
		private String supName;
	/**
	* 物料编号
	*/
		@ApiModelProperty(value = "物料编号")
		private String itemCode;
	/**
	* 物料名称
	*/
		@ApiModelProperty(value = "物料名称")
		private String itemName;
	/**
	* 交货及时率
	*/
		@ApiModelProperty(value = "交货及时率")
		private Float arvRate;
	/**
	* 质量合格率
	*/
		@ApiModelProperty(value = "质量合格率")
		private Float passRate;
	/**
	* 同步日期
	*/
		@ApiModelProperty(value = "同步日期")
		private Integer syncDate;


}
