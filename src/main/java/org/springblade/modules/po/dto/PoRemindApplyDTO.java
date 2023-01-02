package org.springblade.modules.po.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 模型DTO
 * @author Will
 */
@Data
public
class PoRemindApplyDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * po_item_id
	 */
	@ApiModelProperty(value = "PoOffsetView.id")
	private Long poOffsetId;
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;
}
