package org.springblade.modules.supplier.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
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
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springblade.common.config.AtwSrmConfiguration;
import org.springblade.common.utils.CommonUtil;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.common.utils.WillHttpUtil;
import org.springblade.common.utils.WillU9Util;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.DigestUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.pr.entity.ItemInfoEntityOfZDJ;
import org.springblade.modules.report.entity.AllOtdReport;
import org.springblade.modules.report.entity.OrderOtdReport;
import org.springblade.modules.supplier.dto.*;
import org.springblade.modules.supplier.entity.*;
import org.springblade.modules.supplier.mapper.SupplierMapper;
import org.springblade.modules.supplier.mapper.SupplierScheduleMapper;
import org.springblade.modules.supplier.service.IPaywayService;
import org.springblade.modules.supplier.service.ISupUserService;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springblade.modules.supplier.vo.EchartVo;
import org.springblade.modules.supplier.vo.OmsEchrtsOfSupplierVO;
import org.springblade.modules.supplier.vo.SupplierOtdExcel;
import org.springblade.modules.supplier.vo.SupplierVO;
import org.springblade.modules.system.entity.DictBiz;
import org.springblade.modules.system.service.IDictBizService;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

import static org.springblade.common.utils.ItemAnalysisUtil.getItemInfoOfZhuDuanJian;
import static org.springblade.core.secure.utils.AuthUtil.getUser;

/**
 * ????????? ???????????????
 * @author Will
 */
@Service
@Log
public
class SupplierServiceImpl extends BaseServiceImpl<SupplierMapper, Supplier> implements ISupplierService {

    @Autowired
    ISupUserService supUserService;
    @Autowired
    AtwSrmConfiguration atwSrmConfiguration;
    @Autowired
    IDictBizService bizService;
    @Autowired
    IPaywayService paywayService;
    @Autowired
    IDictBizService iDictBizService;
    @Autowired
    SupplierScheduleMapper supplierScheduleMapper;
    @Autowired
    @Lazy
    IParamService paramService;

    @Value("${sendCloud.url}")
    private String url;

    @Value("${sendCloud.apiUser}")
    private String apiUser;

    @Value("${sendCloud.apiKey}")
    private String apiKey;

    @Value("${sendCloud.splitSymbol}")
    private String splitSymbol;

    @Value("${model.modelPath}")
    private String MODEL_PATH;

    @Value("${model.outputPath}")
    private String OUTPUT_PATH;

    @Value("${sqlServer.url}")
    private String sqlServerUrl;

    @Value("${sqlServer.user}")
    private String sqlServerUser;

    @Value("${sqlServer.password}")
    private String sqlServerPassword;

    @Value("${sqlServer.driver}")
    private String sqlServerDriver;

    @Value("${oracle.url}")
    private String oracleUrl;

    @Value("${oracle.user}")
    private String oracleUser;

    @Value("${oracle.password}")
    private String oraclePassword;

    @Value("${oracle.driver}")
    private String oracleDriver;


    @Override
    public IPage<SupplierVO> selectSupplierPage(IPage<SupplierVO> page, SupplierVO supplier) {
        return this.baseMapper.selectSupplierPage(page,supplier);
    }


    @Scheduled(cron = "0 50 17 ? * *")
//    @Transactional(rollbackFor = Exception.class)
    public void test() {
        List<SupUser> supUsers = this.baseMapper.getAllSupplierUsers();
        for (SupUser user: supUsers) {
            String passswod = user.getPassword();
            String username = user.getTenantId();
            if(DigestUtil.encrypt(username).equals(passswod)){
                // System.out.println(username + " : ????????????");
                String initialPassword = String.valueOf((int)((Math.random() * 9 + 1) * 100000)); // ????????????6??????
                user.setInitialPassword(initialPassword);
                user.setPassword(DigestUtil.encrypt(initialPassword));
                supUserService.updateById(user);
                System.out.println(username + " : ?????????????????????" + initialPassword);
            }
        }
    }


    @SneakyThrows
    @Override
    public boolean save(SupplierDTO supplier) {
        supplier.setTypeName(bizService.getValue("sup_type_code", supplier.getTypeCode()));
        createByU9(supplier);
        // ?????????????????????
        SupUser supUser = supUserService.create(supplier);
        supplier.setSupId(supUser.getId());
        // ????????????
        supplier.setPrimaryContact("1");
        super.save(supplier);

        //??????????????????
        PaywayEntity paywayEntity = new PaywayEntity();
        paywayEntity.setType(supplier.getType());
        paywayEntity.setTypeName(supplier.getPayWay());
        paywayEntity.setFirstPrepayRate(supplier.getFirstPrepayRate());
        paywayEntity.setAccumPrepayRate(supplier.getAccumPrepayRate());
        paywayEntity.setPayDate(supplier.getPayDate());
        paywayEntity.setIsDefault(true);
        paywayEntity.setSupCode(supplier.getCode());
        paywayEntity.setSupName(supplier.getName());
        paywayEntity.setRemark(supplier.getRemark());
        return paywayService.save(paywayEntity);
    }

    /**
     * @param supplier SuppName	??????
     *                 ShortName	??????
     *                 Category	??????
     *                 Purchaser	?????????
     *                 Address	??????
     *                 BusinessScope	????????????
     *                 PaymentMethod	????????????
     *                 AccountPeriod	?????????????????????
     *                 PaymentProportion	??????
     *                 Tax	??????
     *                 OutSide	????????????????????????
     *                 CheckCurrency	????????????
     *                 Payment	????????????
     *                 APConfirmTerm	????????????
     *                 InvoiceVerificationDetai	????????????
     *                 ReceiptRule	????????????
     *                 Org	??????
     *                 IsTaxPrice	????????????
     */
    private void createByU9(SupplierDTO supplier) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        if(this.baseMapper.getCountSupBrief(supplier.getSupBrief()) > 0) {
            throw new RuntimeException("?????????????????????????????????????????????????????????????????????");
        }

        Map<String, Object> content = new HashMap<>(5);
        content.put("ContactName", supplier.getCtcName());
        content.put("ContactFixedPhone", supplier.getMobile());
        content.put("ContactMobilePhone", supplier.getPhone());
        content.put("ContactFax", supplier.getFax());
        content.put("ContactMail", supplier.getEmail());

        Map<String, Object> req = new ArrayMap<String, Object>() {{
            put("SuppName", supplier.getName());    //??????
            put("ShortName", supplier.getName());   //??????
            put("Category", supplier.getTypeCode());   //
            put("Purchaser", supplier.getPurchCode());  //?????????
            put("Address", supplier.getAddress());  //??????
            put("BusinessScope", "");   //????????????
            put("PaymentMethod", supplier.getPayWay().equals("??????") ? supplier.getRemark() : supplier.getPayWay());   //????????????
            put("AccountPeriod", supplier.getPayRate());   //?????????????????????
            put("PaymentProportion", supplier.getPayDate());   //??????
            put("Tax", supplier.getTaxRateCode()); //??????
            put("OutSide", 1); //????????????????????????  1-????????????  0-???????????????
            put("CheckCurrency", supplier.getCurrencyType());   //????????????
            put("Payment", "01"); //????????????
            put("APConfirmTerm", "01");   //????????????
            // put("InvoiceVerificationDetai", "01");    //???????????? 0-???????????????1-???????????????2-??????????????????3-???????????????????????????
            put("ReceiptRule", "01"); //????????????
            put("Org", "001"); //??????
            put("IsTaxPrice", StringUtils.isNotBlank(supplier.getTaxRateCode()));  //????????????
            put("Content", content); //???????????????
        }};
        //???????????????????????????001???002 ??????????????????
        String res = WillHttpUtil.postJson(atwSrmConfiguration.getU9ApiDomain() + "/api/CreateSupplier", mapper.writeValueAsString(req));

        req.put("Org", "002");
        //String res2 = WillHttpUtil.postJson(atwSrmConfiguration.getU9ApiDomain() + "/api/CreateSupplier", mapper.writeValueAsString(req));


        ObjectNode objectNode = (ObjectNode) mapper.readTree(res);
        String code = mapper.readValue(String.valueOf(objectNode.get("code")), String.class);

        /*ObjectNode objectNode2 = (ObjectNode) mapper.readTree(res2);
        String code2 = mapper.readValue(String.valueOf(objectNode2.get("code")), String.class);*/
        /*if ((!"2000".equals(code2)||!"2000".equals(code))&&("????????????".indexOf(String.valueOf(objectNode.get("msg")))==-1||"????????????".indexOf(String.valueOf(objectNode2.get("msg")))==-1)) {
            throw new RuntimeException("U9?????????????????????" +"?????????"+ mapper.readValue(String.valueOf(objectNode.get("msg")), String.class)+"?????????"+mapper.readValue(String.valueOf(objectNode2.get("msg")), String.class));
        }*/

        if (!"2000".equals(code)) {
            throw new RuntimeException("U9?????????????????????" + mapper.readValue(String.valueOf(objectNode.get("msg")), String.class));
        }


