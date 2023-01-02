package org.springblade.modules.helpcenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.api.client.util.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.helpcenter.dto.ContentElasticDTO;
import org.springblade.modules.helpcenter.dto.ContentQueryDTO;
import org.springblade.modules.helpcenter.entity.ClassEntity;
import org.springblade.modules.helpcenter.entity.ContentEntity;
import org.springblade.modules.helpcenter.mapper.ContentMapper;
import org.springblade.modules.helpcenter.service.IClassService;
import org.springblade.modules.helpcenter.service.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 帮助内容 服务实现类
 *
 * @author Will
 */
@Slf4j
@Service
public class ContentServiceImpl extends BaseServiceImpl<ContentMapper, ContentEntity> implements IContentService {


    @Autowired
    IClassService classService;

    public QueryWrapper<ContentEntity> getQueryWrapper(ContentEntity content) {
        QueryWrapper<ContentEntity> queryWrapper = Condition.getQueryWrapper(new ContentEntity());
        queryWrapper.eq(content.getClassId() != null, "class_id", content.getClassId());
        queryWrapper.like(StringUtil.isNotBlank(content.getTitle()), "title", content.getTitle());
        return queryWrapper;
    }


    @Override
    public boolean delete(@NotEmpty List<Long> ids) {
        return super.deleteLogic(ids);
    }

    @Override
    public boolean isExistContent(Long classId) {
        Integer contentCount = contentCount(classId);
        return contentCount > 0;
    }


    @Override
    public IPage<ContentEntity> getPage(Query query, ContentEntity content) {
        return page(Condition.getPage(query), getQueryWrapper(content));
    }

    /**
     * 文章数量
     *
     * @param classId
     * @return
     */
    private Integer contentCount(Long classId) {
        LambdaQueryWrapper<ContentEntity> wrapper = Wrappers.<ContentEntity>lambdaQuery().eq(ContentEntity::getClassId, classId);
        return count(wrapper);
    }


    @Override
    public IPage<ContentElasticDTO> search(Query query, ContentQueryDTO queryDTO) {
        QueryWrapper<ContentEntity> queryWrapper = Condition.getQueryWrapper(new ContentEntity());
        queryWrapper.like(StringUtil.isNotBlank(queryDTO.getKeyword()), "title", queryDTO.getKeyword())
        .or().like(StringUtil.isNotBlank(queryDTO.getKeyword()), "content", queryDTO.getKeyword());
        IPage<ContentEntity> page = page(Condition.getPage(query), queryWrapper);
        IPage<ContentElasticDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ContentElasticDTO> dtos = Lists.newArrayList();
        for (ContentEntity contentEntity : page.getRecords()) {
            ContentElasticDTO contentElasticDTO = BeanUtil.copy(contentEntity, ContentElasticDTO.class);
            ClassEntity classEntity = classService.getById(contentEntity.getClassId());
            if (StringUtil.isEmpty(classEntity)) {
                throw new RuntimeException("该分类ID不存在：" + contentEntity.getClassId());
            }
            contentElasticDTO.setClassName(classEntity.getClassName());
            contentElasticDTO.setCode(classEntity.getCode());
            dtos.add(contentElasticDTO);
        }
        dtoPage.setRecords(dtos);
        return dtoPage;
    }
}
