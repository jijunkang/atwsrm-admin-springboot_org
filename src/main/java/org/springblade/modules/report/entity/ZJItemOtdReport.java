package org.springblade.modules.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 铸件及时率报表
 * @author Will
 */
@Data
@ApiModel(value = "铸件及时率报表", description = "铸件及时率报表")
public class ZJItemOtdReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "采购单号")
    private String poCode;

    @ApiModelProperty(value = "采购行号")
    private String poLn;

    @ApiModelProperty(value = "物料编码")
    private String itemCode;

    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @ApiModelProperty(value = "送货数量")
    private String doNum;

    @ApiModelProperty(value = "物料分类")
    private String itemize;

    @ApiModelProperty(value = "尺寸")
    private String itemSize;

    @ApiModelProperty(value = "形式")
    private String form;

    @ApiModelProperty(value = "磅级")
    private String pound;

    @ApiModelProperty(value = "法兰")
    private String flange;

    @ApiModelProperty(value = "系列")
    private String series;

    @ApiModelProperty(value = "材质")
    private String material;

    @ApiModelProperty(value = "需求日期")
    private String reqDate;

    @ApiModelProperty(value = "报检日期")
    private String snDate;

    @ApiModelProperty(value = "交期及时率")
    private String otd;

    @ApiModelProperty(value = "汇总交期及时率、分子")
    private String totalOtdSon;

    @ApiModelProperty(value = "汇总交期及时率、分母")
    private String totalOtdMother;

    @ApiModelProperty(value = "汇总交期及时率")
    private String totalOtd;

}
