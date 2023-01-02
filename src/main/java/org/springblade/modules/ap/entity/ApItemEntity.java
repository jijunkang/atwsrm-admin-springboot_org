package org.springblade.modules.ap.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author libin
 * @date 11:32 2020/6/3
 **/
@Data
@TableName("atw_ap_item")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ApItem对象", description = "")
public class ApItemEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * billId
     */
    @ApiModelProperty(value = "billId")
    private Long billId;
    /**
     * rcvId
     */
    @ApiModelProperty(value = "billCode")
    private String billCode;
    /**
     * rcvId
     */
    @ApiModelProperty(value = "rcvId")
    private Long rcvId;
    /**
     * 到货日期
     */
    @ApiModelProperty(value = "到货日期")
    private Long rcvDate;
    /**
     * RCV单号
     */
    @ApiModelProperty(value = "RCV单号")
    private String rcvCode;
    /**
     * RCV行号
     */
    @ApiModelProperty(value = "RCV行号")
    private Integer rcvLn;
    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String poCode;
    /**
     * 订单行号
     */
    @ApiModelProperty(value = "订单行号")
    private Integer poLn;
    /**
     * 物料编号
     */
    @ApiModelProperty(value = "物料编号")
    private String itemCode;
    /**
     * 物料名称
     */
    @ApiModelProperty(value = "物料名称")
    private String itemName;
    /**
     * 实收数量
     */
    @ApiModelProperty(value = "实收数量")
    private BigDecimal rcvActualQty;
    /**
     * 本次对账数量
     */
    @ApiModelProperty(value = "本次对账数量")
    private BigDecimal recThisQty;
    /**
     * 未税单价
     */
    @ApiModelProperty(value = "未税单价")
    private BigDecimal price;
    /**
     * 含税单价
     */
    @ApiModelProperty(value = "含税单价")
    private BigDecimal taxPrice;
    /**
     * 未税小计
     */
    @ApiModelProperty(value = "未税小计")
    private BigDecimal subTotal;
    /**
     * 含税小计
     */
    @ApiModelProperty(value = "含税小计")
    private BigDecimal taxSubTotal;
    /**
     * 税组合
     */
    @ApiModelProperty(value = "税组合")
    private BigDecimal taxRate;
    /**
     * 应付单ID
     */
    @ApiModelProperty(value = "应付单ID")
    private Long apId;
    /**
     * 应付单号
     */
    @ApiModelProperty(value = "应付单号")
    private String apCode;
    /**
     * 应付单行号
     */
    @ApiModelProperty(value = "应付单行号")
    private Integer apLn;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer status;
    /**
     * 税额
     */
    @ApiModelProperty(value = "税额")
    private BigDecimal tax;
    /**
     * 计价单位
     */
    @ApiModelProperty(value = "计价单位")
    private String uom;
    /**
     * 预付冲应付金额
     */
    @ApiModelProperty(value = "预付冲应付金额")
    private BigDecimal pipPrice;
    /**
     * 请购金额
     */
    @ApiModelProperty(value = "请购金额")
    private BigDecimal purPrice;
    /**
     * 预计付款日期
     */
    @ApiModelProperty(value = "预计付款日期")
    private Long prepayDate;

    @ApiModelProperty(value = "组织代码")
    private String orgCode;
}
