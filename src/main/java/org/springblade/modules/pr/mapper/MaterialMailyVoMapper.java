package org.springblade.modules.pr.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.modules.material.entity.MaterialPriceEntity;
import org.springblade.modules.mathmodel.entity.MailyMaterialTotalEntity;
import org.springblade.modules.pr.entity.ItemInfoMaterialPriceMaliy;
import org.springblade.modules.pr.vo.MaterialMaliyVO;

import java.util.List;

/**
 * Author: 昕月
 * Date：2022/5/14 14:44
 * Desc:
 */
@Mapper
public interface MaterialMailyVoMapper extends BaseMapper<MaterialMailyVoMapper> {

    /* 精准查找 */
    MaterialMaliyVO selectItemName(@Param("externalDiameter") int externalDiameter, @Param("internalDiamete") int internalDiamete);

    Double selectTheMaterial(@Param("theMaterial") String theMaterial,@Param("range") String range);

    MaterialMaliyVO selectProcessPrice(@Param("externalDiameter") int externalDiameter, @Param("internalDiamete") int internalDiamete,@Param("length") int length);

    MaterialMaliyVO selectSprayingPrice(@Param("coating") String coating);

    MaterialMaliyVO selectLengthProcessPrice(@Param("length") int length);


    /* 外径+8  内径-5 查找*/
//    MaterialMaliyVO selectItemNameMin(@Param("externalDiameter") int externalDiameter,@Param("internalDiamete") int internalDiamete);

    Double selectMinPrice(@Param("theMaterial") String theMaterial,@Param("range") String range);

    MaterialMaliyVO selectSprayingPriceMin(@Param("coating")String coating);
    /**/
    MaterialMaliyVO selectItemNameMax(@Param("externalDiameter") int externalDiameter, @Param("internalDiamete") int internalDiamete);


    Double selectMaxPrice(@Param("theMaterial") String theMaterial,@Param("range") String range);

    MaterialMaliyVO selectSprayingMaxPrice(@Param("coating") String coating,@Param("theMaterial") String theMaterial);

    MaterialMaliyVO selectProcessMaxPrice(@Param("externalDiameter") int externalDiameter, @Param("internalDiamete") int internalDiamete,@Param("length") int length);

    MaterialMaliyVO selectMaxProcessPrice(@Param("externalDiameter") int externalDiameter, @Param("internalDiamete") int internalDiamete,@Param("length") int length);

    /**
     * 查找多家供应商
     * @param maliyVO
     * @return
     */
    List<MaterialMaliyVO> selectSupName(MaterialMaliyVO maliyVO);

    List<MaterialMaliyVO> selectMaterilaList(MaterialMaliyVO maliyVO);

    @SqlParser(filter = true)
    Double selectTheMaterialPrice(@Param("theMaterial") String theMaterial, @Param("range") String range, @Param("supplierCode") String supplierCode);

    @SqlParser(filter = true)
    ItemInfoMaterialPriceMaliy selectMaterialPriceVO(@Param("theMaterial") String theMaterial, @Param("range") String range, @Param("supplierCode") String supplierCode);

    MaterialMaliyVO selectProcessPrices(@Param("externalDiameter") Double externalDiameter, @Param("internalDiamete") Double internalDiamete, @Param("length") Double length, @Param("supplierCode") String supplierCode);

    Double selectBySprayPrice(@Param("coating") String coating,@Param("supplierCode") String supplierCode, @Param("theMaterial") String theMaterial);

    MaterialMaliyVO selectExter(@Param("externalDiameter") Double externalDiameter);

    MaterialMaliyVO selectInner(@Param("externalDiameter")Double externalDiameter,@Param("internalDiamete") Double internalDiamete);

    MaterialMaliyVO selectProcessPricesMax(@Param("externalDiameter") Double externalDiameter, @Param("internalDiamete") Double internalDiamete,@Param("length") Double length, @Param("supplierCode") String supplierCode);

    @Select("select * from atw_maily_res where sup_code=#{supCode}  and  FIND_IN_SET(#{theMaterial},material_type) ")
    MaterialMaliyVO selectRes(@Param("supCode") String supCode, @Param("theMaterial") String theMaterial);

}
