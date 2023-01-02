package org.springblade.modules.aps.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.aps.entity.ApsReportExdevEntity;
import org.springblade.modules.aps.vo.ApsReportExdevVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class ApsReportExdevWrapper extends BaseEntityWrapper<ApsReportExdevEntity, ApsReportExdevVO>  {

	public static ApsReportExdevWrapper build() {
		return new ApsReportExdevWrapper();
 	}

	@Override
	public ApsReportExdevVO entityVO(ApsReportExdevEntity apsReportExdev) {
		ApsReportExdevVO apsReportExdevVO = BeanUtil.copy(apsReportExdev, ApsReportExdevVO.class);

		return apsReportExdevVO;
	}

}
