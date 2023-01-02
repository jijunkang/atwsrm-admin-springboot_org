package org.springblade.modules.ap.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.ap.dto.ApReq;
import org.springblade.modules.ap.dto.SubReq;
import org.springblade.modules.ap.entity.ApEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author Will
 */
public interface IApService extends BaseService<ApEntity> {

    //待审核
    Integer STATUS_WAITE  = 10;
    //已对账
    Integer STATUS_AGREE = 20;
    //已退回
    Integer STATUS_BACK = 30;

    //一级审核
    Integer STATUS_1AGREE = 40;
    //二级审核
    Integer STATUS_2AGREE = 50;
    //财务审核
    Integer STATUS_3AGREE = 60;
    //审批通过
    Integer STATUS_SUCCESS = 70;
    //审批被拒
    Integer STATUS_REJECT = 80;

    String TYPE = "按实收对账";

    QueryWrapper<ApEntity> getQueryWrapper(ApReq apReq);

    IPage<ApEntity> getPage(IPage<ApEntity> page, ApReq apReq);

    IPage<ApEntity> getVmiPage(IPage<ApEntity> page, ApReq apReq);

    int getCountByStatus(Integer status);

    Map<String, Object> dzDetail(ApEntity apEntity);

    Map<String, Object> dzDetailVmi(ApEntity apEntity);

    boolean dzSubmit(ApReq apReq);

    boolean dzSubmitVmi(ApReq apReq);

    boolean dtSubmit(ApReq apReq);

    boolean dtRemove(ApReq apReq);

    boolean subInvoice(ApReq apReq);

    boolean saveAps(ApReq apReq);

    boolean audit(ApReq apReq) throws IOException;

    boolean yfSave(ApReq apReq);

    List<Map<String, Object>> countList(ApReq apReq);

    List<Map<String, Object>> countVmiList(ApReq apReq);

    List<Map<String, Object>> yfCountList(ApReq apReq);

    boolean subOrBack(SubReq subReq);

    boolean subOrBackBatch(List<SubReq> subReqs);

    void export(ApReq apReq, HttpServletResponse response);

    Map<String, Object> print(ApReq apReq);

    Map<String, Object> yfRecord(ApReq apReq);
}
