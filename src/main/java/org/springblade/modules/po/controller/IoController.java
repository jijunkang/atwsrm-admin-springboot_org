package org.springblade.modules.po.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.springblade.common.dto.CheckDTO;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.modules.po.dto.IoDTO;
import org.springblade.modules.po.dto.IoWinbidReq;
import org.springblade.modules.po.dto.OutIoDTO;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.entity.OutIoEntity;
import org.springblade.modules.po.service.IIoOutService;
import org.springblade.modules.po.service.IIoService;
import org.springblade.modules.po.vo.IoVO;
import org.springblade.modules.po.vo.OutIoVO;
import org.springblade.modules.pricelib.service.IPriceLibService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-io/io")
@Api(value = "", tags = "")
public
class IoController extends BladeController{

    private IIoService ioService;

    private IPriceLibService priceLibService;

    private IIoOutService iIoOutService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入io")
    public
    R<IoEntity> detail(IoEntity io){
        IoEntity detail = ioService.getOne(Condition.getQueryWrapper(io));
        return R.data(detail);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入io")
    public
    R<IPage<IoVO>> list(IoEntity io, Query query){
        IPage<IoVO> pages = ioService.getPage(Condition.getPage(query), Condition.getQueryWrapper(io));
        return R.data(pages);
    }


    /**
     * 分页 代码自定义代号 小零件
     */
    @GetMapping("/listOfOthers")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入io")
    public
    R<IPage<IoVO>> listOfOthers(IoEntity io, Query query){
        IPage<IoVO> pages = ioService.getPageOfOthers(Condition.getPage(query), Condition.getQueryWrapper(io));
        return R.data(pages);
    }

    /**
     * 分页 代码自定义代号 委外
     */
    @GetMapping("/listOfOut")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入io")
    public
    R<IPage<OutIoVO>> listOfOut(OutIoEntity io, Query query){
        IPage<OutIoVO> pages = ioService.getPageOfOut(Condition.getPage(query), Condition.getQueryWrapper(io));
        return R.data(pages);
    }


    /**
     * 新增 代码自定义代号
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入io")
    public
    R save(@Valid @RequestBody IoEntity io){
        return R.status(ioService.save(io));
    }

    /**
     * 修改 代码自定义代号
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入io")
    public
    R update(@Valid @RequestBody IoEntity io){
        return R.status(ioService.updateById(io));
    }

    /**
     * 新增或修改 代码自定义代号
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入io")
    public
    R submit(@Valid @RequestBody IoEntity io){
        return R.status(ioService.saveOrUpdate(io));
    }


    /**
     * 使io中标
     */
    @PostMapping("/letiowinbid")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "使io中标", notes = "传入io")
    public
    R letIoWinBid(@Valid @RequestBody IoWinbidReq ioWinbidReq){
        IoEntity winBidIo = ioService.letIoWinBid(ioWinbidReq);
        if(ioWinbidReq.getIsIntoPriceLib() == 0){ //无需要加入白名单
            return R.status(winBidIo != null);
        }
        if(ioWinbidReq.getEffectiveDate() == null){ //白名单生效日期 默认当天时间
            ioWinbidReq.setEffectiveDate(WillDateUtil.getTodayStart());
        }
        //需要加入白名单
        boolean ioToPriceLibOk = priceLibService.ioToPriceLib(ioWinbidReq);
        if(ioToPriceLibOk){
            return R.status(true);
        }
        return R.fail("中标操作成功，加入白名单失败。");
    }


