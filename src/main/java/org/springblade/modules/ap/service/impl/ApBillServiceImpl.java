package org.springblade.modules.ap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import org.springblade.common.config.AtwSrmConfiguration;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.common.utils.WillHttpUtil;
import org.springblade.common.utils.WillU9Util;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.ap.dto.ApReq;
import org.springblade.modules.ap.entity.*;
import org.springblade.modules.ap.mapper.ApBillMapper;
import org.springblade.modules.ap.mapper.ApRcvMapper;
import org.springblade.modules.ap.service.*;
import org.springblade.modules.ap.vo.ApRcvVO;
import org.springblade.modules.ap.wrapper.ApRcvWrapper;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.mapper.PoItemMapper;
import org.springblade.modules.po.service.IPoService;
import org.springblade.modules.queue.entity.QueueEmailEntity;
import org.springblade.modules.queue.service.IQueueEmailService;
import org.springblade.modules.supplier.entity.PaywayEntity;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.IPaywayService;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springblade.modules.system.entity.AuditRecordEntity;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.service.IAuditRecordService;
import org.springblade.modules.system.service.IParamService;
import org.springblade.modules.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ???????????????
 * @author Will
 */
@Service
public
class ApBillServiceImpl extends BaseServiceImpl<ApBillMapper, ApBillEntity> implements IApBillService{

    @Autowired
    private IApInvoiceService apInvoiceService;

    @Autowired
    private IApItemService apItemService;

    @Autowired
    @Lazy
    private IApRcvService apRcvService;

    @Autowired
    private IAuditRecordService auditRecordService;

    @Autowired
    AtwSrmConfiguration atwSrmConfiguration;

    @Autowired
    private ISupplierService supplierService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IPoService poService;

    @Autowired
    private IQueueEmailService queueEmailService;

    @Autowired
    private IParamService paramService;

    @Autowired
    private IPaywayService paywayService;

    @Autowired
    private PoItemMapper poItemMapper;

    @Autowired
    private ApRcvMapper apRcvMapper;

    @Override
    public
    QueryWrapper<ApBillEntity> getQueryWrapper(ApReq apReq){
        QueryWrapper<ApBillEntity> queryWrapper = Condition.getQueryWrapper(new ApBillEntity()).orderByDesc("create_time");
        if(!StringUtil.isEmpty(apReq.getSupName())){
            queryWrapper.like("sup_name", apReq.getSupName());
        }
        if(!StringUtil.isEmpty(apReq.getSupCode())){
            queryWrapper.like("sup_code", apReq.getSupCode());
        }
        if(!StringUtil.isEmpty(apReq.getCreateTimeStart())){
            queryWrapper.ge("create_time", apReq.getCreateTimeStart());
        }
        if(!StringUtil.isEmpty(apReq.getCreateTimeEnd())){
            queryWrapper.le("create_time", apReq.getCreateTimeEnd());
        }
        if(!StringUtil.isEmpty(apReq.getBackDateStart())){
            queryWrapper.ge("back_date", apReq.getBackDateStart());
        }
        if(!StringUtil.isEmpty(apReq.getBackDateEnd())){
            queryWrapper.le("back_date", apReq.getBackDateEnd());
        }
        if(!StringUtil.isEmpty(apReq.getSrmDateStart())){
            queryWrapper.ge("srm_date", apReq.getSrmDateStart());
        }
        if(!StringUtil.isEmpty(apReq.getSrmDateEnd())){
            queryWrapper.le("srm_date", apReq.getSrmDateEnd());
        }
        if(!StringUtil.isEmpty(apReq.getStatus())){
            queryWrapper.eq("status", apReq.getStatus());
        }
        if(!StringUtil.isEmpty(apReq.getBillCode())){
            queryWrapper.eq("bill_code", apReq.getBillCode());
        }
        return queryWrapper;
    }

    @Override
    public
    IPage<ApBillEntity> getPage(IPage<ApBillEntity> page, ApReq apReq){
        IPage<ApBillEntity> apPage = this.baseMapper.selectApPage(page,apReq);
        return apPage;
    }

