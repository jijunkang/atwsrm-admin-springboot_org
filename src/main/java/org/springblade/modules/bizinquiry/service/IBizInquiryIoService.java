package org.springblade.modules.bizinquiry.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.bizinquiry.dto.BizInquiryReq;
import org.springblade.modules.bizinquiry.entity.BizInquiryIoEntity;
import org.springblade.modules.bizinquiry.entity.BizInquiryIoFileListEntity;

import java.util.List;

/**
 * 服务类
 *
 * @author Will
 */
public interface IBizInquiryIoService extends BaseService<BizInquiryIoEntity> {

    boolean saveBizBatch(BizInquiryReq bizInquiryReq);

    BizInquiryIoEntity getByQoId(Long qoId);

    boolean saveFile(BizInquiryReq bizInquiryReq);

    boolean saveFileList(BizInquiryReq bizInquiryReq);

    boolean removeFileList(BizInquiryReq bizInquiryReq);

    List<BizInquiryIoFileListEntity> getFileList(Long id);

    boolean sendEmailToBusiness(BizInquiryReq bizInquiryReq);

    boolean sendEmailListToBusiness(BizInquiryReq bizInquiryReq);
}
