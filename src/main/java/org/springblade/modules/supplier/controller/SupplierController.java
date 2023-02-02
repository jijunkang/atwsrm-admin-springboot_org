package org.springblade.modules.supplier.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.material.dto.MaterialPriceDTO;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.supplier.dto.CaiGouScheduleReq;
import org.springblade.modules.supplier.dto.SupplierDTO;
import org.springblade.modules.supplier.dto.SupplierScheduleReq;
import org.springblade.modules.supplier.dto.SupplierUpdateReq;
import org.springblade.modules.supplier.entity.CaiGouSchedule;
import org.springblade.modules.supplier.entity.SupUser;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.entity.SupplierSchedule;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springblade.modules.supplier.vo.OmsEchrtsOfSupplierVO;
import org.springblade.modules.supplier.vo.SupplierScheduleVO;
import org.springblade.modules.supplier.vo.SupplierVO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 供应商 控制器
 *
 * @author xianboss
 */
@RestController
@AllArgsConstructor
@RequestMapping("blade-supplier/supplier")
@Api(value = "供应商", tags = "供应商")
public class SupplierController extends BladeController {

	private ISupplierService supplierService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入supplier")
    public R<SupplierVO> detail(Supplier supplier){
        supplier.setPrimaryContact("1");
        SupplierVO detail = supplierService.getDetails(supplier);
        return R.data(detail);
    }

