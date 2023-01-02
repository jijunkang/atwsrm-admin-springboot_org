package org.springblade.modules.material.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;


/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_material_price_new")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MaterialPrice对象", description = "")
public class MaterialPriceNewEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "原材料编码")
	private String material;

	@ApiModelProperty(value = "工艺")
	private String technic;

	@ApiModelProperty(value = "原材料价格")
	private String materialPrice;

	@ApiModelProperty(value = "原材料描述")
	private String materialDesc;

    @ApiModelProperty(value = "状态")
    private Integer status;

}
