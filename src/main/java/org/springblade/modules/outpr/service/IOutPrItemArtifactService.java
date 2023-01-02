package org.springblade.modules.outpr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.outpr.entity.OutPrItemArtifactEntity;
import org.springblade.modules.outpr.vo.OutPrItemArtifactVO;

import java.util.List;
import java.util.Map;

/**
 * 服务类
 * @author Will
 */
public
interface IOutPrItemArtifactService extends BaseService<OutPrItemArtifactEntity>{
    Integer STATUS_INIT      = 10;  // 待指定供应商;
    Integer STATUS_ASSIGNSUP = 20;  // 已指定供应商;
    Integer STATUS_CHECK1    = 30;// 一级审核通过;
    Integer STATUS_CHECK2    = 31;// 一级审核已阅;
    Integer STATUS_REJECT    = 40;// 审核拒绝;
    Integer STATUS_WAIT      = 50;// 待下单;
    Integer STATUS_ORDER     = 60;// 已下单;

    boolean assignSup(OutPrItemArtifactEntity dto);

    boolean check(CheckDTO checkDto);
    boolean check(List<CheckDTO > checkDtos);

    QueryWrapper<OutPrItemArtifactEntity> getQueryWrapper(OutPrItemArtifactEntity entity);

    List<Map<String, Object>> centerCount();

    int getCount();

    boolean isExistPrItemId(long prItemId);

    IPage<OutPrItemArtifactVO> getVoPage(OutPrItemArtifactVO vo, Query query);
}
