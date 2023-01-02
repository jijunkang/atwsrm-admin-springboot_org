package org.springblade.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.modules.system.entity.AuditRecordEntity;
import org.springblade.modules.system.service.IAuditRecordService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * 控制器
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-system/auditrecord")
@Api(value = "", tags = "")
public
class AuditRecordController extends BladeController{

    private IAuditRecordService auditRecordService;

//    /**
//     * 详情
//     */
//    @GetMapping("/detail")
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "详情", notes = "传入auditRecord")
//    public
//    R<AuditRecordEntity> detail(AuditRecordEntity auditRecord){
//        AuditRecordEntity detail = auditRecordService.getOne(Condition.getQueryWrapper(auditRecord));
//        return R.data(detail);
//    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入auditRecord")
    public
    R<IPage<AuditRecordEntity>> page(AuditRecordEntity auditRecord, Query query){
        IPage<AuditRecordEntity> pages = auditRecordService
                .page(Condition.getPage(query), Condition.getQueryWrapper(auditRecord));
        return R.data(pages);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入auditRecord")
    public
    R save(@Valid @RequestBody AuditRecordEntity auditRecord){
        return R.status(auditRecordService.save(auditRecord));
    }
//
//    /**
//     * 修改 代码自定义代号
//     */
//    @PostMapping("/update")
//    @ApiOperationSupport(order = 5)
//    @ApiOperation(value = "修改", notes = "传入auditRecord")
//    public
//    R update(@Valid @RequestBody AuditRecordEntity auditRecord){
//        return R.status(auditRecordService.updateById(auditRecord));
//    }
//
//    /**
//     * 新增或修改 代码自定义代号
//     */
//    @PostMapping("/submit")
//    @ApiOperationSupport(order = 6)
//    @ApiOperation(value = "新增或修改", notes = "传入auditRecord")
//    public
//    R submit(@Valid @RequestBody AuditRecordEntity auditRecord){
//        return R.status(auditRecordService.saveOrUpdate(auditRecord));
//    }
//
//
//    /**
//     * 删除 代码自定义代号
//     */
//    @PostMapping("/remove")
//    @ApiOperationSupport(order = 7)
//    @ApiOperation(value = "逻辑删除", notes = "传入ids")
//    public
//    R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids){
//        return R.status(auditRecordService.deleteLogic(Func.toLongList(ids)));
//    }


    /**
     * 查看审核记录
     *
     * @param auditRecord AuditRecordEntity
     * @return R
     */
    @GetMapping("/query")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查看审核记录", notes = "传入auditRecord")
    public R<List<AuditRecordEntity>> detail(AuditRecordEntity auditRecord) {
        QueryWrapper<AuditRecordEntity> queryWrapper = Condition.getQueryWrapper(new AuditRecordEntity());
        queryWrapper.eq("obj_id", auditRecord.getObjId());
        List<AuditRecordEntity> auditRecordEntities = auditRecordService.list(queryWrapper);
        return R.data(auditRecordEntities);
    }
}
