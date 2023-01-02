package org.springblade.modules.bizinquiry.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.bizinquiry.entity.BizInquiryIoEntity;
import org.springblade.modules.bizinquiry.vo.BizInquiryIoVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class BizInquiryIoWrapper extends BaseEntityWrapper<BizInquiryIoEntity, BizInquiryIoVO>  {

	public static BizInquiryIoWrapper build() {
		return new BizInquiryIoWrapper();
 	}

	@Override
	public BizInquiryIoVO entityVO(BizInquiryIoEntity bizInquiryIo) {
		BizInquiryIoVO bizInquiryIoVO = BeanUtil.copy(bizInquiryIo, BizInquiryIoVO.class);

		return bizInquiryIoVO;
	}

}
