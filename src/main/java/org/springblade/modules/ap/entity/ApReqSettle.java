package org.springblade.modules.ap.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "ApRcv对象", description = "")
public class ApReqSettle implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;
    private String settleRcvCode;
    private String settleRcvLn;
    private String reqRcvCode;
    private String reqRcvLn;
    private String reqPoCode;
    private String reqPoLn;
    private String reqRcvNum;
    private String reqAccumRecQty;
    private String vmiContractNew;
    private String vmiStatusNew;
}
