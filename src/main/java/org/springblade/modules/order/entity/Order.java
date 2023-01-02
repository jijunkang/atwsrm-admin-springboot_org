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
package org.springblade.modules.order.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.mp.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体类
 *
 * @author xianboss
 * @since 2019-10-22
 */
@Data
@TableName("shc_order")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Order对象", description = "Order对象")
public class Order extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 订单标题
	*/
		@ApiModelProperty(value = "订单标题")
		private LocalDateTime orderTitle;
	/**
	* 客户ID
	*/
		@ApiModelProperty(value = "客户ID")
		private Long costomerId;
	/**
	* 审批状态
	*/
		@ApiModelProperty(value = "审批状态")
		private String approvalStatus;
	/**
	* 订单总金额
	*/
		@ApiModelProperty(value = "订单总金额")
		private BigDecimal totalAmount;
	/**
	* 已付款金额
	*/
		@ApiModelProperty(value = "已付款金额")
		private BigDecimal amountRefunded;
	/**
	* 已开票金额
	*/
		@ApiModelProperty(value = "已开票金额")
		private BigDecimal invoicedAmount;
	/**
	* 未付款金额
	*/
		@ApiModelProperty(value = "未付款金额")
		private BigDecimal unpaidAmount;


}
