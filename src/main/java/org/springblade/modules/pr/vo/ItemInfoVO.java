package org.springblade.modules.pr.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ItemInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料编号")
    private String itemCode;

    @ApiModelProperty(value = "物料名称")
    private String itemName;

}
