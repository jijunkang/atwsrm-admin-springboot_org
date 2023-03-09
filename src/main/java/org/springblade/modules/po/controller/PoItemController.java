package org.springblade.modules.po.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.service.IOutPrItemService;
import org.springblade.modules.po.dto.PoItemDTO;
import org.springblade.modules.po.dto.PoItemNodeReq;
import org.springblade.modules.po.dto.PoUpDateReq;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.mapper.PoItemMapper;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.po.vo.PoItemNewReportVO;
import org.springblade.modules.po.vo.PoItemReqRepotVO;
import org.springblade.modules.po.vo.PoItemVO;
import org.springblade.modules.pr.entity.U9PrEntity;
import org.springblade.modules.pr.service.IU9PrService;
import org.springblade.modules.queue.entity.QueueEmailEntity;
import org.springblade.modules.queue.service.IQueueEmailService;
import org.springblade.modules.supplier.entity.CaiGouSchedule;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 采购订单明细 控制器
 *
 * @author Will
 */

@RestController
@AllArgsConstructor
@RequestMapping("/blade-poitem/po_item")
@Api(value = "采购订单明细", tags = "采购订单明细")
public class PoItemController extends BladeController {

    private IPoItemService poItemService;

    private IU9PrService prService;

    IOutPrItemService outPrItemService;

    private PoItemMapper poItemMapper;

    private ISupplierService iSupplierService;
    @Autowired
    private IQueueEmailService queueEmailService;


    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入po_item")
    public R<PoItemEntity> detail(PoItemEntity po_item) {
        PoItemEntity detail = poItemService.getOne(Condition.getQueryWrapper(po_item));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public R<IPage<PoItemVO>> page(PoItemDTO poItemEntity, Query query) {
        IPage<PoItemVO> pages = poItemService.pageWithItemPoContract(query,poItemEntity);

//        for (PoItemEntity entity : pages.getRecords()) {
//            U9PrEntity pr = prService.getById(entity.getPrId());
//            if (StringUtil.isEmpty(entity.getEndUser()) && !StringUtil.isEmpty(pr) && StringUtil.isNotBlank(pr.getEndUser())) {
//                entity.setEndUser(pr.getEndUser());
//            }
//        }
        return R.data(pages);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/pagewithpr")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public R<IPage<PoItemVO>> pageWithPr(PoItemDTO poItemEntity, Query query) {
        IPage<PoItemVO> pages = poItemService.pageWithPr(query, poItemEntity);
//        for (PoItemVO entity : pages.getRecords()) {
//            U9PrEntity pr = prService.getById(entity.getPrId());
//            entity.setEndUser(pr == null ? "" : pr.getEndUser());
//
//            String prprCode = entity.getPrprCode();
//            if(prprCode == null){
//                // 去atw_out_pr_item中查找
//                OutPrItemEntity outPrItemEntity = outPrItemService.getByPrcodeAndItemcode(entity.getPrCode(),entity.getItemCode());
//                if(outPrItemEntity != null){
//                    entity.setAvailableQuantity(outPrItemEntity.getAvailableQuantity());
//                    entity.setProjectOccupancyNum(outPrItemEntity.getProjectOccupancyNum());
//                    entity.setRequisitionRemark(outPrItemEntity.getRequisitionRemark());
//                }
//            }
//        }
        return R.data(pages);
    }

    /**
     * 交付中心
     */
    @GetMapping("/deliverypage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public R<IPage<PoItemVO>> deliveryPage(PoItemDTO poItem, Query query) {
        IPage<PoItemVO> pages = poItemService.getDeliveryPage(Condition.getPage(query), poItem);
        return R.data(pages);
    }


    /**
     * 交付中心-修改交期审批中台
     */
    @GetMapping("/deliverycenter")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public R<IPage<PoItemVO>> deliveryCenter(PoItemDTO poItem, Query query) {
        poItem.setTraceCode(getUser().getAccount());
        IPage<PoItemVO> pages = poItemService.getDeliveryPage(Condition.getPage(query), poItem);
        return R.data(pages);
    }

    /**
     * 交付中心-修改交期
     *
     * @return R
     */
    @PostMapping("/submitupdate")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "poUpDateReq")
    public R submitUpdateDate(@Valid @RequestBody PoUpDateReq poUpDateReq) throws Exception {
        return R.status(poItemService.submitUpdateDate(poUpDateReq));
    }


