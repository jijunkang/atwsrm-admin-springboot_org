package org.springblade.modules.po.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.po.entity.PoTracelogEntity;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PoTracelogDTO extends PoTracelogEntity {

	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "PoOffsetView.id")
	private Long poOffsetId;
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;
}
