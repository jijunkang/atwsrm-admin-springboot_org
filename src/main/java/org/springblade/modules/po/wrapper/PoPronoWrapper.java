package org.springblade.modules.po.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.po.entity.PoPronoEntity;
import org.springblade.modules.po.vo.PoPronoVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class PoPronoWrapper extends BaseEntityWrapper<PoPronoEntity, PoPronoVO>  {

	public static PoPronoWrapper build() {
		return new PoPronoWrapper();
 	}

	@Override
	public PoPronoVO entityVO(PoPronoEntity poprono) {
		PoPronoVO popronoVO = BeanUtil.copy(poprono, PoPronoVO.class);

		return popronoVO;
	}

}
