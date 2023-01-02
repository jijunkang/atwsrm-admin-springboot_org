package org.springblade.modules.mathmodel.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.mathmodel.entity.MailyMaterialTotalEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Author: 昕月
 * Date：2022/5/24 13:35
 * Desc:
 */

public interface TubeMaterialService  extends BaseService<MailyMaterialTotalEntity> {
    List<MailyMaterialTotalEntity> selectLists(MailyMaterialTotalEntity maliy, MailyMaterialTotalEntity totalEntity);


    void export(MailyMaterialTotalEntity materialMaliyVO, Query query, HttpServletResponse response);

    Wrapper<MailyMaterialTotalEntity> getQueryWrapper(MailyMaterialTotalEntity mailyMaterialTotalEntity);

}

