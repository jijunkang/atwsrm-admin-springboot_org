package org.springblade.modules.outpr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.mathmodel.service.IMmVolumeCalculateService;
import org.springblade.modules.outpr.dto.OutSupPreOrderDTO;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.entity.OutSupItemPriceEntity;
import org.springblade.modules.outpr.entity.OutSupItemProcessPriceEntity;
import org.springblade.modules.outpr.entity.OutSupPreOrderEntity;
import org.springblade.modules.outpr.mapper.OutSupPreOrderMapper;
import org.springblade.modules.outpr.service.IOutPrItemService;
import org.springblade.modules.outpr.service.IOutSupItemPriceService;
import org.springblade.modules.outpr.service.IOutSupItemProcessPriceService;
import org.springblade.modules.outpr.service.IOutSupPreOrderService;
import org.springblade.modules.outpr.vo.OutSupPreOrderVO;
import org.springblade.modules.outpr.wrapper.OutSupPreOrderWrapper;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springblade.core.secure.utils.AuthUtil.getUser;

/**
 * ???????????????
 * @author Will
 */
@Service
public
class OutSupPreOrderServiceImpl extends BaseServiceImpl<OutSupPreOrderMapper, OutSupPreOrderEntity> implements IOutSupPreOrderService{

    @Autowired
    IOutPrItemService outPrItemService;

    @Autowired
    IOutSupItemPriceService outSupItemPriceService;

    @Autowired
    IOutSupItemProcessPriceService outSupItemProcessPriceService;

    @Autowired @Lazy
    IPoItemService poItemService;

    @Autowired @Lazy
    IParamService paramService;

    @Autowired @Lazy
    private IOutSupPreOrderService outsuppreorderService;

    @Autowired @Lazy
    IMmVolumeCalculateService mmVolumeCalculateService;

    @Autowired
    @Lazy
    IItemService itemService;

    @Override
    public
    QueryWrapper<OutSupPreOrderEntity> getQueryWrapper(OutSupPreOrderDTO dto){
        return Wrappers.<OutSupPreOrderEntity>query()
                .like(StringUtils.isNotBlank(dto.getPrCode()),"pr_code",dto.getPrCode())
                .like(StringUtils.isNotBlank(dto.getItemCode()),"item_code",dto.getItemCode())
                .like(StringUtils.isNotBlank(dto.getItemName()),"item_name",dto.getItemName())
                .like(StringUtils.isNotBlank(dto.getSupCode()),"sup_code",dto.getSupCode())
                .like(StringUtils.isNotBlank(dto.getSupName()),"sup_name",dto.getSupName())
                .ge( dto.getReqDateStart() != null ,"req_date", dto.getReqDateStart())
                .le(dto.getReqDateEnd() != null,"req_date", dto.getReqDateEnd())
                .ge( dto.getPrDateStart() != null ,"pr_date", dto.getPrDateStart())
                .le(dto.getPrDateEnd() != null,"pr_date", dto.getPrDateEnd())
                .eq( dto.getStatus() != null,"status",dto.getStatus());
    }

    /**
     * ???????????? ????????? ???????????????????????????
     * @return
     */
    @Override public
    IPage<OutSupPreOrderVO> toConfirmVoPage(IPage<OutSupPreOrderEntity> page, QueryWrapper<OutSupPreOrderEntity> queryWrapper){
       // ??????????????? ??? ??????
        // AuthUtil.getUserRole();
        queryWrapper.in("status", STATUS_SUPACCEPT,STATUS_INIT).eq("inquiry_way", INQUIRYWAY_PRICELIB).orderByDesc("update_time");
        IPage<OutSupPreOrderEntity> entityPage = page(page, queryWrapper);
        IPage<OutSupPreOrderVO>     retPage    = new Page<>();
        List<OutSupPreOrderVO>      voList     = Lists.newArrayList();
        retPage.setRecords(voList);
        for(OutSupPreOrderEntity entity : entityPage.getRecords()){
            OutSupPreOrderVO vo = OutSupPreOrderWrapper.build().entityVO(entity);
            Map<String, Object> queryMap = new HashMap<String, Object>(){{
                put("item_price_id", entity.getItemPriceId());
            }};
            vo.setProcessPriceList((List<OutSupItemProcessPriceEntity>) outSupItemProcessPriceService.listByMap(queryMap));

            OutPrItemEntity outPrItemEntity = outPrItemService.getById(entity.getPrItemId());
            if(outPrItemEntity != null){
                vo.setAvailableQuantity(outPrItemEntity.getAvailableQuantity());
                vo.setProjectOccupancyNum(outPrItemEntity.getProjectOccupancyNum());
                vo.setRequisitionRemark(outPrItemEntity.getRequisitionRemark());
            }

            Item item = itemService.getByCode(entity.getItemCode());
            if(item != null){
                vo.setPurchMix(item.getPurchMix());
                vo.setStockLowerLimit(item.getStockLowerLimit());
            }

            voList.add(vo);
        }
        return retPage;
    }

