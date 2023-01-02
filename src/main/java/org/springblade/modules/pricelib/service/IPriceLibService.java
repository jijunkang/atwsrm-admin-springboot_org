package org.springblade.modules.pricelib.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.po.dto.IoWinbidReq;
import org.springblade.modules.pr.entity.U9PrEntity;
import org.springblade.modules.pricelib.entity.PriceLibEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 服务类
 * @author Will
 */
public
interface IPriceLibService extends BaseService<PriceLibEntity>{

    Integer STATUS_INIT    = 10; //待审核
    Integer STATUS_CHEKE   = 20; //一级审核通过
    Integer STATUS_CHEKE1  = 21; //一级审核已阅
    Integer STATUS_ENABLED = 30; // 审核通过，生效
    Integer STATUS_REJECT  = 40; //审核拒绝


    QueryWrapper<PriceLibEntity> getQueryWrapper(PriceLibEntity priceLib);

    boolean ioToPriceLib(IoWinbidReq req);

    boolean ioToPriceLibOfWW(IoWinbidReq req);

    boolean check(CheckDTO checkDto);

    IPage<PriceLibEntity> toCheckPage(IPage<PriceLibEntity> page, PriceLibEntity priceLib);

    int toCheckCount();

    IPage<PriceLibEntity> myPage(IPage<PriceLibEntity> page, PriceLibEntity priceLib);

    /**
     * 导入excel
     * @param file
     * @return
     */
    @Transactional
    boolean importExcel(MultipartFile file) throws Exception;


    @Transactional
    boolean importexcelfromESB(List<PriceLibEntity> file) throws Exception;

    IPage<PriceLibEntity> selectPage(IPage<PriceLibEntity> page, PriceLibEntity priceLib);

    IPage<PriceLibEntity> getPriceLib(IPage<PriceLibEntity> page, U9PrEntity u9PrEntity);

    /**
     * 导出
     * @param priceLib
     * @param query
     * @param response
     */
    void export(PriceLibEntity priceLib, Query query, HttpServletResponse response) throws Exception;

    List<PriceLibEntity> getByItemCodes(List<String> itemCodes);

    boolean update(List<String> itemCodes);
}
