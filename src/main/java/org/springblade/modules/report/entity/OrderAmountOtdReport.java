package org.springblade.modules.report.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 自动下单率报表
 * @author Will
 */
@Data
@ApiModel(value = "采购额统计报表", description = "采购额统计报表")
public class OrderAmountOtdReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商")
    private String supName;

    @Excel(name = "供应商等级")
    @ApiModelProperty(value = "供应商等级")
    private String templateType;

    @Excel(name = "供应类型")
    @ApiModelProperty(value = "供应类型")
    private String mainName;

    @Excel(name = "采购金额")
    @ApiModelProperty(value = "采购金额")
    private String totalamount;

    @Excel(name = "去年同期金额")
    @ApiModelProperty(value = "去年同期金额")
    private String lastyeartotalamount;

    @Excel(name = "去年全年/2金额")
    @ApiModelProperty(value = "去年全年/2金额")
    private String lastyeartotalamount2;

    @Excel(name = "同类型占比")
    @ApiModelProperty(value = "同类型占比")
    private String typepercent;



}
