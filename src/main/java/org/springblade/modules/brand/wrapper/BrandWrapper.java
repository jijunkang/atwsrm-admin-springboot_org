package org.springblade.modules.brand.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.brand.entity.BrandEntity;
import org.springblade.modules.brand.vo.BrandVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class BrandWrapper extends BaseEntityWrapper<BrandEntity, BrandVO>  {

	public static BrandWrapper build() {
		return new BrandWrapper();
 	}

	@Override
	public BrandVO entityVO(BrandEntity brand) {
		BrandVO brandVO = BeanUtil.copy(brand, BrandVO.class);

		return brandVO;
	}

}
