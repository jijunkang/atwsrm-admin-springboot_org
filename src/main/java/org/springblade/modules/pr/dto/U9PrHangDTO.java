package org.springblade.modules.pr.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author libin
 *
 * @date 13:46 2020/6/1
 **/
@Data
public class U9PrHangDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "prId")
    private Long prId;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "供应商承诺交期")
    private Long supConfirmDate;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "附件")
    private String attachment;

}
