package org.springblade.modules.ap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import io.swagger.models.auth.In;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.ap.dto.*;
import org.springblade.modules.ap.entity.ApRcvEntity;
import org.springblade.modules.ap.entity.ApRcvReqEntity;
import org.springblade.modules.ap.mapper.ApRcvMapper;
import org.springblade.modules.ap.service.IApRcvService;
import org.springblade.modules.ap.service.IApService;
import org.springblade.modules.ap.vo.ApRcvVO;
import org.springblade.modules.ncr.entity.NcrEntity;
import org.springblade.modules.ncr.service.INcrService;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
public class ApRcvServiceImpl extends BaseServiceImpl<ApRcvMapper, ApRcvEntity> implements IApRcvService {


    @Autowired
    IParamService paramService;
    @Autowired
    IApService apService;
    @Autowired
    @Lazy
    INcrService ncrService;

    @Override
    public QueryWrapper<ApRcvEntity> getQueryWrapper(ApReq apReq) {
        QueryWrapper<ApRcvEntity> queryWrapper = Condition.getQueryWrapper(new ApRcvEntity()).orderByDesc("rcv_code");
        if(!StringUtil.isEmpty(apReq.getSupName())){
            queryWrapper.like("sup_name", apReq.getSupName());
        }
        if(!StringUtil.isEmpty(apReq.getRcvCode())){
            queryWrapper.like("rcv_code", apReq.getRcvCode());
        }
        if(!StringUtil.isEmpty(apReq.getStatus()) && !apReq.getStatus().equals(STATUS_ALL)){
            queryWrapper.eq("status", apReq.getStatus());
        }
        queryWrapper.eq("type", "KK");
        return queryWrapper;
    }

    @Override
    public IPage<ApRcvEntity> getPage(IPage<ApRcvEntity> page, ApReq apReq) {
        return this.baseMapper.getPage(page, apReq);
    }

    @Override
    public IPage<ApRcvReqEntity> getVmiPage(IPage<ApRcvEntity> page, ApReq apReq) {

        IPage<ApRcvReqEntity> apRcvReqEntityIPage = this.baseMapper.getVmiPage(page, apReq);

        for(ApRcvReqEntity rcv :apRcvReqEntityIPage.getRecords()){
            rcv.setId(Long.valueOf(rcv.getReqId()));
            rcv.setRcvCode(rcv.getReqRcvCode());
            rcv.setRcvLn(rcv.getReqRcvLn()==null?null:Integer.valueOf(rcv.getReqRcvLn()));
            rcv.setPoCode(rcv.getReqPoCode());
            rcv.setPoLn(rcv.getReqPoLn()==null?null:Integer.valueOf(rcv.getReqPoLn()));
            rcv.setRcvActualQty(rcv.getReqRcvNum()==null?null:new BigDecimal(rcv.getReqRcvNum()));
            rcv.setVmiContract(rcv.getVmiContractNew());
            rcv.setVmiStatus(rcv.getVmiStatusNew());

            rcv.setAccumRecQty(new BigDecimal(rcv.getReqAccumRecQty()));
            rcv.setPrice(new BigDecimal(rcv.getTaxPricePo()).divide(rcv.getTaxRate().add(new BigDecimal("1")),6));
            rcv.setTaxPrice(new BigDecimal(rcv.getTaxPricePo()));

            if(rcv.getReqRcvNum()!=null && rcv.getTaxPrice()!=null) {
                //taxprice也保留两位小数
                //BigDecimal taxprice= rcv.getTaxPrice().setScale(2, RoundingMode.HALF_UP);
                rcv.setTaxSubTotal(new BigDecimal(rcv.getReqRcvNum()).multiply(rcv.getTaxPrice()).setScale(2, RoundingMode.HALF_UP));
            }
        }

        return apRcvReqEntityIPage;

    }

    @Override
    public int getListCount() {
        String userRoleId = paramService.getValue("purch_place.role_id");
        String deputyManagerRoleId = paramService.getValue("purch_deputy_manager.role_id");

        BladeUser user = AuthUtil.getUser();
        if (StringUtil.containsAny(user.getRoleId(), userRoleId)) {
            //采购员--待审核
            return apService.getCountByStatus(IApService.STATUS_WAITE);
        } else if (StringUtil.containsAny(user.getRoleId(), deputyManagerRoleId)) {
            //副经理--待审核
            return apService.getCountByStatus(IApService.STATUS_WAITE);
        } else {
            return 0;
        }
    }

