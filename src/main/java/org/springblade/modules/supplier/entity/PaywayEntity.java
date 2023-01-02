package org.springblade.modules.supplier.entity;

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
@TableName("atw_payway")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Payway对象", description = "")
public class PaywayEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String type;
    /**
     * 类型名称
     */
    @ApiModelProperty(value = "类型名称")
    private String typeName;
    /**
     * 首次预付比例
     */
    @ApiModelProperty(value = "首次预付比例")
    private BigDecimal firstPrepayRate;
    /**
     * 累计预付比例
     */
    @ApiModelProperty(value = "累计预付比例")
    private BigDecimal accumPrepayRate;
    /**
     * 是否默认
     */
    @ApiModelProperty(value = "是否默认")
    private Boolean isDefault;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "账期")
    private Integer payDate;
    /**
     * 供应商代码
     */
    @ApiModelProperty(value = "供应商代码")
    private String supCode;
    /**
     * 供应商名
     */
    @ApiModelProperty(value = "供应商名")
    private String supName;
    /**
     * 最后同步U9时间
     */
    @ApiModelProperty(value = "最后同步U9时间")
    private Integer lastSyncTime;
    /**
     * sys
     */
    @ApiModelProperty(value = "sys")
    private String sysDatagram;
    /**
     * 系统日志
     */
    @ApiModelProperty(value = "系统日志")
    private String sysLog;

}
