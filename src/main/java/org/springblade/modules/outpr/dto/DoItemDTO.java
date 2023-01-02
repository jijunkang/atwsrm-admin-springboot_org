package org.springblade.modules.outpr.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 模型DTO
 * @author Will
 */
@Data
public
class DoItemDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String rcvCode;
    private String itemCode;
}
