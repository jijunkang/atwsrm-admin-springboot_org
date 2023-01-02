package org.springblade.modules.bizinquiry.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import javax.validation.Valid;

import org.codehaus.jettison.json.JSONException;
import org.springblade.core.boot.ctrl.BladeController;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.bizinquiry.dto.BizInquiryReq;
import org.springblade.modules.bizinquiry.entity.BizInquiryIoFileListEntity;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.springblade.modules.bizinquiry.entity.BizInquiryIoEntity;
import org.springblade.modules.bizinquiry.service.IBizInquiryIoService;

import java.util.List;


/**
 * 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-bizinquiry/bizInquiryIo")
@Api(value = "", tags = "")
public class BizInquiryIoController extends BladeController {

    private IBizInquiryIoService bizInquiryIoService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入bizInquiryIo")
    public R<BizInquiryIoEntity> detail(BizInquiryIoEntity bizInquiryIo) {
        BizInquiryIoEntity detail = bizInquiryIoService.getOne(Condition.getQueryWrapper(bizInquiryIo));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入bizInquiryIo")
    public R<IPage<BizInquiryIoEntity>> page(BizInquiryIoEntity bizInquiryIo, Query query) {
        IPage<BizInquiryIoEntity> pages = bizInquiryIoService.page(Condition.getPage(query), Condition.getQueryWrapper(bizInquiryIo));
        return R.data(pages);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入bizInquiryIo")
    public R save(@Valid @RequestBody BizInquiryIoEntity bizInquiryIo) {
        return R.status(bizInquiryIoService.save(bizInquiryIo));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入bizInquiryIo")
    public R update(@Valid @RequestBody BizInquiryIoEntity bizInquiryIo) {
        BizInquiryIoEntity temp = bizInquiryIoService.getByQoId(bizInquiryIo.getQoId());
        temp.setAttachment(bizInquiryIo.getAttachment());
        return R.status(bizInquiryIoService.updateById(temp));
    }

    /**
     * 新增或修改 代码自定义代号
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入bizInquiryIo")
    public R submit(@Valid @RequestBody BizInquiryIoEntity bizInquiryIo) {
        return R.status(bizInquiryIoService.saveOrUpdate(bizInquiryIo));
    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入id")
    public R remove(@Valid @RequestBody BizInquiryReq bizInquiryReq) {
        return R.status(bizInquiryIoService.removeById(bizInquiryReq.getId()));
    }


    /**
     * 批量录入报价
     */
    @PostMapping("/savebatch")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入bizInquiryReq")
    public R saveBizBatch(@Valid @RequestBody BizInquiryReq bizInquiryReq) {
        return R.status(bizInquiryIoService.saveBizBatch(bizInquiryReq));
    }


    /**
     * 文件一览详情
     */
    @GetMapping("/fileList")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "文件一览详情", notes = "传入bizInquiryIo")
    public R<List<BizInquiryIoFileListEntity>> getFileList(BizInquiryIoEntity bizInquiryIo) {
        return R.data(bizInquiryIoService.getFileList(bizInquiryIo.getId()));
    }

    /**
     * 保存文件
     */
    @PostMapping("/saveFile")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "保存文件", notes = "传入bizInquiryReq")
    public R saveFile(@Valid @RequestBody BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException {
        return R.status(bizInquiryIoService.saveFile(bizInquiryReq));
    }

    /**
     * 批量保存文件
     */
    @PostMapping("/saveFileList")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "批量存文件", notes = "传入bizInquiryReq")
    public R saveFileList(@Valid @RequestBody BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException {
        return R.status(bizInquiryIoService.saveFileList(bizInquiryReq));
    }

    /**
     * 批量删除文件
     */
    @PostMapping("/removeFileList")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "批量删文件", notes = "传入bizInquiryReq")
    public R removeFileList(@Valid @RequestBody BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException {
        return R.status(bizInquiryIoService.removeFileList(bizInquiryReq));
    }

    /**
     * 发送邮件
     */
    @PostMapping("/sendEmailToBusiness")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "批量存文件", notes = "传入bizInquiryReq")
    public R sendEmailToBusiness(@Valid @RequestBody BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException {
        return R.status(bizInquiryIoService.sendEmailToBusiness(bizInquiryReq));
    }

    /**
     * 批量发送未发送邮件
     */
    @PostMapping("/sendEmailListToBusiness")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "批量存文件", notes = "传入bizInquiryReq")
    public R sendEmailListToBusiness(@Valid @RequestBody BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException {
        return R.status(bizInquiryIoService.sendEmailListToBusiness(bizInquiryReq));
    }

}
