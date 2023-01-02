package org.springblade.modules.pr.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

@Data
@TableName("atw_auto_order_part")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "小零件自动下单", description = "")
public class AutoOrderOfXLJ extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)//指定自增策略
    private  Long id;

    @Excel(name ="供应商料号")
    @ApiModelProperty(value = "供应商料号")
    private String supCode;

    @Excel(name ="供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @Excel(name ="请购单号")
    @ApiModelProperty(value = "请购单号")
    private String prCode;

    @Excel(name ="请购行号")
    @ApiModelProperty(value = "请购行号")
    private String prLn;

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

    @Excel(name ="系值K")
    @ApiModelProperty(value = "系值K")
    private String k; // 系值K

    @Excel(name ="r")
    @ApiModelProperty(value = "r")
    private String r; // 系值K

    @Excel(name ="x")
    @ApiModelProperty(value = "K")
    private String x; // 系值K

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

    @Excel(name ="数量")
    @ApiModelProperty(value = "数量")
    private String priceNum;

    @Excel(name ="总价")
    @ApiModelProperty(value = "总价")
    private String amount;

}
