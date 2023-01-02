package org.springblade.modules.ap.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.ap.dto.ApRcvDTO;
import org.springblade.modules.ap.dto.ApReq;
import org.springblade.modules.ap.entity.ApRcvEntity;
import org.springblade.modules.ap.entity.ApRcvReqEntity;
import org.springblade.modules.ap.vo.ApRcvVO;
import org.springblade.modules.ncr.entity.NcrEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author Will
 */
public interface IApRcvService extends BaseService<ApRcvEntity> {

    //开立
    Integer STATUS_INIT  = 11;
    //待审核
    Integer STATUS_WAITE = 12;
    //待扣款
    Integer STATUS_BUCKLE = 13;
    //已对账
    Integer STATUS_AGREE = 14;
    //已扣款
    Integer STATUS_PASS = 15;
    //已拒绝
    Integer STATUS_BACK = 16;
    //全部
    Integer STATUS_ALL = 99;


    QueryWrapper<ApRcvEntity> getQueryWrapper(ApReq apReq);

    IPage<ApRcvEntity> getPage(IPage<ApRcvEntity> page, ApReq apReq);

    IPage<ApRcvReqEntity> getVmiPage(IPage<ApRcvEntity> page, ApReq apReq);

    int getListCount();

    IPage<ApRcvEntity> kkList(IPage<ApRcvEntity> page, ApReq apReq);

    int getKKListCount();

    List<ApRcvEntity> getList(ApReq apReq);

    List<ApRcvReqEntity> getVmiList(ApReq apReq);

    String getProNoByApIdOrBillId(Long id, String type);

    boolean audit(ApReq apReq);

    boolean auditBatch(List<ApReq> apReqs);

    boolean updates(ApReq apReq);

    List<Map<String, Object>> countList(ApReq apReq);

    void vmiExport(HttpServletResponse response,String selectionIds,ApReq apReq);

    /**
     * 生成扣款单号
     * @return
     */
    String genCode(String type);

    List<NcrEntity> ncrDetail(ApRcvEntity aprcv);

    void exportNcr(HttpServletResponse response, ApReq apReq);

    boolean remove(String ids);

    boolean reviewContract(ApRcvDTO apRcvDTO);

    void export(HttpServletResponse response, String selectionIds, ApReq apReq);

    List<ApRcvVO> getDetailOfVmi(String rcvIds);
}
