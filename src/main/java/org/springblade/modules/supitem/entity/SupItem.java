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
package org.springblade.modules.supitem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

/**
 * 实体类
 *
 * @author Blade
 * @since 2019-12-04
 */
@Data
@TableName("atw_sup_item")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SupItem对象", description = "SupItem对象")
public class SupItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 物料编码
     */
    @ApiModelProperty(value = "物料编码")
    private String itemCode;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String supCode;
    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String supName;
    /**
     * 物料名称
     */
    @ApiModelProperty(value = "物料名称")
    private String itemName;
    /**
     * 标准周期 单位天
     */
    @ApiModelProperty(value = "标准周期 单位天")
    private Integer normalPeriod;
    /**
     * 是否生效
     */
    @ApiModelProperty(value = "是否生效")
    private Boolean isEnabled;
}
