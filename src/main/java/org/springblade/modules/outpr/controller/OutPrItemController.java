package org.springblade.modules.outpr.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.outpr.dto.OutPrItemDTO;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.service.IOutPrItemService;
import org.springblade.modules.outpr.vo.OutPrItemVO;
import org.springblade.modules.pr.dto.ItemInfoDTO;
import org.springblade.modules.pr.dto.SubmitPriceReq;
import org.springblade.modules.pr.dto.U9PrDTO;
import org.springblade.modules.pr.entity.U9PrEntity;
import org.springblade.modules.pr.service.IU9PrService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-outpr/outpritem")
@Api(value = "", tags = "")
public class OutPrItemController extends BladeController {

    private IOutPrItemService outpritemService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入outpritem")
    public R<OutPrItemDTO> detail(OutPrItemEntity outpritem) {
        OutPrItemDTO detail = outpritemService.getDtoById(outpritem.getId());
        return R.data(detail);
    }


    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入outpritem")
    public
    R<IPage<OutPrItemVO>> page(OutPrItemDTO outPrItemDTO, Query query){
        IPage<OutPrItemVO> pages = outpritemService.voPage( query , outPrItemDTO);
        return R.data(pages);
    }


    /**
     * 分页 询价单
     */
    @GetMapping("/inquiryPage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入outpritem")
    public
    R<IPage<OutPrItemVO>> inquiryPage(OutPrItemDTO outPrItemDTO, Query query){
        IPage<OutPrItemVO> pages = outpritemService.inquiryPage( query , outPrItemDTO);
        return R.data(pages);
    }

    /**
     * 代下单分页 代码自定义代号 - 流标
     */
    @GetMapping("/bidPage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入outpritem")
    public
    R<IPage<OutPrItemVO>> bidPage(OutPrItemDTO outPrItemDTO, Query query){
        IPage<OutPrItemVO> pages = outpritemService.voBidPage( query , outPrItemDTO);
        return R.data(pages);
    }

    /**
     * 代下单分页 代码自定义代号 - 询价单
     */
    @GetMapping("/inquiryBidPage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入outpritem")
    public
    R<IPage<OutPrItemVO>> inquiryBidPage(OutPrItemDTO outPrItemDTO, Query query){
        IPage<OutPrItemVO> pages = outpritemService.voInquiryBidPage( query , outPrItemDTO);
        return R.data(pages);
    }

    /**
     * 中台统计
     *
     * @return
     */
    @GetMapping("/outTab")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "", notes = "")
    public R<List<Map<String, Object>>> getOutTab(){
        return R.data(outpritemService.getOutTab());
    }

    /**
     * 流标 批量录入价格
     */
    @PostMapping("/submitbatch")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "流标录入价格", notes = "submitPrice")
    public
    R submitBatch(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(outpritemService.submitBatch(submitPriceReq));
    }

    /**
     * 批量提交附件
     *
     * @param  submitPriceReq
     * @return R
     */
    @PostMapping("/flowBatchExcelOfOut")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "流标暂存-批量提交附件", notes = "submitPrice")
    public R flowBatchExcel(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(outpritemService.flowBatchExcelOfOut(submitPriceReq));
    }


    /**
     * 重置PR单的信息
     *
     * @param submitPriceReq
     * @return
     */
    @PostMapping("/removeOutPrList")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "自动获取供应商价格信息", notes = "submitPrice")
    public R<List<ItemInfoDTO>> removeOutPrList(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        return R.status(outpritemService.removeOutPrList(submitPriceReq));
    }


    /**
     * 自动获取供应商价格信息 international
     *
     * @param submitPriceReq
     * @return
     */
    @PostMapping("/autoRetrieve")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "自动获取供应商价格信息", notes = "submitPrice")
    public R<List<ItemInfoDTO>> autoRetrieve(@Valid @RequestBody SubmitPriceReq submitPriceReq){
        List<ItemInfoDTO> itemInfoDTOS = outpritemService.autoRetrieve(submitPriceReq.getOutPrItemDTOS());
        return R.data(itemInfoDTOS);
    }


    /**
     * 询价单中台统计
     *
     * @return
     */
    @GetMapping("/inquiryCountOfWW")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "", notes = "")
    public R<List<Map<String, Object>>> inquiryCountOfWW(){
        return R.data(outpritemService.inquiryCountOfWW());
    }


}
