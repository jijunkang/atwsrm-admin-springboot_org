package org.springblade.modules.pr.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class ItemInfoOfQZVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料编号")
    private String itemCode;

    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "球座单重")
    private String qzWeight;

    @ApiModelProperty(value = "球座加工费")
    private String qzCharge;

    @ApiModelProperty(value = "阀座单重")
    private String fzWeight;

    @ApiModelProperty(value = "阀座加工费")
    private String fzCharge;

    @ApiModelProperty(value = "球座喷涂面积")
    private String qzSprayArea;

    @ApiModelProperty(value = "材料单价")
    private String price;

    @ApiModelProperty(value = "球座喷涂单价")
    private String ptPrice;

    @ApiModelProperty(value = "阀座喷涂费")
    private String fzSprayCharge;

    @ApiModelProperty(value = "单价")
    private String quotePrice;

    @ApiModelProperty(value = "阀座材质单价")
    private String fzPrice;

    @ApiModelProperty(value = "球体材质单价")
    private String qtPrice;
}
