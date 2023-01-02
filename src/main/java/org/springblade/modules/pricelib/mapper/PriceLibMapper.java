package org.springblade.modules.pricelib.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springblade.modules.pricelib.entity.PriceLibEntity;
import org.springblade.modules.pricelib.vo.PriceLibVO;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface PriceLibMapper extends BaseMapper<PriceLibEntity> {

    boolean deleteByItemCode(String itemCode);

    List<PriceLibEntity> getByItemCodes(@Param("itemCodes") List<String> itemCodes);

    @Update("update atw_item set purch_attr = #{lib} where code = #{itemCode}")
    boolean updatePricelibByIemCdoe(@Param("itemCode")String itemCode,@Param("lib")String lib);


    @Select("select * from atw_price_lib  where item_code = #{itemCode} and now()<=expiration_date")
    PriceLibEntity selectByItemCode(@Param("itemCode") String itemCode);


}
