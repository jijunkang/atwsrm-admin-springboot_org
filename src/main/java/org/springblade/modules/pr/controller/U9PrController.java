package org.springblade.modules.pr.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.mathmodel.entity.CastingOrderEntity;
import org.springblade.modules.mathmodel.entity.MailyMaterialTotalEntity;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.entity.OutPrItemProcessEntity;
import org.springblade.modules.outpr.service.IOutPrItemProcessService;
import org.springblade.modules.outpr.service.IOutPrItemService;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.mapper.PoItemMapper;
import org.springblade.modules.pr.dto.*;
import org.springblade.modules.pr.entity.*;
import org.springblade.modules.pr.service.IU9PrService;
import org.springblade.modules.pr.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 请购单 控制器
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-pr/u9_pr")
@Api(value = "请购单", tags = "请购单")
public
class U9PrController extends BladeController{

    private IU9PrService prService;

    private IOutPrItemService iOutPrItemService;

    private IOutPrItemProcessService outPrItemProcessService;

    @Autowired
    PoItemMapper poItemMapper;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<U9PrDTO> detail(U9PrEntity u9_pr){
        U9PrDTO dto = prService.getDtoById(u9_pr.getId());
        return R.data(dto);
    }


    /**
     * 铸锻件信息详情
     */
    @PostMapping("/itemInfoOfZDJ")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<List<ItemInfoOfZDJVO>> itemInfoOfCast(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        List<ItemInfoOfZDJVO> itemInfoOfCastVOS = prService.getItemInfoOfZDJVO(submitPriceReq.getItemCode(),submitPriceReq.getItemName());
        return R.data(itemInfoOfCastVOS);
    }

    /**
     * 锻件信息详情 maily
     */
    @PostMapping("/itemInfoOfDJ")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<List<ItemInfoOfZDJVO>> itemInfoOfdj(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        List<ItemInfoOfZDJVO> itemInfoOfCastVOS = prService.getItemInfoOfDJVO(submitPriceReq.getItemCode(),submitPriceReq.getItemName());
        return R.data(itemInfoOfCastVOS);
    }

    /**
     * 管棒料信息详情 昕月
     */
    @PostMapping("itemInfoGBL")
    @ApiOperation(value = "管棒料详情",notes = "传入u9_pr")
    public R<List<MaterialMaliyVO>> itemInfoGBL(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        List<MaterialMaliyVO> maliyVOList = prService.getitemInfoGBL(submitPriceReq.getItemCode(),submitPriceReq.getItemName());
        return R.data(maliyVOList);
    }

    /**
     * 锻件信息详情 - 报表
     */
    @GetMapping("/itemInfoOfDJReport")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<IPage<ItemInfoEntityDJReport>> itemInfoOfdjReport(SubmitPriceReq submitPriceReq, Query query){
        IPage<ItemInfoEntityDJReport> itemInfoOfCastVOS = prService.getItemInfoOfDJVOReport(Condition.getPage(query),submitPriceReq);
        return R.data(itemInfoOfCastVOS);
    }

    /**
     * 小零件信息详情 - 报表
     */
    @GetMapping("/itemInfoOfXLJReport")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<IPage<ItemInfoEntityOfXLJ>> itemInfoOfXLJReport(SubmitPriceReq submitPriceReq, Query query){
        IPage<ItemInfoEntityOfXLJ> itemInfoOfCastVOS = prService.getItemInfoOfXLJVOReport(Condition.getPage(query),submitPriceReq);
        return R.data(itemInfoOfCastVOS);
    }

    /**
     * 小零件信息详情 - 报表
     */
    @GetMapping("/itemInfoOfLZQReport")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<IPage<ItemInfoEntityOfLZQ>> itemInfoOfLZQReport(SubmitPriceReq submitPriceReq, Query query){
        IPage<ItemInfoEntityOfLZQ> itemInfoOfCastVOS = prService.getItemInfoOfLZQVOReport(Condition.getPage(query),submitPriceReq);
        return R.data(itemInfoOfCastVOS);
    }

    /**
     * 小零件信息详情 - 报表
     */
    @GetMapping("/itemInfoOfQZReport")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<IPage<ItemInfoEntityOfQZNew>> itemInfoOfQZReport(SubmitPriceReq submitPriceReq, Query query){
        IPage<ItemInfoEntityOfQZNew> itemInfoOfCastVOS = prService.getItemInfoOfQZReport(Condition.getPage(query),submitPriceReq);
        return R.data(itemInfoOfCastVOS);
    }

