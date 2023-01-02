package org.springblade.modules.ncr.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.ap.entity.ApRcvEntity;
import org.springblade.modules.ncr.dto.NcrDTO;
import org.springblade.modules.ncr.entity.NcrEntity;
import org.springblade.modules.ncr.vo.NcrVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 服务类
 * @author Will
 */
public
interface INcrService extends BaseService<NcrEntity>{

    Integer STATUS_INIT      = 10; //待处理
    Integer STATUS_TOSUP     = 20; //发给供应商整改
    Integer STATUS_SUPSUBMIT = 21; //供应商已整改
    Integer STATUS_TOMES     = 30; //退回质量部

    /**
     * 已结案
     */
    Integer STATUS_HAS = 30;
    /**
     * 未结案
     */
    Integer STATUS_NOT = 10;
    /**
     * 纠正措施类型
     */
    String TYPE_RECTIFY = "rectify";
    /**
     * 预防措施类型
     */
    String TYPE_PREVENT = "prevent";

    Wrapper<NcrEntity> getQueryWrapper(NcrDTO ncr);

    Wrapper<NcrEntity> getCenter(NcrDTO ncr);

    NcrVO detail(NcrEntity ncrEntity);

    void export(HttpServletResponse response, NcrDTO ncr);

    boolean createRcv(List<NcrEntity> ncrEntityList);

    List<NcrEntity> queryByRcvCode(String rcvCode);

    void createRcvBatch();

    Integer getCount();

    boolean updateByRcvCode(String rcvCode);

    Map<String, Object> getNotCount();
}
