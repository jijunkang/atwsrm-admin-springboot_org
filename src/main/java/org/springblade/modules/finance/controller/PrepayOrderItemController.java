package org.springblade.modules.finance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.modules.finance.dto.PrepayOrderItemDTO;
import org.springblade.modules.finance.entity.PrepayOrderItemEntity;
import org.springblade.modules.finance.service.IPrepayOrderItemService;
import org.springblade.modules.finance.vo.PrepayOrderItemVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/finance/prepayorderitem")
@Api(value = "", tags = "")
public class PrepayOrderItemController extends BladeController {

    private final IPrepayOrderItemService prepayOrderItemService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入prepayOrderItem")
    public R<PrepayOrderItemEntity> detail(PrepayOrderItemEntity prepayOrderItem) {
        PrepayOrderItemEntity detail = prepayOrderItemService.getOne(Condition.getQueryWrapper(prepayOrderItem));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入prepayOrderItem")
    public R<IPage<PrepayOrderItemVO>> list(PrepayOrderItemDTO prepayOrderItem, Query query) {
        IPage<PrepayOrderItemVO> pages = prepayOrderItemService.pageVo(prepayOrderItem, query);
        return R.data(pages);
    }

//    /**
//     * 新增 代码自定义代号
//     */
//    @PostMapping("/save")
//    @ApiOperationSupport(order = 4)
//    @ApiOperation(value = "新增", notes = "传入prepayOrderItem")
//    public R save(@Valid @RequestBody PrepayOrderItemEntity prepayOrderItem) {
//        return R.status(prepayOrderItemService.save(prepayOrderItem));
//    }
//
//    /**
//     * 修改 代码自定义代号
//     */
//    @PostMapping("/update")
//    @ApiOperationSupport(order = 5)
//    @ApiOperation(value = "修改", notes = "传入prepayOrderItem")
//    public R update(@Valid @RequestBody PrepayOrderItemEntity prepayOrderItem) {
//        return R.status(prepayOrderItemService.updateById(prepayOrderItem));
//    }
//
//    /**
//     * 新增或修改 代码自定义代号
//     */
//    @PostMapping("/submit")
//    @ApiOperationSupport(order = 6)
//    @ApiOperation(value = "新增或修改", notes = "传入prepayOrderItem")
//    public R submit(@Valid @RequestBody PrepayOrderItemEntity prepayOrderItem) {
//        return R.status(prepayOrderItemService.saveOrUpdate(prepayOrderItem));
//    }
//
//
//    /**
//     * 删除 代码自定义代号
//     */
//    @PostMapping("/remove")
//    @ApiOperationSupport(order = 7)
//    @ApiOperation(value = "逻辑删除", notes = "传入ids")
//    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//        return R.status(prepayOrderItemService.deleteLogic(Func.toLongList(ids)));
//    }

}
