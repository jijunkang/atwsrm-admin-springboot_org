package org.springblade.modules.mathmodel.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springblade.core.boot.ctrl.BladeController;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.mathmodel.vo.MmHotPriceVO;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

import org.springblade.modules.mathmodel.entity.MmHotPriceEntity;
import org.springblade.modules.mathmodel.service.IMmHotPriceService;
import org.springframework.web.multipart.MultipartFile;


/**
 * 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-mathmodel/mmHotPrice")
@Api(value = "", tags = "")
public class MmHotPriceController extends BladeController {

    private IMmHotPriceService mmHotPriceService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入mmHotPrice")
    public R<MmHotPriceEntity> detail(MmHotPriceEntity mmHotPrice) {
        MmHotPriceEntity detail = mmHotPriceService.getOne(Condition.getQueryWrapper(mmHotPrice));
        MmHotPriceVO vo = BeanUtil.copy(detail, MmHotPriceVO.class);
        vo.setHistories(mmHotPriceService.getByHistoryId(detail.getId()));
        return R.data(vo);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入mmHotPrice")
    public R<IPage<MmHotPriceEntity>> page(MmHotPriceEntity mmHotPrice, Query query) {
        IPage<MmHotPriceEntity> pages = mmHotPriceService.page(Condition.getPage(query), mmHotPriceService.getQueryWrapper(mmHotPrice));
        return R.data(pages);
    }

    /**
     * 列表 代码自定义代号
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "列表", notes = "传入mmHotPrice")
    public R<List<MmHotPriceEntity>> list(MmHotPriceEntity mmHotPrice) {
        List<MmHotPriceEntity> list = mmHotPriceService.list(Condition.getQueryWrapper(mmHotPrice));
        return R.data(list);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入mmHotPrice")
    public R save(@Valid @RequestBody MmHotPriceEntity mmHotPrice) {
        return R.status(mmHotPriceService.save(mmHotPrice));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入mmHotPrice")
    public R update(@Valid @RequestBody MmHotPriceEntity mmHotPrice) {
        return R.status(mmHotPriceService.update(mmHotPrice));
    }

    /**
     * 新增或修改 代码自定义代号
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入mmHotPrice")
    public R submit(@Valid @RequestBody MmHotPriceEntity mmHotPrice) {
        return R.status(mmHotPriceService.saveOrUpdate(mmHotPrice));
    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(mmHotPriceService.deleteLogic(Func.toLongList(ids)));
    }

    /**
     * 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入mmHotPrice")
    public void export(MmHotPriceEntity mmHotPrice, Query query, HttpServletResponse response) throws Exception {
        mmHotPriceService.export(mmHotPrice, query, response);
    }

    /**
     * 导入
     */
    @PostMapping("/importexcel")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "导入", notes = "MultipartFile")
    public R importExcel(@Valid @RequestParam MultipartFile file) throws Exception {
        Map<String, Object> map = mmHotPriceService.importExcel(file);
        if (Boolean.parseBoolean(map.get("flag").toString())) {
            return R.success(map.get("msg").toString());
        }
        return R.fail(map.get("msg").toString());
    }

}