    /**
     * 使io流标
     */
    @PostMapping("/letioflow")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "使io中标", notes = "传入io")
    public
    R letIoFlow(@Valid @RequestBody IoDTO io){
        return R.status(ioService.letIoFlow(io));
    }


    /**
     * 使io中标 - 委外
     */
    @PostMapping("/letiowinbidOfWW")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "使io中标", notes = "传入io")
    public
    R letIoWinBidOfWW(@Valid @RequestBody IoWinbidReq ioWinbidReq){
        OutIoEntity winBidIo = ioService.letIoWinBidOfWW(ioWinbidReq);
        if(ioWinbidReq.getIsIntoPriceLib() == 0){ //无需要加入白名单
            return R.status(winBidIo != null);
        }
        if(ioWinbidReq.getEffectiveDate() == null){ //白名单生效日期 默认当天时间
            ioWinbidReq.setEffectiveDate(WillDateUtil.getTodayStart());
        }
        //需要加入白名单
        boolean ioToPriceLibOk = priceLibService.ioToPriceLibOfWW(ioWinbidReq);

        if(ioToPriceLibOk){
            return R.status(true);
        }
        return R.fail("中标操作成功，加入白名单失败。");
    }


    /**
     * 使io流标 - 委外
     */
    @PostMapping("/letPrFlowOfWW")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "使io中标", notes = "传入io")
    public
    R letioflowOfWW(@Valid @RequestBody OutIoDTO io){
        return R.status(ioService.letIoFlowOfWW(io));
    }

    /**
     * 删除 代码自定义代号
     */
    //    @PostMapping("/remove")
    //    @ApiOperationSupport(order = 7)
    //    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    //    public
//    R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids){
//        return R.status(ioService.deleteLogic(Func.toLongList(ids)));
//    }

    /**
     * 待白名单确认的列表
     */
    @GetMapping("/toconfirmpage")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "分页", notes = "传入io")
    public
    R<IPage<IoDTO>> toConfirmPage(IoDTO io, Query query){
        io.setPurchCode(getUser().getAccount());
        IPage<IoDTO> pages  = ioService.selectToConfirmPage(Condition.getPage(query),io);
        return R.data(pages);
    }
    /**
     * 待审核列表
     */
    @GetMapping("/toCheckList")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "分页", notes = "传入io")
    public
    R<IPage<IoDTO>> toCheckList(IoEntity io, Query query){
        IPage<IoDTO> pages  = ioService.selectToCheckPage(Condition.getPage(query), io,getUser());
        return R.data(pages);
    }

    /**
     * 一级审核（副经理审核）
     */
    @PostMapping("/check1")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "审核", notes = "CheckDTO 51：审核通过，   52：已阅， 70：拒绝")
    public
    R check1(@Valid @RequestBody CheckDTO checkDto){

        return R.status(ioService.check1(checkDto));
    }

    /**
     * 一级审核批量（副经理审核）
     */
    @PostMapping("/check1batch")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "审核", notes = "CheckDTO 51：审核通过，   52：已阅， 70：拒绝")
    public
    R check1batch(@Valid @RequestBody List<CheckDTO> checkDtoList){
        for(CheckDTO checkDto : checkDtoList){
            ioService.check1(checkDto);
        }
        return R.success("操作成功");
    }

    /**
     * 二级审核（经理审核）
     */
    @PostMapping("/check2")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "审核", notes = "CheckDTO 80：审核通过； 70：拒绝")
    public
    R check2(@Valid @RequestBody CheckDTO checkDto){
        return R.status(ioService.check2(checkDto));
    }

    /**
     * 二级审核批量（经理审核）
     */
    @PostMapping("/check2batch")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "审核", notes = "CheckDTO 51：审核通过，   52：已阅， 70：拒绝")
    public
    R check2batch(@Valid @RequestBody List<CheckDTO> checkDtoList){
        for(CheckDTO checkDto : checkDtoList){
            ioService.check2(checkDto);
        }
        return R.success("操作成功");
    }


    /**
     * 二级审核批量（经理审核）- 阀内件
     */
    @PostMapping("/check2BatchOfOthers")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "审核", notes = "CheckDTO 51：审核通过，   52：已阅， 70：拒绝")
    @Transactional(rollbackFor = Exception.class)
    public
    R check2batchOfOthers(@Valid @RequestBody List<CheckDTO> checkDtoList){
        for(CheckDTO checkDto : checkDtoList){
            ioService.check2OfOthers(checkDto);
        }
        return R.success("操作成功");
    }



    /**
     * 二级审核批量（经理审核）- 工序委外
     */
    @PostMapping("/check2BatchOfWW")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "审核", notes = "CheckDTO 51：审核通过，   52：已阅， 70：拒绝")
    @Transactional(rollbackFor = Exception.class)
    public
    R check2batchOfWW(@Valid @RequestBody List<CheckDTO> checkDtoList){
        for(CheckDTO checkDto : checkDtoList){
            iIoOutService.check2OfWW(checkDto);
        }
        return R.success("操作成功");
    }

}
