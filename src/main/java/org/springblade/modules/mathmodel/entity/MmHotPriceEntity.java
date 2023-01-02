package org.springblade.modules.mathmodel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 实体类
 * @author Will
 */
@Data
@TableName("atw_mm_hot_price")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MmHotPrice对象", description = "")
public
class MmHotPriceEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 材料牌号
     */
    @ApiModelProperty(value = "材料牌号")
    private String     metal;
    /**
     * 热处理单价
     */
    @ApiModelProperty(value = "热处理单价")
    private BigDecimal hotPrice;
    /**
     * 飞削单价
     */
    @ApiModelProperty(value = "飞削单价")
    private BigDecimal cutPrice;
    /**
     * 供应商编号
     */
    @ApiModelProperty(value = "供应商编号")
    private String     supCode;
    /**
     * 供应商描述
     */
    @ApiModelProperty(value = "供应商名称")
    private String     supName;
    /**
     * 历史记录ID
     */
    @ApiModelProperty(value = "历史记录ID")
    private Long       historyId;
    private Date       deletedTime;
}
