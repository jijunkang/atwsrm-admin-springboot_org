package org.springblade.modules.material.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import com.google.api.client.util.SecurityUtils;
import com.google.api.client.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.finance.service.IPrepayOrderService;
import org.springblade.modules.material.dto.MaterialPriceDTO;
import org.springblade.modules.material.dto.MaterialPriceNewDTO;
import org.springblade.modules.material.entity.MaterialPriceEntity;
import org.springblade.modules.material.entity.MaterialPriceNewEntity;
import org.springblade.modules.material.service.IMaterialPriceNewService;
import org.springblade.modules.material.service.IMaterialPriceService;
import org.springblade.modules.po.dto.PoItemDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * 控制器
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-material/materialPrice")
@Api(value = "", tags = "")
public
class MaterialPriceController extends BladeController{

    private IMaterialPriceService materialPriceService;

    private IMaterialPriceNewService materialPriceNewService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入materialPrice")
    public
    R<MaterialPriceEntity> detail(MaterialPriceEntity materialPrice){
        MaterialPriceEntity detail = materialPriceService.getOne(Condition.getQueryWrapper(materialPrice));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入materialPrice")
    public
    R<IPage<MaterialPriceEntity>> list(MaterialPriceDTO materialPrice, Query query){
        IPage<MaterialPriceEntity> pages = materialPriceService.page(Condition.getPage(query), materialPriceService.getQueryWrapper(materialPrice));
        return R.data(pages);
    }



    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入materialPrice")
    public
    R save(@Valid @RequestBody MaterialPriceEntity materialPrice){
        return R.status(materialPriceService.save(materialPrice));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入materialPrice")
    public
    R update(@Valid @RequestBody MaterialPriceEntity materialPrice){
        return R.status(materialPriceService.updateById(materialPrice));
    }

    @PostMapping("/updatebatch")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入materialPrice")
    public
    R updateBatch(@Valid @RequestBody List<MaterialPriceEntity> entityList){
        return R.status(materialPriceService.updateBatchById(entityList));
    }

    /**
     * 新增或修改 代码自定义代号
     */
//    @PostMapping("/submit")
//    @ApiOperationSupport(order = 6)
//    @ApiOperation(value = "新增或修改", notes = "传入materialPrice")
//    public
//    R submit(@Valid @RequestBody MaterialPriceEntity materialPrice){
//        return R.status(materialPriceService.saveOrUpdate(materialPrice));
//    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public
    R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids){
        return R.status(materialPriceService.deleteLogic(Func.toLongList(ids)));
    }

    /**
     * 交付中心 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public
    void exportExcel(MaterialPriceDTO dto, HttpServletResponse response) throws RuntimeException{
        materialPriceService.exportExcel(dto, response);
    }

    /**
     * 导入
     */
    @PostMapping("/import")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "导入", notes = "MultipartFile")
    public
    R importExcel(@Valid @RequestParam MultipartFile file) throws RuntimeException{
        return R.status(materialPriceService.importExcel(file));
    }

    /**
     * 分页 代码自定义代号 页面重写
     */
    @GetMapping("/pageNew")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入materialPrice")
    public
    R<IPage<MaterialPriceNewEntity>> getMaterialPricelist(MaterialPriceNewDTO materialPrice, Query query){
        BladeUser user= getUser();
        if(materialPrice.getStatus()==null){
            if (user.getRoleName().indexOf("商务")>-1) {
                materialPrice.setStatus(30);
            }else{
                materialPrice.setStatus(10);
            }
        }

        IPage<MaterialPriceNewEntity> pages = materialPriceNewService.page(Condition.getPage(query), materialPriceNewService.getQueryWrapper(materialPrice));
        return R.data(pages);
    }

    /**
     * 交付中心 导出 重写
     */
    @GetMapping("/exportNew")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public
    void exportExcelNew(MaterialPriceNewDTO dto, HttpServletResponse response) throws RuntimeException{
        materialPriceNewService.exportExcel(dto, response);
    }

    /**
     * 导入 重写
     */
    @PostMapping("/importNew")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "导入", notes = "MultipartFile")
    public
    R importExcelNew(@Valid @RequestParam MultipartFile file) throws RuntimeException{
        return R.status(materialPriceNewService.importExcel(file));
    }

    @PostMapping("/removeNew")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "非逻辑删除", notes = "传入ids")
    public
    R removeNew(@ApiParam(value = "主键集合", required = true) @RequestParam String ids){
        return R.status(materialPriceNewService.deleteById(Func.toLongList(ids)));
    }

    @PostMapping("/updatebatchNew")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入materialPrice")
    public
    R updatebatchNew(@Valid @RequestBody List<MaterialPriceNewEntity> entityList){

        if(entityList.size()>0){
            if(entityList.get(0).getStatus()==10){
                return R.status(materialPriceNewService.passMateralPrice(entityList));

            }else{
                return R.status(materialPriceNewService.updateBatchById(entityList));
            }

        }
        return R.status(true);

    }



}
