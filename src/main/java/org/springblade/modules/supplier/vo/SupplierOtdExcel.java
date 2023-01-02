package org.springblade.modules.supplier.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.modules.report.entity.OrderOtdStatistics;

import java.io.Serializable;
import java.util.List;

/**
 * 供应商 及时率报表
 * @author Will
 */
@Data
@ApiModel(value = "供应商及时率报表", description = "供应商及时率报表")
public class SupplierOtdExcel implements Serializable {

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

    @Excel(name = "po行号")
    @ApiModelProperty(value = "po行号")
    private String poCodeLn;

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

    @Excel(name = "采购单位")
    @ApiModelProperty(value = "采购单位")
    private String tcUom;

    @Excel(name = "采购数量")
    @ApiModelProperty(value = "采购数量")
    private String tcNum;

    @Excel(name = "承诺交期内SN报检数量")
    @ApiModelProperty(value = "承诺交期内SN报检数量")
    private String rcvNUm;

    @Excel(name = "是否及时")
    @ApiModelProperty(value = "是否及时")
    private String idOtd;

}
