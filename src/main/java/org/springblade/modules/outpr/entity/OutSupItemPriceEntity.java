package org.springblade.modules.outpr.entity;

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
@TableName("atw_out_sup_item_price")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OutSupItemPrice对象", description = "")
public class OutSupItemPriceEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 委外物料id
	 */
	@ApiModelProperty(value = "委外物料id")
	private Long prItemId;
	/**
	 * 供应商编码
	 */
	@ApiModelProperty(value = "供应商编码")
	private String supCode;
	/**
	 * 供应商名称
	 */
	@ApiModelProperty(value = "供应商名称")
	private String supName;
	/**
	 * 首件准备工时费用
	 */
	@ApiModelProperty(value = "首件准备工时费用")
	private BigDecimal firstSetupCost;
	/**
	 * 物料单价
	 */
	@ApiModelProperty(value = "物料单价")
	private BigDecimal price;

	@ApiModelProperty(value = "含税单价")
	private BigDecimal taxPrice;

	@ApiModelProperty(value = "物料总价")
	private BigDecimal subtotal;

	@ApiModelProperty(value = "评标分")
	private BigDecimal bidScore;
	/**
	 * 评标排名次序
	 */
	@ApiModelProperty(value = "评标排名次序")
	private Integer bidSeq;

}
