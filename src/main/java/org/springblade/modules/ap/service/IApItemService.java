package org.springblade.modules.ap.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.ap.entity.ApEntity;
import org.springblade.modules.ap.entity.ApItemEntity;
import org.springblade.modules.ap.entity.ApRcvEntity;

import java.util.List;

/**
 * @author libin
 *
 * @date 15:14 2020/6/3
 **/
public interface IApItemService extends BaseService<ApItemEntity> {

    List<ApItemEntity> getApItemEntities(Long id, String type);

    List<ApItemEntity> getGroupBy(Long id);

    List<ApItemEntity> getByRcvId(ApRcvEntity apRcvEntity);

    String getPoCodesByApId(Long apId, String type);

    int getMinPayDateByBillId(Long id);
}
