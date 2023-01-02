package org.springblade.modules.pr.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

@Data
@TableName("atw_item_info_part_report")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "小零件物料信息对象", description = "")
public class ItemInfoEntityOfXLJ extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @Excel(name ="供应商料号")
    @ApiModelProperty(value = "供应商料号")
    private String supCode;

    @Excel(name ="供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @Excel(name ="料号")
    @ApiModelProperty(value = "料号")
    private String itemCode;

    @Excel(name ="物料描述")
    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @Excel(name ="外径")
    @ApiModelProperty(value = "外径")
    private String outerSizeOld;

    @Excel(name ="外径余量")
    @ApiModelProperty(value = "外径余量")
    private String outerRes;

    @Excel(name ="内径")
    @ApiModelProperty(value = "内径")
    private String innerSizeOld;

    @Excel(name ="内径余量")
    @ApiModelProperty(value = "内径余量")
    private String innerRes;

    @Excel(name ="长度")
    @ApiModelProperty(value = "长度")
    private String heightSizeOld;

    @Excel(name ="长度余量")
    @ApiModelProperty(value = "长度余量")
    private String heightRes;

    @Excel(name ="材料外径")
    @ApiModelProperty(value = "材料外径")
    private String outerSize;

    @Excel(name ="材料内径")
    @ApiModelProperty(value = "材料内径")
    private String innerSize;

    @Excel(name ="材料长度")
    @ApiModelProperty(value = "材料长度")
    private String heightSize;

    @Excel(name ="单重")
    @ApiModelProperty(value = "单重")
    private String weight;

    @Excel(name ="材料单价")
    @ApiModelProperty(value = "材料单价")
    private String singleCost;

    @Excel(name ="系数K")
    private String k; // 系值K

    @Excel(name ="R")
    private String r; // 系值R

    @Excel(name ="X")
    private String x; // 系值X

    @Excel(name ="材料费")
    @ApiModelProperty(value = "材料费")
    private String materialCost;

    @Excel(name ="车床工时")
    @ApiModelProperty(value = "车床工时")
    private String jcMinutes;

    @Excel(name ="车床工时单价")
    @ApiModelProperty(value = "车床工时单价")
    private String jcPrice;

    @Excel(name ="CNC工时")
    @ApiModelProperty(value = "CNC工时")
    private String cncMinutes;

    @Excel(name ="CNC工时单价")
    @ApiModelProperty(value = "CNC工时单价")
    private String cncPrice;

    @Excel(name ="准备工时")
    @ApiModelProperty(value = "准备工时")
    private String preMinutes;

    @Excel(name ="加工费")
    @ApiModelProperty(value = "加工费")
    private String processCost;

    @Excel(name ="产品单价")
    @ApiModelProperty(value = "产品单价")
    private String price;

    @Excel(name ="最近采购价")
    @ApiModelProperty(value = "最近采购价")
    private String lastPrice;

    @Excel(name ="历史最低价")
    @ApiModelProperty(value = "历史最低价")
    private String lowestPrice;

    @Excel(name ="历史最高价")
    @ApiModelProperty(value = "历史最高价")
    private String highestPrice;

    private String material; //材质

    private String biHou; // 壁厚

    private String mainType; // 主分类

    private String materialType; // 材料类型

    private String series; // 系列

    private String priority;


}
