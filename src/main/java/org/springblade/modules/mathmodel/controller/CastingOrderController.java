package org.springblade.modules.mathmodel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.modules.mathmodel.entity.CastingOrderEntity;
import org.springblade.modules.mathmodel.service.CastingOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Author: 昕月
 * Date：2022/6/7 19:50
 * Desc: 铸件自动下单报表
 */

@RestController
@RequestMapping("/blade-mathmodel/castingReport")
public class CastingOrderController {

    @Autowired
    private CastingOrderService castingOrderService;


    @GetMapping("/page")
    public R<IPage<CastingOrderEntity>> page(CastingOrderEntity castingOrder, Query query){
        IPage<CastingOrderEntity> page = castingOrderService.page(Condition.getPage(query), castingOrderService.getQueryWrapper(castingOrder));
        return R.data(page);
    }

    @GetMapping("/export")
    public void export(CastingOrderEntity castingOrder, Query query, HttpServletResponse response){
        castingOrderService.export(castingOrder,query,response);
    }
}


