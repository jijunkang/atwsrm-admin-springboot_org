package org.springblade.modules.helpcenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.helpcenter.entity.ClassEntity;
import org.springblade.modules.helpcenter.vo.ClassVO;

import java.util.List;

/**
 * 帮助分类 Mapper 接口
 *
 * @author Will
 */
public interface ClassMapper extends BaseMapper<ClassEntity> {

	/**
	 * 获取树形结构
	 * @return
	 * @param code
	 */
    List<ClassVO> tree(@Param("code") String code);
}
