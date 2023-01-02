package org.springblade.modules.bizinquiry.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;


/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_biz_inquiry_io")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "BizInquiryIo对象", description = "")
public class BizInquiryIoEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 商务询价单id
	 */
    @JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "商务询价单id")
	private Long qoId;
	/**
	 * 商务询价编号
	 */
	@ApiModelProperty(value = "商务询价编号")
	private String qoCode;
	/**
	 * 供应商名称
	 */
	@ApiModelProperty(value = "供应商名称")
	private String supName;
	/**
	 * 报价
	 */
	@ApiModelProperty(value = "报价")
    @TableField(value = "price",updateStrategy= FieldStrategy.IGNORED)
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
