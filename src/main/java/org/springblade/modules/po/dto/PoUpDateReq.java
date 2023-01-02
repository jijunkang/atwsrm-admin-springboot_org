package org.springblade.modules.po.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author libin
 *
 * @date 9:56 2020/6/2
 **/
@Data
public class PoUpDateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "poItemId")
    private Long poItemId;

    @ApiModelProperty(value = "供应商修改交期")
    private Long date;

    @ApiModelProperty(value = "备注")
    private String note;

}
