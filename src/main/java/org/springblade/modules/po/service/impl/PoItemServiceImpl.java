package org.springblade.modules.po.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jettison.json.JSONObject;
import org.springblade.common.cache.PlanReqPoItemCache;
import org.springblade.common.cache.PoItemCache;
import org.springblade.common.config.AtwSrmConfiguration;
import org.springblade.common.utils.ExcelExportStatisticStyler;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.common.utils.WillHttpUtil;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.OkHttpUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.aps.entity.ApsReportExdevEntity;
import org.springblade.modules.aps.service.IApsReportExdevService;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.outpr.entity.OutPrItemArtifactEntity;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.entity.OutPrItemProcessEntity;
import org.springblade.modules.outpr.entity.OutSupPreOrderEntity;
import org.springblade.modules.outpr.service.IOutPrItemProcessService;
import org.springblade.modules.outpr.service.IOutPrItemService;
import org.springblade.modules.outpr.service.IOutSupPreOrderService;
import org.springblade.modules.po.dto.*;
import org.springblade.modules.po.entity.CraftCtrlNodeEntity;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.entity.PoItemCraftCtrlNodeEntity;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.entity.PoItemReqRepotTotal;
import org.springblade.modules.po.entity.PoItemReqRepotTotal2;
import org.springblade.modules.po.mapper.PoItemMapper;
import org.springblade.modules.po.mapper.PoMapper;
import org.springblade.modules.po.service.ICraftCtrlNodeService;
import org.springblade.modules.po.service.IPoItemCraftCtrlNodeService;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.po.service.IPoPronoService;
import org.springblade.modules.po.service.IPoService;
import org.springblade.modules.po.vo.*;
import org.springblade.modules.po.wrapper.PoItemWrapper;
import org.springblade.modules.pr.dto.U9PrDTO;
import org.springblade.modules.pr.entity.ItemInfoEntityOfQZ;
import org.springblade.modules.pr.mapper.U9PrMapper;
import org.springblade.modules.pricelib.service.IPriceLibService;
import org.springblade.modules.queue.entity.QueueEmailEntity;
import org.springblade.modules.queue.service.IQueueEmailService;
import org.springblade.modules.supplier.entity.CaiGouSchedule;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.mapper.SupplierScheduleMapper;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.service.IParamService;
import org.springblade.modules.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.springblade.common.utils.ItemAnalysisUtil.getItemInfoOfFZ;
import static org.springblade.common.utils.ItemAnalysisUtil.getItemInfoOfQiuZuo;
import static org.springblade.core.secure.utils.AuthUtil.*;

/**
 * 采购订单明细 服务实现类
 * @author Will
 */
@Service
public
class PoItemServiceImpl extends BaseServiceImpl<PoItemMapper, PoItemEntity> implements IPoItemService{


    @Autowired
    ISupplierService supplierService;

    @Autowired
    @Lazy
    IItemService itemService;

    @Autowired
    IOutPrItemService outPrItemService;

    @Autowired
    IOutPrItemProcessService outPrItemProcessSer;

    @Autowired
    AtwSrmConfiguration atwSrmConfiguration;

    @Autowired
    @Lazy
    IPoService      poService;

    @Autowired
    @Lazy
    IPoPronoService poPronoService;

    @Autowired
    IPoItemCraftCtrlNodeService poItemCraftCtrlNodeService;

    @Autowired
    ICraftCtrlNodeService craftCtrlNodeService;

    @Autowired
    IPriceLibService priceLibService;

    @Autowired
    private IQueueEmailService queueEmailService;

    @Autowired
    private IParamService paramService;

    @Autowired
    private IUserService userService;

    @Autowired
    private U9PrMapper u9PrMapper;

    @Autowired
    private PoMapper poMapper;
    @Autowired
    private ISupplierService iSupplierService;

    @Autowired
    SupplierScheduleMapper supplierScheduleMapper;

    @Autowired
    @Lazy
    private IApsReportExdevService iApsReportExdevService;

    @Value("${sendCloud.url}")
    private String url;

    @Value("${sendCloud.apiUser}")
    private String apiUser;

    @Value("${sendCloud.apiKey}")
    private String apiKey;

    @Value("${WWEMAILS.FROM}")
    private  String from;

    @Value("${WWEMAILS.TO}")
    private  String to;

    @Value("${WWEMAILS.CC}")
    private  String cc;

    @Value("${WWEMAILS.CG}")
    private  String cg;

    @Override
    public
    QueryWrapper<PoItemEntity> getQueryWrapper(PoItemDTO poItem){
        QueryWrapper<PoItemEntity> query = Wrappers.<PoItemEntity>query()
            .eq(poItem.getStatus() != null, "status", poItem.getStatus())
            .eq(poItem.getIsByWeight() != null, "is_by_weight", poItem.getIsByWeight())
            .like(poItem.getIsDeliverablesFull() != null, "is_deliverables_full", poItem.getIsDeliverablesFull())
            .like(StringUtil.isNotBlank(poItem.getPrCode()), "pr_code", poItem.getPrCode())
            .like(StringUtil.isNotBlank(poItem.getPoCode()), "po_code", poItem.getPoCode())
            .eq(poItem.getPoLn() != null, "po_ln", poItem.getPoLn())
            .like(StringUtil.isNotBlank(poItem.getSupCode()), "sup_code", poItem.getSupCode())
            .like(StringUtil.isNotBlank(poItem.getSupName()), "sup_name", poItem.getSupName())
            .like(StringUtil.isNotBlank(poItem.getItemCode()), "item_code", poItem.getItemCode())
            .like(StringUtil.isNotBlank(poItem.getItemName()), "item_name", poItem.getItemName())
            .ge(poItem.getReqDateStart() != null, "req_date", poItem.getReqDateStart())
            .le(poItem.getReqDateEnd() != null, "req_date", poItem.getReqDateEnd());
        return query;
    }

    private IPage<PoItemEntity> getPoItemEntityIPage(IPage<PoItemEntity> page, PoItemDTO poItem) {
        int length = 0;
        if(poItem.getItemCode()!=null){
            length = poItem.getItemCode().split(",").length;
        }

        QueryWrapper query = Wrappers.<PoItemEntity>query()
            .gt("pro_goods_num", 0)
            .eq("status", STATUS_ORDER)
            .eq(StringUtil.isNotBlank(poItem.getTraceCode()),"trace_code", poItem.getTraceCode())
            .like(poItem.getIsDeliverablesFull() != null, "is_deliverables_full", poItem.getIsDeliverablesFull())
            .like(StringUtil.isNotBlank(poItem.getPoCode()), "po_code", poItem.getPoCode())
            .like(StringUtil.isNotBlank(poItem.getSupCode()), "sup_code", poItem.getSupCode())
            .like(StringUtil.isNotBlank(poItem.getSupName()), "sup_name", poItem.getSupName())
            .like(StringUtil.isNotBlank(poItem.getItemCode()) && length == 1, "item_code", poItem.getItemCode())
            .like(StringUtil.isNotBlank(poItem.getItemName()), "item_name", poItem.getItemName())
            .like(StringUtil.isNotBlank(poItem.getEndUser()), "end_user", poItem.getEndUser())
            .eq(StringUtil.isNotBlank(poItem.getMoNo()), "mo_no", poItem.getMoNo())
            .apply(StringUtil.isNotBlank(poItem.getItemCode()) && length > 1, "FIND_IN_SET (" + "item_code,'"+ poItem.getItemCode() +"')")
            .orderByAsc("sup_confirm_date","tc_num","item_code");

        if (!StringUtil.isEmpty(poItem.getIsSupUpdate())) {
            query.eq("is_sup_update", poItem.getIsSupUpdate());
        }
        return page(page, query);
    }


    @Override
    public
    boolean submitTrace(Long poItemId){
        BladeUser    user   = getUser();
        PoItemEntity poItem = getById(poItemId);
        poItem.setLastTraceTime(new Date());
        poItem.setLastTracer(user.getUserId());
        return updateById(poItem);
    }

    /**
     * 待下单数量
     * @return
     */
    @Override
    public
    int waitCount(){
        return count(Condition.getQueryWrapper(new PoItemEntity()).eq("status", STATUS_INIT));
    }

    @Override
    public
    boolean createByOutPreOrder(OutSupPreOrderEntity preOrder, String remark){
        Long                         nowTime          = new Date().getTime()/1000;
        Supplier                     sup              = supplierService.getByCode(preOrder.getSupCode());
        OutPrItemEntity              outprItem        = outPrItemService.getById(preOrder.getPrItemId());
        List<OutPrItemProcessEntity> outprItemprocess = outPrItemProcessSer.getListByItemId(preOrder.getPrItemId());
        // 阀内件待下单 一个物料编号一行
        if(IOutPrItemService.PURCHTYPE_INN.equals(outprItem.getPurchaseType())){
            Integer      prLn   = outprItemprocess.get(0).getPrLn();
            PoItemEntity poitem = new PoItemEntity();

            poitem.setItemCode(preOrder.getItemCode());
            poitem.setItemName(preOrder.getItemName());
            poitem.setSupCode(preOrder.getSupCode());
            poitem.setSupName(preOrder.getSupName());
            poitem.setPriceNum(preOrder.getPriceNum());
            poitem.setPriceUom(preOrder.getPriceUom());
            poitem.setPriceUomCode(preOrder.getPriceUom());
            poitem.setTcNum(preOrder.getPriceNum());
            poitem.setTcUom(preOrder.getPriceUom());
            poitem.setTcUomCode(preOrder.getPriceUom());
            poitem.setSupConfirmDate(preOrder.getSupDeliveryTime());
            poitem.setReqDate(preOrder.getReqDate());
            poitem.setPrice(preOrder.getTaxPrice());
            poitem.setAmount(preOrder.getTaxPrice().multiply(preOrder.getPriceNum()));
            poitem.setProGoodsNum(preOrder.getPriceNum());

            poitem.setPrId(outprItem.getPrId());
            poitem.setPrCode(outprItem.getPrCode());
            poitem.setMaterialCost(outprItem.getMaterialCost());
            poitem.setPrLn(prLn);

            poitem.setTaxRate(sup.getTaxRate());
            Item item = itemService.getByCode(preOrder.getItemCode());
            poitem.setPurchCode(item.getPurchCode());
            poitem.setPurchName(item.getPurchName());

            poitem.setWinbidTime(nowTime);
            poitem.setRemark(remark);
            poitem.setSource(SOURCE_INNER);
            poitem.setSourceId(outprItem.getId());
            poitem.setStatus(STATUS_INIT);

            if(IOutSupPreOrderService.INQUIRYWAY_PRICELIB.equals(preOrder.getInquiryWay()) && !itemService.isGasCtrl(item.getMainCode())){
                //2020.07.07 并单逻辑  //2020.07.29 不是气控件的才走并单逻辑
                poitem.setBizBranch(BIZ_BRANCH_COMBILL);
            }

            return save(poitem);
        }
        // 委外到 待下单 一个工序一行
        if(IOutPrItemService.PURCHTYPE_OUT.equals(outprItem.getPurchaseType())){
            List<PoItemEntity> poItems = Lists.newArrayList();
            for(OutPrItemProcessEntity procss : outprItemprocess){
                PoItemEntity poitem = new PoItemEntity();
                poItems.add(poitem);
                String itemCode = preOrder.getItemCode() + "-" + procss.getProcessCode();
                poitem.setItemCode(itemCode);
                poitem.setItemName(preOrder.getItemName() + "-" + procss.getProcessName());
                poitem.setSupCode(preOrder.getSupCode());
                poitem.setSupName(preOrder.getSupName());
                poitem.setPriceNum(preOrder.getPriceNum());
                poitem.setPriceUom(preOrder.getPriceUom());
                poitem.setPriceUomCode(preOrder.getPriceUom());
                poitem.setTcNum(preOrder.getPriceNum());
                poitem.setTcUom(preOrder.getPriceUom());
                poitem.setTcUomCode(preOrder.getPriceUom());
                poitem.setSupConfirmDate(preOrder.getSupDeliveryTime());
                poitem.setReqDate(preOrder.getReqDate());
                poitem.setPrice(preOrder.getTaxPrice());
                poitem.setAmount(preOrder.getTaxPrice().multiply(preOrder.getPriceNum()));
                poitem.setProGoodsNum(preOrder.getPriceNum());

                poitem.setPrId(outprItem.getPrId());
                poitem.setPrCode(outprItem.getPrCode());
                poitem.setMaterialCost(outprItem.getMaterialCost());
                poitem.setPrLn(procss.getPrLn());

                poitem.setTaxRate(sup.getTaxRate());
                Item item = itemService.getByCode(itemCode);
                poitem.setPurchCode(item.getPurchCode());
                poitem.setPurchName(item.getPurchName());

                poitem.setWinbidTime(nowTime);
                poitem.setRemark(remark);
                poitem.setSource(SOURCE_OUT);
                poitem.setSourceId(outprItem.getId());
                poitem.setStatus(STATUS_INIT);

                if(IOutSupPreOrderService.INQUIRYWAY_PRICELIB.equals(preOrder.getInquiryWay()) && !itemService.isGasCtrl(item.getMainCode())){
                    //2020.07.07 并单逻辑   //2020.07.29 不是气控件的才走并单逻辑
                    poitem.setBizBranch(BIZ_BRANCH_COMBILL);
                }
            }
            return saveBatch(poItems);
        }
        return false;
    }

    @Override
    public
    boolean createByOutArtifact(OutPrItemArtifactEntity artifactEntity, String remark){
        PoItemEntity poitem = new PoItemEntity();
        String itemCode = IOutPrItemService.PURCHTYPE_INN.equals(artifactEntity.getPurchaseType()) ? artifactEntity
            .getItemCode() : artifactEntity.getItemCode() + "-" + artifactEntity.getProcessCode();
        Item item = itemService.getByCode(itemCode);

        poitem.setItemCode(item.getCode());
        poitem.setItemName(item.getName());
        poitem.setPurchCode(item.getPurchCode());
        poitem.setPurchName(item.getPurchName());

        poitem.setSupCode(artifactEntity.getSupCode());
        poitem.setSupName(artifactEntity.getSupName());
        poitem.setPriceNum(artifactEntity.getPriceNum());
        poitem.setPriceUom(artifactEntity.getPriceUom());
        poitem.setPriceUomCode(artifactEntity.getPriceUom());
        poitem.setTcNum(artifactEntity.getPriceNum());
        poitem.setTcUom(artifactEntity.getPriceUom());
        poitem.setTcUomCode(artifactEntity.getPriceUom());
        poitem.setSupConfirmDate(artifactEntity.getSupDeliveryTime());
        poitem.setReqDate(artifactEntity.getReqDate());
        poitem.setPrice(artifactEntity.getPrice());
        poitem.setAmount(artifactEntity.getPrice().multiply(artifactEntity.getPriceNum()));
        poitem.setProGoodsNum(artifactEntity.getPriceNum());
        poitem.setPrId(artifactEntity.getPrId());
        poitem.setPrCode(artifactEntity.getPrCode());
        poitem.setMaterialCost(artifactEntity.getMaterialCost());
        poitem.setPrLn(artifactEntity.getPrLn());

        Supplier sup = supplierService.getByCode(artifactEntity.getSupCode());
        poitem.setTaxRate(sup.getTaxRate());

        Long nowTime = new Date().getTime()/1000;
        poitem.setWinbidTime(nowTime);
        poitem.setRemark(remark);
        poitem.setSource(SOURCE_ART);
        poitem.setSourceId(artifactEntity.getId());
        poitem.setStatus(STATUS_INIT);
        return save(poitem);
    }

    @Override
    public IPage<PoItemVO> getDeliveryPage(IPage<PoItemEntity> page, PoItemDTO poItem) {
        IPage<PoItemEntity> entityPage = getPoItemEntityIPage(page, poItem);
        IPage<PoItemVO>     retPage    = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<PoItemVO>      voList     = Lists.newArrayList();

        // 针对球体查询的特殊处理
        if (((poItem.getSupCode() != null && !poItem.getSupCode().isEmpty()) || (poItem.getSupName() != null && !poItem.getSupName().isEmpty() && poItem.getItemName() != null)) && (poItem.getItemName()!=null && poItem.getItemName().equals("球"))) {

            List<PoItemEntity> qzList = entityPage.getRecords();
            poItem.setItemName("阀座");
            page.setSize(1000000);
            page.setCurrent(1);
            IPage<PoItemEntity> fzPage = getPoItemEntityIPage(page, poItem);
            List<PoItemEntity> fzList = fzPage.getRecords();
            List<PoItemEntity> qtFzList = this.getQTFZListOfEntity(poItem, qzList,fzList);

            for(PoItemEntity entity : qtFzList){
                PoItemVO vo = PoItemWrapper.build().entityVO(entity);
                PoEntity po = poMapper.getPoInfoByPoCode(vo.getPoCode());
                Integer rcvNumAll = this.baseMapper.getRcvAllNumByPiId(entity.getId().toString());
                String codeType =this.baseMapper.getABCType(entity.getItemCode());
                vo.setCodeType(codeType);
                vo.setNotSendNum(entity.getTcNum().add(entity.getFillGoodsNum().subtract(new BigDecimal(rcvNumAll))));
                vo.setPoStatus(po.getStatus());
                vo.setTemplateType(po.getTemplateType());
                voList.add(vo);
            }

        } else {

            for(PoItemEntity entity : entityPage.getRecords()){
                PoItemVO vo = PoItemWrapper.build().entityVO(entity);
                PoEntity po = poMapper.getPoInfoByPoCode(vo.getPoCode());
                Integer rcvNumAll = this.baseMapper.getRcvAllNumByPiId(entity.getId().toString());
                String codeType =this.baseMapper.getABCType(entity.getItemCode());
                vo.setCodeType(codeType);
                vo.setNotSendNum(entity.getTcNum().add(entity.getFillGoodsNum().subtract(new BigDecimal(rcvNumAll))));
                vo.setPoStatus(po.getStatus());
                vo.setTemplateType(po.getTemplateType());
                voList.add(vo);
            }

        }

        retPage.setRecords(voList);
        return retPage;
    }

    @Override
    public
    int getDeliveryCount(){
        QueryWrapper query = Wrappers.<PoItemEntity>query().gt("pro_goods_num", 0).eq("status", STATUS_ORDER)
            .eq("is_sup_update", 1)
            .eq("trace_code",getUser().getAccount());
        List<PoItemEntity> entityPage = list(query);
        return entityPage.size();
    }

