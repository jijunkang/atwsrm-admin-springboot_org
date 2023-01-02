package org.springblade.modules.helpcenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.logging.log4j.util.Strings;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.helpcenter.entity.ClassEntity;
import org.springblade.modules.helpcenter.mapper.ClassMapper;
import org.springblade.modules.helpcenter.service.IClassService;
import org.springblade.modules.helpcenter.service.IContentService;
import org.springblade.modules.helpcenter.vo.ClassDetailVO;
import org.springblade.modules.helpcenter.vo.ClassVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 帮助分类 服务实现类
 *
 * @author Will
 */
@Service
public class ClassServiceImpl extends BaseServiceImpl<ClassMapper, ClassEntity> implements IClassService {

	@Autowired
	@Lazy
	private IContentService contentService;

	@Override
	public List<ClassVO> tree(String code) {
		return ForestNodeMerger.merge(this.baseMapper.tree(code));
	}

	@Override
	public Boolean delete(Long id) {
		ClassEntity classEntity = getById(id);
		if (Objects.isNull(classEntity)) {
			throw new RuntimeException("分类不存在");
		}
		String className = classEntity.getClassName();
		// 检查是否存在下级
		if (isExistChild(id)) {
            throw new RuntimeException("请先删除子级分类");
		}
		// 检查是否存在文章
		if (contentService.isExistContent(id)) {
            throw new RuntimeException("请先删除[" + className + "]分类下的文章");
		}
		return deleteLogic(Func.toLongList(id.toString()));
	}

	@Override
	public ClassDetailVO getDetail(Long id) {
		ClassEntity classEntity = getById(id);
		if (Objects.isNull(classEntity)) {
            throw new RuntimeException("分类不存在");
		}
		ClassDetailVO classDetailVO = BeanUtil.copyProperties(classEntity, ClassDetailVO.class);

		ClassEntity parentClass = getById(classDetailVO.getParentId());
		String parentClassName = "";
		if (!Objects.isNull(parentClass)) {
			parentClassName = parentClass.getClassName();
		}
		classDetailVO.setParentClassName(parentClassName);
		return classDetailVO;
	}

	@Override
	public String getClassName(Long classId) {
		ClassEntity classEntity = getById(classId);
		if (Objects.isNull(classEntity)) {
			return Strings.EMPTY;
		}
		return classEntity.getClassName();
	}

	/**
	 * 获取子级数量
	 *
	 * @param parentId
	 * @return
	 */
	private Integer getChildCount(Long parentId) {
		LambdaQueryWrapper<ClassEntity> wrapper = Wrappers.<ClassEntity>lambdaQuery().eq(ClassEntity::getParentId, parentId);
		return count(wrapper);
	}

	/**
	 * 是否包含子级
	 *
	 * @param parentId
	 * @return
	 */
	private Boolean isExistChild(Long parentId) {
		Integer childCount = getChildCount(parentId);
		return childCount > 0;
	}

}
