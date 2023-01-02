package org.springblade.modules.po.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.po.entity.PoItemEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 采购订单明细 模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PoItemVO extends PoItemEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "供应商编码")
    @Excel(name = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "供应商名")
    @Excel(name = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "采购订单号")
    @Excel(name = "采购单号")
    private String poCode;

    @ApiModelProperty(value = "采购订单行号")
    @Excel(name = "行号")
    private Integer poLn;

    @ApiModelProperty(value = "料品号")
    @Excel(name = "物料编号")
    private String itemCode;

    @ApiModelProperty(value = "料品描述")
    @Excel(name = "物料名称")
    private String itemName;

    @ApiModelProperty(value = "计量数量")
    // @Excel(name = "计量数量")
    private BigDecimal priceNum;

    @ApiModelProperty(value = "计量单位")
    // @Excel(name = "计量单位")
    private String priceUom;

    @ApiModelProperty(value = "交易数量")
    @Excel(name = "交易数量")
    private BigDecimal tcNum;

    @ApiModelProperty(value = "交易单位")
    @Excel(name = "交易单位")
    private String tcUom;

    @ApiModelProperty(value = "项目号")
    @Excel(name = "项目号")
    private String proNo;

    @Excel(name = "到货数量")
    private BigDecimal rcvGoodsNum;

    @Excel(name = "实收数量")
    private BigDecimal arvGoodsNum;

    @Excel(name = "未到货数量")
    private BigDecimal proGoodsNum;

    @Excel(name = "退补数量")
    private BigDecimal fillGoodsNum;

    @Excel(name = "要求交期")
    private String reqDateFmt;

    @Excel(name = "承诺交期")
    private String supConfirmDateFmt;

    // @Excel(name = "修改交期")
    private String supUpdateDateFmt;

    // @Excel(name = "第一批交货日期")
    private String firstDeliveryDateFmt;
    private Long firstDeliveryDate;

    // @Excel(name = "第一批交货数量")
    private BigDecimal firstDeliveryNum;

    // @Excel(name = "第二批交货日期")
    private String secondDeliveryDateFmt;
    private Long secondDeliveryDate;

    // @Excel(name = "第二批交货数量")
    private BigDecimal secondDeliveryNum;

    // @Excel(name = "第三批交货日期")
    private String thirdDeliveryDateFmt;
    private Long thirdDeliveryDate;

    // @Excel(name = "第三批交货数量")
    private BigDecimal thirdDeliveryNum;

//    @Excel(name = "单价")
//    private BigDecimal price;
//
//    @Excel(name = "小计")
//    private BigDecimal amount;

    @Excel(name = "最终用户")
    private String endUser;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @Excel(name = "状态")
    private String u9Status;

    //	@Excel(name = "资料齐全")
    private String isDeliverablesFullFmt;

    private Integer poStatus;

    private String isHavesup;

    private String bizType;

    List<PoItemCraftCtrlNodeVO> poItemCraftCtrlNodeVos;

    private String craftCtrlNodeName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long craftCtrlNodeId;

    @ApiModelProperty(value = "可用量")
    private BigDecimal availableQuantity;

    @ApiModelProperty(value = "项目占用量")
    private BigDecimal projectOccupancyNum;

    @ApiModelProperty(value = "请购单备注")
    private String requisitionRemark;

    @ApiModelProperty(value = "最小起订量")
    private BigDecimal purchMix;

    @ApiModelProperty(value = "安全库存量")
    private BigDecimal stockLowerLimit;

    @ApiModelProperty(value = "atw_u9_pr请购单号")
    private String prprCode;

    @ApiModelProperty(value = "未送货数量")
    private BigDecimal notSendNum;

    @ApiModelProperty(value = "送货数量")
    private BigDecimal rcvNum;

    @ApiModelProperty(value = "炉号")
    private String heatCode;

    @ApiModelProperty(value = "送货单备注")
    private String doRemark;

    @ApiModelProperty(value = "不合格数量")
    private Integer unqualifiedNum;

    @ApiModelProperty(value = "是否外检")
    private String isOutCheck;

    @ApiModelProperty(value = "是否VMI")
    private String isVmi;

    @ApiModelProperty(value = "ABC类")
    private String codeType;

    @ApiModelProperty(value = "合同模板")
    private String templateType;

    @ApiModelProperty(value = "是否紧急")
    private String isUrgent;

    @ApiModelProperty(value = "合同附件")
    private String vmiContract;

    @ApiModelProperty(value = "组织代码")
    private String orgCode;
}
