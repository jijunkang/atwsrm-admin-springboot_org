package org.springblade.modules.mathmodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.modules.mathmodel.entity.MailyMaterialTotalEntity;
import org.springblade.modules.mathmodel.mapper.TubeMaterialMapper;
import org.springblade.modules.mathmodel.service.TubeMaterialService;
import org.springblade.modules.pr.mapper.MaterialMailyVoMapper;
import org.springblade.modules.pr.vo.MaterialMaliyVO;
import org.springblade.modules.system.service.IDictBizService;
import org.springblade.modules.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springblade.common.utils.ItemAnalysisUtil.getItemEntity;
import static org.springblade.common.utils.ItemAnalysisUtil.getItemInfoGuanBangLiao;


/**
 * Author: 昕月
 * Date：2022/5/24 13:36
 * Desc:
 */
@Service
@Slf4j
public class TubeMaterialServiceImpl extends BaseServiceImpl<TubeMaterialMapper, MailyMaterialTotalEntity> implements TubeMaterialService {

    @Autowired
    IDictBizService dictBizService;

    @Autowired
    IUserService userService;

    @Autowired
    private MaterialMailyVoMapper materialMailyVoMapper;

    @Autowired
    TubeMaterialMapper tubeMaterialMapper;

    private final Double PI = 3.14;