    @Override
    public
    boolean submitUpdateDate(PoUpDateReq poUpDateReq) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<String, Object>(3){{
            put("poItemId", poUpDateReq.getPoItemId());
            put("date", StringUtil.isNotBlank(poUpDateReq.getDate() + "") ? poUpDateReq.getDate() : "");
            put("note", poUpDateReq.getNote());
        }};
        List<Map<String, Object>> reqJson = Lists.newArrayList();
        reqJson.add(map);
        String res = OkHttpUtil.postJson(
            atwSrmConfiguration.getBizapiDomain() + "/openapi/updateconfirmdate", mapper.writeValueAsString(reqJson));
        if(StringUtil.isBlank(res)){
            throw new RuntimeException("修改失败: res为空");
        }
        JSONObject jsonObject = new JSONObject(res);
        if(!"2000".equals(String.valueOf(jsonObject.get("code")))){
            throw new RuntimeException("修改失败:" + jsonObject.get("msg"));
        }
        PoItemEntity poItemEntity = getById(poUpDateReq.getPoItemId());
        poItemEntity.setIsSupUpdate(0);
        poItemEntity.setSupUpdateDateCheck(null);
        return updateById(poItemEntity);
    }

    /**
     * 交付中心导出
     * @DESE maily
     */
    @Override
    public
    void getDeliveryExport(PoItemDTO poItem, HttpServletResponse response){

        List<PoItemVO> list = this.baseMapper.getExcelData(poItem);
        List<PoItemExcelVO> excelVOS = new ArrayList<>();

        // 针对球体查询的特殊处理
        if (((poItem.getSupCode() != null && !poItem.getSupCode().isEmpty()) || (poItem.getSupName() != null && !poItem.getSupName().isEmpty() && poItem.getItemName() != null)) && poItem.getItemName().equals("球")) {
            poItem.setItemName("阀座");
            List<PoItemVO> fzList = this.baseMapper.getExcelData(poItem);
            List<PoItemVO> qtFzList = this.getQTFZListOfVO(poItem, list,fzList);
            list = qtFzList;
        }

        for (PoItemVO poItemVO : list) {
            PoItemExcelVO poItemExcelVO = BeanUtil.copy(poItemVO,PoItemExcelVO.class);

            Integer key = 0;
            if(poItemVO.getPoStatus() != null){
                key = poItemVO.getPoStatus();
            }
            String u9 = poItemVO.getU9StatusCode();
            BigDecimal arvGoodsNum = poItemVO.getArvGoodsNum();
            String status = "";

            if (key == 0) {
                status = "";
            }
            if (u9 == null) {
                status = "";
            } else if (u9.equals("2")) {
                switch (key){
                    case 10 : status="待确认";break;
                    case 20 : status="退回";break;
                    case 30 : status="待上传";break;
                    case 40 : status="待审核";break;
                    case 50 : status="执行中";break;
                    case 60 : status="合同拒绝";break;
                    case 70 : status="关闭";break;
                }
            } else if (u9.equals("3")) {
                status = "自然关闭";
            } else if (u9.equals("4")) {
                if (arvGoodsNum.compareTo(new BigDecimal("0")) == 1) {
                    status = "部分取消";
                } else if (arvGoodsNum.compareTo(new BigDecimal("0")) == 0) {
                    status = "全部取消";
                }
            } else if (u9.equals("5")) {
                status = "超额关闭";
            } else {
                status = "";
            }
            poItemExcelVO.setU9Status(status);
            String codeType =this.baseMapper.getABCType(poItemVO.getItemCode());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            poItemExcelVO.setCodeType(codeType);
            poItemExcelVO.setReqDateFmt((poItemVO.getReqDate()!=null&&poItemVO.getReqDate()!=0)?sdf.format(poItemVO.getReqDate()*1000):"");
            poItemExcelVO.setSupConfirmDateFmt((poItemVO.getSupConfirmDate()!=null&&poItemVO.getSupConfirmDate()!=0)? sdf.format(poItemVO.getSupConfirmDate()*1000):"");
            excelVOS.add(poItemExcelVO);
        }

        ExcelUtils.defaultExport(excelVOS, PoItemExcelVO.class, "交付中心" + DateUtil.formatDate(new Date()), response);
    }


    /**
     * 采购报表 - 导出
     * @param poItem
     * @param response
     * @DESC maily
     */
    @Override
    public void getReportExport(PoItemDTO poItem, HttpServletResponse response) {
        if(poItem.getStatuss()==null){
            poItem.setStatuss("");
        }
        QueryWrapper queryWrapper = Wrappers.<PoItemEntity>query()
            .ne("status", STATUS_INIT)
            .ge(poItem.getLastSyncTimeStart() != null, "last_sync_time", poItem.getLastSyncTimeStart())
            .le(poItem.getLastSyncTimeEnd() != null, "last_sync_time", poItem.getLastSyncTimeEnd())
            .like(StringUtil.isNotBlank(poItem.getPrCode()), "pr_code", poItem.getPrCode())
            .like(StringUtil.isNotBlank(poItem.getPoCode()), "po_code", poItem.getPoCode())
            .like(StringUtil.isNotBlank(poItem.getSupCode()), "sup_code", poItem.getSupCode())
            .like(StringUtil.isNotBlank(poItem.getSupName()), "sup_name", poItem.getSupName())
            .like(StringUtil.isNotBlank(poItem.getItemCode()), "item_code", poItem.getItemCode())
            .like(StringUtil.isNotBlank(poItem.getItemName()), "item_name", poItem.getItemName())
            .like(StringUtil.isNotBlank(poItem.getPurchName()), "purch_name", poItem.getPurchName())
            .like(StringUtil.isNotBlank(poItem.getProNo()), "pro_no", poItem.getProNo())
            .like(StringUtil.isNotBlank(poItem.getEndUser()), "end_user", poItem.getEndUser())
            .apply(StringUtil.isNotBlank(poItem.getIds()), "FIND_IN_SET (" + "id,'"+ poItem.getIds() +"')")
            .in(StringUtil.isNotBlank(poItem.getStatuss()),"status",Arrays.asList(poItem.getStatuss().split(",")));
             List<PoItemEntity> list = list(queryWrapper);

        List<PoItemCaiGouBaoBiaoExcel>      voList     = new ArrayList<>();
        List<PoItemCaiGouBaoBiaoSecExcel>   poItemCaiGouBaoBiaoSecExcels     = new ArrayList<>();
        List<PoItemVO>  po     = Lists.newArrayList();
        for (PoItemVO poItemVO : po) {
            System.out.println("poItemVO = " + poItemVO);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(PoItemEntity poItemEntity : list){

            /*abc*/
            String codeType =this.baseMapper.getABCType(poItemEntity.getItemCode());
            PoItemCaiGouBaoBiaoExcel poItemCaiGouBaoBiaoExcel = BeanUtil.copy(poItemEntity,PoItemCaiGouBaoBiaoExcel.class);
            poItemCaiGouBaoBiaoExcel.setCodeType(codeType);
            poItemCaiGouBaoBiaoExcel.setReqDateFmt((poItemEntity.getReqDate()!=null && poItemEntity.getReqDate()>0)  ? sdf.format(new Date(poItemEntity.getReqDate()*1000)):"");
            poItemCaiGouBaoBiaoExcel.setSupConfirmDateFmt((poItemEntity.getSupConfirmDate()!=null && poItemEntity.getSupConfirmDate()>0)  ? sdf.format(new Date(poItemEntity.getSupConfirmDate()*1000)):"");
            poItemCaiGouBaoBiaoExcel.setSupUpdateDateFmt((poItemEntity.getSupUpdateDate()!=null && poItemEntity.getSupUpdateDate()>0) ? sdf.format(new Date(poItemEntity.getSupUpdateDate()*1000)):"");
            poItemCaiGouBaoBiaoExcel.setLastSyncTime((poItemEntity.getLastSyncTime()!=null && poItemEntity.getLastSyncTime()>0) ? sdf.format(new Date(poItemEntity.getLastSyncTime()*1000)):"");


            PoItemCaiGouBaoBiaoSecExcel poItemCaiGouBaoBiaoSecExcel = BeanUtil.copy(poItemCaiGouBaoBiaoExcel,PoItemCaiGouBaoBiaoSecExcel.class);
            poItemCaiGouBaoBiaoSecExcels.add(poItemCaiGouBaoBiaoSecExcel);
            voList.add(poItemCaiGouBaoBiaoExcel);
        }


        String account = getUserAccount();
        if(account.equals("180411") || account.equals("admin") ) {
            ExcelUtils.defaultExport(voList, PoItemCaiGouBaoBiaoExcel.class, "采购报表" + DateUtil.formatDate(new Date()), response);
        } else {
            ExcelUtils.defaultExport(poItemCaiGouBaoBiaoSecExcels, PoItemCaiGouBaoBiaoSecExcel.class, "采购报表" + DateUtil.formatDate(new Date()), response);
        }

    }

    /**
     * VMI监控采购报表 - 导出
     * @param poItem
     * @param response
     * @DESC maily
     */
    @Override
    public void getVMIReportExport(PoItemDTO poItem, HttpServletResponse response) {
        if(poItem.getStatuss()==null){
            poItem.setStatuss("");
        }
        /*QueryWrapper queryWrapper = Wrappers.<PoItemEntity>query()
            .ne("status", STATUS_INIT)
            .ge(poItem.getLastSyncTimeStart() != null, "last_sync_time", poItem.getLastSyncTimeStart())
            .le(poItem.getLastSyncTimeEnd() != null, "last_sync_time", poItem.getLastSyncTimeEnd())
            .like(StringUtil.isNotBlank(poItem.getPrCode()), "pr_code", poItem.getPrCode())
            .like(StringUtil.isNotBlank(poItem.getPoCode()), "po_code", poItem.getPoCode())
            .like(StringUtil.isNotBlank(poItem.getSupCode()), "sup_code", poItem.getSupCode())
            .like(StringUtil.isNotBlank(poItem.getSupName()), "sup_name", poItem.getSupName())
            .like(StringUtil.isNotBlank(poItem.getItemCode()), "item_code", poItem.getItemCode())
            .like(StringUtil.isNotBlank(poItem.getItemName()), "item_name", poItem.getItemName())
            .like(StringUtil.isNotBlank(poItem.getPurchName()), "purch_name", poItem.getPurchName())
            .like(StringUtil.isNotBlank(poItem.getProNo()), "pro_no", poItem.getProNo())
            .like(StringUtil.isNotBlank(poItem.getEndUser()), "end_user", poItem.getEndUser())
            .apply(StringUtil.isNotBlank(poItem.getIds()), "FIND_IN_SET (" + "id,'"+ poItem.getIds() +"')")
            .in(StringUtil.isNotBlank(poItem.getStatuss()),"status",Arrays.asList(poItem.getStatuss().split(",")));
        List<PoItemEntity> list = list(queryWrapper);*/
        List<String> statusarray = Arrays.asList(poItem.getStatuss().split(","));
        poItem.setStatusarray(statusarray);
        List<PoItemEntity> list = this.baseMapper.getPoItemEntityPageFromOracleList(poItem);

        List<PoItemCaiGouBaoBiaoExcel>      voList     = new ArrayList<>();
        List<PoItemCaiGouBaoBiaoSecExcel>   poItemCaiGouBaoBiaoSecExcels     = new ArrayList<>();
        List<PoItemVO>  po     = Lists.newArrayList();
        for (PoItemVO poItemVO : po) {
            System.out.println("poItemVO = " + poItemVO);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(PoItemEntity poItemEntity : list){

            /*abc*/
            String codeType =this.baseMapper.getABCType(poItemEntity.getItemCode());
            PoItemCaiGouBaoBiaoExcel poItemCaiGouBaoBiaoExcel = BeanUtil.copy(poItemEntity,PoItemCaiGouBaoBiaoExcel.class);
            poItemCaiGouBaoBiaoExcel.setCodeType(codeType);
            poItemCaiGouBaoBiaoExcel.setReqDateFmt((poItemEntity.getReqDate()!=null && poItemEntity.getReqDate()>0)  ? sdf.format(new Date(poItemEntity.getReqDate()*1000)):"");
            poItemCaiGouBaoBiaoExcel.setSupConfirmDateFmt((poItemEntity.getSupConfirmDate()!=null && poItemEntity.getSupConfirmDate()>0)  ? sdf.format(new Date(poItemEntity.getSupConfirmDate()*1000)):"");
            poItemCaiGouBaoBiaoExcel.setSupUpdateDateFmt((poItemEntity.getSupUpdateDate()!=null && poItemEntity.getSupUpdateDate()>0) ? sdf.format(new Date(poItemEntity.getSupUpdateDate()*1000)):"");
            poItemCaiGouBaoBiaoExcel.setLastSyncTime((poItemEntity.getLastSyncTime()!=null && poItemEntity.getLastSyncTime()>0) ? sdf.format(new Date(poItemEntity.getLastSyncTime()*1000)):"");


            PoItemCaiGouBaoBiaoSecExcel poItemCaiGouBaoBiaoSecExcel = BeanUtil.copy(poItemCaiGouBaoBiaoExcel,PoItemCaiGouBaoBiaoSecExcel.class);
            poItemCaiGouBaoBiaoSecExcels.add(poItemCaiGouBaoBiaoSecExcel);
            voList.add(poItemCaiGouBaoBiaoExcel);
        }


        String account = getUserAccount();
        if(account.equals("180411") || account.equals("admin") ) {
            ExcelUtils.defaultExport(voList, PoItemCaiGouBaoBiaoExcel.class, "VMI监控报表" + DateUtil.formatDate(new Date()), response);
        } else {
            ExcelUtils.defaultExport(poItemCaiGouBaoBiaoSecExcels, PoItemCaiGouBaoBiaoSecExcel.class, "VMI监控报表" + DateUtil.formatDate(new Date()), response);
        }

    }


    /**
     * 导入修改交期
     * @return
     */
    @Override
    public
    boolean importUpdateDate(MultipartFile file) throws Exception{
        List<PoItemVO>            voList  = ExcelUtils.importExcel(file, 0, 1, PoItemVO.class);
        List<Map<String, Object>> reqJson = Lists.newArrayList();
        for(PoItemVO vo : voList){
            Map<String, Object> map = new HashMap<String, Object>(){{
                put("poItemId", vo.getId());
                put("date", StringUtil.isNotBlank(vo.getSupUpdateDateFmt()) ?
                    DateUtil.parse(vo.getSupUpdateDateFmt(), DateUtil.PATTERN_DATE).getTime()/1000 : "");
                put("note", vo.getRemark());
            }};
            reqJson.add(map);
        }
        ObjectMapper mapper = new ObjectMapper();
        String       res    = OkHttpUtil.postJson(
            atwSrmConfiguration.getBizapiDomain() + "/openapi/updateconfirmdate", mapper.writeValueAsString(reqJson));
        // todo
        return true;
    }

    /**
     * 采购报表
     * @return
     * @DESC maily  select
     */
    @Override
    public
    IPage<PoItemVO> reportPage(Query query, PoItemDTO poItem){
        if(poItem.getStatuss()==null){
            poItem.setStatuss("");
        }
        QueryWrapper queryWrapper = Wrappers.<PoItemEntity>query()
            .ne("status", STATUS_INIT)
            .ge(poItem.getLastSyncTimeStart() != null, "last_sync_time", poItem.getLastSyncTimeStart())
            .le(poItem.getLastSyncTimeEnd() != null, "last_sync_time", poItem.getLastSyncTimeEnd())
            .like(StringUtil.isNotBlank(poItem.getPrCode()), "pr_code", poItem.getPrCode())
            .like(StringUtil.isNotBlank(poItem.getPoCode()), "po_code", poItem.getPoCode())
            .like(StringUtil.isNotBlank(poItem.getSupCode()), "sup_code", poItem.getSupCode())
            .like(StringUtil.isNotBlank(poItem.getSupName()), "sup_name", poItem.getSupName())
            .like(StringUtil.isNotBlank(poItem.getItemCode()), "item_code", poItem.getItemCode())
            .like(StringUtil.isNotBlank(poItem.getItemName()), "item_name", poItem.getItemName())
            .like(StringUtil.isNotBlank(poItem.getPurchName()), "purch_name", poItem.getPurchName())
            .like(StringUtil.isNotBlank(poItem.getProNo()), "pro_no", poItem.getProNo())
            .like(StringUtil.isNotBlank(poItem.getEndUser()), "end_user", poItem.getEndUser())
            .in(StringUtil.isNotBlank(poItem.getStatuss()),"status",Arrays.asList(poItem.getStatuss().split(",")));
        IPage<PoItemEntity> entityPage = page(Condition.getPage(query), queryWrapper);
        IPage<PoItemVO>     retPage    = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<PoItemVO>      voList     = Lists.newArrayList();
        for(PoItemEntity entity : entityPage.getRecords()){
            PoItemVO poItemVO = PoItemWrapper.build().entityVO(entity);
            String codeType =this.baseMapper.getABCType(entity.getItemCode());
            //ABC类卡
            poItemVO.setCodeType(codeType);
            voList.add(poItemVO);
        }
        retPage.setRecords(voList);
        return retPage;
    }


    /**
     * VMI监控报表
     * @return
     * @DESC maily  select
     */
    @Override
    public
    IPage<PoItemVO> vmiReportPage(Query query, PoItemDTO poItem){
        if(poItem.getStatuss()==null){
            poItem.setStatuss("");
        }
        /*QueryWrapper queryWrapper = Wrappers.<PoItemEntity>query()
            .ne("status", STATUS_INIT)
            .ge(poItem.getLastSyncTimeStart() != null, "last_sync_time", poItem.getLastSyncTimeStart())
            .le(poItem.getLastSyncTimeEnd() != null, "last_sync_time", poItem.getLastSyncTimeEnd())
            .like(StringUtil.isNotBlank(poItem.getPrCode()), "pr_code", poItem.getPrCode())
            .like(StringUtil.isNotBlank(poItem.getPoCode()), "po_code", poItem.getPoCode())
            .like(StringUtil.isNotBlank(poItem.getSupCode()), "sup_code", poItem.getSupCode())
            .like(StringUtil.isNotBlank(poItem.getSupName()), "sup_name", poItem.getSupName())
            .like(StringUtil.isNotBlank(poItem.getItemCode()), "item_code", poItem.getItemCode())
            .like(StringUtil.isNotBlank(poItem.getItemName()), "item_name", poItem.getItemName())
            .like(StringUtil.isNotBlank(poItem.getPurchName()), "purch_name", poItem.getPurchName())
            .like(StringUtil.isNotBlank(poItem.getProNo()), "pro_no", poItem.getProNo())
            .like(StringUtil.isNotBlank(poItem.getEndUser()), "end_user", poItem.getEndUser())
            .in(StringUtil.isNotBlank(poItem.getStatuss()),"status",Arrays.asList(poItem.getStatuss().split(",")));
        IPage<PoItemEntity> entityPage = page(Condition.getPage(query), queryWrapper);*/
        List<String> statusarray = Arrays.asList(poItem.getStatuss().split(","));
        poItem.setStatusarray(statusarray);
        IPage<PoItemEntity> entityPage = this.baseMapper.getPoItemEntityPageFromOracle(Condition.getPage(query), poItem);
        IPage<PoItemVO>     retPage    = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<PoItemVO>      voList     = Lists.newArrayList();
        for(PoItemEntity entity : entityPage.getRecords()){
            PoItemVO poItemVO = PoItemWrapper.build().entityVO(entity);
            String codeType =this.baseMapper.getABCType(entity.getItemCode());
            //ABC类卡
            poItemVO.setCodeType(codeType);
            voList.add(poItemVO);
        }
        retPage.setRecords(voList);
        return retPage;
    }

    /**
     * 物料历史最高价
     * @return
     */
    @Override
    public
    BigDecimal getHighestPrice(String itemCode){
        return baseMapper.getHighestPrice(itemCode);
    }

    /**
     * 物料历史最低价
     * @return
     */
    @Override
    public
    BigDecimal getLowestPrice(String itemCode){
        return baseMapper.getLowestPrice(itemCode);
    }

    /**
     * 物料最近价
     * @return
     */
    @Override
    public
    PoItemEntity getLastPoInfos(String itemCode, String itemName){
        return baseMapper.getLastPoInfo(itemCode,itemName);
    }

    @Override
    public
    IPage<PoItemVO> pageWithPr(Query query, PoItemDTO poItemEntity){

        IPage<PoItemVO> page = null;
        String mRoleId  = paramService.getValue("purch_manager.role_id");
        String account = getUser().getAccount();
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            poItemEntity.setPurchCode(getUser().getAccount());
        }
        if(poItemEntity.getBizType()!=null && poItemEntity.getBizType().toString().equals("2")){
            poItemEntity.setIsVmi("1");
            poItemEntity.setBizType(null);
        }

        if(StringUtils.isNotEmpty(poItemEntity.getItemCode()) && poItemEntity.getItemCode().split(",").length > 1){
             page = baseMapper.pageWithPrList(Condition.getPage(query), poItemEntity);
        } else {
            page = baseMapper.pageWithPr(Condition.getPage(query), poItemEntity);
        }

        for (PoItemVO poItemVO:page.getRecords()) {

            //订单发布页面关键物料要修改 承诺交期 20230228
            //承诺交期要么是0要么是用户自己填写的
            if (!poItemVO.getItemCode().startsWith("15110")
                && !poItemVO.getItemCode().startsWith("130301")
                && !poItemVO.getItemCode().startsWith("130302")
                && !poItemVO.getItemCode().startsWith("130101")
                && !poItemVO.getItemCode().startsWith("130102")
                && !poItemVO.getItemCode().startsWith("131111")
                && !poItemVO.getItemCode().startsWith("131106")
                && poItemVO.getItemName().indexOf("锻")<0
            ) {
                continue;
            }else {
                //承诺交期为null的时候 修改承诺日期
                if(poItemVO.getPromiseDateFromQt()!=null && poItemVO.getSupConfirmDate()==null){
                    Date promiseDateFromQt = cn.hutool.core.date.DateUtil.offsetDay(poItemVO.getPromiseDateFromQt(),-25);
                    long time = promiseDateFromQt.getTime()/1000;
                    poItemVO.setSupConfirmDate(time);

                }
            }

            poItemVO.setQtPlanDate(poItemVO.getPromiseDateFromQt());





        }



        // 针对球体查询的特殊处理
        if (((poItemEntity.getSupCode() != null && !poItemEntity.getSupCode().isEmpty()) || (poItemEntity.getSupName() != null && !poItemEntity.getSupName().isEmpty() && poItemEntity.getItemName() != null)) && poItemEntity.getItemName().indexOf("球") > -1) {
            List<PoItemVO> qtFzList = this.getQTFZList(poItemEntity, page);
            page.setRecords(qtFzList);
        }
        return page;
    }

    private List<PoItemVO> getQTFZList(PoItemDTO poItemEntity, IPage<PoItemVO> page) {

        Map<String, PoItemVO> qzfzMap = new LinkedHashMap<>();
        Map<String, PoItemVO> fzMap = new LinkedHashMap<>();
        Map<String, PoItemVO> qzMap = new LinkedHashMap<>();

        // 球体的集合已经出来了
        List<PoItemVO> qtList = page.getRecords();
        // 再将所有的阀座查出来
        poItemEntity.setItemName("阀座");
        List<PoItemVO> fzList = baseMapper.fzPrList(poItemEntity);
        // 将阀座List 变为 MAP
        for(PoItemVO item : fzList) {
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfFZ(item.getItemName());
            String name1 = "阀座"; // 阀座
            String name2 = item.getItemName().split("-")[1]; // 规格
            String name3 = itemInfoEntity.getMaterial(); // 材质
            if("Monel400".equals(name3)) {
                name3 = "MonelK500";  // 3、球体材质MonelK500阀座Monel400排序在一起；
            }
            String name4 = itemInfoEntity.getFzCoat(); // 喷涂材质
            String key = name1+name2+name3+name4;
            fzMap.put(key,item);
        }

        // 处理球体,先排序球体
        boolean isExistQZ = true;
        int name6 = 0;
        for (PoItemVO dto : qtList) {

            if(!dto.getItemName().split("-")[0].equals("球体")) {
                String qzKey = "球座其他" + name6;
                qzMap.put(qzKey, dto);
                name6++;
                continue;
            }

            // 拆解
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(dto.getItemName());
            if(itemInfoEntity==null || itemInfoEntity.getMaterial()==null || itemInfoEntity.getCoat()==null) {
                String qzKey = "球座其他" + name6;
                qzMap.put(qzKey, dto);
                name6++;
                continue;
            }
            String name1 = "球座";
            String name2 = dto.getItemName().split("-")[1]; // 规格
            String name3 = itemInfoEntity.getMaterial(); // 材质
            String name4 = itemInfoEntity.getCoat(); // 喷涂材质
            if (name4.equals("G20")) {
                name4 = "G14";
            }
            String name5 = "Y";
            if (dto.getItemName().split("-")[1].indexOf("F") > -1) {
                name5 = "F";
            }
            String qzKey = name1 + name2 + name3 + name4 + name5 + "-" + name6;
            qzMap.put(qzKey, dto);
            name6++;
        }
        // 将排好序并且去重后的MAP转换成list
        Map<String, PoItemVO> sortedQzMap = qzMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (oldVal, newVal) -> oldVal,
            LinkedHashMap::new
        ));
        qtList = sortedQzMap.values().stream().collect(Collectors.toList());


        // 开始匹配 球体 和 阀座
        for (PoItemVO dto : qtList) {
            // 先在最终的MAP中插入球体数据
            String key = dto.getPrCode() + dto.getPrLn() + dto.getItemCode();
            qzfzMap.put(key, dto);

            // 开始匹配对应的阀座
            String itemName = dto.getItemName(); // 球体信息
            if (itemName.split("-").length > 2 && itemName.split("-")[0].toString().equals("球体") && itemName.split("-")[1].indexOf("F") < 0 && isExistQZ) { // 球体且不包含软密封（规格不能有 F）
                Map<String, PoItemVO> matchFzMap = this.getMatchFzInfo(itemName,fzMap);
                if(!matchFzMap.isEmpty()) {
                    for (String tempKey : matchFzMap.keySet()) {
                        // 得到与球座相匹配的阀座
                        PoItemVO qtfzPoItem = matchFzMap.get(tempKey);
                        // fz的key值： prCode + prLn + itemCode
                        String fzKey = qtfzPoItem.getPrCode() + qtfzPoItem.getPrLn() + qtfzPoItem.getItemCode();
                        // 判断最终的球座&&阀座集合里面是否已经存在，是，则移除；(为了将阀座放在球体下面，一个阀座可能对应多个球体)
                        if(qzfzMap.containsKey(fzKey)) {
                            qzfzMap.remove(fzKey);
                        }
                        // 在最终集合里面添加 阀座信息
                        qzfzMap.put(fzKey,qtfzPoItem);
                    }
                }
            }
        }
        return qzfzMap.values().stream().collect(Collectors.toList());
    }

    private Map<String, PoItemVO> getMatchFzInfo(String itemname, Map<String, PoItemVO> fzMap) {
        Map<String, PoItemVO> matchFzMap = new LinkedHashMap<>();
        // 拆解
        ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(itemname);
        String name1 = "阀座";
        String name2 = itemname.split("-")[1]; // 规格
        String name3 = itemInfoEntity.getMaterial(); // 材质
        String name4 = itemInfoEntity.getCoat(); // 喷涂材质
        String name5 = "G50";// 表面处理

        if("G20".equals(name4) ||"G14".equals(name4) ) {
            name4 = "G06";
        } else if("G06".equals(name4)) {
            name4 = "G05";
        } else {
            name4 = "";
        }
        // 键
        String key1 = name1 + name2 + name3 + name4;
        String key2 = name1 + name2 + name3 + name4 + "+" + name5;
        if(fzMap.containsKey(key1)) {
            matchFzMap.put(key1,fzMap.get(key1));
        }
        if(fzMap.containsKey(key2)) {
            matchFzMap.put(key2,fzMap.get(key2));
        }
        return matchFzMap;
    }

    private List<PoItemEntity> getQTFZListOfEntity(PoItemDTO poItemEntity, List<PoItemEntity> qtList,List<PoItemEntity> fzList) {

        Map<String, PoItemEntity> qzfzMap = new LinkedHashMap<>();
        Map<String, PoItemEntity> fzMap = new LinkedHashMap<>();
        Map<String, PoItemEntity> qzMap = new LinkedHashMap<>();

        // 将阀座List 变为 MAP
        for(PoItemEntity item : fzList) {
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfFZ(item.getItemName());
            String name1 = "阀座"; // 阀座
            String name2 = item.getItemName().split("-")[1]; // 规格
            String name3 = itemInfoEntity.getMaterial(); // 材质
            if("Monel400".equals(name3)) {
                name3 = "MonelK500";  // 3、球体材质MonelK500阀座Monel400排序在一起；
            }
            String name4 = itemInfoEntity.getFzCoat(); // 喷涂材质
            String key = name1+name2+name3+name4;
            fzMap.put(key,item);
        }

        // 处理球体,先排序球体
        boolean isExistQZ = true;
        int name6 = 0;
        for (PoItemEntity dto : qtList) {

            if(!dto.getItemName().split("-")[0].equals("球体")) {
                String qzKey = "球座其他" + name6;
                qzMap.put(qzKey, dto);
                name6++;
                continue;
            }

            // 拆解
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(dto.getItemName());
            if(itemInfoEntity==null || itemInfoEntity.getMaterial()==null || itemInfoEntity.getCoat()==null) {
                String qzKey = "球座其他" + name6;
                qzMap.put(qzKey, dto);
                name6++;
                continue;
            }
            String name1 = "球座";
            String name2 = dto.getItemName().split("-")[1]; // 规格
            String name3 = itemInfoEntity.getMaterial(); // 材质
            String name4 = itemInfoEntity.getCoat(); // 喷涂材质
            if (name4.equals("G20")) {
                name4 = "G14";
            }
            String name5 = "Y";
            if (dto.getItemName().split("-")[1].indexOf("F") > -1) {
                name5 = "F";
            }
            String qzKey = name1 + name2 + name3 + name4 + name5 + "-" + name6;
            qzMap.put(qzKey, dto);
            name6++;
        }
        // 将排好序并且去重后的MAP转换成list
        Map<String, PoItemEntity> sortedQzMap = qzMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (oldVal, newVal) -> oldVal,
            LinkedHashMap::new
        ));
        qtList = sortedQzMap.values().stream().collect(Collectors.toList());


        // 开始匹配 球体 和 阀座
        for (PoItemEntity dto : qtList) {
            // 先在最终的MAP中插入球体数据
            String key = dto.getPrCode() + dto.getPrLn() + dto.getItemCode();
            qzfzMap.put(key, dto);

            // 开始匹配对应的阀座
            String itemName = dto.getItemName(); // 球体信息
            if (itemName.split("-").length > 2 && itemName.split("-")[0].toString().equals("球体") && itemName.split("-")[1].indexOf("F") < 0 && isExistQZ) { // 球体且不包含软密封（规格不能有 F）
                Map<String, PoItemEntity> matchFzMap = this.getMatchFzInfoOfEntity(itemName,fzMap);
                if(!matchFzMap.isEmpty()) {
                    for (String tempKey : matchFzMap.keySet()) {
                        // 得到与球座相匹配的阀座
                        PoItemEntity qtfzPoItem = matchFzMap.get(tempKey);
                        // fz的key值： prCode + prLn + itemCode
                        String fzKey = qtfzPoItem.getPrCode() + qtfzPoItem.getPrLn() + qtfzPoItem.getItemCode();
                        // 判断最终的球座&&阀座集合里面是否已经存在，是，则移除；(为了将阀座放在球体下面，一个阀座可能对应多个球体)
                        if(qzfzMap.containsKey(fzKey)) {
                            qzfzMap.remove(fzKey);
                        }
                        // 在最终集合里面添加 阀座信息
                        qzfzMap.put(fzKey,qtfzPoItem);
                    }
                }
            }
        }
        return qzfzMap.values().stream().collect(Collectors.toList());
    }

    private Map<String, PoItemEntity> getMatchFzInfoOfEntity(String itemname, Map<String, PoItemEntity> fzMap) {
        Map<String, PoItemEntity> matchFzMap = new LinkedHashMap<>();
        // 拆解
        ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(itemname);
        String name1 = "阀座";
        String name2 = itemname.split("-")[1]; // 规格
        String name3 = itemInfoEntity.getMaterial(); // 材质
        String name4 = itemInfoEntity.getCoat(); // 喷涂材质
        String name5 = "G50";// 表面处理

        if("G20".equals(name4) ||"G14".equals(name4) ) {
            name4 = "G06";
        } else if("G06".equals(name4)) {
            name4 = "G05";
        } else {
            name4 = "";
        }
        // 键
        String key1 = name1 + name2 + name3 + name4;
        String key2 = name1 + name2 + name3 + name4 + "+" + name5;
        if(fzMap.containsKey(key1)) {
            matchFzMap.put(key1,fzMap.get(key1));
        }
        if(fzMap.containsKey(key2)) {
            matchFzMap.put(key2,fzMap.get(key2));
        }
        return matchFzMap;
    }

    private List<PoItemVO> getQTFZListOfVO(PoItemDTO poItemEntity, List<PoItemVO> qtList,List<PoItemVO> fzList) {

        Map<String, PoItemVO> qzfzMap = new LinkedHashMap<>();
        Map<String, PoItemVO> fzMap = new LinkedHashMap<>();
        Map<String, PoItemVO> qzMap = new LinkedHashMap<>();

        // 将阀座List 变为 MAP
        for(PoItemVO item : fzList) {
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfFZ(item.getItemName());
            String name1 = "阀座"; // 阀座
            String name2 = item.getItemName().split("-")[1]; // 规格
            String name3 = itemInfoEntity.getMaterial(); // 材质
            if("Monel400".equals(name3)) {
                name3 = "MonelK500";  // 3、球体材质MonelK500阀座Monel400排序在一起；
            }
            String name4 = itemInfoEntity.getFzCoat(); // 喷涂材质
            String key = name1+name2+name3+name4;
            fzMap.put(key,item);
        }

        // 处理球体,先排序球体
        boolean isExistQZ = true;
        int name6 = 0;
        for (PoItemVO dto : qtList) {

            if(!dto.getItemName().split("-")[0].equals("球体")) {
                String qzKey = "球座其他" + name6;
                qzMap.put(qzKey, dto);
                name6++;
                continue;
            }

            // 拆解
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(dto.getItemName());
            if(itemInfoEntity==null || itemInfoEntity.getMaterial()==null || itemInfoEntity.getCoat()==null) {
                String qzKey = "球座其他" + name6;
                qzMap.put(qzKey, dto);
                name6++;
                continue;
            }

            String name1 = "球座";
            String name2 = dto.getItemName().split("-")[1]; // 规格
            String name3 = itemInfoEntity.getMaterial(); // 材质
            String name4 = itemInfoEntity.getCoat(); // 喷涂材质

            if (name4.equals("G20")) {
                name4 = "G14";
            }
            String name5 = "Y";
            if (dto.getItemName().split("-")[1].indexOf("F") > -1) {
                name5 = "F";
            }
            String qzKey = name1 + name2 + name3 + name4 + name5 + "-" + name6;
            qzMap.put(qzKey, dto);
            name6++;
        }
        // 将排好序并且去重后的MAP转换成list
        Map<String, PoItemVO> sortedQzMap = qzMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (oldVal, newVal) -> oldVal,
            LinkedHashMap::new
        ));
        qtList = sortedQzMap.values().stream().collect(Collectors.toList());


        // 开始匹配 球体 和 阀座
        for (PoItemVO dto : qtList) {
            // 先在最终的MAP中插入球体数据
            String key = dto.getPrCode() + dto.getPrLn() + dto.getItemCode();
            qzfzMap.put(key, dto);

            // 开始匹配对应的阀座
            String itemName = dto.getItemName(); // 球体信息
            if (itemName.split("-").length > 2 && itemName.split("-")[0].toString().equals("球体") && itemName.split("-")[1].indexOf("F") < 0 && isExistQZ) { // 球体且不包含软密封（规格不能有 F）
                Map<String, PoItemVO> matchFzMap = this.getMatchFzInfoOfVO(itemName,fzMap);
                if(!matchFzMap.isEmpty()) {
                    for (String tempKey : matchFzMap.keySet()) {
                        // 得到与球座相匹配的阀座
                        PoItemVO qtfzPoItem = matchFzMap.get(tempKey);
                        // fz的key值： prCode + prLn + itemCode
                        String fzKey = qtfzPoItem.getPrCode() + qtfzPoItem.getPrLn() + qtfzPoItem.getItemCode();
                        // 判断最终的球座&&阀座集合里面是否已经存在，是，则移除；(为了将阀座放在球体下面，一个阀座可能对应多个球体)
                        if(qzfzMap.containsKey(fzKey)) {
                            qzfzMap.remove(fzKey);
                        }
                        // 在最终集合里面添加 阀座信息
                        qzfzMap.put(fzKey,qtfzPoItem);
                    }
                }
            }
        }
        return qzfzMap.values().stream().collect(Collectors.toList());
    }

    private Map<String, PoItemVO> getMatchFzInfoOfVO(String itemname, Map<String, PoItemVO> fzMap) {
        Map<String, PoItemVO> matchFzMap = new LinkedHashMap<>();
        // 拆解
        ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(itemname);
        String name1 = "阀座";
        String name2 = itemname.split("-")[1]; // 规格
        String name3 = itemInfoEntity.getMaterial(); // 材质
        String name4 = itemInfoEntity.getCoat(); // 喷涂材质
        String name5 = "G50";// 表面处理

        if("G20".equals(name4) ||"G14".equals(name4) ) {
            name4 = "G06";
        } else if("G06".equals(name4)) {
            name4 = "G05";
        } else {
            name4 = "";
        }
        // 键
        String key1 = name1 + name2 + name3 + name4;
        String key2 = name1 + name2 + name3 + name4 + "+" + name5;
        if(fzMap.containsKey(key1)) {
            matchFzMap.put(key1,fzMap.get(key1));
        }
        if(fzMap.containsKey(key2)) {
            matchFzMap.put(key2,fzMap.get(key2));
        }
        return matchFzMap;
    }

    @Override
    public
    IPage<PoItemVO> pageWithItemPoContract(Query query, PoItemDTO poItemEntity){
        IPage<PoItemVO> voiPage = baseMapper.pageWithItemPoContract(Condition.getPage(query), poItemEntity);
        List<PoItemVO> poItemDTOList = voiPage.getRecords();
        boolean isQT = false;
        boolean isFz = false;
        for(PoItemVO item : poItemDTOList) {
            if(item.getItemName().indexOf("球体")>-1) {
                isQT = true;
            }
            if (item.getItemName().indexOf("阀座")>-1) {
                isFz = true;
            }
        }
        // 订单里面又有球体又有阀座，则需要重新排序
        if(isQT && isFz) {
            List<PoItemVO> list = this.getQTFZListOfOrder(poItemDTOList);
            voiPage.setRecords(list);
        }

        return voiPage;
    }

    /**
     * 订单查询的球座排序
     * @param poItemDTOList
     * @return
     */
    private List<PoItemVO> getQTFZListOfOrder(List<PoItemVO> poItemDTOList) {

        Map<String, PoItemVO> qzfzMap = new LinkedHashMap<>();
        Map<String, PoItemVO> fzMap = new LinkedHashMap<>();
        Map<String, PoItemVO> qtMap = new LinkedHashMap<>();
        Map<String, PoItemVO> othersMap = new LinkedHashMap<>();

        List<PoItemVO> finalAllList = new ArrayList<>();
        List<PoItemVO> qtList = new ArrayList<>();
        List<PoItemVO> fzList = new ArrayList<>();

        // 将 球体、阀座、其他分开
        for (PoItemVO item : poItemDTOList) {
            if (item.getItemName().split("-")[0].equals("球体")) {
                if (item.getItemName().split("-").length > 2 && item.getItemName().split("-")[1].indexOf("F") < 0) { // 是球体还不够，还得是Y密封
                    qtList.add(item);
                } else {
                    finalAllList.add(item);
                }
            } else if (item.getItemName().split("-")[0].equals("阀座")) {
                fzList.add(item);
            } else {
                finalAllList.add(item);
            }
        }

        // 将 阀座 List 变为 MAP
        for (PoItemVO item : fzList) {
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfFZ(item.getItemName());
            String name1 = "阀座"; // 阀座
            String name2 = item.getItemName().split("-")[1]; // 规格
            String name3 = itemInfoEntity.getMaterial(); // 材质
            if ("Monel400".equals(name3)) {
                name3 = "MonelK500";  // 3、球体材质MonelK500阀座Monel400排序在一起；
            }
            String name4 = itemInfoEntity.getFzCoat(); // 喷涂材质
            String key = name1 + name2 + name3 + name4;
            fzMap.put(key, item);
        }

        // 处理其他因素,先排序球体
        boolean isExistQZ = true;
        int name6 = 0;
        for (PoItemVO dto : qtList) {
            if(!dto.getItemName().split("-")[0].equals("球体")) {
                String qzKey = "球座其他" + name6;
                qtMap.put(qzKey, dto);
                name6++;
                continue;
            }
            // 拆解
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(dto.getItemName());
            String name1 = "球座";
            String name2 = dto.getItemName().split("-")[1]; // 规格
            String name3 = itemInfoEntity.getMaterial(); // 材质
            String name4 = itemInfoEntity.getCoat(); // 喷涂材质
            if (name4.equals("G20")) {
                name4 = "G14";
            }
            String name5 = "Y";
            if (dto.getItemName().split("-")[1].indexOf("F") > -1) {
                name5 = "F";
            }
            String qzKey = name1 + name2 + name3 + name4 + name5 + "-" + name6;
            qtMap.put(qzKey, dto);
            name6++;
        }
        // 将排好序并且去重后的MAP转换成list
        Map<String, PoItemVO> sortedQzMap = qtMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (oldVal, newVal) -> oldVal,
            LinkedHashMap::new
        ));
        qtList = sortedQzMap.values().stream().collect(Collectors.toList());

        // 开始匹配 球体 和 阀座
        for (PoItemVO dto : qtList) {
            // 先在最终的MAP中插入球体数据
            String key = dto.getPrCode() + dto.getPrLn() + dto.getItemCode();
            qzfzMap.put(key, dto);

            // 开始匹配对应的阀座
            String itemName = dto.getItemName(); // 球体信息
            if (itemName.split("-").length > 2 && itemName.split("-")[0].toString().equals("球体") && itemName.split("-")[1].indexOf("F") < 0 && isExistQZ) { // 球体且不包含软密封（规格不能有 F）
                Map<String, PoItemVO> matchFzMap = this.getMatchFzInfo(itemName, fzMap);
                if (!matchFzMap.isEmpty()) {
                    for (String tempKey : matchFzMap.keySet()) {
                        // 得到与球座相匹配的阀座
                        PoItemVO qtfzPoItem = matchFzMap.get(tempKey);
                        // fz的key值： prCode + prLn + itemCode
                        String fzKey = qtfzPoItem.getPrCode() + qtfzPoItem.getPrLn() + qtfzPoItem.getItemCode();
                        // 判断最终的球座&&阀座集合里面是否已经存在，是，则移除；(为了将阀座放在球体下面，一个阀座可能对应多个球体)
                        if (qzfzMap.containsKey(fzKey)) {
                            qzfzMap.remove(fzKey);
                        }
                        // 在最终集合里面添加 阀座信息
                        qzfzMap.put(fzKey, qtfzPoItem);
                    }
                }
            }
        }

        // 匹配完阀座之后，若还有剩余的阀座，则需要重新安排进入最终的list
        for (String tempKey : fzMap.keySet()) {
            // 得到与球座相匹配的阀座
            PoItemVO qtfzPoItem = fzMap.get(tempKey);
            // fz的key值： prCode + prLn + itemCode
            String fzKey = qtfzPoItem.getPrCode() + qtfzPoItem.getPrLn() + qtfzPoItem.getItemCode();
            // 判断该阀座是否存在于 球体阀座组合map 中，若不存在，则添加在最终list
            if (!qzfzMap.containsKey(fzKey)) {
                finalAllList.add(fzMap.get(tempKey));
            }
        }

        List<PoItemVO> qtfzList = qzfzMap.values().stream().collect(Collectors.toList());

        finalAllList.addAll(qtfzList);

        return finalAllList;
    }


    /**
     * @return columnValues:[
     * {
     * "date"         :"2020-05-01",
     * "title"        :"5/1",
     * "qty"          :10,
     * "isMeetOptDate":0
     * },
     * {
     * "date"         :"2020-05-02",
     * "title"        :"5/1",
     * "qty"          :10,
     * "isMeetOptDate":1
     * },
     * {
     * "date"         :"2020-06",
     * "title"        :"6月",
     * "qty"          :10,
     * "isMeetOptDate":1
     * }
     * ]
     */
    @Override
    public
    IPage<PoItemReqRepotVO> getReqRepotPage(Query query, PoItemEntity poItemEntity){
        IPage<PoItemReqRepotTotal> totalPage = this.baseMapper.getPoItemReqRepotTotal(Condition.getPage(query), poItemEntity);
        List<PoItemReqRepotVO>     voList    = getVoList(totalPage.getRecords());
        IPage<PoItemReqRepotVO> newPage = new Page<>(totalPage.getCurrent(), totalPage.getSize(), totalPage.getTotal());
        return newPage.setRecords(voList);
    }

    @Override
    public IPage<PoItemNewReportVO> newReportPage(Query query, PoItemEntity poItemEntity) {
        IPage<PoItemReqRepotTotal> totalPage = this.baseMapper.getNewReportTotal(Condition.getPage(query), poItemEntity);
        List<PoItemNewReportVO> voList = getNewVoList(totalPage.getRecords());
        IPage<PoItemNewReportVO> newPage = new Page<>(totalPage.getCurrent(), totalPage.getSize(), totalPage.getTotal());
        return newPage.setRecords(voList);
    }

    @Override
    public IPage<PoItemNewReportVO> newReportPage2(Query query, PoItemEntity poItemEntity) {
        IPage<PoItemReqRepotTotal2> totalPage = this.baseMapper.getNewReportTotal2(Condition.getPage(query), poItemEntity);
        List<PoItemNewReportVO> voList = getNewVoList2ForExcel(totalPage.getRecords());
        IPage<PoItemNewReportVO> newPage = new Page<>(totalPage.getCurrent(), totalPage.getSize(), totalPage.getTotal());
        return newPage.setRecords(voList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean planreqreportsendEmail(List<String> poCodeAndLnAndSupCodes) {
        boolean                    result               = true;
        Map<String,List<ApsReportExdevEntity>> map = new HashMap<>();
        for (String poCodeAndLnAndSupCode : poCodeAndLnAndSupCodes) {
            String[] split = poCodeAndLnAndSupCode.split(",");
            List<ApsReportExdevEntity> searchList = iApsReportExdevService.getByPoCodeAndLns(split[0],Integer.parseInt(split[1]));
            if(searchList.size() > 0){
                List<ApsReportExdevEntity> list = map.get(split[2]);
                if(list == null){
                    list = searchList;
                }else{
                    list.addAll(searchList);
                }
                map.put(split[2],list);
            }
        }
        Set<String> sets = map.keySet();
        BladeUser    user         = SecureUtil.getUser();
        User bladeUser    = userService.getById(user.getUserId());
        for (String set : sets) {
            List<ApsReportExdevEntity> keyList = map.get(set);
            // 查询当前供应商邮箱
            Supplier supplier = supplierService.getByCode(set);
            if(supplier != null){
                String email = supplier.getEmail();
                //邮件提醒
                QueueEmailEntity queueEmailEntity = new QueueEmailEntity();


                String desc0 = supplier.getName();
                String desc1 = "您好！";
                String desc = "以下物料是贵公司最近应交货的清单，请确认是否能按要求交货，并在相应的地方做回复，如不能按订单数量交清，必须保证我的需求数量，收到邮件后请在当天下班前回复完成，你们的回复及时率也将纳入一个考核指标,指标将影响贵公司的等绩评定。";
                StringBuilder content = new StringBuilder("<html>" +
                    "<head></head>" +
                    "<style type=\"text/css\">" +
                    "table {border-collapse:collapse;border:1px solid #000000} td,th{border:1px solid #000000;}" +
                    "</style>"+
                    "<body>" +
                    "<p>"+ desc0 +"</p>" +
                    "<p>"+ desc1 +"</p>" +
                    "<p style=\"text-indent:2em;\">"+ desc +"</p>");
                content.append("<table  style=\"font-size:15px;width:85%\">");

                content.append("<tr style=\"background-color:#428BCA;color:#000000;font-family:宋体;font-size:13.5px;height:25px;\">" +
                    "<th style=\"width:100px\"></th>" +
                    "<th style=\"width:150px\"></th>" +
                    "<th style=\"width:450px\"></th>" +
                    "<th style=\"width:80px\"></th>" +
                    "<th style=\"width:120px\"></th>" +
                    "<th style=\"width:150px\"></th>" +
                    "<th colspan=\"2\" style=\"width:150px\">厂家回复交期</th>" +
                    "</tr>");

                content.append("<tr style=\"background-color:#428BCA;color:#000000;font-family:宋体;font-size:13.5px;height:25px;\">" +
                    "<th style=\"width:100px\">订单编号</th>" +
                    "<th style=\"width:150px\">物料编码</th>" +
                    "<th style=\"width:450px\">物料名称</th>" +
                    "<th style=\"width:80px\">订单数量</th>" +
                    "<th style=\"width:120px\">项目需求数量</th>" +
                    "<th style=\"width:150px\">要求到厂时间</th>" +
                    "<th style=\"width:150px\">能按时到场</th>" +
                    "<th style=\"width:150px\">修改到厂时间</th>" +
                    "</tr>");
                for (ApsReportExdevEntity apsReportExdevEntity : keyList) {
                    Long deliveryDate = apsReportExdevEntity.getDeliveryDate();
                    String showDeliveryDate = WillDateUtil.unixTimeToStr(deliveryDate, "yyyy-MM-dd");

                    content.append("<tr style=\"font-family:宋体;font-size:13.5px;height:25px\">");
                    content.append("<td align=\"center\">"+ apsReportExdevEntity.getPoCode() +"</td>");
                    content.append("<td align=\"center\">"+ apsReportExdevEntity. getItemCode()+"</td>");
                    content.append("<td align=\"center\">"+ apsReportExdevEntity.getItemName() +"</td>");
                    content.append("<td align=\"center\">"+ apsReportExdevEntity.getTcNum().intValue() +"</td>");
                    content.append("<td align=\"center\">"+ apsReportExdevEntity.getProReqNum().intValue() +"</td>");
                    content.append("<td align=\"center\">"+ showDeliveryDate +"</td>");
                    content.append("<td align=\"center\"></td>");
                    content.append("<td align=\"center\"></td>");
                    content.append("</tr>");
                }

                content.append("</table>");
                content.append("</body></html>");

                queueEmailEntity.setSender(bladeUser.getEmail());
                queueEmailEntity.setReceiver(email + ";All_Purchase@antiwearvalve.com");

                queueEmailEntity.setSubject("最近应交货清单");
                queueEmailEntity.setContent(content.toString());

                queueEmailEntity.setSendCount(0);
                queueEmailEntity.setStatus(IQueueEmailService.STATUS_INIT);
                boolean current = queueEmailService.save(queueEmailEntity);
                if(!current){
                    result = false;
                }

            }
        }
        return result;

    }




    /**
     * getNewVoList
     *
     * @param totalList List
     * @return List
     */
    @Override
    public List<PoItemNewReportVO> getNewVoList(List<PoItemReqRepotTotal> totalList) {
        List<PoItemNewReportVO> voList = Lists.newArrayList();

        Map<String,PoItemReqRepotTotal> poItemReqRepotTotalMap = new HashMap<>();
        for (PoItemReqRepotTotal poItemReqRepotTotal : totalList) {
            String supCode = poItemReqRepotTotal.getSupCode();
            String itemCode = poItemReqRepotTotal.getItemCode();
            String key = supCode + itemCode;
            poItemReqRepotTotalMap.put(key,poItemReqRepotTotal);
        }

        // 查询全部PoItemNewReportVO，PoItemNewReportVO
        List<PoItemNewReportVO> poItemNewReportVos = this.baseMapper.getActualVos();
        List<PoItemNewReportVO> predictColumnVos = this.baseMapper.getPredictVos();

        Map<String,Map<String,Object>> map = new HashMap<>();// 用不同的sup_code + item_code作为key记录 PoItemNewReportVO，PoItemNewReportVO的list
        for (PoItemNewReportVO poItemNewReportVo : poItemNewReportVos) {
            String supCode  = poItemNewReportVo.getSupCode() == null ? "" : poItemNewReportVo.getSupCode();
            String itemCode = poItemNewReportVo.getItemCode() == null ? "" : poItemNewReportVo.getItemCode();
            String key = supCode + itemCode;
            PoItemReqRepotTotal poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }
            if(map.get(key) == null){ // 不存在
                Map<String,Object> subMap = new HashMap();
                subMap.put("Actual",poItemNewReportVo);
                map.put(key,subMap);
            }else{ // 存在
                continue;
            }
        }

        for (PoItemNewReportVO predictColumnVo : predictColumnVos) {
            String supCode = predictColumnVo.getSupCode() == null ? "" : predictColumnVo.getSupCode();
            String itemCode = predictColumnVo.getItemCode() == null ? "" : predictColumnVo.getItemCode();
            String key = supCode + itemCode;
            PoItemReqRepotTotal poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }
            if(map.get(key) == null){ // 不存在
                Map<String,Object> subMap = new HashMap();
                subMap.put("Predict",predictColumnVo);
                map.put(key,subMap);
            }else{ // 存在
                Map<String, Object> subMap = map.get(key);
                PoItemNewReportVO reportVo = (PoItemNewReportVO) subMap.get("Predict");
                if(reportVo == null){
                    subMap.put("Predict",predictColumnVo);
                }else{
                    continue;
                }
            }
        }
        /***************************************/
        // 先查询所有料号下的日期
        List<PoItemReqRepotCurrMonthDTO> actualColumnDtos = this.baseMapper.getActualColumnValues();
        Map <String,Map<String,PoItemReqRepotCurrMonthDTO>> actualColumnDtosMap = new HashMap<>();
        for (PoItemReqRepotCurrMonthDTO actualColumnDto : actualColumnDtos) {
            String supCode = actualColumnDto.getSupCode() == null ? "" : actualColumnDto.getSupCode();
            String itemCode = actualColumnDto.getItemCode() == null ? "" : actualColumnDto.getItemCode();
            String date = actualColumnDto.getDate();
            String key = supCode + itemCode;

            PoItemReqRepotTotal poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }

            Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
            if(actualColumnDtosSubMap == null){
                actualColumnDtosSubMap = new HashMap<>();
                actualColumnDtosSubMap.put(date,actualColumnDto);
                actualColumnDtosMap.put(key,actualColumnDtosSubMap);
            }else{
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthFastDTO = actualColumnDtosSubMap.get(date);
                if(poItemReqRepotCurrMonthFastDTO == null){
                    actualColumnDtosSubMap.put(date,actualColumnDto);
                }
            }

        }

        List<PoItemReqRepotCurrMonthDTO > predictColumnDtos = this.baseMapper.getPredictColumnValues();
        Map <String,Map<String,PoItemReqRepotCurrMonthDTO>> predictColumnDtosMap = new HashMap<>();
        for (PoItemReqRepotCurrMonthDTO predictColumnDto : predictColumnDtos) {
            String supCode = predictColumnDto.getSupCode() == null ? "" : predictColumnDto.getSupCode();
            String itemCode = predictColumnDto.getItemCode() == null ? "" : predictColumnDto.getItemCode();
            String date = predictColumnDto.getDate();
            String key = supCode + itemCode;

            PoItemReqRepotTotal poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }

            Map<String, PoItemReqRepotCurrMonthDTO> predictColumnDtosSubMap = predictColumnDtosMap.get(key);
            if(predictColumnDtosSubMap == null){
                predictColumnDtosSubMap = new HashMap<>();
                predictColumnDtosSubMap.put(date,predictColumnDto);
                predictColumnDtosMap.put(key,predictColumnDtosSubMap);
            }else{
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthFastDTO = predictColumnDtosSubMap.get(date);
                if(poItemReqRepotCurrMonthFastDTO == null){
                    predictColumnDtosSubMap.put(date,predictColumnDto);
                }
            }
        }


        List<PoItemReqRepotCurrMonthDTO> actualNextColumnDtos = this.baseMapper.getActualNextColumnValues();
        Map <String,Map<String,PoItemReqRepotCurrMonthDTO>> actualNextColumnDtosMap = new HashMap<>();
        for (PoItemReqRepotCurrMonthDTO actualNextColumnDto : actualNextColumnDtos) {
            String supCode = actualNextColumnDto.getSupCode() == null ? "" : actualNextColumnDto.getSupCode();
            String itemCode = actualNextColumnDto.getItemCode() == null ? "" : actualNextColumnDto.getItemCode();
            String date = actualNextColumnDto.getDate();
            String key = supCode + itemCode;

            PoItemReqRepotTotal poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }

            Map<String, PoItemReqRepotCurrMonthDTO> actualNextColumnDtosSubMap = actualNextColumnDtosMap.get(key);
            if(actualNextColumnDtosSubMap == null){
                actualNextColumnDtosSubMap = new HashMap<>();
                actualNextColumnDtosSubMap.put(date,actualNextColumnDto);
                actualNextColumnDtosMap.put(key,actualNextColumnDtosSubMap);
            }else{
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthFastDTO = actualNextColumnDtosSubMap.get(date);
                if(poItemReqRepotCurrMonthFastDTO == null){
                    actualNextColumnDtosSubMap.put(date,actualNextColumnDto);
                }
            }
        }




        List<PoItemReqRepotCurrMonthDTO> predictNextColumnDtos = this.baseMapper.getPredictNextColumnValues();
        Map <String,Map<String,PoItemReqRepotCurrMonthDTO>> predictNextColumnDtosMap = new HashMap<>();
        for (PoItemReqRepotCurrMonthDTO predictNextColumnDto : predictNextColumnDtos) {
            String supCode = predictNextColumnDto.getSupCode() == null ? "" : predictNextColumnDto.getSupCode();
            String itemCode = predictNextColumnDto.getItemCode() == null ? "" : predictNextColumnDto.getItemCode();
            String date = predictNextColumnDto.getDate();
            String key = supCode + itemCode;

            PoItemReqRepotTotal poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }

            Map<String, PoItemReqRepotCurrMonthDTO> predictNextColumnDtosSubMap = predictNextColumnDtosMap.get(key);
            if(predictNextColumnDtosSubMap == null){
                predictNextColumnDtosSubMap = new HashMap<>();
                predictNextColumnDtosSubMap.put(date,predictNextColumnDto);
                predictNextColumnDtosMap.put(key,predictNextColumnDtosSubMap);
            }else{
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthFastDTO = predictNextColumnDtosSubMap.get(date);
                if(poItemReqRepotCurrMonthFastDTO == null){
                    predictNextColumnDtosSubMap.put(date,predictNextColumnDto);
                }
            }
        }
        /***************************************/



        Set<String> keySets = map.keySet();
        for (String key : keySets) {
            PoItemReqRepotTotal poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }
            Map<String, Object> subMap = map.get(key);
            //实际需求
            //获取项目号+订单号+总数量
            PoItemNewReportVO actualColumnVo = (PoItemNewReportVO) subMap.get("Actual");
            if(actualColumnVo == null){
                actualColumnVo = new PoItemNewReportVO();
                actualColumnVo.setSupCode(poItemReqRepotTotal.getSupCode());
                actualColumnVo.setSupName(poItemReqRepotTotal.getSupName());
                actualColumnVo.setItemCode(poItemReqRepotTotal.getItemCode());
                actualColumnVo.setItemName(poItemReqRepotTotal.getItemName());
            }
            actualColumnVo.setType("实际需求");

            //预测需求
            //获取项目号+订单号+总数量
            PoItemNewReportVO predictColumnVo = (PoItemNewReportVO) subMap.get("Predict");
            if(predictColumnVo == null){
                predictColumnVo = new PoItemNewReportVO();
                predictColumnVo.setSupCode(poItemReqRepotTotal.getSupCode());
                predictColumnVo.setSupName(poItemReqRepotTotal.getSupName());
                predictColumnVo.setItemCode(poItemReqRepotTotal.getItemCode());
                predictColumnVo.setItemName(poItemReqRepotTotal.getItemName());
            }
            predictColumnVo.setType("预测需求");

            /*************/
            List<PoItemReqRepotCurrMonthDTO> actualList = Lists.newArrayList();
            List<PoItemReqRepotCurrMonthDTO> predictList = Lists.newArrayList();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int monthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int today = calendar.get(Calendar.DATE);
            calendar.setTime(new Date());
            int actualDelayQties = 0;
            int predictDelayQties = 0;
            if(today < 20){
                for (int i = monthDay; i >= 1; i--) { // 之前逻辑today 改为1号开始取值
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(), fullDate);
                            actualColumnDto.setIsMeetOptDate(actualIsMeetDto != null ? 0 : 1);
                            if(actualColumnDto.getIsMeetOptDate().equals(0) && i < today){
                                actualDelayQties += actualIsMeetDto.getQty();
                            }
                        } else {
                            actualColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        actualList.add(actualColumnDto);
                    }

                    Map<String, PoItemReqRepotCurrMonthDTO> predictColumnDtosSubMap = predictColumnDtosMap.get(key);
                    if(predictColumnDtosSubMap != null){
                        PoItemReqRepotCurrMonthDTO predictColumnDto = predictColumnDtosSubMap.get(fullDate);
                        if (predictColumnDto == null) {
                            predictColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }else{
                            if(i < today){
                                predictDelayQties += predictColumnDto.getQty();
                            }
                        }
                        predictList.add(predictColumnDto);
                    }

                }
            }else{
                // 统计1号到今天前一天的延期数量之和
                for (int i = 1; i < today; i++) { // 改为20号开始取值
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(), fullDate);
                            actualDelayQties += actualIsMeetDto.getQty();
                        }
                    }
                    Map<String, PoItemReqRepotCurrMonthDTO> predictColumnDtosSubMap = predictColumnDtosMap.get(key);
                    if(predictColumnDtosSubMap != null){
                        PoItemReqRepotCurrMonthDTO predictColumnDto = predictColumnDtosSubMap.get(fullDate);
                        if (predictColumnDto == null) {
                            predictColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }else{
                            predictDelayQties += predictColumnDto.getQty();
                        }
                    }
                }
                // 先到当前月底
                for (int i = monthDay; i >= 20; i--) { // 改为20号开始取值
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(), fullDate);
                            actualColumnDto.setIsMeetOptDate(actualIsMeetDto != null ? 0 : 1);
                        } else {
                            actualColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        actualList.add(actualColumnDto);
                    }

                    Map<String, PoItemReqRepotCurrMonthDTO> predictColumnDtosSubMap = predictColumnDtosMap.get(key);
                    if(predictColumnDtosSubMap != null){
                        PoItemReqRepotCurrMonthDTO predictColumnDto = predictColumnDtosSubMap.get(fullDate);
                        if (predictColumnDto == null) {
                            predictColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        predictList.add(predictColumnDto);
                    }

                }
                // 下个月整月都取值
                calendar.add(Calendar.MONTH, +1);
                int nextMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = nextMonthDay; i >= 1; i--) {
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(), fullDate);
                            actualColumnDto.setIsMeetOptDate(actualIsMeetDto != null ? 0 : 1);
                        } else {
                            actualColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        actualList.add(actualColumnDto);
                    }

                    Map<String, PoItemReqRepotCurrMonthDTO> predictColumnDtosSubMap = predictColumnDtosMap.get(key);
                    if(predictColumnDtosSubMap != null){
                        PoItemReqRepotCurrMonthDTO predictColumnDto = predictColumnDtosSubMap.get(fullDate);
                        if (predictColumnDto == null) {
                            predictColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        predictList.add(predictColumnDto);
                    }
                }
            }


            //15号之后：当前月最后一天--下个月最后一天
            /*if (today > 15) {
                //倒序日期
                List<PoItemReqRepotCurrMonthDTO> actualSortList = Lists.newArrayList();
                actualSortList.addAll(actualList);
                actualList.removeAll(actualSortList);
                List<PoItemReqRepotCurrMonthDTO> predictSortList = Lists.newArrayList();
                predictSortList.addAll(predictList);
                predictList.removeAll(predictSortList);

                calendar.add(Calendar.MONTH, +1);
                int nextMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = nextMonthDay; i >= 1; i--) {
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(), fullDate);
                            actualColumnDto.setIsMeetOptDate(actualIsMeetDto != null ? 0 : 1);
                        } else {
                            actualColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        actualList.add(actualColumnDto);
                    }

                    Map<String, PoItemReqRepotCurrMonthDTO> predictColumnDtosSubMap = predictColumnDtosMap.get(key);
                    if(predictColumnDtosSubMap != null){
                        PoItemReqRepotCurrMonthDTO predictColumnDto = predictColumnDtosSubMap.get(fullDate);
                        if (predictColumnDto == null) {
                            predictColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        predictList.add(predictColumnDto);
                    }
                }
                actualList.addAll(actualSortList);
                predictList.addAll(predictSortList);
            }*/

            //下半年 (如果20号之前 +1 - +6，否则 +2 - +7)
            for (int i = calendar.get(Calendar.MONTH) + 7; i >= calendar.get(Calendar.MONTH) + 2; i--) {
                String timeCurr;
                switch (i) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        timeCurr = calendar.get(Calendar.YEAR) + "-0" + i;
                        break;
                    case 10:
                    case 11:
                    case 12:
                        timeCurr = calendar.get(Calendar.YEAR) + "-" + i;
                        break;
                    default:
                        timeCurr = (calendar.get(Calendar.YEAR) + 1) + "-0" + (i - 12);
                        break;
                }
                Map<String, PoItemReqRepotCurrMonthDTO> actualNextColumnDtosSubMap = actualNextColumnDtosMap.get(key);
                if(actualNextColumnDtosSubMap != null){
                    PoItemReqRepotCurrMonthDTO actualNextColumnDto = actualNextColumnDtosSubMap.get(timeCurr);
                    if (actualNextColumnDto == null) {
                        actualNextColumnDto = new PoItemReqRepotCurrMonthDTO(timeCurr);
                    }
                    actualList.add(actualNextColumnDto);
                }


                Map<String, PoItemReqRepotCurrMonthDTO> predictNextColumnDtosSubMap = predictNextColumnDtosMap.get(key);
                if(predictNextColumnDtosSubMap != null){
                    PoItemReqRepotCurrMonthDTO predictNextColumnDto = predictNextColumnDtosSubMap.get(timeCurr);
                    if (predictNextColumnDto == null) {
                        predictNextColumnDto = new PoItemReqRepotCurrMonthDTO(timeCurr);
                    }
                    String date = predictNextColumnDto.getDate();
                    if(date != null){
                        predictNextColumnDto.setDate(date.replace("/","-"));
                    }
                    predictList.add(predictNextColumnDto);
                }

            }

            actualColumnVo.setColumnValues(actualList);
            actualColumnVo.setDelayQties(actualDelayQties);
            predictColumnVo.setColumnValues(predictList);
            predictColumnVo.setDelayQties(predictDelayQties);
            /**************/

            //后推至下个月底或者本月底
            // getDateValue(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(), actualColumnVo, predictColumnVo);

            voList.add(actualColumnVo);
            voList.add(predictColumnVo);
        }
        return voList;
    }


    /**
     * getNewVoList2
     *
     * @param totalList List
     * @return List
     */
    public List<PoItemNewReportVO> getNewVoList2(List<PoItemReqRepotTotal2> totalList) {
        List<PoItemNewReportVO> voList = Lists.newArrayList();

        Map<String,PoItemReqRepotTotal2> poItemReqRepotTotalMap = new HashMap<>();
        for (PoItemReqRepotTotal2 poItemReqRepotTotal : totalList) {
            String supCode = poItemReqRepotTotal.getSupCode();
            String itemCode = poItemReqRepotTotal.getItemCode();
            String poCode = poItemReqRepotTotal.getPoCode();
            Integer poLn = poItemReqRepotTotal.getPoLn();
            String proNo = poItemReqRepotTotal.getProNo();
            String key = supCode + itemCode + poCode + poLn + proNo;
            poItemReqRepotTotalMap.put(key,poItemReqRepotTotal);
        }

        // 查询全部PoItemNewReportVO，PoItemNewReportVO
        List<PoItemNewReportVO> poItemNewReportVos = this.baseMapper.getActualVos2();
        List<PoItemNewReportVO> predictColumnVos = new ArrayList<>();

        Map<String,Map<String,Object>> map = new HashMap<>();// 用不key记录 PoItemNewReportVO，PoItemNewReportVO的list
        for (PoItemNewReportVO poItemNewReportVo : poItemNewReportVos) {
            String supCode  = poItemNewReportVo.getSupCode() == null ? "" : poItemNewReportVo.getSupCode();
            String itemCode = poItemNewReportVo.getItemCode() == null ? "" : poItemNewReportVo.getItemCode();
            String poCode = poItemNewReportVo.getPoCode() == null ? "" : poItemNewReportVo.getPoCode();
            String poLn = poItemNewReportVo.getPoLn() == null ? "" : poItemNewReportVo.getPoLn().toString();
            String proNo = poItemNewReportVo.getProNo() == null ? "" : poItemNewReportVo.getProNo();

            String key = supCode + itemCode + poCode + poLn + proNo;
            PoItemReqRepotTotal2 poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }
            if(map.get(key) == null){ // 不存在
                Map<String,Object> subMap = new HashMap();
                subMap.put("Actual",poItemNewReportVo);
                map.put(key,subMap);
            }else{ // 存在
                continue;
            }
        }


        for (PoItemNewReportVO predictColumnVo : predictColumnVos) {
            String supCode  = predictColumnVo.getSupCode() == null ? "" : predictColumnVo.getSupCode();
            String itemCode = predictColumnVo.getItemCode() == null ? "" : predictColumnVo.getItemCode();
            String poCode = predictColumnVo.getPoCode() == null ? "" : predictColumnVo.getPoCode();
            String poLn = predictColumnVo.getPoLn() == null ? "" : predictColumnVo.getPoLn().toString();
            String proNo = predictColumnVo.getProNo() == null ? "" : predictColumnVo.getProNo();

            String key = supCode + itemCode + poCode + poLn + proNo;
            PoItemReqRepotTotal2 poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }
            if(map.get(key) == null){ // 不存在
                Map<String,Object> subMap = new HashMap();
                subMap.put("Predict",predictColumnVo);
                map.put(key,subMap);
            }else{ // 存在
                Map<String, Object> subMap = map.get(key);
                PoItemNewReportVO reportVo = (PoItemNewReportVO) subMap.get("Predict");
                if(reportVo == null){
                    subMap.put("Predict",predictColumnVo);
                }else{
                    continue;
                }
            }
        }

        /***************************************/
        // 先查询所有料号下的日期
        List<PoItemReqRepotCurrMonthDTO> actualColumnDtos = this.baseMapper.getActualColumnValues2();
        Map <String,Map<String,PoItemReqRepotCurrMonthDTO>> actualColumnDtosMap = new HashMap<>();
        for (PoItemReqRepotCurrMonthDTO actualColumnDto : actualColumnDtos) {
            String supCode = actualColumnDto.getSupCode() == null ? "" : actualColumnDto.getSupCode();
            String itemCode = actualColumnDto.getItemCode() == null ? "" : actualColumnDto.getItemCode();
            String date = actualColumnDto.getDate();
            String poCode = actualColumnDto.getPoCode() == null ? "" : actualColumnDto.getPoCode();
            String poLn = actualColumnDto.getPoLn() == null ? "" : actualColumnDto.getPoLn().toString();
            String proNo = actualColumnDto.getProNo() == null ? "" : actualColumnDto.getProNo();

            String key = supCode + itemCode + poCode + poLn + proNo;
            PoItemReqRepotTotal2 poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }

            Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
            if(actualColumnDtosSubMap == null){
                actualColumnDtosSubMap = new HashMap<>();
                actualColumnDtosSubMap.put(date,actualColumnDto);
                actualColumnDtosMap.put(key,actualColumnDtosSubMap);
            }else{
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthFastDTO = actualColumnDtosSubMap.get(date);
                if(poItemReqRepotCurrMonthFastDTO == null){
                    actualColumnDtosSubMap.put(date,actualColumnDto);
                }
            }

        }

        List<PoItemReqRepotCurrMonthDTO > predictColumnDtos = new ArrayList<>();
        Map <String,Map<String,PoItemReqRepotCurrMonthDTO>> predictColumnDtosMap = new HashMap<>();
        for (PoItemReqRepotCurrMonthDTO predictColumnDto : predictColumnDtos) {
            String supCode = predictColumnDto.getSupCode() == null ? "" : predictColumnDto.getSupCode();
            String itemCode = predictColumnDto.getItemCode() == null ? "" : predictColumnDto.getItemCode();
            String date = predictColumnDto.getDate();
            String poCode = predictColumnDto.getPoCode() == null ? "" : predictColumnDto.getPoCode();
            String poLn = predictColumnDto.getPoLn() == null ? "" : predictColumnDto.getPoLn().toString();
            String proNo = predictColumnDto.getProNo() == null ? "" : predictColumnDto.getProNo();

            String key = supCode + itemCode + poCode + poLn + proNo;
            PoItemReqRepotTotal2 poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }

            Map<String, PoItemReqRepotCurrMonthDTO> predictColumnDtosSubMap = predictColumnDtosMap.get(key);
            if(predictColumnDtosSubMap == null){
                predictColumnDtosSubMap = new HashMap<>();
                predictColumnDtosSubMap.put(date,predictColumnDto);
                predictColumnDtosMap.put(key,predictColumnDtosSubMap);
            }else{
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthFastDTO = predictColumnDtosSubMap.get(date);
                if(poItemReqRepotCurrMonthFastDTO == null){
                    predictColumnDtosSubMap.put(date,predictColumnDto);
                }
            }
        }


        List<PoItemReqRepotCurrMonthDTO> actualNextColumnDtos = this.baseMapper.getActualNextColumnValues2();
        Map <String,Map<String,PoItemReqRepotCurrMonthDTO>> actualNextColumnDtosMap = new HashMap<>();
        for (PoItemReqRepotCurrMonthDTO actualNextColumnDto : actualNextColumnDtos) {
            String supCode = actualNextColumnDto.getSupCode() == null ? "" : actualNextColumnDto.getSupCode();
            String itemCode = actualNextColumnDto.getItemCode() == null ? "" : actualNextColumnDto.getItemCode();
            String date = actualNextColumnDto.getDate();
            String poCode = actualNextColumnDto.getPoCode() == null ? "" : actualNextColumnDto.getPoCode();
            String poLn = actualNextColumnDto.getPoLn() == null ? "" : actualNextColumnDto.getPoLn().toString();
            String proNo = actualNextColumnDto.getProNo() == null ? "" : actualNextColumnDto.getProNo();

            String key = supCode + itemCode + poCode + poLn + proNo;
            PoItemReqRepotTotal2 poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }

            Map<String, PoItemReqRepotCurrMonthDTO> actualNextColumnDtosSubMap = actualNextColumnDtosMap.get(key);
            if(actualNextColumnDtosSubMap == null){
                actualNextColumnDtosSubMap = new HashMap<>();
                actualNextColumnDtosSubMap.put(date,actualNextColumnDto);
                actualNextColumnDtosMap.put(key,actualNextColumnDtosSubMap);
            }else{
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthFastDTO = actualNextColumnDtosSubMap.get(date);
                if(poItemReqRepotCurrMonthFastDTO == null){
                    actualNextColumnDtosSubMap.put(date,actualNextColumnDto);
                }
            }
        }




        List<PoItemReqRepotCurrMonthDTO> predictNextColumnDtos = new ArrayList<>();
        Map <String,Map<String,PoItemReqRepotCurrMonthDTO>> predictNextColumnDtosMap = new HashMap<>();
        for (PoItemReqRepotCurrMonthDTO predictNextColumnDto : predictNextColumnDtos) {
            String supCode = predictNextColumnDto.getSupCode() == null ? "" : predictNextColumnDto.getSupCode();
            String itemCode = predictNextColumnDto.getItemCode() == null ? "" : predictNextColumnDto.getItemCode();
            String date = predictNextColumnDto.getDate();
            String poCode = predictNextColumnDto.getPoCode() == null ? "" : predictNextColumnDto.getPoCode();
            String poLn = predictNextColumnDto.getPoLn() == null ? "" : predictNextColumnDto.getPoLn().toString();
            String proNo = predictNextColumnDto.getProNo() == null ? "" : predictNextColumnDto.getProNo();

            String key = supCode + itemCode + poCode + poLn + proNo;
            PoItemReqRepotTotal2 poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }

            Map<String, PoItemReqRepotCurrMonthDTO> predictNextColumnDtosSubMap = predictNextColumnDtosMap.get(key);
            if(predictNextColumnDtosSubMap == null){
                predictNextColumnDtosSubMap = new HashMap<>();
                predictNextColumnDtosSubMap.put(date,predictNextColumnDto);
                predictNextColumnDtosMap.put(key,predictNextColumnDtosSubMap);
            }else{
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthFastDTO = predictNextColumnDtosSubMap.get(date);
                if(poItemReqRepotCurrMonthFastDTO == null){
                    predictNextColumnDtosSubMap.put(date,predictNextColumnDto);
                }
            }
        }
        /***************************************/



        Set<String> keySets = map.keySet();
        for (String key : keySets) {
            PoItemReqRepotTotal2 poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }
            Map<String, Object> subMap = map.get(key);
            //实际需求
            //获取项目号+订单号+总数量
            PoItemNewReportVO actualColumnVo = (PoItemNewReportVO) subMap.get("Actual");
            if(actualColumnVo == null){
                actualColumnVo = new PoItemNewReportVO();
                actualColumnVo.setSupCode(poItemReqRepotTotal.getSupCode());
                actualColumnVo.setSupName(poItemReqRepotTotal.getSupName());
                actualColumnVo.setItemCode(poItemReqRepotTotal.getItemCode());
                actualColumnVo.setItemName(poItemReqRepotTotal.getItemName());

                actualColumnVo.setPoCode(poItemReqRepotTotal.getPoCode());
                actualColumnVo.setPoLn(poItemReqRepotTotal.getPoLn());
                actualColumnVo.setProNo(poItemReqRepotTotal.getProNo());
            }
            actualColumnVo.setType("实际需求");

            //预测需求
            //获取项目号+订单号+总数量
            PoItemNewReportVO predictColumnVo = (PoItemNewReportVO) subMap.get("Predict");
            if(predictColumnVo == null){
                predictColumnVo = new PoItemNewReportVO();
                predictColumnVo.setSupCode(poItemReqRepotTotal.getSupCode());
                predictColumnVo.setSupName(poItemReqRepotTotal.getSupName());
                predictColumnVo.setItemCode(poItemReqRepotTotal.getItemCode());
                predictColumnVo.setItemName(poItemReqRepotTotal.getItemName());

                predictColumnVo.setPoCode(poItemReqRepotTotal.getPoCode());
                predictColumnVo.setPoLn(poItemReqRepotTotal.getPoLn());
                predictColumnVo.setProNo(poItemReqRepotTotal.getProNo());
            }
            predictColumnVo.setType("预测需求");

            /*************/
            List<PoItemReqRepotCurrMonthDTO> actualList = Lists.newArrayList();
            List<PoItemReqRepotCurrMonthDTO> predictList = Lists.newArrayList();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int monthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int today = calendar.get(Calendar.DATE);
            calendar.setTime(new Date());
            int actualDelayQties = 0;
            int predictDelayQties = 0;
            if(today < 20){
                for (int i = monthDay; i >= 1; i--) { // 之前逻辑today 改为1号开始取值
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate2(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(),poItemReqRepotTotal.getPoCode(),poItemReqRepotTotal.getPoLn(),poItemReqRepotTotal.getProNo(),fullDate);
                            actualColumnDto.setIsMeetOptDate(actualIsMeetDto != null ? 0 : 1);
                            if(actualColumnDto.getIsMeetOptDate().equals(0) && i < today){
                                actualDelayQties += actualIsMeetDto.getQty();
                            }
                        } else {
                            actualColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        actualList.add(actualColumnDto);
                    }

                    Map<String, PoItemReqRepotCurrMonthDTO> predictColumnDtosSubMap = predictColumnDtosMap.get(key);
                    if(predictColumnDtosSubMap != null){
                        PoItemReqRepotCurrMonthDTO predictColumnDto = predictColumnDtosSubMap.get(fullDate);
                        if (predictColumnDto == null) {
                            predictColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }else{
                            if(i < today){
                                predictDelayQties += predictColumnDto.getQty();
                            }
                        }
                        predictList.add(predictColumnDto);
                    }

                }
            }else{
                // 统计1号到今天前一天的延期数量之和
                for (int i = 1; i < today; i++) { // 改为20号开始取值
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate2(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(),poItemReqRepotTotal.getPoCode(),poItemReqRepotTotal.getPoLn(),poItemReqRepotTotal.getProNo(),fullDate);
                            actualDelayQties += actualIsMeetDto.getQty();
                        }
                    }
                    Map<String, PoItemReqRepotCurrMonthDTO> predictColumnDtosSubMap = predictColumnDtosMap.get(key);
                    if(predictColumnDtosSubMap != null){
                        PoItemReqRepotCurrMonthDTO predictColumnDto = predictColumnDtosSubMap.get(fullDate);
                        if (predictColumnDto == null) {
                            predictColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }else{
                            predictDelayQties += predictColumnDto.getQty();
                        }
                    }
                }
                // 先到当前月底
                for (int i = monthDay; i >= 20; i--) { // 改为20号开始取值
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate2(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(),poItemReqRepotTotal.getPoCode(),poItemReqRepotTotal.getPoLn(),poItemReqRepotTotal.getProNo(),fullDate);
                            actualColumnDto.setIsMeetOptDate(actualIsMeetDto != null ? 0 : 1);
                        } else {
                            actualColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        actualList.add(actualColumnDto);
                    }

                    Map<String, PoItemReqRepotCurrMonthDTO> predictColumnDtosSubMap = predictColumnDtosMap.get(key);
                    if(predictColumnDtosSubMap != null){
                        PoItemReqRepotCurrMonthDTO predictColumnDto = predictColumnDtosSubMap.get(fullDate);
                        if (predictColumnDto == null) {
                            predictColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        predictList.add(predictColumnDto);
                    }

                }
                // 下个月整月都取值
                calendar.add(Calendar.MONTH, +1);
                int nextMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = nextMonthDay; i >= 1; i--) {
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate2(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(),poItemReqRepotTotal.getPoCode(),poItemReqRepotTotal.getPoLn(),poItemReqRepotTotal.getProNo(),fullDate);
                            actualColumnDto.setIsMeetOptDate(actualIsMeetDto != null ? 0 : 1);
                        } else {
                            actualColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        actualList.add(actualColumnDto);
                    }

                    Map<String, PoItemReqRepotCurrMonthDTO> predictColumnDtosSubMap = predictColumnDtosMap.get(key);
                    if(predictColumnDtosSubMap != null){
                        PoItemReqRepotCurrMonthDTO predictColumnDto = predictColumnDtosSubMap.get(fullDate);
                        if (predictColumnDto == null) {
                            predictColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        predictList.add(predictColumnDto);
                    }
                }
            }


            //15号之后：当前月最后一天--下个月最后一天
            /*if (today > 15) {
                //倒序日期
                List<PoItemReqRepotCurrMonthDTO> actualSortList = Lists.newArrayList();
                actualSortList.addAll(actualList);
                actualList.removeAll(actualSortList);
                List<PoItemReqRepotCurrMonthDTO> predictSortList = Lists.newArrayList();
                predictSortList.addAll(predictList);
                predictList.removeAll(predictSortList);

                calendar.add(Calendar.MONTH, +1);
                int nextMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = nextMonthDay; i >= 1; i--) {
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(), fullDate);
                            actualColumnDto.setIsMeetOptDate(actualIsMeetDto != null ? 0 : 1);
                        } else {
                            actualColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        actualList.add(actualColumnDto);
                    }

                    Map<String, PoItemReqRepotCurrMonthDTO> predictColumnDtosSubMap = predictColumnDtosMap.get(key);
                    if(predictColumnDtosSubMap != null){
                        PoItemReqRepotCurrMonthDTO predictColumnDto = predictColumnDtosSubMap.get(fullDate);
                        if (predictColumnDto == null) {
                            predictColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        predictList.add(predictColumnDto);
                    }
                }
                actualList.addAll(actualSortList);
                predictList.addAll(predictSortList);
            }*/

            //下半年 (如果20号之前 +1 - +6，否则 +2 - +7)
            for (int i = calendar.get(Calendar.MONTH) + 7; i >= calendar.get(Calendar.MONTH) + 2; i--) {
                String timeCurr;
                switch (i) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        timeCurr = calendar.get(Calendar.YEAR) + "-0" + i;
                        break;
                    case 10:
                    case 11:
                    case 12:
                        timeCurr = calendar.get(Calendar.YEAR) + "-" + i;
                        break;
                    default:
                        timeCurr = (calendar.get(Calendar.YEAR) + 1) + "-0" + (i - 12);
                        break;
                }
                Map<String, PoItemReqRepotCurrMonthDTO> actualNextColumnDtosSubMap = actualNextColumnDtosMap.get(key);
                if(actualNextColumnDtosSubMap != null){
                    PoItemReqRepotCurrMonthDTO actualNextColumnDto = actualNextColumnDtosSubMap.get(timeCurr);
                    if (actualNextColumnDto == null) {
                        actualNextColumnDto = new PoItemReqRepotCurrMonthDTO(timeCurr);
                    }
                    actualList.add(actualNextColumnDto);
                }


                Map<String, PoItemReqRepotCurrMonthDTO> predictNextColumnDtosSubMap = predictNextColumnDtosMap.get(key);
                if(predictNextColumnDtosSubMap != null){
                    PoItemReqRepotCurrMonthDTO predictNextColumnDto = predictNextColumnDtosSubMap.get(timeCurr);
                    if (predictNextColumnDto == null) {
                        predictNextColumnDto = new PoItemReqRepotCurrMonthDTO(timeCurr);
                    }
                    String date = predictNextColumnDto.getDate();
                    if(date != null){
                        predictNextColumnDto.setDate(date.replace("/","-"));
                    }
                    predictList.add(predictNextColumnDto);
                }

            }

            actualColumnVo.setColumnValues(actualList);
            actualColumnVo.setDelayQties(actualDelayQties);
            predictColumnVo.setColumnValues(predictList);
            predictColumnVo.setDelayQties(predictDelayQties);
            /**************/

            //后推至下个月底或者本月底
            // getDateValue(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(), actualColumnVo, predictColumnVo);

            voList.add(actualColumnVo);
            voList.add(actualColumnVo);
        }
        return voList;
    }


    /**
     * getNewVoList2ForExcel
     *
     * @param totalList List
     * @return List
     */
    public List<PoItemNewReportVO> getNewVoList2ForExcel(List<PoItemReqRepotTotal2> totalList) {
        List<PoItemNewReportVO> voList = Lists.newArrayList();

        Map<String,PoItemReqRepotTotal2> poItemReqRepotTotalMap = new HashMap<>();
        for (PoItemReqRepotTotal2 poItemReqRepotTotal : totalList) {
            String supCode = poItemReqRepotTotal.getSupCode();
            String itemCode = poItemReqRepotTotal.getItemCode();
            String poCode = poItemReqRepotTotal.getPoCode();
            Integer poLn = poItemReqRepotTotal.getPoLn();
            String proNo = poItemReqRepotTotal.getProNo();
            String key = supCode + itemCode + poCode + poLn + proNo;
            poItemReqRepotTotalMap.put(key,poItemReqRepotTotal);
        }

        // 查询全部PoItemNewReportVO，PoItemNewReportVO
        List<PoItemNewReportVO> poItemNewReportVos = this.baseMapper.getActualVos2();

        Map<String,Map<String,Object>> map = new HashMap<>();// 用不key记录 PoItemNewReportVO，PoItemNewReportVO的list
        for (PoItemNewReportVO poItemNewReportVo : poItemNewReportVos) {
            String supCode  = poItemNewReportVo.getSupCode() == null ? "" : poItemNewReportVo.getSupCode();
            String itemCode = poItemNewReportVo.getItemCode() == null ? "" : poItemNewReportVo.getItemCode();
            String poCode = poItemNewReportVo.getPoCode() == null ? "" : poItemNewReportVo.getPoCode();
            String poLn = poItemNewReportVo.getPoLn() == null ? "" : poItemNewReportVo.getPoLn().toString();
            String proNo = poItemNewReportVo.getProNo() == null ? "" : poItemNewReportVo.getProNo();

            String key = supCode + itemCode + poCode + poLn + proNo;
            PoItemReqRepotTotal2 poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }
            if(map.get(key) == null){ // 不存在
                Map<String,Object> subMap = new HashMap();
                subMap.put("Actual",poItemNewReportVo);
                map.put(key,subMap);
            }else{ // 存在
                continue;
            }
        }



        /***************************************/
        // 先查询所有料号下的日期
        List<PoItemReqRepotCurrMonthDTO> actualColumnDtos = this.baseMapper.getActualColumnValues2();
        Map <String,Map<String,PoItemReqRepotCurrMonthDTO>> actualColumnDtosMap = new HashMap<>();
        for (PoItemReqRepotCurrMonthDTO actualColumnDto : actualColumnDtos) {
            String supCode = actualColumnDto.getSupCode() == null ? "" : actualColumnDto.getSupCode();
            String itemCode = actualColumnDto.getItemCode() == null ? "" : actualColumnDto.getItemCode();
            String date = actualColumnDto.getDate();
            String poCode = actualColumnDto.getPoCode() == null ? "" : actualColumnDto.getPoCode();
            String poLn = actualColumnDto.getPoLn() == null ? "" : actualColumnDto.getPoLn().toString();
            String proNo = actualColumnDto.getProNo() == null ? "" : actualColumnDto.getProNo();

            String key = supCode + itemCode + poCode + poLn + proNo;
            PoItemReqRepotTotal2 poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }

            Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
            if(actualColumnDtosSubMap == null){
                actualColumnDtosSubMap = new HashMap<>();
                actualColumnDtosSubMap.put(date,actualColumnDto);
                actualColumnDtosMap.put(key,actualColumnDtosSubMap);
            }else{
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthFastDTO = actualColumnDtosSubMap.get(date);
                if(poItemReqRepotCurrMonthFastDTO == null){
                    actualColumnDtosSubMap.put(date,actualColumnDto);
                }
            }

        }



        List<PoItemReqRepotCurrMonthDTO> actualNextColumnDtos = this.baseMapper.getActualNextColumnValues2();
        Map <String,Map<String,PoItemReqRepotCurrMonthDTO>> actualNextColumnDtosMap = new HashMap<>();
        for (PoItemReqRepotCurrMonthDTO actualNextColumnDto : actualNextColumnDtos) {
            String supCode = actualNextColumnDto.getSupCode() == null ? "" : actualNextColumnDto.getSupCode();
            String itemCode = actualNextColumnDto.getItemCode() == null ? "" : actualNextColumnDto.getItemCode();
            String date = actualNextColumnDto.getDate();
            String poCode = actualNextColumnDto.getPoCode() == null ? "" : actualNextColumnDto.getPoCode();
            String poLn = actualNextColumnDto.getPoLn() == null ? "" : actualNextColumnDto.getPoLn().toString();
            String proNo = actualNextColumnDto.getProNo() == null ? "" : actualNextColumnDto.getProNo();

            String key = supCode + itemCode + poCode + poLn + proNo;
            PoItemReqRepotTotal2 poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }

            Map<String, PoItemReqRepotCurrMonthDTO> actualNextColumnDtosSubMap = actualNextColumnDtosMap.get(key);
            if(actualNextColumnDtosSubMap == null){
                actualNextColumnDtosSubMap = new HashMap<>();
                actualNextColumnDtosSubMap.put(date,actualNextColumnDto);
                actualNextColumnDtosMap.put(key,actualNextColumnDtosSubMap);
            }else{
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthFastDTO = actualNextColumnDtosSubMap.get(date);
                if(poItemReqRepotCurrMonthFastDTO == null){
                    actualNextColumnDtosSubMap.put(date,actualNextColumnDto);
                }
            }
        }

        /***************************************/



        Set<String> keySets = map.keySet();
        for (String key : keySets) {
            PoItemReqRepotTotal2 poItemReqRepotTotal = poItemReqRepotTotalMap.get(key);
            if(poItemReqRepotTotal == null){
                continue;
            }
            Map<String, Object> subMap = map.get(key);
            //实际需求
            //获取项目号+订单号+总数量
            PoItemNewReportVO actualColumnVo = (PoItemNewReportVO) subMap.get("Actual");
            if(actualColumnVo == null){
                actualColumnVo = new PoItemNewReportVO();
                actualColumnVo.setSupCode(poItemReqRepotTotal.getSupCode());
                actualColumnVo.setSupName(poItemReqRepotTotal.getSupName());
                actualColumnVo.setItemCode(poItemReqRepotTotal.getItemCode());
                actualColumnVo.setItemName(poItemReqRepotTotal.getItemName());

                actualColumnVo.setPoCode(poItemReqRepotTotal.getPoCode());
                actualColumnVo.setPoLn(poItemReqRepotTotal.getPoLn());
                actualColumnVo.setProNo(poItemReqRepotTotal.getProNo());
            }
            actualColumnVo.setType("实际需求");


            /*************/
            List<PoItemReqRepotCurrMonthDTO> actualList = Lists.newArrayList();
            List<PoItemReqRepotCurrMonthDTO> predictList = Lists.newArrayList();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int monthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int today = calendar.get(Calendar.DATE);
            calendar.setTime(new Date());
            int actualDelayQties = 0;
            int predictDelayQties = 0;
            if(today < 20){
                for (int i = monthDay; i >= 1; i--) { // 之前逻辑today 改为1号开始取值
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate2(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(),poItemReqRepotTotal.getPoCode(),poItemReqRepotTotal.getPoLn(),poItemReqRepotTotal.getProNo(),fullDate);
                            actualColumnDto.setIsMeetOptDate(actualIsMeetDto != null ? 0 : 1);
                            if(actualColumnDto.getIsMeetOptDate().equals(0) && i < today){
                                actualDelayQties += actualIsMeetDto.getQty();
                            }
                        } else {
                            actualColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        actualList.add(actualColumnDto);
                    }
                }
            }else{
                // 统计1号到今天前一天的延期数量之和
                for (int i = 1; i < today; i++) { // 改为20号开始取值
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate2(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(),poItemReqRepotTotal.getPoCode(),poItemReqRepotTotal.getPoLn(),poItemReqRepotTotal.getProNo(),fullDate);
                            actualDelayQties += actualIsMeetDto.getQty();
                        }
                    }
                }
                // 先到当前月底
                for (int i = monthDay; i >= 20; i--) { // 改为20号开始取值
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate2(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(),poItemReqRepotTotal.getPoCode(),poItemReqRepotTotal.getPoLn(),poItemReqRepotTotal.getProNo(),fullDate);
                            actualColumnDto.setIsMeetOptDate(actualIsMeetDto != null ? 0 : 1);
                        } else {
                            actualColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        actualList.add(actualColumnDto);
                    }


                }
                // 下个月整月都取值
                calendar.add(Calendar.MONTH, +1);
                int nextMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = nextMonthDay; i >= 1; i--) {
                    String fullDate = getStringDay(calendar, i);
                    //获取日期值
                    Map<String, PoItemReqRepotCurrMonthDTO> actualColumnDtosSubMap = actualColumnDtosMap.get(key);
                    if(actualColumnDtosSubMap !=null){
                        PoItemReqRepotCurrMonthDTO actualColumnDto = actualColumnDtosSubMap.get(fullDate);
                        if (actualColumnDto != null) {
                            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate2(poItemReqRepotTotal.getSupCode(), poItemReqRepotTotal.getItemCode(),poItemReqRepotTotal.getPoCode(),poItemReqRepotTotal.getPoLn(),poItemReqRepotTotal.getProNo(),fullDate);
                            actualColumnDto.setIsMeetOptDate(actualIsMeetDto != null ? 0 : 1);
                        } else {
                            actualColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
                        }
                        actualList.add(actualColumnDto);
                    }

                }
            }

            //下半年 (如果20号之前 +1 - +6，否则 +2 - +7)
            for (int i = calendar.get(Calendar.MONTH) + 7; i >= calendar.get(Calendar.MONTH) + 2; i--) {
                String timeCurr;
                switch (i) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        timeCurr = calendar.get(Calendar.YEAR) + "-0" + i;
                        break;
                    case 10:
                    case 11:
                    case 12:
                        timeCurr = calendar.get(Calendar.YEAR) + "-" + i;
                        break;
                    default:
                        timeCurr = (calendar.get(Calendar.YEAR) + 1) + "-0" + (i - 12);
                        break;
                }
                Map<String, PoItemReqRepotCurrMonthDTO> actualNextColumnDtosSubMap = actualNextColumnDtosMap.get(key);
                if(actualNextColumnDtosSubMap != null){
                    PoItemReqRepotCurrMonthDTO actualNextColumnDto = actualNextColumnDtosSubMap.get(timeCurr);
                    if (actualNextColumnDto == null) {
                        actualNextColumnDto = new PoItemReqRepotCurrMonthDTO(timeCurr);
                    }
                    actualList.add(actualNextColumnDto);
                }



            }

            actualColumnVo.setColumnValues(actualList);
            actualColumnVo.setDelayQties(actualDelayQties);
            /**************/

            voList.add(actualColumnVo);
        }
        return voList;
    }


    /**
     * getDateValue
     *
     * @param supCode String
     * @param itemCode String
     * @param actualColumnVo PoItemNewReportVO
     * @param predictColumnVo PoItemNewReportVO
     */
    private void getDateValue(String supCode, String itemCode, PoItemNewReportVO actualColumnVo, PoItemNewReportVO predictColumnVo) {
        List<PoItemReqRepotCurrMonthDTO> actualList = Lists.newArrayList();
        List<PoItemReqRepotCurrMonthDTO> predictList = Lists.newArrayList();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int monthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int today = calendar.get(Calendar.DATE);
        calendar.setTime(new Date());
        for (int i = monthDay; i >= today; i--) {
            String fullDate = getStringDay(calendar, i);
            //获取日期值
            addDtoList(supCode, itemCode, actualList, predictList, fullDate);
        }
        //15号之后：当前月最后一天--下个月最后一天
        if (today > 15) {
            //倒序日期
            List<PoItemReqRepotCurrMonthDTO> actualSortList = Lists.newArrayList();
            actualSortList.addAll(actualList);
            actualList.removeAll(actualSortList);
            List<PoItemReqRepotCurrMonthDTO> predictSortList = Lists.newArrayList();
            predictSortList.addAll(predictList);
            predictList.removeAll(predictSortList);

            calendar.add(Calendar.MONTH, +1);
            int nextMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = nextMonthDay; i >= 1; i--) {
                String fullDate = getStringDay(calendar, i);
                //获取日期值
                addDtoList(supCode, itemCode, actualList, predictList, fullDate);
            }
            actualList.addAll(actualSortList);
            predictList.addAll(predictSortList);
        }
        //未来半年
        getHalfYearValue(supCode, itemCode, actualList, predictList, calendar);

        actualColumnVo.setColumnValues(actualList);
        predictColumnVo.setColumnValues(predictList);
    }

    /**
     * getHalfYearValue
     *
     * @param supCode String
     * @param itemCode String
     * @param actualList String
     * @param predictList String
     * @param calendar Calendar
     */
    private void getHalfYearValue(String supCode, String itemCode, List<PoItemReqRepotCurrMonthDTO> actualList, List<PoItemReqRepotCurrMonthDTO> predictList, Calendar calendar) {




        //下半年
        for (int i = calendar.get(Calendar.MONTH) + 7; i >= calendar.get(Calendar.MONTH) + 2; i--) {
            String timeCurr;
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    timeCurr = calendar.get(Calendar.YEAR) + "-0" + i;
                    break;
                case 10:
                case 11:
                case 12:
                    timeCurr = calendar.get(Calendar.YEAR) + "-" + i;
                    break;
                default:
                    timeCurr = (calendar.get(Calendar.YEAR) + 1) + "-0" + (i - 12);
                    break;
            }
            PoItemReqRepotCurrMonthDTO actualNextColumnDto = this.baseMapper.getActualNextColumnValue(supCode, itemCode, timeCurr);
            if (actualNextColumnDto == null) {
                actualNextColumnDto = new PoItemReqRepotCurrMonthDTO(timeCurr);
            }
            actualList.add(actualNextColumnDto);

            PoItemReqRepotCurrMonthDTO predictNextColumnDto = this.baseMapper.getPredictNextColumnValue(supCode, itemCode, timeCurr);
            if (predictNextColumnDto == null) {
                predictNextColumnDto = new PoItemReqRepotCurrMonthDTO(timeCurr);
            }
            predictList.add(predictNextColumnDto);
        }
    }


    /**
     * getDateValue
     *
     * @param supCode String
     * @param itemCode String
     * @param actualColumnVo PoItemNewReportVO
     * @param predictColumnVo PoItemNewReportVO
     */
    private void getExportDateValue(String supCode, String itemCode, PoItemNewReportVO actualColumnVo, PoItemNewReportVO predictColumnVo) {
        List<PoItemReqRepotCurrMonthDTO> actualList = Lists.newArrayList();
        List<PoItemReqRepotCurrMonthDTO> predictList = Lists.newArrayList();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int monthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int today = calendar.get(Calendar.DATE);
        calendar.setTime(new Date());
        for (int i = today; i <= monthDay; i++) {
            String fullDate = getStringDay(calendar, i);
            addDtoList(supCode, itemCode, actualList, predictList, fullDate);
        }
        //15号之后：当前月最后一天--下个月最后一天
        if (today > 15) {
            calendar.add(Calendar.MONTH, +1);
            int nextMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 1; i <= nextMonthDay; i++) {
                String fullDate = getStringDay(calendar, i);
                addDtoList(supCode, itemCode, actualList, predictList, fullDate);
            }
        }
        //未来半年
        getExportHalfYearValue(supCode, itemCode, actualList, predictList, calendar);

        actualColumnVo.setColumnValues(actualList);
        predictColumnVo.setColumnValues(predictList);
    }

    /**
     * getHalfYearValue
     *
     * @param supCode String
     * @param itemCode String
     * @param actualList String
     * @param predictList String
     * @param calendar Calendar
     */
    private void getExportHalfYearValue(String supCode, String itemCode, List<PoItemReqRepotCurrMonthDTO> actualList, List<PoItemReqRepotCurrMonthDTO> predictList, Calendar calendar) {
        //下半年
        for (int i = calendar.get(Calendar.MONTH) + 2; i <= calendar.get(Calendar.MONTH) + 7; i++) {
            String timeCurr;
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    timeCurr = calendar.get(Calendar.YEAR) + "-0" + i;
                    break;
                case 10:
                case 11:
                case 12:
                    timeCurr = calendar.get(Calendar.YEAR) + "-" + i;
                    break;
                default:
                    timeCurr = (calendar.get(Calendar.YEAR) + 1) + "-0" + (i - 12);
                    break;
            }
            PoItemReqRepotCurrMonthDTO actualNextColumnDto = this.baseMapper.getActualNextColumnValue(supCode, itemCode, timeCurr);
            if (actualNextColumnDto == null) {
                actualNextColumnDto = new PoItemReqRepotCurrMonthDTO(timeCurr);
            }
            actualList.add(actualNextColumnDto);

            PoItemReqRepotCurrMonthDTO predictNextColumnDto = this.baseMapper.getPredictNextColumnValue(supCode, itemCode, timeCurr);
            if (predictNextColumnDto == null) {
                predictNextColumnDto = new PoItemReqRepotCurrMonthDTO(timeCurr);
            }
            predictList.add(predictNextColumnDto);
        }
    }

    /**
     * addDtoList
     *
     * @param supCode String
     * @param itemCode String
     * @param actualList List
     * @param predictList List
     * @param fullDate String
     */
    private void addDtoList(String supCode, String itemCode, List<PoItemReqRepotCurrMonthDTO> actualList, List<PoItemReqRepotCurrMonthDTO> predictList, String fullDate) {
        PoItemReqRepotCurrMonthDTO actualColumnDto = this.baseMapper.getActualColumnValue(supCode, itemCode, fullDate);
        if (actualColumnDto != null) {
            PoItemReqRepotCurrMonthDTO actualIsMeetDto = this.baseMapper.getActualIsMeetOptDate(supCode, itemCode, fullDate);
            actualColumnDto.setIsMeetOptDate(actualIsMeetDto != null ? 0 : 1);
        } else {
            actualColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
        }
        actualList.add(actualColumnDto);

        PoItemReqRepotCurrMonthDTO predictColumnDto = this.baseMapper.getPredictColumnValue(supCode, itemCode, fullDate);
        if (predictColumnDto == null) {
            predictColumnDto = new PoItemReqRepotCurrMonthDTO(fullDate);
        }
        predictList.add(predictColumnDto);
    }

    /**
     * getStringDay
     *
     * @param calendar Calendar
     * @param i int
     * @return String
     */
    private String getStringDay(Calendar calendar, int i) {
        String day;
        if (i < 10) {
            day = "0" + i;
        } else {
            day = "" + i;
        }
        String month;
        if (calendar.get(Calendar.MONTH) + 1 >= 10) {
            month = "" + (calendar.get(Calendar.MONTH) + 1);
        } else {
            month = "0" + (calendar.get(Calendar.MONTH) + 1);
        }
        return calendar.get(Calendar.YEAR) + "-" + month + "-" + day;
    }


    @Override
    public List<PoItemNewReportVO> getExportVoList(List<PoItemReqRepotTotal> totalList) {
        List<PoItemNewReportVO> voList = Lists.newArrayList();
        for(PoItemReqRepotTotal total : totalList){
            PoItemNewReportVO vo = new PoItemNewReportVO();
            vo.setSupCode(total.getSupCode());
            vo.setSupName(total.getSupName());
            vo.setItemCode(total.getItemCode());
            vo.setItemName(total.getItemName());

            PoItemNewReportVO actualPoItemVo = this.baseMapper.getActualVo(total.getSupCode(), total.getItemCode());
            if(actualPoItemVo == null){
                actualPoItemVo = new PoItemNewReportVO();
            }
            actualPoItemVo.setType("实际需求");
            PoItemNewReportVO predictPoItemVo = this.baseMapper.getPredictVo(total.getSupCode(), total.getItemCode());
            if(predictPoItemVo == null){
                predictPoItemVo = new PoItemNewReportVO();
            }
            predictPoItemVo.setType("预测需求");
            //后推至下个月底或者本月底
            getExportDateValue(total.getSupCode(), total.getItemCode(), actualPoItemVo, predictPoItemVo);

            NewReportColumnVO actualColumnVo = BeanUtil.copy(actualPoItemVo, NewReportColumnVO.class);
            NewReportColumnVO predictColumnVo = BeanUtil.copy(predictPoItemVo, NewReportColumnVO.class);
            vo.setActualColumnVo(actualColumnVo);
            vo.setPredictColumnVo(predictColumnVo);
            voList.add(vo);
        }
        return voList;
    }


    @Override
    public
    void poItemExport(PoItemEntity poItemEntity, HttpServletResponse response){
        List<PoItemReqRepotVO> voList = getAllReqRepot(poItemEntity);
        List<PoItemReqRepotCurrMonthDTO> dateTitle = voList.get(0).getColumnValues();
        List<ExcelExportEntity> entity = getAllEntity(dateTitle);
        List<Map<String, Object>> list = getValueList(voList, dateTitle, entity);

        ExportParams params = new ExportParams("", "", ExcelType.XSSF);
        params.setStyle(ExcelExportStatisticStyler.class);
        Workbook workbook = ExcelExportUtil.exportExcel(params, entity, list);

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("供应计划排程" + DateUtil.formatDate(new Date()) + ".xls", "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            //throw new NormalException(e.getMessage());
        }
    }


    /**
     * getNewAllEntity
     *
     * @param dateTitle List
     * @return List
     */
    @Override
    public List<ExcelExportEntity> getNewAllEntity(List<PoItemReqRepotCurrMonthDTO> dateTitle){
        List<ExcelExportEntity> entity = new ArrayList<>();
        entity.add(buildExcelEntity("供应商编码", "supCode", 0, 20));
        entity.add(buildExcelEntity("供应商名称", "supName", 1, 20));
        entity.add(buildExcelEntity("料号", "itemCode", 2, 20));
        entity.add(buildExcelEntity("物料描述", "itemName", 3, 40));
        entity.add(buildExcelEntity("总数量", "total", 4, 20));
        entity.add(buildExcelEntity("订单号", "poCodeAndPoLn", 5, 20));
        entity.add(buildExcelEntity("项目号", "proNo", 6, 20));
        entity.add(buildExcelEntity("类型", "type", 7, 20));
        //日期分组
        for (int i = 0; i < dateTitle.size(); i++) {
            if(i< dateTitle.size() - 6){
                String date = dateTitle.get(i).getDate().substring(5);
                entity.add(buildExcelEntityByGroup(date, date, "当月需求量", 8, 10));
            }else{
                String date = dateTitle.get(i).getDate().substring(5) + "月";
                entity.add(buildExcelEntityByGroup(date, date, "未来半年需求量", 9, 10));
            }
        }
        return entity;
    }


    /**
     * getNewValueList
     *
     * @param voList List
     * @param dateTitle List
     * @param entity List
     * @return List<Map<String, Object>>
     */
    @Override
    public List<Map<String, Object>> getNewValueList(List<PoItemNewReportVO> voList, List<PoItemReqRepotCurrMonthDTO> dateTitle, List<ExcelExportEntity> entity){
        List<Map<String, Object>> list = new ArrayList<>();
        for (PoItemNewReportVO vo : voList) {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("supCode", vo.getSupCode());
            params.put("supName", vo.getSupName());
            params.put("itemCode", vo.getItemCode());
            params.put("itemName", vo.getItemName());
            params.put("total", vo.getTotal());
            params.put("poCodeAndPoLn", vo.getPoCodeAndPoLn());
            params.put("proNo", vo.getProNo());
            params.put("type", vo.getType());
            for (int i = 0; i < vo.getColumnValues().size(); i++) {
                if (vo.getColumnValues().get(i).getIsMeetOptDate() == 0) {
                    for (ExcelExportEntity excelExportEntity : entity) {
                        //设置默认(防止从缓存中读取为true)
                        excelExportEntity.setWrap(false);
                        if (vo.getColumnValues().get(i).getDate().contains(excelExportEntity.getName())) {
                            excelExportEntity.setWrap(true);
                        }
                    }
                }
                if (i < dateTitle.size() - 6) {
                    String date = vo.getColumnValues().get(i).getDate().substring(5);
                    params.put(date, vo.getColumnValues().get(i).getQty() == 0 ? "" : vo.getColumnValues().get(i).getQty());
                } else {
                    String date = vo.getColumnValues().get(i).getDate().substring(5) + "月";
                    params.put(date, vo.getColumnValues().get(i).getQty() == 0 ? "" : vo.getColumnValues().get(i).getQty());
                }
            }
            list.add(params);
        }
        return list;
    }

    @Override
    public void newPoItemExport(PoItemEntity poItemEntity, HttpServletResponse response) {
        List<PoItemReqRepotTotal> totalList = this.baseMapper.getNewReportTotal(poItemEntity);
        /*List<PoItemNewReportVO> voList = getExportVoList(totalList);
        List<PoItemReqRepotCurrMonthDTO> dateTitle = voList.get(0).getActualColumnVo().getColumnValues();
        XSSFWorkbook wb = new XSSFWorkbook();
        getExportTableHead(voList, dateTitle, wb, response);
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("送货计划报表" + DateUtil.formatDate(new Date()) + ".xls", "UTF-8"));
            wb.write(response.getOutputStream());
        } catch (IOException e) {
            //throw new NormalException(e.getMessage());
        }*/
        List<PoItemNewReportVO> voList = getNewVoList(totalList);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int monthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int today = calendar.get(Calendar.DATE);
        calendar.setTime(new Date());


        List<String> days = new ArrayList<>();

        if(today < 20){
            for (int i = 1; i <=monthDay ; i++) { // 之前逻辑today 改为1号开始取值
                String fullDate = getStringDay(calendar, i);
                days.add(fullDate);
            }
        }else{
            // 先到当前月底
            for (int i = 20; i <= monthDay; i++) { // 改为20号开始取值
                String fullDate = getStringDay(calendar, i);
                days.add(fullDate);
            }
            // 下个月整月都取值
            calendar.add(Calendar.MONTH, +1);
            int nextMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 1; i <= nextMonthDay; i++) {
                String fullDate = getStringDay(calendar, i);
                days.add(fullDate);
            }
        }

        List<String> months = new ArrayList<>();

        for (int i = calendar.get(Calendar.MONTH) + 2 ; i <= calendar.get(Calendar.MONTH) + 7 ; i++) {
            String timeCurr;
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    timeCurr = calendar.get(Calendar.YEAR) + "-0" + i;
                    break;
                case 10:
                case 11:
                case 12:
                    timeCurr = calendar.get(Calendar.YEAR) + "-" + i;
                    break;
                default:
                    timeCurr = (calendar.get(Calendar.YEAR) + 1) + "-0" + (i - 12);
                    break;
            }
            months.add(timeCurr);
        }

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();

        // 表头
        CellStyle cellStyleHead = wb.createCellStyle();
        XSSFFont fontHead = wb.createFont();
        fontHead.setFontName("黑体");
        fontHead.setBold(true);
        cellStyleHead.setFont(fontHead);
        cellStyleHead.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
        cellStyleHead.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());  //设置填充颜色
        cellStyleHead.setBorderBottom(BorderStyle.THIN);
        cellStyleHead.setBorderLeft(BorderStyle.THIN);
        cellStyleHead.setBorderRight(BorderStyle.THIN);
        cellStyleHead.setBorderTop(BorderStyle.THIN);

        XSSFRow row_title = sheet.createRow(0);
        //sheet.createFreezePane(9 + days.size() + months.size(), 1);//固定第一行的个列
        //row_title.setHeight((short) 200);//行高设置成30px


        XSSFCell row_title_cell0 = row_title.createCell(0);
        row_title_cell0.setCellValue("供应商");
        row_title_cell0.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(0, 10000);

        XSSFCell row_title_cell1 = row_title.createCell(1);
        row_title_cell1.setCellValue("物料编码");
        row_title_cell1.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(1, 3000);

        XSSFCell row_title_cell2 = row_title.createCell(2);
        row_title_cell2.setCellValue("物料描述");
        row_title_cell2.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(2, 15000);

        XSSFCell row_title_cell3 = row_title.createCell(3);
        row_title_cell3.setCellValue("类型");
        row_title_cell3.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(3, 3000);

        XSSFCell row_title_cell4 = row_title.createCell(4);
        row_title_cell4.setCellValue("总数量");
        row_title_cell4.setCellStyle(cellStyleHead);

        XSSFCell row_title_cell5 = row_title.createCell(5);
        row_title_cell5.setCellValue("延期未送货数量");
        row_title_cell5.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(5, 5000);

        XSSFCell row_title_cell6 = row_title.createCell(6);
        row_title_cell6.setCellValue("订单号");
        row_title_cell6.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(6, 5000);

        XSSFCell row_title_cell7 = row_title.createCell(7);
        row_title_cell7.setCellValue("项目号");
        row_title_cell7.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(7, 5000);


        for(int i = 0 ; i<days.size();i++){
            int index = 8 + i;
            XSSFCell row_title_cell = row_title.createCell(index);
            row_title_cell.setCellValue(days.get(i));
            row_title_cell.setCellStyle(cellStyleHead);
            sheet.setColumnWidth(index, 3000);
        }

        for(int i = 0 ; i<months.size();i++){
            int index = 8 + days.size() + i;
            XSSFCell row_title_cell = row_title.createCell(index);
            row_title_cell.setCellValue(months.get(i));
            row_title_cell.setCellStyle(cellStyleHead);
            sheet.setColumnWidth(index, 3000);
        }

        // 表体
        int rowIndex = 1;
        for (PoItemNewReportVO reportVO : voList) {
            XSSFRow row = sheet.createRow(rowIndex);

            List<PoItemReqRepotCurrMonthDTO> columnValues = reportVO.getColumnValues();
            Map<String,PoItemReqRepotCurrMonthDTO> map = new HashMap<>();
            for (PoItemReqRepotCurrMonthDTO columnValue : columnValues) {
                map.put(columnValue.getDate(),columnValue);
            }

            String type = reportVO.getType();
            CellStyle cellStyleType = wb.createCellStyle();
            cellStyleType.setBorderBottom(BorderStyle.THIN);
            cellStyleType.setBorderLeft(BorderStyle.THIN);
            cellStyleType.setBorderRight(BorderStyle.THIN);
            cellStyleType.setBorderTop(BorderStyle.THIN);
            if(type.equals("实际需求")){
                cellStyleType.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                cellStyleType.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());  //设置填充颜色
            }
            if(type.equals("预测需求")){
                cellStyleType.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                cellStyleType.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  //设置填充颜色
            }

            XSSFCell row_cell0 = row.createCell(0);
            row_cell0.setCellValue(reportVO.getSupCode() + " " + reportVO.getSupName());
            row_cell0.setCellStyle(cellStyleType);

            XSSFCell row_cell1 = row.createCell(1);
            row_cell1.setCellValue(reportVO.getItemCode());
            row_cell1.setCellStyle(cellStyleType);

            XSSFCell row_cell2 = row.createCell(2);
            row_cell2.setCellValue(reportVO.getItemName());
            row_cell2.setCellStyle(cellStyleType);

            XSSFCell row_cell3 = row.createCell(3);
            row_cell3.setCellValue(reportVO.getType());
            row_cell3.setCellStyle(cellStyleType);

            XSSFCell row_cell4 = row.createCell(4);
            Integer total = reportVO.getTotal();
            if(total == null){
                row_cell4.setCellValue("");
            }else{
                row_cell4.setCellValue(total);
            }
            row_cell4.setCellStyle(cellStyleType);

            XSSFFont font = wb.createFont();
            XSSFFont fontNull = wb.createFont();
            font.setColor(IndexedColors.RED.getIndex());//设置excel数据字体颜色

            XSSFCell row_cell5 = row.createCell(5);
            Integer delayQties = reportVO.getDelayQties();
            if(delayQties > 0){
                CellStyle cellStyleType1 = wb.createCellStyle();
                cellStyleType1.setBorderBottom(BorderStyle.THIN);
                cellStyleType1.setBorderLeft(BorderStyle.THIN);
                cellStyleType1.setBorderRight(BorderStyle.THIN);
                cellStyleType1.setBorderTop(BorderStyle.THIN);
                if(type.equals("实际需求")){
                    cellStyleType1.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                    cellStyleType1.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());  //设置填充颜色
                }
                if(type.equals("预测需求")){
                    cellStyleType1.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                    cellStyleType1.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  //设置填充颜色
                }
                cellStyleType1.setFont(font);
                row_cell5.setCellStyle(cellStyleType1);
            }else{
                row_cell5.setCellStyle(cellStyleType);
            }
            row_cell5.setCellValue(delayQties);


            XSSFCell row_cell6 = row.createCell(6);
            row_cell6.setCellValue(reportVO.getPoCodeAndPoLn());
            row_cell6.setCellStyle(cellStyleType);

            XSSFCell row_cell7 = row.createCell(7);
            row_cell7.setCellValue(reportVO.getProNo());
            row_cell7.setCellStyle(cellStyleType);

            for(int i = 0 ; i<days.size();i++){
                int index = 8 + i;
                XSSFCell row_cell = row.createCell(index);
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthDTO = map.get(days.get(i));
                if(poItemReqRepotCurrMonthDTO != null){
                    Integer qty = poItemReqRepotCurrMonthDTO.getQty();
                    Integer isMeetOptDate = poItemReqRepotCurrMonthDTO.getIsMeetOptDate();
                    if(qty > 0 && isMeetOptDate == 0){
                        CellStyle cellStyleType1 = wb.createCellStyle();
                        cellStyleType1.setBorderBottom(BorderStyle.THIN);
                        cellStyleType1.setBorderLeft(BorderStyle.THIN);
                        cellStyleType1.setBorderRight(BorderStyle.THIN);
                        cellStyleType1.setBorderTop(BorderStyle.THIN);
                        if(type.equals("实际需求")){
                            cellStyleType1.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                            cellStyleType1.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());  //设置填充颜色
                        }
                        if(type.equals("预测需求")){
                            cellStyleType1.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                            cellStyleType1.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  //设置填充颜色
                        }
                        cellStyleType1.setFont(font);
                        row_cell.setCellStyle(cellStyleType1);
                        row_cell.setCellValue(qty);
                    }else if(qty > 0 && isMeetOptDate == 1){
                        row_cell.setCellStyle(cellStyleType);
                        row_cell.setCellValue(qty);
                    }else{
                        row_cell.setCellStyle(cellStyleType);
                    }

                }else{
                    row_cell.setCellStyle(cellStyleType);
                }

            }

            for(int i = 0 ; i<months.size();i++){
                int index = 8 + days.size() + i;
                XSSFCell row_cell = row.createCell(index);
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthDTO = map.get(months.get(i));
                if(poItemReqRepotCurrMonthDTO != null){
                    Integer qty = poItemReqRepotCurrMonthDTO.getQty();
                    Integer isMeetOptDate = poItemReqRepotCurrMonthDTO.getIsMeetOptDate();
                    if(qty > 0 && isMeetOptDate == 0){
                        CellStyle cellStyleType1 = wb.createCellStyle();
                        cellStyleType1.setBorderBottom(BorderStyle.THIN);
                        cellStyleType1.setBorderLeft(BorderStyle.THIN);
                        cellStyleType1.setBorderRight(BorderStyle.THIN);
                        cellStyleType1.setBorderTop(BorderStyle.THIN);
                        if(type.equals("实际需求")){
                            cellStyleType1.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                            cellStyleType1.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());  //设置填充颜色
                        }
                        if(type.equals("预测需求")){
                            cellStyleType1.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                            cellStyleType1.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  //设置填充颜色
                        }
                        cellStyleType1.setFont(font);
                        row_cell.setCellStyle(cellStyleType1);
                        row_cell.setCellValue(qty);
                    }else if(qty > 0 && isMeetOptDate == 1){
                        row_cell.setCellStyle(cellStyleType);
                        row_cell.setCellValue(qty);
                    }else{
                        row_cell.setCellStyle(cellStyleType);
                    }
                }else{
                    row_cell.setCellStyle(cellStyleType);
                }
            }


            rowIndex ++ ;
        }

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("送货计划报表" + DateUtil.formatDate(new Date()) + ".xlsx", "UTF-8"));
            wb.write(response.getOutputStream());
        } catch (IOException e) {
            //throw new NormalException(e.getMessage());
        }
    }


    @Override
    public void newPoItemExport2(PoItemEntity poItemEntity, HttpServletResponse response) {
        List<PoItemReqRepotTotal2> totalList = this.baseMapper.getNewReportTotal2(poItemEntity);
        List<PoItemNewReportVO> voList = getNewVoList2ForExcel(totalList);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int monthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int today = calendar.get(Calendar.DATE);
        calendar.setTime(new Date());


        List<String> days = new ArrayList<>();

        if(today < 20){
            for (int i = 1; i <=monthDay ; i++) { // 之前逻辑today 改为1号开始取值
                String fullDate = getStringDay(calendar, i);
                days.add(fullDate);
            }
        }else{
            // 先到当前月底
            for (int i = 20; i <= monthDay; i++) { // 改为20号开始取值
                String fullDate = getStringDay(calendar, i);
                days.add(fullDate);
            }
            // 下个月整月都取值
            calendar.add(Calendar.MONTH, +1);
            int nextMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 1; i <= nextMonthDay; i++) {
                String fullDate = getStringDay(calendar, i);
                days.add(fullDate);
            }
        }

        List<String> months = new ArrayList<>();

        for (int i = calendar.get(Calendar.MONTH) + 2 ; i <= calendar.get(Calendar.MONTH) + 7 ; i++) {
            String timeCurr;
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    timeCurr = calendar.get(Calendar.YEAR) + "-0" + i;
                    break;
                case 10:
                case 11:
                case 12:
                    timeCurr = calendar.get(Calendar.YEAR) + "-" + i;
                    break;
                default:
                    timeCurr = (calendar.get(Calendar.YEAR) + 1) + "-0" + (i - 12);
                    break;
            }
            months.add(timeCurr);
        }

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();

        // 表头
        CellStyle cellStyleHead = wb.createCellStyle();
        XSSFFont fontHead = wb.createFont();
        fontHead.setFontName("黑体");
        fontHead.setBold(true);
        cellStyleHead.setFont(fontHead);
        cellStyleHead.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
        cellStyleHead.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());  //设置填充颜色
        cellStyleHead.setBorderBottom(BorderStyle.THIN);
        cellStyleHead.setBorderLeft(BorderStyle.THIN);
        cellStyleHead.setBorderRight(BorderStyle.THIN);
        cellStyleHead.setBorderTop(BorderStyle.THIN);

        XSSFRow row_title = sheet.createRow(0);
        //sheet.createFreezePane(9 + days.size() + months.size(), 1);//固定第一行的个列
        //row_title.setHeight((short) 200);//行高设置成30px

        XSSFCell row_title_cell0 = row_title.createCell(0);
        row_title_cell0.setCellValue("项目号");
        row_title_cell0.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(0, 5000);


        XSSFCell row_title_cell1 = row_title.createCell(1);
        row_title_cell1.setCellValue("供应商");
        row_title_cell1.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(1, 10000);

        XSSFCell row_title_cell2 = row_title.createCell(2);
        row_title_cell2.setCellValue("物料编码");
        row_title_cell2.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(2, 3000);

        XSSFCell row_title_cell3 = row_title.createCell(3);
        row_title_cell3.setCellValue("物料描述");
        row_title_cell3.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(3, 15000);

        XSSFCell row_title_cell4 = row_title.createCell(4);
        row_title_cell4.setCellValue("类型");
        row_title_cell4.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(4, 3000);

        XSSFCell row_title_cell5 = row_title.createCell(5);
        row_title_cell5.setCellValue("总数量");
        row_title_cell5.setCellStyle(cellStyleHead);

        XSSFCell row_title_cell6 = row_title.createCell(6);
        row_title_cell6.setCellValue("延期未送货数量");
        row_title_cell6.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(6, 5000);

        XSSFCell row_title_cell7 = row_title.createCell(7);
        row_title_cell7.setCellValue("订单号");
        row_title_cell7.setCellStyle(cellStyleHead);
        sheet.setColumnWidth(7, 5000);




        for(int i = 0 ; i<days.size();i++){
            int index = 8 + i;
            XSSFCell row_title_cell = row_title.createCell(index);
            row_title_cell.setCellValue(days.get(i));
            row_title_cell.setCellStyle(cellStyleHead);
            sheet.setColumnWidth(index, 3000);
        }

        for(int i = 0 ; i<months.size();i++){
            int index = 8 + days.size() + i;
            XSSFCell row_title_cell = row_title.createCell(index);
            row_title_cell.setCellValue(months.get(i));
            row_title_cell.setCellStyle(cellStyleHead);
            sheet.setColumnWidth(index, 3000);
        }

        // 表体
        int rowIndex = 1;
        for (PoItemNewReportVO reportVO : voList) {
            XSSFRow row = sheet.createRow(rowIndex);

            List<PoItemReqRepotCurrMonthDTO> columnValues = reportVO.getColumnValues();
            Map<String,PoItemReqRepotCurrMonthDTO> map = new HashMap<>();
            for (PoItemReqRepotCurrMonthDTO columnValue : columnValues) {
                map.put(columnValue.getDate(),columnValue);
            }

            String type = reportVO.getType();
            CellStyle cellStyleType = wb.createCellStyle();
            cellStyleType.setBorderBottom(BorderStyle.THIN);
            cellStyleType.setBorderLeft(BorderStyle.THIN);
            cellStyleType.setBorderRight(BorderStyle.THIN);
            cellStyleType.setBorderTop(BorderStyle.THIN);
            if(type.equals("实际需求")){
                cellStyleType.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                cellStyleType.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());  //设置填充颜色
            }
            if(type.equals("预测需求")){
                cellStyleType.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                cellStyleType.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  //设置填充颜色
            }

            XSSFCell row_cell0 = row.createCell(0);
            row_cell0.setCellValue(reportVO.getProNo());
            row_cell0.setCellStyle(cellStyleType);

            XSSFCell row_cell1 = row.createCell(1);
            row_cell1.setCellValue(reportVO.getSupCode() + " " + reportVO.getSupName());
            row_cell1.setCellStyle(cellStyleType);

            XSSFCell row_cell2 = row.createCell(2);
            row_cell2.setCellValue(reportVO.getItemCode());
            row_cell2.setCellStyle(cellStyleType);

            XSSFCell row_cell3 = row.createCell(3);
            row_cell3.setCellValue(reportVO.getItemName());
            row_cell3.setCellStyle(cellStyleType);

            XSSFCell row_cell4 = row.createCell(4);
            row_cell4.setCellValue(reportVO.getType());
            row_cell4.setCellStyle(cellStyleType);

            XSSFCell row_cell5 = row.createCell(5);
            Integer total = reportVO.getTotal();
            if(total == null){
                row_cell5.setCellValue("");
            }else{
                row_cell5.setCellValue(total);
            }
            row_cell5.setCellStyle(cellStyleType);

            XSSFFont font = wb.createFont();
            XSSFFont fontNull = wb.createFont();
            font.setColor(IndexedColors.RED.getIndex());//设置excel数据字体颜色

            XSSFCell row_cell6 = row.createCell(6);
            Integer delayQties = reportVO.getDelayQties();
            if(delayQties > 0){
                CellStyle cellStyleType1 = wb.createCellStyle();
                cellStyleType1.setBorderBottom(BorderStyle.THIN);
                cellStyleType1.setBorderLeft(BorderStyle.THIN);
                cellStyleType1.setBorderRight(BorderStyle.THIN);
                cellStyleType1.setBorderTop(BorderStyle.THIN);
                if(type.equals("实际需求")){
                    cellStyleType1.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                    cellStyleType1.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());  //设置填充颜色
                }
                if(type.equals("预测需求")){
                    cellStyleType1.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                    cellStyleType1.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  //设置填充颜色
                }
                cellStyleType1.setFont(font);
                row_cell6.setCellStyle(cellStyleType1);
            }else{
                row_cell6.setCellStyle(cellStyleType);
            }
            row_cell6.setCellValue(delayQties);


            XSSFCell row_cell7 = row.createCell(7);
            row_cell7.setCellValue(reportVO.getPoCode() + " " + reportVO.getPoLn());
            row_cell7.setCellStyle(cellStyleType);



            for(int i = 0 ; i<days.size();i++){
                int index = 8 + i;
                XSSFCell row_cell = row.createCell(index);
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthDTO = map.get(days.get(i));
                if(poItemReqRepotCurrMonthDTO != null){
                    Integer qty = poItemReqRepotCurrMonthDTO.getQty();
                    Integer isMeetOptDate = poItemReqRepotCurrMonthDTO.getIsMeetOptDate();
                    if(qty > 0 && isMeetOptDate == 0){
                        CellStyle cellStyleType1 = wb.createCellStyle();
                        cellStyleType1.setBorderBottom(BorderStyle.THIN);
                        cellStyleType1.setBorderLeft(BorderStyle.THIN);
                        cellStyleType1.setBorderRight(BorderStyle.THIN);
                        cellStyleType1.setBorderTop(BorderStyle.THIN);
                        if(type.equals("实际需求")){
                            cellStyleType1.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                            cellStyleType1.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());  //设置填充颜色
                        }
                        if(type.equals("预测需求")){
                            cellStyleType1.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                            cellStyleType1.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  //设置填充颜色
                        }
                        cellStyleType1.setFont(font);
                        row_cell.setCellStyle(cellStyleType1);
                        row_cell.setCellValue(qty);
                    }else if(qty > 0 && isMeetOptDate == 1){
                        row_cell.setCellStyle(cellStyleType);
                        row_cell.setCellValue(qty);
                    }else{
                        row_cell.setCellStyle(cellStyleType);
                    }

                }else{
                    row_cell.setCellStyle(cellStyleType);
                }

            }

            for(int i = 0 ; i<months.size();i++){
                int index = 8 + days.size() + i;
                XSSFCell row_cell = row.createCell(index);
                PoItemReqRepotCurrMonthDTO poItemReqRepotCurrMonthDTO = map.get(months.get(i));
                if(poItemReqRepotCurrMonthDTO != null){
                    Integer qty = poItemReqRepotCurrMonthDTO.getQty();
                    Integer isMeetOptDate = poItemReqRepotCurrMonthDTO.getIsMeetOptDate();
                    if(qty > 0 && isMeetOptDate == 0){
                        CellStyle cellStyleType1 = wb.createCellStyle();
                        cellStyleType1.setBorderBottom(BorderStyle.THIN);
                        cellStyleType1.setBorderLeft(BorderStyle.THIN);
                        cellStyleType1.setBorderRight(BorderStyle.THIN);
                        cellStyleType1.setBorderTop(BorderStyle.THIN);
                        if(type.equals("实际需求")){
                            cellStyleType1.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                            cellStyleType1.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());  //设置填充颜色
                        }
                        if(type.equals("预测需求")){
                            cellStyleType1.setFillPattern(FillPatternType.SOLID_FOREGROUND);    //设置填充方案
                            cellStyleType1.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  //设置填充颜色
                        }
                        cellStyleType1.setFont(font);
                        row_cell.setCellStyle(cellStyleType1);
                        row_cell.setCellValue(qty);
                    }else if(qty > 0 && isMeetOptDate == 1){
                        row_cell.setCellStyle(cellStyleType);
                        row_cell.setCellValue(qty);
                    }else{
                        row_cell.setCellStyle(cellStyleType);
                    }
                }else{
                    row_cell.setCellStyle(cellStyleType);
                }
            }


            rowIndex ++ ;
        }

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("送货计划报表" + DateUtil.formatDate(new Date()) + ".xlsx", "UTF-8"));
            wb.write(response.getOutputStream());
        } catch (IOException e) {
            //throw new NormalException(e.getMessage());
        }
    }

    /**
     * getExportTableHead
     *
     * @param voList List
     * @param dateTitle List
     * @param wb XSSFWorkbook
     * @param response HttpServletResponse
     */
    private void getExportTableHead(List<PoItemNewReportVO> voList,List<PoItemReqRepotCurrMonthDTO> dateTitle, XSSFWorkbook wb, HttpServletResponse response){

        // 工作表
        XSSFSheet sheet = wb.createSheet("排程计划");
        CellStyle style = wb.createCellStyle();
        setPublicStyle(style);

        //表头内容
        getTableHead(dateTitle, sheet, style);

        //背景色
        CellStyle actualStyle = wb.createCellStyle();
        actualStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        actualStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setPublicStyle(actualStyle);

        CellStyle predictStyle = wb.createCellStyle();
        predictStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        predictStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setPublicStyle(predictStyle);

        //表行内容
        getTableBody(voList, wb, sheet, style, actualStyle, predictStyle);
    }

    /**
     * getTableBody
     *
     * @param voList List
     * @param wb XSSFWorkbook
     * @param sheet XSSFSheet
     * @param style CellStyle
     * @param actualStyle CellStyle
     * @param predictStyle CellStyle
     */
    private void getTableBody(List<PoItemNewReportVO> voList, XSSFWorkbook wb, XSSFSheet sheet, CellStyle style, CellStyle actualStyle, CellStyle predictStyle) {
        int createRow = 2;
        int firstRow = 2;
        int lastRow = 3;

        for (int i = 0; i < voList.size(); i++) {
            //合并行
            XSSFRow row2 = sheet.createRow(createRow);
            row2.setHeight((short)500);
            XSSFCell title21 = row2.createCell(0);
            title21.setCellStyle(style);
            title21.setCellValue(voList.get(i).getSupCode());
            CellRangeAddress region21 = new CellRangeAddress(firstRow, lastRow, 0, 0);
            sheet.addMergedRegion(region21);
            XSSFCell title22 = row2.createCell(1);
            title22.setCellStyle(style);
            title22.setCellValue(voList.get(i).getSupName());
            CellRangeAddress region22 = new CellRangeAddress(firstRow, lastRow, 1, 1);
            sheet.addMergedRegion(region22);
            XSSFCell title23 = row2.createCell(2);
            title23.setCellStyle(style);
            title23.setCellValue(voList.get(i).getItemCode());
            CellRangeAddress region23 = new CellRangeAddress(firstRow, lastRow, 2, 2);
            sheet.addMergedRegion(region23);
            XSSFCell title24 = row2.createCell(3);
            title24.setCellStyle(style);
            title24.setCellValue(voList.get(i).getItemName());
            CellRangeAddress region24 = new CellRangeAddress(firstRow, lastRow, 3, 3);
            sheet.addMergedRegion(region24);

            //实际行
            XSSFCell cell3 = row2.createCell(4);
            cell3.setCellStyle(actualStyle);
            cell3.setCellValue(voList.get(i).getActualColumnVo().getType());
            XSSFCell cell4 = row2.createCell(5);
            cell4.setCellStyle(actualStyle);
            cell4.setCellValue(voList.get(i).getActualColumnVo().getPoCodeAndPoLn());
            XSSFCell cell5 = row2.createCell(6);
            cell5.setCellStyle(actualStyle);
            cell5.setCellValue(voList.get(i).getActualColumnVo().getProNo());
            XSSFCell cell6 = row2.createCell(7);
            cell6.setCellStyle(actualStyle);
            cell6.setCellValue(voList.get(i).getActualColumnVo().getTotal() == null ? null : voList.get(i).getActualColumnVo().getTotal().toString());

            int count1 = voList.get(i).getActualColumnVo().getColumnValues().size() - 1;
            for (int j = 0; j <= count1; j++) {
                XSSFCell cell7 = row2.createCell(j + 8);
                if(voList.get(i).getActualColumnVo().getColumnValues().get(j).getIsMeetOptDate() == 0){
                    CellStyle cellStyle = wb.createCellStyle();
                    cellStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    setPublicStyle(cellStyle);
                    XSSFFont font = (XSSFFont) wb.createFont();
                    font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
                    cellStyle.setFont(font);
                    cell7.setCellStyle(cellStyle);
                }else{
                    cell7.setCellStyle(actualStyle);
                }
                cell7.setCellValue(voList.get(i).getActualColumnVo().getColumnValues().get(j).getQty() == 0
                    ? null : voList.get(i).getActualColumnVo().getColumnValues().get(j).getQty().toString());
            }

            //预测行
            XSSFRow row21 = sheet.createRow(createRow + 1);
            row21.setHeight((short)500);
            XSSFCell cell31 = row21.createCell(4);
            cell31.setCellStyle(predictStyle);
            cell31.setCellValue(voList.get(i).getPredictColumnVo().getType());
            XSSFCell cell41 = row21.createCell(5);
            cell41.setCellStyle(predictStyle);
            cell41.setCellValue(voList.get(i).getPredictColumnVo().getPoCodeAndPoLn());
            XSSFCell cell51 = row21.createCell(6);
            cell51.setCellStyle(predictStyle);
            cell51.setCellValue(voList.get(i).getPredictColumnVo().getProNo());
            XSSFCell cell61 = row21.createCell(7);
            cell61.setCellStyle(predictStyle);
            cell61.setCellValue(voList.get(i).getPredictColumnVo().getTotal() == null ? null : voList.get(i).getPredictColumnVo().getTotal().toString());

            int count11 = voList.get(i).getPredictColumnVo().getColumnValues().size() - 1;
            for (int j = 0; j <= count11; j++) {
                XSSFCell cell71 = row21.createCell(j + 8);
                if(voList.get(i).getActualColumnVo().getColumnValues().get(j).getIsMeetOptDate() == 0){
                    CellStyle cellStyle = wb.createCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    cellStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    XSSFFont font = (XSSFFont) wb.createFont();
                    font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
                    cellStyle.setFont(font);
                    cell71.setCellStyle(cellStyle);
                }else{
                    cell71.setCellStyle(predictStyle);
                }
                cell71.setCellValue(voList.get(i).getPredictColumnVo().getColumnValues().get(j).getQty() == 0
                    ? null : voList.get(i).getPredictColumnVo().getColumnValues().get(j).getQty().toString());
            }

            createRow = createRow + 2;
            firstRow = firstRow + 2;
            lastRow = lastRow + 2;

        }
    }

    /**
     *
     *
     * @param dateTitle List
     * @param sheet XSSFSheet
     * @param style CellStyle
     */
    private void getTableHead(List<PoItemReqRepotCurrMonthDTO> dateTitle, XSSFSheet sheet, CellStyle style) {
        XSSFRow row1 = sheet.createRow(0);
        row1.setHeight((short)500);

        XSSFCell title1 = row1.createCell(0);
        title1.setCellStyle(style);
        title1.setCellValue("供应商编码");
        CellRangeAddress region1 = new CellRangeAddress(0, 1, 0, 0);
        sheet.addMergedRegion(region1);

        XSSFCell title2 = row1.createCell(1);
        title2.setCellStyle(style);
        title2.setCellValue("供应商名称");
        CellRangeAddress region2 = new CellRangeAddress(0, 1, 1, 1);
        sheet.addMergedRegion(region2);

        XSSFCell title3 = row1.createCell(2);
        title3.setCellStyle(style);
        title3.setCellValue("料号");
        CellRangeAddress region3 = new CellRangeAddress(0, 1, 2, 2);
        sheet.addMergedRegion(region3);

        XSSFCell title4 = row1.createCell(3);
        title4.setCellStyle(style);
        title4.setCellValue("物料描述");
        CellRangeAddress region4 = new CellRangeAddress(0, 1, 3, 3);
        sheet.addMergedRegion(region4);

        XSSFCell title5 = row1.createCell(4);
        title5.setCellStyle(style);
        //title5.setCellValue("总数量");
        title5.setCellValue("类型");
        CellRangeAddress region5 = new CellRangeAddress(0, 1, 4, 4);
        sheet.addMergedRegion(region5);

        XSSFCell title6 = row1.createCell(5);
        title6.setCellStyle(style);
        title6.setCellValue("订单号");
        CellRangeAddress region6 = new CellRangeAddress(0, 1, 5, 5);
        sheet.addMergedRegion(region6);

        XSSFCell title7 = row1.createCell(6);
        title7.setCellStyle(style);
        title7.setCellValue("项目号");
        CellRangeAddress region7 = new CellRangeAddress(0, 1, 6, 6);
        sheet.addMergedRegion(region7);

        XSSFCell title8 = row1.createCell(7);
        title8.setCellStyle(style);
        //title8.setCellValue("类型");
        title8.setCellValue("总数量");
        CellRangeAddress region8 = new CellRangeAddress(0, 1, 7, 7);
        sheet.addMergedRegion(region8);

        XSSFCell cell1 = row1.createCell(8);
        cell1.setCellStyle(style);
        cell1.setCellValue("当月需求");
        int count = dateTitle.size() - 1 - 6;
        XSSFRow row11 = sheet.createRow(1);
        row11.setHeight((short)500);
        for (int i = 0; i <= count; i++) {
            XSSFCell cell = row11.createCell(i + 8);
            cell.setCellStyle(style);
            cell.setCellValue(dateTitle.get(i).getDate().substring(5));
        }
        CellRangeAddress region9 = new CellRangeAddress(0, 0, 8, 8  + count);
        sheet.addMergedRegion(region9);

        XSSFCell cell2 = row1.createCell(8 + count + 1);
        cell2.setCellStyle(style);
        cell2.setCellValue("半年后需求");
        int count2 = 6;
        int index = dateTitle.size() - 6;
        for (int i = 0; i < count2; i++) {
            XSSFCell cell = row11.createCell(i + 8 + count + 1);
            cell.setCellStyle(style);
            cell.setCellValue(dateTitle.get(index + i).getDate().substring(5) + "月");
        }
        CellRangeAddress region10 = new CellRangeAddress(0, 0, 8 + count + 1, 8 + count  + count2);
        sheet.addMergedRegion(region10);
    }

    /**
     * 设置单元格公用属性
     *
     * @param style CellStyle
     */
    private void setPublicStyle(CellStyle style) {
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }


    @Override
    public boolean poItemDownload(HttpServletResponse response){
        List<PoItemReqRepotVO> voList = PoItemCache.getVoList();
        if(StringUtil.isEmpty(voList)){
            throw new RuntimeException("读取失败");
        }
        List<PoItemReqRepotCurrMonthDTO> dateTitle = voList.get(0).getColumnValues();
        List<ExcelExportEntity> entity = PoItemCache.getAllEntity();
        //List<Map<String, Object>> list = PoItemCache.getValueList();
        List<Map<String, Object>> list = getValueList(voList, dateTitle, entity);
        ExportParams params = new ExportParams("", "", ExcelType.XSSF);
        params.setStyle(ExcelExportStatisticStyler.class);
        Workbook workbook = ExcelExportUtil.exportExcel(params, entity, list);
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("供应计划排程" + DateUtil.formatDate(new Date()) + ".xls", "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            //throw new NormalException(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean newReportDownload(HttpServletResponse response) {
        List<PoItemNewReportVO> voList = PlanReqPoItemCache.getExportVoList();
        if(StringUtil.isEmpty(voList)){
            throw new RuntimeException("读取失败");
        }
        List<PoItemReqRepotCurrMonthDTO> dateTitle = voList.get(0).getColumnValues();
        XSSFWorkbook wb = new XSSFWorkbook();
        getExportTableHead(voList, dateTitle, wb, response);
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("供应计划排程" + DateUtil.formatDate(new Date()) + ".xls", "UTF-8"));
            wb.write(response.getOutputStream());
        } catch (IOException e) {
            //throw new NormalException(e.getMessage());
        }
        return true;
    }


    /**
     * 获取供应计划排程全集
     * @param poItemEntity PoItemEntity
     * @return List
     */
    private
    List<PoItemReqRepotVO> getAllReqRepot(PoItemEntity poItemEntity){
        List<PoItemReqRepotTotal> totalList = this.baseMapper.getPoItemReqRepotTotal(poItemEntity);
        return getVoList(totalList);
    }


    /**
     * getVoList
     *
     * @param totalList List
     * @return List
     */
    @Override
    public List<PoItemReqRepotVO> getVoList(List<PoItemReqRepotTotal> totalList){
        List<PoItemReqRepotVO> voList = Lists.newArrayList();
        for(PoItemReqRepotTotal total : totalList){
            PoItemReqRepotVO vo = new PoItemReqRepotVO(
                total.getSupCode(),
                total.getSupName(),
                total.getItemCode(),
                total.getItemName(),
                total.getTotal(),
                total.getProNo(),
                this.baseMapper.getPoItems(total.getSupCode(), total.getItemCode())
            );
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            //后推21天
            getLatelyData(total, vo, calendar);
            List<PoItemReqRepotCurrMonthDTO> predictValues = Lists.newArrayList();
            //下半年需求量、预测量
            getNextHalfYear(total, vo, calendar, predictValues);
            vo.getColumnValues().addAll(predictValues);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public List<PoItemReqRepotTotal> getTotalList(PoItemEntity poItemEntity) {
        return this.baseMapper.getPoItemReqRepotTotal(poItemEntity);
    }

    @Override
    public List<PoItemReqRepotTotal> getNewTotalList(PoItemEntity poItemEntity) {
        return this.baseMapper.getNewReportTotal(poItemEntity);
    }

    @Override
    public void putPoItemTask() {
        PoItemCache.putVoList();
        PoItemCache.putAllEntity();
        PoItemCache.putValueList();
    }

    @Override
    public void newPutPoItemTask() {
        PlanReqPoItemCache.putVoList();
        PlanReqPoItemCache.putAllEntity();
        PlanReqPoItemCache.putValueList();
        PlanReqPoItemCache.putExportVoList();
    }

    @Override
    public List<ExcelExportEntity> getAllEntity(List<PoItemReqRepotCurrMonthDTO> dateTitle) {
        List<ExcelExportEntity> entity = new ArrayList<>();
        entity.add(buildExcelEntity("供应商编码", "supCode", 0, 20));
        entity.add(buildExcelEntity("供应商名称", "supName", 1, 20));
        entity.add(buildExcelEntity("料号", "itemCode", 2, 20));
        entity.add(buildExcelEntity("物料描述", "itemName", 3, 40));
        entity.add(buildExcelEntity("总数量", "total", 4, 20));
        entity.add(buildExcelEntity("项目号", "proNo", 5, 20));
        entity.add(buildExcelEntity("订单号", "poItems", 6, 20));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int toDay = calendar.get(Calendar.DATE);
        //分组
        for (int i = 0; i < dateTitle.size(); i++) {
            if (i < dateTitle.size() - 12) {
                String date = dateTitle.get(i).getDate().substring(5);
                entity.add(buildExcelEntityByGroup(date, date, "当月需求量", 6, 10));
            } else {
                //entity.add(buildExcelEntityByGroup(dateTitle.get(i).getDate(), dateTitle.get(i).getDate(), dateTitle.get(i).getDate().contains("/") ? "未来半年预测量" : "未来半年需求量", 7, 10));
                if(dateTitle.get(i).getDate().contains("/")){
                    entity.add(buildExcelEntityByGroup(dateTitle.get(i).getDate(), dateTitle.get(i).getDate(), "未来半年预测量" , 8, 10));
                }else{
                    entity.add(buildExcelEntityByGroup(dateTitle.get(i).getDate(), dateTitle.get(i).getDate(), "未来半年需求量" , 7, 10));
                }
            }
        }
        return entity;
    }

    @Override
    public List<Map<String, Object>> getValueList(List<PoItemReqRepotVO> voList, List<PoItemReqRepotCurrMonthDTO> dateTitle, List<ExcelExportEntity> entity) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PoItemReqRepotVO vo : voList) {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("supCode", vo.getSupCode());
            params.put("supName", vo.getSupName());
            params.put("itemCode", vo.getItemCode());
            params.put("itemName", vo.getItemName());
            params.put("total", vo.getTotal());
            params.put("proNo", vo.getProNo());
            List<PoItemEntity> poItemEntities = vo.getPoItems();
            StringBuilder sb = new StringBuilder();
            if(!StringUtil.isEmpty(poItemEntities)){
                for (int i = 0; i < poItemEntities.size(); i++) {
                    if(!StringUtil.isEmpty(poItemEntities.get(i))){
                        String poCode = "";
                        String poLn = "";
                        if (StringUtil.isNotBlank(poItemEntities.get(i).getPoCode())) {
                            poCode = poItemEntities.get(i).getPoCode();
                        }
                        if (!StringUtil.isEmpty(poItemEntities.get(i).getPoLn())) {
                            poLn = String.valueOf(poItemEntities.get(i).getPoLn());
                        }
                        sb.append(poCode).append(" ").append(poLn);
                        if (i < poItemEntities.size() - 1) {
                            sb.append("\n");
                        }
                    }
                }
            }
            params.put("poItems", sb.toString());
            List<PoItemReqRepotCurrMonthDTO> columnValues = vo.getColumnValues();
            for (int i = 0; i < columnValues.size(); i++) {
                if (columnValues.get(i).getIsMeetOptDate() == 0) {
                    for (ExcelExportEntity excelExportEntity : entity) {
                        //设置默认(防止从缓存中读取为true)
                        excelExportEntity.setWrap(false);
                        if (columnValues.get(i).getDate().contains(excelExportEntity.getName())) {
                            excelExportEntity.setWrap(true);
                        }
                    }
                }
                if (i < dateTitle.size() - 12) {
                    String date = columnValues.get(i).getDate().substring(5);
                    params.put(date, columnValues.get(i).getQty() == 0 ? "" : columnValues.get(i).getQty());
                } else {
                    params.put(columnValues.get(i).getDate(), columnValues.get(i).getQty() == 0 ? "" : columnValues.get(i).getQty());
                }
            }
            list.add(params);
        }
        return list;
    }

    @Override
    public IPage<PoItemVO> getCraftCtrlPage(IPage<PoItemEntity> page, PoItemDTO poItem) {
        IPage<PoItemEntity> entityPage = this.baseMapper.getPoItemEntityPage(page, poItem);
        IPage<PoItemVO> retPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<PoItemVO> voList = Lists.newArrayList();

        // 针对球体查询的特殊处理
        if (((poItem.getSupCode() != null && !poItem.getSupCode().isEmpty()) || (poItem.getSupName() != null && !poItem.getSupName().isEmpty() && poItem.getItemName() != null)) && poItem.getItemName().equals("球")) {

            List<PoItemEntity> qzList = entityPage.getRecords();
            poItem.setItemName("阀座");
            page.setSize(1000000);
            page.setCurrent(1);
            IPage<PoItemEntity> fzPage = getPoItemEntityIPage(page, poItem);
            List<PoItemEntity> fzList = fzPage.getRecords();
            List<PoItemEntity> qtFzList = this.getQTFZListOfEntity(poItem, qzList,fzList);

            for(PoItemEntity entity : qtFzList){
                PoItemVO vo = PoItemWrapper.build().entityVO(entity);
                PoEntity po = poService.getById(vo.getPoId());
                vo.setCodeType(this.baseMapper.getABCType(entity.getItemCode()));
                vo.setPoStatus(po.getStatus());
                //获取工艺卡控进度
                getCraftCtrl(vo);
                voList.add(vo);
            }

        } else {

            for (PoItemEntity entity : entityPage.getRecords()) {
                PoItemVO vo = PoItemWrapper.build().entityVO(entity);
                PoEntity po = poService.getById(vo.getPoId());
                vo.setCodeType(this.baseMapper.getABCType(entity.getItemCode()));
                vo.setPoStatus(po.getStatus());
                //获取工艺卡控进度
                getCraftCtrl(vo);
                voList.add(vo);
            }
        }

        retPage.setRecords(voList);
        return retPage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean savesCraftCtrl(List<PoItemNodeReq> poItemNodeReqs) {
        for (PoItemNodeReq poItemNodeReq : poItemNodeReqs) {
            for (PoItemCraftCtrlNodeVO temp : poItemNodeReq.getPoItemCraftCtrlNodeVos()) {
                PoItemCraftCtrlNodeEntity entity = poItemCraftCtrlNodeService.getById(temp.getId());
                entity.setPlanConfirmDate(!StringUtil.isEmpty(temp.getPlanConfirmDate()) ? temp.getPlanConfirmDate() : 0);
                entity.setIsComplete(temp.getIsComplete());
                entity.setPurchRemark(temp.getPurchRemark());
                poItemCraftCtrlNodeService.updateById(entity);
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateCraftCtrl(PoItemNodeReq poItemNodeReq) {
        for (PoItemCraftCtrlNodeVO temp : poItemNodeReq.getPoItemCraftCtrlNodeVos()) {
            List<PoItemCraftCtrlNodeEntity> entities = poItemCraftCtrlNodeService.getByParentId(poItemNodeReq.getCraftCtrlNodeId());
            for (PoItemCraftCtrlNodeEntity entity : entities) {
                if (temp.getId().equals(entity.getId())) {
                    entity.setPlanConfirmDate(!StringUtil.isEmpty(temp.getPlanConfirmDate()) ? temp.getPlanConfirmDate() : 0);
                    entity.setIsComplete(temp.getIsComplete());
                    entity.setPurchRemark(temp.getPurchRemark());
                    poItemCraftCtrlNodeService.updateById(entity);
                }
            }
        }
        return true;
    }

    @Override
    public void craftCtrlExport(PoItemDTO poItem, HttpServletResponse response) throws Exception {
        List<PoItemEntity> poItemEntities = this.baseMapper.getPoItemEntity(poItem);

        // 针对球体查询的特殊处理
        if (((poItem.getSupCode() != null && !poItem.getSupCode().isEmpty()) || (poItem.getSupName() != null && !poItem.getSupName().isEmpty() && poItem.getItemName() != null)) && poItem.getItemName().equals("球")) {
            poItem.setItemName("阀座");
            List<PoItemEntity> fzList = this.baseMapper.getPoItemEntity(poItem);
            List<PoItemEntity> qtFzList = this.getQTFZListOfEntity(poItem, poItemEntities,fzList);
            poItemEntities = qtFzList;
        }

        List<PoItemNodeDTO> dtos = Lists.newArrayList();
        for (PoItemEntity entity : poItemEntities) {
            PoItemVO vo = PoItemWrapper.build().entityVO(entity);
            PoEntity po = poService.getById(vo.getPoId());
            if(po!=null) {
                vo.setPoStatus(po.getStatus());
            }
            PoItemNodeDTO poItemNodeDTO = BeanUtil.copy(vo , PoItemNodeDTO.class);
            poItemNodeDTO.setPoItemNodeList(poItemCraftCtrlNodeService.getDTOS(poItemNodeDTO.getId()));
            Item item = itemService.getByCode(poItemNodeDTO.getItemCode());
            if(item!=null && item.getCraftctrlName()!=null){
                poItemNodeDTO.setCraftCtrlNodeName(StringUtil.isEmpty(item.getCraftctrlName()) ? null : item.getCraftctrlName());
            }
            dtos.add(poItemNodeDTO);
        }
        ExcelUtils.defaultExport(dtos, PoItemNodeDTO.class, "工艺卡控" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public List<PoItemEntity> getPoItemEntity(PoItemDTO poItem) {
        return this.baseMapper.getPoItemEntity(poItem);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateReqDateBatch(List<PoItemDTO> poItemDTOS) {
        poItemDTOS.forEach(dto ->{
            PoItemEntity poItemEntity = getById(dto.getId());
            poItemEntity.setReqDate(dto.getReqDate());
            updateById(poItemEntity);
        });
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updatePromiseDateBatch(List<PoItemDTO> poItemDTOS) {
        poItemDTOS.forEach(dto ->{
            PoItemEntity poItemEntity = getById(dto.getId());
            poItemEntity.setSupConfirmDate(dto.getSupConfirmDate());
            updateById(poItemEntity);
        });
        return true;
    }

    @Override
    public boolean updatePromiseDateBatchToU9(List<PoItemDTO> poItemDTOS) {
        if(poItemDTOS.size()==0){
            return true;
        }


        List<PromiseDateDTO> promiseDateDTOS=new ArrayList<>();
        poItemDTOS.forEach(dto ->{
            PoItemEntity poItemEntity = getById(dto.getId());
            poItemEntity.setSupConfirmDate(dto.getSupConfirmDate());
            poItemEntity.setIsReserve("N");
            updateById(poItemEntity);
            promiseDateDTOS.add(new PromiseDateDTO(poItemEntity.getPoCode(),String.valueOf(poItemEntity.getPoLn()),dto.getSupConfirmDate(),poItemEntity.getOrgCode()));

        });

        /*调用接口
        [{
                "DocNo":"PO0012302250001",
                "DocLineNo":"10",
                "ConfirmDate":"123465",
                "OrgCode":"001"

        }]*/
        String jsonString = JSON.toJSONString(promiseDateDTOS);
        String res = WillHttpUtil.postJson(atwSrmConfiguration.getU9ApiDomain() + "/api/ModifyPOConfirmDate", jsonString,600L);
        if(res.isEmpty()){
            throw  new RuntimeException("调用U9接口超时");
        }else {
            org.json.JSONObject returnJson = new org.json.JSONObject(res);
            boolean IsSuccess= returnJson.getBoolean("IsSuccess");
            if (!IsSuccess){
                throw  new RuntimeException("调用U9接口失败："+res);
            }

        }

        //更新预PO订单状态
        updatePoStatus();

        return true;
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean batchUpdateIsUrgent(List<PoItemDTO> poItemDTOS) {
        poItemDTOS.forEach(dto ->{
            if(dto.getIsUrgent().equals(1)){
                this.baseMapper.setIsUrgent(dto.getPrCode(),dto.getPrLn().toString());
            } else {
                this.baseMapper.cancelIsUrgent(dto.getPrCode(),dto.getPrLn().toString());
            }
        });
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitPrice(List<PoItemEntity> poItemEntityList) {
        //修改后的订单总金额
        BigDecimal docAmount = BigDecimal.ZERO;
        PoEntity poEntity = poService.getById(poItemEntityList.get(0).getPoId());
        if(poEntity == null){
            throw new RuntimeException("未找到PO，ID：" + poItemEntityList.get(0).getPoId());
        }
        if(poEntity.getStatus().equals(IPoService.STATUS_PRICE_WAIT) || poEntity.getStatus().equals(IPoService.STATUS_PRICE_PASS)){
            throw new RuntimeException("该PO单已提交审核或已通过审核");
        }
        for (PoItemEntity poItemEntity : poItemEntityList) {
            docAmount = docAmount.add(poItemEntity.getAmount());
            PoItemEntity entity = getById(poItemEntity.getId());
            entity.setWeight(poItemEntity.getWeight());
            entity.setPriceUpdate(poItemEntity.getPriceUpdate());
            entity.setAmountUpdate(poItemEntity.getAmountUpdate());
            entity.setUpdatePriceRemark(poItemEntity.getUpdatePriceRemark());
            updateById(entity);
        }
        poEntity.setDocAmountUpdate(docAmount);
        poEntity.setStatus(IPoService.STATUS_PRICE_WAIT);
        return poService.updateById(poEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateDelDateBatch(List<PoItemEntity> poItemEntityList) {
        for (PoItemEntity poItemEntity : poItemEntityList) {
            updateById(poItemEntity);
        }
        return true;
    }

    @Override
    public PoItemEntity getByPoCodeAndPoLn(String poCode, Integer poLn) {
        PoItemDTO poItem = new PoItemDTO();
        if(StringUtil.isNotBlank(poCode)){
            poItem.setPoCode(poCode);
        }
        if(poLn != null){
            poItem.setPoLn(poLn);
        }
        return getOne(getQueryWrapper(poItem));
    }

    /**
     * 获取下半年
     *
     * @param total PoItemReqRepotTotal
     * @param vo PoItemReqRepotVO
     * @param calendar Calendar
     * @param predictValues List
     */
    private void getNextHalfYear(PoItemReqRepotTotal total, PoItemReqRepotVO vo, Calendar calendar, List<PoItemReqRepotCurrMonthDTO> predictValues) {
        for (int i =  calendar.get(Calendar.MONTH) + 7 ; i >= calendar.get(Calendar.MONTH) + 2; i--) {
            String timeCurr;
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    timeCurr = calendar.get(Calendar.YEAR) + "-0" + i;
                    break;
                case 10:
                case 11:
                case 12:
                    timeCurr = calendar.get(Calendar.YEAR) + "-" + i;
                    break;
                default:
                    timeCurr = (calendar.get(Calendar.YEAR) + 1) + "-0" + (i - 12);
                    break;
            }
            //需求量
            PoItemReqRepotCurrMonthDTO dto = this.baseMapper.getNexHalfYear(
                total.getSupCode(), total.getItemCode(), timeCurr);
            if (dto != null) {
                vo.getColumnValues().add(dto);
            } else {
                vo.getColumnValues().add(new PoItemReqRepotCurrMonthDTO(timeCurr));
            }
            //预测量
            PoItemReqRepotCurrMonthDTO dtoPredict = this.baseMapper.getNexHalfYearPredict(
                total.getSupCode(), total.getItemCode(), timeCurr.replace("-", "/"));
            if (dtoPredict != null) {
                predictValues.add(dtoPredict);
            } else {
                predictValues.add(new PoItemReqRepotCurrMonthDTO(timeCurr.replace("-", "/")));
            }
        }
    }

    /**
     * 获取后推21天（直到月底）
     *
     * @param total PoItemReqRepotTotal
     * @param vo PoItemReqRepotVO
     * @param calendar Calendar
     */
    private void getLatelyData(PoItemReqRepotTotal total, PoItemReqRepotVO vo, Calendar calendar) {
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int today = calendar.get(Calendar.DATE);
        for (int i = today + 20; i >= today; i--) {
            if (i <= maxDay) {
                String fullDate = getStringDay(calendar, i);

                PoItemReqRepotCurrMonthDTO dto = this.baseMapper.getCurrMouth(total.getSupCode(), total.getItemCode(), fullDate);
                if (dto != null) {
                    boolean isMeet = poPronoService.isMeetOptDate(total.getSupCode(), total.getItemCode(), fullDate);
                    dto.setIsMeetOptDate(isMeet ? 1 : 0);
                    vo.getColumnValues().add(dto);
                } else {
                    vo.getColumnValues().add(new PoItemReqRepotCurrMonthDTO(fullDate));
                }
            }
        }
    }



    /**
     * 在excel构建单列
     * @param name    String
     * @param key     Object
     * @param orderNo int
     * @param width   double
     * @return ExcelExportEntity
     */
    private static
    ExcelExportEntity buildExcelEntity(String name, Object key, int orderNo, double width){
        ExcelExportEntity excelEntity = new ExcelExportEntity(name, key);
        excelEntity.setOrderNum(orderNo);
        excelEntity.setWidth(width);
        return excelEntity;
    }

    /**
     * 构建分组列
     * @param name    String
     * @param key     Object
     * @param group   String
     * @param orderNo int
     * @param width   double
     * @return ExcelExportEntity
     */
    private static
    ExcelExportEntity buildExcelEntityByGroup(String name, Object key, String group, int orderNo, double width){
        ExcelExportEntity excelEntity = new ExcelExportEntity(name, key);
        excelEntity.setGroupName(group);
        excelEntity.setOrderNum(orderNo);
        excelEntity.setWidth(width);
        return excelEntity;
    }

    /**
     * 统计所有项目号
     * @return
     */
    @Override
    public
    String connectProNoByPoCode(Set<String> orderCodes){
        return this.baseMapper.connectProNoByPoCode(orderCodes);
    }

    @Override
    public
    List<String> getRemindPoCodes(int days){
        return baseMapper.getRemindPoCodes(days);
    }

    /**
     * 获取工艺卡控进度
     *
     * @param vo PoItemVO
     */
    private void getCraftCtrl(PoItemVO vo) {
        List<PoItemCraftCtrlNodeVO> list = poItemCraftCtrlNodeService.getByPoItemId(vo.getId());
        vo.setPoItemCraftCtrlNodeVos(list);
        Item item = itemService.getByCode(vo.getItemCode());
        vo.setCraftCtrlNodeName(StringUtil.isEmpty(item.getCraftctrlName()) ? null : item.getCraftctrlName());
        if (!StringUtil.isEmpty(item.getCraftctrlCode())) {
            CraftCtrlNodeEntity craftCtrlNodeEntity = craftCtrlNodeService.getByCode(item.getCraftctrlCode());
            vo.setCraftCtrlNodeId(StringUtil.isEmpty(craftCtrlNodeEntity) ? null : craftCtrlNodeEntity.getId());
        }
    }

    /**
     * 取得历史订单的价格
     * @param page
     * @param
     * @return
     */
    @Override
    public IPage<PoItemVO> getHistoryPrice(IPage<PoItemVO> page, String itemCode) {
        return page.setRecords(baseMapper.getHistoryPrice(page, itemCode));
    }




    @Override
    public boolean sendEmail(String ids) {
        List<String> idArr = new ArrayList<>(Arrays.asList(ids.split(",")));
        List<PoItemEntity> poItemEntities = new ArrayList<>();

        for(String id : idArr){
            PoItemEntity poItemEntity = getById(id);
            if(poItemEntity != null) {
                poItemEntities.add(poItemEntity);
            }
        }

        boolean emailFlag = false;
        String CC = cc;
        String purchEmail = this.baseMapper.getPurchEmail(poItemEntities.get(0).getSupCode());
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        if (!purchEmail.isEmpty() && purchEmail.matches(regEx1)) {
            CC = cc + ";" + purchEmail;
        } else {
            CC = cc + ";" + cg;
            emailFlag = true;
        }

        try {
            // 处理文件
            HttpPost httpPost = new HttpPost(url);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            // 主题
            String subject = "安特威领料扣账 : " + poItemEntities.get(0).getPoCode();
            // 内容
            StringBuilder content = new StringBuilder("<html>" +
                "<head></head>" +
                "<style type=\"text/css\">" +
                "table {border-collapse:collapse;border:1px solid #000000} td,th{border:1px solid #000000;}" +
                "</style>"+
                "<body>" +
                "<h2>您好，请在以下工单中领料扣账，谢谢~</h2>");
            content.append("<table border=\"1\" style=\"font-size:15px;width:85%\">");

            content.append("<tr style=\"background-color:#428BCA;color:#000000;font-family:宋体;font-size:13.5px;height:25px;text-align:left;\">" +
                "<th style=\"width:250px\">PO单号</th>" +
                "<th style=\"width:100px\">PO行号</th>" +
                "<th style=\"width:250px\">工单号</th>" +
                "<th style=\"width:150px\">料号</th>" +
                "<th style=\"width:450px\">料品名称</th>" +
                "<th style=\"width:100px\">扣账数量</th>" +
                "</tr>");
            for (PoItemEntity po: poItemEntities) {
                content.append("<tr>");
                content.append("<td>"+ po.getPoCode() +"</td>");
                content.append("<td>"+ po.getPoLn()+"</td>");
                content.append("<td>"+ po.getMoNo() +"</td>");
                content.append("<td>"+ po.getItemCode()+"</td>");
                content.append("<td>"+ po.getItemName() +"</td>");
                content.append("<td>"+ po.getPriceNum() +"</td>");
                content.append("</tr>");
            }
            content.append("</br>");
            content.append("</table>");
            content.append("</body></html>");

            // 如果有附件，需要 用 MultipartEntityBuilder
            MultipartEntityBuilder entity = MultipartEntityBuilder.create();
            ContentType TEXT_PLAIN = ContentType.create("text/plain", Charset.forName("UTF-8"));
            entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.setCharset(Charset.forName("UTF-8"));
            entity.addTextBody("apiUser", apiUser, TEXT_PLAIN);
            entity.addTextBody("apiKey", apiKey, TEXT_PLAIN);
            entity.addTextBody("to", to, TEXT_PLAIN);
            entity.addTextBody("cc", CC, TEXT_PLAIN);
            entity.addTextBody("from", from, TEXT_PLAIN);
            entity.addTextBody("fromName", "安特威数字化采购平台", TEXT_PLAIN);
            entity.addTextBody("subject", subject, TEXT_PLAIN);
            entity.addTextBody("html", content.toString(), TEXT_PLAIN);

            // 发送邮件
            httpPost.setEntity(entity.build());
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println(EntityUtils.toString(response.getEntity()));
            } else {
                System.err.println("error");
            }
            httpPost.releaseConnection();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("发送邮件失败！");
        }  finally {
            if(emailFlag){
                throw new RuntimeException("该供应商的采购员邮件信息没有完善,已经将邮件抄送到 " + cg + " 处了");
            }
        }
        return true;
    }


    @Override
    public boolean batchSetUpdateCheckDate(String ids) {
        List<String> idArr = new ArrayList<>(Arrays.asList(ids.split(",")));
        String account = getUser().getAccount();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for(String id : idArr){
            PoItemEntity poItemEntity = getById(id);
            if(poItemEntity != null) {
                CaiGouSchedule schedule = new CaiGouSchedule();

                schedule.setPoCode(poItemEntity.getPoCode());
                schedule.setPoLn(poItemEntity.getPoLn().toString());
                schedule.setCheckUpdateDate(new Date(poItemEntity.getSupConfirmDate() * 1000L));
                schedule.setCheckUpdateDateFrist(new Date(poItemEntity.getSupConfirmDate() * 1000L));
                schedule.setLimits("0");
                supplierScheduleMapper.insertCaiGou(schedule,account,df.format(new Date()));
            }
        }
        return true;
    }

    @Override
    public boolean updatePromiseDateBatchFromShjh(List<PoItemDTO> poItemDTOS) {
        for (PoItemEntity poItemEntity:poItemDTOS) {
            //不是关键物料直接跳过
            String itemCode = poItemEntity.getItemCode();
            String itemName = poItemEntity.getItemName();
            if (!itemCode.startsWith("15110")
                && !itemCode.startsWith("130301")
                && !itemCode.startsWith("130302")
                && !itemCode.startsWith("130101")
                && !itemCode.startsWith("130102")
                && !itemCode.startsWith("131111")
                && !itemCode.startsWith("131106")
                && itemName.indexOf("锻")<0
            ) {
                continue;

            }

            CaiGouSchedule locked=null;

            locked = this.baseMapper.isLocked(poItemEntity.getPrCode(), String.valueOf(poItemEntity.getPrLn()));


            if(locked!=null){
                Date reqDate = locked.getReqDate();
                long reqDate_sjc = reqDate.getTime()/1000;
                this.baseMapper.updateSupConfirmDate(reqDate_sjc,poItemEntity.getPrCode(), String.valueOf(poItemEntity.getPrLn()));
            }else{
                this.baseMapper.updateSupConfirmDate(0L,poItemEntity.getPrCode(), String.valueOf(poItemEntity.getPrLn()));
            }
        }

        return true;
    }

    @Override
    public void updatePoStatus() {

        List<PoEntity> poEntities = this.baseMapper.selectUnLockPo();
        for (PoEntity po:poEntities) {
            this.baseMapper.updateIsReserve(po.getId());
            //邮件提醒
            QueueEmailEntity queueEmailEntity = new QueueEmailEntity();
            queueEmailEntity.setSender(IQueueEmailService.AP_INTI_SENDER);

            Supplier byCode = iSupplierService.getByCode(po.getSupCode());
            if (byCode!=null){
                queueEmailEntity.setReceiver(byCode.getEmail());//供应商邮件
            }
            queueEmailEntity.setSubject(IQueueEmailService.AP_SUBJECT_MSG);
            queueEmailEntity.setContent("您有预订单转为正常订单【"+po.getOrderCode()+"】，请确认回传合同。");
            queueEmailEntity.setSendCount(0);
            queueEmailEntity.setStatus(IQueueEmailService.STATUS_INIT);
            queueEmailService.save(queueEmailEntity);

        }

    }
}
