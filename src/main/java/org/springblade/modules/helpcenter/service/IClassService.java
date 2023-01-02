package org.springblade.modules.helpcenter.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.helpcenter.entity.ClassEntity;
import org.springblade.modules.helpcenter.vo.ClassDetailVO;
import org.springblade.modules.helpcenter.vo.ClassVO;

import java.util.List;

/**
 * 帮助分类 服务类
 *
 * @author Will
 */
public interface IClassService extends BaseService<ClassEntity> {

	/**
	 * 树形列表
	 *
	 * @return
	 * @param code
	 */
	List<ClassVO> tree(String code);

	/**
	 * 删除分类
	 *
	 * @param id
	 * @return
	 */
	Boolean delete(Long id);

	/**
	 * 详情
	 * @param id
	 * @return
	 */
	ClassDetailVO getDetail(Long id);

	/**
	 * 查询分类名称
	 * @param classId
	 * @return
	 */
    String getClassName(Long classId);
}
