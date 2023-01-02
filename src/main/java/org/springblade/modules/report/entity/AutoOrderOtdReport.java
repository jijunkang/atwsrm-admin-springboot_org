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
@ApiModel(value = "自动下单率报表", description = "自动下单率报表")
public class AutoOrderOtdReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "请购+行号")
    @ApiModelProperty(value = "请购+行号")
    private String PRCODELN;

    @Excel(name = "订单号+行号")
    @ApiModelProperty(value = "订单号+行号")
    private String POCODELN;

    @Excel(name = "自动下单类型")
    @ApiModelProperty(value = "自动下单类型")
    private String AUTOORDERTYPE;

    @Excel(name = "是否自动下单")
    @ApiModelProperty(value = "是否自动下单")
    private String ISAUTOORDER;

    @Excel(name = "责任人")
    @ApiModelProperty(value = "责任人")
    private String PURCHNAME;

    @Excel(name = "是否标准化")
    @ApiModelProperty(value = "是否标准化")
    private String ISSTANDARD;

    @Excel(name = "ABC分类")
    @ApiModelProperty(value = "ABC分类")
    private String SORN;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商")
    private String SUPNAME;

    @Excel(name = "料号")
    @ApiModelProperty(value = "料号")
    private String ITEMCODE;

    @Excel(name = "料品名称")
    @ApiModelProperty(value = "料品名称")
    private String ITEMNAME;

    @Excel(name = "采购单位")
    @ApiModelProperty(value = "采购单位")
    private String TCUOM;

    @Excel(name = "采购数量")
    @ApiModelProperty(value = "采购数量")
    private String TCNUM;

    @Excel(name = "要求交期")
    @ApiModelProperty(value = "要求交期")
    private String REQDATE;

    @Excel(name = "确认交期")
    @ApiModelProperty(value = "确认交期")
    private String SUPCONFIRMDATE;

    //@Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String PERSON;

    @Excel(name = "行状态")
    @ApiModelProperty(value = "行状态")
    private String LINESTATUS;

    private String ISNEEDCHECK;

    @ApiModelProperty(value = "自动下单统计率")
    private List<AutoOrderOtdStatistics> autoOrderOtdStatisticsList;


}
