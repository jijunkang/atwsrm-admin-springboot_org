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
import org.springblade.modules.mathmodel.vo.MmCoefVO;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

import org.springblade.modules.mathmodel.entity.MmCoefEntity;
import org.springblade.modules.mathmodel.service.IMmCoefService;
import org.springframework.web.multipart.MultipartFile;


/**
 * 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-mathmodel/mmCoef")
@Api(value = "", tags = "")
public class MmCoefController extends BladeController {

    private IMmCoefService mmCoefService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入mmCoef")
    public R<MmCoefVO> detail(MmCoefEntity mmCoef) {
        MmCoefEntity detail = mmCoefService.getOne(Condition.getQueryWrapper(mmCoef));
        MmCoefVO vo = BeanUtil.copy(detail, MmCoefVO.class);
        vo.setHistories(mmCoefService.getByHistoryId(detail.getId()));
        return R.data(vo);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入mmCoef")
    public R<IPage<MmCoefEntity>> page(MmCoefEntity mmCoef, Query query) {
        IPage<MmCoefEntity> pages = mmCoefService.page(Condition.getPage(query), mmCoefService.getQueryWrapper(mmCoef));
        return R.data(pages);
    }

    /**
     * 列表 代码自定义代号
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "列表", notes = "传入mmCoef")
    public R<List<MmCoefEntity>> list(MmCoefEntity mmCoef) {
        List<MmCoefEntity> list = mmCoefService.list(Condition.getQueryWrapper(mmCoef));
        return R.data(list);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入mmCoef")
    public R save(@Valid @RequestBody MmCoefEntity mmCoef) {
        return R.status(mmCoefService.save(mmCoef));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入mmCoef")
    public R update(@Valid @RequestBody MmCoefEntity mmCoef) {
        return R.status(mmCoefService.update(mmCoef));
    }

    /**
     * 新增或修改 代码自定义代号
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入mmCoef")
    public R submit(@Valid @RequestBody MmCoefEntity mmCoef) {
        return R.status(mmCoefService.saveOrUpdate(mmCoef));
    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(mmCoefService.deleteLogic(Func.toLongList(ids)));
    }

    /**
     * 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入mmSize")
    public void export(MmCoefEntity mmCoefEntity, Query query, HttpServletResponse response) throws Exception {
        mmCoefService.export(mmCoefEntity, query, response);
    }


    /**
     * 导入
     */
    @PostMapping("/importexcel")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "导入", notes = "MultipartFile")
    public R importExcel(@Valid @RequestParam MultipartFile file) throws Exception {
        Map<String, Object> map = mmCoefService.importExcel(file);
        if (Boolean.parseBoolean(map.get("flag").toString())) {
            return R.success(map.get("msg").toString());
        }
        return R.fail(map.get("msg").toString());
    }
}
