package org.springblade.modules.pr.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("atw_auto_order_dj")
@ApiModel(value = "锻件 - 自动下单表", description = "")
public class AutoOrderOfDJ implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @Excel(name = "PR单号")
    @ApiModelProperty(value = "PR单号")
    private String prCode;

    @Excel(name = "PR行号")
    @ApiModelProperty(value = "PR行号")
    private Integer prLn;

    @Excel(name = "料号")
    @ApiModelProperty(value = "料号")
    private String itemCode;

    @Excel(name = "物料描述")
    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @Excel(name = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @Excel(name = "重量")
    @ApiModelProperty(value = "重量")
    private String weight;

    @Excel(name = "材料单价")
    @ApiModelProperty(value = "材料单价")
    private String materialPrice;

    @Excel(name = "单价")
    @ApiModelProperty(value = "单价")
    private String price;

    @Excel(name = "大头外径")
    @ApiModelProperty(value = "大头外径-A")
    private String bigOuterSize;

    @Excel(name = "大头高度")
    @ApiModelProperty(value = "大头高度-A")
    private String bigHeightSize;

    @Excel(name = "小头外径")
    @ApiModelProperty(value = "小头外径-A")
    private String smallOuterSize;

    @Excel(name = "总高度")
    @ApiModelProperty(value = "总高度-A")
    private String totalHeightSize;

    @Excel(name = "内径")
    @ApiModelProperty(value = "内径-AB")
    private String innerSize;

    @Excel(name = "外径")
    @ApiModelProperty(value = "外径-B")
    private String outerSize;

    @Excel(name = "高度")
    @ApiModelProperty(value = "高度-B")
    private String heightSize;

    @Excel(name = "外径余量")
    private String outerRemain;

    @Excel(name = "新内径余量")
    private String newInnerRemain;

    @Excel(name = "内径余量")
    private String innerRemain;

    @Excel(name = "高度余量")
    private String heightRemain;

    @Excel(name = "余量类型")
    @ApiModelProperty(value = "余量类型 - fm：阀帽 zfl：中法兰 zyd：自由锻")
    private String res;

    @Excel(name = "公式类型")
    @ApiModelProperty(value = "公式类型 - A：大小头 B：常规")
    private String type;

    @Excel(name = "是否中标")
    private String swnningBid;
}
