package org.springblade.modules.ap.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import org.apache.poi.ss.usermodel.Workbook;
import org.springblade.common.config.AtwSrmConfiguration;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.common.utils.WillHttpUtil;
import org.springblade.common.utils.WillU9Util;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.ap.dto.ApReq;
import org.springblade.modules.ap.dto.SubReq;
import org.springblade.modules.ap.entity.*;
import org.springblade.modules.ap.mapper.ApMapper;
import org.springblade.modules.ap.mapper.ApRcvMapper;
import org.springblade.modules.ap.service.*;
import org.springblade.modules.ap.vo.ApRcvVO;
import org.springblade.modules.ap.wrapper.ApRcvWrapper;
import org.springblade.modules.finance.dto.PrepayOrderItemDTO;
import org.springblade.modules.finance.entity.PrepayOrderEntity;
import org.springblade.modules.finance.entity.PrepayOrderItemEntity;
import org.springblade.modules.finance.service.IPrepayOrderItemService;
import org.springblade.modules.finance.service.IPrepayOrderService;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.service.IPoService;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springblade.modules.system.entity.AuditRecordEntity;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.service.IAuditRecordService;
import org.springblade.modules.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务实现类
 * @author Will
 */
@Service
public
class ApServiceImpl extends BaseServiceImpl<ApMapper, ApEntity> implements IApService{

    @Autowired
    private IApInvoiceService apInvoiceService;

    @Autowired
    private IApItemService apItemService;

    @Autowired
    @Lazy
    private IApRcvService apRcvService;

    @Autowired
    private ISupplierService supplierService;

    @Autowired
    private IPoService poService;

    @Autowired
    private IPrepayOrderItemService prepayOrderItemService;

    @Autowired
    private IPrepayOrderService prepayOrderService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IAuditRecordService auditRecordService;

    @Autowired
    AtwSrmConfiguration atwSrmConfiguration;

    @Autowired
    IApBillService apBillService;

    @Autowired
    private ApRcvMapper apRcvMapper;


    @Override
    public
    QueryWrapper<ApEntity> getQueryWrapper(ApReq apReq){
        QueryWrapper<ApEntity> queryWrapper = Condition.getQueryWrapper(new ApEntity()).orderByDesc("create_time");
        if(!StringUtil.isEmpty(apReq.getSupName())){
            queryWrapper.like("sup_name", apReq.getSupName());
        }
        if(!StringUtil.isEmpty(apReq.getSupCode())){
            queryWrapper.like("sup_code", apReq.getSupCode());
        }
        if(!StringUtil.isEmpty(apReq.getApCode())){
            queryWrapper.like("ap_code", apReq.getApCode());
        }
        if(!StringUtil.isEmpty(apReq.getCreateTimeStart())){
            queryWrapper.ge("create_time", apReq.getCreateTimeStart());
        }
        if(!StringUtil.isEmpty(apReq.getCreateTimeEnd())){
            queryWrapper.le("create_time", apReq.getCreateTimeEnd());
        }
        if(!StringUtil.isEmpty(apReq.getBackDateStart())){
            queryWrapper.ge("back_date", apReq.getBackDateStart());
        }
        if(!StringUtil.isEmpty(apReq.getBackDateEnd())){
            queryWrapper.le("back_date", apReq.getBackDateEnd());
        }
        if(!StringUtil.isEmpty(apReq.getSrmDateStart())){
            queryWrapper.ge("srm_date", apReq.getSrmDateStart());
        }
        if(!StringUtil.isEmpty(apReq.getSrmDateEnd())){
            queryWrapper.le("srm_date", apReq.getSrmDateEnd());
        }
        if(!StringUtil.isEmpty(apReq.getStatus())){
            queryWrapper.eq("status", apReq.getStatus());
        }
        if(!StringUtil.isEmpty(apReq.getIsVmi())){
            queryWrapper.like("po_code", apReq.getSupName());
        }
        return queryWrapper;
    }

    @Override
    public
    IPage<ApEntity> getPage(IPage<ApEntity> page, ApReq apReq){
        QueryWrapper<ApEntity> queryWrapper = getQueryWrapper(apReq);
        return page(page, queryWrapper);
    }

    @Override
    public
    IPage<ApEntity> getVmiPage(IPage<ApEntity> page, ApReq apReq){
        return this.baseMapper.getApPageWithVmi(page,apReq);
    }

    @Override
    public
    int getCountByStatus(Integer status){
        QueryWrapper<ApEntity> queryWrapper = Condition.getQueryWrapper(new ApEntity());
        queryWrapper.in("status", status);
        return count(queryWrapper);
    }


    @Override
    public
    Map<String, Object> dzDetail(ApEntity ap){
        Map<String, Object> result = new HashMap<>(3);

        ApEntity apEntity = getById(ap.getId());
        result.put("apEntity", apEntity);

        List<ApItemEntity> apItemEntities = apItemService.getApItemEntities(apEntity.getId(), "AP");
        List<ApRcvVO>      apRcvVos       = Lists.newArrayList();
        for(ApItemEntity apItemEntity : apItemEntities){
            ApRcvEntity rcvEntity = apRcvService.getById(apItemEntity.getRcvId());
            if (rcvEntity == null) {
                rcvEntity = apRcvMapper.getSettleVmiInfoByReqId(apItemEntity.getRcvId().toString());
            }
            ApRcvVO     apRcvVo   = ApRcvWrapper.build().entityVO(rcvEntity);
            apRcvVo.setApItemId(apItemEntity.getId().toString());
            apRcvVo.setRecThisQty(apItemEntity.getRecThisQty());
            apRcvVo.setTax(apItemEntity.getTax());
            apRcvVo.setTaxSubTotal(apItemEntity.getTaxSubTotal());
            apRcvVo.setSubTotal(apItemEntity.getSubTotal());
            apRcvVo.setPipAmount(apItemEntity.getPipPrice());
            apRcvVo.setPurAmount(apItemEntity.getPurPrice());
            apRcvVos.add(apRcvVo);
        }
        result.put("apRcvVos", apRcvVos);

        List<ApInvoiceEntity> apInvoiceEntities = apInvoiceService.getApInvoiceEntities(apEntity.getId(), "AP");
        result.put("apInvoiceEntities", apInvoiceEntities);
        return result;
    }

