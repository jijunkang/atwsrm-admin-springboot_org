package org.springblade.modules.bizinquiry.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springblade.core.mp.BladeMetaObjectHandler;
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
@TableName("atw_biz_inquiry")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "BizInquiry对象", description = "")
public class BizInquiryEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 询价单编号
	 */
	@ApiModelProperty(value = "询价单编号")
	private String qoCode;
    /**
     * 型号
     */
    @ApiModelProperty(value = "阀门类型")
    private String type;
	/**
	 * 物料描述
	 */
	@ApiModelProperty(value = "型号")
	private String model;
	/**
	 * 品牌
	 */
	@ApiModelProperty(value = "品牌")
	private String brand;
	/**
	 * 数量
	 */
	@ApiModelProperty(value = "数量")
	private BigDecimal num;
    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String uom;
    /**
     * 申请人
     */
    @ApiModelProperty(value = "申请人")
    private String applyUser;
    /**
     * 申请日期
     */
    @ApiModelProperty(value = "申请日期")
    private Long applyDate;
	/**
	 * 最终用户
	 */
	@ApiModelProperty(value = "最终用户")
	private String endUser;
	/**
	 * 需求日期
	 */
	@ApiModelProperty(value = "需求日期")
	private Long reqDate;
	/**
	 * 中标的报价id
	 */
    @JsonSerialize(
        using = ToStringSerializer.class
    )
	@ApiModelProperty(value = "中标的报价id")
	private Long winioId;
    /**
     * 特殊需求
     */
    @ApiModelProperty(value = "特殊需求")
    private String remark;
    /**
     * 招标单位
     */
    @ApiModelProperty(value = "招标单位")
    private String tenderingOrg;
    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    private String contactName;
    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String contactPhone;
    /**
     * 项目保护
     */
    @ApiModelProperty(value = "项目保护")
    private Integer projProtect;
    /**
     * 数量是否变化
     */
    @ApiModelProperty(value = "数量是否变化")
    private Integer isChangeNum;
    /**
     * 型号是否新增
     */
    @ApiModelProperty(value = "型号是否新增")
    private Integer isAddModel;
    /**
     * 商务部附件
     */
    @ApiModelProperty(value = "商务部附件")
    private String cdAttachment;

    /**
     * 邮件接收方
     */
    @ApiModelProperty(value = "邮件接收方")
    private String applyEmail;

    /**
     * 附件
     */
    @ApiModelProperty(value = "附件标识")
    private String attachment;

}
