package org.springblade.modules.bizinquiry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.bizinquiry.dto.BizInquiryReq;
import org.springblade.modules.bizinquiry.entity.BizInquiryEntity;
import org.springblade.modules.bizinquiry.entity.BizInquiryIoEntity;
import org.springblade.modules.bizinquiry.vo.BizInquiryVO;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface BizInquiryMapper extends BaseMapper<BizInquiryEntity> {

    IPage<BizInquiryVO> list(IPage<BizInquiryEntity> page, BizInquiryReq bizInquiryReq);

    List<BizInquiryVO> getList(@Param("bizInquiryReq") BizInquiryReq bizInquiryReq);

    List<BizInquiryVO> getWaitList(@Param("bizInquiryReq") BizInquiryReq bizInquiryReq);

    BizInquiryVO getWaitById(Long id);

    Integer deleteList(@Param("bizInquiryReq") BizInquiryReq bizInquiryReq);

    Integer updateSupFeedbackById(@Param("bizInquiryIoEntity") BizInquiryIoEntity bizInquiryIoEntity);

    boolean updateStatusToEndById(Long id);
}
