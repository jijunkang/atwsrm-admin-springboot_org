package org.springblade.modules.desk.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.api.R;
import org.springblade.modules.ap.service.IApBillService;
import org.springblade.modules.ap.service.IApRcvService;
import org.springblade.modules.bizinquiry.service.IBizInquiryService;
import org.springblade.modules.finance.service.IPrepayOrderService;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.ncr.service.INcrService;
import org.springblade.modules.outpr.service.IOutPrItemArtifactService;
import org.springblade.modules.outpr.service.IOutPrItemService;
import org.springblade.modules.outpr.service.IOutPrReportFormsService;
import org.springblade.modules.outpr.service.IOutSupPreOrderService;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.entity.PoRemindEntity;
import org.springblade.modules.po.service.*;
import org.springblade.modules.pr.service.IU9PrService;
import org.springblade.modules.priceframe.service.IPriceFrameService;
import org.springblade.modules.pricelib.service.IPriceLibService;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页
 *
 * @author zhuangqian
 */
@ApiIgnore
@RestController
@RequestMapping(AppConstant.APPLICATION_DESK_NAME)
@AllArgsConstructor
@Api(value = "首页", tags = "首页")
public
class DashBoardController{

    @Autowired
    IItemService     iItemService;
    @Autowired
    IPriceLibService priceLibService;
    @Autowired
    IU9PrService     prService;
    @Autowired
    IIoService       ioService;
    @Autowired
    IPoService       poService;
    @Autowired
    IPoItemService   poItemService;
    @Autowired
    IPoOffsetViewService poOffsetViewService;
    @Autowired
    IPoRemindService       poRemindService;
    @Autowired
    IOutSupPreOrderService outSupPreOrderService;
    @Autowired
    IU9PrService u9PrService;
    @Autowired
    ISupplierService iSupplierService;
    @Autowired
    IPriceFrameService priceFrameService;
    @Autowired
    IPrepayOrderService prepayOrderService;
    @Autowired
    IApBillService apBillService;
    @Autowired
    IApRcvService apRcvService;
    @Autowired
    IOutPrItemArtifactService artifactService;
    @Autowired
    IOutPrItemService outPrItemService;
    @Autowired
    IBizInquiryService bizInquiryService;
    @Autowired
    INcrService ncrService;
    @Autowired
    IOutPrReportFormsService iOutPrReportFormsService;
    /**
     * @return
     * 2020年5月15日 15:10:30 弃用 旧中台接口
     */
    @GetMapping("/dashboard/taskstatistics")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "中台数据", notes = "")
    public
    R taskStatistics(){

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object>       map1 = new HashMap<>(5);
        map1.put("title", "未维护的物料数量");
        map1.put("count", iItemService.incompleteCount() );
        list.add(map1);

        list.add(new HashMap<String, Object>(){{
            put("title", "呆滞物料数量");
            put("count", iItemService.dullCount());
        }});
        list.add(new HashMap<String, Object>(){{
            put("title", "待处理询价数");
            put("count",prService.toProcessCount());
        }});
        list.add(new HashMap<String, Object>(){{
            put("title", "流标请购单行数");
            put("count", prService.flowCount());
        }});
        list.add(new HashMap<String, Object>(){{
            put("title", "待下单数量");
            put("count",poItemService.waitCount());
        }});
        list.add(new HashMap<String, Object>(){{
            put("title", "待处理请款单数量");
            put("count", 0);
        }});
        list.add(new HashMap<String, Object>(){{
            put("title", "待处理跟单数量");
            put("count", poOffsetViewService.toProcessCount());
        }});

        list.add(new HashMap<String, Object>(){{
            put("title", "待处理催单数量");
            put("count", poRemindService.count(Wrappers.<PoRemindEntity>query()
                    .eq("trace_code",SecureUtil.getUserAccount())
                    .eq("status",poRemindService.STATUS_PASS)));
            //			put("count", poService.count(poService.getTodoQueryWrapper(new PoEntity())));
        }});

        return R.data(list);
    }

    /**
     * @return
     *  desk_item
     * 	desk_sluggish_item
     * 	desk_pricelib_ckeck
     * 	desk_io_index
     * 	desk_io_result_entry
     * 	desk_po_tracelist
     * 	desk_io_result_review
     * 	desk_po_remindapplaylist
     * 	desk_po_remindcheck
     * 	desk_po_remindlist
     * 	desk_po_returnassign
     * 	desk_po_assign
     * 	desk_po_todo
     * 	desk_io_framepr
     * 	desk_io_mathmodelpr
     * 	desk_io_flowinputcheck
     * 	desk_io_wxdeliveryconfirmation
     * 	desk_io_standdeliveryconfirmation
     */
    @GetMapping("/dashboard/taskcount")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "任务数量", notes = "")
    public
    R taskCount() throws ParseException {
        Map<String, Object> map1 = new HashMap<>(20);
        map1.put("desk_io_index", prService.toProcessCount());  // 待处理询价请购单行数 ok
        map1.put("desk_io_result_entry", prService.flowCount());  //流标价格录入
        map1.put("po_reserve_count", prService.poReserveCount());  //预订单数量
        map1.put("desk_io_result_review", ioService.toCheckCount("quote"));  //询价结果审核数量
        map1.put("desk_io_flowinputcheck", ioService.toCheckCount("purch_submit"));  //流标录入审核数量
        // map1.put("desk_po_returnassign", 0 );  //订单退回指派面板
        // map1.put("desk_po_assign", iItemService.incompleteCount() );  //订单指派结果面板
        map1.put("desk_po_tracelist", iSupplierService.getTraceTabCount());  //交期跟踪面板 - 跟单
        map1.put("desk_item", iItemService.incompleteCount());  //未维护的物料面板
        map1.put("desk_sluggish_item", iItemService.dullCount());  //呆滞物料面板
        map1.put("desk_po_remindcheck", poRemindService.count(Wrappers.<PoRemindEntity>query().eq("status", poRemindService.STATUS_INIT)));  //申请催单审核
        map1.put("desk_po_remindlist", poRemindService.count(Wrappers.<PoRemindEntity>query()
                .eq("trace_code", SecureUtil.getUserAccount())
                .eq("status", poRemindService.STATUS_PASS)));  //催单面板
        map1.put("desk_pricelib_ckeck", priceLibService.toCheckCount());  //白名单审核
        map1.put("desk_io_standdeliveryconfirmation", ioService.toConfirmCount());  //标准白名单交期确认面板
        map1.put("desk_io_wxdeliveryconfirmation", outSupPreOrderService.toConfirmCount());  //委外白名单交期确认面板
        map1.put("desk_po_todo", poService.count(poService.getTodoQueryWrapper(new PoEntity())));  //待处理订单
        //        map1.put("desk_po_remindapplaylist", iItemService.incompleteCount());  //申请催单审核面板
        //        map1.put("desk_io_framepr", iItemService.incompleteCount());  //框架请购单行数面板
        //        map1.put("desk_io_mathmodelpr", iItemService.incompleteCount());  //数学模型请购单面板
        map1.put("desk_poitem_updatedate", poItemService.getDeliveryCount());  //待处理订单
        map1.put("desk_finance_acremind", poService.getRemindCount());  //预付请款提醒

        map1.put("desk_io_framepr", u9PrService.getPriceFrameCount());  //框架协议请款单
        map1.put("desk_priceframe_check", priceFrameService.auditCount());  //框架协议审核
        map1.put("desk_finance_acapplaycheck", prepayOrderService.getListCount()); //预付申请审核
        map1.put("desk_finance_apapplay", apBillService.getListCount()); //应付申请
        map1.put("desk_finance_doreconciliation", apRcvService.getListCount()); //到货对账
        map1.put("desk_finance_deductorder", apRcvService.getKKListCount()); //扣款单
        map1.put("desk_ncr_report", ncrService.getCount()); //NCR对外单
        map1.put("desk_outpr_index", outPrItemService.inquiryCountOfWWForZT()); //委外询价单
        map1.put("desk_outpr_pritemflow", outPrItemService.getCount()); //委外流标
        map1.put("desk_outpr_preoutorder", outSupPreOrderService.getCount()); //委外预定单
        map1.put("desk_outpr_pritemartifact", artifactService.getCount()); //委外转人工

        map1.put("desk_io_bizInquiry", bizInquiryService.getCount()); //商务询价
        map1.put("desk_po_price_audit", poService.getPriceAuditCount());//订单金额审核

        map1.put("desk_io_index_others", prService.toProcessCountOfOthers());  // 小零件待处理询价请购单行数 ok
        map1.put("desk_io_result_entry_others", prService.flowCountOfOthers());  //小零件 流标价格录入

        map1.put("desk_outpr_do_list", iOutPrReportFormsService.getDoTabCount()); //虚拟入库

        return R.data(map1);
    }

}
