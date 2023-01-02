package org.springblade.modules.pr.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class ItemInfoOfXLJVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料编号")
    private String itemCode;

    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "单重")
    private String weight;

    @ApiModelProperty(value = "材料单价")
    private String price;

    @ApiModelProperty(value = "材料费")
    private String materialCost;

    @ApiModelProperty(value = "加工费")
    private String charge;

    @ApiModelProperty(value = "单价")
    private String quotePrice;
}
