package org.springblade.modules.pr.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.pr.entity.U9PrEntity;
import org.springblade.modules.pr.vo.U9PrVO;

/**
 * 请购单 包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class U9PrWrapper extends BaseEntityWrapper<U9PrEntity, U9PrVO>  {

	public static U9PrWrapper build() {
		return new U9PrWrapper();
 	}

	@Override
	public U9PrVO entityVO(U9PrEntity u9_pr) {
		U9PrVO u9_prVO = BeanUtil.copy(u9_pr, U9PrVO.class);

		return u9_prVO;
	}

}
