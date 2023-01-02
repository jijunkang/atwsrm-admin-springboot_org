package org.springblade.modules.material.entity;

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
 * 原材料尺寸表 实体类
 *
 * @author Will
 */
@Data
@TableName("atw_material")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Material对象", description = "原材料尺寸表")
public class MaterialEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 类型
	 */
	@ApiModelProperty(value = "类型")
	private String type;
	/**
	 * 规格
	 */
	@ApiModelProperty(value = "规格")
	private String spec;
	/**
	 * 材质
	 */
	@ApiModelProperty(value = "材质")
	private String metal;
	/**
	 * 外径1
	 */
	@ApiModelProperty(value = "外径1")
	private BigDecimal outD1;
	/**
	 * 外径2
	 */
	@ApiModelProperty(value = "外径2")
	private BigDecimal outD2;
	/**
	 * 高度1
	 */
	@ApiModelProperty(value = "高度1")
	private BigDecimal h1;
	/**
	 * 高度2
	 */
	@ApiModelProperty(value = "高度2")
	private BigDecimal h2;
	/**
	 * 内孔
	 */
	@ApiModelProperty(value = "内孔")
	private BigDecimal hole;
	/**
	 * 必要加工费
	 */
	@ApiModelProperty(value = "必要加工费")
	private BigDecimal needProcFees;
	/**
	 * 非必要加工费
	 */
	@ApiModelProperty(value = "非必要加工费")
	private BigDecimal unneedProcFees;
	/**
	 * 锻打费用
	 */
	@ApiModelProperty(value = "锻打费用")
	private BigDecimal forgeFees;
	/**
	 * 粗加工费用
	 */
	@ApiModelProperty(value = "粗加工费用")
	private BigDecimal roughProcFees;


}
