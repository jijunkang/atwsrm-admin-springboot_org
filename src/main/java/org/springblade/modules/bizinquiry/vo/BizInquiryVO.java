package org.springblade.modules.bizinquiry.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.bizinquiry.entity.BizInquiryEntity;

import java.math.BigDecimal;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizInquiryVO extends BizInquiryEntity {

	private static final long serialVersionUID = 1L;

    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String supName;
    /**
     * 报价
     */
    @ApiModelProperty(value = "报价")
    private BigDecimal price;
    /**
     * 确认交期
     */
    @ApiModelProperty(value = "确认交期")
    private Long confirmDate;
    /**
     * 交货期
     */
    @ApiModelProperty(value = "交货期")
    private String deliveryDate;
    /**
     * 报价有效期
     */
    @ApiModelProperty(value = "报价有效期")
    private String offerValidity;
    /**
     * 价格归属
     */
    @ApiModelProperty(value = "价格归属")
    private Integer attribution;
    /**
     * 拒绝原因
     */
    @ApiModelProperty(value = "拒绝原因")
    private String backReason;
    /**
     * 附件
     */
    @ApiModelProperty(value = "附件")
    private String attachment;
    /**
     * 供应商反馈
     */
    @ApiModelProperty(value = "供应商反馈")
    private String supFeedback;

}
