package org.springblade.modules.po.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.po.dto.PoRemindApplyDTO;
import org.springblade.modules.po.dto.PoRemindDTO;
import org.springblade.modules.po.entity.PoRemindEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 服务类
 * @author Will
 */
public
interface IPoRemindService extends BaseService<PoRemindEntity>{

    Integer STATUS_INIT   = 10; //待审核
    Integer STATUS_PASS   = 20; //审核通过
    Integer STATUS_FINISH = 30; //已完成
    Integer STATUS_REJECT = 40; //审核拒绝

    @Transactional
    boolean apply(PoRemindApplyDTO applyDTO, Long userId);

    boolean check(CheckDTO checkDto);

    boolean check(List<CheckDTO> checkDtos);

    boolean complete(CheckDTO checkDto);

    IPage<PoRemindEntity> myRemind(IPage<PoRemindEntity> page, PoRemindEntity poremind);

    /**
     * 导出
     */
    void export(PoRemindDTO poremind, Query query, HttpServletResponse response) throws Exception;

    QueryWrapper<PoRemindEntity> getQueryMapper(PoRemindDTO remindDTO);

}
