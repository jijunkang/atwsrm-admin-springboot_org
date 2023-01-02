package org.springblade.modules.po.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.vo.IoVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class IoWrapper extends BaseEntityWrapper<IoEntity, IoVO>  {

	public static IoWrapper build() {
		return new IoWrapper();
 	}

	@Override
	public IoVO entityVO(IoEntity io) {
		IoVO ioVO = BeanUtil.copy(io, IoVO.class);

		return ioVO;
	}

}
