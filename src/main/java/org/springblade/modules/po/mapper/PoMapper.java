package org.springblade.modules.po.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.po.dto.PoDTO;
import org.springblade.modules.po.dto.PoExcel;
import org.springblade.modules.po.entity.PoEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购订单表头 Mapper 接口
 *
 * @author Will
 */
public interface PoMapper extends BaseMapper<PoEntity> {

    IPage<PoEntity> getPrePoPage(IPage<PoEntity> page, PoDTO poDTO);

    BigDecimal getSumCloseAmount(String poCode);

    IPage<PoEntity> getList(IPage<PoEntity> page, @Param("poDTO") PoDTO poDTO);

    IPage<PoEntity> getVmiList(IPage<PoEntity> page, @Param("poDTO") PoDTO poDTO);

    List<PoEntity> getPoList(@Param("poDTO") PoDTO poDTO);

    PoEntity getPoInfoByPoCode(@Param("poCode") String poCode);

}
