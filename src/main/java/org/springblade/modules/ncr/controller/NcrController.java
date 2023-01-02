package org.springblade.modules.ncr.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.modules.ncr.dto.NcrDTO;
import org.springblade.modules.ncr.entity.NcrEntity;
import org.springblade.modules.ncr.service.INcrService;
import org.springblade.modules.ncr.vo.NcrVO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/ncr")
@Api(value = "", tags = "")
public
class NcrController extends BladeController {

    private INcrService ncrService;

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页", notes = "传入ncr")
    public R<IPage<NcrEntity>> page(NcrDTO ncr, Query query) {
        IPage<NcrEntity> pages = ncrService.page(Condition.getPage(query), ncrService.getQueryWrapper(ncr));
        return R.data(pages);
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "详情", notes = "传入ncr")
    public R<NcrVO> detail(NcrEntity ncr) {
        NcrVO detail = ncrService.detail(ncr);
        return R.data(detail);
    }

    /**
     * 报表导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "报表导出", notes = "报表导出")
    public void export(HttpServletResponse response, NcrDTO ncr){
        ncrService.export(response, ncr);
    }

    /**
     * 中台列表
     */
    @GetMapping("/center")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "中台列表", notes = "传入ncr")
    public R<IPage<NcrEntity>> center(NcrDTO ncr, Query query) {
        IPage<NcrEntity> pages = ncrService.page(Condition.getPage(query), ncrService.getCenter(ncr));
        return R.data(pages);
    }

    /**
     * 生成扣款单
     */
    @PostMapping("/creatercv")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "生成扣款单", notes = "生成扣款单")
    public R createRcv(@Valid @RequestBody List<NcrEntity> ncrEntityList) {
        return R.status(ncrService.createRcv(ncrEntityList));
    }

    /**
     * 合并供应商生成扣款单
     */
    @GetMapping("/creatercvbatch")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "生成扣款单", notes = "生成扣款单")
    public void createRcvBatch() {
        ncrService.createRcvBatch();
    }

    /**
     * 统计未结案数量
     */
    @GetMapping("/getCount")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "统计未结案数量", notes = "统计未结案数量")
    public R<Map<String, Object>> getNotCount(){
        return R.data(ncrService.getNotCount());
    }
}
