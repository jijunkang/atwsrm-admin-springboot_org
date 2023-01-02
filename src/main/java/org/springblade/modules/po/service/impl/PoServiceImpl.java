package org.springblade.modules.po.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.util.Lists;
import org.springblade.common.config.AtwSrmConfiguration;
import org.springblade.common.dto.AssignDTO;
import org.springblade.common.dto.CheckDTO;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.common.utils.WillHttpUtil;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.finance.service.IPrepayOrderService;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.entity.OutPrItemProcessEntity;
import org.springblade.modules.outpr.service.IOutPrItemProcessService;
import org.springblade.modules.outpr.service.IOutPrItemService;
import org.springblade.modules.po.dto.PoDTO;
import org.springblade.modules.po.dto.PoExcel;
import org.springblade.modules.po.dto.PoItemDTO;
import org.springblade.modules.po.dto.PoPendingExcel;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.entity.OutIoEntity;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.mapper.PoMapper;
import org.springblade.modules.po.service.IIoService;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.po.service.IPoService;
import org.springblade.modules.pr.entity.U9PrEntity;
import org.springblade.modules.pr.service.IU9PrService;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 采购订单表头 服务实现类
 * @author Will
 */
@Service
public
class PoServiceImpl extends BaseServiceImpl<PoMapper, PoEntity> implements IPoService{

    @Autowired
    ISupplierService supplierService;

    @Autowired
    IU9PrService prService;

    @Autowired
    IOutPrItemService iOutPrItemService;

    @Autowired
    @Lazy
    IPoItemService poItemService;

    @Autowired
    @Lazy
    IOutPrItemProcessService outPrItemProcessService;

    @Autowired
    IPrepayOrderService prepayOrderService;

    @Autowired
    AtwSrmConfiguration atwSrmConfiguration;

    /**
     * IO 转 PoItem
     */
    @Override
    public
    PoItemEntity placeOrderByIo(IoEntity ioEntity, String source, Long sourceId){
        return placeOrderByIo(ioEntity, source, sourceId, null);
    }


    @Override
    public
    PoItemEntity placeOrderByIo(IoEntity ioEntity, String source, Long sourceId, String branch){
        Supplier   supplier = supplierService.getByCode(ioEntity.getSupCode());
        U9PrEntity pr       = prService.getById(ioEntity.getPrId());

        PoItemEntity poItem = new PoItemEntity();
        poItem.setItemCode(ioEntity.getItemCode());
        poItem.setItemName(ioEntity.getItemName());
        poItem.setSupCode(ioEntity.getSupCode());
        poItem.setSupName(ioEntity.getSupName());
        poItem.setPriceNum(ioEntity.getPriceNum());
        poItem.setPriceUom(ioEntity.getPriceUom());
        poItem.setTcNum(ioEntity.getTcNum());
        poItem.setTcUom(ioEntity.getTcUom());
        poItem.setPrId(ioEntity.getPrId());
        poItem.setPrCode(ioEntity.getPrCode());
        poItem.setPrLn(ioEntity.getPrLn());
        poItem.setReqDate(ioEntity.getReqDate());
        //是否按重量计算
        poItem.setIsByWeight(ioEntity.getIsByWeight());

        poItem.setSupConfirmDate(ioEntity.getPromiseDate());
        poItem.setRemark(ioEntity.getRemark());
        poItem.setIsPersent(ioEntity.getIsPersent());
        poItem.setPrice(ioEntity.getQuotePrice());
        poItem.setAmount(ioEntity.getQuotePrice().multiply(ioEntity.getPriceNum()));
        poItem.setTaxRate(supplier.getTaxRate());
        poItem.setStatus(IPoItemService.STATUS_INIT);
        poItem.setProNo(pr.getProNo());
        poItem.setIsSpilt(pr.getIsSpilt());
        poItem.setTcUomCode(pr.getTcUomCode());
        poItem.setPriceUomCode(pr.getPriceUomCode());
        poItem.setProGoodsNum(pr.getTcNum());
        poItem.setEndUser(pr.getEndUser());
        poItem.setPurchCode(pr.getPurchCode());
        poItem.setPurchName(pr.getPurchName());
        if(ioEntity.getStatus().equals(IIoService.STATUS_WAIT) && source.equals(IPoItemService.SOURCE_PROTOCOL)){
            //自动下单（框架协议）
            poItem.setIsAutoOrder(1);
        }

        poItem.setLaborCost(ioEntity.getLaborCost());
        poItem.setMaterialCost(ioEntity.getMaterialCost());
        poItem.setSource(source);
        poItem.setSourceId(sourceId);
        poItem.setAttachment(ioEntity.getAttachment());
        poItem.setBizBranch(branch);
        if("002".equals(pr.getOrgcode())){
            poItem.setOrgCode("002");
        }else{
            poItem.setOrgCode("001");
        }

        poItemService.save(poItem);

        return poItem;
    }


