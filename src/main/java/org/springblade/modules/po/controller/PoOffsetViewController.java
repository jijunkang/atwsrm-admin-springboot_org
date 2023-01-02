package org.springblade.modules.po.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.common.dto.StatisticDTO;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.po.dto.PoOffsetViewInfo;
import org.springblade.modules.po.dto.PoOffsetViewReq;
import org.springblade.modules.po.dto.PoTracelogDTO;
import org.springblade.modules.po.entity.PoOffsetViewEntity;
import org.springblade.modules.po.service.IPoOffsetViewService;
import org.springblade.modules.po.vo.PoOffsetViewVO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;


/**
 * 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-po/pooffsetview")
@Api(value = "", tags = "")
public class PoOffsetViewController extends BladeController {

    private IPoOffsetViewService pooffsetviewService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入pooffsetview")
    public R<PoOffsetViewEntity> detail(PoOffsetViewEntity pooffsetview) {
        PoOffsetViewEntity detail = pooffsetviewService.getOne(Condition.getQueryWrapper(pooffsetview));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入pooffsetview")
    public R<IPage<PoOffsetViewVO>> list(PoOffsetViewVO poOffsetViewVO, Query query) {
        poOffsetViewVO.setTraceCode(getUser().getAccount());
        IPage<PoOffsetViewVO> pages = pooffsetviewService.selectTodoPage(query, poOffsetViewVO);
        return R.data(pages);
    }

    /**
     * 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入price_lib")
    public void export(PoOffsetViewEntity poOffsetViewEntity, Query query, HttpServletResponse response) throws Exception {
        pooffsetviewService.export(poOffsetViewEntity, query, response);
    }

    /**
     * 自定义导出
     */
    @GetMapping("/customexport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入price_lib")
    public void customExport(PoOffsetViewEntity poOffsetView, @RequestParam String[] fields, HttpServletResponse response) {
        pooffsetviewService.customExport(poOffsetView, fields, response);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入pooffsetview")
    public R save(@Valid @RequestBody PoOffsetViewEntity pooffsetview) {
        return R.status(pooffsetviewService.save(pooffsetview));
    }


    /**
     * 提交跟单日志
     */
    @PostMapping("/submitlog")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入pooffsetview")
    public R submitLog(@Valid @RequestBody PoTracelogDTO tracelogDTO) {
        return R.status(pooffsetviewService.submitLog(tracelogDTO));
    }

    /**
     * 批量提交 跟单日志
     */
    @PostMapping("/submitlogbatch")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入pooffsetview")
    public R submitLogBatch(@Valid @RequestBody List<PoTracelogDTO> tracelogDTOs) {
        return R.status(pooffsetviewService.submitLog(tracelogDTOs));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入pooffsetview")
    public R update(@Valid @RequestBody PoOffsetViewEntity pooffsetview) {
        return R.status(pooffsetviewService.updateById(pooffsetview));
    }

    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(pooffsetviewService.deleteLogic(Func.toLongList(ids)));
    }


    @GetMapping("/statistics")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "统计", notes = "")
    public R<List<StatisticDTO>> statistics() {
        List<StatisticDTO> data = pooffsetviewService.getStatistics(getUser().getAccount());
        return R.data(data);
    }


    @GetMapping("/getinfo")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "数据更新时间", notes = "")
    public R<PoOffsetViewInfo> getLastUpdateTime() {
        PoOffsetViewInfo data = pooffsetviewService.getPoOffsetViewInfo();
        return R.data(data);
    }


    /**
     * 自定义导出（卡控节点）
     */
    @GetMapping("/craftctrlexport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入price_lib")
    public void craftCtrlExport(PoOffsetViewVO poOffsetViewVO, HttpServletResponse response) throws Exception {
        pooffsetviewService.craftCtrlExport(poOffsetViewVO,  response);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/pagemore")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入pooffsetview")
    public R<IPage<PoOffsetViewVO>> listMore(PoOffsetViewReq poOffsetViewReq, Query query) {
        poOffsetViewReq.setTraceCode(getUser().getAccount());
        IPage<PoOffsetViewVO> pages = pooffsetviewService.listMore(query, poOffsetViewReq);
        return R.data(pages);
    }
}
