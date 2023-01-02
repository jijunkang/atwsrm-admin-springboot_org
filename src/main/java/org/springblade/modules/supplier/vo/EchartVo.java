package org.springblade.modules.supplier.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "供应商供应计划表", description = "供应商供应计划表")
public class EchartVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "时间段")
    private String time;
    @ApiModelProperty(value = "数量")
    private String number;
    @ApiModelProperty(value = "物料描述")
    private String itemName;
    @ApiModelProperty(value = "重量")
    private String weight;

    @ApiModelProperty(value = "交期合格率")
    private String otd;

}
