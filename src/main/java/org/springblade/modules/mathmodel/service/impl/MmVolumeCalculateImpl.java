package org.springblade.modules.mathmodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.material.service.IMaterialPriceService;
import org.springblade.modules.mathmodel.entity.*;
import org.springblade.modules.mathmodel.mapper.MmVolumeCalculateMapper;
import org.springblade.modules.mathmodel.service.*;
import org.springblade.modules.mathmodel.vo.MmVolumeCalculateVO;
import org.springblade.modules.priceframe.service.IPriceFrameService;
import org.springblade.modules.supitem.entity.SupItem;
import org.springblade.modules.supitem.service.ISupItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author libin
 * @date 11:18 2020/9/11
 **/
@Service
public class MmVolumeCalculateImpl extends BaseServiceImpl<MmVolumeCalculateMapper, MmVolumeCalculateEntity> implements IMmVolumeCalculateService {


    @Autowired
    private IItemService itemService;

    @Autowired
    private IMmSizeService mmSizeService;

    @Autowired
    private IMmOutMarginService outMarginService;

    @Autowired
    private IMmInMarginService inMarginService;

    @Autowired
    private IMmDensityService densityService;

    @Autowired
    private IMaterialPriceService materialPriceService;

    @Autowired
    private IMmHotPriceService mmHotPriceService;

    @Autowired
    private ISupItemService supItemService;

    @Autowired
    private IPriceFrameService priceFrameService;

    private static final Logger logger = LoggerFactory.getLogger(MmVolumeCalculateImpl.class);

//    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public boolean countBatch(MmSizeEntity entity) {
        this.baseMapper.deleteByMmSize(entity);
        //????????????
        List<MmSizeEntity> mmSizeEntities = mmSizeService.list(mmSizeService.getQueryWrapper(entity));
        count(mmSizeEntities);
        return true;
    }


