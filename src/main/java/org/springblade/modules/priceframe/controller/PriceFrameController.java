package org.springblade.modules.priceframe.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.pr.dto.U9PrDTO;
import org.springblade.modules.priceframe.dto.CenterPriceFrame;
import org.springblade.modules.priceframe.entity.PriceFrameEntity;
import org.springblade.modules.priceframe.service.IPriceFrameService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-priceframe/priceframe")
@Api(value = "", tags = "")
public
class PriceFrameController extends BladeController {

    private IPriceFrameService priceFrameService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入price_lib")
    public R<PriceFrameEntity> detail(PriceFrameEntity priceFrameEntity) {
        PriceFrameEntity detail = priceFrameService.getOne(Condition.getQueryWrapper(priceFrameEntity));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入priceFrameEntity")
    public R<IPage<PriceFrameEntity>> list(PriceFrameEntity priceFrameEntity, Query query) {
        IPage<PriceFrameEntity> pages = priceFrameService.selectPage(Condition.getPage(query), priceFrameEntity);
        return R.data(pages);
    }

    /**
     * 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入priceFrameEntity")
    public void export(PriceFrameEntity priceFrameEntity, Query query, HttpServletResponse response) throws Exception {
        priceFrameService.export(priceFrameEntity, query, response);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入priceFrameEntity")
    public R save(@Valid @RequestBody PriceFrameEntity priceFrameEntity) {
        if (StringUtil.isAnyBlank(priceFrameEntity.getItemCode(), priceFrameEntity.getSupCode())) {
            return R.fail("物料编号/供应商编号 不能为空");
        }
        if (priceFrameEntity.getPrice() == null) {
            return R.fail("价格不能为空");
        }
        if (priceFrameEntity.getExpirationDate() == null || priceFrameEntity.getEffectiveDate() == null) {
            return R.fail("生效日期/失效日期不能为空");
        }
        PriceFrameEntity result = priceFrameService.saveCheck(priceFrameEntity);
        if (StringUtil.isEmpty(result)) {
            return R.status(priceFrameService.save(priceFrameEntity));
        }
        return R.fail("该采购数量区间" + result.getLimitMin() + "-" + result.getLimitMax() + "已存在，若需修改，请将冲突的旧数据失效后在录入");
    }


    /**
     * 获取默认最小值
     */
    @GetMapping("/getlimitmin")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入priceFrameEntity")
    public R<Map<String, Object>> getLimitMin(PriceFrameEntity priceFrameEntity) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("limitMin", priceFrameService.getLimitMin(priceFrameEntity));
        return R.data(map);
    }


    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入priceFrameEntity")
    public R update(@Valid @RequestBody PriceFrameEntity priceFrameEntity) {
        return R.status(priceFrameService.updateById(priceFrameEntity));
    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(priceFrameService.deleteLogic(Func.toLongList(ids)));
    }


    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/tocheckpage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入priceFrameEntity")
    public R<IPage<PriceFrameEntity>> toCheckPage(PriceFrameEntity priceFrameEntity, Query query) {
        IPage<PriceFrameEntity> pages = priceFrameService.toCheckPage(Condition.getPage(query), priceFrameEntity);
        return R.data(pages);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/mypage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入priceFrameEntity")
    public R<IPage<PriceFrameEntity>> myPage(PriceFrameEntity priceFrameEntity, Query query) {
        IPage<PriceFrameEntity> pages = priceFrameService.myPage(Condition.getPage(query), priceFrameEntity);
        return R.data(pages);
    }

    /**
     * （审核）
     */
    @PostMapping("/check")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = " 审核", notes = "CheckDTO")
    public R check(@Valid @RequestBody CheckDTO checkDto) {
        return R.status(priceFrameService.check(checkDto));
    }

    /**
     * （审核）
     */
    @PostMapping("/checkbatch")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = " 审核", notes = "CheckDTO")
    public R checkBatch(@Valid @RequestBody List<CheckDTO> checkDTOList) {
        for (CheckDTO checkDto : checkDTOList) {
            priceFrameService.check(checkDto);
        }
        return R.success("操作成功");
    }

    /**
     * 导入框架协议
     */
    @PostMapping("/importexcel")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "导入", notes = "MultipartFile")
    public R importExcel(@Valid @RequestParam MultipartFile file) throws Exception {
        Map<String, Object> map = priceFrameService.importExcel(file);
        if(Boolean.parseBoolean(map.get("flag").toString())){
            return R.success(map.get("msg").toString());
        }
        return R.fail(map.get("msg").toString());
    }


    /**
     * 中台-框架协议请购单
     */
    @GetMapping("/center")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入priceFrameEntity")
    public R<IPage<CenterPriceFrame>> center(PriceFrameEntity priceFrameEntity, Query query) {
        IPage<CenterPriceFrame> pages = priceFrameService.center(Condition.getPage(query), priceFrameEntity);
        return R.data(pages);
    }


    /**
     * 中台-框架协议请购单-状态统计
     */
    @GetMapping("/countlist")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入bizInquiry")
    public R<List<Map<String, Object>>> countList() {
        return R.data(priceFrameService.countList());
    }


    /**
     * 询交期
     */
    @PostMapping("/submitdates")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入priceFrameEntity")
    public R submitDates(@Valid @RequestBody List<CenterPriceFrame> centerPriceFrames) {
        return R.status(priceFrameService.submitDates(centerPriceFrames));
    }

    /**
     * （审核）
     */
    @PostMapping("/submitbatch")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = " 审核", notes = "CheckDTO")
    public R submitBatch(@Valid @RequestBody List<CenterPriceFrame> centerPriceFrames) {
        return R.status(priceFrameService.submitBatch(centerPriceFrames));
    }


    /**
     * 失效物料判断
     */
    @PostMapping("/checkonly")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = " 审核", notes = "CheckDTO")
    public R checkOnly(@Valid @RequestBody List<CenterPriceFrame> centerPriceFrames) {
        return R.success(priceFrameService.checkOnly(centerPriceFrames));
    }
    /**
     * 批量作废
     */
    @PostMapping("/invalids")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = " 审核", notes = "CheckDTO")
    public R invalids(@Valid @RequestBody List<CenterPriceFrame> centerPriceFrames) {
        return R.status(priceFrameService.invalids(centerPriceFrames));
    }
}