    /**
     * @return
     */
    @Override
    public
    int toConfirmCount(){
        QueryWrapper<OutSupPreOrderEntity> queryWrapper =Wrappers.<OutSupPreOrderEntity>query()
                .in("status", STATUS_SUPACCEPT,STATUS_INIT)
                .eq("inquiry_way", INQUIRYWAY_PRICELIB);
        return count(queryWrapper);
    }

    @Override public
    IPage<OutSupPreOrderVO> voPage(IPage<OutSupPreOrderEntity> page, QueryWrapper<OutSupPreOrderEntity> queryWrapper){

        IPage<OutSupPreOrderEntity> entityPage = page(page, queryWrapper);
        IPage<OutSupPreOrderVO>     retPage    = new Page<>(entityPage.getCurrent(),entityPage.getSize(),entityPage.getTotal());
        List<OutSupPreOrderVO>      voList     = Lists.newArrayList();
        retPage.setRecords(voList);
        for(OutSupPreOrderEntity entity : entityPage.getRecords()){
            OutSupPreOrderVO vo = OutSupPreOrderWrapper.build().entityVO(entity);
            Map<String, Object> queryMap = new HashMap<String, Object>(){{
                put("item_price_id", entity.getItemPriceId());
            }};
            vo.setProcessPriceList((List<OutSupItemProcessPriceEntity>) outSupItemProcessPriceService.listByMap(queryMap));
            //?????????????????????
            vo.setReferencePrice(mmVolumeCalculateService.getPrice(vo.getItemCode(), vo.getSupCode()));

            OutPrItemEntity outPrItemEntity = outPrItemService.getById(entity.getPrItemId());
            if(outPrItemEntity != null){
                vo.setAvailableQuantity(outPrItemEntity.getAvailableQuantity());
                vo.setProjectOccupancyNum(outPrItemEntity.getProjectOccupancyNum());
                vo.setRequisitionRemark(outPrItemEntity.getRequisitionRemark());
            }

            Item item = itemService.getByCode(entity.getItemCode());
            if(item != null){
                vo.setPurchMix(item.getPurchMix());
                vo.setStockLowerLimit(item.getStockLowerLimit());
            }

            voList.add(vo);
        }
        return retPage;
    }

