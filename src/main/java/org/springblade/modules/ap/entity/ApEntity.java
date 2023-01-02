package org.springblade.modules.ap.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
@TableName("atw_ap")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Ap对象", description = "")
public class ApEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 应付单号
     */
    @ApiModelProperty(value = "应付单号")
    private String apCode;
    /**
     * 应付单号
     */
    @ApiModelProperty(value = "应付单号")
    private String u9Code;
    /**
     * 对账单金额
     */
    @ApiModelProperty(value = "对账单金额")
    private BigDecimal amount;
    /**
     * 对账单金额
     */
    @ApiModelProperty(value = "对账单金额(含税)")
    private BigDecimal taxAmount;
    /**
     * 预付冲应付金额
     */
    @ApiModelProperty(value = "预付冲应付金额")
    private BigDecimal pipAmount;
    /**
     * 请款金额
     */
    @ApiModelProperty(value = "请款金额")
    private BigDecimal purAmount;
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
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String type;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer status;
    /**
     * 系统日志
     */
    @ApiModelProperty(value = "系统日志")
    private String sysLog;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 退回日期
     */
    @ApiModelProperty(value = "退回日期")
    private Long backDate;
    /**
     * 退回采购员
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "退回采购员")
    private Long backBuyer;
    /**
     * SRM生成日期
     */
    @ApiModelProperty(value = "SRM生成日期")
    private Long srmDate;
    /**
     * 项目号
     */
    @ApiModelProperty(value = "项目号")
    private String proNo;
    /**
     * 一级审核
     */
    @ApiModelProperty(value = "一级审核")
    private Long audit1;
    /**
     * 二级审核
     */
    @ApiModelProperty(value = "二级审核")
    private Long audit2;

    @ApiModelProperty(value = "组织代码")
    private String orgCode;


}
