package org.springblade.modules.outpr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.modules.outpr.service.IOutSupPreOrderService;

import java.math.BigDecimal;

/**
 * 实体类
 * @author Will
 */
@Data
@TableName("atw_out_sup_pre_order")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OutSupPreOrder对象", description = "")
public
class OutSupPreOrderEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "请购单号")
    private String prCode;

    @ApiModelProperty(value = "请购日期")
    private Long prDate;

    @ApiModelProperty(value = "委外物料id")
    private Long       prItemId;

    @ApiModelProperty(value = "委外工序报价id")
    private Long       itemPriceId;

    @ApiModelProperty(value = "状态")
    private String     supCode;

    @ApiModelProperty(value = "供应商名称")
    private String     supName;

    @ApiModelProperty(value = "物料单价")
    private BigDecimal price;

    @ApiModelProperty(value = "含税单价")
    private BigDecimal taxPrice;

    @ApiModelProperty(value = "物料总价")
    private BigDecimal total;

    @ApiModelProperty(value = "计量数量")
    private BigDecimal priceNum;

    @ApiModelProperty(value = "计量单位")
    private String priceUom;

    @ApiModelProperty(value = "需求日期")
    private Long reqDate;

    @ApiModelProperty(value = "物料编号")
    private String itemCode;

    @ApiModelProperty(value = "物料名称")
    private String itemName;

    @ApiModelProperty(value = "首件准备工时费用")
    private BigDecimal firstSetupCost;

    @ApiModelProperty(value = "材料费")
    private BigDecimal materialCost;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "供应商承诺交期")
    private Long supDeliveryTime;

    @ApiModelProperty(value = "接单截止时间")
    private Long rcvEndtime;

    @ApiModelProperty(value = "接单时间")
    private Long rcvTime;

    @ApiModelProperty(value = "供应商拒绝接单时间")
    private Long refuseTime;

    @ApiModelProperty(value = "弃审时间")
    private Long waiveTime;

    @ApiModelProperty(value = "审核通过时间")
    private Long approveTime;

    @ApiModelProperty(value = "下单时间")
    private Long orderTime;

    @ApiModelProperty(value = "询价方式")
    private String inquiryWay;

    public String getSupStatus(){
        if(this.getStatus() == null){
            return "";
        }
        switch(this.getStatus()){
            case 10: return "待接单";
            case 20: return "待审核";
            case 30: return "拒绝接单";
            case 40: return "一级审核通过";
            case 41: return "一级审核已阅";
            case 50: return "审核拒绝";
            case 60: return "待下单";
            case 70: return "已下单";
            default:
                return String.valueOf(this.getStatus());
        }
    }
}
