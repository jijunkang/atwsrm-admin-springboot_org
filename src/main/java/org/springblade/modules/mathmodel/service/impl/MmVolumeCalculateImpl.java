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
        //重新计算
        List<MmSizeEntity> mmSizeEntities = mmSizeService.list(mmSizeService.getQueryWrapper(entity));
        count(mmSizeEntities);
        return true;
    }


//    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean countAll() {
        this.baseMapper.deleteByMmSize(new MmSizeEntity());
        //重新计算
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
     * 计算
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


        //(外径1 + 余量1)ˇ2
        BigDecimal temp11 = mmSizeEntity.getOutD1().add(mmOutMarginEntity1.getMargin()).pow(2);
        //(内孔-内孔余量)ˇ2
        BigDecimal temp12 = mmSizeEntity.getHole().subtract(mmInMarginEntity.getMargin()).pow(2);
        // ((外径1 + 余量1)ˇ2 - (内孔-内孔余量)ˇ2) * (高度1 + 余量1)
        BigDecimal temp13 = temp11.subtract(temp12).multiply(mmSizeEntity.getH1().add(mmOutMarginEntity1.getMargin()));

        //(外径2 + 余量2)ˇ2
        BigDecimal temp21 = mmSizeEntity.getOutD2().add(mmOutMarginEntity2.getMargin()).pow(2);
        //(内孔-内孔余量)ˇ2
        BigDecimal temp22 = mmSizeEntity.getHole().subtract(mmInMarginEntity.getMargin()).pow(2);
        // ((外径2 + 余量2)ˇ2 - (内孔-内孔余量)ˇ2) * (高度2 + 余量2)
        BigDecimal temp23 = temp21.subtract(temp22).multiply(mmSizeEntity.getH2().add(mmOutMarginEntity2.getMargin()));

        // ( ((外径1 + 余量1)ˇ2 - (内孔-内孔余量)ˇ2) * (高度1 + 余量1) + ((外径2 + 余量2)ˇ2 - (内孔-内孔余量)ˇ2) * (高度2 + 余量2) ) * 0.7856 / 1000000
        BigDecimal result = temp13.add(temp23).multiply(new BigDecimal("0.7856"))
            .divide(new BigDecimal("1000000"), 6);

        //体积
        newEntity.setVolume(result);

        //重量 = 体积 * 密度
        newEntity.setWeight(newEntity.getVolume().multiply(newEntity.getDensity()));
        //公斤价 = 公斤单价 + 热处理单价 + 飞削单价
        newEntity.setTotalPrice(priceKg.add(mmHotPriceEntity.getHotPrice()).add(mmHotPriceEntity.getCutPrice()));
        //其他材质 单价 = 体积 * 密度 * 公斤单价
        newEntity.setPrice(newEntity.getVolume().multiply(newEntity.getDensity()).multiply(priceKg)
            .setScale(2, BigDecimal.ROUND_HALF_UP));
        //A105材质 单价 = 重量 * 公斤价
        if ("A105".equals(mmSizeEntity.getMetal())) {
            newEntity.setPrice(newEntity.getWeight().multiply(newEntity.getTotalPrice())
                .setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        //建议采购价 = 单价 * 1.127 / 0.87
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
            //外圆高度余量1
            MmOutMarginEntity mmOutMarginEntity1 = outMarginService.getOutMargin(mmSizeEntity.getChildCode(), mmSizeEntity.getOutD1(), mmSizeEntity.getH1());
            if (mmOutMarginEntity1 == null) {
                logger.info("---countAll---暂无外圆高度余量1：[" + mmSizeEntity.getItemCode() + "]子分类：" + mmSizeEntity.getChildCode() + "外径1：" + mmSizeEntity.getOutD1() + "高度1：" + mmSizeEntity.getH1());
                continue;
            }
            //外圆高度余量2
            MmOutMarginEntity mmOutMarginEntity2 = outMarginService.getOutMargin(mmSizeEntity.getChildCode(), mmSizeEntity.getOutD2(), mmSizeEntity.getH2());
            if (mmOutMarginEntity2 == null) {
                logger.info("---countAll---暂无外圆高度余量2：[" + mmSizeEntity.getItemCode() + "]子分类：" + mmSizeEntity.getChildCode() + "外径2：" + mmSizeEntity.getOutD2() + "高度2：" + mmSizeEntity.getH2());
                continue;
            }
            //内圆余量
            MmInMarginEntity mmInMarginEntity = inMarginService.getInMargin(mmSizeEntity.getChildCode(), mmSizeEntity.getHole(), mmSizeEntity.getH1(), mmSizeEntity.getH2());
            if (mmInMarginEntity == null) {
                logger.info("---countAll---该子分类暂无内孔余量：[" + mmSizeEntity.getItemCode() + "]子分类：" + mmSizeEntity.getChildCode() + "内孔：" + mmSizeEntity.getHole() + "高度1：" + mmSizeEntity.getH1() + "高度2：" + mmSizeEntity.getH2());
                continue;
            }
            //密度
            MmDensityEntity mmDensityEntity = densityService.getByMetal(mmSizeEntity.getMetal());
            if (mmDensityEntity == null) {
                logger.info("---countAll---该材质暂无密度：[" + mmSizeEntity.getItemCode() + "]" + mmSizeEntity.getMetal());
                continue;
            }
            //原材料价格
            Item item = itemService.getByCode(mmSizeEntity.getItemCode());
            BigDecimal priceKg = materialPriceService.getPriceKg(item.getName());
            if (priceKg == null) {
                logger.info("---countAll---暂无公斤单价：[" + item.getCode() + "] 主分类:" + item.getMainCode() + " 规格:" + item.getSpecs() + " 材质:" + item.getMatQuality());
                continue;
            }
            //供应商交叉关系 （多个供应商）
            List<SupItem> supItems = supItemService.listSupItemByItemCode(mmSizeEntity.getItemCode());
            for (SupItem supItem : supItems) {
                //热处理(飞削)单价
                MmHotPriceEntity mmHotPriceEntity = mmHotPriceService.getByMetalAndSupCode(mmSizeEntity.getMetal(), supItem.getSupCode());
                if (mmHotPriceEntity == null) {
                    if("A105".equals(mmSizeEntity.getMetal())){
                        logger.info("---countAll---该供应商材质暂无热处理(飞削)单价：" + mmSizeEntity.getMetal() + " " + supItem.getSupCode());
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

                //计算
                saveVolumeCalculate(mmSizeEntity, mmOutMarginEntity1, mmOutMarginEntity2, mmInMarginEntity, priceKg, mmHotPriceEntity, newEntity);
            }
        }
    }

}
