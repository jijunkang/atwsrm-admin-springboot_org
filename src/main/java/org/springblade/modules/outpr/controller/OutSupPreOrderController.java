package org.springblade.modules.outpr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.outpr.dto.OutSupPreOrderDTO;
import org.springblade.modules.outpr.entity.OutSupPreOrderEntity;
import org.springblade.modules.outpr.service.IOutSupPreOrderService;
import org.springblade.modules.outpr.vo.OutSupPreOrderVO;
import org.springblade.modules.pr.service.IU9PrService;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/blade-outpr/preorder")
@Api(value = "", tags = "")
public
class OutSupPreOrderController extends BladeController{

    @Autowired
    private IOutSupPreOrderService outsuppreorderService;

    @Autowired
    IParamService paramService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入outsuppreorder")
    public
    R<OutSupPreOrderEntity> detail(OutSupPreOrderEntity outsuppreorder){
        OutSupPreOrderEntity detail = outsuppreorderService.getOne(Condition.getQueryWrapper(outsuppreorder));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入outsuppreorder")
    public
    R<IPage<OutSupPreOrderVO>> page(OutSupPreOrderEntity outsuppreorder, Query query){
        IPage<OutSupPreOrderVO> pages = outsuppreorderService.voPage(Condition.getPage(query),
                                                                     Condition.getQueryWrapper(outsuppreorder)
                                                                             .orderByDesc("update_time"));
        return R.data(pages);
    }

    /**
     * 有白名单 交期不满足需要采购确认的数据
     * @param outsuppreorder
     * @param query
     * @return
     */
    @GetMapping("/toconfirmpage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入outsuppreorder")
    public
    R<IPage<OutSupPreOrderVO>> toConfirmPage(OutSupPreOrderEntity outsuppreorder, Query query){
        QueryWrapper<OutSupPreOrderEntity> queryWrapper = Condition.getQueryWrapper(outsuppreorder);
        IPage<OutSupPreOrderVO> pages = outsuppreorderService.toConfirmVoPage(Condition.getPage(query), queryWrapper);
        return R.data(pages);
    }

    @GetMapping("/tocheckpage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入outsuppreorder")
    public
    R<IPage<OutSupPreOrderVO>> toCheckPage(OutSupPreOrderDTO outsuppreorder, Query query){
        String mRoleId  = paramService.getValue("purch_manager.role_id");//经理角色ID
        String pRoleId  = paramService.getValue("purch_user.role_id");//采购员角色ID

        QueryWrapper<OutSupPreOrderEntity> queryWrapper = outsuppreorderService.getQueryWrapper(outsuppreorder);
        if(  StringUtil.containsAny(getUser().getRoleId(), pRoleId) ){
            //queryWrapper.in("status", IOutSupPreOrderService.STATUS_SUPACCEPT);
            queryWrapper.isNull("inquiry_way");
        }else if( StringUtil.containsAny(getUser().getRoleId(), mRoleId) ){
            queryWrapper.in("status", IOutSupPreOrderService.STATUS_CHECK1, IOutSupPreOrderService.STATUS_CHECK2);
        }else {
            return R.data(Condition.getPage(query));
        }
        IPage<OutSupPreOrderVO> pages = outsuppreorderService.voPage(Condition.getPage(query), queryWrapper);
        return R.data(pages);
    }

    /**
     * 委外预定单-中台 统计数量
     *
     * @return  R
     */
    @GetMapping("/centercount")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "")
    public R<List<Map<String, Object>>> centerCount(){
        return R.data(outsuppreorderService.centerCount());
    }


    /**
     */
    @PostMapping("/check")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "审核", notes = "CheckDTO")
    public
    R check(@Valid @RequestBody CheckDTO checkDto ){
        return R.status(outsuppreorderService.check(checkDto));
    }

    /**
     */
    @PostMapping("/checkbatch")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "批量审核", notes = "CheckDTO")
    public
    R checkBatch(@Valid @RequestBody List<CheckDTO> checkDtos){
        return R.status(outsuppreorderService.check(checkDtos));
    }

    @PostMapping("/assignsup")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "指定供应商", notes = "传入outpritem")
    public R assignSup(@Valid @RequestBody OutSupPreOrderEntity preOrder) {
        return R.status(outsuppreorderService.assignSup(preOrder));
    }

}
