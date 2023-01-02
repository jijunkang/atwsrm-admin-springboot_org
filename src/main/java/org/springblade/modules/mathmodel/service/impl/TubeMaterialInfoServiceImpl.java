package org.springblade.modules.mathmodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.modules.mathmodel.entity.TubeMaterialInfoEntity;
import org.springblade.modules.mathmodel.mapper.TubeMaterialInfoMapper;
import org.springblade.modules.mathmodel.service.TubeMaterialInfoService;
import org.springblade.modules.pr.entity.ItemInfoMaterialPriceMaliy;
import org.springblade.modules.pr.mapper.MaterialMailyVoMapper;
import org.springblade.modules.pr.vo.MaterialMaliyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.springblade.common.utils.ItemAnalysisUtil.getEntity;

/**
 * Author: 昕月
 * Date：2022/6/2 9:32
 * Desc:
 */
@Service
@Slf4j
public class TubeMaterialInfoServiceImpl  extends BaseServiceImpl<TubeMaterialInfoMapper, TubeMaterialInfoEntity> implements TubeMaterialInfoService {

    @Autowired
    private TubeMaterialInfoMapper tubeMaterialInfoMapper;

    @Autowired
    MaterialMailyVoMapper materialMailyVoMapper;

    private final Double PI = 3.14;

    @Override
    public Wrapper<TubeMaterialInfoEntity> getQueryWrapper(TubeMaterialInfoEntity tubeMaterialInfoEntity) {
        QueryWrapper<TubeMaterialInfoEntity> queryWrapper = Condition.getQueryWrapper(tubeMaterialInfoEntity);
        return queryWrapper;
    }

    @Override
    public void export(TubeMaterialInfoEntity materialInfoEntity, Query query, HttpServletResponse response) {
        List<TubeMaterialInfoEntity> list = list(getQueryWrapper(materialInfoEntity));
        if (list.size() == 0){
            throw  new RuntimeException("暂无数据,导出失败");
        }
        ArrayList<TubeMaterialInfoEntity> excel = new ArrayList<>();
        list.forEach( entity -> {
            TubeMaterialInfoEntity dto = BeanUtil.copy(entity, TubeMaterialInfoEntity.class);
            excel.add(dto);
        });
        ExcelUtils.defaultExport(excel,TubeMaterialInfoEntity.class,"管料自动获取报表"+ DateUtil.formatDate(new Date()),response);
    }

