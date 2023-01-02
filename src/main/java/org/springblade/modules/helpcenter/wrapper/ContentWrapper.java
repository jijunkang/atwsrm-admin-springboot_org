package org.springblade.modules.helpcenter.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.helpcenter.entity.ContentEntity;
import org.springblade.modules.helpcenter.vo.ContentVO;

/**
 * 帮助内容 包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class ContentWrapper extends BaseEntityWrapper<ContentEntity, ContentVO> {

	public static ContentWrapper build() {
		return new ContentWrapper();
 	}

	@Override
	public ContentVO entityVO(ContentEntity content) {
		ContentVO contentVO = BeanUtil.copy(content, ContentVO.class);

		return contentVO;
	}

}
