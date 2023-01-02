package org.springblade.modules.finance.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.finance.dto.PrepayOrderDTO;
import org.springblade.modules.finance.dto.PrepayOrderUpdateDto;
import org.springblade.modules.finance.entity.PrepayOrderEntity;
import org.springblade.modules.finance.entity.PrepayOrderItemEntity;
import org.springblade.modules.finance.vo.PrepayOrderVO;
import org.springblade.modules.po.entity.PoEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 服务类
 * @author Will
 */
public
interface IPrepayOrderService extends BaseService<PrepayOrderEntity>{
    Integer STATUS_INIT    = 10; //待提交
    Integer STATUS_SUBMIT  = 20; //已提交审核
    Integer STATUS_1PASS   = 30; //一级审核通过
    Integer STATUS_1PASS_R = 31; //一级审核已阅
    Integer STATUS_2PASS   = 40; //二级审核通过
    Integer STATUS_3PASS   = 50; //三级审核通过
    Integer STATUS_REFUSE  = 60; //审核拒绝

    /**
     * 根据PO创建首次预付款
     * @param po
     * @return
     */
    @Transactional
    PrepayOrderEntity createFirstByPo(PoEntity po);

    /**
     * 根据状态统计数量
     * @param statuss
     * @return
     */
    int countByStatus(String statuss);

    Wrapper<PrepayOrderEntity> getQueryWrapper(PrepayOrderDTO prepayOrder);

    IPage<PrepayOrderVO> getPage(PrepayOrderDTO prepayOrder, Query query);

    int getListCount();

    List<PrepayOrderEntity> getByPoCode(String poCode);

    /**
     * 批量生成预付单
     * @param prepayOrders
     * @return
     */
    @Transactional
    boolean genPrepayorder(List<PrepayOrderItemEntity>  prepayOrders);

    /**
     * 批量审核
     * @param checkDtos
     * @return
     */
    //@Transactional
    boolean checkBatch(List<CheckDTO> checkDtos);

    boolean bizDelete(List<Long> toLongList);

    /**
     * 业务更新
     * @param prepayOrder
     * @return
     */
    @Transactional
    boolean bizUpdate(PrepayOrderUpdateDto prepayOrder);

    /**
     * 打印
     *
     * @param prepayOrder PrepayOrderDTO
     * @return Map
     */
    Map<String, Object> print(PrepayOrderDTO prepayOrder);
}
