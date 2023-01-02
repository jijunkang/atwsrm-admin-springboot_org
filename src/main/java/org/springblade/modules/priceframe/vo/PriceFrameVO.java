package org.springblade.modules.priceframe.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.priceframe.entity.PriceFrameEntity;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PriceLibVO对象", description = "价格库")
public class PriceFrameVO extends PriceFrameEntity {

	private static final long serialVersionUID = 1L;

    /**
     * 分类统计数量
     */
	private Integer count;
}
