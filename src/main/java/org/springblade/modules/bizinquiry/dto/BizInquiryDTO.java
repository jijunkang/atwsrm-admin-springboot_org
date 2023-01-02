package org.springblade.modules.bizinquiry.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.bizinquiry.entity.BizInquiryEntity;

import java.math.BigDecimal;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizInquiryDTO extends BizInquiryEntity {

	private static final long serialVersionUID = 1L;


}
