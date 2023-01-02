package org.springblade.modules.mathmodel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.modules.mathmodel.entity.MmSizeEntity;
import org.springblade.modules.mathmodel.entity.MmVolumeCalculateEntity;
import org.springblade.modules.mathmodel.service.IMmVolumeCalculateService;
import org.springblade.modules.mathmodel.vo.MmVolumeCalculateVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author libin
 *
 * @date 11:15 2020/9/11
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/blade-mathmodel/mmVolumeCalculate")
@Api(value = "", tags = "")
public class MmVolumeCalculateController {

    private IMmVolumeCalculateService mmVolumeCalculateService;


    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入ap")
    public R<IPage<MmVolumeCalculateEntity>> list(MmVolumeCalculateEntity mmVolumeCalculateEntity, Query query) {
        IPage<MmVolumeCalculateEntity> pages = mmVolumeCalculateService.page(Condition.getPage(query), Condition.getQueryWrapper(mmVolumeCalculateEntity));
        return R.data(pages);
    }


    /**
     * 批量选中计算
     */
    @PostMapping("/countbatch")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "批量选中计算", notes = "mmSizeEntity")
    public R countBatch(@Valid @RequestBody MmSizeEntity mmSizeEntity) {
        return R.status(mmVolumeCalculateService.countBatch(mmSizeEntity));
    }


    /**
     * 重新计算全部价格
     */
    @GetMapping("/countall")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "重新计算全部价格", notes = "")
    public R countAll() {
        return R.status(mmVolumeCalculateService.countAll());
    }


    /**
     * 获取数学模型参考价格
     */
    @GetMapping("/getprice")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "获取数学模型参考价格", notes = "")
    public R<Map<String, Object>> getPriceByCode(MmVolumeCalculateVO mmVolumeCalculateVO) {
        return R.data(mmVolumeCalculateService.getPriceByCode(mmVolumeCalculateVO));
    }


    /**
     * 根据料号查询价格表
     */
    @GetMapping("/getlist")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "重新计算全部价格", notes = "")
    public R getList(MmVolumeCalculateEntity mmVolumeCalculateEntity) {
        return R.data(mmVolumeCalculateService.getByItemCode(mmVolumeCalculateEntity.getItemCode()));
    }
}
