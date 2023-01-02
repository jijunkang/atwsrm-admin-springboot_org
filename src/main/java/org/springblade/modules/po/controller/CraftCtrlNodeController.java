package org.springblade.modules.po.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springblade.common.constant.CommonConstant;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.node.INode;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.po.entity.CraftCtrlNodeEntity;
import org.springblade.modules.po.service.ICraftCtrlNodeService;
import org.springblade.modules.po.vo.CraftCtrlNodeVO;
import org.springblade.modules.po.wrapper.CraftCtrlNodeWrapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * @author libin
 *
 * @date 11:07 2020/7/22
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/blade-node/craftctrl")
@Api(value = "卡控节点类型维护", tags = "卡控节点类型维护")
public class CraftCtrlNodeController {


    private ICraftCtrlNodeService craftCtrlNodeService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入craftCtrlNodeEntity")
    public R<CraftCtrlNodeVO> detail(CraftCtrlNodeEntity craftCtrlNodeEntity) {
        CraftCtrlNodeEntity detail = craftCtrlNodeService.getOne(Condition.getQueryWrapper(craftCtrlNodeEntity));
        CraftCtrlNodeVO  craftCtrlNodeVO = CraftCtrlNodeWrapper.build().entityVO(detail);
        if(!StringUtil.isEmpty(detail.getParentId()) && detail.getParentId() != 0){
            craftCtrlNodeVO.setParentName(craftCtrlNodeService.getById(detail.getParentId()).getName());
        }
        return R.data(craftCtrlNodeVO);
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "code", value = "编号", paramType = "query", dataType = "string"),
        @ApiImplicitParam(name = "name", value = "名称", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "列表", notes = "传入craftCtrlNodeEntity")
    public R<List<INode>> list(@ApiIgnore @RequestParam Map<String, Object> craftCtrlNodeEntity) {
        List<CraftCtrlNodeEntity> list = craftCtrlNodeService.list(Condition.getQueryWrapper(craftCtrlNodeEntity, CraftCtrlNodeEntity.class).lambda().orderByAsc(CraftCtrlNodeEntity::getSort));
        return R.data(CraftCtrlNodeWrapper.build().listNodeVO(list));
    }

    /**
     * 顶级列表
     */
    @GetMapping("/parent-list")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "code", value = "编号", paramType = "query", dataType = "string"),
        @ApiImplicitParam(name = "name", value = "名称", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "列表", notes = "传入craftCtrlNodeEntity")
    public R<IPage<CraftCtrlNodeVO>> parentList(@ApiIgnore @RequestParam Map<String, Object> craftCtrlNodeEntity, Query query) {
        IPage<CraftCtrlNodeEntity> page = craftCtrlNodeService.page(Condition.getPage(query), Condition.getQueryWrapper(craftCtrlNodeEntity, CraftCtrlNodeEntity.class).lambda().eq(CraftCtrlNodeEntity::getParentId, CommonConstant.TOP_PARENT_ID).orderByAsc(CraftCtrlNodeEntity::getSort));
        return R.data(CraftCtrlNodeWrapper.build().pageVO(page));
    }

    /**
     * 子列表
     */
    @GetMapping("/child-list")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "code", value = "编号", paramType = "query", dataType = "string"),
        @ApiImplicitParam(name = "name", value = "名称", paramType = "query", dataType = "string"),
        @ApiImplicitParam(name = "parentId", value = "名称", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "列表", notes = "传入craftCtrlNodeEntity")
    public R<IPage<CraftCtrlNodeVO>> childList(@ApiIgnore @RequestParam Map<String, Object> craftCtrlNodeEntity, Query query) {
        IPage<CraftCtrlNodeEntity> page = craftCtrlNodeService.page(Condition.getPage(query), Condition.getQueryWrapper(craftCtrlNodeEntity, CraftCtrlNodeEntity.class).lambda().orderByAsc(CraftCtrlNodeEntity::getSort));
        IPage<CraftCtrlNodeVO> voPage = CraftCtrlNodeWrapper.build().pageVO(page);
        for (CraftCtrlNodeVO record : voPage.getRecords()) {
            record.setParentName(craftCtrlNodeService.getById(record.getParentId()).getName());
        }
        return R.data(voPage);
    }

    /**
     * 获取树形结构
     *
     * @return
     */
    @GetMapping("/tree")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "树形结构", notes = "树形结构")
    public R<List<CraftCtrlNodeVO>> tree() {
        List<CraftCtrlNodeVO> tree = craftCtrlNodeService.tree();
        return R.data(tree);
    }

    /**
     * 新增
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增", notes = "传入craftCtrlNodeEntity")
    public R submit(@Valid @RequestBody CraftCtrlNodeEntity craftCtrlNodeEntity) {
        return R.status(craftCtrlNodeService.submit(craftCtrlNodeEntity));
    }


    /**
     * 编辑
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "编辑", notes = "传入craftCtrlNodeEntity")
    public R update(@Valid @RequestBody CraftCtrlNodeEntity craftCtrlNodeEntity) {
        return R.status(craftCtrlNodeService.update(craftCtrlNodeEntity));
    }

    /**
     * 删除
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(craftCtrlNodeService.removeCraftCtrlNode(ids));
    }

    /**
     * 获取
     *
     * @return
     */
    @GetMapping("/dictionary")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "获取", notes = "获取")
    public R<List<CraftCtrlNodeEntity>> dictionary(String code) {
        List<CraftCtrlNodeEntity> tree = craftCtrlNodeService.getList(code);
        return R.data(tree);
    }


    /**
     * 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出", notes = "传入craftCtrlNodeEntity")
    public void export(CraftCtrlNodeEntity craftCtrlNodeEntity, Query query, HttpServletResponse response) throws Exception {
        craftCtrlNodeService.export(craftCtrlNodeEntity, query, response);
    }


    /**
     * 导入
     */
    @PostMapping("/importexcel")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "导入", notes = "MultipartFile")
    public R importExcel(@Valid @RequestParam MultipartFile file) throws Exception {
        Map<String, Object> map = craftCtrlNodeService.importExcel(file);
        if(Boolean.parseBoolean(map.get("flag").toString())){
            return R.success(map.get("msg").toString());
        }
        return R.fail(map.get("msg").toString());
    }

    /**
     * 全集
     *
     * @return R
     */
    @GetMapping("/allparent")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "列表", notes = "")
    public R<List<CraftCtrlNodeEntity>> allParent() {
        QueryWrapper<CraftCtrlNodeEntity> queryWrapper = Condition.getQueryWrapper(new CraftCtrlNodeEntity());
        queryWrapper.eq("parent_id", 0) ;
        return R.data(craftCtrlNodeService.list(queryWrapper));
    }
}
