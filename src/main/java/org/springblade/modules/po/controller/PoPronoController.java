package org.springblade.modules.po.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.common.constant.CommonConstant;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.springblade.modules.po.entity.PoPronoEntity;
import org.springblade.modules.po.vo.PoPronoVO;
import org.springblade.modules.po.wrapper.PoPronoWrapper;
import org.springblade.modules.po.service.IPoPronoService;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-po/poprono")
@Api(value = "", tags = "")
public class PoPronoController extends BladeController {

    private IPoPronoService popronoService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入poprono")
    public R<PoPronoEntity> detail(PoPronoEntity poprono) {
        PoPronoEntity detail = popronoService.getOne(Condition.getQueryWrapper(poprono));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入poprono")
    public R<IPage<PoPronoEntity>> list(PoPronoEntity poprono, Query query) {
        IPage<PoPronoEntity> pages = popronoService.page(Condition.getPage(query), Condition.getQueryWrapper(poprono));
        return R.data(pages);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入poprono")
    public R save(@Valid @RequestBody PoPronoEntity poprono) {
        return R.status(popronoService.save(poprono));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入poprono")
    public R update(@Valid @RequestBody PoPronoEntity poprono) {
        return R.status(popronoService.updateById(poprono));
    }

    /**
     * 新增或修改 代码自定义代号
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入poprono")
    public R submit(@Valid @RequestBody PoPronoEntity poprono) {
        return R.status(popronoService.saveOrUpdate(poprono));
    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(popronoService.deleteLogic(Func.toLongList(ids)));
    }

}
