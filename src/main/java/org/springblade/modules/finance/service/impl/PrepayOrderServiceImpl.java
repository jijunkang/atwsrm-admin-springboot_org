package org.springblade.modules.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;
import org.springblade.common.config.AtwSrmConfiguration;
import org.springblade.common.dto.CheckDTO;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.common.utils.WillHttpUtil;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.finance.dto.PrepayOrderDTO;
import org.springblade.modules.finance.dto.PrepayOrderItemUpdateDto;
import org.springblade.modules.finance.dto.PrepayOrderUpdateDto;
import org.springblade.modules.finance.entity.PrepayOrderEntity;
import org.springblade.modules.finance.entity.PrepayOrderItemEntity;
import org.springblade.modules.finance.mapper.PrepayOrderMapper;
import org.springblade.modules.finance.service.IPrepayOrderItemService;
import org.springblade.modules.finance.service.IPrepayOrderService;
import org.springblade.modules.finance.vo.PrepayOrderVO;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.po.service.IPoService;
import org.springblade.modules.queue.entity.QueueEmailEntity;
import org.springblade.modules.queue.service.IQueueEmailService;
import org.springblade.modules.supplier.entity.PaywayEntity;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.IPaywayService;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.service.IAuditRecordService;
import org.springblade.modules.system.service.IParamService;
import org.springblade.modules.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务实现类
 * @author Will
 */
@Service
public
class PrepayOrderServiceImpl extends BaseServiceImpl<PrepayOrderMapper, PrepayOrderEntity> implements IPrepayOrderService{

    @Autowired
    @Lazy
    IPoItemService poItemService;

    @Autowired
    @Lazy
    IPoService poService;

    @Autowired
    IPrepayOrderItemService prepayOrderItemService;

    @Autowired
    IAuditRecordService auditRecordService;
    @Autowired
    AtwSrmConfiguration atwSrmConfiguration;

    @Autowired
    ISupplierService supplierService;

    @Autowired
    private IUserService userService;

    @Autowired
    IParamService paramService;
    @Autowired
    private IQueueEmailService queueEmailService;

    @Autowired
    private IPaywayService paywayService;

    @Override
    public
    Wrapper<PrepayOrderEntity> getQueryWrapper(PrepayOrderDTO prepayOrder){
        Wrapper<PrepayOrderEntity> qw = Wrappers.<PrepayOrderEntity>query()
                .like(StringUtils.isNotEmpty(prepayOrder.getSupCode()), "sup_code", prepayOrder.getSupCode())
                .like(StringUtils.isNotEmpty(prepayOrder.getSupName()), "sup_name", prepayOrder.getSupName())
                .ge(StringUtils.isNotEmpty(prepayOrder.getCreateTimeStart()), "create_time", prepayOrder.getCreateTimeStart())
                .le(StringUtils.isNotEmpty(prepayOrder.getCreateTimeEnd()), "create_time", prepayOrder.getCreateTimeEnd())
                .in(StringUtils.isNotEmpty(prepayOrder.getStatuss()), "status", Func.toLongList(prepayOrder.getStatuss()))
                .eq(!StringUtil.isEmpty(prepayOrder.getIsPrint()), "is_print", prepayOrder.getIsPrint());
        return qw;
    }

