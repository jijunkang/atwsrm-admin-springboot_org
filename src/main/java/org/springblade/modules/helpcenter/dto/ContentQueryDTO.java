package org.springblade.modules.helpcenter.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yh
 */
@Data
public class ContentQueryDTO implements Serializable {

	@ApiModelProperty(value = "code")
	private String code;

	/**
	 * 关键字
	 */
	@ApiModelProperty(value = "关键字")
	private String keyword;

}


