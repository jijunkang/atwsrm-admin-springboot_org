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
@TableName("atw_prepay_order_item")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PrepayOrderItem对象", description = "")
public class PrepayOrderItemEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * SRM请款单ID
     */
    @ApiModelProperty(value = "SRM请款单ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long prepayId;
    /**
     * SRM请款单号
     */
    @ApiModelProperty(value = "SRM请款单号")
    private String prepayCode;
    /**
     * U9请款单号
     */
    @ApiModelProperty(value = "U9请款单号")
    private String prepayCodeU9;
    /**
     * SRM请款单行号
     */
    @ApiModelProperty(value = "SRM请款单行号")
    private Integer prepayLn;

    @ApiModelProperty(value = "采购单ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long poId;
    /**
     * 采购单号
     */
    @ApiModelProperty(value = "采购单号")
    private String poCode;
    /**
     * 是否是第一次请款
     */
    @ApiModelProperty(value = "是否是第一次请款")
    private Integer    isPoFirst;

    @ApiModelProperty(value = "本次预付比例")
    private Double prepayRate;

    @ApiModelProperty(value = "小计")
    private BigDecimal subtotal;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String     remark;
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

    @ApiModelProperty(value = "组织代码")
    private String orgCode;


}
