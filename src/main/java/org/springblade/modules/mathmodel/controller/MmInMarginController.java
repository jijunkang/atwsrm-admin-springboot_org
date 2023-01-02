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
import org.springblade.modules.mathmodel.vo.MmInMarginVO;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

import org.springblade.modules.mathmodel.entity.MmInMarginEntity;
import org.springblade.modules.mathmodel.service.IMmInMarginService;
import org.springframework.web.multipart.MultipartFile;


/**
 * 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-mathmodel/mmInMargin")
@Api(value = "", tags = "")
public class MmInMarginController extends BladeController {

    private IMmInMarginService mmInMarginService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入mmInMargin")
    public R<MmInMarginVO> detail(MmInMarginEntity mmInMargin) {
        MmInMarginEntity detail = mmInMarginService.getOne(Condition.getQueryWrapper(mmInMargin));
        MmInMarginVO vo = BeanUtil.copy(detail, MmInMarginVO.class);
        vo.setHistories(mmInMarginService.getByHistoryId(detail.getId()));
        return R.data(vo);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入mmInMargin")
    public R<IPage<MmInMarginEntity>> page(MmInMarginEntity mmInMargin, Query query) {
        IPage<MmInMarginEntity> pages = mmInMarginService.page(Condition.getPage(query), mmInMarginService.getQueryWrapper(mmInMargin));
        return R.data(pages);
    }

    /**
     * 列表 代码自定义代号
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "列表", notes = "传入mmInMargin")
    public R<List<MmInMarginEntity>> list(MmInMarginEntity mmInMargin) {
        List<MmInMarginEntity> list = mmInMarginService.list(Condition.getQueryWrapper(mmInMargin));
        return R.data(list);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入mmInMargin")
    public R save(@Valid @RequestBody MmInMarginEntity mmInMargin) {
        /*if(mmInMarginService.getByChildCode(mmInMargin.getChildCode()) != null){
            throw new RuntimeException("改子分类已存在：" + mmInMargin.getChildCode());
        }*/
        return R.status(mmInMarginService.save(mmInMargin));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入mmInMargin")
    public R update(@Valid @RequestBody MmInMarginEntity mmInMargin) {
        return R.status(mmInMarginService.update(mmInMargin));
    }

    /**
     * 新增或修改 代码自定义代号
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入mmInMargin")
    public R submit(@Valid @RequestBody MmInMarginEntity mmInMargin) {
        return R.status(mmInMarginService.saveOrUpdate(mmInMargin));
    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(mmInMarginService.deleteLogic(Func.toLongList(ids)));
    }

    /**
     * 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入mmInMargin")
    public void export(MmInMarginEntity mmInMargin, Query query, HttpServletResponse response) throws Exception {
        mmInMarginService.export(mmInMargin, query, response);
    }

    /**
     * 导入
     */
    @PostMapping("/importexcel")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "导入", notes = "MultipartFile")
    public R importExcel(@Valid @RequestParam MultipartFile file) throws Exception {
        Map<String, Object> map = mmInMarginService.importExcel(file);
        if (Boolean.parseBoolean(map.get("flag").toString())) {
            return R.success(map.get("msg").toString());
        }
        return R.fail(map.get("msg").toString());
    }


    /**
     * 批量修改余量
     */
    @PostMapping("/updatemargins")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "批量修改余量", notes = "传入mmInMarginEntities")
    public R updateMargins(@Valid @RequestBody List<MmInMarginEntity> mmInMarginEntities) {
        return R.status(mmInMarginService.updateMargins(mmInMarginEntities));
    }
}
