package org.springblade.modules.mathmodel.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.mathmodel.entity.TubeMaterialInfoEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Author: 昕月
 * Date：2022/6/2 9:31
 * Desc:
 */
public interface TubeMaterialInfoService extends BaseService<TubeMaterialInfoEntity> {

    Wrapper<TubeMaterialInfoEntity> getQueryWrapper(TubeMaterialInfoEntity tubeMaterialInfoEntity);

    void export(TubeMaterialInfoEntity materialInfoEntity, Query query, HttpServletResponse response);

    List<TubeMaterialInfoEntity> getInfoList(TubeMaterialInfoEntity infoEntity);

}