    @Override
    public List<MailyMaterialTotalEntity> selectLists(MailyMaterialTotalEntity maliyVO , MailyMaterialTotalEntity totalEntity) {
        List<MailyMaterialTotalEntity> maliyVOList = new ArrayList<>();
        maliyVO.setSupplierCode(totalEntity.getSupplierCode());
        maliyVO.setSupplierName(totalEntity.getSupplierName());
            // 直接计算费用
            if (maliyVO.getName() != null){
                    if (maliyVO.getExternalDiameter() < 300) {
                        MaterialMaliyVO materialExternalDiameter = materialMailyVoMapper.selectExter(maliyVO.getExternalDiameter());
                        //根据外径找内径
                        MaterialMaliyVO material85 = materialMailyVoMapper.selectInner(materialExternalDiameter.getExternalDiameter(), maliyVO.getInternalDiamete());
                        // 余量查询
                        maliyVO.setDiameterAllowance(materialExternalDiameter.getExternalDiameter() - maliyVO.getExternalDiameter());// 外径余量
                        maliyVO.setApertureAllowance(maliyVO.getInternalDiamete() - material85.getInternalDiamete()); //孔径余量
                        if (material85 != null) {
                            //材料费
                            MailyMaterialTotalEntity materialList = getMaterialPrice(maliyVO, material85.getExternalDiameter(), material85.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength(),maliyVO.getSupplierCode());
                            maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); // 材料单价
                            maliyVO.setMaterialPrice(materialList.getMaterialPrice());  // 材料费
                            maliyVO.setWeight(materialList.getWeight()); // 单重
                            // 喷涂费
                            MailyMaterialTotalEntity sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
                            maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); // 喷涂单价
                            maliyVO.setSprayPrice(sprayList.getSprayPrice());// 喷涂费
                            maliyVO.setCoatingLength(maliyVO.getLength()); // 喷涂长度
                            maliyVO.setCoatingInternalDiameter(maliyVO.getInternalDiamete()); // 喷涂内径
                            maliyVO.setCoatingArea(sprayList.getCoatingArea()); //喷涂面积

                            // 加工费
                            MailyMaterialTotalEntity proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                            maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
                            maliyVO.setPrice(proscessList.getPrice()); // 切割费

                            // 产品单价   产品单价=材料费+喷涂费+加工费+切割费
                            BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                            maliyVO.setUnitPrice(productPrice);


                            MailyMaterialTotalEntity entity = new MailyMaterialTotalEntity();
                            entity.setPrLn(totalEntity.getPrLn());  // 行号
                            entity.setPrCode(totalEntity.getPrCode()); //prcode
                            entity.setSupplierCode(totalEntity.getSupplierCode()); //供应商编号
                            entity.setCoatingLength(maliyVO.getCoatingLength()); // 喷涂长度
                            entity.setCoatingInternalDiameter(maliyVO.getCoatingInternalDiameter()); // 喷涂内径
                            entity.setCoatingArea(maliyVO.getCoatingArea()); // 喷涂面积
                            entity.setDiameterAllowance(maliyVO.getDiameterAllowance()); // 外径余量
                            entity.setApertureAllowance(maliyVO.getApertureAllowance()); // 孔径余量
                            entity.setSupplierName(totalEntity.getSupplierName());  // 供应商名称
                            entity.setItemCode(totalEntity.getItemCode()); // 物料编号
                            entity.setItemDesc(totalEntity.getItemDesc());// 物料描述
                            entity.setExternalDiameter(maliyVO.getExternalDiameter());  //外径
                            entity.setInternalDiamete(maliyVO.getInternalDiamete()); //内径
                            entity.setLength(maliyVO.getLength()); // 原材料长度
                            entity.setWeight(maliyVO.getWeight()); // 原材料单重
                            entity.setTheMaterialPrice(maliyVO.getTheMaterialPrice().setScale(2, BigDecimal.ROUND_DOWN));// 材料单价
                            entity.setMaterialPrice(maliyVO.getMaterialPrice()); // 材料费
                            entity.setProcessingFee(maliyVO.getProcessingFee()); // 加工费
                            entity.setPrice(maliyVO.getPrice());// 切割费
                            entity.setCoatingPrice(maliyVO.getCoatingPrice()); // 喷涂单价
                            entity.setSprayPrice(maliyVO.getSprayPrice()); // 喷涂费
                            entity.setUnitPrice(productPrice); // 产品单价
                            entity.setPriceNum(totalEntity.getPriceNum()); //数量
                            entity.setTotalPrice(totalEntity.getUnitPrice().multiply(BigDecimal.valueOf(totalEntity.getPriceNum()))); //总价
                            maliyVOList.add(entity);
                            this.baseMapper.insert(entity);
                        } else {
                            Double exter = maliyVO.getExternalDiameter() + 10;
                            Double inner = maliyVO.getInternalDiamete() - 10;
                            // 余量查询
                            maliyVO.setDiameterAllowance(exter - maliyVO.getExternalDiameter());// 外径余量
                            maliyVO.setApertureAllowance(maliyVO.getInternalDiamete() - inner); //孔径余量
                            // 材料费
                            MailyMaterialTotalEntity materialList = getMaterialPrice(maliyVO, exter, inner, maliyVO.getTheMaterial(), maliyVO.getLength(),maliyVO.getSupplierCode());
                            maliyVO.setSupplierName(materialList.getSupplierName());
                            maliyVO.setSupplierCode(materialList.getSupplierCode());
                            maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); // 材料单价
                            maliyVO.setMaterialPrice(materialList.getMaterialPrice());  // 材料费
                            maliyVO.setWeight(materialList.getWeight()); // 单重

                            // 喷涂费
                            MailyMaterialTotalEntity sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
                            maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); // 喷涂单价
                            maliyVO.setSprayPrice(sprayList.getSprayPrice());// 喷涂费
                            maliyVO.setCoatingLength(maliyVO.getLength()); // 喷涂长度
                            maliyVO.setCoatingInternalDiameter(maliyVO.getInternalDiamete()); // 喷涂内径
                            maliyVO.setCoatingArea(sprayList.getCoatingArea()); //喷涂面积

                            // 加工费
                            MailyMaterialTotalEntity proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                            maliyVO.setProcessingFee(proscessList.getProcessingFee()); // 加工费
                            maliyVO.setPrice(proscessList.getPrice()); // 切割费

                            // 产品单价   产品单价=材料费+喷涂费+加工费+切割费
                            BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                            maliyVO.setUnitPrice(productPrice);

