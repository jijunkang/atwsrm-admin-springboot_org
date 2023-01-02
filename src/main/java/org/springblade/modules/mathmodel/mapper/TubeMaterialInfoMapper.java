package org.springblade.modules.mathmodel.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.modules.mathmodel.entity.MailyMaterialTotalEntity;
import org.springblade.modules.mathmodel.entity.TubeMaterialInfoEntity;

import java.util.List;

/**
 * Author: 昕月
 * Date：2022/6/2 9:32
 * Desc:
 */
@Mapper
public interface TubeMaterialInfoMapper extends BaseMapper<TubeMaterialInfoEntity> {

    @Select("  select * from atw_maily_spraying_price where   coating  =  #{coating} and FIND_IN_SET(#{theMaterial},the_material)")
    List<TubeMaterialInfoEntity> selectSupName(TubeMaterialInfoEntity entity);

    TubeMaterialInfoEntity selectExternal(@Param("externalDiameter") Double externalDiameter);

    TubeMaterialInfoEntity selectInner(@Param("externalDiameter")Double externalDiameter, @Param("internalDiamete")Double internalDiamete);

    @SqlParser(filter = true)
    Double selectTheMaterialPrice(@Param("theMaterial")String theMaterial,@Param("range") String range,@Param("supplierCode") String supplierCode);

    Double selectBySprayPrice(@Param("coating")String coating, @Param("theMaterial")String theMaterial,@Param("supplierCode") String supplierCode);

    TubeMaterialInfoEntity selectProcessPrice(@Param("externalDiameter")Double externalDiameter, @Param("internalDiamete")Double internalDiamete,@Param("length") Double length,@Param("supplierCode") String supplierCode);

    TubeMaterialInfoEntity selectByInfo(@Param("itemCode") String itemCode);

    void deleteByItemCode(@Param("itemCode") String itemCode);
}
