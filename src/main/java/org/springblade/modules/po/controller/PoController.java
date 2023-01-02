package org.springblade.modules.po.controller;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.dto.AssignDTO;
import org.springblade.common.dto.CheckDTO;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.po.dto.PoDTO;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.mapper.PoMapper;
import org.springblade.modules.po.service.IPoService;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.pr.dto.U9PrDTO;
import org.springblade.modules.pr.dto.U9PrFromPhpDTO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * 采购订单表头 控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-po/po")
@Api(value = "采购订单表头", tags = "采购订单表头")
public class PoController extends BladeController {

    private IPoService poService;

    private PoMapper poMapper;



    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入po")
    public R<PoEntity> detail(PoEntity po) {
        PoEntity detail = poService.getOne(Condition.getQueryWrapper(po));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号  - po
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po")
    public R<IPage<PoEntity>> list(PoDTO po, Query query) {
        IPage<PoEntity> pages = poService.list(query, po);
        return R.data(pages);
    }


    /**
     * 分页 代码自定义代号 - vmi
     */
    @GetMapping("/vmiList")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po")
    public R<IPage<PoEntity>> vmiList(PoDTO po, Query query) {
        IPage<PoEntity> pages = poService.vmiList(query, po);
        return R.data(pages);
    }


    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/poExport")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po")
    public void poExport(PoDTO po, HttpServletResponse response) {
        poService.poExport(po, response);
    }

    /**
     * 待处理订单
     */
    @GetMapping("/todopage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po")
    public R<IPage<PoEntity>> todoPage(PoEntity po, Query query) {
        IPage<PoEntity> pages = poService.getTodoPage(Condition.getPage(query), po);
        return R.data(pages);
    }


    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入po")
    public R update(@Valid @RequestBody PoEntity po) {
        return R.status(poService.updateById(po));
    }

    /**
     * 上传合同
     */
    @PostMapping("/updatecontract")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入po")
    public R uploadContract(@Valid @RequestBody PoEntity po) {
        return R.status(poService.uploadContract(po));
    }

    /**
     * 一级审核（副经理审核）
     */
    @PostMapping("/contractcheck")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "审核", notes = "CheckDTO")
    public R contractCheck(@Valid @RequestBody CheckDTO checkDto) {
        return R.status(poService.contractCheck(checkDto));
    }

    /**
     * 退回订单-需要指派采购员处理的列表
     */
    @GetMapping("/cancelneedassign")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po")
    public R<IPage<PoEntity>> cancelPoPage(PoEntity po, Query query) {
        PoEntity queryPo = new PoEntity();
        queryPo.setStatus(IPoService.STATUS_CANCEL);
        queryPo.setCancelAssign(IPoService.CANCEL_ASSIGN_MANAGER);
        IPage<PoEntity> pages = poService.page(Condition.getPage(query), Condition.getQueryWrapper(queryPo));
        return R.data(pages);
    }

    /**
     *
     */
    @PostMapping("/cancelpoassignpurch")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "退回订单指派采购员", notes = " ")
    public R cancelPoAssignPurch(@Valid @RequestBody AssignDTO assign) {
        return R.status(poService.cancelPoAssignPurch(assign));
    }

    /**
     * 退回订单-已经指派采购员处理的列表
     */
    @GetMapping("/cancelassigned")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入po")
    public R<IPage<PoEntity>> cancelAssignedPage(PoEntity po, Query query) {
        PoEntity queryPo = new PoEntity();
        queryPo.setStatus(IPoService.STATUS_CANCEL);
        queryPo.setCancelAssign(IPoService.CANCEL_ASSIGN_PURCH);
        queryPo.setCancelAssignId(getUser().getUserId());
        IPage<PoEntity> pages = poService.page(Condition.getPage(query), Condition.getQueryWrapper(queryPo));
        return R.data(pages);
    }

    /**
     * 中台订单待处理导出
     *
     * @param po       PoEntity
     * @param response HttpServletResponse
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "")
    public void export(PoEntity po, HttpServletResponse response) {
        poService.export(po, response);
    }


    /**
     * 订单金额审核-中台
     *
     * @param poEntity poEntity
     * @return R
     */
    @PostMapping("/auditprice")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "退回订单指派采购员", notes = " ")
    public R auditPrice(@Valid @RequestBody PoEntity poEntity) throws IOException {
        return R.status(poService.auditPrice(poEntity));
    }

    @GetMapping("/downLoadPo")
    public void downLoadPo() {

        String gyss = "青田保俐铸造有限公司," +
            "廊坊展鹏达仪表有限责任公司," +
            "盐城益鑫铸业有限公司," +
            "希诚控制设备(上海)有限公司," +
            "无锡市诚天诺执行器制造有限公司," +
            "西迪技术股份有限公司," +
            "上海鑫漪自动化设备中心," +
            "上海昌亦胜自动化设备有限公司," +
            "无锡福斯拓科科技有限公司," +
            "上海富泰斯流体设备有限公司," +
            "温州欧纳达阀门有限公司," +
            "上海潜度贸易商行," +
            "温州阿泰科机械科技有限公司," +
            "濮阳市启德商贸有限公司," +
            "福建胜阀机械制造有限公司," +
            "上海耀扬自动化控制设备有限公司," +
            "上海中洲特种合金材料股份有限公司," +
            "江阴市鑫裕锻件有限公司," +
            "浙江烨新机械科技有限公司," +
            "浙江联大锻压有限公司," +
            "永嘉通球阀门有限公司";
        String[] strings = gyss.split(",");

        for (String gys:strings) {
            int i = 0;
            //通过供应商名称找到所有订单
            PoDTO poDTO=new PoDTO();
            poDTO.setSupName(gys);
            List<PoEntity> poList = poMapper.getPoList(poDTO);
            for (PoEntity s:poList) {

                if(StringUtils.isBlank(s.getContract())){
                    continue;
                }
                String[] split = s.getContract().split("\\|");
                for (String url:split) {
                    if(StringUtils.isBlank(url)){
                        continue;
                    }
                    String houzhui=url.substring(url.lastIndexOf("."));
                    String filename=i+" "+s.getOrderCode()+" "+ WillDateUtil.dateFormat( s.getCreateTime())+houzhui;
                    long file = HttpUtil.downloadFile(url, "d:/供应商合同/"+gys+"/"+ filename);
                    i++;
                }

            }




        }
        System.out.println("结束");

    }

    public static void main(String[] args) {

        String url="http://atw.img.antiwearvalve.com/d513907cb5e86dd7/fdbb962bb19be648.jpg";

        String[] split = url.split("\\|");
        for (String s:
             split) {
            System.out.println();
        }

    }
}
