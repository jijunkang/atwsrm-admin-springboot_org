package org.springblade.modules.bizinquiry.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.codehaus.jettison.json.JSONException;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.bizinquiry.dto.BizInquiryReq;
import org.springblade.modules.bizinquiry.entity.BizInquiryEntity;
import org.springblade.modules.bizinquiry.vo.BizInquiryVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author Will
 */
public interface IBizInquiryService extends BaseService<BizInquiryEntity> {

    //待报价
    Integer STATUS_WAIT = 10;
    //待提交
    Integer STATUS_SUB = 20;
    //待审核
    Integer STATUS_AUDIT = 30;
    //审核拒绝
    Integer STATUS_BACK = 40;
    //已完成
    Integer STATUS_SUCCESS = 50;

    String DIC_VAL = "price_ownership";

    IPage<BizInquiryVO> list(IPage<BizInquiryEntity> page, BizInquiryReq bizInquiryReq);

    boolean push(BizInquiryReq bizInquiryReq);

    List<Map<String, Object>> countList();

    boolean audits(BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException;

    // 批量删除询价单 zlw add 20210423 NO.1
    boolean deleteList(BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException;

    // 批量保存备注 zlw add 20210426 NO.10
    boolean saveRemarks(BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException;

    // 批量发送邮件通知 zlw add 20210426 NO.5
    boolean sendEmail(BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException;

    void export(HttpServletResponse response, BizInquiryReq bizInquiryReq);

    void exportWait(HttpServletResponse response, BizInquiryReq bizInquiryReq);

    boolean importExcel(MultipartFile file) throws ParseException;

    int getCount();

    boolean listToEndButNotSend(BizInquiryReq bizInquiryReq);
}
