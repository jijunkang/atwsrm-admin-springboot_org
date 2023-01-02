package org.springblade.modules.helpcenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.helpcenter.dto.ContentElasticDTO;
import org.springblade.modules.helpcenter.dto.ContentQueryDTO;
import org.springblade.modules.helpcenter.entity.ContentEntity;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 帮助内容 服务类
 *
 * @author Will
 */
public interface IContentService extends BaseService<ContentEntity> {

	/**
	 * @param ids
	 * @return
	 */
	boolean delete(@NotEmpty List<Long> ids);

	/**
	 * 检查分类下是否存在内容
     *
	 * @param classId
	 * @return
	 */
	boolean isExistContent(Long classId);

    /**
     * 列表
     *
     * @param query Query
     * @param content ContentEntity
     * @return
     */
    IPage<ContentEntity> getPage(Query query, ContentEntity content);

    /**
     * 搜索
     * @param query Query
     * @param queryDTO ContentQueryDTO
     * @return IPage
     */
    IPage<ContentElasticDTO> search(Query query, ContentQueryDTO queryDTO);
}
