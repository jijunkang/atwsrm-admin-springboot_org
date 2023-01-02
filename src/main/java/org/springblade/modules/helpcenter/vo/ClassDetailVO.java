package org.springblade.modules.helpcenter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.helpcenter.entity.ClassEntity;

/**
 * 帮助分类 模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClassDetailVO extends ClassEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "父级分类id")
	private String parentClassName;
}