                            MailyMaterialTotalEntity entity = new MailyMaterialTotalEntity();
                            entity.setPrLn(totalEntity.getPrLn());  // 行号
                            entity.setPrCode(totalEntity.getPrCode()); //prcode
                            entity.setSupplierCode(totalEntity.getSupplierCode()); //供应商编号
                            entity.setCoatingLength(maliyVO.getCoatingLength()); //喷涂长度
                            entity.setCoatingInternalDiameter(maliyVO.getCoatingInternalDiameter()); //喷涂内径
                            entity.setCoatingArea(maliyVO.getCoatingArea()); //喷涂面积
                            entity.setDiameterAllowance(maliyVO.getDiameterAllowance()); // 外径余量
                            entity.setApertureAllowance(maliyVO.getApertureAllowance()); //孔径余量
                            entity.setSupplierName(totalEntity.getSupplierName());  // 供应商名称
                            entity.setItemCode(totalEntity.getItemCode()); // 物料编号
                            entity.setItemDesc(totalEntity.getItemDesc());// 物料描述
                            entity.setExternalDiameter(maliyVO.getExternalDiameter());  // 外径
                            entity.setInternalDiamete(maliyVO.getInternalDiamete()); // 内径
                            entity.setLength(maliyVO.getLength()); // 原材料长度
                            entity.setWeight(maliyVO.getWeight()); // 原材料单重
                            entity.setTheMaterialPrice(maliyVO.getTheMaterialPrice().setScale(2, BigDecimal.ROUND_DOWN));// 材料单价
                            entity.setMaterialPrice(maliyVO.getMaterialPrice()); // 材料费
                            entity.setProcessingFee(maliyVO.getProcessingFee()); // 加工费
                            entity.setPrice(maliyVO.getPrice());// 切割费
                            entity.setCoatingPrice(maliyVO.getCoatingPrice()); // 喷涂单价
                            entity.setSprayPrice(maliyVO.getSprayPrice()); // 喷涂费
                            entity.setUnitPrice(productPrice); // 产品单价
                            entity.setPriceNum(totalEntity.getPriceNum()); //数量
                            entity.setTotalPrice(totalEntity.getUnitPrice().multiply(BigDecimal.valueOf(totalEntity.getPriceNum()))); //总价
                            maliyVOList.add(entity);
                            this.baseMapper.insert(entity);
                        }
                    }

                    if (maliyVO.getExternalDiameter() > 300) {
                        Double exter = maliyVO.getExternalDiameter() + 10;
                        Double inner = maliyVO.getInternalDiamete() - 10;
                        // 余量查询
                        maliyVO.setDiameterAllowance(exter - maliyVO.getExternalDiameter());// 外径余量
                        maliyVO.setApertureAllowance(maliyVO.getInternalDiamete() - inner); //孔径余量
                        // 材料费
                        MailyMaterialTotalEntity materialList = getMaterialPrice(maliyVO, exter, inner, maliyVO.getTheMaterial(), maliyVO.getLength(),maliyVO.getSupplierCode());
                        maliyVO.setSupplierName(materialList.getSupplierName());
                        maliyVO.setSupplierCode(materialList.getSupplierCode());
                        maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); // 材料单价
                        maliyVO.setMaterialPrice(materialList.getMaterialPrice());  // 材料费
                        maliyVO.setWeight(materialList.getWeight()); // 单重
                        // 喷涂费
                        MailyMaterialTotalEntity sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
                        maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); // 喷涂单价
                        maliyVO.setSprayPrice(sprayList.getSprayPrice());// 喷涂费
                        maliyVO.setCoatingLength(maliyVO.getLength()); // 喷涂长度
                        maliyVO.setCoatingInternalDiameter(maliyVO.getInternalDiamete()); // 喷涂内径
                        maliyVO.setCoatingArea(sprayList.getCoatingArea()); //喷涂面积
                        //加工费
                        MailyMaterialTotalEntity proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                        maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
                        maliyVO.setPrice(proscessList.getPrice()); //切割费

