package org.springblade.modules.outpr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.google.api.client.util.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.outpr.dto.OutPrItemDTO;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.entity.OutPrItemProcessEntity;
import org.springblade.modules.outpr.mapper.OutPrItemMapper;
import org.springblade.modules.outpr.service.IOutPrItemArtifactService;
import org.springblade.modules.outpr.service.IOutPrItemProcessService;
import org.springblade.modules.outpr.service.IOutPrItemService;
import org.springblade.modules.outpr.service.IOutPrService;
import org.springblade.modules.outpr.vo.OutPrItemVO;
import org.springblade.modules.po.entity.OutIoEntity;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.mapper.OutIoMapper;
import org.springblade.modules.po.service.IIoOutService;
import org.springblade.modules.po.service.IIoService;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.pr.dto.*;
import org.springblade.modules.pr.entity.ItemInfoEntityOfWW;
import org.springblade.modules.pr.entity.ItemInfoEntityOfZDJ;
import org.springblade.modules.pr.mapper.U9PrMapper;
import org.springblade.modules.pr.vo.PriceVO;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import static org.springblade.common.utils.ItemAnalysisUtil.*;
import static org.springblade.core.secure.utils.AuthUtil.getUser;

/**
 * ???????????????
 * @author Will
 */
@Service
public
class OutPrItemServiceImpl extends BaseServiceImpl<OutPrItemMapper, OutPrItemEntity> implements IOutPrItemService{

    @Autowired
    IOutPrItemProcessService outPrItemProcessService;

    @Autowired
    IOutPrItemArtifactService outPrItemArtifactService;

    @Autowired
    ISupplierService supplierService;

    @Autowired
    OutIoMapper outIoMapper;

    @Autowired
    U9PrMapper u9PrMapper;

    @Autowired
    IIoOutService iIoOutService;

    @Autowired
    IOutPrService outPrService;

    @Autowired
    @Lazy
    IPoItemService poItemService;

    @Autowired
    @Lazy
    IParamService paramService;

    @Autowired
    PaginationInterceptor paginationInterceptor;

    @Autowired
    @Lazy
    IItemService itemService;

    private
    QueryWrapper<OutPrItemEntity> getQueryMapper(OutPrItemDTO dto){
        QueryWrapper<OutPrItemEntity> queryWrapper = Wrappers.<OutPrItemEntity>query()
                .eq(dto.getStatuss() != null && !dto.getStatuss().isEmpty(), "status", dto.getStatuss())
                .eq(dto.getStatuss() == null || dto.getStatuss().isEmpty(), "status", STATUS_FLOW)
                .like(StringUtils.isNotBlank(dto.getPrCode()), "pr_code", dto.getPrCode())
                .like(StringUtils.isNotBlank(dto.getItemCode()) , "item_code", dto.getItemCode())
                .like( StringUtils.isNotBlank(dto.getItemName()), "item_name", dto.getItemName())
                .isNotNull(dto.getPurchCode() == null,"purch_code")
                .isNotNull(true,"flow_cause")
                .eq(dto.getPurchCode() != null, "purch_code", dto.getPurchCode());
        if(StringUtil.isNotBlank(dto.getCreateTimeStart()) ){
            queryWrapper.ge("create_time", dto.getCreateTimeStart());
        }
        if(StringUtil.isNotBlank(dto.getCreateTimeEnd())){
            queryWrapper.le("create_time", dto.getCreateTimeEnd());
        }
        return queryWrapper;
    }

