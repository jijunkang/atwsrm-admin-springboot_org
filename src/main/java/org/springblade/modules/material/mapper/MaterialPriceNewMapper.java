package org.springblade.modules.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springblade.modules.material.entity.MaterialPriceEntity;
import org.springblade.modules.material.entity.MaterialPriceNewEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface MaterialPriceNewMapper extends BaseMapper<MaterialPriceNewEntity> {

    List<MaterialPriceNewEntity> getlist(@Param("req")MaterialPriceNewEntity req);

    @Delete("DELETE FROM atw_material_price_new where id=#{id}")
    boolean deleteById(@Param("id")Long id);

    @Update("update  atw_material_price_new set  status=40  , update_time = (now()) where status=10 and material=#{material} and technic=#{technic} ")
    boolean passMaterialPrice(@Param("material")String material,@Param("technic")String technic);

    List<MaterialPriceNewEntity> getlistnohistory(@Param("req")MaterialPriceNewEntity req);

}
