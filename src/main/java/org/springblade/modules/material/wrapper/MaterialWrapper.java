package org.springblade.modules.material.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.material.entity.MaterialEntity;
import org.springblade.modules.material.vo.MaterialVO;

/**
 * 原材料尺寸表 包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class MaterialWrapper extends BaseEntityWrapper<MaterialEntity, MaterialVO>  {

	public static MaterialWrapper build() {
		return new MaterialWrapper();
 	}

	@Override
	public MaterialVO entityVO(MaterialEntity material) {
		MaterialVO materialVO = BeanUtil.copy(material, MaterialVO.class);

		return materialVO;
	}

}
