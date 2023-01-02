package org.springblade.modules.ncr.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.ncr.entity.NcrEntity;
import org.springblade.modules.ncr.vo.NcrVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class NcrWrapper extends BaseEntityWrapper<NcrEntity, NcrVO>  {

	public static NcrWrapper build() {
		return new NcrWrapper();
 	}

	@Override
	public NcrVO entityVO(NcrEntity ncr) {
		NcrVO ncrVO = BeanUtil.copy(ncr, NcrVO.class);

		return ncrVO;
	}

}
