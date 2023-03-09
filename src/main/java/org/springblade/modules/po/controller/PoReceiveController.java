package org.springblade.modules.po.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.po.dto.PoItemDTO;
import org.springblade.modules.po.dto.PoReceiveDTO;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.entity.PoReceiveEntity;
import org.springblade.modules.po.mapper.PoItemMapper;
import org.springblade.modules.po.mapper.PoMapper;
import org.springblade.modules.po.mapper.PoReceiveMapper;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.po.service.IPoReceiveService;
import org.springblade.modules.po.vo.PoItemVO;
import org.springblade.modules.po.vo.PoReceiveVO;
import org.springblade.modules.po.wrapper.PoReceiveWrapper;
import org.springblade.modules.system.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;


/**
 *  控制器
 *
 * @author Will
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blade-poreceive/poReceive")
@Api(value = "", tags = "")
public class PoReceiveController extends BladeController {

    private IPoReceiveService poReceiveService;

    private IPoItemService poItemService;

    private IUserService userService;

    private PoItemMapper poItemMapper;

    private PoMapper poMapper;

    private PoReceiveMapper poReceiveMapper;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入poReceive")
    public R<PoReceiveEntity> detail(PoReceiveEntity poReceive) {
        PoReceiveEntity detail = poReceiveService.getOne(Condition.getQueryWrapper(poReceive));
        return R.data(detail);
    }


    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入poReceive")
    public R<IPage<PoReceiveVO>> page(PoReceiveEntity poReceive, Query query) {
        IPage<PoReceiveEntity> pages = poReceiveService.page(Condition.getPage(query), Condition.getQueryWrapper(poReceive));
        IPage<PoReceiveVO> vopage = new Page<PoReceiveVO>(pages.getCurrent(),pages.getSize(),pages.getTotal());
        List<PoReceiveVO> voList = Lists.newArrayList();
        for(PoReceiveEntity entity :pages.getRecords()){
            PoReceiveVO vo = PoReceiveWrapper.build().entityVO(entity);
            voList.add(vo);
            PoItemEntity pi = poItemService.getById(entity.getPiId());
            if(pi != null){
                vo.setPoCode(pi.getPoCode());
                vo.setPoLn(pi.getPoLn());
                vo.setItemCode(pi.getItemCode());
                vo.setItemName(pi.getItemName());
                vo.setProGoodsNum(pi.getProGoodsNum());
                vo.setPriceUom(pi.getPriceUom());
                vo.setTcUom(pi.getTcUom());
            }
        }
        vopage.setRecords(voList);
        return R.data(vopage);
    }

    /**
     * 分页 代码自定义代号
     */
    @GetMapping("/doPage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入poReceive")
    public
    R<IPage<PoReceiveVO>> doPage(PoReceiveDTO poReceive, Query query) {

        String rcvCode = poReceive.getRcvCode() == null ? "" : poReceive.getRcvCode();
        int size = rcvCode.split(",").length;
        if(size > 1){
            poReceive.setRcvCodes(rcvCode);
        }
        IPage<PoReceiveVO> pages = poReceiveService.selectPageOfParams(Condition.getPage(query), poReceive);

        IPage<PoReceiveVO> vopage = new Page<PoReceiveVO>(pages.getCurrent(), pages.getSize(), pages.getTotal());
        List<PoReceiveVO> voList = Lists.newArrayList();

        for (PoReceiveVO entity : pages.getRecords()) {
            PoReceiveVO vo = entity;
            Long createUser = entity.getCreateUserRecord() == null ? entity.getCreateUser() : entity.getCreateUserRecord();
            Date createTimeRecord = entity.getCreateTimeRecord() == null ? entity.getCreateTime() : entity.getCreateTimeRecord();
            vo.setCreateTimeRecord(createTimeRecord);
            if (userService.getById(createUser) == null) {
                // 1 是 本厂
                vo.setCreater(entity.getSupCode());
            } else {
                vo.setCreater("安特威");
            }

            if (userService.getById(entity.getUpdateUser()) == null) {
                vo.setUpdater(entity.getSupCode());
            } else {
                vo.setUpdater("安特威");
            }
            voList.add(vo);
        }
        vopage.setRecords(voList);
        return R.data(vopage);
    }

    /**
     * 获取rcv送货单
     */
    @GetMapping("/rcvcodepage")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入poReceive")
    public R<IPage<PoReceiveEntity>> rcvCodePage(PoReceiveEntity poReceive, Query query) {
        QueryWrapper<PoReceiveEntity> qw =  poReceiveService.getQueryWrapper(poReceive);
        IPage<PoReceiveEntity> pages = poReceiveService.page(Condition.getPage(query), qw);
        return R.data(pages);
    }


    /**
     * 分页 详情
     */
    @GetMapping("/doDetail")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入poReceive")
    public
    R<List<PoItemVO>> doDetail(PoReceiveDTO poReceive, Query query) {
        List<PoItemVO> voList = Lists.newArrayList();
        List<PoReceiveEntity> poReceiveEntityIPage = poReceiveService.list(
            Condition.getQueryWrapper(new PoReceiveEntity())
                .in(poReceive.getStatuss()!=null && poReceive.getStatuss().equals(poReceiveService.STATUS_UPDATE) && poReceive.getStatus()==null,"status",poReceiveService.STATUS_ORDER,poReceiveService.STATUS_OUT_TOCHECK,poReceiveService.STATUS_OUT_CHECK,poReceiveService.STATUS_TOHANDLE)
                .eq(poReceive.getStatus()!=null ,"status",poReceive.getStatus())
                .le(poReceive.getStatus()==null && poReceive.getStatuss()==null,"status",poReceiveService.STATUS_CANCEL)
                .like(StringUtil.isNotBlank(poReceive.getSupName()), "sup_name", poReceive.getSupName())
                .like(StringUtil.isNotBlank(poReceive.getSupCode()), "sup_code", poReceive.getSupCode())
                .like(StringUtil.isNotBlank(poReceive.getRcvCode()), "rcv_code", poReceive.getRcvCode()));

        for(PoReceiveEntity entity :poReceiveEntityIPage){
            PoItemEntity pi = poItemService.getById(entity.getPiId());
            PoEntity po = poMapper.getPoInfoByPoCode(pi.getPoCode());
            PoItemVO vo = BeanUtil.copy(pi,PoItemVO.class);
            vo.setTemplateType(po.getTemplateType());
            if(pi != null){
                vo.setRcvNum(new BigDecimal(entity.getRcvNum().toString()));
                vo.setHeatCode(entity.getHeatCode());
                vo.setDoRemark(entity.getRemark());
                Integer rcvNumAll = poItemMapper.getRcvAllNumByPiId(entity.getPiId().toString());
                vo.setNotSendNum(pi.getTcNum().add(pi.getFillGoodsNum().subtract(new BigDecimal(rcvNumAll))));
            }

            if(poReceiveService.OUT.equals(entity.getIsOutCheck())){
                Integer unqualifiedNum = poReceiveMapper.getUnqualifiedNum(entity.getRcvCode(),pi.getPoCode(),pi.getPoLn(),entity.getHeatCode());
                if(unqualifiedNum==null){
                    vo.setUnqualifiedNum(0);
                } else {
                    vo.setUnqualifiedNum(unqualifiedNum);
                }
                vo.setIsOutCheck(poReceiveService.OUT);
            } else {
                vo.setUnqualifiedNum(0);
                vo.setIsOutCheck(poReceiveService.INNER);
            }
            voList.add(vo);
        }
        return R.data(voList);
    }

    /**
     * 创建送货单
     */
    @PostMapping("/create")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "创建送货单", notes = "传入poReceive")
    public R save(@Valid @RequestBody List<PoReceiveDTO> poReceiveList) {
        //分001  002 组织
        List<PoReceiveDTO> PoReceiveDTO1=new ArrayList<>();
        List<PoReceiveDTO> PoReceiveDTO2=new ArrayList<>();

        for (PoReceiveDTO poReceiveDTO:poReceiveList) {
            if (StringUtil.isEmpty(poReceiveDTO.getOrgCode())||"001".equals(poReceiveDTO.getOrgCode())){
                PoReceiveDTO1.add(poReceiveDTO);
            }else{
                PoReceiveDTO2.add(poReceiveDTO);
            }

        }
        R asR1 = poReceiveService.createAsR(PoReceiveDTO1);
        R asR2 = poReceiveService.createAsR(PoReceiveDTO2);
        String data1 = (String) asR1.getData();
        String data2 = (String) asR2.getData();
        String rcvnum="";
        if(StringUtil.isEmpty(data1)){
            rcvnum=data2;
        }else if(StringUtil.isEmpty(data2)){
            rcvnum=data1;
        }else{
            rcvnum=data1+","+data2;
        }
        return R.data(rcvnum);
    }

    /**
     * 校验送货单行是否可选
     */
    @PostMapping("/checkPO")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "创建送货单", notes = "传入poReceive")
    public R checkPO(@Valid @RequestBody List<PoReceiveDTO> poReceiveList) {
        return poReceiveService.checkPO(poReceiveList);
    }


    /**
     * 创建送货单
     */
    @PostMapping("/createFromESB")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "创建送货单", notes = "传入poReceive")
    public R createFromESB(@Valid @RequestBody List<PoReceiveDTO> poReceiveList) {

        for (PoReceiveDTO poReceiveDTO:poReceiveList) {
            PoItemDTO poItemByPOCode = poItemMapper.getPOItemByPOCode(poReceiveDTO.getPoCode(), poReceiveDTO.getPoLn());
            poReceiveDTO.setPiId(poItemByPOCode.getId());
            PoEntity po = poMapper.getPoInfoByPoCode(poItemByPOCode.getPoCode());
            poReceiveDTO.setTemplateType(po.getTemplateType());
        }
        R asR = poReceiveService.createAsR(poReceiveList);

        if (200==asR.getCode()){
            //直接关闭订单
            String data = (String) asR.getData();
            poItemMapper.closedofromESB(data);
        }



        return asR;
    }

    /**
     * 补打送货单
     */
    @PostMapping("/reCreate")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "补打送货单", notes = "传入poReceive")
    public
    R reCreate(@Valid @RequestBody List<PoReceiveDTO> poReceiveList) {

        /*//分001  002 组织
        List<PoReceiveDTO> PoReceiveDTO1=new ArrayList<>();
        List<PoReceiveDTO> PoReceiveDTO2=new ArrayList<>();

        for (PoReceiveDTO poReceiveDTO:poReceiveList) {
            if (StringUtil.isEmpty(poReceiveDTO.getOrgCode())||"001".equals(poReceiveDTO.getOrgCode())){
                PoReceiveDTO1.add(poReceiveDTO);
            }else{
                PoReceiveDTO2.add(poReceiveDTO);
            }

        }
        R asR1 =null;
        R asR2=null;
        if(PoReceiveDTO1.size()>0){
            asR1 = poReceiveService.reCreateAsR(PoReceiveDTO1);
        }
        if (PoReceiveDTO2.size()>0){
            asR2 = poReceiveService.reCreateAsR(PoReceiveDTO2);
        }

        HashMap<String,String> data1 = null;
        HashMap<String,String> data2 = null;
        if(asR1!=null){
            data1= (HashMap) asR1.getData();
        }
        if(asR2!=null){
            data2= (HashMap) asR2.getData();
        }
        String rcvnum="";
        if(data1==null &&data2!=null){
            rcvnum= data2.get("rcvCode");
        }else if(data2==null &&data1!=null){
            rcvnum= data1.get("rcvCode");;
        }else{
            rcvnum=data1.get("rcvCode")+","+data2.get("rcvCode");
        }

        Map<String, String> retMap = Maps.newHashMap();
        retMap.put("rcvCode", rcvnum);
        return R.data(retMap);*/
        return poReceiveService.reCreateAsR(poReceiveList);
    }

    /**
     * 作废送货单
     */
    @PostMapping("/doCancel")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "作废送货单", notes = "传入poReceive")
    public
    R doCancel(@Valid @RequestBody List<PoReceiveDTO> poReceiveList) {
        return poReceiveService.doCancel(poReceiveList);
    }

    /**
     * 关闭送货单
     */
    @PostMapping("/doClose")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "关闭送货单", notes = "传入poReceive")
    public
    R doClose(@Valid @RequestBody List<PoReceiveDTO> poReceiveList) {
        return poReceiveService.doClose(poReceiveList);
    }

    /**
     * 更改送货单物流信息
     */
    @PostMapping("/doBusiness")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "更改送货单物流信息", notes = "传入poReceive")
    public
    R doBusiness(@Valid @RequestBody List<PoReceiveDTO> poReceiveList) {
        return poReceiveService.doBusiness(poReceiveList);
    }

    /**
     * 恢复送货单状态
     */
    @PostMapping("/doRecovery")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "恢复送货单状态", notes = "传入poReceive")
    public
    R doRecovery(@Valid @RequestBody List<PoReceiveDTO> poReceiveList) {
        return poReceiveService.doRecovery(poReceiveList);
    }

    /**
     * 导出
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "outPrReportFormsDTO")
    public void export(PoReceiveDTO poReceiveDTO, HttpServletResponse response) {
        poReceiveService.export(poReceiveDTO, response);
    }

    /**
     * 将送货单设为外检状态
     */
    @PostMapping("/setOut")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "恢复送货单状态", notes = "传入poReceive")
    public
    R setOut(@Valid @RequestBody List<PoReceiveDTO> poReceiveList) {
        return poReceiveService.setOut(poReceiveList);
    }



}
