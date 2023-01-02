package org.springblade.modules.bizinquiry.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.bizinquiry.entity.BizInquiryEntity;
import org.springblade.modules.bizinquiry.vo.BizInquiryVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class BizInquiryWrapper extends BaseEntityWrapper<BizInquiryEntity, BizInquiryVO>  {

	public static BizInquiryWrapper build() {
		return new BizInquiryWrapper();
 	}

	@Override
	public BizInquiryVO entityVO(BizInquiryEntity bizInquiry) {
		BizInquiryVO bizInquiryVO = BeanUtil.copy(bizInquiry, BizInquiryVO.class);

		return bizInquiryVO;
	}

}