        String supCode = mapper.readValue(String.valueOf(objectNode.get("supplierCode")), String.class);
        supplier.setCode(supCode);
    }



    @Override
    public List<Supplier> listByCode(String code) {
        return list(query().like("code", code));
    }


    @Override
    public Supplier getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        Supplier sup = new Supplier();
        sup.setCode(code);
        sup.setPrimaryContact("1");
        return getOne(Condition.getQueryWrapper(sup));
    }

    @Override
    public Supplier getByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        Supplier sup = new Supplier();
        sup.setName(name);
        sup.setPrimaryContact("1");
        return getOne(Condition.getQueryWrapper(sup));
    }

    @Override
    public boolean resetPassword(Supplier supplier) {
        SupUser supUser = supUserService.getBySupCode(supplier.getCode());
        String initialPassword =  supUser.getInitialPassword();
        if(initialPassword==null || initialPassword.isEmpty()) {
            initialPassword = String.valueOf((int)((Math.random() * 9 + 1) * 100000)); // ????????????6??????
        }
        supUser.setPassword(DigestUtil.encrypt(initialPassword));
        supUser.setInitialPassword(initialPassword);
        return supUserService.updateById(supUser);
    }

    /**
     * ??????
     *
     * @return
     */
    @Override
    public boolean updateBiz(SupplierUpdateReq supDto) {

        String taxCode = WillU9Util.getTaxRateCode(supDto.getTaxRate());
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> req = Maps.newHashMap();
        PaywayEntity paywayEntity = paywayService.getBySupCode(supDto.getCode());

        if(this.baseMapper.getCountSupBriefByCodeAndSupBrief(supDto.getSupBrief(),supDto.getCode()) > 0) {
            throw new RuntimeException("?????????????????????????????????????????????????????????????????????");
        }

        req.put("Code", supDto.getCode()); //   ???????????????
        req.put("AccountPeriod", paywayEntity.getPayDate()); //  ????????????
        req.put("Tax", taxCode); //  ???????????????
        req.put("DescFlexField_PrivateDescSeg3", paywayEntity.getTypeName().equals("??????") ? paywayEntity.getRemark() : paywayEntity.getTypeName()); //  ????????????
        req.put("APConfirmTerm", "01"); // ???????????? ??
        req.put("Effective_IsEffective", true); //??????/??????
        req.put("SupplierName", supDto.getName()); //  ???????????????
        req.put("IsTaxPrice", StringUtils.isNotBlank(taxCode)); //  ????????????
        req.put("ReceiptRuleCode", "02"); //  ??????????????????
        req.put("DescFlexField_PrivateDescSeg1", supDto.getAddress()); //  ??????
        //req.put("DescFlexField_PrivateDescSeg2", supDto.getAddress()); //  ????????????
        req.put("DescFlexField_PrivateDescSeg4", paywayEntity.getAccumPrepayRate()); //  ?????????????????????(0-1??????)
        req.put("CheckCurrencyCode", supDto.getCurrencyType()); //  ??????????????????
        req.put("PurchaserCode", supDto.getPurchCode()); //  ???????????????
        req.put("OrgCode", "001"); //  ????????????
        //req.put("InvoiceVerificationDetai", supDto.getName()); //  ????????????;0,????????????;1,????????????;2,???????????????;3,???????????????????????????
        req.put("PaymentTermCode", "01"); //  ??????????????????
        //req.put("OurSideSupplyRecRefStd", supDto.getName()); //  ????????????????????????;0,???????????????;1,????????????
        req.put("PersonName_DisplayName", supDto.getCtcName()); //  ???????????????
        req.put("DefaultPhoneNum", supDto.getPhone()); //  ???????????????
        req.put("DefaultMobilNum", supDto.getMobile()); //  ???????????????
        req.put("DefaultEmail", supDto.getEmail()); //  ???????????????
        req.put("DefaultFaxNum", supDto.getFax()); //  ???????????????

        String res = null;
        try {
            // ??????U9??????  ?????????????????????
            res = WillHttpUtil.postJson(atwSrmConfiguration.getU9ApiDomain() + "/api/UpdateSupplier", mapper.writeValueAsString(req));
            req.put("OrgCode", "002"); //  ????????????
            //String res2 = WillHttpUtil.postJson(atwSrmConfiguration.getU9ApiDomain() + "/api/UpdateSupplier", mapper.writeValueAsString(req));

            ObjectNode objectNode = (ObjectNode) mapper.readTree(res);
            String code = mapper.readValue(String.valueOf(objectNode.get("code")), String.class);
            if (!"2000".equals(code)) {
                throw new RuntimeException("U9?????????????????????" + mapper.readValue(String.valueOf(objectNode.get("msg")), String.class));
            }

            /*ObjectNode objectNode2 = (ObjectNode) mapper.readTree(res2);
            String code2 = mapper.readValue(String.valueOf(objectNode2.get("code")), String.class);
            if (!"2000".equals(code2)) {
                throw new RuntimeException("U9?????????????????????" + mapper.readValue(String.valueOf(objectNode2.get("msg")), String.class));
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("????????????." + e.getMessage());
        }

        String placeName = this.baseMapper.getName(supDto.getPlaceCode());
        String purchName = this.baseMapper.getName(supDto.getPurchCode());
        Supplier supplier = BeanUtil.copy(supDto, Supplier.class);
        supplier.setPurchName(purchName);
        supplier.setPlaceName(placeName);

        // ????????????????????????
        UpdateWrapper<Supplier> updateWrapper = new UpdateWrapper<Supplier>();
        updateWrapper.eq("primary_contact", "1");
        updateWrapper.eq("id", supplier.getId());
        updateWrapper.set("update_time",new Date());
        return update(supplier, updateWrapper);
    }


    /**
     * ??????????????????????????????
     *
     * @param page
     * @param supCode
     * @return
     */
    @Override
    public IPage<SupplierVO> getOhterCtcInfos(IPage<SupplierVO> page, String supCode) {
        return page.setRecords(baseMapper.selectOtherCtcInfos(page, supCode));
    }

    /**
     * ??????????????????????????????
     *
     * @param supplier
     * @return
     */
    @Override
    public boolean saveOhterCtcInfos(SupplierDTO supplier) {
        SupplierVO supplierToSave = baseMapper.selectMainCtcInfos(supplier.getCode());
        Integer primaryContact = baseMapper.selectCountCtcInfos(supplier.getCode()) + 1;
        supplierToSave.setPrimaryContact(primaryContact.toString());
        supplierToSave.setCtcName(supplier.getCtcName());
        supplierToSave.setCtcDuty(supplier.getCtcDuty());
        supplierToSave.setPhone(supplier.getPhone());
        supplierToSave.setEmail(supplier.getEmail());
        return super.save(supplierToSave);
    }

    /**
     * ?????? ????????????????????????
     *
     * @param supplier
     * @return
     */
    @Override
    public boolean updateOhterCtcInfos(Supplier supplier) {
        return baseMapper.updateOtherCtcInfos(supplier);
    }

    /**
     * ?????? ????????????????????????
     *
     * @param supplier
     * @return
     */
    @Override
    public boolean delOhterCtcInfos(Supplier supplier) {
        return baseMapper.delOhterCtcInfos(supplier);
    }

    /**
     * ?????? ???????????????
     *
     * @param supCodes
     * @param response
     * @throws RuntimeException
     */
    @Override
    public void exportExcel(String supCodes, HttpServletResponse response) throws RuntimeException {
        List<SupplierExcelDTO> suppliers = new ArrayList<>();
        List<DictBiz> tree = iDictBizService.getList("sup_status");

        if(supCodes.isEmpty() || supCodes == null) {
            List<Supplier> supplierList = this.baseMapper.getAllSuppliers();
            supplierList.stream().forEach(supplier -> {
                SupplierExcelDTO supplierExcelDTO = BeanUtil.copy(supplier, SupplierExcelDTO.class);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if(supplier.getUpdateTime()!=null){
                    supplierExcelDTO.setUpdateTime(sdf.format(supplier.getUpdateTime()));
                }
                supplierExcelDTO.setTaxRate(supplier.getTaxRate().multiply(new BigDecimal("100")).toString() + "%");
                for (DictBiz dictBiz : tree) {
                    if (dictBiz.getDictKey().equals(supplier.getStatus().toString())) {
                        supplierExcelDTO.setStatus(dictBiz.getDictValue());
                    }
                }
                String payWay = this.baseMapper.getPayWay(supplier.getCode());
                supplierExcelDTO.setPayWay(payWay);
                suppliers.add(supplierExcelDTO);
            });
        } else {
            List<String> codes = Arrays.asList(supCodes.split(","));
            for (String supCode : codes) {
                Supplier supplier = this.baseMapper.getSupplierByCode(supCode);
                if (supplier != null) {
                    SupplierExcelDTO supplierExcelDTO = BeanUtil.copy(supplier, SupplierExcelDTO.class);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if(supplier.getUpdateTime()!=null){
                        supplierExcelDTO.setUpdateTime(sdf.format(supplier.getUpdateTime()));
                    }
                    supplierExcelDTO.setTaxRate(supplier.getTaxRate().multiply(new BigDecimal("100")).toString() + "%");
                    for (DictBiz dictBiz : tree) {
                        if (dictBiz.getDictKey().equals(supplier.getStatus().toString())) {
                            supplierExcelDTO.setStatus(dictBiz.getDictValue());
                        }
                    }
                    String payWay = this.baseMapper.getPayWay(supplier.getCode());
                    supplierExcelDTO.setPayWay(payWay);
                    suppliers.add(supplierExcelDTO);
                } else {
                    R.fail(codes + ": ????????????????????????????????????");
                }
            }
        }
        ExcelUtils.defaultExport(suppliers, SupplierExcelDTO.class, "??????????????????" + DateUtil.formatDate(new Date()), response);
    }


    /**
     * ????????????????????????
     *
     * @param page
     * @param supplierSchedule
     * @return
     */
    @Override
    public IPage<SupplierSchedule> getSchedule(IPage<SupplierSchedule> page, SupplierSchedule supplierSchedule) {
        IPage<SupplierSchedule> supplierSchedules = supplierScheduleMapper.getSupplierSchedule(page,supplierSchedule);
        return supplierSchedules;
    }


    @Override
    public int getTraceTabCount() {
        String mRoleId  = paramService.getValue("trace_admin.role_id");
        SupplierScheduleReq req = new SupplierScheduleReq();
        req.setStatuss(LAST_WEEK);
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            req.setFuzr(getUser().getAccount());
        }
        return supplierScheduleMapper.getSupplierScheduleCountOfOms(req);
    }

    @Override
    public List<Map<String, Object>> getWeekCount(SupplierScheduleReq supplierScheduleReq) {
        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            supplierScheduleReq.setFuzr(getUser().getAccount());
        }
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new HashMap<String, Object>(3){{
            supplierScheduleReq.setStatuss(LAST_WEEK);
            put("status", LAST_WEEK);
            put("title", "???????????????");
            put("count", supplierScheduleMapper.getSupplierScheduleCountOfOms(supplierScheduleReq));
        }});
        result.add(new HashMap<String, Object>(3){{
            supplierScheduleReq.setStatuss(THIS_WEEK);
            put("status", THIS_WEEK);
            put("title", "??????");
            put("count", supplierScheduleMapper.getSupplierScheduleCountOfOms(supplierScheduleReq));
        }});
        result.add(new HashMap<String, Object>(3){{
            supplierScheduleReq.setStatuss(SECOND_WEEK);
            put("status", SECOND_WEEK);
            put("title", "?????????");
            put("count", supplierScheduleMapper.getSupplierScheduleCountOfOms(supplierScheduleReq));
        }});
        result.add(new HashMap<String, Object>(3){{
            supplierScheduleReq.setStatuss(THIRD_WEEK);
            put("status", THIRD_WEEK);
            put("title", "?????????");
            put("count", supplierScheduleMapper.getSupplierScheduleCountOfOms(supplierScheduleReq));
        }});
        result.add(new HashMap<String, Object>(3){{
            supplierScheduleReq.setStatuss(FORTH_WEEK);
            put("status", FORTH_WEEK);
            put("title", "?????????");
            put("count", supplierScheduleMapper.getSupplierScheduleCountOfOms(supplierScheduleReq));
        }});
        result.add(new HashMap<String, Object>(3){{
            supplierScheduleReq.setStatuss(FIFTH_WEEK);
            put("status", FIFTH_WEEK);
            put("title", "?????????");
            put("count", supplierScheduleMapper.getSupplierScheduleCountOfOms(supplierScheduleReq));
        }});
        result.add(new HashMap<String, Object>(3){{
            supplierScheduleReq.setStatuss(FUTURE_WEEK);
            put("status", FUTURE_WEEK);
            put("title", "?????????");
            put("count", supplierScheduleMapper.getSupplierScheduleCountOfOms(supplierScheduleReq));
        }});
        result.add(new HashMap<String, Object>(3){{
            supplierScheduleReq.setStatuss(THREE_WEEK);
            put("status", THREE_WEEK);
            put("title", "????????????");
            put("count", supplierScheduleMapper.getSupplierScheduleCountOfOms(supplierScheduleReq));
        }});
        return result;
    }

    /**
     * ????????????????????????  - OMS
     *
     * @param page
     * @param supplierScheduleReq
     * @return
     */
    @Override
    public IPage<SupplierSchedule> getScheduleOfOms(IPage<SupplierSchedule> page, SupplierScheduleReq supplierScheduleReq) {
        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            supplierScheduleReq.setFuzr(getUser().getAccount());
        }
        IPage<SupplierSchedule> supplierSchedules = supplierScheduleMapper.getSupplierScheduleOfOms(page, supplierScheduleReq);
        return supplierSchedules;
    }

    @Override
    public boolean batchSendEmail(List<SupplierSchedule> scheduleList) {

        Map<String, List<SupplierSchedule>> supMap = new HashMap<>();

        List<SupplierSchedule> scheduleListFromData = new ArrayList<>();

        for (SupplierSchedule schedule : scheduleList) {
            scheduleListFromData.add(supplierScheduleMapper.getSupplierScheduleByItemCodeAndSupNo(schedule));
        }

        for (SupplierSchedule schedule : scheduleListFromData) {
            if (supMap.containsKey(schedule.getSupNo())) {
                supMap.get(schedule.getSupNo()).add(schedule);
            } else {
                List<SupplierSchedule> scheduleOfOneSup = new ArrayList<>();
                scheduleOfOneSup.add(schedule);
                supMap.put(schedule.getSupNo(), scheduleOfOneSup);
            }
        }

        for (String key : supMap.keySet()) {
            try{
                // ????????????
                String filePath = this.handleExcel(supMap.get(key));
                HttpPost httpPost = new HttpPost(url);
                CloseableHttpClient httpClient = HttpClients.createDefault();

                // ?????????
                String from = supplierScheduleMapper.getSenderEmail(supMap.get(key).get(0).getFuzr());
                //String fromS = supplierScheduleMapper.getSenderEmail(supMap.get(key).get(0).getFuzr());
                // ?????????
                String to = "zhouliangwei@antiwearvalve.com;xiayanjuan@antiwearvalve.com";
                String tos = supplierScheduleMapper.getReceiverEmail(supMap.get(key).get(0).getSupNo());
                // ??????
                String subject = "???????????????????????????";
                // ??????
                String html = supMap.get(key).get(0).getSupName()+ ": " + tos + ","+"?????????????????????????????????????????????????????????????????????????????????????????????";

                // ???????????????????????? ??? MultipartEntityBuilder
                MultipartEntityBuilder entity = MultipartEntityBuilder.create();
                ContentType TEXT_PLAIN = ContentType.create("text/plain", Charset.forName("UTF-8"));
                entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entity.setCharset(Charset.forName("UTF-8"));
                entity.addTextBody("apiUser", apiUser, TEXT_PLAIN);
                entity.addTextBody("apiKey", apiKey, TEXT_PLAIN);
                entity.addTextBody("to", to, TEXT_PLAIN);
                entity.addTextBody("from", from, TEXT_PLAIN);
                entity.addTextBody("fromName", "??????????????????????????????", TEXT_PLAIN);
                entity.addTextBody("subject", subject, TEXT_PLAIN);
                entity.addTextBody("html", html, TEXT_PLAIN);

                // ????????????
                ContentType OCTEC_STREAM = ContentType.create("application/octet-stream", Charset.forName("UTF-8"));
                File file = new File(filePath);
                String attachName = filePath.substring(filePath.lastIndexOf(splitSymbol));
                entity.addBinaryBody("attachments", file, OCTEC_STREAM, attachName);

                // ????????????
                httpPost.setEntity(entity.build());
                HttpResponse response = httpClient.execute(httpPost);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    System.out.println(EntityUtils.toString(response.getEntity()));
                } else {
                    System.err.println("error");
                }
                httpPost.releaseConnection();

            } catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException("?????????????????????");
            }
        }
        return true;
    }

    /**
     * ??????????????????
     * @param supplierSchedules
     * @return
     * @throws IOException
     */
    public String handleExcel(List<SupplierSchedule> supplierSchedules) throws IOException {

        // ???????????????
        FileInputStream fis = new FileInputStream(MODEL_PATH);
        XSSFWorkbook workBook = new XSSFWorkbook(fis);

        // ??????????????????????????????????????????
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        String thisWeekStartTime = monday.getMonthValue() + "/" + monday.getDayOfMonth();
        String thisWeekEndTime = sunday.getMonthValue() + "/" + sunday.getDayOfMonth();
        String thisWeek = thisWeekStartTime + "-" + thisWeekEndTime;

        LocalDate nextWeekDay = LocalDate.now().plusDays(7);
        LocalDate nextMonday = nextWeekDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate nextSunday = nextWeekDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        String nextWeekStartTime = nextMonday.getMonthValue() + "/" + nextMonday.getDayOfMonth();
        String nextWeekEndTime = nextSunday.getMonthValue() + "/" + nextSunday.getDayOfMonth();
        String nextWeek = nextWeekStartTime + "-" + nextWeekEndTime;

        // ?????????????????????(??????????????????????????????????????????sheet)
        XSSFSheet sheet = workBook.cloneSheet(0);
        workBook.setSheetName(1, "???????????????"); // ???sheet??????

        // ?????????????????????(???????????????cell?????????????????????????????????)
        sheet.getRow(1).getCell(9).setCellValue(thisWeek);
        sheet.getRow(1).getCell(13).setCellValue(nextWeek);

        int i = 3;
        for(SupplierSchedule schedule:supplierSchedules){
            XSSFRow creRow = sheet.createRow(i);
            creRow.createCell(0).setCellValue(schedule.getItemCode()); // ????????????
            creRow.createCell(1).setCellValue(schedule.getItemName());// ????????????
            creRow.createCell(2).setCellValue(schedule.getSupName());// ?????????
            creRow.createCell(3).setCellValue(schedule.getPo());// ?????????
            creRow.createCell(4).setCellValue(schedule.getDdsl()==null ? "" : new DecimalFormat("#").format(schedule.getDdsl()).toString());// ????????????
            creRow.createCell(5).setCellValue(schedule.getShsl()==null ? "" : new DecimalFormat("#").format(schedule.getShsl()).toString());// ???????????????
            creRow.createCell(6).setCellValue(schedule.getWshsl()==null ? "" : new DecimalFormat("#").format(schedule.getWshsl()).toString());// ???????????????
            creRow.createCell(7).setCellValue(schedule.getXqsl()==null ? "" : new DecimalFormat("#").format(schedule.getXqsl()).toString());// ????????????
            creRow.createCell(8).setCellValue(schedule.getLswdh()==null ? "" : new DecimalFormat("#").format(schedule.getLswdh()).toString());// ?????????????????????
            creRow.createCell(9).setCellValue(schedule.getCwjhshsl()==null ? "" : new DecimalFormat("#").format(schedule.getCwjhshsl()).toString());// ??????????????????????????????
            creRow.createCell(10).setCellValue((schedule.getCwqssl()==null || schedule.getCwqssl().compareTo(new BigDecimal("0")) == 0) ? "" : new DecimalFormat("#").format(schedule.getCwqssl()).toString());// ??????????????????????????????
            creRow.createCell(11).setCellValue(schedule.getCwkdhsl()==null ? "" : new DecimalFormat("#").format(schedule.getCwkdhsl()).toString());// ???????????????????????????
            creRow.createCell(12).setCellValue(schedule.getCwkdhrq()==null ? "":new SimpleDateFormat("yyyy-MM-dd").format(schedule.getCwkdhrq()));// ???????????????????????????
            creRow.createCell(13).setCellValue(schedule.getN1wjhshsl()==null ? "" : new DecimalFormat("#").format(schedule.getN1wjhshsl()).toString());// ??????????????????????????????
            creRow.createCell(14).setCellValue(schedule.getN1wjhshsl()==null ? "" : new DecimalFormat("#").format(schedule.getN1wjhshsl()).toString());// ???????????????????????????
            creRow.createCell(15).setCellValue(schedule.getN1wkdhrq()==null ? "":new SimpleDateFormat("yyyy-MM-dd").format(schedule.getN1wkdhrq()));// ???????????????????????????
            i++;
        }

        // ?????????????????????Excel????????????????????????????????????excel
        String fileName = "??????????????? - " + supplierSchedules.get(0).getSupNo() + "-" + today + ".xlsx";
        OutputStream out = new FileOutputStream(OUTPUT_PATH + fileName);
        workBook.removeSheetAt(0); // ??????workbook????????????sheet
        workBook.write(out);

        fis.close();
        out.flush();
        out.close();

        return OUTPUT_PATH + fileName;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveData(SupplierScheduleReq supplierScheduleReq) {

        String type = supplierScheduleReq.getStatuss();

        List<SupplierSchedule> supplierScheduleList  = new ArrayList<>();

        for(SupplierSchedule schedule : supplierScheduleReq.getScheduleList()){
            SupplierSchedule supplierSchedule = supplierScheduleMapper.getSupplierScheduleByItemCodeAndSupNo(schedule);
            if(THIS_WEEK.equals(type)){
                supplierSchedule.setCwkdhsl(schedule.getCwkdhsl());
                supplierSchedule.setCwkdhrq(schedule.getCwkdhrq());
            } else if(SECOND_WEEK.equals(type)) {
                supplierSchedule.setN1wkdhsl(schedule.getN1wkdhsl());
                supplierSchedule.setN1wkdhrq(schedule.getN1wkdhrq());
            } else if(THIRD_WEEK.equals(type)) {
                supplierSchedule.setN2wkdhsl(schedule.getN2wkdhsl());
                supplierSchedule.setN2wkdhrq(schedule.getN2wkdhrq());
            }else if(FORTH_WEEK.equals(type)) {
                supplierSchedule.setN3wkdhsl(schedule.getN3wkdhsl());
                supplierSchedule.setN3wkdhrq(schedule.getN3wkdhrq());
            } else  {
                supplierSchedule.setN4wkdhsl(schedule.getN4wkdhsl());
                supplierSchedule.setN4wkdhrq(schedule.getN4wkdhrq());
            }
            supplierScheduleMapper.updateByItemCodeAndSupNo(supplierSchedule);
            supplierScheduleList.add(supplierSchedule);
        }
        this.toUpdateBI(supplierScheduleList,type);
        return true;
    }

    /**
     * ??????bi???
     * @param scheduleList
     * @param type
     */
    public void toUpdateBI(List<SupplierSchedule> scheduleList, String type) {
        // ???????????????????????????????????????SQL?????????
        String tableName ="";
        if(THIS_WEEK.equals(type)){
            tableName = "BI_SupDeliv_Plan";
        } else if(SECOND_WEEK.equals(type)) {
            tableName = "BI_SupDeliv_Plan_nwk";
        } else if(THIRD_WEEK.equals(type)) {
            tableName = "BI_SupDeliv_Plan_n2wk";
        }else if(FORTH_WEEK.equals(type)) {
            tableName = "BI_SupDeliv_Plan_n3wk";
        } else  {
            tableName = "BI_SupDeliv_Plan_n4wk";
        }

        String driver = sqlServerDriver;
        String url = sqlServerUrl; //mydb???????????????
        String user = sqlServerUser;
        String password = sqlServerPassword;
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            Class.forName(driver);
            //1.????????????
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);// ?????????????????????
            Statement s = conn.createStatement();

            for(SupplierSchedule schedule : scheduleList) {
                //2.??????????????????sql
                String sql = "update "+ tableName +" set kdhuonum = ?,kdhuodate = ? where lb = ? and material_no = ? and supp_no = ? and yr = ? and wk = ?";
                //3.???????????????sql??????(????????????)
                stmt = conn.prepareStatement(sql);
                //4.???????????????
                stmt.setString(3, schedule.getLb());
                stmt.setString(4, schedule.getItemCode());
                stmt.setString(5, schedule.getSupNo());

                if(THIS_WEEK.equals(type)){
                    if(schedule.getCwkdhsl()!=null){
                        stmt.setString(1, schedule.getCwkdhsl().toString());
                    } else {
                        stmt.setNull(1, Types.DECIMAL);
                    }
                    if(schedule.getCwkdhrq()!=null){
                        stmt.setDate(2, new java.sql.Date(schedule.getCwkdhrq().getTime()));
                    } else {
                        stmt.setNull(2, Types.NVARCHAR);
                    }
                    stmt.setString(6, schedule.getYr());
                    stmt.setString(7, schedule.getWk());
                } else if(SECOND_WEEK.equals(type)) {
                    if(schedule.getN1wkdhsl()!=null){
                        stmt.setString(1, schedule.getN1wkdhsl().toString());
                    } else {
                        stmt.setNull(1, Types.DECIMAL);
                    }
                    if(schedule.getN1wkdhrq()!=null){
                        stmt.setDate(2, new java.sql.Date(schedule.getN1wkdhrq().getTime()));
                    } else {
                        stmt.setNull(2, Types.NVARCHAR);
                    }
                    stmt.setString(6, schedule.getNyr());
                    stmt.setString(7, schedule.getNwk());
                } else if(THIRD_WEEK.equals(type)) {
                    if(schedule.getN2wkdhsl()!=null){
                        stmt.setString(1, schedule.getN2wkdhsl().toString());
                    } else {
                        stmt.setNull(1, Types.DECIMAL);
                    }
                    if(schedule.getN2wkdhrq()!=null){
                        stmt.setDate(2, new java.sql.Date(schedule.getN2wkdhrq().getTime()));
                    } else {
                        stmt.setNull(2, Types.NVARCHAR);
                    }
                    stmt.setString(6, schedule.getN2yr());
                    stmt.setString(7, schedule.getN2wk());
                }else if(FORTH_WEEK.equals(type)) {
                    if(schedule.getN3wkdhsl()!=null){
                        stmt.setString(1, schedule.getN3wkdhsl().toString());
                    } else {
                        stmt.setNull(1, Types.DECIMAL);
                    }
                    if(schedule.getN3wkdhrq()!=null){
                        stmt.setDate(2, new java.sql.Date(schedule.getN3wkdhrq().getTime()));
                    } else {
                        stmt.setNull(2, Types.NVARCHAR);
                    }
                    stmt.setString(6, schedule.getN3yr());
                    stmt.setString(7, schedule.getN3wk());
                } else  {
                    if(schedule.getN4wkdhsl()!=null){
                        stmt.setString(1, schedule.getN4wkdhsl().toString());
                    } else {
                        stmt.setNull(1, Types.DECIMAL);
                    }
                    if(schedule.getN4wkdhrq()!=null){
                        stmt.setDate(2, new java.sql.Date(schedule.getN4wkdhrq().getTime()));
                    } else {
                        stmt.setNull(2, Types.NVARCHAR);
                    }
                    stmt.setString(6, schedule.getN4yr());
                    stmt.setString(7, schedule.getN4wk());
                }

                stmt.executeUpdate();// ?????????
            }
            conn.commit(); // ??????????????????????????????????????????

        } catch (Exception e) {
            try {
                conn.rollback();// ???????????????????????????
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("??????BI??????");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("??????BI??????");
                }
            }
        }
    }

    @Override
    public void exportAll(SupplierScheduleReq supplierScheduleReq, HttpServletResponse response) {
        List<SupplierSchedule> scheduleList = this.baseMapper.getAllBIInfo();

        // ??????????????????????????????????????????
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        String thisWeekStartTime = monday.getMonthValue() + "/" + monday.getDayOfMonth();
        String thisWeekEndTime = sunday.getMonthValue() + "/" + sunday.getDayOfMonth();
        String thisWeek = thisWeekStartTime + "-" + thisWeekEndTime;

        LocalDate nextWeekDay = LocalDate.now().plusDays(7);
        LocalDate nextMonday = nextWeekDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate nextSunday = nextWeekDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        String nextWeekStartTime = nextMonday.getMonthValue() + "/" + nextMonday.getDayOfMonth();
        String nextWeekEndTime = nextSunday.getMonthValue() + "/" + nextSunday.getDayOfMonth();
        String nextWeek = nextWeekStartTime + "-" + nextWeekEndTime;
        WeekFields n2W = WeekFields.of(DayOfWeek.MONDAY, 1);
        int n2 = nextMonday.get(n2W.weekOfYear());

        LocalDate n3WeekDay = LocalDate.now().plusDays(14);
        LocalDate n3Monday = n3WeekDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate n3Sunday = n3WeekDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        String n3WeekStartTime = n3Monday.getMonthValue() + "/" + n3Monday.getDayOfMonth();
        String n3WeekEndTime = n3Sunday.getMonthValue() + "/" + n3Sunday.getDayOfMonth();
        String n3Week = n3WeekStartTime + "-" + n3WeekEndTime;
        WeekFields n3W = WeekFields.of(DayOfWeek.MONDAY, 1);
        int n3 = n3Monday.get(n3W.weekOfYear());

        LocalDate n4WeekDay = LocalDate.now().plusDays(21);
        LocalDate n4Monday = n4WeekDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate n4Sunday = n4WeekDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        String n4WeekStartTime = n4Monday.getMonthValue() + "/" + n4Monday.getDayOfMonth();
        String n4WeekEndTime = n4Sunday.getMonthValue() + "/" + n4Sunday.getDayOfMonth();
        String n4Week = n4WeekStartTime + "-" + n4WeekEndTime;
        WeekFields n4W = WeekFields.of(DayOfWeek.MONDAY, 1);
        int n4 = n4Monday.get(n4W.weekOfYear());

        LocalDate n5WeekDay = LocalDate.now().plusDays(28);
        LocalDate n5Monday = n5WeekDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate n5Sunday = n5WeekDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        String n5WeekStartTime = n5Monday.getMonthValue() + "/" + n5Monday.getDayOfMonth();
        String n5WeekEndTime = n5Sunday.getMonthValue() + "/" + n5Sunday.getDayOfMonth();
        String n5Week = n5WeekStartTime + "-" + n5WeekEndTime;
        WeekFields n5W = WeekFields.of(DayOfWeek.MONDAY, 1);
        int n5 = n5Monday.get(n5W.weekOfYear());

        // ?????????
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("?????????????????????");//??????sheet??????
        HSSFRow row1 = sheet.createRow(0); //???sheet???????????????????????????????????????
        HSSFRow row2 = sheet.createRow(1); //???sheet???????????????????????????????????????
        HSSFRow row3 = sheet.createRow(2); //???sheet???????????????????????????????????????
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER); //?????????????????????????????????
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); //?????????????????????????????????


        //??????????????? ?????????int firstRow, int lastRow, int firstCol, int lastCol)
        for (int i = 0; i < 10; i++) {
            CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 2, i, i);
            //???sheet????????????????????????
            sheet.addMergedRegion(cellRangeAddress);
        }
        CellRangeAddress cellRangeAddress1 = new CellRangeAddress(0, 0, 10, 13);
        CellRangeAddress cellRangeAddress2 = new CellRangeAddress(1, 1, 10, 13);
        CellRangeAddress cellRangeAddress3 = new CellRangeAddress(0, 0, 14, 16);
        CellRangeAddress cellRangeAddress4 = new CellRangeAddress(1, 1, 14, 16);
        CellRangeAddress cellRangeAddress5 = new CellRangeAddress(0, 0, 17, 19);
        CellRangeAddress cellRangeAddress6 = new CellRangeAddress(1, 1, 17, 19);
        CellRangeAddress cellRangeAddress7 = new CellRangeAddress(0, 0, 20, 22);
        CellRangeAddress cellRangeAddress8 = new CellRangeAddress(1, 1, 20, 22);
        CellRangeAddress cellRangeAddress9 = new CellRangeAddress(0, 0, 23, 25);
        CellRangeAddress cellRangeAddress10 = new CellRangeAddress(1, 1, 23, 25);
        CellRangeAddress cellRangeAddress11 = new CellRangeAddress(0, 0, 26, 28);
        CellRangeAddress cellRangeAddress12 = new CellRangeAddress(1, 1, 26, 28);
        sheet.addMergedRegion(cellRangeAddress1);
        sheet.addMergedRegion(cellRangeAddress2);
        sheet.addMergedRegion(cellRangeAddress3);
        sheet.addMergedRegion(cellRangeAddress4);
        sheet.addMergedRegion(cellRangeAddress5);
        sheet.addMergedRegion(cellRangeAddress6);
        sheet.addMergedRegion(cellRangeAddress7);
        sheet.addMergedRegion(cellRangeAddress8);
        sheet.addMergedRegion(cellRangeAddress9);
        sheet.addMergedRegion(cellRangeAddress10);
        sheet.addMergedRegion(cellRangeAddress11);
        sheet.addMergedRegion(cellRangeAddress12);

        // ??????
        row1.createCell(0).setCellValue("????????????");
        row1.createCell(1).setCellValue("????????????");
        row1.createCell(2).setCellValue("?????????");
        row1.createCell(3).setCellValue("??????");
        row1.createCell(4).setCellValue("?????????");
        row1.createCell(5).setCellValue("????????????");
        row1.createCell(6).setCellValue("???????????????");
        row1.createCell(7).setCellValue("???????????????");
        row1.createCell(8).setCellValue("??????????????????");
        row1.createCell(9).setCellValue("?????????????????????");
        row1.createCell(10).setCellValue(thisWeek);
        row1.createCell(14).setCellValue(nextWeek);
        row1.createCell(17).setCellValue(n3Week);
        row1.createCell(20).setCellValue(n4Week);
        row1.createCell(23).setCellValue(n5Week);
        row2.createCell(10).setCellValue("??????");
        row2.createCell(14).setCellValue("???"+ n2 +"???");
        row2.createCell(17).setCellValue("???"+ n3 +"???");
        row2.createCell(20).setCellValue("???"+ n4 +"???");
        row2.createCell(23).setCellValue("???"+ n5 +"???");
        row2.createCell(26).setCellValue("?????????");
        row3.createCell(10).setCellValue("??????????????????");
        row3.createCell(11).setCellValue("??????????????????");
        row3.createCell(12).setCellValue("???????????????");
        row3.createCell(13).setCellValue("???????????????");
        row3.createCell(14).setCellValue("??????????????????");
        row3.createCell(15).setCellValue("???????????????");
        row3.createCell(16).setCellValue("???????????????");
        row3.createCell(17).setCellValue("??????????????????");
        row3.createCell(18).setCellValue("???????????????");
        row3.createCell(19).setCellValue("???????????????");
        row3.createCell(20).setCellValue("??????????????????");
        row3.createCell(21).setCellValue("???????????????");
        row3.createCell(22).setCellValue("???????????????");
        row3.createCell(23).setCellValue("??????????????????");
        row3.createCell(24).setCellValue("???????????????");
        row3.createCell(25).setCellValue("???????????????");
        row3.createCell(26).setCellValue("??????????????????");
        row3.createCell(27).setCellValue("???????????????");
        row3.createCell(28).setCellValue("???????????????");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int i = 3;
        for (SupplierSchedule schedule: scheduleList) {
            HSSFRow row = sheet.createRow(i);

            //????????????
            for (int x = 0; x < 29; x++) {
                row.createCell(x).setCellStyle(cellStyle);
            }

            row.getCell(0).setCellValue(schedule.getItemCode());
            row.getCell(1).setCellValue(schedule.getItemName());
            row.getCell(2).setCellValue(schedule.getSupName());
            row.getCell(3).setCellValue(schedule.getProgNo());
            row.getCell(4).setCellValue(schedule.getPo());
            row.getCell(5).setCellValue(schedule.getDdsl() == null ? "" : schedule.getDdsl().toString());
            row.getCell(6).setCellValue(schedule.getShsl() == null ? "" : schedule.getShsl().toString());
            row.getCell(7).setCellValue(schedule.getWshsl() == null ? "" : schedule.getWshsl().toString());
            row.getCell(8).setCellValue(schedule.getXqsl() == null ? "" : schedule.getXqsl().toString());
            row.getCell(9).setCellValue(schedule.getLswdh() == null ? "" : schedule.getLswdh().toString());
            row.getCell(10).setCellValue(schedule.getCwjhshsl() == null ? "" : schedule.getCwjhshsl().toString());
            row.getCell(11).setCellValue(schedule.getCwkdhsl() == null ? "" : schedule.getCwkdhsl().toString());
            row.getCell(12).setCellValue(schedule.getCwqssl() == null ? "" : schedule.getCwqssl().toString());
            row.getCell(13).setCellValue(schedule.getCwkdhrq() == null ? "" : sdf.format(schedule.getCwkdhrq()));
            row.getCell(14).setCellValue(schedule.getN1wjhshsl() == null ? "" : schedule.getN1wjhshsl().toString());
            row.getCell(15).setCellValue(schedule.getN1wkdhsl() == null ? "" : schedule.getN1wkdhsl().toString());
            row.getCell(16).setCellValue(schedule.getN1wkdhrq() == null ? "" : sdf.format(schedule.getN1wkdhrq()));
            row.getCell(17).setCellValue(schedule.getN2wjhshsl() == null ? "" : schedule.getN2wjhshsl().toString());
            row.getCell(18).setCellValue(schedule.getN2wkdhsl() == null ? "" : schedule.getN2wkdhsl().toString());
            row.getCell(19).setCellValue(schedule.getN2wkdhrq() == null ? "" : sdf.format(schedule.getN2wkdhrq()));
            row.getCell(20).setCellValue(schedule.getN3wjhshsl() == null ? "" : schedule.getN3wjhshsl().toString());
            row.getCell(21).setCellValue(schedule.getN3wkdhsl() == null ? "" : schedule.getN3wkdhsl().toString());
            row.getCell(22).setCellValue(schedule.getN3wkdhrq() == null ? "" : sdf.format(schedule.getN3wkdhrq()));
            row.getCell(23).setCellValue(schedule.getN4wjhshsl() == null ? "" : schedule.getN4wjhshsl().toString());
            row.getCell(24).setCellValue(schedule.getN4wkdhsl() == null ? "" : schedule.getN4wkdhsl().toString());
            row.getCell(25).setCellValue(schedule.getN4wkdhrq() == null ? "" : sdf.format(schedule.getN4wkdhrq()));
            row.getCell(26).setCellValue(schedule.getWljhshsl() == null ? "" : schedule.getWljhshsl().toString());
            row.getCell(27).setCellValue(schedule.getWlkdhsl() == null ? "" : schedule.getWlkdhsl().toString());
            row.getCell(28).setCellValue(schedule.getWlkdhrq() == null ? "" : sdf.format(schedule.getWlkdhrq()));

            i++;
        }

        String fileName = "?????????????????????" + DateUtil.formatDate(new Date()) + ".xls";

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            wb.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("???????????????");
        }
    }


    /**
     * ?????????????????????
     * @param page
     * @param caiGouScheduleReq
     * @return
     */
    @Override
    public IPage<CaiGouSchedule> getCaiGouSchedules(IPage<CaiGouSchedule> page, CaiGouScheduleReq caiGouScheduleReq) {

        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;

        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            caiGouScheduleReq.setDutyPerson(getUser().getAccount());
        }
        if(caiGouScheduleReq.getPoCodeLn()!=null && !caiGouScheduleReq.getPoCodeLn().isEmpty() && caiGouScheduleReq.getPoCodeLn().indexOf(",")>=0){
            caiGouScheduleReq.setPoCodeLnBatch("1");
        } else {
            caiGouScheduleReq.setPoCodeLnBatch("0");
        }
        if(caiGouScheduleReq.getItemCode()!=null && !caiGouScheduleReq.getItemCode().isEmpty() && caiGouScheduleReq.getItemCode().indexOf(",")>=0){
            caiGouScheduleReq.setItemCodeBatch("1");
        } else {
            caiGouScheduleReq.setItemCodeBatch("0");
        }
        if(caiGouScheduleReq.getProNo()!=null && !caiGouScheduleReq.getProNo().isEmpty() && caiGouScheduleReq.getProNo().indexOf(",")>=0){
            caiGouScheduleReq.setProNoBatch("1");
        } else {
            caiGouScheduleReq.setProNoBatch("0");
        }
        if(caiGouScheduleReq.getDoStatuss()!=null && !caiGouScheduleReq.getDoStatuss().isEmpty() && caiGouScheduleReq.getDoStatuss().indexOf("19")>=0){
            caiGouScheduleReq.setIsToSendStatus("1");
        } else {
            caiGouScheduleReq.setIsToSendStatus("0");
        }

        IPage<CaiGouSchedule> supplierSchedules = supplierScheduleMapper.getCaiGouSchedule(page, caiGouScheduleReq);

        for (CaiGouSchedule schedule : supplierSchedules.getRecords()) {

            //?????????????????????A??????????????????????????????????????????
            if (StringUtil.isBlank(schedule.getProNo())  && "A".equals(schedule.getCodeType())){
                schedule.setReqDate(schedule.getSupConfirmDate());
            }

            Integer doStatus = schedule.getDoStatus();

            //????????????????????????????????????????????????
            if("21,22,23,24,26".indexOf(Integer.toString(doStatus))==-1){
                schedule.setSnCreateTime("");
            }


            if (doStatus == null) {
                //schedule.setArrivalStatus("?????????");
                schedule.setCheckStatus("19");
                schedule.setStoreStatus("?????????");
            } else {
                String statusStroe = ""; // po???????????????
                String rcvCode = schedule.getRcvCode(); // ????????????DO??????

                if (schedule.getPoCode().indexOf("PR") > -1) {
                    schedule.setStoreStatus("?????????");
                } else {
                    // ????????????
                    if(schedule.getOrderNum()!=null && schedule.getStoreNum()!= null) {
                        Integer tcNum = schedule.getOrderNum().intValue();
                        Integer storeNum = schedule.getStoreNum();
                        if (storeNum <= 0) {
                            statusStroe = "?????????";
                        } else if (storeNum > 0 && storeNum < tcNum) {
                            statusStroe = "????????????";
                        } else if (storeNum > 0 && storeNum >= tcNum) {
                            statusStroe = "?????????";
                        } else {
                            statusStroe = "";
                        }
                    } else {
                        statusStroe = "";
                    }
                    schedule.setCheckStatus(doStatus.toString());
                    schedule.setStoreStatus(statusStroe);
                }
            }
        }
        return supplierSchedules;
    }

    /**
     * ?????????????????????
     * @param page
     * @param caiGouScheduleReq
     * @return
     */
    @Override
    public IPage<CaiGouSchedule> caiGouScheduleAutoSort(IPage<CaiGouSchedule> page, CaiGouScheduleReq caiGouScheduleReq) {

        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;

        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            caiGouScheduleReq.setDutyPerson(getUser().getAccount());
        }
        if(caiGouScheduleReq.getPoCodeLn()!=null && !caiGouScheduleReq.getPoCodeLn().isEmpty() && caiGouScheduleReq.getPoCodeLn().indexOf(",")>=0){
            caiGouScheduleReq.setPoCodeLnBatch("1");
        } else {
            caiGouScheduleReq.setPoCodeLnBatch("0");
        }
        if(caiGouScheduleReq.getItemCode()!=null && !caiGouScheduleReq.getItemCode().isEmpty() && caiGouScheduleReq.getItemCode().indexOf(",")>=0){
            caiGouScheduleReq.setItemCodeBatch("1");
        } else {
            caiGouScheduleReq.setItemCodeBatch("0");
        }
        if(caiGouScheduleReq.getProNo()!=null && !caiGouScheduleReq.getProNo().isEmpty() && caiGouScheduleReq.getProNo().indexOf(",")>=0){
            caiGouScheduleReq.setProNoBatch("1");
        } else {
            caiGouScheduleReq.setProNoBatch("0");
        }
        if(caiGouScheduleReq.getDoStatuss()!=null && !caiGouScheduleReq.getDoStatuss().isEmpty() && caiGouScheduleReq.getDoStatuss().indexOf("19")>=0){
            caiGouScheduleReq.setIsToSendStatus("1");
        } else {
            caiGouScheduleReq.setIsToSendStatus("0");
        }

        IPage<CaiGouSchedule> supplierSchedules = supplierScheduleMapper.getCaiGouScheduleAutoSort(page, caiGouScheduleReq);

        for (CaiGouSchedule schedule : supplierSchedules.getRecords()) {

            //?????????????????????A??????????????????????????????????????????
            if (StringUtil.isBlank(schedule.getProNo())  && "A".equals(schedule.getCodeType())){
                schedule.setReqDate(schedule.getSupConfirmDate());
            }

            Integer doStatus = schedule.getDoStatus();

            //????????????????????????????????????????????????
            if("21,22,23,24,26".indexOf(Integer.toString(doStatus))==-1){
                schedule.setSnCreateTime("");
            }


            if (doStatus == null) {
                //schedule.setArrivalStatus("?????????");
                schedule.setCheckStatus("19");
                schedule.setStoreStatus("?????????");
            } else {
                String statusStroe = ""; // po???????????????
                String rcvCode = schedule.getRcvCode(); // ????????????DO??????

                if (schedule.getPoCode().indexOf("PR") > -1) {
                    schedule.setStoreStatus("?????????");
                } else {
                    // ????????????
                    if(schedule.getOrderNum()!=null && schedule.getStoreNum()!= null) {
                        Integer tcNum = schedule.getOrderNum().intValue();
                        Integer storeNum = schedule.getStoreNum();
                        if (storeNum <= 0) {
                            statusStroe = "?????????";
                        } else if (storeNum > 0 && storeNum < tcNum) {
                            statusStroe = "????????????";
                        } else if (storeNum > 0 && storeNum >= tcNum) {
                            statusStroe = "?????????";
                        } else {
                            statusStroe = "";
                        }
                    } else {
                        statusStroe = "";
                    }
                    schedule.setCheckStatus(doStatus.toString());
                    schedule.setStoreStatus(statusStroe);
                }
            }
        }
        return supplierSchedules;
    }

    /**
     * ??????
     * @param caiGouScheduleReq
     * @param response
     */
    @Override
    public void exportCaiGouAll(CaiGouScheduleReq caiGouScheduleReq, HttpServletResponse response) {
        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            caiGouScheduleReq.setDutyPerson(getUser().getAccount());
        }
        if(caiGouScheduleReq.getPoCodeLn()!=null && !caiGouScheduleReq.getPoCodeLn().isEmpty() && caiGouScheduleReq.getPoCodeLn().indexOf(",")>=0){
            caiGouScheduleReq.setPoCodeLnBatch("1");
        } else {
            caiGouScheduleReq.setPoCodeLnBatch("0");
        }
        if(caiGouScheduleReq.getItemCode()!=null && !caiGouScheduleReq.getItemCode().isEmpty() && caiGouScheduleReq.getItemCode().indexOf(",")>=0){
            caiGouScheduleReq.setItemCodeBatch("1");
        } else {
            caiGouScheduleReq.setItemCodeBatch("0");
        }
        if(caiGouScheduleReq.getProNo()!=null && !caiGouScheduleReq.getProNo().isEmpty() && caiGouScheduleReq.getProNo().indexOf(",")>=0){
            caiGouScheduleReq.setProNoBatch("1");
        } else {
            caiGouScheduleReq.setProNoBatch("0");
        }
        if(caiGouScheduleReq.getDoStatuss()!=null && !caiGouScheduleReq.getDoStatuss().isEmpty() && caiGouScheduleReq.getDoStatuss().indexOf("19")>=0){
            caiGouScheduleReq.setIsToSendStatus("1");
        } else {
            caiGouScheduleReq.setIsToSendStatus("0");
        }

        List<CaiGouSchedule> caiGouSchedules = supplierScheduleMapper.getCaiGouScheduleList(caiGouScheduleReq);

        for (CaiGouSchedule schedule : caiGouSchedules) {


            //?????????????????????A??????????????????????????????????????????
            if (StringUtil.isBlank(schedule.getProNo())  && "A".equals(schedule.getCodeType())){
                schedule.setReqDate(schedule.getSupConfirmDate());
            }

            Integer doStatus = schedule.getDoStatus();

            //????????????????????????????????????????????????
            if("21,22,23,24,26".indexOf(Integer.toString(doStatus))==-1){
                schedule.setSnCreateTime("");
            }

            if(doStatus == null) {
                schedule.setArrivalStatus("?????????");
                schedule.setCheckStatus("?????????");
                schedule.setStoreStatus("?????????");
            } else {
                String statusValue = "";
                String statusStroe = "";
                switch (doStatus){
                    case 20: statusValue = "?????????";break;
                    case 21: statusValue = "???????????????";break;
                    case 22: statusValue = "???????????????";break;
                    case 23: statusValue = "???????????????";break;
                    case 24: statusValue = "???????????????";break;
                    case 25: statusValue = "?????????";break;
                    case 26: statusValue = "?????????";break;
                    case 27: statusValue = "???????????????";break;
                    case 30: statusValue = "?????????";break;
                    case 40: statusValue = "?????????";break;
                }
                schedule.setCheckStatus(statusValue);
                schedule.setArrivalStatus(statusValue);

                if(schedule.getPoCode().indexOf("PR")>-1){
                    schedule.setStoreStatus("?????????");
                } else {
                    // ????????????
                    if(schedule.getOrderNum()!=null && schedule.getStoreNum()!= null) {
                        Integer tcNum = schedule.getOrderNum().intValue();
                        Integer storeNum = schedule.getStoreNum();
                        if (storeNum <= 0) {
                            statusStroe = "?????????";
                        } else if (storeNum > 0 && storeNum < tcNum) {
                            statusStroe = "????????????";
                        } else if (storeNum > 0 && storeNum >= tcNum) {
                            statusStroe = "?????????";
                        } else {
                            statusStroe = "";
                        }
                    } else {
                        statusStroe = "";
                    }
                    schedule.setStoreStatus(statusStroe);
                }
            }
        }
        List<CaiGouScheduleExcel> caiGouScheduleExcels = new ArrayList<>();
        for (CaiGouScheduleExcel demo : caiGouScheduleExcels) {
            System.out.println("demo = " + demo);
        }


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for(CaiGouSchedule caiGouSchedule : caiGouSchedules){
            CaiGouScheduleExcel caiGouScheduleExcel = BeanUtil.copy(caiGouSchedule,CaiGouScheduleExcel.class);
            caiGouScheduleExcel.setAgreeDate(caiGouSchedule.getAgreeDate()==null ? "" : sdf.format(caiGouSchedule.getAgreeDate()));
            caiGouScheduleExcel.setPlanDate(caiGouSchedule.getPlanDate()==null ? "" : sdf.format(caiGouSchedule.getPlanDate()));
            caiGouScheduleExcel.setReqDate(caiGouSchedule.getReqDate()==null ? "" : sdf.format(caiGouSchedule.getReqDate()));
            //caiGouScheduleExcel.setWwpoDate(caiGouSchedule.getWwpoDate()==null ? "" : sdf.format(caiGouSchedule.getWwpoDate()));
            caiGouScheduleExcel.setNowReqDate(caiGouSchedule.getNowReqDate()==null ? "" : sdf.format(caiGouSchedule.getNowReqDate()));
            caiGouScheduleExcel.setCheckUpdateDate(caiGouSchedule.getCheckUpdateDate()==null ? "" : sdf.format(caiGouSchedule.getCheckUpdateDate()));
            caiGouScheduleExcel.setCheckUpdateDateFrist(caiGouSchedule.getCheckUpdateDateFrist()==null ? "" : sdf.format(caiGouSchedule.getCheckUpdateDateFrist()));
            caiGouScheduleExcel.setCheckStatus(caiGouSchedule.getCheckStatus());
            caiGouScheduleExcel.setStoreStatus(caiGouSchedule.getStoreStatus());
            caiGouScheduleExcel.setWwpoDate(caiGouSchedule.getSupConfirmDate()==null ? "" :sdf.format(caiGouSchedule.getSupConfirmDate()));
            if(caiGouSchedule.getIsUrgent()!=null) {
                String isUrgent = caiGouSchedule.getIsUrgent().equals("1") ? "???" : "???";
                caiGouScheduleExcel.setIsUrgent(isUrgent);
            }
            caiGouScheduleExcels.add(caiGouScheduleExcel);
        }

        ExcelUtils.defaultExport(caiGouScheduleExcels, CaiGouScheduleExcel.class, "?????????????????????" + DateUtil.formatDate(new Date()), response);
    }

    /**
     * ??????
     * @param caiGouScheduleReq
     * @param response
     */
    @Override
    public void exportCaiGouAllAutoSort(CaiGouScheduleReq caiGouScheduleReq, HttpServletResponse response) {
        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            caiGouScheduleReq.setDutyPerson(getUser().getAccount());
        }
        if(caiGouScheduleReq.getPoCodeLn()!=null && !caiGouScheduleReq.getPoCodeLn().isEmpty() && caiGouScheduleReq.getPoCodeLn().indexOf(",")>=0){
            caiGouScheduleReq.setPoCodeLnBatch("1");
        } else {
            caiGouScheduleReq.setPoCodeLnBatch("0");
        }
        if(caiGouScheduleReq.getItemCode()!=null && !caiGouScheduleReq.getItemCode().isEmpty() && caiGouScheduleReq.getItemCode().indexOf(",")>=0){
            caiGouScheduleReq.setItemCodeBatch("1");
        } else {
            caiGouScheduleReq.setItemCodeBatch("0");
        }
        if(caiGouScheduleReq.getProNo()!=null && !caiGouScheduleReq.getProNo().isEmpty() && caiGouScheduleReq.getProNo().indexOf(",")>=0){
            caiGouScheduleReq.setProNoBatch("1");
        } else {
            caiGouScheduleReq.setProNoBatch("0");
        }
        if(caiGouScheduleReq.getDoStatuss()!=null && !caiGouScheduleReq.getDoStatuss().isEmpty() && caiGouScheduleReq.getDoStatuss().indexOf("19")>=0){
            caiGouScheduleReq.setIsToSendStatus("1");
        } else {
            caiGouScheduleReq.setIsToSendStatus("0");
        }

        List<CaiGouSchedule> caiGouSchedules = supplierScheduleMapper.getCaiGouScheduleAutoSortList(caiGouScheduleReq);

        for (CaiGouSchedule schedule : caiGouSchedules) {


            //?????????????????????A??????????????????????????????????????????
            if (StringUtil.isBlank(schedule.getProNo())  && "A".equals(schedule.getCodeType())){
                schedule.setReqDate(schedule.getSupConfirmDate());
            }

            Integer doStatus = schedule.getDoStatus();

            //????????????????????????????????????????????????
            if("21,22,23,24,26".indexOf(Integer.toString(doStatus))==-1){
                schedule.setSnCreateTime("");
            }

            if(doStatus == null) {
                schedule.setArrivalStatus("?????????");
                schedule.setCheckStatus("?????????");
                schedule.setStoreStatus("?????????");
            } else {
                String statusValue = "";
                String statusStroe = "";
                switch (doStatus){
                    case 20: statusValue = "?????????";break;
                    case 21: statusValue = "???????????????";break;
                    case 22: statusValue = "???????????????";break;
                    case 23: statusValue = "???????????????";break;
                    case 24: statusValue = "???????????????";break;
                    case 25: statusValue = "?????????";break;
                    case 26: statusValue = "?????????";break;
                    case 27: statusValue = "???????????????";break;
                    case 30: statusValue = "?????????";break;
                    case 40: statusValue = "?????????";break;
                }
                schedule.setCheckStatus(statusValue);
                schedule.setArrivalStatus(statusValue);

                if(schedule.getPoCode().indexOf("PR")>-1){
                    schedule.setStoreStatus("?????????");
                } else {
                    // ????????????
                    if(schedule.getOrderNum()!=null && schedule.getStoreNum()!= null) {
                        Integer tcNum = schedule.getOrderNum().intValue();
                        Integer storeNum = schedule.getStoreNum();
                        if (storeNum <= 0) {
                            statusStroe = "?????????";
                        } else if (storeNum > 0 && storeNum < tcNum) {
                            statusStroe = "????????????";
                        } else if (storeNum > 0 && storeNum >= tcNum) {
                            statusStroe = "?????????";
                        } else {
                            statusStroe = "";
                        }
                    } else {
                        statusStroe = "";
                    }
                    schedule.setStoreStatus(statusStroe);
                }
            }
        }
        List<CaiGouScheduleExcel> caiGouScheduleExcels = new ArrayList<>();
        for (CaiGouScheduleExcel demo : caiGouScheduleExcels) {
            System.out.println("demo = " + demo);
        }


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for(CaiGouSchedule caiGouSchedule : caiGouSchedules){
            CaiGouScheduleExcel caiGouScheduleExcel = BeanUtil.copy(caiGouSchedule,CaiGouScheduleExcel.class);
            caiGouScheduleExcel.setAgreeDate(caiGouSchedule.getAgreeDate()==null ? "" : sdf.format(caiGouSchedule.getAgreeDate()));
            caiGouScheduleExcel.setPlanDate(caiGouSchedule.getPlanDate()==null ? "" : sdf.format(caiGouSchedule.getPlanDate()));
            caiGouScheduleExcel.setReqDate(caiGouSchedule.getReqDate()==null ? "" : sdf.format(caiGouSchedule.getReqDate()));
            //caiGouScheduleExcel.setWwpoDate(caiGouSchedule.getWwpoDate()==null ? "" : sdf.format(caiGouSchedule.getWwpoDate()));
            caiGouScheduleExcel.setNowReqDate(caiGouSchedule.getNowReqDate()==null ? "" : sdf.format(caiGouSchedule.getNowReqDate()));
            caiGouScheduleExcel.setCheckUpdateDate(caiGouSchedule.getCheckUpdateDate()==null ? "" : sdf.format(caiGouSchedule.getCheckUpdateDate()));
            caiGouScheduleExcel.setCheckUpdateDateFrist(caiGouSchedule.getCheckUpdateDateFrist()==null ? "" : sdf.format(caiGouSchedule.getCheckUpdateDateFrist()));
            caiGouScheduleExcel.setCheckStatus(caiGouSchedule.getCheckStatus());
            caiGouScheduleExcel.setStoreStatus(caiGouSchedule.getStoreStatus());
            caiGouScheduleExcel.setWwpoDate(caiGouSchedule.getSupConfirmDate()==null ? "" :sdf.format(caiGouSchedule.getSupConfirmDate()));
            if(caiGouSchedule.getIsUrgent()!=null) {
                String isUrgent = caiGouSchedule.getIsUrgent().equals("1") ? "???" : "???";
                caiGouScheduleExcel.setIsUrgent(isUrgent);
            }
            caiGouScheduleExcels.add(caiGouScheduleExcel);
        }

        ExcelUtils.defaultExport(caiGouScheduleExcels, CaiGouScheduleExcel.class, "?????????????????????" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public void exportCaiGouAllOffset(CaiGouScheduleReq caiGouScheduleReq, HttpServletResponse response) {
        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            caiGouScheduleReq.setDutyPerson(getUser().getAccount());
        }
        if(caiGouScheduleReq.getPoCodeLn()!=null && !caiGouScheduleReq.getPoCodeLn().isEmpty() && caiGouScheduleReq.getPoCodeLn().indexOf(",")>=0){
            caiGouScheduleReq.setPoCodeLnBatch("1");
        } else {
            caiGouScheduleReq.setPoCodeLnBatch("0");
        }
        if(caiGouScheduleReq.getItemCode()!=null && !caiGouScheduleReq.getItemCode().isEmpty() && caiGouScheduleReq.getItemCode().indexOf(",")>=0){
            caiGouScheduleReq.setItemCodeBatch("1");
        } else {
            caiGouScheduleReq.setItemCodeBatch("0");
        }
        if(caiGouScheduleReq.getProNo()!=null && !caiGouScheduleReq.getProNo().isEmpty() && caiGouScheduleReq.getProNo().indexOf(",")>=0){
            caiGouScheduleReq.setProNoBatch("1");
        } else {
            caiGouScheduleReq.setProNoBatch("0");
        }
        if(caiGouScheduleReq.getDoStatuss()!=null && !caiGouScheduleReq.getDoStatuss().isEmpty() && caiGouScheduleReq.getDoStatuss().indexOf("19")>=0){
            caiGouScheduleReq.setIsToSendStatus("1");
        } else {
            caiGouScheduleReq.setIsToSendStatus("0");
        }

        List<CaiGouSchedule> caiGouSchedules = supplierScheduleMapper.getCaiGouScheduleOffsetList(caiGouScheduleReq);

        for (CaiGouSchedule schedule : caiGouSchedules) {
            Integer doStatus = schedule.getDoStatus();
            if(doStatus == null) {
                schedule.setArrivalStatus("?????????");
                schedule.setCheckStatus("?????????");
                schedule.setStoreStatus("?????????");
            } else {
                String statusValue = "";
                String statusStroe = "";
                switch (doStatus){
                    case 20: statusValue = "?????????";break;
                    case 21: statusValue = "???????????????";break;
                    case 22: statusValue = "???????????????";break;
                    case 23: statusValue = "???????????????";break;
                    case 24: statusValue = "???????????????";break;
                    case 25: statusValue = "?????????";break;
                    case 26: statusValue = "?????????";break;
                    case 27: statusValue = "???????????????";break;
                    case 30: statusValue = "?????????";break;
                    case 40: statusValue = "?????????";break;
                }
                schedule.setCheckStatus(statusValue);
                schedule.setArrivalStatus(statusValue);

                if(schedule.getPoCode().indexOf("PR")>-1){
                    schedule.setStoreStatus("?????????");
                } else {
                    // ????????????
                    if(schedule.getOrderNum()!=null && schedule.getStoreNum()!= null) {
                        Integer tcNum = schedule.getOrderNum().intValue();
                        Integer storeNum = schedule.getStoreNum();
                        if (storeNum <= 0) {
                            statusStroe = "?????????";
                        } else if (storeNum > 0 && storeNum < tcNum) {
                            statusStroe = "????????????";
                        } else if (storeNum > 0 && storeNum >= tcNum) {
                            statusStroe = "?????????";
                        } else {
                            statusStroe = "";
                        }
                    } else {
                        statusStroe = "";
                    }
                    schedule.setStoreStatus(statusStroe);
                }
            }
        }
        List<CaiGouScheduleExcel> caiGouScheduleExcels = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for(CaiGouSchedule caiGouSchedule : caiGouSchedules){
            CaiGouScheduleExcel caiGouScheduleExcel = BeanUtil.copy(caiGouSchedule,CaiGouScheduleExcel.class);
            caiGouScheduleExcel.setAgreeDate(caiGouSchedule.getAgreeDate()==null ? "" : sdf.format(caiGouSchedule.getAgreeDate()));
            caiGouScheduleExcel.setPlanDate(caiGouSchedule.getPlanDate()==null ? "" : sdf.format(caiGouSchedule.getPlanDate()));
            caiGouScheduleExcel.setReqDate(caiGouSchedule.getReqDate()==null ? "" : sdf.format(caiGouSchedule.getReqDate()));
            caiGouScheduleExcel.setWwpoDate(caiGouSchedule.getWwpoDate()==null ? "" : sdf.format(caiGouSchedule.getWwpoDate()));
            caiGouScheduleExcel.setNowReqDate(caiGouSchedule.getNowReqDate()==null ? "" : sdf.format(caiGouSchedule.getNowReqDate()));
            caiGouScheduleExcel.setCheckUpdateDate(caiGouSchedule.getCheckUpdateDate()==null ? "" : sdf.format(caiGouSchedule.getCheckUpdateDate()));
            caiGouScheduleExcel.setCheckStatus(caiGouSchedule.getCheckStatus());
            caiGouScheduleExcel.setStoreStatus(caiGouSchedule.getStoreStatus());
            caiGouScheduleExcels.add(caiGouScheduleExcel);
        }

        ExcelUtils.defaultExport(caiGouScheduleExcels, CaiGouScheduleExcel.class, "?????????????????????" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public void exportCaiGouAllUnchecked(CaiGouScheduleReq caiGouScheduleReq, HttpServletResponse response) {
        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            caiGouScheduleReq.setDutyPerson(getUser().getAccount());
        }
        if(caiGouScheduleReq.getPoCodeLn()!=null && !caiGouScheduleReq.getPoCodeLn().isEmpty() && caiGouScheduleReq.getPoCodeLn().indexOf(",")>=0){
            caiGouScheduleReq.setPoCodeLnBatch("1");
        } else {
            caiGouScheduleReq.setPoCodeLnBatch("0");
        }
        if(caiGouScheduleReq.getItemCode()!=null && !caiGouScheduleReq.getItemCode().isEmpty() && caiGouScheduleReq.getItemCode().indexOf(",")>=0){
            caiGouScheduleReq.setItemCodeBatch("1");
        } else {
            caiGouScheduleReq.setItemCodeBatch("0");
        }
        if(caiGouScheduleReq.getProNo()!=null && !caiGouScheduleReq.getProNo().isEmpty() && caiGouScheduleReq.getProNo().indexOf(",")>=0){
            caiGouScheduleReq.setProNoBatch("1");
        } else {
            caiGouScheduleReq.setProNoBatch("0");
        }
        if(caiGouScheduleReq.getDoStatuss()!=null && !caiGouScheduleReq.getDoStatuss().isEmpty() && caiGouScheduleReq.getDoStatuss().indexOf("19")>=0){
            caiGouScheduleReq.setIsToSendStatus("1");
        } else {
            caiGouScheduleReq.setIsToSendStatus("0");
        }

        List<CaiGouSchedule> caiGouSchedules = supplierScheduleMapper.getCaiGouScheduleUncheckedList(caiGouScheduleReq);

        for (CaiGouSchedule schedule : caiGouSchedules) {
            Integer doStatus = schedule.getDoStatus();
            if(doStatus == null) {
                schedule.setArrivalStatus("?????????");
                schedule.setCheckStatus("?????????");
                schedule.setStoreStatus("?????????");
            } else {
                String statusValue = "";
                String statusStroe = "";
                switch (doStatus){
                    case 20: statusValue = "?????????";break;
                    case 21: statusValue = "???????????????";break;
                    case 22: statusValue = "???????????????";break;
                    case 23: statusValue = "???????????????";break;
                    case 24: statusValue = "???????????????";break;
                    case 25: statusValue = "?????????";break;
                    case 26: statusValue = "?????????";break;
                    case 27: statusValue = "???????????????";break;
                    case 30: statusValue = "?????????";break;
                    case 40: statusValue = "?????????";break;
                }
                schedule.setCheckStatus(statusValue);
                schedule.setArrivalStatus(statusValue);

                if(schedule.getPoCode().indexOf("PR")>-1){
                    schedule.setStoreStatus("?????????");
                } else {
                    // ????????????
                    if(schedule.getOrderNum()!=null && schedule.getStoreNum()!= null) {
                        Integer tcNum = schedule.getOrderNum().intValue();
                        Integer storeNum = schedule.getStoreNum();
                        if (storeNum <= 0) {
                            statusStroe = "?????????";
                        } else if (storeNum > 0 && storeNum < tcNum) {
                            statusStroe = "????????????";
                        } else if (storeNum > 0 && storeNum >= tcNum) {
                            statusStroe = "?????????";
                        } else {
                            statusStroe = "";
                        }
                    } else {
                        statusStroe = "";
                    }
                    schedule.setStoreStatus(statusStroe);
                }
            }
        }
        List<CaiGouScheduleExcel> caiGouScheduleExcels = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for(CaiGouSchedule caiGouSchedule : caiGouSchedules){
            CaiGouScheduleExcel caiGouScheduleExcel = BeanUtil.copy(caiGouSchedule,CaiGouScheduleExcel.class);
            caiGouScheduleExcel.setAgreeDate(caiGouSchedule.getAgreeDate()==null ? "" : sdf.format(caiGouSchedule.getAgreeDate()));
            caiGouScheduleExcel.setPlanDate(caiGouSchedule.getPlanDate()==null ? "" : sdf.format(caiGouSchedule.getPlanDate()));
            caiGouScheduleExcel.setReqDate(caiGouSchedule.getReqDate()==null ? "" : sdf.format(caiGouSchedule.getReqDate()));
            caiGouScheduleExcel.setWwpoDate(caiGouSchedule.getWwpoDate()==null ? "" : sdf.format(caiGouSchedule.getWwpoDate()));
            caiGouScheduleExcel.setNowReqDate(caiGouSchedule.getNowReqDate()==null ? "" : sdf.format(caiGouSchedule.getNowReqDate()));
            caiGouScheduleExcel.setCheckUpdateDate(caiGouSchedule.getCheckUpdateDate()==null ? "" : sdf.format(caiGouSchedule.getCheckUpdateDate()));
            caiGouScheduleExcel.setCheckStatus(caiGouSchedule.getCheckStatus());
            caiGouScheduleExcel.setStoreStatus(caiGouSchedule.getStoreStatus());
            caiGouScheduleExcels.add(caiGouScheduleExcel);
        }

        ExcelUtils.defaultExport(caiGouScheduleExcels, CaiGouScheduleExcel.class, "?????????????????????" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public void exportCaiGouAllUnpip(CaiGouScheduleReq caiGouScheduleReq, HttpServletResponse response) {
        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            caiGouScheduleReq.setDutyPerson(getUser().getAccount());
        }
        if(caiGouScheduleReq.getPoCodeLn()!=null && !caiGouScheduleReq.getPoCodeLn().isEmpty() && caiGouScheduleReq.getPoCodeLn().indexOf(",")>=0){
            caiGouScheduleReq.setPoCodeLnBatch("1");
        } else {
            caiGouScheduleReq.setPoCodeLnBatch("0");
        }
        if(caiGouScheduleReq.getItemCode()!=null && !caiGouScheduleReq.getItemCode().isEmpty() && caiGouScheduleReq.getItemCode().indexOf(",")>=0){
            caiGouScheduleReq.setItemCodeBatch("1");
        } else {
            caiGouScheduleReq.setItemCodeBatch("0");
        }
        if(caiGouScheduleReq.getProNo()!=null && !caiGouScheduleReq.getProNo().isEmpty() && caiGouScheduleReq.getProNo().indexOf(",")>=0){
            caiGouScheduleReq.setProNoBatch("1");
        } else {
            caiGouScheduleReq.setProNoBatch("0");
        }
        if(caiGouScheduleReq.getDoStatuss()!=null && !caiGouScheduleReq.getDoStatuss().isEmpty() && caiGouScheduleReq.getDoStatuss().indexOf("19")>=0){
            caiGouScheduleReq.setIsToSendStatus("1");
        } else {
            caiGouScheduleReq.setIsToSendStatus("0");
        }

        List<CaiGouSchedule> caiGouSchedules = supplierScheduleMapper.getCaiGouScheduleUnpipList(caiGouScheduleReq);

        for (CaiGouSchedule schedule : caiGouSchedules) {
            Integer doStatus = schedule.getDoStatus();
            if(doStatus == null) {
                schedule.setArrivalStatus("?????????");
                schedule.setCheckStatus("?????????");
                schedule.setStoreStatus("?????????");
            } else {
                String statusValue = "";
                String statusStroe = "";
                switch (doStatus){
                    case 20: statusValue = "?????????";break;
                    case 21: statusValue = "???????????????";break;
                    case 22: statusValue = "???????????????";break;
                    case 23: statusValue = "???????????????";break;
                    case 24: statusValue = "???????????????";break;
                    case 25: statusValue = "?????????";break;
                    case 26: statusValue = "?????????";break;
                    case 27: statusValue = "???????????????";break;
                    case 30: statusValue = "?????????";break;
                    case 40: statusValue = "?????????";break;
                }
                schedule.setCheckStatus(statusValue);
                schedule.setArrivalStatus(statusValue);

                if(schedule.getPoCode().indexOf("PR")>-1){
                    schedule.setStoreStatus("?????????");
                } else {
                    // ????????????
                    if(schedule.getOrderNum()!=null && schedule.getStoreNum()!= null) {
                        Integer tcNum = schedule.getOrderNum().intValue();
                        Integer storeNum = schedule.getStoreNum();
                        if (storeNum <= 0) {
                            statusStroe = "?????????";
                        } else if (storeNum > 0 && storeNum < tcNum) {
                            statusStroe = "????????????";
                        } else if (storeNum > 0 && storeNum >= tcNum) {
                            statusStroe = "?????????";
                        } else {
                            statusStroe = "";
                        }
                    } else {
                        statusStroe = "";
                    }
                    schedule.setStoreStatus(statusStroe);
                }
            }
        }
        List<CaiGouScheduleExcel> caiGouScheduleExcels = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for(CaiGouSchedule caiGouSchedule : caiGouSchedules){
            CaiGouScheduleExcel caiGouScheduleExcel = BeanUtil.copy(caiGouSchedule,CaiGouScheduleExcel.class);
            caiGouScheduleExcel.setAgreeDate(caiGouSchedule.getAgreeDate()==null ? "" : sdf.format(caiGouSchedule.getAgreeDate()));
            caiGouScheduleExcel.setPlanDate(caiGouSchedule.getPlanDate()==null ? "" : sdf.format(caiGouSchedule.getPlanDate()));
            caiGouScheduleExcel.setReqDate(caiGouSchedule.getReqDate()==null ? "" : sdf.format(caiGouSchedule.getReqDate()));
            caiGouScheduleExcel.setWwpoDate(caiGouSchedule.getWwpoDate()==null ? "" : sdf.format(caiGouSchedule.getWwpoDate()));
            caiGouScheduleExcel.setNowReqDate(caiGouSchedule.getNowReqDate()==null ? "" : sdf.format(caiGouSchedule.getNowReqDate()));
            caiGouScheduleExcel.setCheckUpdateDate(caiGouSchedule.getCheckUpdateDate()==null ? "" : sdf.format(caiGouSchedule.getCheckUpdateDate()));
            caiGouScheduleExcel.setCheckStatus(caiGouSchedule.getCheckStatus());
            caiGouScheduleExcel.setStoreStatus(caiGouSchedule.getStoreStatus());
            caiGouScheduleExcels.add(caiGouScheduleExcel);
        }

        ExcelUtils.defaultExport(caiGouScheduleExcels, CaiGouScheduleExcel.class, "?????????????????????" + DateUtil.formatDate(new Date()), response);
    }

    /**
     * ????????????????????????
     * @param page
     * @param caiGouScheduleReq
     * @return
     */
    @Override
    public IPage<CaiGouSchedule> getCaiGouSchedulesOffset(IPage<CaiGouSchedule> page, CaiGouScheduleReq caiGouScheduleReq) {

        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            caiGouScheduleReq.setDutyPerson(getUser().getAccount());
        }
        if(caiGouScheduleReq.getPoCodeLn()!=null && !caiGouScheduleReq.getPoCodeLn().isEmpty() && caiGouScheduleReq.getPoCodeLn().indexOf(",")>=0){
            caiGouScheduleReq.setPoCodeLnBatch("1");
        } else {
            caiGouScheduleReq.setPoCodeLnBatch("0");
        }
        if(caiGouScheduleReq.getItemCode()!=null && !caiGouScheduleReq.getItemCode().isEmpty() && caiGouScheduleReq.getItemCode().indexOf(",")>=0){
            caiGouScheduleReq.setItemCodeBatch("1");
        } else {
            caiGouScheduleReq.setItemCodeBatch("0");
        }
        if(caiGouScheduleReq.getProNo()!=null && !caiGouScheduleReq.getProNo().isEmpty() && caiGouScheduleReq.getProNo().indexOf(",")>=0){
            caiGouScheduleReq.setProNoBatch("1");
        } else {
            caiGouScheduleReq.setProNoBatch("0");
        }
        if(caiGouScheduleReq.getDoStatuss()!=null && !caiGouScheduleReq.getDoStatuss().isEmpty() && caiGouScheduleReq.getDoStatuss().indexOf("19")>=0){
            caiGouScheduleReq.setIsToSendStatus("1");
        } else {
            caiGouScheduleReq.setIsToSendStatus("0");
        }

        IPage<CaiGouSchedule> supplierSchedules = supplierScheduleMapper.getCaiGouScheduleOffset(page, caiGouScheduleReq);

        for (CaiGouSchedule schedule : supplierSchedules.getRecords()) {
            Integer doStatus = schedule.getDoStatus();
            if (doStatus == null) {
                //schedule.setArrivalStatus("?????????");
                schedule.setCheckStatus("19");
                schedule.setStoreStatus("?????????");
            } else {
                String statusStroe = ""; // po???????????????
                String rcvCode = schedule.getRcvCode(); // ????????????DO??????

                if (schedule.getPoCode().indexOf("PR") > -1) {
                    schedule.setStoreStatus("?????????");
                } else {
                    // ????????????
                    if(schedule.getOrderNum()!=null && schedule.getStoreNum()!= null) {
                        Integer tcNum = schedule.getOrderNum().intValue();
                        Integer storeNum = schedule.getStoreNum();
                        if (storeNum <= 0) {
                            statusStroe = "?????????";
                        } else if (storeNum > 0 && storeNum < tcNum) {
                            statusStroe = "????????????";
                        } else if (storeNum > 0 && storeNum >= tcNum) {
                            statusStroe = "?????????";
                        } else {
                            statusStroe = "";
                        }
                    } else {
                        statusStroe = "";
                    }
                    schedule.setCheckStatus(doStatus.toString());
                    schedule.setStoreStatus(statusStroe);
                }
            }
        }
        return supplierSchedules;
    }


    /**
     * ???????????????????????????
     * @param page
     * @param caiGouScheduleReq
     * @return
     */
    @Override
    public IPage<CaiGouSchedule> getCaiGouSchedulesUnchecked(IPage<CaiGouSchedule> page, CaiGouScheduleReq caiGouScheduleReq) {

        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            caiGouScheduleReq.setDutyPerson(getUser().getAccount());
        }
        if(caiGouScheduleReq.getPoCodeLn()!=null && !caiGouScheduleReq.getPoCodeLn().isEmpty() && caiGouScheduleReq.getPoCodeLn().indexOf(",")>=0){
            caiGouScheduleReq.setPoCodeLnBatch("1");
        } else {
            caiGouScheduleReq.setPoCodeLnBatch("0");
        }
        if(caiGouScheduleReq.getItemCode()!=null && !caiGouScheduleReq.getItemCode().isEmpty() && caiGouScheduleReq.getItemCode().indexOf(",")>=0){
            caiGouScheduleReq.setItemCodeBatch("1");
        } else {
            caiGouScheduleReq.setItemCodeBatch("0");
        }
        if(caiGouScheduleReq.getProNo()!=null && !caiGouScheduleReq.getProNo().isEmpty() && caiGouScheduleReq.getProNo().indexOf(",")>=0){
            caiGouScheduleReq.setProNoBatch("1");
        } else {
            caiGouScheduleReq.setProNoBatch("0");
        }
        if(caiGouScheduleReq.getDoStatuss()!=null && !caiGouScheduleReq.getDoStatuss().isEmpty() && caiGouScheduleReq.getDoStatuss().indexOf("19")>=0){
            caiGouScheduleReq.setIsToSendStatus("1");
        } else {
            caiGouScheduleReq.setIsToSendStatus("0");
        }

        IPage<CaiGouSchedule> supplierSchedules = supplierScheduleMapper.getCaiGouScheduleUnchecked(page, caiGouScheduleReq);

        for (CaiGouSchedule schedule : supplierSchedules.getRecords()) {
            Integer doStatus = schedule.getDoStatus();
            if (doStatus == null) {
                //schedule.setArrivalStatus("?????????");
                schedule.setCheckStatus("19");
                schedule.setStoreStatus("?????????");
            } else {
                String statusStroe = ""; // po???????????????
                String rcvCode = schedule.getRcvCode(); // ????????????DO??????

                if (schedule.getPoCode().indexOf("PR") > -1) {
                    schedule.setStoreStatus("?????????");
                } else {
                    // ????????????
                    if(schedule.getOrderNum()!=null && schedule.getStoreNum()!= null) {
                        Integer tcNum = schedule.getOrderNum().intValue();
                        Integer storeNum = schedule.getStoreNum();
                        if (storeNum <= 0) {
                            statusStroe = "?????????";
                        } else if (storeNum > 0 && storeNum < tcNum) {
                            statusStroe = "????????????";
                        } else if (storeNum > 0 && storeNum >= tcNum) {
                            statusStroe = "?????????";
                        } else {
                            statusStroe = "";
                        }
                    } else {
                        statusStroe = "";
                    }
                    schedule.setCheckStatus(doStatus.toString());
                    schedule.setStoreStatus(statusStroe);
                }
            }
        }
        return supplierSchedules;
    }


    /**
     * ????????????????????????
     * @param page
     * @param caiGouScheduleReq
     * @return
     */
    @Override
    public IPage<CaiGouSchedule> getCaiGouSchedulesUnpip(IPage<CaiGouSchedule> page, CaiGouScheduleReq caiGouScheduleReq) {

        String mRoleId  = paramService.getValue("trace_admin.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            caiGouScheduleReq.setDutyPerson(getUser().getAccount());
        }
        if(caiGouScheduleReq.getPoCodeLn()!=null && !caiGouScheduleReq.getPoCodeLn().isEmpty() && caiGouScheduleReq.getPoCodeLn().indexOf(",")>=0){
            caiGouScheduleReq.setPoCodeLnBatch("1");
        } else {
            caiGouScheduleReq.setPoCodeLnBatch("0");
        }
        if(caiGouScheduleReq.getItemCode()!=null && !caiGouScheduleReq.getItemCode().isEmpty() && caiGouScheduleReq.getItemCode().indexOf(",")>=0){
            caiGouScheduleReq.setItemCodeBatch("1");
        } else {
            caiGouScheduleReq.setItemCodeBatch("0");
        }
        if(caiGouScheduleReq.getProNo()!=null && !caiGouScheduleReq.getProNo().isEmpty() && caiGouScheduleReq.getProNo().indexOf(",")>=0){
            caiGouScheduleReq.setProNoBatch("1");
        } else {
            caiGouScheduleReq.setProNoBatch("0");
        }
        if(caiGouScheduleReq.getDoStatuss()!=null && !caiGouScheduleReq.getDoStatuss().isEmpty() && caiGouScheduleReq.getDoStatuss().indexOf("19")>=0){
            caiGouScheduleReq.setIsToSendStatus("1");
        } else {
            caiGouScheduleReq.setIsToSendStatus("0");
        }

        IPage<CaiGouSchedule> supplierSchedules = supplierScheduleMapper.getCaiGouScheduleUnpip(page, caiGouScheduleReq);

        for (CaiGouSchedule schedule : supplierSchedules.getRecords()) {
            Integer doStatus = schedule.getDoStatus();
            if (doStatus == null) {
                //schedule.setArrivalStatus("?????????");
                schedule.setCheckStatus("19");
                schedule.setStoreStatus("?????????");
            } else {
                String statusStroe = ""; // po???????????????
                String rcvCode = schedule.getRcvCode(); // ????????????DO??????

                if (schedule.getPoCode().indexOf("PR") > -1) {
                    schedule.setStoreStatus("?????????");
                } else {
                    // ????????????
                    if(schedule.getOrderNum()!=null && schedule.getStoreNum()!= null) {
                        Integer tcNum = schedule.getOrderNum().intValue();
                        Integer storeNum = schedule.getStoreNum();
                        if (storeNum <= 0) {
                            statusStroe = "?????????";
                        } else if (storeNum > 0 && storeNum < tcNum) {
                            statusStroe = "????????????";
                        } else if (storeNum > 0 && storeNum >= tcNum) {
                            statusStroe = "?????????";
                        } else {
                            statusStroe = "";
                        }
                    } else {
                        statusStroe = "";
                    }
                    schedule.setCheckStatus(doStatus.toString());
                    schedule.setStoreStatus(statusStroe);
                }
            }
        }
        return supplierSchedules;
    }


    /**
     *  ??????????????????
     * @param caiGouScheduleReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDataOfCaiGou(CaiGouScheduleReq caiGouScheduleReq) {
        List<SupplierSchedule> supplierScheduleList  = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String account = getUser().getAccount();
        List<CaiGouSchedule> list = new ArrayList<>();
        List<String> keys = new ArrayList<>();

        //??????????????????
        for(CaiGouSchedule schedule : caiGouScheduleReq.getScheduleList()){
            /*if (StringUtils.isBlank(schedule.getProNo())) {
                continue;
            }*/

            CaiGouSchedule selectCaiGouSchedule = supplierScheduleMapper.selectCaiGouSchedule(schedule);

            Integer limits = Integer.valueOf(schedule.getLimits());
            String updateUser ="";
            if (selectCaiGouSchedule != null) {
                updateUser = selectCaiGouSchedule.getUpdateUser();
                // ???????????????????????????????????????
                if (selectCaiGouSchedule.getCheckUpdateDate() != null && schedule.getCheckUpdateDate() != null && selectCaiGouSchedule.getCheckUpdateDate().compareTo(schedule.getCheckUpdateDate()) != 0) {
                    limits = limits + 1;
                    updateUser = account;
                }
                if ((selectCaiGouSchedule.getCheckUpdateDate() == null && schedule.getCheckUpdateDate() != null) || (selectCaiGouSchedule.getCheckUpdateDate() != null && schedule.getCheckUpdateDate() == null)) {
                    limits = limits + 1;
                    updateUser = account;
                }
            }

            schedule.setLimits(limits.toString());
            schedule.setUpdateUser(updateUser);
            if (selectCaiGouSchedule!=null){
                supplierScheduleMapper.updateCaiGouRemark(schedule,account,df.format(new Date()));
            }else{
                supplierScheduleMapper.insertCaiGouRemark(schedule,account,df.format(new Date()));
            }

        }


        // ?????? poCode-poLn ??????
        for(CaiGouSchedule schedule : caiGouScheduleReq.getScheduleList()){
            String key = schedule.getPoCode()+'-'+schedule.getPoLn();
            if(!keys.contains(key)){
                list.add(schedule);
                keys.add(key);
            }
        }

        // ??????????????????
        for(CaiGouSchedule schedule : list){
            CaiGouSchedule item = supplierScheduleMapper.getWriteInfo(schedule);
            // ????????????????????????????????????
            /*if(item != null){
                Integer limits = Integer.valueOf(schedule.getLimits());
                String updateUser = item.getUpdateUser();

                // ???????????????????????????????????????
                if(item.getCheckUpdateDate()!=null  && schedule.getCheckUpdateDate()!=null && item.getCheckUpdateDate().compareTo(schedule.getCheckUpdateDate()) != 0) {
                    limits = limits + 1;
                    updateUser = account;
                }
                if((item.getCheckUpdateDate()==null && schedule.getCheckUpdateDate()!=null) || (item.getCheckUpdateDate()!=null && schedule.getCheckUpdateDate()==null)) {
                    limits = limits + 1;
                    updateUser = account;
                }

                schedule.setLimits(limits.toString());
                if(StringUtil.isBlank(schedule.getProNo())){
                    //???????????????
                    supplierScheduleMapper.updateCaiGouNoProNo(schedule,updateUser,df.format(new Date()));
                }else{
                    //????????????
                    supplierScheduleMapper.updateCaiGou(schedule,updateUser,df.format(new Date()));
                }

            } else {
                //??????
                if(StringUtil.isBlank(schedule.getProNo())){
                    supplierScheduleMapper.insertCaiGouNoProNo(schedule,account,df.format(new Date()));
                }else{
                    supplierScheduleMapper.insertCaiGou(schedule,account,df.format(new Date()));
                }

            }*/

            // ?????? SEQ ?????????
            if(schedule.getProNo()!=null && !schedule.getProNo().isEmpty() && supplierScheduleMapper.isExistedSeqByCG(schedule) > 0) {
                supplierScheduleMapper.updateCaiGouSeq(schedule);
            } else {
                supplierScheduleMapper.insertCaiGouSeq(schedule);
            }
        }
        return true;
    }

    /**
     * ??????1???1????????????????????????????????????
     * @throws Exception
     */
    @Scheduled(cron = "0 0 1 1 * ?") // ??????1???1????????????
    @Transactional(rollbackFor = Exception.class)
    public void resetUpdateCheckTimeLimits() throws Exception{
        supplierScheduleMapper.resetUpdateCheckTimeLimits();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean lockPro(CaiGouScheduleReq caiGouScheduleReq) {

        List<SupplierSchedule> supplierScheduleList  = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String account = getUser().getAccount();

        for(CaiGouSchedule schedule : caiGouScheduleReq.getScheduleList()){
            if(supplierScheduleMapper.isExistedByCGOfLock(schedule) == 0){
                supplierScheduleMapper.insertLockCaiGou(schedule);
            } else {
                throw new RuntimeException("???????????????????????????????????????!");
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean freePro(CaiGouScheduleReq caiGouScheduleReq) {

        List<SupplierSchedule> supplierScheduleList  = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String account = getUser().getAccount();

        for(CaiGouSchedule schedule : caiGouScheduleReq.getScheduleList()){
            if(supplierScheduleMapper.isExistedByCGOfLock(schedule) > 0){
                supplierScheduleMapper.deleteLockedInfo(schedule.getPoCode(),schedule.getPoLn(),schedule.getProNo());
            } else {
                throw new RuntimeException("???????????????????????????????????????!");
            }
        }
        return true;
    }

    @Scheduled(cron = "0 0 6 ? * *")
    //@PostConstruct
    public void autoSortPlan() {
        log.info("??????????????????????????????????????????" + new Date());

        List<CaiGouSchedule> selectCaiGouPlandiff = supplierScheduleMapper.selectCaiGouPlandiff();
        selectCaiGouPlandiff.addAll(supplierScheduleMapper.selectCaiGouPlandiff2());

        for ( CaiGouSchedule diff:selectCaiGouPlandiff) {
            supplierScheduleMapper.deleteAllCaiGouPlanFromDisk(String.valueOf(diff.getId()));
        }

        //???????????????????????????????????????15110???130301???130302???130101???130102???131111???131106 ??????????????????
        List<CaiGouSchedule> caiGouSchedules = supplierScheduleMapper.selectAllCaiGouPlan();
        caigouPlanSort(caiGouSchedules);

        //??????????????????
        saveHistoryData();


        log.info("??????????????????????????????????????????" + new Date());

    }



    private void caigouPlanSort(List<CaiGouSchedule> caiGouSchedules) {
        for (CaiGouSchedule item : caiGouSchedules) {

            List<CaiGouSchedule> polist = null;

            //reqNum???null?????? ???????????? ??????PR?????????????????????????????????  ??????????????????????????????7???
            if(item.getReqNum()==null){
                PrPoLink(item);
            }

            //????????????PO????????????????????????????????????>???????????????
            BigDecimal reqNum = item.getReqNum();

            //????????????PO????????????????????????????????????????????????????????????????????????????????????po
            List<CaiGouSchedule> caiGouSchedulelockList = supplierScheduleMapper.selectLockData(item.getItemCode(), item.getProNo());

            List<CaiGouSchedule> caiGouSchedules1=new ArrayList<>();

            for (CaiGouSchedule caiGouSchedule:caiGouSchedulelockList) {
                //?????????PR???????????????????????????PO???????????????
                if (caiGouSchedule.getPoCode().startsWith("PR")){
                    List<PoItemEntity> poItemFromPR = supplierScheduleMapper.getPoItemFromPR(caiGouSchedule.getPoCode(), caiGouSchedule.getPoLn());
                    for (PoItemEntity poItemEntity:poItemFromPR) {
                        List<CaiGouSchedule> caiGouSchedules2 = supplierScheduleMapper.selectAllCaiGouPlanPoWithPoln(poItemEntity.getItemCode(), poItemEntity.getPoCode()+"-"+poItemEntity.getPoLn(),item.getOrgcode());
                        if(caiGouSchedules2.size()>0){
                            caiGouSchedules1.add(caiGouSchedules2.get(0));
                        }

                    }
                }

                caiGouSchedules1.add(caiGouSchedule);

            }

            if (caiGouSchedules1.size() > 0) {
                List<CaiGouSchedule> caiGouSchedules2 =new ArrayList<>();
                for (CaiGouSchedule lockdataitem : caiGouSchedules1) {

                        caiGouSchedules2 = supplierScheduleMapper.selectAllCaiGouPlanPoWithPoln(lockdataitem.getItemCode(), lockdataitem.getPoCodeLn(),item.getOrgcode());

                        if (caiGouSchedules2.size() <= 0||caiGouSchedules2.get(0).getYsdsl()>caiGouSchedules2.get(0).getWshsl()) {
                            supplierScheduleMapper.DeleteCaiGouPlan(lockdataitem.getItemCode(), lockdataitem.getProNo(), lockdataitem.getId().toString());
                            continue;
                        }

                        CaiGouSchedule podata = caiGouSchedules2.get(0);

                        int po_kysl = podata.getWshsl() - podata.getYsdsl();//po????????????

                        if(po_kysl<=0){
                            po_kysl=0;
                        }

                        BigDecimal reqNumOld = lockdataitem.getReqNum() == null || lockdataitem.getReqNum().compareTo(BigDecimal.ZERO) == -1? new BigDecimal("0") : lockdataitem.getReqNum();
                        reqNumOld = reqNumOld.add(new BigDecimal(po_kysl));//????????????+????????????


                        if (reqNum.compareTo(reqNumOld) == 1 || reqNum.compareTo(reqNumOld) == 0) {//???????????????>=????????????+????????????
                            supplierScheduleMapper.updateReqNum(item.getPlanDate(), item.getReqDate(), String.valueOf(reqNumOld), lockdataitem.getId().toString());
                            reqNum = reqNum.subtract(reqNumOld);
                        } else {
                            //???????????????<????????????+????????????
                            if (reqNum.compareTo(BigDecimal.ZERO) == 0) {
                                supplierScheduleMapper.DeleteCaiGouPlan(lockdataitem.getItemCode(), lockdataitem.getProNo(), lockdataitem.getId().toString());
                            }
                            supplierScheduleMapper.updateReqNum(item.getPlanDate(), item.getReqDate(), reqNum.toString(), lockdataitem.getId().toString());
                            reqNum = reqNum.subtract(reqNum);

                        }


                }

                if(reqNum.compareTo(BigDecimal.ZERO) == 1){
                    polist = supplierScheduleMapper.selectAllCaiGouPlanPo(item.getItemCode(),item.getOrgcode());
                    for (CaiGouSchedule po : polist) {
                        int po_kysl = po.getWshsl() - po.getYsdsl();//po????????????

                        if (reqNum.compareTo(BigDecimal.ZERO) == 1 && po_kysl>=0) {//????????????????????????????????????0?????????????????????po

                            //??????po??????????????????????????????
                            CaiGouSchedule lockdata = new CaiGouSchedule();
                            BeanUtil.copy(po, lockdata);
                            lockdata.setProNo(item.getProNo());
                            lockdata.setAgreeDate(item.getAgreeDate());
                            lockdata.setPlanDate(item.getPlanDate());
                            lockdata.setProNum(item.getProNum());
                            lockdata.setReqDate(item.getReqDate());

                            if (reqNum.compareTo(new BigDecimal(po_kysl)) == 1) {
                                lockdata.setReqNum(new BigDecimal(po_kysl));
                            } else {
                                lockdata.setReqNum(reqNum);
                            }
                            reqNum = reqNum.subtract(new BigDecimal(po_kysl));
                            supplierScheduleMapper.insertMatchLockCaiGou(lockdata);

                        }
                    }
                }

                //???????????????Po???????????????reqNum????????????0???????????????PR
                if (reqNum.compareTo(BigDecimal.ZERO) == 1){
                    CaiGouSchedule caiGouSchedule = supplierScheduleMapper.selectCaiGouPlanPR(item.getItemCode(),item.getOrgcode());
                    if(caiGouSchedule!=null){
                        //??????po??????????????????????????????
                        CaiGouSchedule lockdata = new CaiGouSchedule();
                        BeanUtil.copy(caiGouSchedule, lockdata);
                        lockdata.setProNo(item.getProNo());
                        lockdata.setAgreeDate(item.getAgreeDate());
                        lockdata.setPlanDate(item.getPlanDate());
                        lockdata.setProNum(item.getProNum());
                        lockdata.setReqNum(reqNum);
                        lockdata.setReqDate(item.getReqDate());
                        supplierScheduleMapper.insertMatchLockCaiGou(lockdata);
                    }
                }

            } else {
                polist = supplierScheduleMapper.selectAllCaiGouPlanPo(item.getItemCode(),item.getOrgcode());
                for (CaiGouSchedule po : polist) {
                    int po_kysl = po.getWshsl() - po.getYsdsl();//po????????????

                    if (reqNum.compareTo(BigDecimal.ZERO) == 1 && po_kysl>=0) {//????????????????????????????????????0?????????????????????po

                        //??????po??????????????????????????????
                        CaiGouSchedule lockdata = new CaiGouSchedule();
                        BeanUtil.copy(po, lockdata);
                        lockdata.setProNo(item.getProNo());
                        lockdata.setAgreeDate(item.getAgreeDate());
                        lockdata.setPlanDate(item.getPlanDate());
                        lockdata.setProNum(item.getProNum());
                        lockdata.setReqDate(item.getReqDate());

                        if (reqNum.compareTo(new BigDecimal(po_kysl)) == 1) {
                            lockdata.setReqNum(new BigDecimal(po_kysl));
                        } else {
                            lockdata.setReqNum(reqNum);
                        }
                        reqNum = reqNum.subtract(new BigDecimal(po_kysl));
                        supplierScheduleMapper.insertMatchLockCaiGou(lockdata);

                    }
                }
                //???????????????Po???????????????reqNum????????????0???????????????PR
                if (reqNum.compareTo(BigDecimal.ZERO) == 1){
                    CaiGouSchedule caiGouSchedule = supplierScheduleMapper.selectCaiGouPlanPR(item.getItemCode(),item.getOrgcode());
                    if(caiGouSchedule!=null){
                        //??????po??????????????????????????????
                        CaiGouSchedule lockdata = new CaiGouSchedule();
                        BeanUtil.copy(caiGouSchedule, lockdata);
                        lockdata.setProNo(item.getProNo());
                        lockdata.setAgreeDate(item.getAgreeDate());
                        lockdata.setPlanDate(item.getPlanDate());
                        lockdata.setProNum(item.getProNum());
                        lockdata.setReqNum(reqNum);
                        lockdata.setReqDate(item.getReqDate());
                        supplierScheduleMapper.insertMatchLockCaiGou(lockdata);


                    }
                }

            }


        }
    }

    private void PrPoLink(CaiGouSchedule item) {
        List<CaiGouSchedule> qitaolist = supplierScheduleMapper.selectQtData(item.getItemCode(), item.getProNo());

        BigDecimal ReqNum=new BigDecimal(0);
        Date reqdate=null;
        for (CaiGouSchedule qitao:qitaolist) {
            CaiGouSchedule PRdata = supplierScheduleMapper.selectQtDataReqNum(qitao.getPoCodeLn(),qitao.getProNo());
            ReqNum=ReqNum.add(PRdata.getReqNum());
            reqdate=PRdata.getReqDate();
        }
        item.setReqNum(ReqNum);

        //item.setPlanDate(cn.hutool.core.date.DateUtil.offsetDay(item.getPlanDate(), -7));
        item.setReqDate(cn.hutool.core.date.DateUtil.offsetDay(reqdate, -7));
    }

    private void saveHistoryData() {
        supplierScheduleMapper.saveAllCaiGouPlanAsHistory();
        supplierScheduleMapper.updateCaiGouPlanHistoryDate();
    }




    /*public void autoLockPlan() {
        log.info("??????16?????????????????????????????????????????????" + new Date());
        //???????????????????????????

        String nextMonth=DateUtil.format(cn.hutool.core.date.DateUtil.nextMonth(), "yyyy-MM");
        supplierScheduleMapper.LockCaiGouPlan(nextMonth);


        log.info("??????16?????????????????????????????????????????????" + new Date());
    }*/


    //@Scheduled(cron = "0 0 5 ? * *")
    //@PostConstruct
    public void autoLockOfCaiGou(){
        System.out.println("????????????????????????????????????????????????"+new Date());
        SimpleDateFormat formatWeb = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        //????????????????????????????????????
        List<String> proNoList = supplierScheduleMapper.selectAllProNo();

        for(String proNo : proNoList){
            // ?????????pro_no????????????????????????????????????
            Integer isExisted = supplierScheduleMapper.selectLockedProNoIsExisted(proNo);

            // ??????????????????????????????????????????
            List<CaiGouSchedule> caiGouSchedules = supplierScheduleMapper.selectNewBIInfo(proNo);


            if(caiGouSchedules.get(0).getPlanDate()!= null) {
                String planTimeNew = formatWeb.format(caiGouSchedules.get(0).getPlanDate());
                supplierScheduleMapper.updatePlanDateFromproNo(planTimeNew,proNo);
            }


            caiGouSchedules.forEach(caiGouSchedule -> {
                String planTimeNew = "";
                String reqDateNew = "";
                String lackItemNum = "";
                Integer proNum = null;
                String poCodeLn = caiGouSchedule.getPoCodeLn();
                String isBigFourItem  = caiGouSchedule.getIsFourBigItem();
                String itemCode = caiGouSchedule.getItemCode();
                Long id  = caiGouSchedule.getId();

                // PR??????????????????????????????????????????????????????BOM????????????????????????????????????
                if (poCodeLn.indexOf("PR") > -1 && isBigFourItem == null) {
                    // ?????? ????????????????????? ?????????????????????
                    List<CaiGouSchedule> hisInfo = supplierScheduleMapper.selectLockedInfoByProAndItemCode(proNo, itemCode);
                    // ???????????????
                    if (hisInfo != null && hisInfo.size() > 0) {
                        CaiGouSchedule his = hisInfo.get(0);
                        his.setId(id);
                        his.setPlanDate(caiGouSchedule.getPlanDate());
                        // ?????????po???????????????
                        PoItemEntity poItemEntity = supplierScheduleMapper.selectPoItemInfo(his.getPoCode(), his.getPoLn());
                        if (poItemEntity != null && poItemEntity.getStatus() != 30) {
                            supplierScheduleMapper.updateWWBomInfoByHisInfo(his);
                        }
                    }
                }

                if(caiGouSchedule.getPlanDate()!= null) {
                    planTimeNew = formatWeb.format(caiGouSchedule.getPlanDate());
                }

                if(caiGouSchedule.getReqDate()!= null){
                    reqDateNew = formatWeb.format(caiGouSchedule.getReqDate());
                }

                if(caiGouSchedule.getLackItemNum()!= null){
                    lackItemNum = caiGouSchedule.getLackItemNum().toString();
                }

                if(caiGouSchedule.getProNum()!= null){
                    proNum = Integer.valueOf(caiGouSchedule.getProNum().toString());
                }

                if(poCodeLn.indexOf("PO")>-1 || poCodeLn.indexOf("WW")>-1){
                    // ????????????????????????????????????(????????????????????????????????????)??????????????????
                    supplierScheduleMapper.updateLockedInfo(proNo,planTimeNew,reqDateNew,lackItemNum,poCodeLn,proNum);
                }
            });

                // ???????????????
                if(isExisted > 0 ){
                    // ?????????????????????PO??????
                    List<CaiGouSchedule> caiGouSchedulesToLocked = supplierScheduleMapper.selectToLockInfo(proNo);

                    // ?????????????????????PO??????
                    List<CaiGouSchedule> caiGouSchedulesisLocked = supplierScheduleMapper.selectLockedInfo(proNo);

                    // ?????????????????????PO??????
                    for(CaiGouSchedule caiGouSchedule : caiGouSchedulesToLocked) {
                        String poCode = caiGouSchedule.getPoCode();
                        if( poCode==null || poCode.isEmpty() || poCode.indexOf("PR") > -1) {
                            continue;
                        }

                        // ????????????????????????????????????????????????ItemCode
                        Integer itemIsExisted = supplierScheduleMapper.selectItemIsExistedOfPro(proNo, caiGouSchedule.getItemCode());
                        if (itemIsExisted == 0) {
                            synchronized (SupplierServiceImpl.class){
                                boolean b = supplierScheduleMapper.insertLockCaiGou(caiGouSchedule);
                            }


                        }
                    }
                } else { // ???????????????
                    // ?????????????????????????????????????????????
                    for (CaiGouSchedule caiGouSchedule : caiGouSchedules) {
                        String itemCode = caiGouSchedule.getItemCode();
                        // ????????????0??????????????????
                        if (supplierScheduleMapper.selectIsNeedLock(itemCode) > 0 && caiGouSchedule.getPoCodeLn().indexOf("PO") > -1) {

                            synchronized (SupplierServiceImpl.class){
                                boolean b = supplierScheduleMapper.insertLockCaiGou(caiGouSchedule);
                            }

                        }
                    }
                }



        }
        System.out.println("????????????????????????????????????????????????"+new Date());
    }


    /**
     * ????????????????????????
     */
    @Scheduled(cron = "0 30 6 ? * *")
    @Scheduled(cron = "0 43 11 ? * *")
    @Transactional(isolation = Isolation.SERIALIZABLE,rollbackFor = Exception.class)
    public void autoArrRateOfSupplier(){
        System.out.println("???????????????????????? ???????????????"+new Date());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        // ??????
        supplierScheduleMapper.resetArrRate();

        // ?????????????????????PO????????????????????????
        List<OrderOtdReport> orderOtdReports = this.getPoInfoFromOracle("");
        if(orderOtdReports.size()>0) {
            // ????????????????????????????????????
            String supCode = orderOtdReports.get(0).getSupCode();
            int poNum = 0;
            int rightPoNum = 0;
            for(OrderOtdReport item : orderOtdReports) {
                String itemSupCode = item.getSupCode();
                String supComfirmDate = item.getSupConfirmDate();
                String poCode = item.getPoCodeLn().split(" ")[0];
                String poLn = item.getPoCodeLn().split(" ")[1];
                Integer tcNum = Integer.valueOf(item.getTcNum());
                Integer rcvNum = supplierScheduleMapper.getSnNumByPoCodeLnAndTime(poCode,poLn,supComfirmDate);

                if(supCode.equals(itemSupCode)) { // ?????????????????????????????????????????????????????????????????????????????????
                    poNum++;
                    if(rcvNum >= tcNum) {
                        rightPoNum++;
                    }
                } else {
                    BigDecimal arvRateOfLastSupCode = new BigDecimal(rightPoNum).divide(new BigDecimal(poNum),2,RoundingMode.UP);
                    supplierScheduleMapper.updateArrRate(supCode,arvRateOfLastSupCode);
                    // ??????????????????????????????
                    supCode = itemSupCode;
                    poNum = 1;
                    rightPoNum = 0;

                    if(rcvNum >= tcNum) {
                        rightPoNum++;
                    }
                }
            }
        }
        System.out.println("???????????????????????? ???????????????"+new Date());
    }


    @Override
    public void exportExcelOtd(String supCodes, HttpServletResponse response) throws RuntimeException {
        List<SupplierOtdExcel> suppliers = new ArrayList<>();

        List<OrderOtdReport> orderOtdReports = this.getPoInfoFromOracle(supCodes);

        if(orderOtdReports.size()>0) {
            for(OrderOtdReport item : orderOtdReports) {
                String itemSupCode = item.getSupCode();
                String supComfirmDate = item.getSupConfirmDate();
                String poCode = item.getPoCodeLn().split(" ")[0];
                String poLn = item.getPoCodeLn().split(" ")[1];
                Integer tcNum = Integer.valueOf(item.getTcNum());
                Integer rcvNum = supplierScheduleMapper.getSnNumByPoCodeLnAndTime(poCode,poLn,supComfirmDate);
                SupplierOtdExcel supplierOtdExcel = BeanUtil.copy(item,SupplierOtdExcel.class);
                supplierOtdExcel.setRcvNUm(rcvNum.toString());
                if(rcvNum >= tcNum) {
                    supplierOtdExcel.setIdOtd("??????");
                } else {
                    supplierOtdExcel.setIdOtd("?????????");
                }
                suppliers.add(supplierOtdExcel);
            }
        }

        ExcelUtils.defaultExport(suppliers, SupplierOtdExcel.class, "???????????????????????????" + DateUtil.formatDate(new Date()), response);
    }

    private List<OrderOtdReport> getPoInfoFromOracle(String supCode) {
        List<OrderOtdReport> orderOtdReports = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;

        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.??????????????????sql
            @Cleanup Statement stmt = conn.createStatement();

            StringBuilder sqlListBuilder = new StringBuilder();
            StringBuilder sqlCountBuilder = new StringBuilder();

            sqlListBuilder.append(" SELECT po.sup_code,po.sup_name,pr.item_code,pr.item_name,pr.tc_uom,po.tc_num,pr.pr_code||' '||pr.pr_ln pr_code_ln,po.po_code||' '||po.po_ln po_code_ln,sup_confirm_date,NVL( pr.req_date, po.req_date ) req_date,to_char(p.approvedon,'yyyy-mm-dd') pr_check_date,to_char(o.approvedon,'yyyy-mm-dd') po_check_date,item.purch_name,row_number() over(order by to_char(o.approvedon,'yyyy-mm-dd') desc) rowno ");
            sqlListBuilder.append(" FROM");
            sqlListBuilder.append(" atwsrm.atw_u9_pr pr");
            sqlListBuilder.append(" LEFT JOIN atwsrm.atw_po_item po ON pr.pr_code = po.pr_code and pr.pr_ln = po.pr_ln");
            sqlListBuilder.append(" LEFT JOIN atwsrm.atw_item item ON item.code = pr.item_code ");
            sqlListBuilder.append(" LEFT JOIN ATWERP.PM_PURCHASEORDER o ON po.po_code = o.docNO ");
            sqlListBuilder.append(" LEFT JOIN ATWERP.PR_PR P on pr.pr_code = p.docno ");
            sqlListBuilder.append(" WHERE pr.is_deleted = 0 AND po.is_deleted = 0 AND po.po_code IS NOT NULL ");
            //sqlListBuilder.append(" and to_char(po.approvedon,'yyyy-mm-dd') >= ( SELECT to_char ( trunc ( add_months ( last_day( sysdate ), - 1 ) + 1 ), 'yyyy-mm-dd' ) FROM DUAL ) ");
            //sqlListBuilder.append(" and to_char(po.approvedon,'yyyy-mm-dd') <= ( select to_char(last_day(sysdate), 'yyyy-mm-dd') from dual)");
            sqlListBuilder.append(" and TO_CHAR(po.sup_confirm_date/ ( 60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD') >= ( SELECT to_char ( trunc ( add_months ( last_day( sysdate ), - 1 ) + 1 ), 'yyyy-mm-dd' ) FROM DUAL )  ");
            sqlListBuilder.append(" and TO_CHAR(po.sup_confirm_date/ ( 60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD') <= ( SELECT to_char ( last_day( sysdate ), 'yyyy-mm-dd' ) FROM DUAL )  ");


            if(!supCode.isEmpty()) {
                sqlListBuilder.append(" and instr('").append(supCode).append("',sup_code) > 0");
            }
            sqlListBuilder.append(" order by sup_code");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //3.??????
            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {

                OrderOtdReport orderOtdReport = new OrderOtdReport();
                orderOtdReport.setSupCode(resultSetList.getString("sup_Code"));
                orderOtdReport.setSupName(resultSetList.getString("sup_Name"));
                orderOtdReport.setItemCode(resultSetList.getString("item_Code"));
                orderOtdReport.setItemName(resultSetList.getString("item_Name"));
                orderOtdReport.setTcUom(resultSetList.getString("tc_uom"));
                orderOtdReport.setPrCodeLn(resultSetList.getString("pr_code_ln"));
                orderOtdReport.setPoCodeLn(resultSetList.getString("po_code_ln"));
                orderOtdReport.setSupConfirmDate(resultSetList.getString("sup_confirm_date"));
                if(orderOtdReport.getSupConfirmDate()!=null) {
                    orderOtdReport.setSupConfirmDate(sdf.format(new Date(Integer.parseInt(orderOtdReport.getSupConfirmDate()) * 1000L)));
                }
                orderOtdReport.setReqDate(resultSetList.getString("req_date"));
                if(orderOtdReport.getReqDate()!=null) {
                    orderOtdReport.setReqDate(sdf.format(new Date(Integer.parseInt(orderOtdReport.getReqDate()) * 1000L)));
                }
                orderOtdReport.setPrCheckDate(resultSetList.getString("pr_check_date"));
                orderOtdReport.setTcNum(resultSetList.getString("tc_num"));
                orderOtdReport.setPoCheckDate(resultSetList.getString("po_check_date"));
                orderOtdReport.setPurchName(resultSetList.getString("purch_name"));
                orderOtdReports.add(orderOtdReport);
            }
        }catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
        } finally {
            return orderOtdReports;
        }
    }



    /**
     * ??????Echarts??????(?????????)
     * @param supplierScheduleReq
     * @return
     */
    @Override
    public List<OmsEchrtsOfSupplierVO> getBarEchartsNum(SupplierScheduleReq supplierScheduleReq) {
        List<OmsEchrtsOfSupplierVO> omsEchrtsOfSupplierVOS = new LinkedList<>();
        omsEchrtsOfSupplierVOS.add(this.getItemECharts(supplierScheduleReq)); // ????????????
        omsEchrtsOfSupplierVOS.add(this.getPriceECharts(supplierScheduleReq)); // ????????????
        omsEchrtsOfSupplierVOS.add(this.getNumECharts(supplierScheduleReq)); // ????????????
        omsEchrtsOfSupplierVOS.add(this.getZJECharts(supplierScheduleReq)); // ????????????
        omsEchrtsOfSupplierVOS.add(this.getThreeMonthData(supplierScheduleReq)); // ??????????????????????????????
        return omsEchrtsOfSupplierVOS;
    }

    /**
     * ????????????
     * @param supplierScheduleReq
     * @return
     */
    private OmsEchrtsOfSupplierVO getItemECharts(SupplierScheduleReq supplierScheduleReq) {

        String supCode = supplierScheduleReq.getSupCode();
        String year = supplierScheduleReq.getYear();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat formatWeb = new SimpleDateFormat("yyyy-MM");
        String startMonth = "";
        String endMonth = "";
        Calendar calendar = Calendar.getInstance();

        if (year==null || year.isEmpty()) {
            Date now = new Date();
            Date startDate = stepMonth(now, -6);
            Date endDate = stepMonth(now, 5);
            startMonth = format.format(startDate);
            endMonth = format.format(endDate);
        } else {
            startMonth = year + "01";
            endMonth = year + "12";
        }

        OmsEchrtsOfSupplierVO omsEchrtsOfSupplierVO = new OmsEchrtsOfSupplierVO();
        List<String> monthList = new LinkedList<>();
        List<String> needNum = new LinkedList<>(); // ??????
        List<String> orderNum = new LinkedList<>(); // ??????
        List<String> deliveredNum = new LinkedList<>(); // ??????
        List<String> preNum = new LinkedList<>(); // ??????
        List<String> undeliveredNum = new LinkedList<>(); // ?????????

        BigDecimal totalUndeliveredNum = new BigDecimal("0");

        List<EchartVo> needData = getItemTypeData(supCode,startMonth, endMonth,"need");
        List<EchartVo> orderData = supplierScheduleMapper.selectOrderNumOfItem(supCode, year, startMonth, endMonth);
        List<EchartVo> deliveredData = supplierScheduleMapper.selectDeliveredNumOfItem(supCode, year, startMonth, endMonth);
        List<EchartVo> preData = getItemTypeData(supCode,startMonth, endMonth,"pre");

        Map<String, String> needMap = needData.stream().collect(Collectors.toMap(EchartVo::getTime, EchartVo::getNumber));
        Map<String, String> orderMap = orderData.stream().collect(Collectors.toMap(EchartVo::getTime, EchartVo::getNumber));
        Map<String, String> deliveredMap = deliveredData.stream().collect(Collectors.toMap(EchartVo::getTime, EchartVo::getNumber));
        Map<String, String> preMap = preData.stream().collect(Collectors.toMap(EchartVo::getTime, EchartVo::getNumber));

        for (int i = -6; i <= 5; i++) {
            String month = "";
            if (year == null || year.isEmpty()) {
                Date now = new Date();
                Date newDate = stepMonth(now, i);
                month = formatWeb.format(newDate);
            } else {
                Integer mon = 7 + i;
                month = year + '-' + String.format("%02d",mon);
            }
            monthList.add(month);

            if (!needMap.containsKey(month)) {
                needNum.add("0");
            } else {
                needNum.add(new BigDecimal(needMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
            }

            if (!orderMap.containsKey(month)) {
                orderNum.add("0");
            } else {
                orderNum.add(new BigDecimal(orderMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
            }

            if (!deliveredMap.containsKey(month)) {
                deliveredNum.add("0");
            } else {
                deliveredNum.add(new BigDecimal(deliveredMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
            }

            if (!preMap.containsKey(month)) {
                preNum.add("0");
            } else {
                preNum.add(new BigDecimal(preMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
            }
        }

        for (int j = 0; j < 12; j++) {
            undeliveredNum.add(new BigDecimal(needNum.get(j)).add(new BigDecimal(preNum.get(j))).subtract(new BigDecimal(deliveredNum.get(j))).toString());
            totalUndeliveredNum = totalUndeliveredNum.add(new BigDecimal(needNum.get(j)).add(new BigDecimal(preNum.get(j))).subtract(new BigDecimal(deliveredNum.get(j)))).setScale(0,RoundingMode.HALF_UP);
        }

        omsEchrtsOfSupplierVO.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        omsEchrtsOfSupplierVO.setNeedNum(needNum.toArray(new String[needNum.size()]));// ????????????
        omsEchrtsOfSupplierVO.setOrderNum(orderNum.toArray(new String[orderNum.size()])); // ?????????
        omsEchrtsOfSupplierVO.setDeliveryNum(deliveredNum.toArray(new String[deliveredNum.size()]));//?????????
        omsEchrtsOfSupplierVO.setPreNum(preNum.toArray(new String[preNum.size()])); // ?????????
        omsEchrtsOfSupplierVO.setUndeliveredNum(undeliveredNum.toArray(new String[deliveredNum.size()]));//????????????
        omsEchrtsOfSupplierVO.setTotalUndeliveredNum(totalUndeliveredNum.toString());

        return omsEchrtsOfSupplierVO;
    }

    private List<EchartVo> getItemTypeData(String supCode, String startMonth, String endMonth,String type) {
        List<EchartVo> echartVoList = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;
        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            @Cleanup Statement stmt = conn.createStatement();
            //2.??????????????????sql
            String sql = "";
            if (type.equals("need")) {
                sql = getNeedItemSql(supCode, startMonth, endMonth);
            } else {
                sql = getPreItemSql(supCode, startMonth, endMonth);
            }
            //3.??????
            @Cleanup ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {
                EchartVo echartVo = new EchartVo();
                echartVo.setTime(resultSet.getString("need_time"));
                echartVo.setNumber(resultSet.getString("type_number"));
                echartVoList.add(echartVo);
            }
        }catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
        } finally {
            return echartVoList;
        }
    }

    /**
     * ????????????
     * @param supplierScheduleReq
     * @return
     */
    private OmsEchrtsOfSupplierVO getPriceECharts(SupplierScheduleReq supplierScheduleReq) {
        String supCode = supplierScheduleReq.getSupCode();
        String year = supplierScheduleReq.getYear();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat formatWeb = new SimpleDateFormat("yyyy-MM");
        BigDecimal totalUndeliveredNum = new BigDecimal("0");

        String startMonth = "";
        String endMonth = "";
        String startMonth2 = "";
        String endMonth2 = "";
        Calendar calendar = Calendar.getInstance();

        if (year==null || year.isEmpty()) {
            Date now = new Date();
            Date startDate = stepMonth(now, -6);
            Date endDate = stepMonth(now, 5);
            startMonth = formatWeb.format(startDate);
            endMonth = formatWeb.format(endDate);
            startMonth2 = format.format(startDate);
            endMonth2 = format.format(endDate);
        } else {
            startMonth = year + "-01";
            endMonth = year + "-12";
        }

        OmsEchrtsOfSupplierVO omsEchrtsOfSupplierVO = new OmsEchrtsOfSupplierVO();
        List<String> monthList = new LinkedList<>();
        List<String> needNum = new LinkedList<>(); // ??????
        List<String> orderNum = new LinkedList<>(); // ??????
        List<String> deliveredNum = new LinkedList<>(); // ??????
        List<String> preNum = new LinkedList<>(); // ??????
        List<String> undeliveredNum = new LinkedList<>(); // ?????????

        List<EchartVo> needData = getPriceData(supCode, startMonth, endMonth,"need");
        List<EchartVo> orderData = supplierScheduleMapper.selectOrderPriceNeed(supCode, year, startMonth2, endMonth2);
        List<EchartVo> deliveredDate = supplierScheduleMapper.selectDeliveredPriceNeed(supCode, year, startMonth2, endMonth2);
        List<EchartVo> preData = getPriceData(supCode, startMonth, endMonth,"pre");

        Map<String, String> needMap = needData.stream().collect(Collectors.toMap(EchartVo::getTime, EchartVo::getNumber));
        Map<String, String> orderMap = orderData.stream().collect(Collectors.toMap(EchartVo::getTime, EchartVo::getNumber));
        Map<String, String> deliveredMap = deliveredDate.stream().collect(Collectors.toMap(EchartVo::getTime, EchartVo::getNumber));
        Map<String, String> preMap = preData.stream().collect(Collectors.toMap(EchartVo::getTime, EchartVo::getNumber));


        for (int i = -6; i <= 5; i++) {
            String month = "";
            if (year == null || year.isEmpty()) {
                Date now = new Date();
                Date newDate = stepMonth(now, i);
                month = formatWeb.format(newDate);
            } else {
                Integer mon = 7 + i;
                month = year + '-' + String.format("%02d",mon);
            }
            monthList.add(month);

            if (!needMap.containsKey(month)) {
                needNum.add("0");
            } else {
                needNum.add(new BigDecimal(needMap.get(month).toString()).divide(new BigDecimal("10000")).setScale(2, RoundingMode.HALF_UP).toString());
            }

            if (!orderMap.containsKey(month)) {
                orderNum.add("0");
            } else {
                orderNum.add(new BigDecimal(orderMap.get(month).toString()).divide(new BigDecimal("10000")).setScale(2, RoundingMode.HALF_UP).toString());
            }

            if (!deliveredMap.containsKey(month)) {
                deliveredNum.add("0");
            } else {
                deliveredNum.add(new BigDecimal(deliveredMap.get(month).toString()).divide(new BigDecimal("10000")).setScale(2, RoundingMode.HALF_UP).toString());
            }

            if (!preMap.containsKey(month)) {
                preNum.add("0");
            } else {
                preNum.add(new BigDecimal(preMap.get(month).toString()).divide(new BigDecimal("10000")).setScale(0, RoundingMode.HALF_UP).toString());
            }
        }

        for (int j = 0; j < 12; j++) {
            undeliveredNum.add(new BigDecimal(needNum.get(j)).add(new BigDecimal(preNum.get(j))).subtract(new BigDecimal(deliveredNum.get(j))).toString());
            totalUndeliveredNum = totalUndeliveredNum.add(new BigDecimal(needNum.get(j)).add(new BigDecimal(preNum.get(j))).subtract(new BigDecimal(deliveredNum.get(j)))).setScale(0,RoundingMode.HALF_UP);
        }

        omsEchrtsOfSupplierVO.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        omsEchrtsOfSupplierVO.setNeedNum(needNum.toArray(new String[needNum.size()]));// ????????????
        omsEchrtsOfSupplierVO.setOrderNum(orderNum.toArray(new String[orderNum.size()])); // ?????????
        omsEchrtsOfSupplierVO.setDeliveryNum(deliveredNum.toArray(new String[deliveredNum.size()]));//?????????
        omsEchrtsOfSupplierVO.setPreNum(preNum.toArray(new String[preNum.size()])); // ?????????
        omsEchrtsOfSupplierVO.setUndeliveredNum(undeliveredNum.toArray(new String[deliveredNum.size()]));//????????????
        omsEchrtsOfSupplierVO.setTotalUndeliveredNum(totalUndeliveredNum.toString());

        return omsEchrtsOfSupplierVO;
    }

    private List<EchartVo> getPriceData(String supCode, String startMonth, String endMonth,String type) {
        List<EchartVo> echartVoList = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;
        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            @Cleanup Statement stmt = conn.createStatement();
            //2.??????????????????sql
            String sql = "";
            if (type.equals("need")) {
                sql = getNeedPriceSql(supCode, startMonth, endMonth);
            } else {
                sql = getPrePriceSql(supCode, startMonth, endMonth);
            }
            //3.??????
            @Cleanup ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {
                EchartVo echartVo = new EchartVo();
                echartVo.setTime(resultSet.getString("need_time"));
                echartVo.setNumber(resultSet.getString("amount"));
                echartVoList.add(echartVo);
            }
        }catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
        } finally {
            return echartVoList;
        }
    }


    /**
     * ????????????
     * @param supplierScheduleReq
     * @return
     */
    private OmsEchrtsOfSupplierVO getNumECharts(SupplierScheduleReq supplierScheduleReq) {
        String supCode = supplierScheduleReq.getSupCode();
        String year = supplierScheduleReq.getYear();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat formatWeb = new SimpleDateFormat("yyyy-MM");
        BigDecimal totalUndeliveredNum = new BigDecimal("0");

        String startMonth = "";
        String endMonth = "";
        Calendar calendar = Calendar.getInstance();

        if (year==null || year.isEmpty()) {
            Date now = new Date();
            Date startDate = stepMonth(now, -6);
            Date endDate = stepMonth(now, 5);
            startMonth = format.format(startDate);
            endMonth = format.format(endDate);
        } else {
            startMonth = year + "01";
            endMonth = year + "12";
        }

        OmsEchrtsOfSupplierVO omsEchrtsOfSupplierVO = new OmsEchrtsOfSupplierVO();
        List<String> monthList = new LinkedList<>();
        List<String> needNum = new LinkedList<>(); // ??????
        List<String> orderNum = new LinkedList<>(); // ??????
        List<String> deliveredNum = new LinkedList<>(); // ??????
        List<String> preNum = new LinkedList<>(); // ??????
        List<String> undeliveredNum = new LinkedList<>(); // ?????????

        List<EchartVo> needData = getNumData(supCode, startMonth, endMonth,"need");
        List<EchartVo> orderData = supplierScheduleMapper.selectOrderNumNeed(supCode, year, startMonth, endMonth);
        List<EchartVo> deliveredData = supplierScheduleMapper.selectDeliveredNumNeed(supCode, year, startMonth, endMonth);
        List<EchartVo> preData = getNumData(supCode, startMonth, endMonth,"pre");

        Map<String, String> needMap = needData.stream().collect(Collectors.toMap(EchartVo::getTime, EchartVo::getNumber));
        Map<String, String> orderMap = orderData.stream().collect(Collectors.toMap(EchartVo::getTime, EchartVo::getNumber));
        Map<String, String> deliveredMap = deliveredData.stream().collect(Collectors.toMap(EchartVo::getTime, EchartVo::getNumber));
        Map<String, String> preMap = preData.stream().collect(Collectors.toMap(EchartVo::getTime, EchartVo::getNumber));

        for (int i = -6; i <= 5; i++) {
            String month = "";
            if (year == null || year.isEmpty()) {
                Date now = new Date();
                Date newDate = stepMonth(now, i);
                month = formatWeb.format(newDate);
            } else {
                Integer mon = 7 + i;
                month = year + '-' + String.format("%02d",mon);
            }
            monthList.add(month);

            if (!needMap.containsKey(month)) {
                needNum.add("0");
            } else {
                needNum.add(new BigDecimal(needMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
            }

            if (!orderMap.containsKey(month)) {
                orderNum.add("0");
            } else {
                orderNum.add(new BigDecimal(orderMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
            }


            if (!deliveredMap.containsKey(month)) {
                deliveredNum.add("0");
            } else {
                deliveredNum.add(new BigDecimal(deliveredMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
            }

            if (!preMap.containsKey(month)) {
                preNum.add("0");
            } else {
                preNum.add(new BigDecimal(preMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
            }
        }

        for (int j = 0; j < 12; j++) {
            undeliveredNum.add(new BigDecimal(needNum.get(j)).add(new BigDecimal(preNum.get(j))).subtract(new BigDecimal(deliveredNum.get(j))).toString());
            totalUndeliveredNum = totalUndeliveredNum.add(new BigDecimal(needNum.get(j)).add(new BigDecimal(preNum.get(j))).subtract(new BigDecimal(deliveredNum.get(j)))).setScale(0,RoundingMode.HALF_UP);
        }

        omsEchrtsOfSupplierVO.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        omsEchrtsOfSupplierVO.setNeedNum(needNum.toArray(new String[needNum.size()]));// ????????????
        omsEchrtsOfSupplierVO.setOrderNum(orderNum.toArray(new String[orderNum.size()])); // ?????????
        omsEchrtsOfSupplierVO.setDeliveryNum(deliveredNum.toArray(new String[deliveredNum.size()]));//?????????
        omsEchrtsOfSupplierVO.setPreNum(preNum.toArray(new String[preNum.size()])); // ?????????
        omsEchrtsOfSupplierVO.setUndeliveredNum(undeliveredNum.toArray(new String[deliveredNum.size()]));//????????????
        omsEchrtsOfSupplierVO.setTotalUndeliveredNum(totalUndeliveredNum.toString());

        return omsEchrtsOfSupplierVO;
    }

    private List<EchartVo> getNumData(String supCode, String startMonth, String endMonth,String type) {
        List<EchartVo> echartVoList = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;
        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            @Cleanup Statement stmt = conn.createStatement();
            //2.??????????????????sql
            String sql = "";
            if (type.equals("need")) {
                sql = getNeedNumSql(supCode, startMonth, endMonth);
            } else {
                sql = getPreNumSql(supCode, startMonth, endMonth);
            }
            //3.??????
            @Cleanup ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {
                EchartVo echartVo = new EchartVo();
                echartVo.setTime(resultSet.getString("need_time"));
                echartVo.setNumber(resultSet.getString("amount"));
                echartVoList.add(echartVo);
            }
        }catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
        } finally {
            return echartVoList;
        }
    }

    /**
     * ????????????
     * @param supplierScheduleReq
     * @return
     */
    private OmsEchrtsOfSupplierVO getZJECharts(SupplierScheduleReq supplierScheduleReq) {
        String supCode = supplierScheduleReq.getSupCode();
        String year = supplierScheduleReq.getYear();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat formatWeb = new SimpleDateFormat("yyyy-MM");
        BigDecimal totalUndeliveredNum = new BigDecimal("0");

        String startMonth = "";
        String endMonth = "";
        Calendar calendar = Calendar.getInstance();

        if (year==null || year.isEmpty()) {
            Date now = new Date();
            Date startDate = stepMonth(now, -6);
            Date endDate = stepMonth(now, 5);
            startMonth = format.format(startDate);
            endMonth = format.format(endDate);
        } else {
            startMonth = year + "01";
            endMonth = year + "12";
        }

        OmsEchrtsOfSupplierVO omsEchrtsOfSupplierVO = new OmsEchrtsOfSupplierVO();
        List<String> monthList = new LinkedList<>();
        List<String> needNum = new LinkedList<>(); // ??????
        List<String> orderNum = new LinkedList<>(); // ??????
        List<String> deliveredNum = new LinkedList<>(); // ??????
        List<String> preNum = new LinkedList<>(); // ??????
        List<String> undeliveredNum = new LinkedList<>(); // ?????????
        Map<String,String> itemWeight = new HashMap<>(); // ??????MAP

        // ???????????????????????????????????????[??????????????????]
        List<String> itemNameList = supplierScheduleMapper.selectAllTypeItemName(supCode, year, startMonth, endMonth);
        itemNameList.stream().forEach(itemName ->{
            // ??????
            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(itemName);
            itemInfoEntity.setSupCode(supCode);
            // ???????????????
            if (itemInfoEntity.getSeries() != null) {
                // ??????
                try {
                    List<ItemInfoEntityOfZDJ> itemInfoEntityOfZDJ = supplierScheduleMapper.selectZJWeightOfSup(itemInfoEntity);
                    if (itemInfoEntityOfZDJ.size()>0 && itemInfoEntityOfZDJ.get(0).getWeight() != null && !itemInfoEntityOfZDJ.get(0).getWeight().isEmpty()) {
                        // ?????????MAP??????????????????
                        itemWeight.put(itemName,itemInfoEntityOfZDJ.get(0).getWeight());
                    }
                } catch (Exception e){
                    throw new RuntimeException("????????????????????????????????????????????????????????????????????????????????????????????????" + itemName);
                }
            }
        });

        // ????????????????????????????????????????????????????????????
        List<EchartVo> needData = getZJData(supCode, startMonth, endMonth,"need");
        List<EchartVo> preData = getZJData(supCode, startMonth, endMonth,"pre");
        List<EchartVo> orderData = supplierScheduleMapper.selectOrderZJNeed(supCode, year, startMonth, endMonth);
        List<EchartVo> deliveredData = supplierScheduleMapper.selectDeliveredZJNeed(supCode, year, startMonth, endMonth);

        Map<String, String> needMap = handleZJData(itemWeight,needData); //?????????
        Map<String, String> orderMap = handleZJData(itemWeight,orderData); // ??????
        Map<String, String> deliveredMap = handleZJData(itemWeight,deliveredData); // ??????
        Map<String, String> preMap = handleZJData(itemWeight,preData);// ??????

        for (int i = -6; i <= 5; i++) {
            String month = "";
            if (year == null || year.isEmpty()) {
                Date now = new Date();
                Date newDate = stepMonth(now, i);
                month = formatWeb.format(newDate);
            } else {
                Integer mon = 7 + i;
                month = year + '-' + String.format("%02d",mon);
            }
            monthList.add(month);

            if (!needMap.containsKey(month)) {
                needNum.add("0");
            } else {
                needNum.add(new BigDecimal(needMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
            }

            if (!orderMap.containsKey(month)) {
                orderNum.add("0");
            } else {
                orderNum.add(new BigDecimal(orderMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
            }

            if (!deliveredMap.containsKey(month)) {
                deliveredNum.add("0");
            } else {
                deliveredNum.add(new BigDecimal(deliveredMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
            }

            if (!preMap.containsKey(month)) {
                preNum.add("0");
            } else {
                preNum.add(new BigDecimal(preMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
            }
        }

        for (int j = 0; j < 12; j++) {
            undeliveredNum.add(new BigDecimal(needNum.get(j)).add(new BigDecimal(preNum.get(j))).subtract(new BigDecimal(deliveredNum.get(j))).toString());
            totalUndeliveredNum = totalUndeliveredNum.add(new BigDecimal(needNum.get(j)).add(new BigDecimal(preNum.get(j))).subtract(new BigDecimal(deliveredNum.get(j)))).setScale(0,RoundingMode.HALF_UP);
        }

        omsEchrtsOfSupplierVO.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        omsEchrtsOfSupplierVO.setNeedNum(needNum.toArray(new String[needNum.size()]));// ????????????
        omsEchrtsOfSupplierVO.setOrderNum(orderNum.toArray(new String[orderNum.size()])); // ?????????
        omsEchrtsOfSupplierVO.setDeliveryNum(deliveredNum.toArray(new String[deliveredNum.size()]));//?????????
        omsEchrtsOfSupplierVO.setPreNum(preNum.toArray(new String[preNum.size()])); // ?????????
        omsEchrtsOfSupplierVO.setUndeliveredNum(undeliveredNum.toArray(new String[deliveredNum.size()]));//????????????
        omsEchrtsOfSupplierVO.setTotalUndeliveredNum(totalUndeliveredNum.toString());

        return omsEchrtsOfSupplierVO;
    }

    private List<EchartVo> getZJData(String supCode, String startMonth, String endMonth,String type) {
        List<EchartVo> echartVoList = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;
        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            @Cleanup Statement stmt = conn.createStatement();
            //2.??????????????????sql
            String sql = "";
            if (type.equals("need")) {
                sql = getNeedZJSql(supCode, startMonth, endMonth);
            } else {
                sql = getPreZJSql(supCode, startMonth, endMonth);
            }
            //3.??????
            @Cleanup ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {
                EchartVo echartVo = new EchartVo();
                echartVo.setTime(resultSet.getString("need_time"));
                echartVo.setNumber(resultSet.getString("amount"));
                echartVo.setItemName(resultSet.getString("item_name"));
                echartVoList.add(echartVo);
            }
        }catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
        } finally {
            return echartVoList;
        }
    }

    private Map<String, String> handleZJData(Map<String,String> itemWeight,List<EchartVo> echartVoList){
        Map<String, String> echartsMap = new HashMap<>();
        // ??????
        echartVoList.stream().forEach(itemInfo ->{
            if(itemWeight.containsKey(itemInfo.getItemName())){ //?????????????????????????????????????????????
                // ??????
                String singleWeight = itemWeight.get(itemInfo.getItemName());
                // ??????
                String weight = new BigDecimal(singleWeight).multiply(new BigDecimal(itemInfo.getNumber())).setScale(2,RoundingMode.HALF_UP).toString();
                // ???????????????
                if(echartsMap.containsKey(itemInfo.getTime())){ // ??????????????????????????????????????????
                    String existedWeight = echartsMap.get(itemInfo.getTime());
                    String nowWeight = new BigDecimal(existedWeight).add(new BigDecimal(weight)).setScale(2,RoundingMode.HALF_UP).toString();
                    echartsMap.put(itemInfo.getTime(),nowWeight);
                } else { //???????????????????????????
                    echartsMap.put(itemInfo.getTime(),weight);
                }
            }
        });
        return echartsMap;
    }


    private OmsEchrtsOfSupplierVO getThreeMonthData(SupplierScheduleReq supplierScheduleReq) {
        OmsEchrtsOfSupplierVO omsEchrtsOfSupplierVO = new OmsEchrtsOfSupplierVO();
        List<String> threeMonthOtd = new ArrayList<>();
        SimpleDateFormat formatWeb = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat formatWebEnd = new SimpleDateFormat("yyyy-MM-dd");
        BigDecimal totalUndeliveredNum = new BigDecimal("0");
        String startMonth = "";
        String endMonth = "";
        String endDay = "";
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        Date startDate = stepMonth(now, -2);
        Date endDate = stepMonth(now, 0);
        startMonth = formatWeb.format(startDate);
        endDay = formatWebEnd.format(endDate);
        endMonth = formatWeb.format(endDate);
        String supCode = supplierScheduleReq.getSupCode();

        // ???????????????
        List<OtdReport> otdReportsOfOk = supplierScheduleMapper.getOkThreeMotnthOtdForSup(startMonth,endDay,supCode);
        // ???????????????
        List<OtdReport> otdReportsOfTotal = supplierScheduleMapper.getTotalThreeMotnthOtdForSup(startMonth,endDay,supCode);

        Map<String, String> okMap = otdReportsOfOk.stream().collect(Collectors.toMap(OtdReport::getReqDate, OtdReport::getTypeNumber));

        otdReportsOfTotal.stream().forEach(otdReport -> {
            String key = otdReport.getReqDate();
            BigDecimal totalNum  = new BigDecimal(otdReport.getTypeNumber());
            BigDecimal okNum = new BigDecimal("0");
            if(okMap.get(key)!=null){
                okNum = new BigDecimal(okMap.get(key));
            }
            String otd = okNum.multiply(new BigDecimal("100")).divide(totalNum,1,RoundingMode.HALF_UP).toString();
            otd = key + " : " + otd + "%";
            threeMonthOtd.add(otd);
        });


        if(threeMonthOtd.size()==2) {
            threeMonthOtd.add("????????????????????????????????????");
        }

        omsEchrtsOfSupplierVO.setThreeMonthOtd(threeMonthOtd);
        return  omsEchrtsOfSupplierVO;
    }

    /**
     * ?????????????????????(???)
     * @param supplierScheduleReq
     * @return
     */
    @Override
    public List<OmsEchrtsOfSupplierVO> getMainData(SupplierScheduleReq supplierScheduleReq) {
        List<OmsEchrtsOfSupplierVO> omsEchrtsOfSupplierVOS = new LinkedList<>();
        SimpleDateFormat formatWeb = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat formatWebEnd = new SimpleDateFormat("yyyy-MM-dd");
        BigDecimal totalUndeliveredNum = new BigDecimal("0");
        String startMonth = "";
        String endMonth = "";
        String endDay = "";
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        Date startDate = stepMonth(now, -3);
        Date endDate = stepMonth(now, 0);
        startMonth = formatWeb.format(startDate);
        endDay = formatWebEnd.format(endDate); // ??????
        endDay = getLastDayOfMonth(Integer.parseInt(endDay.split("-")[0]),Integer.parseInt(endDay.split("-")[1])); // ???????????????????????????
        endMonth = formatWeb.format(endDate);
        List<String> monthList = new LinkedList<>();
        for (int i = -3; i <= 0; i++) {
            String month = "";
            Date newDate = stepMonth(now, i);
            month = formatWeb.format(newDate);
            monthList.add(month);
        }

        // ??????
        OmsEchrtsOfSupplierVO ZJData = new OmsEchrtsOfSupplierVO();
        ZJData.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        //Map<String,String> ZJOTD = getOTDData(startMonth,endDay,"ZJ");
        Map<String,String> ZJOTD = getOTDDataByItemCode(startMonth,endDay,"ZJ");
        List<String> otdZJList = new ArrayList<>();
        List<String> otdSevenZJList = new ArrayList<>();
        monthList.stream().forEach(month->{
                if(ZJOTD.containsKey(month)){
                    otdZJList.add(ZJOTD.get(month).split("~")[0]);
                    otdSevenZJList.add(ZJOTD.get(month).split("~")[1]);
                } else {
                    otdZJList.add("0");
                    otdSevenZJList.add("0");
                }
        });
        ZJData.setOtd(otdZJList.toArray(new String[otdZJList.size()]));
        ZJData.setOtdSeven(otdSevenZJList.toArray(new String[otdSevenZJList.size()]));
        omsEchrtsOfSupplierVOS.add(ZJData);

        // ??????
        OmsEchrtsOfSupplierVO DJData = new OmsEchrtsOfSupplierVO();
        DJData.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        // Map<String,String> DJOTD = getOTDData(startMonth,endDay,"DJ");
        Map<String,String> DJOTD = getOTDDataByItemCode(startMonth,endDay,"DJ");
        List<String> otdDJList = new ArrayList<>();
        List<String> otdSevenDJList = new ArrayList<>();
        monthList.stream().forEach(month->{
            if(DJOTD.containsKey(month)){
                otdDJList.add(DJOTD.get(month).split("~")[0]);
                otdSevenDJList.add(DJOTD.get(month).split("~")[1]);
            } else {
                otdDJList.add("0");
                otdSevenDJList.add("0");
            }
        });
        DJData.setOtd(otdDJList.toArray(new String[otdDJList.size()]));
        DJData.setOtdSeven(otdSevenDJList.toArray(new String[otdSevenDJList.size()]));
        omsEchrtsOfSupplierVOS.add(DJData);

        // ??????
        OmsEchrtsOfSupplierVO QZData = new OmsEchrtsOfSupplierVO();
        QZData.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        // Map<String,String> QZOTD = getOTDData(startMonth,endDay,"QZ");
        Map<String,String> QZOTD = getOTDDataByItemCode(startMonth,endDay,"QZ");
        List<String> otdQZList = new ArrayList<>();
        List<String> otdSevenQZList = new ArrayList<>();
        monthList.stream().forEach(month->{
            if(QZOTD.containsKey(month)){
                otdQZList.add(QZOTD.get(month).split("~")[0]);
                otdSevenQZList.add(QZOTD.get(month).split("~")[1]);
            } else {
                otdQZList.add("0");
                otdSevenQZList.add("0");
            }
        });
        QZData.setOtd(otdQZList.toArray(new String[otdQZList.size()]));
        QZData.setOtdSeven(otdSevenQZList.toArray(new String[otdSevenQZList.size()]));
        omsEchrtsOfSupplierVOS.add(QZData);

        // ??????
        OmsEchrtsOfSupplierVO FXData = new OmsEchrtsOfSupplierVO();
        FXData.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        Map<String,String> FXOTD = getOTDDataByItemCode(startMonth,endDay,"FX");
        List<String> otdFXList = new ArrayList<>();
        List<String> otdSevenFXList = new ArrayList<>();
        monthList.stream().forEach(month->{
            if(FXOTD.containsKey(month)){
                otdFXList.add(FXOTD.get(month).split("~")[0]);
                otdSevenFXList.add(FXOTD.get(month).split("~")[1]);
            } else {
                otdFXList.add("0");
                otdSevenFXList.add("0");
            }
        });
        FXData.setOtd(otdFXList.toArray(new String[otdFXList.size()]));
        FXData.setOtdSeven(otdSevenFXList.toArray(new String[otdSevenFXList.size()]));
        omsEchrtsOfSupplierVOS.add(FXData);

        // ?????????
        OmsEchrtsOfSupplierVO WGFData = new OmsEchrtsOfSupplierVO();
        WGFData.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        // Map<String,String> WGFOTD = getOTDData(startMonth,endDay,"WGF");
        Map<String,String> WGFOTD = getOTDDataByItemCode(startMonth,endDay,"WGF");
        List<String> otdWGFList = new ArrayList<>();
        List<String> otdSevenWGFList = new ArrayList<>();
        monthList.stream().forEach(month->{
            if(WGFOTD.containsKey(month)){
                otdWGFList.add(WGFOTD.get(month).split("~")[0]);
                otdSevenWGFList.add(WGFOTD.get(month).split("~")[1]);
            } else {
                otdWGFList.add("0");
                otdSevenWGFList.add("0");
            }
        });
        WGFData.setOtd(otdWGFList.toArray(new String[otdWGFList.size()]));
        WGFData.setOtdSeven(otdSevenWGFList.toArray(new String[otdSevenWGFList.size()]));
        omsEchrtsOfSupplierVOS.add(WGFData);

        // ?????????
        OmsEchrtsOfSupplierVO QKJData = new OmsEchrtsOfSupplierVO();
        QKJData.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        Map<String,String> QKJOTD = getOTDDataByItemCode(startMonth,endDay,"QKJ");
        List<String> otdQKJList = new ArrayList<>();
        List<String> otdSevenQKJList = new ArrayList<>();
        monthList.stream().forEach(month->{
            if(QKJOTD.containsKey(month)){
                otdQKJList.add(QKJOTD.get(month).split("~")[0]);
                otdSevenQKJList.add(QKJOTD.get(month).split("~")[1]);
            } else {
                otdQKJList.add("0");
                otdSevenQKJList.add("0");
            }
        });
        QKJData.setOtd(otdQKJList.toArray(new String[otdQKJList.size()]));
        QKJData.setOtdSeven(otdSevenQKJList.toArray(new String[otdSevenQKJList.size()]));
        omsEchrtsOfSupplierVOS.add(QKJData);

        // ??????
        OmsEchrtsOfSupplierVO QGData = new OmsEchrtsOfSupplierVO();
        QGData.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        Map<String,String> QGOTD = getOTDDataByItemCode(startMonth,endDay,"QG");
        List<String> otdQGList = new ArrayList<>();
        List<String> otdSevenQGList = new ArrayList<>();
        monthList.stream().forEach(month->{
            if(QGOTD.containsKey(month)){
                otdQGList.add(QGOTD.get(month).split("~")[0]);
                otdSevenQGList.add(QGOTD.get(month).split("~")[1]);
            } else {
                otdQGList.add("0");
                otdSevenQGList.add("0");
            }
        });
        QGData.setOtd(otdQGList.toArray(new String[otdQGList.size()]));
        QGData.setOtdSeven(otdSevenQGList.toArray(new String[otdSevenQGList.size()]));
        omsEchrtsOfSupplierVOS.add(QGData);

        // ??????
        OmsEchrtsOfSupplierVO WWData = new OmsEchrtsOfSupplierVO();
        WWData.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        Map<String,String> WWOTD = getOTDDataByItemCode(startMonth,endDay,"WW");
        List<String> otdWWList = new ArrayList<>();
        List<String> otdSevenWWList = new ArrayList<>();
        monthList.stream().forEach(month->{
            if(WWOTD.containsKey(month)){
                otdWWList.add(WWOTD.get(month).split("~")[0]);
                otdSevenWWList.add(WWOTD.get(month).split("~")[1]);
            } else {
                otdWWList.add("0");
                otdSevenWWList.add("0");
            }
        });
        WWData.setOtd(otdWWList.toArray(new String[otdWWList.size()]));
        WWData.setOtdSeven(otdSevenWWList.toArray(new String[otdSevenWWList.size()]));
        omsEchrtsOfSupplierVOS.add(WWData);

        // ?????? ?????????????????????
        OmsEchrtsOfSupplierVO QTData = new OmsEchrtsOfSupplierVO();
        QTData.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        // Map<String,String> QTOTD = getOTDData(startMonth,endDay,"QT");
        Map<String,String> QTOTD = getOTDDataByItemCode(startMonth,endDay,"QT");
        List<String> otdQTList = new ArrayList<>();
        List<String> otdSevenQTList = new ArrayList<>();
        monthList.stream().forEach(month->{
            if(QTOTD.containsKey(month)){
                otdQTList.add(QTOTD.get(month).split("~")[0]);
                otdSevenQTList.add(QTOTD.get(month).split("~")[1]);
            } else {
                otdQTList.add("0");
                otdSevenQTList.add("0");
            }
        });
        QTData.setOtd(otdQTList.toArray(new String[otdQTList.size()]));
        QTData.setOtdSeven(otdSevenQTList.toArray(new String[otdSevenQTList.size()]));
        omsEchrtsOfSupplierVOS.add(QTData);

        // ????????????
        OmsEchrtsOfSupplierVO ALLData = new OmsEchrtsOfSupplierVO();
        ALLData.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
        Map<String,String> ALLOTD = getOTDDataByItemCode(startMonth,endDay,"ALL");
        List<String> otdALLList = new ArrayList<>();
        List<String> otdSevenALLlist = new ArrayList<>();
        monthList.stream().forEach(month->{
            if(ALLOTD.containsKey(month)){
                otdALLList.add(ALLOTD.get(month).split("~")[0]);
                otdSevenALLlist.add(ALLOTD.get(month).split("~")[1]);
            } else {
                otdALLList.add("0");
                otdSevenALLlist.add("0");
            }
        });
        ALLData.setOtd(otdALLList.toArray(new String[otdALLList.size()]));
        ALLData.setOtdSeven(otdSevenALLlist.toArray(new String[otdSevenALLlist.size()]));
        omsEchrtsOfSupplierVOS.add(ALLData);

        return omsEchrtsOfSupplierVOS;
    }


    /**
     * ?????????????????????(???)
     * @param supplierScheduleReq
     * @return
     */
    @Override
    public List<OmsEchrtsOfSupplierVO> getMainDataWeek(SupplierScheduleReq supplierScheduleReq) {
        List<OmsEchrtsOfSupplierVO> omsEchrtsOfSupplierVOS = new LinkedList<>();
        SimpleDateFormat formatWeb = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat formatWebEnd = new SimpleDateFormat("yyyy-MM-dd");
        BigDecimal totalUndeliveredNum = new BigDecimal("0");
        Calendar c = Calendar.getInstance();
        String endWeek = String.valueOf(c.get(Calendar.WEEK_OF_YEAR)-1);
        String startWeek = String.valueOf(c.get(Calendar.WEEK_OF_YEAR)-4);
        List<String> weekList = new LinkedList<>();
        weekList.add(String.valueOf(c.get(Calendar.YEAR)) + "-" +String.valueOf(c.get(Calendar.WEEK_OF_YEAR) - 3));
        weekList.add(String.valueOf(c.get(Calendar.YEAR)) + "-" +String.valueOf(c.get(Calendar.WEEK_OF_YEAR) - 2));
        weekList.add(String.valueOf(c.get(Calendar.YEAR)) + "-" +String.valueOf(c.get(Calendar.WEEK_OF_YEAR) - 1));
        weekList.add(String.valueOf(c.get(Calendar.YEAR)) + "-" +String.valueOf(c.get(Calendar.WEEK_OF_YEAR)));

        // ??????
        OmsEchrtsOfSupplierVO ZJData = new OmsEchrtsOfSupplierVO();
        ZJData.setMonthList(weekList.toArray(new String[weekList.size()]));// x???
        //Map<String,String> ZJOTD = getOTDData(startMonth,endDay,"ZJ");
        Map<String,String> ZJOTD = getOTDDataByItemCodeWeek(startWeek,endWeek,"ZJ");
        List<String> otdZJList = new ArrayList<>();
        List<String> otdZJListSeven = new ArrayList<>();
        weekList.stream().forEach(month->{
            if(ZJOTD.containsKey(month)){
                otdZJList.add(ZJOTD.get(month).split("~")[0]);
                otdZJListSeven.add(ZJOTD.get(month).split("~")[1]);
            } else {
                otdZJList.add("0");
                otdZJListSeven.add("0");
            }
        });
        ZJData.setOtd(otdZJList.toArray(new String[otdZJList.size()]));
        ZJData.setOtdSeven(otdZJListSeven.toArray(new String[otdZJListSeven.size()]));
        omsEchrtsOfSupplierVOS.add(ZJData);

        // ??????
        OmsEchrtsOfSupplierVO DJData = new OmsEchrtsOfSupplierVO();
        DJData.setMonthList(weekList.toArray(new String[weekList.size()]));// x???
        // Map<String,String> DJOTD = getOTDData(startMonth,endDay,"DJ");
        Map<String,String> DJOTD = getOTDDataByItemCodeWeek(startWeek,endWeek,"DJ");
        List<String> otdDJList = new ArrayList<>();
        List<String> otdDJListSeven = new ArrayList<>();
        weekList.stream().forEach(month->{
            if(DJOTD.containsKey(month)){
                otdDJList.add(DJOTD.get(month).split("~")[0]);
                otdDJListSeven.add(DJOTD.get(month).split("~")[1]);
            } else {
                otdDJList.add("0");
                otdDJListSeven.add("0");
            }
        });
        DJData.setOtd(otdDJList.toArray(new String[otdDJList.size()]));
        DJData.setOtdSeven(otdDJListSeven.toArray(new String[otdDJListSeven.size()]));
        omsEchrtsOfSupplierVOS.add(DJData);

        // ??????
        OmsEchrtsOfSupplierVO QZData = new OmsEchrtsOfSupplierVO();
        QZData.setMonthList(weekList.toArray(new String[weekList.size()]));// x???
        // Map<String,String> QZOTD = getOTDData(startMonth,endDay,"QZ");
        Map<String,String> QZOTD = getOTDDataByItemCodeWeek(startWeek,endWeek,"QZ");
        List<String> otdQZList = new ArrayList<>();
        List<String> otdQZListSeven = new ArrayList<>();
        weekList.stream().forEach(month->{
            if(QZOTD.containsKey(month)){
                otdQZList.add(QZOTD.get(month).split("~")[0]);
                otdQZListSeven.add(QZOTD.get(month).split("~")[1]);
            } else {
                otdQZList.add("0");
                otdQZListSeven.add("0");
            }
        });
        QZData.setOtd(otdQZList.toArray(new String[otdQZList.size()]));
        QZData.setOtdSeven(otdQZListSeven.toArray(new String[otdQZListSeven.size()]));
        omsEchrtsOfSupplierVOS.add(QZData);


        // ??????
        OmsEchrtsOfSupplierVO FXData = new OmsEchrtsOfSupplierVO();
        FXData.setMonthList(weekList.toArray(new String[weekList.size()]));// x???
        Map<String,String> FXOTD = getOTDDataByItemCodeWeek(startWeek,endWeek,"FX");
        List<String> otdFXList = new ArrayList<>();
        List<String> otdFXListSeven = new ArrayList<>();
        weekList.stream().forEach(month->{
            if(FXOTD.containsKey(month)){
                otdFXList.add(FXOTD.get(month).split("~")[0]);
                otdFXListSeven.add(FXOTD.get(month).split("~")[1]);
            } else {
                otdFXList.add("0");
                otdFXListSeven.add("0");
            }
        });
        FXData.setOtd(otdFXList.toArray(new String[otdFXList.size()]));
        FXData.setOtdSeven(otdFXListSeven.toArray(new String[otdFXListSeven.size()]));
        omsEchrtsOfSupplierVOS.add(FXData);

        // ?????????
        OmsEchrtsOfSupplierVO WGFData = new OmsEchrtsOfSupplierVO();
        WGFData.setMonthList(weekList.toArray(new String[weekList.size()]));// x???
        // Map<String,String> WGFOTD = getOTDData(startMonth,endDay,"WGF");
        Map<String,String> WGFOTD = getOTDDataByItemCodeWeek(startWeek,endWeek,"WGF");
        List<String> otdWGFList = new ArrayList<>();
        List<String> otdWGFListSeven = new ArrayList<>();
        weekList.stream().forEach(month->{
            if(WGFOTD.containsKey(month)){
                otdWGFList.add(WGFOTD.get(month).split("~")[0]);
                otdWGFListSeven.add(WGFOTD.get(month).split("~")[1]);
            } else {
                otdWGFList.add("0");
                otdWGFListSeven.add("0");
            }
        });
        WGFData.setOtd(otdWGFList.toArray(new String[otdWGFList.size()]));
        WGFData.setOtdSeven(otdWGFListSeven.toArray(new String[otdWGFListSeven.size()]));
        omsEchrtsOfSupplierVOS.add(WGFData);

        // ?????????
        OmsEchrtsOfSupplierVO QKJData = new OmsEchrtsOfSupplierVO();
        QKJData.setMonthList(weekList.toArray(new String[weekList.size()]));// x???
        Map<String,String> QKJOTD = getOTDDataByItemCodeWeek(startWeek,endWeek,"QKJ");
        List<String> otdQKJList = new ArrayList<>();
        List<String> otdQKJListSeven = new ArrayList<>();
        weekList.stream().forEach(month->{
            if(QKJOTD.containsKey(month)){
                otdQKJList.add(QKJOTD.get(month).split("~")[0]);
                otdQKJListSeven.add(QKJOTD.get(month).split("~")[1]);
            } else {
                otdQKJList.add("0");
                otdQKJListSeven.add("0");
            }
        });
        QKJData.setOtd(otdQKJList.toArray(new String[otdQKJList.size()]));
        QKJData.setOtdSeven(otdQKJListSeven.toArray(new String[otdQKJListSeven.size()]));
        omsEchrtsOfSupplierVOS.add(QKJData);

        // ??????
        OmsEchrtsOfSupplierVO QGData = new OmsEchrtsOfSupplierVO();
        QGData.setMonthList(weekList.toArray(new String[weekList.size()]));// x???
        Map<String,String> QGOTD = getOTDDataByItemCodeWeek(startWeek,endWeek,"QG");
        List<String> otdQGList = new ArrayList<>();
        List<String> otdQGListSeven = new ArrayList<>();
        weekList.stream().forEach(month->{
            if(QGOTD.containsKey(month)){
                otdQGList.add(QGOTD.get(month).split("~")[0]);
                otdQGListSeven.add(QGOTD.get(month).split("~")[1]);
            } else {
                otdQGList.add("0");
                otdQGListSeven.add("0");
            }
        });
        QGData.setOtd(otdQGList.toArray(new String[otdQGList.size()]));
        QGData.setOtdSeven(otdQGListSeven.toArray(new String[otdQGListSeven.size()]));
        omsEchrtsOfSupplierVOS.add(QGData);

        // ??????
        OmsEchrtsOfSupplierVO WWData = new OmsEchrtsOfSupplierVO();
        WWData.setMonthList(weekList.toArray(new String[weekList.size()]));// x???
        Map<String,String> WWOTD = getOTDDataByItemCodeWeek(startWeek,endWeek,"WW");
        List<String> otdWWList = new ArrayList<>();
        List<String> otdWWListSeven = new ArrayList<>();
        weekList.stream().forEach(month->{
            if(WWOTD.containsKey(month)){
                otdWWList.add(WWOTD.get(month).split("~")[0]);
                otdWWListSeven.add(WWOTD.get(month).split("~")[1]);
            } else {
                otdWWList.add("0");
                otdWWListSeven.add("0");
            }
        });
        WWData.setOtd(otdWWList.toArray(new String[otdWWList.size()]));
        WWData.setOtdSeven(otdWWListSeven.toArray(new String[otdWWListSeven.size()]));
        omsEchrtsOfSupplierVOS.add(WWData);

        // ?????? ?????????????????????
        OmsEchrtsOfSupplierVO QTData = new OmsEchrtsOfSupplierVO();
        QTData.setMonthList(weekList.toArray(new String[weekList.size()]));// x???
        // Map<String,String> QTOTD = getOTDData(startMonth,endDay,"QT");
        Map<String,String> QTOTD = getOTDDataByItemCodeWeek(startWeek,endWeek,"QT");
        List<String> otdQTList = new ArrayList<>();
        List<String> otdQTListSeven = new ArrayList<>();
        weekList.stream().forEach(month->{
            if(QTOTD.containsKey(month)){
                otdQTList.add(QTOTD.get(month).split("~")[0]);
                otdQTListSeven.add(QTOTD.get(month).split("~")[1]);
            } else {
                otdQTList.add("0");
                otdQTListSeven.add("0");
            }
        });
        QTData.setOtd(otdQTList.toArray(new String[otdQTList.size()]));
        QTData.setOtdSeven(otdQTListSeven.toArray(new String[otdQTListSeven.size()]));
        omsEchrtsOfSupplierVOS.add(QTData);

        // ????????????
        OmsEchrtsOfSupplierVO ALLData = new OmsEchrtsOfSupplierVO();
        ALLData.setMonthList(weekList.toArray(new String[weekList.size()]));// x???
        Map<String,String> ALLOTD = getOTDDataByItemCodeWeek(startWeek,endWeek,"ALL");
        List<String> otdALLList = new ArrayList<>();
        List<String> otdALLListSeven = new ArrayList<>();
        weekList.stream().forEach(month->{
            if(ALLOTD.containsKey(month)){
                otdALLList.add(ALLOTD.get(month).split("~")[0]);
                otdALLListSeven.add(ALLOTD.get(month).split("~")[1]);
            } else {
                otdALLList.add("0");
                otdALLListSeven.add("0");
            }
        });
        ALLData.setOtd(otdALLList.toArray(new String[otdALLList.size()]));
        ALLData.setOtdSeven(otdALLListSeven.toArray(new String[otdALLListSeven.size()]));
        omsEchrtsOfSupplierVOS.add(ALLData);

        return omsEchrtsOfSupplierVOS;
    }

    /**
     * ?????????????????????(??????)
     * @param supplierScheduleReq
     * @return
     */
    @Override
    public List<OmsEchrtsOfSupplierVO> getMainDataForest(SupplierScheduleReq supplierScheduleReq) {
        List<OmsEchrtsOfSupplierVO> omsEchrtsOfSupplierVOS = new LinkedList<>();
        SimpleDateFormat formatWeb = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat formatWebEnd = new SimpleDateFormat("yyyy-MM-dd");
        BigDecimal totalUndeliveredNum = new BigDecimal("0");
        Calendar c = Calendar.getInstance();
        String pointedmonth = formatWeb.format(new Date());
        String startWeek = String.valueOf(c.get(Calendar.WEEK_OF_YEAR)+1);
        String endWeek = String.valueOf(c.get(Calendar.WEEK_OF_YEAR)+2);

        // ??????????????????????????????
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        String nextFirstDay =  new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        int year = Integer.parseInt(nextFirstDay.split("-")[0]);  //???
        int preMonth = Integer.parseInt(nextFirstDay.split("-")[1]); //???
        Calendar cal = Calendar.getInstance();
        // ????????????
        cal.set(Calendar.YEAR, year);
        // ????????????
        // cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.MONTH, preMonth); //??????????????????????????????
        // ????????????????????????
        //int lastDay = cal.getActualMaximum(Calendar.DATE);
        int lastDay = cal.getMinimum(Calendar.DATE); //??????????????????????????????????????????
        // ????????????????????????????????????
        //cal.set(Calendar.DAY_OF_MONTH, lastDay);
        cal.set(Calendar.DAY_OF_MONTH, lastDay - 1); //????????????????????????1???????????????????????????
        // ???????????????
        String endDay =  new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()).substring(0,7);


        List<String> timeList = new LinkedList<>();
        timeList.add(pointedmonth +"(???)");
        timeList.add(endDay +"(???)");
        timeList.add(String.valueOf(c.get(Calendar.YEAR)) + "-" +String.valueOf(c.get(Calendar.WEEK_OF_YEAR) + 1) + "(???)");
        timeList.add(String.valueOf(c.get(Calendar.YEAR)) + "-" +String.valueOf(c.get(Calendar.WEEK_OF_YEAR) + 2) + "(???)");

        // ??????
        OmsEchrtsOfSupplierVO ZJData = new OmsEchrtsOfSupplierVO();
        ZJData.setMonthList(timeList.toArray(new String[timeList.size()]));// x???
        Map<String,String> ZJOTD = getOTDDataByItemCodeForest(pointedmonth,endDay,startWeek,endWeek,"ZJ");
        List<String> otdZJList = new ArrayList<>();
        timeList.stream().forEach(month->{
            if(ZJOTD.containsKey(month)){
                otdZJList.add(ZJOTD.get(month));
            } else {
                otdZJList.add("0");
            }
        });
        ZJData.setOtd(otdZJList.toArray(new String[otdZJList.size()]));
        omsEchrtsOfSupplierVOS.add(ZJData);

        // ??????
        OmsEchrtsOfSupplierVO DJData = new OmsEchrtsOfSupplierVO();
        DJData.setMonthList(timeList.toArray(new String[timeList.size()]));// x???
        Map<String,String> DJOTD = getOTDDataByItemCodeForest(pointedmonth,endDay,startWeek,endWeek,"DJ");
        List<String> otdDJList = new ArrayList<>();
        timeList.stream().forEach(month->{
            if(DJOTD.containsKey(month)){
                otdDJList.add(DJOTD.get(month));
            } else {
                otdDJList.add("0");
            }
        });
        DJData.setOtd(otdDJList.toArray(new String[otdDJList.size()]));
        omsEchrtsOfSupplierVOS.add(DJData);

        // ??????
        OmsEchrtsOfSupplierVO QZData = new OmsEchrtsOfSupplierVO();
        QZData.setMonthList(timeList.toArray(new String[timeList.size()]));// x???
        Map<String,String> QZOTD = getOTDDataByItemCodeForest(pointedmonth,endDay,startWeek,endWeek,"QZ");
        List<String> otdQZList = new ArrayList<>();
        timeList.stream().forEach(month->{
            if(QZOTD.containsKey(month)){
                otdQZList.add(QZOTD.get(month));
            } else {
                otdQZList.add("0");
            }
        });
        QZData.setOtd(otdQZList.toArray(new String[otdQZList.size()]));
        omsEchrtsOfSupplierVOS.add(QZData);


        // ??????
        OmsEchrtsOfSupplierVO FXData = new OmsEchrtsOfSupplierVO();
        FXData.setMonthList(timeList.toArray(new String[timeList.size()]));// x???
        Map<String,String> FXOTD = getOTDDataByItemCodeForest(pointedmonth,endDay,startWeek,endWeek,"FX");
        List<String> otdFXList = new ArrayList<>();
        timeList.stream().forEach(month->{
            if(FXOTD.containsKey(month)){
                otdFXList.add(FXOTD.get(month));
            } else {
                otdFXList.add("0");
            }
        });
        FXData.setOtd(otdFXList.toArray(new String[otdFXList.size()]));
        omsEchrtsOfSupplierVOS.add(FXData);

        // ?????????
        OmsEchrtsOfSupplierVO WGFData = new OmsEchrtsOfSupplierVO();
        WGFData.setMonthList(timeList.toArray(new String[timeList.size()]));// x???
        Map<String,String> WGFOTD = getOTDDataByItemCodeForest(pointedmonth,endDay,startWeek,endWeek,"WGF");
        List<String> otdWGFList = new ArrayList<>();
        timeList.stream().forEach(month->{
            if(WGFOTD.containsKey(month)){
                otdWGFList.add(WGFOTD.get(month));
            } else {
                otdWGFList.add("0");
            }
        });
        WGFData.setOtd(otdWGFList.toArray(new String[otdWGFList.size()]));
        omsEchrtsOfSupplierVOS.add(WGFData);

        // ?????????
        OmsEchrtsOfSupplierVO QKJData = new OmsEchrtsOfSupplierVO();
        QKJData.setMonthList(timeList.toArray(new String[timeList.size()]));// x???
        Map<String,String> QKJOTD = getOTDDataByItemCodeForest(pointedmonth,endDay,startWeek,endWeek,"QKJ");
        List<String> otdQKJList = new ArrayList<>();
        timeList.stream().forEach(month->{
            if(QKJOTD.containsKey(month)){
                otdQKJList.add(QKJOTD.get(month));
            } else {
                otdQKJList.add("0");
            }
        });
        QKJData.setOtd(otdQKJList.toArray(new String[otdQKJList.size()]));
        omsEchrtsOfSupplierVOS.add(QKJData);

        // ??????
        OmsEchrtsOfSupplierVO QGData = new OmsEchrtsOfSupplierVO();
        QGData.setMonthList(timeList.toArray(new String[timeList.size()]));// x???
        Map<String,String> QGOTD = getOTDDataByItemCodeForest(pointedmonth,endDay,startWeek,endWeek,"QG");
        List<String> otdQGList = new ArrayList<>();
        timeList.stream().forEach(month->{
            if(QGOTD.containsKey(month)){
                otdQGList.add(QGOTD.get(month));
            } else {
                otdQGList.add("0");
            }
        });
        QGData.setOtd(otdQGList.toArray(new String[otdQGList.size()]));
        omsEchrtsOfSupplierVOS.add(QGData);

        // ??????
        OmsEchrtsOfSupplierVO WWData = new OmsEchrtsOfSupplierVO();
        WWData.setMonthList(timeList.toArray(new String[timeList.size()]));// x???
        Map<String,String> WWOTD = getOTDDataByItemCodeForest(pointedmonth,endDay,startWeek,endWeek,"WW");
        List<String> otdWWList = new ArrayList<>();
        timeList.stream().forEach(month->{
            if(WWOTD.containsKey(month)){
                otdWWList.add(WWOTD.get(month));
            } else {
                otdWWList.add("0");
            }
        });
        WWData.setOtd(otdWWList.toArray(new String[otdWWList.size()]));
        omsEchrtsOfSupplierVOS.add(WWData);

        // ?????? ?????????????????????
        OmsEchrtsOfSupplierVO QTData = new OmsEchrtsOfSupplierVO();
        QTData.setMonthList(timeList.toArray(new String[timeList.size()]));// x???
        Map<String,String> QTOTD = getOTDDataByItemCodeForest(pointedmonth,endDay,startWeek,endWeek,"QT");
        List<String> otdQTList = new ArrayList<>();
        timeList.stream().forEach(month->{
            if(QTOTD.containsKey(month)){
                otdQTList.add(QTOTD.get(month));
            } else {
                otdQTList.add("0");
            }
        });
        QTData.setOtd(otdQTList.toArray(new String[otdQTList.size()]));
        omsEchrtsOfSupplierVOS.add(QTData);

        // ????????????
        OmsEchrtsOfSupplierVO ALLData = new OmsEchrtsOfSupplierVO();
        ALLData.setMonthList(timeList.toArray(new String[timeList.size()]));// x???
        Map<String,String> ALLOTD = getOTDDataByItemCodeForest(pointedmonth,endDay,startWeek,endWeek,"ALL");
        List<String> otdALLList = new ArrayList<>();
        timeList.stream().forEach(month->{
            if(ALLOTD.containsKey(month)){
                otdALLList.add(ALLOTD.get(month));
            } else {
                otdALLList.add("0");
            }
        });
        ALLData.setOtd(otdALLList.toArray(new String[otdALLList.size()]));
        omsEchrtsOfSupplierVOS.add(ALLData);

        return omsEchrtsOfSupplierVOS;
    }


    /**
     * 20220223 ????????????????????????????????????????????? (???)
     * @param startDate
     * @param endDate
     * @param type
     * @return
     */
    private Map<String, String> getOTDDataByItemCode(String startDate, String endDate, String type) {
        StringBuffer okSql = new StringBuffer();
        StringBuffer totalSql = new StringBuffer();
        List<EchartVo> echartVoList = new ArrayList<>();
        Map<String, String> otdMap = new HashMap<>();
        String year = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).substring(0,4);

        if (endDate.compareTo("2022-05-31") <= 0) {
            // ??????????????? (OTD)
            List<OtdReport> otdReportsOfOk = supplierScheduleMapper.getOkMap(startDate, endDate, type);
            // ???????????????
            List<OtdReport> otdReportsOfTotal = supplierScheduleMapper.getTotalMap(startDate, endDate, type);

            Map<String, String> okMap = otdReportsOfOk.stream().collect(Collectors.toMap(OtdReport::getReqDate, OtdReport::getTypeNumber));

            otdReportsOfTotal.stream().forEach(otdReport -> {
                String key = otdReport.getReqDate();
                BigDecimal totalNum = new BigDecimal(otdReport.getTypeNumber());
                String otd = "";
                if (okMap.containsKey(key)) {
                    BigDecimal okNum = new BigDecimal(okMap.get(key));
                    otd = okNum.multiply(new BigDecimal("100")).divide(totalNum, 2, RoundingMode.HALF_UP).toString();
                } else {
                    otd = "0";
                }
                otdMap.put(key, otd);
            });
        }
        else if (startDate.compareTo("2022-05") > 0){
            // ???????????????
            List<OtdReport> otdReportsOfOk = supplierScheduleMapper.getOkMap(startDate, endDate, type);
            // ???????????????
            List<OtdReport> otdReportsOfTotal = supplierScheduleMapper.getTotalMap(startDate, endDate, type);

            Map<String, String> okMap = otdReportsOfOk.stream().collect(Collectors.toMap(OtdReport::getReqDate, OtdReport::getTypeNumber));

            otdReportsOfTotal.stream().forEach(otdReport -> {
                String key = otdReport.getReqDate();
                BigDecimal totalNum = new BigDecimal(otdReport.getTypeNumber());
                String otd = "";
                if (okMap.containsKey(key)) {
                    BigDecimal okNum = new BigDecimal(okMap.get(key));
                    otd = okNum.multiply(new BigDecimal("100")).divide(totalNum, 2, RoundingMode.HALF_UP).toString();
                } else {
                    otd = "0";
                }
                otdMap.put(key, otd);
            });
        }
        else {
            // ???????????????
            List<OtdReport> otdReportsOfOk = supplierScheduleMapper.getOkMap(startDate, "2022-05-31", type);
            // ???????????????
            List<OtdReport> otdReportsOfTotal = supplierScheduleMapper.getTotalMap(startDate, "2022-05-31", type);

            Map<String, String> okMap = otdReportsOfOk.stream().collect(Collectors.toMap(OtdReport::getReqDate, OtdReport::getTypeNumber));

            otdReportsOfTotal.stream().forEach(otdReport -> {
                String key = otdReport.getReqDate();
                BigDecimal totalNum = new BigDecimal(otdReport.getTypeNumber());
                String otd = "";
                if (okMap.containsKey(key)) {
                    BigDecimal okNum = new BigDecimal(okMap.get(key));
                    otd = okNum.multiply(new BigDecimal("100")).divide(totalNum, 2, RoundingMode.HALF_UP).toString();
                } else {
                    otd = "0";
                }
                otdMap.put(key, otd);
            });

            /*  <        *****  2022???5???31????????????  *****        > */

            // ???????????????
            List<OtdReport> otdReportsOfOkReq = supplierScheduleMapper.getOkMapOfReqDate("2022-06", endDate, type);
            // ???????????????
            List<OtdReport> otdReportsOfTotalReq = supplierScheduleMapper.getTotalMapOfReqDate("2022-06", endDate, type);

            Map<String, String> okMapSec = otdReportsOfOkReq.stream().collect(Collectors.toMap(OtdReport::getReqDate, OtdReport::getTypeNumber));

            otdReportsOfTotalReq.stream().forEach(otdReport -> {
                String key = otdReport.getReqDate();
                BigDecimal totalNum = new BigDecimal(otdReport.getTypeNumber());
                String otd = "";
                if (okMapSec.containsKey(key)) {
                    BigDecimal okNum = new BigDecimal(okMapSec.get(key));
                    otd = okNum.multiply(new BigDecimal("100")).divide(totalNum, 2, RoundingMode.HALF_UP).toString();
                } else {
                    otd = "0";
                }
                otdMap.put(key, otd);
            });
        }

        // ??????????????? (OTDSeven) [???????????????]
        List<OtdReport> otdReportsOfOkSeven = supplierScheduleMapper.getOkMapSeven(startDate, endDate, type);
        // ??????????????? (OTDSeven) [???????????????]
        List<OtdReport> otdReportsOfTotalSeven = supplierScheduleMapper.getTotalMapSeven(startDate, endDate, type);

        Map<String, String> okMapSeven = otdReportsOfOkSeven.stream().collect(Collectors.toMap(OtdReport::getReqDate, OtdReport::getTypeNumber));

        otdReportsOfTotalSeven.stream().forEach(otdReport -> {
            String key = otdReport.getReqDate();
            BigDecimal totalNum = new BigDecimal(otdReport.getTypeNumber());
            String otdSeven = "";
            if (okMapSeven.containsKey(key)) {
                BigDecimal okNum = new BigDecimal(okMapSeven.get(key));
                otdSeven = okNum.multiply(new BigDecimal("100")).divide(totalNum, 2, RoundingMode.HALF_UP).toString();
            } else {
                otdSeven = "0";
            }
            if(otdMap.containsKey(key)) {
                String otdOld = otdMap.get(key);
                otdMap.put(key, otdOld + "~" + otdSeven);
            } else {
                otdMap.put(key, "0" + "~" + otdSeven);
            }
        });
        return  otdMap;
    }

    /**
     * 20220223 ????????????????????????????????????????????? ?????????
     * @param startDate
     * @param endDate
     * @param type
     * @return
     */
    private Map<String, String> getOTDDataByItemCodeWeek(String startDate, String endDate, String type) {
        StringBuffer okSql = new StringBuffer();
        StringBuffer totalSql = new StringBuffer();
        List<EchartVo> echartVoList = new ArrayList<>();
        Map<String, String> otdMap = new HashMap<>();


        String year = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).substring(0,4);

        if( year.equals("2022") && Integer.valueOf(endDate) <= 21) {
            // ??????????????? (???????????????????????????????????????)
            List<OtdReport> otdReportsOfOk = supplierScheduleMapper.getOkMapWeek(startDate,endDate,type,year);
            // ??????????????? (???????????????????????????????????????,?????????)
            List<OtdReport> otdReportsOfTotal = supplierScheduleMapper.getTotalMapWeek(startDate,endDate,type,year);
            // ?????????????????? (???????????????????????????????????????) ????????????????????? ??? ?????????????????? ????????????
            List<OtdReport> totalMapWeekWithOutCheckUpdateTime = supplierScheduleMapper.getTotalMapWeekWithOutCheckUpdateTime(startDate,endDate,type,year);


            Map<String, String> okMap = otdReportsOfOk.stream().collect(Collectors.toMap(OtdReport::getReqDate, OtdReport::getTypeNumber));
            Map<String, String> ngMap = totalMapWeekWithOutCheckUpdateTime.stream().collect(Collectors.toMap(OtdReport::getReqDate, OtdReport::getTypeNumber));
            otdReportsOfTotal.stream().forEach(otdReport -> {
                // update_check_time?????????
                String key = otdReport.getReqDate();
                String typeNumber = otdReport.getTypeNumber();
                if(ngMap.containsKey(key)){
                    typeNumber = new BigDecimal(typeNumber).add(new BigDecimal(ngMap.get(key))).toString();
                }
                BigDecimal totalNum  = new BigDecimal(typeNumber);

                String otd = "";
                if(okMap.containsKey(key)){
                    BigDecimal okNum = new BigDecimal(okMap.get(key));
                    otd =  okNum.multiply(new BigDecimal("100")).divide(totalNum,2,RoundingMode.HALF_UP).toString();
                } else {
                    otd = "0";
                }
                otdMap.put(key,otd);
            });
        //} else if ( Integer.valueOf(year) >= 2022 && Integer.valueOf(startDate) > 21)) {//?????????  2022???21????????????????????????   ????????????????????????
        } else if ( Integer.valueOf(year) >= 2022 ) {
            // ???????????????
            List<OtdReport> otdReportsOfOk = supplierScheduleMapper.getOkMapWeekOfReqDate(startDate,endDate,type,year);
            // ???????????????
            List<OtdReport> otdReportsOfTotal = supplierScheduleMapper.getTotalMapWeekOfReqDate(startDate,endDate,type,year);

            Map<String, String> okMap = otdReportsOfOk.stream().collect(Collectors.toMap(OtdReport::getReqDate, OtdReport::getTypeNumber));

            otdReportsOfTotal.stream().forEach(otdReport -> {
                String key = otdReport.getReqDate();
                BigDecimal totalNum  = new BigDecimal(otdReport.getTypeNumber());
                String otd = "";
                if(okMap.containsKey(key)){
                    BigDecimal okNum = new BigDecimal(okMap.get(key));
                    otd =  okNum.multiply(new BigDecimal("100")).divide(totalNum,2,RoundingMode.HALF_UP).toString();
                } else {
                    otd = "0";
                }
                otdMap.put(key,otd);
            });
        } else {
            // ??????????????? (???????????????????????????????????????)
            List<OtdReport> otdReportsOfOkOne = supplierScheduleMapper.getOkMapWeek(startDate,"21",type,year);
            // ??????????????? (???????????????????????????????????????,?????????)
            List<OtdReport> otdReportsOfTotalOne = supplierScheduleMapper.getTotalMapWeek(startDate,"21",type,year);
            // ?????????????????? (???????????????????????????????????????) ????????????????????? ??? ?????????????????? ????????????
            List<OtdReport> totalMapWeekWithOutCheckUpdateTime = supplierScheduleMapper.getTotalMapWeekWithOutCheckUpdateTime(startDate,"21",type,year);

            Map<String, String> okMap = otdReportsOfOkOne.stream().collect(Collectors.toMap(OtdReport::getReqDate, OtdReport::getTypeNumber));
            Map<String, String> ngMap = totalMapWeekWithOutCheckUpdateTime.stream().collect(Collectors.toMap(OtdReport::getReqDate, OtdReport::getTypeNumber));

            otdReportsOfTotalOne.stream().forEach(otdReport -> {
                // update_check_time?????????
                String key = otdReport.getReqDate();
                String typeNumber = otdReport.getTypeNumber();
                if(ngMap.containsKey(key)){
                    typeNumber = new BigDecimal(typeNumber).add(new BigDecimal(ngMap.get(key))).toString();
                }
                BigDecimal totalNum  = new BigDecimal(typeNumber);

                String otd = "";
                if(okMap.containsKey(key)){
                    BigDecimal okNum = new BigDecimal(okMap.get(key));
                    otd =  okNum.multiply(new BigDecimal("100")).divide(totalNum,2,RoundingMode.HALF_UP).toString();
                } else {
                    otd = "0";
                }
                otdMap.put(key,otd);
            });

            /*  <        *****  2022???5???31????????????  *****        > */

            // ???????????????
            List<OtdReport> otdReportsOfOk = supplierScheduleMapper.getOkMapWeekOfReqDate("22",endDate,type,year);
            // ???????????????
            List<OtdReport> otdReportsOfTotal = supplierScheduleMapper.getTotalMapWeekOfReqDate("22",endDate,type,year);

            Map<String, String> okMapSecond = otdReportsOfOk.stream().collect(Collectors.toMap(OtdReport::getReqDate, OtdReport::getTypeNumber));

            otdReportsOfTotal.stream().forEach(otdReport -> {
                String key = otdReport.getReqDate();
                BigDecimal totalNum = new BigDecimal(otdReport.getTypeNumber());
                BigDecimal okNum = new BigDecimal(okMapSecond.get(key));
                String otd = okNum.multiply(new BigDecimal("100")).divide(totalNum, 2, RoundingMode.HALF_UP).toString();
                otdMap.put(key, otd);
            });
        }


        // ??????????????? (OTDSeven) [???????????????]
        List<OtdReport> otdReportsOfOkSeven = supplierScheduleMapper.getOkMapWeekOfReqDateSeven(startDate,endDate,type,year);
        // ??????????????? (OTDSeven) [???????????????]
        List<OtdReport> otdReportsOfTotalSeven = supplierScheduleMapper.getTotalMapWeekOfReqDate(startDate,endDate,type,year);

        Map<String, String> okMapSeven = otdReportsOfOkSeven.stream().collect(Collectors.toMap(OtdReport::getReqDate, OtdReport::getTypeNumber));

        otdReportsOfTotalSeven.stream().forEach(otdReport -> {
            String key = otdReport.getReqDate();
            BigDecimal totalNum = new BigDecimal(otdReport.getTypeNumber());
            String otdSeven = "";
            if (okMapSeven.containsKey(key)) {
                BigDecimal okNum = new BigDecimal(okMapSeven.get(key));
                otdSeven = okNum.multiply(new BigDecimal("100")).divide(totalNum, 2, RoundingMode.HALF_UP).toString();
            } else {
                otdSeven = "0";
            }
            if(otdMap.containsKey(key)) {
                String otdOld = otdMap.get(key);
                otdMap.put(key, otdOld + "~" + otdSeven);
            } else {
                otdMap.put(key, "0" + "~" + otdSeven);
            }
        });
        return  otdMap;
    }

    /**
     * 20220223 ?????????????????????????????? ?????????????????????
     * @param startDate
     * @param endDate
     * @param type
     * @return
     */
    private Map<String, String> getOTDDataByItemCodeForest(String month, String preMonth,String startWeek, String endWeek,String type) {
        StringBuffer okSql = new StringBuffer();
        StringBuffer totalSql = new StringBuffer();
        List<EchartVo> echartVoList = new ArrayList<>();
        Map<String, String> otdMap = new LinkedHashMap<>();

        String year = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).substring(0,4);
        Calendar c = Calendar.getInstance();

        /* ???????????? */
        // ???????????????
        List<AllOtdReport> otdReportsOfMonth = supplierScheduleMapper.getMonthForestInfoOfPlanDate(month,type,year);
        if(otdReportsOfMonth.size()==0) {
            otdMap.put(month,"0");
        } else {
            int isOtdMonth = 0;
            for (AllOtdReport item : otdReportsOfMonth) {
                String idOtd = this.isOtdByChargeReqAndUpdateCheck(item);
                if(idOtd.equals("Y")) {
                    isOtdMonth = isOtdMonth + 1;
                }
            }
            String monthOtd = new BigDecimal(String.valueOf(isOtdMonth)).multiply(new BigDecimal("100")).divide(new BigDecimal(String.valueOf(otdReportsOfMonth.size())),2,RoundingMode.HALF_UP).toString();
            otdMap.put(month+"(???)",monthOtd);
        }

        /* ???????????? */
        // ???????????????
        List<AllOtdReport> otdReportsOfPreMonth = supplierScheduleMapper.getMonthForestInfoOfPlanDate(preMonth,type,year);
        if(otdReportsOfPreMonth.size()==0) {
            otdMap.put(month,"0");
        } else {
            int isOtdMonth = 0;
            for (AllOtdReport item : otdReportsOfPreMonth) {
                String idOtd = this.isOtdByChargeReqAndUpdateCheck(item);
                if(idOtd.equals("Y")) {
                    isOtdMonth = isOtdMonth + 1;
                }
            }
            String preMonthOtd = new BigDecimal(String.valueOf(isOtdMonth)).multiply(new BigDecimal("100")).divide(new BigDecimal(String.valueOf(otdReportsOfPreMonth.size())),2,RoundingMode.HALF_UP).toString();
            otdMap.put(preMonth+"(???)",preMonthOtd);
        }

        /* ???????????? */
        // ???????????????
        List<AllOtdReport> otdReportsOfWeek1 = supplierScheduleMapper.getWeekForestInfoOfReqDate(startWeek,type,year);
        if (otdReportsOfWeek1.size() == 0) {
            otdMap.put(startWeek, "0");
        } else {
            int isOtdWeek1 = 0;
            for (AllOtdReport item : otdReportsOfWeek1) {
                String idOtd = this.isOtdByChargeReqAndUpdateCheck(item);
                if (idOtd.equals("Y")) {
                    isOtdWeek1 = isOtdWeek1 + 1;
                }
            }
            String weekOtd1 = new BigDecimal(String.valueOf(isOtdWeek1)).multiply(new BigDecimal("100")).divide(new BigDecimal(String.valueOf(otdReportsOfWeek1.size())), 2, RoundingMode.HALF_UP).toString();
            otdMap.put(String.valueOf(c.get(Calendar.YEAR)) + "-" +String.valueOf(c.get(Calendar.WEEK_OF_YEAR) + 1)+"(???)", weekOtd1);
        }

        /* ??????????????? */
        // ???????????????
        List<AllOtdReport> otdReportsOfWeek2 = supplierScheduleMapper.getWeekForestInfoOfReqDate(endWeek, type, year);
        if (otdReportsOfWeek2.size() == 0) {
            otdMap.put(endWeek, "0");
        } else {
            int isOtdWeek2 = 0;
            for (AllOtdReport item : otdReportsOfWeek2) {
                String idOtd = this.isOtdByChargeReqAndUpdateCheck(item);
                if (idOtd.equals("Y")) {
                    isOtdWeek2 = isOtdWeek2 + 1;
                }
            }
            String weekOtd2 = new BigDecimal(String.valueOf(isOtdWeek2)).multiply(new BigDecimal("100")).divide(new BigDecimal(String.valueOf(otdReportsOfWeek2.size())), 2, RoundingMode.HALF_UP).toString();
            otdMap.put(String.valueOf(c.get(Calendar.YEAR)) + "-" +String.valueOf(c.get(Calendar.WEEK_OF_YEAR) + 2)+"(???)", weekOtd2);
        }

        return  otdMap;
    }



    private String isOtdByChargeReqAndUpdateCheck(AllOtdReport allOtdReport){
        String reqDate = allOtdReport.getReqDate().substring(0,10);
        String checkUpdateDate = allOtdReport.getCheckUpdateTime();
        if(checkUpdateDate==null) {
            return "N";
        } else {
            checkUpdateDate = checkUpdateDate.substring(0,10);
            if(reqDate.compareTo(checkUpdateDate)>=0) {
                return "Y";
            } else {
                return "N";
            }
        }
    }


    private Map<String, String> getOTDData(String startDate, String endDate, String type) {
        StringBuffer okSql = new StringBuffer();
        StringBuffer totalSql = new StringBuffer();
        List<EchartVo> echartVoList = new ArrayList<>();
        Map<String, String> okMap = new HashMap<>();
        Map<String, String> otdMap = new HashMap<>();

        // ???????????????
        okSql.append("SELECT")
            .append(" TO_CHAR(reqdate,'YYYY-MM') need_time ,")
            .append(" COUNT(reqdate) type_number")
            .append(" FROM")
            .append(" ATWAPSMID.SRM_OTD_RPT_NEW")
            .append(" WHERE")
            .append(" rcvqty >= ponum")
            .append(" AND TO_CHAR(reqdate,'YYYY-MM')  >= '").append(startDate).append("'")
            .append(" AND TO_CHAR(reqdate,'YYYY-MM-DD')  <= '").append(endDate).append("'");
        if (type.equals("ZJ")) {
            okSql.append(" AND (item_code like '1501%' or item_code like '1511%')");
        } else if (type.equals("DJ")) {
            okSql.append(" AND (item_code like '1502%' or item_code like '1505%')");
        } else if (type.equals("QZ")) {
            okSql.append(" AND (item_code like '1249%' or item_code like '130301%' or item_code like '130302%')");
        } else if (type.equals("WGF")) {
            okSql.append(" AND (item_code like '10%' or item_code like '11%')");
        } else if (type.equals("WW")) {
            okSql.append(" AND biz_type != '0'");
        } else {
            okSql.append(" AND item_code not like '1501%' AND item_code not like '1511%' AND item_code not like '1502%' AND item_code not like '1505%' " +
                "AND item_code not like '1249%' AND item_code not like '130301%' AND item_code not like '130302%' AND item_code not like '10%' AND item_code not like '11%' " +
                "AND biz_type = '0'");
        }
        okSql.append(" GROUP BY")
            .append(" TO_CHAR(reqdate,'YYYY-MM')")
            .append(" ORDER BY")
            .append(" TO_CHAR(reqdate,'YYYY-MM')");


        // ???????????????
        totalSql.append("SELECT")
            .append(" TO_CHAR(reqdate,'YYYY-MM') need_time ,")
            .append(" COUNT(reqdate) type_number")
            .append(" FROM")
            .append(" ATWAPSMID.SRM_OTD_RPT_NEW")
            .append(" WHERE")
            .append(" TO_CHAR(reqdate,'YYYY-MM')  >= '").append(startDate).append("'")
            .append(" AND TO_CHAR(reqdate,'YYYY-MM-DD')  <= '").append(endDate).append("'");
        if (type.equals("ZJ")) {
            totalSql.append(" AND (item_code like '1501%' or item_code like '1511%')");
        } else if (type.equals("DJ")) {
            totalSql.append(" AND (item_code like '1502%' or item_code like '1505%')");
        } else if (type.equals("QZ")) {
            totalSql.append(" AND (item_code like '1249%' or item_code like '130301%' or item_code like '130302%')");
        } else if (type.equals("WGF")) {
            totalSql.append(" AND (item_code like '10%' or item_code like '11%')");
        } else if (type.equals("WW")) {
            totalSql.append(" AND biz_type != '0'");
        } else {
            totalSql.append(" AND item_code not like '1501%' AND item_code not like '1511%' AND item_code not like '1502%' AND item_code not like '1505%' " +
                "AND item_code not like '1249%' AND item_code not like '130301%' AND item_code not like '130302%' AND item_code not like '10%' AND item_code not like '11%' " +
                "AND biz_type = '0'");
        }
        totalSql.append(" GROUP BY")
            .append(" TO_CHAR(reqdate,'YYYY-MM')")
            .append(" ORDER BY")
            .append(" TO_CHAR(reqdate,'YYYY-MM')");

        List<String> otdData = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;

        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.??????????????????sql
            @Cleanup Statement stmt = conn.createStatement();
            String OKSQL = okSql.toString();
            String TOTALSQL = totalSql.toString();
            //3.??????
            @Cleanup ResultSet resultSetOK = stmt.executeQuery(OKSQL);
            while (resultSetOK.next()) {
                okMap.put(resultSetOK.getString("need_time"),resultSetOK.getString("type_number"));
            }

            @Cleanup ResultSet resultSetTOTAL = stmt.executeQuery(TOTALSQL);
            while (resultSetTOTAL.next()) {
                String time = resultSetTOTAL.getString("need_time");
                if(okMap.containsKey(time)){
                    BigDecimal okNum = new BigDecimal(okMap.get(time));
                    BigDecimal totalNum = new BigDecimal(resultSetTOTAL.getString("type_number"));
                    String otd = okNum.multiply(new BigDecimal("100")).divide(totalNum,2,RoundingMode.HALF_UP).toString();
                    otdMap.put(time,otd);
                }
            }
        }catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
        } finally {
            return otdMap;
        }
    }

    /**
     * ??????Echarts??????(?????????)
     * @param supplierScheduleReq
     * @return
     */
    @Override
    public OmsEchrtsOfSupplierVO getLineEchartsNum(SupplierScheduleReq supplierScheduleReq) {
        // ???????????????????????????????????????????????????
        List<String> supOfZJ = new ArrayList<>();
        supOfZJ.add("MAT-006");
        supOfZJ.add("MAT-007");
        supOfZJ.add("MAT-009");
        supOfZJ.add("MAT-025");
        supOfZJ.add("MAT-030");
        supOfZJ.add("MAT-039");
        supOfZJ.add("MAT-046");
        supOfZJ.add("MAT-048");
        supOfZJ.add("MAT-053");
        supOfZJ.add("MAT-055");
        supOfZJ.add("AP-061");

        String supCodes = supOfZJ.stream().map(String::valueOf).collect(Collectors.joining(","));


        OmsEchrtsOfSupplierVO omsEchrtsOfSupplierVO = new OmsEchrtsOfSupplierVO();

        if(true){ // ????????????
            return omsEchrtsOfSupplierVO;
        }

        Map<String,List<String>> lineMap = new LinkedHashMap<>();
        Map<String,String> itemWeight = new HashMap<>(); // ??????MAP
        String year = supplierScheduleReq.getYear();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat formatWeb = new SimpleDateFormat("yyyy-MM");

        String startMonth = "";
        String endMonth = "";
        Calendar calendar = Calendar.getInstance();

        if (year==null || year.isEmpty()) {
            Date now = new Date();
            Date startDate = stepMonth(now, -11);
            Date endDate = stepMonth(now, 0);
            startMonth = format.format(startDate);
            endMonth = format.format(endDate);
        } else {
            startMonth = year + "01";
            endMonth = year + "12";
        }

        for(String supCode:supOfZJ){
            // ???????????????????????????????????????[??????????????????]
            List<String> itemNameList = supplierScheduleMapper.selectAllTypeItemName(supCode, year, startMonth, endMonth);
            itemNameList.stream().forEach(itemName ->{
                // ??????
                ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(itemName);
                itemInfoEntity.setSupCode(supCode);
                // ???????????????
                if (itemInfoEntity.getSeries() != null) {
                    // ??????
                    try {
                        List<ItemInfoEntityOfZDJ> itemInfoEntityOfZDJ = supplierScheduleMapper.selectZJWeightOfSup(itemInfoEntity);
                        if (itemInfoEntityOfZDJ.size()>0 && itemInfoEntityOfZDJ.get(0).getWeight() != null && !itemInfoEntityOfZDJ.get(0).getWeight().isEmpty()) {
                            // ?????????MAP??????????????????
                            itemWeight.put(itemName,itemInfoEntityOfZDJ.get(0).getWeight());
                        }
                    } catch (Exception e){
                        throw new RuntimeException("????????????????????????????????????????????????????????????????????????????????????????????????" + itemName);
                    }
                }
            });

            List<String> otdList = new ArrayList<>();
            List<String> monthList = new LinkedList<>();
            List<String> totalNum = new LinkedList<>(); // ???????????????
            List<String> deliveredNum = new LinkedList<>(); // ??????????????????

            List<EchartVo> totalNumEcharts = supplierScheduleMapper.selectTotalZJNeed(supCode, year, startMonth, endMonth);
            List<EchartVo> deliveredNumEcharts = supplierScheduleMapper.selectDeliveredZJNeed(supCode, year, startMonth, endMonth);

            Map<String, String> totalNumEchartsMap = handleZJData(itemWeight,totalNumEcharts); //?????????
            Map<String, String> deliveredNumEchartsMap = handleZJData(itemWeight,deliveredNumEcharts); // ??????

            for (int i = -11; i <= 0; i++) {
                String month = "";
                if (year == null || year.isEmpty()) {
                    Date now = new Date();
                    Date newDate = stepMonth(now, i);
                    month = formatWeb.format(newDate);
                } else {
                    Integer mon = 12 + i;
                    month = year + '-' + String.format("%02d", mon);
                }
                monthList.add(month);

                if (!totalNumEchartsMap.containsKey(month)) {
                    totalNumEchartsMap.put(month, "0");
                } else {
                    totalNumEchartsMap.put(month, new BigDecimal(totalNumEchartsMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
                }

                if (!deliveredNumEchartsMap.containsKey(month)) {
                    deliveredNumEchartsMap.put(month, "0");
                } else {
                    deliveredNumEchartsMap.put(month, new BigDecimal(deliveredNumEchartsMap.get(month).toString()).setScale(0, RoundingMode.HALF_UP).toString());
                }

                String otd = "";
                if (!totalNumEchartsMap.get(month).equals("0") && !deliveredNumEchartsMap.get(month).equals("0")) {
                    otd = new BigDecimal(deliveredNumEchartsMap.get(month)).multiply(new BigDecimal("100")).divide(new BigDecimal(totalNumEchartsMap.get(month)), 0, RoundingMode.HALF_UP).toString();
                } else {
                    otd = "0";
                }
                omsEchrtsOfSupplierVO.setMonthList(monthList.toArray(new String[monthList.size()]));// x???
                otdList.add(otd);
            }
            lineMap.put(supCode,otdList);
        }
        omsEchrtsOfSupplierVO.setLineEChartsData(lineMap);

        return omsEchrtsOfSupplierVO;
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param sourceDate ????????????
     * @param month      ??????????????????????????????????????????????????????
     * @return
     */
    public static Date stepMonth(Date sourceDate, int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.MONTH, month);
        return c.getTime();
    }


    private String getNeedItemSql(String supCode, String startMonth, String endMonth) {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT")
                .append(" TO_CHAR(rpt.need_date,'YYYY-MM') need_time ,")
                .append(" COUNT(DISTINCT pi.item_code ) type_number")
                .append(" FROM")
                .append(" atwsrm.atw_po_item pi left join atwrpt.mv_srm_promonitor_rpt rpt on pi.po_code||'-'||pi.po_ln = rpt.po_code_ln")
                .append(" WHERE")
                .append(" rpt.need_date is not null ")
                .append(" AND pi.sup_code = '").append(supCode).append("' ")
                .append(" AND pi.is_deleted = 0")
                .append(" AND pi.po_code is not null")
                .append(" AND TO_CHAR(rpt.need_date,'YYYYMM')  >= '").append(startMonth).append("'")
                .append(" AND TO_CHAR(rpt.need_date,'YYYYMM')  <= '").append(endMonth).append("'")
                .append(" GROUP BY")
                .append(" TO_CHAR(rpt.need_date,'YYYY-MM')")
                .append(" ORDER BY")
                .append(" TO_CHAR(rpt.need_date,'YYYY-MM')");
            return sql.toString();
    }

    private String getPreItemSql(String supCode, String startMonth, String endMonth) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT")
            .append(" TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM') need_time ,")
            .append(" COUNT(DISTINCT pi.item_code ) type_number")
            .append(" FROM")
            .append(" atwsrm.atw_po_item pi LEFT JOIN (  SELECT TO_CHAR ( need_date, 'YYYY-MM' ) needTime,PO_CODE_LN FROM atwrpt.mv_srm_promonitor_rpt group by TO_CHAR ( need_date, 'YYYY-MM' ),PO_CODE_LN ) rpt ON pi.po_code || '-' || pi.po_ln = rpt.po_code_ln ")
            .append(" WHERE")
            .append(" rpt.needTime is null ")
            .append(" AND pi.sup_code = '").append(supCode).append("' ")
            .append(" AND pi.is_deleted = 0")
            .append(" AND pi.po_code is not null")
            .append(" AND TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYYMM')  >= '").append(startMonth).append("'")
            .append(" AND TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYYMM')  <= '").append(endMonth).append("'")
            .append(" GROUP BY")
            .append(" TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM')")
            .append(" ORDER BY")
            .append(" TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM')");
        return sql.toString();
    }

    private String getNeedPriceSql(String supCode, String startMonth, String endMonth) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT")
            .append(" rpt.need_time ,")
            .append(" ROUND(sum( rpt.reqNum * pi.price ),2) amount ")
            .append(" FROM")
            .append(" atwsrm.atw_po_item pi LEFT JOIN ( SELECT TO_CHAR ( need_date, 'YYYY-MM' ) need_time, PO_CODE_LN,sum(????????????) reqNum FROM atwrpt.mv_srm_promonitor_rpt GROUP BY TO_CHAR ( need_date, 'YYYY-MM' ), PO_CODE_LN ) rpt ON pi.po_code || '-' || pi.po_ln = rpt.po_code_ln ")
            .append(" WHERE")
            .append(" rpt.need_time is not null ")
            .append(" AND pi.sup_code = '").append(supCode).append("' ")
            .append(" AND pi.is_deleted = 0")
            .append(" AND pi.po_code is not null")
            .append(" AND rpt.need_time  >= '").append(startMonth).append("'")
            .append(" AND rpt.need_time  <= '").append(endMonth).append("'")
            .append(" GROUP BY")
            .append(" rpt.need_time")
            .append(" ORDER BY")
            .append(" rpt.need_time");
        return sql.toString();
    }

    private String getPrePriceSql(String supCode, String startMonth, String endMonth) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT")
            .append(" TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM') need_time ,")
            .append(" sum( pi.amount ) amount ")
            .append(" FROM")
            .append(" atwsrm.atw_po_item pi LEFT JOIN (  SELECT TO_CHAR ( need_date, 'YYYY-MM' ) needTime,PO_CODE_LN FROM atwrpt.mv_srm_promonitor_rpt group by TO_CHAR ( need_date, 'YYYY-MM' ),PO_CODE_LN ) rpt ON pi.po_code || '-' || pi.po_ln = rpt.po_code_ln ")
            .append(" WHERE")
            .append(" rpt.needTime is null ")
            .append(" AND pi.sup_code = '").append(supCode).append("' ")
            .append(" AND pi.is_deleted = 0")
            .append(" AND pi.po_code is not null")
            .append(" AND TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM')  >= '").append(startMonth).append("'")
            .append(" AND TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM')  <= '").append(endMonth).append("'")
            .append(" GROUP BY")
            .append(" TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM')")
            .append(" ORDER BY")
            .append(" TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM')");
        return sql.toString();
    }

    private String getNeedNumSql(String supCode, String startMonth, String endMonth) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT")
            .append(" TO_CHAR(rpt.need_date,'YYYY-MM') need_time ,")
            .append(" sum( rpt.???????????? ) amount")
            .append(" FROM")
            .append(" atwsrm.atw_po_item pi left join atwrpt.mv_srm_promonitor_rpt rpt on pi.po_code||'-'||pi.po_ln = rpt.po_code_ln")
            .append(" WHERE")
            .append(" rpt.need_date is not null ")
            .append(" AND pi.sup_code = '").append(supCode).append("' ")
            .append(" AND pi.is_deleted = 0")
            .append(" AND pi.po_code is not null")
            .append(" AND TO_CHAR(rpt.need_date,'YYYYMM')  >= '").append(startMonth).append("'")
            .append(" AND TO_CHAR(rpt.need_date,'YYYYMM')  <= '").append(endMonth).append("'")
            .append(" GROUP BY")
            .append(" TO_CHAR(rpt.need_date,'YYYY-MM')")
            .append(" ORDER BY")
            .append(" TO_CHAR(rpt.need_date,'YYYY-MM')");
        return sql.toString();
    }

    private String getPreNumSql(String supCode, String startMonth, String endMonth) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT")
            .append(" TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM') need_time ,")
            .append(" sum( pi.price_num ) amount")
            .append(" FROM")
            .append(" atwsrm.atw_po_item pi LEFT JOIN (  SELECT TO_CHAR ( need_date, 'YYYY-MM' ) needTime,PO_CODE_LN FROM atwrpt.mv_srm_promonitor_rpt group by TO_CHAR ( need_date, 'YYYY-MM' ),PO_CODE_LN ) rpt ON pi.po_code || '-' || pi.po_ln = rpt.po_code_ln")
            .append(" WHERE")
            .append(" rpt.needTime is null ")
            .append(" AND pi.sup_code = '").append(supCode).append("' ")
            .append(" AND pi.is_deleted = 0")
            .append(" AND pi.po_code is not null")
            .append(" AND TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYYMM')  >= '").append(startMonth).append("'")
            .append(" AND TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYYMM')  <= '").append(endMonth).append("'")
            .append(" GROUP BY")
            .append(" TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM')")
            .append(" ORDER BY")
            .append(" TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM')");
        return sql.toString();
    }


    private String getNeedZJSql(String supCode, String startMonth, String endMonth) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT")
            .append(" TO_CHAR(rpt.need_date,'YYYY-MM') need_time ,")
            .append(" sum( rpt.???????????? ) amount,")
            .append(" pi.item_name")
            .append(" FROM")
            .append(" atwsrm.atw_po_item pi left join atwrpt.mv_srm_promonitor_rpt rpt on pi.po_code||'-'||pi.po_ln = rpt.po_code_ln")
            .append(" WHERE")
            .append(" rpt.need_date is not null ")
            .append(" AND pi.sup_code = '").append(supCode).append("' ")
            .append(" AND pi.is_deleted = 0")
            .append(" AND pi.po_code is not null")
            .append(" AND TO_CHAR(rpt.need_date,'YYYYMM')  >= '").append(startMonth).append("'")
            .append(" AND TO_CHAR(rpt.need_date,'YYYYMM')  <= '").append(endMonth).append("'")
            .append(" AND substr(pi.item_code,0,4) in ('1501', '1511')")
            .append(" GROUP BY")
            .append(" pi.item_name,")
            .append(" TO_CHAR(rpt.need_date,'YYYY-MM')")
            .append(" ORDER BY")
            .append(" TO_CHAR(rpt.need_date,'YYYY-MM'),")
            .append(" pi.item_name");
        return sql.toString();
    }

    private String getPreZJSql(String supCode, String startMonth, String endMonth) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT")
            .append(" TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM') need_time ,")
            .append(" sum( pi.tc_num ) amount,")
            .append(" pi.item_name")
            .append(" FROM")
            .append(" atwsrm.atw_po_item pi LEFT JOIN (  SELECT TO_CHAR ( need_date, 'YYYY-MM' ) needTime,PO_CODE_LN FROM atwrpt.mv_srm_promonitor_rpt group by TO_CHAR ( need_date, 'YYYY-MM' ),PO_CODE_LN ) rpt ON pi.po_code || '-' || pi.po_ln = rpt.po_code_ln")
            .append(" WHERE")
            .append(" rpt.needTime is null ")
            .append(" AND pi.sup_code = '").append(supCode).append("' ")
            .append(" AND pi.is_deleted = 0")
            .append(" AND pi.po_code is not null")
            .append(" AND TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYYMM')  >= '").append(startMonth).append("'")
            .append(" AND TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYYMM')  <= '").append(endMonth).append("'")
            .append(" AND substr(pi.item_code,0,4) in ('1501', '1511')")
            .append(" GROUP BY")
            .append(" pi.item_name,")
            .append(" TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM')")
            .append(" ORDER BY")
            .append(" TO_CHAR(pi.req_date / (60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM'),")
            .append(" pi.item_name");
        return sql.toString();
    }


    @Override
    public void otdExport(SupplierScheduleReq supplierScheduleReq, HttpServletResponse response) throws Exception  {
        String seriesName = supplierScheduleReq.getSeriesName();
        if(seriesName.indexOf("7???") < 0) {
            this.otdExportOrigin(supplierScheduleReq, response);
        } else {
            this.otdExportSeven(supplierScheduleReq, response);
        }
    }



    private void otdExportSeven(SupplierScheduleReq supplierScheduleReq, HttpServletResponse response) throws Exception {
        List<OtdExcel> allOtdReports = new ArrayList<>();
        String year = supplierScheduleReq.getDate().split("-")[0];
        String month = supplierScheduleReq.getDate().split("-")[1];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        if (supplierScheduleReq.getDateType().equals("month")) {
            allOtdReports = getOtdBasicInfo(supplierScheduleReq.getOtdType(), supplierScheduleReq.getDate());
        } else {
            List<OtdExcel> first = getOtdWeekBasicInfoOfReqDate(supplierScheduleReq.getOtdType(), supplierScheduleReq.getDate());
            allOtdReports.addAll(first);
        }

        String otdType = supplierScheduleReq.getOtdType();
        List<OtdExcel> otdExcelList = new ArrayList<>();

        for(OtdExcel item : allOtdReports) {
            // ??????????????????
            String itemName = item.getItemName();
            String bizType = item.getBizType();
            String placeName = item.getPlaceName();
            String itemCode = item.getItemCode();
            if (otdType.equals("QZ")) {
                if (bizType.equals("1")) {
                    // ????????????????????????????????????????????????QZ
                    if (!(itemName.indexOf("G50") > -1 || itemName.indexOf("G51") > -1)) {
                        continue;
                    }
                } else {
                    // ???????????????????????????????????????????????????QZ
                    if (!( (placeName.equals("?????????") || placeName.equals("?????????")) && (itemCode.indexOf("1251") > -1 || itemCode.indexOf("1252") > -1 || itemCode.indexOf("1303") > -1 || itemCode.indexOf("1226") > -1 || itemCode.indexOf("1249") > -1))) {
                        continue;
                    }
                }
            }

            String pointedDateSeven = item.getReqDate();
            String apsEndDate = item.getApsEndDate().substring(0,10);
            String apsEndFlag = "";
            if(item.getApsEndDate().substring(0,10).compareTo(new SimpleDateFormat("yyyy-MM-dd").format(new Date())) ==0 ) {
                apsEndFlag = "0";
            } else {
                apsEndFlag = "1";
            }
            String nowDate = format.format(new Date());

            if( pointedDateSeven!=null && !pointedDateSeven.isEmpty()){
                try {
                    Date date = new Date(format.parse(pointedDateSeven).getTime() + 24 * 3600 * 1000 * 7); //  ???????????? ????????? 7 ???
                    pointedDateSeven = format.format(date);
                } catch (Exception e) {
                    throw new RuntimeException("????????????????????????");
                }
            }
            // ???????????? ???????????? ???????????????????????????
            if(pointedDateSeven==null || pointedDateSeven.isEmpty()) {
                item.setIsOtd("?????????");
            } else {
                // ??????APS???????????????
                if ( apsEndFlag.equals("1")) {
                    // ????????????  >= APS?????????????????????    ==>  ??????
                    if (pointedDateSeven.compareTo(apsEndDate) >= 0) {
                        item.setIsOtd("??????");
                    } else {
                        item.setIsOtd("?????????");
                    }
                } else { // ???APS????????????
                    // ???????????? < APS?????????????????????????????????    ==>  ?????????
                    if (pointedDateSeven.compareTo(apsEndDate) < 0) {
                        item.setIsOtd("?????????");
                    } else {
                        // ???????????? >= APS?????????????????????????????????    ==>  ?????????
                        item.setIsOtd("??????");
                    }
                }
            }
            item.setPlanDate(item.getPlanDate().substring(0,10));
            item.setCheckUpdateDate(item.getCheckUpdateDate()==null?"":item.getCheckUpdateDate().substring(0,10));
            item.setApsEndDate(item.getApsEndDate().substring(0,10));
            otdExcelList.add(item);
        }

        // ??????????????????
        for (OtdExcel item : otdExcelList) {
            String apsEndFlag = "";

            if(item.getApsEndDate().substring(0,10).compareTo(new SimpleDateFormat("yyyy-MM-dd").format(new Date())) ==0 ) {
                apsEndFlag = "0";
            } else {
                apsEndFlag = "1";
            }

            // ?????????????????????????????? ????????????-????????????>30????????????????????????-17??????aps?????????????????????
            if (item.getPlaceName() != null && item.getPlaceName().equals("?????????") && item.getIsOtd().equals("?????????")) {
                Date planDate2 = format.parse(item.getPlanDate().substring(0, 10));
                //Date reqDate2 = format.parse(item.getReqDate().substring(0, 10));
                Date pointedZJDate = CommonUtil.getDateBefore(planDate2, 17); // ????????????-17???

                Date pointedZJDateSeven = CommonUtil.getDateAfter(pointedZJDate, 7); // ????????????-17???  + 7
                if (CommonUtil.daysBetween(pointedZJDateSeven,planDate2) > 30) {
                    // ??????APS???????????????
                    if (apsEndFlag.equals("1")) {

                        // ????????????-17?????? ??????????????? ???????????? ??? + 7  >= aps????????????  ===> ??????
                        if (format.format(pointedZJDateSeven).compareTo(item.getApsEndDate()) >= 0) {
                            item.setIsOtd("??????");
                        }
                    }
                }
            }
        }

        ExcelUtils.defaultExport(otdExcelList, OtdExcel.class, "????????????????????????7??????" + DateUtil.formatDate(new Date()), response);
    }

    private void otdExportOrigin(SupplierScheduleReq supplierScheduleReq, HttpServletResponse response)  throws Exception{

        List<OtdExcel> allOtdReports = new ArrayList<>();
        String year = supplierScheduleReq.getDate().split("-")[0];
        String month = supplierScheduleReq.getDate().split("-")[1];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        if (supplierScheduleReq.getDateType().equals("month")) {
            if(year.equals("2022") && Integer.valueOf(month)< 6 ) {
                allOtdReports = getOtdBasicInfo(supplierScheduleReq.getOtdType(), supplierScheduleReq.getDate());
            } else {
                allOtdReports = getOtdBasicInfo(supplierScheduleReq.getOtdType(), supplierScheduleReq.getDate());
            }
        } else if(supplierScheduleReq.getDateType().equals("WEEK")){
            if(year.equals("2022") && Integer.valueOf(month)<23) {
                List<OtdExcel> first = getOtdWeekBasicInfo(supplierScheduleReq.getOtdType(), supplierScheduleReq.getDate());
                List<OtdExcel> second = getOtdWeekBasicInfoWithOutCheck(supplierScheduleReq.getOtdType(), supplierScheduleReq.getDate());
                allOtdReports.addAll(first);
                allOtdReports.addAll(second);
            } else {
                List<OtdExcel> first = getOtdWeekBasicInfoOfReqDate(supplierScheduleReq.getOtdType(), supplierScheduleReq.getDate());
                allOtdReports.addAll(first);
            }
        } else {

            if(month.indexOf("???")>-1) {
                allOtdReports = getForecastOtdBasicInfoOfReq(supplierScheduleReq.getOtdType(), supplierScheduleReq.getDate().split("\\(")[0]);
            } else {
                allOtdReports = getOtdWeekBasicInfoOfReqDate(supplierScheduleReq.getOtdType(), supplierScheduleReq.getDate().split("\\(")[0]);
            }
        }

        String otdType = supplierScheduleReq.getOtdType();
        List<OtdExcel> otdExcelList = new ArrayList<>();
        if(supplierScheduleReq.getDateType().equals("month") || supplierScheduleReq.getDateType().equals("WEEK")) {
            for(OtdExcel item : allOtdReports) {

                String itemName = item.getItemName();
                String bizType = item.getBizType();
                String placeName = item.getPlaceName();
                String itemCode = item.getItemCode();

                // ??????????????????
                if (otdType.equals("QZ")) {
                    if (bizType.equals("1")) {
                        // ????????????????????????????????????????????????QZ
                        if (!(itemName.indexOf("G50") > -1 || itemName.indexOf("G51") > -1)) {
                            continue;
                        }
                    } else {
                        // ???????????????????????????????????????????????????QZ
                        if (!(    (placeName.equals("?????????") || placeName.equals("?????????"))    && (itemCode.indexOf("1251") > -1 || itemCode.indexOf("1252") > -1 || itemCode.indexOf("1303") > -1 || itemCode.indexOf("1226") > -1 || itemCode.indexOf("1249") > -1))) {
                            continue;
                        }
                    }
                }

                // ??????????????????
                if (otdType.equals("FX")) {
                        // ???1303???????????????????????????
                        if (itemCode.substring(0,4).equals("1303") && itemName.indexOf("??????")>-1) {
                            continue;
                        }
                }

                // ??????????????????
                if (otdType.equals("QT")) {
                    // ???1303??????????????????????????????
                    if (itemCode.substring(0,4).equals("1303") && itemName.indexOf("??????")<0) {
                        continue;
                    }
                }

                String pointedDate = "";// ???????????? (????????????????????? ????????? ??? )
                String pointedDateSeven = "";// ???????????? (????????????????????? ????????? ??? )
                if ((supplierScheduleReq.getDateType().equals("month") && year.equals("2022") && Integer.valueOf(month) < 5) || (supplierScheduleReq.getDateType().equals("week") && year.equals("2022") && Integer.valueOf(month) < 23)) {
                    pointedDate = item.getCheckUpdateDate();
                } else {
                    pointedDate = item.getReqDate();
                }

                String apsEndDate = item.getApsEndDate().substring(0,10);
                String apsEndFlag = "";
                if(item.getApsEndDate().substring(0,10).compareTo(new SimpleDateFormat("yyyy-MM-dd").format(new Date())) ==0 ) {
                    apsEndFlag = "0";
                } else {
                    apsEndFlag = "1";
                }
                String nowDate = format.format(new Date());

                if( pointedDate!=null && !pointedDate.isEmpty()){
                    try {
                        // Date date = new Date(format.parse(pointedDate).getTime() + 24 * 3600 * 1000 * 3); // ??????????????????????????? 3 ???
                        Date date = new Date(format.parse(pointedDate).getTime()); // ??????????????????
                        pointedDate = format.format(date);
                    } catch (Exception e) {
                        throw new RuntimeException("????????????????????????");
                    }
                }
                // ???????????? ???????????? ???????????????????????????
                if(pointedDate==null || pointedDate.isEmpty()) {
                    item.setIsOtd("?????????");
                } else {
                    // ??????APS???????????????
                    if ( apsEndFlag.equals("1")) {
                        // ????????????  >= APS?????????????????????    ==>  ??????
                        if (pointedDate.compareTo(apsEndDate) >= 0) {
                            item.setIsOtd("??????");
                        } else {
                            item.setIsOtd("?????????");
                        }
                    } else { // ???APS????????????
                        // ???????????? < APS?????????????????????????????????    ==>  ?????????
                        if (pointedDate.compareTo(apsEndDate) < 0) {
                            item.setIsOtd("?????????");
                        } else {
                            // ???????????? >= APS?????????????????????????????????    ==>  ??????
                            item.setIsOtd("??????");
                        }
                    }
                }
                item.setPlanDate(item.getPlanDate().substring(0,10));
                item.setCheckUpdateDate(item.getCheckUpdateDate()==null?"":item.getCheckUpdateDate().substring(0,10));
                item.setApsEndDate(item.getApsEndDate().substring(0,10));
                otdExcelList.add(item);
            }
        } else {
            for(OtdExcel item : allOtdReports) {
                // ??????????????????
                String itemName = item.getItemName();
                String bizType = item.getBizType();
                String placeName = item.getPlaceName();
                String itemCode = item.getItemCode();
                if (otdType.equals("QZ")) {
                    if (bizType.equals("1")) {
                        // ????????????????????????????????????????????????QZ
                        if (!(itemName.indexOf("G50") > -1 || itemName.indexOf("G51") > -1)) {
                            continue;
                        }
                    } else {
                        // ???????????????????????????????????????????????????QZ
                        if (!(  (placeName.equals("?????????") || placeName.equals("?????????")) && (itemCode.indexOf("1251") > -1 || itemCode.indexOf("1252") > -1 || itemCode.indexOf("1303") > -1 || itemCode.indexOf("1226") > -1 || itemCode.indexOf("1249") > -1)))
                        {
                            continue;
                        }
                    }
                }

                // ??????????????????
                if (otdType.equals("FX")) {
                    // ???1303???????????????????????????
                    if (itemCode.substring(0,4).equals("1303") && itemName.indexOf("??????")>-1) {
                        continue;
                    }
                }

                // ??????????????????
                if (otdType.equals("QT")) {
                    // ???1303??????????????????????????????
                    if (itemCode.substring(0,4).equals("1303") && itemName.indexOf("??????")<0) {
                        continue;
                    }
                }

                String checkUpdateDate = item.getCheckUpdateDate();
                String reqDate = item.getReqDate();
                if(checkUpdateDate==null) {
                    item.setIsOtd("?????????");
                } else {
                    if(checkUpdateDate.substring(0,10).compareTo(reqDate)<=0) {
                        item.setIsOtd("??????");
                    } else {
                        item.setIsOtd("?????????");
                    }
                }
                item.setPlanDate(item.getPlanDate().substring(0,10));
                item.setCheckUpdateDate(item.getCheckUpdateDate()==null?"":item.getCheckUpdateDate().substring(0,10));
                item.setApsEndDate(item.getApsEndDate().substring(0,10));
                otdExcelList.add(item);
            }
        }

        // ??????????????????
        for (OtdExcel item : otdExcelList) {
            String apsEndFlag = "";

            if(item.getApsEndDate().substring(0,10).compareTo(new SimpleDateFormat("yyyy-MM-dd").format(new Date())) ==0 ) {
                apsEndFlag = "0";
            } else {
                apsEndFlag = "1";
            }
            // ?????????????????????????????? ????????????-????????????>30????????????????????????-17??????aps?????????????????????
            if (item.getPlaceName() != null && item.getPlaceName().equals("?????????") && item.getIsOtd().equals("?????????")) {
                Date planDate2 = format.parse(item.getPlanDate().substring(0, 10));
                Date reqDate2 = format.parse(item.getReqDate().substring(0, 10));
                Date pointedZJDate = CommonUtil.getDateBefore(planDate2, 17); // ????????????-17???

                Date pointedZJDateSeven = CommonUtil.getDateAfter(pointedZJDate, 7); // ????????????-17???  + 7
                if (CommonUtil.daysBetween(reqDate2,planDate2) > 30) {
                    // ??????APS???????????????
                    if (apsEndFlag.equals("1")) {
                        // ????????????-17?????? ??????????????? ???????????? ??? >= aps????????????  ===> ??????
                        if (format.format(pointedZJDate).compareTo(item.getApsEndDate()) >= 0) {
                            item.setIsOtd("??????");
                        }
                    }
                }
            }
        }

        ExcelUtils.defaultExport(otdExcelList, OtdExcel.class, "?????????????????????" + DateUtil.formatDate(new Date()), response);
    }

    private String getChargeSql(String otdType,String firstDay) {

        String pointedMonth = firstDay.substring(0,7);

        // String sqlStr = " AND to_char(r.needtime,'yyyy-MM') = to_char(r.plandate, 'yyyy-MM') ";

        String sqlStr = " ";
        if(otdType.equals("ZJ")) {
            // sqlListBuilder.append(" AND ( r.itemcode LIKE '1501%' OR r.itemcode LIKE '1511%' ) AND SUBSTR ( r.pr_code,0,2 ) != 'WX' AND r.biz_type != 1");
            sqlStr = sqlStr + " AND s.place_name ='?????????'";
        } else if (otdType.equals("DJ")){
            sqlStr = sqlStr + " AND ( r.itemcode like '1502%' or r.itemcode like '1505%') AND SUBSTR ( r.pr_code,0,2 ) != 'WX' AND r.biz_type != 1";
        } else if (otdType.equals("QZ")) {
            sqlStr = sqlStr +  " AND (r.itemcode like '1251%' or r.itemcode like '1252%' or r.itemcode like '1303%'  or r.itemcode like '1226%'  or r.itemcode like '1249%' or r.itemname LIKE '%G50%' or  r.itemname LIKE '%G51%' ) AND SUBSTR ( r.pr_code,0,2 ) != 'WX'";
        } else if (otdType.equals("FX")) {
            sqlStr = sqlStr +  " AND s.place_name!='?????????'  and s.place_name!='?????????' and  (r.itemcode like '1251%' or r.itemcode like '1252%' or r.itemcode like '1303%'  or r.itemcode like '1226%'  or r.itemcode like '1249%') AND SUBSTR ( r.pr_code,0,2 ) != 'WX' AND r.biz_type != 1";
        } else if(otdType.equals("WGF")){
            sqlStr = sqlStr +  " AND (r.itemcode like '10%' or r.itemcode like '11%') AND SUBSTR ( r.pr_code,0,2 ) != 'WX' AND r.biz_type != 1";
        } else if(otdType.equals("QKJ")){
            sqlStr = sqlStr +  " AND  r.itemname not like '%??????%' AND s.place_name='?????????' and (r.itemcode not like '1403%') AND SUBSTR ( r.pr_code,0,2 ) != 'WX' AND r.biz_type != 1";
        } else if(otdType.equals("QG")){
            sqlStr = sqlStr +  " AND (r.itemcode like '1403%') AND SUBSTR ( r.pr_code,0,2 ) != 'WX' AND r.biz_type != 1";
        } else if(otdType.equals("WW")){
            sqlStr = sqlStr +  " AND (SUBSTR ( r.pr_code,0,2 )='WX' or r.biz_type = 1) and itemname NOT LIKE '%G50%' and itemname NOT LIKE '%G51%' and s.place_name!='?????????' and s.place_name!='?????????'";
        } else if(otdType.equals("QT")){
            sqlStr = sqlStr +  " AND s.place_name!='?????????' AND s.place_name!='?????????' AND s.place_name!='?????????' and s.place_name!='?????????' and r.biz_type != 1 AND SUBSTR ( r.pr_code,0,2 ) !='WX' AND SUBSTR ( r.itemcode,0,2 ) not IN ( '10', '11' ) AND SUBSTR ( r.itemcode,0,4 )  not IN ( '1501', '1511', '1502', '1505', '1249', '1401', '1403','1251','1252','1226')";
        }


        return sqlStr;
    }

    public List<OtdExcel> getOtdBasicInfo(String otdType,String date){
        List<OtdExcel> allOtdReports = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;

        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.set(Calendar.DAY_OF_MONTH,1);
        firstCalendar.add(Calendar.MONTH,0);
        String firstDay =  new SimpleDateFormat("yyyy-MM-dd").format(firstCalendar.getTime());

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.DAY_OF_MONTH,endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        endCalendar.add(Calendar.MONTH,0);
        String endDay =  new SimpleDateFormat("yyyy-MM-dd").format(endCalendar.getTime());

        String firstDate = getFisrtDayOfMonth(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));
        //String firstDate ="2022-01-01";
        String thisMonth = new SimpleDateFormat("yyyy-MM").format(new Date());
        String endDate = "";
//        if(thisMonth.equals(date)) {
//            endDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//        } else {
//            endDate = getLastDayOfMonth(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));
//        }
        endDate = getLastDayOfMonth(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));
        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.??????????????????sql
            @Cleanup Statement stmt = conn.createStatement();
            StringBuilder sqlListBuilder = new StringBuilder();

            // ?????????????????? ??? ??????????????? ??????
            sqlListBuilder.append("select r.subprjno,r.po_codesrm,r.po_lnsrm,r.status,r.plandate,r.needtime,r.check_update_date,r.updtime,r.status,r.pr_code,r.biz_type,r.itemcode,r.subneednum,r.needtime,r.itemname,r.sup_code,r.sup_name,s.place_name from atwrpt.mv_srm_otd_rpt r left join atwsrm.atw_supplier s on (r.sup_code = s.code and s.primary_contact=1) where  r.sup_code is not null and r.needtime is not NULL and r.plandate>=to_date('")
                .append(firstDate)
                .append("','yyyy-MM-dd') and r.plandate<=to_date('")
                .append(endDate)
                .append("','yyyy-MM-dd')");

            String chargeSql = getChargeSql(otdType,firstDate);
            sqlListBuilder.append(chargeSql);

            sqlListBuilder.append(" group by r.subprjno,r.po_codesrm,r.po_lnsrm,r.status,r.plandate,r.check_update_date,r.updtime,r.status,r.pr_code,r.biz_type,r.itemcode,r.subneednum,r.needtime,r.itemname,r.sup_code,r.sup_name,s.place_name");

            //3.??????
            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                OtdExcel allOtdReport = new OtdExcel();
                allOtdReport.setProNo(resultSetList.getString("subprjno"));
                allOtdReport.setItemCode(resultSetList.getString("itemcode"));
                allOtdReport.setItemName(resultSetList.getString("itemname"));
                allOtdReport.setSupCode(resultSetList.getString("sup_code"));
                allOtdReport.setSupName(resultSetList.getString("sup_name"));
                allOtdReport.setPoCode(resultSetList.getString("po_codesrm"));
                allOtdReport.setPoLn(resultSetList.getString("po_lnsrm"));
                allOtdReport.setPlanDate(resultSetList.getString("plandate"));
                allOtdReport.setCheckUpdateDate(resultSetList.getString("check_update_date"));
                allOtdReport.setReqDate(resultSetList.getString("needtime")==null?"":resultSetList.getString("needtime").substring(0,10));
                allOtdReport.setApsEndDate(resultSetList.getString("updtime"));
                allOtdReport.setApsEndFlag(resultSetList.getString("status"));
                allOtdReport.setBizType(resultSetList.getString("biz_Type"));
                allOtdReport.setPlaceName(resultSetList.getString("place_name"));
                allOtdReports.add(allOtdReport);
            }
        }catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
        } finally {
            return allOtdReports;
        }
    }

    public List<OtdExcel> getOtdBasicInfoOfReq(String otdType,String date){
        List<OtdExcel> allOtdReports = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;

        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.set(Calendar.DAY_OF_MONTH,1);
        firstCalendar.add(Calendar.MONTH,0);
        String firstDay =  new SimpleDateFormat("yyyy-MM-dd").format(firstCalendar.getTime());

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.DAY_OF_MONTH,endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        endCalendar.add(Calendar.MONTH,0);
        String endDay =  new SimpleDateFormat("yyyy-MM-dd").format(endCalendar.getTime());

        String firstDate = getFisrtDayOfMonth(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));
        String thisMonth = new SimpleDateFormat("yyyy-MM").format(new Date());
        String endDate = "";
        if(thisMonth.equals(date)) {
            endDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        } else {
            endDate = getLastDayOfMonth(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));
        }

        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.??????????????????sql
            @Cleanup Statement stmt = conn.createStatement();
            StringBuilder sqlListBuilder = new StringBuilder();

            // ?????????????????? ??? ??????????????? ??????
            sqlListBuilder.append("select r.subprjno,r.po_codesrm,r.po_lnsrm,r.status,r.plandate,r.needtime,r.check_update_date,r.updtime,r.status,r.pr_code,r.biz_type,r.itemcode,r.subneednum,r.usageqty,r.needtime,r.itemname,r.sup_code,r.sup_name,s.place_name from atwrpt.mv_srm_otd_rpt r left join atwsrm.atw_supplier s on (r.sup_code = s.code and s.primary_contact=1) where r.sup_code is not null and r.needtime>=to_date('")
                .append(firstDate)
                .append("','yyyy-MM-dd') and r.needtime<=to_date('")
                .append(endDate)
                .append("','yyyy-MM-dd')");

            String chargeSql = getChargeSql(otdType,firstDate);
            sqlListBuilder.append(chargeSql);

            sqlListBuilder.append(" group by r.subprjno,r.po_codesrm,r.po_lnsrm,r.status,r.plandate,r.check_update_date,r.updtime,r.status,r.pr_code,r.biz_type,r.itemcode,r.subneednum,r.usageqty,r.needtime,r.itemname,r.sup_code,r.sup_name,s.place_name");

            //3.??????
            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                OtdExcel allOtdReport = new OtdExcel();
                allOtdReport.setProNo(resultSetList.getString("subprjno"));
                allOtdReport.setItemCode(resultSetList.getString("itemcode"));
                allOtdReport.setItemName(resultSetList.getString("itemname"));
                allOtdReport.setSupCode(resultSetList.getString("sup_code"));
                allOtdReport.setSupName(resultSetList.getString("sup_name"));
                allOtdReport.setPoCode(resultSetList.getString("po_codesrm"));
                allOtdReport.setPoLn(resultSetList.getString("po_lnsrm"));
                allOtdReport.setPlanDate(resultSetList.getString("plandate"));
                allOtdReport.setCheckUpdateDate(resultSetList.getString("check_update_date"));
                allOtdReport.setReqDate(resultSetList.getString("needtime")==null?"":resultSetList.getString("needtime").substring(0,10));
                allOtdReport.setApsEndDate(resultSetList.getString("updtime"));
                allOtdReport.setApsEndFlag(resultSetList.getString("status"));
                allOtdReport.setBizType(resultSetList.getString("biz_Type"));
                allOtdReport.setPlaceName(resultSetList.getString("place_name"));
                allOtdReports.add(allOtdReport);
            }
        }catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
        } finally {
            return allOtdReports;
        }
    }

    public List<OtdExcel> getForecastOtdBasicInfoOfReq(String otdType,String date){
        List<OtdExcel> allOtdReports = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;

        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.set(Calendar.DAY_OF_MONTH,1);
        firstCalendar.add(Calendar.MONTH,0);
        String firstDay =  new SimpleDateFormat("yyyy-MM-dd").format(firstCalendar.getTime());

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.DAY_OF_MONTH,endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        endCalendar.add(Calendar.MONTH,0);
        String endDay =  new SimpleDateFormat("yyyy-MM-dd").format(endCalendar.getTime());

        String firstDate = getFisrtDayOfMonth(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));
        String endDate = getLastDayOfMonth(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));


        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.??????????????????sql
            @Cleanup Statement stmt = conn.createStatement();
            StringBuilder sqlListBuilder = new StringBuilder();

            // ?????????????????? ??? ??????????????? ??????
            sqlListBuilder.append("select r.subprjno,r.po_codesrm,r.po_lnsrm,r.status,r.plandate,r.needtime,r.check_update_date,r.updtime,r.status,r.pr_code,r.biz_type,r.itemcode,r.subneednum,r.usageqty,r.needtime,r.itemname,r.sup_code,r.sup_name,s.place_name from atwrpt.mv_srm_otd_rpt r left join atwsrm.atw_supplier s on (r.sup_code = s.code and s.primary_contact=1) where  r.sup_code is not null and r.needtime is not NULL and r.plandate>=to_date('")
                .append(firstDate)
                .append("','yyyy-MM-dd') and r.plandate<=to_date('")
                .append(endDate)
                .append("','yyyy-MM-dd')");

            String chargeSql = getChargeSql(otdType,firstDate);
            sqlListBuilder.append(chargeSql);

            sqlListBuilder.append(" group by r.subprjno,r.po_codesrm,r.po_lnsrm,r.status,r.plandate,r.check_update_date,r.updtime,r.status,r.pr_code,r.biz_type,r.itemcode,r.subneednum,r.usageqty,r.needtime,r.itemname,r.sup_code,r.sup_name,s.place_name");

            //3.??????
            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                OtdExcel allOtdReport = new OtdExcel();
                allOtdReport.setProNo(resultSetList.getString("subprjno"));
                allOtdReport.setItemCode(resultSetList.getString("itemcode"));
                allOtdReport.setItemName(resultSetList.getString("itemname"));
                allOtdReport.setSupCode(resultSetList.getString("sup_code"));
                allOtdReport.setSupName(resultSetList.getString("sup_name"));
                allOtdReport.setPoCode(resultSetList.getString("po_codesrm"));
                allOtdReport.setPoLn(resultSetList.getString("po_lnsrm"));
                allOtdReport.setPlanDate(resultSetList.getString("plandate"));
                allOtdReport.setCheckUpdateDate(resultSetList.getString("check_update_date"));
                allOtdReport.setReqDate(resultSetList.getString("needtime")==null?"":resultSetList.getString("needtime").substring(0,10));
                allOtdReport.setApsEndDate(resultSetList.getString("updtime"));
                allOtdReport.setApsEndFlag(resultSetList.getString("status"));
                allOtdReport.setBizType(resultSetList.getString("biz_Type"));
                allOtdReport.setPlaceName(resultSetList.getString("place_name"));
                allOtdReports.add(allOtdReport);
            }
        }catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
        } finally {
            return allOtdReports;
        }
    }



    public List<OtdExcel> getOtdWeekBasicInfo(String otdType,String date){
        List<OtdExcel> allOtdReports = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;

        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.set(Calendar.DAY_OF_MONTH,1);
        firstCalendar.add(Calendar.MONTH,0);
        String firstDay =  new SimpleDateFormat("yyyy-MM-dd").format(firstCalendar.getTime());

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.DAY_OF_MONTH,endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        endCalendar.add(Calendar.MONTH,0);
        String endDay =  new SimpleDateFormat("yyyy-MM-dd").format(endCalendar.getTime());

        String firstDate = getFisrtDayOfWeek(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));
        String endDate = getLastDayOfWeek(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));

        // ????????????????????????
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        String nextFirstDay =  new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        int year = Integer.parseInt(nextFirstDay.split("-")[0]);  //???
        int month = Integer.parseInt(nextFirstDay.split("-")[1]); //???
        Calendar cal = Calendar.getInstance();
        // ????????????
        cal.set(Calendar.YEAR, year);
        // ????????????
        // cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.MONTH, month); //??????????????????????????????
        // ????????????????????????
        //int lastDay = cal.getActualMaximum(Calendar.DATE);
        int lastDay = cal.getMinimum(Calendar.DATE); //??????????????????????????????????????????
        // ????????????????????????????????????
        //cal.set(Calendar.DAY_OF_MONTH, lastDay);
        cal.set(Calendar.DAY_OF_MONTH, lastDay - 1); //????????????????????????1???????????????????????????
        // ???????????????
        String planDateEndDate =  new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.??????????????????sql
            @Cleanup Statement stmt = conn.createStatement();
            StringBuilder sqlListBuilder = new StringBuilder();

            // ?????????????????? ??? ??????????????? ??????
            sqlListBuilder.append("select r.subprjno,r.po_codesrm,r.po_lnsrm,r.status,r.plandate,r.needtime,r.check_update_date,r.updtime,r.status,r.pr_code,r.biz_type,r.itemcode,r.subneednum,r.usageqty,r.needtime,r.itemname,r.sup_code,r.sup_name,s.place_name from atwrpt.mv_srm_otd_rpt r left join atwsrm.atw_supplier s on (r.sup_code = s.code and s.primary_contact=1) where r.plandate >= to_date ( '2022-01-01', 'yyyy-MM-dd' ) and r.needtime is not NULL and  r.sup_code is not null and r.check_update_date > =to_date('")
                .append(firstDate)
                .append("','yyyy-MM-dd') and r.check_update_date <=to_date('")
                .append(endDate)
                .append("','yyyy-MM-dd')")
                .append(" AND r.plandate <= to_date('")
                .append(planDateEndDate)
                .append("','yyyy-MM-dd')");

            String chargeSql = getChargeSql(otdType,firstDate);
            sqlListBuilder.append(chargeSql);

            sqlListBuilder.append(" group by r.subprjno,r.po_codesrm,r.po_lnsrm,r.status,r.plandate,r.check_update_date,r.updtime,r.status,r.pr_code,r.biz_type,r.itemcode,r.subneednum,r.usageqty,r.needtime,r.itemname,r.sup_code,r.sup_name,s.place_name");

            //3.??????
            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                OtdExcel allOtdReport = new OtdExcel();
                allOtdReport.setProNo(resultSetList.getString("subprjno"));
                allOtdReport.setItemCode(resultSetList.getString("itemcode"));
                allOtdReport.setItemName(resultSetList.getString("itemname"));
                allOtdReport.setSupCode(resultSetList.getString("sup_code"));
                allOtdReport.setSupName(resultSetList.getString("sup_name"));
                allOtdReport.setPoCode(resultSetList.getString("po_codesrm"));
                allOtdReport.setPoLn(resultSetList.getString("po_lnsrm"));
                allOtdReport.setPlanDate(resultSetList.getString("plandate"));
                allOtdReport.setCheckUpdateDate(resultSetList.getString("check_update_date"));
                allOtdReport.setReqDate(resultSetList.getString("needtime")==null?"":resultSetList.getString("needtime").substring(0,10));
                allOtdReport.setApsEndDate(resultSetList.getString("updtime"));
                allOtdReport.setApsEndFlag(resultSetList.getString("status"));
                allOtdReport.setBizType(resultSetList.getString("biz_Type"));
                allOtdReport.setPlaceName(resultSetList.getString("place_name"));
                allOtdReports.add(allOtdReport);
            }
        }catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
        } finally {
            return allOtdReports;
        }
    }

    public List<OtdExcel> getOtdWeekBasicInfoWithOutCheck(String otdType,String date){
        List<OtdExcel> allOtdReports = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;

        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.set(Calendar.DAY_OF_MONTH,1);
        firstCalendar.add(Calendar.MONTH,0);
        String firstDay =  new SimpleDateFormat("yyyy-MM-dd").format(firstCalendar.getTime());

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.DAY_OF_MONTH,endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        endCalendar.add(Calendar.MONTH,0);
        String endDay =  new SimpleDateFormat("yyyy-MM-dd").format(endCalendar.getTime());

        String firstDate = getFisrtDayOfWeek(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));
        String endDate = getLastDayOfWeek(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));

        // ????????????????????????
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        String nextFirstDay =  new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        int year = Integer.parseInt(nextFirstDay.split("-")[0]);  //???
        int month = Integer.parseInt(nextFirstDay.split("-")[1]); //???
        Calendar cal = Calendar.getInstance();
        // ????????????
        cal.set(Calendar.YEAR, year);
        // ????????????
        // cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.MONTH, month); //??????????????????????????????
        // ????????????????????????
        //int lastDay = cal.getActualMaximum(Calendar.DATE);
        int lastDay = cal.getMinimum(Calendar.DATE); //??????????????????????????????????????????
        // ????????????????????????????????????
        //cal.set(Calendar.DAY_OF_MONTH, lastDay);
        cal.set(Calendar.DAY_OF_MONTH, lastDay - 1); //????????????????????????1???????????????????????????
        // ???????????????
        String planDateEndDate =  new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.??????????????????sql
            @Cleanup Statement stmt = conn.createStatement();
            StringBuilder sqlListBuilder = new StringBuilder();

            // ?????????????????? ??? ??????????????? ??????
            sqlListBuilder.append("select r.subprjno,r.po_codesrm,r.po_lnsrm,r.status,r.plandate,r.check_update_date,r.updtime,r.status,r.pr_code,r.biz_type,r.itemcode,r.subneednum,r.usageqty,r.needtime,r.itemname,r.sup_code,r.sup_name,s.place_name from atwrpt.mv_srm_otd_rpt r left join atwsrm.atw_supplier s on (r.sup_code = s.code and s.primary_contact=1) where r.plandate >= to_date ( '2022-01-01', 'yyyy-MM-dd' ) and r.check_update_date is null and  r.sup_code is not null and r.updtime > =to_date('")
                .append(firstDate)
                .append("','yyyy-MM-dd') and r.updtime <=to_date('")
                .append(endDate)
                .append("','yyyy-MM-dd')")
                .append(" AND r.plandate <= to_date('")
                .append(planDateEndDate)
                .append("','yyyy-MM-dd')");

            String chargeSql = getChargeSql(otdType,firstDate);
            sqlListBuilder.append(chargeSql);

            sqlListBuilder.append(" group by r.subprjno,r.po_codesrm,r.po_lnsrm,r.status,r.plandate,r.check_update_date,r.updtime,r.status,r.pr_code,r.biz_type,r.itemcode,r.subneednum,r.usageqty,r.needtime,r.itemname,r.sup_code,r.sup_name,s.place_name");

            //3.??????
            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                OtdExcel allOtdReport = new OtdExcel();
                allOtdReport.setProNo(resultSetList.getString("subprjno"));
                allOtdReport.setItemCode(resultSetList.getString("itemcode"));
                allOtdReport.setItemName(resultSetList.getString("itemname"));
                allOtdReport.setSupCode(resultSetList.getString("sup_code"));
                allOtdReport.setSupName(resultSetList.getString("sup_name"));
                allOtdReport.setPoCode(resultSetList.getString("po_codesrm"));
                allOtdReport.setPoLn(resultSetList.getString("po_lnsrm"));
                allOtdReport.setPlanDate(resultSetList.getString("plandate"));
                allOtdReport.setCheckUpdateDate(resultSetList.getString("check_update_date"));
                allOtdReport.setReqDate(resultSetList.getString("needtime")==null?"":resultSetList.getString("needtime").substring(0,10));
                allOtdReport.setApsEndDate(resultSetList.getString("updtime"));
                allOtdReport.setApsEndFlag(resultSetList.getString("status"));
                allOtdReport.setBizType(resultSetList.getString("biz_Type"));
                allOtdReport.setPlaceName(resultSetList.getString("place_name"));
                allOtdReports.add(allOtdReport);
            }
        }catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
        } finally {
            return allOtdReports;
        }
    }

    public List<OtdExcel> getOtdWeekBasicInfoOfReqDate(String otdType,String date){
        List<OtdExcel> allOtdReports = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;

        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.set(Calendar.DAY_OF_MONTH,1);
        firstCalendar.add(Calendar.MONTH,0);
        String firstDay =  new SimpleDateFormat("yyyy-MM-dd").format(firstCalendar.getTime());

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.DAY_OF_MONTH,endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        endCalendar.add(Calendar.MONTH,0);
        String endDay =  new SimpleDateFormat("yyyy-MM-dd").format(endCalendar.getTime());

        String firstDate = getFisrtDayOfWeek(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));
        String endDate = getLastDayOfWeek(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));

        // ????????????????????????
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        String nextFirstDay =  new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        int year = Integer.parseInt(nextFirstDay.split("-")[0]);  //???
        int month = Integer.parseInt(nextFirstDay.split("-")[1]); //???
        Calendar cal = Calendar.getInstance();
        // ????????????
        cal.set(Calendar.YEAR, year);
        // ????????????
        // cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.MONTH, month); //??????????????????????????????
        // ????????????????????????
        //int lastDay = cal.getActualMaximum(Calendar.DATE);
        int lastDay = cal.getMinimum(Calendar.DATE); //??????????????????????????????????????????
        // ????????????????????????????????????
        //cal.set(Calendar.DAY_OF_MONTH, lastDay);
        cal.set(Calendar.DAY_OF_MONTH, lastDay - 1); //????????????????????????1???????????????????????????
        // ???????????????
        String planDateEndDate =  new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            //2.??????????????????sql
            @Cleanup Statement stmt = conn.createStatement();
            StringBuilder sqlListBuilder = new StringBuilder();

            // ?????????????????? ??? ??????????????? ??????
            sqlListBuilder.append("select r.subprjno,r.po_codesrm,r.po_lnsrm,r.status,r.plandate,r.needtime,r.check_update_date,r.updtime,r.status,r.pr_code,r.biz_type,r.itemcode,r.subneednum,r.usageqty,r.needtime,r.itemname,r.sup_code,r.sup_name,s.place_name from atwrpt.mv_srm_otd_rpt r left join atwsrm.atw_supplier s on (r.sup_code = s.code and s.primary_contact=1) where r.plandate >= to_date ( '2022-01-01', 'yyyy-MM-dd' )  and  r.sup_code is not null and r.needtime > =to_date('")
                .append(firstDate)
                .append("','yyyy-MM-dd') and r.needtime <=to_date('")
                .append(endDate)
                .append("','yyyy-MM-dd')")
                .append(" AND r.plandate <= to_date('")
                .append(planDateEndDate)
                .append("','yyyy-MM-dd')");

            String chargeSql = getChargeSql(otdType,firstDate);
            sqlListBuilder.append(chargeSql);

            sqlListBuilder.append(" group by r.subprjno,r.po_codesrm,r.po_lnsrm,r.status,r.plandate,r.check_update_date,r.updtime,r.status,r.pr_code,r.biz_type,r.itemcode,r.subneednum,r.usageqty,r.needtime,r.itemname,r.sup_code,r.sup_name,s.place_name");

            //3.??????
            @Cleanup ResultSet resultSetList = stmt.executeQuery(sqlListBuilder.toString());
            while (resultSetList.next()) {
                OtdExcel allOtdReport = new OtdExcel();
                allOtdReport.setProNo(resultSetList.getString("subprjno"));
                allOtdReport.setItemCode(resultSetList.getString("itemcode"));
                allOtdReport.setItemName(resultSetList.getString("itemname"));
                allOtdReport.setSupCode(resultSetList.getString("sup_code"));
                allOtdReport.setSupName(resultSetList.getString("sup_name"));
                allOtdReport.setPoCode(resultSetList.getString("po_codesrm"));
                allOtdReport.setPoLn(resultSetList.getString("po_lnsrm"));
                allOtdReport.setPlanDate(resultSetList.getString("plandate"));
                allOtdReport.setCheckUpdateDate(resultSetList.getString("check_update_date"));
                allOtdReport.setReqDate(resultSetList.getString("needtime")==null?"":resultSetList.getString("needtime").substring(0,10));
                allOtdReport.setApsEndDate(resultSetList.getString("updtime"));
                allOtdReport.setApsEndFlag(resultSetList.getString("status"));
                allOtdReport.setBizType(resultSetList.getString("biz_Type"));
                allOtdReport.setPlaceName(resultSetList.getString("place_name"));
                allOtdReports.add(allOtdReport);
            }
        } catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
        } finally {
            return allOtdReports;
        }
    }


    public String getFisrtDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //????????????
        cal.set(Calendar.YEAR, year);
        //????????????
        cal.set(Calendar.MONTH, month - 1);
        //????????????????????????
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //????????????????????????????????????
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //???????????????
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }

    public String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //????????????
        cal.set(Calendar.YEAR, year);
        //????????????
        cal.set(Calendar.MONTH, month - 1);
        //????????????????????????
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //????????????????????????????????????
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //???????????????
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayOfMonth = sdf.format(cal.getTime());
        return lastDayOfMonth;
    }

    public String getFisrtDayOfWeek(int year, int week) {
        Calendar cal = Calendar.getInstance();
        //????????????
        cal.set(Calendar.YEAR, year);
        //?????????
        cal.set(Calendar.WEEK_OF_YEAR, week);
        // ????????????????????????????????????
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        //???????????????
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayOfWeek = sdf.format(cal.getTime());
        return lastDayOfWeek;
    }

    public String getLastDayOfWeek(int year, int week) {
        Calendar cal = Calendar.getInstance();
        //????????????
        cal.set(Calendar.YEAR, year);
        //?????????
        cal.set(Calendar.WEEK_OF_YEAR, week);
        // ????????????????????????????????????
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        // ???????????????????????????????????????
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + 6);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.add(cal.DATE,1);
        //???????????????
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayOfWeek = sdf.format(cal.getTime());
        return lastDayOfWeek;
    }

    @Override
    public boolean updateMore(SupplierUpdateReq supplier) {
        String name = "";
        if(supplier.getPurchCode()!=null) {
            name = this.baseMapper.getName(supplier.getPurchCode());
            supplier.setPurchName(name);
        } else {
            name = this.baseMapper.getName(supplier.getPlaceCode());
            supplier.setPlaceName(name);
        }

        String supCodes = supplier.getSupCodes().substring(1,supplier.getSupCodes().length());
        supplier.setSupCodes(supCodes);

        return this.baseMapper.updateMore(supplier);
    }

    @Override
    public SupplierVO getDetails(Supplier supplier) {
        SupplierVO supplierVO = this.baseMapper.getDetails(supplier.getId().toString());
        return supplierVO;
    }

    public static void main(String[] args) {

        String getResult = HttpUtil
            .createGet("http://116.148.135.20:10860/?customerID=atw166141L")
            .execute()
            .charset("UTF-8")
            .body();

        SupplierProductData testEntity = JSON.toJavaObject(JSONObject.parseObject(getResult),SupplierProductData.class) ;
        System.out.println(testEntity);
    }


    @Override
    public R synProductData() {
        String getResult = HttpUtil
            .createGet("http://116.148.135.20:10860/?customerID=atw166141L")
            .execute()
            .charset("UTF-8")
            .body();

        SupplierProductData testEntity = JSON.toJavaObject(JSONObject.parseObject(getResult),SupplierProductData.class) ;
        List<SupplierProductDataList> orderItem = testEntity.getData();
        SupplierProductDataEntity supplierProductDataEntity=new SupplierProductDataEntity();
        supplierProductDataEntity.setSupCode("MAT-048");
        supplierProductDataEntity.setSupName("??????");
        for (SupplierProductDataList supplierProductDataList:orderItem) {
            BeanUtil.copy(supplierProductDataList,supplierProductDataEntity);
            List<SupplierProductDataEntity> supplierProductDataBySrcid = this.baseMapper.selectSupplierProductDataList(supplierProductDataEntity);
            if(supplierProductDataBySrcid.size()>0){
                //update
                supplierProductDataEntity.setUpdateTime(new Date());
                this.baseMapper.updateSupplierProductData(supplierProductDataEntity);
            }else {
                //insert
                supplierProductDataEntity.setCreateTime(new Date());
                this.baseMapper.insertSupplierProductData(supplierProductDataEntity);

            }

        }
        return R.success("??????");
    }

    @Override
    public IPage<SupplierProductDataEntity> getProductData(IPage<SupplierProductDataEntity> page, SupplierProductDataEntity supplierProductDataEntity) {

        return this.baseMapper.selectSupplierProductDataPage(page,supplierProductDataEntity);
    }
}