    /**
     * 自动获取报表
     * @param infoEntity
     * @return
     */
    @Override
    public List<TubeMaterialInfoEntity> getInfoList(TubeMaterialInfoEntity infoEntity) {
        List<TubeMaterialInfoEntity> list = new ArrayList<>();
        TubeMaterialInfoEntity entity = getEntity(infoEntity.getItemDesc());
        if (entity.getName() != null){
            //查找是不是有多家供应商
            List<TubeMaterialInfoEntity> supList = tubeMaterialInfoMapper.selectSupName(entity);

            if (supList.size()>0){
                Double externalDiameter = entity.getExternalDiameter();
                Double internalDiamete = entity.getInternalDiamete();
                Double length = entity.getLength();



                for (TubeMaterialInfoEntity tubeMaterialInfo : supList) {
                    entity.setSupplierCode(tubeMaterialInfo.getSupplierCode());  //供应商编号
                    entity.setSupplierName(tubeMaterialInfo.getSupplierName());  //供应商名称

                    //获取余量
                    MaterialMaliyVO materialMaliyVO1 = materialMailyVoMapper.selectRes(entity.getSupplierCode(), entity.getTheMaterial());

                    String outerSizeRes=materialMaliyVO1.getOuterSize();
                    String heightSizeRes=materialMaliyVO1.getHeightSize();
                    String innerSizeRes=materialMaliyVO1.getInnerSize();
                    Double Exter=entity.getExternalDiameter()+ Double.valueOf(outerSizeRes)  ;//余量外径
                    Double Inner=entity.getInternalDiamete()- Double.valueOf(innerSizeRes);//余量内径
                    Double Length=entity.getLength()+ Double.valueOf(heightSizeRes);//余量长度

                    if (entity.getExternalDiameter() <= 301){
                        TubeMaterialInfoEntity materialExternalDiameter = tubeMaterialInfoMapper.selectExternal(Exter);

                        //根据外径找内径

                        //根据外径找内径
                        TubeMaterialInfoEntity materialinner=null;
                        if(materialExternalDiameter!=null){
                            materialinner = tubeMaterialInfoMapper.selectInner(materialExternalDiameter.getExternalDiameter(),Inner);
                        }


                        if (materialinner != null){
                            // 余量查询
                            entity.setDiameterAllowance(Double.valueOf(outerSizeRes));// 外径余量
                            entity.setApertureAllowance(Double.valueOf(innerSizeRes)); //孔径余量
                            entity.setExternalDiameter(materialinner.getExternalDiameter());//原材料外径
                            entity.setInternalDiamete(materialinner.getInternalDiamete());//原材料内径
                            entity.setLength(Length);

                            //材料费
                            TubeMaterialInfoEntity materialList = getMaterialPrice(entity,entity.getExternalDiameter(), entity.getInternalDiamete(), entity.getTheMaterial(), Length,entity.getSupplierCode());
                            entity.setTheMaterialPrice(materialList.getTheMaterialPrice()); // 材料单价
                            entity.setMaterialPrice(materialList.getMaterialPrice());  // 材料费
                            entity.setWeight(materialList.getWeight()); // 单重

                            // 喷涂费
                            TubeMaterialInfoEntity sprayList = getSprayPrice(entity, length, internalDiamete, entity.getCoating(), entity.getTheMaterial(), entity.getSupplierCode());
                            entity.setCoatingPrice(sprayList.getCoatingPrice()); // 喷涂单价
                            entity.setSprayPrice(sprayList.getSprayPrice());// 喷涂费
                            entity.setCoatingLength(length); // 喷涂长度
                            entity.setCoatingInternalDiameter(internalDiamete); // 喷涂内径
                            entity.setCoatingArea(sprayList.getCoatingArea()); //喷涂面积

                            // 加工费
                            TubeMaterialInfoEntity proscessList = getProscessPrice(entity, externalDiameter,internalDiamete,length, entity.getSupplierCode());
                            entity.setProcessingFee(proscessList.getProcessingFee()); //加工费
                            entity.setPrice(proscessList.getPrice()); // 切割费

                            // 产品单价   产品单价=材料费+喷涂费+加工费+切割费
                            BigDecimal productPrice = null;
                            if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                                productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                                entity.setUnitPrice(productPrice);
                            }
                            entity.setUnitPrice(productPrice);

                            entity.setItemDesc(infoEntity.getItemDesc());   //物料描述
                            entity.setItemCode(infoEntity.getItemCode());   //物料编号
                            entity.setPrLn(infoEntity.getPrLn());    //行号
                            entity.setPrCode(infoEntity.getPrCode());  //pr
                            entity.setPriceNum(infoEntity.getPriceNum());  //数量

                            if(entity.getUnitPrice()!=null && entity.getPriceNum()!=null) {
                                entity.setTotalPrice(entity.getUnitPrice().multiply((entity.getPriceNum()))); //总价
                            }
                            entity.setCreateTime(new Date());
                            list.add(entity);
                            //查询数据表中是否有该prCode和prLn
                            TubeMaterialInfoEntity code = tubeMaterialInfoMapper.selectByInfo(infoEntity.getItemCode());
                            if (code != null){
                                this.baseMapper.deleteByItemCode(infoEntity.getItemCode());
                                this.baseMapper.insert(entity);
                            }else {
                                this.baseMapper.insert(entity);
                            }

                        }else {
                            double exter = externalDiameter + 10;
                            double inner = internalDiamete - 10;

                            entity.setExternalDiameter(exter);//原材料外径
                            entity.setInternalDiamete(inner);//原材料内径
                            entity.setLength(length+8);
                            // 余量查询
                            entity.setDiameterAllowance(10D);// 外径余量
                            entity.setApertureAllowance(10D); //孔径余量

                            //材料费
                            TubeMaterialInfoEntity materialList = getMaterialPrice(entity, exter, inner, entity.getTheMaterial(),Length,entity.getSupplierCode());
                            entity.setTheMaterialPrice(materialList.getTheMaterialPrice()); // 材料单价
                            entity.setMaterialPrice(materialList.getMaterialPrice());  // 材料费
                            entity.setWeight(materialList.getWeight()); // 单重

                            // 喷涂费
                            TubeMaterialInfoEntity sprayList = getSprayPrice(entity, length,internalDiamete, entity.getCoating(), entity.getTheMaterial(), entity.getSupplierCode());
                            entity.setCoatingPrice(sprayList.getCoatingPrice()); // 喷涂单价
                            entity.setSprayPrice(sprayList.getSprayPrice());// 喷涂费
                            entity.setCoatingLength(length); // 喷涂长度
                            entity.setCoatingInternalDiameter(entity.getInternalDiamete()); // 喷涂内径
                            entity.setCoatingArea(sprayList.getCoatingArea()); //喷涂面积

                            // 加工费
                            TubeMaterialInfoEntity proscessList = getProscessPrice(entity, externalDiameter,internalDiamete,length, entity.getSupplierCode());
                            entity.setProcessingFee(proscessList.getProcessingFee()); //加工费
                            entity.setPrice(proscessList.getPrice()); // 切割费

                            // 产品单价   产品单价=材料费+喷涂费+加工费+切割费
                            BigDecimal productPrice = null;
                            if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                                productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                                entity.setUnitPrice(productPrice);
                            }
                            entity.setUnitPrice(productPrice);

                            entity.setPrLn(infoEntity.getPrLn());    //行号
                            entity.setPrCode(infoEntity.getPrCode());  //pr
                            entity.setPriceNum(infoEntity.getPriceNum());  //数量
                            entity.setItemDesc(infoEntity.getItemDesc());   //物料描述
                            entity.setItemCode(infoEntity.getItemCode());   //物料编号
                            if(entity.getUnitPrice()!=null && entity.getPriceNum()!=null) {
                                entity.setTotalPrice(entity.getUnitPrice().multiply((entity.getPriceNum()))); //总价
                            }
                            entity.setCreateTime(new Date());
                            list.add(entity);
                            //查询数据表中是否有该prCode和prLn
                            TubeMaterialInfoEntity code = tubeMaterialInfoMapper.selectByInfo(infoEntity.getItemCode());
                            if (code != null){
                                this.baseMapper.deleteByItemCode(infoEntity.getItemCode());
                                this.baseMapper.insert(entity);
                            }else {
                                this.baseMapper.insert(entity);
                            }
                        }
                    }
                    if (entity.getExternalDiameter() >301){
                        double exter = externalDiameter + 10;
                        double inner = internalDiamete - 10;

                        // 余量查询
                        entity.setDiameterAllowance(10D);// 外径余量
                        entity.setApertureAllowance(10D); //孔径余量
                        entity.setExternalDiameter(exter);//原材料外径
                        entity.setInternalDiamete(inner);//原材料内径
                        entity.setLength(length+8);

                        //材料费
                        TubeMaterialInfoEntity materialList = getMaterialPrice(entity, exter, inner, entity.getTheMaterial(), Length,entity.getSupplierCode());
                        entity.setTheMaterialPrice(materialList.getTheMaterialPrice()); // 材料单价
                        entity.setMaterialPrice(materialList.getMaterialPrice());  // 材料费
                        entity.setWeight(materialList.getWeight()); // 单重

                        // 喷涂费
                        TubeMaterialInfoEntity sprayList = getSprayPrice(entity, length,internalDiamete, entity.getCoating(), entity.getTheMaterial(), entity.getSupplierCode());
                        entity.setCoatingPrice(sprayList.getCoatingPrice()); // 喷涂单价
                        entity.setSprayPrice(sprayList.getSprayPrice());// 喷涂费
                        entity.setCoatingLength(length); // 喷涂长度
                        entity.setCoatingInternalDiameter(entity.getInternalDiamete()); // 喷涂内径
                        entity.setCoatingArea(sprayList.getCoatingArea()); //喷涂面积

                        // 加工费
                        TubeMaterialInfoEntity proscessList = getProscessPrice(entity, externalDiameter, internalDiamete,length, entity.getSupplierCode());
                        entity.setProcessingFee(proscessList.getProcessingFee()); //加工费
                        entity.setPrice(proscessList.getPrice()); // 切割费

                        // 产品单价   产品单价=材料费+喷涂费+加工费+切割费
                        BigDecimal productPrice = null;
                        if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                            productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                            entity.setUnitPrice(productPrice);
                        }
                        entity.setUnitPrice(productPrice);

                        entity.setPrLn(infoEntity.getPrLn());    //行号
                        entity.setPrCode(infoEntity.getPrCode());  //pr
                        entity.setPriceNum(infoEntity.getPriceNum());  //数量
                        entity.setItemDesc(infoEntity.getItemDesc());   //物料描述
                        entity.setItemCode(infoEntity.getItemCode());   //物料编号
                        if(entity.getUnitPrice()!=null && entity.getPriceNum()!=null) {
                            entity.setTotalPrice(entity.getUnitPrice().multiply((entity.getPriceNum()))); //总价
                        }
                        entity.setCreateTime(new Date());
                        list.add(entity);
                        //查询数据表中是否有该prCode和prLn
                        TubeMaterialInfoEntity code = tubeMaterialInfoMapper.selectByInfo(infoEntity.getItemCode());
                        if (code != null){
                            this.baseMapper.deleteByItemCode(infoEntity.getItemCode());
                            this.baseMapper.insert(entity);
                        }else {
                            this.baseMapper.insert(entity);
                        }
                    }
                }
            }
        }
        return list;
    }

    private TubeMaterialInfoEntity getProscessPrice(TubeMaterialInfoEntity entity, Double externalDiameter, Double internalDiamete, Double length, String supplierCode) {
        TubeMaterialInfoEntity tubeMaterialInfo = tubeMaterialInfoMapper.selectProcessPrice(externalDiameter,internalDiamete,length,supplierCode);
        // 长度加余量
        length += 8;
        // 切割费
        if (length < 200) {
            //有切割费
            if (externalDiameter <= 80) {
                entity.setPrice(BigDecimal.valueOf(6));
            }
            if (externalDiameter > 80 && externalDiameter <= 150) {
                entity.setPrice(BigDecimal.valueOf(7.2));
            }
            if (externalDiameter > 150 && externalDiameter <= 200) {
                entity.setPrice(BigDecimal.valueOf(15.6));
            }
            if (externalDiameter > 200) {
                entity.setPrice(BigDecimal.valueOf(18)); // 切割费
            }
        }else {
            entity.setPrice(new BigDecimal("0"));
        }
        if (tubeMaterialInfo == null){
            entity.setProcessingFee(null);  // 加工费
            return entity;
        }

        entity.setProcessingFee(tubeMaterialInfo.getProcessingFee().setScale(2,BigDecimal.ROUND_DOWN)); // 加工费
        return entity;
    }

    private TubeMaterialInfoEntity getSprayPrice(TubeMaterialInfoEntity entity, Double length, Double internalDiamete, String coating, String theMaterial, String supplierCode) {
        Double price = tubeMaterialInfoMapper.selectBySprayPrice(coating,theMaterial,supplierCode);
        if (price == null){
            entity.setSprayPrice(null);  //喷涂费
            entity.setCoatingPrice(null);
            return entity;
        }
        entity.setCoatingPrice(BigDecimal.valueOf(price));  //喷涂单价
        //计算喷涂面积
        BigDecimal coatingArea = new BigDecimal(internalDiamete).multiply(BigDecimal.valueOf(PI)).multiply(BigDecimal.valueOf(length)).divide(BigDecimal.valueOf(100));
        entity.setCoatingArea(coatingArea); //喷涂面积
        //计算喷涂费
        BigDecimal sprayPrice = new BigDecimal(internalDiamete).multiply(BigDecimal.valueOf(PI)).multiply(BigDecimal.valueOf(length)).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(price));

        if (("G05".equals(coating) || "G06".equals(coating)) && sprayPrice.doubleValue() < 40) {
            entity.setSprayPrice(new BigDecimal(40));
        } else if (("G14".equals(coating) || "G20".equals(coating)) && sprayPrice.doubleValue() < 40) {
            entity.setSprayPrice(new BigDecimal(55));
        } else {
            entity.setSprayPrice(sprayPrice.setScale(2, BigDecimal.ROUND_DOWN));  // 喷涂费
        }
        return entity;
    }



    private TubeMaterialInfoEntity getMaterialPrice(TubeMaterialInfoEntity entity, Double externalDiameter, Double internalDiamete, String theMaterial, Double length,String supplierCode) {
        String range = "";

        // 查找材质单价
        //Double itemprice = tubeMaterialInfoMapper.selectTheMaterialPrice(theMaterial, externalDiameter.toString(),supplierCode);
        ItemInfoMaterialPriceMaliy itemInfoMaterialPriceMaliy = materialMailyVoMapper.selectMaterialPriceVO(theMaterial, externalDiameter.toString(), supplierCode);
        if (itemInfoMaterialPriceMaliy == null){
            entity.setTheMaterialPrice(null); // 材料单价
            entity.setMaterialPrice(null);
            return entity;
        }
        BigDecimal itemprice =itemInfoMaterialPriceMaliy.getTheMaterialPrice();
        entity.setTheMaterialPrice(itemprice);

        String k=itemInfoMaterialPriceMaliy.getK();

        //计算单重
        BigDecimal price = new BigDecimal(externalDiameter).multiply(BigDecimal.valueOf(externalDiameter)).subtract(BigDecimal.valueOf(internalDiamete).multiply(BigDecimal.valueOf(internalDiamete)));
        BigDecimal   weight =   new BigDecimal(String.valueOf(price)).multiply(BigDecimal.valueOf(PI)).divide(BigDecimal.valueOf(4)).multiply(BigDecimal.valueOf(length)).multiply(BigDecimal.valueOf(8)).divide(BigDecimal.valueOf(1000000));
        //计算材料费
        BigDecimal MaterialPrice = new BigDecimal(String.valueOf(weight)).multiply(entity.getTheMaterialPrice()).multiply(new BigDecimal(k));
        entity.setWeight(weight.setScale(2,BigDecimal.ROUND_DOWN));  // 单重
        entity.setMaterialPrice(MaterialPrice.setScale(2,BigDecimal.ROUND_DOWN)); // 材料费
        entity.setTheMaterialPrice(entity.getTheMaterialPrice()); // 材料单价
        return entity;
    }
}
