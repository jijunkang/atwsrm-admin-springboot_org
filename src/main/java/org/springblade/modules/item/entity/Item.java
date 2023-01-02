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
package org.springblade.modules.item.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 物料表实体类
 *
 * @author Blade
 * @since 2019-11-27
 */
@Data
@TableName("atw_item")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Item对象", description = "物料表")
public
class Item extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 物料编码
     */
    @ApiModelProperty(value = "物料编码")
    @Excel(name = "物料编码")
    private String     code;

    @Excel(name = "物料名称")
    @ApiModelProperty(value = "物料名称")
    @TableField("`name`")
    private String     name;
    /**
     * 物料规格
     */
    @ApiModelProperty(value = "物料规格")
    private String     specs;
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
     * 主分类编码
     */
    @ApiModelProperty(value = "主分类编码")
    private String     mainCode;
    /**
     * 主分类名称
     */
    @ApiModelProperty(value = "主分类名称")
    private String     mainName;
    /**
     * 物料属性
     */
    @ApiModelProperty(value = "物料属性")
    private String     itemAttr;
    /**
     * 材质
     */
    @ApiModelProperty(value = "材质")
    private String     matQuality;
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

    @Excel(name = "采购员工号")
    @ApiModelProperty(value = "采购员工号")
    private String     purchCode;

    @Excel(name = "采购员名称")
    @ApiModelProperty(value = "采购员名称")
    private String     purchName;

    @Excel(name = "跟单员工号")
    @ApiModelProperty(value = "跟单员编号")
    private String     traceCode;

    @Excel(name = "跟单员名称")
    @ApiModelProperty(value = "跟单员名称")
    private String     traceName;

    @Excel(name = "下单员工号")
    @ApiModelProperty(value = "下单员编号")
    private String     placeCode;

    @Excel(name = "下单员名称")
    @ApiModelProperty(value = "下单员名称")
    private String     placeName;

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
     * 图纸
     */
    @ApiModelProperty(value = "图纸")
    private String     drawing;
    /**
     * 上一道工序图纸
     */
    @ApiModelProperty(value = "上一道工序图纸")
    private String     processDrawing;
    /**
     * 子件BOM
     */
    @ApiModelProperty(value = "子件BOM")
    private String     subBom;
    /**
     * 工艺路线
     */
    @ApiModelProperty(value = "工艺路线")
    private String     craftDrawing;
    /**
     * 检验标准文件
     */
    @ApiModelProperty(value = "检验标准文件")
    private String     checkstdDrawing;
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
    @ApiModelProperty(value = "物料采购属性")
    private String     purchAttr;
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
    @JsonProperty("sOrN")
    @ApiModelProperty(value = "是否系列化")
    private String     sOrN;
    /**
     * 物料描述
     */
    @ApiModelProperty(value = "物料描述")
    @TableField("`desc`")
    private String     desc;
    /**
     * 0不需要停顿 1停顿
     */
    @ApiModelProperty(value = "0不需要停顿 1停顿")
    private Integer    isStop;
    /**
     * 报价期限单位秒 默认24小时
     */
    @ApiModelProperty(value = "报价期限单位秒 默认24小时")
    private Integer    quoteLimit;
    /**
     * 修改次数
     */
    @ApiModelProperty(value = "修改次数")
    private Integer    updateCnt;

    /**
     * 有供应商数量
     */
    @ApiModelProperty(value = "供应商数量")
    private Integer supCount;

    @ApiModelProperty(value = "公称通径")
    private String diameter;

    @ApiModelProperty(value = "压力等级")
    private String pressureClass;

    @ApiModelProperty(value = "工艺卡控类型编码")
    private String craftctrlCode;

    @ApiModelProperty(value = "工艺卡控类型名称")
    private String craftctrlName;

    @ApiModelProperty(value = "是否是VMI物料")
    private String isVmi;

    @ApiModelProperty(value = "ABC类")
    private String codeType;

    @ApiModelProperty(value = "是否标准件")
    private String isStandard;

}