    /**
     * tab统计
     *
     * @return
     */
    @GetMapping("/weekCount")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "", notes = "")
    public R<List<Map<String, Object>>> getFlowCount(){
        SupplierScheduleReq supplierScheduleReq = new SupplierScheduleReq();
        return R.data(supplierService.getWeekCount(supplierScheduleReq));
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入supplier")
    public R<IPage<Supplier>> list(Supplier supplier, Query query) {
        IPage<Supplier> pages = supplierService.page(Condition.getPage(query), Condition.getQueryWrapper(supplier));
        return R.data(pages);
    }

    /**
     * 自定义分页 供应商
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "分页", notes = "传入supplier")
    public R<IPage<SupplierVO>> page(SupplierVO supplier, Query query){
        IPage<SupplierVO> pages = supplierService.selectSupplierPage(Condition.getPage(query), supplier);
        return R.data(pages);
    }


	/**
	 * 新增 代码自定义代号
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入supplier")
	public R save(@Valid @RequestBody SupplierDTO supplier) {
		return R.status(supplierService.save(supplier));
	}

	/**
	 * 修改 代码自定义代号
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入supplier")
	public R update(@Valid @RequestBody SupplierUpdateReq supplier) {
		return R.status(supplierService.updateBiz(supplier));
	}

	/**
	 * 新增或修改 代码自定义代号
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入supplier")
	public R submit(@Valid @RequestBody Supplier supplier) {
		return R.status(supplierService.saveOrUpdate(supplier));
	}


	/**
	 * 删除 代码自定义代号
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(supplierService.deleteLogic(Func.toLongList(ids)));
	}

    /**
     * 联想搜索 前十条
     */
    @GetMapping("/associate")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "不分页", notes = "传入supplier")
    public R<List<SupplierVO>> associate(SupplierVO supplier, Query query){
        if(StringUtils.isBlank(supplier.getCode()) && StringUtils.isBlank(supplier.getName())){
            return R.data(null);
        }
        IPage<SupplierVO> page = supplierService.selectSupplierPage(Condition.getPage(query), supplier);
        List<SupplierVO> list = page.getRecords();
        return R.data(list);
    }


    /**
     * 重置登录密码
     */
    @PostMapping("/resetpwd")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入supplier")
    public R resetPassword(@Valid @RequestBody Supplier supplier) {
        return R.status(supplierService.resetPassword(supplier));
    }

    /**
     * 获取其他联系人信息
     */
    @GetMapping("/getOtherCtcInfos")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "分页", notes = "传入supCode")
    public R<IPage<SupplierVO>> getOtherCtcInfos(String supCode, Query query){
        IPage<SupplierVO> pages = supplierService.getOhterCtcInfos(Condition.getPage(query), supCode);
        return R.data(pages);
    }

    /**
     * 新增 其他联系人信息
     */
    @PostMapping("/saveOtherCtcInfos")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "新增其他联系人信息", notes = "传入supplier")
    public R saveOtherCtcInfos(@Valid @RequestBody SupplierDTO supplier) {
        return R.status(supplierService.saveOhterCtcInfos(supplier));
    }

    /**
     * 编辑 其他联系人信息
     */
    @PostMapping("/updateOtherCtcInfos")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "新增其他联系人信息", notes = "传入supplier")
    public R updateOtherCtcInfos(@Valid @RequestBody Supplier supplier) {
        return R.status(supplierService.updateOhterCtcInfos(supplier));
    }

    /**
     * 删除 其他联系人信息
     */
    @PostMapping("/delOtherCtcInfos")
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "删除其他联系人信息", notes = "传入supplier")
    public R delOtherCtcInfos(@Valid @RequestBody Supplier supplier) {
        return R.status(supplierService.delOhterCtcInfos(supplier));
    }


    /**
     * 供应商 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "supCodes")
    public
    void exportExcel(@RequestParam String supCodes ,HttpServletResponse response) throws RuntimeException{
        supplierService.exportExcel(supCodes, response);
    }

    /**
     * 供应商 及时率 导出
     */
    @GetMapping("/exportOtd")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "supCodes")
    public
    void exportExcelOtd(@RequestParam String supCodes ,HttpServletResponse response) throws RuntimeException{
        supplierService.exportExcelOtd(supCodes, response);
    }

    /**
     * 供应商供应计划表
     */
    @GetMapping("/supplierSchedule")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "supplierScheduleReq")
    public
    R<IPage<SupplierSchedule>> supplierSchedule(SupplierSchedule supplierSchedule, Query query) throws RuntimeException{
        IPage<SupplierSchedule> pages = supplierService.getSchedule(Condition.getPage(query), supplierSchedule);
        for(SupplierSchedule schedule : pages.getRecords()){
            if (schedule.getLswdh() == null) {schedule.setLswdh(new BigDecimal("0"));}
            if (schedule.getCwjhshsl() == null) {schedule.setCwjhshsl(new BigDecimal("0"));}
            if (schedule.getCwqssl() == null) {schedule.setCwqssl(new BigDecimal("0"));}
            if (schedule.getCwkdhsl() == null) {schedule.setCwkdhsl(new BigDecimal("0"));}
            if (schedule.getN1wjhshsl() == null) {schedule.setN1wjhshsl(new BigDecimal("0"));}
            if (schedule.getN1wkdhsl() == null) {schedule.setN1wkdhsl(new BigDecimal("0"));}
            if (schedule.getN2wjhshsl() == null) {schedule.setN2wjhshsl(new BigDecimal("0"));}
            if (schedule.getN2wkdhsl() == null) {schedule.setN2wkdhsl(new BigDecimal("0"));}
            if (schedule.getN3wjhshsl() == null) {schedule.setN3wjhshsl(new BigDecimal("0"));}
            if (schedule.getN3wkdhsl() == null) {schedule.setN3wkdhsl(new BigDecimal("0"));}
            if (schedule.getN4wjhshsl() == null) {schedule.setN4wjhshsl(new BigDecimal("0"));}
            if (schedule.getN4wkdhsl() == null) {schedule.setN4wkdhsl(new BigDecimal("0"));}
            if (schedule.getWljhshsl() == null) {schedule.setWljhshsl(new BigDecimal("0"));}
            if (schedule.getWlkdhsl() == null) {schedule.setWlkdhsl(new BigDecimal("0"));}
        }
        return R.data(pages);
    }

    /**
     * 供应商供应计划表
     */
    @GetMapping("/supplierScheduleOfOms")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "supplierScheduleReq")
    public
    R<IPage<SupplierSchedule>> supplierScheduleOfOms(SupplierScheduleReq supplierScheduleReq, Query query) throws RuntimeException{
        IPage<SupplierSchedule> pages = supplierService.getScheduleOfOms(Condition.getPage(query), supplierScheduleReq);
        for(SupplierSchedule schedule : pages.getRecords()){
            if (schedule.getN1wjhshsl() == null) {schedule.setN1wjhshsl(new BigDecimal("0"));}
            if (schedule.getN1wkdhsl() == null) {schedule.setN1wkdhsl(new BigDecimal("0"));}
            if (schedule.getN2wjhshsl() == null) {schedule.setN2wjhshsl(new BigDecimal("0"));}
            if (schedule.getN2wkdhsl() == null) {schedule.setN2wkdhsl(new BigDecimal("0"));}
            if (schedule.getN3wjhshsl() == null) {schedule.setN3wjhshsl(new BigDecimal("0"));}
            if (schedule.getN3wkdhsl() == null) {schedule.setN3wkdhsl(new BigDecimal("0"));}
            if (schedule.getN4wjhshsl() == null) {schedule.setN4wjhshsl(new BigDecimal("0"));}
            if (schedule.getN4wkdhsl() == null) {schedule.setN4wkdhsl(new BigDecimal("0"));}
            if (schedule.getWljhshsl() == null) {schedule.setWljhshsl(new BigDecimal("0"));}
            if (schedule.getWlkdhsl() == null) {schedule.setWlkdhsl(new BigDecimal("0"));}
            if (schedule.getLswdh() == null) {schedule.setLswdh(new BigDecimal("0"));}
            if (schedule.getCwjhshsl() == null) {schedule.setCwjhshsl(new BigDecimal("0"));}
            if (schedule.getCwqssl() == null) {schedule.setCwqssl(new BigDecimal("0"));}
            if (schedule.getCwkdhsl() == null) {schedule.setCwkdhsl(new BigDecimal("0"));}
        }
        return R.data(pages);
    }


    /**
     * 批量发送邮件
     */
    @PostMapping("/batchSendEmail")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "supplierScheduleReq")
    public
    R batchSendEmail(@Valid @RequestBody SupplierScheduleReq supplierScheduleReq) throws RuntimeException{
        return R.status(supplierService.batchSendEmail(supplierScheduleReq.getScheduleList()));
    }


    /**
     * 批量保存数据
     */
    @PostMapping("/saveData")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "supplierScheduleReq")
    public
    R saveData(@Valid @RequestBody SupplierScheduleReq supplierScheduleReq) throws RuntimeException{
        return R.status(supplierService.saveData(supplierScheduleReq));
    }

    /**
     * 导出
     *
     * @param supplierScheduleReq
     * @param response HttpServletResponse
     */
    @GetMapping("/exportAll")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void flowExport(SupplierScheduleReq supplierScheduleReq, HttpServletResponse response) {
        supplierService.exportAll(supplierScheduleReq, response);
    }


    /**
     * 采购送货计划表
     */
    @GetMapping("/caiGouSchedule")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "CaiGouScheduleReq")
    public
    R<IPage<CaiGouSchedule>> caiGouSchedule(CaiGouScheduleReq caiGouScheduleReq, Query query) throws RuntimeException{
        IPage<CaiGouSchedule> pages = supplierService.getCaiGouSchedules(Condition.getPage(query), caiGouScheduleReq);
        return R.data(pages);
    }

    /**
     * 采购送货计划表自己排程
     */
    @GetMapping("/caiGouScheduleAutoSort")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "CaiGouScheduleReq")
    public
    R<IPage<CaiGouSchedule>> caiGouScheduleAutoSort(CaiGouScheduleReq caiGouScheduleReq, Query query) throws RuntimeException{
        IPage<CaiGouSchedule> pages = supplierService.caiGouScheduleAutoSort(Condition.getPage(query), caiGouScheduleReq);
        return R.data(pages);
    }

    /**
     * 采购送货报表导出
     * @param caiGouScheduleReq
     * @param response
     */
    @GetMapping("/exportCaiGouAll")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportCaiGouAll(CaiGouScheduleReq caiGouScheduleReq , HttpServletResponse response) {
        supplierService.exportCaiGouAll(caiGouScheduleReq, response);
    }


    /**
     * 采购送货报表导出
     * @param caiGouScheduleReq
     * @param response
     */
    @GetMapping("/exportCaiGouAllAutoSort")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportCaiGouAllAutoSort(CaiGouScheduleReq caiGouScheduleReq , HttpServletResponse response) {
        supplierService.exportCaiGouAllAutoSort(caiGouScheduleReq, response);
    }


    /**
     * 主界面  -- 导出
     * @param caiGouScheduleReq
     * @param response
     */
    @GetMapping("/otdExport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void otdExport(SupplierScheduleReq supplierScheduleReq, HttpServletResponse response) throws Exception{
        supplierService.otdExport(supplierScheduleReq,response);
    }


    /**
     * 采购送货报表导出 - offset
     * @param caiGouScheduleReq
     * @param response
     */
    @GetMapping("/exportCaiGouAllOffset")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportCaiGouAllOffset(CaiGouScheduleReq caiGouScheduleReq , HttpServletResponse response) {
        supplierService.exportCaiGouAllOffset(caiGouScheduleReq, response);
    }

    /**
     * 采购送货报表导出 - unchecked
     * @param caiGouScheduleReq
     * @param response
     */
    @GetMapping("/exportCaiGouAllUnchecked")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportCaiGouAllUnchecked(CaiGouScheduleReq caiGouScheduleReq , HttpServletResponse response) {
        supplierService.exportCaiGouAllUnchecked(caiGouScheduleReq, response);
    }

    /**
     * 采购送货报表导出 - unpip
     * @param caiGouScheduleReq
     * @param response
     */
    @GetMapping("/exportCaiGouAllUnpip")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public void exportCaiGouAllUnpip(CaiGouScheduleReq caiGouScheduleReq , HttpServletResponse response) {
        supplierService.exportCaiGouAllUnpip(caiGouScheduleReq, response);
    }

    /**
     * 送货执行偏差报表
     */
    @GetMapping("/caiGouScheduleOffset")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "CaiGouScheduleReq")
    public
    R<IPage<CaiGouSchedule>> caiGouScheduleOffset(CaiGouScheduleReq caiGouScheduleReq, Query query) throws RuntimeException{
        IPage<CaiGouSchedule> pages = supplierService.getCaiGouSchedulesOffset(Condition.getPage(query), caiGouScheduleReq);
        return R.data(pages);
    }

    /**
     * 审核交期未修改报表
     */
    @GetMapping("/caiGouScheduleUnchecked")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "CaiGouScheduleReq")
    public
    R<IPage<CaiGouSchedule>> caiGouScheduleUnchecked(CaiGouScheduleReq caiGouScheduleReq, Query query) throws RuntimeException{
        IPage<CaiGouSchedule> pages = supplierService.getCaiGouSchedulesUnchecked(Condition.getPage(query), caiGouScheduleReq);
        return R.data(pages);
    }

    /**
     * 标准交期偏差报表
     */
    @GetMapping("/caiGouScheduleUnpip")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "CaiGouScheduleReq")
    public
    R<IPage<CaiGouSchedule>> caiGouScheduleUnpip(CaiGouScheduleReq caiGouScheduleReq, Query query) throws RuntimeException{
        IPage<CaiGouSchedule> pages = supplierService.getCaiGouSchedulesUnpip(Condition.getPage(query), caiGouScheduleReq);
        return R.data(pages);
    }

    /**
     * 批量保存数据
     */
    @PostMapping("/saveDataOfCaiGou")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "caiGouScheduleReq")
    public R saveDataOfCaiGou(@Valid @RequestBody CaiGouScheduleReq caiGouScheduleReq) throws RuntimeException{
        return R.status(supplierService.saveDataOfCaiGou(caiGouScheduleReq));
    }


    /**
     * 批量锁定数据
     */
    @PostMapping("/lockPro")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "caiGouScheduleReq")
    public R lockPro(@Valid @RequestBody CaiGouScheduleReq caiGouScheduleReq) throws RuntimeException{
        return R.status(supplierService.lockPro(caiGouScheduleReq));
    }

    /**
     * 批量释放数据
     */
    @PostMapping("/freePro")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "caiGouScheduleReq")
    public R freePro(@Valid @RequestBody CaiGouScheduleReq caiGouScheduleReq) throws RuntimeException{
        return R.status(supplierService.freePro(caiGouScheduleReq));
    }

    /**
     * 获取料号种类个数
     * @param caiGouScheduleReq
     * @param response
     */
    @GetMapping("/getItemCodeTypeNum")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public List<OmsEchrtsOfSupplierVO> getItemCodeTypeNum(SupplierScheduleReq supplierScheduleReq) {
        return supplierService.getBarEchartsNum(supplierScheduleReq);
    }

    /**
     * 获取料号种类 供应商交期及时率
     * @param caiGouScheduleReq
     * @param response
     */
    @GetMapping("/getItemTypeNum")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public OmsEchrtsOfSupplierVO getItemTypeNum(SupplierScheduleReq supplierScheduleReq) {
        return supplierService.getLineEchartsNum(supplierScheduleReq);
    }


    /**
     * 主界面 (月)
     * @param caiGouScheduleReq
     * @param response
     */
    @GetMapping("/getMainData")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public List<OmsEchrtsOfSupplierVO> getMainData(SupplierScheduleReq supplierScheduleReq) {
        return supplierService.getMainData(supplierScheduleReq);
    }


    /**
     * 主界面 （周）
     * @param caiGouScheduleReq
     * @param response
     */
    @GetMapping("/getMainDataWeek")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public List<OmsEchrtsOfSupplierVO> getMainDataWeek(SupplierScheduleReq supplierScheduleReq) {
        return supplierService.getMainDataWeek(supplierScheduleReq);
    }


    /**
     * 主界面 （预测）
     * @param caiGouScheduleReq
     * @param response
     */
    @GetMapping("/getMainDataForest")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "prReq")
    public List<OmsEchrtsOfSupplierVO> getMainDataForest(SupplierScheduleReq supplierScheduleReq) {
        return supplierService.getMainDataForest(supplierScheduleReq);
    }



    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/updateMore")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入supplier")
    public R updateMore(@Valid @RequestBody SupplierUpdateReq supplier) {
        return R.status(supplierService.updateMore(supplier));
    }

}
