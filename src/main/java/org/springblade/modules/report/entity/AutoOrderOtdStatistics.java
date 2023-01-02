package org.springblade.modules.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 下单及时率统计报表
 * @author Will
 */
@Data
@ApiModel(value = "下单及时率统计报表", description = "下单及时率统计报表")
public class AutoOrderOtdStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "责任人")
    private String person;

    @ApiModelProperty(value = "po行数")
    private Integer poTotal;

    @ApiModelProperty(value = "白名单")
    private Integer whiteList;

    @ApiModelProperty(value = "框架（PO不审核）")
    private Integer FANotApproval;

    @ApiModelProperty(value = "框架（PO需审核）")
    private Integer FA;

    @ApiModelProperty(value = "自动下单率")
    private String autoOrderCent;

}
