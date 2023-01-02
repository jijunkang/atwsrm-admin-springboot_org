package org.springblade.modules.pricelib.controller;

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
import org.springblade.modules.pricelib.entity.PriceLibEntity;
import org.springblade.modules.pricelib.service.IPriceLibService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * 控制器
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-pricelib/pricelib")
@Api(value = "", tags = "")
public
class PriceLibController extends BladeController{

    private IPriceLibService priceLibService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入price_lib")
    public
    R<PriceLibEntity> detail(PriceLibEntity price_lib){
        PriceLibEntity detail = priceLibService.getOne(Condition.getQueryWrapper(price_lib));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入price_lib")
    public
    R<IPage<PriceLibEntity>> list(PriceLibEntity price_lib, Query query){
        IPage<PriceLibEntity> pages = priceLibService.selectPage(Condition.getPage(query), price_lib);
        return R.data(pages);
    }

    /**
     * 白名单一键失效
     * @return
     */
    @PostMapping("/invalid")
    public R WhiteByinvalid(@RequestBody List<String> itemCodes){
        return R.status(priceLibService.update(itemCodes));

    }
    /**
     *  导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入price_lib")
    public
    void export(PriceLibEntity priceLib, Query query, HttpServletResponse response) throws Exception{
        priceLibService.export(priceLib,query, response);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入price_lib")
    public
    R save(@Valid @RequestBody PriceLibEntity priceLib){
        if(StringUtil.isAnyBlank(priceLib.getItemCode(), priceLib.getSupCode())){
            return R.fail("物料编号/供应商编号 不能为空");
        }
        if(priceLib.getPrice() == null){
            return R.fail("价格不能为空");
        }
        if(priceLib.getExpirationDate() == null || priceLib.getEffectiveDate() == null){
            return R.fail("生效日期/失效日期不能为空");
        }
        return R.status(priceLibService.save(priceLib));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入price_lib")
    public
    R update(@Valid @RequestBody PriceLibEntity price_lib){
        return R.status(priceLibService.updateById(price_lib));
    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public
    R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids){
        return R.status(priceLibService.deleteLogic(Func.toLongList(ids)));
    }


    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/tocheckpage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入price_lib")
    public
    R<IPage<PriceLibEntity>> toCheckPage(PriceLibEntity priceLib, Query query){
        IPage<PriceLibEntity> pages = priceLibService.toCheckPage(Condition.getPage(query), priceLib);
        return R.data(pages);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/mypage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入price_lib")
    public
    R<IPage<PriceLibEntity>> myPage(PriceLibEntity priceLib, Query query){
        IPage<PriceLibEntity> pages = priceLibService.myPage(Condition.getPage(query), priceLib);
        return R.data(pages);
    }

    /**
     * （审核）
     */
    @PostMapping("/check")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = " 审核", notes = "CheckDTO")
    public
    R check(@Valid @RequestBody CheckDTO checkDto){
        return R.status(priceLibService.check(checkDto));
    }
    /**
     * （审核）
     */
    @PostMapping("/checkbatch")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = " 审核", notes = "CheckDTO")
    public
    R checkBatch(@Valid @RequestBody List<CheckDTO> checkDTOList){
        for(CheckDTO checkDto : checkDTOList ){
            priceLibService.check(checkDto);
        }
        return R.success("操作成功");
    }

    /**
     * 导入白名单
     */
    @PostMapping("/importexcel")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "导入", notes = "MultipartFile")
    public
    R importExcel(@Valid @RequestParam MultipartFile file) throws Exception{
        return R.status(priceLibService.importExcel(file));
    }

    /**
     * 导入白名单api
     */
    @PostMapping("/importexcelfromESB")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "导入", notes = "MultipartFile")
    public
    R importexcelfromESB(@RequestBody List<PriceLibEntity> file) throws Exception{
        return R.status(priceLibService.importexcelfromESB(file));
    }

}
