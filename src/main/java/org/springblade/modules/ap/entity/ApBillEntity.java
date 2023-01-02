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
import java.util.Date;
import java.time.LocalDateTime;


/**
 * 实体类
 *
 * @author Will
 */
@Data
@TableName("atw_ap_bill")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ApBill对象", description = "")
public class ApBillEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 应付单号
     */
    @ApiModelProperty(value = "应付单号")
    private String billCode;
    /**
     * U9编号
     */
    @ApiModelProperty(value = "U9编号")
    private String u9Code;
    /**
     * U9生成日期
     */
    @ApiModelProperty(value = "U9生成日期")
    private Long u9Date;
    /**
     * 对账单金额(含税)
     */
    @ApiModelProperty(value = "对账单金额(含税)")
    private BigDecimal taxAmount;
    /**
     * 请款金额
     */
    @ApiModelProperty(value = "请款金额")
    private BigDecimal purAmount;
    /**
     * 请款采购员
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "请款采购员")
    private Long purBuyer;
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
     * 项目号
     */
    @ApiModelProperty(value = "项目号")
    private String proNo;
    /**
     * 预计付款日期
     */
    @ApiModelProperty(value = "预计付款日期")
    private Long prepayDate;
    /**
     * 到票日期
     */
    @ApiModelProperty(value = "到票日期")
    private Long invoiceDate;
    /**
     * 对账类型
     */
    @ApiModelProperty(value = "对账类型")
    private String type;
    /**
     * 一级审批
     */
    @ApiModelProperty(value = "一级审批")
    private Long audit1;
    /**
     * 二级审批
     */
    @ApiModelProperty(value = "二级审批")
    private Long audit2;
    /**
     * 系统日志
     */
    @ApiModelProperty(value = "系统日志")
    private String sysLog;
    /**
     * 退回日期
     */
    @ApiModelProperty(value = "退回日期")
    private Integer backDate;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * SRM生成日期
     */
    @ApiModelProperty(value = "SRM生成日期")
    private Long srmDate;

    private String orgCode;

}