    @Override
    public Map<String, Object> dzDetailVmi(ApEntity ap) {
        Map<String, Object> result = new HashMap<>(3);

        ApEntity apEntity = getById(ap.getId());
        result.put("apEntity", apEntity);

        List<ApItemEntity> apItemEntities = apItemService.getApItemEntities(apEntity.getId(), "AP");
        List<ApRcvVO> apRcvVos = Lists.newArrayList();
        for (ApItemEntity apItemEntity : apItemEntities) {

            List<ApRcvVO> VOs = apRcvMapper.getListOfVmi(apItemEntity.getRcvId().toString());
            ApRcvVO reqVo = VOs.get(0);
            ApRcvEntity rcvEntity = apRcvService.getById(reqVo.getId());
            if (rcvEntity == null) {
                rcvEntity = apRcvMapper.getSettleVmiInfoByReqId(reqVo.getId().toString());
            }

            ApRcvVO apRcvVo = ApRcvWrapper.build().entityVO(rcvEntity);

            // VMI专属
            apRcvVo.setRcvCode(reqVo.getReqRcvCode());
            apRcvVo.setRcvLn(Integer.valueOf(reqVo.getReqRcvLn()));
            apRcvVo.setPoCode(reqVo.getReqPoCode());
            apRcvVo.setPoLn(Integer.valueOf(reqVo.getReqPoLn()));
            apRcvVo.setRcvActualQty(reqVo.getReqRcvNum()==null?null:new BigDecimal(reqVo.getReqRcvNum()));
            apRcvVo.setAccumRecQty(reqVo.getReqAccumRecQty()==null?null:new BigDecimal(reqVo.getReqAccumRecQty()));

            apRcvVo.setRecThisQty(apItemEntity.getRecThisQty());
            apRcvVo.setApItemId(apItemEntity.getId().toString());
            apRcvVo.setTax(apItemEntity.getTax());
            apRcvVo.setTaxSubTotal(apItemEntity.getTaxSubTotal());
            apRcvVo.setSubTotal(apItemEntity.getSubTotal());
            apRcvVo.setPipAmount(apItemEntity.getPipPrice());
            apRcvVo.setPurAmount(apItemEntity.getPurPrice());
            apRcvVo.setPrice(apItemEntity.getPrice());
            apRcvVo.setTaxPrice(apItemEntity.getTaxPrice());
            apRcvVos.add(apRcvVo);
        }

        result.put("apRcvVos", apRcvVos);

        List<ApInvoiceEntity> apInvoiceEntities = apInvoiceService.getApInvoiceEntities(apEntity.getId(), "AP");
        result.put("apInvoiceEntities", apInvoiceEntities);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public
    boolean dzSubmit(ApReq apReq){
        BladeUser user   = SecureUtil.getUser();
        boolean   result = false;

        ApEntity apEntity = new ApEntity();
        apEntity.setApCode(genCode());
        apEntity.setType(TYPE);
        apEntity.setStatus(STATUS_WAITE);
        apEntity.setSupCode(apReq.getSupCode());
        apEntity.setSupName(apReq.getSupName());
        apEntity.setSysLog(user.getUserId() + "--对账提交--AP状态：" + apEntity.getStatus());



        BigDecimal         amount       = new BigDecimal("0");
        BigDecimal         taxAmount    = new BigDecimal("0");
        List<ApItemEntity> itemEntities = Lists.newArrayList();
        List<ApRcvVO>      apRcvVos     = apReq.getApRcvVos();
        for(ApRcvVO apRcvVO : apRcvVos){

            if("KK".equals(apRcvVO.getType())){
                ApItemEntity apItemEntity = new ApItemEntity();
                //AP对账单金额（含税） = 含税单价 * 本次对账数量
                taxAmount = taxAmount.add(apRcvVO.getTaxSubTotal());
                ApRcvEntity apRcvEntity = apRcvService.getById(apRcvVO.getId());
                if (apRcvEntity == null) {
                    apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(apRcvVO.getId().toString());
                }
                apItemEntity.setRcvId(apRcvEntity.getId());
                apItemEntity.setRcvCode(apRcvVO.getRcvCode());
                apItemEntity.setTaxSubTotal(apRcvVO.getTaxSubTotal());
                apItemEntity.setItemCode(apRcvVO.getItemCode());
                apItemEntity.setItemName(apRcvVO.getItemName());
                apItemEntity.setUom(apRcvVO.getUom());
                apItemEntity.setTaxPrice(apRcvVO.getTaxPrice());
                apItemEntity.setTaxRate(apRcvVO.getTaxRate());
                apItemEntity.setRecThisQty(apRcvVO.getRecThisQty());

                apItemEntity.setCreateUser(user.getUserId());
                apItemEntity.setCreateDept((Long) Func.toLongList(user.getDeptId()).iterator().next());
                apItemEntity.setCreateTime(new Date());
                apItemEntity.setUpdateTime(new Date());

                apItemEntity.setPipPrice(new BigDecimal("0"));
                apItemEntity.setPurPrice(apItemEntity.getTaxSubTotal().subtract(new BigDecimal("0")));
                //多组织修改
                apItemEntity.setOrgCode(apRcvEntity.getOrgCode());
                apEntity.setOrgCode(apRcvEntity.getOrgCode());

                itemEntities.add(apItemEntity);
            }else if("DZ".equals(apRcvVO.getType())){

                ApItemEntity apItemEntity = new ApItemEntity();
                //未税小计 = 未税单价 * 本次对账数量
                apItemEntity.setSubTotal(apRcvVO.getPrice().multiply(apRcvVO.getRecThisQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
                //含税小计 = 价税合计 = 含税单价 * 本次对账数量
                apItemEntity.setTaxSubTotal(apRcvVO.getTaxPrice().setScale(2, RoundingMode.HALF_UP).multiply(apRcvVO.getRecThisQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
                //税额 = 价税合计 - 未税金额
                apItemEntity.setTax(apItemEntity.getTaxSubTotal().subtract(apItemEntity.getSubTotal()));
                //AP对账单金额 = 未税单价 * 本次对账数量
                amount = amount.add(apItemEntity.getSubTotal());
                //AP对账单金额（含税） = 含税单价 * 本次对账数量
                taxAmount = taxAmount.add(apItemEntity.getTaxSubTotal());

                //更新RCV累计对账数量
                ApRcvEntity apRcvEntity = apRcvService.getById(apRcvVO.getId());
                if (apRcvEntity == null) {
                    apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(apRcvVO.getId().toString());
                }
                apRcvEntity.setAccumRecQty(apRcvEntity.getAccumRecQty().add(apRcvVO.getRecThisQty()));
                apRcvService.updateById(apRcvEntity);

                apItemEntity.setPipPrice(new BigDecimal("0"));
                apItemEntity.setPurPrice(apItemEntity.getTaxSubTotal().subtract(apItemEntity.getPipPrice()));

                apItemEntity.setRcvId(apRcvEntity.getId());
                apItemEntity.setRcvDate(apRcvVO.getRcvDate());
                apItemEntity.setRcvCode(apRcvVO.getRcvCode());
                apItemEntity.setRcvLn(apRcvVO.getRcvLn());
                apItemEntity.setPoCode(apRcvVO.getPoCode());
                apItemEntity.setPoLn(apRcvVO.getPoLn());
                apItemEntity.setItemCode(apRcvVO.getItemCode());
                apItemEntity.setItemName(apRcvVO.getItemName());
                apItemEntity.setRcvActualQty(apRcvVO.getRcvActualQty());
                apItemEntity.setRecThisQty(apRcvVO.getRecThisQty());
                apItemEntity.setTaxPrice(apRcvVO.getTaxPrice());
                apItemEntity.setTaxRate(apRcvVO.getTaxRate());
                apItemEntity.setUom(apRcvVO.getUom());
                apItemEntity.setPrice(apRcvVO.getPrice());
                apItemEntity.setTaxRate(apRcvVO.getTaxRate());
                apItemEntity.setCreateUser(user.getUserId());
                apItemEntity.setCreateDept((Long) Func.toLongList(user.getDeptId()).iterator().next());
                apItemEntity.setCreateTime(new Date());
                apItemEntity.setUpdateTime(new Date());
                //多组织修改
                apItemEntity.setOrgCode(apRcvEntity.getOrgCode());
                apEntity.setOrgCode(apRcvEntity.getOrgCode());

                itemEntities.add(apItemEntity);
            }
        }
        apEntity.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
        apEntity.setTaxAmount(taxAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        if(taxAmount.compareTo(new BigDecimal("0")) < 0){
            return false;
        }
        //预付冲应付金额
        apEntity.setPipAmount(new BigDecimal("0"));
        //请购金额 = 对账单含税金额 - 预付冲应付金额
        apEntity.setPurAmount(apEntity.getTaxAmount().subtract(apEntity.getPipAmount()));
        result = save(apEntity);

        itemEntities.forEach(temp->{
            temp.setApId(apEntity.getId());
            temp.setApCode(apEntity.getApCode());
            ApRcvEntity apRcvEntity = apRcvService.getById(temp.getRcvId());
            if (apRcvEntity == null) {
                apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(temp.getRcvId().toString());
            }
            if("KK".equals(apRcvEntity.getType())){
                apRcvEntity.setStatus(STATUS_WAITE);
                apRcvService.updateById(apRcvEntity);
            }
        });
        result = apItemService.saveBatch(itemEntities);

        //项目号
        apEntity.setProNo(apRcvService.getProNoByApIdOrBillId(apEntity.getId(), "AP"));
        updateById(apEntity);

        if(!StringUtil.isEmpty(apReq.getInvoiceEntities())){
            List<ApInvoiceEntity> invoiceEntities = apReq.getInvoiceEntities();
            invoiceEntities.forEach(temp->{
                temp.setApId(apEntity.getId());
                temp.setApCode(apEntity.getApCode());
                temp.setCreateUser(user.getUserId());
                temp.setCreateDept((Long) Func.toLongList(user.getDeptId()).iterator().next());
            });
            result = apInvoiceService.saveBatch(invoiceEntities);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean dzSubmitVmi(ApReq apReq) {
        BladeUser user = SecureUtil.getUser();
        boolean result = false;

        ApEntity apEntity = new ApEntity();
        apEntity.setApCode(genCode());
        apEntity.setType(TYPE);
        apEntity.setStatus(STATUS_WAITE);
        apEntity.setSupCode(apReq.getSupCode());
        apEntity.setSupName(apReq.getSupName());
        apEntity.setSysLog(user.getUserId() + "--对账提交--AP状态：" + apEntity.getStatus());

        BigDecimal amount = new BigDecimal("0");
        BigDecimal taxAmount = new BigDecimal("0");
        List<ApItemEntity> itemEntities = Lists.newArrayList();
        List<ApRcvVO> apRcvVos = apReq.getApRcvVos();
        for (ApRcvVO apRcvVO : apRcvVos) {

            if("KK".equals(apRcvVO.getType())){
                ApItemEntity apItemEntity = new ApItemEntity();
                //AP对账单金额（含税） = 含税单价 * 本次对账数量
                taxAmount = taxAmount.add(apRcvVO.getTaxSubTotal());
                ApRcvEntity apRcvEntity = apRcvService.getById(apRcvVO.getId());
                if (apRcvEntity == null) {
                    apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(apRcvVO.getReqId());
                }
                apItemEntity.setRcvId(apRcvEntity.getId());
                apItemEntity.setRcvCode(apRcvVO.getRcvCode());
                apItemEntity.setTaxSubTotal(apRcvVO.getTaxSubTotal());
                apItemEntity.setItemCode(apRcvVO.getItemCode());
                apItemEntity.setItemName(apRcvVO.getItemName());
                apItemEntity.setUom(apRcvVO.getUom());
                apItemEntity.setTaxPrice(apRcvVO.getTaxPrice());
                apItemEntity.setTaxRate(apRcvVO.getTaxRate());
                apItemEntity.setRecThisQty(apRcvVO.getRecThisQty());
                apItemEntity.setCreateUser(user.getUserId());
                apItemEntity.setCreateDept((Long) Func.toLongList(user.getDeptId()).iterator().next());
                apItemEntity.setCreateTime(new Date());
                apItemEntity.setUpdateTime(new Date());
                apItemEntity.setPipPrice(new BigDecimal("0"));
                apItemEntity.setPurPrice(apItemEntity.getTaxSubTotal().subtract(new BigDecimal("0")));
                //多组织修改
                apItemEntity.setOrgCode(apRcvEntity.getOrgCode());
                apEntity.setOrgCode(apRcvEntity.getOrgCode());

                itemEntities.add(apItemEntity);
            }else if("DZ".equals(apRcvVO.getType())){

                ApItemEntity apItemEntity = new ApItemEntity();
                //未税小计 = 未税单价 * 本次对账数量
                apItemEntity.setSubTotal(apRcvVO.getPrice().multiply(apRcvVO.getRecThisQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
                //含税小计 = 价税合计 = 含税单价 * 本次对账数量
                apItemEntity.setTaxSubTotal(apRcvVO.getTaxPrice().multiply(apRcvVO.getRecThisQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
                //税额 = 价税合计 - 未税金额
                apItemEntity.setTax(apItemEntity.getTaxSubTotal().subtract(apItemEntity.getSubTotal()));
                //AP对账单金额 = 未税单价 * 本次对账数量
                amount = amount.add(apItemEntity.getSubTotal());
                //AP对账单金额（含税） = 含税单价 * 本次对账数量
                taxAmount = taxAmount.add(apItemEntity.getTaxSubTotal());

                //更新RCV累计对账数量
                ApRcvEntity apRcvEntity = apRcvService.getById(apRcvVO.getId());
                if (apRcvEntity == null) {
                    apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(apRcvVO.getId().toString());
                }
                apRcvEntity.setAccumRecQty(apRcvEntity.getAccumRecQty().add(apRcvVO.getRecThisQty()));
                apRcvService.updateById(apRcvEntity);

                // vmi的
                apRcvMapper.updateReqRcvAdd(apRcvVO.getReqId(),apRcvVO.getRecThisQty().intValue());

                apItemEntity.setPipPrice(new BigDecimal("0"));
                apItemEntity.setPurPrice(apItemEntity.getTaxSubTotal().subtract(apItemEntity.getPipPrice()));

                apItemEntity.setRcvId(Long.valueOf(apRcvVO.getReqId()));
                apItemEntity.setRcvDate(apRcvVO.getRcvDate());
                apItemEntity.setRcvCode(apRcvVO.getRcvCode());
                apItemEntity.setRcvLn(Integer.valueOf(apRcvVO.getReqRcvLn()));
                apItemEntity.setPoCode(apRcvVO.getReqPoCode());
                apItemEntity.setPoLn(Integer.valueOf(apRcvVO.getReqPoLn()));
                apItemEntity.setItemCode(apRcvVO.getItemCode());
                apItemEntity.setItemName(apRcvVO.getItemName());
                apItemEntity.setRcvActualQty(apRcvVO.getRcvActualQty());
                apItemEntity.setRecThisQty(apRcvVO.getRecThisQty());
                apItemEntity.setTaxPrice(apRcvVO.getTaxPrice());
                apItemEntity.setTaxRate(apRcvVO.getTaxRate());
                apItemEntity.setUom(apRcvVO.getUom());
                apItemEntity.setPrice(apRcvVO.getPrice());
                apItemEntity.setTaxRate(apRcvVO.getTaxRate());
                apItemEntity.setCreateUser(user.getUserId());
                apItemEntity.setCreateDept((Long) Func.toLongList(user.getDeptId()).iterator().next());
                apItemEntity.setCreateTime(new Date());
                apItemEntity.setUpdateTime(new Date());

                //多组织修改
                apItemEntity.setOrgCode(apRcvEntity.getOrgCode());
                apEntity.setOrgCode(apRcvEntity.getOrgCode());

                itemEntities.add(apItemEntity);
            }
        }
        apEntity.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
        apEntity.setTaxAmount(taxAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        if(taxAmount.compareTo(new BigDecimal("0")) < 0){
            return  false;
        }
        //预付冲应付金额
        apEntity.setPipAmount(new BigDecimal("0"));
        //请购金额 = 对账单含税金额 - 预付冲应付金额
        apEntity.setPurAmount(apEntity.getTaxAmount().subtract(apEntity.getPipAmount()));
        result = save(apEntity);

        itemEntities.forEach(temp -> {
            temp.setApId(apEntity.getId());
            temp.setApCode(apEntity.getApCode());
            ApRcvEntity apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(temp.getRcvId().toString());  // VMI对账的里面存的是需求收货单号
            if("KK".equals(apRcvEntity.getType())){
                apRcvEntity.setStatus(STATUS_WAITE);
                apRcvService.updateById(apRcvEntity);
            }
        });
        result = apItemService.saveBatch(itemEntities);

        updateById(apEntity);

        if (!StringUtil.isEmpty(apReq.getInvoiceEntities())) {
            List<ApInvoiceEntity> invoiceEntities = apReq.getInvoiceEntities();
            invoiceEntities.forEach(temp -> {
                temp.setApId(apEntity.getId());
                temp.setApCode(apEntity.getApCode());
                temp.setCreateUser(user.getUserId());
                temp.setCreateDept((Long) Func.toLongList(user.getDeptId()).iterator().next());
            });
            result = apInvoiceService.saveBatch(invoiceEntities);
        }
        return result;
    }


    // TOBEUSED
    public boolean dtSubmitVmi(ApReq apReq) {
        BladeUser user = SecureUtil.getUser();
        boolean result = false;

        ApEntity apEntity = new ApEntity();
        apEntity.setApCode(genCode());
        apEntity.setType(TYPE);
        apEntity.setStatus(STATUS_WAITE);
        apEntity.setSupCode(apReq.getSupCode());
        apEntity.setSupName(apReq.getSupName());
        apEntity.setSysLog(user.getUserId() + "--对账提交--AP状态：" + apEntity.getStatus());

        BigDecimal amount = new BigDecimal("0");
        BigDecimal taxAmount = new BigDecimal("0");
        List<ApItemEntity> itemEntities = Lists.newArrayList();
        List<ApRcvVO> apRcvVos = apReq.getApRcvVos();
        for (ApRcvVO apRcvVO : apRcvVos) {

            if("KK".equals(apRcvVO.getType())){
                ApItemEntity apItemEntity = new ApItemEntity();
                //AP对账单金额（含税） = 含税单价 * 本次对账数量
                taxAmount = taxAmount.add(apRcvVO.getTaxSubTotal());
                ApRcvEntity apRcvEntity = apRcvService.getById(apRcvVO.getId());
                if (apRcvEntity == null) {
                    apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(apRcvVO.getId().toString());
                }
                apItemEntity.setRcvId(apRcvEntity.getId());
                apItemEntity.setRcvCode(apRcvVO.getRcvCode());
                apItemEntity.setTaxSubTotal(apRcvVO.getTaxSubTotal());
                apItemEntity.setItemCode(apRcvVO.getItemCode());
                apItemEntity.setItemName(apRcvVO.getItemName());
                apItemEntity.setUom(apRcvVO.getUom());
                apItemEntity.setTaxPrice(apRcvVO.getTaxPrice());
                apItemEntity.setTaxRate(apRcvVO.getTaxRate());
                apItemEntity.setRecThisQty(apRcvVO.getRecThisQty());
                apItemEntity.setCreateUser(user.getUserId());
                apItemEntity.setCreateDept((Long) Func.toLongList(user.getDeptId()).iterator().next());
                apItemEntity.setCreateTime(new Date());
                apItemEntity.setUpdateTime(new Date());
                apItemEntity.setPipPrice(new BigDecimal("0"));
                apItemEntity.setPurPrice(apItemEntity.getTaxSubTotal().subtract(new BigDecimal("0")));

                itemEntities.add(apItemEntity);
            }else if("DZ".equals(apRcvVO.getType())){

                ApItemEntity apItemEntity = new ApItemEntity();
                //未税小计 = 未税单价 * 本次对账数量
                apItemEntity.setSubTotal(apRcvVO.getPrice().multiply(apRcvVO.getRecThisQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
                //含税小计 = 价税合计 = 含税单价 * 本次对账数量
                apItemEntity.setTaxSubTotal(apRcvVO.getTaxPrice().multiply(apRcvVO.getRecThisQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
                //税额 = 价税合计 - 未税金额
                apItemEntity.setTax(apItemEntity.getTaxSubTotal().subtract(apItemEntity.getSubTotal()));
                //AP对账单金额 = 未税单价 * 本次对账数量
                amount = amount.add(apItemEntity.getSubTotal());
                //AP对账单金额（含税） = 含税单价 * 本次对账数量
                taxAmount = taxAmount.add(apItemEntity.getTaxSubTotal());

                //更新RCV累计对账数量
                ApRcvEntity apRcvEntity = apRcvService.getById(apRcvVO.getId());
                if (apRcvEntity == null) {
                    apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(apRcvVO.getId().toString());
                }
                apRcvEntity.setAccumRecQty(apRcvEntity.getAccumRecQty().add(apRcvVO.getRecThisQty()));
                apRcvService.updateById(apRcvEntity);

                apItemEntity.setPipPrice(new BigDecimal("0"));
                apItemEntity.setPurPrice(apItemEntity.getTaxSubTotal().subtract(apItemEntity.getPipPrice()));

                apItemEntity.setRcvId(Long.valueOf(apRcvVO.getReqId()));
                apItemEntity.setRcvDate(apRcvVO.getRcvDate());
                apItemEntity.setRcvCode(apRcvVO.getRcvCode());
                apItemEntity.setRcvLn(Integer.valueOf(apRcvVO.getReqRcvLn()));
                apItemEntity.setPoCode(apRcvVO.getReqPoCode());
                apItemEntity.setPoLn(Integer.valueOf(apRcvVO.getReqPoLn()));
                apItemEntity.setItemCode(apRcvVO.getItemCode());
                apItemEntity.setItemName(apRcvVO.getItemName());
                apItemEntity.setRcvActualQty(apRcvVO.getRcvActualQty());
                apItemEntity.setRecThisQty(apRcvVO.getRecThisQty());
                apItemEntity.setTaxPrice(apRcvVO.getTaxPrice());
                apItemEntity.setTaxRate(apRcvVO.getTaxRate());
                apItemEntity.setUom(apRcvVO.getUom());
                apItemEntity.setPrice(apRcvVO.getPrice());
                apItemEntity.setTaxRate(apRcvVO.getTaxRate());
                apItemEntity.setCreateUser(user.getUserId());
                apItemEntity.setCreateDept((Long) Func.toLongList(user.getDeptId()).iterator().next());
                apItemEntity.setCreateTime(new Date());
                apItemEntity.setUpdateTime(new Date());
                itemEntities.add(apItemEntity);
            }
        }
        apEntity.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
        apEntity.setTaxAmount(taxAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        if(taxAmount.compareTo(new BigDecimal("0")) < 0){
            return  false;
        }
        //预付冲应付金额
        apEntity.setPipAmount(new BigDecimal("0"));
        //请购金额 = 对账单含税金额 - 预付冲应付金额
        apEntity.setPurAmount(apEntity.getTaxAmount().subtract(apEntity.getPipAmount()));
        result = save(apEntity);

        itemEntities.forEach(temp -> {
            temp.setApId(apEntity.getId());
            temp.setApCode(apEntity.getApCode());
            ApRcvEntity apRcvEntity = apRcvService.getById(temp.getRcvId());
            if (apRcvEntity == null) {
                apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(temp.getRcvId().toString());
            }
            if("KK".equals(apRcvEntity.getType())){
                apRcvEntity.setStatus(STATUS_WAITE);
                apRcvService.updateById(apRcvEntity);
            }
        });
        result = apItemService.saveBatch(itemEntities);

        updateById(apEntity);

        if (!StringUtil.isEmpty(apReq.getInvoiceEntities())) {
            List<ApInvoiceEntity> invoiceEntities = apReq.getInvoiceEntities();
            invoiceEntities.forEach(temp -> {
                temp.setApId(apEntity.getId());
                temp.setApCode(apEntity.getApCode());
                temp.setCreateUser(user.getUserId());
                temp.setCreateDept((Long) Func.toLongList(user.getDeptId()).iterator().next());
            });
            result = apInvoiceService.saveBatch(invoiceEntities);
        }
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public
    boolean dtSubmit(ApReq apReq){
        BladeUser user = SecureUtil.getUser();

        List<ApRcvVO>      apRcvVos     = apReq.getApRcvVos();
        List<ApItemEntity> itemEntities = Lists.newArrayList();

        BigDecimal amount    = new BigDecimal("0");
        BigDecimal taxAmount = new BigDecimal("0");
        ApEntity   apEntity  = getById(apReq.getId());

        List<ApItemEntity> apItemEntities = apItemService.getApItemEntities(apEntity.getId(), "AP");

        for(ApRcvVO apRcvVO : apRcvVos){
            //卡控重复收货单号、行号
            if(checkRcvCodeAndLn(apItemEntities, apRcvVO)){
                throw new RuntimeException("收货单号：" + apRcvVO.getRcvCode() + "行号：" + apRcvVO.getRcvLn() + "已存在");
            }

            if("KK".equals(apRcvVO.getType())){
                ApItemEntity apItemEntity = new ApItemEntity();
                //AP对账单金额（含税） = 含税单价 * 本次对账数量
                taxAmount = taxAmount.add(apRcvVO.getTaxSubTotal());
                ApRcvEntity apRcvEntity = apRcvService.getById(apRcvVO.getId());
                if (apRcvEntity == null) {
                    apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(apRcvVO.getReqId());
                }
                apRcvEntity.setStatus(STATUS_WAITE);
                apRcvService.updateById(apRcvEntity);

                apItemEntity.setApId(apEntity.getId());
                apItemEntity.setApCode(apEntity.getApCode());
                apItemEntity.setTaxSubTotal(apRcvVO.getTaxSubTotal());
                apItemEntity.setRcvId(apRcvEntity.getId());
                apItemEntity.setRcvCode(apRcvVO.getRcvCode());

                apItemEntity.setRcvLn(apRcvVO.getRcvLn());

                apItemEntity.setItemCode(apRcvVO.getItemCode());
                apItemEntity.setItemName(apRcvVO.getItemName());

                apItemEntity.setRecThisQty(apRcvVO.getRecThisQty());
                apItemEntity.setCreateUser(user.getUserId());
                apItemEntity.setCreateDept((Long) Func.toLongList(user.getDeptId()).iterator().next());
                apItemEntity.setCreateTime(new Date());
                apItemEntity.setUpdateTime(new Date());

                apItemEntity.setPipPrice(new BigDecimal("0"));
                apItemEntity.setPurPrice(apItemEntity.getTaxSubTotal().subtract(apItemEntity.getPipPrice()));

                itemEntities.add(apItemEntity);

            }else if("DZ".equals(apRcvVO.getType())){
                ApItemEntity apItemEntity = new ApItemEntity();
                //未税小计 = 未税单价 * 本次对账数量
                apItemEntity.setSubTotal(apRcvVO.getPrice().multiply(apRcvVO.getRecThisQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
                //含税小计 = 价税合计 = 含税单价 * 本次对账数量
                apItemEntity.setTaxSubTotal(apRcvVO.getTaxPrice().multiply(apRcvVO.getRecThisQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
                //税额 = 价税合计 - 未税金额
                apItemEntity.setTax(apItemEntity.getTaxSubTotal().subtract(apItemEntity.getSubTotal()));

                //更新RCV累计对账数量
                ApRcvEntity apRcvEntity = apRcvService.getById(apRcvVO.getId());
                if (apRcvEntity == null) {
                    apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(apRcvVO.getReqId());
                }
                apRcvEntity.setAccumRecQty(apRcvEntity.getAccumRecQty().add(apRcvVO.getRecThisQty()));
                apRcvService.updateById(apRcvEntity);

                //AP对账单金额 = 未税单价 * 本次对账数量
                amount = amount.add(apItemEntity.getSubTotal());
                //AP对账单金额（含税） = 含税单价 * 本次对账数量
                taxAmount = taxAmount.add(apItemEntity.getTaxSubTotal());

                apItemEntity.setPipPrice(new BigDecimal("0"));
                apItemEntity.setPurPrice(apItemEntity.getTaxSubTotal().subtract(apItemEntity.getPipPrice()));

                //绑定AP
                apItemEntity.setApId(apEntity.getId());
                apItemEntity.setApCode(apEntity.getApCode());
                apItemEntity.setRcvId(apRcvEntity.getId());
                apItemEntity.setRcvDate(apRcvVO.getRcvDate());
                apItemEntity.setRcvCode(apRcvVO.getRcvCode());
                apItemEntity.setRcvLn(apRcvVO.getRcvLn());
                apItemEntity.setPoCode(apRcvVO.getPoCode());
                apItemEntity.setPoLn(apRcvVO.getPoLn());
                apItemEntity.setItemCode(apRcvVO.getItemCode());
                apItemEntity.setItemName(apRcvVO.getItemName());
                apItemEntity.setRcvActualQty(apRcvVO.getRcvActualQty());
                apItemEntity.setRecThisQty(apRcvVO.getRecThisQty());
                apItemEntity.setTaxPrice(apRcvVO.getTaxPrice());
                apItemEntity.setTaxRate(apRcvVO.getTaxRate());
                apItemEntity.setUom(apRcvVO.getUom());
                apItemEntity.setPrice(apRcvVO.getPrice());
                apItemEntity.setTaxPrice(apRcvVO.getTaxPrice());
                apItemEntity.setCreateUser(user.getUserId());
                apItemEntity.setCreateDept((Long) Func.toLongList(user.getDeptId()).iterator().next());
                apItemEntity.setCreateTime(new Date());
                apItemEntity.setUpdateTime(new Date());
                itemEntities.add(apItemEntity);
            }
        }
        apEntity.setAmount(apEntity.getAmount().add(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
        apEntity.setTaxAmount(apEntity.getTaxAmount().add(taxAmount).setScale(2, BigDecimal.ROUND_HALF_UP));
        //预付冲应付金额
        apEntity.setPipAmount(new BigDecimal("0"));
        //请购金额 = 对账单含税金额 - 预付冲应付金额
        apEntity.setPurAmount(apEntity.getTaxAmount().subtract(apEntity.getPipAmount()));
        //项目号
        apEntity.setProNo(apRcvService.getProNoByApIdOrBillId(apEntity.getId(), "AP"));
        updateById(apEntity);
        return apItemService.saveBatch(itemEntities);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public
    boolean dtRemove(ApReq apReq){
        ApItemEntity apItemEntity = apItemService.getById(apReq.getApItemId());
        //更新RCV累计数量
        ApRcvEntity apRcvEntity = apRcvService.getById(apItemEntity.getRcvId());
        if (apRcvEntity == null) {
            apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(apItemEntity.getRcvId().toString());
            apRcvMapper.updateReqRcvRemove(apItemEntity.getRcvId().toString(),apItemEntity.getRecThisQty().intValue());
        }

        apRcvEntity.setAccumRecQty(apRcvEntity.getAccumRecQty().subtract(apItemEntity.getRecThisQty()));
        if("KK".equals(apRcvEntity.getType())){
            apRcvEntity.setStatus(IApRcvService.STATUS_BUCKLE);
        }
        apRcvService.updateById(apRcvEntity);
        apItemService.removeById(apReq.getApItemId());
        //更新金额
        ApEntity           apEntity       = getById(apReq.getApId());
        List<ApItemEntity> apItemEntities = apItemService.getApItemEntities(apEntity.getId(), "AP");
        BigDecimal         amount         = new BigDecimal("0");
        BigDecimal         taxAmount      = new BigDecimal("0");
        for(ApItemEntity itemEntity : apItemEntities){
            //AP对账单金额 = 未税单价 * 本次对账数量
            amount = amount.add(itemEntity.getSubTotal());
            //AP对账单金额（含税） = 含税单价 * 本次对账数量
            taxAmount = taxAmount.add(itemEntity.getTaxSubTotal());
        }
        apEntity.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
        apEntity.setTaxAmount(taxAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        //预付冲应付金额
        apEntity.setPipAmount(new BigDecimal("0"));
        //请购金额 = 对账单含税金额 - 预付冲应付金额
        apEntity.setPurAmount(apEntity.getTaxAmount().subtract(apEntity.getPipAmount()));
        //项目号
        apEntity.setProNo(apRcvService.getProNoByApIdOrBillId(apEntity.getId(), "AP"));
        return updateById(apEntity);
    }

    @Override
    public
    boolean subInvoice(ApReq apReq){
        BladeUser             user              = SecureUtil.getUser();
        ApEntity              apEntity          = getById(apReq.getId());
        List<ApInvoiceEntity> apInvoiceEntities = apReq.getInvoiceEntities();
        List<ApInvoiceEntity> removeList        = apInvoiceService.getApInvoiceEntities(apEntity.getId(), "AP");
        removeList.forEach(temp->{
            apInvoiceService.removeById(temp.getId());
        });
        apInvoiceEntities.forEach(temp->{
            temp.setApId(apEntity.getId());
            temp.setApCode(apEntity.getApCode());
            temp.setCreateUser(user.getUserId());
            temp.setCreateDept((Long) Func.toLongList(user.getDeptId()).iterator().next());
        });
        //更改AP状态为待审核
        if(apEntity.getStatus().equals(STATUS_BACK)){
            apEntity.setStatus(STATUS_WAITE);
        }
        updateById(apEntity);

        return apInvoiceService.saveBatch(apInvoiceEntities);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public
    boolean saveAps(ApReq apReq){
        BladeUser      user       = SecureUtil.getUser();
        List<ApEntity> apEntities = Lists.newArrayList();

        //应付单对账金额
        BigDecimal taxAmount = new BigDecimal("0");
        //应付单请款金额
        BigDecimal purAmount = new BigDecimal("0");

        Map<String, BigDecimal> totalMap = new HashMap<>();

        //卡控重复收货单号、行号
        List<ApItemEntity> checkList = Lists.newArrayList();
        apReq.getApEntities().forEach(temp ->{
            ApEntity apEntity = getById(temp.getId());
            List<ApItemEntity> apItemEntities = apItemService.getApItemEntities(apEntity.getId(), "AP");
            checkList.addAll(apItemEntities);
        });

        if(StringUtil.isBlank(checkList.get(0).getPoCode())|| checkList.get(0).getPoCode().indexOf("VMI")<0) {
            checkRcvCodeAndLn(checkList);
        }


        for(ApEntity temp : apReq.getApEntities()){
            ApEntity apEntity = getById(temp.getId());
            apEntity.setStatus(STATUS_1AGREE);
            apEntity.setSrmDate(System.currentTimeMillis()/1000);
            apEntity.setSysLog(apEntity.getSysLog() + "||" + user.getUserId() + "--生成应付--AP状态：" + apEntity.getStatus());
            apEntities.add(apEntity);
            //更新item预计付款日期
            List<ApItemEntity> apItemEntities = apItemService.getApItemEntities(apEntity.getId(), "AP");
            apItemEntities.forEach(apItemEntity->{
                apItemEntity.setPrepayDate(apReq.getPrepayDate());
                apItemService.updateById(apItemEntity);
                if(StringUtil.isBlank(apItemEntity.getPoCode())){
                    return;
                }
                if(totalMap.containsKey(apItemEntity.getPoCode())){
                    totalMap.put(apItemEntity.getPoCode(), totalMap.get(apItemEntity.getPoCode())
                                                                   .add(apItemEntity.getTaxSubTotal()));
                }else{
                    totalMap.put(apItemEntity.getPoCode(), apItemEntity.getTaxSubTotal());
                }
            });
            taxAmount = taxAmount.add(apEntity.getTaxAmount());
            purAmount = purAmount.add(apEntity.getPurAmount());
        }
        //更新对应PO累计应付金额
        for(String key : totalMap.keySet()){
            PoEntity poEntity = poService.getByOrderCode(key);
            if(poEntity != null){
                poEntity.setAccpaidTotal(poEntity.getAccpaidTotal().add(totalMap.get(key)).setScale(2, BigDecimal.ROUND_HALF_UP));
                poService.updateById(poEntity);
            }
        }

        //生成应付单
        ApBillEntity apBillEntity = new ApBillEntity();
        apBillEntity.setBillCode(apBillService.genCode());
        apBillEntity.setTaxAmount(taxAmount);
        apBillEntity.setPurAmount(purAmount);
        apBillEntity.setSupCode(apEntities.get(0).getSupCode());
        apBillEntity.setSupName(apEntities.get(0).getSupName());
        apBillEntity.setType(TYPE);
        apBillEntity.setPrepayDate(apReq.getPrepayDate());
        apBillEntity.setPurBuyer(user.getUserId());
        //到票日期
        apBillEntity.setInvoiceDate(apReq.getInvoiceDate());
        apBillEntity.setSrmDate(System.currentTimeMillis()/1000);
        apBillEntity.setStatus(IApBillService.STATUS_INIT);

        ApEntity apEntity = getById( apReq.getApEntities().get(0).getId());//多组织修改 20220026
        apBillEntity.setOrgCode(apEntity.getOrgCode());
        apBillService.save(apBillEntity);
        //应付单-行
        apEntities.forEach(temp->{
            List<ApItemEntity> apItemEntities = apItemService.getApItemEntities(temp.getId(), "AP");
            for(ApItemEntity item : apItemEntities){
                //绑定apItem
                item.setBillId(apBillEntity.getId());
                item.setBillCode(apBillEntity.getBillCode());
                apItemService.updateById(item);
            }
            List<ApInvoiceEntity> apInvoiceEntities = apInvoiceService.getApInvoiceEntities(temp.getId(), "AP");
            apInvoiceEntities.forEach(invoice->{
                //绑定发票
                invoice.setBillId(apBillEntity.getId());
                invoice.setBillCode(apBillEntity.getBillCode());
                apInvoiceService.updateById(invoice);
            });
        });
        //项目号
        apBillEntity.setProNo(apRcvService.getProNoByApIdOrBillId(apBillEntity.getId(), "BI"));
        //付款日期 = 到票日期 + 最小账期
        int payDate = apItemService.getMinPayDateByBillId(apBillEntity.getId());
        if(apReq.getInvoiceDate() < System.currentTimeMillis()/1000){
            apBillEntity.setPrepayDate(System.currentTimeMillis()/1000);
        }else{
            apBillEntity.setPrepayDate(getPrePayDate(apBillEntity.getInvoiceDate(), payDate));
        }
        apBillService.updateById(apBillEntity);

        return updateBatchById(apEntities);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public
    boolean audit(ApReq apReq) throws IOException{
        BladeUser user     = SecureUtil.getUser();
        ApEntity  apEntity = getById(apReq.getApId());
        apEntity.setStatus(apReq.getStatus());

        AuditRecordEntity auditRecordEntity = new AuditRecordEntity();
        auditRecordEntity.setAuditUser(user.getUserId());
        auditRecordEntity.setAuditStatus(apReq.getStatus());
        auditRecordEntity.setAuditTime(System.currentTimeMillis()/1000);
        auditRecordEntity.setRemark(apReq.getRemark());
        auditRecordEntity.setObjId(apEntity.getId());
        auditRecordEntity.setObjType("AP");
        auditRecordService.save(auditRecordEntity);

        if(apReq.getStatus().equals(STATUS_2AGREE)){
            apEntity.setAudit1(user.getUserId());
            apEntity.setSysLog(apEntity.getSysLog() + "||" + user.getUserId() + "--二级审核--AP状态：" + apEntity.getStatus());
        }else if(apReq.getStatus().equals(STATUS_3AGREE)){
            apEntity.setAudit2(user.getUserId());
            apEntity.setSysLog(apEntity.getSysLog() + "||" + user.getUserId() + "--财务审核--AP状态：" + apEntity.getStatus());
        }else if(apReq.getStatus().equals(STATUS_SUCCESS)){
            Supplier            supplier = supplierService.getByCode(apEntity.getSupCode());
            if(StringUtil.isBlank(supplier.getPurchCode())){
                throw new RuntimeException("该供应商["+supplier.getCode()+"]没有维护采购员。请先维护采购员再试。");
            }
            Map<String, Object> params   = new HashMap<String, Object>();
            params.put("DocType", "01");
            params.put("SupplierCode", apEntity.getSupCode());
            params.put("Currency", "C001");
            params.put("AccrueDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            params.put("SrcBusinessType", "316");
            if ("002".equals(apEntity.getOrgCode())) {
                params.put("SrcOrg", "001");
            }else {
                params.put("SrcOrg", "001");
            }

            params.put("PriorityGrade", "4");
            params.put("Transactor", supplier.getPurchCode());
            params.put("MatchLayer", "3");

            params.put("InvoiceNum", ""); //发票号 多个用/分割
            params.put("InvoiceDate", ""); //发票日期  2020-08-30
            params.put("RecviceInvoiceDate", "" ); //到票日期 2020-08-30
            List<Map<String, Object>> lines          = new ArrayList<>();
            List<ApItemEntity>        apItemEntities = apItemService.getApItemEntities(apEntity.getId(), "AP");
            apItemEntities.forEach(temp->{
                Map<String, Object> line = new HashMap<>();
                line.put("SrcDocNo", StringUtil.containsAny(temp.getRcvCode(),"DP")?"":  temp.getRcvCode());
                line.put("SrcDocLineNo", temp.getRcvLn() == null ? "" : temp.getRcvLn());
                line.put("ItemCode", temp.getItemCode());
                line.put("ItemQty", temp.getRecThisQty().abs());
                line.put("TaxPrice", temp.getTaxPrice().abs());
                //预计付款日期
                long prepayDate = temp.getPrepayDate()*1000;
                line.put("ExpectPayDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date(prepayDate)));
                line.put("TotalMoney", temp.getTaxSubTotal().abs());
                line.put("Tax", WillU9Util.getTaxRateCode(temp.getTaxRate())); // 税率 TS08
                line.put("PurOper", supplier.getPurchCode());
                line.put("PurDept", "07");

                if ("002".equals(temp.getOrgCode())) {
                    line.put("BizOrg","002" );
                }else {
                    line.put("BizOrg","001" );
                }
                lines.add(line);
            });
            params.put("lines", lines);
            //调用U9第三方接口获取u9Code
            ObjectMapper mapper     = new ObjectMapper();
            String       res        = WillHttpUtil.postJson(
                    atwSrmConfiguration.getU9ApiDomain() + "/api/CreateAPBill", mapper.writeValueAsString(params), 600L);
            ObjectNode   objectNode = (ObjectNode) mapper.readTree(res);
            String       code       = mapper.readValue(String.valueOf(objectNode.get("code")), String.class);
            if(!"2000".equals(code)){
                throw new RuntimeException("U9接口返回异常：" + mapper.readValue(String.valueOf(objectNode.get("msg")), String.class));
            }
            String u9Code = mapper.readValue(String.valueOf(objectNode.get("APBillCode")), String.class);
            apEntity.setU9Code(u9Code);
            apEntity.setSysLog(apEntity.getSysLog() + "||" + user.getUserId() + "--审核通过--AP状态：" + apEntity.getStatus());
        }else if(apReq.getStatus().equals(STATUS_REJECT)){
            apEntity.setSysLog(apEntity.getSysLog() + "||" + user.getUserId() + "--审核被拒--AP状态：" + apEntity.getStatus());
        }
        return updateById(apEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public
    boolean yfSave(ApReq apReq){
        List<ApRcvVO> apRcvVos  = apReq.getApRcvVos();
        ApEntity      apEntity  = getById(apReq.getApId());
        BigDecimal    pipAmount = new BigDecimal("0");
        for(ApRcvVO apRcvVo : apRcvVos){
            ApItemEntity apItemEntity = apItemService.getById(apRcvVo.getApItemId());
            apItemEntity.setPipPrice(apRcvVo.getPipAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
            //请款金额
            apItemEntity.setPurPrice(apItemEntity.getTaxSubTotal().subtract(apItemEntity.getPipPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
            pipAmount = pipAmount.add(apRcvVo.getPipAmount());
            apItemService.updateById(apItemEntity);
        }
        apEntity.setPipAmount(pipAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        apEntity.setPurAmount(apEntity.getTaxAmount().subtract(apEntity.getPipAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
        apEntity.setStatus(STATUS_1AGREE);
        return updateById(apEntity);
    }

    @Override
    public
    List<Map<String, Object>> countList(ApReq apReq){
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new ArrayMap<String, Object>(){{
            put("status", 0);
            put("title", "待对账");
            put("count", apRcvService.getList(apReq).size());
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_WAITE);
            put("title", "待审核");
            put("count", countByStatus(STATUS_WAITE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_AGREE);
            put("title", "已对账");
            put("count", countByStatus(STATUS_AGREE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_BACK);
            put("title", "已退回");
            put("count", countByStatus(STATUS_BACK, apReq.getSupCode()));
        }});
        return result;
    }


    @Override
    public
    List<Map<String, Object>> countVmiList(ApReq apReq){
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new ArrayMap<String, Object>(){{
            put("status", 0);
            put("title", "待对账");
            put("count", apRcvService.getVmiList(apReq).size());
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_WAITE);
            put("title", "待审核");
            put("count", countByStatusOfVmi(STATUS_WAITE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_AGREE);
            put("title", "已对账");
            put("count", countByStatusOfVmi(STATUS_AGREE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_BACK);
            put("title", "已退回");
            put("count", countByStatusOfVmi(STATUS_BACK, apReq.getSupCode()));
        }});
        return result;
    }


    @Override
    public
    List<Map<String, Object>> yfCountList(ApReq apReq){
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_1AGREE);
            put("title", "待一级审核");
            put("count", countByStatus(STATUS_1AGREE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_2AGREE);
            put("title", "待二级审核");
            put("count", countByStatus(STATUS_2AGREE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_3AGREE);
            put("title", "财务审核");
            put("count", countByStatus(STATUS_3AGREE, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_SUCCESS);
            put("title", "审批通过");
            put("count", countByStatus(STATUS_SUCCESS, apReq.getSupCode()));
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("status", STATUS_REJECT);
            put("title", "审批被拒");
            put("count", countByStatus(STATUS_REJECT, apReq.getSupCode()));
        }});
        return result;
    }

    @Override
    public
    void export(ApReq apReq, HttpServletResponse response){
        TemplateExportParams params = new TemplateExportParams("templates/ap_detail_template.xls");
        Map<String, Object>       resultMap        = new HashMap<String, Object>();
        List<Map<String, Object>> listMap          = new ArrayList<Map<String, Object>>();
        SimpleDateFormat          simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        ApEntity           apEntity       = getById(apReq.getId());
        List<ApItemEntity> apItemEntities = apItemService.getApItemEntities(apEntity.getId(), "AP");
        resultMap.put("apCode", apEntity.getApCode());
        resultMap.put("taxAmount", apEntity.getTaxAmount());
        resultMap.put("supCode", apEntity.getSupCode());
        resultMap.put("supName", apEntity.getSupName());
        resultMap.put("createTime", simpleDateFormat.format(apEntity.getCreateTime()));
        resultMap.put("type", apEntity.getType());
        resultMap.put("status", fmtStatus(apEntity.getStatus()));

        apItemEntities.forEach(temp->{
            ApRcvEntity rcvEntity = apRcvService.getById(temp.getRcvId());
            if (rcvEntity == null) {
                rcvEntity = apRcvMapper.getSettleVmiInfoByReqId(temp.getRcvId().toString());
            }
            ApRcvVO     apRcvVo   = ApRcvWrapper.build().entityVO(rcvEntity);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("poCode", temp.getPoCode());
            map.put("poLn", temp.getPoLn());
            map.put("rcvCode", temp.getRcvCode());
            map.put("rcvLn", temp.getRcvLn());
            map.put("itemCode", temp.getItemCode());
            map.put("itemName", temp.getItemName());
            map.put("rcvActualQty", temp.getRcvActualQty());
            map.put("recThisQty", temp.getRecThisQty());
            map.put("accumRecQty", apRcvVo.getAccumRecQty());
            map.put("uom", apRcvVo.getUom());
            map.put("price", temp.getPrice());
            map.put("taxRate", apRcvVo.getTaxRate());
            map.put("tax", temp.getTax());
            map.put("taxPrice", temp.getTaxPrice());
            map.put("taxSubTotal", temp.getTaxSubTotal());
            if("DZ".equals(rcvEntity.getType())){
                long rcvDate = temp.getRcvDate()*1000;
                map.put("rcvDate", simpleDateFormat.format(new Date(rcvDate)));
            }else{
                map.put("rcvDate", "");
            }
            listMap.add(map);
        });
        resultMap.put("mapList", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, resultMap);
        try{
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("对账详情" + DateUtil.formatDate(new Date()) + ".xls", "UTF-8"));
            workbook.write(response.getOutputStream());
        }catch(IOException e){
            //throw new NormalException(e.getMessage());
        }
    }

    @Override
    public
    Map<String, Object> print(ApReq apReq){
        Map<String, Object> result   = new HashMap<>();
        ApEntity            apEntity = getById(apReq.getApId());
        Supplier            supplier = supplierService.getByCode(apEntity.getSupCode());

        result.put("u9Code", apEntity.getU9Code());
        result.put("printDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        result.put("department", "采购部");
        result.put("useReason", "货款");
        result.put("supName", apEntity.getSupName());
        result.put("bankName", supplier.getBankName());
        result.put("bankAccountCode", supplier.getBankAccountCode());
        result.put("proNo", apEntity.getProNo());
        result.put("payWay", supplier.getPayWay());
        result.put("purAmount", apEntity.getPurAmount());
        result.put("taxAmount", apEntity.getTaxAmount());
        result.put("poCodes", apItemService.getPoCodesByApId(apEntity.getId(), "AP"));
        result.put("invoiceCodes", apInvoiceService.getInvoiceCodesByApId(apEntity.getId(), "AP"));
        //剩余金额
        BigDecimal         surplus        = new BigDecimal("0");
        List<ApItemEntity> apItemEntities = apItemService.getApItemEntities(apEntity.getId(), "AP");
        for(ApItemEntity apItemEntity : apItemEntities){
            ApRcvEntity apRcvEntity = apRcvService.getById(apItemEntity.getRcvId());
            if (apRcvEntity == null) {
                apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(apItemEntity.getRcvId().toString());
            }
            if("DZ".equals(apRcvEntity.getType())){
                PoEntity poEntity = poService.getByOrderCode(apItemEntity.getPoCode());
                if(poEntity.getPrepaidTotal().compareTo(poEntity.getAccpaidTotal()) > 0){
                    surplus = surplus.add(poEntity.getDocAmount().subtract(poEntity.getPrepaidTotal()));
                }else{
                    surplus = surplus.add(poEntity.getDocAmount().subtract(poEntity.getAccpaidTotal()));
                }
            }
        }
        result.put("surplus", surplus);
        //电子签章
        User audit1     = userService.getById(apEntity.getAudit1());
        User audit2     = userService.getById(apEntity.getAudit2());
        User createUser = userService.getById(apEntity.getCreateUser());
        result.put("audit1", audit1 == null || audit1.getSignatures() == null ? "" : audit1.getSignatures());
        result.put("audit2", audit2 == null || audit2.getSignatures() == null ? "" : audit2.getSignatures());
        result.put("createUser", createUser.getSignatures() == null ? "" : createUser.getSignatures());
        return result;
    }

    @Override
    public
    Map<String, Object> yfRecord(ApReq apReq){
        PoEntity                    poEntity                = poService.getByOrderCode(apReq.getPoCode());
        List<PrepayOrderItemEntity> prepayOrderItemEntities = prepayOrderItemService.getByPoCode(apReq.getPoCode());
        List<PrepayOrderItemDTO>    prepayOrderItemDTOS     = Lists.newArrayList();
        for(PrepayOrderItemEntity prepayOrderItemEntity : prepayOrderItemEntities){
            PrepayOrderEntity  prepayOrderEntity  = prepayOrderService.getById(prepayOrderItemEntity.getPrepayId());
            PrepayOrderItemDTO prepayOrderItemDTO = BeanUtil.copy(prepayOrderItemEntity, PrepayOrderItemDTO.class);
            prepayOrderItemDTO.setPrepayStatus(prepayOrderEntity.getStatus());
            prepayOrderItemDTO.setApplyUserId(prepayOrderEntity.getApplyUserId());
            prepayOrderItemDTOS.add(prepayOrderItemDTO);
        }
        Map<String, Object> result = new HashMap<>(2);
        result.put("poEntity", poEntity);
        result.put("prepayOrderItemEntities", prepayOrderItemDTOS);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public
    boolean subOrBack(SubReq subReq){
        BladeUser user = SecureUtil.getUser();
        if(StringUtil.isEmpty(subReq)){
            return false;
        }
        ApEntity apEntity = getById(subReq.getId());
        apEntity.setStatus(subReq.getStatus());
        List<ApItemEntity> apItemEntities = apItemService.getApItemEntities(apEntity.getId(), "AP");
        //判断金额是否大于0
        BigDecimal taxSubTotal = new BigDecimal("0");
        for(ApItemEntity apItemEntity : apItemEntities){
            taxSubTotal = taxSubTotal.add(apItemEntity.getTaxSubTotal());
        }
        if(taxSubTotal.compareTo(new BigDecimal("0")) < 0){
            return false;
        }
        apItemEntities.forEach(temp->{
            ApRcvEntity apRcvEntity = apRcvService.getById(temp.getRcvId());
            if (apRcvEntity == null) {
                apRcvEntity = apRcvMapper.getSettleVmiInfoByReqId(temp.getRcvId().toString());
            }

            if(STATUS_AGREE.equals(subReq.getStatus()) && "KK".equals(apRcvEntity.getType())){
                apRcvEntity.setStatus(IApRcvService.STATUS_AGREE);
                apRcvService.updateById(apRcvEntity);
            }
        });
        apEntity.setRemark(subReq.getRemark() == null ? "" : subReq.getRemark());
        if(STATUS_BACK.equals(subReq.getStatus())){
            apEntity.setBackBuyer(user.getUserId());
            apEntity.setBackDate(System.currentTimeMillis()/1000);
        }
        if(!StringUtil.isEmpty(apEntity.getSysLog())){
            apEntity.setSysLog(apEntity.getSysLog() + "||" + user.getUserId() + "--采购对账审批--AP状态：" + apEntity.getStatus());
        }else{
            apEntity.setSysLog(user.getUserId() + "--采购对账审批--AP状态：" + apEntity.getStatus());
        }
        return updateById(apEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public
    boolean subOrBackBatch(List<SubReq> subReqs){
        subReqs.forEach(this::subOrBack);
        return true;
    }


    /**
     * 根据到票日期和账期计算付款日期
     * @param payDate Long
     * @param day     int
     * @return
     */
    private
    Long getPrePayDate(Long payDate, int day){
        Date             date   = new Date(payDate*1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar         ca     = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.DATE, day);
        Date resultDate = ca.getTime();
        return resultDate.getTime()/1000;
    }

    /**
     * 根据状态统计数量
     * @param status String
     * @return int
     */
    private
    int countByStatus(Integer status, String supCode){
//        QueryWrapper<ApEntity> queryWrapper = Wrappers.<ApEntity>query().in("status", status);
//        if(!StringUtil.isEmpty(supCode)){
//            queryWrapper.like("sup_code", supCode);
//        }
//        return count(queryWrapper);
        ApReq apReq = new ApReq();
        apReq.setSupCode(supCode);
        apReq.setStatus(status);
        return this.baseMapper.getApCountWithoutVmi(apReq);

    }

    /**
     * 根据状态统计数量 - VMI
     * @param status String
     * @return int
     */
    private
    int countByStatusOfVmi(Integer status, String supCode){
        ApReq apReq = new ApReq();
        apReq.setSupCode(supCode);
        apReq.setStatus(status);
        return this.baseMapper.getApCountWithVmi(apReq);
    }


    /**
     * 生成对账编号规则：MS+yyyy+mm+dd+三个流水码
     * @return String
     */
    private
    String genCode(){
        Date                   start = WillDateUtil.getTodayStart();
        Date                   end   = WillDateUtil.getTodayEnd();
        QueryWrapper<ApEntity> qw    = Wrappers.<ApEntity>query().ge("create_time", start).le("create_time", end);
        int                    seq   = count(qw) + 1;
        return "MS" + DateUtil.format(start, "yyyyMMdd") + String.format("%03d", seq);
    }

    /**
     * 格式化状态
     * @param status Integer
     * @return String
     */
    private
    String fmtStatus(Integer status){
        switch(status){
            case 10:
                return "待审核";
            case 20:
                return "已对账";
            case 30:
                return "已退回";
            case 40:
                return "待一级审核";
            case 50:
                return "待二级审核";
            case 60:
                return "财务审核";
            case 70:
                return "审批通过";
            case 80:
                return "审批被拒";
            default:
                return "";
        }
    }

    /**
     * checkRcvCodeAndLn
     *
     *
     * @param apItemEntities List
     * @param apRcvVO ApRcvVO
     * @return boolean
     */
    private boolean checkRcvCodeAndLn(List<ApItemEntity> apItemEntities, ApRcvVO apRcvVO){
        for (ApItemEntity apItemEntity : apItemEntities) {
            if(apItemEntity.getRcvCode().equals(apRcvVO.getRcvCode()) && apItemEntity.getRcvLn().equals(apRcvVO.getRcvLn())){
                return true;
            }
        }
        return false;
    }

    /**
     *  checkRcvCodeAndLn
     *
     * @param lists List
     */
    private void checkRcvCodeAndLn(List<ApItemEntity> lists){
        for (int i = 0; i < lists.size() - 1; i++) {
            for (int j = lists.size() - 1; j > i; j--) {
                if (lists.get(j).getRcvCode().equals(lists.get(i).getRcvCode()) &&
                    lists.get(j).getRcvLn().equals(lists.get(i).getRcvLn())) {
                    throw new RuntimeException("收货单号：" + lists.get(j).getRcvCode() + "行号：" + lists.get(j).getRcvLn() + "已存在");
                }
            }
        }
    }

}
