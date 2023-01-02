package org.springblade.modules.ap.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.ap.entity.ApRcvEntity;
import org.springblade.modules.ap.vo.ApRcvVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class ApRcvWrapper extends BaseEntityWrapper<ApRcvEntity, ApRcvVO>  {

	public static ApRcvWrapper build() {
		return new ApRcvWrapper();
 	}

	@Override
	public ApRcvVO entityVO(ApRcvEntity aprcv) {
		ApRcvVO aprcvVO = BeanUtil.copy(aprcv, ApRcvVO.class);

		return aprcvVO;
	}

}
