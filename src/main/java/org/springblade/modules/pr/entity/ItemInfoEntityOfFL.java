package org.springblade.modules.pr.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

@Data
@TableName("atw_item_info_fl_report")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "法兰信息对象", description = "")
public class ItemInfoEntityOfFL extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @Excel(name ="供应商编号")
    @ApiModelProperty(value = "供应商编号")
    private String supCode;

    @Excel(name ="供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @Excel(name ="物料编号")
    @ApiModelProperty(value = "物料编号")
    private String itemCode;

    @Excel(name ="物料描述")
    @ApiModelProperty(value = "物料描述")
    private String itemName;

    //@Excel(name ="外径")
    @ApiModelProperty(value = "外径")
    private String outerSizeOld;

    //@Excel(name ="外径余量")
    @ApiModelProperty(value = "外径余量")
    private String outerRes;

    //@Excel(name ="内径")
    @ApiModelProperty(value = "内径")
    private String innerSizeOld;

    //@Excel(name ="内径余量")
    @ApiModelProperty(value = "内径余量")
    private String innerRes;

    //@Excel(name ="长度")
    @ApiModelProperty(value = "长度")
    private String heightSizeOld;

    //@Excel(name ="长度余量")
    @ApiModelProperty(value = "长度余量")
    private String heightRes;

    //@Excel(name ="材料外径")
    @ApiModelProperty(value = "材料外径")
    private String outerSize;

    //@Excel(name ="材料内径")
    @ApiModelProperty(value = "材料内径")
    private String innerSize;

    //@Excel(name ="材料长度")
    @ApiModelProperty(value = "材料长度")
    private String heightSize;

    @Excel(name ="单重")
    @ApiModelProperty(value = "单重")
    private String weight;

    @Excel(name ="材料单价")
    @ApiModelProperty(value = "材料单价")
    private String singleCost;

    //@Excel(name ="系数K")
    private String k; // 系值K

    //@Excel(name ="R")
    private String r; // 系值R

    //@Excel(name ="X")
    private String x; // 系值X

    //@Excel(name ="材料费")
    @ApiModelProperty(value = "材料费")
    private String materialCost;

    //@Excel(name ="车床工时")
    @ApiModelProperty(value = "车床工时")
    private String jcMinutes;

    //@Excel(name ="车床工时单价")
    @ApiModelProperty(value = "车床工时单价")
    private String jcPrice;

    //@Excel(name ="CNC工时")
    @ApiModelProperty(value = "CNC工时")
    private String cncMinutes;

    //@Excel(name ="CNC工时单价")
    @ApiModelProperty(value = "CNC工时单价")
    private String cncPrice;

    //@Excel(name ="准备工时")
    @ApiModelProperty(value = "准备工时")
    private String preMinutes;

    //@Excel(name ="加工费")
    @ApiModelProperty(value = "加工费")
    private String processCost;

    @Excel(name ="产品单价")
    @ApiModelProperty(value = "产品单价")
    private String price;

    //@Excel(name ="最近采购价")
    @ApiModelProperty(value = "最近采购价")
    private String lastPrice;

    //@Excel(name ="历史最低价")
    @ApiModelProperty(value = "历史最低价")
    private String lowestPrice;

    //@Excel(name ="历史最高价")
    @ApiModelProperty(value = "历史最高价")
    private String highestPrice;

    //@Excel(name ="长度")
    @ApiModelProperty(value = "长度")
    private String lengthSize;

    //@Excel(name ="长度余量")
    @ApiModelProperty(value = "长度余量")
    private String lengthRes;

    //@Excel(name ="原材料长度")
    @ApiModelProperty(value = "原材料长度")
    private String lengthSizeOld;

    //@Excel(name ="切割费")
    @ApiModelProperty(value = "切割费")
    private String cuttingCost;

    //@Excel(name ="打磨费")
    @ApiModelProperty(value = "打磨费")
    private String grindingCost;

    //@Excel(name ="喷涂外径")
    @ApiModelProperty(value = "喷涂外径")
    private String sprayingOuterSize;

    //@Excel(name ="喷涂长度")
    @ApiModelProperty(value = "喷涂长度")
    private String sprayingLengthSize;

    //@Excel(name ="喷涂面积")
    @ApiModelProperty(value = "喷涂面积")
    private String sprayingArea;

    //@Excel(name ="喷涂单价")
    @ApiModelProperty(value = "喷涂单价")
    private String sprayingPrice;

    //@Excel(name ="喷涂费")
    @ApiModelProperty(value = "喷涂费")
    private String sprayingFee;

    @Excel(name ="总价")
    @ApiModelProperty(value = "总价")
    private String totalCost;

    @Excel(name ="数量")
    @ApiModelProperty(value = "数量")
    private String quantity;

    private String material; //材质

    private String spraying; //涂层

    @Excel(name ="请购单号")
    @ApiModelProperty(value = "请购单号")
    private String prCode;

    @Excel(name ="请购行")
    @ApiModelProperty(value = "请购行")
    private String prLn;

    @ApiModelProperty(value = "寸级")
    private String size;

    @ApiModelProperty(value = "形式")
    private String form;

    @ApiModelProperty(value = "磅级")
    private String pound;

    @ApiModelProperty(value = "英寸")
    private String inch;

    @ApiModelProperty(value = "国标")
    private String gb;

    private String ljxs;


}