    @Override
    public IPage<ApRcvEntity> kkList(IPage<ApRcvEntity> page, ApReq apReq) {
        QueryWrapper<ApRcvEntity> queryWrapper = getQueryWrapper(apReq);
        return page(page, queryWrapper);
    }

    @Override
    public int getKKListCount() {
        QueryWrapper<ApRcvEntity> queryWrapper = Condition.getQueryWrapper(new ApRcvEntity());
        String userRoleId = paramService.getValue("purch_place.role_id");
        String deputyManagerRoleId = paramService.getValue("purch_deputy_manager.role_id");
        BladeUser user = AuthUtil.getUser();
        if (StringUtil.containsAny(user.getRoleId(), userRoleId)) {
            //采购员--开立、审核拒绝、已对账
            queryWrapper.in("status", STATUS_INIT, STATUS_BACK, STATUS_AGREE );
        } else if (StringUtil.containsAny(user.getRoleId(), deputyManagerRoleId)) {
            //副经理--待审核
            queryWrapper.in("status", STATUS_WAITE);
        } else {
            return 0;
        }
        return count(queryWrapper);
    }

    @Override
    public List<ApRcvEntity> getList(ApReq apReq) {
        return this.baseMapper.getList(apReq);
    }

    @Override
    public List<ApRcvReqEntity> getVmiList(ApReq apReq) {
        return this.baseMapper.getVmiList(apReq);
    }

    @Override
    public String getProNoByApIdOrBillId(Long id, String type) {
        return this.baseMapper.getProNoByApId(id, type);
    }

