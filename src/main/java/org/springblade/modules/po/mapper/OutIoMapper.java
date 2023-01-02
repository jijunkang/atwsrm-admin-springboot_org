package org.springblade.modules.po.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.outpr.dto.OutPrItemDTO;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.vo.OutPrItemVO;
import org.springblade.modules.outpr.vo.OutPrVO;
import org.springblade.modules.po.dto.IoDTO;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.entity.OutIoEntity;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.pr.dto.U9PrDTO;
import org.springblade.modules.priceframe.dto.CenterPriceFrame;

import java.math.BigDecimal;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface OutIoMapper extends BaseMapper<OutIoEntity> {

    IPage<IoDTO> selectToCheckPage(IPage page, IoEntity io);

    int selectToCheckCount(@Param("io") IoEntity io);

    IPage<IoDTO> selectToConfirmPage(IPage<IoEntity> page, IoDTO ioDto);

    Integer countToConfirm(@Param("ioDto") IoDTO ioDto);

    IPage<CenterPriceFrame> getByStatus(IPage<CenterPriceFrame> page, Integer status);


    OutIoEntity getWinBidIo(@Param("id") String id);

    int getStatusCount();

    BigDecimal getMaterialCostByItemCode(@Param("itemCode") String itemCode);

    BigDecimal getLaborCostByItemCode(@Param("itemCode") String itemCode);

    OutIoEntity getInfoByPrId(@Param("dto") OutPrItemEntity dto);

}
