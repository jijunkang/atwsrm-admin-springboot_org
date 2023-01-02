package org.springblade.modules.supplier.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.supplier.entity.PaywayEntity;
import org.springblade.modules.supplier.service.IPaywayService;
import org.springblade.modules.supplier.vo.PaywayVO;
import org.springblade.modules.system.service.IDictBizService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-supplier/payway")
@Api(value = "", tags = "")
public class PaywayController extends BladeController {

    private IPaywayService paywayService;

    private IDictBizService bizService;
    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入payway")
    public R<PaywayEntity> detail(PaywayEntity payway) {
        PaywayEntity detail = paywayService.getOne(Condition.getQueryWrapper(payway));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入payway")
    public R<IPage<PaywayEntity>> page(PaywayEntity payway, Query query) {
        IPage<PaywayEntity> pages = paywayService.page(Condition.getPage(query), Condition.getQueryWrapper(payway));
        return R.data(pages);
    }

    /**
     * 下单弹出框使用
     */
    @GetMapping("/payWayAndTemplate")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入payway")
    public R<IPage<PaywayVO>> payWayAndTemplate(PaywayEntity payway, Query query) {
        IPage<PaywayVO> pages = paywayService.getPayWayAndTemplate(Condition.getPage(query), payway.getSupCode());
        return R.data(pages);
    }




    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入payway")
    public R save(@Valid @RequestBody PaywayEntity payway) {

        if (payway.getIsDefault()) {
            QueryWrapper<PaywayEntity> qw = Wrappers.<PaywayEntity>query().eq("sup_code",payway.getSupCode());
            paywayService.list(qw).forEach(temp -> {
                temp.setIsDefault(false);
                paywayService.updateById(temp);
            });
        }
        payway.setTypeName( bizService.getValue(IPaywayService.DICT_BIZ_CODE,payway.getType()) );
        return R.status(paywayService.save(payway));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入payway")
    public R update(@Valid @RequestBody PaywayEntity payway) {
        if (payway.getIsDefault()) {
            QueryWrapper<PaywayEntity> qw = Wrappers.<PaywayEntity>query().eq("sup_code",payway.getSupCode());
            paywayService.list(qw).forEach(temp -> {
                temp.setIsDefault(false);
                paywayService.updateById(temp);
            });
        }
        payway.setTypeName( bizService.getValue(IPaywayService.DICT_BIZ_CODE,payway.getType()) );
        return R.status(paywayService.updateById(payway));
    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(paywayService.deleteLogic(Func.toLongList(ids)));
    }

    /**
     * 详情
     */
    @GetMapping("/getone")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入payway")
    public R<PaywayEntity> getOne(PaywayEntity payway) {
        QueryWrapper<PaywayEntity> queryWrapper = Condition.getQueryWrapper(new PaywayEntity()).orderByDesc("create_time");
        queryWrapper.eq("sup_code", payway.getSupCode());
        List<PaywayEntity> payWayEntities = paywayService.list(queryWrapper);
        PaywayEntity detail = payWayEntities.get(0);
        for (PaywayEntity payWayEntity : payWayEntities) {
            if (payWayEntity.getIsDefault()) {
                detail = payWayEntity;
            }
        }
        return R.data(detail);
    }


}
