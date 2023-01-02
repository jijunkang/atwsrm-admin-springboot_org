package org.springblade.modules.bizinquiry.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.bizinquiry.entity.BizInquiryIoEntity;

/**
 * 模型DTO
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public
class BizInquiryIoDTO extends BizInquiryIoEntity{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "询价单编号")
    private String qoCode;
}
