package org.springblade.modules.po.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.modules.po.dto.IoDTO;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.priceframe.dto.CenterPriceFrame;

import java.math.BigDecimal;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface IoMapper extends BaseMapper<IoEntity> {

    IPage<IoDTO> selectToCheckPage(IPage page, IoEntity io);

    int selectToCheckCount(@Param("io") IoEntity io);

    IPage<IoDTO> selectToConfirmPage(IPage<IoEntity> page,IoDTO ioDto);

    Integer countToConfirm(@Param("ioDto") IoDTO ioDto);

    IPage<CenterPriceFrame> getByStatus(IPage<CenterPriceFrame> page, Integer status);

    @Select("select count(*) from atw_po_item where pr_id = #{prId} and is_deleted = 0")
    Integer poItemIsExisted(@Param("prId")Long prId);

    IoEntity getWinBidIo(@Param("id") String id);

    int getStatusCount();

    BigDecimal getMaterialCostByItemCode(@Param("itemCode") String itemCode);

    BigDecimal getLaborCostByItemCode(@Param("itemCode") String itemCode);
}
