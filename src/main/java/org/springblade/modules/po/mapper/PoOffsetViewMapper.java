package org.springblade.modules.po.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.po.entity.PoOffsetViewEntity;
import org.springblade.modules.po.vo.PoOffsetViewVO;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface PoOffsetViewMapper extends BaseMapper<PoOffsetViewEntity> {

    IPage<PoOffsetViewEntity> getPoOffetViewPage(IPage<PoOffsetViewEntity> page, PoOffsetViewVO poOffsetViewVO);

    List<PoOffsetViewEntity> getPoOffetViewPage(@Param("poOffsetViewVO") PoOffsetViewVO poOffsetViewVO);
}
