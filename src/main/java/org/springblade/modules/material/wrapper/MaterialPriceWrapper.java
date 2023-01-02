package org.springblade.modules.material.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.material.entity.MaterialPriceEntity;
import org.springblade.modules.material.vo.MaterialPriceVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class MaterialPriceWrapper extends BaseEntityWrapper<MaterialPriceEntity, MaterialPriceVO>  {

	public static MaterialPriceWrapper build() {
		return new MaterialPriceWrapper();
 	}

	@Override
	public MaterialPriceVO entityVO(MaterialPriceEntity materialPrice) {
		MaterialPriceVO materialPriceVO = BeanUtil.copy(materialPrice, MaterialPriceVO.class);

		return materialPriceVO;
	}

}
