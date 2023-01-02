package org.springblade.modules.finance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
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
import org.springblade.modules.finance.dto.PrepayOrderDTO;
import org.springblade.modules.finance.dto.PrepayOrderUpdateDto;
import org.springblade.modules.finance.entity.PrepayOrderEntity;
import org.springblade.modules.finance.entity.PrepayOrderItemEntity;
import org.springblade.modules.finance.service.IPrepayOrderService;
import org.springblade.modules.finance.vo.PrepayOrderVO;
import org.springblade.modules.po.dto.PoDTO;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.service.IPoService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 控制器
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/finance/prepayorder")
@Api(value = "", tags = "")
public
class PrepayOrderController extends BladeController{

    private final IPrepayOrderService prepayOrderService;

    private final IPoService poService;

    /**
     * 订单付款明细列表
     */
    @GetMapping("/prepostatistics")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "数量统计", notes = "传入prepayOrder")
    public
    R<List> prePoStatistics() throws ParseException {
        PoDTO poDTO = new PoDTO();
        List  list  = Lists.newArrayList();
        poDTO.setType("remind");
        Map<String, String> retMap1 = new HashMap<String, String>(){{
            put("key", "remind");
            put("title", "请款提醒");
            put("count", poService.getPrePoCount(poDTO) + "");
        }};
        list.add(retMap1);

        poDTO.setType("all");
        Map<String, String> retMap2 = new HashMap<String, String>(){{
            put("key", "all");
            put("title", "订单付款明细");
            put("count", poService.getPrePoCount(poDTO) + "");
        }};
        list.add(retMap2);
        return R.data(list);
    }

    /**
     * 订单付款明细列表
     */
    @GetMapping("/prepopage")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入prepayOrder")
    public
    R<IPage<PoEntity>> prePoPage(PoDTO poDTO, Query query) throws ParseException {
        IPage<PoEntity> page = poService.getPrePoPage(poDTO, Condition.getPage(query));
        for (PoEntity entity : page.getRecords()) {
            BigDecimal closeAmount = poService.getSumCloseAmount(entity.getOrderCode());
            if(closeAmount != null){
                entity.setDocAmount(entity.getDocAmount().subtract(closeAmount));
            }
        }
        return R.data(page);
    }

    /**
     * 预付款申请页签数据
     */
    @GetMapping("/statistics")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入prepayOrder")
    public
    R<List<Map<String, Object>>> statistics(){
        List<Map<String, Object>> ret = Lists.newArrayList();
        ret.add(new ArrayMap<String, Object>(){{
            put("status", IPrepayOrderService.STATUS_INIT);
            put("title", "待提交");
            put("count", prepayOrderService.countByStatus(IPrepayOrderService.STATUS_INIT + ""));
        }});
        ret.add(new ArrayMap<String, Object>(){{
            put("status", IPrepayOrderService.STATUS_SUBMIT);
            put("title", "待审核");
            put("count", prepayOrderService.countByStatus(IPrepayOrderService.STATUS_SUBMIT + ""));
        }});
//        ret.add(new ArrayMap<String, Object>(){{
//            put("status", IPrepayOrderService.STATUS_1PASS + "," + IPrepayOrderService.STATUS_1PASS_R);
//            put("title", "待二级审核");
//            put("count", prepayOrderService
//                    .countByStatus(IPrepayOrderService.STATUS_1PASS + "," + IPrepayOrderService.STATUS_1PASS_R));
//        }});
//        ret.add(new ArrayMap<String, Object>(){{
//            put("status", IPrepayOrderService.STATUS_2PASS);
//            put("title", "待三级审核");
//            put("count", prepayOrderService.countByStatus(IPrepayOrderService.STATUS_2PASS + ""));
//        }});
        ret.add(new ArrayMap<String, Object>(){{
            put("status", IPrepayOrderService.STATUS_3PASS);
            put("title", "审核通过");
            put("count", prepayOrderService.countByStatus(IPrepayOrderService.STATUS_3PASS + ""));
        }});
        ret.add(new ArrayMap<String, Object>(){{
            put("status", IPrepayOrderService.STATUS_REFUSE);
            put("title", "审核拒绝");
            put("count", prepayOrderService.countByStatus(IPrepayOrderService.STATUS_REFUSE + ""));
        }});
        return R.data(ret);
    }

    //    /**
    //     * 详情
    //     */
    //    @GetMapping("/detail")
    //    @ApiOperationSupport(order = 1)
    //    @ApiOperation(value = "详情", notes = "传入prepayOrder")
    //    public R<PrepayOrderEntity> detail(PrepayOrderEntity prepayOrder) {
    //        PrepayOrderEntity detail = prepayOrderService.getOne(Condition.getQueryWrapper(prepayOrder));
    //        return R.data(detail);
    //    }

    /**
     * 列表
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入prepayOrder")
    public
    R<IPage<PrepayOrderVO>> list(PrepayOrderDTO prepayOrder, Query query){
        IPage<PrepayOrderVO> pages = prepayOrderService
                .getPage(prepayOrder, query);
        return R.data(pages);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/genprepayorder")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入prepayOrder")
    public
    R genPrepayorder(@Valid @RequestBody List<PrepayOrderItemEntity> preOrderItems){
        return R.status(prepayOrderService.genPrepayorder(preOrderItems));
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入prepayOrder")
    public
    R update(@Valid @RequestBody PrepayOrderUpdateDto prepayOrder){
        return R.status(prepayOrderService.bizUpdate(prepayOrder));
    }

    /**
     * 批量审核
     */
    @PostMapping("/checkbatch")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "审核", notes = "传入prepayOrder")
    public
    R checkBatch(@Valid @RequestBody List<CheckDTO> checkDtos){
        return R.status(prepayOrderService.checkBatch(checkDtos));
    }


    /**
     * 作废
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public
    R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids){
        return R.status(prepayOrderService.bizDelete(Func.toLongList(ids)));
    }



    /**
     * 打印数据
     */
    @GetMapping("/print")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "打印数据", notes = "")
    public R<Map<String, Object>> print(PrepayOrderDTO prepayOrder) {
        return R.data(prepayOrderService.print(prepayOrder));
    }


    /**
     * 修改打印状态
     */
    @PostMapping("/updateprint")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入prepayOrder")
    public
    R updatePrint(@Valid @RequestBody PrepayOrderEntity prepayOrder){
        return R.status(prepayOrderService.updateById(prepayOrder));
    }
}
