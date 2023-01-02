package org.springblade.modules.helpcenter.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.helpcenter.entity.ClassEntity;

/**
 * 帮助分类 模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClassDTO extends ClassEntity {

	private static final long serialVersionUID = 1L;

}
