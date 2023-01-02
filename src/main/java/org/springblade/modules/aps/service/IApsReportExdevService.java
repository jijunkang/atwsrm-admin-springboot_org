package org.springblade.modules.aps.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.aps.entity.ApsReportExdevEntity;
import org.springblade.modules.aps.vo.ApsReportExdevVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *  服务类
 *
 * @author Will
 */
public interface IApsReportExdevService extends BaseService<ApsReportExdevEntity> {

    QueryWrapper<ApsReportExdevEntity> getQueryWrapper(ApsReportExdevEntity apsReportExdevEntity);

    void export(ApsReportExdevEntity apsReportExdev, HttpServletResponse response);

    IPage<ApsReportExdevVO> getPage(Query query, ApsReportExdevEntity apsReportExdev);

    List<ApsReportExdevEntity> getByPoCodeAndLns(String poCode,Integer poLn);
}