//    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean countAll() {
        this.baseMapper.deleteByMmSize(new MmSizeEntity());
        //????????????
        List<MmSizeEntity> mmSizeEntities = mmSizeService.list(mmSizeService.getQueryWrapper(new MmSizeEntity()));
        count(mmSizeEntities);
        return true;
    }

    @Override
    public BigDecimal getPrice(String itemCode, String supCode) {
        QueryWrapper<MmVolumeCalculateEntity> queryWrapper = Condition.getQueryWrapper(new MmVolumeCalculateEntity());
        queryWrapper.like("item_code", itemCode);
        queryWrapper.like("sup_code", supCode);
        MmVolumeCalculateEntity mmVolumeCalculateEntity = getOne(queryWrapper);
        if (mmVolumeCalculateEntity == null) {
            return null;
        }
        return mmVolumeCalculateEntity.getPrice();
    }

    @Override
    public Map<String, Object> getPriceByCode(MmVolumeCalculateVO mmVolumeCalculateVO) {
        Map<String, Object> result = new HashMap<>(2);
        result.put("referencePrice", getPrice(mmVolumeCalculateVO.getItemCode(), mmVolumeCalculateVO.getSupCode()));
        result.put("framePrice", priceFrameService.getPrice(mmVolumeCalculateVO.getItemCode(), mmVolumeCalculateVO.getSupCode(), mmVolumeCalculateVO.getPriceNum()));
        return result;
    }

    @Override
    public List<MmVolumeCalculateEntity> getByItemCode(String itemCode) {
        QueryWrapper<MmVolumeCalculateEntity> queryWrapper = Condition.getQueryWrapper(new MmVolumeCalculateEntity());
        queryWrapper.like("item_code", itemCode);
        return list(queryWrapper);
    }


    /**
     * ??????
     *
     * @param mmSizeEntity       MmSizeEntity
     * @param mmOutMarginEntity1 MmOutMarginEntity
     * @param mmOutMarginEntity2 MmOutMarginEntity
     * @param mmInMarginEntity   MmInMarginEntity
     * @param priceKg            BigDecimal
     * @param mmHotPriceEntity   MmHotPriceEntity
     * @param newEntity          MmVolumeCalculateEntity
     * @return boolean
     */
    private boolean saveVolumeCalculate(MmSizeEntity mmSizeEntity, MmOutMarginEntity mmOutMarginEntity1, MmOutMarginEntity mmOutMarginEntity2, MmInMarginEntity mmInMarginEntity, BigDecimal priceKg, MmHotPriceEntity mmHotPriceEntity, MmVolumeCalculateEntity newEntity) {
        newEntity.setOutD1(mmSizeEntity.getOutD1());
        newEntity.setOutD2(mmSizeEntity.getOutD2());
        newEntity.setH1(mmSizeEntity.getH1());
        newEntity.setH2(mmSizeEntity.getH2());
        newEntity.setOutMargin1(mmOutMarginEntity1.getMargin());
        newEntity.setOutMargin2(mmOutMarginEntity2.getMargin());
        newEntity.setInMargin(mmInMarginEntity.getMargin());
        newEntity.setHole(mmSizeEntity.getHole());
        newEntity.setKgPrice(priceKg);
        newEntity.setHotPrice(mmHotPriceEntity.getHotPrice());
        newEntity.setCutPrice(mmHotPriceEntity.getCutPrice());
        newEntity.setItemCode(mmSizeEntity.getItemCode());
        newEntity.setItemName(mmSizeEntity.getItemName());


        //(??????1 + ??????1)??2
        BigDecimal temp11 = mmSizeEntity.getOutD1().add(mmOutMarginEntity1.getMargin()).pow(2);
        //(??????-????????????)??2
        BigDecimal temp12 = mmSizeEntity.getHole().subtract(mmInMarginEntity.getMargin()).pow(2);
        // ((??????1 + ??????1)??2 - (??????-????????????)??2) * (??????1 + ??????1)
        BigDecimal temp13 = temp11.subtract(temp12).multiply(mmSizeEntity.getH1().add(mmOutMarginEntity1.getMargin()));

        //(??????2 + ??????2)??2
        BigDecimal temp21 = mmSizeEntity.getOutD2().add(mmOutMarginEntity2.getMargin()).pow(2);
        //(??????-????????????)??2
        BigDecimal temp22 = mmSizeEntity.getHole().subtract(mmInMarginEntity.getMargin()).pow(2);
        // ((??????2 + ??????2)??2 - (??????-????????????)??2) * (??????2 + ??????2)
        BigDecimal temp23 = temp21.subtract(temp22).multiply(mmSizeEntity.getH2().add(mmOutMarginEntity2.getMargin()));

        // ( ((??????1 + ??????1)??2 - (??????-????????????)??2) * (??????1 + ??????1) + ((??????2 + ??????2)??2 - (??????-????????????)??2) * (??????2 + ??????2) ) * 0.7856 / 1000000
        BigDecimal result = temp13.add(temp23).multiply(new BigDecimal("0.7856"))
            .divide(new BigDecimal("1000000"), 6);

        //??????
        newEntity.setVolume(result);

        //?????? = ?????? * ??????
        newEntity.setWeight(newEntity.getVolume().multiply(newEntity.getDensity()));
        //????????? = ???????????? + ??????????????? + ????????????
        newEntity.setTotalPrice(priceKg.add(mmHotPriceEntity.getHotPrice()).add(mmHotPriceEntity.getCutPrice()));
        //???????????? ?????? = ?????? * ?????? * ????????????
        newEntity.setPrice(newEntity.getVolume().multiply(newEntity.getDensity()).multiply(priceKg)
            .setScale(2, BigDecimal.ROUND_HALF_UP));
        //A105?????? ?????? = ?????? * ?????????
        if ("A105".equals(mmSizeEntity.getMetal())) {
            newEntity.setPrice(newEntity.getWeight().multiply(newEntity.getTotalPrice())
                .setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        //??????????????? = ?????? * 1.127 / 0.87
        newEntity.setAdvicePrice(newEntity.getPrice().multiply(new BigDecimal("1.127")).divide(new BigDecimal("0.87"), 6));
        return save(newEntity);
    }

    /**
     * count
     *
     * @param mmSizeEntities List
     */
    private void count(List<MmSizeEntity> mmSizeEntities) {
        for (MmSizeEntity mmSizeEntity : mmSizeEntities) {
            //??????????????????1
            MmOutMarginEntity mmOutMarginEntity1 = outMarginService.getOutMargin(mmSizeEntity.getChildCode(), mmSizeEntity.getOutD1(), mmSizeEntity.getH1());
            if (mmOutMarginEntity1 == null) {
                logger.info("---countAll---????????????????????????1???[" + mmSizeEntity.getItemCode() + "]????????????" + mmSizeEntity.getChildCode() + "??????1???" + mmSizeEntity.getOutD1() + "??????1???" + mmSizeEntity.getH1());
                continue;
            }
            //??????????????????2
            MmOutMarginEntity mmOutMarginEntity2 = outMarginService.getOutMargin(mmSizeEntity.getChildCode(), mmSizeEntity.getOutD2(), mmSizeEntity.getH2());
            if (mmOutMarginEntity2 == null) {
                logger.info("---countAll---????????????????????????2???[" + mmSizeEntity.getItemCode() + "]????????????" + mmSizeEntity.getChildCode() + "??????2???" + mmSizeEntity.getOutD2() + "??????2???" + mmSizeEntity.getH2());
                continue;
            }
            //????????????
            MmInMarginEntity mmInMarginEntity = inMarginService.getInMargin(mmSizeEntity.getChildCode(), mmSizeEntity.getHole(), mmSizeEntity.getH1(), mmSizeEntity.getH2());
            if (mmInMarginEntity == null) {
                logger.info("---countAll---?????????????????????????????????[" + mmSizeEntity.getItemCode() + "]????????????" + mmSizeEntity.getChildCode() + "?????????" + mmSizeEntity.getHole() + "??????1???" + mmSizeEntity.getH1() + "??????2???" + mmSizeEntity.getH2());
                continue;
            }
            //??????
            MmDensityEntity mmDensityEntity = densityService.getByMetal(mmSizeEntity.getMetal());
            if (mmDensityEntity == null) {
                logger.info("---countAll---????????????????????????[" + mmSizeEntity.getItemCode() + "]" + mmSizeEntity.getMetal());
                continue;
            }
            //???????????????
            Item item = itemService.getByCode(mmSizeEntity.getItemCode());
            BigDecimal priceKg = materialPriceService.getPriceKg(item.getName());
            if (priceKg == null) {
                logger.info("---countAll---?????????????????????[" + item.getCode() + "] ?????????:" + item.getMainCode() + " ??????:" + item.getSpecs() + " ??????:" + item.getMatQuality());
                continue;
            }
            //????????????????????? ?????????????????????
            List<SupItem> supItems = supItemService.listSupItemByItemCode(mmSizeEntity.getItemCode());
            for (SupItem supItem : supItems) {
                //?????????(??????)??????
                MmHotPriceEntity mmHotPriceEntity = mmHotPriceService.getByMetalAndSupCode(mmSizeEntity.getMetal(), supItem.getSupCode());
                if (mmHotPriceEntity == null) {
                    if("A105".equals(mmSizeEntity.getMetal())){
                        logger.info("---countAll---?????????????????????????????????(??????)?????????" + mmSizeEntity.getMetal() + " " + supItem.getSupCode());
                        continue;
                    }
                    mmHotPriceEntity = new MmHotPriceEntity();
                    mmHotPriceEntity.setHotPrice(BigDecimal.ZERO);
                    mmHotPriceEntity.setCutPrice(BigDecimal.ZERO);
                }
                MmVolumeCalculateEntity newEntity = new MmVolumeCalculateEntity();
                newEntity.setMainCode(item.getMainCode());
                newEntity.setChildCode(mmSizeEntity.getChildCode());
                newEntity.setSupCode(supItem.getSupCode());
                newEntity.setSupName(supItem.getSupName());
                newEntity.setDensity(mmDensityEntity.getDensity());

                //??????
                saveVolumeCalculate(mmSizeEntity, mmOutMarginEntity1, mmOutMarginEntity2, mmInMarginEntity, priceKg, mmHotPriceEntity, newEntity);
            }
        }
    }

}