    /**
     * @return
     */
    @Override
    public
    OutPrItemDTO getDtoById(Long id){
        OutPrItemEntity entity = getById(id);
        OutPrItemDTO    dto    = BeanUtil.copy(entity, OutPrItemDTO.class);

        QueryWrapper<OutIoEntity> ioQw = Condition.getQueryWrapper(new OutIoEntity()).eq("pr_id", id);
        dto.setIoTotal(iIoOutService.count(ioQw));
        ioQw.eq("status", IIoService.STATUS_QUOTED);
        dto.setQuotedIoTotal(iIoOutService.count(ioQw));
        dto.setHighestPrice(poItemService.getHighestPrice(dto.getItemCode()));
        dto.setLowestPrice(poItemService.getLowestPrice(dto.getItemCode()));
        PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(),dto.getItemName());
        if(poItemEntity!=null) {
            dto.setLastPrice(poItemEntity.getPrice());
            dto.setLastSupName(poItemEntity.getSupName());
        }
        return dto;
    }


    @Override
    public
    IPage<OutPrItemVO> voPage(Query query, OutPrItemDTO queryDto){
        //????????????????????????500
        paginationInterceptor.setLimit(-1);
        String mRoleId  = paramService.getValue("purch_manager.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            queryDto.setPurchCode(getUser().getAccount());
            if(queryDto.getStatuss().isEmpty()){
                queryDto.setStatuss(STATUS_FLOW.toString());
            }
        } else {
            if(queryDto.getStatuss().isEmpty()){
                queryDto.setStatuss(STATUS_WINBID.toString());
            }
        }

        IPage<OutPrItemVO> retPage = null;
        if(STATUS_FLOW.toString().equals(queryDto.getStatuss()) || queryDto.getStatuss().isEmpty()){
             retPage = this.baseMapper.selectOutPrFlowPage(Condition.getPage(query), queryDto);
        } else {
             retPage = this.baseMapper.selectOutPrCheckPage(Condition.getPage(query), queryDto);
        }

        if(retPage.getRecords() == null){
            return retPage;
        }
        for(OutPrItemVO dto : retPage.getRecords()){
            // ?????????????????????U9???????????????????????????
            dto.setPromiseDate(dto.getReqDate());
            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(),dto.getItemName());
            if(poItemEntity!=null){
                dto.setLastPrice(poItemEntity.getPrice());
                //dto.setLastSupName(poItemEntity.getSupName());
            }
            dto.setProcessList(outPrItemProcessService.getListByItemId(dto.getId()));
        }
        return retPage;
    }


    @Override
    public
    IPage<OutPrItemVO> inquiryPage(Query query, OutPrItemDTO queryDto){
        //????????????????????????500
        paginationInterceptor.setLimit(-1);
        String mRoleId  = paramService.getValue("purch_manager.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            queryDto.setPurchCode(getUser().getAccount());
        } else {
            if(queryDto.getStatuss().isEmpty()){
                queryDto.setStatuss(STATUS_WINBID.toString());
            }
        }
        IPage<OutPrItemVO> retPage = this.baseMapper.selectOutPrInquiryTabPage(Condition.getPage(query), queryDto);

        if(retPage.getRecords() == null){
            return retPage;
        }
        for(OutPrItemVO dto : retPage.getRecords()){
            // ?????????????????????U9???????????????????????????
            dto.setPromiseDate(dto.getReqDate());
            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(),dto.getItemName());
            if(poItemEntity!=null){
                dto.setLastPrice(poItemEntity.getPrice());
                //dto.setLastSupName(poItemEntity.getSupName());
            }
            dto.setProcessList(outPrItemProcessService.getListByItemId(dto.getId()));
        }
        return retPage;
    }

    @Override
    public
    IPage<OutPrItemVO> voBidPage(Query query, OutPrItemDTO queryDto){
        //????????????????????????500
        paginationInterceptor.setLimit(-1);
        String mRoleId  = paramService.getValue("purch_manager.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            queryDto.setPurchCode(getUser().getAccount());
        }

        IPage<OutPrItemVO> retPage= this.baseMapper.selectOutPrCheckPage(Condition.getPage(query), queryDto);

        if(retPage.getRecords() == null){
            return retPage;
        }

        List<OutPrItemVO> outPrItemVOS = new ArrayList<>();

        for(OutPrItemVO dto : retPage.getRecords()){
            // ?????????????????????U9???????????????????????????
            dto.setPromiseDate(dto.getReqDate());
            List<OutPrItemProcessEntity> processList = outPrItemProcessService.getListByItemId(dto.getId());
            DecimalFormat df = new DecimalFormat("0.00");
            BigDecimal price = dto.getQuotePrice();
            Integer size = processList.size();

            BigDecimal averagePrice = price.divide(new BigDecimal(size.toString()),2,BigDecimal.ROUND_HALF_UP);

            int i = 1;
            for(OutPrItemProcessEntity prItemProcessEntity : processList){
                OutPrItemVO vo = BeanUtil.copy(dto, OutPrItemVO.class);
                vo.setPrLn(prItemProcessEntity.getPrLn());
                vo.setProcessCode(prItemProcessEntity.getProcessCode());
                if (i == size) {
                    vo.setQuotePrice(price.subtract(averagePrice.multiply(new BigDecimal(size-1))));
                    i = 1;
                } else {
                    vo.setQuotePrice(averagePrice);
                    i++;
                }
                outPrItemVOS.add(vo);
            }
        }
        retPage.setRecords(outPrItemVOS);
        retPage.setTotal(outPrItemVOS.size());
        return retPage;
    }


    @Override
    public
    IPage<OutPrItemVO> voInquiryBidPage(Query query, OutPrItemDTO queryDto){
        //????????????????????????500
        paginationInterceptor.setLimit(-1);
        String mRoleId  = paramService.getValue("purch_manager.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            queryDto.setPurchCode(getUser().getAccount());
        }

        IPage<OutPrItemVO> retPage= this.baseMapper.selectOutPrInquiryBidPage(Condition.getPage(query), queryDto);

        if(retPage.getRecords() == null){
            return retPage;
        }

        List<OutPrItemVO> outPrItemVOS = new ArrayList<>();

        for(OutPrItemVO dto : retPage.getRecords()){
            // ?????????????????????U9???????????????????????????
            dto.setPromiseDate(dto.getReqDate());
            List<OutPrItemProcessEntity> processList = outPrItemProcessService.getListByItemId(dto.getId());
            Integer price = dto.getQuotePrice().intValue();
            Integer size = processList.size();
            Integer remainder = price % size;
            Integer average = price/size;
            Integer lastPrice = average + remainder;

            int i = 1;
            for(OutPrItemProcessEntity prItemProcessEntity : processList){
                OutPrItemVO vo = BeanUtil.copy(dto, OutPrItemVO.class);
                vo.setPrLn(prItemProcessEntity.getPrLn());
                vo.setProcessCode(prItemProcessEntity.getProcessCode());
                if (i == size) {
                    vo.setQuotePrice(new BigDecimal(lastPrice.toString()));
                    i = 1;
                } else {
                    vo.setQuotePrice(new BigDecimal(average.toString()));
                    i++;
                }
                outPrItemVOS.add(vo);
            }
        }
        retPage.setRecords(outPrItemVOS);
        retPage.setTotal(outPrItemVOS.size());
        return retPage;
    }

    @Override public
    boolean flowPrItemId(Long prItemId, String cause){
        Long            nowTime = new Date().getTime()/1000;
        OutPrItemEntity entity  = getById(prItemId);
        entity.setStatus(STATUS_FLOW);
        entity.setFlowTime(nowTime);
        entity.setFlowCause(cause);
        return updateById(entity);
    }


    @Override
    public int getCount() {
        OutPrItemDTO queryDto = new OutPrItemDTO();

        String mRoleId  = paramService.getValue("purch_manager.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            queryDto.setPurchCode(getUser().getAccount());
            queryDto.setStatuss(STATUS_FLOW.toString());
            return this.baseMapper.getCountFromOut(queryDto);
        }

        queryDto.setStatuss(STATUS_WINBID.toString());

        return this.baseMapper.getCountFromOut(queryDto);
    }

    @Override
    public OutPrItemEntity getByPrcodeAndItemcode(String prCode, String itemCode) {
        return this.baseMapper.getByPrcodeAndItemcode(prCode,itemCode);
    }

    @Override
    public List<Map<String, Object>> getOutTab() {
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new HashMap<String, Object>(3){{
            put("status", STATUS_FLOW);
            put("title", "?????????");
            put("count", getTabCount(STATUS_FLOW.toString()));
        }});
        result.add(new HashMap<String, Object>(3){{
            put("status", STATUS_FLOW_SUBMIT);
            put("title", "?????????");
            put("count", getTabCount(STATUS_FLOW_SUBMIT.toString()));
        }});
        result.add(new HashMap<String, Object>(3){{
            put("status", STATUS_WINBID);
            put("title", "?????????");
            put("count", getTabCount(STATUS_WINBID.toString()));
        }});
        result.add(new HashMap<String, Object>(3){{
            put("status", STATUS_ACCORD);
            put("title", "?????????");
            put("count", getTabCount(STATUS_ACCORD.toString()));
        }});
        return result;
    }


    @Override
    public
    QueryWrapper<OutPrItemEntity> getQueryWrapper(OutPrItemEntity outPrItemEntity) {
        return Wrappers.<OutPrItemEntity>query()
            .like(StringUtils.isNotBlank(outPrItemEntity.getPrCode()), "pr_code", outPrItemEntity.getPrCode())
            .like(StringUtils.isNotBlank(outPrItemEntity.getItemCode()), "item_code", outPrItemEntity.getItemCode())
            .like(StringUtils.isNotBlank(outPrItemEntity.getItemName()), "item_name", outPrItemEntity.getItemName())
            .eq(StringUtils.isNotBlank(outPrItemEntity.getPurchCode()), "purch_code", outPrItemEntity.getPurchCode())
            .like(StringUtils.isNotBlank(outPrItemEntity.getPurchName()), "purch_name", outPrItemEntity.getPurchName())
            .eq(outPrItemEntity.getStatus() != null, "status", outPrItemEntity.getStatus())
            .eq(StringUtils.isNotBlank(outPrItemEntity.getMoNo()), "mo_no", outPrItemEntity.getMoNo())
            .isNotNull(true,"purch_code")
            .ne("purch_code","")
            .like(StringUtils.isNotBlank(outPrItemEntity.getLastSupName()), "last_sup_name", outPrItemEntity.getLastSupName());
    }

    /**
     * ????????????
     *
     * @param status String
     * @return int
     */
    @Override
    public int getTabCount(String status){

        OutPrItemDTO outPrItemDTO = new OutPrItemDTO();
        outPrItemDTO.setStatuss(status);

        String mRoleId  = paramService.getValue("purch_manager.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            outPrItemDTO.setPurchCode(getUser().getAccount());
        }
        return this.baseMapper.getCountFromOut(outPrItemDTO);
    }

    /**
     * ???????????????
     * @param submitPriceReq
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitBatch(SubmitPriceReq submitPriceReq) {
        boolean result = false;
        List<SubmitPriceDTO> items = submitPriceReq.getSubmitPriceDTOs();
        for (SubmitPriceDTO item : items) {
            result = submitPrice(item);
        }
        return result;
    }

    public
    boolean submitPrice(SubmitPriceDTO dto) {
        Supplier sup = supplierService.getByName(dto.getSupName());
        if (sup == null) {
            return false;
        }
        OutPrItemEntity pr = this.getById(dto.getId());
        if(pr == null) {
            throw new RuntimeException("?????????PR???" + "id???" + dto.getId());
        }
        OutIoEntity io = new OutIoEntity();

        io.setPrId(pr.getId());
        io.setPrLn(pr.getPrLn());
        io.setPrCode(pr.getPrCode());
        io.setItemCode(pr.getItemCode());
        io.setItemName(pr.getItemName());
        io.setPriceNum(pr.getPriceNum());
        io.setPriceUom(pr.getPriceUom());
        io.setTcNum(pr.getPriceNum());
        io.setTcUom(pr.getPriceUom());
        io.setReqDate(pr.getReqDate());
        io.setStatus(IIoService.STATUS_WINBID_CHECK1);
        io.setSupCode(sup.getCode());
        io.setSupName(sup.getName());
        //?????????????????????
        io.setIsByWeight(dto.getIsByWeight());
        io.setPromiseDate(dto.getPromiseDate().getTime()/1000 + 8 * 3600);
        io.setIsPersent(dto.getIsPersent());
        io.setQuotePrice(dto.getQuotePrice());
        io.setQuoteDate(new Date().getTime() / 1000);
        io.setRemark(dto.getRemark());
        io.setSource(IIoService.SOURCE_PURCHSUBMIT);

        if (dto.getAttachment() != null) {
            io.setAttachment(dto.getAttachment());
            pr.setAttachment(dto.getAttachment());
        }

        if ("flowSubmit".equals(dto.getType())) {
            //??????
            pr.setStatus(STATUS_FLOW_SUBMIT);
        } else if ("winBid".equals(dto.getType())){
            //??????
            pr.setStatus(STATUS_WINBID);
        } else {
            // ???????????? ???????????????
            pr.setStatus(STATUS_WINBID);
            String attachMent = this.baseMapper.selectAttachment(dto.getIoId());
            if (StringUtils.isNotEmpty(attachMent)) {
                io.setAttachment(attachMent);
            }
            this.baseMapper.deleteIo(dto.getIoId());
        }

        iIoOutService.save(io);
        pr.setIsNeedCheck(dto.getIsNeedCheck());
        this.updateById(pr);

        //????????????PR??????IO?????????  ???????????????????????????IO?????????ID???
        List<OutIoEntity> ioEntities = iIoOutService.getByPrId(pr.getId());
        ioEntities.forEach(ioEntity -> {
            if (!ioEntity.getId().equals(io.getId())) {
                ioEntity.setRemark("???????????????????????????IO?????????ID???" + io.getId());
                iIoOutService.updateById(ioEntity);
                iIoOutService.removeById(ioEntity);
            }
        });

        // ?????????????????????
        if ((!FLOW_SUBMIT.equals(dto.getType())) && NOT_NEED_CHECK.equals(dto.getIsNeedCheck())) {
            CheckDTO checkDTO = new CheckDTO();
            checkDTO.setId(dto.getId());
            checkDTO.setIoId(io.getId());
            checkDTO.setStatus(IIoOutService.STATUS_WAIT);
            iIoOutService.check2OfWW(checkDTO);
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean flowBatchExcelOfOut(SubmitPriceReq submitPriceReq) {
        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();
        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
            Long id = submitPriceDTO.getId();
            OutPrItemEntity outPrItemEntity = getById(id);
            if (outPrItemEntity == null) {
                throw new RuntimeException("?????????PR???" + "id???" + id);
            }
            if (FLOW_SUBMIT.equals(submitPriceDTO.getType())) {
                String attachmentAndRemark = submitPriceDTO.getAttachment();
                outPrItemEntity.setAttachment(attachmentAndRemark);
                updateById(outPrItemEntity);
            } else {
                PrReq prReq = new PrReq();
                prReq.setId(id);
                prReq.setStatuss(STATUS_WINBID.toString());
                OutIoEntity ioEntity = iIoOutService.getBySourceAndPrId(prReq);
                if (ioEntity == null) {
                    throw new RuntimeException("?????????IO???" + "prId???" + id + "?????????" + STATUS_WINBID);
                }
                ioEntity.setAttachment(submitPriceDTO.getAttachment());
                if (StringUtil.isNotBlank(submitPriceDTO.getRemark())) {
                    ioEntity.setRemark(submitPriceDTO.getRemark());
                }
                outPrItemEntity.setAttachment(submitPriceDTO.getAttachment());
                updateById(outPrItemEntity);
                iIoOutService.updateById(ioEntity);
            }
        }
        return true;
    }

    /**
     * ??????PR????????????
     *
     * @param submitPriceReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeOutPrList(SubmitPriceReq submitPriceReq) {
        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();

        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
            this.baseMapper.updateOutPr(submitPriceDTO.getId());
            this.baseMapper.deleteIo(submitPriceDTO.getIoId());
        }
        return true;
    }


    /**
     * ?????????????????? internation
     * @param outPrItemDTOS
     * @return
     */
    @Override
    public List<ItemInfoDTO> autoRetrieve(List<OutPrItemDTO> outPrItemDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        // ?????????????????? ?????? ??? ???????????? + ????????????????????????

        // ????????????
        List<ItemInfoDTO> itemInfoDTOSOfZJWX = autoRetrieveOfZJWX(outPrItemDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfZJWX);

        // ???????????????????????? ( ????????????????????? )
        List<ItemInfoDTO> itemInfoDTOSOfGXWW = autoRetrieveOfWW(outPrItemDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfGXWW);

        return itemInfoDTOS;
    }

    /**
     * ??????????????????????????????????????????(????????????)
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfZJWX(List<OutPrItemDTO> outPrItemDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        // ?????????????????? ???????????? ???????????????
        for (OutPrItemDTO dto : outPrItemDTOS ) {
            // ??????
            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(dto.getItemName());
            // ???????????????
            if (itemInfoEntity.getSeries() != null) {
                // ?????????????????????????????????????????????
                List<ItemInfoEntityOfWW> itemInfoEntitiesWW = u9PrMapper.selectSupAndWeightOfWW(itemInfoEntity);
                String price = "";

                // ????????????????????????
                if (itemInfoEntitiesWW.size() > 0) {

                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
                    List<PriceVO> priceVOS = new ArrayList<>();

                    // id?????????????????????????????????
                    itemInfoDTO.setId(dto.getId().toString());

                    // ??????????????????????????????
                    for (ItemInfoEntityOfWW iteminfo : itemInfoEntitiesWW) {
                        price = iteminfo.getPrice().toString();
                        // ??????????????????
                        if (StringUtils.isNotEmpty(price)) {
                            PriceVO priceVO = new PriceVO();
                            // ???????????????????????????????????????
                            priceVO.setSupName(iteminfo.getSupName());
                            priceVO.setSupCode(iteminfo.getSupCode());
                            priceVO.setPriority(iteminfo.getPriority());
                            // ????????????
                            BigDecimal priceToReturn = (new BigDecimal(price));
                            priceVO.setPrice(priceToReturn);
                            priceVOS.add(priceVO);
                            itemInfoDTO.setSupAndPriceList(priceVOS);
                        }
                    }

                    // ?????????????????????????????????????????????supType?????? 1
                    if ((itemInfoEntitiesWW!=null) &&(itemInfoEntitiesWW.size() > 1) &&  (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                        itemInfoDTO.setSupColorType("1");
                    } else {
                        itemInfoDTO.setSupColorType("0");
                    }
                    // ???????????????
                    if (itemInfoDTO.getSupAndPriceList().size()>0) {
                        itemInfoDTOS.add(itemInfoDTO);
                    }
                }
            }
        }
        return itemInfoDTOS;
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfWW(List<OutPrItemDTO> outPrItemDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        // ?????????????????? ???????????? ???????????????
        for (OutPrItemDTO dto : outPrItemDTOS ) {
            // ??????
            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfQCWW(dto.getItemName());
            // ???????????????
            if (itemInfoEntity.getSeries() != null) {
                // ?????????????????????????????????????????????
                List<ItemInfoEntityOfWW> itemInfoEntitiesWW = u9PrMapper.selectSupAndWeightOfQCWW(itemInfoEntity);
                String price = "";

                // ????????????????????????
                if (itemInfoEntitiesWW.size() > 0) {

                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
                    List<PriceVO> priceVOS = new ArrayList<>();

                    // id?????????????????????????????????
                    itemInfoDTO.setId(dto.getId().toString());

                    // ??????????????????????????????
                    for (ItemInfoEntityOfWW iteminfo : itemInfoEntitiesWW) {
                        price = iteminfo.getPrice().toString();
                        // ??????????????????
                        if (StringUtils.isNotEmpty(price)) {
                            PriceVO priceVO = new PriceVO();
                            // ???????????????????????????????????????
                            priceVO.setSupName(iteminfo.getSupName());
                            priceVO.setSupCode(iteminfo.getSupCode());
                            priceVO.setPriority(iteminfo.getPriority());
                            // ???????????? ??????2????????? ??????*??????+?????????
                            BigDecimal priceToReturn = (new BigDecimal(price));
                            priceVO.setPrice(priceToReturn);
                            priceVOS.add(priceVO);
                            itemInfoDTO.setSupAndPriceList(priceVOS);
                        }
                    }

                    // ?????????????????????????????????????????????supType?????? 1
                    if ((itemInfoEntitiesWW!=null) &&(itemInfoEntitiesWW.size() > 1) &&  (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                        itemInfoDTO.setSupColorType("1");
                    } else {
                        itemInfoDTO.setSupColorType("0");
                    }
                    // ???????????????
                    if (itemInfoDTO.getSupAndPriceList().size()>0) {
                        itemInfoDTOS.add(itemInfoDTO);
                    }
                }
            }
        }
        return itemInfoDTOS;
    }



    /**
     * ??????????????????
     * @param purchType
     * @return
     */
    @Override
    public List<Map<String, Object>> inquiryCountOfWW() {
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new HashMap<String, Object>(3){{
            put("status", STATUS_INQUIRY);
            put("title", "?????????");
            put("count", getInquiryCount(STATUS_INQUIRY.toString()));
        }});
        result.add(new HashMap<String, Object>(3){{
            put("status", STATUS_QUOTED);
            put("title", "?????????");
            put("count", getInquiryCount(STATUS_QUOTED.toString()));
        }});
        // ???????????????????????????????????????
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_WINBID);
            put("title", "?????????");
            put("count", getInquiryCount(STATUS_WINBID.toString()));
        }});
        result.add(new HashMap<String, Object>(3){{
            put("status", STATUS_ACCORD);
            put("title", "?????????");
            put("count", getInquiryCount(STATUS_ACCORD.toString()));
        }});
        return result;
    }

    private int getInquiryCount(String status){
        OutPrItemDTO outPrItemDTO = new OutPrItemDTO();
        outPrItemDTO.setStatuss(status);

        String mRoleId  = paramService.getValue("purch_manager.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            outPrItemDTO.setPurchCode(getUser().getAccount());
        }
        return this.baseMapper.selectInquiryPageOfWW(outPrItemDTO).size();
    }

    @Override
    public int inquiryCountOfWWForZT() {
        OutPrItemDTO outPrItemDTO = new OutPrItemDTO();


        String mRoleId  = paramService.getValue("purch_manager.role_id");
        if(!StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            outPrItemDTO.setPurchCode(getUser().getAccount());
            outPrItemDTO.setStatuss(STATUS_INQUIRY.toString() + "," +STATUS_QUOTED.toString());
        } else {
            outPrItemDTO.setStatuss(STATUS_WINBID.toString());
        }
        return this.baseMapper.selectInquiryPageOfWWForZT(outPrItemDTO);
    }

    @Override
    public void letPrFlow(OutPrItemEntity pr, String flowCause) {
        pr.setStatus(STATUS_FLOW);
        pr.setFlowCause(flowCause);
        updateById(pr);
        //  ??????io??????
        OutIoEntity upIo = new OutIoEntity();
        upIo.setStatus(IIoService.STATUS_CLOSE);
        OutIoEntity whereIo = new OutIoEntity();
        whereIo.setPrId(pr.getId());
        iIoOutService.update(upIo, Condition.getQueryWrapper(whereIo));
    }
}
