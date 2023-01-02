package org.springblade.modules.supplier.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.pr.dto.U9PrDTO;
import org.springblade.modules.supplier.entity.PaywayEntity;
import org.springblade.modules.supplier.vo.PaywayVO;

/**
 *  服务类
 *
 * @author Will
 */
public interface IPaywayService extends BaseService<PaywayEntity> {

    String DICT_BIZ_CODE = "sup_payway";

    PaywayEntity getBySupCode(String supCode);

    IPage<PaywayVO> getPayWayAndTemplate(IPage<PaywayVO> page,String supCode);

}
