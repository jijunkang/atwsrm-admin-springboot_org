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
 * 实体类
 * @author Will
 */
@Data
@TableName("atw_io")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Io对象", description = "")
public
class IoEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 请购单id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "请购单id")
    private Long       prId;
    /**
     * 请购单行号
     */
    @ApiModelProperty(value = "请购单行号")
    private Integer    prLn;
    /**
     * 请购单编码
     */
    @ApiModelProperty(value = "请购单编码")
    private String     prCode;
    /**
     * 物料编号
     */
    @ApiModelProperty(value = "物料编号")
    private String     itemCode;
    /**
     * 物料名称
     */
    @ApiModelProperty(value = "物料名称")
    private String     itemName;
    /**
     * 供应商编码
     */
    @ApiModelProperty(value = "供应商编码")
    private String     supCode;
    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String     supName;
    /**
     * 承诺交期
     */
    @ApiModelProperty(value = "承诺交期")
    private Long   promiseDate;

    @ApiModelProperty(value = "是否是赠品")
    private Integer isPersent;

    @ApiModelProperty(value = "报价")
    private BigDecimal quotePrice;
    /**
     * 报价日期
     */
    @ApiModelProperty(value = "报价日期")
    private Long    quoteDate;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String     remark;
    /**
     * 中标时间
     */
    @ApiModelProperty(value = "中标时间")
    private Long   winbidDate;
    /**
     * 报价截止日期
     */
    @ApiModelProperty(value = "报价截止日期")
    private Long    quoteEndtime;
    /**
     * 计价数量
     */
    @ApiModelProperty(value = "计价数量")
    private BigDecimal    priceNum;
    /**
     * 计价单位
     */
    @ApiModelProperty(value = "计价单位")
    private String     priceUom;
    /**
     * 交易数量
     */
    @ApiModelProperty(value = "交易数量")
    private BigDecimal    tcNum;
    /**
     * 交易单位
     */
    @ApiModelProperty(value = "交易单位")
    private String     tcUom;
    /**
     * 需求日期
     */
    @ApiModelProperty(value = "需求日期")
    private Long       reqDate;
    /**
     * 拒绝原因
     */
    @ApiModelProperty(value = "拒绝原因")
    private String     refuseCause;
    /**
     * 供应商阅读时间
     */
    @ApiModelProperty(value = "供应商阅读时间")
    private Integer    readAt;
    /**
     * 最终用户
     */
    @ApiModelProperty(value = "最终用户")
    private String     endUser;

    /**
     * 加工费
     */
    @ApiModelProperty(value = "加工费")
    private BigDecimal laborCost;
    /**
     * 材料费
     */
    @ApiModelProperty(value = "材料费")
    private BigDecimal materialCost;

    /**
     * 材料费
     */
    @ApiModelProperty(value = "评标得分")
    private BigDecimal evaluateScore;
    /**
     * 来源  'model','protocol','quote'
     */
    @ApiModelProperty(value = "来源")
    private String  source;

    @ApiModelProperty(value = "供应商备注")
    private String supRemark;

    @ApiModelProperty(value = "附件")
    private String attachment;

    @ApiModelProperty(value = "业务分支")
    private String bizBranch;

    @ApiModelProperty(value = "是否按重量计算")
    private Integer isByWeight;
}
