package org.springblade.modules.bizinquiry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.modules.bizinquiry.dto.BizInquiryReq;
import org.springblade.modules.bizinquiry.entity.BizInquiryEntity;
import org.springblade.modules.bizinquiry.entity.BizInquiryIoEntity;
import org.springblade.modules.bizinquiry.entity.BizInquiryIoFileListEntity;
import org.springblade.modules.bizinquiry.vo.BizInquiryVO;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Will
 */
@Mapper
public interface BizInquiryIoFileNameMapper {

    boolean insertFile(@Param("bizInquiryIoFileListEntity") BizInquiryIoFileListEntity bizInquiryIoFileListEntity);

    Integer fileIsExisted(Long id);

    boolean removeFileList(@Param("id") Long id, @Param("fileUrl") String fileUrl);

    @Select("select count(*) from atw_biz_inquiry_io where qo_id = #{id} and is_deleted = 0")
    String isIoExisted(@Param("id") Long id);

    @Select("select * from atw_biz_inquiry where id = #{id}")
    BizInquiryEntity selectBizById(@Param("id") Long id);

    @Insert("insert into atw_biz_inquiry_io(qo_id,qo_code,attachment) value (#{qoId},#{qoCode},'1')")
    boolean insertIo(@Param("qoId")Long qoId, @Param("qoCode")String qoCode);

    boolean updateQoAttachment(Long id);

    boolean updateIoAttachmentToZero(Long id);

    List<BizInquiryIoFileListEntity> getFileList(Long id);

    List<BizInquiryIoFileListEntity> getFileListOfNotSend(Long id);

    boolean updateFileStatus(@Param("id") Long id, @Param("fileUrl") String fileUrl, @Param("sendTime") Long sendTime);
}
