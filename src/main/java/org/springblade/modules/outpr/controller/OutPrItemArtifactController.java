package org.springblade.modules.outpr.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.modules.outpr.entity.OutPrItemArtifactEntity;
import org.springblade.modules.outpr.service.IOutPrItemArtifactService;
import org.springblade.modules.outpr.vo.OutPrItemArtifactVO;
import org.springblade.modules.system.service.IParamService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * 控制器
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-outpr/artifact")
@Api(value = "", tags = "")
public
class OutPrItemArtifactController extends BladeController{

    private IOutPrItemArtifactService artifactService;

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入OutPrItemArtifactEntity")
    public
    R<IPage<OutPrItemArtifactVO>> page(OutPrItemArtifactVO vo, Query query){
        return R.data(artifactService.getVoPage(vo, query));
    }

    /**
     * 委外转人工-中台 统计数量
     *
     * @return  R
     */
    @GetMapping("/centercount")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "")
    public R<List<Map<String, Object>>> centerCount(){
        return R.data(artifactService.centerCount());
    }

    /**
     * 流标转人工-指定供应商
     */
    @PostMapping("/assignsup")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入outpritem")
    public
    R assignSup(@Valid @RequestBody List<OutPrItemArtifactEntity> entityList){
        for(OutPrItemArtifactEntity entity : entityList){
            artifactService.assignSup(entity);
        }
        return R.status(true);
    }

    /**
     * （审核）
     */
    @PostMapping("/check")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = " 审核", notes = "CheckDTO")
    public
    R check(@Valid @RequestBody CheckDTO checkDto){
        return R.status(artifactService.check(checkDto));
    }


    @PostMapping("/checkbatch")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "批量审核", notes = "CheckDTO")
    public
    R check(@Valid @RequestBody List<CheckDTO> checkDtos){
        return R.status(artifactService.check(checkDtos));
    }

    //	/**
    //	 * 新增 代码自定义代号
    //	 */
    //	@PostMapping("/save")
    //	@ApiOperationSupport(order = 4)
    //	@ApiOperation(value = "新增", notes = "传入outpritem")
    //	public R save(@Valid @RequestBody OutPrItemEntity outpritem) {
    //		return R.status(outpritemService.save(outpritem));
    //	}
    //
    //	/**
    //	 * 修改 代码自定义代号
    //	 */
    //	@PostMapping("/update")
    //	@ApiOperationSupport(order = 5)
    //	@ApiOperation(value = "修改", notes = "传入outpritem")
    //	public R update(@Valid @RequestBody OutPrItemEntity outpritem) {
    //		return R.status(outpritemService.updateById(outpritem));
    //	}
    //
    //	/**
    //	 * 新增或修改 代码自定义代号
    //	 */
    //	@PostMapping("/submit")
    //	@ApiOperationSupport(order = 6)
    //	@ApiOperation(value = "新增或修改", notes = "传入outpritem")
    //	public R submit(@Valid @RequestBody OutPrItemEntity outpritem) {
    //		return R.status(outpritemService.saveOrUpdate(outpritem));
    //	}
    //
    //
    //	/**
    //	 * 删除 代码自定义代号
    //	 */
    //	@PostMapping("/remove")
    //	@ApiOperationSupport(order = 7)
    //	@ApiOperation(value = "逻辑删除", notes = "传入ids")
    //	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
    //		return R.status(outpritemService.deleteLogic(Func.toLongList(ids)));
    //	}

}
