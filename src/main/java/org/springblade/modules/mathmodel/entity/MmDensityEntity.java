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
@TableName("atw_mm_density")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MmDensity对象", description = "")
public
class MmDensityEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 材质
     */
    @ApiModelProperty(value = "材质")
    private String     metal;
    /**
     * 密度
     */
    @ApiModelProperty(value = "密度")
    private BigDecimal density;
    /**
     * 历史记录ID
     */
    @ApiModelProperty(value = "历史记录ID")
    private Long       historyId;

    private Date deletedTime;

}
