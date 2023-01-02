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
import org.springblade.modules.helpcenter.entity.ClassEntity;
import org.springblade.modules.helpcenter.service.IClassService;
import org.springblade.modules.helpcenter.vo.ClassDetailVO;
import org.springblade.modules.helpcenter.vo.ClassVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * 帮助分类 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-support/class")
@Api(value = "帮助分类", tags = "帮助分类")
public class ClassController extends BladeController {

	private IClassService classService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "")
	public R<ClassDetailVO> detail(Long id) {
		ClassDetailVO detail = classService.getDetail(id);
		return R.data(detail);
	}

	/**
	 * 分页 代码自定义代号
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入class")
	public R<IPage<ClassEntity>> list(ClassEntity clazz, Query query) {
		IPage<ClassEntity> pages = classService.page(Condition.getPage(query), Condition.getQueryWrapper(clazz));
		return R.data(pages);
	}

	/**
	 * 新增 代码自定义代号
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入class")
	public R save(@Valid @RequestBody ClassEntity clazz) {
		return R.status(classService.save(clazz));
	}

	/**
	 * 修改 代码自定义代号
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入class")
	public R update(@Valid @RequestBody ClassEntity clazz) {
		return R.status(classService.updateById(clazz));
	}

	/**
	 * 新增或修改 代码自定义代号
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入class")
	public R submit(@Valid @RequestBody ClassEntity clazz) {
		return R.status(classService.saveOrUpdate(clazz));
	}


	/**
	 * 删除 代码自定义代号
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入id")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam Long id) {
		return R.status(classService.delete(id));
	}

	/**
	 * 获取字典树形结构
	 *
	 * @return
	 */
	@GetMapping("/tree")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "树形结构", notes = "树形结构")
	public R<List<ClassVO>> tree(@RequestParam @ApiParam(value = "code", required = true) String code) {
		List<ClassVO> tree = classService.tree(code);
		return R.data(tree);
	}


}
