package org.springblade.modules.ap.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.ap.entity.ApRcvEntity;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApRcvDTO extends ApRcvEntity {

	private static final long serialVersionUID = 1L;
    private String rcvIds;
    private String uploadUrlList;
    private String vmiStatus;
}
