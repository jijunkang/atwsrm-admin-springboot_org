package org.springblade.modules.outpr.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.modules.outpr.dto.DoDTO;
import org.springblade.modules.outpr.dto.OutPrReportFormsDTO;
import org.springblade.modules.outpr.dto.OutPrReportFormsReq;
import org.springblade.modules.outpr.entity.OutPrEntity;
import org.springblade.modules.outpr.entity.OutPrReportFormsEntity;
import org.springblade.modules.po.entity.PoReceiveEntity;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.supplier.dto.SupplierScheduleReq;
import org.springblade.modules.supplier.entity.SupplierSchedule;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author Will
 */
public interface IOutPrReportFormsService extends BaseService<OutPrReportFormsEntity> {
    String TO_CHECK         = "10";          //  待审核
    String HAVE_CHECK       = "20";       //  已通过

    String MES_FAIL = "0";
    String MES_SUCC = "1";


    IPage<OutPrReportFormsEntity> voPage(IPage<OutPrReportFormsEntity> page , OutPrReportFormsDTO outPrReportFormsDTO);

    void export(OutPrReportFormsDTO outPrReportFormsDTO, HttpServletResponse response);

    void exportDo(OutPrReportFormsDTO outPrReportFormsDTO, HttpServletResponse response);

    R pass(OutPrReportFormsReq outPrReportFormsReq);

    int getDoTabCount();

    List<Map<String, Object>> getDoCount(OutPrReportFormsDTO outPrReportFormsDTO);

    IPage<DoDTO> doPage(IPage<DoDTO> page , OutPrReportFormsDTO outPrReportFormsDTO);
}


