package org.springblade.modules.ap.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.ap.dto.ApReq;
import org.springblade.modules.ap.entity.ApBillEntity;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author Will
 */
public interface IApBillService extends BaseService<ApBillEntity> {

    //待提交
    Integer STATUS_INIT = 10;
    //一级审核
    Integer STATUS_1AGREE = 20;
    //二级审核
    Integer STATUS_2AGREE = 30;
    //财务审核
    Integer STATUS_3AGREE = 40;
    //审批通过
    Integer STATUS_SUCCESS = 50;
    //审批被拒
    Integer STATUS_REJECT = 60;


    QueryWrapper<ApBillEntity> getQueryWrapper(ApReq apReq);

    IPage<ApBillEntity> getPage(IPage<ApBillEntity> page, ApReq apReq);

    int getListCount();

    Map<String, Object> billDetail(ApBillEntity apEntity);

    //@Transactional
    boolean audit(ApReq apReq) throws IOException;

    boolean auditBatch(List<ApReq> apReqs) throws IOException;

    boolean yfSave(ApReq apReq);

    List<Map<String, Object>> countList(ApReq apReq);

    Map<String, Object> print(ApReq apReq);

    /**
     * 生成对付编号
     *
     * @return String
     */
    String genCode();

    void backToRec(List<String> apCodes);

}
