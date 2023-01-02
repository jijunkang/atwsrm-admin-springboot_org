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
package org.springblade.modules.item.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.supitem.entity.SupItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 物料表数据传输对象实体类
 *
 * @author Blade
 * @since 2019-11-27
 */
@Data
public class ItemReq implements Serializable{
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	private Long id;

	/**
	 * 通用名
	 */
	@ApiModelProperty(value = "通用名")
	private String     commonName;
	/**
	 * 云平台物料编码
	 */
	@ApiModelProperty(value = "云平台物料编码")
	private String     cloudCode;

	/**
	 * 物料属性
	 */
	@ApiModelProperty(value = "物料属性")
	private String     itemAttr;

	/**
	 * 基本计量单位
	 */
	@ApiModelProperty(value = "基本计量单位")
	private String     baseUom;
	/**
	 * 辅助计量单位
	 */
	@ApiModelProperty(value = "辅助计量单位")
	private String     assistUom;
	/**
	 * 计价单位
	 */
	@ApiModelProperty(value = "交易单位")
	private String     tcUom;
	/**
	 * 计价单位
	 */
	@ApiModelProperty(value = "计价单位")
	private String     priceUom;
	/**
	 * 毛重
	 */
	@ApiModelProperty(value = "毛重")
	private BigDecimal roughWeight;
	/**
	 * 净重
	 */
	@ApiModelProperty(value = "净重")
	private BigDecimal netWeight;
	/**
	 * 重量单位
	 */
	@ApiModelProperty(value = "重量单位")
	private String     netUom;
	/**
	 * 体积
	 */
	@ApiModelProperty(value = "体积")
	private BigDecimal volume;
	/**
	 * 体积单位
	 */
	@ApiModelProperty(value = "体积单位")
	private String     volumeUom;
	/**
	 * 采购员编号
	 */
	@ApiModelProperty(value = "采购员编号")
	private String     purchCode;

	@ApiModelProperty(value = "跟员编号")
	private String     traceCode;
	/**
	 * 品牌
	 */
	@ApiModelProperty(value = "品牌")
	private String     brand;
	/**
	 * PLM设计者
	 */
	@ApiModelProperty(value = "PLM设计者")
	private String     plmAuthor;
	/**
	 * PLM设计日期
	 */
	@ApiModelProperty(value = "PLM设计日期")
	private Date       plmDate;

	/**
	 * 库存
	 */
	@ApiModelProperty(value = "库存")
	private BigDecimal stock;
	/**
	 * 库存号
	 */
	@ApiModelProperty(value = "库存号")
	private String     stockNo;
	/**
	 * 安全库存量
	 */
	@ApiModelProperty(value = "安全库存量")
	private BigDecimal stockLowerLimit;
	/**
	 * 库存上限（最大库存）
	 */
	@ApiModelProperty(value = "库存上限（最大库存）")
	private BigDecimal stockUpperLimit;
	/**
	 * 物料采购属性
	 */
	/**
	 * 采购预提前期
	 */
	@ApiModelProperty(value = "采购预提前期")
	private Integer    purchBeforeDate;
	/**
	 * 采购后提前期
	 */
	@ApiModelProperty(value = "采购后提前期")
	private Integer    purchAfterDate;
	/**
	 * 采购处理提前期
	 */
	@ApiModelProperty(value = "采购处理提前期")
	private Integer    purchDisposeDate;
	/**
	 * 标准交期
	 */
	@ApiModelProperty(value = "标准交期")
	private Integer    standardDate;
	/**
	 * 价格属性
	 */
	@ApiModelProperty(value = "价格属性")
	private String     priceAttr;
	/**
	 * 标准价格
	 */
	@ApiModelProperty(value = "标准价格")
	private BigDecimal standardPrice;
	/**
	 * 采购倍数
	 */
	@ApiModelProperty(value = "采购倍数")
	private BigDecimal purchMultiple;
	/**
	 * 最小起订量
	 */
	@ApiModelProperty(value = "最小起订量")
	private BigDecimal purchMix;
	/**
	 * 供应商订单分配比例
	 */
	@ApiModelProperty(value = "供应商订单分配比例")
	private BigDecimal supAllocateRate;
	/**
	 * 期货让步比例
	 */
	@ApiModelProperty(value = "期货让步比例")
	private BigDecimal futureScale;
	/**
	 * 价格权重
	 */
	@ApiModelProperty(value = "价格权重")
	private BigDecimal priceWeight;
	/**
	 * 技术权重
	 */
	@ApiModelProperty(value = "技术权重")
	private BigDecimal techWeight;
	/**
	 * 商务权重
	 */
	@ApiModelProperty(value = "商务权重")
	private BigDecimal businessWeight;
	/**
	 * 是否系列化
	 */
	@ApiModelProperty(value = "是否系列化")
	private String     sOrN;


	/**
	 * 报价期限单位秒 默认24小时
	 */
	@ApiModelProperty(value = "报价期限单位秒 默认24小时")
	private Integer    quoteLimit;

	@ApiModelProperty(value = "供应商编码")
	private String[] supCodes;

    /**
     * 下单员编号
     */
    @ApiModelProperty(value = "下单员编号")
    private String  placeCode;
}
