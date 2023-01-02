package org.springblade.modules.mathmodel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.modules.mathmodel.entity.MailyMaterialTotalEntity;
import org.springblade.modules.mathmodel.service.TubeMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


/**
 * Author: 昕月
 * Date：2022/5/24 13:29
 * Desc:
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-mathmodel/tube")
public class TubeMaterialController extends BladeController {


    @Autowired
    private TubeMaterialService tubeMaterialService;


    @GetMapping("/page")
    public R<IPage<MailyMaterialTotalEntity>> page(MailyMaterialTotalEntity mailyMaterialTotalEntity, Query query) {
        IPage<MailyMaterialTotalEntity> list = tubeMaterialService.page(Condition.getPage(query),Condition.getQueryWrapper(mailyMaterialTotalEntity));
        return R.data(list);
    }

    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入mmSize")
    public void export(MailyMaterialTotalEntity materialMaliyVO, Query query, HttpServletResponse response) throws Exception {
        tubeMaterialService.export(materialMaliyVO, query, response);
    }



}
