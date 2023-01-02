package org.springblade.modules.priceframe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.lettuce.core.dynamic.annotation.Param;
import org.springblade.modules.pr.dto.U9PrDTO;
import org.springblade.modules.pr.entity.U9PrEntity;
import org.springblade.modules.priceframe.dto.CenterPriceFrame;
import org.springblade.modules.priceframe.entity.PriceFrameEntity;
import org.springblade.modules.priceframe.vo.PriceFrameVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface PriceFrameMapper extends BaseMapper<PriceFrameEntity> {

    IPage<CenterPriceFrame> center(IPage<CenterPriceFrame> page, @Param("priceFrameEntity") PriceFrameEntity priceFrameEntity);

    Integer countCenter(Integer status);

    List<PriceFrameEntity> getByPr(String itemCode, BigDecimal priceNum);

    PriceFrameEntity saveCheck(String itemCode, String supCode, Date effectiveDate, BigDecimal limitMin);

    BigDecimal getLimitMin(String itemCode, String supCode);

    List<PriceFrameVO> getGroupByCount(Long userId);
}
