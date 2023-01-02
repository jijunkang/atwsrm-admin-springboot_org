package org.springblade.modules.bizinquiry.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 模型DTO
 *
 * @author Will
 */
@Data
public class BizInquiryIoToEsbDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "报价编号")
    private String projectnumber;

    @ApiModelProperty(value = "型号")
    private String modelnumber;

    @ApiModelProperty(value = "单价")
    private String unitprice;

    @ApiModelProperty(value = "交货期")
    private String deliverydate;

    @ApiModelProperty(value = "价格库归属")
    private String attribution;

    @ApiModelProperty(value = "报价有效期")
    private String offervalidity;

    @ApiModelProperty(value = "供应商反馈")
    private String supFeedback;

    @ApiModelProperty(value = "发送人名称")
    private String feedbackName;
}
