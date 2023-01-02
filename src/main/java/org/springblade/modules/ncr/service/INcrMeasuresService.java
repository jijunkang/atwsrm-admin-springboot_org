package org.springblade.modules.ncr.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.ncr.entity.NcrMeasuresEntity;

import java.util.List;

/**
 * @author libin
 *
 * @date 17:57 2020/8/4
 **/
public interface INcrMeasuresService extends BaseService<NcrMeasuresEntity> {


    List<NcrMeasuresEntity> getList(NcrMeasuresEntity entity);


}
