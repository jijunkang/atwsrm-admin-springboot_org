package org.springblade.modules.supplier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.pr.dto.U9PrDTO;
import org.springblade.modules.supplier.entity.PaywayEntity;
import org.springblade.modules.supplier.vo.PaywayVO;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface PaywayMapper extends BaseMapper<PaywayEntity> {

    @Select("Select p.*,s.template_type from atw_payway p left join atw_supplier s on (p.sup_code = s.code and s.primary_contact='1') WHERE p.sup_code = #{supCode} AND p.is_deleted = 0 and s.is_deleted = 0")
    IPage<PaywayVO> getPayWayAndTemplate(IPage<PaywayVO> page, @Param("supCode") String supCode);

}
