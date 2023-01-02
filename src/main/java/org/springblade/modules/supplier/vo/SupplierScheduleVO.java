package org.springblade.modules.supplier.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 供应商供应计划表 数据 给前台展示
 *
 * @author Will
 */
@Data
@ApiModel(value = "供应商供应计划表", description = "供应商供应计划表")
public class SupplierScheduleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物料号
     */
    @ApiModelProperty(value = "物料号")
    private String itemCode;
    /**
     * 物料描述
     */
    @ApiModelProperty(value = "物料描述")
    private String itemName;
    /**
     * 供应商代码
     */
    @ApiModelProperty(value = "供应商代码")
    private String supCode;
    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String supName;
    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String poCode;
    /**
     * 订单数量
     */
    @ApiModelProperty(value = "订单数量")
    private String poNUm;
    /**
     * 已交货数量
     */
    @ApiModelProperty(value = "已交货数量")
    private String rcvNum;

    /**
     * 未到货数量
     */
    @ApiModelProperty(value = "未到货数量")
    private String notRcvNum;

    /**
     * 未到货总数
     */
    @ApiModelProperty(value = "未到货总数")
    private String notRcvAllNum;

    /**
     * 计划送货总数
     */
    @ApiModelProperty(value = "计划送货总数")
    private String orderAllNum;

    /**
     * 延期未交货数量
     */
    @ApiModelProperty(value = "延期未交货数量")
    private String hisNotRcvAllNum;

    /**
     * 本周计划送货
     * 数量
     */
    @ApiModelProperty(value = "本周计划送货数量")
    private String orderNumOfThisWeek;

    /**
     * 本周可到货
     * 数量
     */
    @ApiModelProperty(value = "本周可到货数量")
    private String toRcvNumOfThisWeek;

    /**
     * 本周欠交
     * 数量
     */
    @ApiModelProperty(value = "本周欠交数量")
    private String oweNumOfThisWeek;

    /**
     * 本周可交货
     * 日期
     */
    @ApiModelProperty(value = "本周可交货日期")
    private String deliverDateOfThisWeek;


    /**
     * 下周计划送货
     * 数量
     */
    @ApiModelProperty(value = "下周计划送货数量")
    private String orderNumOfNextWeek;

    /**
     * 下周可到货
     * 数量
     */
    @ApiModelProperty(value = "下周可到货数量")
    private String toRcvNumOfNextWeek;

    /**
     * 下周可交货
     * 日期
     */
    @ApiModelProperty(value = "下周可交货日期")
    private String deliverDateOfNextWeek;

    /**
     * 下下周计划送货
     * 数量
     */
    @ApiModelProperty(value = "下下周计划送货数量")
    private String orderNumOfThirdWeek;

    /**
     * 下下周可到货
     * 数量
     */
    @ApiModelProperty(value = "下下周可到货数量")
    private String toRcvNumOfThirdWeek;

    /**
     * 下下周可交货
     * 日期
     */
    @ApiModelProperty(value = "下下周可交货日期")
    private String deliverDateOfThirdWeek;

    /**
     * 第四周计划送货
     * 数量
     */
    @ApiModelProperty(value = "第四周计划送货数量")
    private String orderNumOfForthWeek;

    /**
     * 第四周可到货
     * 数量
     */
    @ApiModelProperty(value = "第四周可到货数量")
    private String toRcvNumOfForthWeek;

    /**
     * 第四周可交货
     * 日期
     */
    @ApiModelProperty(value = "第四周可交货日期")
    private String deliverDateOfForthWeek;

    /**
     * 第五周计划送货
     * 数量
     */
    @ApiModelProperty(value = "第五周计划送货数量")
    private String orderNumOfFifthWeek;

    /**
     * 第五周可到货
     * 数量
     */
    @ApiModelProperty(value = "第五周可到货数量")
    private String toRcvNumOfFifthWeek;

    /**
     * 第五周可交货
     * 日期
     */
    @ApiModelProperty(value = "第五周可交货日期")
    private String deliverDateOfFifthWeek;

    /**
     * 未来周计划送货
     * 数量
     */
    @ApiModelProperty(value = "未来周计划送货数量")
    private String orderNumOfFutureWeek;

    /**
     * 未来周可到货
     * 数量
     */
    @ApiModelProperty(value = "未来周可到货数量")
    private String toRcvNumOfFutureWeek;

    /**
     * 未来周可交货
     * 日期
     */
    @ApiModelProperty(value = "未来周可交货日期")
    private String deliverDateOfFutureWeek;

}
