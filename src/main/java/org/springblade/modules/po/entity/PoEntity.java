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


/**
 * 采购订单表头 实体类
 * @author Will
 */
@Data
@TableName("atw_po")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Po对象", description = "采购订单表头")
public
class PoEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String        orderCode;
    /**
     * 单据类型 U9参数  PO01
     */
    @ApiModelProperty(value = "单据类型 U9参数  PO01")
    private String     docType;
    /**
     * 订单总金额
     */
    @ApiModelProperty(value = "订单总金额")
    private BigDecimal docAmount;
    /**
     * 下单日期
     */
    @ApiModelProperty(value = "下单日期")
    private Integer    docDate;
    /**
     * 供应商编码
     */
    @ApiModelProperty(value = "供应商编码")
    private String supCode;
    /**
     * 供应商名
     */
    @ApiModelProperty(value = "供应商名")
    private String        supName;
    /**
     * 项目号
     */
    @ApiModelProperty(value = "项目号")
    private String        proNo;
    /**
     * 币种编码  C001:人民币
     */
    @ApiModelProperty(value = "币种编码  C001:人民币")
    private String        tcCode;
    /**
     * U9参数 固定值：316
     */
    @ApiModelProperty(value = "U9参数 固定值：316")
    private String        bizType;
    /**
     * 是否含税
     */
    @ApiModelProperty(value = "是否含税")
    private Integer       isIncludeTax;
    /**
     * 合同映象
     */
    @ApiModelProperty(value = "合同映象")
    private String        contract;
    /**
     * 签订合同时间
     */
    @ApiModelProperty(value = "签订合同时间")
    private Integer       contractTime;
    /**
     * 合同审核备注/拒绝原因
     */
    @ApiModelProperty(value = "合同审核备注/拒绝原因")
    private String        remark;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Integer       createAt;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Integer       updateAt;
    /**
     * 阅读时间
     */
    @ApiModelProperty(value = "阅读时间")
    private Integer       readAt;
    /**
     * U9状态
     */
    @ApiModelProperty(value = "U9状态")
    private String        u9StatusCode;
    /**
     * U9状态
     */
    @ApiModelProperty(value = "U9状态")
    private String        u9Status;
    /**
     * 最后同步U9时间
     */
    @ApiModelProperty(value = "最后同步U9时间")
    private Integer lastSyncTime;
    /**
     * 拒绝原因
     */
    @ApiModelProperty(value = "拒绝原因")
    private String        cancelCause;
    /**
     * 是否是业务关闭
     */
    @ApiModelProperty(value = "是否是业务关闭")
    private Integer       isBizClosed;
    /**
     * 是否推送消息给供应商
     */
    @ApiModelProperty(value = "是否推送消息给供应商")
    private Integer       isPushMsg;

    @ApiModelProperty(value = "指派类型")
    private String cancelAssign;

    @ApiModelProperty(value = "指派类型给谁")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cancelAssignId;

    @ApiModelProperty(value = "支付方式")
    private String payWay;

    @ApiModelProperty(value = "首次预付比例")
    private Double firstPrepayRate;

    @ApiModelProperty(value = "累计预付比例")
    private Double accumPrepayRate;

    @ApiModelProperty(value = "累计预付金额")
    private BigDecimal accumPrepay;

    @ApiModelProperty(value = "实际累计预付金额")
    private BigDecimal prepaidTotal;

    @ApiModelProperty(value = "实际累计应付付金额")
    private BigDecimal accpaidTotal;

    @ApiModelProperty(value = "账期")
    private Integer payDate;  //@Will add 2020-07-02 10:09:25

    @ApiModelProperty(value = "附加条款")
    private String addClause;

    @ApiModelProperty(value = "合同状态")
    private Integer contractStatus;

    @ApiModelProperty(value = "是否按重量计算")
    private Integer isByWeight;

    @ApiModelProperty(value = "订单金额审核备注")
    private String priceRemark;

    @ApiModelProperty(value = "修改后的订单总金额")
    private BigDecimal docAmountUpdate;

    @ApiModelProperty(value = "模板合同")
    private String templateType;

    @ApiModelProperty(value = "组织代码")
    private String orgCode;
}
