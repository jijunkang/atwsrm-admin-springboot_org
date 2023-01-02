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
// * 请购单 服务实现类
// * @author Will
// */
//@Service
//public
//class U9PrServiceImplDemo extends BaseServiceImpl<U9PrMapper, U9PrEntity> implements IU9PrService{
//
//
//    @Autowired
//    @Lazy  //  @Lazy  防止service 循环引用
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
//            throw new RuntimeException("未找到PR：" + "id：" + dto.getId());
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
//        //是否按重量计算
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
//            //暂存
//            pr.setStatus(IU9PrService.STATUS_FLOW_SUBMIT);
//        } else if (WIN_BID.equals(dto.getType())){
//            //提交
//            pr.setStatus(IU9PrService.STATUS_WINBID);
//        } else {
//            // 提交审核 （待提交）
//            pr.setStatus(IU9PrService.STATUS_WINBID);
//            String attachMent = this.baseMapper.selectAttachment(dto.getIoId());
//            if (StringUtils.isNotEmpty(attachMent)) {
//                io.setAttachment(attachMent);
//            }
//            this.baseMapper.deleteIo(dto.getIoId());
//        }
//
//        ioService.save(io);
//        if (!pr.getItemName().contains("铸件")) {
//            pr.setIsNeedCheck(dto.getIsNeedCheck());
//        }
//        this.updateById(pr);
//
//        //删除当前PR下的IO并备注  流标录入价格关联的IO删除，ID：
//        List<IoEntity> ioEntities = ioService.getByPrId(pr.getId());
//        ioEntities.forEach(ioEntity -> {
//            if (!ioEntity.getId().equals(io.getId())) {
//                ioEntity.setRemark("流标录入价格关联的IO删除，ID：" + io.getId());
//                ioService.updateById(ioEntity);
//                ioService.removeById(ioEntity);
//            }
//        });
//
////        // 白名单处理逻辑（2021.6.30 不在这里处理了）
////        if (dto.getIsIntoPriceLib() == 1) {
////            IoWinbidReq ioWinbid = new IoWinbidReq();
////            ioWinbid.setIoId(io.getId());
////            ioWinbid.setEffectiveDate(dto.getEffectiveDate());
////            ioWinbid.setExpirationDate(dto.getExpirationDate());
////            ioWinbid.setAttachment(dto.getAttachment());
////            priceLibService.ioToPriceLib(ioWinbid);
////        }
//
//        // 跳过审核的步骤(仅是提交审核的时候)[球座、委外可以跳、铸件不可以]
//        if ((!FLOW_SUBMIT.equals(dto.getType())) && NOT_NEED_CHECK.equals(dto.getIsNeedCheck()) && (!pr.getItemName().contains("铸件"))) {
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
//     * 导出
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
//            // 项目号如果是null或者是空，取 proNo
//            if(dto.getProNo() ==null || dto.getProNo().isEmpty()){
//                dto.setProNo(dto.getApsProNo());
//            }
//            excelDTO.setFlowType(dictBizService.getValue("pr_flow_type", excelDTO.getFlowType()));
//            excelDTO.setStatusFmt(dictBizService.getValue("pr_status", excelDTO.getStatus() + ""));
//            excelDTO.setInquiryWayFmt(dictBizService.getValue("pr_inquiry_way", excelDTO.getInquiryWay()));
//            excelList.add(excelDTO);
//        }
//        ExcelUtils.defaultExport(excelList, U9PrExcelDTO.class, "需求池" + DateUtil.formatDate(new Date()), response);
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
//            put("title", "询价中");
//            put("count", getInquiryCount(STATUS_INQUIRY.toString(),purchType, null));
//        }});
//        result.add(new HashMap<String, Object>(3){{
//            put("status", STATUS_QUOTED);
//            put("title", "待评标");
//            put("count", getInquiryCount(STATUS_QUOTED.toString(),purchType,  null));
//        }});
//        // 小零件询价单、不需要待审核
//        if(PURCHASE_TYPE_NORMAL.equals(purchType)) {
//            result.add(new HashMap<String, Object>(3) {{
//                put("status", STATUS_WINBID);
//                put("title", "待审核");
//                put("count", getInquiryCount(STATUS_WINBID.toString(), purchType, null));
//            }});
//        }
//        result.add(new HashMap<String, Object>(3){{
//            put("status", STATUS_WAIT);
//            put("title", "待下单");
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
//            put("title", "待处理");
//            put("count", getFlowCount(STATUS_HANG + "," + STATUS_FLOW, purchType));
//        }});
//        result.add(new HashMap<String, Object>(3){{
//            put("status", STATUS_FLOW_SUBMIT);
//            put("title", "待提交");
//            put("count", getFlowCount(STATUS_FLOW_SUBMIT.toString(), purchType));
//        }});
//        result.add(new HashMap<String, Object>(3){{
//            put("status", STATUS_WINBID);
//            put("title", "待审核");
//            put("count", getFlowCount(STATUS_WINBID.toString(), purchType));
//        }});
//        result.add(new HashMap<String, Object>(3){{
//            put("status", STATUS_WAIT);
//            put("title", "待下单");
//            put("count", getFlowCount(STATUS_WAIT.toString(), purchType));
//        }});
//        // 小零件询价单、多出无供应商tab
//        if(PURCHASE_TYPE_INNER.equals(purchType)) {
//            result.add(new HashMap<String, Object>(3){{
//                put("status", STATUS_FLOW_NOSUP);
//                put("title", "无供应商");
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
//        highestPrice.setType("历史最高价");
//        highestPrice.setSupName("-");
//        highestPrice.setNumberInterval("-");
//        highestPrice.setPrice(poItemService.getHighestPrice(u9PrVO.getItemCode()));
//        voList.add(highestPrice);
//        PriceVO lowestPrice = new PriceVO();
//        lowestPrice.setType("历史最低价");
//        lowestPrice.setSupName("-");
//        lowestPrice.setNumberInterval("-");
//        lowestPrice.setPrice(poItemService.getLowestPrice(u9PrVO.getItemCode()));
//        voList.add(lowestPrice);
//        PriceVO lastPrice = new PriceVO();
//        lastPrice.setType("最近价");
//        lastPrice.setSupName("-");
//        lastPrice.setNumberInterval("-");
//        PoItemEntity poItemEntity = poItemService.getLastPoInfos(u9PrVO.getItemCode(),u9PrVO.getItemName());
//        if(poItemEntity!=null){
//            lastPrice.setPrice(poItemEntity.getPrice());
//        }
//        voList.add(lastPrice);
//
//        //框架协议
//        u9PrVO.getPriceNumList().forEach(entity ->{
//            //获取有效期间内指定数量的所有区间价格
//            List<PriceFrameEntity> priceFrameEntities = priceFrameService.getNumberInterval(u9PrVO.getItemCode(), new BigDecimal(entity));
//            if(priceFrameEntities.size() > 0){
//                priceFrameEntities.forEach(priceFrameEntity -> {
//                    PriceVO u9Price = new PriceVO();
//                    u9Price.setType("框架协议");
//                    u9Price.setSupName(priceFrameEntity.getSupName());
//                    u9Price.setPrice(priceFrameEntity.getPrice());
//                    u9Price.setNumberInterval(priceFrameEntity.getLimitMin() + "~" + priceFrameEntity.getLimitMax());
//                    voList.add(u9Price);
//                });
//            }
//        });
//        //数学模型
//        List<MmVolumeCalculateEntity> mmVolumeCalculateEntities = mmVolumeCalculateService.getByItemCode(u9PrVO.getItemCode());
//        if(mmVolumeCalculateEntities.size() > 0){
//            mmVolumeCalculateEntities.forEach(entity ->{
//                PriceVO u9Price = new PriceVO();
//                u9Price.setType("数学模型");
//                u9Price.setSupName(entity.getSupName());
//                u9Price.setNumberInterval("-");
//                u9Price.setPrice(entity.getPrice());
//                voList.add(u9Price);
//            });
//        }
//
//        //去重
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
//        ExcelUtils.defaultExport(dtoList, U9PrInquiryExcelDTO.class, "询价单" + DateUtil.formatDate(new Date()), response);
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
//        ExcelUtils.defaultExport(dtoList, U9PrInquiryExcelDTO.class, "询价单" + DateUtil.formatDate(new Date()), response);
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
//        ExcelUtils.defaultExport(dtoList, U9PrFlowExcelDTO.class, "流标单" + DateUtil.formatDate(new Date()), response);
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
//        ExcelUtils.defaultExport(dtoList, U9PrFlowExcelDTO.class, "流标单" + DateUtil.formatDate(new Date()), response);
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
//                throw new RuntimeException("未找到PR：" + "id：" + id);
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
//                    throw new RuntimeException("未找到IO：" + "prId：" + id + "状态：" + STATUS_WINBID);
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
//                // 外面的批量上传
//                List<IoEntity> ioEntities = ioService.getByPrId(submitPriceDTO.getId());
//                for(IoEntity ioEntity : ioEntities){
//                    ioEntity.setAttachment(submitPriceDTO.getAttachment());
//                    if (StringUtil.isNotBlank(submitPriceDTO.getRemark())) {
//                        ioEntity.setRemark(submitPriceDTO.getRemark());
//                    }
//                    ioService.updateById(ioEntity);
//                }
//            } else {
//                // 里面的批量上传
//                IoEntity ioEntity = ioService.getById(submitPriceDTO.getIoId());
//                if (ioEntity == null) {
//                    throw new RuntimeException("未找到小零件的IO：" + "Id：" + submitPriceDTO.getIoId());
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
//                throw new RuntimeException("未找到PR：" + "id：" + id);
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
//            throw new RuntimeException("未找到IO：" + ioReq.getId());
//        }
//        U9PrEntity u9PrEntity = getById(ioEntity.getPrId());
//        if(u9PrEntity == null){
//            throw new RuntimeException("未找到PR：" + ioEntity.getPrId());
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
//            // 承诺交期就是从U9那边传来的要求交期
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
//            // 项目号如果是null或者是空，取 proNo
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
//        // 如果是无供应商状态的，则 找出 流标类型 是no_sup 且是 流标状态 的
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
//     * 获取中标的IO
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
//     * 询价中台统计
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
//     * 流标中台统计
//     *
//     * @param status String
//     * @return int
//     */
//    private int getFlowCount(String status, String type){
//        PrReq prReq = new PrReq();
//        if(!status.equals(STATUS_FLOW_NOSUP)){
//            prReq.setStatuss(status);
//        } else {
//            // 无供应商流标
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
//            // 待处理、
//            if(status.equals(STATUS_HANG + "," + STATUS_FLOW) || status.equals(STATUS_FLOW_SUBMIT.toString()) || status.equals(STATUS_WINBID.toString())){
//                int all = this.baseMapper.selectFlowPageList(prReq).size();
//                int noSup = this.baseMapper.selectPageWithoutIo(new Page<>(),prReq).getRecords().size();
//                return all - noSup;
//            }
//            // 待提交、待审核
//            if(status.equals(STATUS_FLOW_SUBMIT.toString()) || status.equals(STATUS_WINBID.toString())){
//                return this.baseMapper.selectFlowPageList(prReq).size();
//            }
//            // 无供应商
//            if(status.equals(STATUS_FLOW_NOSUP.toString())){
//                prReq.setStatuss("40");
//                prReq.setPurchCode(null);
//                return this.baseMapper.selectPageWithoutIo(new Page<>(),prReq).getRecords().size();
//            }
//            // 待下单
//            prReq.setSource("purch_submit");
//            return this.baseMapper.selectFlowPageOfOthers(new Page<>(),prReq).getRecords().size();
//
//        } else {
//            // normal
//            if(status.equals(STATUS_HANG + "," + STATUS_FLOW)){
//                return this.baseMapper.selectFlowPageList(prReq).size();
//            }
//            // 待审核
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
//            //流标待处理
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
//     * 待处理询价单数量
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
//     * 待处理询价单数量-小零件
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
//     * 待处理流标单数量
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
//     * 待处理流标数量-小零件
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
//        //  修改io状态
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
//        System.out.println("*************** 开始录入 自动获取信息 ***************");
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        Calendar calendar=new GregorianCalendar();
//        String endTime=sdf.format(calendar.getTime());
//        calendar.add(calendar.DATE,-2);//把日期往后增加一天.整数往后推,负数往前移动
//        String time = sdf.format(calendar.getTime());
//        List<U9PrDTO> u9PrDTOS = this.baseMapper.getOrderInfo(time);
//
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//        // 铸件
//        List<ItemInfoDTO> itemInfoDTOSOfZhuJian = autoRetrieveOfZhuJian(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfZhuJian);
//        // 锻件
//        List<ItemInfoDTO> itemInfoDTOSOfDuanJian = autoRetrieveOfDuanJian(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfDuanJian);
//        // 球座
//        List<ItemInfoDTO> itemInfoDTOSOfQiuZuo = autoRetrieveOfQiuZuo(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfQiuZuo);
//        // 全程委外
//        List<ItemInfoDTO> itemInfoDTOSOfWW = autoRetrieveOfWW(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfWW);
//
//        itemInfoDTOS.forEach(item->{
//            if(this.baseMapper.autoItemIsExisted(item.getItemCode())==0){
//                this.baseMapper.insertAutoItem(item.getItemCode(),item.getItemName());
//            }
//        });
//
//        System.out.println("*************** 结束录入 自动获取信息 ***************");
//    }
//
//    /**
//     * 自动获取供应商信息
//     * @param u9PrDTOS
//     * @return
//     * @Desc maily
//     */
//    @Override
//    public List<ItemInfoDTO> autoRetrieve(List<U9PrDTO> u9PrDTOS) {
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//
//        // 铸件
//        List<ItemInfoDTO> itemInfoDTOSOfZhuJian = autoRetrieveOfZhuJian(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfZhuJian);
//
//        // 锻件
//        List<ItemInfoDTO> itemInfoDTOSOfDuanJian = autoRetrieveOfDuanJian(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfDuanJian);
//
//        // 球座
//        List<ItemInfoDTO> itemInfoDTOSOfQiuZuo = autoRetrieveOfQiuZuo(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfQiuZuo);
//
//        // 全程委外
//        List<ItemInfoDTO> itemInfoDTOSOfWW = autoRetrieveOfWW(u9PrDTOS);
//        itemInfoDTOS.addAll(itemInfoDTOSOfWW);
//
//
//        // 管棒料maily
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
//     * 自动获取球座供应商信息
//     * @param u9PrDTOS
//     * @return
//     */
//    public List<ItemInfoDTO> autoRetrieveOfQiuZuo(List<U9PrDTO> u9PrDTOS) {
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//
//        // 获取所有符合 球座 的物料信息
//        for (U9PrDTO u9Prs : u9PrDTOS ) {
//            // 若不是球座，则下一条
//            if(u9Prs.getItemName().indexOf("球座") < 0){
//                continue;
//            }
//            // 若 料号1249开头的组件&&pr是全程委外
//            if(u9Prs.getItemCode().substring(0,4).equals("1249") && u9Prs.getBizType().toString().equals("1")){
//                continue;
//            }
//            // 拆解
//            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(u9Prs.getItemName());
//            // 若拆解成功
//            if (itemInfoEntity.getItemize() != null) {
//
//                // 第一步查找，可能出现多家供应商
//                List<ItemInfoEntityOfQZ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfQZ(itemInfoEntity);
//
//                String price = "";
//                String ptPrice = "";
//                String range = "";
//
//                // 遍历每一家供应商
//                if (itemInfoEntitiesFromDataBase.size() > 0) {
//
//                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
//                    List<PriceVO> priceVOS = new ArrayList<>();
//
//                    // id，用来识别前台的选中行
//                    itemInfoDTO.setId(u9Prs.getId().toString());
//                    itemInfoDTO.setItemCode(u9Prs.getItemCode());
//                    itemInfoDTO.setItemName(u9Prs.getItemName());
//
//                    // 第二次查找，找到单价
//                    for (ItemInfoEntityOfQZ iteminfo : itemInfoEntitiesFromDataBase) {
//                        // 球体加工费
//                        String qzCharge = iteminfo.getQzCharge();
//                        if(StringUtils.isEmpty(qzCharge)) {
//                            qzCharge = "0";
//                        }
//                        // 阀座加工费
//                        String fzCharge = iteminfo.getFzCharge();
//                        if(StringUtils.isEmpty(fzCharge)) {
//                            fzCharge = "0";
//                        }
//
//                        price = this.baseMapper.selectItemPriceOfQZ(iteminfo);
//
//                        if(Double.valueOf(iteminfo.getSize())>=0.5 && Double.valueOf(iteminfo.getSize())<=10 ) {
//                            range = "0.5≤寸级≤10寸";
//                        } else {
//                            range = "寸级≥12寸";
//                        }
//                        ptPrice = this.baseMapper.selectPtPriceOfQZ(iteminfo,range);
//
//                        // 若能找出单价
//                        if (StringUtils.isNotEmpty(price) && StringUtils.isNotEmpty(ptPrice)) {
//                            PriceVO priceVO = new PriceVO();
//                            // 设值单价和供应商的连带关系
//                            priceVO.setSupName(iteminfo.getSupName());
//                            priceVO.setSupCode(iteminfo.getSupCode());
//                            priceVO.setPriority(iteminfo.getPriority());
//
//                            // 四舍五入 保留2位小数 （球座单重*单价+喷涂面积*喷涂单价+球体加工费）+ （阀座单重*单价+喷涂费+阀座加工费）* 2
//                            BigDecimal qzPrice = new BigDecimal(iteminfo.getQzWeight().trim()).multiply(new BigDecimal(price.trim())).add(new BigDecimal(qzCharge.trim())).add(new BigDecimal(iteminfo.getQzSprayArea().trim()).multiply(new BigDecimal(ptPrice.trim())));
//                            BigDecimal fzPrice = new BigDecimal(iteminfo.getFzWeight().trim()).multiply(new BigDecimal(price.trim())).add(new BigDecimal(fzCharge.trim())).add(new BigDecimal(iteminfo.getFzSprayCharge().trim()));
//                            BigDecimal priceToReturn = qzPrice.add(fzPrice.multiply(new BigDecimal("2"))).setScale(0, RoundingMode.HALF_UP);
//                            priceVO.setPrice(priceToReturn);
//
//                            priceVOS.add(priceVO);
//                            itemInfoDTO.setSupAndPriceList(priceVOS);
//                        }
//                    }
//                    // 若一行物料对应的供应商有多行，supType变为 1
//                    if ((itemInfoEntitiesFromDataBase != null) && (itemInfoEntitiesFromDataBase.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                        itemInfoDTO.setSupColorType("1");
//                    } else {
//                        itemInfoDTO.setSupColorType("0");
//                    }
//
//                    // 设值
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
//     * 自动获取铸件供应商信息
//     * @param u9PrDTOS
//     * @return
//     */
//    public List<ItemInfoDTO> autoRetrieveOfZhuJian(List<U9PrDTO> u9PrDTOS) {
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//
//        // 获取所有符合 铸件锻件 的物料信息
//        for (U9PrDTO u9Prs : u9PrDTOS ) {
//
//            // 若不是铸件，则下一条
//            if( u9Prs.getItemName().indexOf("铸件") < 0){
//                continue;
//            }
//
//            // 拆解
//            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(u9Prs.getItemName());
//
//            // 若拆解成功
//            if (itemInfoEntity.getSeries() != null) {
//
//                // 第一步查找，可能出现多家供应商
//                List<ItemInfoEntityOfZDJ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfZDJ(itemInfoEntity);
//
//                String price = "";
//
//                // 遍历每一家供应商
//                if (itemInfoEntitiesFromDataBase.size() > 0) {
//
//                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
//                    List<PriceVO> priceVOS = new ArrayList<>();
//
//                    // id，用来识别前台的选中行
//                    itemInfoDTO.setId(u9Prs.getId().toString());
//                    itemInfoDTO.setItemCode(u9Prs.getItemCode());
//                    itemInfoDTO.setItemName(u9Prs.getItemName());
//
//                    // 第二次查找，找到单价
//                    for (ItemInfoEntityOfZDJ iteminfo : itemInfoEntitiesFromDataBase) {
//                        String extraPrice = iteminfo.getCharge();
//                        if(StringUtils.isEmpty(extraPrice)) {
//                            extraPrice = "0";
//                        }
//                        // 不管有没有 替换 材料，都用原来的物料描述里面的材料
//                        iteminfo.setMaterial(itemInfoEntity.getMaterial());
//
//                        price = this.baseMapper.selectItemPriceOfZDJ(iteminfo);
//                        // 若能找出单价
//                        if (StringUtils.isNotEmpty(price)) {
//
//                            PriceVO priceVO = new PriceVO();
//                            // 设值单价和供应商的连带关系
//                            priceVO.setSupName(iteminfo.getSupName());
//                            priceVO.setSupCode(iteminfo.getSupCode());
//                            priceVO.setPriority(iteminfo.getPriority());
//                            // 四舍五入 保留2位小数 单重*单价+加工费
//                            BigDecimal priceToReturn = (new BigDecimal(price).multiply(new BigDecimal(iteminfo.getWeight())).add(new BigDecimal(extraPrice))).setScale(2, RoundingMode.HALF_UP);
//
//                            priceVO.setPrice(priceToReturn);
//                            priceVOS.add(priceVO);
//                            itemInfoDTO.setSupAndPriceList(priceVOS);
//                        }
//                    }
//
//                    // 若一行物料对应的供应商有多行，supType变为 1
//                    if ((itemInfoEntitiesFromDataBase!=null) &&(itemInfoEntitiesFromDataBase.size() > 1) &&  (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                        itemInfoDTO.setSupColorType("1");
//                    } else {
//                        itemInfoDTO.setSupColorType("0");
//                    }
//                    // 设值默认值
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
//     * 自动获取锻件供应商信息
//     * @param u9PrDTOS
//     * @return
//     */
//    public List<ItemInfoDTO> autoRetrieveOfDuanJian(List<U9PrDTO> u9PrDTOS) {
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//
//        // 获取所有符合 锻件 的物料信息
//        for (U9PrDTO u9Prs : u9PrDTOS ) {
//            // 若不是锻件，则下一条
//            if(u9Prs.getItemName().indexOf("锻件") < 0){
//                continue;
//            }
//            // 规格 & 材质
//            String standards = "";
//            String material = "";
//
//            // 拆解
//            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(u9Prs.getItemName());
//            if (itemInfoEntity.getMaterial() == null) { // 正常拆解如果拆不出来，则 继续拆 材质
//                itemInfoEntity = getMaterialItemInfoOfDuanJian(u9Prs.getItemName());
//                if(itemInfoEntity==null) { // 如果拆解还拆不出来，就返回null
//                    return null;
//                }
//            }
//            // 规格 & 材质
//            standards = itemInfoEntity.getSize() + "-" + itemInfoEntity.getPound();
//            material = itemInfoEntity.getMaterial();
//
//            // 分别处理计算价格并返回
//            List<ItemInfoDTO> itemInfoDTOSOfOld = handleOfDJ(u9Prs,standards,material); // 原有的逻辑（余量是分开的，根据 外圆 / 规格 来取余量）
//            List<ItemInfoDTO> itemInfoDTOSOfNew = handleOfDJOfNew(u9Prs,standards,material); // 新有逻辑 （余量放在一起，根据 外圆 + 高度 来取余量）
//
//            // 新旧逻辑合并
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
//            // 如果可以查到信息,处理数据进入到 最终的信息表中
//            if(itemInfoDTOSOfOld.size()>0){
//                this.handleDJReport(itemInfoDTOSOfOld,u9Prs);
//            }
//
//            itemInfoDTOS.addAll(itemInfoDTOSOfOld);
//        }
//        return itemInfoDTOS;
//    }
//
//    // 处理最终信息进入到表中
//    private void handleDJReport(List<ItemInfoDTO> itemInfoDTOSOfOld,U9PrDTO u9Prs) {
//        // 获取信息后，往锻件最终的信息表里填写数据，方便日后查看。
//        Integer count = this.baseMapper.itemCodeOfDJIsExisted(u9Prs.getItemCode());
//        if (count == 0) { // 最终信息表中没有该数据
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
//                if ("A".equals(formulaType)) { //大小头公式
//                    bigOuterSize = itemInfoEntityBasOfDJ.getBigOuterSize();  // 大头外径
//                    bigHeightSize = itemInfoEntityBasOfDJ.getBigHeightSize();  // 大头高度
//                    smallOuterSize = itemInfoEntityBasOfDJ.getSmallOuterSize();// 小头外径
//                    totalHeightSize = itemInfoEntityBasOfDJ.getTotalHeightSize(); // 总高度
//                    innerSize = itemInfoEntityBasOfDJ.getInnerSize();// 内径
//                } else { // 常规
//                    innerSize = itemInfoEntityBasOfDJ.getInnerSize();// 内径
//                    outerSize = itemInfoEntityBasOfDJ.getOuterSize();  // 外径
//                    heightSize = itemInfoEntityBasOfDJ.getHeightSize();  // 高度
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
//     * 自动获取全程委外的供应商信息
//     * @param u9PrDTOS
//     * @return
//     */
//    public List<ItemInfoDTO> autoRetrieveOfWW(List<U9PrDTO> u9PrDTOS) {
//        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//
//        // 获取所有符合 铸件锻件 的物料信息
//        for (U9PrDTO u9Prs : u9PrDTOS ) {
//            // 若不是委外，则下一条
//            if(!u9Prs.getBizType().toString().equals(WW)){
//                continue;
//            }
//            // 拆解
//            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfQCWW(u9Prs.getItemName());
//            // 若拆解成功
//            if (itemInfoEntity.getSeries() != null) {
//                // 第一步查找，可能出现多家供应商
//                List<ItemInfoEntityOfWW> itemInfoEntitiesWW = this.baseMapper.selectSupAndWeightOfQCWW(itemInfoEntity);
//                String price = "";
//
//                // 遍历每一家供应商
//                if (itemInfoEntitiesWW.size() > 0) {
//
//                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
//                    List<PriceVO> priceVOS = new ArrayList<>();
//
//                    // id，用来识别前台的选中行
//                    itemInfoDTO.setId(u9Prs.getId().toString());
//                    itemInfoDTO.setItemCode(u9Prs.getItemCode());
//                    itemInfoDTO.setItemName(u9Prs.getItemName());
//
//                    // 第二次查找，找到单价
//                    for (ItemInfoEntityOfWW iteminfo : itemInfoEntitiesWW) {
//                        price = iteminfo.getPrice().toString();
//                        // 若能找出单价
//                        if (StringUtils.isNotEmpty(price)) {
//                            PriceVO priceVO = new PriceVO();
//                            // 设值单价和供应商的连带关系
//                            priceVO.setSupName(iteminfo.getSupName());
//                            priceVO.setSupCode(iteminfo.getSupCode());
//                            priceVO.setPriority(iteminfo.getPriority());
//                            // 四舍五入 保留2位小数 单重*单价+加工费
//                            BigDecimal priceToReturn = (new BigDecimal(price));
//                            priceVO.setPrice(priceToReturn);
//                            priceVOS.add(priceVO);
//                            itemInfoDTO.setSupAndPriceList(priceVOS);
//                        }
//                    }
//
//                    // 若一行物料对应的供应商有多行，supType变为 1
//                    if ((itemInfoEntitiesWW!=null) &&(itemInfoEntitiesWW.size() > 1) &&  (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                        itemInfoDTO.setSupColorType("1");
//                    } else {
//                        itemInfoDTO.setSupColorType("0");
//                    }
//                    // 设值默认值
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
//     * 重置PR单的信息
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
//     * 删除物料号的基础信息和报表信息
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
//     * 获取铸件信息，弹出dialog
//     * @param itemCode
//     * @param itemName
//     * @return
//     */
//    @Override
//    public List<ItemInfoOfZDJVO> getItemInfoOfZDJVO(String itemCode, String itemName) {
//        List<ItemInfoOfZDJVO> itemInfoOfCastVOS = new ArrayList<>();
//        ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(itemName);
//        // 若拆解成功
//        if (itemInfoEntity.getSeries() != null) {
//            // 第一步查找，可能出现多家供应商
//            List<ItemInfoEntityOfZDJ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfZDJ(itemInfoEntity);
//
//            // 遍历每一家供应商
//            if (itemInfoEntitiesFromDataBase.size() > 0) {
//                // 第二次查找，找到单价
//                for (ItemInfoEntityOfZDJ iteminfo : itemInfoEntitiesFromDataBase) {
//                    String extraPrice = iteminfo.getCharge();
//                    if(StringUtils.isEmpty(extraPrice)) {
//                        extraPrice = "0";
//                    }
//
//                    // 无论是否替换，都用原来的 材料
//                    iteminfo.setMaterial(itemInfoEntity.getMaterial());
//
//                    String price = this.baseMapper.selectItemPriceOfZDJ(iteminfo);
//                    // 若能找出单价
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
//     * 获取委外信息，弹出dialog（全程委外）
//     * @param itemCode
//     * @param itemName
//     * @return
//     */
//    @Override
//    public List<ItemInfoOfZDJVO> getItemInfoOfWWVO(String itemCode, String itemName) {
//
//        List<ItemInfoOfZDJVO> itemInfoOfCastVOS = new ArrayList<>();
//        ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfQCWW(itemName);
//        // 若拆解成功
//        if (itemInfoEntity.getSeries() != null) {
//            // 第一步查找，可能出现多家供应商
//            List<ItemInfoEntityOfWW> itemInfoEntitiesWW = this.baseMapper.selectSupAndWeightOfQCWW(itemInfoEntity);;
//
//            // 遍历每一家供应商
//            if (itemInfoEntitiesWW.size() > 0) {
//                // 第二次查找，找到单价
//                for (ItemInfoEntityOfWW iteminfo : itemInfoEntitiesWW) {
//
//                    // 无论是否替换，都用原来的 材料
//                    iteminfo.setMaterial(itemInfoEntity.getMaterial());
//
//                    String price = iteminfo.getPrice().toString();
//                    // 若能找出单价
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
//     * 获取球座信息，弹出dialog
//     * @param itemCode
//     * @param itemName
//     * @return
//     */
//    @Override
//    public List<ItemInfoOfQZVO> getItemInfoOfQZVO(String itemCode, String itemName) {
//        List<ItemInfoOfQZVO> itemInfoOfCastVOS = new ArrayList<>();
//        ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(itemName);
//        // 若拆解成功
//        if (itemInfoEntity.getItemize() != null) {
//            // 第一步查找，可能出现多家供应商
//            List<ItemInfoEntityOfQZ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfQZ(itemInfoEntity);
//
//            // 遍历每一家供应商
//            if (itemInfoEntitiesFromDataBase.size() > 0) {
//                // 第二次查找，找到单价
//                for (ItemInfoEntityOfQZ iteminfo : itemInfoEntitiesFromDataBase) {
//                    // 球体加工费
//                    String qzCharge = iteminfo.getQzCharge();
//                    if(StringUtils.isEmpty(qzCharge)) {
//                        qzCharge = "0";
//                    }
//                    // 阀座加工费
//                    String fzCharge = iteminfo.getFzCharge();
//                    if(StringUtils.isEmpty(fzCharge)) {
//                        fzCharge = "0";
//                    }
//
//                    String price = this.baseMapper.selectItemPriceOfQZ(iteminfo);
//                    String range = "";
//                    if(Integer.valueOf(iteminfo.getSize())>=0.5 && Integer.valueOf(iteminfo.getSize())<=10 ) {
//                        range = "0.5≤寸级≤10寸";
//                    } else {
//                        range = "寸级≥12寸";
//                    }
//                    String ptPrice = this.baseMapper.selectPtPriceOfQZ(iteminfo,range);
//
//                    // 若能找出单价
//                    if (StringUtils.isNotEmpty(price)) {
//                        ItemInfoOfQZVO itemInfoOfCastVO = new ItemInfoOfQZVO();
//
//                        // 四舍五入 保留2位小数 （球座单重*单价+球座加工费）+ （阀座单重*单价+阀座加工费）
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
//     * 保存，批量保存 - 外层
//     * @param SubmitPriceReq
//     * @return
//     */
//    @Override
//    public boolean saveBatchOfOthers(SubmitPriceReq SubmitPriceReq) {
//
//        List<SubmitPriceDTO> submitPriceDTOs = SubmitPriceReq.getSubmitPriceDTOs();
//
//        // 根据id来去重
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
//                throw new RuntimeException("未找到PR：" + "id：" + submitPriceDTO.getId());
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
//                //是否按重量计算
//                io.setIsByWeight(submitPriceDTO.getIsByWeight());
//                io.setIsPersent(submitPriceDTO.getIsPersent());
//                io.setRemark(submitPriceDTO.getRemark());
//                io.setQuotePrice(submitPriceDTO.getQuotePrice());
//                io.setPromiseDate(submitPriceDTO.getPromiseDate().getTime()/1000 + 8 * 3600); //这里用datePicker会存在8小时时间差
//                io.setSource(IIoService.SOURCE_PURCHSUBMIT);
//
//                if ("flowSubmit".equals(submitPriceDTO.getType())) {
//                    //暂存（待处理画面的）
//                    pr.setStatus(IU9PrService.STATUS_FLOW_SUBMIT);
//                } else if ("winBid".equals(submitPriceDTO.getType())){
//                    //提交（待处理画面的）
//                    pr.setStatus(IU9PrService.STATUS_WINBID);
//                } else {
//                    // 提交审核 （待提交画面的）
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
//     * 保存，批量保存 - 弹出框
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
//                throw new RuntimeException("未找到PR：" + "id：" + submitPriceDTO.getId());
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
//            //是否按重量计算
//            io.setIsByWeight(submitPriceDTO.getIsByWeight());
//            io.setIsPersent(submitPriceDTO.getIsPersent());
//            io.setRemark(submitPriceDTO.getRemark());
//            io.setQuotePrice(submitPriceDTO.getQuotePrice());
//            io.setPromiseDate(submitPriceDTO.getPromiseDate().getTime() / 1000 + 8 * 3600);
//            io.setSource(IIoService.SOURCE_PURCHSUBMIT);
//
//            if ("flowSubmit".equals(submitPriceDTO.getType())) {
//                //暂存（待处理画面的）
//                pr.setStatus(IU9PrService.STATUS_FLOW_SUBMIT);
//            } else if ("winBid".equals(submitPriceDTO.getType())) {
//                Integer bidNum = this.baseMapper.countTheBid(submitPriceDTO.getId().toString());
//                if(bidNum !=  1 ){
//                    throw new RuntimeException("提交审核失败，请检查该料号的中标情况是否有错！");
//                }
//                //提交（待处理画面的）
//                pr.setStatus(IU9PrService.STATUS_WINBID);
//            } else {
//                Integer bidNum = this.baseMapper.countTheBid(submitPriceDTO.getId().toString());
//                if(bidNum > 0 ){
//                    throw new RuntimeException("提交审核失败，请检查该料号的中标情况是否有错！");
//                }
//                // 提交审核 （待提交画面的）
//                pr.setStatus(IU9PrService.STATUS_WINBID);
//            }
//            ioService.updateById(io);
//            updateById(pr);
//        }
//        return true;
//    }
//
//    /**
//     * 重置PR单的信息 - 其他小零件
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
//     * 设为中标 - 其他小零件
//     *
//     * @param submitPriceReq
//     * @return
//     */
//    @Override
//    public boolean winTheBid(SubmitPriceReq submitPriceReq) {
//
//        Integer bidNum = this.baseMapper.countTheBid(submitPriceReq.getSubmitPriceDTOs().get(0).getId().toString());
//        if(bidNum > 0 ){
//            throw new RuntimeException("该料号已经有中标商家了");
//        }
//        return this.baseMapper.winTheBid(submitPriceReq.getSubmitPriceDTOs().get(0).getIoId().toString());
//    }
//
//    /**
//     * 取消中标 - 其他小零件
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
//     * 移至无供应商
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
//     * 添加供应商交叉关系
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
//                throw  new RuntimeException("不存在该供应商、请检查供应商！");
//            }
//            supItemOthers.setSupName(sup.getName());
//            // 零：暂时添加 不是生效状态
//            supItemOthers.setStatus(0);
//
//            supItemOthers.setSupCode(supCode);
//            this.baseMapper.addOtherInfos(supItemOthers);
//        }
//        return true;
//    }
//
//    /**
//     * 获取其他小零件信息
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
//     * 更新供应商交叉关系
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
//            throw  new RuntimeException("不存在该供应商、请检查供应商！");
//        }
//        supItemOthers.setSupName(sup.getName());
//
//        this.baseMapper.updateOtherInfos(supItemOthers);
//
//        return true;
//    }
//
//    /**
//     * 移除供应商交叉关系
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
//            throw  new RuntimeException("不存在该供应商、请检查供应商！");
//        }
//        supItemOthers.setSupName(sup.getName());
//
//        return this.baseMapper.removeOtherInfos(supItemOthers);
//    }
//
//
//    /**
//     * 提交并询价
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
//        // 往后顺延工作日
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
//            // 创建新的IO
//            ioService.save(ioEntity);
//            // 将暂时生效的供应商交叉关系生效
//            supItems.setStatus(1);
//            this.baseMapper.updateStatusOfOthers(supItems);
//        }
//
//        // 更新PR的状态
//        u9PrEntity.setStatus(STATUS_INQUIRY);
//        u9PrEntity.setInquiryWay(INQUIRYWAY_COMPETE);
//        saveOrUpdate(u9PrEntity);
//        return true;
//    }
//
//
//    /**
//     * 立即评标 - 小零件
//     * @param prid
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean evaluateBidOfOthers(Long id) {
//        List<IoEntity> ioEntities = this.baseMapper.getIoListByPrId(id);
//        U9PrEntity pr = this.baseMapper.getPrById(id);
//
//        // 材料费
//        BigDecimal matPrice = new BigDecimal("0");
//        BigDecimal mat = this.baseMapper.getMaterialCostByItemCode(pr.getItemCode());
//        if(mat != null){
//            matPrice = mat;
//        } else {
//            throw new RuntimeException("材料费还未传到、请稍等...");
//        }
//
//        // 加工费
//        BigDecimal laborPrice = new BigDecimal("0");
//        BigDecimal lab = this.baseMapper.getLaborCostByItemCode(pr.getItemCode());
//        if(lab != null){
//            laborPrice = lab;
//        }else {
//            throw new RuntimeException("加工费还未传到、请稍等...");
//        }
//        pr.setMaterialCost(matPrice);
//        pr.setLaborCost(laborPrice);
//        saveOrUpdate(pr);
//
//        // 标准要求交期
//        Long reqDate = pr.getReqDate();
//        // 标准采购成本
//        BigDecimal standandPrice = laborPrice.multiply(new BigDecimal("1.4")).add(matPrice) ;
//
//        // 计算得分
//        for(IoEntity ioEntity : ioEntities) {
//            // 报价价格
//            BigDecimal quotoPrice = ioEntity.getQuotePrice();
//
//            // 报价日期
//            Long quotoDate = ioEntity.getQuoteDate();
//
//            // 承诺日期
//            Long promiseDate = ioEntity.getPromiseDate();
//
//            // 记录得分
//            if( quotoDate == null || quotoPrice == null){
//                this.baseMapper.updateEvaluateScore(new BigDecimal("0"),ioEntity.getId());
//            } else if ( promiseDate > reqDate ||quotoDate > reqDate || quotoPrice.compareTo(standandPrice) > 0) {
//                this.baseMapper.updateEvaluateScore(new BigDecimal("0"),ioEntity.getId());
//            } else {
//                // 得分是： 标准采购成本 和 报价价格 的差 + 1
//                this.baseMapper.updateEvaluateScore(standandPrice.subtract(quotoPrice).add(new BigDecimal("1")),ioEntity.getId());
//            }
//        }
//
//        // 取得计算得分后的LIST
//        List<IoEntity> ioEntitiesAfterScore = this.baseMapper.getIoListByPrId(id);
//        // 基础得分 第一条数据
//        List<IoEntity> bestIoEntities = new ArrayList<>();
//        IoEntity baseIo = ioEntitiesAfterScore.get(0);
//
//        for (IoEntity ioEntity : ioEntitiesAfterScore) {
//            BigDecimal baseScore = baseIo.getEvaluateScore();
//
//            // 如果大于基础数据
//            if (ioEntity.getEvaluateScore().compareTo(baseScore) > 0 ) {
//                // 清空之前的最好的数据，新增当前最好的数据
//                bestIoEntities.clear();
//                bestIoEntities.add(ioEntity);
//                baseIo = ioEntity;
//            } else if (ioEntity.getEvaluateScore().compareTo(baseScore) == 0){
//                // 新增当前最好的数据
//                bestIoEntities.add(ioEntity);
//            }
//        }
//
//        // 如果只有一个，但是这个评分不合格，那就要清空
//        if(bestIoEntities.size() == 1  && bestIoEntities.get(0).getEvaluateScore().compareTo(new BigDecimal("0")) == 0 ){
//            bestIoEntities.clear();
//        }
//
//        // 如果只有一个，那么将这个IO的状态改为 代下单、其他的 改为 未中标；如果不是只有一个，那么就不改变状态，流标转人工
//        if (bestIoEntities.size() == 1 && bestIoEntities.get(0).getEvaluateScore().compareTo(new BigDecimal("0")) > 0) {
//            for (IoEntity ioEntity : ioEntitiesAfterScore) {
//                // 不是同一个ID，则流标
//                if(!ioEntity.getId().toString().equals(bestIoEntities.get(0).getId().toString())){
//                    this.baseMapper.setLoseTheBid(ioEntity);
//                } else {
//                    this.baseMapper.setWinTheBid(ioEntity.getId(), new Date().getTime()/1000);
//                }
//            }
//            this.baseMapper.setPrToWait(pr.getId());
//        }
//
//        // 没有符合条件的供应商 ,按流标处理
//        if( bestIoEntities.size() == 0 ) {
//            this.baseMapper.setPrFlow(pr.getId());
//        }
//
//        // 最佳评标大于 1 个的时候
//        if( bestIoEntities.size() > 1 ) {
//            this.baseMapper.setPrToChoose(pr.getId());
//        }
//
//        return true;
//    }
//
//
//    /**
//     * 自动获取管棒料信息
//     *
//     * @param u9PrDTOS
//     * @return 物料描述 itemcode
//     */
//    private List<ItemInfoDTO> authRetriveOfGuanBangLiao(List<U9PrDTO> u9PrDTOS) {
//        ArrayList<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
//        System.out.println("*************** 目的是将 每日可以使用 自动获取信息 的料号保存下来 ***************");
//
//        List<MaterialMaliyVO> list = new ArrayList<>();
//        //集合
//        MaterialMaliyVO maliyVO = new MaterialMaliyVO();
//        //获取所有符合管棒料 的物料信息
//        for (U9PrDTO u9Prs : u9PrDTOS) {
//            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
//            //若不是管棒料,则contiune 跳出
//            if (u9Prs.getItemName().indexOf("管") < 0) {
//                continue;
//            }
////            tubeMaterialService.selectLists(u9Prs.getItemName(),u9Prs.getItemCode());
//            //管棒料拆解 获取材料对象名
//            maliyVO = getItemInfoGuanBangLiao(u9Prs.getItemName());
//            //如果拆解成功
//            if (maliyVO.getItemName() != null) {
//                //查找是不是有多家供应商
//                List<MaterialMaliyVO> mailyVOList = materialMailyVoMapper.selectSupName(maliyVO);
//                //遍历多个供应商
//                if (mailyVOList.size() > 0) {
//                    List<PriceVO> priceVOS = null;
//                    for (int i = 0; i < mailyVOList.size(); i++) {
//                        //判断外圆是否小于300MM
//                        if (maliyVO.getExternalDiameter() < 300) {
//                            MaterialMaliyVO material = materialMailyVoMapper.selectItemName(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//                            //如果精确找到数据,则按原始价位计算
//                            if (material != null) {
//                                //材料费     1234567
//                                MaterialMaliyVO materialList = getMaterialList(maliyVO, material.getExternalDiameter(), material.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                                if (materialList == null) {
//                                    System.out.println("材料费出现问题");
//                                    continue;
//                                }
//                                maliyVO.setSupplierName(materialList.getSupplierName());
//                                maliyVO.setSupplierCode(materialList.getSupplierCode());
//                                maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
//                                maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
//                                maliyVO.setWeight(materialList.getWeight()); //单重
//                                System.out.println("材料单价 = " + maliyVO.getTheMaterialPrice());
//                                System.out.println("材料费 = " + maliyVO.getMaterialPrice());
//                                System.out.println("单重 = " + maliyVO.getWeight());
//                                //页面不显示
//                                if (maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
//                                    System.out.println("材料费0");
//                                    continue;
//                                }
//
//                                //喷涂费
//                                MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
//                                if (sprayList.equals(null) ) {
//                                    System.out.println("喷涂费不存在");
//                                    continue;
//                                }
//                                maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //喷涂单价
//                                maliyVO.setSprayPrice(sprayList.getSprayPrice());//喷涂费
//                                System.out.println("喷涂单价 = " + maliyVO.getCoatingPrice());
//                                System.out.println("喷涂费 = " + maliyVO.getSprayPrice());
//                                //页面不显示
//                                if (maliyVO.getSprayPrice().equals(new BigDecimal("0"))){
//                                    System.out.println("喷涂费0");
//                                    continue;
//                                }
//
//                                //加工费
//                                MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                                if (proscessList == null) {
//                                    System.out.println("加工费计算出现问题");
//                                    continue;
//                                }
//                                maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
//                                maliyVO.setPrice(proscessList.getPrice()); //切割费
//                                System.out.println("加工费 = " + maliyVO.getProcessingFee());
//                                System.out.println("切割费 = " + maliyVO.getPrice());
//                                if ( maliyVO.getProcessingFee().equals(new BigDecimal("0"))){
//                                    System.out.println("加工费0");
//                                    continue;
//                                }
//
//                                    //产品单价   产品单价=材料费+喷涂费+加工费+切割费
//                                    BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                                    maliyVO.setUnitPrice(productPrice);
//                                    System.out.println("产品单价 = " + productPrice);
//                                    //如果价格有一个显示为0,则进行下一步操作
////                                    if (maliyVO.getSprayPrice().equals(new BigDecimal("0")) || maliyVO.getProcessingFee().equals(new BigDecimal("0")) || maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
////                                        continue;
////                                    }
//                                    // 若一行物料对应的供应商有多行，supType变为 1
//                                    if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                                        itemInfoDTO.setSupColorType("1");
//                                    } else {
//                                        itemInfoDTO.setSupColorType("0");
//                                    }
//                                    // 设值默认值
//                                    if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
//                                        itemInfoDTOS.add(itemInfoDTO);
//                                    }
//                                    priceVOS = new ArrayList<>();
//                                    PriceVO priceVO = new PriceVO();
//                                    priceVO.setPrice(productPrice); //价格
//                                    priceVO.setSupName(maliyVO.getSupplierName());  //供应商名称
//                                    priceVO.setSupCode(maliyVO.getSupplierCode()); //供应商编码
//                                    priceVO.setSupKey("1"); //供应商默认值
//                                    priceVO.setType("类型"); //类型
//                                    priceVO.setSingleWeight(String.valueOf(maliyVO.getWeight()));
//                                    priceVO.setPriority("1");
//                                    priceVOS.add(priceVO);
//                                 } else {
//                                //先找外径
//                                MaterialMaliyVO materials8 = materialMailyVoMapper.selectExter(maliyVO.getExternalDiameter());
//                                System.out.println("materials8 = " + materials8);
//                                //根据外径找内径
//                                MaterialMaliyVO materials5 = materialMailyVoMapper.selectInner(materials8.getExternalDiameter(),maliyVO.getInternalDiamete());
//                                System.out.println("materials5 = " + materials5);
//                                //如找不到,按照外径+8  内径-5 进行计算
//                                MaterialMaliyVO material8 = materialMailyVoMapper.selectSupName8(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//                                if (material8 != null) {
//                                    //材料费
//                                    MaterialMaliyVO materialList = getMaterialList(maliyVO, material8.getExternalDiameter(), material8.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                                    if (materialList == null) {
//                                        System.out.println("材料费出现问题");
//                                        continue;
//                                    }
//                                    maliyVO.setSupplierName(materialList.getSupplierName());
//                                    maliyVO.setSupplierCode(materialList.getSupplierCode());
//                                    maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
//                                    maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
//                                    maliyVO.setWeight(materialList.getWeight()); //单重
//                                    System.out.println("材料单价 = " + maliyVO.getTheMaterialPrice());
//                                    System.out.println("材料费 = " + maliyVO.getMaterialPrice());
//                                    System.out.println("单重 = " + maliyVO.getWeight());
//                                    //页面不显示
//                                    if (maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
//                                        System.out.println("材料费0");
//                                        continue;
//                                    }
//
//
//
//                                    //喷涂费
//                                    MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material8.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
//                                    if (sprayList.equals(null) ) {
//                                        System.out.println("喷涂费不存在");
//                                        continue;
//                                    }
//                                    maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //喷涂单价
//                                    maliyVO.setSprayPrice(sprayList.getSprayPrice());//喷涂费
//                                    System.out.println("喷涂单价 = " + maliyVO.getCoatingPrice());
//                                    System.out.println("喷涂费 = " + maliyVO.getSprayPrice());
//                                    //页面不显示
//                                    if (maliyVO.getSprayPrice().equals(new BigDecimal("0"))){
//                                        System.out.println("喷涂费0");
//                                        continue;
//                                    }
//
//
//                                    //加工费
//                                    MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                                    if (proscessList == null) {
//                                        System.out.println("加工费计算出现问题");
//                                        continue;
//                                    }
//                                    maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
//                                    maliyVO.setPrice(proscessList.getPrice()); //切割费
//                                    System.out.println("加工费 = " + maliyVO.getProcessingFee());
//                                    System.out.println("切割费 = " + maliyVO.getPrice());
//                                    if ( maliyVO.getProcessingFee().equals(new BigDecimal("0"))){
//                                        System.out.println("加工费0");
//                                        continue;
//                                    }
//                    //panda
//                                        //产品单价   产品单价=材料费+喷涂费+加工费+切割费
//                                        BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                                        maliyVO.setUnitPrice(productPrice);
//
////                                        如果价格有一个显示为0,则进行下一步操作
////                                        if (!(maliyVO.getSprayPrice().equals(new BigDecimal("0")) || maliyVO.getProcessingFee().equals(new BigDecimal("0")) || maliyVO.getMaterialPrice().equals(new BigDecimal("0")))){
////                                            continue;
////                                        }
//                                        // 若一行物料对应的供应商有多行，supType变为 1
//                                        if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                                            itemInfoDTO.setSupColorType("1");
//                                        } else {
//                                            itemInfoDTO.setSupColorType("0");
//                                        }
//                                        // 设值默认值
//                                        if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
//                                            itemInfoDTOS.add(itemInfoDTO);
//                                        }
//                                        priceVOS = new ArrayList<>();
//                                        PriceVO priceVO = new PriceVO();
//                                        priceVO.setPrice(productPrice); //价格
//                                        priceVO.setSupName(maliyVO.getSupplierName());  //供应商名称
//                                        priceVO.setSupCode(maliyVO.getSupplierCode()); //供应商编码
//                                        priceVO.setSupKey("1"); //供应商默认值
//                                        priceVO.setType("类型"); //类型
//                                        priceVO.setSingleWeight(String.valueOf(maliyVO.getWeight()));
//                                        priceVO.setPriority("1");
//                                        priceVOS.add(priceVO);
//                                    } else {
//                                    MaterialMaliyVO material10 = materialMailyVoMapper.selectSupName10(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
////                                    if (material10 != null) {
////                                        //材料费
////                                        MaterialMaliyVO materialList = getMaterialList(maliyVO, material10.getExternalDiameter(), material10.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
////                                        if (materialList == null) {
////                                            System.out.println("材料费出现问题");
////                                            continue;
////                                        }
////                                        maliyVO.setSupplierName(materialList.getSupplierName());
////                                        maliyVO.setSupplierCode(materialList.getSupplierCode());
////                                        maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
////                                        maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
////                                        maliyVO.setWeight(materialList.getWeight()); //单重
////                                        System.out.println("材料单价 = " + maliyVO.getTheMaterialPrice());
////                                        System.out.println("材料费 = " + maliyVO.getMaterialPrice());
////                                        System.out.println("单重 = " + maliyVO.getWeight());
////                                        //页面不显示
////                                        if (maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
////                                            System.out.println("材料费0");
////                                            continue;
////                                        }
////
////                                        //喷涂费
////                                        MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material10.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), material10.getSupplierCode());
////                                        if (sprayList.equals(null) ) {
////                                            System.out.println("喷涂费不存在");
////                                            continue;
////                                        }
////                                        maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //喷涂单价
////                                        maliyVO.setSprayPrice(sprayList.getSprayPrice());//喷涂费
////                                        System.out.println("喷涂单价 = " + maliyVO.getCoatingPrice());
////                                        System.out.println("喷涂费 = " + maliyVO.getSprayPrice());
////                                        //页面不显示
////                                        if (maliyVO.getSprayPrice().equals(new BigDecimal("0"))){
////                                            System.out.println("喷涂费0");
////                                            continue;
////                                        }
////
////                                        //加工费
////                                        MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
////                                        if (proscessList == null) {
////                                            System.out.println("加工费计算出现问题");
////                                            continue;
////                                        }
////                                        maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
////                                        maliyVO.setPrice(proscessList.getPrice()); //切割费
////                                        System.out.println("加工费 = " + maliyVO.getProcessingFee());
////                                        System.out.println("切割费 = " + maliyVO.getPrice());
////                                        if ( maliyVO.getProcessingFee().equals(new BigDecimal("0"))){
////                                            System.out.println("加工费0");
////                                            continue;
////                                        }
////
////                                            //产品单价   产品单价=材料费+喷涂费+加工费+切割费
////                                            BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
////                                            maliyVO.setUnitPrice(productPrice);
////                                            System.out.println("产品单价 = " + productPrice);
////
////                                            //如果价格有一个显示为0,则进行下一步操作
//////                                            if (maliyVO.getSprayPrice().equals(new BigDecimal("0")) || maliyVO.getProcessingFee().equals(new BigDecimal("0")) || maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
//////                                                continue;
//////                                            }
////
////                                            // 若一行物料对应的供应商有多行，supType变为 1
////                                            if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
////                                                itemInfoDTO.setSupColorType("1");
////                                            } else {
////                                                itemInfoDTO.setSupColorType("0");
////                                            }
////                                            // 设值默认值
////                                            if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
////                                                itemInfoDTOS.add(itemInfoDTO);
////                                            }
////                                            priceVOS = new ArrayList<>();
////                                            PriceVO priceVO = new PriceVO();
////                                            priceVO.setPrice(productPrice); //价格
////                                            priceVO.setSupName(maliyVO.getSupplierName());  //供应商名称
////                                            priceVO.setSupCode(maliyVO.getSupplierCode()); //供应商编码
////                                            priceVO.setSupKey("1"); //供应商默认值
////                                            priceVO.setType("类型"); //类型
////                                            priceVO.setSingleWeight(String.valueOf(maliyVO.getWeight()));
////                                            priceVO.setPriority("1");
////                                            priceVOS.add(priceVO);
////                                    }else {
////                                        System.out.println("在该范围内找到不到该材料");
////                                        continue;
////                                    }
//                                }
//                            }
//                        }
//
//                        //外园大于300mm
//                        if (maliyVO.getExternalDiameter() > 300) {
//                            MaterialMaliyVO material10 = materialMailyVoMapper.selectSupName10(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//                            if (material10 !=null) {
//                                //材料费
//                                MaterialMaliyVO materialList = MaterialListMax(maliyVO, material10.getExternalDiameter(), material10.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                                if (materialList == null) {
//                                    System.out.println("材料费出现问题");
//                                    continue;
//                                }
//                                maliyVO.setSupplierName(materialList.getSupplierName());
//                                maliyVO.setSupplierCode(materialList.getSupplierCode());
//                                maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
//                                maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
//                                maliyVO.setWeight(materialList.getWeight()); //单重
//                                System.out.println("材料单价 = " + maliyVO.getTheMaterialPrice());
//                                System.out.println("材料费 = " + maliyVO.getMaterialPrice());
//                                System.out.println("单重 = " + maliyVO.getWeight());
//                                //页面不显示
//                                if (maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
//                                    System.out.println("材料费0");
//                                    continue;
//                                }
//
//                                //喷涂费
//                                MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material10.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), material10.getSupplierCode());
//                                if (sprayList.equals(null) ) {
//                                    System.out.println("喷涂费不存在");
//                                    continue;
//                                }
//                                maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //喷涂单价
//                                maliyVO.setSprayPrice(sprayList.getSprayPrice());//喷涂费
//                                System.out.println("喷涂单价 = " + maliyVO.getCoatingPrice());
//                                System.out.println("喷涂费 = " + maliyVO.getSprayPrice());
//                                //页面不显示
//                                if (maliyVO.getSprayPrice().equals(new BigDecimal("0"))){
//                                    System.out.println("喷涂费0");
//                                    continue;
//                                }
//
//                                //加工费
//                                MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                                if (proscessList == null) {
//                                    System.out.println("加工费计算出现问题");
//                                    continue;
//                                }
//                                maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
//                                maliyVO.setPrice(proscessList.getPrice()); //切割费
//                                System.out.println("加工费 = " + maliyVO.getProcessingFee());
//                                System.out.println("切割费 = " + maliyVO.getPrice());
//                                if ( maliyVO.getProcessingFee().equals(new BigDecimal("0"))){
//                                    System.out.println("加工费0");
//                                    continue;
//                                }
//
//                                    //产品单价   产品单价=材料费+喷涂费+加工费+切割费
//                                    BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                                    maliyVO.setUnitPrice(productPrice);
//                                     System.out.println("单价 = " + productPrice);
//                                    //如果价格有一个显示为0,则进行下一步操作
////                                    if (maliyVO.getSprayPrice().equals(new BigDecimal("0")) || maliyVO.getProcessingFee().equals(new BigDecimal("0")) || maliyVO.getMaterialPrice().equals(new BigDecimal("0"))){
////                                        continue;
////                                    }
//                                    // 若一行物料对应的供应商有多行，supType变为 1
//                                    if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                                        itemInfoDTO.setSupColorType("1");
//                                    } else {
//                                        itemInfoDTO.setSupColorType("0");
//                                    }
//                                    // 设值默认值
//                                    if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
//                                        itemInfoDTOS.add(itemInfoDTO);
//                                    }
//                                    priceVOS = new ArrayList<>();
//                                    PriceVO priceVO = new PriceVO();
//                                    priceVO.setPrice(productPrice); //价格
//                                    priceVO.setSupName(maliyVO.getSupplierName());  //供应商名称
//                                    priceVO.setSupCode(maliyVO.getSupplierCode()); //供应商编码
//                                    priceVO.setSupKey("1"); //供应商默认值
//                                    priceVO.setType("类型"); //类型
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
//     * 管棒料信息详情
//     * @param itemCode
//     * @param itemName
//     * @return
//     */
//    @Override
//    public List<MaterialMaliyVO> getitemInfoGBL(String itemCode, String itemName) {
//        List<MaterialMaliyVO> maliyVOList = new ArrayList<>();
//        //对物料进行拆解
//        MaterialMaliyVO maliyVO = getItemInfoGuanBangLiao(itemName);
//        //如果拆解不为空
//        if (maliyVO.getItemName() != null) {
//            //遍历供应商
//            List<MaterialMaliyVO> mailyVOS = materialMailyVoMapper.selectMaterilaList(maliyVO);
//            if (mailyVOS.size() > 0) {
//
//                for (int i = 0; i < mailyVOS.size(); i++) {
//
//                    if (maliyVO.getExternalDiameter() < 300) {
//
//                        MaterialMaliyVO material = materialMailyVoMapper.selectItemName(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//
//                        //如果精确找到数据,则按原始价位计算
//                        if (material != null) {
//                            //材料费
//                            MaterialMaliyVO materialList = getMaterialList(maliyVO, material.getExternalDiameter(), material.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                            if (materialList == null) {
//                                System.out.println("材料费出现问题");
//                                continue;
//                            }
//                            maliyVO.setSupplierName(materialList.getSupplierName());
//                            maliyVO.setSupplierCode(materialList.getSupplierCode());
//                            maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
//                            maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
//                            maliyVO.setWeight(materialList.getWeight()); //单重
//                            System.out.println("材料单价 = " + maliyVO.getTheMaterialPrice());
//                            System.out.println("材料费 = " + maliyVO.getMaterialPrice());
//                            System.out.println("单重 = " + maliyVO.getWeight());
//
//                            //喷涂费   1234567
//                            MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
//                            if (sprayList.equals(null) ) {
//                                System.out.println("喷涂费不存在");
//                                continue;
//                            }
//                            maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //喷涂单价
//                            maliyVO.setSprayPrice(sprayList.getSprayPrice());//喷涂费
//                            System.out.println("喷涂单价 = " + maliyVO.getCoatingPrice());
//                            System.out.println("喷涂费 = " + maliyVO.getSprayPrice());
//
//
//                            //加工费
//                            MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                            if (proscessList == null) {
//                                System.out.println("加工费计算出现问题");
//                                continue;
//                            }
//                            maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
//                            maliyVO.setPrice(proscessList.getPrice()); //切割费
//                            System.out.println("加工费 = " + maliyVO.getProcessingFee());
//                            System.out.println("切割费 = " + maliyVO.getPrice());
//
//                            //产品单价   产品单价=材料费+喷涂费+加工费+切割费
//                            BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                            maliyVO.setUnitPrice(productPrice); //单价
//                            System.out.print("产品单价 = 材料费 : " + materialList.getMaterialPrice() + "喷涂费 :" + sprayList.getSprayPrice() + " 加工费 : " + proscessList.getProcessingFee() + " 切割费 : " + proscessList.getPrice() + " = 单价 : " + productPrice + "  ");
//
//                            MaterialMaliyVO vo = new MaterialMaliyVO();
//                            vo.setCoatingPrice(maliyVO.getCoatingPrice()); //喷涂单价
//                            vo.setSprayPrice(maliyVO.getSprayPrice()); //喷涂费
//
//                            vo.setProcessingFee(maliyVO.getProcessingFee()); //加工费
//                            vo.setPrice(maliyVO.getPrice());//切割费
//
//                            vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice().setScale(2,BigDecimal.ROUND_DOWN));//材料单价
//                            vo.setMaterialPrice(maliyVO.getMaterialPrice()); //材料费
//                            vo.setWeight(maliyVO.getWeight()); //单重
//
//                            vo.setUnitPrice(maliyVO.getUnitPrice()); //产品单价
//
//                            vo.setSupplierCode(maliyVO.getSupplierCode()); //供应商编码
//                            vo.setSupplierName(maliyVO.getSupplierName());  //供应商名称
//                            vo.setCoating(maliyVO.getCoating());  //涂层
//                            vo.setItemCode(itemCode); //物料编号
//                            vo.setItemName(itemName); //物料描述
//                            vo.setLength(maliyVO.getLength());  //物料长度
//                            vo.setTheMaterial(maliyVO.getTheMaterial()); //物料材质
//                            vo.setExternalDiameter(maliyVO.getExternalDiameter()); //外径
//                            vo.setInternalDiamete(maliyVO.getInternalDiamete()); //内径
//                            maliyVOList.add(vo);
//                        } else {
//                            //如找不到,按照外径+8  内径-5 进行计算
//                            MaterialMaliyVO material8 = materialMailyVoMapper.selectSupName8(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//                            if (material8 != null) {
//                                //材料费
//                                MaterialMaliyVO materialList = getMaterialList(maliyVO, material8.getExternalDiameter(), material8.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                                if (materialList == null) {
//                                    System.out.println("材料费出现问题");
//                                    continue;
//                                }
//                                maliyVO.setSupplierName(materialList.getSupplierName());
//                                maliyVO.setSupplierCode(materialList.getSupplierCode());
//                                maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
//                                maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
//                                maliyVO.setWeight(materialList.getWeight()); //单重
//                                System.out.println("材料单价 = " + maliyVO.getTheMaterialPrice());
//                                System.out.println("材料费 = " + maliyVO.getMaterialPrice());
//                                System.out.println("单重 = " + maliyVO.getWeight());
//
//                                //喷涂费
//                                MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material8.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
//                                if (sprayList.equals(null) ) {
//                                    System.out.println("喷涂费不存在");
//                                    continue;
//                                }
//                                maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //喷涂单价
//                                maliyVO.setSprayPrice(sprayList.getSprayPrice());//喷涂费
//                                System.out.println("喷涂单价 = " + maliyVO.getCoatingPrice());
//                                System.out.println("喷涂费 = " + maliyVO.getSprayPrice());
//
//                                //加工费
//                                MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                                if (proscessList == null) {
//                                    System.out.println("加工费计算出现问题");
//                                    continue;
//                                }
//                                maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
//                                maliyVO.setPrice(proscessList.getPrice()); //切割费
//                                System.out.println("加工费 = " + maliyVO.getProcessingFee());
//                                System.out.println("切割费 = " + maliyVO.getPrice());
//
//
//                                //产品单价   产品单价=材料费+喷涂费+加工费+切割费
//                                BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                                maliyVO.setUnitPrice(productPrice);
////                                System.out.print("产品单价 = 材料费 : " + materialList.getMaterialPrice() + "喷涂费 :" + sprayList.getSprayPrice() + " 加工费 : " + proscessList.getProcessingFee() + " 切割费 : " + proscessList.getPrice() + " = 单价 : " + productPrice + "  ");
//
//                                MaterialMaliyVO vo = new MaterialMaliyVO();
//
//                                vo.setCoatingPrice(maliyVO.getCoatingPrice()); //喷涂单价
//                                vo.setSprayPrice(maliyVO.getSprayPrice()); //喷涂费
//
//                                vo.setProcessingFee(maliyVO.getProcessingFee()); //加工费
//                                vo.setPrice(maliyVO.getPrice());//切割费
//
//                                vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice().setScale(2,BigDecimal.ROUND_DOWN));//材料单价
//                                vo.setMaterialPrice(maliyVO.getMaterialPrice()); //材料费
//                                vo.setWeight(maliyVO.getWeight()); //单重
//
//                                vo.setUnitPrice(maliyVO.getUnitPrice()); //产品单价
//
//                                vo.setSupplierCode(maliyVO.getSupplierCode()); //供应商编码
//                                vo.setSupplierName(maliyVO.getSupplierName());  //供应商名称
//                                vo.setCoating(maliyVO.getCoating());  //涂层
//                                vo.setItemCode(itemCode); //物料编号
//                                vo.setItemName(itemName); //物料描述
//                                vo.setLength(maliyVO.getLength());  //物料长度
//                                vo.setTheMaterial(maliyVO.getTheMaterial()); //物料材质
//                                vo.setExternalDiameter(maliyVO.getExternalDiameter()); //外径
//                                vo.setInternalDiamete(maliyVO.getInternalDiamete()); //内径
//                                maliyVOList.add(vo);
//                            } else {
//                                MaterialMaliyVO material10 = materialMailyVoMapper.selectSupName10(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//                                if (material10 == null) {
//                                    throw new RuntimeException("error");
//                                }
//                                //材料费
//                                MaterialMaliyVO materialList = getMaterialList(maliyVO, material10.getExternalDiameter(), material10.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                                if (materialList == null) {
//                                    System.out.println("材料费出现问题");
//                                    continue;
//                                }
//                                maliyVO.setSupplierName(materialList.getSupplierName());
//                                maliyVO.setSupplierCode(materialList.getSupplierCode());
//                                maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
//                                maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
//                                maliyVO.setWeight(materialList.getWeight()); //单重
//                                System.out.println("材料单价 = " + maliyVO.getTheMaterialPrice());
//                                System.out.println("材料费 = " + maliyVO.getMaterialPrice());
//                                System.out.println("单重 = " + maliyVO.getWeight());
//
//                                //喷涂费
//                                MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material10.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), material10.getSupplierName());
//                                if (sprayList.equals(null) ) {
//                                    System.out.println("喷涂费不存在");
//                                    continue;
//                                }
//                                maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //喷涂单价
//                                maliyVO.setSprayPrice(sprayList.getSprayPrice());//喷涂费
//                                System.out.println("喷涂单价 = " + maliyVO.getCoatingPrice());
//                                System.out.println("喷涂费 = " + maliyVO.getSprayPrice());
//
//
//                                //加工费
//                                MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                                if (proscessList == null) {
//                                    System.out.println("加工费计算出现问题");
//                                    continue;
//                                }
//                                maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
//                                maliyVO.setPrice(proscessList.getPrice()); //切割费
//                                System.out.println("加工费 = " + maliyVO.getProcessingFee());
//                                System.out.println("切割费 = " + maliyVO.getPrice());
//
//                                //产品单价   产品单价=材料费+喷涂费+加工费+切割费
//                                BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                                maliyVO.setUnitPrice(productPrice);
////                                System.out.print("产品单价 = 材料费 : " + materialList.getMaterialPrice() + "喷涂费 :" + sprayList.getSprayPrice() + " 加工费 : " + proscessList.getProcessingFee() + " 切割费 : " + proscessList.getPrice() + " = 单价 : " + productPrice + "  ");
//
//                                MaterialMaliyVO vo = new MaterialMaliyVO();
//                                vo.setCoatingPrice(maliyVO.getCoatingPrice()); //喷涂单价
//                                vo.setSprayPrice(maliyVO.getSprayPrice()); //喷涂费
//                                vo.setProcessingFee(maliyVO.getProcessingFee()); //加工费
//                                vo.setPrice(maliyVO.getPrice());//切割费
//                                vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice().setScale(2,BigDecimal.ROUND_DOWN));//材料单价
//                                vo.setMaterialPrice(maliyVO.getMaterialPrice()); //材料费
//                                vo.setWeight(maliyVO.getWeight()); //单重
//                                vo.setUnitPrice(maliyVO.getUnitPrice()); //产品单价
//                                vo.setSupplierCode(maliyVO.getSupplierCode()); //供应商编码
//                                vo.setSupplierName(maliyVO.getSupplierName());  //供应商名称
//                                vo.setCoating(maliyVO.getCoating());  //涂层
//                                vo.setItemCode(itemCode); //物料编号
//                                vo.setItemName(itemName); //物料描述
//                                vo.setLength(maliyVO.getLength());  //物料长度
//                                vo.setTheMaterial(maliyVO.getTheMaterial()); //物料材质
//                                vo.setExternalDiameter(maliyVO.getExternalDiameter()); //外径
//                                vo.setInternalDiamete(maliyVO.getInternalDiamete()); //内径
//                                maliyVOList.add(vo);
//                            }
//                        }
//
//                    } else if (maliyVO.getExternalDiameter() > 300) {
//                        MaterialMaliyVO material10 = materialMailyVoMapper.selectSupName10(maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete());
//                        if (material10 !=null) {
//                            //材料费
//                            MaterialMaliyVO materialList = MaterialListMax(maliyVO, material10.getExternalDiameter(), material10.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength());
//                            if (materialList == null) {
//                                System.out.println("材料费出现问题");
//                                continue;
//                            }
//                            maliyVO.setSupplierName(materialList.getSupplierName());
//                            maliyVO.setSupplierCode(materialList.getSupplierCode());
//                            maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
//                            maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
//                            maliyVO.setWeight(materialList.getWeight()); //单重
//                            System.out.println("材料单价 = " + maliyVO.getTheMaterialPrice());
//                            System.out.println("材料费 = " + maliyVO.getMaterialPrice());
//                            System.out.println("单重 = " + maliyVO.getWeight());
//
//
//                            //喷涂费
//                            MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), material10.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), material10.getSupplierCode());
//                            if (sprayList.equals(null) ) {
//                                System.out.println("喷涂费不存在");
//                                continue;
//                            }
//                            maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //喷涂单价
//                            maliyVO.setSprayPrice(sprayList.getSprayPrice());//喷涂费
//                            System.out.println("喷涂单价 = " + maliyVO.getCoatingPrice());
//                            System.out.println("喷涂费 = " + maliyVO.getSprayPrice());
//
//                            //加工费
//                            MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
//                            if (proscessList == null) {
//                                System.out.println("加工费计算出现问题");
//                                continue;
//                            }
//                            maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
//                            maliyVO.setPrice(proscessList.getPrice()); //切割费
//                            System.out.println("加工费 = " + maliyVO.getProcessingFee());
//                            System.out.println("切割费 = " + maliyVO.getPrice());
//
//                            //产品单价   产品单价=材料费+喷涂费+加工费+切割费
//                            BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
//                            maliyVO.setUnitPrice(productPrice);
////                            System.out.print("产品单价 = 材料费 : " + materialList.getMaterialPrice() + "喷涂费 :" + sprayList.getSprayPrice() + " 加工费 : " + proscessList.getProcessingFee() + " 切割费 : " + proscessList.getPrice() + " = 单价 : " + productPrice + "  ");
//
//                            MaterialMaliyVO vo = new MaterialMaliyVO();
//
//                            vo.setCoatingPrice(maliyVO.getCoatingPrice()); //喷涂单价
//                            vo.setSprayPrice(maliyVO.getSprayPrice()); //喷涂费
//
//                            vo.setProcessingFee(maliyVO.getProcessingFee()); //加工费
//                            vo.setPrice(maliyVO.getPrice());//切割费
//
//                            vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice());//材料单价
//                            vo.setMaterialPrice(maliyVO.getMaterialPrice()); //材料费
//                            vo.setWeight(maliyVO.getWeight()); //单重
//
//                            vo.setUnitPrice(maliyVO.getUnitPrice()); //产品单价
//
//                            vo.setSupplierCode(maliyVO.getSupplierCode()); //供应商编码
//                            vo.setSupplierName(maliyVO.getSupplierName());  //供应商名称
//                            vo.setCoating(maliyVO.getCoating());  //涂层
//                            vo.setItemCode(itemCode); //物料编号
//                            vo.setItemName(itemName); //物料描述
//                            vo.setLength(maliyVO.getLength());  //物料长度
//                            vo.setTheMaterial(maliyVO.getTheMaterial()); //物料材质
//                            vo.setExternalDiameter(maliyVO.getExternalDiameter()); //外径
//                            vo.setInternalDiamete(maliyVO.getInternalDiamete()); //内径
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
//     * 材料费 外径>300
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
//        Double itemprice = null;  //查找材质单价
//        if (theMaterial == "20"){
//            maliyVO.setTheMaterialPrice(BigDecimal.valueOf(9.5));
//        }
//        if (externalDiameter < 148 && (theMaterial != "20")) {
//            range = "148";
//            itemprice = materialMailyVoMapper.selectTheMaterialPrice(theMaterial, range,list.getSupplierCode());
//            if (itemprice == null){
//                maliyVO.setTheMaterialPrice(new BigDecimal("0")); //材料单价
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
//                maliyVO.setTheMaterialPrice(new BigDecimal("0")); //材料单价
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
//                maliyVO.setTheMaterialPrice(new BigDecimal("0")); //材料单价
//                maliyVO.setMaterialPrice(new BigDecimal("0"));
//                maliyVO.setSupplierName(list.getSupplierName());
//                maliyVO.setSupplierCode(list.getSupplierCode());
//                return maliyVO;
//            }
//            maliyVO.setTheMaterialPrice(BigDecimal.valueOf(itemprice));
//        }
//        System.out.println("价格 = " + maliyVO.getTheMaterialPrice());
//        //计算单重
//        BigDecimal price = new BigDecimal(externalDiameter).multiply(BigDecimal.valueOf(externalDiameter)).subtract(BigDecimal.valueOf(internalDiamete).multiply(BigDecimal.valueOf(internalDiamete)));
//        BigDecimal   weight =   new BigDecimal(String.valueOf(price)).multiply(BigDecimal.valueOf(PI)).divide(BigDecimal.valueOf(4)).multiply(BigDecimal.valueOf(length)).multiply(BigDecimal.valueOf(8)).divide(BigDecimal.valueOf(1000000));
//        //计算材料费
//        BigDecimal MaterialPrice = new BigDecimal(String.valueOf(weight)).multiply(BigDecimal.valueOf(itemprice)).multiply(BigDecimal.valueOf(1.2));
//        maliyVO.setWeight(weight.setScale(2,BigDecimal.ROUND_DOWN));  //单重
//        maliyVO.setSupplierName(list.getSupplierName());
//        maliyVO.setSupplierCode(list.getSupplierCode());
//        maliyVO.setMaterialPrice(MaterialPrice.setScale(2,BigDecimal.ROUND_DOWN)); //材料费
//        maliyVO.setTheMaterialPrice(BigDecimal.valueOf(itemprice)); //材料单价
//        return maliyVO;
//    }
//
//    /**
//     * 加工费
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
//            maliyVO.setPrice(new BigDecimal("0"));  //切割费
//            maliyVO.setProcessingFee(new BigDecimal("0"));  //加工费
//            return maliyVO;
//        }
//        //切割费
//        if (length < 200) {
//            //有切割费
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
//                maliyVO.setPrice(BigDecimal.valueOf(18)); //切割费
//            }
//        }else {
//            maliyVO.setPrice(new BigDecimal("0"));
//        }
//        maliyVO.setProcessingFee(vo.getProcessingFee().setScale(2,BigDecimal.ROUND_DOWN)); //加工费
//        return maliyVO;
//    }
//
//    /**
//     * 计算喷涂费
//     * @param maliyVO
//     * @param length
//     * @param internalDiamete
//     * @param coating
//     * @param theMaterial
//     * @return
//     */
//    private MaterialMaliyVO getSprayList(MaterialMaliyVO maliyVO, Integer length, Integer internalDiamete, String coating, String theMaterial,String supplierCode) {
//        //计算喷涂费
//        Double price = materialMailyVoMapper.selectBySprayPrice(coating, supplierCode, theMaterial);
//        if (price == null){
//            maliyVO.setSprayPrice(new BigDecimal("0"));  //喷涂费
//            maliyVO.setCoatingPrice(new BigDecimal("0"));
//            return maliyVO;
//        }
//        maliyVO.setCoatingPrice(BigDecimal.valueOf(price));  //喷涂单价
//        //计算喷涂费
//        BigDecimal sprayPrice = new BigDecimal(internalDiamete).multiply(BigDecimal.valueOf(PI)).multiply(BigDecimal.valueOf(length)).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(price));
//        maliyVO.setSprayPrice(sprayPrice.setScale(2,BigDecimal.ROUND_DOWN));  //喷涂费
//        return maliyVO;
//    }
//
//    /**
//     * 计算材料费  外径<300
//     * @param externalDiameter
//     * @param internalDiamete
//     * @param theMaterial
//     * @return
//     */
//    private MaterialMaliyVO getMaterialList(MaterialMaliyVO maliyVO,Integer externalDiameter, Integer internalDiamete, String theMaterial, Integer length) {
//        MaterialMaliyVO list = materialMailyVoMapper.selectMaterialPrice(externalDiameter, internalDiamete);
//        String range = "";
//        //判断外径的取值范围    优化  加一个冒泡排序
//        Double itemprice = null;  //查找材质单价
//        if (theMaterial == "20"){
//            maliyVO.setTheMaterialPrice(BigDecimal.valueOf(9.5));
//        }
//        if (externalDiameter < 148 && (theMaterial != "20")) {
//            range = "148";
//             itemprice = materialMailyVoMapper.selectTheMaterialPrice(theMaterial, range,list.getSupplierCode());
//             if (itemprice == null){
//                 maliyVO.setTheMaterialPrice(new BigDecimal("0")); //材料单价
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
//                maliyVO.setTheMaterialPrice(new BigDecimal("0")); //材料单价
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
//                maliyVO.setTheMaterialPrice(new BigDecimal("0")); //材料单价
//                maliyVO.setMaterialPrice(new BigDecimal("0"));
//                maliyVO.setSupplierName(list.getSupplierName());
//                maliyVO.setSupplierCode(list.getSupplierCode());
//                return maliyVO;
//            }
//            maliyVO.setTheMaterialPrice(BigDecimal.valueOf(itemprice));
//        }
//        System.out.println("价格 = " + maliyVO.getTheMaterialPrice());
//        //计算单重
//        BigDecimal price = new BigDecimal(externalDiameter).multiply(BigDecimal.valueOf(externalDiameter)).subtract(BigDecimal.valueOf(internalDiamete).multiply(BigDecimal.valueOf(internalDiamete)));
//        BigDecimal   weight =   new BigDecimal(String.valueOf(price)).multiply(BigDecimal.valueOf(PI)).divide(BigDecimal.valueOf(4)).multiply(BigDecimal.valueOf(length)).multiply(BigDecimal.valueOf(8)).divide(BigDecimal.valueOf(1000000));
//        //计算材料
//        BigDecimal MaterialPrice = new BigDecimal(String.valueOf(weight)).multiply(maliyVO.getTheMaterialPrice()).multiply(BigDecimal.valueOf(1.2));
//        System.out.println("MaterialPrice = " + MaterialPrice);
//        maliyVO.setWeight(weight.setScale(2,BigDecimal.ROUND_DOWN));  //单重
//        maliyVO.setSupplierName(list.getSupplierName());
//        maliyVO.setSupplierCode(list.getSupplierCode());
//        maliyVO.setMaterialPrice(MaterialPrice.setScale(2,BigDecimal.ROUND_DOWN)); //材料费
//        maliyVO.setTheMaterialPrice(BigDecimal.valueOf(itemprice)); //材料单价
//        return maliyVO;
//    }
//
//    /**
//     * 获取锻件信息，弹出dialog
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
//        // 规格 & 材质
//        String standards = "";
//        String material = "";
//
//        // 拆解
//        ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(itemName);
//        if (itemInfoEntity.getMaterial() == null) { // 正常拆解如果拆不出来，则 继续拆 材质
//            itemInfoEntity = getMaterialItemInfoOfDuanJian(itemName);
//            if(itemInfoEntity==null) { // 如果拆解还拆不出来，就返回null
//                return null;
//            }
//        }
//        // 规格 & 材质
//        standards = itemInfoEntity.getSize() + "-" + itemInfoEntity.getPound();
//        material = itemInfoEntity.getMaterial();
//
//        // 分别处理计算价格并返回
//        List<ItemInfoDTO> itemInfoDTOSOfOld = handleOfDJ(u9Prs, standards,material);
//        List<ItemInfoDTO> itemInfoDTOSOfNew = handleOfDJOfNew(u9Prs, standards,material);
//
//        // 新旧逻辑合并到旧的list里面
//        if (itemInfoDTOSOfNew.size() > 0 && itemInfoDTOSOfOld.size()>0) {
//            itemInfoDTOSOfNew.get(0).getSupAndPriceList().stream().forEach(itemInfoDTO -> {
//                itemInfoDTOSOfOld.get(0).getSupAndPriceList().add(itemInfoDTO);
//            });
//        } else if(itemInfoDTOSOfNew.size() > 0 && itemInfoDTOSOfOld.size()==0){
//            itemInfoDTOSOfOld.addAll(itemInfoDTOSOfNew);
//        }
//
//        // 如果可以查到信息,处理数据进入到 最终的信息表中
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
//     * 锻件信息报表
//     * @param SubmitPriceReq
//     * @return
//     */
//    @Override
//    public IPage<ItemInfoEntityDJReport> getItemInfoOfDJVOReport(IPage<ItemInfoEntityDJReport> page, SubmitPriceReq submitPriceReq) {
//        // 以下3行代码，仅在出现问题清空表时使用，平时注释即可。
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
//        // 锻件基础信息
//        ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = this.baseMapper.selectBasicItemInfoOfDJ(u9Prs.getItemCode());
//        if(itemInfoEntityBasOfDJ ==null){
//            return itemInfoDTOS;
//        }
//
//        if(itemInfoEntityBasOfDJ.getBigOuterSize()!=null && !itemInfoEntityBasOfDJ.getBigOuterSize().isEmpty() ) {
//            bigOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigOuterSize()).doubleValue();  // 大头外径
//        }
//        if(itemInfoEntityBasOfDJ.getBigHeightSize()!=null && !itemInfoEntityBasOfDJ.getBigHeightSize().isEmpty() ) {
//            bigHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigHeightSize()).doubleValue();  // 大头高度
//        }
//        if(itemInfoEntityBasOfDJ.getSmallOuterSize()!=null && !itemInfoEntityBasOfDJ.getSmallOuterSize().isEmpty() ) {
//            smallOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getSmallOuterSize()).doubleValue();// 小头外径
//        }
//        if(itemInfoEntityBasOfDJ.getTotalHeightSize()!=null && !itemInfoEntityBasOfDJ.getTotalHeightSize().isEmpty() ) {
//            totalHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getTotalHeightSize()).doubleValue(); // 总高度
//        }
//
//        if(itemInfoEntityBasOfDJ.getInnerSize()!=null && !itemInfoEntityBasOfDJ.getInnerSize().isEmpty() ) {
//            innerSize = Double.valueOf(itemInfoEntityBasOfDJ.getInnerSize()).doubleValue();// 内径
//        }
//
//        if(itemInfoEntityBasOfDJ.getOuterSize()!=null && !itemInfoEntityBasOfDJ.getOuterSize().isEmpty() ) {
//            outerSize = Double.valueOf(itemInfoEntityBasOfDJ.getOuterSize()).doubleValue();  // 外径
//        }
//        if(itemInfoEntityBasOfDJ.getHeightSize()!=null && !itemInfoEntityBasOfDJ.getHeightSize().isEmpty() ) {
//            heightSize = Double.valueOf(itemInfoEntityBasOfDJ.getHeightSize()).doubleValue();  // 高度
//        }
//
//        String formulaType =  itemInfoEntityBasOfDJ.getType();
//        if("A".equals(formulaType)){ //大小头公式
//            outSizeToSearch = bigOuterSize;
//        } else {
//            outSizeToSearch = outerSize;
//        }
//
//        // 余量选择
//        if(itemInfoEntityBasOfDJ.getRes().equals("fm")){ // (阀帽的情况）
//            itemInfoEntityResFMOfDJList = this.baseMapper.selectResItemInfoOfFm(standards,material);
//            if(itemInfoEntityResFMOfDJList.size()>0){
//                itemInfoEntityResFMOfDJList.stream().forEach(itemInfoEntityResFMOfDJ ->{
//                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResFMOfDJ,ItemInfoEntityResALLOfDJ.class);
//                    itemInfoEntityResALLOfDJList.add(iteminfo);
//                });
//            }
//        } else if(itemInfoEntityBasOfDJ.getRes().equals("zfl")){// (中法兰的情况)
//            itemInfoEntityResZFLOfDJList = this.baseMapper.selectResItemInfoOfZfl(outSizeToSearch,material);
//            if(itemInfoEntityResZFLOfDJList.size()>0){
//                itemInfoEntityResZFLOfDJList.stream().forEach(itemInfoEntityResFMOfDJ ->{
//                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResFMOfDJ,ItemInfoEntityResALLOfDJ.class);
//                    itemInfoEntityResALLOfDJList.add(iteminfo);
//                });
//            }
//        } else {// (自由锻的情况)
//            itemInfoEntityResZYDOfDJList = this.baseMapper.selectResItemInfoOfZyd(outSizeToSearch,material);
//            if(itemInfoEntityResZYDOfDJList.size()>0){
//                itemInfoEntityResZYDOfDJList.stream().forEach(itemInfoEntityResZYDOfDJ ->{
//                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResZYDOfDJ,ItemInfoEntityResALLOfDJ.class);
//                    itemInfoEntityResALLOfDJList.add(iteminfo);
//                });
//            }
//        }
//
//        // 遍历每一家供应商
//        if (itemInfoEntityResALLOfDJList.size() > 0) {
//            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
//            List<PriceVO> priceVOS = new ArrayList<>();
//            // id，用来识别前台的选中行
//            itemInfoDTO.setId(u9Prs.getId().toString());
//            itemInfoDTO.setItemCode(u9Prs.getItemCode());
//            itemInfoDTO.setItemName(u9Prs.getItemName());
//
//            // 根据每一家供应商计算价格
//            for (ItemInfoEntityResALLOfDJ iteminfo : itemInfoEntityResALLOfDJList) {
//                Double outerRemain = Double.valueOf(iteminfo.getOuterRemain()).doubleValue(); // 外径余量
//                Double innerRemain = Double.valueOf(iteminfo.getInnerRemain()).doubleValue(); // 内径余量
//                Double heightRemain = Double.valueOf(iteminfo.getHeightRemain()).doubleValue(); // 高度余量
//
//                Double weight = 0D;
//                Double weightOfBasic = 0D;
//
//                if (itemInfoEntityBasOfDJ.getType().equals("A")){ // --大小头公式--
//                    // 重量=(((大头外径/2+外径余量/2)^2 – (内径/2-内径余量/2)^2 )) * PI * (大头高度+高度余量)+((小头外径/2 + 外径余量/2)^2-(内径/2 – 内径余量/2)^2)*PI*(总高度-大头高度))*7.856/1000000
//                    if ((innerSize  - innerRemain) >= 80) {
//                        weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2) )* PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * PI * (totalHeightSize - bigHeightSize)) * 7.856 / 1000000;
//                    } else {
//                        weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) * PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2)) * PI * (totalHeightSize - bigHeightSize))) * 7.856 / 1000000;
//                    }
//                } else { // --常规公式--
//                    // 重量 = PI*((外圆直径/2 +外圆余量/2)^2 – (内孔直径/2 - 内孔余量/2)^2)*(高度 + 高度余量)*7.856/1000000
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
//                // 单价
//                String singlePrice = this.baseMapper.selectSinglePrice(iteminfo.getSupCode(),material);
//                if(singlePrice==null) {
//                    return itemInfoDTOS;
//                }
//
//                // 价格 = 重量 * 单价
//                Double price = weight*Double.valueOf(singlePrice);
//
//                // 设值单价和供应商的连带关系
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
//            // 若一行物料对应的供应商有多行，supType变为 1
//            if ((itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                itemInfoDTO.setSupColorType("1");
//            } else {
//                itemInfoDTO.setSupColorType("0");
//            }
//            // 赋值（返回值）
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
//        Double height = 0D; // 高度
//        Double waijing = 0D; // 外径
//        Double neijing = 0D; // 内径
//
//
//        // 锻件基础信息
//        ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = this.baseMapper.selectBasicItemInfoOfDJ(u9Prs.getItemCode());
//        if(itemInfoEntityBasOfDJ ==null){
//            return itemInfoDTOS;
//        }
//
//        if(itemInfoEntityBasOfDJ.getBigOuterSize()!=null && !itemInfoEntityBasOfDJ.getBigOuterSize().isEmpty() ) {
//            bigOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigOuterSize()).doubleValue();  // 大头外径
//        }
//        if(itemInfoEntityBasOfDJ.getBigHeightSize()!=null && !itemInfoEntityBasOfDJ.getBigHeightSize().isEmpty() ) {
//            bigHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigHeightSize()).doubleValue();  // 大头高度
//        }
//        if(itemInfoEntityBasOfDJ.getSmallOuterSize()!=null && !itemInfoEntityBasOfDJ.getSmallOuterSize().isEmpty() ) {
//            smallOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getSmallOuterSize()).doubleValue();// 小头外径
//        }
//        if(itemInfoEntityBasOfDJ.getTotalHeightSize()!=null && !itemInfoEntityBasOfDJ.getTotalHeightSize().isEmpty() ) {
//            totalHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getTotalHeightSize()).doubleValue(); // 总高度
//        }
//
//        if(itemInfoEntityBasOfDJ.getInnerSize()!=null && !itemInfoEntityBasOfDJ.getInnerSize().isEmpty() ) {
//            innerSize = Double.valueOf(itemInfoEntityBasOfDJ.getInnerSize()).doubleValue();// 内径
//        }
//
//        if(itemInfoEntityBasOfDJ.getOuterSize()!=null && !itemInfoEntityBasOfDJ.getOuterSize().isEmpty() ) {
//            outerSize = Double.valueOf(itemInfoEntityBasOfDJ.getOuterSize()).doubleValue();  // 外径
//        }
//        if(itemInfoEntityBasOfDJ.getHeightSize()!=null && !itemInfoEntityBasOfDJ.getHeightSize().isEmpty() ) {
//            heightSize = Double.valueOf(itemInfoEntityBasOfDJ.getHeightSize()).doubleValue();  // 高度
//        }
//
//        String formulaType =  itemInfoEntityBasOfDJ.getType();
//        if("A".equals(formulaType)){ //大小头公式
//            outSizeToSearch = bigOuterSize;
//            height = totalHeightSize;
//            waijing = bigOuterSize;
//            neijing = innerSize;
//        } else { // 常规
//            outSizeToSearch = outerSize;
//            height = heightSize;
//            waijing = outerSize;
//            neijing = innerSize;
//        }
//
//        // 余量选择
//        itemInfoEntityResCommonOfDJList = this.baseMapper.selectResItemInfoOfNew(outSizeToSearch,material,height);
//        if(itemInfoEntityResCommonOfDJList.size()>0){
//            itemInfoEntityResCommonOfDJList.stream().forEach(itemInfoEntityResCommonOfDJ ->{
//                ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResCommonOfDJ,ItemInfoEntityResALLOfDJ.class);
//                itemInfoEntityResALLOfDJList.add(iteminfo);
//            });
//        }
//
//        // 遍历每一家供应商
//        if (itemInfoEntityResALLOfDJList.size() > 0) {
//            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
//            List<PriceVO> priceVOS = new ArrayList<>();
//            // id，用来识别前台的选中行
//            itemInfoDTO.setId(u9Prs.getId().toString());
//            itemInfoDTO.setItemCode(u9Prs.getItemCode());
//            itemInfoDTO.setItemName(u9Prs.getItemName());
//
//            // 根据每一家供应商计算价格
//            for (ItemInfoEntityResALLOfDJ iteminfo : itemInfoEntityResALLOfDJList) {
//                Double outerRemain = Double.valueOf(iteminfo.getOuterRemain()).doubleValue(); // 外径余量
//                Double innerRemain = Double.valueOf(iteminfo.getInnerRemain()).doubleValue(); // 内径余量
//                Double heightRemain = Double.valueOf(iteminfo.getHeightRemain()).doubleValue(); // 高度余量
//
//                Double bihou = (waijing - neijing)/2;
//                if (waijing >= 500 && neijing >= 300 && bihou >= 55 && height >= 110 ) { // 是否是 碾环
//                    if(innerRemain<=15) { // 小于等于15的时候为15
//                        innerRemain = 15D;
//                    } else { // 否则为下述条件
//                        innerRemain = outerRemain + 2;
//                    }
//                }
//
//                Double weight = 0D;
//                Double weightOfBasic = 0D;
//
//                if (itemInfoEntityBasOfDJ.getType().equals("A")){ // --大小头公式--
//                    // 重量=(((大头外径/2+外径余量/2)^2 – (内径/2-内径余量/2)^2 )) * PI * (大头高度+高度余量)+((小头外径/2 + 外径余量/2)^2-(内径/2 – 内径余量/2)^2)*PI*(总高度-大头高度))*7.856/1000000
//                    if ((innerSize  - innerRemain) >= 80) {
//                        weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2) )* PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * PI * (totalHeightSize - bigHeightSize)) * 7.856 / 1000000;
//                    } else {
//                        weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) * PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2)) * PI * (totalHeightSize - bigHeightSize))) * 7.856 / 1000000;
//                    }
//                } else { // --常规公式--
//                    // 重量 = PI*((外圆直径/2 +外圆余量/2)^2 – (内孔直径/2 - 内孔余量/2)^2)*(高度 + 高度余量)*7.856/1000000
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
//                // 单价
//                String singlePrice = this.baseMapper.selectSinglePrice(iteminfo.getSupCode(),material);
//                if(singlePrice==null) {
//                    return itemInfoDTOS;
//                }
//
//                // 价格 = 重量 * 单价
//                Double price = weight*Double.valueOf(singlePrice);
//
//                // 设值单价和供应商的连带关系
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
//            // 若一行物料对应的供应商有多行，supType变为 1
//            if ((itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
//                itemInfoDTO.setSupColorType("1");
//            } else {
//                itemInfoDTO.setSupColorType("0");
//            }
//            // 赋值（返回值）
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
//            // 锻件基础信息
//            ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = submitPriceReq.getItemInfoEntityBasOfDJList().get(0);
//            if(this.baseMapper.selectBasicItemInfoOfDJ(itemInfoEntityBasOfDJ.getItemCode())==null){
//                this.baseMapper.insertBasicItemInfoOfDJ(itemInfoEntityBasOfDJ);
//            } else {
//                this.baseMapper.updateBasicItemInfoOfDJ(itemInfoEntityBasOfDJ);
//                String account = getUser().getAccount();
//                String time = DateUtil.format(new Date(), "yyyy-MM-dd");
//                this.baseMapper.deleteDjInfoReport(itemInfoEntityBasOfDJ.getItemCode(),account,time); // 更新时、需要删除原有的最终信息表，待自动获取信息后，再插入新的。
//            }
//        }
//        return true;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean addBasicItemInfoOfDJ(SubmitPriceReq submitPriceReq) {
//        if (submitPriceReq.getItemInfoEntityBasOfDJList() != null) {
//            // 锻件基础信息
//            ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = submitPriceReq.getItemInfoEntityBasOfDJList().get(0);
//            if(this.baseMapper.selectBasicItemInfoOfDJ(itemInfoEntityBasOfDJ.getItemCode())==null){
//                if(itemInfoEntityBasOfDJ.getItemCode()==null || itemInfoEntityBasOfDJ.getItemCode().isEmpty() ) {
//                    String itemCode = this.baseMapper.getItemInfoByItemName(itemInfoEntityBasOfDJ.getItemName());
//                    if (itemCode == null){
//                        throw new RuntimeException("根据该物料的 物料描述，找不到对应的 物料号，请确认。");
//                    }
//                    itemInfoEntityBasOfDJ.setItemCode(itemCode);
//                }
//                if(itemInfoEntityBasOfDJ.getItemName()==null || itemInfoEntityBasOfDJ.getItemName().isEmpty() ) {
//                    String itemName = this.baseMapper.getItemInfoByItemCode(itemInfoEntityBasOfDJ.getItemCode());
//                    if (itemName == null){
//                        throw new RuntimeException("根据该物料的 物料号，找不到对应的 物料描述，请确认。");
//                    }
//                    itemInfoEntityBasOfDJ.setItemName(itemName);
//                }
//                this.baseMapper.insertBasicItemInfoOfDJ(itemInfoEntityBasOfDJ);
//                this.getItemInfoOfDJVO(itemInfoEntityBasOfDJ.getItemCode(),itemInfoEntityBasOfDJ.getItemName());
//                Integer count = this.baseMapper.itemCodeOfDJIsExisted(itemInfoEntityBasOfDJ.getItemCode());
//                if (count == 0) { // 最终信息表中没有该数据
//                    throw new RuntimeException("该物料号的基础信息 经过程序计算,不能算出最后结果，请确认。");
//                }
//            } else {
//                throw new RuntimeException("该物料号的基础信息已经存在了，请确认。");
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
//                    item.setType("大小头公式");
//                    item.setOuterSize("");
//                    item.setHeightSize("");
//                } else {
//                    item.setType("常规公式");
//                    item.setBigOuterSize("");
//                    item.setBigHeightSize("");
//                    item.setSmallOuterSize("");
//                    item.setTotalHeightSize("");
//                }
//
//                if(item.getRes().equals("fm")) {
//                    item.setRes("阀帽");
//                } else if(item.getRes().equals("zyd")){
//                    item.setRes("自由锻");
//                } else {
//                    item.setRes("中法兰");
//                }
//            });
//        }
//        ExcelUtils.defaultExport(itemInfoEntityDJReportList, ItemInfoEntityDJReport.class, "物料获取信息表" + DateUtil.formatDate(new Date()), response);
//    }
//
//
//}
