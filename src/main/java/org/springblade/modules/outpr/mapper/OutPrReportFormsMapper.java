package org.springblade.modules.outpr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.modules.outpr.dto.DoDTO;
import org.springblade.modules.outpr.dto.OutPrReportFormsDTO;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.entity.OutPrReportFormsEntity;
import org.springblade.modules.po.entity.PoReceiveEntity;
import org.springblade.modules.supplier.entity.SupplierSchedule;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface OutPrReportFormsMapper extends BaseMapper<OutPrReportFormsEntity> {

    IPage<OutPrReportFormsEntity> getPage(IPage page, @Param("outPrReportFormsEntity") OutPrReportFormsEntity outPrReportFormsEntity);

    List<OutPrReportFormsEntity> getList(@Param("outPrReportFormsEntity") OutPrReportFormsEntity outPrReportFormsEntity);

    List<OutPrReportFormsEntity> getDOListOfWW(@Param("outPrReportFormsEntity") OutPrReportFormsDTO outPrReportFormsDTO);

    IPage<OutPrReportFormsEntity> getDOPageOfWW(IPage page,@Param("outPrReportFormsEntity") OutPrReportFormsDTO outPrReportFormsDTO);

    List<DoDTO> getParams(@Param("doCode") String doCode);

    IPage<DoDTO> getDoPage(IPage page, @Param("outPrReportFormsDto") OutPrReportFormsDTO outPrReportFormsDto);

    List<DoDTO> getDoList(@Param("outPrReportFormsDto") OutPrReportFormsDTO outPrReportFormsDto);

    boolean virtualWareById(@Param("rcvCode")String rcvCode);

    int getToCheckCount();

    int getHaveCheckCount();
}
