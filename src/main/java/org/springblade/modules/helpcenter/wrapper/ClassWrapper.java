package org.springblade.modules.helpcenter.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.helpcenter.entity.ClassEntity;
import org.springblade.modules.helpcenter.vo.ClassVO;

/**
 * 帮助分类 包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class ClassWrapper extends BaseEntityWrapper<ClassEntity, ClassVO> {

	public static ClassWrapper build() {
		return new ClassWrapper();
	}

	@Override
	public ClassVO entityVO(ClassEntity clazz) {
		ClassVO classVO = BeanUtil.copy(clazz, ClassVO.class);

		return classVO;
	}

}
