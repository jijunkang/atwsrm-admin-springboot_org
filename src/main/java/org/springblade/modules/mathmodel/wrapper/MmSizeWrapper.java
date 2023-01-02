package org.springblade.modules.mathmodel.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.mathmodel.entity.MmSizeEntity;
import org.springblade.modules.mathmodel.vo.MmSizeVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class MmSizeWrapper extends BaseEntityWrapper<MmSizeEntity, MmSizeVO>  {

	public static MmSizeWrapper build() {
		return new MmSizeWrapper();
 	}

	@Override
	public MmSizeVO entityVO(MmSizeEntity mmSize) {
		MmSizeVO mmSizeVO = BeanUtil.copy(mmSize, MmSizeVO.class);

		return mmSizeVO;
	}

}