    @Override
    public
    void placeOrderByIoOfWW(OutIoEntity ioEntity, String source, Long sourceId){
        Supplier   supplier = supplierService.getByCode(ioEntity.getSupCode());
        OutPrItemEntity pr  = iOutPrItemService.getById(ioEntity.getPrId());

        List<OutPrItemProcessEntity> processList = outPrItemProcessService.getListByItemId(pr.getId());
        BigDecimal price = ioEntity.getQuotePrice();
        Integer size = processList.size();
        BigDecimal averagePrice = price.divide(new BigDecimal(size.toString()),2,BigDecimal.ROUND_HALF_UP);

        int i = 1;
        for(OutPrItemProcessEntity prItemProcessEntity : processList){
            PoItemEntity poItem = new PoItemEntity();
            poItem.setMoNo(prItemProcessEntity.getMoNo());
            poItem.setItemCode(ioEntity.getItemCode()+'-'+prItemProcessEntity.getProcessCode());
            poItem.setItemName(ioEntity.getItemName()+'-'+prItemProcessEntity.getProcessName());
            poItem.setSupCode(ioEntity.getSupCode());
            poItem.setSupName(ioEntity.getSupName());
            poItem.setPriceNum(ioEntity.getPriceNum());
            poItem.setPriceUom(ioEntity.getPriceUom());
            poItem.setTcNum(ioEntity.getTcNum());
            poItem.setTcUom(ioEntity.getTcUom());
            poItem.setPrId(ioEntity.getPrId());
            poItem.setPrCode(ioEntity.getPrCode());
            poItem.setPrLn(prItemProcessEntity.getPrLn());
            poItem.setReqDate(ioEntity.getReqDate());
            // 临时处理（待采购提供新的匹配逻辑）
            poItem.setPurchCode("150323");
            poItem.setPurchName("夏艳娟");
            //是否按重量计算
            poItem.setIsByWeight(ioEntity.getIsByWeight());

            poItem.setSupConfirmDate(ioEntity.getPromiseDate());
            poItem.setRemark(ioEntity.getRemark());
            poItem.setIsPersent(ioEntity.getIsPersent());
            if (i == size) {
                poItem.setPrice(price.subtract(averagePrice.multiply(new BigDecimal(size-1))));
                i = 1;
            } else {
                poItem.setPrice(new BigDecimal(averagePrice.toString()));
                i++;
            }
            poItem.setAmount(poItem.getPrice().multiply(ioEntity.getPriceNum()));
            poItem.setTaxRate(supplier.getTaxRate());
            poItem.setStatus(IPoItemService.STATUS_INIT);
            //poItem.setProNo(pr.getProNo());
            //poItem.setIsSpilt(pr.getIsSpilt());
            poItem.setTcUomCode(pr.getPriceUom());
            poItem.setPriceUomCode(pr.getPriceUom());
            poItem.setProGoodsNum(pr.getPriceNum());
            //poItem.setEndUser(pr.getEndUser());
            if(ioEntity.getStatus().equals(IIoService.STATUS_WAIT) && source.equals(IPoItemService.SOURCE_PROTOCOL)){
                //自动下单（框架协议）
                poItem.setIsAutoOrder(1);
            }

            poItem.setLaborCost(ioEntity.getLaborCost());
            poItem.setMaterialCost(ioEntity.getMaterialCost());
            poItem.setSource(source);
            poItem.setSourceId(sourceId);
            poItem.setAttachment(ioEntity.getAttachment());
            poItem.setBizBranch(null);
            poItemService.save(poItem);
        }
    }

    @Override
    public
    boolean contractCheck(CheckDTO checkDto){
        PoEntity po = getById(checkDto.getId());
        po.setStatus(checkDto.getStatus());
        po.setRemark(checkDto.getRemark());
        po.setContractStatus(checkDto.getContractStatus());
        //todo 记录remark
        //如果是审核通过 则 创建预付请款单
        if(STATUS_EXECUTING.equals(checkDto.getStatus()) && po.getFirstPrepayRate() > 0){
            prepayOrderService.createFirstByPo(po);
        }
        return updateById(po);
    }

