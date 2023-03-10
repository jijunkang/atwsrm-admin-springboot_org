package org.springblade.modules.ap.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import io.swagger.models.auth.In;
import lombok.extern.java.Log;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.ap.dto.*;
import org.springblade.modules.ap.entity.ApRcvEntity;
import org.springblade.modules.ap.entity.ApRcvReqEntity;
import org.springblade.modules.ap.entity.ApReqSettle;
import org.springblade.modules.ap.mapper.ApRcvMapper;
import org.springblade.modules.ap.service.IApRcvService;
import org.springblade.modules.ap.service.IApService;
import org.springblade.modules.ap.vo.ApRcvVO;
import org.springblade.modules.ncr.entity.NcrEntity;
import org.springblade.modules.ncr.service.INcrService;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.pr.entity.ItemInfoEntityOfQZ;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static org.springblade.common.utils.ItemAnalysisUtil.getItemInfoOfFZ;
import static org.springblade.common.utils.ItemAnalysisUtil.getItemInfoOfQiuZuo;

/**
 *  ???????????????
 *
 * @author Will
 */
@Log
@Service
public class ApRcvServiceImpl extends BaseServiceImpl<ApRcvMapper, ApRcvEntity> implements IApRcvService {


    @Autowired
    IParamService paramService;
    @Autowired
    IApService apService;
    @Autowired
    @Lazy
    INcrService ncrService;

    @Override
    public QueryWrapper<ApRcvEntity> getQueryWrapper(ApReq apReq) {
        QueryWrapper<ApRcvEntity> queryWrapper = Condition.getQueryWrapper(new ApRcvEntity()).orderByDesc("rcv_code");
        if(!StringUtil.isEmpty(apReq.getSupName())){
            queryWrapper.like("sup_name", apReq.getSupName());
        }
        if(!StringUtil.isEmpty(apReq.getRcvCode())){
            queryWrapper.like("rcv_code", apReq.getRcvCode());
        }
        if(!StringUtil.isEmpty(apReq.getStatus()) && !apReq.getStatus().equals(STATUS_ALL)){
            queryWrapper.eq("status", apReq.getStatus());
        }
        queryWrapper.eq("type", "KK");
        return queryWrapper;
    }

    @Override
    public IPage<ApRcvEntity> getPage(IPage<ApRcvEntity> page, ApReq apReq) {
        IPage<ApRcvEntity> apRcvEntityIPage = this.baseMapper.getPage(page, apReq);

        // ????????????
        if(apReq.getItemName()!=null && apReq.getItemName().equals("???")) {
            apReq.setItemName("??????");
            List<ApRcvEntity> apRcvEntityList = this.getQTFZList(apReq,apRcvEntityIPage.getRecords());
            apRcvEntityIPage.setRecords(apRcvEntityList);
        }

        return apRcvEntityIPage;
    }


    private List<ApRcvEntity> getQTFZList(ApReq apReq ,List<ApRcvEntity> qtList) {
        List<ApRcvEntity> fzEntityList = this.baseMapper.getList(apReq);

        Map<String, ApRcvEntity> qtfzMap = new LinkedHashMap<>();
        Map<String, ApRcvEntity> fzMap = new LinkedHashMap<>();
        Map<String, ApRcvEntity> qtMap = new LinkedHashMap<>();

        // ??? ?????? List ?????? MAP
        for (ApRcvEntity item : fzEntityList) {
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfFZ(item.getItemName());
            String name1 = "??????"; // ??????
            String name2 = item.getItemName().split("-")[1]; // ??????
            String name3 = itemInfoEntity.getMaterial(); // ??????
            if ("Monel400".equals(name3)) {
                name3 = "MonelK500";  // 3???????????????MonelK500??????Monel400??????????????????
            }
            String name4 = itemInfoEntity.getFzCoat(); // ????????????
            String key = name1 + name2 + name3 + name4;
            fzMap.put(key, item);
        }


        // ??????????????????,???????????????
        boolean isExistQZ = true;
        int name6 = 0;
        for (ApRcvEntity dto : qtList) {
            if(!dto.getItemName().split("-")[0].equals("??????")) {
                String qzKey = "????????????" + name6;
                qtMap.put(qzKey, dto);
                name6++;
                continue;
            }
            // ??????
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(dto.getItemName());
            String name1 = "??????";
            String name2 = dto.getItemName().split("-")[1]; // ??????
            String name3 = itemInfoEntity.getMaterial(); // ??????
            String name4 = itemInfoEntity.getCoat(); // ????????????
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
        // ??????????????????????????????MAP?????????list
        Map<String, ApRcvEntity> sortedQzMap = qtMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (oldVal, newVal) -> oldVal,
            LinkedHashMap::new
        ));
        qtList = sortedQzMap.values().stream().collect(Collectors.toList());


