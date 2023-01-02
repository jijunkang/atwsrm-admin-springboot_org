package org.springblade.modules.pr.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

@Data
@TableName("atw_item_info_lzq_report")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "联轴器物料信息对象", description = "")
public class ItemInfoEntityOfLZQ extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "供应商料号")
    private String supCode;

    @Excel(name ="供应商名称",orderNum = "3")
    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @Excel(name ="料号",orderNum = "4")
    @ApiModelProperty(value = "料号")
    private String itemCode;

    @Excel(name ="物料描述",orderNum = "5")
    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @Excel(name ="外径",orderNum = "7")
    @ApiModelProperty(value = "外径")
    private String outerSizeOld;

    @Excel(name ="外径余量",orderNum = "8")
    @ApiModelProperty(value = "外径余量")
    private String outerRes;

    @Excel(name ="内径",orderNum = "17")
    @ApiModelProperty(value = "内径")
    private String innerSizeOld;

    //@Excel(name ="内径余量")
    @ApiModelProperty(value = "内径余量")
    private String innerRes;

    @Excel(name ="长度",orderNum = "9")
    @ApiModelProperty(value = "长度")
    private String heightSizeOld;

    @Excel(name ="长度余量",orderNum = "10")
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

    @Excel(name ="单重",orderNum = "11")
    @ApiModelProperty(value = "单重")
    private String weight;

    @Excel(name ="材料单价",orderNum = "12")
    @ApiModelProperty(value = "材料单价")
    private String singleCost;

    //@Excel(name ="系数K")
    private String k; // 系值K

    //@Excel(name ="R")
    private String r; // 系值R

    //@Excel(name ="X")
    private String x; // 系值X

    @Excel(name ="材料费",orderNum = "13")
    @ApiModelProperty(value = "材料费")
    private String materialCost;

    @Excel(name ="车床工时",orderNum = "20")
    @ApiModelProperty(value = "车床工时")
    private String jcMinutes;

    @Excel(name ="车床工时单价",orderNum = "21")
    @ApiModelProperty(value = "车床工时单价")
    private String jcPrice;

    @Excel(name ="CNC工时",orderNum = "22")
    @ApiModelProperty(value = "CNC工时")
    private String cncMinutes;

    @Excel(name ="CNC工时单价",orderNum = "23")
    @ApiModelProperty(value = "CNC工时单价")
    private String cncPrice;

    //@Excel(name ="准备工时")
    @ApiModelProperty(value = "准备工时")
    private String preMinutes;

    @Excel(name ="加工费",orderNum = "24")
    @ApiModelProperty(value = "加工费")
    private String processCost;

    @Excel(name ="产品单价",orderNum = "25")
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


    @Excel(name ="线切割长度",orderNum = "14")
    @ApiModelProperty(value = "线切割长度")
    private String xqgSize;

    @Excel(name ="槽边宽",orderNum = "15")
    @ApiModelProperty(value = "槽边宽")
    private String cbkSize;
    @Excel(name ="槽边中心距",orderNum = "16")
    @ApiModelProperty(value = "槽边中心距")
    private String cbzxjSize;
    @Excel(name ="线切割宽",orderNum = "17")
    @ApiModelProperty(value = "线切割宽")
    private String xqgkSize;
    @Excel(name ="线切割费",orderNum = "18")
    private String xqgCost;//线切割费
    @Excel(name ="焊接费",orderNum = "19")
    private String hjfCost;//焊接费

    private String material; //材质

    private String biHou; // 壁厚

    private String mainType; // 主分类

    private String materialType; // 材料类型

    private String series; // 系列

    private String priority;

    private String workPrice;
    @Excel(name ="请购单号",orderNum = "1")
    private String prCode;
    @Excel(name ="请购行",orderNum = "2")
    private String prLn;
    @Excel(name ="数量",orderNum = "6")
    private String quantity;
    @Excel(name ="总价",orderNum = "28")
    private String totalCost;





}