    /**
     * 小零件信息详情 - 报表
     */
    @GetMapping("/itemInfoOfDZReport")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<IPage<ItemInfoEntityOfDZ>> itemInfoOfDZReport(SubmitPriceReq submitPriceReq, Query query){
        IPage<ItemInfoEntityOfDZ> itemInfoOfCastVOS = prService.getItemInfoOfDZVOReport(Condition.getPage(query),submitPriceReq);
        return R.data(itemInfoOfCastVOS);
    }


    /**
     * 法兰 - 报表
     */
    @GetMapping("/itemInfoOfFLReport")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<IPage<ItemInfoEntityOfFL>> itemInfoOfFLReport(SubmitPriceReq submitPriceReq, Query query){
        IPage<ItemInfoEntityOfFL> itemInfoOfCastVOS = prService.getItemInfoOfFLVOReport(Condition.getPage(query),submitPriceReq);
        return R.data(itemInfoOfCastVOS);
    }

    /**
     * 球座信息详情
     */
    @PostMapping("/itemInfoOfQZ")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<List<ItemInfoOfQZVO>> itemInfoOfQZ(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        List<ItemInfoOfQZVO> itemInfoOfQZVOS = prService.getItemInfoOfQZVO(submitPriceReq.getItemCode(),submitPriceReq.getItemName());
        return R.data(itemInfoOfQZVOS);
    }

    /**
     * 球座信息详情
     */
    @PostMapping("/itemInfoOfQZNew")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<List<ItemInfoEntityOfQZNew>> itemInfoOfQZNew(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        List<ItemInfoEntityOfQZNew> itemInfoOfQZVOS = prService.getItemInfoOfQZNew(submitPriceReq);
        return R.data(itemInfoOfQZVOS);
    }

    /**
     * 全程委外信息详情
     */
    @PostMapping("/itemInfoOfWW")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<List<ItemInfoOfZDJVO>> itemInfoOfWW(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        List<ItemInfoOfZDJVO> itemInfoOfQZVOS = prService.getItemInfoOfWWVO(submitPriceReq.getItemCode(),submitPriceReq.getItemName());
        return R.data(itemInfoOfQZVOS);
    }

    /**
     * 小零件信息详情
     */
    @PostMapping("/itemInfoOfXLJ")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<List<ItemInfoEntityOfXLJ>> itemInfoOfXLJ(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        List<ItemInfoEntityOfXLJ> itemInfoOfXLJVOS = prService.getItemInfoOfXLJVO(submitPriceReq.getItemCode(),submitPriceReq.getItemName());
        return R.data(itemInfoOfXLJVOS);
    }

    /**
     * 底轴信息详情
     */
    @PostMapping("/itemInfoOfDZ")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<List<ItemInfoEntityOfDZ>> itemInfoOfDZ(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        List<ItemInfoEntityOfDZ> itemInfoOfDZVOS = prService.getItemInfoOfDZVO(submitPriceReq.getItemCode(),submitPriceReq.getItemName());
        return R.data(itemInfoOfDZVOS);
    }

    /**
     * 底轴信息详情
     */
    @PostMapping("/itemInfoOfFL")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<List<ItemInfoEntityOfFL>> itemInfoOfFL(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        List<ItemInfoEntityOfFL> itemInfoOfFLVOS = prService.getItemInfoOfFLVO(submitPriceReq.getItemCode(),submitPriceReq.getItemName());
        return R.data(itemInfoOfFLVOS);
    }

    /**
     * 联轴器信息详情
     */
    @PostMapping("/itemInfoOfLZQ")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<List<ItemInfoEntityOfLZQ>> itemInfoOfLZQ(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        List<ItemInfoEntityOfLZQ> itemInfoOfFLVOS = prService.getItemInfoOfLZQVO(submitPriceReq.getItemCode(),submitPriceReq.getItemName());
        return R.data(itemInfoOfFLVOS);
    }

    /**
     * 锻件基础信息详情  昕月
     */
    @PostMapping("/basicItemInfoOfDJ")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<T> basicItemInfoOfDJ(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.data(prService.getBasicItemInfoOfDJ(submitPriceReq.getItemCode(),submitPriceReq.getItemName()));
    }

