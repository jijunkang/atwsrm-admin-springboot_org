package org.springblade.modules.pr.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;

import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.entity.OutPrItemProcessEntity;
import org.springblade.modules.outpr.service.IOutPrItemProcessService;
import org.springblade.modules.outpr.service.IOutPrItemService;
import org.springblade.modules.po.dto.PoItemDTO;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.mapper.PoItemMapper;
import org.springblade.modules.po.vo.PoItemVO;
import org.springblade.modules.pr.dto.*;
import org.springblade.modules.pr.entity.*;
import org.springblade.modules.pr.mapper.U9PrEntityExMapper;
import org.springblade.modules.pr.mapper.U9PrMapper;
import org.springblade.modules.pr.service.IU9PrExService;
import org.springblade.modules.pr.service.IU9PrService;
import org.springblade.modules.pr.vo.*;
import org.springblade.modules.supplier.dto.CaiGouScheduleReq;
import org.springblade.modules.supplier.entity.CaiGouSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 请购单 控制器
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-pr/u9_pr_ex")
@Api(value = "请购单", tags = "请购单")
@Log
public
class U9PrExController extends BladeController{

    private IU9PrExService iu9PrExService;

    private IU9PrService prService;

    private U9PrEntityExMapper u9PrEntityExMapper;



    /**
     * 请购单异常分析报表
     */
    @GetMapping("/report")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "请购单异常分析报表")
    public R<IPage<U9PrEntityEx>> report(U9PrExReq u9PrExReq, Query query) {
        IPage<U9PrEntityEx> pages = iu9PrExService.getU9PrEx(Condition.getPage(query),u9PrExReq );
        return R.data(pages);
    }

    /**
     * 无项目请购单拦截报表
     */
    @GetMapping("/report_no_project")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "请购单异常分析报表")
    public R<IPage<U9PrEntityNoPro>> reportNoPro(U9PrExReq u9PrExReq, Query query) {
        IPage<U9PrEntityNoPro> pages = iu9PrExService.getU9PrNoPro(Condition.getPage(query),u9PrExReq );
        return R.data(pages);
    }

    /**
     * 请购单异常分析报表 释放PR
     */
    @PostMapping ("/free_pr")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "释放PR", notes = "释放PR")
    public R freePro(@RequestBody List<U9PrEntityEx> u9PrEntityExList) {
        return R.status(iu9PrExService.freePr(u9PrEntityExList));
    }

    /**
     * 无项目 释放PR
     */
    @PostMapping ("/free_pr_no_project")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "释放PR", notes = "释放PR")
    public R freeProWithNoProject(@RequestBody List<U9PrEntityNoPro> u9PrEntityNoPros) {
        return R.status(prService.releaseNoProPRByPrLn(u9PrEntityNoPros));
    }

    /**
     * 请购单异常分析报表 导出
     */
    @GetMapping ("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "导出异常PR", notes = "导出异常PR")
    public void export(U9PrExReq u9PrExReq, HttpServletResponse response) {
        iu9PrExService.exportExcel(u9PrExReq,response);
    }



    //@GetMapping ("/reHandleExceptData")
    public void reHandleExceptData(String time) throws ParseException {
        //Date date = new SimpleDateFormat("yyyy-MM-dd").parse(time);
        log.info("HandleExceptData处理时间"+time);
        PrReq prReq=new PrReq();
        prReq.setCreateTimeStart(time);
        List<U9PrFromPhpDTO> u9php=new ArrayList<>();
        List <U9PrDTO> u9list=u9PrEntityExMapper.selectU9ListByReq(prReq);
        for (U9PrDTO item:u9list) {
            U9PrFromPhpDTO u9PrFromPhpDTO=new U9PrFromPhpDTO();
            u9PrFromPhpDTO.setPRDate(Math.toIntExact(item.getPrDate()));
            u9PrFromPhpDTO.setPRNO(item.getPrCode());
            u9PrFromPhpDTO.setPRLineNo(item.getPrLn());
            u9PrFromPhpDTO.setItemCode(item.getItemCode());
            u9PrFromPhpDTO.setRequireDate(item.getReqDate());
            u9PrFromPhpDTO.setPrspDate(DateUtil.parse(item.getJobDate()));
            u9php.add(u9PrFromPhpDTO);
        }
        prService.handleExceptData2(u9php);
    }

    @Scheduled(cron="0 30 7 * * ?")
    @Scheduled(cron="0 30 23 * * ?")
    //@PostConstruct
    public void JobHandleExceptData() throws ParseException {

        log.info("开始定时任务JobHandleExceptData");

        //DateTime dateTime = DateUtil.yesterday();
        //String timeString= DateUtil.formatDate(dateTime);

        reHandleExceptData(DateUtil.today());



    }

    public static void main(String[] args) {
        DateTime dateTime = DateUtil.yesterday();
        String timeString= DateUtil.formatDate(dateTime);

        String now = DateUtil.today();
        System.out.println(now);
    }




    @Scheduled(cron="0 10 00 ? * * ")
    public void JobReleaseNoProPR()  {

        log.info("开始定时任务JobReleaseNoProPR");

        prService.releaseNoProPR();

    }

    @Scheduled(cron="0 0 5 * * ?")
    public void JobReleaseNoProPR2() throws ParseException {

        log.info("开始定时任务JobReleaseNoProPR2，为了删除掉有些项目后跑出来的PR");
        prService.releaseNoProPR2();
    }








}