    @Override
    public boolean audit(ApReq apReq) {
        ApRcvEntity apRcvEntity = getById(apReq.getId());
        apRcvEntity.setStatus(apReq.getStatus());
        if(!StringUtil.isEmpty(apReq.getBackReason())){
            apRcvEntity.setBackReason(apReq.getBackReason());
        }
        return updateById(apRcvEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean auditBatch(List<ApReq> apReqs) {
        apReqs.forEach(this::audit);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updates(ApReq apReq) {
        List<ApRcvVO> list = apReq.getApRcvVos();
        List<ApRcvEntity> apRcvEntityList = new ArrayList<>();
        list.forEach(temp->{
            ApRcvEntity apRcvEntity = getById(temp.getId());
            apRcvEntity.setStatus(STATUS_PASS);
            apRcvEntityList.add(apRcvEntity);
        });
        return saveBatch(apRcvEntityList);
    }

    @Override
    public List<Map<String, Object>> countList(ApReq apReq) {
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_INIT);
            put("title", "开立");
            put("count", countByStatus(STATUS_INIT, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_WAITE);
            put("title", "待审核");
            put("count", countByStatus(STATUS_WAITE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_BACK);
            put("title", "审核拒绝");
            put("count", countByStatus(STATUS_BACK, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_BUCKLE);
            put("title", "待扣款");
            put("count", countByStatus(STATUS_BUCKLE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_AGREE);
            put("title", "已对账");
            put("count", countByStatus(STATUS_AGREE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_PASS);
            put("title", "已扣款");
            put("count", countByStatus(STATUS_PASS, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_ALL);
            put("title", "全部");
            put("count", list(getQueryWrapper(new ApReq())).size());
        }});
        return result;
    }

    @Override
    public void vmiExport(HttpServletResponse response,String selectionIds, ApReq apReq) {
        if (StringUtil.isNotBlank(selectionIds)) {
            apReq.setSelectionIds(selectionIds);
        }

        List<ApRcvReqEntity> list  = this.baseMapper.getVmiList(apReq);

        if (list == null) {
            throw new RuntimeException("暂无数据");
        } else {
            for (ApRcvReqEntity rcv : list) {
                rcv.setVmiStatus(rcv.getVmiStatusNew());
                rcv.setVmiContract(rcv.getVmiContractNew());
                rcv.setRcvCode(rcv.getReqRcvCode());
                rcv.setPoLn(rcv.getReqPoLn() == null ? null : Integer.valueOf(rcv.getReqPoLn()));
                rcv.setRcvLn(rcv.getReqRcvLn() == null ? null : Integer.valueOf(rcv.getReqRcvLn()));
                rcv.setPoCode(rcv.getReqPoCode());
                rcv.setRcvActualQty(rcv.getReqRcvNum() == null ? null : new BigDecimal(rcv.getReqRcvNum()));
                rcv.setTaxPrice(rcv.getTaxPrice() == null ? null : rcv.getTaxPrice().setScale(2, RoundingMode.HALF_UP));
                rcv.setAccumRecQty(new BigDecimal(rcv.getReqAccumRecQty()));

                rcv.setPrice(new BigDecimal(rcv.getTaxPricePo()).divide(rcv.getTaxRate().add(new BigDecimal("1")),6));
                rcv.setTaxPrice(new BigDecimal(rcv.getTaxPricePo()));

                if(rcv.getReqRcvNum()!=null && rcv.getTaxPrice()!=null) {
                    //taxprice也保留两位小数
                    //BigDecimal taxprice= rcv.getTaxPrice().setScale(2, RoundingMode.HALF_UP);
                    rcv.setTaxSubTotal(new BigDecimal(rcv.getReqRcvNum()).multiply(rcv.getTaxPrice()).setScale(2, RoundingMode.HALF_UP));
                }
            }
            List<ApRcvExcelDTO> excelList = Lists.newArrayList();
            for (ApRcvEntity apRcvEntity : list) {
                ApRcvExcelDTO apRcvExcelDTO = BeanUtil.copy(apRcvEntity, ApRcvExcelDTO.class);
                excelList.add(apRcvExcelDTO);
            }
            ExcelUtils.defaultExport(excelList, ApRcvExcelDTO.class, "VMI待对账列表" + DateUtil.formatDate(new Date()), response);
        }

    }

    @Override
    public void export(HttpServletResponse response, String selectionIds, ApReq apReq) {
        List<ApRcvEntity> list = new ArrayList<>();
        if (StringUtil.isNotBlank(selectionIds)) {
            String[] ids = selectionIds.split(",");
            list = baseMapper.selectByIdList(ids);
        } else {
            list = this.baseMapper.getList(apReq);
        }
        if (list == null) {
            throw new RuntimeException("暂无数据,导出失败");
        }
        ArrayList<ApRcvExcelDTO> excel = Lists.newArrayList();
        for (ApRcvEntity apRcvEntity : list) {
            ApRcvExcelDTO apRcvExcelDTO = BeanUtil.copy(apRcvEntity, ApRcvExcelDTO.class);
            excel.add(apRcvExcelDTO);
        }
        ExcelUtils.defaultExport(excel, ApRcvExcelDTO.class, "待对账列表" + DateUtil.formatDate(new Date()), response);
    }


    /**
     * 根据状态统计数量
     *
     * @param status String
     * @return int
     */
    private int countByStatus(Integer status, String supCode) {
        QueryWrapper<ApRcvEntity> queryWrapper = Wrappers.<ApRcvEntity>query().in("status", status);
        if (!StringUtil.isEmpty(supCode)) {
            queryWrapper.like("sup_code", supCode);
        }
        return count(queryWrapper);
    }

    /**
     * 扣款编号规则：DP+yyyy+mm+dd+三个流水码
     * @return
     */
    @Override
    public
    String genCode(String type){
        Date start = WillDateUtil.getTodayStart();
        Date end   = WillDateUtil.getTodayEnd();
        int  seq   = this.baseMapper.getTodayCount(start, end,type) + 1;
        return "DP" + DateUtil.format(start, "yyyyMMdd") + String.format("%03d", seq);
    }

    @Override
    public List<NcrEntity> ncrDetail(ApRcvEntity aprcv) {
        return ncrService.queryByRcvCode(aprcv.getRcvCode());
    }

    @Override
    public void exportNcr(HttpServletResponse response, ApReq apReq) {
        List<ApRcvEntity> entities = list(getQueryWrapper(apReq));
        List<ApNcrExcelDTO> dtos = Lists.newArrayList();
        entities.forEach(entity ->{
            ApNcrExcelDTO dto = BeanUtil.copy(entity, ApNcrExcelDTO.class);
            dto.setApNcrItemExcelDTOS(getItemDto(entity));
            dtos.add(dto);
        });
        ExcelUtils.defaultExport(dtos, ApNcrExcelDTO.class, "扣款单明细表" + DateUtil.formatDate(new Date()), response);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(String ids) {
        List<Long> idList = Func.toLongList(ids);
        QueryWrapper<ApRcvEntity> queryWrapper = Condition.getQueryWrapper(new ApRcvEntity());
        queryWrapper.in("id", idList);
        list(queryWrapper).forEach(temp ->{
            //若是NCR生成的扣款单则还原NCR
            if(temp.getIsNcr() == 1){
                ncrService.updateByRcvCode(temp.getRcvCode());
            }
        });
        return deleteLogic(idList);
    }

    /**
     * 获取NCR明细DTO
     *
     * @param entity ApRcvEntity
     * @return List
     */
    private List<ApNcrItemExcelDTO> getItemDto(ApRcvEntity entity){
        List<NcrEntity> ncrEntities = ncrService.queryByRcvCode(entity.getRcvCode());
        List<ApNcrItemExcelDTO> dtos = Lists.newArrayList();
        ncrEntities.forEach(temp ->{
            ApNcrItemExcelDTO dto = BeanUtil.copy(temp, ApNcrItemExcelDTO.class);
            dtos.add(dto);
        });
        return dtos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewContract(ApRcvDTO apRcvDTO) {
        List<String> ids = new ArrayList<>(Arrays.asList(apRcvDTO.getRcvIds().split(",")));
        String vmiStatus = apRcvDTO.getVmiStatus();

        ids.stream().forEach(id->{
            this.baseMapper.reviewVmiRcv(id,vmiStatus);
        });
        return true;
    }


    /**
     * VMI历史数据处理
     * @throws Exception
     */
//    @Scheduled(cron = "0 10 18 ? * *")
//    @Transactional(rollbackFor = Exception.class)
    public void handleVmiHistory() throws Exception{
        // 1.找出所有已经对好帐的 结算RCV单号
        List<ApRcvEntity> apRcvEntityList = this.baseMapper.getSettleVmiList();

        // 2.根据供应商 + 需求收货单号的创建时间  ，按照（先到先出）的原则给 需求送货单的累计对货数量赋值
        for(ApRcvEntity item: apRcvEntityList) {
            String supCode = item.getSupCode();
            String itemCode = item.getItemCode();

            // 找到所有需求收货还未对账完毕的单子
            List<ApRcvEntity> reqList = this.baseMapper.getReqVmiList(supCode,itemCode);

            // 结算单号实际已对账数量
            Integer accumRecQty = item.getAccumRecQty().intValue();

            for (ApRcvEntity rcv : reqList) {
                // 如果是负数，直接在PO的已对账基础上加上 退货的数量；无须再去倒推对账
                if(rcv.getRcvActualQty().intValue()<0){
                    this.baseMapper.updateReqRcv(rcv.getId().toString(), rcv.getRcvActualQty().toString());
                    this.baseMapper.updatePoItem(rcv.getPoCode(), rcv.getPoLn().toString(), Math.abs(rcv.getRcvActualQty().intValue()));
                    continue;
                }

                // 需求单号中剩余的 可对帐数量
                Integer reqNum = rcv.getRcvActualQty().intValue() - rcv.getAccumRecQty().intValue();

                if (accumRecQty >= reqNum) { // 如果  结算单号【已对账数量的剩余数量】 >= 可对帐数量
                    this.baseMapper.updateReqRcv(rcv.getId().toString(), reqNum.toString());
                    // 找到需求收货的对应的PO【VMI】，根据累计对账数量 进行倒冲
                    this.baseMapper.updatePoItem(rcv.getPoCode(), rcv.getPoLn().toString(), reqNum);

                    this.baseMapper.insertReqSettle(item.getRcvCode(),item.getRcvLn().toString(),rcv.getRcvCode(),rcv.getRcvLn().toString(),rcv.getPoCode(),rcv.getPoLn().toString(),reqNum.toString(),reqNum.toString());

                    // 已对账数量的剩余数量
                    accumRecQty = accumRecQty - reqNum ;

                } else { // 如果  结算单号【已对账数量的剩余数量】 < 可对帐数量

                    this.baseMapper.updateReqRcv(rcv.getId().toString(), accumRecQty.toString());
                    // 找到需求收货的对应的PO【VMI】，根据累计对账数量 进行倒冲
                    this.baseMapper.updatePoItem(rcv.getPoCode(), rcv.getPoLn().toString(), accumRecQty);

                    this.baseMapper.insertReqSettle(item.getRcvCode(),item.getRcvLn().toString(),rcv.getRcvCode(),rcv.getRcvLn().toString(),rcv.getPoCode(),rcv.getPoLn().toString(),accumRecQty.toString(),reqNum.toString());

                    break; // 跳出内循环，进行下一次外循环
                }
            }
        }

        this.handleVmiNow();
    }

    public void handleVmiNow() throws Exception{
        // 1.找出所有 还未对好帐的 结算RCV单号
        List<ApRcvEntity> apRcvEntityList = this.baseMapper.getSettleVmiListNow();

        // 2.根据供应商 + 需求收货单号的创建时间  ，按照（先到先出）的原则给 需求送货单的累计对货数量赋值
        for(ApRcvEntity item: apRcvEntityList) {
            String supCode = item.getSupCode();
            String itemCode = item.getItemCode();

            // 找到所有需求收货还未对账完毕的单子
            List<ApRcvEntity> reqList = this.baseMapper.getReqVmiList(supCode,itemCode);

            // 结算单号 需要对账数量
            Integer rcvActualQty = item.getRcvActualQty().intValue();

            for (ApRcvEntity rcv : reqList) {
                // 如果是负数，直接在PO的已对账基础上加上 退货的数量；无须再去倒推对账
                if(rcv.getRcvActualQty().intValue()<0){
                    this.baseMapper.updateReqRcv(rcv.getId().toString(), rcv.getRcvActualQty().toString());
                    this.baseMapper.updatePoItem(rcv.getPoCode(), rcv.getPoLn().toString(), Math.abs(rcv.getRcvActualQty().intValue()));
                    continue;
                }

                // 需求单号中剩余的 可对帐数量
                Integer reqNum = rcv.getRcvActualQty().intValue() - rcv.getAccumRecQty().intValue();

                if (rcvActualQty >= reqNum) { // 如果  结算单号【需对账数量的剩余数量】 >= 可对帐数量
                    this.baseMapper.updateReqRcv(rcv.getId().toString(), reqNum.toString());
                    // 找到需求收货的对应的PO【VMI】，根据累计对账数量 进行倒冲
                    this.baseMapper.updatePoItem(rcv.getPoCode(), rcv.getPoLn().toString(), reqNum);

                    this.baseMapper.insertReqSettle(item.getRcvCode(),item.getRcvLn().toString(),rcv.getRcvCode(),rcv.getRcvLn().toString(),rcv.getPoCode(),rcv.getPoLn().toString(),reqNum.toString(),"0");

                    // 已对账数量的剩余数量
                    rcvActualQty = rcvActualQty - reqNum ;

                } else { // 如果  结算单号【已对账数量的剩余数量】 < 可对帐数量

                    this.baseMapper.updateReqRcv(rcv.getId().toString(), rcvActualQty.toString());
                    // 找到需求收货的对应的PO【VMI】，根据累计对账数量 进行倒冲
                    this.baseMapper.updatePoItem(rcv.getPoCode(), rcv.getPoLn().toString(), rcvActualQty);

                    this.baseMapper.insertReqSettle(item.getRcvCode(),item.getRcvLn().toString(),rcv.getRcvCode(),rcv.getRcvLn().toString(),rcv.getPoCode(),rcv.getPoLn().toString(),rcvActualQty.toString(),"0");
                    break; // 跳出内循环，进行下一次外循环
                }
            }
        }
    }


    @Override
    public List<ApRcvVO> getDetailOfVmi(String rcvIds) {

        List<ApRcvVO> apRcvVOS = this.baseMapper.getListOfVmi(rcvIds);

        for(ApRcvVO rcv :apRcvVOS){

            rcv.setVmiContract(rcv.getVmiContractNew());
            rcv.setVmiStatus(rcv.getVmiStatusNew());
            rcv.setPoCode(rcv.getReqPoCode());
            rcv.setPoLn(rcv.getReqPoLn()==null?null:Integer.valueOf(rcv.getReqRcvLn()));
            rcv.setRcvLn(rcv.getReqRcvLn()==null?null:Integer.valueOf(rcv.getReqRcvLn()));
            rcv.setRcvCode(rcv.getReqRcvCode());
            rcv.setRcvActualQty(rcv.getReqRcvNum()==null?null:new BigDecimal(rcv.getReqRcvNum()));
            rcv.setAccumRecQty(rcv.getReqAccumRecQty()==null?null:new BigDecimal(rcv.getReqAccumRecQty()));

            if(rcv.getReqRcvNum()!=null && rcv.getTaxPrice()!=null) {
                rcv.setTaxSubTotal(new BigDecimal(rcv.getReqRcvNum()).multiply(rcv.getTaxPrice()).setScale(2, RoundingMode.HALF_UP));
            }
        }

        return apRcvVOS;
    }

}