        // ???????????? ?????? ??? ??????
        for (ApRcvEntity dto : qtList) {
            // ???????????????MAP?????????????????????
            String key = dto.getRcvCode() + dto.getRcvLn() + dto.getItemCode();
            qtfzMap.put(key, dto);

            // ???????????????????????????
            String itemName = dto.getItemName(); // ????????????
            if (itemName.split("-").length > 2 && itemName.split("-")[0].toString().equals("??????") && itemName.split("-")[1].indexOf("F") < 0 && isExistQZ) { // ????????????????????????????????????????????? F???
                Map<String, ApRcvEntity> matchFzMap = this.getMatchFzInfo(itemName, fzMap);
                if (!matchFzMap.isEmpty()) {
                    for (String tempKey : matchFzMap.keySet()) {
                        // ?????????????????????????????????
                        ApRcvEntity apRcvEntity = matchFzMap.get(tempKey);
                        // fz???key?????? prCode + prLn + itemCode
                        String fzKey = apRcvEntity.getRcvCode() + apRcvEntity.getRcvLn() + apRcvEntity.getItemCode();
                        // ?????????????????????&&?????????????????????????????????????????????????????????(????????????????????????????????????????????????????????????????????????)
                        if (qtfzMap.containsKey(fzKey)) {
                            qtfzMap.remove(fzKey);
                        }

                        // ??????????????????????????? ????????????
                        qtfzMap.put(fzKey, apRcvEntity);
                    }
                }
            }
        }
        return qtfzMap.values().stream().collect(Collectors.toList());
    }

    private Map<String, ApRcvEntity> getMatchFzInfo(String itemname, Map<String, ApRcvEntity> fzMap) {
        Map<String, ApRcvEntity> matchFzMap = new LinkedHashMap<>();
        // ??????
        ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(itemname);
        String name1 = "??????";
        String name2 = itemname.split("-")[1]; // ??????
        String name3 = itemInfoEntity.getMaterial(); // ??????
        String name4 = itemInfoEntity.getCoat(); // ????????????
        String name5 = "G50";// ????????????

        if("G20".equals(name4) ||"G14".equals(name4) ) {
            name4 = "G06";
        } else if("G06".equals(name4)) {
            name4 = "G05";
        } else {
            name4 = "";
        }
        // ???
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
    public IPage<ApRcvReqEntity> getVmiPage(IPage<ApRcvEntity> page, ApReq apReq) {

        IPage<ApRcvReqEntity> apRcvReqEntityIPage = this.baseMapper.getVmiPage(page, apReq);

        for(ApRcvReqEntity rcv :apRcvReqEntityIPage.getRecords()){
            rcv.setId(Long.valueOf(rcv.getReqId()));
            rcv.setRcvCode(rcv.getReqRcvCode());
            rcv.setRcvLn(rcv.getReqRcvLn()==null?null:Integer.valueOf(rcv.getReqRcvLn()));
            rcv.setPoCode(rcv.getReqPoCode());
            rcv.setPoLn(rcv.getReqPoLn()==null?null:Integer.valueOf(rcv.getReqPoLn()));
            rcv.setRcvActualQty(rcv.getReqRcvNum()==null?null:new BigDecimal(rcv.getReqRcvNum()));
            rcv.setVmiContract(rcv.getVmiContractNew());
            rcv.setVmiStatus(rcv.getVmiStatusNew());

            rcv.setAccumRecQty(new BigDecimal(rcv.getReqAccumRecQty()));
            rcv.setPrice(new BigDecimal(rcv.getTaxPricePo()).divide(rcv.getTaxRate().add(new BigDecimal("1")),6));
            rcv.setTaxPrice(new BigDecimal(rcv.getTaxPricePo()));

            if(rcv.getReqRcvNum()!=null && rcv.getTaxPrice()!=null) {
                //taxprice?????????????????????
                //BigDecimal taxprice= rcv.getTaxPrice().setScale(2, RoundingMode.HALF_UP);
                rcv.setTaxSubTotal(new BigDecimal(rcv.getReqRcvNum()).multiply(rcv.getTaxPrice()).setScale(2, RoundingMode.HALF_UP));
            }
        }

        return apRcvReqEntityIPage;

    }

    @Override
    @Transactional
    public R deleteVmiSettle(ApReq apReq) {
        log.info("/aprcv/deleteVmiSettle"+JSON.toJSONString(apReq));

        //??????atw_ap_req_settle ?????????????????????
        int accumCount = this.baseMapper.getAccumCount(apReq.getRcvCode());
        if (accumCount>0){
            return R.fail(apReq.getRcvCode()+"??????????????????????????????");
        }
        //????????????
        List<ApReqSettle> apReqSettleBySettleCode = this.baseMapper.getApReqSettleBySettleCode(apReq.getRcvCode());
        if(apReqSettleBySettleCode.size()<=0){
            return R.fail(apReq.getRcvCode()+"???????????????????????????");
        }

        for (ApReqSettle apReqSettle: apReqSettleBySettleCode) {

            String reqRcvNum = apReqSettle.getReqRcvNum();
            //req_rcv_code req_rcv_ln??????
            //ApRcvEntity apReqByCodeLn = this.baseMapper.getApReqByCodeLn(apReqSettle.getReqRcvCode(), apReqSettle.getReqRcvLn());
            this.baseMapper.updateApReqByCodeLn(apReqSettle.getReqRcvCode(), apReqSettle.getReqRcvLn(),reqRcvNum);

        }


        //??????atw_ap_req_settle????????????
        this.baseMapper.deleteSettleRcv(apReq.getRcvCode());

        //??????atw_ap_rcv???????????????
        this.baseMapper.deleteApRcv(apReq.getRcvCode());




        return R.success("??????");
    }

    @Override
    public int getListCount() {
        String userRoleId = paramService.getValue("purch_place.role_id");
        String deputyManagerRoleId = paramService.getValue("purch_deputy_manager.role_id");

        BladeUser user = AuthUtil.getUser();
        if (StringUtil.containsAny(user.getRoleId(), userRoleId)) {
            //?????????--?????????
            return apService.getCountByStatus(IApService.STATUS_WAITE);
        } else if (StringUtil.containsAny(user.getRoleId(), deputyManagerRoleId)) {
            //?????????--?????????
            return apService.getCountByStatus(IApService.STATUS_WAITE);
        } else {
            return 0;
        }
    }

    @Override
    public IPage<ApRcvEntity> kkList(IPage<ApRcvEntity> page, ApReq apReq) {
        QueryWrapper<ApRcvEntity> queryWrapper = getQueryWrapper(apReq);
        return page(page, queryWrapper);
    }

    @Override
    public int getKKListCount() {
        QueryWrapper<ApRcvEntity> queryWrapper = Condition.getQueryWrapper(new ApRcvEntity());
        String userRoleId = paramService.getValue("purch_place.role_id");
        String deputyManagerRoleId = paramService.getValue("purch_deputy_manager.role_id");
        BladeUser user = AuthUtil.getUser();
        if (StringUtil.containsAny(user.getRoleId(), userRoleId)) {
            //?????????--?????????????????????????????????
            queryWrapper.in("status", STATUS_INIT, STATUS_BACK, STATUS_AGREE );
        } else if (StringUtil.containsAny(user.getRoleId(), deputyManagerRoleId)) {
            //?????????--?????????
            queryWrapper.in("status", STATUS_WAITE);
        } else {
            return 0;
        }
        return count(queryWrapper);
    }

    @Override
    public List<ApRcvEntity> getList(ApReq apReq) {
        return this.baseMapper.getList(apReq);
    }

    @Override
    public List<ApRcvReqEntity> getVmiList(ApReq apReq) {
        return this.baseMapper.getVmiList(apReq);
    }

    @Override
    public String getProNoByApIdOrBillId(Long id, String type) {
        return this.baseMapper.getProNoByApId(id, type);
    }

    @Override
    public boolean audit(ApReq apReq) {
        ApRcvEntity apRcvEntity = getById(apReq.getId());
        apRcvEntity.setStatus(apReq.getStatus());
        if(!StringUtil.isEmpty(apReq.getBackReason())){
            apRcvEntity.setBackReason(apReq.getBackReason());
        }
        return updateById(apRcvEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean auditBatch(List<ApReq> apReqs) {
        apReqs.forEach(this::audit);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updates(ApReq apReq) {
        List<ApRcvVO> list = apReq.getApRcvVos();
        List<ApRcvEntity> apRcvEntityList = new ArrayList<>();
        list.forEach(temp->{
            ApRcvEntity apRcvEntity = getById(temp.getId());
            apRcvEntity.setStatus(STATUS_PASS);
            apRcvEntityList.add(apRcvEntity);
        });
        return saveBatch(apRcvEntityList);
    }

    @Override
    public List<Map<String, Object>> countList(ApReq apReq) {
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_INIT);
            put("title", "??????");
            put("count", countByStatus(STATUS_INIT, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_WAITE);
            put("title", "?????????");
            put("count", countByStatus(STATUS_WAITE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_BACK);
            put("title", "????????????");
            put("count", countByStatus(STATUS_BACK, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_BUCKLE);
            put("title", "?????????");
            put("count", countByStatus(STATUS_BUCKLE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_AGREE);
            put("title", "?????????");
            put("count", countByStatus(STATUS_AGREE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_PASS);
            put("title", "?????????");
            put("count", countByStatus(STATUS_PASS, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_ALL);
            put("title", "??????");
            put("count", list(getQueryWrapper(new ApReq())).size());
        }});
        return result;
    }

    @Override
    public void vmiExport(HttpServletResponse response,String selectionIds, ApReq apReq) {
        if (StringUtil.isNotBlank(selectionIds)) {
            apReq.setSelectionIds(selectionIds);
        }

        List<ApRcvReqEntity> list  = this.baseMapper.getVmiList(apReq);

        if (list == null) {
            throw new RuntimeException("????????????");
        } else {
            for (ApRcvReqEntity rcv : list) {
                rcv.setVmiStatus(rcv.getVmiStatusNew());
                rcv.setVmiContract(rcv.getVmiContractNew());
                rcv.setRcvCode(rcv.getReqRcvCode());
                rcv.setPoLn(rcv.getReqPoLn() == null ? null : Integer.valueOf(rcv.getReqPoLn()));
                rcv.setRcvLn(rcv.getReqRcvLn() == null ? null : Integer.valueOf(rcv.getReqRcvLn()));
                rcv.setPoCode(rcv.getReqPoCode());
                rcv.setRcvActualQty(rcv.getReqRcvNum() == null ? null : new BigDecimal(rcv.getReqRcvNum()));
                rcv.setTaxPrice(rcv.getTaxPrice() == null ? null : rcv.getTaxPrice().setScale(2, RoundingMode.HALF_UP));
                rcv.setAccumRecQty(new BigDecimal(rcv.getReqAccumRecQty()));

                rcv.setPrice(new BigDecimal(rcv.getTaxPricePo()).divide(rcv.getTaxRate().add(new BigDecimal("1")),6));
                rcv.setTaxPrice(new BigDecimal(rcv.getTaxPricePo()));

                if(rcv.getReqRcvNum()!=null && rcv.getTaxPrice()!=null) {
                    //taxprice?????????????????????
                    //BigDecimal taxprice= rcv.getTaxPrice().setScale(2, RoundingMode.HALF_UP);
                    rcv.setTaxSubTotal(new BigDecimal(rcv.getReqRcvNum()).multiply(rcv.getTaxPrice()).setScale(2, RoundingMode.HALF_UP));
                }
            }
            List<ApRcvExcelDTO> excelList = Lists.newArrayList();
            for (ApRcvEntity apRcvEntity : list) {
                ApRcvExcelDTO apRcvExcelDTO = BeanUtil.copy(apRcvEntity, ApRcvExcelDTO.class);
                excelList.add(apRcvExcelDTO);
            }
            ExcelUtils.defaultExport(excelList, ApRcvExcelDTO.class, "VMI???????????????" + DateUtil.formatDate(new Date()), response);
        }

    }

    @Override
    public void export(HttpServletResponse response, String selectionIds, ApReq apReq) {
        List<ApRcvEntity> list = new ArrayList<>();
        if (StringUtil.isNotBlank(selectionIds)) {
            String[] ids = selectionIds.split(",");
            list = baseMapper.selectByIdList(ids);
        } else {
            list = this.baseMapper.getList(apReq);
        }
        if (list == null) {
            throw new RuntimeException("????????????,????????????");
        }
        ArrayList<ApRcvExcelDTO> excel = Lists.newArrayList();
        for (ApRcvEntity apRcvEntity : list) {
            ApRcvExcelDTO apRcvExcelDTO = BeanUtil.copy(apRcvEntity, ApRcvExcelDTO.class);
            excel.add(apRcvExcelDTO);
        }
        ExcelUtils.defaultExport(excel, ApRcvExcelDTO.class, "???????????????" + DateUtil.formatDate(new Date()), response);
    }


    /**
     * ????????????????????????
     *
     * @param status String
     * @return int
     */
    private int countByStatus(Integer status, String supCode) {
        QueryWrapper<ApRcvEntity> queryWrapper = Wrappers.<ApRcvEntity>query().in("status", status);
        if (!StringUtil.isEmpty(supCode)) {
            queryWrapper.like("sup_code", supCode);
        }
        return count(queryWrapper);
    }

    /**
     * ?????????????????????DP+yyyy+mm+dd+???????????????
     * @return
     */
    @Override
    public
    String genCode(String type){
        Date start = WillDateUtil.getTodayStart();
        Date end   = WillDateUtil.getTodayEnd();
        int  seq   = this.baseMapper.getTodayCount(start, end,type) + 1;
        return "DP" + DateUtil.format(start, "yyyyMMdd") + String.format("%03d", seq);
    }

    @Override
    public List<NcrEntity> ncrDetail(ApRcvEntity aprcv) {
        return ncrService.queryByRcvCode(aprcv.getRcvCode());
    }

    @Override
    public void exportNcr(HttpServletResponse response, ApReq apReq) {
        List<ApRcvEntity> entities = list(getQueryWrapper(apReq));
        List<ApNcrExcelDTO> dtos = Lists.newArrayList();
        entities.forEach(entity ->{
            ApNcrExcelDTO dto = BeanUtil.copy(entity, ApNcrExcelDTO.class);
            dto.setApNcrItemExcelDTOS(getItemDto(entity));
            dtos.add(dto);
        });
        ExcelUtils.defaultExport(dtos, ApNcrExcelDTO.class, "??????????????????" + DateUtil.formatDate(new Date()), response);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(String ids) {
        List<Long> idList = Func.toLongList(ids);
        QueryWrapper<ApRcvEntity> queryWrapper = Condition.getQueryWrapper(new ApRcvEntity());
        queryWrapper.in("id", idList);
        list(queryWrapper).forEach(temp ->{
            //??????NCR???????????????????????????NCR
            if(temp.getIsNcr() == 1){
                ncrService.updateByRcvCode(temp.getRcvCode());
            }
        });
        return deleteLogic(idList);
    }

    /**
     * ??????NCR??????DTO
     *
     * @param entity ApRcvEntity
     * @return List
     */
    private List<ApNcrItemExcelDTO> getItemDto(ApRcvEntity entity){
        List<NcrEntity> ncrEntities = ncrService.queryByRcvCode(entity.getRcvCode());
        List<ApNcrItemExcelDTO> dtos = Lists.newArrayList();
        ncrEntities.forEach(temp ->{
            ApNcrItemExcelDTO dto = BeanUtil.copy(temp, ApNcrItemExcelDTO.class);
            dtos.add(dto);
        });
        return dtos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewContract(ApRcvDTO apRcvDTO) {
        List<String> ids = new ArrayList<>(Arrays.asList(apRcvDTO.getRcvIds().split(",")));
        String vmiStatus = apRcvDTO.getVmiStatus();

        ids.stream().forEach(id->{
            this.baseMapper.reviewVmiRcv(id,vmiStatus);
        });
        return true;
    }


    /**
     * VMI??????????????????
     * @throws Exception
     */
//    @Scheduled(cron = "0 10 18 ? * *")
//    @Transactional(rollbackFor = Exception.class)
    public void handleVmiHistory() throws Exception{
        // 1.?????????????????????????????? ??????RCV??????
        List<ApRcvEntity> apRcvEntityList = this.baseMapper.getSettleVmiList();

        // 2.??????????????? + ?????????????????????????????????  ??????????????????????????????????????? ??????????????????????????????????????????
        for(ApRcvEntity item: apRcvEntityList) {
            String supCode = item.getSupCode();
            String itemCode = item.getItemCode();

            // ???????????????????????????????????????????????????
            List<ApRcvEntity> reqList = this.baseMapper.getReqVmiList(supCode,itemCode);

            // ?????????????????????????????????
            Integer accumRecQty = item.getAccumRecQty().intValue();

            for (ApRcvEntity rcv : reqList) {
                // ???????????????????????????PO??????????????????????????? ??????????????????????????????????????????
                if(rcv.getRcvActualQty().intValue()<0){
                    this.baseMapper.updateReqRcv(rcv.getId().toString(), rcv.getRcvActualQty().toString());
                    this.baseMapper.updatePoItem(rcv.getPoCode(), rcv.getPoLn().toString(), Math.abs(rcv.getRcvActualQty().intValue()));
                    continue;
                }

                // ???????????????????????? ???????????????
                Integer reqNum = rcv.getRcvActualQty().intValue() - rcv.getAccumRecQty().intValue();

                if (accumRecQty >= reqNum) { // ??????  ???????????????????????????????????????????????? >= ???????????????
                    this.baseMapper.updateReqRcv(rcv.getId().toString(), reqNum.toString());
                    // ??????????????????????????????PO???VMI?????????????????????????????? ????????????
                    this.baseMapper.updatePoItem(rcv.getPoCode(), rcv.getPoLn().toString(), reqNum);

                    this.baseMapper.insertReqSettle(item.getRcvCode(),item.getRcvLn().toString(),rcv.getRcvCode(),rcv.getRcvLn().toString(),rcv.getPoCode(),rcv.getPoLn().toString(),reqNum.toString(),reqNum.toString());

                    // ??????????????????????????????
                    accumRecQty = accumRecQty - reqNum ;

                } else { // ??????  ???????????????????????????????????????????????? < ???????????????

                    this.baseMapper.updateReqRcv(rcv.getId().toString(), accumRecQty.toString());
                    // ??????????????????????????????PO???VMI?????????????????????????????? ????????????
                    this.baseMapper.updatePoItem(rcv.getPoCode(), rcv.getPoLn().toString(), accumRecQty);

                    this.baseMapper.insertReqSettle(item.getRcvCode(),item.getRcvLn().toString(),rcv.getRcvCode(),rcv.getRcvLn().toString(),rcv.getPoCode(),rcv.getPoLn().toString(),accumRecQty.toString(),reqNum.toString());

                    break; // ??????????????????????????????????????????
                }
            }
        }

        this.handleVmiNow();
    }

    public void handleVmiNow() throws Exception{
        // 1.???????????? ?????????????????? ??????RCV??????
        List<ApRcvEntity> apRcvEntityList = this.baseMapper.getSettleVmiListNow();

        // 2.??????????????? + ?????????????????????????????????  ??????????????????????????????????????? ??????????????????????????????????????????
        for(ApRcvEntity item: apRcvEntityList) {
            String supCode = item.getSupCode();
            String itemCode = item.getItemCode();

            // ???????????????????????????????????????????????????
            List<ApRcvEntity> reqList = this.baseMapper.getReqVmiList(supCode,itemCode);

            // ???????????? ??????????????????
            Integer rcvActualQty = item.getRcvActualQty().intValue();

            for (ApRcvEntity rcv : reqList) {
                // ???????????????????????????PO??????????????????????????? ??????????????????????????????????????????
                if(rcv.getRcvActualQty().intValue()<0){
                    this.baseMapper.updateReqRcv(rcv.getId().toString(), rcv.getRcvActualQty().toString());
                    this.baseMapper.updatePoItem(rcv.getPoCode(), rcv.getPoLn().toString(), Math.abs(rcv.getRcvActualQty().intValue()));
                    continue;
                }

                // ???????????????????????? ???????????????
                Integer reqNum = rcv.getRcvActualQty().intValue() - rcv.getAccumRecQty().intValue();

                if (rcvActualQty >= reqNum) { // ??????  ???????????????????????????????????????????????? >= ???????????????
                    this.baseMapper.updateReqRcv(rcv.getId().toString(), reqNum.toString());
                    // ??????????????????????????????PO???VMI?????????????????????????????? ????????????
                    this.baseMapper.updatePoItem(rcv.getPoCode(), rcv.getPoLn().toString(), reqNum);

                    this.baseMapper.insertReqSettle(item.getRcvCode(),item.getRcvLn().toString(),rcv.getRcvCode(),rcv.getRcvLn().toString(),rcv.getPoCode(),rcv.getPoLn().toString(),reqNum.toString(),"0");

                    // ??????????????????????????????
                    rcvActualQty = rcvActualQty - reqNum ;

                } else { // ??????  ???????????????????????????????????????????????? < ???????????????

                    this.baseMapper.updateReqRcv(rcv.getId().toString(), rcvActualQty.toString());
                    // ??????????????????????????????PO???VMI?????????????????????????????? ????????????
                    this.baseMapper.updatePoItem(rcv.getPoCode(), rcv.getPoLn().toString(), rcvActualQty);

                    this.baseMapper.insertReqSettle(item.getRcvCode(),item.getRcvLn().toString(),rcv.getRcvCode(),rcv.getRcvLn().toString(),rcv.getPoCode(),rcv.getPoLn().toString(),rcvActualQty.toString(),"0");
                    break; // ??????????????????????????????????????????
                }
            }
        }
    }


    @Override
    public List<ApRcvVO> getDetailOfVmi(String rcvIds) {

        List<ApRcvVO> apRcvVOS = this.baseMapper.getListOfVmi(rcvIds);

        for(ApRcvVO rcv :apRcvVOS){

            rcv.setVmiContract(rcv.getVmiContractNew());
            rcv.setVmiStatus(rcv.getVmiStatusNew());
            rcv.setPoCode(rcv.getReqPoCode());
            rcv.setPoLn(rcv.getReqPoLn()==null?null:Integer.valueOf(rcv.getReqRcvLn()));
            rcv.setRcvLn(rcv.getReqRcvLn()==null?null:Integer.valueOf(rcv.getReqRcvLn()));
            rcv.setRcvCode(rcv.getReqRcvCode());
            rcv.setRcvActualQty(rcv.getReqRcvNum()==null?null:new BigDecimal(rcv.getReqRcvNum()));
            rcv.setAccumRecQty(rcv.getReqAccumRecQty()==null?null:new BigDecimal(rcv.getReqAccumRecQty()));

            if(rcv.getReqRcvNum()!=null && rcv.getTaxPrice()!=null) {
                rcv.setTaxSubTotal(new BigDecimal(rcv.getReqRcvNum()).multiply(rcv.getTaxPrice()).setScale(2, RoundingMode.HALF_UP));
            }
        }

        return apRcvVOS;
    }

}