    @Override
    public
    IPage<PrepayOrderVO> getPage(PrepayOrderDTO prepayOrder, Query query){
        IPage<PrepayOrderEntity> entityPage = page(Condition.getPage(query), getQueryWrapper(prepayOrder));

        // 根据poCode来查找
        if(StringUtils.isNotEmpty(prepayOrder.getPoCode())){
            List<PrepayOrderEntity> prepayOrderEntitiesToRemove = new ArrayList<>();
            for(PrepayOrderEntity entity : entityPage.getRecords()){
                String payCode = entity.getCode();
                Integer count = this.baseMapper.getCountOfPoCode(prepayOrder.getPoCode(),payCode);
                if (count == 0) {
                    prepayOrderEntitiesToRemove.add(entity);
                }
            }
            entityPage.getRecords().removeAll(prepayOrderEntitiesToRemove);
        }

        IPage<PrepayOrderVO>     voPage     = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<PrepayOrderVO>      voList     = Lists.newArrayList();
        for(PrepayOrderEntity entity : entityPage.getRecords()){
            PrepayOrderVO vo = BeanUtil.copy(entity, PrepayOrderVO.class);
            voList.add(vo);
            //  计算平均预付比例
            List<PrepayOrderItemEntity> itemEntityList = prepayOrderItemService.listByPreOrderId(entity.getId());
            if(itemEntityList == null || itemEntityList.isEmpty()){
                vo.setPrepayRate(0.0);
                continue;
            }
            BigDecimal poDocAmount = BigDecimal.ZERO;
            for(PrepayOrderItemEntity prepayItem : itemEntityList){
                PoEntity poEntity = poService.getById(prepayItem.getPoId());
                poDocAmount = poDocAmount.add(poEntity == null ? BigDecimal.ZERO : poEntity.getDocAmount());
            }
            if(poDocAmount .compareTo(BigDecimal.ZERO) == 0){
                vo.setPrepayRate(0.0);
                continue;
            }
            //四舍五入保留两位小数
            double prepayRate = entity.getAmount().divide(poDocAmount, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
            vo.setPrepayRate(prepayRate);
        }
        voPage.setRecords(voList);
        return voPage;
    }


    @Override
    public
    int getListCount(){
        QueryWrapper<PrepayOrderEntity> qw = Condition.getQueryWrapper(new PrepayOrderEntity());

        String userRoleId          = paramService.getValue("purch_place.role_id");
        String deputyManagerRoleId = paramService.getValue("purch_deputy_manager.role_id");
        String managerRoleId       = paramService.getValue("purch_manager.role_id");
        String yanManagerRoleId    = paramService.getValue("yan_manager.role_id");

        BladeUser user = AuthUtil.getUser();
        if(StringUtil.containsAny(user.getRoleId(), userRoleId)){
            //采购员--待审核、已拒绝
            qw.in("status", STATUS_INIT, STATUS_REFUSE);
        }else if(StringUtil.containsAny(user.getRoleId(), deputyManagerRoleId)){
            //副经理--一级审核
            qw.in("status", STATUS_SUBMIT);
        }else if(StringUtil.containsAny(user.getRoleId(), managerRoleId)){
            //经理--二级审核
            qw.in("status", STATUS_1PASS, STATUS_1PASS_R);
        }else if(StringUtil.containsAny(user.getRoleId(), yanManagerRoleId)){
            //严总经理--三级审核
            qw.in("status", STATUS_2PASS);
        }else{
            return 0;
        }
        return count(qw);
    }

    @Override
    public
    List<PrepayOrderEntity> getByPoCode(String poCode){
        QueryWrapper<PrepayOrderEntity> queryWrapper = Condition.getQueryWrapper(new PrepayOrderEntity());
        queryWrapper.eq("po_code", poCode);
        return list(queryWrapper);
    }

    /**
     * 根据PO创建首次预付款
     * @return
     */
    @Override
    public
    PrepayOrderEntity createFirstByPo(PoEntity po){
        PrepayOrderEntity entity = new PrepayOrderEntity();
        entity.setReqPayTime(System.currentTimeMillis()/1000);
        entity.setCode(this.genCode());
        entity.setAmount(po.getDocAmount().multiply(BigDecimal.valueOf(po.getFirstPrepayRate())));
        entity.setSupCode(po.getSupCode());
        entity.setSupName(po.getSupName());
        entity.setProNo(poItemService.connectProNoByPoCode(new HashSet<String>(){{
            add(po.getOrderCode());
        }}));
        entity.setStatus(STATUS_INIT);
        entity.setSysLog("首笔预付请款单.");
        save(entity);

        prepayOrderItemService.createFirst(entity, po);

        return entity;
    }

    /**
     * 生成预付订单编号规则：PAY+yyyy+mm+dd+三个流水码
     * @return
     */
    private
    String genCode(){
        Date start = WillDateUtil.getTodayStart();
        Date end   = WillDateUtil.getTodayEnd();
        int  seq   = this.baseMapper.getTodayCount(start, end) + 1;
        return "PAY" + DateUtil.format(start, "yyyyMMdd") + String.format("%03d", seq);
    }

    /**
     * 根据状态统计数量
     * @return
     */
    @Override
    public
    int countByStatus(String statuss){
        QueryWrapper<PrepayOrderEntity> qw = Wrappers.<PrepayOrderEntity>query().in("status", Func.toLongList(statuss));
        return count(qw);
    }

    /**
     * 批量生成预付单
     * @return
     */
    @Override
    public
    boolean genPrepayorder(List<PrepayOrderItemEntity> preOrderItemEntities){

        if(preOrderItemEntities == null || preOrderItemEntities.isEmpty()){
            return false;
        }
        PoEntity    firstPoEntity = poService.getById(preOrderItemEntities.get(0).getPoId());
        String      supCode       = firstPoEntity.getSupCode();
        String      supName       = firstPoEntity.getSupName();
        Set<String> poCodes       = Sets.newHashSet();
        BigDecimal  amount        = BigDecimal.ZERO;
        int         prepayLn      = 1;

        for(PrepayOrderItemEntity entity : preOrderItemEntities){
            PoEntity poEntity = poService.getById(entity.getPoId());
            if(!Objects.equals(poEntity.getSupCode(), supCode)){
                throw new RuntimeException("请选择同一家供应商," + supCode + " ≠ " + poEntity.getSupCode());
            }
            poCodes.add(poEntity.getOrderCode());
            amount = amount.add(entity.getSubtotal());
        }

        PrepayOrderEntity prepayOrder = new PrepayOrderEntity();
        prepayOrder.setReqPayTime(System.currentTimeMillis()/1000);
        prepayOrder.setCode(this.genCode());
        prepayOrder.setAmount(amount);
        prepayOrder.setSupCode(supCode);
        prepayOrder.setSupName(supName);
        prepayOrder.setProNo(poItemService.connectProNoByPoCode(poCodes));
        prepayOrder.setStatus(STATUS_INIT);
        prepayOrder.setSysLog(AuthUtil.getUserName() + "请款.");
        prepayOrder.setOrgCode(firstPoEntity.getOrgCode());
        save(prepayOrder);

        for(PrepayOrderItemEntity preOrderItem : preOrderItemEntities){
            PoEntity po = poService.getById(preOrderItem.getPoId());
            if(po == null){
                throw new RuntimeException("不存在的poId=" + preOrderItem.getPoId());
            }
            preOrderItem.setPrepayId(prepayOrder.getId());
            preOrderItem.setPrepayCode(prepayOrder.getCode());
            preOrderItem.setPrepayLn(prepayLn++);
            preOrderItem.setIsPoFirst(0);
            preOrderItem.setPrepayRate(preOrderItem.getSubtotal().divide(po.getDocAmount(), 6).doubleValue());
            preOrderItem.setPoCode(po.getOrderCode());
            preOrderItem.setSysLog(AuthUtil.getUserName() + "请款.");
            preOrderItem.setOrgCode(po.getOrgCode());
            prepayOrderItemService.save(preOrderItem);

            po.setPrepaidTotal(po.getPrepaidTotal().add(preOrderItem.getSubtotal()).setScale(2, BigDecimal.ROUND_HALF_UP));
            poService.updateById(po);
        }

        return true;
    }

    @Override
    public
    boolean checkBatch(List<CheckDTO> checkDtos){
        if(checkDtos == null || checkDtos.isEmpty()){
            return false;
        }

        for(CheckDTO checkDTO : checkDtos){
            if(!check(checkDTO)){
                return false;
            }
        }
        return true;
    }

    public
    boolean check(CheckDTO checkDto){
        PrepayOrderEntity prepayOrderEntity = getById(checkDto.getId());
        if(prepayOrderEntity == null){
            System.out.println("id不存在：" + checkDto.getId());
            return false;
        }

        // 提交审核
        if(STATUS_SUBMIT.equals(checkDto.getStatus())){
            //必须填写支付日期
            if(StringUtil.isEmpty(prepayOrderEntity.getReqPayTime())){
                throw new RuntimeException("支付日期为空=" + prepayOrderEntity.getId());
            }
            prepayOrderEntity.setApplyTime(System.currentTimeMillis()/1000);
            prepayOrderEntity.setApplyUserId(AuthUtil.getUserId());
            prepayOrderEntity.setApplyUserCode(AuthUtil.getUserAccount());
        }
        auditRecordService.record(auditRecordService.TYPE_PREPAY, checkDto);
        prepayOrderEntity.setStatus(checkDto.getStatus());
        prepayOrderEntity.setSysLog(
                prepayOrderEntity.getSysLog() + "|" + DateUtil.formatDate(new Date()) + " " + AuthUtil.getNickName() + "审核状态：" +
                checkDto.getStatus() + "," + checkDto.getRemark());

        BladeUser user = SecureUtil.getUser();
        //增加三审核人ID
        if(STATUS_1PASS.equals(checkDto.getStatus()) || STATUS_1PASS_R.equals(checkDto.getStatus())){
            prepayOrderEntity.setAudit1(user.getUserId());
        }else if(STATUS_2PASS.equals(checkDto.getStatus())){
            prepayOrderEntity.setAudit2(user.getUserId());

            // 发送邮件通知
            QueueEmailEntity queueEmailEntity = new QueueEmailEntity();
            queueEmailEntity.setSender(IQueueEmailService.PRE_INTI_SENDER);
            queueEmailEntity.setReceiver(paramService.getValue(IQueueEmailService.PRE_PARAM_KEY));
            queueEmailEntity.setSubject(IQueueEmailService.PRE_SUBJECT_DP);
            queueEmailEntity.setContent(IQueueEmailService.PRE_CONTENT_DP);
            queueEmailEntity.setSendCount(0);
            queueEmailEntity.setStatus(IQueueEmailService.STATUS_INIT);

            queueEmailService.save(queueEmailEntity);
        }

        // 三级审核通过
        if(STATUS_3PASS.equals(checkDto.getStatus())){

            prepayOrderEntity.setAudit3(user.getUserId());

            // 调用U9接口
            pushToU9(prepayOrderEntity);
        }

        return updateById(prepayOrderEntity);
    }

    private
    void pushToU9(PrepayOrderEntity prepayOrderEntity){
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>>   lineList    = Lists.newArrayList();
        List<PrepayOrderItemEntity> ppoItemList = prepayOrderItemService.listByPreOrderId(prepayOrderEntity.getId());
        for(PrepayOrderItemEntity ppoi : ppoItemList){
            Map<String, Object> lineMap = new ArrayMap<String, Object>(){{
                put("SupplierNo", prepayOrderEntity.getSupCode());
                put("SrcDocNo", ppoi.getPoCode());
                put("RFMoneyTotalMoney", ppoi.getSubtotal());

                if ("002".equals(ppoi.getOrgCode())) {
                    put("BizOrg", "002");// 业务组织
                    put("SrcOrg", "002");// 来源组织
                }else {
                    put("BizOrg", "001");// 业务组织
                    put("SrcOrg", "001");// 来源组织
                }

                put("SttlMethod", "02");// 结算方式
                put("PrePayType", "0");// 预付款类型
            }};
            lineList.add(lineMap);
        }

        Map<String, Object> req = new ArrayMap<String, Object>(){{
            put("DocType", "001");// 单据类型
            put("PayRFDate", DateUtil.format(prepayOrderEntity.getCreateTime(), "yyyy.MM.dd"));// 申请日期
            put("ExpectDate", DateUtil.format(new Date(prepayOrderEntity.getReqPayTime()*1000), "yyyy.MM.dd"));// 预付款日期
            put("RequestObjType", "1");// 请款对象
            put("ReqFundUse", "1");// 请款用途
            put("Currency", "C001");// 币种

            if ("002".equals(prepayOrderEntity.getOrgCode())) {
                put("SrcOrg", "002");// 来源组织
                put("SettleOrg", "002");// 结算组织
                put("BizOrg", "002");// 业务组织
            }else {
                put("SrcOrg", "001");// 来源组织
                put("SettleOrg", "001");// 结算组织
                put("BizOrg", "001");// 业务组织
            }

            put("Transactor", prepayOrderEntity.getApplyUserCode());// 业务员
            put("Dept", "07");// 部门
            put("lineList", lineList);//
        }};
        ObjectNode objectNode = null;
        try{
            String res = WillHttpUtil
                    .postJson(atwSrmConfiguration.getU9ApiDomain() + "/api/CustCreatePayReq", mapper.writeValueAsString(req));
            objectNode = (ObjectNode) mapper.readTree(res);
            String code = mapper.readValue(String.valueOf(objectNode.get("code")), String.class);
            if(!"2000".equals(code)){
                throw new RuntimeException("U9接口返回异常：" + mapper.readValue(String.valueOf(objectNode.get("msg")), String.class));
            }
            String u9Code = mapper.readValue(String.valueOf(objectNode.get("RECode")), String.class);
            prepayOrderEntity.setU9Code(u9Code);
        }catch(IOException e){
            e.printStackTrace();
            throw new RuntimeException("解析U9接口返回结果异常");

        }
    }

    @Override
    public
    boolean bizDelete(List<Long> idList){
        if(idList == null || idList.isEmpty()){
            return false;
        }
        for(Long id : idList){
            PrepayOrderEntity entity = getById(id);
            entity.setSysLog(entity.getSysLog() + "|" + DateUtil.formatDate(new Date()) + " " + AuthUtil.getNickName() + "作废。");
            updateById(entity);
            removeById(entity.getId());

            List<PrepayOrderItemEntity> preItemList = prepayOrderItemService.listByPreOrderId(entity.getId());
            for(PrepayOrderItemEntity prepayItem : preItemList){
                PoEntity po = poService.getById(prepayItem.getPoId());
                po.setPrepaidTotal(po.getPrepaidTotal().subtract(prepayItem.getSubtotal()));
                prepayOrderItemService.removeById(prepayItem.getId());
                poService.updateById(po);
            }
        }
        return true;
    }

    /**
     * 业务更新
     * @return
     */
    @Override
    public
    boolean bizUpdate(PrepayOrderUpdateDto ppoDto){
        if(ppoDto == null || ppoDto.getPpoItems() == null || ppoDto.getPpoItems().isEmpty()){
            return false;
        }
        PrepayOrderEntity ppoEntity = getById(ppoDto.getId());
        if(ppoEntity == null){
            return false;
        }
        BigDecimal amount   = BigDecimal.ZERO;
        int        prepayLn = 1;
        for(PrepayOrderItemUpdateDto ppoItemDto : ppoDto.getPpoItems()){
            PoEntity              po = poService.getById(ppoItemDto.getPoId());
            PrepayOrderItemEntity ppoItemEntity;

            if(ppoItemDto.getId() == null){
                ppoItemEntity = new PrepayOrderItemEntity();
                ppoItemEntity.setSubtotal(BigDecimal.ZERO);
            }else{
                ppoItemEntity = prepayOrderItemService.getById(ppoItemDto.getId());
            }

            po.setPrepaidTotal(po.getPrepaidTotal().subtract(ppoItemEntity.getSubtotal()).add(ppoItemDto.getSubtotal()));
            if(po.getPrepaidTotal().compareTo(po.getAccumPrepay()) > 0){
                throw new RuntimeException("订单预付金额不能大于累计预付金额，PO:" + po.getOrderCode());
            }

            ppoItemEntity.setPrepayId(ppoEntity.getId());
            ppoItemEntity.setPrepayCode(ppoEntity.getCode());
            //ppoItemEntity.setIsPoFirst(0);
            ppoItemEntity.setPoId(po.getId());
            ppoItemEntity.setPoCode(po.getOrderCode());
            ppoItemEntity.setPrepayLn(prepayLn++);
            ppoItemEntity.setPrepayRate(ppoItemDto.getSubtotal().divide(po.getDocAmount(), 6).doubleValue());
            ppoItemEntity.setRemark(ppoItemDto.getRemark());
            ppoItemEntity.setSubtotal(ppoItemDto.getSubtotal());
            ppoItemEntity.setSysLog(
                    ppoItemEntity.getSysLog() + "|" + DateUtil.formatDateTime(new Date()) + AuthUtil.getUserName() + "编辑请款。");

            prepayOrderItemService.saveOrUpdate(ppoItemEntity);
            poService.updateById(po);
            amount = amount.add(ppoItemDto.getSubtotal());
        }

        ppoEntity.setReqPayTime(ppoDto.getReqPayTime());
        ppoEntity.setAmount(amount);
        ppoEntity.setStatus(STATUS_INIT);
        ppoEntity.setProNo(ppoDto.getProNo());
        return updateById(ppoEntity);
    }

    @Override
    public
    Map<String, Object> print(PrepayOrderDTO prepayOrder){
        Map<String, Object> result            = new HashMap<>();
        PrepayOrderEntity   prepayOrderEntity = getById(prepayOrder.getId());
        PaywayEntity paywayEntity = paywayService.getBySupCode(prepayOrderEntity.getSupCode());
        Supplier            supplier          = supplierService.getByCode(prepayOrderEntity.getSupCode());

        result.put("orgCode", prepayOrderEntity.getOrgCode()==null?"001":prepayOrderEntity.getOrgCode());
        result.put("u9Code", prepayOrderEntity.getU9Code());
        result.put("printDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        result.put("department", "采购部");
        result.put("supName", prepayOrderEntity.getSupName());
        result.put("bankName", supplier.getBankName());
        result.put("bankAccountCode", supplier.getBankAccountCode());
        result.put("proNo", prepayOrderEntity.getProNo());
        if(StringUtil.isNotBlank(paywayEntity.getTypeName())){
            result.put("payWay", "其他".equals(paywayEntity.getTypeName()) ? paywayEntity.getRemark() : paywayEntity.getTypeName());
        }else{
            result.put("payWay", "");
        }
        result.put("amount", prepayOrderEntity.getAmount());
        result.put("poCodes", prepayOrderItemService.getPoCodesByPayId(prepayOrderEntity.getId()));
        result.put("reqPayTime", prepayOrderEntity.getReqPayTime());
        //剩余金额
        BigDecimal                  surplus                 = new BigDecimal("0");
        List<PrepayOrderItemEntity> prepayOrderItemEntities = prepayOrderItemService.listByPreOrderId(prepayOrderEntity.getId());
        for(PrepayOrderItemEntity prepayOrderItemEntity : prepayOrderItemEntities){
            PoEntity poEntity = poService.getByOrderCode(prepayOrderItemEntity.getPoCode());
            if(poEntity.getPrepaidTotal().compareTo(poEntity.getAccpaidTotal()) > 0){
                surplus = surplus.add(poEntity.getDocAmount().subtract(poEntity.getPrepaidTotal()));
            }else{
                surplus = surplus.add(poEntity.getDocAmount().subtract(poEntity.getAccpaidTotal()));
            }
        }
        result.put("surplus", surplus);
        //电子签章
        User createUser = userService.getById(prepayOrderEntity.getCreateUser());
        User audit1     = userService.getById(prepayOrderEntity.getAudit1());
        User audit2     = userService.getById(prepayOrderEntity.getAudit2());
        User audit3     = userService.getById(prepayOrderEntity.getAudit3());
        result.put("audit1", audit1 == null || audit1.getSignatures() == null ? "" : audit1.getSignatures());
        result.put("audit2", audit2 == null || audit2.getSignatures() == null ? "" : audit2.getSignatures());
        result.put("audit3", audit3 == null || audit3.getSignatures() == null ? "" : audit3.getSignatures());
        result.put("createUser", createUser == null || createUser.getSignatures() == null ? "" : createUser.getSignatures());
        return result;
    }
}
