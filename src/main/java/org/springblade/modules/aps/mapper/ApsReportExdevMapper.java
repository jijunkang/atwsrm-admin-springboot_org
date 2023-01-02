package org.springblade.modules.aps.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.modules.aps.entity.ApsReportExdevEntity;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface ApsReportExdevMapper extends BaseMapper<ApsReportExdevEntity> {

    IPage<ApsReportExdevEntity> getPage(IPage<Object> page, @Param("aps") ApsReportExdevEntity apsReportExdevEntity);

    List<ApsReportExdevEntity> getPage(@Param("aps") ApsReportExdevEntity apsReportExdevEntity);

    @Select({
        "select * from atw_aps_report_exdev where po_code = #{poCode} and po_ln = #{poLn} order by delivery_date limit 1"
    })
    List<ApsReportExdevEntity> getByPoCodeAndLns(@Param("poCode") String poCode, @Param("poLn") Integer poLn);
}
