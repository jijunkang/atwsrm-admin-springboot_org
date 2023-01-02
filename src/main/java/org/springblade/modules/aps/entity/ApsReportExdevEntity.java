package org.springblade.modules.aps.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;


/**
 * 实体类
 *
 * @author Will
 */
@Data
@TableName("atw_aps_report_exdev")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ApsReportExdev对象", description = "")
public
class ApsReportExdevEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 项目号
     */
    @ApiModelProperty(value = "项目号")
    private String proNo;
    /**
     * 子项目号
     */
    @ApiModelProperty(value = "子项目号")
    private String proNoSub;
    /**
     * 合同交期
     */
    @ApiModelProperty(value = "合同交期")
    private Long contractDeliDate;
    /**
     * 计划交期
     */
    @ApiModelProperty(value = "计划交期")
    private Long planDeliDate;
    /**
     * 计划交期修改原因
     */
    @ApiModelProperty(value = "计划交期修改原因")
    private String planUpdateCause;
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
     * 料号
     */
    @ApiModelProperty(value = "料号")
    private String itemCode;
    /**
     * 物料名称
     */
    @ApiModelProperty(value = "物料名称")
    private String itemName;
    /**
     * 供应商编码
     */
    @ApiModelProperty(value = "供应商编码")
    private String supCode;
    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String supName;
    /**
     * 订单数量
     */
    @ApiModelProperty(value = "订单数量")
    private BigDecimal tcNum;
    /**
     * 项目需求数量
     */
    @ApiModelProperty(value = "项目需求数量")
    private BigDecimal proReqNum;
    /**
     * 最早到货日
     */
    @ApiModelProperty(value = "最早到货日")
    private Long poEarliestDeliDate;
    /**
     * 送货日期
     */
    @ApiModelProperty(value = "送货日期")
    private Long deliveryDate;
    /**
     * 交期
     */
    @ApiModelProperty(value = "交期")
    private Long poDeliDate;
    /**
     * 评审交期
     */
    @ApiModelProperty(value = "评审交期")
    private Long reviewDeliDate;
    /**
     * 责任人
     */
    @ApiModelProperty(value = "责任人")
    private String personInCharge;
    /**
     * 机加可压缩比例
     */
    @ApiModelProperty(value = "机加可压缩比例")
    private String machiningCompRate;
    /**
     * 机加最终评审完工日期
     */
    @ApiModelProperty(value = "机加最终评审完工日期")
    private Long machiningReviewCompleteDate;
    /**
     * 装配可压缩比例
     */
    @ApiModelProperty(value = "装配可压缩比例")
    private String fittingCompRate;
    /**
     * 装配最终评审完工日期
     */
    @ApiModelProperty(value = "装配最终评审完工日期")
    private Long fittingReviewCompleteDate;
    /**
     * 计划交期(评审后)
     */
    @ApiModelProperty(value = "计划交期(评审后)")
    private Long planDeliDateReview;
    /**
     * NCR编号
     */
    @ApiModelProperty(value = "NCR编号")
    private String ncrNo;
    /**
     * 系统备注
     */
    @ApiModelProperty(value = "系统备注")
    private String sysLog;

}
