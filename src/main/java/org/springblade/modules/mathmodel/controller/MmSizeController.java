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
import org.springblade.modules.mathmodel.vo.MmSizeVO;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

import org.springblade.modules.mathmodel.entity.MmSizeEntity;
import org.springblade.modules.mathmodel.service.IMmSizeService;
import org.springframework.web.multipart.MultipartFile;


/**
 * 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-mathmodel/mmSize")
@Api(value = "", tags = "")
public class MmSizeController extends BladeController {

    private IMmSizeService mmSizeService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入mmSize")
    public R<MmSizeVO> detail(MmSizeEntity mmSize) {
        MmSizeEntity detail = mmSizeService.getOne(Condition.getQueryWrapper(mmSize));
        MmSizeVO vo = BeanUtil.copy(detail, MmSizeVO.class);
        vo.setHistories(mmSizeService.getByHistoryId(detail.getId()));
        return R.data(vo);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入mmSize")
    public R<IPage<MmSizeEntity>> page(MmSizeEntity mmSize, Query query) {
        IPage<MmSizeEntity> pages = mmSizeService.page(Condition.getPage(query), mmSizeService.getQueryWrapper(mmSize));
        return R.data(pages);
    }

    /**
     * 列表 代码自定义代号
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "列表", notes = "传入mmSize")
    public R<List<MmSizeEntity>> list(MmSizeEntity mmSize) {
        List<MmSizeEntity> list = mmSizeService.list(Condition.getQueryWrapper(mmSize));
        return R.data(list);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入mmSize")
    public R save(@Valid @RequestBody MmSizeEntity mmSize) {
        //物料编号唯一
        if(mmSizeService.getByItemCode(mmSize.getItemCode()) != null){
            throw new RuntimeException("该料号已存在：" + mmSize.getItemCode());
        }
        return R.status(mmSizeService.save(mmSize));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入mmSize")
    public R update(@Valid @RequestBody MmSizeEntity mmSize) {
        return R.status(mmSizeService.update(mmSize));
    }

    /**
     * 新增或修改 代码自定义代号
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入mmSize")
    public R submit(@Valid @RequestBody MmSizeEntity mmSize) {
        return R.status(mmSizeService.saveOrUpdate(mmSize));
    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(mmSizeService.deleteLogic(Func.toLongList(ids)));
    }

    /**
     * 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入mmSize")
    public void export(MmSizeEntity mmSize, Query query, HttpServletResponse response) throws Exception {
        mmSizeService.export(mmSize, query, response);
    }

    /**
     * 导入
     */
    @PostMapping("/importexcel")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "导入", notes = "MultipartFile")
    public R importExcel(@Valid @RequestParam MultipartFile file) throws Exception {
        Map<String, Object> map = mmSizeService.importExcel(file);
        if (Boolean.parseBoolean(map.get("flag").toString())) {
            return R.success(map.get("msg").toString());
        }
        return R.fail(map.get("msg").toString());
    }
}
