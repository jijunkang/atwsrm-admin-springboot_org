package org.springblade.modules.outpr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.outpr.entity.OutSupItemPriceEntity;
import org.springblade.modules.outpr.vo.OutSupItemPriceVO;

import java.util.List;

/**
 *  服务类
 *
 * @author Will
 */
public interface IOutSupItemPriceService extends BaseService<OutSupItemPriceEntity> {

    OutSupItemPriceEntity findNextSup(Long itemPriceId);

    List<OutSupItemPriceVO> getProcessPriceList(QueryWrapper<OutSupItemPriceEntity> queryWrapper);
}
