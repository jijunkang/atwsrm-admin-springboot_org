package org.springblade.modules.po.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.po.dto.CraftCtrlNodeDTO;
import org.springblade.modules.po.entity.CraftCtrlNodeEntity;
import org.springblade.modules.po.vo.CraftCtrlNodeVO;

import java.util.List;

/**
 * @author libin
 *
 * @date 11:29 2020/7/22
 **/
public interface CraftCtrlNodeMapper extends BaseMapper<CraftCtrlNodeEntity> {

	/**
	 * 获取字典表对应中文
	 *
	 * @param code    字典编号
	 * @param dictKey 字典序号
	 * @return
	 */
	String getValue(String code, String dictKey);

	/**
	 * 获取字典表
	 *
	 * @param code 字典编号
	 * @return
	 */
	List<CraftCtrlNodeEntity> getList(String code);

	/**
	 * 获取树形节点
	 *
	 * @return
	 */
	List<CraftCtrlNodeVO> tree();

	String getMaxCode();

	List<CraftCtrlNodeDTO> getdtos(@Param("craftCtrlNodeEntity") CraftCtrlNodeEntity craftCtrlNodeEntity);
}
