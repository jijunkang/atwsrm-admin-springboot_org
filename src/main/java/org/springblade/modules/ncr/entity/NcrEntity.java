package org.springblade.modules.ncr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_ncr")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Ncr对象", description = "")
public class NcrEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 处理方式
     */
    @ApiModelProperty(value = "处理方式")
    private String processType;
    /**
     * 返修流程
     */
    @ApiModelProperty(value = "返修流程")
    private String repairProcess;
    /**
     * 罚款金额
     */
    @ApiModelProperty(value = "罚款金额")
    private BigDecimal finePrice;
    /**
     * NCR来源
     */
    @ApiModelProperty(value = "NCR来源")
    private String source;
    /**
     * 发生时间
     */
    @ApiModelProperty(value = "发生时间")
    private Long discovererTime;
    /**
     * 要求完成日期
     */
    @ApiModelProperty(value = "要求完成日期")
    private Long reqTime;
    /**
     * 项目号
     */
    @ApiModelProperty(value = "项目号")
    private String proNo;
    /**
     * 采购单编号
     */
    @ApiModelProperty(value = "采购单编号")
    private String poCode;
    /**
     * 采购单行号
     */
    @ApiModelProperty(value = "采购单行号")
    private Integer poLn;
    /**
     * 报告编号
     */
    @ApiModelProperty(value = "报告编号")
    private String reportCode;
    /**
     * 不合格品单号
     */
    @ApiModelProperty(value = "不合格品单号")
    private String code;
    /**
     * 不合格品数量
     */
    @ApiModelProperty(value = "不合格品数量")
    private Double unQualifiedQty;
    /**
     * 炉批号
     */
    @ApiModelProperty(value = "炉批号")
    private String furnaceNo;
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
     * 结案人
     */
    @ApiModelProperty(value = "结案人")
    private String caseName;
    /**
     * 结案时间
     */
    @ApiModelProperty(value = "结案时间")
    private Long caseTime;
    /**
     * 问题描述
     */
    @ApiModelProperty(value = "问题描述")
    private String problemDesc;
    /**
     * 确认人
     */
    @ApiModelProperty(value = "确认人")
    private String confirmName;
    /**
     * 确认时间
     */
    @ApiModelProperty(value = "确认时间")
    private Long confirmTime;
    /**
     * 根本原因
     */
    @ApiModelProperty(value = "根本原因")
    private String cause;
    /**
     * 不良品图片
     */
    @ApiModelProperty(value = "不良品图片")
    private String imgs;
    /**
     * 扣款单号
     */
    @ApiModelProperty(value = "扣款单号")
    private String rcvCode;
    /**
     * 是否接受罚款
     */
    @ApiModelProperty(value = "是否接受罚款")
    private Integer isAccept;
}
