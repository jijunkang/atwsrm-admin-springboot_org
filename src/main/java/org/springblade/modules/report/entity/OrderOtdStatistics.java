package org.springblade.modules.report.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 下单及时率统计报表
 * @author Will
 */
@Data
@ApiModel(value = "下单及时率统计报表", description = "下单及时率统计报表")
public class OrderOtdStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "责任人")
    private String person;

    @ApiModelProperty(value = "po行数")
    private Integer poTotal;

    @ApiModelProperty(value = "<=3天及时行")
    private Integer threeNum;

    @ApiModelProperty(value = "<=5天及时行")
    private Integer sevenNum;

    @ApiModelProperty(value = "综合及时率")
    private Integer multipleNum;

    @ApiModelProperty(value = "<=3天及时率")
    private String threeOtd;

    @ApiModelProperty(value = "<=5天及时率")
    private String sevenOtd;

    @ApiModelProperty(value = "综合及时率")
    private String multipleOtd;

}