//                        // 产品单价   产品单价=材料费+喷涂费+加工费+切割费
                        BigDecimal productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                        maliyVO.setUnitPrice(productPrice);// 单价

                        MailyMaterialTotalEntity entity = new MailyMaterialTotalEntity();
                        entity.setPrLn(totalEntity.getPrLn());  // 行号
                        entity.setPrCode(totalEntity.getPrCode()); //prcode
                        entity.setSupplierCode(totalEntity.getSupplierCode()); //供应商编号
                        entity.setCoatingLength(maliyVO.getCoatingLength()); //喷涂长度
                        entity.setCoatingInternalDiameter(maliyVO.getCoatingInternalDiameter()); //喷涂内径
                        entity.setCoatingArea(maliyVO.getCoatingArea()); //喷涂面积
                        entity.setDiameterAllowance(maliyVO.getDiameterAllowance()); // 外径余量
                        entity.setApertureAllowance(maliyVO.getApertureAllowance()); //孔径余量
                        entity.setSupplierName(totalEntity.getSupplierName());  // 供应商名称
                        entity.setItemCode(totalEntity.getItemCode()); // 物料编号
                        entity.setItemDesc(totalEntity.getItemDesc());// 物料描述
                        entity.setExternalDiameter(maliyVO.getExternalDiameter());  // 外径
                        entity.setInternalDiamete(maliyVO.getInternalDiamete()); // 内径
                        entity.setLength(maliyVO.getLength()); // 原材料长度
                        entity.setWeight(maliyVO.getWeight()); // 原材料单重
                        entity.setTheMaterialPrice(maliyVO.getTheMaterialPrice());// 材料单价
                        entity.setMaterialPrice(maliyVO.getMaterialPrice()); // 材料费
                        entity.setProcessingFee(maliyVO.getProcessingFee()); // 加工费
                        entity.setPrice(maliyVO.getPrice());// 切割费
                        entity.setCoatingPrice(maliyVO.getCoatingPrice()); // 喷涂单价
                        entity.setSprayPrice(maliyVO.getSprayPrice()); // 喷涂费
                        entity.setUnitPrice(productPrice); // 产品单价
                        entity.setPriceNum(totalEntity.getPriceNum()); //数量
                        entity.setTotalPrice(totalEntity.getUnitPrice().multiply(BigDecimal.valueOf(totalEntity.getPriceNum()))); //总价
                        maliyVOList.add(entity);
                        this.baseMapper.insert(entity);
                    }
            }else {
                throw  new RuntimeException("此物料不是管状物料");
            }
        return maliyVOList;
    }


    @Override
    public void export(MailyMaterialTotalEntity materialMaliyVO, Query query, HttpServletResponse response) {
        List<MailyMaterialTotalEntity> list = list(getQueryWrapper(materialMaliyVO));
        if (list.size() == 0){
            throw  new RuntimeException("暂无数据");
        }
        ArrayList<MailyMaterialTotalEntity> excel = new ArrayList<>();
        list.forEach( entity -> {
            MailyMaterialTotalEntity dto = BeanUtil.copy(entity, MailyMaterialTotalEntity.class);
            excel.add(dto);
        });
        ExcelUtils.defaultExport(excel,MailyMaterialTotalEntity.class,"管料自动下单报表"+ DateUtil.formatDate(new Date()),response);
    }

    @Override
    public QueryWrapper<MailyMaterialTotalEntity> getQueryWrapper(MailyMaterialTotalEntity mailyMaterialTotalEntity) {
        QueryWrapper<MailyMaterialTotalEntity> queryWrapper = Condition.getQueryWrapper(mailyMaterialTotalEntity);
        return  queryWrapper;
    }


    /**
     * 材料费 外径>300
     * @param maliyVO
     * @param externalDiameter
     * @param internalDiamete
     * @param theMaterial
     * @param length
     * @return
     */
    private MailyMaterialTotalEntity getMaterialPrice(MailyMaterialTotalEntity maliyVO, Double externalDiameter, Double internalDiamete, String theMaterial, Double length,String supplierCode) {
        String range = "";
        // 查找材质单价
        Double itemprice = materialMailyVoMapper.selectTheMaterialPrice(theMaterial, externalDiameter.toString(),supplierCode);
        if (itemprice == null){
            maliyVO.setTheMaterialPrice(null); // 材料单价
            maliyVO.setMaterialPrice(null);
            return maliyVO;
        }
        maliyVO.setTheMaterialPrice(BigDecimal.valueOf(itemprice));

        //计算单重
        BigDecimal price = new BigDecimal(externalDiameter).multiply(BigDecimal.valueOf(externalDiameter)).subtract(BigDecimal.valueOf(internalDiamete).multiply(BigDecimal.valueOf(internalDiamete)));
        BigDecimal   weight =   new BigDecimal(String.valueOf(price)).multiply(BigDecimal.valueOf(PI)).divide(BigDecimal.valueOf(4)).multiply(BigDecimal.valueOf(length+12)).multiply(BigDecimal.valueOf(8)).divide(BigDecimal.valueOf(1000000));
        //计算材料费
        BigDecimal MaterialPrice = new BigDecimal(String.valueOf(weight)).multiply(maliyVO.getTheMaterialPrice()).multiply(BigDecimal.valueOf(1.2));
        maliyVO.setWeight(weight.setScale(2,BigDecimal.ROUND_DOWN));  // 单重
        maliyVO.setMaterialPrice(MaterialPrice.setScale(2,BigDecimal.ROUND_DOWN)); // 材料费
        maliyVO.setTheMaterialPrice(maliyVO.getTheMaterialPrice()); // 材料单价
        return maliyVO;
    }

    /**
     * 加工费
     *
     * @param maliyVO
     * @param externalDiameter
     * @param internalDiamete
     * @param length
     * @return
     */
    private MailyMaterialTotalEntity getProscessList(MailyMaterialTotalEntity maliyVO, Double externalDiameter, Double internalDiamete, Double length,String supplierCode) {
        MaterialMaliyVO vo = materialMailyVoMapper.selectProcessPricesMax(externalDiameter,internalDiamete,length,supplierCode);
        // 长度加余量
        length += 12;
        // 切割费
        if (length < 200) {
            //有切割费
            if (externalDiameter <= 80) {
                maliyVO.setPrice(BigDecimal.valueOf(6));
            }
            if (externalDiameter > 80 && externalDiameter <= 150) {
                maliyVO.setPrice(BigDecimal.valueOf(7.2));
            }
            if (externalDiameter > 150 && externalDiameter <= 200) {
                maliyVO.setPrice(BigDecimal.valueOf(15.6));
            }
            if (externalDiameter > 200) {
                maliyVO.setPrice(BigDecimal.valueOf(18)); // 切割费
            }
        }else {
            maliyVO.setPrice(new BigDecimal("0"));
        }
        if (vo == null){
            maliyVO.getPrice();
            maliyVO.setProcessingFee(new BigDecimal("0"));  // 加工费
            return maliyVO;
        }

        maliyVO.setProcessingFee(vo.getProcessingFee().setScale(2,BigDecimal.ROUND_DOWN)); // 加工费
        return maliyVO;
    }

    /**
     * 计算喷涂费
     * @param maliyVO
     * @param length
     * @param internalDiamete
     * @param coating
     * @param theMaterial
     * @return
     */
    private MailyMaterialTotalEntity getSprayList(MailyMaterialTotalEntity maliyVO, Double length, Double internalDiamete, String coating, String theMaterial,String supplierCode) {
        //计算喷涂费
        Double price = materialMailyVoMapper.selectBySprayPrice(coating, supplierCode, theMaterial);
        if (price == null){
            maliyVO.setSprayPrice(new BigDecimal("0"));  //喷涂费
            maliyVO.setCoatingPrice(new BigDecimal("0"));
            return maliyVO;
        }
        maliyVO.setCoatingPrice(BigDecimal.valueOf(price));  //喷涂单价
        //计算喷涂面积
        BigDecimal coatingArea = new BigDecimal(internalDiamete).multiply(BigDecimal.valueOf(PI)).multiply(BigDecimal.valueOf(length)).divide(BigDecimal.valueOf(100));
        maliyVO.setCoatingArea(coatingArea); //喷涂面积
        //计算喷涂费
        BigDecimal sprayPrice = new BigDecimal(internalDiamete).multiply(BigDecimal.valueOf(PI)).multiply(BigDecimal.valueOf(length)).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(price));
        maliyVO.setSprayPrice(sprayPrice.setScale(2,BigDecimal.ROUND_DOWN));  // 喷涂费
        return maliyVO;
    }



}
