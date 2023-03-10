package org.springblade.modules.po.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.api.client.util.Lists;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.mathmodel.service.IMmVolumeCalculateService;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.service.IOutPrItemService;
import org.springblade.modules.po.dto.IoDTO;
import org.springblade.modules.po.dto.IoWinbidReq;
import org.springblade.modules.po.dto.OutIoDTO;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.entity.OutIoEntity;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.mapper.IoMapper;
import org.springblade.modules.po.service.IIoOutService;
import org.springblade.modules.po.service.IIoService;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.po.service.IPoService;
import org.springblade.modules.po.vo.IoVO;
import org.springblade.modules.po.vo.OutIoVO;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.pr.entity.U9PrEntity;
import org.springblade.modules.pr.service.IU9PrService;
import org.springblade.modules.priceframe.dto.CenterPriceFrame;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * ???????????????
 * @author Will
 */
@Service
public
class IoServiceImpl extends BaseServiceImpl<IoMapper, IoEntity> implements IIoService{

    @Autowired
    IParamService paramService;

    @Autowired
    @Lazy
    IU9PrService prService;

    @Autowired
    @Lazy
    IIoOutService iIoOutService;

    @Autowired
    @Lazy
    IOutPrItemService iOutPrItemService;

    @Autowired
    @Lazy
    IItemService itemService;

    @Autowired
    IPoService poService;

    @Autowired
    IPoItemService poItemService;

    @Autowired @Lazy
    IMmVolumeCalculateService mmVolumeCalculateService;

    @Autowired
    IoMapper ioMapper;