    /**
     * 交付中心 导出 maily
     */
    @GetMapping("/deliveryexport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public void deliveryExport(PoItemDTO poItem, HttpServletResponse response) {
        poItemService.getDeliveryExport(poItem, response);
    }

    /**
     * 交付中心 导入修改交期
     */
    @PostMapping("/importupdatedate")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导入修改交期", notes = "导入修改交期")
    public R importUpdateDate(@Valid @RequestParam MultipartFile file) {
        try {
            return R.status(poItemService.importUpdateDate(file));
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("读取excel文件失败");
        }
    }

    /**
     * 采购报表
     */
    @GetMapping("/report")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public R<IPage<PoItemVO>> report(PoItemDTO po_item, Query query) {
        IPage<PoItemVO> pages = poItemService.reportPage(query, po_item);
        return R.data(pages);
    }

    /**
     * VMI监控采购报表
     */
    @GetMapping("/vmireport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public R<IPage<PoItemVO>> vmireport(PoItemDTO po_item, Query query) {
        IPage<PoItemVO> pages = poItemService.vmiReportPage(query, po_item);
        return R.data(pages);
    }

    /**
     * 采购报表 导出
     */
    @GetMapping("/reportExport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public void reportExport(PoItemDTO poItem, HttpServletResponse response) {
        poItemService.getReportExport(poItem, response);
    }


    @GetMapping("/vmiReportExport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public void vmiReportExport(PoItemDTO poItem, HttpServletResponse response) {
        poItemService.getVMIReportExport(poItem, response);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入po_item")
    public R save(@Valid @RequestBody PoItemEntity po_item) {
        return R.status(poItemService.save(po_item));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入po_item")
    public R update(@Valid @RequestBody PoItemEntity po_item) {
        return R.status(poItemService.updateById(po_item));
    }

    /**
     * 新增或修改 代码自定义代号
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入po_item")
    public R submit(@Valid @RequestBody PoItemEntity po_item) {
        return R.status(poItemService.saveOrUpdate(po_item));
    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(poItemService.deleteLogic(Func.toLongList(ids)));
    }

    /**
     * 供应计划排程
     *
     * @param query        Query
     * @param poItemEntity PoItemEntity
     * @return R
     */
    @GetMapping("/reqreport")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "来料需求报表分页", notes = "poItemReqRepot")
    public R<IPage<PoItemReqRepotVO>> reqRepotPage(Query query, PoItemEntity poItemEntity) {
        return R.data(poItemService.getReqRepotPage(query, poItemEntity));
    }

    /**
     * 供应计划排程导出
     *
     * @param query        Query
     * @param poItemEntity PoItemEntity
     * @param response     HttpServletResponse
     */
    @GetMapping("/poItemExport")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "供应计划排程导出", notes = "poItemExport")
    public void poItemExport(Query query, PoItemEntity poItemEntity, HttpServletResponse response) {
        poItemService.poItemExport(poItemEntity, response);
    }

