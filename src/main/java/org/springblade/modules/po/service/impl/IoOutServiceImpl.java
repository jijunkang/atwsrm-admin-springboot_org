package org.springblade.modules.po.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.api.client.util.Lists;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.mathmodel.service.IMmVolumeCalculateService;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.service.IOutPrItemService;
import org.springblade.modules.po.entity.OutIoEntity;
import org.springblade.modules.po.mapper.IoMapper;
import org.springblade.modules.po.mapper.OutIoMapper;
import org.springblade.modules.po.service.IIoOutService;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.po.service.IPoService;
import org.springblade.modules.po.vo.IoVO;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.pr.service.IU9PrService;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务实现类
 * @author Will
 */
@Service
public
class IoOutServiceImpl extends BaseServiceImpl<OutIoMapper, OutIoEntity> implements IIoOutService {

    @Autowired
    IParamService paramService;

    @Autowired
    @Lazy
    IOutPrItemService iOutPrItemService;

    @Autowired
    @Lazy
    IItemService itemService;

    @Autowired
    @Lazy
    IPoService poService;

    @Autowired @Lazy
    IMmVolumeCalculateService mmVolumeCalculateService;

    @Autowired
    IoMapper ioMapper;

    /**
     * 审核拒绝 io
     */
    private
    void checkReject(OutIoEntity entity, String flowType, String remark){
        OutPrItemEntity pr = iOutPrItemService.getById(entity.getPrId());
        pr.setStatus(IOutPrItemService.STATUS_FLOW);
        pr.setFlowType(flowType);
        String refuseCase = AuthUtil.getNickName() + "审批拒绝:" + remark;
        pr.setCheckRemark(refuseCase);
        iOutPrItemService.updateById(pr);
    }


    /**
     * @return
     */
    @Override
    public
    IPage<IoVO> getPage(IPage<OutIoEntity> page, QueryWrapper<OutIoEntity> queryWrapper){
        IPage<OutIoEntity> entityIPage = page(page, queryWrapper);
        IPage<IoVO>     voPage      = new Page<IoVO>();
        List<IoVO>      voList      = Lists.newArrayList();
        voPage.setRecords(voList);
        for(OutIoEntity entity : entityIPage.getRecords()){
            Item item = itemService.getByCode(entity.getItemCode());
            IoVO vo   = new IoVO();
            BeanUtil.copy(entity, vo);
            vo.setStandardPrice(item.getStandardPrice());
            vo.setReferencePrice(mmVolumeCalculateService.getPrice(vo.getItemCode(), vo.getSupCode()));
            voList.add(vo);
        }

        return voPage;
    }

    @Override
    public
    int countByStatus(Integer status){
        QueryWrapper<OutIoEntity> queryWrapper = Wrappers.<OutIoEntity>query().in("status", status);
        queryWrapper.eq("biz_branch", "price_frame");
        return count(queryWrapper);
    }

    @Override
    public List<OutIoEntity> getByPrId(Long prId) {
        QueryWrapper<OutIoEntity> queryWrapper = Condition.getQueryWrapper(new OutIoEntity());
        queryWrapper.eq("pr_id", prId);
        return list(queryWrapper);
    }

    @Override
    public OutIoEntity getBySourceAndPrId(PrReq prReq) {
        QueryWrapper<OutIoEntity> queryWrapper = Condition.getQueryWrapper(new OutIoEntity());
        queryWrapper.eq("pr_id", prReq.getId());
        if(prReq.getStatuss().equals(iOutPrItemService.STATUS_WINBID.toString()) ||
            prReq.getStatuss().equals(iOutPrItemService.STATUS_FLOW_SUBMIT.toString())){
            queryWrapper.in("status", STATUS_WINBID_CHECK1, STATUS_WINBID_CHECK2);
        }
        if(prReq.getStatuss().equals(iOutPrItemService.STATUS_ACCORD.toString())){
            queryWrapper.in("status", STATUS_WAIT);
        }
        queryWrapper.last("limit 0,1");
        return getOne(queryWrapper);
    }

    @Override
    public boolean check2OfWW(CheckDTO checkDto) {
        OutIoEntity ioEntity = this.baseMapper.getWinBidIo(checkDto.getId().toString());

        if (ioMapper.poItemIsExisted(ioEntity.getPrId()) < 1) {
            // 审核通拒绝->Pr流标
            if (STATUS_WINBID_REJECT.equals(checkDto.getStatus())) {
                ioEntity.setRefuseCause(checkDto.getRemark());
                checkReject(ioEntity, IU9PrService.FLOW_TYPE_ATWREJECT, checkDto.getRemark());
            }

            // 审核通过->待下单
            if (STATUS_WAIT.equals(checkDto.getStatus())) {

                OutPrItemEntity pr = iOutPrItemService.getById(ioEntity.getPrId());
                pr.setStatus(IU9PrService.STATUS_WAIT);
                poService.placeOrderByIoOfWW(ioEntity, IPoItemService.SOURCE_INNER, ioEntity.getId());
                iOutPrItemService.updateById(pr);
            }
            ioEntity.setStatus(checkDto.getStatus());
            ioEntity.setRemark(checkDto.getRemark());
            updateById(ioEntity);
        }
        return true;
    }

}
