package org.springblade.modules.finance.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.finance.entity.PrepayOrderItemEntity;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrepayOrderItemDTO extends PrepayOrderItemEntity {

	private static final long serialVersionUID = 1L;

	private Integer prepayStatus;
    @ApiModelProperty(value = "申请人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long applyUserId;

}
