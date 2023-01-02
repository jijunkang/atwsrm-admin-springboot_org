package org.springblade.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 指派DTO
 * @author Will
 */
@Data
public
class AssignDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("业务 id")
    private Long bizId;

    @ApiModelProperty("用户id")
    private Long userId;

}
