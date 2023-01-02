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
import org.springblade.modules.mathmodel.entity.TubeMaterialInfoEntity;
import org.springblade.modules.mathmodel.service.TubeMaterialInfoService;
import org.springblade.modules.mathmodel.service.TubeMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


/**
 * Author: 昕月
 * Date：2022/5/24 13:29
 * Desc:
 */
@RestController
@RequestMapping("/blade-mathmodel/gblReport")
public class TubeMaterialInfoController extends BladeController {

    @Autowired
    private TubeMaterialInfoService tubeMaterialInfoService;



    @GetMapping("/page")
    public R<IPage<TubeMaterialInfoEntity>> page(TubeMaterialInfoEntity tubeMaterialInfoEntity,Query query){
        IPage<TubeMaterialInfoEntity> page = tubeMaterialInfoService.page(Condition.getPage(query), tubeMaterialInfoService.getQueryWrapper(tubeMaterialInfoEntity));
        return R.data(page);
    }


    @GetMapping("/export")
    public void export(TubeMaterialInfoEntity materialInfoEntity,Query query,HttpServletResponse response){
        tubeMaterialInfoService.export(materialInfoEntity,query,response);
    }



}
