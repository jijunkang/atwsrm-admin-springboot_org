package org.springblade.modules.helpcenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.helpcenter.dto.ContentElasticDTO;
import org.springblade.modules.helpcenter.dto.ContentQueryDTO;
import org.springblade.modules.helpcenter.entity.ContentEntity;
import org.springblade.modules.helpcenter.service.IContentService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * 帮助内容 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-support/content")
@Api(value = "帮助内容", tags = "帮助内容")
public class ContentController extends BladeController {

	private IContentService contentService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入content")
	public R<ContentEntity> detail(ContentEntity content) {
		ContentEntity detail = contentService.getOne(Condition.getQueryWrapper(content));
		return R.data(detail);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入content")
	public R<IPage<ContentEntity>> list(ContentEntity content, Query query) {
		IPage<ContentEntity> pages = contentService.getPage(query, content);
		return R.data(pages);
	}

	/**
	 * 新增或修改 代码自定义代号
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入content")
	public R submit(@Valid @RequestBody ContentEntity content) {
		return R.status(contentService.saveOrUpdate(content));
	}

	/**
	 * 删除 代码自定义代号
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(contentService.delete(Func.toLongList(ids)));
	}

    /**
     * 搜索
     */
    @GetMapping("/search")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "搜索", notes = "")
    public R<IPage<ContentElasticDTO>> search(ContentQueryDTO queryDTO, Query query) {
        IPage<ContentElasticDTO> pages = contentService.search(query,queryDTO);
        return R.data(pages);
    }

}
