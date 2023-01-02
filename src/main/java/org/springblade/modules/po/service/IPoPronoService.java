package org.springblade.modules.po.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.po.entity.PoPronoEntity;

/**
 *  服务类
 *
 * @author Will
 */
public interface IPoPronoService extends BaseService<PoPronoEntity> {
    boolean isMeetOptDate(String supCode, String itemCode, String compareDate);
}
