package org.springblade.modules.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 交期及时率报表
 * @author Will
 */
@Data
@ApiModel(value = "交期及时率报表", description = "交期及时率报表")
public class AllOtdReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "子项目号")
    private String subProCode;

    @ApiModelProperty(value = "物料编码")
    private String itemCode;

    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "需求用量（子项目需求数量 * 料号单位用量）")
    private String reqNum;

    @ApiModelProperty(value = "需求时间")
    private String reqDate;

    @ApiModelProperty(value = "计划时间")
    private String planDate;

    @ApiModelProperty(value = "请购单号")
    private String prCode;

    @ApiModelProperty(value = "全程委外标识")
    private String bizType;

    @ApiModelProperty(value = "审核修改交期")
    private String checkUpdateTime;

    @ApiModelProperty(value = "齐套最后一次出现的时间")
    private String apsEndDate;

    @ApiModelProperty(value = "是否结束")
    private String apsEndFlag;

    @ApiModelProperty(value = "负责人")
    private String person;

    @ApiModelProperty(value = "是否及时")
    private String isOtd;

    @ApiModelProperty(value = "7天是否及时")
    private String isOtdSeven;

}
