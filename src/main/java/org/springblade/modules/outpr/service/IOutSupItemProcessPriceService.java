package org.springblade.modules.outpr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.outpr.entity.OutSupItemProcessPriceEntity;
import org.springblade.modules.outpr.vo.OutSupItemProcessPriceVO;

import java.util.List;

/**
 *  服务类
 *
 * @author Will
 */
public interface IOutSupItemProcessPriceService extends BaseService<OutSupItemProcessPriceEntity> {

    List<OutSupItemProcessPriceVO> listVo(QueryWrapper<OutSupItemProcessPriceEntity> queryWrapper);
}