    @Override
    public
    IPage<IoDTO> selectToCheckPage(IPage<Object> page, IoEntity io, BladeUser user){
        String dmRoleId = paramService.getValue("purch_deputy_manager.role_id");
        String mRoleId  = paramService.getValue("purch_manager.role_id");
        if(StringUtil.containsAny(user.getRoleName(), "?????????")){
            io.setStatus(STATUS_WINBID_UNCHECK);
        }else if(StringUtil.containsAny(user.getRoleId(), mRoleId)){
            io.setStatus(STATUS_WINBID_CHECK1);
        }else{
            return new Page<IoDTO>();
        }
        IPage<IoDTO> ret = this.baseMapper.selectToCheckPage(page, io);
        for(IoDTO dto : ret.getRecords()){
            String itemCode = dto.getItemCode();
            dto.setHighestPrice(poItemService.getHighestPrice(itemCode));
            dto.setLowestPrice(poItemService.getLowestPrice(itemCode));
            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(),dto.getItemName());
            if(poItemEntity!=null) {
                dto.setLastPrice(poItemEntity.getPrice());
            }
            Item item = itemService.getByCode(itemCode);
            if(item == null){
                item = new Item();
            }
            dto.setStandardDate(item.getStandardDate());
            dto.setStandardPrice(item.getStandardPrice());
            dto.setPurchCode(item.getPurchCode());
            dto.setPurchName(item.getPurchName());
            dto.setPlaceCode(item.getPlaceCode());
            dto.setPlaceName(item.getPlaceName());

            dto.setReferencePrice(mmVolumeCalculateService.getPrice(dto.getItemCode(), dto.getSupCode()));
        }
        return ret;
    }

    /**
     * ???????????? ??????????????? ?????? ????????????????????? ?????????
     * @return
     */
    @Override
    public
    IPage<IoDTO> selectToConfirmPage(IPage<IoEntity> page, IoDTO io){
        return baseMapper.selectToConfirmPage(page, io);
    }

    /**
     * ????????????  51??????????????????52???????????? 70?????????
     * @return
     */
    @Override
    public
    boolean check1(CheckDTO checkDto){
        IoEntity io = getById(checkDto.getId());
        if(io == null){
            return false;
        }
        U9PrEntity pr = prService.getById(io.getPrId());
        boolean isAllow = Arrays.asList(STATUS_WINBID_CHECK1, STATUS_WINBID_CHECK2, STATUS_WINBID_REJECT)
                                .contains(checkDto.getStatus());
        // ????????????
        if(!isAllow){
            return false;
        }
        // ???????????????->Pr??????
        if(STATUS_WINBID_REJECT.equals(checkDto.getStatus())){
            io.setRefuseCause(checkDto.getRemark());
            checkReject(io, IU9PrService.FLOW_TYPE_ATWREJECT, checkDto.getRemark());
        }
        //?????? ???????????? ????????? ????????? ???????????????
        if(IU9PrService.INQUIRYWAY_HAVEPRICE1DATE.equals(pr.getInquiryWay())){
            io.setStatus(STATUS_WAIT);
            if(checkDto.getDate() != null){// ???????????????????????????????????? ??????????????????????????????
                io.setPromiseDate(checkDto.getDate());
            }
            updateById(io);
            if(itemService.isGasCtrl(StringUtil.sub(io.getItemCode(), 0, 6))){
                poService.placeOrderByIo(io, IPoItemService.SOURCE_ENQUIRY, io.getId());
            }else{
                poService.placeOrderByIo(io, IPoItemService.SOURCE_ENQUIRY, io.getId(), IPoItemService.BIZ_BRANCH_COMBILL);
            }

            pr.setStatus(IU9PrService.STATUS_WAIT);
            return prService.updateById(pr);
        }

        io.setStatus(checkDto.getStatus());
        updateById(io);
        // todo ??????????????????
        return true;

    }

    /**
     * ???????????? 80?????????????????? 70?????????
     * @return
     */
    @Override
    public
    boolean check2(CheckDTO checkDto){
        IoEntity ioEntity = getById(checkDto.getIoId());
        if(ioEntity == null){
            return false;
        }
        boolean isAllow = Arrays.asList(STATUS_WAIT, STATUS_WINBID_REJECT).contains(checkDto.getStatus());
        if(!isAllow){
            return false;
        }

        // ???????????????->Pr??????
        if(STATUS_WINBID_REJECT.equals(checkDto.getStatus())){
            ioEntity.setRefuseCause(checkDto.getRemark());
            checkReject(ioEntity, IU9PrService.FLOW_TYPE_ATWREJECT, checkDto.getRemark());
        }
        // ????????????->?????????
        if(STATUS_WAIT.equals(checkDto.getStatus())){
            U9PrEntity pr = prService.getById(ioEntity.getPrId());
            pr.setStatus(IU9PrService.STATUS_WAIT);
            poService.placeOrderByIo(ioEntity, IPoItemService.SOURCE_ENQUIRY, ioEntity.getId());
            prService.updateById(pr);
        }

        ioEntity.setStatus(checkDto.getStatus());
        ioEntity.setRemark(checkDto.getRemark());
        updateById(ioEntity);
        // todo ??????????????????
        return true;
    }


    /**
     * ???????????? 80?????????????????? 70?????????
     * @return
     */
    @Override
    public
    boolean check2OfWW(CheckDTO checkDto){
        OutIoEntity ioEntity = iIoOutService.getById(checkDto.getIoId());
        if(ioEntity == null){
            return false;
        }
        boolean isAllow = Arrays.asList(STATUS_WAIT, STATUS_WINBID_REJECT).contains(checkDto.getStatus());
        if(!isAllow){
            return false;
        }

        // ???????????????->Pr??????
        if(STATUS_WINBID_REJECT.equals(checkDto.getStatus())){
            ioEntity.setRefuseCause(checkDto.getRemark());
            checkRejectOfWW(ioEntity, IU9PrService.FLOW_TYPE_ATWREJECT, checkDto.getRemark());
        }

        // ????????????->?????????
        if(STATUS_WAIT.equals(checkDto.getStatus())){
            U9PrEntity pr = prService.getById(ioEntity.getPrId());
            pr.setStatus(IU9PrService.STATUS_WAIT);
            poService.placeOrderByIoOfWW(ioEntity, IPoItemService.SOURCE_ENQUIRY, ioEntity.getId());
            prService.updateById(pr);
        }

        ioEntity.setStatus(checkDto.getStatus());
        ioEntity.setRemark(checkDto.getRemark());
        iIoOutService.updateById(ioEntity);
        // todo ??????????????????
        return true;
    }

    /**
     * ???????????? 80?????????????????? 70?????????
     * ?????????
     * @return
     */
    @Override
    public
    boolean check2OfOthers(CheckDTO checkDto) {
        IoEntity ioEntity = this.baseMapper.getWinBidIo(checkDto.getId().toString());
        if (ioMapper.poItemIsExisted(ioEntity.getPrId()) < 1) {
            // ???????????????->Pr??????
            if (STATUS_WINBID_REJECT.equals(checkDto.getStatus())) {
                ioEntity.setRefuseCause(checkDto.getRemark());
                checkReject(ioEntity, IU9PrService.FLOW_TYPE_ATWREJECT, checkDto.getRemark());
            }

            // ????????????->?????????
            if (STATUS_WAIT.equals(checkDto.getStatus())) {
                boolean isAllow = Arrays.asList(STATUS_WAIT, STATUS_WINBID_REJECT).contains(checkDto.getStatus());
                if (!isAllow || ioEntity == null) {
                    throw new RuntimeException("????????????????????????????????????");
                }
                U9PrEntity pr = prService.getById(ioEntity.getPrId());
                pr.setStatus(IU9PrService.STATUS_WAIT);
                poService.placeOrderByIo(ioEntity, IPoItemService.SOURCE_INNER, ioEntity.getId());
                prService.updateById(pr);
            }

            ioEntity.setStatus(checkDto.getStatus());
            ioEntity.setRemark(checkDto.getRemark());
            updateById(ioEntity);
        }
        return true;
    }

    /**
     * ???????????? io
     */
    private
    void checkReject(IoEntity entity, String flowType, String remark){
        U9PrEntity pr = prService.getById(entity.getPrId());
        pr.setStatus(IU9PrService.STATUS_FLOW);
        pr.setFlowType(flowType);
        String refuseCase = AuthUtil.getNickName() + "????????????:" + remark;
        pr.setCheckRemark(refuseCase);
        entity.setRefuseCause(refuseCase);
        prService.updateById(pr);
    }


    /**
     * ???????????? io ??????
     */
    private
    void checkRejectOfWW(OutIoEntity entity, String flowType, String remark){
        OutPrItemEntity pr = iOutPrItemService.getById(entity.getPrId());
        pr.setStatus(IU9PrService.STATUS_FLOW);
        pr.setFlowType(flowType);
        String refuseCase = AuthUtil.getNickName() + "????????????:" + remark;
        pr.setCheckRemark(refuseCase);
        entity.setRefuseCause(refuseCase);
        iOutPrItemService.updateById(pr);
    }

    /**
     * private
     * function updateWinbid(&$io, $isSingle = false){
     * $now = time();
     * $this->where(['pr_id' => $io['pr_id']])->whereNotNull('quote_date')->update(['status' =>
     * self::STATUS_LOSEBID]);  //'losebid'
     * $this->where(['pr_id' => $io['pr_id']])->whereNull('quote_date')->update(['status' =>
     * self::STATUS_UN_TENDER]); //'un_tender'
     * $status = $isSingle ?  self::STATUS_WINBID_UNCHECK : self::STATUS_WAIT;  //  'winbid_uncheck' :
     * 'wait'; //?????????????????????????????????
     * $this->where(['id' => $io['id']])->update(['status' => $status, 'winbid_date' => $now]);
     * $io['status']      = $status;
     * $io['winbid_date'] = $now;
     * }
     * @return
     */
    @Override
    @Transactional
    public
    IoEntity letIoWinBid(IoWinbidReq ioWinbidReq){
        Long       ioId = ioWinbidReq.getIoId();
        IoEntity   io   = getById(ioId);
        U9PrEntity pr   = prService.getById(io.getPrId());
        pr.setStatus(IU9PrService.STATUS_WINBID);
        prService.updateById(pr);

        // ??????PR???IO ????????????---????????????
        QueryWrapper<IoEntity> unQuoteWrap = Condition.getQueryWrapper(new IoEntity()).ne("id", ioId).eq("pr_id", io.getPrId())
            .isNotNull("quote_date");
        IoEntity unQuoteIo = new IoEntity();
        unQuoteIo.setStatus(STATUS_UN_QUOTE);
        update(unQuoteIo, unQuoteWrap);

        // ??????PR???IO ????????????---?????? ????????????
        QueryWrapper<IoEntity> losebidWrap = Condition.getQueryWrapper(new IoEntity()).ne("id", ioId).eq("pr_id", io.getPrId())
            .isNotNull("quote_date");
        IoEntity losebidIo = new IoEntity();
        losebidIo.setStatus(STATUS_LOSEBID);
        update(unQuoteIo, losebidWrap);

        io.setStatus(STATUS_WINBID_CHECK1);
        io.setPromiseDate(ioWinbidReq.getPromiseDate());
        io.setWinbidDate(DateUtil.now().getTime()/1000);
        updateById(io);

        return io;
    }

    /**
     * ?????????????????? ?????????
     * @param ioWinbidReq
     * @return
     */
    @Override
    @Transactional
    public
    OutIoEntity letIoWinBidOfWW(IoWinbidReq ioWinbidReq){
        Long       ioId = ioWinbidReq.getIoId();
        OutIoEntity   io   = iIoOutService.getById(ioId);
        OutPrItemEntity pr   = iOutPrItemService.getById(io.getPrId());
        pr.setStatus(IU9PrService.STATUS_WINBID);
        iOutPrItemService.updateById(pr);

        // ??????PR???IO ????????????---????????????
        QueryWrapper<OutIoEntity> unQuoteWrap = Condition.getQueryWrapper(new OutIoEntity()).ne("id", ioId).eq("pr_id", io.getPrId())
            .isNotNull("quote_date");
        OutIoEntity unQuoteIo = new OutIoEntity();
        unQuoteIo.setStatus(STATUS_UN_QUOTE);
        iIoOutService.update(unQuoteIo, unQuoteWrap);

        // ??????PR???IO ????????????---?????? ????????????
        QueryWrapper<OutIoEntity> losebidWrap = Condition.getQueryWrapper(new OutIoEntity()).ne("id", ioId).eq("pr_id", io.getPrId())
            .isNotNull("quote_date");
        OutIoEntity losebidIo = new OutIoEntity();
        losebidIo.setStatus(STATUS_LOSEBID);
        iIoOutService.update(unQuoteIo, losebidWrap);

        io.setStatus(STATUS_WINBID_CHECK1);
        io.setPromiseDate(ioWinbidReq.getPromiseDate());
        io.setWinbidDate(DateUtil.now().getTime()/1000);
        iIoOutService.updateById(io);
        return io;
    }


    /**
     * @return
     */
    @Override
    public
    IPage<IoVO> getPage(IPage<IoEntity> page, QueryWrapper<IoEntity> queryWrapper){
        IPage<IoEntity> entityIPage = page(page, queryWrapper);
        IPage<IoVO>     voPage      = new Page<IoVO>();
        List<IoVO>      voList      = Lists.newArrayList();
        voPage.setRecords(voList);
        for(IoEntity entity : entityIPage.getRecords()){
            Item item = itemService.getByCode(entity.getItemCode());
            IoVO vo   = new IoVO();
            BeanUtil.copy(entity, vo);
            vo.setStandardPrice(item.getStandardPrice());
            vo.setReferencePrice(mmVolumeCalculateService.getPrice(vo.getItemCode(), vo.getSupCode()));
            voList.add(vo);
        }

        return voPage;
    }


    /**
     * @return ?????????
     */
    @Override
    public
    IPage<IoVO> getPageOfOthers(IPage<IoEntity> page, QueryWrapper<IoEntity> queryWrapper){
        IPage<IoEntity> entityIPage = page(page, queryWrapper);
        IPage<IoVO>     voPage      = new Page<IoVO>();
        List<IoVO>      voList      = Lists.newArrayList();
        voPage.setRecords(voList);
        for(IoEntity entity : entityIPage.getRecords()){
            Item item = itemService.getByCode(entity.getItemCode());
            IoVO vo   = new IoVO();
            BeanUtil.copy(entity, vo);

            // ?????????
            BigDecimal matPrice = new BigDecimal("0");
            BigDecimal mat = this.baseMapper.getMaterialCostByItemCode(entity.getItemCode());
            if(mat != null){
                matPrice = mat;
            } else {
                throw new RuntimeException("?????????????????????????????????...");
            }
            // ?????????
            BigDecimal laborPrice = new BigDecimal("0");
            BigDecimal lab = this.baseMapper.getLaborCostByItemCode(entity.getItemCode());
            if(lab != null){
                laborPrice = lab;
            }else {
                throw new RuntimeException("?????????????????????????????????...");
            }
            // ??????????????????
            BigDecimal standandPrice = laborPrice.multiply(new BigDecimal("1.4")).add(matPrice) ;

            vo.setStandardPrice(standandPrice);
            vo.setReferencePrice(mmVolumeCalculateService.getPrice(vo.getItemCode(), vo.getSupCode()));
            voList.add(vo);
        }

        return voPage;
    }


    /**
     * @return ?????????
     */
    @Override
    public
    IPage<OutIoVO> getPageOfOut(IPage<OutIoEntity> page, QueryWrapper<OutIoEntity> queryWrapper){
        IPage<OutIoEntity> entityIPage = iIoOutService.page(page, queryWrapper);
        IPage<OutIoVO>     voPage      = new Page<OutIoVO>();
        List<OutIoVO>      voList      = Lists.newArrayList();
        voPage.setRecords(voList);
        for(OutIoEntity entity : entityIPage.getRecords()){
            Item item = itemService.getByCode(entity.getItemCode());
            OutIoVO vo   = new OutIoVO();
            BeanUtil.copy(entity, vo);
            vo.setReferencePrice(mmVolumeCalculateService.getPrice(vo.getItemCode(), vo.getSupCode()));
            voList.add(vo);
        }

        return voPage;
    }


    @Override
    public
    boolean letIoFlow(IoDTO io){
        IoEntity   ioEntity = getById(io.getId());
        U9PrEntity pr       = prService.getById(ioEntity.getPrId());
        prService.letPrFlow(pr, io.getFlowType());

        ioEntity.setStatus(STATUS_LOSEBID);
        ioEntity.setRemark(AuthUtil.getNickName() + " ??????????????????");
        return updateById(ioEntity);
    }

    @Override
    public
    boolean letIoFlowOfWW(OutIoDTO io){
        OutIoEntity   ioEntity = iIoOutService.getById(io.getId());
        OutPrItemEntity pr       = iOutPrItemService.getById(ioEntity.getPrId());
        iOutPrItemService.letPrFlow(pr, io.getFlowType());
        ioEntity.setStatus(STATUS_LOSEBID);
        ioEntity.setRemark(AuthUtil.getNickName() + " ??????????????????");
        return iIoOutService.updateById(ioEntity);
    }

    /**
     * ????????????????????? ??????
     * @return
     */
    @Override
    public
    int toCheckCount(String source){
        IoEntity ioEntity = new IoEntity();
        ioEntity.setSource(source);
        String    mRoleId = paramService.getValue("purch_manager.role_id");
        BladeUser user    = AuthUtil.getUser();
        if(StringUtil.containsAny(user.getRoleName(), "?????????")){
            ioEntity.setStatus(STATUS_WINBID_UNCHECK);
        }else if(StringUtil.containsAny(user.getRoleId(), mRoleId)){
            ioEntity.setStatus(STATUS_WINBID_CHECK1);
        }else{
            return 0;
        }
        return this.baseMapper.selectToCheckCount(ioEntity);
    }

    /**
     * ?????????????????????
     * @return
     */
    @Override
    public
    int toConfirmCount(){
        IoDTO io = new IoDTO();
        io.setPurchCode(AuthUtil.getUserAccount());
        return baseMapper.countToConfirm(io);
    }

    @Override
    public
    int countByStatus(Integer status){
        QueryWrapper<IoEntity> queryWrapper = Wrappers.<IoEntity>query().in("status", status);
        queryWrapper.eq("biz_branch", "price_frame");
        return count(queryWrapper);
    }

    @Override
    public
    IPage<CenterPriceFrame> getByStatus(IPage<CenterPriceFrame> page, Integer status){
        return this.baseMapper.getByStatus(page, status);
    }

    @Override
    public
    int getStatusCount(){
        return this.baseMapper.getStatusCount();
    }

    @Override
    public IoEntity getBySourceAndPrId(PrReq prReq) {
        QueryWrapper<IoEntity> queryWrapper = Condition.getQueryWrapper(new IoEntity());
        queryWrapper.eq("pr_id", prReq.getId());
        if(prReq.getStatuss().equals(IU9PrService.STATUS_WINBID.toString()) ||
            prReq.getStatuss().equals(IU9PrService.STATUS_FLOW_SUBMIT.toString())){
            queryWrapper.in("status", STATUS_WINBID_CHECK1, STATUS_WINBID_CHECK2);
        }
        if(prReq.getStatuss().equals(IU9PrService.STATUS_WAIT.toString())){
            queryWrapper.in("status", STATUS_WAIT);
        }
        queryWrapper.last("limit 0,1");
        return getOne(queryWrapper);
    }

    @Override
    public List<IoEntity> getByPrId(Long prId) {
        QueryWrapper<IoEntity> queryWrapper = Condition.getQueryWrapper(new IoEntity());
        queryWrapper.eq("pr_id", prId);
        return list(queryWrapper);
    }


}