    @Override
    public
    boolean cancelPoAssignPurch(AssignDTO assign){
        PoEntity po = getById(assign.getBizId());
        po.setCancelAssign(CANCEL_ASSIGN_PURCH);
        po.setCancelAssignId(assign.getUserId());
        return updateById(po);
    }

    /**
     * 上传合同
     * @return
     */
    @Override
    public
    boolean uploadContract(PoEntity po){
        PoEntity upPo = new PoEntity();
        upPo.setId(po.getId());
        upPo.setContract(po.getContract());
        upPo.setContractTime((int) (new Date().getTime()/1000));
        upPo.setContractStatus(po.getContractStatus());
        return updateById(upPo);
    }

    /**
     * 待处理订单
     * @return
     */
    @Override
    public
    IPage<PoEntity> getTodoPage(IPage<PoEntity> page, PoEntity po){
        QueryWrapper<PoEntity> query = getTodoQueryWrapper(po).orderByDesc("doc_date");
        return page(page, query);
    }

    /**
     * 待处理订单
     * @return
     */
    @Override
    public
    QueryWrapper<PoEntity> getTodoQueryWrapper(PoEntity po){
        return Wrappers.<PoEntity>query()
                .in("status", STATUS_INIT, STATUS_SUPSURE, STATUS_UPLOAD_CONTRACT, STATUS_CONTRACT_REFUSE, STATUS_PRICE_REJECT)
                .like(StringUtil.isNotBlank(po.getSupCode()), "sup_code", po.getSupCode())
                .like(StringUtil.isNotBlank(po.getSupName()), "sup_name", po.getSupName());
    }

