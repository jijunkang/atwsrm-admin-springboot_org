package org.springblade.modules.outpr.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.modules.outpr.dto.DoDTO;
import org.springblade.modules.outpr.dto.OutPrReportFormsDTO;
import org.springblade.modules.outpr.dto.OutPrReportFormsReq;
import org.springblade.modules.outpr.entity.OutPrReportFormsEntity;
import org.springblade.modules.outpr.service.IOutPrReportFormsService;
import org.springblade.modules.po.entity.PoReceiveEntity;
import org.springblade.modules.supplier.dto.SupplierScheduleReq;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-outpr/reportForms")
@Api(value = "", tags = "")
public class OutPrReportFormsController extends BladeController {

    private IOutPrReportFormsService outPrReportFormsService;

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "outPrReportFormsDTO")
    public R<IPage<OutPrReportFormsEntity>> page(OutPrReportFormsDTO outPrReportFormsDTO, Query query) {
        IPage<OutPrReportFormsEntity> pages = outPrReportFormsService.voPage(Condition.getPage(query), outPrReportFormsDTO);
        return R.data(pages);
    }

    /**
     * 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "outPrReportFormsDTO")
    public void export(OutPrReportFormsDTO outPrReportFormsDTO, HttpServletResponse response) {
        outPrReportFormsService.export(outPrReportFormsDTO, response);
    }

    /**
     * 导出 审核通过的DO
     */
    @GetMapping("/exportDo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "outPrReportFormsDTO")
    public void exportDo(OutPrReportFormsDTO outPrReportFormsDTO, HttpServletResponse response) {
        outPrReportFormsService.exportDo(outPrReportFormsDTO, response);
    }



    /**
     * 审核通过-虚拟入库
     */
    @PostMapping("/pass")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "outPrReportFormsReq")
    public R pass(@RequestBody OutPrReportFormsReq outPrReportFormsReq) {
        return outPrReportFormsService.pass(outPrReportFormsReq);
    }


    /**
     * 虚拟入库-标签页 统计
     */
    @GetMapping("/doCount")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "outPrReportFormsDTO")
    public R<List<Map<String, Object>>> doCount(OutPrReportFormsDTO outPrReportFormsDTO) {
        return R.data(outPrReportFormsService.getDoCount(outPrReportFormsDTO));
    }

    /**
     * 虚拟入库 查询
     */
    @GetMapping("/doPage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "outPrReportFormsDTO")
    public R<IPage<DoDTO>> doPage(OutPrReportFormsDTO outPrReportFormsDTO, Query query) {
        IPage<DoDTO> pages = outPrReportFormsService.doPage(Condition.getPage(query), outPrReportFormsDTO);
        return R.data(pages);
    }
}
