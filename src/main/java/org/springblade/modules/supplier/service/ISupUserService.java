package org.springblade.modules.supplier.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.supplier.entity.SupUser;
import org.springblade.modules.supplier.entity.Supplier;
import org.springframework.transaction.annotation.Transactional;

/**
 * 供应商用户 服务类
 * @author xianboss
 */
public
interface ISupUserService extends BaseService<SupUser>{

    @Transactional
    SupUser create(Supplier entity);

    /**
     * 根据供应商编号查找
     * @param code
     * @return
     */
    SupUser getBySupCode(String code);
}
