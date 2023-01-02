package org.springblade.modules.ap.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.ap.entity.ApEntity;
import org.springblade.modules.ap.vo.ApVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class ApWrapper extends BaseEntityWrapper<ApEntity, ApVO>  {

	public static ApWrapper build() {
		return new ApWrapper();
 	}

	@Override
	public ApVO entityVO(ApEntity ap) {
		ApVO apVO = BeanUtil.copy(ap, ApVO.class);

		return apVO;
	}

}