    /**
     * 供应计划排程下载
     *
     * @param response HttpServletResponse
     */
    @GetMapping("/poItemdownload")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "供应计划排程下载", notes = "poItemExport")
    public R poItemDownload(HttpServletResponse response) {
        return R.status(poItemService.poItemDownload(response));
    }

    /**
     * 供应计划排成写入缓存
     */
    @GetMapping("/putcache")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public void putCache() {
        poItemService.putPoItemTask();
    }


    /**
     * 工艺卡控
     */
    @GetMapping("/craftctrlpage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public R<IPage<PoItemVO>> craftCtrlPage(PoItemDTO poItem, Query query) {
        IPage<PoItemVO> pages = poItemService.getCraftCtrlPage(Condition.getPage(query), poItem);
        return R.data(pages);
    }

    /**
     * 批量保存卡控进度
     */
    @PostMapping("/savescraftctrl")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入poItemNodeReqs")
    public R savesCraftCtrl(@Valid @RequestBody List<PoItemNodeReq> poItemNodeReqs) {
        return R.status(poItemService.savesCraftCtrl(poItemNodeReqs));
    }

    /**
     * 维护卡控节点
     */
    @PostMapping("/updatecraftctrl")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入poItemNodeReq")
    public R updateCraftCtrl(@Valid @RequestBody PoItemNodeReq poItemNodeReq) {
        return R.status(poItemService.updateCraftCtrl(poItemNodeReq));
    }

    /**
     * 导出
     */
    @GetMapping("/craftctrlexport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入craftCtrlNodeEntity")
    public void craftCtrlExport(PoItemDTO poItem,  HttpServletResponse response) throws Exception {
        poItemService.craftCtrlExport(poItem,  response);
    }

    /**
     * 批量查询承诺交期
     */
    @PostMapping("/updatePromiseDateBatchFromShjh")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "批量修改要求交期", notes = "传入poItemDTOS")
    public R updatePromiseDateBatchFromShjh(@Valid @RequestBody List<PoItemDTO> poItemDTOS){
        return R.status( poItemService.updatePromiseDateBatchFromShjh(poItemDTOS));
    }


    /**
     * 批量修改要求交期
     */
    @PostMapping("/updatereqdatebatch")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "批量修改要求交期", notes = "传入poItemDTOS")
    public R updateReqDateBatch(@Valid @RequestBody List<PoItemDTO> poItemDTOS){
        return R.status(poItemService.updateReqDateBatch(poItemDTOS));
    }


    /**
     * 批量修改承诺交期
     */
    @PostMapping("/updatePromiseDateBatch")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "批量修改承诺交期", notes = "传入poItemDTOS")
    public R updatePromiseDateBatch(@Valid @RequestBody List<PoItemDTO> poItemDTOS){
        return R.status(poItemService.updatePromiseDateBatch(poItemDTOS));
    }

    /**
     * 批量修改承诺交期同步到U9
     */
    @PostMapping("/updatePromiseDateBatchToU9")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "批量修改承诺交期", notes = "传入poItemDTOS")
    public R updatePromiseDateBatchToU9(@Valid @RequestBody List<PoItemDTO> poItemDTOS){
        return R.status(poItemService.updatePromiseDateBatchToU9(poItemDTOS));
    }


    /**
     * 批量修改 是否紧急
     */
    @PostMapping("/batchUpdateIsUrgent")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "批量修改是否紧急", notes = "传入poItemDTOS")
    public R batchUpdateIsUrgent(@Valid @RequestBody List<PoItemDTO> poItemDTOS){
        return R.status(poItemService.batchUpdateIsUrgent(poItemDTOS));
    }


    /**
     * 修改订单金额-提交
     */
    @PostMapping("/submitprice")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "批量修改要求交期", notes = "传入poItemDTOS")
    public R submitPrice(@Valid @RequestBody List<PoItemEntity> poItemEntityList){
        return R.status(poItemService.submitPrice(poItemEntityList));
    }


    /**
     * 批量修改交货日期、数量
     *
     * @param poItemEntityList List
     * @return R
     */
    @PostMapping("/updatedeldatebatch")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "poItemEntityList")
    public R updateDelDateBatch(@Valid @RequestBody List<PoItemEntity> poItemEntityList){
        return R.status(poItemService.updateDelDateBatch(poItemEntityList));
    }


    /**
     * 供应计划排程（新）
     *
     * @param query        Query
     * @param poItemEntity PoItemEntity
     * @return R
     */
    @GetMapping("/planreqreport")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "供应计划排程（新）", notes = "poItemReqRepot")
    public R<IPage<PoItemNewReportVO>> newReportPage(Query query, PoItemEntity poItemEntity) {
        return R.data(poItemService.newReportPage(query, poItemEntity));
    }

    /**
     * 供应计划排程2（新） (按项目号索引)
     *
     * @param query        Query
     * @param poItemEntity PoItemEntity
     * @return R
     */
    @GetMapping("/planreqreport2")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "供应计划排程（新）", notes = "poItemReqRepot")
    public R<IPage<PoItemNewReportVO>> newReportPage2(Query query, PoItemEntity poItemEntity) {
        return R.data(poItemService.newReportPage2(query, poItemEntity));
    }

    /**
     * 供应计划排程导出（新）
     *
     * @param poItemEntity PoItemEntity
     * @param response     HttpServletResponse
     */
    @GetMapping("/planreqreportexport")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "供应计划排程（新）", notes = "poItemEntity")
    public void newPoItemExport(PoItemEntity poItemEntity, HttpServletResponse response) {
        poItemService.newPoItemExport(poItemEntity, response);
    }



    /**
     * 供应计划排程导出2（新）
     *
     * @param poItemEntity PoItemEntity
     * @param response     HttpServletResponse
     */
    @GetMapping("/planreqreportexport2")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "供应计划排程2（新）", notes = "poItemEntity")
    public void newPoItemExport2(PoItemEntity poItemEntity, HttpServletResponse response) {
        poItemService.newPoItemExport2(poItemEntity, response);
    }

    /**
     * 供应计划排程2供应计划排程一键发送邮件
     *
     * @param poCodeAndLnAndSupCodes        List<String>
     */
    @PostMapping("/planreqreportsendEmail")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "供应计划排程2一键发送邮件", notes = "poItemReqRepot")
    public R planreqreportsendEmail(@Valid @RequestBody List<String> poCodeAndLnAndSupCodes) {
        return R.status(poItemService.planreqreportsendEmail(poCodeAndLnAndSupCodes));
    }

    /**
     * 供应计划排程下载（新）
     *
     * @param response HttpServletResponse
     */
    @GetMapping("/planreqreportdownload")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "供应计划排程下载", notes = "poItemExport")
    public R newReportDownload(HttpServletResponse response) {
        return R.status(poItemService.newReportDownload(response));
    }


    /**
     * 供应计划排成写入缓存
     */
    @GetMapping("/planreqputcache")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public void newPutCache() {
        poItemService.newPutPoItemTask();
    }

    /**
     * 查看历史订单价格
     */
    @GetMapping("/historyPrice")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "查看历史订单价格", notes = "传入item_code")
    public R<IPage<PoItemVO>>  historyPrice(String itemCode, Query query) {
        IPage<PoItemVO> pages = poItemService.getHistoryPrice(Condition.getPage(query),itemCode);
        return R.data(pages);
    }


    /**
     * 委外请购单生成PO后，发邮件提醒扣账
     * @param ids
     * @return
     */
    @GetMapping("/sendEmailToCaiWu")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "委外请购单生成PO后，发邮件提醒扣账", notes = "")
    public R sendEmailToCaiWu(String ids) {
        return R.status(poItemService.sendEmail(ids));
    }


    /**
     * 紧急请购单生成PO后，自动设置 updateCheckDate
     * @param ids
     * @return
     */
    @GetMapping("/batchSetUpdateCheckDate")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "委外请购单生成PO后，发邮件提醒扣账", notes = "")
    public R batchSetUpdateCheckDate(String ids) {
        return R.status(poItemService.batchSetUpdateCheckDate(ids));
    }


    /**
     * job 更新预PO
     */
    @GetMapping("/updateReseverPo")
    @Scheduled(cron="0 0 8 * * ?")
    public void updateReseverPo(){
        //遍历所有预PO，判断PR，PO有没有被锁定
        List<PoItemDTO> poItemDTOS =new ArrayList<>();

        List<PoItemEntity> poItemEntities = poItemMapper.selectPoItemLock();
        for (PoItemEntity poItemEntity:poItemEntities) {
            CaiGouSchedule locked=null;
            locked = poItemMapper.isLocked(poItemEntity.getPoCode(), String.valueOf(poItemEntity.getPoLn()));

            if (locked==null){
                locked = poItemMapper.isLocked(poItemEntity.getPrCode(), String.valueOf(poItemEntity.getPrLn()));
            }

            if(locked!=null){
                Date PlanDate = locked.getPlanDate();
                PlanDate=cn.hutool.core.date.DateUtil.offsetDay(PlanDate,-25);
                long PlanDate_sjc = PlanDate.getTime()/1000;
                if(poItemEntity.getPoCode()!=null){
                    poItemMapper.updateSupConfirmDateJOB(PlanDate_sjc,poItemEntity.getPoCode(), String.valueOf(poItemEntity.getPoLn()));
                    //调用接口同步承诺交期到U9
                    PoItemDTO poItemDTO = new PoItemDTO();
                    BeanUtil.copy(poItemEntity,poItemDTO);
                    poItemDTO.setSupConfirmDate(PlanDate_sjc);
                    poItemDTOS.add(poItemDTO);
                }


            }
        }

        poItemService.updatePromiseDateBatchToU9(poItemDTOS);
    }


}
