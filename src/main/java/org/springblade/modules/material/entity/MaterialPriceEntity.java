package org.springblade.modules.material.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;


/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_material_price")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MaterialPrice对象", description = "")
public class MaterialPriceEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "类型")
	private String type;

	@ApiModelProperty(value = "标准")
	private String std;

	@ApiModelProperty(value = "规格")
	private String spec;

	@ApiModelProperty(value = "材质")
	private String material;

	@ApiModelProperty(value = "供应商")
	private String supCode;

	@ApiModelProperty(value = "供应商名称")
	private String supName;

	@ApiModelProperty(value = "公斤单价")
	private BigDecimal priceKg;

	@ApiModelProperty(value = "线切割加工费")
	private BigDecimal priceCutting;

	@ApiModelProperty(value = "组间转换率")
	private BigDecimal converRate;

	@ApiModelProperty(value = "毫米单价")
	private BigDecimal priceMm;

	@ApiModelProperty(value = "冗余")
	private Double redun;

	@ApiModelProperty(value = "上偏差")
	private Integer upperDeviation;

	@ApiModelProperty(value = "下偏差")
	private Integer lowerDeviation;

	@ApiModelProperty(value = "是否有效")
	private Integer isEnable;

	@ApiModelProperty(value = "铸件加工费用")
	private BigDecimal castProcFees;

}
