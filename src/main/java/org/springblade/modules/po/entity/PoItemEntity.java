package org.springblade.modules.po.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 采购订单明细 实体类
 *
 * @author Will
 */
@Data
@TableName("atw_po_item")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PoItem对象", description = "采购订单明细")
public
class PoItemEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 采购单id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购单id")
    private     Long   poId;
    /**
     * 采购订单号
     */
    @ApiModelProperty(value = "采购订单号")
    private String poCode;
    /**
     * 采购订单行号
     */
    @ApiModelProperty(value = "采购订单行号")
    private Integer poLn;
    /**
     * 料品号
     */
    @ApiModelProperty(value = "料品号")
    private String itemCode;
    /**
     * 料品描述
     */
    @ApiModelProperty(value = "料品描述")
    private String itemName;
    /**
     * 供应商code
     */
    @ApiModelProperty(value = "供应商编码")
    private String supCode;
    /**
     * 供应商名
     */
    @ApiModelProperty(value = "供应商名")
    private String supName;
    /**
     * 计量数量
     */
    @ApiModelProperty(value = "计量数量")
    private BigDecimal priceNum;
    /**
     * 计量单位
     */
    @ApiModelProperty(value = "计量单位")
    private String priceUom;
    /**
     * 交易数量
     */
    @ApiModelProperty(value = "交易数量")
    private BigDecimal tcNum;
    /**
     * 交易单位
     */
    @ApiModelProperty(value = "交易单位")
    private String tcUom;
    /**
     * 请购单id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "请购单id")
    private Long prId;
    /**
     * 请购单号
     */
    @ApiModelProperty(value = "请购单号")
    private String prCode;
    /**
     * 请购单行号
     */
    @ApiModelProperty(value = "请购单行号")
    private Integer prLn;
    /**
     * 项目号
     */
    @ApiModelProperty(value = "项目号")
    private String proNo;

    @ApiModelProperty(value = "要求交期")
    private Long reqDate;
    @ApiModelProperty(value = "供应商承诺交期")
    private Long supConfirmDate;

    @ApiModelProperty(value = "供应商修改后的交期")
    private Long supUpdateDate;

    @ApiModelProperty(value = "采购单价")
    private BigDecimal price;

    @ApiModelProperty(value = "含税单价")
    private BigDecimal taxPrice;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;
    /**
     * 税率
     */
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "采购员工号")
    private String purchCode;

    @ApiModelProperty(value = "采购员工姓名")
    private String purchName;

    @ApiModelProperty(value = "跟单员工号")
    private String traceCode;

    @ApiModelProperty(value = "跟单员工姓名")
    private String traceName;

    @ApiModelProperty(value = "到货数量")
    private BigDecimal rcvGoodsNum;

    @ApiModelProperty(value = "实收数量")
    private BigDecimal arvGoodsNum;

    @ApiModelProperty(value = "未到货数量")
    private BigDecimal proGoodsNum;
    /**
     * 退货数量
     */
    @ApiModelProperty(value = "退货数量")
    private BigDecimal returnGoodsNum;

    @ApiModelProperty(value = "退货需要补货数量")
    private BigDecimal fillGoodsNum;

    @ApiModelProperty(value = "U9状态")
    private String u9Status;
    /**
     * U9状态
     */
    @ApiModelProperty(value = "U9状态")
    private String u9StatusCode;
    /**
     * 最后同步U9时间
     */
    @ApiModelProperty(value = "最后同步U9时间")
    private Long lastSyncTime;
    /**
     * 中标时间
     */
    @ApiModelProperty(value = "中标时间")
    private Long winbidTime;
    /**
     * 交易单位编码
     */
    @ApiModelProperty(value = "交易单位编码")
    private String tcUomCode;
    /**
     * 计价单位编码
     */
    @ApiModelProperty(value = "计价单位编码")
    private String priceUomCode;

    @ApiModelProperty(value = "是否是赠品")
    private Integer isPersent;

    @ApiModelProperty(value = "工费")
    private BigDecimal laborCost;

    @ApiModelProperty(value = "材料费")
    private BigDecimal materialCost;

    @ApiModelProperty(value = "备注")
    private String     remark;

    @ApiModelProperty(value = "来源")
    private String     source;

    @ApiModelProperty(value = "来源id")
    private Long sourceId;

    @ApiModelProperty(value = "最终用户")
    private String  endUser;

    @ApiModelProperty(value = "未税单价")
    private BigDecimal preTaxPrice;

    @ApiModelProperty(value = "可交付物是否齐全 ")
    private Integer isDeliverablesFull;

    @ApiModelProperty(value = "isSpilt ")
    private Integer isSpilt;

    @ApiModelProperty(value = "最后跟单日时间")
    private Date lastTraceTime;

    @ApiModelProperty(value = "最后跟单日员")
    private Long lastTracer;

    @ApiModelProperty(value = "附件")
    private String     attachment; // 2020.05.11 新加 存放流标录入的附件和报名单

    @ApiModelProperty(value = "炉批号")
    private String furnaceNo;

    @ApiModelProperty(value = "子件BOM描述")
    private String subBomDesc;

    @ApiModelProperty(value = "供应商修改后的交期是否需要审核")
    private Integer isSupUpdate;

    @ApiModelProperty(value = "供应商修改后的交期需要审核")
    private Long supUpdateDateCheck;

    @ApiModelProperty(value = "运算交期")
    private Long operationDate;

    @ApiModelProperty(value = "业务分支")
    private String     bizBranch;

    @ApiModelProperty(value = "是否自动下单")
    private Integer isAutoOrder;

    @ApiModelProperty(value = "第一批交货日期")
    private Long firstDeliveryDate;

    @ApiModelProperty(value = "第一批交货数量")
    private BigDecimal firstDeliveryNum;

    @ApiModelProperty(value = "第二批交货日期")
    private Long secondDeliveryDate;

    @ApiModelProperty(value = "第二批交货数量")
    private BigDecimal secondDeliveryNum;

    @ApiModelProperty(value = "第三批交货日期")
    private Long thirdDeliveryDate;

    @ApiModelProperty(value = "第三批交货数量")
    private BigDecimal thirdDeliveryNum;

    @ApiModelProperty(value = "是否按重量计算")
    private Integer isByWeight;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "修改后的单价")
    private BigDecimal priceUpdate;

    @ApiModelProperty(value = "修改后的金额")
    private BigDecimal amountUpdate;

    @ApiModelProperty(value = "修改金额备注")
    private BigDecimal updatePriceRemark;

    @ApiModelProperty(value = "生产单号")
    private String moNo;

    @ApiModelProperty(value = "组织代码")
    private String orgCode;

    public
    void setAmount(BigDecimal amount){
        if(amount == null){
            this.amount = amount;
            return;
        }
        this.amount = amount.setScale(2,BigDecimal.ROUND_HALF_UP);
    }

}
