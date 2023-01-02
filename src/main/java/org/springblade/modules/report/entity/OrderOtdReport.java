package org.springblade.modules.report.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 下单及时率报表
 * @author Will
 */
@Data
@ApiModel(value = "下单及时率报表", description = "下单及时率报表")
public class OrderOtdReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "供应商编号")
    @ApiModelProperty(value = "供应商编号")
    private String supCode;

    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @Excel(name = "pr行号")
    @ApiModelProperty(value = "pr行号")
    private String prCodeLn;

    @Excel(name = "物料编号")
    @ApiModelProperty(value = "物料编号")
    private String itemCode;

    @Excel(name = "物料描述")
    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @Excel(name = "业务类型",replace = {"标准采购_0","全程委外采购_1","VMI采购_2"})
    @ApiModelProperty(value = "业务类型")
    private String bizType;


    @Excel(name = "po行号")
    @ApiModelProperty(value = "po行号")
    private String poCodeLn;

    @Excel(name = "采购单位")
    @ApiModelProperty(value = "采购单位")
    private String tcUom;

    @ApiModelProperty(value = "采购数量")
    private String tcNum;

    @Excel(name = "要求交期")
    @ApiModelProperty(value = "要求交期")
    private String reqDate;

    @Excel(name = "承诺交期")
    @ApiModelProperty(value = "承诺交期")
    private String supConfirmDate;

    @Excel(name = "PR审核交期")
    @ApiModelProperty(value = "PR审核交期")
    private String prCheckDate;

    @Excel(name = "PO审核交期")
    @ApiModelProperty(value = "PR审核交期")
    private String poCheckDate;

    @Excel(name = "责任人")
    @ApiModelProperty(value = "责任人")
    private String purchName;

    @Excel(name = "<=3是否及时")
    @ApiModelProperty(value = "<=3是否及时")
    private String isThreeTimely;

    @Excel(name = "<=5是否及时")
    @ApiModelProperty(value = "<=5是否及时")
    private String isSevenTimely;


    private String isNeedCheck;

    @Excel(name = "是否自动下单")
    @ApiModelProperty(value = "是否自动下单")
    private String isAutoOrder;


    @ApiModelProperty(value = "下单统计及时率")
    private List<OrderOtdStatistics> orderOtdStatisticsList;

}