    @Override
    public
    int getListCount(){
        QueryWrapper<ApBillEntity> qw                  = Condition.getQueryWrapper(new ApBillEntity());
        String                     userRoleId          = paramService.getValue("purch_place.role_id");
        String                     deputyManagerRoleId = paramService.getValue("purch_deputy_manager.role_id");
        String                     managerRoleId       = paramService.getValue("purch_manager.role_id");
        String                     yanManagerRoleId    = paramService.getValue("yan_manager.role_id");

        BladeUser user = AuthUtil.getUser();
        if(StringUtil.containsAny(user.getRoleId(), userRoleId)){
            //?????????--???????????????????????????????????????
            qw.in("status", STATUS_INIT, STATUS_REJECT, STATUS_3AGREE);
        }else if(StringUtil.containsAny(user.getRoleId(), deputyManagerRoleId)){
            //?????????--????????????
            qw.in("status", STATUS_1AGREE);
        }else if(StringUtil.containsAny(user.getRoleId(), managerRoleId)){
            //??????--????????????
            qw.in("status", STATUS_2AGREE);
        }else if(StringUtil.containsAny(user.getRoleId(), yanManagerRoleId)){
            //????????????--????????????
            qw.in("status", STATUS_3AGREE);
        }else{
            return 0;
        }
        return count(qw);
    }

