package org.springblade.modules.finance.entity;

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
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_prepay_order")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PrepayOrder对象", description = "")
public class PrepayOrderEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * SRM单号
     */
    @ApiModelProperty(value = "SRM单号")
    private String code;
    /**
     * U9单号
     */
    @ApiModelProperty(value = "U9单号")
    private String     u9Code;
    /**
     * 总金额
     */
    @ApiModelProperty(value = "总金额")
    private BigDecimal amount;
    /**
     * 支付日期
     */
    @ApiModelProperty(value = "支付日期")
    private Long    reqPayTime;

    @ApiModelProperty(value = "实际支付时间")
    private Long    realPayTime;

    @ApiModelProperty(value = "申请日期")
    private Long applyTime;

    @ApiModelProperty(value = "供应商代码")
    private String supCode;
    /**
     * 供应商名
     */
    @ApiModelProperty(value = "供应商名")
    private String supName;
    /**
     * 项目号
     */
    @ApiModelProperty(value = "项目号")
    private String proNo;
    /**
     * U9状态
     */
    @ApiModelProperty(value = "U9状态")
    private String u9StatusCode;
    /**
     * U9状态
     */
    @ApiModelProperty(value = "U9状态")
    private String u9Status;
    /**
     * 最后同步U9时间
     */
    @ApiModelProperty(value = "最后同步U9时间")
    private Integer lastSyncTime;
    /**
     *
     */
    @ApiModelProperty(value = "")
    private String sysDatagram;
    /**
     * 系统日志
     */
    @ApiModelProperty(value = "系统日志")
    private String sysLog;

    @ApiModelProperty(value = "申请人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long applyUserId;

    @ApiModelProperty(value = "申请人Code")
    private String applyUserCode;


    /**
     * 一级审核
     */
    @ApiModelProperty(value = "一级审核")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long audit1;
    /**
     * 二级审核
     */
    @ApiModelProperty(value = "二级审核")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long audit2;
    /**
     * 三级审核
     */
    @ApiModelProperty(value = "三级审核")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long audit3;

    /**
     * 是否打印
     */
    @ApiModelProperty(value = "是否打印")
    private Integer isPrint;

    @ApiModelProperty(value = "组织代码")
    private String orgCode;
}
