package org.springblade.modules.ap.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ApRcv对象", description = "")
public class ApRcvReqEntity extends ApRcvEntity {

    private static final long serialVersionUID = 1L;

    private String reqId;

    private String reqRcvCode;

    private String reqRcvLn;

    private String reqPoCode;

    private String reqPoLn;

    private String reqRcvNum;

    private String vmiContractNew;

    private String vmiStatusNew;

    private String reqAccumRecQty;

    private String taxPricePo;

    private String subTotalPo;
}