    /**
     * 订单付款明细列表
     * （订单行交期最早的交期 - 当前日期） <= 15 (交期取修改交期，无修改交期取承诺交期）
     * 并且 该订单需要进行第n次预付（n>1）
     * 并且 订单对应的付款方式的累计预付比例大于预比例（月结除外）
     * @return
     */
    @Override
    public
    IPage<PoEntity> getPrePoPage(PoDTO poDTO, IPage<PoEntity> page) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        QueryWrapper<PoEntity> qw = Wrappers.<PoEntity>query().gt("accum_prepay - prepaid_total", 0)
            .eq(StringUtil.isNotBlank(poDTO.getSupCode()), "sup_code", poDTO.getSupCode())
            .eq(StringUtil.isNotBlank(poDTO.getOrgCode()), "org_code", poDTO.getOrgCode())
            .eq("status", STATUS_EXECUTING)
            .gt("doc_date", simpleDateFormat.parse("2020-07-24").getTime() / 1000);
        if ("remind".equals(poDTO.getType())) {
            qw.in("order_code", poItemService.getRemindPoCodes(15));
        }
        if (!StringUtil.isEmpty(poDTO.getSupName())) {
            qw.like("sup_name", poDTO.getSupName());
        }
        if (!StringUtil.isEmpty(poDTO.getOrderCode())) {
            qw.like("order_code", poDTO.getOrderCode());
        }
        if (!StringUtil.isEmpty(poDTO.getCreateTimeStart())) {
            qw.ge("create_time", poDTO.getCreateTimeStart());
        }
        if (!StringUtil.isEmpty(poDTO.getCreateTimeEnd())) {
            qw.le("create_time", poDTO.getCreateTimeEnd());
        }
        if (!StringUtil.isEmpty(poDTO.getDocDateStart())) {
            qw.ge("doc_date", poDTO.getDocDateStart());
        }
        if (!StringUtil.isEmpty(poDTO.getDocDateEnd())) {
            qw.le("doc_date", poDTO.getDocDateEnd());
        }
        return page(page, qw);
    }

    @Override
    public
    int getPrePoCount(PoDTO poDTO) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        QueryWrapper<PoEntity> qw = Wrappers.<PoEntity>query().gt("accum_prepay - prepaid_total", 0)
            .eq(StringUtil.isNotBlank(poDTO.getSupCode()), "sup_code", poDTO.getSupCode())
            .eq("status", STATUS_EXECUTING)
            .gt("doc_date", simpleDateFormat.parse("2020-07-24").getTime() / 1000);
        if("remind".equals(poDTO.getType())){
            qw.in("order_code", poItemService.getRemindPoCodes(15));
        }
        return count(qw);
    }

    @Override
    public
    int getRemindCount() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        QueryWrapper<PoEntity> qw = Wrappers.<PoEntity>query().gt("accum_prepay - prepaid_total", 0);
        qw.in("order_code", poItemService.getRemindPoCodes(15));
        qw.eq("status", STATUS_EXECUTING);
        qw.gt("doc_date", simpleDateFormat.parse("2020-07-24").getTime() / 1000);
        return count(qw);
    }

    @Override
    public
    PoEntity getByOrderCode(String poCode){
        PoEntity poEntity = new PoEntity();
        poEntity.setOrderCode(poCode);
        return getOne(Condition.getQueryWrapper(poEntity));
    }

    @Override
    public void export(PoEntity po, HttpServletResponse response) {
        List<PoEntity> list = list(getTodoQueryWrapper(po).orderByDesc("doc_date"));
        List<PoPendingExcel> excels = Lists.newArrayList();
        list.forEach(temp ->{
            PoPendingExcel excel = BeanUtil.copy(temp, PoPendingExcel.class);
            excels.add(excel);
        });
        ExcelUtils.defaultExport(excels, PoPendingExcel.class, "待处理订单" + DateUtil.formatDate(new Date()), response);
    }


    @Override
    public void poExport(PoDTO po, HttpServletResponse response) {
        List<PoEntity> list = this.baseMapper.getPoList(po);
        List<PoExcel> excels = Lists.newArrayList();
        list.forEach(temp ->{
            PoExcel excel = BeanUtil.copy(temp, PoExcel.class);
            excels.add(excel);
        });

        ExcelUtils.defaultExport(excels, PoExcel.class, "订单一览" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public BigDecimal getSumCloseAmount(String poCode) {
        return this.baseMapper.getSumCloseAmount(poCode);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean auditPrice(PoEntity poEntity) throws IOException {
        PoEntity po = getById(poEntity);
        po.setStatus(poEntity.getStatus());
        if(StringUtil.isNotBlank(poEntity.getPriceRemark())){
            po.setPriceRemark(poEntity.getPriceRemark());
        }
        if(poEntity.getStatus().equals(STATUS_PRICE_PASS)){
            //同步U9
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("PurchaseOrder", po.getOrderCode());
            List<Map<String, Object>> lines = Lists.newArrayList();
            PoItemDTO dto = new PoItemDTO();
            dto.setPoCode(po.getOrderCode());
            List<PoItemEntity> poItemEntities = poItemService.list(poItemService.getQueryWrapper(dto));
            poItemEntities.forEach(temp -> {
                Map<String, Object> line = new HashMap<String, Object>();
                line.put("PurchaseOrderSeq", temp.getPoLn());
                line.put("OrderPriceTC", temp.getPrice());
                line.put("ReqQty", temp.getTcNum());
                lines.add(line);
            });
            params.put("lines", lines);
            ObjectMapper mapper = new ObjectMapper();
            String res = WillHttpUtil.postJson(
                atwSrmConfiguration.getBizapiDomain() + "/api/ModifyPO", mapper.writeValueAsString(params), 600L);
            ObjectNode objectNode = (ObjectNode) mapper.readTree(res);
            String code = mapper.readValue(String.valueOf(objectNode.get("code")), String.class);
            if (!"2000".equals(code)) {
                throw new RuntimeException("U9接口返回异常：" + mapper.readValue(String.valueOf(objectNode.get("msg")), String.class));
            }

            //更改po累计预付金额
            po.setAccumPrepay(po.getDocAmountUpdate().multiply(BigDecimal.valueOf(po.getAccumPrepayRate())));
            //修改合同状态
            po.setContractStatus(CO_STATUS_INIT);
            //更新PO订单总金额
            po.setDocAmount(po.getDocAmountUpdate());
            //更新PoItem单价、金额
            poItemEntities.forEach(temp ->{
                temp.setPrice(temp.getPriceUpdate());
                temp.setAmount(temp.getAmountUpdate());
                poItemService.updateById(temp);
            });
        }
        return updateById(po);
    }

    @Override
    public int getPriceAuditCount() {
        QueryWrapper<PoEntity> queryWrapper = Condition.getQueryWrapper(new PoEntity());
        queryWrapper.eq("status", STATUS_PRICE_WAIT);
        return count(queryWrapper);
    }

    @Override
    public IPage<PoEntity> list(Query query, PoDTO PoDto) {
        return this.baseMapper.getList(Condition.getPage(query),PoDto);
    }

    @Override
    public IPage<PoEntity> vmiList(Query query, PoDTO PoDto) {
        return this.baseMapper.getVmiList(Condition.getPage(query),PoDto);
    }


}
