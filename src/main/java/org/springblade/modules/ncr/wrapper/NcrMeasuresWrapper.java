package org.springblade.modules.ncr.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.ncr.entity.NcrMeasuresEntity;
import org.springblade.modules.ncr.vo.NcrMeasuresVO;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class NcrMeasuresWrapper extends BaseEntityWrapper<NcrMeasuresEntity, NcrMeasuresVO> {

    public static NcrMeasuresWrapper build() {
        return new NcrMeasuresWrapper();
    }

    @Override
    public NcrMeasuresVO entityVO(NcrMeasuresEntity ncr) {
        NcrMeasuresVO ncrMeasuresVO = BeanUtil.copy(ncr, NcrMeasuresVO.class);
        return ncrMeasuresVO;
    }

}
