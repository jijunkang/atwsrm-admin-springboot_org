package org.springblade.modules.bizinquiry.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.codehaus.jettison.json.JSONException;
import org.springblade.core.boot.ctrl.BladeController;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.bizinquiry.dto.BizInquiryReq;
import org.springblade.modules.bizinquiry.vo.BizInquiryVO;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.springblade.modules.bizinquiry.entity.BizInquiryEntity;
import org.springblade.modules.bizinquiry.service.IBizInquiryService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


/**
 * 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-bizinquiry/bizInquiry")
@Api(value = "", tags = "")
public class BizInquiryController extends BladeController {

    private IBizInquiryService bizInquiryService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入bizInquiry")
    public R<BizInquiryEntity> detail(BizInquiryEntity bizInquiry) {
        BizInquiryEntity detail = bizInquiryService.getOne(Condition.getQueryWrapper(bizInquiry));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入bizInquiry")
    public R<IPage<BizInquiryEntity>> page(BizInquiryEntity bizInquiry, Query query) {
        IPage<BizInquiryEntity> pages = bizInquiryService.page(Condition.getPage(query), Condition.getQueryWrapper(bizInquiry));
        return R.data(pages);
    }

    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入bizInquiry")
    public R save(@Valid @RequestBody BizInquiryEntity bizInquiry) {
        return R.status(bizInquiryService.save(bizInquiry));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入bizInquiry")
    public R update(@Valid @RequestBody BizInquiryEntity bizInquiry) {
        return R.status(bizInquiryService.updateById(bizInquiry));
    }

    /**
     * 新增或修改 代码自定义代号
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入bizInquiry")
    public R submit(@Valid @RequestBody BizInquiryEntity bizInquiry) {
        return R.status(bizInquiryService.saveOrUpdate(bizInquiry));
    }


    /**
     * 删除 代码自定义代号
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(bizInquiryService.deleteLogic(Func.toLongList(ids)));
    }


    /**
     * 商务询价列表
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入bizInquiry")
    public R<IPage<BizInquiryVO>> list(BizInquiryReq bizInquiryReq, Query query) {
        IPage<BizInquiryVO> pages = bizInquiryService.list(Condition.getPage(query), bizInquiryReq);
        return R.data(pages);
    }


    /**
     * 指定供应商推送
     */
    @PostMapping("/push")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R push(@Valid @RequestBody BizInquiryReq bizInquiryReq) {
        return R.status(bizInquiryService.push(bizInquiryReq));
    }


    /**
     * 商务询价-状态统计
     */
    @GetMapping("/countlist")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入bizInquiry")
    public R<List<Map<String, Object>>> countList() {
        return R.data(bizInquiryService.countList());
    }


    /**
     * 批量提交
     */
    @PostMapping("/audits")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入bizInquiryReq")
    public R audits(@Valid @RequestBody BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException{
        return R.status(bizInquiryService.audits(bizInquiryReq));
    }

    /**
     * 导出-已完成
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "导出-已完成", notes = "传入bizInquiryReq")
    public void export(HttpServletResponse response, BizInquiryReq bizInquiryReq) {
       bizInquiryService.export(response, bizInquiryReq);
    }

    /**
     * 导出-待报价
     */
    @GetMapping("/exportwait")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "导出-待报价", notes = "传入bizInquiryReq")
    public void exportWait(HttpServletResponse response, BizInquiryReq bizInquiryReq) {
        bizInquiryService.exportWait(response, bizInquiryReq);
    }

    /**
     * 导入
     */
    @PostMapping("/importexcel")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "导入", notes = "MultipartFile")
    public
    R importExcel(@Valid @RequestParam MultipartFile file) throws Exception{
        return R.status(bizInquiryService.importExcel(file));
    }


    /**
     * 批量删除询价单 zlw ADD 20210423 NO.1
     */
    @PostMapping("/removeList")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "批量逻辑删除询价单", notes = "传入bizInquiryReq")
    public R deleteList(@Valid @RequestBody BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException{
        return R.status(bizInquiryService.deleteList(bizInquiryReq));
    }

    /**
     * 批量保存备注 zlw ADD 20210426 NO.10
     */
    @PostMapping("/saveRemarks")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "批量保存备注", notes = "传入bizInquiryReq")
    public R saveRemarks(@Valid @RequestBody BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException{
        return R.status(bizInquiryService.saveRemarks(bizInquiryReq));
    }

    /**
     * 发送邮件通知供应商 zlw ADD 20210426 NO.10 暂停
     */
    @PostMapping("/sendEmail")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "发送邮件通知供应商", notes = "传入bizInquiryReq")
    public R sendEmail(@Valid @RequestBody BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException{
        return R.status(bizInquiryService.sendEmail(bizInquiryReq));
    }

    /**
     * 批量提交至已完成，但不发送邮件
     */
    @PostMapping("/listToEndButNotSend")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "发送邮件通知供应商", notes = "传入bizInquiryReq")
    public R listToEndButNotSend(@Valid @RequestBody BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException{
        return R.status(bizInquiryService.listToEndButNotSend(bizInquiryReq));
    }

}
