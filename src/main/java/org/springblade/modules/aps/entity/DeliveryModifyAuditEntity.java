package org.springblade.modules.aps.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.time.LocalDateTime;


/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_delivery_modify_audit")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeliveryModifyAudit对象", description = "")
public class DeliveryModifyAuditEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 子项目号
	 */
	@ApiModelProperty(value = "子项目号")
	private String proNoSub;
	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String poCode;
	/**
	 * 订单行号
	 */
	@ApiModelProperty(value = "订单行号")
	private Integer poLn;
	/**
	 * 物料编码
	 */
	@ApiModelProperty(value = "物料编码")
	private String itemCode;
	/**
	 * 物料名称
	 */
	@ApiModelProperty(value = "物料名称")
	private String itemName;
	/**
	 * 订单数量
	 */
	@ApiModelProperty(value = "订单数量")
	private BigDecimal tcNum;
	/**
	 * 项目需求数量
	 */
	@ApiModelProperty(value = "项目需求数量")
	private BigDecimal proReqNum;
	/**
	 * 送货日期
	 */
	@ApiModelProperty(value = "送货日期")
	private Long deliveryDate;
	/**
	 * 审核修改日期
	 */
	@ApiModelProperty(value = "审核修改日期")
	private Long reviewModifyDeliDate;
	/**
	 * 申请修改交期
	 */
	@ApiModelProperty(value = "申请修改交期")
	private Long applyModifyDeliDate;
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;
	/**
	 * 系统日志
	 */
	@ApiModelProperty(value = "系统日志")
	private String sysLog;

}
