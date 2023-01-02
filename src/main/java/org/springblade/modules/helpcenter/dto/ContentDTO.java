package org.springblade.modules.helpcenter.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.helpcenter.entity.ContentEntity;

/**
 * 帮助内容 模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContentDTO extends ContentEntity {

	private static final long serialVersionUID = 1L;

}
