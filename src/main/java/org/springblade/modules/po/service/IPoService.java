package org.springblade.modules.po.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.common.dto.AssignDTO;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.po.dto.PoDTO;
import org.springblade.modules.po.dto.PoItemDTO;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.entity.OutIoEntity;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.vo.PoItemVO;
import org.springblade.modules.supplier.dto.SupplierScheduleReq;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

/**
 * 采购订单表头 服务类
 * @author Will
 */
public
interface IPoService extends BaseService<PoEntity>{

    Integer STATUS_INIT            = 10;   //下单成功 待供应商确认
    Integer STATUS_CANCEL          = 20;   // 供应商取消
    Integer STATUS_SUPSURE         = 30;   //供应商确认 待上传合同
    Integer STATUS_UPLOAD_CONTRACT = 40;   //供应商已上传合同 合同待审核
    Integer STATUS_EXECUTING       = 50;   //合同审核通过 执行中
    Integer STATUS_PRICE_WAIT      = 51;   //订单金额待审核
    Integer STATUS_PRICE_PASS      = 52;   //订单金额审核通过
    Integer STATUS_PRICE_REJECT    = 53;   //订单金额审核拒绝
    Integer STATUS_CONTRACT_REFUSE = 60;   //合同拒绝
    Integer STATUS_CLOSE           = 70;   //关闭 正常关闭 取消关闭

    String CANCEL_ASSIGN_MANAGER = "manager";   // 供应商取消订单 指派给副经理。
    String CANCEL_ASSIGN_PURCH   = "purch";   // 供应商取消订单 指派给采购员。


    //合同状态
    Integer CO_STATUS_INIT = 10;                 //待上传合同
    Integer CO_STATUS_AUDIT = 20;                //待审核
    Integer CO_STATUS_SUCCESS = 30;              //审核通过
    Integer CO_STATUS_FAIL = 40;                 //审核拒绝


    PoItemEntity placeOrderByIo(IoEntity ioEntity, String source, Long sourceId);

    PoItemEntity placeOrderByIo(IoEntity ioEntity, String source, Long sourceId, String branch);


    void placeOrderByIoOfWW(OutIoEntity ioEntity, String source, Long sourceId);

    boolean contractCheck(CheckDTO checkDto);

    boolean cancelPoAssignPurch(AssignDTO assign);

    /**
     * 上传合同
     * @param po
     * @return
     */
    boolean uploadContract(PoEntity po);

    /**
     * 待处理订单
     * @param page
     * @param po
     * @return
     */
    IPage<PoEntity> getTodoPage(IPage<PoEntity> page, PoEntity po);

    QueryWrapper<PoEntity> getTodoQueryWrapper(PoEntity po);

    /**
     * 订单付款明细列表
     * @param poDTO
     * @return
     */
    IPage<PoEntity> getPrePoPage(PoDTO poDTO,IPage<PoEntity> page) throws ParseException;
    /**
     * 订单付款明细数量统计
     * @param poDTO
     * @return
     */
    int getPrePoCount(PoDTO poDTO) throws ParseException;

    int getRemindCount() throws ParseException;

    PoEntity getByOrderCode(String poCode);

    void export(PoEntity po, HttpServletResponse response);

    /**
     * 根据poCode获取自然关闭的所有金额
     *
     * @param poCode String
     * @return BigDecimal
     */
    BigDecimal getSumCloseAmount(String poCode);

    boolean auditPrice(PoEntity poEntity) throws IOException;

    int getPriceAuditCount();

    IPage<PoEntity> list(Query query, PoDTO PoDto);

    IPage<PoEntity> vmiList(Query query, PoDTO PoDto);

    void poExport(PoDTO po, HttpServletResponse response);
}
