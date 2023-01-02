package org.springblade.modules.po.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.secure.BladeUser;
import org.springblade.modules.po.dto.IoDTO;
import org.springblade.modules.po.dto.IoWinbidReq;
import org.springblade.modules.po.dto.OutIoDTO;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.entity.OutIoEntity;
import org.springblade.modules.po.vo.IoVO;
import org.springblade.modules.po.vo.OutIoVO;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.priceframe.dto.CenterPriceFrame;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务类
 * @author Will
 */
public
interface IIoService extends BaseService<IoEntity>{

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

    /**
     * 待审核列表
     * @return
     */
    IPage<IoDTO> selectToCheckPage(IPage<Object> page, IoEntity io , BladeUser user);

    /**
     * 有白名单 确认交期 的列表
     * @param page
     * @return
     */
    IPage<IoDTO> selectToConfirmPage(IPage<IoEntity> page,IoDTO io);
    /**
     * 一级审核
     * @param checkDto
     * @return
     */
    @Transactional
    boolean check1(CheckDTO checkDto);

    /**
     * 二级审核
     * @return
     */
    @Transactional
    boolean check2(CheckDTO checkDto);

    /**
     * 二级审核 - 阀内件
     * @return
     */
    @Transactional
    boolean check2OfOthers(CheckDTO checkDto);

    @Transactional
    boolean check2OfWW(CheckDTO checkDto);

    /**
     * 使io中标
     * @return
     */
    IoEntity letIoWinBid(IoWinbidReq ioWinbidReq);

    OutIoEntity letIoWinBidOfWW(IoWinbidReq ioWinbidReq);

    IPage<IoVO> getPage(IPage<IoEntity> page, QueryWrapper<IoEntity> queryWrapper);

    IPage<IoVO> getPageOfOthers(IPage<IoEntity> page, QueryWrapper<IoEntity> queryWrapper);

    IPage<OutIoVO> getPageOfOut(IPage<OutIoEntity> page, QueryWrapper<OutIoEntity> queryWrapper);

    boolean letIoFlow(IoDTO io);

    boolean letIoFlowOfWW(OutIoDTO io);

    /**
     * 询价结果待审核 数量
     * @return
     */
    int toCheckCount( String source);

    /**
     * 白名单交期确认
     * @return
     */
    int toConfirmCount();

    /**
     * 根据状态统计
     *
     * @param status Integer
     * @return int
     */
    int countByStatus(Integer status);

    /**
     * 根据状态查询
     *
     * @param status Integer
     * @return IPage
     */
    IPage<CenterPriceFrame> getByStatus(IPage<CenterPriceFrame> page, Integer status);

    /**
     * 查询 10 30 状态询交期、待审核
     *
     * @return
     */
    int getStatusCount();

    /**
     * getBySourceAndPrId
     *
     * @param prReq PrReq
     * @return IoEntity
     */
    IoEntity getBySourceAndPrId(PrReq prReq);

    /**
     * getByPrId
     *
     * @param prId Long
     * @return List
     */
    List<IoEntity> getByPrId(Long prId);
}
