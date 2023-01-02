package org.springblade.modules.ap.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.time.LocalDateTime;


/**
 * 实体类
 *
 * @author Will
 */
@Data
@TableName("atw_ap_rcv")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ApRcv对象", description = "")
public class ApRcvEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 供应商编号
     */
    @ApiModelProperty(value = "供应商编号")
    private String supCode;
    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String supName;
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
     * 累计对账数量
     */
    @ApiModelProperty(value = "累计对账数量")
    private BigDecimal accumRecQty;
    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String uom;
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
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer status;
    /**
     * 项目号
     */
    @ApiModelProperty(value = "项目号")
    private String proNo;
    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String type;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 附件
     */
    @ApiModelProperty(value = "附件")
    private String attachment;
    /**
     * 拒绝原因
     */
    @ApiModelProperty(value = "拒绝原因")
    private String backReason;
    /**
     * 是否是NCR生成
     */
    @ApiModelProperty(value = "是否是NCR生成")
    private Integer isNcr;

    /**
     * 合同附件
     */
    @ApiModelProperty(value = "合同附件")
    private String vmiContract;

    /**
     * 合同状态
     */
    @ApiModelProperty(value = "合同状态")
    private String vmiStatus;

    @ApiModelProperty(value = "组织代码")
    private String orgCode;
}
