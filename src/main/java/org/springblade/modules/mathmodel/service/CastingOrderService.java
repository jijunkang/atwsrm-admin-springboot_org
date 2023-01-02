package org.springblade.modules.mathmodel.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.mathmodel.entity.CastingOrderEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Author: 昕月
 * Date：2022/6/7 19:52
 * Desc:
 */
public interface CastingOrderService extends BaseService<CastingOrderEntity> {

    Wrapper<CastingOrderEntity> getQueryWrapper(CastingOrderEntity castingOrder);

    void export(CastingOrderEntity castingOrder, Query query, HttpServletResponse response);

    List<CastingOrderEntity> submitCastingReport(CastingOrderEntity castingOrder);
}
