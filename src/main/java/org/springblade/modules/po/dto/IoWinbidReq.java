package org.springblade.modules.po.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 模型DTO
 * @author Will
 */
@Data
public
class IoWinbidReq implements Serializable{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "IO.id")
    private Long ioId;

    @ApiModelProperty(value = "承诺交期")
    private Long promiseDate;

    @ApiModelProperty(value = "是否加入白名单")
    private Integer isIntoPriceLib;

    @ApiModelProperty(value = "白名单生效日期")
    private Date effectiveDate;

    @ApiModelProperty(value = "白名单失效日期")
    private Date expirationDate;

    @ApiModelProperty(value = "白名单起订量")
    private BigDecimal limitMin;

    @ApiModelProperty(value = "白名单附件")
    private String attachment;
}
