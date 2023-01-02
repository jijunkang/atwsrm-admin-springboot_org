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
package org.springblade.modules.item.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.supitem.entity.SupItem;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "ItemExcel对象", description = "物料表")
public class ItemExcel {
    @ApiModelProperty(value = "物料编码")
    @Excel(name = "物料编码")
    private String     code;

    @Excel(name = "物料名称")
    @ApiModelProperty(value = "物料名称")
    private String     name;

    @Excel(name = "采购倍数")
    @ApiModelProperty(value = "采购倍数")
    private BigDecimal purchMultiple;

    @Excel(name = "最小起订量")
    @ApiModelProperty(value = "最小起订量")
    private BigDecimal purchMix;

    @Excel(name = "供应商交叉关系")
    @ApiModelProperty(value = "供应商交叉关系")
    private String supItems;

    @Excel(name = "采购预处理提前期")
    @ApiModelProperty(value = "采购预提前期")
    private Integer    purchBeforeDate;

    @Excel(name = "采购处理提前期")
    @ApiModelProperty(value = "采购处理提前期")
    private Integer    purchDisposeDate;

    @Excel(name = "采购后提前期")
    @ApiModelProperty(value = "采购后提前期")
    private Integer    purchAfterDate;

    @Excel(name = "安全库存量")
    @ApiModelProperty(value = "安全库存量")
    private BigDecimal stockLowerLimit;

    @Excel(name = "材质")
    @ApiModelProperty(value = "材质")
    private String matQuality;

    @Excel(name = "采购员")
    @ApiModelProperty(value = "采购员")
    private String purchName;


}
