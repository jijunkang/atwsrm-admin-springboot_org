package org.springblade.modules.po.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.secure.BladeUser;
import org.springblade.modules.po.dto.IoDTO;
import org.springblade.modules.po.dto.IoWinbidReq;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.entity.OutIoEntity;
import org.springblade.modules.po.vo.IoVO;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.priceframe.dto.CenterPriceFrame;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务类
 * @author Will
 */
public
interface IIoOutService extends BaseService<OutIoEntity>{

    Integer STATUS_INIT           = 10; // 未报价
    Integer STATUS_UN_QUOTE       = 20; // 未投标
    Integer STATUS_QUOTED         = 30; // 已报价
    Integer STATUS_LOSEBID        = 40; // 未中标
    Integer STATUS_WINBID_UNCHECK = 50; // 中标待副经理审核
    Integer STATUS_WINBID_CHECK1  = 51; // 中标一级审核通过
    Integer STATUS_WINBID_CHECK2  = 52; // 中标一级审核已阅
    Integer STATUS_WINBID_WARNING = 60; // 中标预警
    Integer STATUS_WINBID_REJECT  = 70; // 审核拒绝
    Integer STATUS_WAIT           = 80; //待下单
    Integer STATUS_CLOSE          = 90; //关闭

    String SOURCE_MODEL       = "model";           //  数学模型计算
    String SOURCE_PROTOCOL    = "protocol";        //  有框架协议
    String SOURCE_QUOTE       = "quote";           //  供应商报价
    String SOURCE_QUOTEDATE   = "quotedate";       //  有价格询交期
    String SOURCE_PURCHSUBMIT = "purch_submit";    //  物供供应商报价，采购员提交

    //业务分支
    String BIZ_PRICE_FRAME = "price_frame";

    // 是否需要审核
    String NEED_CHECK = "0";
    String NOT_NEED_CHECK = "1";


    IPage<IoVO> getPage(IPage<OutIoEntity> page, QueryWrapper<OutIoEntity> queryWrapper);

    /**
     * 根据状态统计
     *
     * @param status Integer
     * @return int
     */
    int countByStatus(Integer status);

    /**
     * getByPrId
     *
     * @param prId Long
     * @return List
     */
    List<OutIoEntity> getByPrId(Long prId);

    /**
     * getBySourceAndPrId
     *
     * @param prReq PrReq
     * @return IoEntity
     */
    OutIoEntity getBySourceAndPrId(PrReq prReq);


    /**
     * 二级审核 - 委外
     * @return
     */
    @Transactional
    boolean check2OfWW(CheckDTO checkDto);
}
