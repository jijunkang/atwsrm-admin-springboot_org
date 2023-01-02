package org.springblade.modules.report.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 送货单报表
 * @author Will
 */
@Data
@ApiModel(value = "送货单报表", description = "送货单报表")
public class DeliverReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "送货单号")
    @ApiModelProperty(value = "送货单号")
    private String doCode;

    @Excel(name = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @Excel(name = "采购单号")
    @ApiModelProperty(value = "采购单号")
    private String poCode;

    @Excel(name = "采购行号")
    @ApiModelProperty(value = "采购行号")
    private String poLn;

    @Excel(name = "物料编码")
    @ApiModelProperty(value = "物料编码")
    private String itemCode;

    @Excel(name = "物料描述")
    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @Excel(name = "送货数量")
    @ApiModelProperty(value = "送货数量")
    private String doNum;

    @Excel(name = "炉号")
    @ApiModelProperty(value = "炉号")
    private String heatCode;

    @Excel(name = "do的创建时间")
    @ApiModelProperty(value = "do的创建时间")
    private String doTime;

    @Excel(name = "状态")
    @ApiModelProperty(value = "状态")
    private String doStatus;

    @Excel(name = "报检单号")
    @ApiModelProperty(value = "报检单号")
    private String snCode;

    @Excel(name = "报检行号")
    @ApiModelProperty(value = "报检行号")
    private String snLn;

    @Excel(name = "报检数量")
    @ApiModelProperty(value = "报检数量")
    private String snNum;

    @Excel(name = "报检时间")
    @ApiModelProperty(value = "报检时间")
    private String snTime;

    @Excel(name = "入库状态")
    @ApiModelProperty(value = "入库状态")
    private String storeStatus;

    @Excel(name = "入库单号")
    @ApiModelProperty(value = "入库单号")
    private String storeCode;

    @Excel(name = "入库行号")
    @ApiModelProperty(value = "入库行号")
    private String storeLn;

    @Excel(name = "入库数量")
    @ApiModelProperty(value = "入库数量")
    private String storeQty;

    @Excel(name = "入库时间")
    @ApiModelProperty(value = "入库时间")
    private String storeTime;

}