    @Override
    public
    Map<String, Object> billDetail(ApBillEntity bill){
        Map<String, Object> result = new HashMap<>(3);

        ApBillEntity apBillEntity = getById(bill.getId());
        result.put("apBillEntity", apBillEntity);

        List<ApItemEntity> apItemEntities = apItemService.getApItemEntities(bill.getId(), "BI");
        List<ApRcvVO>      apRcvVos       = Lists.newArrayList();

        for(ApItemEntity apItemEntity : apItemEntities){
            ApRcvEntity rcvEntity = apRcvService.getById(apItemEntity.getRcvId());
            ApRcvReqEntity apRcvReqEntity = null;
            if (rcvEntity == null) {
                rcvEntity = apRcvMapper.getSettleVmiInfoByReqId(apItemEntity.getRcvId().toString());
                apRcvReqEntity =  apRcvMapper.getReqVmiInfoByReqId(apItemEntity.getRcvId().toString());
            }

            ApRcvVO     apRcvVo   = ApRcvWrapper.build().entityVO(rcvEntity);

            apRcvVo.setApItemId(apItemEntity.getId().toString());
            apRcvVo.setRecThisQty(apItemEntity.getRecThisQty());
            apRcvVo.setTax(apItemEntity.getTax());
            apRcvVo.setTaxSubTotal(apItemEntity.getTaxSubTotal());
            apRcvVo.setSubTotal(apItemEntity.getSubTotal());
            apRcvVo.setPipAmount(apItemEntity.getPipPrice());
            apRcvVo.setPurAmount(apItemEntity.getPurPrice());
            apRcvVo.setTaxRate(apItemEntity.getTaxRate());
            apRcvVo.setTaxPrice(apItemEntity.getTaxPrice());
            apRcvVo.setPrice(apItemEntity.getPrice());

            if(apRcvReqEntity!=null) {
                apRcvVo.setRcvCode(apRcvReqEntity.getReqRcvCode());
                apRcvVo.setRcvLn(Integer.valueOf(apRcvReqEntity.getReqRcvLn()));
                apRcvVo.setPoCode(apRcvReqEntity.getReqPoCode());
                apRcvVo.setPoLn(Integer.valueOf(apRcvReqEntity.getReqPoLn()));
                apRcvVo.setRcvActualQty(apRcvReqEntity.getReqRcvNum()==null?null:new BigDecimal(apRcvReqEntity.getReqRcvNum()));
                apRcvVo.setAccumRecQty(apRcvReqEntity.getReqAccumRecQty()==null?null:new BigDecimal(apRcvReqEntity.getReqAccumRecQty()));
            }

            if(!StringUtil.isEmpty(apItemEntity.getPoCode())){
                PoEntity poEntity = poService.getByOrderCode(apItemEntity.getPoCode());
                if(poEntity != null){
                    apRcvVo.setPayWay(poEntity.getPayWay());
                    apRcvVo.setPayDate(poEntity.getPayDate());
                }
            }
            apRcvVos.add(apRcvVo);
        }
        result.put("apRcvVos", apRcvVos);

        List<ApInvoiceEntity> apInvoiceEntities = apInvoiceService.getApInvoiceEntities(bill.getId(), "BI");
        result.put("apInvoiceEntities", apInvoiceEntities);
        return result;
    }

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public
    boolean audit(ApReq apReq) throws IOException{

        BladeUser    user         = SecureUtil.getUser();
        User         bladeUser    = userService.getById(user.getUserId());
        ApBillEntity apBillEntity = getById(apReq.getId());
        apBillEntity.setStatus(apReq.getStatus());
        apBillEntity.setRemark(StringUtil.isNotBlank(apReq.getRemark()) ? apReq.getRemark() : null);

        AuditRecordEntity auditRecordEntity = new AuditRecordEntity();
        auditRecordEntity.setAuditUser(user.getUserId());
        auditRecordEntity.setAuditStatus(apReq.getStatus());
        auditRecordEntity.setAuditTime(System.currentTimeMillis()/1000);
        auditRecordEntity.setRemark(StringUtil.isNotBlank(apReq.getRemark()) ? apReq.getRemark() : null);
        auditRecordEntity.setObjId(apBillEntity.getId());
        auditRecordEntity.setObjType("BI");
        auditRecordService.save(auditRecordEntity);

        if(apReq.getStatus().equals(STATUS_1AGREE)){
            if(StringUtil.isEmpty(bladeUser.getSignatures())){
                throw new RuntimeException("???????????????????????????");
            }
        }else if(apReq.getStatus().equals(STATUS_2AGREE)){
            if(StringUtil.isEmpty(bladeUser.getSignatures())){
                throw new RuntimeException("???????????????????????????");
            }
        }else if(apReq.getStatus().equals(STATUS_3AGREE)){
            if(StringUtil.isEmpty(bladeUser.getSignatures())){
                throw new RuntimeException("?????????????????????????????????");
            }
            apBillEntity.setAudit1(user.getUserId());
            apBillEntity
                .setSysLog(apBillEntity.getSysLog() + "||" + user.getUserId() + "--????????????--BI?????????" + apBillEntity.getStatus());

            //????????????
            QueueEmailEntity queueEmailEntity = new QueueEmailEntity();
            queueEmailEntity.setSender(IQueueEmailService.AP_INTI_SENDER);
            queueEmailEntity.setReceiver(paramService.getValue(IQueueEmailService.AP_PARAM_KEY));
            queueEmailEntity.setSubject(IQueueEmailService.AP_SUBJECT_DP);
            queueEmailEntity.setContent(IQueueEmailService.AP_CONTENT_DP);
            queueEmailEntity.setSendCount(0);
            queueEmailEntity.setStatus(IQueueEmailService.STATUS_INIT);
            queueEmailService.save(queueEmailEntity);

        }else if(apReq.getStatus().equals(STATUS_SUCCESS)){
            if(StringUtil.isEmpty(bladeUser.getSignatures())){
                throw new RuntimeException("?????????????????????????????????");
            }
            apBillEntity.setAudit2(user.getUserId());
            apBillEntity.setSysLog(apBillEntity.getSysLog() + "||" + user.getUserId() + "--????????????--BI?????????" + apBillEntity.getStatus());

            Supplier            supplier = supplierService.getByCode(apBillEntity.getSupCode());
            if(StringUtil.isBlank(supplier.getPurchCode())){
                throw new RuntimeException("????????????["+supplier.getCode()+"]??????????????????????????????????????????????????????");
            }
            List<ApItemEntity>        apItemEntities = apItemService.getApItemEntities(apBillEntity.getId(), "BI");

            Map<String, Object> params   = new HashMap<String, Object>();
            params.put("DocType", "01");
            params.put("SupplierCode", apBillEntity.getSupCode());
            params.put("Currency", "C001");
            params.put("AccrueDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            // 20220111 VMI??????BUG??????
            Integer vmiCount = 0;
            Integer otherCount = 0;
            for (ApItemEntity apItemEntity : apItemEntities) {
                if (apItemEntity.getPoCode()!=null && apItemEntity.getPoCode().indexOf("VMI") > -1) {
                    vmiCount++;
                } else {
                    otherCount++;
                }
            }
            if(vmiCount * otherCount > 1 ){
                throw new RuntimeException("VMI?????? ???????????? ???????????? ????????????.???????????????!");
            }
            String poCode = apItemEntities.get(0).getPoCode();
            String orgcode = apItemEntities.get(0).getOrgCode();
            String srcBusinessType = "316";
            if(StringUtil.isNotBlank(poCode)&& poCode.indexOf("VMI") > -1) {
                srcBusinessType = "322";
            }
            params.put("SrcBusinessType", srcBusinessType);
            if ("002".equals(orgcode)) {
                params.put("SrcOrg", "002");
                params.put("BizOrg", "002");
            }else {
                params.put("SrcOrg", "001");
                params.put("BizOrg", "001");
            }

            params.put("PriorityGrade", "4");
            params.put("Transactor", supplier.getPurchCode());
            params.put("MatchLayer", "3");

            params.put("InvoiceNum", apInvoiceService.getInvoiceCodesByApId(apBillEntity.getId(),"BI")); //????????? ?????????/??????
            params.put("InvoiceDate", WillDateUtil.unixTimeToStr(apInvoiceService.getLastInvoiceDate(apBillEntity.getId(),"BI"),"yyyy-MM-dd")); //????????????  2020-08-30
            params.put("RecviceInvoiceDate", WillDateUtil.unixTimeToStr(apBillEntity.getInvoiceDate(),"yyyy-MM-dd") ); //???????????? 2020-08-30

            List<Map<String, Object>> lines          = new ArrayList<>();


            if(StringUtil.isNotBlank(poCode)&& poCode.indexOf("VMI") > -1){  //VMI??????

                Map<String,ApItemEntity> map = new HashMap<>();

                apItemEntities.forEach(temp-> {
                    ApRcvEntity rcvEntity = apRcvMapper.getSettleVmiInfoByReqId(temp.getRcvId().toString());
                    // key ??? ????????????+????????????
                    String key = rcvEntity.getRcvCode()+"-"+rcvEntity.getRcvLn();
                    if(map.containsKey(key)) {

                        BigDecimal oldRecThisQty  = map.get(key).getRecThisQty();
                        map.get(key).setRecThisQty(oldRecThisQty.add(temp.getRecThisQty()));

                    } else {
                        map.put(key,temp);
                    }
                });

                for(Map.Entry<String, ApItemEntity> entry : map.entrySet()){
                    String rcvCode= entry.getKey().split("-")[0];
                    String rcvLn= entry.getKey().split("-")[1];
                    ApItemEntity temp = entry.getValue();
                    Map<String, Object> line = new HashMap<>();
                    line.put("SrcDocNo", StringUtil.containsAny(rcvCode,"DP")?"":  rcvCode);
                    line.put("SrcDocLineNo",rcvLn);
                    line.put("ItemCode", temp.getItemCode());
                    line.put("ItemQty", temp.getRecThisQty().abs());
                    line.put("TaxPrice", temp.getTaxPrice().abs());
                    //??????????????????
                    long prepayDate = apBillEntity.getPrepayDate()*1000;
                    line.put("ExpectPayDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date(prepayDate)));
                    line.put("TotalMoney", temp.getTaxSubTotal().abs());
                    line.put("Tax", WillU9Util.getTaxRateCode(temp.getTaxRate()));
                    line.put("PurOper", supplier.getPurchCode());
                    line.put("PurDept", "07");
                    if ("002".equals(temp.getOrgCode())) {
                        line.put("BizOrg","002" );
                    }else {
                        line.put("BizOrg","001" );
                    }

                    lines.add(line);
                }
            } else { // ????????????
                apItemEntities.forEach(temp->{
                    Map<String, Object> line = new HashMap<>();
                    line.put("SrcDocNo", StringUtil.containsAny(temp.getRcvCode(),"DP")?"":  temp.getRcvCode());
                    line.put("SrcDocLineNo", temp.getRcvLn() == null ? "" : temp.getRcvLn());
                    line.put("ItemCode", temp.getItemCode());
                    line.put("ItemQty", temp.getRecThisQty().abs());
                    line.put("TaxPrice", temp.getTaxPrice().abs());
                    //??????????????????
                    long prepayDate = apBillEntity.getPrepayDate()*1000;
                    line.put("ExpectPayDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date(prepayDate)));
                    line.put("TotalMoney", temp.getTaxSubTotal().abs());
                    line.put("Tax", WillU9Util.getTaxRateCode(temp.getTaxRate()));
                    line.put("PurOper", supplier.getPurchCode());
                    line.put("PurDept", "07");
                    if ("002".equals(temp.getOrgCode())) {
                        line.put("BizOrg","002" );
                    }else {
                        line.put("BizOrg","001" );
                    }
                    lines.add(line);
                });
            }

            params.put("lines", lines);
            //??????U9?????????????????????u9Code
            ObjectMapper mapper     = new ObjectMapper();
            String       res        = WillHttpUtil.postJson(
                atwSrmConfiguration.getU9ApiDomain() + "/api/CreateAPBill", mapper.writeValueAsString(params), 600L);
            ObjectNode   objectNode = (ObjectNode) mapper.readTree(res);
            String       code       = mapper.readValue(String.valueOf(objectNode.get("code")), String.class);
            if(!"2000".equals(code)){
                throw new RuntimeException("U9?????????????????????" + mapper.readValue(String.valueOf(objectNode.get("msg")), String.class));
            }
            String u9Code = mapper.readValue(String.valueOf(objectNode.get("APBillCode")), String.class);
            apBillEntity.setU9Code(u9Code);
            apBillEntity.setU9Date(System.currentTimeMillis()/1000);
            apBillEntity.setSysLog(apBillEntity.getSysLog() + "||" + user.getUserId() + "--????????????--BI?????????" + apBillEntity.getStatus());

        }else if(apReq.getStatus().equals(IApService.STATUS_REJECT)){
            apBillEntity.setSysLog(apBillEntity.getSysLog() + "||" + user.getUserId() + "--????????????--BI?????????" + apBillEntity.getStatus());
        }
        return updateById(apBillEntity);
    }

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public
    boolean auditBatch(List<ApReq> apReqs) throws IOException{
        for(ApReq apReq : apReqs){
            audit(apReq);
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public
    boolean yfSave(ApReq apReq){
        List<ApRcvVO> apRcvVos     = apReq.getApRcvVos();
        ApBillEntity  apBillEntity = getById(apReq.getId());
        BigDecimal    purAmount    = new BigDecimal("0");
        for(ApRcvVO apRcvVo : apRcvVos){
            ApItemEntity apItemEntity = apItemService.getById(apRcvVo.getApItemId());
            apItemEntity.setPipPrice(apRcvVo.getPipAmount());
            //????????????
            apItemEntity.setPurPrice(apRcvVo.getPurAmount());
            purAmount = purAmount.add(apRcvVo.getPurAmount());
            apItemService.updateById(apItemEntity);
        }
        //????????????
        apInvoiceService.getApInvoiceEntities(apBillEntity.getId(), "BI").forEach(temp->{
            apInvoiceService.removeById(temp.getId());
        });
        apReq.getInvoiceEntities().forEach(temp->{
            temp.setBillId(apBillEntity.getId());
            temp.setBillCode(apBillEntity.getBillCode());
        });
        apInvoiceService.saveBatch(apReq.getInvoiceEntities());

        apBillEntity.setInvoiceDate(apReq.getInvoiceDate());
        apBillEntity.setPrepayDate(apReq.getPrepayDate());
        apBillEntity.setProNo(apReq.getProNo());
        apBillEntity.setPurAmount(purAmount);
        apBillEntity.setStatus(STATUS_INIT);
        return updateById(apBillEntity);
    }

    @Override
    public
    List<Map<String, Object>> countList(ApReq apReq){
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_INIT);
            put("title", "?????????");
            put("count", countByStatus(STATUS_INIT, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_1AGREE);
            put("title", "???????????????");
            put("count", countByStatus(STATUS_1AGREE, apReq.getSupCode()));
        }});
        //        result.add(new ArrayMap<String, Object>() {{
        //            put("status", STATUS_2AGREE);
        //            put("title", "???????????????");
        //            put("count", countByStatus(STATUS_2AGREE, apReq.getSupCode()));
        //        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_3AGREE);
            put("title", "????????????");
            put("count", countByStatus(STATUS_3AGREE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_SUCCESS);
            put("title", "????????????");
            put("count", countByStatus(STATUS_SUCCESS, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_REJECT);
            put("title", "????????????");
            put("count", countByStatus(STATUS_REJECT, apReq.getSupCode()));
        }});
        return result;
    }

    @Override
    public
    Map<String, Object> print(ApReq apReq){
        Map<String, Object> result       = new HashMap<>();
        ApBillEntity        apBillEntity = getById(apReq.getId());
        PaywayEntity paywayEntity = paywayService.getBySupCode(apBillEntity.getSupCode());
        Supplier            supplier     = supplierService.getByCode(apBillEntity.getSupCode());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        result.put("orgCode", apBillEntity.getOrgCode()==null?"001":apBillEntity.getOrgCode());
        result.put("u9Code", apBillEntity.getU9Code());
        result.put("billCode", apBillEntity.getBillCode());
        result.put("printDate", sdf.format(new Date()));
        result.put("department", "?????????");
        result.put("useReason", "??????");
        result.put("supName", apBillEntity.getSupName());
        result.put("bankName", supplier.getBankName());
        result.put("bankAccountCode", supplier.getBankAccountCode());
        result.put("proNo", apBillEntity.getProNo());
        if(StringUtil.isNotBlank(paywayEntity.getTypeName())){
            result.put("payWay", "??????".equals(paywayEntity.getTypeName()) ? paywayEntity.getRemark() : paywayEntity.getTypeName());
        }else{
            result.put("payWay", "");
        }
        // result.put("payWay", supplier.getPayWay());  // 20211025 payWay?????????????????????atw_payway????????????supplier???
        result.put("purAmount", apBillEntity.getPurAmount());
        result.put("taxAmount", apBillEntity.getTaxAmount());
        result.put("poCodes", apItemService.getPoCodesByApId(apBillEntity.getId(), "BI"));
        result.put("invoiceCodes", apInvoiceService.getInvoiceCodesByApId(apBillEntity.getId(), "BI"));
        //????????????
        long invoiceDate = apInvoiceService.getLastInvoiceDate(apBillEntity.getId(), "BI");
        result.put("invoiceDate", sdf.format(new Date(invoiceDate * 1000)));

        //????????????
        BigDecimal         surplus        = new BigDecimal("0");
        List<ApItemEntity> apItemEntities = apItemService.getGroupBy(apBillEntity.getId());
        for(ApItemEntity apItemEntity : apItemEntities){
            ApRcvEntity apRcvEntity = apRcvService.getById(apItemEntity.getRcvId());
            if (apRcvEntity == null) {
                apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(apItemEntity.getRcvId().toString());
            }
            if("DZ".equals(apRcvEntity.getType())){
                String poCode = apItemEntity.getPoCode();
                String supCode = apRcvEntity.getSupCode();
                String itemCode = apRcvEntity.getItemCode();
                if (poCode.indexOf("VMI") > -1) { // VMI??????????????????????????????????????????????????????????????????VMI???????????????????????????
                    List<PoEntity> poEntityList = poItemMapper.getVmiPoInfo(itemCode,supCode);
                    for(PoEntity poEntity : poEntityList) {
                        if(poEntity.getPrepaidTotal().compareTo(poEntity.getAccpaidTotal()) > 0){
                            surplus = surplus.add(poEntity.getDocAmount().subtract(poEntity.getPrepaidTotal()));
                        }else{
                            surplus = surplus.add(poEntity.getDocAmount().subtract(poEntity.getAccpaidTotal()));
                        }
                    }
                } else {
                    PoEntity poEntity = poService.getByOrderCode(apItemEntity.getPoCode());
                    if(poEntity.getPrepaidTotal().compareTo(poEntity.getAccpaidTotal()) > 0){
                        surplus = surplus.add(poEntity.getDocAmount().subtract(poEntity.getPrepaidTotal()));
                    }else{
                        surplus = surplus.add(poEntity.getDocAmount().subtract(poEntity.getAccpaidTotal()));
                    }
                }
            }
        }
        // PO????????????????????????0??????????????????
        result.put("surplus", check(apBillEntity) ? surplus : null);
        //????????????
        User audit1     = userService.getById(apBillEntity.getAudit1());
        User audit2     = userService.getById(apBillEntity.getAudit2());
        User createUser = userService.getById(apBillEntity.getCreateUser());
        result.put("audit1", audit1 == null || audit1.getSignatures() == null ? "" : audit1.getSignatures());
        result.put("audit2", audit2 == null || audit2.getSignatures() == null ? "" : audit2.getSignatures());
        result.put("createUser", createUser.getSignatures());
        return result;
    }


    /**
     * check
     * @param apBillEntity ApBillEntity
     * @return boolean
     */
    private
    boolean check(ApBillEntity apBillEntity){
        List<ApItemEntity> apItemEntities = apItemService.getApItemEntities(apBillEntity.getId(), "BI");
        for(ApItemEntity apItemEntity : apItemEntities){
            if(!StringUtil.isEmpty(apItemEntity.getPoCode())){

                String poCode = apItemEntity.getPoCode();
                String supCode = apBillEntity.getSupCode();
                String itemCode = apItemEntity.getItemCode();

                if (poCode.indexOf("VMI") > -1) { // VMI??????????????????????????????????????????????????????????????????VMI???????????????????????????
                    List<PoEntity> poEntityList = poItemMapper.getVmiPoInfo(itemCode,supCode);
                    for(PoEntity poEntity : poEntityList) {
                        if(!StringUtil.isEmpty(poEntity.getAccumPrepayRate()) && poEntity.getAccumPrepayRate() > 0){
                            return true;
                        }
                    }
                } else {
                    PoEntity poEntity = poService.getByOrderCode(apItemEntity.getPoCode());
                    if(!StringUtil.isEmpty(poEntity.getAccumPrepayRate()) && poEntity.getAccumPrepayRate() > 0){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public
    String genCode(){
        Date                       start = WillDateUtil.getTodayStart();
        Date                       end   = WillDateUtil.getTodayEnd();
        QueryWrapper<ApBillEntity> qw    = Wrappers.<ApBillEntity>query().ge("create_time", start).le("create_time", end);
        int                        seq   = count(qw) + 1;
        return "AP" + DateUtil.format(start, "yyyyMMdd") + String.format("%03d", seq);
    }


    /**
     * ????????????????????????
     * @param status String
     * @return int
     */
    private
    int countByStatus(Integer status, String supCode){
        QueryWrapper<ApBillEntity> queryWrapper = Wrappers.<ApBillEntity>query().in("status", status);
        if(!StringUtil.isEmpty(supCode)){
            queryWrapper.like("sup_code", supCode);
        }
        return count(queryWrapper);
    }

    /**
     * ????????????????????????
     * @param apCodes
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void backToRec(List<String> apCodes) {
        for (String apCode : apCodes) {
            try{
                this.baseMapper.backToRecOfAPI(apCode);
                this.baseMapper.backToRecOfAPB(apCode);

                List<ApItemEntity> apItemEntities = this.baseMapper.getApInfoByApCode(apCode);
                for(ApItemEntity apItemEntity : apItemEntities) {
                    if(apItemEntity.getPoCode().indexOf("VMI") > -1 ) {
                        String reqRcvId = apItemEntity.getRcvId().toString(); // atw_ap_req_settle???id
                        ApReqSettle apReqSettle = this.baseMapper.getReqInfoById(reqRcvId);
                        String rcvCode = apReqSettle.getSettleRcvCode(); // atw_ap_rcv?????????322???????????????
                        String rcvLn = apReqSettle.getSettleRcvLn();

                        // 1.?????? ????????? atw_ap_req_settle
                        this.baseMapper.updateApReqSettleById(reqRcvId,apItemEntity.getRecThisQty());

                        // 2.?????? ????????? atw_ap_rcv
                        this.baseMapper.updateApRcvByCodeLn(rcvCode,rcvLn,apItemEntity.getRecThisQty());
                    }else {
                        this.baseMapper.backToRecOfRCV(apCode);
                    }


                }
            } catch (Exception e){
                e.printStackTrace();
                R.fail(apCode + ": ????????????????????????");
            }
        }
    }
}