    @Override
    public List<Map<String, Object>> centerCount() {
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_INIT);
            put("title", "?????????");
            put("count", countByStatus(STATUS_INIT));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_SUPACCEPT);
            put("title", "?????????");
            put("count", countByStatus(STATUS_SUPACCEPT));
        }});
        return result;
    }

    @Override
    public int getCount() {
        String mRoleId = paramService.getValue("purch_manager.role_id");//????????????ID
        String pRoleId = paramService.getValue("purch_user.role_id");//???????????????ID

        QueryWrapper<OutSupPreOrderEntity> queryWrapper = outsuppreorderService.getQueryWrapper(new OutSupPreOrderDTO());
        if (StringUtil.containsAny(getUser().getRoleId(), pRoleId)) {
            queryWrapper.in("status", IOutSupPreOrderService.STATUS_SUPACCEPT);
        } else if (StringUtil.containsAny(getUser().getRoleId(), mRoleId)) {
            queryWrapper.in("status", IOutSupPreOrderService.STATUS_CHECK1, IOutSupPreOrderService.STATUS_CHECK2);
        } else {
            return 0;
        }
        return list(queryWrapper).size();
    }


    @Override public
    boolean check(CheckDTO checkDto){
        OutSupPreOrderEntity preOrder = getById(checkDto.getId());
        preOrder.setStatus(checkDto.getStatus());

        // ??????
        if(STATUS_CHECKREJECT.equals(checkDto.getStatus())){
            //?????? ???????????? ????????? ????????? ?????????
            if(INQUIRYWAY_PRICELIB.equals(preOrder.getInquiryWay())){
                return statusToFlow(preOrder, "???????????????????????????,????????????");
            }
            // ???????????? ?????????????????????
            return checkReject(preOrder);
        }
        // ??????  ????????? ?????? ???1???????????????????????? ???2??????????????????????????????3??????????????????
        if(checkDto.getDate() != null){
            preOrder.setSupDeliveryTime(checkDto.getDate());
        }
        updateById(preOrder);
        if(!INQUIRYWAY_PRICELIB.equals(preOrder.getInquiryWay()) && STATUS_CHECK1.equals(checkDto.getStatus())){
            return true;
        }
        String remark = "";
        //????????????????????? ???????????????
        if(STATUS_ACCORD.equals(checkDto.getStatus())){
            remark = "??????????????????";
        }
        //?????? ???????????? ????????? ?????????????????? ???????????????
        if(INQUIRYWAY_PRICELIB.equals(preOrder.getInquiryWay())){
            remark = "?????????????????????????????????";
        }
        poItemService.createByOutPreOrder(preOrder, remark);
        return statusToAccord(preOrder);
    }

    @Override
    public
    boolean check(List<CheckDTO> checkDtos){
        for(CheckDTO dto : checkDtos){
            check(dto);
        }
        return true;
    }

    @Override
    public
    OutSupPreOrderEntity getByItemPriceId(Long itemPriceId){
        OutSupPreOrderEntity queryEntity = new OutSupPreOrderEntity();
        queryEntity.setItemPriceId(itemPriceId);
        return getOne(Condition.getQueryWrapper(queryEntity));
    }

    /**
     * ????????????
     * @param entity
     * @return
     */
    private
    boolean checkReject(OutSupPreOrderEntity entity){
        entity.setStatus(STATUS_CHECKREJECT);
        updateById(entity);
        OutSupItemPriceEntity outSupItemPrice = outSupItemPriceService.findNextSup(entity.getItemPriceId());
        //?????????????????????????????? ??? ??????
        if(outSupItemPrice == null){
            return outPrItemService.flowPrItemId(entity.getPrItemId(), "?????????????????????");
        }
        //??? ??? ?????? ????????????????????????
        return placeBySupItemPrice(outSupItemPrice);
    }
    public
    boolean placeBySupItemPrice(OutSupItemPriceEntity outSupItemPrice){
        OutPrItemEntity item = outPrItemService.getById(outSupItemPrice.getPrItemId());
        //item.setStatus( IOutPrItemService.STATUS_ENQUIRY);
        outPrItemService.updateById(item);

        Long                 nowTime  = new Date().getTime()/1000;
        OutSupPreOrderEntity preOrder = new OutSupPreOrderEntity();
        preOrder.setPrItemId(outSupItemPrice.getPrItemId());
        preOrder.setItemPriceId(outSupItemPrice.getId());
        preOrder.setSupCode(outSupItemPrice.getSupCode());
        preOrder.setSupName(outSupItemPrice.getSupName());
        preOrder.setPrice(outSupItemPrice.getPrice());
        preOrder.setTaxPrice( outSupItemPrice.getTaxPrice());
        preOrder.setTotal(outSupItemPrice.getSubtotal());
        preOrder.setFirstSetupCost(outSupItemPrice.getFirstSetupCost());
        preOrder.setItemCode(item.getItemCode());
        preOrder.setItemName(item.getItemName());
        preOrder.setMaterialCost(item.getMaterialCost());
        preOrder.setPriceNum(item.getPriceNum());
        preOrder.setPriceUom(item.getPriceUom());
        preOrder.setPrCode(item.getPrCode());
        preOrder.setPrDate(item.getPrDate());
        preOrder.setReqDate(item.getReqDate());
        preOrder.setRcvEndtime(nowTime + 48*3600);
        preOrder.setStatus(STATUS_INIT);

        return save(preOrder);
    }

    /**
     * ??????????????????
     * ????????????=???????????????+???????????????*??????+????????????????????????/??????
     * @param item
     * @return
     */
    @Override
    public
    BigDecimal comTaxPrice(OutPrItemEntity item, OutSupItemPriceEntity itemPrice){
        if(item.getPriceNum() == null || item.getPriceNum().equals(BigDecimal.ZERO)){
            return BigDecimal.ZERO;
        }
        if(itemPrice.getPrice() == null){
            itemPrice.setPrice(BigDecimal.ZERO);
        }
        return  itemPrice.getPrice().add(item.getMaterialCost()).multiply(item.getPriceNum()).add(itemPrice.getFirstSetupCost()).divide(item.getPriceNum(),4);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public
    boolean assignSup(OutSupPreOrderEntity preOrderReq){
        Long                 nowTime     = new Date().getTime()/1000;
        OutSupPreOrderEntity newPreOrder = getByItemPriceId(preOrderReq.getItemPriceId());
        if(newPreOrder == null){
            newPreOrder = new OutSupPreOrderEntity();
        }

        OutSupItemPriceEntity outSupItemPriceEntity = outSupItemPriceService.getById(preOrderReq.getItemPriceId());
        QueryWrapper<OutSupPreOrderEntity>  queryWrapper = Condition.getQueryWrapper(new OutSupPreOrderEntity());
        queryWrapper.eq("pr_item_id", outSupItemPriceEntity.getPrItemId());
        queryWrapper.ne("item_price_id", outSupItemPriceEntity.getId());
        //???????????????????????????
        if(list(queryWrapper).size() > 0){
            throw new RuntimeException("???????????????????????????");
        }

        OutSupItemPriceEntity supItemPrice = outSupItemPriceService.getById(preOrderReq.getItemPriceId());
        OutPrItemEntity       prItem       = outPrItemService.getById(supItemPrice.getPrItemId());

        newPreOrder.setItemPriceId(supItemPrice.getId());
        newPreOrder.setSupCode(supItemPrice.getSupCode());
        newPreOrder.setSupName(supItemPrice.getSupName());
        newPreOrder.setPrice(supItemPrice.getPrice());
        newPreOrder.setTaxPrice(supItemPrice.getTaxPrice());
        newPreOrder.setTotal(supItemPrice.getSubtotal());
        newPreOrder.setFirstSetupCost(supItemPrice.getFirstSetupCost());
        newPreOrder.setPrItemId(prItem.getId());
        newPreOrder.setPrCode(prItem.getPrCode());
        newPreOrder.setPrDate(prItem.getPrDate());
        newPreOrder.setReqDate(prItem.getReqDate());
        newPreOrder.setItemCode(prItem.getItemCode());
        newPreOrder.setItemName(prItem.getItemName());
        newPreOrder.setMaterialCost(prItem.getMaterialCost());
        newPreOrder.setPriceNum(prItem.getPriceNum());
        newPreOrder.setPriceUom(prItem.getPriceUom());

        newPreOrder.setSupDeliveryTime(preOrderReq.getSupDeliveryTime());
        newPreOrder.setRcvTime(nowTime);
        newPreOrder.setStatus(STATUS_SUPACCEPT); //??????????????? ?????????

        //prItem.setStatus(IOutPrItemService.STATUS_SUPACCEPT);
        outPrItemService.updateById(prItem);

        return saveOrUpdate(newPreOrder);
    }

    public
    boolean statusToAccord(OutSupPreOrderEntity preOrder){
        OutPrItemEntity prItem = new OutPrItemEntity();
        //prItem.setStatus(IOutPrItemService.STATUS_ACCORD);
        prItem.setId(preOrder.getPrItemId());
        outPrItemService.updateById(prItem);

        preOrder.setStatus(STATUS_ACCORD);
        return updateById(preOrder);
    }


    /**
     * ???????????????????????????,????????????
     * @param preOrder
     * @param remark
     * @return
     */
    private
    boolean statusToFlow(OutSupPreOrderEntity preOrder, String remark){
        preOrder.setStatus(STATUS_CHECKREJECT);
        updateById(preOrder);

        OutPrItemEntity prItem = new OutPrItemEntity();
        prItem.setId(preOrder.getPrItemId());
        prItem.setStatus(IOutPrItemService.STATUS_FLOW);
        prItem.setFlowCause(remark);
        return outPrItemService.updateById(prItem);
    }

    /**
     * ????????????????????????
     *
     * @param status Integer
     * @return Integer
     */
    private Integer countByStatus(Integer status){
        String mRoleId = paramService.getValue("purch_manager.role_id");//????????????ID
        String pRoleId = paramService.getValue("purch_user.role_id");//???????????????ID
        OutSupPreOrderDTO dto = new OutSupPreOrderDTO();
        dto.setStatus(status);
        QueryWrapper<OutSupPreOrderEntity> queryWrapper = outsuppreorderService.getQueryWrapper(dto);
        if (StringUtil.containsAny(getUser().getRoleId(), pRoleId)) {
            //queryWrapper.in("status", IOutSupPreOrderService.STATUS_SUPACCEPT);
            queryWrapper.isNull("inquiry_way");
        } else if (StringUtil.containsAny(getUser().getRoleId(), mRoleId)) {
            queryWrapper.in("status", IOutSupPreOrderService.STATUS_CHECK1, IOutSupPreOrderService.STATUS_CHECK2);
        } else {
            return 0;
        }
        return list(queryWrapper).size();
    }



}