    /**
     * 小零件基础信息详情
     */
    @PostMapping("/basicItemInfoOfXLJ")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<T> basicItemInfoOfXLJ(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.data(prService.getBasicItemInfoOfXLJ(submitPriceReq.getItemCode(),submitPriceReq.getItemName()));
    }

    /**
     * 小零件基础信息详情 - rx
     */
    @PostMapping("/basicItemInfoOfXLJRX")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<T> basicItemInfoOfXLJRX(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.data(prService.getBasicItemInfoOfXLJRX(submitPriceReq.getItemCode(),submitPriceReq.getItemName()));
    }


    /**
     * 底轴基础信息详情 - 内外径、喷涂内外径
     *
     */
    @PostMapping("/basicItemInfoOfDZ")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情")
    public
    R<T> basicItemInfoOfDZ(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.data(prService.getBasicItemInfoOfDZ(submitPriceReq.getItemCode(),submitPriceReq.getItemName()));
    }





    @PostMapping("/setBasicItemInfoOfDJ")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "维护锻件基础信息", notes = "submitPrice")
    public
    R setBasicItemInfoOfDJ(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.setBasicItemInfoOfDJ(submitPriceReq));
    }


    @PostMapping("/setBasicItemInfoOfXLJ")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "维护小零件基础信息", notes = "submitPrice")
    public
    R setBasicItemInfoOfXLJ(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.setBasicItemInfoOfXLJ(submitPriceReq));
    }

    @PostMapping("/setBasicItemInfoOfXLJRX")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "维护小零件RX基础信息", notes = "submitPrice")
    public
    R setBasicItemInfoOfXLJRX(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.setBasicItemInfoOfXLJRX(submitPriceReq));
    }

    @PostMapping("/setBasicItemInfoOfDZ")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "维护底轴基础信息")
    public
    R setBasicItemInfoOfDZ(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.setBasicItemInfoOfDZ(submitPriceReq));
    }

    @PostMapping("/addBasicItemInfoOfDJ")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "维护锻件基础信息", notes = "submitPrice")
    public
    R addBasicItemInfoOfDJ(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.addBasicItemInfoOfDJ(submitPriceReq));
    }

    @PostMapping("/addBasicItemInfoOfXLJ")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "维护小零件基础信息", notes = "submitPrice")
    public
    R addBasicItemInfoOfXLJ(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.addBasicItemInfoOfXLJ(submitPriceReq));
    }

    /**
     * 需求池列表
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入u9_pr")
    public
    R<IPage<U9PrVO>> list(PrReq prReq, Query query){
        QueryWrapper<U9PrEntity> queryWrapper = prService.getQueryWrapper(prReq);//.ne("status", IU9PrService.STATUS_INIT);

        QueryWrapper<OutPrItemEntity> queryWrapperOfOut = iOutPrItemService.getQueryWrapper(BeanUtil.copy(prReq,OutPrItemEntity.class));//.ne("status", IU9PrService.STATUS_INIT);
        if(StringUtils.isNotBlank(prReq.getCreateTimeStart())){
            queryWrapper.ge("create_time", prReq.getCreateTimeStart());
            queryWrapperOfOut.ge("create_time", prReq.getCreateTimeStart());
        }
        if(StringUtils.isNotBlank(prReq.getCreateTimeEnd())){
            queryWrapper.le("create_time", prReq.getCreateTimeEnd());
            queryWrapperOfOut.ge("create_time", prReq.getCreateTimeStart());
        }
        if(StringUtils.isNotBlank(prReq.getStatuss())){
            queryWrapper.in("status", prReq.getStatusList());
            queryWrapperOfOut.ge("create_time", prReq.getCreateTimeStart());
        }

        IPage<U9PrEntity> pages = prService.page(Condition.getPage(query), queryWrapper);
        long firstPageTotal = pages.getTotal();
        long secondPageTotal = 0;

        // 若在U9里面查不到，则可能是外协
        if(pages.getRecords().size()==0){
            List<U9PrEntity> u9PrEntityList = new ArrayList<>();
            List<OutPrItemEntity> pagesOfOut = iOutPrItemService.list(queryWrapperOfOut);
            long total = 0;
            for (OutPrItemEntity outPr : pagesOfOut) {
                IPage<OutPrItemProcessEntity> processList = outPrItemProcessService.getPageByItemId(query,outPr.getId());
                secondPageTotal = processList.getTotal();
                for (OutPrItemProcessEntity outPrItemProcessEntity : processList.getRecords()) {
                    U9PrEntity u9PrEntity = BeanUtil.copy(outPr,U9PrEntity.class);
                    u9PrEntity.setPrLn(outPrItemProcessEntity.getPrLn());
                    u9PrEntity.setItemCode(outPr.getItemCode()+"-"+ outPrItemProcessEntity.getProcessCode());
                    u9PrEntity.setItemName(outPr.getItemName()+"-"+ outPrItemProcessEntity.getProcessName());
                    u9PrEntity.setPriceNum(outPr.getPriceNum());
                    u9PrEntity.setMoNo(outPrItemProcessEntity.getMoNo());
                    u9PrEntity.setPriceUom(outPr.getPriceUom());
                    u9PrEntity.setTcNum(outPr.getPriceNum());
                    u9PrEntity.setTcUom(outPr.getPriceUom());
                    u9PrEntity.setFlowType(outPr.getFlowCause());
                    u9PrEntityList.add(u9PrEntity);
                }
                total = processList.getTotal();
            }
            pages.setRecords(u9PrEntityList);
            pages.setTotal(total);
        } else {
            for(U9PrEntity prEntity:pages.getRecords()){
                // 项目号如果是null或者是空，取 proNo
                if(prEntity.getProNo() ==null || prEntity.getProNo().isEmpty()){
                    prEntity.setProNo(prEntity.getApsProNo());
                }
            }
        }
        List<U9PrVO> u9PrVOList = new ArrayList<>();

        pages.getRecords().stream().forEach(u9PrEntity -> {
            U9PrVO u9PrVO = BeanUtil.copy(u9PrEntity,U9PrVO.class);
            String codeType = poItemMapper.getABCType(u9PrEntity.getItemCode());
            u9PrVO.setCodeType(codeType);
            u9PrVOList.add(u9PrVO);
        });


        IPage<U9PrVO> pagesToReturn = new Page<>(pages.getCurrent(), pages.getSize());
        pagesToReturn.setTotal(firstPageTotal+secondPageTotal);
        pagesToReturn.setRecords(u9PrVOList);
        pagesToReturn.setCurrent(pages.getCurrent());

        return R.data(pagesToReturn);
    }

    /**
     * 需求池 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po_item")
    public
    void deliveryExport(PrReq prReq, HttpServletResponse response){
        prService.export(prReq, response);
    }


    /**
     * 需要人工选择供应商报价的PR
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入u9_pr")
    public
    R<IPage<U9PrDTO>> getPage(PrReq prReq, Query query){
        prReq.setPurchCode(getUser().getAccount());
        IPage<U9PrDTO> pages = prService.selectPage(Condition.getPage(query), prReq);
        return R.data(pages);
    }


    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入u9_pr")
    public
    R save(@Valid @RequestBody U9PrEntity u9_pr){
        return R.status(prService.save(u9_pr));
    }

    /**
     * 批量修改
     */
    @PostMapping("/updatebatch")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入u9_pr")
    public
    R updateBatch(@Valid @RequestBody List<U9PrEntity> prList){
        return R.status(prService.updateBatch(prList));
    }

    //    /**
    //     * 新增或修改 代码自定义代号
    //     */
    //    @PostMapping("/submit")
    //    @ApiOperationSupport(order = 6)
    //    @ApiOperation(value = "新增或修改", notes = "传入u9_pr")
    //    public R submit(@Valid @RequestBody U9PrEntity u9_pr) {
    //        return R.status(prService.saveOrUpdate(u9_pr));
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
    //        return R.status(prService.deleteLogic(Func.toLongList(ids)));
    //    }

    /**
     * 流标的 采购员录入价格
     */
    @PostMapping("/submitPrice")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "流标录入价格", notes = "submitPrice")
    public
    R submitPrice(@Valid @RequestBody SubmitPriceDTO submitPrice){
        return R.status(prService.submitPrice(submitPrice));
    }

    /**
     * 流标 批量录入价格  流标审核提交下单
     */
    @PostMapping("/submitbatch")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "流标录入价格", notes = "submitPrice")
    public
    R submitBatch(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.submitBatch(submitPriceReq));
    }


    @PostMapping("/letprflow")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "流标录入价格", notes = "submitPrice")
    public
    R letPrFlow(@Valid @RequestBody U9PrEntity pr){
        return R.status(prService.statusToFlow(pr.getId()));
    }


    /**
     * 选择挂起询价方式列表
     * @param u9pr U9PrEntity
     * @return
     */
    @GetMapping("/getPriceLib")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "选择挂起询价方式列表", notes = "传入u9_pr")
    public
    R<IPage<Map<String, Object>>> getPriceLib(U9PrEntity u9pr, Query query){
        IPage<Map<String, Object>> pages = prService.getPriceLib(Condition.getPage(query), u9pr);
        return R.data(pages);
    }


    /**
     * 挂起询价下单
     * @param u9PrHangDTO U9PrHangDTO
     * @return
     */
    @PostMapping("/createbyhang")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "挂起询价下单", notes = "传入u9PrHangDTO")
    public
    R createByHang(@Valid @RequestBody U9PrHangDTO u9PrHangDTO){
        return R.status(prService.createByHang(u9PrHangDTO));
    }

    /**
     * 询价单中台统计
     *
     * @return
     */
    @GetMapping("/inquirycount")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "", notes = "")
    public R<List<Map<String, Object>>> getInquiryCount(){
        return R.data(prService.getInquiryCount(IU9PrService.PURCHASE_TYPE_NORMAL));
    }

    /**
     * 询价单中台统计-小零件
     *
     * @return
     */
    @GetMapping("/inquirycountOfOthers")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "", notes = "")
    public R<List<Map<String, Object>>> getInquiryCountOthers(){
        return R.data(prService.getInquiryCount(IU9PrService.PURCHASE_TYPE_INNER));
    }

    /**
     * 流标中台统计
     *
     * @return
     */
    @GetMapping("/flowcount")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "", notes = "")
    public R<List<Map<String, Object>>> getFlowCount(){
        return R.data(prService.getFlowCount(IU9PrService.PURCHASE_TYPE_NORMAL));
    }

    /**
     * 流标中台统计-小零件
     *
     * @return
     */
    @GetMapping("/flowcountOfOthers")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "", notes = "")
    public R<List<Map<String, Object>>> getFlowCountOfOthers(){
        return R.data(prService.getFlowCount(IU9PrService.PURCHASE_TYPE_INNER));
    }


    /**
     * 询价、流标中台-待审核、待下单列表
     */
    @GetMapping("/checkpage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入u9_pr")
    public R<IPage<U9PrDTO>> checkPage(PrReq prReq, Query query){
        IPage<U9PrDTO> pages = prService.getCheckPage(Condition.getPage(query), prReq);
        return R.data(pages);
    }

    /**
     * 流标中台-价格录入-价格参考列表
     *
     * @param u9PrVO U9PrVO
     * @return List
     */
    @GetMapping("/pricelist")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入u9PrVO")
    public R<List<PriceVO>> getReferencePriceList(U9PrVO u9PrVO){
        return R.data(prService.getPriceList(u9PrVO));
    }


    /**
     * 询价单 导出
     *
     * @param prReq PrReq
     * @param response HttpServletResponse
     */
    @GetMapping("/exportinquiry")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void inquiryExport(PrReq prReq, HttpServletResponse response) {
        prReq.setPurchCode(getUser().getAccount());
        prService.inquiryExport(prReq, response);
    }

    /**
     * 流标 导出
     *
     * @param prReq PrReq
     * @param response HttpServletResponse
     */
    @GetMapping("/exportflow")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void flowExport(PrReq prReq, HttpServletResponse response) {
        prReq.setPurchCode(getUser().getAccount());
        prService.flowExport(prReq, response);
    }


    /**
     * 询价单 导出 （小零件）
     *
     * @param prReq PrReq
     * @param response HttpServletResponse
     */
    @GetMapping("/exportinquiryOfOthers")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void inquiryExportOfOthers(PrReq prReq, HttpServletResponse response) {
        prReq.setPurchCode(getUser().getAccount());
        prService.inquiryExport(prReq, response);
    }

    /**
     * 流标 导出 （小零件）
     *
     * @param prReq PrReq
     * @param response HttpServletResponse
     */
    @GetMapping("/exportflowOfOthers")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void flowExportOfOthers(PrReq prReq, HttpServletResponse response) {
        prReq.setPurchCode(getUser().getAccount());
        prService.flowExportOfOthers(prReq, response);
    }

    /**
     * 流标（待提交）-批量提交附件
     *
     * @param submitPriceDTO SubmitPriceDTO
     * @return R
     */
    @PostMapping("/flowbatchexcel")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "流标暂存-批量提交附件", notes = "submitPrice")
    public R flowBatchExcel(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.flowBatchExcel(submitPriceReq));
    }


    /**
     * 流标（待提交）-批量提交附件-小零件
     *
     * @param submitPriceDTO SubmitPriceDTO
     * @return R
     */
    @PostMapping("/flowbatchexcelOfSupItem")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "流标暂存-批量提交附件-小零件", notes = "submitPrice")
    public R flowBatchExcelOfSupItem(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.flowBatchExcelOfSupItem(submitPriceReq));
    }

    /**
     * 流标（待提交）-批量提交审核
     *
     * @param submitPriceDTO SubmitPriceDTO
     * @return R
     */
    @PostMapping("/flowbatchaudit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "流标暂存-批量提交审核", notes = "submitPrice")
    public R<IPage<U9PrDTO>> flowSubmitBatch(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.flowBatchAudit(submitPriceReq));
    }

    /**
     * 流标（待提交）-提交
     *
     * @param ioEntity IoEntity
     * @return R
     */
    @PostMapping("/flowaudit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "流标暂存-提交", notes = "submitPrice")
    public R flowAudit(@Valid @RequestBody IoEntity ioEntity){
        return R.status(prService.flowAudit(ioEntity));
    }

    /**
     * 自动获取供应商价格信息
     *
     * @param submitPriceReq
     * @return
     */
    @PostMapping("/autoRetrieve")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "自动获取供应商价格信息", notes = "submitPrice")
    public R<List<ItemInfoDTO>> autoRetrieve(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        List<ItemInfoDTO> itemInfoDTOS = prService.autoRetrieve(submitPriceReq.getU9PrDTOS());
        return R.data(itemInfoDTOS);
    }

    /**
     * 重置PR单的信息
     *
     * @param submitPriceReq
     * @return
     */
    @PostMapping("/removePrList")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "自动获取供应商价格信息", notes = "submitPrice")
    public R<List<ItemInfoDTO>> removePrList(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.removePrList(submitPriceReq));
    }

    /**
     * 删除物料号的基础信息和报表信息
     *
     * @param submitPriceReq
     * @return
     */
    @PostMapping("/deleteDJInfo")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "自动获取供应商价格信息", notes = "submitPrice")
    public R<List<ItemInfoDTO>> deleteDJInfo(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.deleteDJInfo(submitPriceReq));
    }

    /**
     * 删除物料号的基础信息和报表信息  -- 小零件
     *
     * @param submitPriceReq
     * @return
     */
    @PostMapping("/deleteXLJInfo")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "自动获取供应商价格信息", notes = "submitPrice")
    public R<List<ItemInfoDTO>> deleteXLJInfo(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.deleteXLJInfo(submitPriceReq));
    }

    /**
     * 询价单中台--询价中、待评标列表
     *
     * @param prReq PrReq
     * @param query Query
     * @return R
     */
    @GetMapping("/inquirypage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入prReq")
    public R<IPage<U9PrDTO>> inquiryPage(PrReq prReq, Query query) {
        prReq.setPurchCode(getUser().getAccount());
        IPage<U9PrDTO> pages = prService.inquiryPage(Condition.getPage(query), prReq);
        return R.data(pages);
    }

    /**
     * 询价单中台--待审核、待下单列表caiGouScheduleUnpip
     *
     * @param prReq PrReq
     * @param query Query
     * @return R
     */
    @GetMapping("/inquirycheckpage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入u9_pr")
    public R<IPage<U9PrDTO>> inquiryCheckPage(PrReq prReq, Query query) {
        IPage<U9PrDTO> pages = prService.inquiryCheckPage(Condition.getPage(query), prReq);
        return R.data(pages);
    }

    /**
     * 流标中台--待处理列表
     *
     * @param prReq PrReq
     * @param query Query
     * @return R
     */
    @GetMapping("/flowpage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入prReq")
    public R<IPage<U9PrDTO>> flowPage(PrReq prReq, Query query) {
        prReq.setPurchCode(getUser().getAccount());
        IPage<U9PrDTO> pages = prService.flowPage(Condition.getPage(query), prReq);
        return R.data(pages);
    }

    /**
     * 流标中台--待提交、待审核、待下单列表
     *
     * @param prReq PrReq
     * @param query Query
     * @return R
     */
    @GetMapping("/flowpagecheckpage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入u9_pr")
    public R<IPage<U9PrDTO>> flowCheckPage(PrReq prReq, Query query) {
        IPage<U9PrDTO> pages = prService.flowCheckPage(Condition.getPage(query), prReq);
        return R.data(pages);
    }

    /**
     * 小零件流标中台--无供应商界面
     *
     * @param prReq PrReq
     * @param query Query
     * @return R
     */
    @GetMapping("/flowNoSupPage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入u9_pr")
    public R<IPage<U9PrDTO>> flowNoSupPage(PrReq prReq, Query query) {
        IPage<U9PrDTO> pages = prService.flowNoSupPage(Condition.getPage(query), prReq);
        return R.data(pages);
    }

    /**
     * 小零件流标中台--待处理、待提交、待审核、待下单列表
     *
     * @param prReq PrReq
     * @param query Query
     * @return R
     */
    @GetMapping("/flowPageOfOthers")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入prReq")
    public R<IPage<U9PrDTO>> flowPageOfOthers(PrReq prReq, Query query) {
        prReq.setPurchCode(getUser().getAccount());
        IPage<U9PrDTO> pages = prService.flowPageOfOthers(Condition.getPage(query), prReq);
        return R.data(pages);
    }

    /**
     * 小零件流标中台--待处理 - 外层 - 保存
     */
    @PostMapping("/saveBatch")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "保存", notes = "submitPrice")
    public
    R saveBatch(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.saveBatchOfOthers(submitPriceReq));
    }

    /**
     * 小零件流标中台--待处理 - Dialog - 保存
     */
    @PostMapping("/saveBatchForDialog")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "保存", notes = "submitPrice")
    public
    R saveBatchForDialog(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.saveBatchOfOthersForDialog(submitPriceReq));
    }

    /**
     * 重置PR单的信息 - 其他小零件
     *
     * @param submitPriceReq
     * @return
     */
    @PostMapping("/removePrListOfOthers")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "重置PR单的信息", notes = "submitPrice")
    public R<List<ItemInfoDTO>> removePrListOfOthers(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.removePrListOfOthers(submitPriceReq));
    }


    /**
     * 设为中标 - 其他小零件
     *
     * @param ioId
     * @return
     */
    @PostMapping("/winTheBid")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "中标", notes = "submitPriceReq")
    public R winTheBid(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.winTheBid(submitPriceReq));
    }

    /**
     * 取消中标 - 其他小零件
     *
     * @param ioId
     * @return
     */
    @PostMapping("/cancelTheBid")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "取消中标", notes = "submitPriceReq")
    public R cancelTheBid(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.cancelTheBid(submitPriceReq));
    }


    /**
     * 移至无供应商 - 其他小零件
     *
     * @param submitPriceReq
     * @return
     */
    @PostMapping("/moveToOthersOfNoSup")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "中标", notes = "submitPriceReq")
    public R moveToOthersOfNoSup(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.moveToOthersOfNoSup(submitPriceReq));
    }

    /**
     * 添加供应商交叉信息 - 其他小零件 - 无供应商界面
     *
     * @param submitPriceReq
     * @return
     */
    @PostMapping("/addOtherInfos")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "中标", notes = "submitPriceReq")
    public R addOtherInfos(@Valid @RequestBody SupItemOthers supItemOthers){
        return R.status(prService.addOtherInfos(supItemOthers));
    }

    /**
     * 查看供应商交叉信息 - 其他小零件 - 无供应商界面
     *
     * @param supItemOthers
     * @return
     */
    @GetMapping("/getOthersInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入u9_pr")
    public R<IPage<SupItemOthers>> getOthersInfo(SubmitPriceReq SubmitPriceReq, Query query) {
        IPage<SupItemOthers> pages = prService.getOthersInfo(Condition.getPage(query), SubmitPriceReq);
        return R.data(pages);
    }


    /**
     * 更新供应商交叉信息 - 其他小零件 - 无供应商界面
     *
     * @param supItemOthers
     * @return
     */
    @PostMapping("/updateOtherInfos")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "中标", notes = "submitPriceReq")
    public R updateOtherInfos(@Valid @RequestBody SupItemOthers supItemOthers){
        return R.status(prService.updateOtherInfos(supItemOthers));
    }

    /**
     * 移除供应商交叉信息 - 其他小零件 - 无供应商界面
     *
     * @param supItemOthers
     * @return
     */
    @PostMapping("/removeOtherInfos")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "中标", notes = "submitPriceReq")
    public R removeOtherInfos(@Valid @RequestBody SupItemOthers supItemOthers){
        return R.status(prService.removeOtherInfos(supItemOthers));
    }

    /**
     * 提交并询价 - 其他小零件 - 无供应商界面
     *
     * @param supItemOthers
     * @return
     */
    @PostMapping("/sendInquiry")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "提交并询价", notes = "submitPriceReq")
    public R sendAndInquiry(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(prService.sendAndInquiry(submitPriceReq));
    }


    @PostMapping("/evaluateBidOfOthers")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "立即评标", notes = "submitPrice")
    public
    R evaluateBidOfOthers(@Valid @RequestBody U9PrEntity pr){
        return R.status(prService.evaluateBidOfOthers(pr.getId()));
    }


    @GetMapping("/exportAllItemInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportAllItemInfo(SubmitPriceReq submitPriceReq, HttpServletResponse response) {
        prService.exportAllItemInfo(submitPriceReq, response);
    }


    @GetMapping("/exportAllItemInfoXLJ")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportAllItemInfoXLJ(SubmitPriceReq submitPriceReq, HttpServletResponse response) {
        prService.exportAllItemInfoXLJ(submitPriceReq, response);
    }

    @GetMapping("/exportAllItemInfoLZQ")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportAllItemInfoLZQ(SubmitPriceReq submitPriceReq, HttpServletResponse response) {
        prService.exportAllItemInfoLZQ(submitPriceReq, response);
    }

    @GetMapping("/exportAllItemInfoQZNew")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportAllItemInfoQZNew(SubmitPriceReq submitPriceReq, HttpServletResponse response) {
        prService.exportAllItemInfoQZNew(submitPriceReq, response);
    }


    @GetMapping("/exportAllItemInfoDZ")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportAllItemInfoDZ(SubmitPriceReq submitPriceReq, HttpServletResponse response) {
        prService.exportAllItemInfoDZ(submitPriceReq, response);
    }

    @GetMapping("/exportAllItemInfoFL")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportAllItemInfoFL(SubmitPriceReq submitPriceReq, HttpServletResponse response) {
        prService.exportAllItemInfoFL(submitPriceReq, response);
    }


    /**
     * 锻件自动下单报表
     */
    @GetMapping("/autoOrderOfDJ")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入u9_pr")
    public
    R<IPage<AutoOrderOfDJ>> autoOrderOfDJ(SubmitPriceReq submitPriceReq, Query query){
        IPage<AutoOrderOfDJ> autoOrderOfDJIPage = prService.autoOrderOfDJ(Condition.getPage(query),submitPriceReq);
        return R.data(autoOrderOfDJIPage);
    }

    /**
     * 锻件自动下单报表 - 导出
     */
    @GetMapping("/exportAutoOrderOfDJ")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportAutoOrderOfDJ(SubmitPriceReq submitPriceReq, HttpServletResponse response) {
        prService.exportAutoOrderOfDJ(submitPriceReq, response);
    }


    @GetMapping("/autoOrderOfXLJ")
    public R<IPage<AutoOrderOfXLJ>> autoOrderOfXLJ(AutoOrderOfXLJ autoOrderOfXLJ, Query query){
        IPage<AutoOrderOfXLJ> page = prService.autoOrderOfXLJ(Condition.getPage(query),autoOrderOfXLJ);
        return R.data(page);
    }


    /**
     * 锻件自动下单报表 - 导出
     */
    @GetMapping("/exportAutoOrderOfXLJ")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportAutoOrderOfXLJ(AutoOrderOfXLJ autoOrderOfXLJ, HttpServletResponse response) {
        prService.exportAutoOrderOfXLJ(autoOrderOfXLJ, response);
    }


    @PostMapping ("/handleExceptData")
    public String handleExceptData(@RequestBody List<U9PrFromPhpDTO> u9list) {
        return prService.handleExceptDataWithNoProject(u9list);

        //prService.handleExceptData(u9list);

        //List<U9PrFromPhpDTO> exceptData=prService.handleExceptData(u9list);

        //return JSONObject.toJSONString(exceptData);
    }
}
