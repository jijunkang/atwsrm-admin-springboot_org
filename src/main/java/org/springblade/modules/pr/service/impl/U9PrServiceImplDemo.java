//package org.springblade.modules.pr.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.google.api.client.util.Lists;
//import org.apache.commons.lang3.StringUtils;
//import org.springblade.common.dto.CheckDTO;
//import org.springblade.common.utils.ExcelUtils;
//import org.springblade.core.mp.base.BaseServiceImpl;
//import org.springblade.core.mp.support.Condition;
//import org.springblade.core.tool.utils.BeanUtil;
//import org.springblade.core.tool.utils.DateUtil;
//import org.springblade.core.tool.utils.StringUtil;
//import org.springblade.modules.item.entity.Item;
//import org.springblade.modules.item.service.IItemService;
//import org.springblade.modules.mathmodel.entity.MmVolumeCalculateEntity;
//import org.springblade.modules.mathmodel.service.IMmVolumeCalculateService;
//import org.springblade.modules.mathmodel.service.TubeMaterialService;
//import org.springblade.modules.po.entity.IoEntity;
//import org.springblade.modules.po.entity.PoItemEntity;
//import org.springblade.modules.po.service.IIoService;
//import org.springblade.modules.po.service.IPoItemService;
//import org.springblade.modules.pr.dto.*;
//import org.springblade.modules.pr.entity.*;
//import org.springblade.modules.pr.mapper.MaterialMailyVoMapper;
//import org.springblade.modules.pr.mapper.U9PrMapper;
//import org.springblade.modules.pr.service.IU9PrService;
//import org.springblade.modules.pr.vo.*;
//import org.springblade.modules.priceframe.entity.PriceFrameEntity;
//import org.springblade.modules.priceframe.service.IPriceFrameService;
//import org.springblade.modules.pricelib.entity.PriceLibEntity;
//import org.springblade.modules.pricelib.service.IPriceLibService;
//import org.springblade.modules.supplier.entity.Supplier;
//import org.springblade.modules.supplier.service.ISupplierService;
//import org.springblade.modules.system.service.IDictBizService;
//import org.springblade.modules.system.service.IParamService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.servlet.http.HttpServletResponse;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//import static java.util.Comparator.comparingLong;
//import static java.util.stream.Collectors.collectingAndThen;
//import static java.util.stream.Collectors.toCollection;
//import static org.springblade.common.utils.ItemAnalysisUtil.*;
//import static org.springblade.core.secure.utils.AuthUtil.getUser;
//import static org.springblade.modules.po.service.IPoItemService.SOURCE_HANG;
//
///**
// * ????????? ???????????????
// * @author Will
// */
//@Service
//public
//class U9PrServiceImplDemo extends BaseServiceImpl<U9PrMapper, U9PrEntity> implements IU9PrService{
//
//
//    @Autowired
//    @Lazy  //  @Lazy  ??????service ????????????
//     IIoService ioService;
//
//    @Autowired
//    IPriceLibService priceLibService;
//
//    @Autowired
//    ISupplierService supplierService;
//
//    @Autowired
//    @Lazy
//    IPoItemService poItemService;
//
//    @Autowired
//    @Lazy
//    IDictBizService dictBizService;
//
//    @Autowired
//    @Lazy
//    IPriceFrameService priceFrameService;
//
//    @Autowired
//    @Lazy
//    IParamService paramService;
//
//    @Autowired
//    @Lazy
//    IMmVolumeCalculateService mmVolumeCalculateService;
//
//    @Autowired
//    @Lazy
//    IItemService itemService;
//
//    @Autowired
//    @Lazy
//    ISupplierService iSupplierService;
//
//
//
//
//    private final Double PI = 3.14;
//
//    @Override
//    public
//    QueryWrapper<U9PrEntity> getQueryWrapper(U9PrEntity u9Pr) {
//
//        return Wrappers.<U9PrEntity>query()
//            .like(StringUtils.isNotBlank(u9Pr.getPrCode()), "pr_code", u9Pr.getPrCode())
//            .like(StringUtils.isNotBlank(u9Pr.getItemCode()), "item_code", u9Pr.getItemCode())
//            .like(StringUtils.isNotBlank(u9Pr.getItemName()), "item_name", u9Pr.getItemName())
//            .eq(StringUtils.isNotBlank(u9Pr.getPurchCode()), "purch_code", u9Pr.getPurchCode())
//            .like(StringUtils.isNotBlank(u9Pr.getPurchName()), "purch_name", u9Pr.getPurchName())
//            .eq(u9Pr.getStatus() != null, "status", u9Pr.getStatus())
//            .eq(StringUtils.isNotBlank(u9Pr.getMoNo()), "mo_no", u9Pr.getMoNo())
//            .like(StringUtils.isNotBlank(u9Pr.getLastSupName()), "last_sup_name", u9Pr.getLastSupName());
//    }
//
//
//    @Override
//    public
//    boolean submitPrice(SubmitPriceDTO dto) {
//        Supplier sup = supplierService.getByName(dto.getSupName());
//        if (sup == null) {
//            return false;
//        }
//        U9PrEntity pr = this.getById(dto.getId());
//        if(pr == null) {
//            throw new RuntimeException("?????????PR???" + "id???" + dto.getId());
//        }
//        IoEntity io = new IoEntity();
//
//        io.setPrId(pr.getId());
//        io.setPrLn(pr.getPrLn());
//        io.setPrCode(pr.getPrCode());
//        io.setItemCode(pr.getItemCode());
//        io.setItemName(pr.getItemName());
//        io.setPriceNum(pr.getPriceNum());
//        io.setPriceUom(pr.getPriceUom());
//        io.setTcNum(pr.getTcNum());
//        io.setTcUom(pr.getTcUom());
//        io.setReqDate(pr.getReqDate());
//        io.setStatus(IIoService.STATUS_WINBID_CHECK1);
//        io.setSupCode(sup.getCode());
//        io.setSupName(sup.getName());
//        //?????????????????????
//        io.setIsByWeight(dto.getIsByWeight());
//        io.setPromiseDate(dto.getPromiseDate().getTime()/1000 + 8 * 3600);
//        io.setIsPersent(dto.getIsPersent());
//        io.setQuotePrice(dto.getQuotePrice());
//        io.setQuoteDate(new Date().getTime() / 1000);
//        io.setRemark(dto.getRemark());
//        io.setSource(IIoService.SOURCE_PURCHSUBMIT);
//
//        if (dto.getAttachment() != null) {
//            io.setAttachment(dto.getAttachment());
//            pr.setAttachment(dto.getAttachment());
//        }
//
//
//        if (FLOW_SUBMIT.equals(dto.getType())) {
//            //??????
//            pr.setStatus(IU9PrService.STATUS_FLOW_SUBMIT);
//        } else if (WIN_BID.equals(dto.getType())){
//            //??????
//            pr.setStatus(IU9PrService.STATUS_WINBID);
//        } else {
//            // ???????????? ???????????????
//            pr.setStatus(IU9PrService.STATUS_WINBID);
//            String attachMent = this.baseMapper.selectAttachment(dto.getIoId());
//            if (StringUtils.isNotEmpty(attachMent)) {
//                io.setAttachment(attachMent);
//            }
//            this.baseMapper.deleteIo(dto.getIoId());
//        }
//
//        ioService.save(io);
//        if (!pr.getItemName().contains("??????")) {
//            pr.setIsNeedCheck(dto.getIsNeedCheck());
//        }
//        this.updateById(pr);
//
//        //????????????PR??????IO?????????  ???????????????????????????IO?????????ID???
//        List<IoEntity> ioEntities = ioService.getByPrId(pr.getId());
//        ioEntities.forEach(ioEntity -> {
//            if (!ioEntity.getId().equals(io.getId())) {
//                ioEntity.setRemark("???????????????????????????IO?????????ID???" + io.getId());
//                ioService.updateById(ioEntity);
//                ioService.removeById(ioEntity);
//            }
//        });
//
////        // ????????????????????????2021.6.30 ????????????????????????
////        if (dto.getIsIntoPriceLib() == 1) {
////            IoWinbidReq ioWinbid = new IoWinbidReq();
////            ioWinbid.setIoId(io.getId());
////            ioWinbid.setEffectiveDate(dto.getEffectiveDate());
////            ioWinbid.setExpirationDate(dto.getExpirationDate());
////            ioWinbid.setAttachment(dto.getAttachment());
////            priceLibService.ioToPriceLib(ioWinbid);
////        }
//
//        // ?????????????????????(???????????????????????????)[??????????????????????????????????????????]
//        if ((!FLOW_SUBMIT.equals(dto.getType())) && NOT_NEED_CHECK.equals(dto.getIsNeedCheck()) && (!pr.getItemName().contains("??????"))) {
//            CheckDTO checkDTO = new CheckDTO();
//            checkDTO.setId(dto.getId());
//            checkDTO.setIoId(io.getId());
//            checkDTO.setStatus(IU9PrService.STATUS_ORDER);
//            ioService.check2(checkDTO);
//        }
//
//        return true;
//    }
//
//    /**
//     * @return
//     */
//    @Override
//    public
//    IPage<U9PrDTO> selectPage(IPage<U9PrDTO> page, PrReq prReq){
//        IPage<U9PrDTO> retPage = baseMapper.selectWaitPageByReq(page, prReq);
//        if(retPage.getRecords() == null){
//            return retPage;
//        }
//        for(U9PrDTO dto : retPage.getRecords()){
//            dto.setHighestPrice(poItemService.getHighestPrice(dto.getItemCode()));
//            dto.setLowestPrice(poItemService.getLowestPrice(dto.getItemCode()));
//            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(),dto.getItemName());
//            if(poItemEntity!=null){
//                dto.setLastPrice(poItemEntity.getPrice());
//                dto.setLastSupName(poItemEntity.getSupName());
//            }
//
//            if(dto.getPiLastSupName()!=null && !dto.getPiLastSupName().isEmpty()) {
//                dto.setLastSupName(dto.getPiLastSupName());
//            }
//        }
//        return retPage;
//    }
//
//
//    @Override
//    public IPage<U9PrDTO> selectAllPrPage(IPage<U9PrDTO> page, PrReq prReq) {
//        IPage<U9PrDTO> retPage = baseMapper.selectAllPrPage(page, prReq);
//        if(retPage.getRecords() == null){
//            return retPage;
//        }
//        for(U9PrDTO dto : retPage.getRecords()){
//            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(),dto.getItemName());
//            if(poItemEntity!=null){
//                dto.setLastPrice(poItemEntity.getPrice());
//                dto.setLastSupName(poItemEntity.getSupName());
//            }
//
//            if(dto.getPiLastSupName()!=null && !dto.getPiLastSupName().isEmpty()) {
//                dto.setLastSupName(dto.getPiLastSupName());
//            }
//        }
//        return retPage;
//    }
//
//
//    /**
//     * ??????
//     */
//    @Override
//    public
//    void export(PrReq prReq, HttpServletResponse response){
//        QueryWrapper<U9PrEntity> queryWrapper = getQueryWrapper(prReq).ne("status", IU9PrService.STATUS_INIT);
//        if(StringUtils.isNotBlank(prReq.getCreateTimeStart())){
//            queryWrapper.ge("create_time", prReq.getCreateTimeStart());
//        }
//        if(StringUtils.isNotBlank(prReq.getCreateTimeEnd())){
//            queryWrapper.le("create_time", prReq.getCreateTimeEnd());
//        }
//        if(StringUtils.isNotBlank(prReq.getStatuss())){
//            queryWrapper.in("status", prReq.getStatusList());
//        }
//        List<U9PrEntity> dtoList = list(queryWrapper);
//
//        List<U9PrExcelDTO> excelList = Lists.newArrayList();
//        for(U9PrEntity dto : dtoList){
//            U9PrExcelDTO excelDTO = BeanUtil.copy(dto, U9PrExcelDTO.class);
//            // ??????????????????null?????????????????? proNo
//            if(dto.getProNo() ==null || dto.getProNo().isEmpty()){
//                dto.setProNo(dto.getApsProNo());
//            }
//            excelDTO.setFlowType(dictBizService.getValue("pr_flow_type", excelDTO.getFlowType()));
//            excelDTO.setStatusFmt(dictBizService.getValue("pr_status", excelDTO.getStatus() + ""));
//            excelDTO.setInquiryWayFmt(dictBizService.getValue("pr_inquiry_way", excelDTO.getInquiryWay()));
//            excelList.add(excelDTO);
//        }
//        ExcelUtils.defaultExport(excelList, U9PrExcelDTO.class, "?????????" + DateUtil.formatDate(new Date()), response);
//
//    }
//
//    @Override
//    public
//    IPage<Map<String, Object>> getPriceLib(IPage<PriceLibEntity> page, U9PrEntity u9pr){
//        U9PrEntity                u9PrEntity = getById(u9pr.getId());
//        IPage<PriceLibEntity>     totalPage  = priceLibService.getPriceLib(page, u9PrEntity);
//        List<Map<String, Object>> listVo     = Lists.newArrayList();
//        for(PriceLibEntity priceLibEntity : totalPage.getRecords()){
//            Map<String, Object> map = new HashMap<>();
//            map.put("id", String.valueOf(priceLibEntity.getId()));
//            map.put("type", "priceLib");
//            map.put("supCode", priceLibEntity.getSupCode());
//            map.put("supName", priceLibEntity.getSupName());
//            map.put("price", priceLibEntity.getPrice());
//            listVo.add(map);
//        }
//        IPage<Map<String, Object>> newPage = new Page<>(totalPage.getCurrent(), totalPage.getSize(), totalPage.getTotal());
//        return newPage.setRecords(listVo);
//    }
//
//    @Override
//    public
//    boolean createByHang(U9PrHangDTO u9PrHangDTO){
//        U9PrEntity   u9PrEntity   = getById(u9PrHangDTO.getPrId());
//        PoItemEntity poItemEntity = new PoItemEntity();
//        if("priceLib".equals(u9PrHangDTO.getType())){
//            PriceLibEntity priceLibEntity = priceLibService.getById(u9PrHangDTO.getId());
//            if(!StringUtil.isEmpty(priceLibEntity) && !StringUtil.isEmpty(u9PrEntity)){
//
//                poItemEntity.setItemCode(u9PrEntity.getItemCode());
//                poItemEntity.setItemName(u9PrEntity.getItemName());
//                poItemEntity.setPurchCode(u9PrEntity.getPurchCode());
//                poItemEntity.setPurchName(u9PrEntity.getPurchName());
//
//                poItemEntity.setSupCode(priceLibEntity.getSupCode());
//                poItemEntity.setSupName(priceLibEntity.getSupName());
//                poItemEntity.setPriceNum(u9PrEntity.getPriceNum());
//                poItemEntity.setPriceUom(u9PrEntity.getPriceUom());
//                poItemEntity.setPriceUomCode(u9PrEntity.getPriceUomCode());
//                poItemEntity.setTcNum(u9PrEntity.getPriceNum());
//                poItemEntity.setTcUom(u9PrEntity.getPriceUom());
//                poItemEntity.setTcUomCode(u9PrEntity.getPriceUomCode());
//                poItemEntity.setSupConfirmDate(u9PrHangDTO.getSupConfirmDate());
//                poItemEntity.setReqDate(u9PrEntity.getReqDate());
//                poItemEntity.setPrice(priceLibEntity.getPrice());
//                poItemEntity.setAmount(priceLibEntity.getPrice().multiply(u9PrEntity.getPriceNum()));
//                poItemEntity.setProGoodsNum(priceLibEntity.getLimitMin());
//                poItemEntity.setPrId(u9PrEntity.getId());
//                poItemEntity.setPrCode(u9PrEntity.getPrCode());
//                poItemEntity.setPrLn(u9PrEntity.getPrLn());
//
//                Supplier sup = supplierService.getByCode(priceLibEntity.getSupCode());
//                poItemEntity.setTaxRate(sup.getTaxRate());
//                Long nowTime = System.currentTimeMillis()/1000;
//                poItemEntity.setWinbidTime(nowTime);
//                poItemEntity.setRemark(u9PrHangDTO.getRemark());
//                poItemEntity.setAttachment(u9PrHangDTO.getAttachment());
//                poItemEntity.setSource(SOURCE_HANG);
//                poItemEntity.setSourceId(u9PrEntity.getId());
//                poItemEntity.setStatus(IPoItemService.STATUS_INIT);
//
//
//                u9PrEntity.setStatus(STATUS_WAIT);
//                u9PrEntity.setAttachment(u9PrHangDTO.getAttachment());
//                updateById(u9PrEntity);
//                return poItemService.save(poItemEntity);
//            }
//
//        }
//        return false;
//    }
//
//    @Override
//    public
//    IPage<U9PrEntity> getU9Page(IPage<U9PrEntity> page){
//        QueryWrapper<U9PrEntity> queryWrapper = Condition.getQueryWrapper(new U9PrEntity());
//        queryWrapper.eq("status", STATUS_INIT);
//        queryWrapper.eq("inquiry_way", INQUIRYWAY_HAVEPROTOCOL);
//        return page(page, queryWrapper);
//    }
//
//    @Override
//    public
//    int getPriceFrameCount(){
//        List<Map<String, Object>> countList = priceFrameService.countList();
//        int count = 0;
//        for (Map<String, Object> stringObjectMap : countList) {
//            count = count + Integer.parseInt(stringObjectMap.get("count").toString());
//        }
//        return count;
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public boolean submitBatch(SubmitPriceReq submitPriceReq) {
//        boolean result = false;
//        List<SubmitPriceDTO> items = submitPriceReq.getSubmitPriceDTOs();
//        for (SubmitPriceDTO item : items) {
//            result = submitPrice(item);
//        }
//        return result;
//    }
//
//    @Override
//    public List<Map<String, Object>> getInquiryCount(String purchType) {
//        List<Map<String, Object>> result = Lists.newArrayList();
//        result.add(new HashMap<String, Object>(3){{
//            put("status", STATUS_INQUIRY);
//            put("title", "?????????");
//            put("count", getInquiryCount(STATUS_INQUIRY.toString(),purchType, null));
//        }});
//        result.add(new HashMap<String, Object>(3){{
//            put("status", STATUS_QUOTED);
//            put("title", "?????????");
//            put("count", getInquiryCount(STATUS_QUOTED.toString(),purchType,  null));
//        }});
//        // ???????????????????????????????????????
//        if(PURCHASE_TYPE_NORMAL.equals(purchType)) {
//            result.add(new HashMap<String, Object>(3) {{
//                put("status", STATUS_WINBID);
//                put("title", "?????????");
//                put("count", getInquiryCount(STATUS_WINBID.toString(), purchType, null));
//            }});
//        }
//        result.add(new HashMap<String, Object>(3){{
//            put("status", STATUS_WAIT);
//            put("title", "?????????");
//            put("count", getInquiryCount(STATUS_WAIT.toString(),purchType,  1));
//        }});
//        return result;
//    }
//
//    @Override
//    public List<Map<String, Object>> getFlowCount(String purchType) {
//        List<Map<String, Object>> result = Lists.newArrayList();
//        result.add(new HashMap<String, Object>(3){{
//            put("status", STATUS_HANG + "," + STATUS_FLOW);
//            put("title", "?????????");
//            put("count", getFlowCount(STATUS_HANG + "," + STATUS_FLOW, purchType));
//        }});
//        result.add(new HashMap<String, Object>(3){{
//            put("status", STATUS_FLOW_SUBMIT);
//            put("title", "?????????");
//            put("count", getFlowCount(STATUS_FLOW_SUBMIT.toString(), purchType));
//        }});
//        result.add(new HashMap<String, Object>(3){{
//            put("status", STATUS_WINBID);
//            put("title", "?????????");
//            put("count", getFlowCount(STATUS_WINBID.toString(), purchType));
//        }});
//        result.add(new HashMap<String, Object>(3){{
//            put("status", STATUS_WAIT);
//            put("title", "?????????");
//            put("count", getFlowCount(STATUS_WAIT.toString(), purchType));
//        }});
//        // ???????????????????????????????????????tab
//        if(PURCHASE_TYPE_INNER.equals(purchType)) {
//            result.add(new HashMap<String, Object>(3){{
//                put("status", STATUS_FLOW_NOSUP);
//                put("title", "????????????");
//                put("count", getFlowCount(STATUS_FLOW_NOSUP.toString(), purchType));
//            }});
//        }
//        return result;
//    }
//
//
//
//    @Override
//    public IPage<U9PrDTO> getCheckPage(IPage<U9PrDTO> page, PrReq prReq) {
//        String mRoleId  = paramService.getValue("purch_manager.role_id");
//        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
//            prReq.setPurchCode(getUser().getAccount());
//        }
//        IPage<U9PrDTO> u9Page = this.baseMapper.selectPageByReq(page, prReq);
//        getWinIo(prReq, u9Page);
//        return u9Page;
//    }
//
//    @Override
//    public List<PriceVO> getPriceList(U9PrVO u9PrVO) {
//        List<PriceVO> voList = Lists.newArrayList();
//
//        PriceVO highestPrice = new PriceVO();
//        highestPrice.setType("???????????????");
//        highestPrice.setSupName("-");
//        highestPrice.setNumberInterval("-");
//        highestPrice.setPrice(poItemService.getHighestPrice(u9PrVO.getItemCode()));
//        voList.add(highestPrice);
//        PriceVO lowestPrice = new PriceVO();
//        lowestPrice.setType("???????????????");
//        lowestPrice.setSupName("-");
//        lowestPrice.setNumberInterval("-");
//        lowestPrice.setPrice(poItemService.getLowestPrice(u9PrVO.getItemCode()));
//        voList.add(lowestPrice);
//        PriceVO lastPrice = new PriceVO();
//        lastPrice.setType("?????????");
//        lastPrice.setSupName("-");
//        lastPrice.setNumberInterval("-");
//        PoItemEntity poItemEntity = poItemService.getLastPoInfos(u9PrVO.getItemCode(),u9PrVO.getItemName());
//        if(poItemEntity!=null){
//            lastPrice.setPrice(poItemEntity.getPrice());
//        }
//        voList.add(lastPrice);
//
//        //????????????
//        u9PrVO.getPriceNumList().forEach(entity ->{
//            //??????????????????????????????????????????????????????
//            List<PriceFrameEntity> priceFrameEntities = priceFrameService.getNumberInterval(u9PrVO.getItemCode(), new BigDecimal(entity));
//            if(priceFrameEntities.size() > 0){
//                priceFrameEntities.forEach(priceFrameEntity -> {
//                    PriceVO u9Price = new PriceVO();
//                    u9Price.setType("????????????");
//                    u9Price.setSupName(priceFrameEntity.getSupName());
//                    u9Price.setPrice(priceFrameEntity.getPrice());
//                    u9Price.setNumberInterval(priceFrameEntity.getLimitMin() + "~" + priceFrameEntity.getLimitMax());
//                    voList.add(u9Price);
//                });
//            }
//        });
//        //????????????
//        List<MmVolumeCalculateEntity> mmVolumeCalculateEntities = mmVolumeCalculateService.getByItemCode(u9PrVO.getItemCode());
//        if(mmVolumeCalculateEntities.size() > 0){
//            mmVolumeCalculateEntities.forEach(entity ->{
//                PriceVO u9Price = new PriceVO();
//                u9Price.setType("????????????");
//                u9Price.setSupName(entity.getSupName());
//                u9Price.setNumberInterval("-");
//                u9Price.setPrice(entity.getPrice());
//                voList.add(u9Price);
//            });
//        }
//
//        //??????
//        LinkedHashSet<PriceVO> hashSet = new LinkedHashSet<>(voList);
//        return new ArrayList<>(hashSet);
//    }
//
//    @Override
//    public void inquiryExport(PrReq prReq, HttpServletResponse response) {
//        List<U9PrDTO> list = this.baseMapper.selectListByReq(prReq);
//        List<U9PrInquiryExcelDTO> dtoList = Lists.newArrayList();
//        list.forEach(temp -> {
//            U9PrInquiryExcelDTO dto = BeanUtil.copy(temp, U9PrInquiryExcelDTO.class);
//            dtoList.add(dto);
//        });
//        ExcelUtils.defaultExport(dtoList, U9PrInquiryExcelDTO.class, "?????????" + DateUtil.formatDate(new Date()), response);
//    }
//
//    @Override
//    public void inquiryExportOfOthers(PrReq prReq, HttpServletResponse response) {
//        List<U9PrDTO> list = this.baseMapper.selectListByReqOfOthers(prReq);
//        List<U9PrInquiryExcelDTO> dtoList = Lists.newArrayList();
//        list.forEach(temp -> {
//            U9PrInquiryExcelDTO dto = BeanUtil.copy(temp, U9PrInquiryExcelDTO.class);
//            dtoList.add(dto);
//        });
//        ExcelUtils.defaultExport(dtoList, U9PrInquiryExcelDTO.class, "?????????" + DateUtil.formatDate(new Date()), response);
//    }
//
//    @Override
//    public void flowExport(PrReq prReq, HttpServletResponse response) {
//        List<U9PrDTO> list = new ArrayList<>();
//        if(prReq.getSource()!=null && !prReq.getSource().isEmpty()) {
//            list =this.baseMapper.selectFlowListToExport(prReq);
//        } else {
//            list = this.baseMapper.selectFlowPageList(prReq);
//
//        }
//        List<U9PrFlowExcelDTO> dtoList = Lists.newArrayList();
//        list.forEach(temp -> {
//            U9PrFlowExcelDTO dto = BeanUtil.copy(temp, U9PrFlowExcelDTO.class);
//            if(dto.getProNo() ==null || dto.getProNo().isEmpty()){
//                dto.setProNo(temp.getApsProNo());
//            }
//
//            if(temp.getQuotePrice()!=null){
//                dto.setQuotePrice(temp.getQuotePrice().setScale(BigDecimal.ROUND_HALF_UP,2).toString());
//            }
//
//            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(),dto.getItemName());
//            if(poItemEntity!=null){
//                dto.setLastPrice(poItemEntity.getPrice());
//                dto.setLastSupName(poItemEntity.getSupName());
//            }
//            if(dto.getPiLastSupName()!=null && !dto.getPiLastSupName().isEmpty()) {
//                dto.setLastSupName(dto.getPiLastSupName());
//            }
//            dtoList.add(dto);
//        });
//        ExcelUtils.defaultExport(dtoList, U9PrFlowExcelDTO.class, "?????????" + DateUtil.formatDate(new Date()), response);
//    }
//
//
//    @Override
//    public void flowExportOfOthers(PrReq prReq, HttpServletResponse response) {
//        List<U9PrDTO> list = this.baseMapper.selectFlowPageOfOthersForExcel(prReq);
//        List<U9PrFlowExcelDTO> dtoList = Lists.newArrayList();
//        list.forEach(temp -> {
//            U9PrFlowExcelDTO dto = BeanUtil.copy(temp, U9PrFlowExcelDTO.class);
//            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(),dto.getItemName());
//            if(poItemEntity!=null){
//                dto.setLastPrice(poItemEntity.getPrice());
//                dto.setLastSupName(poItemEntity.getSupName());
//            }
//
//            if(dto.getPiLastSupName()!=null && !dto.getPiLastSupName().isEmpty()) {
//                dto.setLastSupName(dto.getPiLastSupName());
//            }
//
//            List<IoEntity> ioEntities = this.baseMapper.selectPriceFromIo(temp.getId());
//            if(ioEntities.size()>0 && !prReq.getStatuss().equals("20,40")){
//                dto.setQuotePrice(ioEntities.get(0).getQuotePrice().setScale(2,BigDecimal.ROUND_DOWN).toString());
//                dto.setSupCode(ioEntities.get(0).getSupCode());
//                dto.setSupName(ioEntities.get(0).getSupName());
//            }
//            prReq.setId(temp.getId());
//            IoEntity ioEntity = ioService.getBySourceAndPrId(prReq);
//            if(ioEntity != null){
//                dto.setRemark(ioEntity.getRemark());
//            }
//            dtoList.add(dto);
//        });
//        ExcelUtils.defaultExport(dtoList, U9PrFlowExcelDTO.class, "?????????" + DateUtil.formatDate(new Date()), response);
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public boolean flowBatchExcel(SubmitPriceReq submitPriceReq) {
//        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();
//        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
//            Long id = submitPriceDTO.getId();
//            U9PrEntity u9PrEntity = getById(id);
//            if (u9PrEntity == null) {
//                throw new RuntimeException("?????????PR???" + "id???" + id);
//            }
//            if (FLOW_SUBMIT.equals(submitPriceDTO.getType())) {
//                String attachmentAndRemark = submitPriceDTO.getAttachment();
//                u9PrEntity.setAttachment(attachmentAndRemark);
//                updateById(u9PrEntity);
//            } else {
//                PrReq prReq = new PrReq();
//                prReq.setId(id);
//                prReq.setStatuss(STATUS_WINBID.toString());
//                IoEntity ioEntity = ioService.getBySourceAndPrId(prReq);
//                if (ioEntity == null) {
//                    throw new RuntimeException("?????????IO???" + "prId???" + id + "?????????" + STATUS_WINBID);
//                }
//                ioEntity.setAttachment(submitPriceDTO.getAttachment());
//                if (StringUtil.isNotBlank(submitPriceDTO.getRemark())) {
//                    ioEntity.setRemark(submitPriceDTO.getRemark());
//                }
//                u9PrEntity.setAttachment(submitPriceDTO.getAttachment());
//                updateById(u9PrEntity);
//                ioService.updateById(ioEntity);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public boolean flowBatchExcelOfSupItem(SubmitPriceReq submitPriceReq) {
//        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();
//        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
//            if(submitPriceDTO.getType().equals("prId")){
//                // ?????????????????????
//                List<IoEntity> ioEntities = ioService.getByPrId(submitPriceDTO.getId());
//                for(IoEntity ioEntity : ioEntities){
//                    ioEntity.setAttachment(submitPriceDTO.getAttachment());
//                    if (StringUtil.isNotBlank(submitPriceDTO.getRemark())) {
//                        ioEntity.setRemark(submitPriceDTO.getRemark());
//                    }
//                    ioService.updateById(ioEntity);
//                }
//            } else {
//                // ?????????????????????
//                IoEntity ioEntity = ioService.getById(submitPriceDTO.getIoId());
//                if (ioEntity == null) {
//                    throw new RuntimeException("?????????????????????IO???" + "Id???" + submitPriceDTO.getIoId());
//                }
//                ioEntity.setAttachment(submitPriceDTO.getAttachment());
//                if (StringUtil.isNotBlank(submitPriceDTO.getRemark())) {
//                    ioEntity.setRemark(submitPriceDTO.getRemark());
//                }
//                ioService.updateById(ioEntity);
//            }
//        }
//        return true;
//    }
//
//
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public boolean flowBatchAudit(SubmitPriceReq submitPriceReq) {
//        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();
//
//        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
//            Long id = submitPriceDTO.getId();
//            U9PrEntity u9PrEntity = getById(id);
//            if (u9PrEntity == null) {
//                throw new RuntimeException("?????????PR???" + "id???" + id);
//            }
//
//            u9PrEntity.setStatus(STATUS_WINBID);
//            updateById(u9PrEntity);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean flowAudit(IoEntity ioReq) {
//        IoEntity ioEntity = ioService.getById(ioReq.getId());
//        if( ioEntity == null){
//            throw new RuntimeException("?????????IO???" + ioReq.getId());
//        }
//        U9PrEntity u9PrEntity = getById(ioEntity.getPrId());
//        if(u9PrEntity == null){
//            throw new RuntimeException("?????????PR???" + ioEntity.getPrId());
//        }
//        ioService.updateById(ioEntity);
//        u9PrEntity.setStatus(STATUS_WINBID);
//        return updateById(u9PrEntity);
//    }
//
//    @Override
//    public IPage<U9PrDTO> inquiryPage(IPage<U9PrDTO> page, PrReq prReq) {
//        Integer codeCount = 0;
//        if(prReq.getItemCode() != null){
//            codeCount = prReq.getItemCode().split(",").length;
//        }
//        prReq.setItemCodeCount(codeCount);
//        IPage<U9PrDTO> retPage = this.baseMapper.selectInquiryPage(page, prReq);
//        if(retPage.getRecords() == null){
//            return retPage;
//        }
//        for(U9PrDTO dto : retPage.getRecords()){
//            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(),dto.getItemName());
//            if(poItemEntity!=null){
//                dto.setLastPrice(poItemEntity.getPrice());
//                dto.setLastSupName(poItemEntity.getSupName());
//            }
//            if(dto.getPiLastSupName()!=null && !dto.getPiLastSupName().isEmpty()) {
//                dto.setLastSupName(dto.getPiLastSupName());
//            }
//            // zlw add 20210426 NO.10
//            if((!dto.getQoNo().isEmpty()) && (!dto.getItemName().isEmpty())){
//                String supRemark = this.baseMapper.selectSupRemarks(dto.getQoNo(), dto.getItemName());
//                dto.setRequisitionRemark(supRemark);
//            }
//        }
//        return retPage;
//    }
//
//    @Override
//    public IPage<U9PrDTO> inquiryCheckPage(IPage<U9PrDTO> page, PrReq prReq) {
//        String mRoleId  = paramService.getValue("purch_manager.role_id");
//        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
//            prReq.setPurchCode(getUser().getAccount());
//        }
//        IPage<U9PrDTO> u9Page = this.baseMapper.selectInquiryCheckPage(page, prReq);
//        getWinIo(prReq, u9Page);
//        return u9Page;
//    }
//
//    @Override
//    public IPage<U9PrDTO> flowPage(IPage<U9PrDTO> page, PrReq prReq) {
//
//        if(prReq.getItemCode()!=null && !prReq.getItemCode().isEmpty() && prReq.getItemCode().indexOf(",") > -1){
//            prReq.setItemCodeCount(1);
//        }
//
//        if(prReq.getPrCode()!=null && !prReq.getPrCode().isEmpty() && prReq.getPrCode().indexOf(",") > -1){
//            prReq.setPrCodeCount(1);
//        }
//
//        if(prReq.getBizType()!=null && prReq.getBizType().toString().equals("2")){
//            prReq.setIsVmi("1");
//            prReq.setBizType(null);
//        }
//
//        IPage<U9PrDTO> retPage = baseMapper.selectFlowPage(page, prReq);
//
//        List<U9PrDTO> returnPage = new ArrayList<>();
//        if(retPage.getRecords() == null){
//            return retPage;
//        }
//        for(U9PrDTO dto : retPage.getRecords()){
//            // ?????????????????????U9???????????????????????????
//            dto.setPromiseDate(dto.getReqDate());
//            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(),dto.getItemName());
//            if(poItemEntity!=null) {
//                dto.setLastPrice(poItemEntity.getPrice());
//                dto.setLastSupName(poItemEntity.getSupName());
//            }
//
//
//            if(dto.getPiLastSupName()!=null && !dto.getPiLastSupName().isEmpty()) {
//                dto.setLastSupName(dto.getPiLastSupName());
//            }
//
//            // ??????????????????null?????????????????? proNo
//            if(dto.getProNo() ==null || dto.getProNo().isEmpty()){
//                dto.setProNo(dto.getApsProNo());
//            }
////            dto.setHighestPrice(poItemService.getHighestPrice(dto.getItemCode()));
////            dto.setLowestPrice(poItemService.getLowestPrice(dto.getItemCode()));
////            Item item = itemService.getByCode(dto.getItemCode());
////            if(item != null){
////                dto.setPurchMix(item.getPurchMix());
////                dto.setStockLowerLimit(item.getStockLowerLimit());
////            }
//        }
//
//        return retPage;
//    }
//
//    @Override
//    public IPage<U9PrDTO> flowPageOfOthers(IPage<U9PrDTO> page, PrReq prReq) {
//        String mRoleId  = paramService.getValue("purch_manager.role_id");
//        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
//            prReq.setPurchCode(getUser().getAccount());
//        } else {
//            prReq.setPurchCode(null);
//        }
//
//        IPage<U9PrDTO> retPage = baseMapper.selectFlowPageOfOthers(page, prReq);
//        if(retPage.getRecords() == null){
//            return retPage;
//        }
//        for(U9PrDTO dto : retPage.getRecords()){
//            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(),dto.getItemName());
//            if(poItemEntity!=null){
//                dto.setLastPrice(poItemEntity.getPrice());
//                dto.setLastSupName(poItemEntity.getSupName());
//            }
//            if(dto.getPiLastSupName()!=null && !dto.getPiLastSupName().isEmpty()) {
//                dto.setLastSupName(dto.getPiLastSupName());
//            }
//            dto.setStandardPrice(dto.getLaborCost().multiply(new BigDecimal("1.4")).add(dto.getMaterialCost()));
//        }
//         return retPage;
//    }
//
//    @Override
//    public IPage<U9PrDTO> flowCheckPage(IPage<U9PrDTO> page, PrReq prReq) {
//        String mRoleId  = paramService.getValue("purch_manager.role_id");
//        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
//            prReq.setPurchCode(getUser().getAccount());
//        }
//        IPage<U9PrDTO> u9Page = this.baseMapper.selectFlowCheckPage(page, prReq);
//        for(U9PrDTO dto : u9Page.getRecords()){
//            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(),dto.getItemName());
//            if(poItemEntity!=null){
//                dto.setLastPrice(poItemEntity.getPrice());
//                dto.setLastSupName(poItemEntity.getSupName());
//            }
//            if(dto.getPiLastSupName()!=null && !dto.getPiLastSupName().isEmpty()) {
//                dto.setLastSupName(dto.getPiLastSupName());
//            }
//        }
//
//        getWinIo(prReq, u9Page);
//        return u9Page;
//    }
//
//
//    @Override
//    public IPage<U9PrDTO> flowNoSupPage(IPage<U9PrDTO> page, PrReq prReq) {
//        // ???????????????????????????????????? ?????? ???????????? ???no_sup ?????? ???????????? ???
//        if((STATUS_FLOW_NOSUP.toString()).equals(prReq.getStatuss())){
//            prReq.setStatuss(STATUS_FLOW.toString());
//            prReq.setFlowType(FLOW_TYPE_NOSUP);
//        }
//        IPage<U9PrDTO> u9Page = this.baseMapper.selectPageWithoutIo(page, prReq);
//        getWinIo(prReq, u9Page);
//        return u9Page;
//    }
//
//    /**
//     * ???????????????IO
//     *[
//     * @param prReq PrReq
//     * @param u9Page IPage
//     */
//    private void getWinIo(PrReq prReq, IPage<U9PrDTO> u9Page) {
//        for (U9PrDTO record : u9Page.getRecords()) {
//            prReq.setId(record.getId());
//            IoEntity ioEntity = ioService.getBySourceAndPrId(prReq);
//            // zlw add 20210426 NO.10
//            if(!record.getQoNo().isEmpty() && !record.getItemName().isEmpty()){
//                String supRemark = this.baseMapper.selectSupRemarks(record.getQoNo(), record.getItemName());
//                if (supRemark != null) {
//                    ioEntity.setSupRemark(supRemark);
//                }
//            }
//
//            if (ioEntity != null) {
//                record.setIoId(ioEntity.getId());
//                record.setQuoteDate(ioEntity.getQuoteDate());
//                record.setPromiseDate(ioEntity.getPromiseDate());
//                record.setSupCode(ioEntity.getSupCode());
//                record.setSupName(ioEntity.getSupName());
//                record.setQuotePrice(ioEntity.getQuotePrice());
//                record.setSupRemark(ioEntity.getSupRemark());
//                record.setIsPersent(ioEntity.getIsPersent());
//                record.setReferencePrice(mmVolumeCalculateService.getPrice(ioEntity.getItemCode(), ioEntity.getSupCode()));
//                record.setRemark(ioEntity.getRemark());
//                record.setAttachment(ioEntity.getAttachment());
//
//                record.setHighestPrice(poItemService.getHighestPrice(record.getItemCode()));
//                record.setLowestPrice(poItemService.getLowestPrice(record.getItemCode()));
//                PoItemEntity poItemEntity = poItemService.getLastPoInfos(record.getItemCode(),record.getItemName());
//                if(poItemEntity!=null){
//                    record.setLastPrice(poItemEntity.getPrice());
//                    record.setLastSupName(poItemEntity.getSupName());
//                }
//
//            }
//        }
//    }
//
//    /**
//     * ??????????????????
//     *
//     * @param status String
//     * @param check Integer
//     * @return int
//     */
//    private int getInquiryCount(String status, String type, Integer check){
//        PrReq prReq = new PrReq();
//        prReq.setStatuss(status);
//        prReq.setPurchaseType(type);
//        String mRoleId  = paramService.getValue("purch_manager.role_id");
//        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
//            prReq.setPurchCode(getUser().getAccount());
//        }
//        prReq.setInquiryWays("'" + INQUIRYWAY_EXCLUSIVE + "'" + "," + "'" + INQUIRYWAY_COMPETE + "'");
//        if(status.equals(STATUS_INQUIRY.toString()) || status.equals(STATUS_QUOTED.toString())){
//            return this.baseMapper.selectInquiryList(prReq).size();
//        }
//        if(check != null){
//            prReq.setCheck(check);
//        }
//        prReq.setSource("quote");
//        return this.baseMapper.selectInquiryCheckList(prReq).size();
//    }
//
//    /**
//     * ??????????????????
//     *
//     * @param status String
//     * @return int
//     */
//    private int getFlowCount(String status, String type){
//        PrReq prReq = new PrReq();
//        if(!status.equals(STATUS_FLOW_NOSUP)){
//            prReq.setStatuss(status);
//        } else {
//            // ??????????????????
//            prReq.setStatuss(STATUS_FLOW.toString());
//            prReq.setFlowType("FLOW_TYPE_NOSUP");
//        }
//        prReq.setPurchaseType(type);
//        String mRoleId  = paramService.getValue("purch_manager.role_id");
//        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
//            prReq.setPurchCode(getUser().getAccount());
//        }
//
//        // inner
//        if(PURCHASE_TYPE_INNER.equals(type)) {
//            // ????????????
//            if(status.equals(STATUS_HANG + "," + STATUS_FLOW) || status.equals(STATUS_FLOW_SUBMIT.toString()) || status.equals(STATUS_WINBID.toString())){
//                int all = this.baseMapper.selectFlowPageList(prReq).size();
//                int noSup = this.baseMapper.selectPageWithoutIo(new Page<>(),prReq).getRecords().size();
//                return all - noSup;
//            }
//            // ?????????????????????
//            if(status.equals(STATUS_FLOW_SUBMIT.toString()) || status.equals(STATUS_WINBID.toString())){
//                return this.baseMapper.selectFlowPageList(prReq).size();
//            }
//            // ????????????
//            if(status.equals(STATUS_FLOW_NOSUP.toString())){
//                prReq.setStatuss("40");
//                prReq.setPurchCode(null);
//                return this.baseMapper.selectPageWithoutIo(new Page<>(),prReq).getRecords().size();
//            }
//            // ?????????
//            prReq.setSource("purch_submit");
//            return this.baseMapper.selectFlowPageOfOthers(new Page<>(),prReq).getRecords().size();
//
//        } else {
//            // normal
//            if(status.equals(STATUS_HANG + "," + STATUS_FLOW)){
//                return this.baseMapper.selectFlowPageList(prReq).size();
//            }
//            // ?????????
//            prReq.setSource("purch_submit");
//            return this.baseMapper.selectFlowCheckList(prReq).size();
//        }
//    }
//
//    /**
//     * getCount
//     *
//     * @param status String
//     * @param type String
//     * @return int
//     */
//    private int getCount(String status, String type){
//        PrReq prReq = new PrReq();
//        prReq.setStatuss(status);
//        String mRoleId  = paramService.getValue("purch_manager.role_id");
//        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
//            prReq.setPurchCode(getUser().getAccount());
//        }
//        if("inquiry".equals(type)){
//            prReq.setInquiryWays("'" + INQUIRYWAY_EXCLUSIVE + "'" + "," + "'" + INQUIRYWAY_COMPETE + "'");
//            if(status.equals(STATUS_INQUIRY.toString()) || status.equals(STATUS_QUOTED.toString())){
//                return this.baseMapper.selectWaitListByReq(prReq).size();
//            }
//            prReq.setSource(IIoService.SOURCE_QUOTE);
//            prReq.setIsFlow(0);
//        }else{
//            //???????????????
//            if(status.equals(STATUS_HANG + "," + STATUS_FLOW)){
//                return this.baseMapper.selectWaitListByReq(prReq).size();
//            }
//            prReq.setIsFlow(1);
//            prReq.setSource(IIoService.SOURCE_PURCHSUBMIT);
//        }
//        return this.baseMapper.selectListByReq(prReq).size();
//    }
//
//    /**
//     * @return
//     */
//    @Override
//    public
//    U9PrDTO getDtoById(Long id){
//        U9PrEntity entity = getById(id);
//        U9PrDTO    dto    = BeanUtil.copy(entity, U9PrDTO.class);
//
//        QueryWrapper<IoEntity> ioQw = Condition.getQueryWrapper(new IoEntity()).eq("pr_id", id);
//        dto.setIoTotal(ioService.count(ioQw));
//        ioQw.eq("status", IIoService.STATUS_QUOTED);
//        dto.setQuotedIoTotal(ioService.count(ioQw));
//        dto.setHighestPrice(poItemService.getHighestPrice(dto.getItemCode()));
//        dto.setLowestPrice(poItemService.getLowestPrice(dto.getItemCode()));
//        PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(),dto.getItemName());
//        if(poItemEntity!=null){
//            dto.setLastPrice(poItemEntity.getPrice());
//            dto.setLastSupName(poItemEntity.getSupName());
//        }
//        return dto;
//    }
//
//    /**
//     * ????????????????????????
//     * @return
//     */
//    @Override
//    public
//    int toProcessCount(){
//        String mRoleId  = paramService.getValue("purch_manager.role_id");
//        if(StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
//            PrReq prReq = new PrReq();
//            prReq.setStatuss(STATUS_WINBID.toString());
//            prReq.setInquiryWays("'" + INQUIRYWAY_EXCLUSIVE + "'" + "," + "'" + INQUIRYWAY_COMPETE + "'");
//            prReq.setSource(IIoService.SOURCE_QUOTE);
//            return this.baseMapper.selectInquiryCheckList(prReq).size();
//        }
//        QueryWrapper<U9PrEntity> query = Condition.getQueryWrapper(new U9PrEntity()).in("status", STATUS_INQUIRY, STATUS_QUOTED)
//                                                  .in("inquiry_way", INQUIRYWAY_EXCLUSIVE, INQUIRYWAY_COMPETE)
//                                                  .eq("purch_code", getUser().getAccount())
//                                                  .eq("purchase_type", PURCHASE_TYPE_NORMAL);
//        return count(query);
//    }
//
//    /**
//     * ????????????????????????-?????????
//     * @return
//     */
//    @Override
//    public
//    int toProcessCountOfOthers(){
//        String mRoleId  = paramService.getValue("purch_manager.role_id");
//        if(StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
//            PrReq prReq = new PrReq();
//            prReq.setStatuss(STATUS_WINBID.toString());
//            prReq.setInquiryWays("'" + INQUIRYWAY_EXCLUSIVE + "'" + "," + "'" + INQUIRYWAY_COMPETE + "'");
//            prReq.setSource(IIoService.SOURCE_QUOTE);
//            prReq.setPurchaseType(PURCHASE_TYPE_INNER);
//            return this.baseMapper.selectInquiryCheckList(prReq).size();
//        }
//        QueryWrapper<U9PrEntity> query = Condition.getQueryWrapper(new U9PrEntity()).in("status", STATUS_INQUIRY, STATUS_QUOTED)
//            .in("inquiry_way", INQUIRYWAY_EXCLUSIVE, INQUIRYWAY_COMPETE)
//            .eq("purch_code", getUser().getAccount())
//            .eq("purchase_type", PURCHASE_TYPE_INNER);
//        return count(query);
//    }
//
//    /**
//     * ????????????????????????
//     * @return
//     */
//    @Override
//    public
//    int flowCount(){
//        String mRoleId  = paramService.getValue("purch_manager.role_id");
//        if(StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
//            PrReq prReq = new PrReq();
//            prReq.setStatuss(STATUS_WINBID.toString());
//            prReq.setSource(IIoService.SOURCE_PURCHSUBMIT);
//            return this.baseMapper.selectFlowCheckList(prReq).size();
//        }
//        QueryWrapper<U9PrEntity> query = Condition.getQueryWrapper(new U9PrEntity()).in("status", STATUS_HANG, STATUS_FLOW)
//            .eq("purch_code", getUser().getAccount())
//            .eq("purchase_type", PURCHASE_TYPE_NORMAL);
//        return count(query);
//    }
//
//
//    /**
//     * ?????????????????????-?????????
//     * @return
//     */
//    @Override
//    public
//    int flowCountOfOthers(){
//        String mRoleId  = paramService.getValue("purch_manager.role_id");
//        if(StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
//            PrReq prReq = new PrReq();
//            prReq.setStatuss(STATUS_WINBID.toString());
//            prReq.setSource(IIoService.SOURCE_PURCHSUBMIT);
//            prReq.setPurchaseType(PURCHASE_TYPE_INNER);
//            return this.baseMapper.selectFlowCheckListOfOthers(prReq).size();
//        }
//
//        if(getUser().getAccount().equals("150323")){
//            PrReq prReq = new PrReq();
//            prReq.setStatuss(STATUS_HANG.toString() + "," + STATUS_FLOW.toString());
//            prReq.setSource(IIoService.SOURCE_PURCHSUBMIT);
//            prReq.setPurchaseType(PURCHASE_TYPE_INNER);
//            prReq.setPurchCode(getUser().getAccount());
//            int all = this.baseMapper.selectFlowPageList(prReq).size();
//            int noSup = this.baseMapper.selectPageWithoutIo(new Page<>(),prReq).getRecords().size();
//            prReq.setPurchCode(null);
//            int noSupOfAll = this.baseMapper.selectPageWithoutIo(new Page<>(),prReq).getRecords().size();
//            return all - noSup + noSupOfAll;
//        } else {
//            PrReq prReq = new PrReq();
//            prReq.setStatuss(STATUS_HANG.toString() + "," + STATUS_FLOW.toString());
//            prReq.setSource(IIoService.SOURCE_PURCHSUBMIT);
//            prReq.setPurchaseType(PURCHASE_TYPE_INNER);
//            prReq.setPurchCode(getUser().getAccount());
//            int all = this.baseMapper.selectFlowPageList(prReq).size();
//            int noSup = this.baseMapper.selectPageWithoutIo(new Page<>(),prReq).getRecords().size();
//            return all - noSup;
//        }
//    }
//
//
//    @Override
//    public
//    void letPrFlow(U9PrEntity pr, String flowType){
//        pr.setStatus(STATUS_FLOW);
//        pr.setFlowType(flowType);
//        updateById(pr);
//        //  ??????io??????
//        IoEntity upIo = new IoEntity();
//        upIo.setStatus(IIoService.STATUS_CLOSE);
//        IoEntity whereIo = new IoEntity();
//        whereIo.setPrId(pr.getId());
//        ioService.update(upIo, Condition.getQueryWrapper(whereIo));
//    }
//
//    @Override
//    public
//    boolean statusToFlow(Long prid){
//        U9PrEntity pr = getById(prid);
//        pr.setStatus(STATUS_FLOW);
//        pr.setFlowType(FLOW_TYPE_INQPRICE_REFUSE);
//
//        IoEntity upIo = new IoEntity();
//        upIo.setStatus(IIoService.STATUS_LOSEBID);
//
//        ioService.update(upIo, Wrappers.<IoEntity>update().eq("pr_code", pr.getPrCode()).eq("pr_ln", pr.getPrLn()));
//        return updateById(pr);
//    }
//
//    @Override
//    public
//    boolean updateBatch(List<U9PrEntity> prList){
//        for(U9PrEntity pr : prList){
//            U9PrEntity oldpr = getById(pr.getId());
//            if(oldpr == null){
//                continue;
//            }
//            if(!Objects.equals(oldpr.getEndUser(), pr.getEndUser())){
//                pr.setEndUserUpdateTimes(oldpr.getEndUserUpdateTimes() + 1);
//            }
//            updateById(pr);
//        }
//        return true;
//    }
//
//    @Scheduled(cron = "0 33 13 ? * *")
//    @Transactional(rollbackFor = Exception.class)
//    public void autoRetrieveRecord() {
//        System.out.println("*************** ???????????? ?????????????????? ***************");
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        Calendar calendar=new GregorianCalendar();
//        String endTime=sdf.format(calendar.getTime());
//        calendar.add(calendar.DATE,-2);//???????????????????????????.???????????????,??????????????????
//        String time = sdf.format(calendar.getTime());
//        List<U9PrDTO> u9PrDTOS = this.baseMapper.getOrderInfo(time);
//
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//        // ??????
//        List<ItemInfoDTO> itemInfoDTOSOfZhuJian = autoRetrieveOfZhuJian(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfZhuJian);
//        // ??????
//        List<ItemInfoDTO> itemInfoDTOSOfDuanJian = autoRetrieveOfDuanJian(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfDuanJian);
//        // ??????
//        List<ItemInfoDTO> itemInfoDTOSOfQiuZuo = autoRetrieveOfQiuZuo(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfQiuZuo);
//        // ????????????
//        List<ItemInfoDTO> itemInfoDTOSOfWW = autoRetrieveOfWW(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfWW);
//
//        itemInfoDTOS.forEach(item->{
//            if(this.baseMapper.autoItemIsExisted(item.getItemCode())==0){
//                this.baseMapper.insertAutoItem(item.getItemCode(),item.getItemName());
//            }
//        });
//
//        System.out.println("*************** ???????????? ?????????????????? ***************");
//    }
//
//    /**
//     * ???????????????????????????
//     * @param u9PrDTOS
//     * @return
//     * @Desc maily
//     */
//    @Override
//    public List<ItemInfoDTO> autoRetrieve(List<U9PrDTO> u9PrDTOS) {
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//
//        // ??????
//        List<ItemInfoDTO> itemInfoDTOSOfZhuJian = autoRetrieveOfZhuJian(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfZhuJian);
//
//        // ??????
//        List<ItemInfoDTO> itemInfoDTOSOfDuanJian = autoRetrieveOfDuanJian(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfDuanJian);
//
//        // ??????
//        List<ItemInfoDTO> itemInfoDTOSOfQiuZuo = autoRetrieveOfQiuZuo(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfQiuZuo);
//
//        // ????????????
//        List<ItemInfoDTO> itemInfoDTOSOfWW = autoRetrieveOfWW(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfWW);
//
//
//        // ?????????maily
//        List<ItemInfoDTO> itemInfoDTOGuanBangLiao = authRetriveOfGuanBangLiao(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOGuanBangLiao);
//
//        return itemInfoDTOS;
//    }
//
//    @Autowired
//    @Lazy
//    private MaterialMailyVoMapper  materialMailyVoMapper;
//
//
//    @Autowired
//    TubeMaterialService tubeMaterialService;
//
//
//    /**
//     * ?????????????????????????????????
//     * @param u9PrDTOS
//     * @return
//     */
//    public List<ItemInfoDTO> autoRetrieveOfQiuZuo(List<U9PrDTO> u9PrDTOS) {
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//
//        // ?????????????????? ?????? ???????????????
//        for (U9PrDTO u9Prs : u9PrDTOS ) {
//            // ??????????????????????????????
//            if(u9Prs.getItemName().indexOf("??????") < 0){
//                continue;
//            }
//            // ??? ??????1249???????????????&&pr???????????????
//            if(u9Prs.getItemCode().substring(0,4).equals("1249") && u9Prs.getBizType().toString().equals("1")){
//                continue;
//            }
//            // ??????
//            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(u9Prs.getItemName());
//            // ???????????????
//            if (itemInfoEntity.getItemize() != null) {
//
//                // ?????????????????????????????????????????????
//                List<ItemInfoEntityOfQZ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfQZ(itemInfoEntity);
//
//                String price = "";
//                String ptPrice = "";
//                String range = "";
//
//                // ????????????????????????
//                if (itemInfoEntitiesFromDataBase.size() > 0) {
//
//                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
//                    List<PriceVO> priceVOS = new ArrayList<>();
//
//                    // id?????????????????????????????????
//                    itemInfoDTO.setId(u9Prs.getId().toString());
//                    itemInfoDTO.setItemCode(u9Prs.getItemCode());
//                    itemInfoDTO.setItemName(u9Prs.getItemName());
//
//                    // ??????????????????????????????
//                    for (ItemInfoEntityOfQZ iteminfo : itemInfoEntitiesFromDataBase) {
//                        // ???????????????
//                        String qzCharge = iteminfo.getQzCharge();
//                        if(StringUtils.isEmpty(qzCharge)) {
//                            qzCharge = "0";
//                        }
//                        // ???????????????
//                        String fzCharge = iteminfo.getFzCharge();
//                        if(StringUtils.isEmpty(fzCharge)) {
//                            fzCharge = "0";
//                        }
//
//                        price = this.baseMapper.selectItemPriceOfQZ(iteminfo);
//
//                        if(Double.valueOf(iteminfo.getSize())>=0.5 && Double.valueOf(iteminfo.getSize())<=10 ) {
//                            range = "0.5????????????10???";
//                        } else {
//                            range = "?????????12???";
//                        }
//                        ptPrice = this.baseMapper.selectPtPriceOfQZ(iteminfo,range);
//
//                        // ??????????????????
//                        if (StringUtils.isNotEmpty(price) && StringUtils.isNotEmpty(ptPrice)) {
//                            PriceVO priceVO = new PriceVO();
//                            // ???????????????????????????????????????
//                            priceVO.setSupName(iteminfo.getSupName());
//                            priceVO.setSupCode(iteminfo.getSupCode());
//                            priceVO.setPriority(iteminfo.getPriority());
//
//                            // ???????????? ??????2????????? ???????????????*??????+????????????*????????????+??????????????????+ ???????????????*??????+?????????+??????????????????* 2
//                            BigDecimal qzPrice = new BigDecimal(iteminfo.getQzWeight().trim()).multiply(new BigDecimal(price.trim())).add(new BigDecimal(qzCharge.trim())).add(new BigDecimal(iteminfo.getQzSprayArea().trim()).multiply(new BigDecimal(ptPrice.trim())));
//                            BigDecimal fzPrice = new BigDecimal(iteminfo.getFzWeight().trim()).multiply(new BigDecimal(price.trim())).add(new BigDecimal(fzCharge.trim())).add(new BigDecimal(iteminfo.getFzSprayCharge().trim()));
//                            BigDecimal priceToReturn = qzPrice.add(fzPrice.multiply(new BigDecimal("2"))).setScale(0, RoundingMode.HALF_UP);
//                            priceVO.setPrice(priceToReturn);
//
//                            priceVOS.add(priceVO);
//                            itemInfoDTO.setSupAndPriceList(priceVOS);
//                        }
//                    }
//                    // ?????????????????????????????????????????????supType?????? 1
//                    if ((itemInfoEntitiesFromDataBase != null) && (itemInfoEntitiesFromDataBase.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                        itemInfoDTO.setSupColorType("1");
//                    } else {
//                        itemInfoDTO.setSupColorType("0");
//                    }
//
//                    // ??????
//                    if (itemInfoDTO.getSupAndPriceList().size()>0) {
//                        itemInfoDTOS.add(itemInfoDTO);
//                    }
//                }
//            }
//        }
//        return itemInfoDTOS;
//    }
//
//
//
//    /**
//     * ?????????????????????????????????
//     * @param u9PrDTOS
//     * @return
//     */
//    public List<ItemInfoDTO> autoRetrieveOfZhuJian(List<U9PrDTO> u9PrDTOS) {
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//
//        // ?????????????????? ???????????? ???????????????
//        for (U9PrDTO u9Prs : u9PrDTOS ) {
//
//            // ??????????????????????????????
//            if( u9Prs.getItemName().indexOf("??????") < 0){
//                continue;
//            }
//
//            // ??????
//            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(u9Prs.getItemName());
//
//            // ???????????????
//            if (itemInfoEntity.getSeries() != null) {
//
//                // ?????????????????????????????????????????????
//                List<ItemInfoEntityOfZDJ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfZDJ(itemInfoEntity);
//
//                String price = "";
//
//                // ????????????????????????
//                if (itemInfoEntitiesFromDataBase.size() > 0) {
//
//                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
//                    List<PriceVO> priceVOS = new ArrayList<>();
//
//                    // id?????????????????????????????????
//                    itemInfoDTO.setId(u9Prs.getId().toString());
//                    itemInfoDTO.setItemCode(u9Prs.getItemCode());
//                    itemInfoDTO.setItemName(u9Prs.getItemName());
//
//                    // ??????????????????????????????
//                    for (ItemInfoEntityOfZDJ iteminfo : itemInfoEntitiesFromDataBase) {
//                        String extraPrice = iteminfo.getCharge();
//                        if(StringUtils.isEmpty(extraPrice)) {
//                            extraPrice = "0";
//                        }
//                        // ??????????????? ?????? ???????????????????????????????????????????????????
//                        iteminfo.setMaterial(itemInfoEntity.getMaterial());
//
//                        price = this.baseMapper.selectItemPriceOfZDJ(iteminfo);
//                        // ??????????????????
//                        if (StringUtils.isNotEmpty(price)) {
//
//                            PriceVO priceVO = new PriceVO();
//                            // ???????????????????????????????????????
//                            priceVO.setSupName(iteminfo.getSupName());
//                            priceVO.setSupCode(iteminfo.getSupCode());
//                            priceVO.setPriority(iteminfo.getPriority());
//                            // ???????????? ??????2????????? ??????*??????+?????????
//                            BigDecimal priceToReturn = (new BigDecimal(price).multiply(new BigDecimal(iteminfo.getWeight())).add(new BigDecimal(extraPrice))).setScale(2, RoundingMode.HALF_UP);
//
//                            priceVO.setPrice(priceToReturn);
//                            priceVOS.add(priceVO);
//                            itemInfoDTO.setSupAndPriceList(priceVOS);
//                        }
//                    }
//
//                    // ?????????????????????????????????????????????supType?????? 1
//                    if ((itemInfoEntitiesFromDataBase!=null) &&(itemInfoEntitiesFromDataBase.size() > 1) &&  (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                        itemInfoDTO.setSupColorType("1");
//                    } else {
//                        itemInfoDTO.setSupColorType("0");
//                    }
//                    // ???????????????
//                    if (itemInfoDTO.getSupAndPriceList()!=null && itemInfoDTO.getSupAndPriceList().size()>0) {
//                        itemInfoDTOS.add(itemInfoDTO);
//                    }
//                }
//            }
//        }
//        return itemInfoDTOS;
//    }
//
//
//    /**
//     * ?????????????????????????????????
//     * @param u9PrDTOS
//     * @return
//     */
//    public List<ItemInfoDTO> autoRetrieveOfDuanJian(List<U9PrDTO> u9PrDTOS) {
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//
//        // ?????????????????? ?????? ???????????????
//        for (U9PrDTO u9Prs : u9PrDTOS ) {
//            // ??????????????????????????????
//            if(u9Prs.getItemName().indexOf("??????") < 0){
//                continue;
//            }
//            // ?????? & ??????
//            String standards = "";
//            String material = "";
//
//            // ??????
//            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(u9Prs.getItemName());
//            if (itemInfoEntity.getMaterial() == null) { // ???????????????????????????????????? ????????? ??????
//                itemInfoEntity = getMaterialItemInfoOfDuanJian(u9Prs.getItemName());
//                if(itemInfoEntity==null) { // ???????????????????????????????????????null
//                    return null;
//                }
//            }
//            // ?????? & ??????
//            standards = itemInfoEntity.getSize() + "-" + itemInfoEntity.getPound();
//            material = itemInfoEntity.getMaterial();
//
//            // ?????????????????????????????????
//            List<ItemInfoDTO> itemInfoDTOSOfOld = handleOfDJ(u9Prs,standards,material); // ????????????????????????????????????????????? ?????? / ?????? ???????????????
//            List<ItemInfoDTO> itemInfoDTOSOfNew = handleOfDJOfNew(u9Prs,standards,material); // ???????????? ?????????????????????????????? ?????? + ?????? ???????????????
//
//            // ??????????????????
//            if (itemInfoDTOSOfNew.size() > 0 && itemInfoDTOSOfOld.size()>0) {
//                itemInfoDTOSOfNew.get(0).getSupAndPriceList().stream().forEach(itemInfoDTO -> {
//                        itemInfoDTOSOfOld.get(0).getSupAndPriceList().add(itemInfoDTO);
//                });
//            } else if(itemInfoDTOSOfNew.size() > 0 && itemInfoDTOSOfOld.size()==0){
//                itemInfoDTOSOfOld.addAll(itemInfoDTOSOfNew);
//            }
//            if (itemInfoDTOSOfOld.size() > 0 && itemInfoDTOSOfOld.get(0).getSupAndPriceList().size() > 1) {
//                itemInfoDTOSOfOld.get(0).setSupColorType("1");
//            }
//
//            // ????????????????????????,????????????????????? ?????????????????????
//            if(itemInfoDTOSOfOld.size()>0){
//                this.handleDJReport(itemInfoDTOSOfOld,u9Prs);
//            }
//
//            itemInfoDTOS.addAll(itemInfoDTOSOfOld);
//        }
//        return itemInfoDTOS;
//    }
//
//    // ?????????????????????????????????
//    private void handleDJReport(List<ItemInfoDTO> itemInfoDTOSOfOld,U9PrDTO u9Prs) {
//        // ????????????????????????????????????????????????????????????????????????????????????
//        Integer count = this.baseMapper.itemCodeOfDJIsExisted(u9Prs.getItemCode());
//        if (count == 0) { // ?????????????????????????????????
//            itemInfoDTOSOfOld.get(0).getSupAndPriceList().stream().forEach(priceInfo -> {
//                String itemCode = u9Prs.getItemCode();
//                String itemName = u9Prs.getItemName();
//                String supCode = priceInfo.getSupCode();
//                String supName = priceInfo.getSupName();
//                String weight = priceInfo.getSingleWeight();
//                String materialPrice = priceInfo.getSinglePrice();
//                String price = priceInfo.getPrice().toString();
//                ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = this.baseMapper.selectBasicItemInfoOfDJ(u9Prs.getItemCode());
//                String bigOuterSize = "";
//                String bigHeightSize = "";
//                String smallOuterSize = "";
//                String totalHeightSize = "";
//                String innerSize = "";
//                String outerSize = "";
//                String heightSize = "";
//                String formulaType = itemInfoEntityBasOfDJ.getType();
//                String res = itemInfoEntityBasOfDJ.getRes();
//                if ("A".equals(formulaType)) { //???????????????
//                    bigOuterSize = itemInfoEntityBasOfDJ.getBigOuterSize();  // ????????????
//                    bigHeightSize = itemInfoEntityBasOfDJ.getBigHeightSize();  // ????????????
//                    smallOuterSize = itemInfoEntityBasOfDJ.getSmallOuterSize();// ????????????
//                    totalHeightSize = itemInfoEntityBasOfDJ.getTotalHeightSize(); // ?????????
//                    innerSize = itemInfoEntityBasOfDJ.getInnerSize();// ??????
//                } else { // ??????
//                    innerSize = itemInfoEntityBasOfDJ.getInnerSize();// ??????
//                    outerSize = itemInfoEntityBasOfDJ.getOuterSize();  // ??????
//                    heightSize = itemInfoEntityBasOfDJ.getHeightSize();  // ??????
//                }
//                String outerRemain = priceInfo.getOuterRemain();
//                String innerRemain = priceInfo.getInnerRemain();
//                String heightRemain = priceInfo.getHeightRemain();
//
//                if( getUser()!= null) {
//                    String account = getUser().getAccount();
//                    String time = DateUtil.format(new Date(), "yyyy-MM-dd");
//                    this.baseMapper.insertDjInfoReport(itemCode, itemName, supCode, supName, weight, materialPrice, price, bigOuterSize, bigHeightSize, smallOuterSize, totalHeightSize, innerSize, outerSize, heightSize, formulaType, res, heightRemain, outerRemain, innerRemain,account,time);
//                }
//            });
//        }
//    }
//
//
//    /**
//     * ??????????????????????????????????????????
//     * @param u9PrDTOS
//     * @return
//     */
//    public List<ItemInfoDTO> autoRetrieveOfWW(List<U9PrDTO> u9PrDTOS) {
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//
//        // ?????????????????? ???????????? ???????????????
//        for (U9PrDTO u9Prs : u9PrDTOS ) {
//            // ??????????????????????????????
//            if(!u9Prs.getBizType().toString().equals(WW)){
//                continue;
//            }
//            // ??????
//            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfQCWW(u9Prs.getItemName());
//            // ???????????????
//            if (itemInfoEntity.getSeries() != null) {
//                // ?????????????????????????????????????????????
//                List<ItemInfoEntityOfWW> itemInfoEntitiesWW = this.baseMapper.selectSupAndWeightOfQCWW(itemInfoEntity);
//                String price = "";
//
//                // ????????????????????????
//                if (itemInfoEntitiesWW.size() > 0) {
//
//                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
//                    List<PriceVO> priceVOS = new ArrayList<>();
//
//                    // id?????????????????????????????????
//                    itemInfoDTO.setId(u9Prs.getId().toString());
//                    itemInfoDTO.setItemCode(u9Prs.getItemCode());
//                    itemInfoDTO.setItemName(u9Prs.getItemName());
//
//                    // ??????????????????????????????
//                    for (ItemInfoEntityOfWW iteminfo : itemInfoEntitiesWW) {
//                        price = iteminfo.getPrice().toString();
//                        // ??????????????????
//                        if (StringUtils.isNotEmpty(price)) {
//                            PriceVO priceVO = new PriceVO();
//                            // ???????????????????????????????????????
//                            priceVO.setSupName(iteminfo.getSupName());
//                            priceVO.setSupCode(iteminfo.getSupCode());
//                            priceVO.setPriority(iteminfo.getPriority());
//                            // ???????????? ??????2????????? ??????*??????+?????????
//                            BigDecimal priceToReturn = (new BigDecimal(price));
//                            priceVO.setPrice(priceToReturn);
//                            priceVOS.add(priceVO);
//                            itemInfoDTO.setSupAndPriceList(priceVOS);
//                        }
//                    }
//
//                    // ?????????????????????????????????????????????supType?????? 1
//                    if ((itemInfoEntitiesWW!=null) &&(itemInfoEntitiesWW.size() > 1) &&  (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                        itemInfoDTO.setSupColorType("1");
//                    } else {
//                        itemInfoDTO.setSupColorType("0");
//                    }
//                    // ???????????????
//                    if (itemInfoDTO.getSupAndPriceList().size()>0) {
//                        itemInfoDTOS.add(itemInfoDTO);
//                    }
//                }
//            }
//        }
//        return itemInfoDTOS;
//    }
//
//
//    /**
//     * ??????PR????????????
//     *
//     * @param submitPriceReq
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean removePrList(SubmitPriceReq submitPriceReq) {
//        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();
//
//        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
//            this.baseMapper.updateU9Pr(submitPriceDTO.getId());
//            this.baseMapper.deleteIo(submitPriceDTO.getIoId());
//        }
//        return true;
//    }
//
//    /**
//     * ?????????????????????????????????????????????
//     *
//     * @param submitPriceReq
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean deleteDJInfo(SubmitPriceReq submitPriceReq) {
//        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();
//        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
//            this.baseMapper.deleteDjInfoBasic(submitPriceDTO.getItemCode());
//            String account = getUser().getAccount();
//            String time = DateUtil.format(new Date(), "yyyy-MM-dd");
//            this.baseMapper.deleteDjInfoReport(submitPriceDTO.getItemCode(),account,time);
//        }
//        return true;
//    }
//
//    /**
//     * ???????????????????????????dialog
//     * @param itemCode
//     * @param itemName
//     * @return
//     */
//    @Override
//    public List<ItemInfoOfZDJVO> getItemInfoOfZDJVO(String itemCode, String itemName) {
//        List<ItemInfoOfZDJVO> itemInfoOfCastVOS = new ArrayList<>();
//        ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(itemName);
//        // ???????????????
//        if (itemInfoEntity.getSeries() != null) {
//            // ?????????????????????????????????????????????
//            List<ItemInfoEntityOfZDJ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfZDJ(itemInfoEntity);
//
//            // ????????????????????????
//            if (itemInfoEntitiesFromDataBase.size() > 0) {
//                // ??????????????????????????????
//                for (ItemInfoEntityOfZDJ iteminfo : itemInfoEntitiesFromDataBase) {
//                    String extraPrice = iteminfo.getCharge();
//                    if(StringUtils.isEmpty(extraPrice)) {
//                        extraPrice = "0";
//                    }
//
//                    // ???????????????????????????????????? ??????
//                    iteminfo.setMaterial(itemInfoEntity.getMaterial());
//
//                    String price = this.baseMapper.selectItemPriceOfZDJ(iteminfo);
//                    // ??????????????????
//                    if (StringUtils.isNotEmpty(price)) {
//                        ItemInfoOfZDJVO itemInfoOfCastVO = new ItemInfoOfZDJVO();
//
//                        itemInfoOfCastVO.setItemCode(itemCode);
//                        itemInfoOfCastVO.setItemName(itemName);
//                        itemInfoOfCastVO.setSupCode(iteminfo.getSupCode());
//                        itemInfoOfCastVO.setSupName(iteminfo.getSupName());
//                        itemInfoOfCastVO.setWeight(iteminfo.getWeight());
//                        itemInfoOfCastVO.setPrice(price);
//                        itemInfoOfCastVO.setCharge(extraPrice);
//                        itemInfoOfCastVO.setQuotePrice((new BigDecimal(price).multiply(new BigDecimal(iteminfo.getWeight())).add(new BigDecimal(extraPrice))).setScale(2, RoundingMode.HALF_UP).toString());
//
//                        itemInfoOfCastVOS.add(itemInfoOfCastVO);
//                    }
//                }
//            }
//        }
//        return itemInfoOfCastVOS;
//    }
//
//    /**
//     * ???????????????????????????dialog??????????????????
//     * @param itemCode
//     * @param itemName
//     * @return
//     */
//    @Override
//    public List<ItemInfoOfZDJVO> getItemInfoOfWWVO(String itemCode, String itemName) {
//
//        List<ItemInfoOfZDJVO> itemInfoOfCastVOS = new ArrayList<>();
//        ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfQCWW(itemName);
//        // ???????????????
//        if (itemInfoEntity.getSeries() != null) {
//            // ?????????????????????????????????????????????
//            List<ItemInfoEntityOfWW> itemInfoEntitiesWW = this.baseMapper.selectSupAndWeightOfQCWW(itemInfoEntity);;
//
//            // ????????????????????????
//            if (itemInfoEntitiesWW.size() > 0) {
//                // ??????????????????????????????
//                for (ItemInfoEntityOfWW iteminfo : itemInfoEntitiesWW) {
//
//                    // ???????????????????????????????????? ??????
//                    iteminfo.setMaterial(itemInfoEntity.getMaterial());
//
//                    String price = iteminfo.getPrice().toString();
//                    // ??????????????????
//                    if (StringUtils.isNotEmpty(price)) {
//                        ItemInfoOfZDJVO itemInfoOfCastVO = new ItemInfoOfZDJVO();
//
//                        itemInfoOfCastVO.setItemCode(itemCode);
//                        itemInfoOfCastVO.setItemName(itemName);
//                        itemInfoOfCastVO.setSupCode(iteminfo.getSupCode());
//                        itemInfoOfCastVO.setSupName(iteminfo.getSupName());
//                        itemInfoOfCastVO.setPrice(price);
//                        itemInfoOfCastVOS.add(itemInfoOfCastVO);
//                    }
//                }
//            }
//        }
//        return itemInfoOfCastVOS;
//    }
//
//    /**
//     * ???????????????????????????dialog
//     * @param itemCode
//     * @param itemName
//     * @return
//     */
//    @Override
//    public List<ItemInfoOfQZVO> getItemInfoOfQZVO(String itemCode, String itemName) {
//        List<ItemInfoOfQZVO> itemInfoOfCastVOS = new ArrayList<>();
//        ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(itemName);
//        // ???????????????
//        if (itemInfoEntity.getItemize() != null) {
//            // ?????????????????????????????????????????????
//            List<ItemInfoEntityOfQZ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfQZ(itemInfoEntity);
//
//            // ????????????????????????
//            if (itemInfoEntitiesFromDataBase.size() > 0) {
//                // ??????????????????????????????
//                for (ItemInfoEntityOfQZ iteminfo : itemInfoEntitiesFromDataBase) {
//                    // ???????????????
//                    String qzCharge = iteminfo.getQzCharge();
//                    if(StringUtils.isEmpty(qzCharge)) {
//                        qzCharge = "0";
//                    }
//                    // ???????????????
//                    String fzCharge = iteminfo.getFzCharge();
//                    if(StringUtils.isEmpty(fzCharge)) {
//                        fzCharge = "0";
//                    }
//
//                    String price = this.baseMapper.selectItemPriceOfQZ(iteminfo);
//                    String range = "";
//                    if(Integer.valueOf(iteminfo.getSize())>=0.5 && Integer.valueOf(iteminfo.getSize())<=10 ) {
//                        range = "0.5????????????10???";
//                    } else {
//                        range = "?????????12???";
//                    }
//                    String ptPrice = this.baseMapper.selectPtPriceOfQZ(iteminfo,range);
//
//                    // ??????????????????
//                    if (StringUtils.isNotEmpty(price)) {
//                        ItemInfoOfQZVO itemInfoOfCastVO = new ItemInfoOfQZVO();
//
//                        // ???????????? ??????2????????? ???????????????*??????+??????????????????+ ???????????????*??????+??????????????????
//                        BigDecimal qzPrice = new BigDecimal(iteminfo.getQzWeight().trim()).multiply(new BigDecimal(price.trim())).add(new BigDecimal(qzCharge.trim())).add(new BigDecimal(iteminfo.getQzSprayArea().trim()).multiply(new BigDecimal(ptPrice.trim())));
//                        BigDecimal fzPrice = new BigDecimal(iteminfo.getFzWeight().trim()).multiply(new BigDecimal(price.trim())).add(new BigDecimal(fzCharge.trim())).add(new BigDecimal(iteminfo.getFzSprayCharge().trim()));
//                        BigDecimal priceToReturn = qzPrice.add(fzPrice.multiply(new BigDecimal("2"))).setScale(0, RoundingMode.HALF_UP);
//                        itemInfoOfCastVO.setItemCode(itemCode);
//                        itemInfoOfCastVO.setItemName(itemName);
//                        itemInfoOfCastVO.setSupCode(iteminfo.getSupCode());
//                        itemInfoOfCastVO.setSupName(iteminfo.getSupName());
//                        itemInfoOfCastVO.setFzCharge(iteminfo.getFzCharge());
//                        itemInfoOfCastVO.setFzWeight(iteminfo.getFzWeight());
//                        itemInfoOfCastVO.setQzCharge(iteminfo.getQzCharge());
//                        itemInfoOfCastVO.setQzWeight(iteminfo.getQzWeight());
//                        itemInfoOfCastVO.setPrice(price);
//                        itemInfoOfCastVO.setFzSprayCharge(iteminfo.getFzSprayCharge());
//                        itemInfoOfCastVO.setQzSprayArea(iteminfo.getQzSprayArea());
//                        itemInfoOfCastVO.setPtPrice(ptPrice);
//                        itemInfoOfCastVO.setQuotePrice(priceToReturn.toString());
//                        itemInfoOfCastVOS.add(itemInfoOfCastVO);
//                    }
//                }
//            }
//        }
//        return itemInfoOfCastVOS;
//    }
//
//    /**
//     * ????????????????????? - ??????
//     * @param SubmitPriceReq
//     * @return
//     */
//    @Override
//    public boolean saveBatchOfOthers(SubmitPriceReq SubmitPriceReq) {
//
//        List<SubmitPriceDTO> submitPriceDTOs = SubmitPriceReq.getSubmitPriceDTOs();
//
//        // ??????id?????????
//        List<SubmitPriceDTO> uniqueSubmitPriceDTOs = submitPriceDTOs.stream().collect(collectingAndThen(
//            toCollection(() -> new TreeSet<>(comparingLong(SubmitPriceDTO::getId))), ArrayList::new)
//        );
//
//        for(SubmitPriceDTO submitPriceDTO : uniqueSubmitPriceDTOs){
//
//            List<IoEntity> ios = ioService.getByPrId(submitPriceDTO.getId());
//
//            Supplier sup = supplierService.getByName(submitPriceDTO.getSupName());
//            if (sup == null) {
//                return false;
//            }
//
//            U9PrEntity pr = this.getById(submitPriceDTO.getId());
//            if(pr == null) {
//                throw new RuntimeException("?????????PR???" + "id???" + submitPriceDTO.getId());
//            }
//
//            for(IoEntity io : ios ){
//                io.setPrId(pr.getId());
//                io.setPrLn(pr.getPrLn());
//                io.setPrCode(pr.getPrCode());
//                io.setItemCode(pr.getItemCode());
//                io.setItemName(pr.getItemName());
//                io.setPriceNum(pr.getPriceNum());
//                io.setPriceUom(pr.getPriceUom());
//                io.setTcNum(pr.getTcNum());
//                io.setTcUom(pr.getTcUom());
//                io.setReqDate(pr.getReqDate());
//                //?????????????????????
//                io.setIsByWeight(submitPriceDTO.getIsByWeight());
//                io.setIsPersent(submitPriceDTO.getIsPersent());
//                io.setRemark(submitPriceDTO.getRemark());
//                io.setQuotePrice(submitPriceDTO.getQuotePrice());
//                io.setPromiseDate(submitPriceDTO.getPromiseDate().getTime()/1000 + 8 * 3600); //?????????datePicker?????????8???????????????
//                io.setSource(IIoService.SOURCE_PURCHSUBMIT);
//
//                if ("flowSubmit".equals(submitPriceDTO.getType())) {
//                    //??????????????????????????????
//                    pr.setStatus(IU9PrService.STATUS_FLOW_SUBMIT);
//                } else if ("winBid".equals(submitPriceDTO.getType())){
//                    //??????????????????????????????
//                    pr.setStatus(IU9PrService.STATUS_WINBID);
//                } else {
//                    // ???????????? ????????????????????????
//                    pr.setStatus(IU9PrService.STATUS_WINBID);
//                }
//                ioService.updateById(io);
//            }
//            updateById(pr);
//        }
//        return true;
//    }
//
//
//    /**
//     * ????????????????????? - ?????????
//     * @param SubmitPriceReq
//     * @return
//     */
//    @Override
//    public boolean saveBatchOfOthersForDialog(SubmitPriceReq SubmitPriceReq) {
//
//        List<SubmitPriceDTO> submitPriceDTOs = SubmitPriceReq.getSubmitPriceDTOs();
//
//        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
//
//            IoEntity io = ioService.getById(submitPriceDTO.getIoId());
//
//            Supplier sup = supplierService.getByName(submitPriceDTO.getSupName());
//            if (sup == null) {
//                return false;
//            }
//
//            U9PrEntity pr = this.getById(submitPriceDTO.getId());
//            if (pr == null) {
//                throw new RuntimeException("?????????PR???" + "id???" + submitPriceDTO.getId());
//            }
//
//            io.setPrId(pr.getId());
//            io.setPrLn(pr.getPrLn());
//            io.setPrCode(pr.getPrCode());
//            io.setItemCode(pr.getItemCode());
//            io.setItemName(pr.getItemName());
//            io.setPriceNum(pr.getPriceNum());
//            io.setPriceUom(pr.getPriceUom());
//            io.setTcNum(pr.getTcNum());
//            io.setTcUom(pr.getTcUom());
//            io.setReqDate(pr.getReqDate());
//            //?????????????????????
//            io.setIsByWeight(submitPriceDTO.getIsByWeight());
//            io.setIsPersent(submitPriceDTO.getIsPersent());
//            io.setRemark(submitPriceDTO.getRemark());
//            io.setQuotePrice(submitPriceDTO.getQuotePrice());
//            io.setPromiseDate(submitPriceDTO.getPromiseDate().getTime() / 1000 + 8 * 3600);
//            io.setSource(IIoService.SOURCE_PURCHSUBMIT);
//
//            if ("flowSubmit".equals(submitPriceDTO.getType())) {
//                //??????????????????????????????
//                pr.setStatus(IU9PrService.STATUS_FLOW_SUBMIT);
//            } else if ("winBid".equals(submitPriceDTO.getType())) {
//                Integer bidNum = this.baseMapper.countTheBid(submitPriceDTO.getId().toString());
//                if(bidNum !=  1 ){
//                    throw new RuntimeException("?????????????????????????????????????????????????????????????????????");
//                }
//                //??????????????????????????????
//                pr.setStatus(IU9PrService.STATUS_WINBID);
//            } else {
//                Integer bidNum = this.baseMapper.countTheBid(submitPriceDTO.getId().toString());
//                if(bidNum > 0 ){
//                    throw new RuntimeException("?????????????????????????????????????????????????????????????????????");
//                }
//                // ???????????? ????????????????????????
//                pr.setStatus(IU9PrService.STATUS_WINBID);
//            }
//            ioService.updateById(io);
//            updateById(pr);
//        }
//        return true;
//    }
//
//    /**
//     * ??????PR???????????? - ???????????????
//     *
//     * @param submitPriceReq
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean removePrListOfOthers(SubmitPriceReq submitPriceReq) {
//        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();
//
//        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
//            this.baseMapper.updateU9Pr(submitPriceDTO.getId());
//        }
//        return true;
//    }
//
//
//    /**
//     * ???????????? - ???????????????
//     *
//     * @param submitPriceReq
//     * @return
//     */
//    @Override
//    public boolean winTheBid(SubmitPriceReq submitPriceReq) {
//
//        Integer bidNum = this.baseMapper.countTheBid(submitPriceReq.getSubmitPriceDTOs().get(0).getId().toString());
//        if(bidNum > 0 ){
//            throw new RuntimeException("?????????????????????????????????");
//        }
//        return this.baseMapper.winTheBid(submitPriceReq.getSubmitPriceDTOs().get(0).getIoId().toString());
//    }
//
//    /**
//     * ???????????? - ???????????????
//     *
//     * @param submitPriceReq
//     * @return
//     */
//    @Override
//    public boolean cancelTheBid(SubmitPriceReq submitPriceReq) {
//        return this.baseMapper.cancelTheBid(submitPriceReq.getSubmitPriceDTOs().get(0).getIoId().toString());
//    }
//
//
//    /**
//     * ??????????????????
//     * @param submitPriceReq
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean moveToOthersOfNoSup(SubmitPriceReq submitPriceReq) {
//        List<U9PrDTO> u9PrDTOS = submitPriceReq.getU9PrDTOS();
//
//        for(U9PrDTO u9PrDTO : u9PrDTOS){
//            this.baseMapper.removeIo(u9PrDTO.getId());
//            this.baseMapper.updatePrToOthers(u9PrDTO.getId());
//        }
//        return true;
//    }
//
//    /**
//     * ???????????????????????????
//     * @param supItemOthers
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean addOtherInfos(SupItemOthers supItemOthers) {
//
//        List<String> supCodes = Arrays.asList(supItemOthers.getSupCode().split(","));
//
//        for (String supCode : supCodes){
//            Supplier sup = iSupplierService.getByCode(supCode);
//            if( sup == null){
//                throw  new RuntimeException("?????????????????????????????????????????????");
//            }
//            supItemOthers.setSupName(sup.getName());
//            // ?????????????????? ??????????????????
//            supItemOthers.setStatus(0);
//
//            supItemOthers.setSupCode(supCode);
//            this.baseMapper.addOtherInfos(supItemOthers);
//        }
//        return true;
//    }
//
//    /**
//     * ???????????????????????????
//     * @param page
//     * @param supItemOthers
//     * @return
//     */
//    @Override
//    public IPage<SupItemOthers> getOthersInfo(IPage<SupItemOthers> page, SubmitPriceReq submitPriceReq) {
//
//        String itemCode = submitPriceReq.getItemCode();
//        String mainCode = itemCode.substring(0,6);
//        String matQuilty = itemService.getByCode(itemCode).getMatQuality();
//
//        return this.baseMapper.getOthersInfo(page,mainCode,matQuilty);
//    }
//
//    /**
//     * ???????????????????????????
//     * @param supItemOthers
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean updateOtherInfos(SupItemOthers supItemOthers) {
//
//        String supCode = supItemOthers.getSupCode();
//
//        Supplier sup = iSupplierService.getByCode(supCode);
//        if( sup == null){
//            throw  new RuntimeException("?????????????????????????????????????????????");
//        }
//        supItemOthers.setSupName(sup.getName());
//
//        this.baseMapper.updateOtherInfos(supItemOthers);
//
//        return true;
//    }
//
//    /**
//     * ???????????????????????????
//     * @param supItemOthers
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean removeOtherInfos(SupItemOthers supItemOthers) {
//
//        String supCode = supItemOthers.getSupCode();
//
//        Supplier sup = iSupplierService.getByCode(supCode);
//        if( sup == null){
//            throw  new RuntimeException("?????????????????????????????????????????????");
//        }
//        supItemOthers.setSupName(sup.getName());
//
//        return this.baseMapper.removeOtherInfos(supItemOthers);
//    }
//
//
//    /**
//     * ???????????????
//     *
//     * @param SubmitPriceReq
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean sendAndInquiry(SubmitPriceReq submitPriceReq) {
//
//        U9PrEntity u9PrEntity = this.baseMapper.selectById(submitPriceReq.getSubmitPriceDTOs().get(0).getId());
//
//        Item item = itemService.getByCode(submitPriceReq.getSubmitPriceDTOs().get(0).getItemCode());
//        Integer addTime  = item.getQuoteLimit();
//        // ?????????????????????
//        Date today = new Date();
//        Calendar c = Calendar.getInstance();
//        c.setTime(today);
//        int weekday = c.get(Calendar.DAY_OF_WEEK);
//        Long endTime = 0L;
//        if(weekday == 5){
//            endTime = today.getTime()/1000 + addTime + 2*24*3600;
//        } else if(weekday == 6) {
//            endTime = today.getTime()/1000 + addTime + 24*3600;
//        } else {
//            endTime = today.getTime()/1000 + addTime;
//        }
//
//        List<SupItemOthers> supItemOthers = this.baseMapper.getOthersInfoList(item.getCode().substring(0,6),item.getMatQuality());
//
//        for(SupItemOthers supItems : supItemOthers) {
//            IoEntity ioEntity = new IoEntity();
//
//            ioEntity.setPrId(u9PrEntity.getId());
//            ioEntity.setPrCode(u9PrEntity.getPrCode());
//            ioEntity.setPrLn(u9PrEntity.getPrLn());
//            ioEntity.setItemCode(u9PrEntity.getItemCode());
//            ioEntity.setItemName(u9PrEntity.getItemName());
//            ioEntity.setSupCode(supItems.getSupCode());
//            ioEntity.setSupName(supItems.getSupName());
//            ioEntity.setQuoteEndtime(endTime);
//            ioEntity.setPriceNum(u9PrEntity.getPriceNum());
//            ioEntity.setPriceUom(u9PrEntity.getPriceUom());
//            ioEntity.setTcNum(u9PrEntity.getTcNum());
//            ioEntity.setTcUom(u9PrEntity.getTcUom());
//            ioEntity.setReqDate(u9PrEntity.getReqDate());
//            ioEntity.setStatus(ioService.STATUS_INIT);
//            ioEntity.setSource(ioService.SOURCE_QUOTE);
//
//            // ????????????IO
//            ioService.save(ioEntity);
//            // ?????????????????????????????????????????????
//            supItems.setStatus(1);
//            this.baseMapper.updateStatusOfOthers(supItems);
//        }
//
//        // ??????PR?????????
//        u9PrEntity.setStatus(STATUS_INQUIRY);
//        u9PrEntity.setInquiryWay(INQUIRYWAY_COMPETE);
//        saveOrUpdate(u9PrEntity);
//        return true;
//    }
//
//
//    /**
//     * ???????????? - ?????????
//     * @param prid
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean evaluateBidOfOthers(Long id) {
//        List<IoEntity> ioEntities = this.baseMapper.getIoListByPrId(id);
//        U9PrEntity pr = this.baseMapper.getPrById(id);
//
//        // ?????????
//        BigDecimal matPrice = new BigDecimal("0");
//        BigDecimal mat = this.baseMapper.getMaterialCostByItemCode(pr.getItemCode());
//        if(mat != null){
//            matPrice = mat;
//        } else {
//            throw new RuntimeException("?????????????????????????????????...");
//        }
//
//        // ?????????
//        BigDecimal laborPrice = new BigDecimal("0");
//        BigDecimal lab = this.baseMapper.getLaborCostByItemCode(pr.getItemCode());
//        if(lab != null){
//            laborPrice = lab;
//        }else {
//            throw new RuntimeException("?????????????????????????????????...");
//        }
//        pr.setMaterialCost(matPrice);
//        pr.setLaborCost(laborPrice);
//        saveOrUpdate(pr);
//
//        // ??????????????????
//        Long reqDate = pr.getReqDate();
//        // ??????????????????
//        BigDecimal standandPrice = laborPrice.multiply(new BigDecimal("1.4")).add(matPrice) ;
//
//        // ????????????
//        for(IoEntity ioEntity : ioEntities) {
//            // ????????????
//            BigDecimal quotoPrice = ioEntity.getQuotePrice();
//
//            // ????????????
//            Long quotoDate = ioEntity.getQuoteDate();
//
//            // ????????????
//            Long promiseDate = ioEntity.getPromiseDate();
//
//            // ????????????
//            if( quotoDate == null || quotoPrice == null){
//                this.baseMapper.updateEvaluateScore(new BigDecimal("0"),ioEntity.getId());
//            } else if ( promiseDate > reqDate ||quotoDate > reqDate || quotoPrice.compareTo(standandPrice) > 0) {
//                this.baseMapper.updateEvaluateScore(new BigDecimal("0"),ioEntity.getId());
//            } else {
//                // ???????????? ?????????????????? ??? ???????????? ?????? + 1
//                this.baseMapper.updateEvaluateScore(standandPrice.subtract(quotoPrice).add(new BigDecimal("1")),ioEntity.getId());
//            }
//        }
//
//        // ????????????????????????LIST
//        List<IoEntity> ioEntitiesAfterScore = this.baseMapper.getIoListByPrId(id);
//        // ???????????? ???????????????
//        List<IoEntity> bestIoEntities = new ArrayList<>();
//        IoEntity baseIo = ioEntitiesAfterScore.get(0);
//
//        for (IoEntity ioEntity : ioEntitiesAfterScore) {
//            BigDecimal baseScore = baseIo.getEvaluateScore();
//
//            // ????????????????????????
//            if (ioEntity.getEvaluateScore().compareTo(baseScore) > 0 ) {
//                // ????????????????????????????????????????????????????????????
//                bestIoEntities.clear();
//                bestIoEntities.add(ioEntity);
//                baseIo = ioEntity;
//            } else if (ioEntity.getEvaluateScore().compareTo(baseScore) == 0){
//                // ???????????????????????????
//                bestIoEntities.add(ioEntity);
//            }
//        }
//
//        // ??????????????????????????????????????????????????????????????????
//        if(bestIoEntities.size() == 1  && bestIoEntities.get(0).getEvaluateScore().compareTo(new BigDecimal("0")) == 0 ){
//            bestIoEntities.clear();
//        }
//
//        // ????????????????????????????????????IO??????????????? ????????????????????? ?????? ?????????????????????????????????????????????????????????????????????????????????
//        if (bestIoEntities.size() == 1 && bestIoEntities.get(0).getEvaluateScore().compareTo(new BigDecimal("0")) > 0) {
//            for (IoEntity ioEntity : ioEntitiesAfterScore) {
//                // ???????????????ID????????????
//                if(!ioEntity.getId().toString().equals(bestIoEntities.get(0).getId().toString())){
//                    this.baseMapper.setLoseTheBid(ioEntity);
//                } else {
//                    this.baseMapper.setWinTheBid(ioEntity.getId(), new Date().getTime()/1000);
//                }
//            }
//            this.baseMapper.setPrToWait(pr.getId());
//        }
//
//        // ?????????????????????????????? ,???????????????
//        if( bestIoEntities.size() == 0 ) {
//            this.baseMapper.setPrFlow(pr.getId());
//        }
//
//        // ?????????????????? 1 ????????????
//        if( bestIoEntities.size() > 1 ) {
//            this.baseMapper.setPrToChoose(pr.getId());
//        }
//
//        return true;
//    }
//
//
//    /**
//     * ???????????????????????????
//     *
//     * @param u9PrDTOS
//     * @return ???????????? itemcode
//     */
//    private List<ItemInfoDTO> authRetriveOfGuanBangLiao(List<U9PrDTO> u9PrDTOS) {
//        ArrayList<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//        System.out.println("*************** ???????????? ?????????????????? ?????????????????? ????????????????????? ***************");
//
//        List<MaterialMaliyVO> list = new ArrayList<>();
//        //??????
//        MaterialMaliyVO maliyVO = new MaterialMaliyVO();
//        //??????????????????????????? ???????????????
//        for (U9PrDTO u9Prs : u9PrDTOS) {
//            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
//            //??????????????????,???contiune ??????
//            if (u9Prs.getItemName().indexOf("???") < 0) {
//                continue;
//            }
////            tubeMaterialService.selectLists(u9Prs.getItemName(),u9Prs.getItemCode());
//            //??????????????? ?????????????????????
//            maliyVO = getItemInfoGuanBangLiao(u9Prs.getItemName());
//            //??????????????????
//            if (maliyVO.getItemName() != null) {
//                //?????????????????????????????????
//                List<MaterialMaliyVO> mailyVOList = materialMailyVoMapper.selectSupName(maliyVO);
//                //?????????????????????
//                if (mailyVOList.size() > 0) {
//                    List<PriceVO> priceVOS = null;
//                    for (int i = 0; i < mailyVOList.size(); i++) {
//                        //????????????????????????300MM
//                        if (maliyVO.getExternalDiameter() < 300) {
//                            MaterialMaliyVO material = materialMailyVoMapper.selectItemName(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//                            //????????????????????????,????????????????????????
//                            if (material != null) {
//                                //?????????     1234567
//                                MaterialMaliyVO materialList = getMaterialList(maliyVO, material.getExternalDiameter(), material.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                                if (materialList == null) {
//                                    System.out.println("?????????????????????");
//                                    continue;
//                                }
//                                maliyVO.setSupplierName(materialList.getSupplierName());
//                                maliyVO.setSupplierCode(materialList.getSupplierCode());
//                                maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
//                                maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
//                                maliyVO.setWeight(materialList.getWeight()); //??????
//                                System.out.println("???????????? = " + maliyVO.getTheMaterialPrice());
//                                System.out.println("????????? = " + maliyVO.getMaterialPrice());
//                                System.out.println("?????? = " + maliyVO.getWeight());
//                                //???????????????
//                                if (maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
//                                    System.out.println("?????????0");
//                                    continue;
//                                }
//
//                                //?????????
//                                MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
//                                if (sprayList.equals(null) ) {
//                                    System.out.println("??????????????????");
//                                    continue;
//                                }
//                                maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //????????????
//                                maliyVO.setSprayPrice(sprayList.getSprayPrice());//?????????
//                                System.out.println("???????????? = " + maliyVO.getCoatingPrice());
//                                System.out.println("????????? = " + maliyVO.getSprayPrice());
//                                //???????????????
//                                if (maliyVO.getSprayPrice().equals(new BigDecimal("0"))){
//                                    System.out.println("?????????0");
//                                    continue;
//                                }
//
//                                //?????????
//                                MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                                if (proscessList == null) {
//                                    System.out.println("???????????????????????????");
//                                    continue;
//                                }
//                                maliyVO.setProcessingFee(proscessList.getProcessingFee()); //?????????
//                                maliyVO.setPrice(proscessList.getPrice()); //?????????
//                                System.out.println("????????? = " + maliyVO.getProcessingFee());
//                                System.out.println("????????? = " + maliyVO.getPrice());
//                                if ( maliyVO.getProcessingFee().equals(new BigDecimal("0"))){
//                                    System.out.println("?????????0");
//                                    continue;
//                                }
//
//                                    //????????????   ????????????=?????????+?????????+?????????+?????????
//                                    BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                                    maliyVO.setUnitPrice(productPrice);
//                                    System.out.println("???????????? = " + productPrice);
//                                    //??????????????????????????????0,????????????????????????
////                                    if (maliyVO.getSprayPrice().equals(new BigDecimal("0")) || maliyVO.getProcessingFee().equals(new BigDecimal("0")) || maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
////                                        continue;
////                                    }
//                                    // ?????????????????????????????????????????????supType?????? 1
//                                    if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                                        itemInfoDTO.setSupColorType("1");
//                                    } else {
//                                        itemInfoDTO.setSupColorType("0");
//                                    }
//                                    // ???????????????
//                                    if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
//                                        itemInfoDTOS.add(itemInfoDTO);
//                                    }
//                                    priceVOS = new ArrayList<>();
//                                    PriceVO priceVO = new PriceVO();
//                                    priceVO.setPrice(productPrice); //??????
//                                    priceVO.setSupName(maliyVO.getSupplierName());  //???????????????
//                                    priceVO.setSupCode(maliyVO.getSupplierCode()); //???????????????
//                                    priceVO.setSupKey("1"); //??????????????????
//                                    priceVO.setType("??????"); //??????
//                                    priceVO.setSingleWeight(String.valueOf(maliyVO.getWeight()));
//                                    priceVO.setPriority("1");
//                                    priceVOS.add(priceVO);
//                                 } else {
//                                //????????????
//                                MaterialMaliyVO materials8 = materialMailyVoMapper.selectExter(maliyVO.getExternalDiameter());
//                                System.out.println("materials8 = " + materials8);
//                                //?????????????????????
//                                MaterialMaliyVO materials5 = materialMailyVoMapper.selectInner(materials8.getExternalDiameter(),maliyVO.getInternalDiamete());
//                                System.out.println("materials5 = " + materials5);
//                                //????????????,????????????+8  ??????-5 ????????????
//                                MaterialMaliyVO material8 = materialMailyVoMapper.selectSupName8(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//                                if (material8 != null) {
//                                    //?????????
//                                    MaterialMaliyVO materialList = getMaterialList(maliyVO, material8.getExternalDiameter(), material8.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                                    if (materialList == null) {
//                                        System.out.println("?????????????????????");
//                                        continue;
//                                    }
//                                    maliyVO.setSupplierName(materialList.getSupplierName());
//                                    maliyVO.setSupplierCode(materialList.getSupplierCode());
//                                    maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
//                                    maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
//                                    maliyVO.setWeight(materialList.getWeight()); //??????
//                                    System.out.println("???????????? = " + maliyVO.getTheMaterialPrice());
//                                    System.out.println("????????? = " + maliyVO.getMaterialPrice());
//                                    System.out.println("?????? = " + maliyVO.getWeight());
//                                    //???????????????
//                                    if (maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
//                                        System.out.println("?????????0");
//                                        continue;
//                                    }
//
//
//
//                                    //?????????
//                                    MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material8.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
//                                    if (sprayList.equals(null) ) {
//                                        System.out.println("??????????????????");
//                                        continue;
//                                    }
//                                    maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //????????????
//                                    maliyVO.setSprayPrice(sprayList.getSprayPrice());//?????????
//                                    System.out.println("???????????? = " + maliyVO.getCoatingPrice());
//                                    System.out.println("????????? = " + maliyVO.getSprayPrice());
//                                    //???????????????
//                                    if (maliyVO.getSprayPrice().equals(new BigDecimal("0"))){
//                                        System.out.println("?????????0");
//                                        continue;
//                                    }
//
//
//                                    //?????????
//                                    MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                                    if (proscessList == null) {
//                                        System.out.println("???????????????????????????");
//                                        continue;
//                                    }
//                                    maliyVO.setProcessingFee(proscessList.getProcessingFee()); //?????????
//                                    maliyVO.setPrice(proscessList.getPrice()); //?????????
//                                    System.out.println("????????? = " + maliyVO.getProcessingFee());
//                                    System.out.println("????????? = " + maliyVO.getPrice());
//                                    if ( maliyVO.getProcessingFee().equals(new BigDecimal("0"))){
//                                        System.out.println("?????????0");
//                                        continue;
//                                    }
//                    //panda
//                                        //????????????   ????????????=?????????+?????????+?????????+?????????
//                                        BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                                        maliyVO.setUnitPrice(productPrice);
//
////                                        ??????????????????????????????0,????????????????????????
////                                        if (!(maliyVO.getSprayPrice().equals(new BigDecimal("0")) || maliyVO.getProcessingFee().equals(new BigDecimal("0")) || maliyVO.getMaterialPrice().equals(new BigDecimal("0")))){
////                                            continue;
////                                        }
//                                        // ?????????????????????????????????????????????supType?????? 1
//                                        if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                                            itemInfoDTO.setSupColorType("1");
//                                        } else {
//                                            itemInfoDTO.setSupColorType("0");
//                                        }
//                                        // ???????????????
//                                        if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
//                                            itemInfoDTOS.add(itemInfoDTO);
//                                        }
//                                        priceVOS = new ArrayList<>();
//                                        PriceVO priceVO = new PriceVO();
//                                        priceVO.setPrice(productPrice); //??????
//                                        priceVO.setSupName(maliyVO.getSupplierName());  //???????????????
//                                        priceVO.setSupCode(maliyVO.getSupplierCode()); //???????????????
//                                        priceVO.setSupKey("1"); //??????????????????
//                                        priceVO.setType("??????"); //??????
//                                        priceVO.setSingleWeight(String.valueOf(maliyVO.getWeight()));
//                                        priceVO.setPriority("1");
//                                        priceVOS.add(priceVO);
//                                    } else {
//                                    MaterialMaliyVO material10 = materialMailyVoMapper.selectSupName10(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
////                                    if (material10 != null) {
////                                        //?????????
////                                        MaterialMaliyVO materialList = getMaterialList(maliyVO, material10.getExternalDiameter(), material10.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
////                                        if (materialList == null) {
////                                            System.out.println("?????????????????????");
////                                            continue;
////                                        }
////                                        maliyVO.setSupplierName(materialList.getSupplierName());
////                                        maliyVO.setSupplierCode(materialList.getSupplierCode());
////                                        maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
////                                        maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
////                                        maliyVO.setWeight(materialList.getWeight()); //??????
////                                        System.out.println("???????????? = " + maliyVO.getTheMaterialPrice());
////                                        System.out.println("????????? = " + maliyVO.getMaterialPrice());
////                                        System.out.println("?????? = " + maliyVO.getWeight());
////                                        //???????????????
////                                        if (maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
////                                            System.out.println("?????????0");
////                                            continue;
////                                        }
////
////                                        //?????????
////                                        MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material10.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), material10.getSupplierCode());
////                                        if (sprayList.equals(null) ) {
////                                            System.out.println("??????????????????");
////                                            continue;
////                                        }
////                                        maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //????????????
////                                        maliyVO.setSprayPrice(sprayList.getSprayPrice());//?????????
////                                        System.out.println("???????????? = " + maliyVO.getCoatingPrice());
////                                        System.out.println("????????? = " + maliyVO.getSprayPrice());
////                                        //???????????????
////                                        if (maliyVO.getSprayPrice().equals(new BigDecimal("0"))){
////                                            System.out.println("?????????0");
////                                            continue;
////                                        }
////
////                                        //?????????
////                                        MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
////                                        if (proscessList == null) {
////                                            System.out.println("???????????????????????????");
////                                            continue;
////                                        }
////                                        maliyVO.setProcessingFee(proscessList.getProcessingFee()); //?????????
////                                        maliyVO.setPrice(proscessList.getPrice()); //?????????
////                                        System.out.println("????????? = " + maliyVO.getProcessingFee());
////                                        System.out.println("????????? = " + maliyVO.getPrice());
////                                        if ( maliyVO.getProcessingFee().equals(new BigDecimal("0"))){
////                                            System.out.println("?????????0");
////                                            continue;
////                                        }
////
////                                            //????????????   ????????????=?????????+?????????+?????????+?????????
////                                            BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
////                                            maliyVO.setUnitPrice(productPrice);
////                                            System.out.println("???????????? = " + productPrice);
////
////                                            //??????????????????????????????0,????????????????????????
//////                                            if (maliyVO.getSprayPrice().equals(new BigDecimal("0")) || maliyVO.getProcessingFee().equals(new BigDecimal("0")) || maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
//////                                                continue;
//////                                            }
////
////                                            // ?????????????????????????????????????????????supType?????? 1
////                                            if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
////                                                itemInfoDTO.setSupColorType("1");
////                                            } else {
////                                                itemInfoDTO.setSupColorType("0");
////                                            }
////                                            // ???????????????
////                                            if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
////                                                itemInfoDTOS.add(itemInfoDTO);
////                                            }
////                                            priceVOS = new ArrayList<>();
////                                            PriceVO priceVO = new PriceVO();
////                                            priceVO.setPrice(productPrice); //??????
////                                            priceVO.setSupName(maliyVO.getSupplierName());  //???????????????
////                                            priceVO.setSupCode(maliyVO.getSupplierCode()); //???????????????
////                                            priceVO.setSupKey("1"); //??????????????????
////                                            priceVO.setType("??????"); //??????
////                                            priceVO.setSingleWeight(String.valueOf(maliyVO.getWeight()));
////                                            priceVO.setPriority("1");
////                                            priceVOS.add(priceVO);
////                                    }else {
////                                        System.out.println("????????????????????????????????????");
////                                        continue;
////                                    }
//                                }
//                            }
//                        }
//
//                        //????????????300mm
//                        if (maliyVO.getExternalDiameter() > 300) {
//                            MaterialMaliyVO material10 = materialMailyVoMapper.selectSupName10(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//                            if (material10 !=null) {
//                                //?????????
//                                MaterialMaliyVO materialList = MaterialListMax(maliyVO, material10.getExternalDiameter(), material10.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                                if (materialList == null) {
//                                    System.out.println("?????????????????????");
//                                    continue;
//                                }
//                                maliyVO.setSupplierName(materialList.getSupplierName());
//                                maliyVO.setSupplierCode(materialList.getSupplierCode());
//                                maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
//                                maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
//                                maliyVO.setWeight(materialList.getWeight()); //??????
//                                System.out.println("???????????? = " + maliyVO.getTheMaterialPrice());
//                                System.out.println("????????? = " + maliyVO.getMaterialPrice());
//                                System.out.println("?????? = " + maliyVO.getWeight());
//                                //???????????????
//                                if (maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
//                                    System.out.println("?????????0");
//                                    continue;
//                                }
//
//                                //?????????
//                                MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material10.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), material10.getSupplierCode());
//                                if (sprayList.equals(null) ) {
//                                    System.out.println("??????????????????");
//                                    continue;
//                                }
//                                maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //????????????
//                                maliyVO.setSprayPrice(sprayList.getSprayPrice());//?????????
//                                System.out.println("???????????? = " + maliyVO.getCoatingPrice());
//                                System.out.println("????????? = " + maliyVO.getSprayPrice());
//                                //???????????????
//                                if (maliyVO.getSprayPrice().equals(new BigDecimal("0"))){
//                                    System.out.println("?????????0");
//                                    continue;
//                                }
//
//                                //?????????
//                                MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                                if (proscessList == null) {
//                                    System.out.println("???????????????????????????");
//                                    continue;
//                                }
//                                maliyVO.setProcessingFee(proscessList.getProcessingFee()); //?????????
//                                maliyVO.setPrice(proscessList.getPrice()); //?????????
//                                System.out.println("????????? = " + maliyVO.getProcessingFee());
//                                System.out.println("????????? = " + maliyVO.getPrice());
//                                if ( maliyVO.getProcessingFee().equals(new BigDecimal("0"))){
//                                    System.out.println("?????????0");
//                                    continue;
//                                }
//
//                                    //????????????   ????????????=?????????+?????????+?????????+?????????
//                                    BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                                    maliyVO.setUnitPrice(productPrice);
//                                     System.out.println("?????? = " + productPrice);
//                                    //??????????????????????????????0,????????????????????????
////                                    if (maliyVO.getSprayPrice().equals(new BigDecimal("0")) || maliyVO.getProcessingFee().equals(new BigDecimal("0")) || maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
////                                        continue;
////                                    }
//                                    // ?????????????????????????????????????????????supType?????? 1
//                                    if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                                        itemInfoDTO.setSupColorType("1");
//                                    } else {
//                                        itemInfoDTO.setSupColorType("0");
//                                    }
//                                    // ???????????????
//                                    if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
//                                        itemInfoDTOS.add(itemInfoDTO);
//                                    }
//                                    priceVOS = new ArrayList<>();
//                                    PriceVO priceVO = new PriceVO();
//                                    priceVO.setPrice(productPrice); //??????
//                                    priceVO.setSupName(maliyVO.getSupplierName());  //???????????????
//                                    priceVO.setSupCode(maliyVO.getSupplierCode()); //???????????????
//                                    priceVO.setSupKey("1"); //??????????????????
//                                    priceVO.setType("??????"); //??????
//                                    priceVO.setSingleWeight(String.valueOf(maliyVO.getWeight()));
//                                    priceVO.setPriority("1");
//                                    priceVOS.add(priceVO);
//                                }
//                        }
//                    }
//                    itemInfoDTO.setSupAndPriceList(priceVOS);
//                }
//            }else {
//                continue;
//            }
//            itemInfoDTO.setItemName(u9Prs.getItemName());
//            itemInfoDTO.setItemCode(u9Prs.getItemCode());
//            itemInfoDTO.setId(u9Prs.getId().toString());
//            itemInfoDTOS.add(itemInfoDTO);
//        }
//        return itemInfoDTOS;
//    }
//
//    /**
//     * ?????????????????????
//     * @param itemCode
//     * @param itemName
//     * @return
//     */
//    @Override
//    public List<MaterialMaliyVO> getitemInfoGBL(String itemCode, String itemName) {
//        List<MaterialMaliyVO> maliyVOList = new ArrayList<>();
//        //?????????????????????
//        MaterialMaliyVO maliyVO = getItemInfoGuanBangLiao(itemName);
//        //?????????????????????
//        if (maliyVO.getItemName() != null) {
//            //???????????????
//            List<MaterialMaliyVO> mailyVOS = materialMailyVoMapper.selectMaterilaList(maliyVO);
//            if (mailyVOS.size() > 0) {
//
//                for (int i = 0; i < mailyVOS.size(); i++) {
//
//                    if (maliyVO.getExternalDiameter() < 300) {
//
//                        MaterialMaliyVO material = materialMailyVoMapper.selectItemName(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//
//                        //????????????????????????,????????????????????????
//                        if (material != null) {
//                            //?????????
//                            MaterialMaliyVO materialList = getMaterialList(maliyVO, material.getExternalDiameter(), material.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                            if (materialList == null) {
//                                System.out.println("?????????????????????");
//                                continue;
//                            }
//                            maliyVO.setSupplierName(materialList.getSupplierName());
//                            maliyVO.setSupplierCode(materialList.getSupplierCode());
//                            maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
//                            maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
//                            maliyVO.setWeight(materialList.getWeight()); //??????
//                            System.out.println("???????????? = " + maliyVO.getTheMaterialPrice());
//                            System.out.println("????????? = " + maliyVO.getMaterialPrice());
//                            System.out.println("?????? = " + maliyVO.getWeight());
//
//                            //?????????   1234567
//                            MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
//                            if (sprayList.equals(null) ) {
//                                System.out.println("??????????????????");
//                                continue;
//                            }
//                            maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //????????????
//                            maliyVO.setSprayPrice(sprayList.getSprayPrice());//?????????
//                            System.out.println("???????????? = " + maliyVO.getCoatingPrice());
//                            System.out.println("????????? = " + maliyVO.getSprayPrice());
//
//
//                            //?????????
//                            MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                            if (proscessList == null) {
//                                System.out.println("???????????????????????????");
//                                continue;
//                            }
//                            maliyVO.setProcessingFee(proscessList.getProcessingFee()); //?????????
//                            maliyVO.setPrice(proscessList.getPrice()); //?????????
//                            System.out.println("????????? = " + maliyVO.getProcessingFee());
//                            System.out.println("????????? = " + maliyVO.getPrice());
//
//                            //????????????   ????????????=?????????+?????????+?????????+?????????
//                            BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                            maliyVO.setUnitPrice(productPrice); //??????
//                            System.out.print("???????????? = ????????? : " + materialList.getMaterialPrice() + "????????? :" + sprayList.getSprayPrice() + " ????????? : " + proscessList.getProcessingFee() + " ????????? : " + proscessList.getPrice() + " = ?????? : " + productPrice + "  ");
//
//                            MaterialMaliyVO vo = new MaterialMaliyVO();
//                            vo.setCoatingPrice(maliyVO.getCoatingPrice()); //????????????
//                            vo.setSprayPrice(maliyVO.getSprayPrice()); //?????????
//
//                            vo.setProcessingFee(maliyVO.getProcessingFee()); //?????????
//                            vo.setPrice(maliyVO.getPrice());//?????????
//
//                            vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice().setScale(2,BigDecimal.ROUND_DOWN));//????????????
//                            vo.setMaterialPrice(maliyVO.getMaterialPrice()); //?????????
//                            vo.setWeight(maliyVO.getWeight()); //??????
//
//                            vo.setUnitPrice(maliyVO.getUnitPrice()); //????????????
//
//                            vo.setSupplierCode(maliyVO.getSupplierCode()); //???????????????
//                            vo.setSupplierName(maliyVO.getSupplierName());  //???????????????
//                            vo.setCoating(maliyVO.getCoating());  //??????
//                            vo.setItemCode(itemCode); //????????????
//                            vo.setItemName(itemName); //????????????
//                            vo.setLength(maliyVO.getLength());  //????????????
//                            vo.setTheMaterial(maliyVO.getTheMaterial()); //????????????
//                            vo.setExternalDiameter(maliyVO.getExternalDiameter()); //??????
//                            vo.setInternalDiamete(maliyVO.getInternalDiamete()); //??????
//                            maliyVOList.add(vo);
//                        } else {
//                            //????????????,????????????+8  ??????-5 ????????????
//                            MaterialMaliyVO material8 = materialMailyVoMapper.selectSupName8(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//                            if (material8 != null) {
//                                //?????????
//                                MaterialMaliyVO materialList = getMaterialList(maliyVO, material8.getExternalDiameter(), material8.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                                if (materialList == null) {
//                                    System.out.println("?????????????????????");
//                                    continue;
//                                }
//                                maliyVO.setSupplierName(materialList.getSupplierName());
//                                maliyVO.setSupplierCode(materialList.getSupplierCode());
//                                maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
//                                maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
//                                maliyVO.setWeight(materialList.getWeight()); //??????
//                                System.out.println("???????????? = " + maliyVO.getTheMaterialPrice());
//                                System.out.println("????????? = " + maliyVO.getMaterialPrice());
//                                System.out.println("?????? = " + maliyVO.getWeight());
//
//                                //?????????
//                                MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material8.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
//                                if (sprayList.equals(null) ) {
//                                    System.out.println("??????????????????");
//                                    continue;
//                                }
//                                maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //????????????
//                                maliyVO.setSprayPrice(sprayList.getSprayPrice());//?????????
//                                System.out.println("???????????? = " + maliyVO.getCoatingPrice());
//                                System.out.println("????????? = " + maliyVO.getSprayPrice());
//
//                                //?????????
//                                MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                                if (proscessList == null) {
//                                    System.out.println("???????????????????????????");
//                                    continue;
//                                }
//                                maliyVO.setProcessingFee(proscessList.getProcessingFee()); //?????????
//                                maliyVO.setPrice(proscessList.getPrice()); //?????????
//                                System.out.println("????????? = " + maliyVO.getProcessingFee());
//                                System.out.println("????????? = " + maliyVO.getPrice());
//
//
//                                //????????????   ????????????=?????????+?????????+?????????+?????????
//                                BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                                maliyVO.setUnitPrice(productPrice);
////                                System.out.print("???????????? = ????????? : " + materialList.getMaterialPrice() + "????????? :" + sprayList.getSprayPrice() + " ????????? : " + proscessList.getProcessingFee() + " ????????? : " + proscessList.getPrice() + " = ?????? : " + productPrice + "  ");
//
//                                MaterialMaliyVO vo = new MaterialMaliyVO();
//
//                                vo.setCoatingPrice(maliyVO.getCoatingPrice()); //????????????
//                                vo.setSprayPrice(maliyVO.getSprayPrice()); //?????????
//
//                                vo.setProcessingFee(maliyVO.getProcessingFee()); //?????????
//                                vo.setPrice(maliyVO.getPrice());//?????????
//
//                                vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice().setScale(2,BigDecimal.ROUND_DOWN));//????????????
//                                vo.setMaterialPrice(maliyVO.getMaterialPrice()); //?????????
//                                vo.setWeight(maliyVO.getWeight()); //??????
//
//                                vo.setUnitPrice(maliyVO.getUnitPrice()); //????????????
//
//                                vo.setSupplierCode(maliyVO.getSupplierCode()); //???????????????
//                                vo.setSupplierName(maliyVO.getSupplierName());  //???????????????
//                                vo.setCoating(maliyVO.getCoating());  //??????
//                                vo.setItemCode(itemCode); //????????????
//                                vo.setItemName(itemName); //????????????
//                                vo.setLength(maliyVO.getLength());  //????????????
//                                vo.setTheMaterial(maliyVO.getTheMaterial()); //????????????
//                                vo.setExternalDiameter(maliyVO.getExternalDiameter()); //??????
//                                vo.setInternalDiamete(maliyVO.getInternalDiamete()); //??????
//                                maliyVOList.add(vo);
//                            } else {
//                                MaterialMaliyVO material10 = materialMailyVoMapper.selectSupName10(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//                                if (material10 == null) {
//                                    throw new RuntimeException("error");
//                                }
//                                //?????????
//                                MaterialMaliyVO materialList = getMaterialList(maliyVO, material10.getExternalDiameter(), material10.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                                if (materialList == null) {
//                                    System.out.println("?????????????????????");
//                                    continue;
//                                }
//                                maliyVO.setSupplierName(materialList.getSupplierName());
//                                maliyVO.setSupplierCode(materialList.getSupplierCode());
//                                maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
//                                maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
//                                maliyVO.setWeight(materialList.getWeight()); //??????
//                                System.out.println("???????????? = " + maliyVO.getTheMaterialPrice());
//                                System.out.println("????????? = " + maliyVO.getMaterialPrice());
//                                System.out.println("?????? = " + maliyVO.getWeight());
//
//                                //?????????
//                                MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material10.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), material10.getSupplierName());
//                                if (sprayList.equals(null) ) {
//                                    System.out.println("??????????????????");
//                                    continue;
//                                }
//                                maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //????????????
//                                maliyVO.setSprayPrice(sprayList.getSprayPrice());//?????????
//                                System.out.println("???????????? = " + maliyVO.getCoatingPrice());
//                                System.out.println("????????? = " + maliyVO.getSprayPrice());
//
//
//                                //?????????
//                                MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                                if (proscessList == null) {
//                                    System.out.println("???????????????????????????");
//                                    continue;
//                                }
//                                maliyVO.setProcessingFee(proscessList.getProcessingFee()); //?????????
//                                maliyVO.setPrice(proscessList.getPrice()); //?????????
//                                System.out.println("????????? = " + maliyVO.getProcessingFee());
//                                System.out.println("????????? = " + maliyVO.getPrice());
//
//                                //????????????   ????????????=?????????+?????????+?????????+?????????
//                                BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                                maliyVO.setUnitPrice(productPrice);
////                                System.out.print("???????????? = ????????? : " + materialList.getMaterialPrice() + "????????? :" + sprayList.getSprayPrice() + " ????????? : " + proscessList.getProcessingFee() + " ????????? : " + proscessList.getPrice() + " = ?????? : " + productPrice + "  ");
//
//                                MaterialMaliyVO vo = new MaterialMaliyVO();
//                                vo.setCoatingPrice(maliyVO.getCoatingPrice()); //????????????
//                                vo.setSprayPrice(maliyVO.getSprayPrice()); //?????????
//                                vo.setProcessingFee(maliyVO.getProcessingFee()); //?????????
//                                vo.setPrice(maliyVO.getPrice());//?????????
//                                vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice().setScale(2,BigDecimal.ROUND_DOWN));//????????????
//                                vo.setMaterialPrice(maliyVO.getMaterialPrice()); //?????????
//                                vo.setWeight(maliyVO.getWeight()); //??????
//                                vo.setUnitPrice(maliyVO.getUnitPrice()); //????????????
//                                vo.setSupplierCode(maliyVO.getSupplierCode()); //???????????????
//                                vo.setSupplierName(maliyVO.getSupplierName());  //???????????????
//                                vo.setCoating(maliyVO.getCoating());  //??????
//                                vo.setItemCode(itemCode); //????????????
//                                vo.setItemName(itemName); //????????????
//                                vo.setLength(maliyVO.getLength());  //????????????
//                                vo.setTheMaterial(maliyVO.getTheMaterial()); //????????????
//                                vo.setExternalDiameter(maliyVO.getExternalDiameter()); //??????
//                                vo.setInternalDiamete(maliyVO.getInternalDiamete()); //??????
//                                maliyVOList.add(vo);
//                            }
//                        }
//
//                    } else if (maliyVO.getExternalDiameter() > 300) {
//                        MaterialMaliyVO material10 = materialMailyVoMapper.selectSupName10(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//                        if (material10 !=null) {
//                            //?????????
//                            MaterialMaliyVO materialList = MaterialListMax(maliyVO, material10.getExternalDiameter(), material10.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                            if (materialList == null) {
//                                System.out.println("?????????????????????");
//                                continue;
//                            }
//                            maliyVO.setSupplierName(materialList.getSupplierName());
//                            maliyVO.setSupplierCode(materialList.getSupplierCode());
//                            maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
//                            maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
//                            maliyVO.setWeight(materialList.getWeight()); //??????
//                            System.out.println("???????????? = " + maliyVO.getTheMaterialPrice());
//                            System.out.println("????????? = " + maliyVO.getMaterialPrice());
//                            System.out.println("?????? = " + maliyVO.getWeight());
//
//
//                            //?????????
//                            MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material10.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), material10.getSupplierCode());
//                            if (sprayList.equals(null) ) {
//                                System.out.println("??????????????????");
//                                continue;
//                            }
//                            maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //????????????
//                            maliyVO.setSprayPrice(sprayList.getSprayPrice());//?????????
//                            System.out.println("???????????? = " + maliyVO.getCoatingPrice());
//                            System.out.println("????????? = " + maliyVO.getSprayPrice());
//
//                            //?????????
//                            MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                            if (proscessList == null) {
//                                System.out.println("???????????????????????????");
//                                continue;
//                            }
//                            maliyVO.setProcessingFee(proscessList.getProcessingFee()); //?????????
//                            maliyVO.setPrice(proscessList.getPrice()); //?????????
//                            System.out.println("????????? = " + maliyVO.getProcessingFee());
//                            System.out.println("????????? = " + maliyVO.getPrice());
//
//                            //????????????   ????????????=?????????+?????????+?????????+?????????
//                            BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                            maliyVO.setUnitPrice(productPrice);
////                            System.out.print("???????????? = ????????? : " + materialList.getMaterialPrice() + "????????? :" + sprayList.getSprayPrice() + " ????????? : " + proscessList.getProcessingFee() + " ????????? : " + proscessList.getPrice() + " = ?????? : " + productPrice + "  ");
//
//                            MaterialMaliyVO vo = new MaterialMaliyVO();
//
//                            vo.setCoatingPrice(maliyVO.getCoatingPrice()); //????????????
//                            vo.setSprayPrice(maliyVO.getSprayPrice()); //?????????
//
//                            vo.setProcessingFee(maliyVO.getProcessingFee()); //?????????
//                            vo.setPrice(maliyVO.getPrice());//?????????
//
//                            vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice());//????????????
//                            vo.setMaterialPrice(maliyVO.getMaterialPrice()); //?????????
//                            vo.setWeight(maliyVO.getWeight()); //??????
//
//                            vo.setUnitPrice(maliyVO.getUnitPrice()); //????????????
//
//                            vo.setSupplierCode(maliyVO.getSupplierCode()); //???????????????
//                            vo.setSupplierName(maliyVO.getSupplierName());  //???????????????
//                            vo.setCoating(maliyVO.getCoating());  //??????
//                            vo.setItemCode(itemCode); //????????????
//                            vo.setItemName(itemName); //????????????
//                            vo.setLength(maliyVO.getLength());  //????????????
//                            vo.setTheMaterial(maliyVO.getTheMaterial()); //????????????
//                            vo.setExternalDiameter(maliyVO.getExternalDiameter()); //??????
//                            vo.setInternalDiamete(maliyVO.getInternalDiamete()); //??????
//                            maliyVOList.add(vo);
//                        }
//                    }
//                }
//            }
//        }
//        return maliyVOList;
//    }
//
//    /**
//     * ????????? ??????>300
//     * @param maliyVO
//     * @param externalDiameter
//     * @param internalDiamete
//     * @param theMaterial
//     * @param length
//     * @return
//     */
//    private MaterialMaliyVO MaterialListMax(MaterialMaliyVO maliyVO, Integer externalDiameter, Integer internalDiamete, String theMaterial, int length) {
//        MaterialMaliyVO list = materialMailyVoMapper.selectMaterialPrice(externalDiameter, internalDiamete);
//
//        String range = "";
//        Double itemprice = null;  //??????????????????
//        if (theMaterial == "20"){
//            maliyVO.setTheMaterialPrice(BigDecimal.valueOf(9.5));
//        }
//        if (externalDiameter < 148 && (theMaterial != "20")) {
//            range = "148";
//            itemprice = materialMailyVoMapper.selectTheMaterialPrice(theMaterial, range,list.getSupplierCode());
//            if (itemprice == null){
//                maliyVO.setTheMaterialPrice(new BigDecimal("0")); //????????????
//                maliyVO.setMaterialPrice(new BigDecimal("0"));
//                maliyVO.setSupplierName(list.getSupplierName());
//                maliyVO.setSupplierCode(list.getSupplierCode());
//                return maliyVO;
//            }
//            maliyVO.setTheMaterialPrice(BigDecimal.valueOf(itemprice));
//        }
//        if (externalDiameter >= 148 && externalDiameter <= 200 && (theMaterial != "20")) {
//            range = "148,200";
//            itemprice = materialMailyVoMapper.selectTheMaterialPrice(theMaterial, range,list.getSupplierCode());
//            if (itemprice == null){
//                maliyVO.setTheMaterialPrice(new BigDecimal("0")); //????????????
//                maliyVO.setMaterialPrice(new BigDecimal("0"));
//                maliyVO.setSupplierName(list.getSupplierName());
//                maliyVO.setSupplierCode(list.getSupplierCode());
//                return maliyVO;
//            }
//            maliyVO.setTheMaterialPrice(BigDecimal.valueOf(itemprice));
//        }
//        if (externalDiameter > 200 && (theMaterial != "20")) {
//            range = "200";
//            itemprice = materialMailyVoMapper.selectTheMaterialPrice(theMaterial, range,list.getSupplierCode());
//            if (itemprice == null){
//                maliyVO.setTheMaterialPrice(new BigDecimal("0")); //????????????
//                maliyVO.setMaterialPrice(new BigDecimal("0"));
//                maliyVO.setSupplierName(list.getSupplierName());
//                maliyVO.setSupplierCode(list.getSupplierCode());
//                return maliyVO;
//            }
//            maliyVO.setTheMaterialPrice(BigDecimal.valueOf(itemprice));
//        }
//        System.out.println("?????? = " + maliyVO.getTheMaterialPrice());
//        //????????????
//        BigDecimal price = new BigDecimal(externalDiameter).multiply(BigDecimal.valueOf(externalDiameter)).subtract(BigDecimal.valueOf(internalDiamete).multiply(BigDecimal.valueOf(internalDiamete)));
//        BigDecimal   weight =   new BigDecimal(String.valueOf(price)).multiply(BigDecimal.valueOf(PI)).divide(BigDecimal.valueOf(4)).multiply(BigDecimal.valueOf(length)).multiply(BigDecimal.valueOf(8)).divide(BigDecimal.valueOf(1000000));
//        //???????????????
//        BigDecimal MaterialPrice = new BigDecimal(String.valueOf(weight)).multiply(BigDecimal.valueOf(itemprice)).multiply(BigDecimal.valueOf(1.2));
//        maliyVO.setWeight(weight.setScale(2,BigDecimal.ROUND_DOWN));  //??????
//        maliyVO.setSupplierName(list.getSupplierName());
//        maliyVO.setSupplierCode(list.getSupplierCode());
//        maliyVO.setMaterialPrice(MaterialPrice.setScale(2,BigDecimal.ROUND_DOWN)); //?????????
//        maliyVO.setTheMaterialPrice(BigDecimal.valueOf(itemprice)); //????????????
//        return maliyVO;
//    }
//
//    /**
//     * ?????????
//     *
//     * @param maliyVO
//     * @param externalDiameter
//     * @param internalDiamete
//     * @param length
//     * @return
//     */
//    private MaterialMaliyVO getProscessList(MaterialMaliyVO maliyVO, Integer externalDiameter, Integer internalDiamete, Integer length,String supplierCode) {
//        MaterialMaliyVO vo = materialMailyVoMapper.selectProcessPrices(externalDiameter,internalDiamete,length,supplierCode);
//        if (vo == null){
//            maliyVO.setPrice(new BigDecimal("0"));  //?????????
//            maliyVO.setProcessingFee(new BigDecimal("0"));  //?????????
//            return maliyVO;
//        }
//        //?????????
//        if (length < 200) {
//            //????????????
//            if (externalDiameter <= 80) {
//                maliyVO.setPrice(BigDecimal.valueOf(6));
//            }
//            if (externalDiameter > 80 && externalDiameter <= 150) {
//                maliyVO.setPrice(BigDecimal.valueOf(7.2));
//            }
//            if (externalDiameter > 150 && externalDiameter <= 200) {
//                maliyVO.setPrice(BigDecimal.valueOf(15.6));
//            }
//            if (externalDiameter > 200) {
//                maliyVO.setPrice(BigDecimal.valueOf(18)); //?????????
//            }
//        }else {
//            maliyVO.setPrice(new BigDecimal("0"));
//        }
//        maliyVO.setProcessingFee(vo.getProcessingFee().setScale(2,BigDecimal.ROUND_DOWN)); //?????????
//        return maliyVO;
//    }
//
//    /**
//     * ???????????????
//     * @param maliyVO
//     * @param length
//     * @param internalDiamete
//     * @param coating
//     * @param theMaterial
//     * @return
//     */
//    private MaterialMaliyVO getSprayList(MaterialMaliyVO maliyVO, Integer length, Integer internalDiamete, String coating, String theMaterial,String supplierCode) {
//        //???????????????
//        Double price = materialMailyVoMapper.selectBySprayPrice(coating, supplierCode, theMaterial);
//        if (price == null){
//            maliyVO.setSprayPrice(new BigDecimal("0"));  //?????????
//            maliyVO.setCoatingPrice(new BigDecimal("0"));
//            return maliyVO;
//        }
//        maliyVO.setCoatingPrice(BigDecimal.valueOf(price));  //????????????
//        //???????????????
//        BigDecimal sprayPrice = new BigDecimal(internalDiamete).multiply(BigDecimal.valueOf(PI)).multiply(BigDecimal.valueOf(length)).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(price));
//        maliyVO.setSprayPrice(sprayPrice.setScale(2,BigDecimal.ROUND_DOWN));  //?????????
//        return maliyVO;
//    }
//
//    /**
//     * ???????????????  ??????<300
//     * @param externalDiameter
//     * @param internalDiamete
//     * @param theMaterial
//     * @return
//     */
//    private MaterialMaliyVO getMaterialList(MaterialMaliyVO maliyVO,Integer externalDiameter, Integer internalDiamete, String theMaterial, Integer length) {
//        MaterialMaliyVO list = materialMailyVoMapper.selectMaterialPrice(externalDiameter, internalDiamete);
//        String range = "";
//        //???????????????????????????    ??????  ?????????????????????
//        Double itemprice = null;  //??????????????????
//        if (theMaterial == "20"){
//            maliyVO.setTheMaterialPrice(BigDecimal.valueOf(9.5));
//        }
//        if (externalDiameter < 148 && (theMaterial != "20")) {
//            range = "148";
//             itemprice = materialMailyVoMapper.selectTheMaterialPrice(theMaterial, range,list.getSupplierCode());
//             if (itemprice == null){
//                 maliyVO.setTheMaterialPrice(new BigDecimal("0")); //????????????
//                 maliyVO.setMaterialPrice(new BigDecimal("0"));
//                 maliyVO.setSupplierName(list.getSupplierName());
//                 maliyVO.setSupplierCode(list.getSupplierCode());
//                 return maliyVO;
//             }
//            maliyVO.setTheMaterialPrice(BigDecimal.valueOf(itemprice));
//        }
//        if (externalDiameter >= 148 && externalDiameter <= 200 && (theMaterial != "20")) {
//            range = "148,200";
//            itemprice = materialMailyVoMapper.selectTheMaterialPrice(theMaterial, range,list.getSupplierCode());
//            if (itemprice == null){
//                maliyVO.setTheMaterialPrice(new BigDecimal("0")); //????????????
//                maliyVO.setMaterialPrice(new BigDecimal("0"));
//                maliyVO.setSupplierName(list.getSupplierName());
//                maliyVO.setSupplierCode(list.getSupplierCode());
//                return maliyVO;
//            }
//            maliyVO.setTheMaterialPrice(BigDecimal.valueOf(itemprice));
//        }
//        if (externalDiameter > 200 && (theMaterial != "20")) {
//            range = "200";
//            itemprice = materialMailyVoMapper.selectTheMaterialPrice(theMaterial, range,list.getSupplierCode());
//            if (itemprice == null){
//                maliyVO.setTheMaterialPrice(new BigDecimal("0")); //????????????
//                maliyVO.setMaterialPrice(new BigDecimal("0"));
//                maliyVO.setSupplierName(list.getSupplierName());
//                maliyVO.setSupplierCode(list.getSupplierCode());
//                return maliyVO;
//            }
//            maliyVO.setTheMaterialPrice(BigDecimal.valueOf(itemprice));
//        }
//        System.out.println("?????? = " + maliyVO.getTheMaterialPrice());
//        //????????????
//        BigDecimal price = new BigDecimal(externalDiameter).multiply(BigDecimal.valueOf(externalDiameter)).subtract(BigDecimal.valueOf(internalDiamete).multiply(BigDecimal.valueOf(internalDiamete)));
//        BigDecimal   weight =   new BigDecimal(String.valueOf(price)).multiply(BigDecimal.valueOf(PI)).divide(BigDecimal.valueOf(4)).multiply(BigDecimal.valueOf(length)).multiply(BigDecimal.valueOf(8)).divide(BigDecimal.valueOf(1000000));
//        //????????????
//        BigDecimal MaterialPrice = new BigDecimal(String.valueOf(weight)).multiply(maliyVO.getTheMaterialPrice()).multiply(BigDecimal.valueOf(1.2));
//        System.out.println("MaterialPrice = " + MaterialPrice);
//        maliyVO.setWeight(weight.setScale(2,BigDecimal.ROUND_DOWN));  //??????
//        maliyVO.setSupplierName(list.getSupplierName());
//        maliyVO.setSupplierCode(list.getSupplierCode());
//        maliyVO.setMaterialPrice(MaterialPrice.setScale(2,BigDecimal.ROUND_DOWN)); //?????????
//        maliyVO.setTheMaterialPrice(BigDecimal.valueOf(itemprice)); //????????????
//        return maliyVO;
//    }
//
//    /**
//     * ???????????????????????????dialog
//     * @param itemCode
//     * @param itemName
//     * @return
//     */
//    @Override
//    public List<ItemInfoOfZDJVO> getItemInfoOfDJVO(String itemCode, String itemName) {
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//        List<ItemInfoOfZDJVO> itemInfoOfZDJVOS = new ArrayList<>();
//        U9PrDTO u9Prs = new U9PrDTO();
//        u9Prs.setId(1L);
//        u9Prs.setItemCode(itemCode);
//        u9Prs.setItemName(itemName);
//
//        // ?????? & ??????
//        String standards = "";
//        String material = "";
//
//        // ??????
//        ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(itemName);
//        if (itemInfoEntity.getMaterial() == null) { // ???????????????????????????????????? ????????? ??????
//            itemInfoEntity = getMaterialItemInfoOfDuanJian(itemName);
//            if(itemInfoEntity==null) { // ???????????????????????????????????????null
//                return null;
//            }
//        }
//        // ?????? & ??????
//        standards = itemInfoEntity.getSize() + "-" + itemInfoEntity.getPound();
//        material = itemInfoEntity.getMaterial();
//
//        // ?????????????????????????????????
//        List<ItemInfoDTO> itemInfoDTOSOfOld = handleOfDJ(u9Prs, standards,material);
//        List<ItemInfoDTO> itemInfoDTOSOfNew = handleOfDJOfNew(u9Prs, standards,material);
//
//        // ???????????????????????????list??????
//        if (itemInfoDTOSOfNew.size() > 0 && itemInfoDTOSOfOld.size()>0) {
//            itemInfoDTOSOfNew.get(0).getSupAndPriceList().stream().forEach(itemInfoDTO -> {
//                itemInfoDTOSOfOld.get(0).getSupAndPriceList().add(itemInfoDTO);
//            });
//        } else if(itemInfoDTOSOfNew.size() > 0 && itemInfoDTOSOfOld.size()==0){
//            itemInfoDTOSOfOld.addAll(itemInfoDTOSOfNew);
//        }
//
//        // ????????????????????????,????????????????????? ?????????????????????
//        if(itemInfoDTOSOfOld.size()>0){
//            this.handleDJReport(itemInfoDTOSOfOld,u9Prs);
//        }
//
//        itemInfoDTOS.addAll(itemInfoDTOSOfOld);
//
//        if(itemInfoDTOS.size()>0){
//            List<PriceVO> priceVOS = itemInfoDTOS.get(0).getSupAndPriceList();
//            for(PriceVO priceVO:priceVOS){
//                ItemInfoOfZDJVO itemInfoOfZDJVO = BeanUtil.copy(priceVO,ItemInfoOfZDJVO.class);
//                itemInfoOfZDJVO.setItemCode(itemCode);
//                itemInfoOfZDJVO.setItemName(itemName);
//                itemInfoOfZDJVO.setWeight(priceVO.getSingleWeight());
//                itemInfoOfZDJVO.setPrice(priceVO.getSinglePrice());
//                itemInfoOfZDJVO.setQuotePrice(priceVO.getPrice().setScale(2, RoundingMode.HALF_UP).toString());
//                itemInfoOfZDJVOS.add(itemInfoOfZDJVO);
//            }
//        }
//        return itemInfoOfZDJVOS;
//    }
//
//
//    /**
//     * ??????????????????
//     * @param SubmitPriceReq
//     * @return
//     */
//    @Override
//    public IPage<ItemInfoEntityDJReport> getItemInfoOfDJVOReport(IPage<ItemInfoEntityDJReport> page, SubmitPriceReq submitPriceReq) {
//        // ??????3????????????????????????????????????????????????????????????????????????
////        List<ItemInfoVO> itemInfoVOS = this.baseMapper.selectAllDjItemCodes();
////        itemInfoVOS.stream().forEach( item ->{
////            this.getItemInfoOfDJVO(item.getItemCode(),item.getItemName());
////        });
//
//        IPage<ItemInfoEntityDJReport> itemInfoEntityDJReportPage = this.baseMapper.selectDjReportPage(page,submitPriceReq);
//        return itemInfoEntityDJReportPage;
//    }
//
//    @Override
//    public <T> T getBasicItemInfoOfDJ(String itemCode, String itemName) {
//        ItemInfoEntityBasOfDJ itemInfoEntityBasZYDOfDJ = this.baseMapper.selectBasicItemInfoOfDJ(itemCode);
//        return (T) itemInfoEntityBasZYDOfDJ;
//    }
//
//    private List<ItemInfoDTO> handleOfDJ(U9PrDTO u9Prs, String standards,String material) {
//        List<ItemInfoEntityResZYDOfDJ> itemInfoEntityResZYDOfDJList = new ArrayList<>();
//        List<ItemInfoEntityResFMOfDJ> itemInfoEntityResFMOfDJList = new ArrayList<>();
//        List<ItemInfoEntityResZFLOfDJ> itemInfoEntityResZFLOfDJList = new ArrayList<>();
//        List<ItemInfoEntityResALLOfDJ> itemInfoEntityResALLOfDJList = new ArrayList<>();
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//        Double bigOuterSize = 0D;
//        Double bigHeightSize = 0D;
//        Double smallOuterSize = 0D;
//        Double totalHeightSize = 0D;
//        Double innerSize = 0D;
//        Double outerSize = 0D;
//        Double heightSize = 0D;
//
//        Double outSizeToSearch = 0D;
//
//        // ??????????????????
//        ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = this.baseMapper.selectBasicItemInfoOfDJ(u9Prs.getItemCode());
//        if(itemInfoEntityBasOfDJ ==null){
//            return itemInfoDTOS;
//        }
//
//        if(itemInfoEntityBasOfDJ.getBigOuterSize()!=null && !itemInfoEntityBasOfDJ.getBigOuterSize().isEmpty() ) {
//            bigOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigOuterSize()).doubleValue();  // ????????????
//        }
//        if(itemInfoEntityBasOfDJ.getBigHeightSize()!=null && !itemInfoEntityBasOfDJ.getBigHeightSize().isEmpty() ) {
//            bigHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigHeightSize()).doubleValue();  // ????????????
//        }
//        if(itemInfoEntityBasOfDJ.getSmallOuterSize()!=null && !itemInfoEntityBasOfDJ.getSmallOuterSize().isEmpty() ) {
//            smallOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getSmallOuterSize()).doubleValue();// ????????????
//        }
//        if(itemInfoEntityBasOfDJ.getTotalHeightSize()!=null && !itemInfoEntityBasOfDJ.getTotalHeightSize().isEmpty() ) {
//            totalHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getTotalHeightSize()).doubleValue(); // ?????????
//        }
//
//        if(itemInfoEntityBasOfDJ.getInnerSize()!=null && !itemInfoEntityBasOfDJ.getInnerSize().isEmpty() ) {
//            innerSize = Double.valueOf(itemInfoEntityBasOfDJ.getInnerSize()).doubleValue();// ??????
//        }
//
//        if(itemInfoEntityBasOfDJ.getOuterSize()!=null && !itemInfoEntityBasOfDJ.getOuterSize().isEmpty() ) {
//            outerSize = Double.valueOf(itemInfoEntityBasOfDJ.getOuterSize()).doubleValue();  // ??????
//        }
//        if(itemInfoEntityBasOfDJ.getHeightSize()!=null && !itemInfoEntityBasOfDJ.getHeightSize().isEmpty() ) {
//            heightSize = Double.valueOf(itemInfoEntityBasOfDJ.getHeightSize()).doubleValue();  // ??????
//        }
//
//        String formulaType =  itemInfoEntityBasOfDJ.getType();
//        if("A".equals(formulaType)){ //???????????????
//            outSizeToSearch = bigOuterSize;
//        } else {
//            outSizeToSearch = outerSize;
//        }
//
//        // ????????????
//        if(itemInfoEntityBasOfDJ.getRes().equals("fm")){ // (??????????????????
//            itemInfoEntityResFMOfDJList = this.baseMapper.selectResItemInfoOfFm(standards,material);
//            if(itemInfoEntityResFMOfDJList.size()>0){
//                itemInfoEntityResFMOfDJList.stream().forEach(itemInfoEntityResFMOfDJ ->{
//                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResFMOfDJ,ItemInfoEntityResALLOfDJ.class);
//                    itemInfoEntityResALLOfDJList.add(iteminfo);
//                });
//            }
//        } else if(itemInfoEntityBasOfDJ.getRes().equals("zfl")){// (??????????????????)
//            itemInfoEntityResZFLOfDJList = this.baseMapper.selectResItemInfoOfZfl(outSizeToSearch,material);
//            if(itemInfoEntityResZFLOfDJList.size()>0){
//                itemInfoEntityResZFLOfDJList.stream().forEach(itemInfoEntityResFMOfDJ ->{
//                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResFMOfDJ,ItemInfoEntityResALLOfDJ.class);
//                    itemInfoEntityResALLOfDJList.add(iteminfo);
//                });
//            }
//        } else {// (??????????????????)
//            itemInfoEntityResZYDOfDJList = this.baseMapper.selectResItemInfoOfZyd(outSizeToSearch,material);
//            if(itemInfoEntityResZYDOfDJList.size()>0){
//                itemInfoEntityResZYDOfDJList.stream().forEach(itemInfoEntityResZYDOfDJ ->{
//                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResZYDOfDJ,ItemInfoEntityResALLOfDJ.class);
//                    itemInfoEntityResALLOfDJList.add(iteminfo);
//                });
//            }
//        }
//
//        // ????????????????????????
//        if (itemInfoEntityResALLOfDJList.size() > 0) {
//            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
//            List<PriceVO> priceVOS = new ArrayList<>();
//            // id?????????????????????????????????
//            itemInfoDTO.setId(u9Prs.getId().toString());
//            itemInfoDTO.setItemCode(u9Prs.getItemCode());
//            itemInfoDTO.setItemName(u9Prs.getItemName());
//
//            // ????????????????????????????????????
//            for (ItemInfoEntityResALLOfDJ iteminfo : itemInfoEntityResALLOfDJList) {
//                Double outerRemain = Double.valueOf(iteminfo.getOuterRemain()).doubleValue(); // ????????????
//                Double innerRemain = Double.valueOf(iteminfo.getInnerRemain()).doubleValue(); // ????????????
//                Double heightRemain = Double.valueOf(iteminfo.getHeightRemain()).doubleValue(); // ????????????
//
//                Double weight = 0D;
//                Double weightOfBasic = 0D;
//
//                if (itemInfoEntityBasOfDJ.getType().equals("A")){ // --???????????????--
//                    // ??????=(((????????????/2+????????????/2)^2 ??? (??????/2-????????????/2)^2 )) * PI * (????????????+????????????)+((????????????/2 + ????????????/2)^2-(??????/2 ??? ????????????/2)^2)*PI*(?????????-????????????))*7.856/1000000
//                    if ((innerSize  - innerRemain) >= 80) {
//                        weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2) )* PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * PI * (totalHeightSize - bigHeightSize)) * 7.856 / 1000000;
//                    } else {
//                        weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) * PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2)) * PI * (totalHeightSize - bigHeightSize))) * 7.856 / 1000000;
//                    }
//                } else { // --????????????--
//                    // ?????? = PI*((????????????/2 +????????????/2)^2 ??? (????????????/2 - ????????????/2)^2)*(?????? + ????????????)*7.856/1000000
//                    if ((innerSize - innerRemain) >= 80) {
//                        weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
//                    } else {
//                        weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
//                    }
//                }
//
//                BigDecimal b = new BigDecimal(weightOfBasic);
//                weight = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//
//                // ??????
//                String singlePrice = this.baseMapper.selectSinglePrice(iteminfo.getSupCode(),material);
//                if(singlePrice==null) {
//                    return itemInfoDTOS;
//                }
//
//                // ?????? = ?????? * ??????
//                Double price = weight*Double.valueOf(singlePrice);
//
//                // ???????????????????????????????????????
//                PriceVO priceVO = new PriceVO();
//                priceVO.setSingleWeight(weight.toString());
//                priceVO.setSupName(iteminfo.getSupName());
//                priceVO.setSupCode(iteminfo.getSupCode());
//                priceVO.setSinglePrice(singlePrice);
//                priceVO.setPrice(new BigDecimal(price.toString()).setScale(1, BigDecimal.ROUND_HALF_UP));
//                priceVOS.add(priceVO);
//
//                priceVO.setHeightRemain(heightRemain.toString());
//                priceVO.setInnerRemain(innerRemain.toString());
//                priceVO.setOuterRemain(outerRemain.toString());
//
//                itemInfoDTO.setSupAndPriceList(priceVOS);
//            }
//
//            // ?????????????????????????????????????????????supType?????? 1
//            if ((itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                itemInfoDTO.setSupColorType("1");
//            } else {
//                itemInfoDTO.setSupColorType("0");
//            }
//            // ?????????????????????
//            if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
//                itemInfoDTOS.add(itemInfoDTO);
//            }
//        }
//
//        return  itemInfoDTOS;
//    }
//
//
//    private List<ItemInfoDTO> handleOfDJOfNew(U9PrDTO u9Prs, String standards,String material) {
//        List<ItemInfoEntityResCommonOfDJ> itemInfoEntityResCommonOfDJList = new ArrayList<>();
//        List<ItemInfoEntityResALLOfDJ> itemInfoEntityResALLOfDJList = new ArrayList<>();
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//        Double bigOuterSize = 0D;
//        Double bigHeightSize = 0D;
//        Double smallOuterSize = 0D;
//        Double totalHeightSize = 0D;
//        Double innerSize = 0D;
//        Double outerSize = 0D;
//        Double heightSize = 0D;
//        Double outSizeToSearch = 0D;
//
//
//        Double height = 0D; // ??????
//        Double waijing = 0D; // ??????
//        Double neijing = 0D; // ??????
//
//
//        // ??????????????????
//        ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = this.baseMapper.selectBasicItemInfoOfDJ(u9Prs.getItemCode());
//        if(itemInfoEntityBasOfDJ ==null){
//            return itemInfoDTOS;
//        }
//
//        if(itemInfoEntityBasOfDJ.getBigOuterSize()!=null && !itemInfoEntityBasOfDJ.getBigOuterSize().isEmpty() ) {
//            bigOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigOuterSize()).doubleValue();  // ????????????
//        }
//        if(itemInfoEntityBasOfDJ.getBigHeightSize()!=null && !itemInfoEntityBasOfDJ.getBigHeightSize().isEmpty() ) {
//            bigHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigHeightSize()).doubleValue();  // ????????????
//        }
//        if(itemInfoEntityBasOfDJ.getSmallOuterSize()!=null && !itemInfoEntityBasOfDJ.getSmallOuterSize().isEmpty() ) {
//            smallOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getSmallOuterSize()).doubleValue();// ????????????
//        }
//        if(itemInfoEntityBasOfDJ.getTotalHeightSize()!=null && !itemInfoEntityBasOfDJ.getTotalHeightSize().isEmpty() ) {
//            totalHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getTotalHeightSize()).doubleValue(); // ?????????
//        }
//
//        if(itemInfoEntityBasOfDJ.getInnerSize()!=null && !itemInfoEntityBasOfDJ.getInnerSize().isEmpty() ) {
//            innerSize = Double.valueOf(itemInfoEntityBasOfDJ.getInnerSize()).doubleValue();// ??????
//        }
//
//        if(itemInfoEntityBasOfDJ.getOuterSize()!=null && !itemInfoEntityBasOfDJ.getOuterSize().isEmpty() ) {
//            outerSize = Double.valueOf(itemInfoEntityBasOfDJ.getOuterSize()).doubleValue();  // ??????
//        }
//        if(itemInfoEntityBasOfDJ.getHeightSize()!=null && !itemInfoEntityBasOfDJ.getHeightSize().isEmpty() ) {
//            heightSize = Double.valueOf(itemInfoEntityBasOfDJ.getHeightSize()).doubleValue();  // ??????
//        }
//
//        String formulaType =  itemInfoEntityBasOfDJ.getType();
//        if("A".equals(formulaType)){ //???????????????
//            outSizeToSearch = bigOuterSize;
//            height = totalHeightSize;
//            waijing = bigOuterSize;
//            neijing = innerSize;
//        } else { // ??????
//            outSizeToSearch = outerSize;
//            height = heightSize;
//            waijing = outerSize;
//            neijing = innerSize;
//        }
//
//        // ????????????
//        itemInfoEntityResCommonOfDJList = this.baseMapper.selectResItemInfoOfNew(outSizeToSearch,material,height);
//        if(itemInfoEntityResCommonOfDJList.size()>0){
//            itemInfoEntityResCommonOfDJList.stream().forEach(itemInfoEntityResCommonOfDJ ->{
//                ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResCommonOfDJ,ItemInfoEntityResALLOfDJ.class);
//                itemInfoEntityResALLOfDJList.add(iteminfo);
//            });
//        }
//
//        // ????????????????????????
//        if (itemInfoEntityResALLOfDJList.size() > 0) {
//            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
//            List<PriceVO> priceVOS = new ArrayList<>();
//            // id?????????????????????????????????
//            itemInfoDTO.setId(u9Prs.getId().toString());
//            itemInfoDTO.setItemCode(u9Prs.getItemCode());
//            itemInfoDTO.setItemName(u9Prs.getItemName());
//
//            // ????????????????????????????????????
//            for (ItemInfoEntityResALLOfDJ iteminfo : itemInfoEntityResALLOfDJList) {
//                Double outerRemain = Double.valueOf(iteminfo.getOuterRemain()).doubleValue(); // ????????????
//                Double innerRemain = Double.valueOf(iteminfo.getInnerRemain()).doubleValue(); // ????????????
//                Double heightRemain = Double.valueOf(iteminfo.getHeightRemain()).doubleValue(); // ????????????
//
//                Double bihou = (waijing - neijing)/2;
//                if (waijing >= 500 && neijing >= 300 && bihou >= 55 && height >= 110 ) { // ????????? ??????
//                    if(innerRemain<=15) { // ????????????15????????????15
//                        innerRemain = 15D;
//                    } else { // ?????????????????????
//                        innerRemain = outerRemain + 2;
//                    }
//                }
//
//                Double weight = 0D;
//                Double weightOfBasic = 0D;
//
//                if (itemInfoEntityBasOfDJ.getType().equals("A")){ // --???????????????--
//                    // ??????=(((????????????/2+????????????/2)^2 ??? (??????/2-????????????/2)^2 )) * PI * (????????????+????????????)+((????????????/2 + ????????????/2)^2-(??????/2 ??? ????????????/2)^2)*PI*(?????????-????????????))*7.856/1000000
//                    if ((innerSize  - innerRemain) >= 80) {
//                        weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2) )* PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * PI * (totalHeightSize - bigHeightSize)) * 7.856 / 1000000;
//                    } else {
//                        weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) * PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2)) * PI * (totalHeightSize - bigHeightSize))) * 7.856 / 1000000;
//                    }
//                } else { // --????????????--
//                    // ?????? = PI*((????????????/2 +????????????/2)^2 ??? (????????????/2 - ????????????/2)^2)*(?????? + ????????????)*7.856/1000000
//                    if ((innerSize - innerRemain) >= 80) {
//                        weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
//                    } else {
//                        weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
//                    }
//                }
//
//                BigDecimal b = new BigDecimal(weightOfBasic);
//                weight = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//
//                // ??????
//                String singlePrice = this.baseMapper.selectSinglePrice(iteminfo.getSupCode(),material);
//                if(singlePrice==null) {
//                    return itemInfoDTOS;
//                }
//
//                // ?????? = ?????? * ??????
//                Double price = weight*Double.valueOf(singlePrice);
//
//                // ???????????????????????????????????????
//                PriceVO priceVO = new PriceVO();
//                priceVO.setSingleWeight(weight.toString());
//                priceVO.setSupName(iteminfo.getSupName());
//                priceVO.setSupCode(iteminfo.getSupCode());
//                priceVO.setSinglePrice(singlePrice);
//                priceVO.setPrice(new BigDecimal(price.toString()).setScale(1, BigDecimal.ROUND_HALF_UP));
//
//                priceVO.setHeightRemain(heightRemain.toString());
//                priceVO.setInnerRemain(innerRemain.toString());
//                priceVO.setOuterRemain(outerRemain.toString());
//
//                priceVOS.add(priceVO);
//                itemInfoDTO.setSupAndPriceList(priceVOS);
//            }
//
//            // ?????????????????????????????????????????????supType?????? 1
//            if ((itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                itemInfoDTO.setSupColorType("1");
//            } else {
//                itemInfoDTO.setSupColorType("0");
//            }
//            // ?????????????????????
//            if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
//                itemInfoDTOS.add(itemInfoDTO);
//            }
//        }
//
//        return  itemInfoDTOS;
//    }
//
//
//    @Override
//    public boolean setBasicItemInfoOfDJ(SubmitPriceReq submitPriceReq) {
//        if (submitPriceReq.getItemInfoEntityBasOfDJList() != null) {
//            // ??????????????????
//            ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = submitPriceReq.getItemInfoEntityBasOfDJList().get(0);
//            if(this.baseMapper.selectBasicItemInfoOfDJ(itemInfoEntityBasOfDJ.getItemCode())==null){
//                this.baseMapper.insertBasicItemInfoOfDJ(itemInfoEntityBasOfDJ);
//            } else {
//                this.baseMapper.updateBasicItemInfoOfDJ(itemInfoEntityBasOfDJ);
//                String account = getUser().getAccount();
//                String time = DateUtil.format(new Date(), "yyyy-MM-dd");
//                this.baseMapper.deleteDjInfoReport(itemInfoEntityBasOfDJ.getItemCode(),account,time); // ????????????????????????????????????????????????????????????????????????????????????????????????
//            }
//        }
//        return true;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean addBasicItemInfoOfDJ(SubmitPriceReq submitPriceReq) {
//        if (submitPriceReq.getItemInfoEntityBasOfDJList() != null) {
//            // ??????????????????
//            ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = submitPriceReq.getItemInfoEntityBasOfDJList().get(0);
//            if(this.baseMapper.selectBasicItemInfoOfDJ(itemInfoEntityBasOfDJ.getItemCode())==null){
//                if(itemInfoEntityBasOfDJ.getItemCode()==null || itemInfoEntityBasOfDJ.getItemCode().isEmpty() ) {
//                    String itemCode = this.baseMapper.getItemInfoByItemName(itemInfoEntityBasOfDJ.getItemName());
//                    if (itemCode == null){
//                        throw new RuntimeException("?????????????????? ????????????????????????????????? ????????????????????????");
//                    }
//                    itemInfoEntityBasOfDJ.setItemCode(itemCode);
//                }
//                if(itemInfoEntityBasOfDJ.getItemName()==null || itemInfoEntityBasOfDJ.getItemName().isEmpty() ) {
//                    String itemName = this.baseMapper.getItemInfoByItemCode(itemInfoEntityBasOfDJ.getItemCode());
//                    if (itemName == null){
//                        throw new RuntimeException("?????????????????? ?????????????????????????????? ???????????????????????????");
//                    }
//                    itemInfoEntityBasOfDJ.setItemName(itemName);
//                }
//                this.baseMapper.insertBasicItemInfoOfDJ(itemInfoEntityBasOfDJ);
//                this.getItemInfoOfDJVO(itemInfoEntityBasOfDJ.getItemCode(),itemInfoEntityBasOfDJ.getItemName());
//                Integer count = this.baseMapper.itemCodeOfDJIsExisted(itemInfoEntityBasOfDJ.getItemCode());
//                if (count == 0) { // ?????????????????????????????????
//                    throw new RuntimeException("??????????????????????????? ??????????????????,???????????????????????????????????????");
//                }
//            } else {
//                throw new RuntimeException("?????????????????????????????????????????????????????????");
//            }
//        }
//        return true;
//    }
//
//
//    @Override
//    public void exportAllItemInfo(SubmitPriceReq submitPriceReq, HttpServletResponse response) {
//
//        List<ItemInfoEntityDJReport> itemInfoEntityDJReportList = this.baseMapper.selectDjReportList(submitPriceReq);
//
//        if(itemInfoEntityDJReportList.size() > 0) {
//            itemInfoEntityDJReportList.stream().forEach(item->{
//                if(item.getType().equals("A")) {
//                    item.setType("???????????????");
//                    item.setOuterSize("");
//                    item.setHeightSize("");
//                } else {
//                    item.setType("????????????");
//                    item.setBigOuterSize("");
//                    item.setBigHeightSize("");
//                    item.setSmallOuterSize("");
//                    item.setTotalHeightSize("");
//                }
//
//                if(item.getRes().equals("fm")) {
//                    item.setRes("??????");
//                } else if(item.getRes().equals("zyd")){
//                    item.setRes("?????????");
//                } else {
//                    item.setRes("?????????");
//                }
//            });
//        }
//        ExcelUtils.defaultExport(itemInfoEntityDJReportList, ItemInfoEntityDJReport.class, "?????????????????????" + DateUtil.formatDate(new Date()), response);
//    }
//
//
//}
