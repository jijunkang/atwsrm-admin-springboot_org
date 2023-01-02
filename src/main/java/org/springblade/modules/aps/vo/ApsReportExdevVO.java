package org.springblade.modules.aps.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.aps.entity.ApsReportExdevEntity;

/**
 * 模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApsReportExdevVO extends ApsReportExdevEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 采购交期偏移
     */
    @ApiModelProperty(value = "采购交期偏移")
    private Integer offsetDays;

    /**
     * 修改交期
     */
    @ApiModelProperty(value = "修改交期")
    private Long applyModifyDeliDate;
}
