package org.springblade.modules.priceframe.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.po.dto.IoWinbidReq;
import org.springblade.modules.pr.dto.U9PrDTO;
import org.springblade.modules.pr.entity.U9PrEntity;
import org.springblade.modules.priceframe.dto.CenterPriceFrame;
import org.springblade.modules.priceframe.entity.PriceFrameEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 服务类
 * @author Will
 */
public
interface IPriceFrameService extends BaseService<PriceFrameEntity>{

    Integer STATUS_AUDIT   = 10; //待审核
    Integer STATUS_SUCCESS = 20; // 审核通过，生效
    Integer STATUS_REJECT  = 30; //审核拒绝


    QueryWrapper<PriceFrameEntity> getQueryWrapper(PriceFrameEntity priceFrameEntity);

    boolean ioToPriceFrame(IoWinbidReq req);

    boolean check(CheckDTO checkDto);

    IPage<PriceFrameEntity> toCheckPage(IPage<PriceFrameEntity> page, PriceFrameEntity priceFrameEntity);

    int toCheckCount();

    IPage<PriceFrameEntity> myPage(IPage<PriceFrameEntity> page, PriceFrameEntity priceFrameEntity);

    /**
     * 导入excel
     * @param file
     * @return
     */
    @Transactional
    Map<String, Object> importExcel(MultipartFile file) throws Exception;

    IPage<PriceFrameEntity> selectPage(IPage<PriceFrameEntity> page, PriceFrameEntity priceFrameEntity);

    IPage<PriceFrameEntity> getPriceFrame(IPage<PriceFrameEntity> page, U9PrEntity u9PrEntity);

    /**
     * 导出
     * @param priceFrameEntity
     * @param query
     * @param response
     */
    void export(PriceFrameEntity priceFrameEntity, Query query, HttpServletResponse response) throws Exception;


    IPage<CenterPriceFrame> center(IPage<CenterPriceFrame> page, PriceFrameEntity priceFrameEntity);

    List<Map<String, Object>> countList();

    boolean submitDates(List<CenterPriceFrame> centerPriceFrames);

    boolean submitBatch(List<CenterPriceFrame> centerPriceFrames);

    String checkOnly(List<CenterPriceFrame> centerPriceFrames);

    boolean invalids(List<CenterPriceFrame> centerPriceFrames);

    PriceFrameEntity saveCheck(PriceFrameEntity priceFrameEntity);

    BigDecimal getLimitMin(PriceFrameEntity priceFrameEntity);

    int auditCount();

    BigDecimal getPrice(String itemCode, String supCode, BigDecimal priceNum);

    /**
     * 获取有效期间内指定数量的所有区间集合
     *
     * @param itemCode String
     * @param priceNum BigDecimal
     * @return List
     */
    List<PriceFrameEntity> getNumberInterval(String itemCode, BigDecimal priceNum);
}
