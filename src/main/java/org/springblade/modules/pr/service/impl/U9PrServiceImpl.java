package org.springblade.modules.pr.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.api.client.util.Lists;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.common.dto.CheckDTO;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.ap.entity.ApItemEntity;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.mathmodel.entity.CastingOrderEntity;
import org.springblade.modules.mathmodel.entity.MailyMaterialTotalEntity;
import org.springblade.modules.mathmodel.entity.MmVolumeCalculateEntity;
import org.springblade.modules.mathmodel.entity.TubeMaterialInfoEntity;
import org.springblade.modules.mathmodel.mapper.TubeMaterialMapper;
import org.springblade.modules.mathmodel.service.CastingOrderService;
import org.springblade.modules.mathmodel.service.IMmVolumeCalculateService;
import org.springblade.modules.mathmodel.service.TubeMaterialInfoService;
import org.springblade.modules.mathmodel.service.TubeMaterialService;
import org.springblade.modules.mathmodel.service.impl.MmVolumeCalculateImpl;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.service.IIoService;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.pr.dto.*;
import org.springblade.modules.pr.entity.*;
import org.springblade.modules.pr.enums.DutyDeptEnum;
import org.springblade.modules.pr.mapper.*;
import org.springblade.modules.pr.service.IU9PrExService;
import org.springblade.modules.pr.service.IU9PrService;
import org.springblade.modules.pr.vo.*;
import org.springblade.modules.priceframe.entity.PriceFrameEntity;
import org.springblade.modules.priceframe.service.IPriceFrameService;
import org.springblade.modules.pricelib.entity.PriceLibEntity;
import org.springblade.modules.pricelib.service.IPriceLibService;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springblade.modules.system.service.IDictBizService;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static org.springblade.common.utils.ItemAnalysisUtil.*;
import static org.springblade.core.secure.utils.AuthUtil.getUser;
import static org.springblade.modules.po.service.IPoItemService.SOURCE_HANG;

/**
 * 请购单 服务实现类
 *
 * @author Will
 */
@Service
@Slf4j
public
class U9PrServiceImpl extends BaseServiceImpl<U9PrMapper, U9PrEntity> implements IU9PrService {

    @Autowired
    @Lazy  //  @Lazy  防止service 循环引用
    IIoService ioService;

    @Autowired
    IPriceLibService priceLibService;

    @Autowired
    ISupplierService supplierService;

    @Autowired
    TubeMaterialService tubeMaterialService;

    @Autowired
    @Lazy
    IPoItemService poItemService;

    @Autowired
    @Lazy
    IDictBizService dictBizService;

    @Autowired
    @Lazy
    IPriceFrameService priceFrameService;

    @Autowired
    @Lazy
    IParamService paramService;

    @Autowired
    @Lazy
    IMmVolumeCalculateService mmVolumeCalculateService;

    @Autowired
    @Lazy
    IItemService itemService;

    @Autowired
    MaterialMailyVoMapper materialMailyVoMapper;

    @Autowired
    @Lazy
    ISupplierService iSupplierService;

    @Autowired
    TubeMaterialInfoService tubeMaterialInfoService;

    @Autowired
    CastingOrderService castingOrderService;

    @Autowired
    TubeMaterialMapper tubeMaterialMapper;

    @Autowired
    U9PrEntityExMapper u9PrEntityExMapper;

    @Autowired
    U9PrEntityNoProMapper u9PrEntityNoProMapper;

    @Autowired
    AutoOrderOfXLJMapper autoOrderOfXLJMapper;

    @Autowired
    IU9PrExService u9PrExService;

    @Autowired
    U9PrMapper u9PrMapper;

    @Value("${oracle.url}")
    private String oracleUrl;

    @Value("${oracle.user}")
    private String oracleUser;

    @Value("${oracle.password}")
    private String oraclePassword;

    @Value("${oracle.driver}")
    private String oracleDriver;

    @Value("${atwsrm.bizapiUrl}")
    private String bizapiUrl;

    private final Double PI = 3.14;


    @Override
    public QueryWrapper<U9PrEntity> getQueryWrapper(U9PrEntity u9Pr) {

        return Wrappers.<U9PrEntity>query()
            .like(StringUtils.isNotBlank(u9Pr.getPrCode()), "pr_code", u9Pr.getPrCode())
            .like(StringUtils.isNotBlank(u9Pr.getItemCode()), "item_code", u9Pr.getItemCode())
            .like(StringUtils.isNotBlank(u9Pr.getItemName()), "item_name", u9Pr.getItemName())
            .eq(StringUtils.isNotBlank(u9Pr.getPurchCode()), "purch_code", u9Pr.getPurchCode())
            .like(StringUtils.isNotBlank(u9Pr.getPurchName()), "purch_name", u9Pr.getPurchName())
            .eq(u9Pr.getStatus() != null, "status", u9Pr.getStatus())
            .eq(StringUtils.isNotBlank(u9Pr.getMoNo()), "mo_no", u9Pr.getMoNo())
            .like(StringUtils.isNotBlank(u9Pr.getLastSupName()), "last_sup_name", u9Pr.getLastSupName());
    }


    @Override
    public boolean submitPrice(SubmitPriceDTO dto) {
        Supplier sup = supplierService.getByName(dto.getSupName());
        if (sup == null) {
            return false;
        }
        U9PrEntity pr = this.getById(dto.getId());
        if (pr == null) {
            throw new RuntimeException("未找到PR：" + "id：" + dto.getId());
        }
        IoEntity io = new IoEntity();

        io.setPrId(pr.getId());
        io.setPrLn(pr.getPrLn());
        io.setPrCode(pr.getPrCode());
        io.setItemCode(pr.getItemCode());
        io.setItemName(pr.getItemName());
        io.setPriceNum(pr.getPriceNum());
        io.setPriceUom(pr.getPriceUom());
        io.setTcNum(pr.getTcNum());
        io.setTcUom(pr.getTcUom());
        io.setReqDate(pr.getReqDate());
        io.setStatus(IIoService.STATUS_WINBID_CHECK1);
        io.setSupCode(sup.getCode());
        io.setSupName(sup.getName());
        //是否按重量计算
        io.setIsByWeight(dto.getIsByWeight());
        io.setPromiseDate(dto.getPromiseDate().getTime() / 1000 + 8 * 3600);
        io.setIsPersent(dto.getIsPersent());
        io.setQuotePrice(dto.getQuotePrice());
        io.setQuoteDate(new Date().getTime() / 1000);
        io.setRemark(dto.getRemark());
        io.setSource(IIoService.SOURCE_PURCHSUBMIT);

        if (dto.getAttachment() != null) {
            io.setAttachment(dto.getAttachment());
            pr.setAttachment(dto.getAttachment());
        }


        if (FLOW_SUBMIT.equals(dto.getType())) {
            //暂存
            pr.setStatus(IU9PrService.STATUS_FLOW_SUBMIT);
        } else if (WIN_BID.equals(dto.getType())) {
            //提交
            pr.setStatus(IU9PrService.STATUS_WINBID);
        } else {
            // 提交审核 （待提交）
            pr.setStatus(IU9PrService.STATUS_WINBID);
            String attachMent = this.baseMapper.selectAttachment(dto.getIoId());
            if (StringUtils.isNotEmpty(attachMent)) {
                io.setAttachment(attachMent);
            }
            this.baseMapper.deleteIo(dto.getIoId());
        }

        ioService.save(io);
        String alltypes = "碟簧座,挡块,压环,填料压套,填料隔环,支撑套,长管,短管,接管,填料垫,凸管,下盖,填料函,连接盘,圆筒,套筒,填料压板,凸板";
        if (pr.getItemName().contains("铸件") || (alltypes.indexOf(pr.getItemName().split("-")[0]) > 0 && pr.getItemName().split("\\+").length < 2)) {
            if (dto.getIsNeedCheck().equals("1")) {
                pr.setIsNeedCheck("2");
                dto.setIsNeedCheck("2");
            }
        } else {
            pr.setIsNeedCheck(dto.getIsNeedCheck());
        }
        this.updateById(pr);

        //删除当前PR下的IO并备注  流标录入价格关联的IO删除，ID：
        List<IoEntity> ioEntities = ioService.getByPrId(pr.getId());
        ioEntities.forEach(ioEntity -> {
            if (!ioEntity.getId().equals(io.getId())) {
                ioEntity.setRemark("流标录入价格关联的IO删除，ID：" + io.getId());
                ioService.updateById(ioEntity);
                ioService.removeById(ioEntity);
            }
        });

        // 跳过审核的步骤(仅是提交审核的时候)[球座、委外可以跳、铸件不可以]
        if ((!FLOW_SUBMIT.equals(dto.getType())) && NOT_NEED_CHECK.equals(dto.getIsNeedCheck()) && (!pr.getItemName().contains("铸件"))) {
            CheckDTO checkDTO = new CheckDTO();
            checkDTO.setId(dto.getId());
            checkDTO.setIoId(io.getId());
            checkDTO.setStatus(IU9PrService.STATUS_ORDER);
            ioService.check2(checkDTO);
        }

        if (NOT_NEED_CHECK.equals(dto.getIsNeedCheck())) { // 代表是自动下单的,需要保存信息
            if (pr.getItemName().indexOf("锻件") > -1) {
                this.insertAutoOrderOfDJ(sup, pr, dto);
            }

            if ((pr.getItemName().indexOf("长管") > -1 || pr.getItemName().indexOf("短管") > -1 || pr.getItemName().indexOf("衬管") > -1) && (pr.getItemName().split("\\+").length > 1)) {
                this.insertAutoOrderOfGBL(sup, pr, dto, io);
            }

            if (pr.getItemName().indexOf("铸件") > -1) { // 铸件
                this.insertAutoOrderOfZJ(sup, pr, dto, io);
            }

            if (alltypes.indexOf(pr.getItemName().split("-")[0]) > -1 && pr.getItemName().split("\\+").length < 2) {  // 小零件
                this.insertAutoOrderOfXLJ(sup, pr, dto, io);
            }

        }

        return true;
    }

    private void insertAutoOrderOfXLJ(Supplier sup, U9PrEntity pr, SubmitPriceDTO dto, IoEntity io) {
        String supCode = sup.getCode();
        String itemCode = pr.getItemCode();
        ItemInfoEntityOfXLJ itemInfoEntityOfXLJ = this.baseMapper.selectXLJInfoReportByItemAndSupCode(itemCode, supCode);

        AutoOrderOfXLJ autoOrderOfXLJ = BeanUtil.copy(itemInfoEntityOfXLJ, AutoOrderOfXLJ.class);

        autoOrderOfXLJ.setPrCode(pr.getPrCode());
        autoOrderOfXLJ.setPrLn(pr.getPrLn().toString());
        String amount = new BigDecimal(io.getPriceNum().toString()).multiply(new BigDecimal(io.getQuotePrice().toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        autoOrderOfXLJ.setAmount(amount);
        autoOrderOfXLJ.setPriceNum(io.getPriceNum().toString());

        autoOrderOfXLJMapper.insert(autoOrderOfXLJ);
    }

    private void insertAutoOrderOfZJ(Supplier sup, U9PrEntity pr, SubmitPriceDTO dto, IoEntity io) {
        CastingOrderEntity castingOrder = new CastingOrderEntity();
        castingOrder.setSupCode(sup.getCode());
        castingOrder.setItemCode(io.getItemCode());
        castingOrder.setItemDesc(io.getItemName());
        castingOrder.setPriceNum(io.getPriceNum());
        castingOrder.setPrCode(io.getPrCode());
        castingOrder.setPrLn(io.getPrLn());
        castingOrderService.submitCastingReport(castingOrder);
    }

    private void insertAutoOrderOfGBL(Supplier sup, U9PrEntity pr, SubmitPriceDTO dto, IoEntity io) {
        TubeMaterialInfoEntity totalEntity = new TubeMaterialInfoEntity();
        totalEntity.setSupplierCode(sup.getCode());  //供应商编号
        totalEntity.setItemCode(pr.getItemCode()); //物料编号
        TubeMaterialInfoEntity maliyVO = tubeMaterialInfoService.getOne(Condition.getQueryWrapper(totalEntity));

        totalEntity.setPrCode(pr.getPrCode());  // 请购单号
        totalEntity.setPrLn(pr.getPrLn());  // 请购单行号

        MailyMaterialTotalEntity entity = new MailyMaterialTotalEntity();
        entity.setPrLn(totalEntity.getPrLn());  // 行号
        entity.setPrCode(totalEntity.getPrCode()); //prcode
        entity.setSupplierCode(totalEntity.getSupplierCode()); //供应商编号
        entity.setCoatingLength(maliyVO.getCoatingLength()); // 喷涂长度
        entity.setCoatingInternalDiameter(maliyVO.getCoatingInternalDiameter()); // 喷涂内径
        entity.setCoatingArea(maliyVO.getCoatingArea()); // 喷涂面积
        entity.setDiameterAllowance(maliyVO.getDiameterAllowance()); // 外径余量
        entity.setApertureAllowance(maliyVO.getApertureAllowance()); // 孔径余量
        entity.setSupplierName(maliyVO.getSupplierName());  // 供应商名称
        entity.setItemCode(maliyVO.getItemCode()); // 物料编号
        entity.setItemDesc(maliyVO.getItemDesc());// 物料描述
        entity.setExternalDiameter(maliyVO.getExternalDiameter());  //外径
        entity.setInternalDiamete(maliyVO.getInternalDiamete()); //内径
        entity.setLength(maliyVO.getLength()); // 原材料长度
        entity.setWeight(maliyVO.getWeight()); // 原材料单重
        entity.setTheMaterialPrice(maliyVO.getTheMaterialPrice().setScale(2, BigDecimal.ROUND_DOWN));// 材料单价
        entity.setMaterialPrice(maliyVO.getMaterialPrice()); // 材料费
        entity.setProcessingFee(maliyVO.getProcessingFee()); // 加工费
        entity.setPrice(maliyVO.getPrice());// 切割费
        entity.setCoatingPrice(maliyVO.getCoatingPrice()); // 喷涂单价
        entity.setSprayPrice(maliyVO.getSprayPrice()); // 喷涂费
        entity.setUnitPrice(maliyVO.getUnitPrice()); // 产品单价
        entity.setPriceNum(Integer.valueOf(io.getPriceNum().toString())); //数量
        entity.setTotalPrice(maliyVO.getUnitPrice().multiply(BigDecimal.valueOf(entity.getPriceNum()))); //总价
        tubeMaterialMapper.insert(entity);
    }

    private void insertAutoOrderOfDJ(Supplier sup, U9PrEntity pr, SubmitPriceDTO dto) {
        SubmitPriceReq req = new SubmitPriceReq();
        req.setItemName(pr.getItemName());
        req.setItemCode(pr.getItemCode());
        req.setSupCode(sup.getCode());
        String chooseSupCode = dto.getSupCode();
        // 查找未中及下单的供应商  包含已中标的供应商

        List<ItemInfoEntityDJReport> NoList = this.baseMapper.selectNoSwnningBid(req);
        for (ItemInfoEntityDJReport entity : NoList) {
            if (sup.getCode().equals(entity.getSupCode())) {
                entity.setSwnningBid("已中标");
                this.baseMapper.insertDjAutoOrder(pr.getPrCode(), pr.getPrLn().toString(), entity.getItemCode(), entity.getItemName(), entity.getSupCode(), entity.getSupName(), entity.getWeight(),
                    entity.getMaterialPrice(), entity.getPrice(), entity.getBigOuterSize(), entity.getBigHeightSize(), entity.getSmallOuterSize(), entity.getTotalHeightSize(), entity.getInnerSize(),
                    entity.getOuterSize(), entity.getHeightSize(), entity.getType(), entity.getRes(), entity.getHeightRemain(), entity.getOuterRemain(), entity.getInnerRemain(), entity.getSwnningBid(), entity.getNewInnerRemain());
            } else {
                entity.setSwnningBid("未中标");
                this.baseMapper.insertDjAutoOrder(pr.getPrCode(), pr.getPrLn().toString(), entity.getItemCode(), entity.getItemName(), entity.getSupCode(), entity.getSupName(), entity.getWeight(),
                    entity.getMaterialPrice(), entity.getPrice(), entity.getBigOuterSize(), entity.getBigHeightSize(), entity.getSmallOuterSize(), entity.getTotalHeightSize(), entity.getInnerSize(),
                    entity.getOuterSize(), entity.getHeightSize(), entity.getType(), entity.getRes(), entity.getHeightRemain(), entity.getOuterRemain(), entity.getInnerRemain(), entity.getSwnningBid(), entity.getNewInnerRemain());
            }
        }
    }


    /**
     * @return
     */
    @Override
    public IPage<U9PrDTO> selectPage(IPage<U9PrDTO> page, PrReq prReq) {
        IPage<U9PrDTO> retPage = baseMapper.selectWaitPageByReq(page, prReq);
        if (retPage.getRecords() == null) {
            return retPage;
        }
        for (U9PrDTO dto : retPage.getRecords()) {
            dto.setHighestPrice(poItemService.getHighestPrice(dto.getItemCode()));
            dto.setLowestPrice(poItemService.getLowestPrice(dto.getItemCode()));
            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(), dto.getItemName());
            if (poItemEntity != null) {
                dto.setLastPrice(poItemEntity.getPrice());
                dto.setLastSupName(poItemEntity.getSupName());
            }

            if (dto.getPiLastSupName() != null && !dto.getPiLastSupName().isEmpty()) {
                dto.setLastSupName(dto.getPiLastSupName());
            }
        }
        return retPage;
    }


    @Override
    public IPage<U9PrDTO> selectAllPrPage(IPage<U9PrDTO> page, PrReq prReq) {
        IPage<U9PrDTO> retPage = baseMapper.selectAllPrPage(page, prReq);
        if (retPage.getRecords() == null) {
            return retPage;
        }
        for (U9PrDTO dto : retPage.getRecords()) {
            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(), dto.getItemName());
            if (poItemEntity != null) {
                dto.setLastPrice(poItemEntity.getPrice());
                dto.setLastSupName(poItemEntity.getSupName());
            }

            if (dto.getPiLastSupName() != null && !dto.getPiLastSupName().isEmpty()) {
                dto.setLastSupName(dto.getPiLastSupName());
            }
        }
        return retPage;
    }


    /**
     * 导出
     */
    @Override
    public void export(PrReq prReq, HttpServletResponse response) {
        QueryWrapper<U9PrEntity> queryWrapper = getQueryWrapper(prReq).ne("status", IU9PrService.STATUS_INIT);
        if (StringUtils.isNotBlank(prReq.getCreateTimeStart())) {
            queryWrapper.ge("create_time", prReq.getCreateTimeStart());
        }
        if (StringUtils.isNotBlank(prReq.getCreateTimeEnd())) {
            queryWrapper.le("create_time", prReq.getCreateTimeEnd());
        }
        if (StringUtils.isNotBlank(prReq.getStatuss())) {
            queryWrapper.in("status", prReq.getStatusList());
        }
        List<U9PrEntity> dtoList = list(queryWrapper);

        List<U9PrExcelDTO> excelList = Lists.newArrayList();
        for (U9PrEntity dto : dtoList) {
            U9PrExcelDTO excelDTO = BeanUtil.copy(dto, U9PrExcelDTO.class);
            // 项目号如果是null或者是空，取 proNo
            if (dto.getProNo() == null || dto.getProNo().isEmpty()) {
                dto.setProNo(dto.getApsProNo());
            }
            excelDTO.setFlowType(dictBizService.getValue("pr_flow_type", excelDTO.getFlowType()));
            excelDTO.setStatusFmt(dictBizService.getValue("pr_status", excelDTO.getStatus() + ""));
            excelDTO.setInquiryWayFmt(dictBizService.getValue("pr_inquiry_way", excelDTO.getInquiryWay()));
            excelList.add(excelDTO);
        }
        ExcelUtils.defaultExport(excelList, U9PrExcelDTO.class, "需求池" + DateUtil.formatDate(new Date()), response);

    }

    @Override
    public IPage<Map<String, Object>> getPriceLib(IPage<PriceLibEntity> page, U9PrEntity u9pr) {
        U9PrEntity u9PrEntity = getById(u9pr.getId());
        IPage<PriceLibEntity> totalPage = priceLibService.getPriceLib(page, u9PrEntity);
        List<Map<String, Object>> listVo = Lists.newArrayList();
        for (PriceLibEntity priceLibEntity : totalPage.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", String.valueOf(priceLibEntity.getId()));
            map.put("type", "priceLib");
            map.put("supCode", priceLibEntity.getSupCode());
            map.put("supName", priceLibEntity.getSupName());
            map.put("price", priceLibEntity.getPrice());
            listVo.add(map);
        }
        IPage<Map<String, Object>> newPage = new Page<>(totalPage.getCurrent(), totalPage.getSize(), totalPage.getTotal());
        return newPage.setRecords(listVo);
    }

    @Override
    public boolean createByHang(U9PrHangDTO u9PrHangDTO) {
        U9PrEntity u9PrEntity = getById(u9PrHangDTO.getPrId());
        PoItemEntity poItemEntity = new PoItemEntity();
        if ("priceLib".equals(u9PrHangDTO.getType())) {
            PriceLibEntity priceLibEntity = priceLibService.getById(u9PrHangDTO.getId());
            if (!StringUtil.isEmpty(priceLibEntity) && !StringUtil.isEmpty(u9PrEntity)) {

                poItemEntity.setItemCode(u9PrEntity.getItemCode());
                poItemEntity.setItemName(u9PrEntity.getItemName());
                poItemEntity.setPurchCode(u9PrEntity.getPurchCode());
                poItemEntity.setPurchName(u9PrEntity.getPurchName());

                poItemEntity.setSupCode(priceLibEntity.getSupCode());
                poItemEntity.setSupName(priceLibEntity.getSupName());
                poItemEntity.setPriceNum(u9PrEntity.getPriceNum());
                poItemEntity.setPriceUom(u9PrEntity.getPriceUom());
                poItemEntity.setPriceUomCode(u9PrEntity.getPriceUomCode());
                poItemEntity.setTcNum(u9PrEntity.getPriceNum());
                poItemEntity.setTcUom(u9PrEntity.getPriceUom());
                poItemEntity.setTcUomCode(u9PrEntity.getPriceUomCode());
                poItemEntity.setSupConfirmDate(u9PrHangDTO.getSupConfirmDate());
                poItemEntity.setReqDate(u9PrEntity.getReqDate());
                poItemEntity.setPrice(priceLibEntity.getPrice());
                poItemEntity.setAmount(priceLibEntity.getPrice().multiply(u9PrEntity.getPriceNum()));
                poItemEntity.setProGoodsNum(priceLibEntity.getLimitMin());
                poItemEntity.setPrId(u9PrEntity.getId());
                poItemEntity.setPrCode(u9PrEntity.getPrCode());
                poItemEntity.setPrLn(u9PrEntity.getPrLn());

                Supplier sup = supplierService.getByCode(priceLibEntity.getSupCode());
                poItemEntity.setTaxRate(sup.getTaxRate());
                Long nowTime = System.currentTimeMillis() / 1000;
                poItemEntity.setWinbidTime(nowTime);
                poItemEntity.setRemark(u9PrHangDTO.getRemark());
                poItemEntity.setAttachment(u9PrHangDTO.getAttachment());
                poItemEntity.setSource(SOURCE_HANG);
                poItemEntity.setSourceId(u9PrEntity.getId());
                poItemEntity.setStatus(IPoItemService.STATUS_INIT);


                u9PrEntity.setStatus(STATUS_WAIT);
                u9PrEntity.setAttachment(u9PrHangDTO.getAttachment());
                updateById(u9PrEntity);
                return poItemService.save(poItemEntity);
            }

        }
        return false;
    }

    @Override
    public IPage<U9PrEntity> getU9Page(IPage<U9PrEntity> page) {
        QueryWrapper<U9PrEntity> queryWrapper = Condition.getQueryWrapper(new U9PrEntity());
        queryWrapper.eq("status", STATUS_INIT);
        queryWrapper.eq("inquiry_way", INQUIRYWAY_HAVEPROTOCOL);
        return page(page, queryWrapper);
    }

    @Override
    public int getPriceFrameCount() {
        List<Map<String, Object>> countList = priceFrameService.countList();
        int count = 0;
        for (Map<String, Object> stringObjectMap : countList) {
            count = count + Integer.parseInt(stringObjectMap.get("count").toString());
        }
        return count;
    }

    /**
     * 昕月
     *
     * @param submitPriceReq
     * @return 审核提交下单
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitBatch(SubmitPriceReq submitPriceReq) {
        boolean result = false;
        // 自动下单
        List<SubmitPriceDTO> items = submitPriceReq.getSubmitPriceDTOs();
        for (SubmitPriceDTO item : items) {
            result = submitPrice(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getInquiryCount(String purchType) {
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_INQUIRY);
            put("title", "询价中");
            put("count", getInquiryCount(STATUS_INQUIRY.toString(), purchType, null));
        }});
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_QUOTED);
            put("title", "待评标");
            put("count", getInquiryCount(STATUS_QUOTED.toString(), purchType, null));
        }});
        // 小零件询价单、不需要待审核
        if (PURCHASE_TYPE_NORMAL.equals(purchType)) {
            result.add(new HashMap<String, Object>(3) {{
                put("status", STATUS_WINBID);
                put("title", "待审核");
                put("count", getInquiryCount(STATUS_WINBID.toString(), purchType, null));
            }});
        }
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_WAIT);
            put("title", "待下单");
            put("count", getInquiryCount(STATUS_WAIT.toString(), purchType, 1));
        }});
        return result;
    }

    @Override
    public List<Map<String, Object>> getFlowCount(String purchType) {
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_HANG + "," + STATUS_FLOW);
            put("title", "待处理");
            put("count", getFlowCount(STATUS_HANG + "," + STATUS_FLOW, purchType));
        }});
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_FLOW_SUBMIT);
            put("title", "待提交");
            put("count", getFlowCount(STATUS_FLOW_SUBMIT.toString(), purchType));
        }});
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_WINBID);
            put("title", "待审核");
            put("count", getFlowCount(STATUS_WINBID.toString(), purchType));
        }});
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_WAIT);
            put("title", "待下单");
            put("count", getFlowCount(STATUS_WAIT.toString(), purchType));
        }});
        // 小零件询价单、多出无供应商tab
        if (PURCHASE_TYPE_INNER.equals(purchType)) {
            result.add(new HashMap<String, Object>(3) {{
                put("status", STATUS_FLOW_NOSUP);
                put("title", "无供应商");
                put("count", getFlowCount(STATUS_FLOW_NOSUP.toString(), purchType));
            }});
        }
        return result;
    }


    @Override
    public IPage<U9PrDTO> getCheckPage(IPage<U9PrDTO> page, PrReq prReq) {
        String mRoleId = paramService.getValue("purch_manager.role_id");
        if (!StringUtil.containsAny(getUser().getRoleId(), mRoleId)) {
            prReq.setPurchCode(getUser().getAccount());
        }
        IPage<U9PrDTO> u9Page = this.baseMapper.selectPageByReq(page, prReq);
        getWinIo(prReq, u9Page);
        return u9Page;
    }

    @Override
    public List<PriceVO> getPriceList(U9PrVO u9PrVO) {
        List<PriceVO> voList = Lists.newArrayList();

        PriceVO highestPrice = new PriceVO();
        highestPrice.setType("历史最高价");
        highestPrice.setSupName("-");
        highestPrice.setNumberInterval("-");
        highestPrice.setPrice(poItemService.getHighestPrice(u9PrVO.getItemCode()));
        voList.add(highestPrice);
        PriceVO lowestPrice = new PriceVO();
        lowestPrice.setType("历史最低价");
        lowestPrice.setSupName("-");
        lowestPrice.setNumberInterval("-");
        lowestPrice.setPrice(poItemService.getLowestPrice(u9PrVO.getItemCode()));
        voList.add(lowestPrice);
        PriceVO lastPrice = new PriceVO();
        lastPrice.setType("最近价");
        lastPrice.setSupName("-");
        lastPrice.setNumberInterval("-");
        PoItemEntity poItemEntity = poItemService.getLastPoInfos(u9PrVO.getItemCode(), u9PrVO.getItemName());
        if (poItemEntity != null) {
            lastPrice.setPrice(poItemEntity.getPrice());
        }
        voList.add(lastPrice);

        //框架协议
        u9PrVO.getPriceNumList().forEach(entity -> {
            //获取有效期间内指定数量的所有区间价格
            List<PriceFrameEntity> priceFrameEntities = priceFrameService.getNumberInterval(u9PrVO.getItemCode(), new BigDecimal(entity));
            if (priceFrameEntities.size() > 0) {
                priceFrameEntities.forEach(priceFrameEntity -> {
                    PriceVO u9Price = new PriceVO();
                    u9Price.setType("框架协议");
                    u9Price.setSupName(priceFrameEntity.getSupName());
                    u9Price.setPrice(priceFrameEntity.getPrice());
                    u9Price.setNumberInterval(priceFrameEntity.getLimitMin() + "~" + priceFrameEntity.getLimitMax());
                    voList.add(u9Price);
                });
            }
        });
        //数学模型
        List<MmVolumeCalculateEntity> mmVolumeCalculateEntities = mmVolumeCalculateService.getByItemCode(u9PrVO.getItemCode());
        if (mmVolumeCalculateEntities.size() > 0) {
            mmVolumeCalculateEntities.forEach(entity -> {
                PriceVO u9Price = new PriceVO();
                u9Price.setType("数学模型");
                u9Price.setSupName(entity.getSupName());
                u9Price.setNumberInterval("-");
                u9Price.setPrice(entity.getPrice());
                voList.add(u9Price);
            });
        }

        //去重
        LinkedHashSet<PriceVO> hashSet = new LinkedHashSet<>(voList);
        return new ArrayList<>(hashSet);
    }

    @Override
    public void inquiryExport(PrReq prReq, HttpServletResponse response) {
        List<U9PrDTO> list = this.baseMapper.selectListByReq(prReq);
        List<U9PrInquiryExcelDTO> dtoList = Lists.newArrayList();
        list.forEach(temp -> {
            U9PrInquiryExcelDTO dto = BeanUtil.copy(temp, U9PrInquiryExcelDTO.class);
            dtoList.add(dto);
        });
        ExcelUtils.defaultExport(dtoList, U9PrInquiryExcelDTO.class, "询价单" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public void inquiryExportOfOthers(PrReq prReq, HttpServletResponse response) {
        List<U9PrDTO> list = this.baseMapper.selectListByReqOfOthers(prReq);
        List<U9PrInquiryExcelDTO> dtoList = Lists.newArrayList();
        list.forEach(temp -> {
            U9PrInquiryExcelDTO dto = BeanUtil.copy(temp, U9PrInquiryExcelDTO.class);
            dtoList.add(dto);
        });
        ExcelUtils.defaultExport(dtoList, U9PrInquiryExcelDTO.class, "询价单" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public void flowExport(PrReq prReq, HttpServletResponse response) {
        List<U9PrDTO> list = new ArrayList<>();
        if (prReq.getSource() != null && !prReq.getSource().isEmpty()) {
            list = this.baseMapper.selectFlowListToExport(prReq);
        } else {
            list = this.baseMapper.selectFlowPageList(prReq);

        }
        List<U9PrFlowExcelDTO> dtoList = Lists.newArrayList();
        list.forEach(temp -> {
            U9PrFlowExcelDTO dto = BeanUtil.copy(temp, U9PrFlowExcelDTO.class);
            if (dto.getProNo() == null || dto.getProNo().isEmpty()) {
                dto.setProNo(temp.getApsProNo());
            }

            if (temp.getQuotePrice() != null) {
                dto.setQuotePrice(temp.getQuotePrice().setScale(BigDecimal.ROUND_HALF_UP, 2).toString());
            }

            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(), dto.getItemName());
            if (poItemEntity != null) {
                dto.setLastPrice(poItemEntity.getPrice());
                dto.setLastSupName(poItemEntity.getSupName());
            }
            if (dto.getPiLastSupName() != null && !dto.getPiLastSupName().isEmpty()) {
                dto.setLastSupName(dto.getPiLastSupName());
            }
            dtoList.add(dto);
        });
        ExcelUtils.defaultExport(dtoList, U9PrFlowExcelDTO.class, "流标单" + DateUtil.formatDate(new Date()), response);
    }


    @Override
    public void flowExportOfOthers(PrReq prReq, HttpServletResponse response) {
        List<U9PrDTO> list = this.baseMapper.selectFlowPageOfOthersForExcel(prReq);
        List<U9PrFlowExcelDTO> dtoList = Lists.newArrayList();
        list.forEach(temp -> {
            U9PrFlowExcelDTO dto = BeanUtil.copy(temp, U9PrFlowExcelDTO.class);
            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(), dto.getItemName());
            if (poItemEntity != null) {
                dto.setLastPrice(poItemEntity.getPrice());
                dto.setLastSupName(poItemEntity.getSupName());
            }

            if (dto.getPiLastSupName() != null && !dto.getPiLastSupName().isEmpty()) {
                dto.setLastSupName(dto.getPiLastSupName());
            }

            List<IoEntity> ioEntities = this.baseMapper.selectPriceFromIo(temp.getId());
            if (ioEntities.size() > 0 && !prReq.getStatuss().equals("20,40")) {
                dto.setQuotePrice(ioEntities.get(0).getQuotePrice().setScale(2, BigDecimal.ROUND_DOWN).toString());
                dto.setSupCode(ioEntities.get(0).getSupCode());
                dto.setSupName(ioEntities.get(0).getSupName());
            }
            prReq.setId(temp.getId());
            IoEntity ioEntity = ioService.getBySourceAndPrId(prReq);
            if (ioEntity != null) {
                dto.setRemark(ioEntity.getRemark());
            }
            dtoList.add(dto);
        });
        ExcelUtils.defaultExport(dtoList, U9PrFlowExcelDTO.class, "流标单" + DateUtil.formatDate(new Date()), response);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean flowBatchExcel(SubmitPriceReq submitPriceReq) {
        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();
        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
            Long id = submitPriceDTO.getId();
            U9PrEntity u9PrEntity = getById(id);
            if (u9PrEntity == null) {
                throw new RuntimeException("未找到PR：" + "id：" + id);
            }
            if (FLOW_SUBMIT.equals(submitPriceDTO.getType())) {
                String attachmentAndRemark = submitPriceDTO.getAttachment();
                u9PrEntity.setAttachment(attachmentAndRemark);
                updateById(u9PrEntity);
            } else {
                PrReq prReq = new PrReq();
                prReq.setId(id);
                prReq.setStatuss(STATUS_WINBID.toString());
                IoEntity ioEntity = ioService.getBySourceAndPrId(prReq);
                if (ioEntity == null) {
                    throw new RuntimeException("未找到IO：" + "prId：" + id + "状态：" + STATUS_WINBID);
                }
                ioEntity.setAttachment(submitPriceDTO.getAttachment());
                if (StringUtil.isNotBlank(submitPriceDTO.getRemark())) {
                    ioEntity.setRemark(submitPriceDTO.getRemark());
                }
                u9PrEntity.setAttachment(submitPriceDTO.getAttachment());
                updateById(u9PrEntity);
                ioService.updateById(ioEntity);
            }
        }
        return true;
    }

    @Override
    public boolean flowBatchExcelOfSupItem(SubmitPriceReq submitPriceReq) {
        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();
        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
            if (submitPriceDTO.getType().equals("prId")) {
                // 外面的批量上传
                List<IoEntity> ioEntities = ioService.getByPrId(submitPriceDTO.getId());
                for (IoEntity ioEntity : ioEntities) {
                    ioEntity.setAttachment(submitPriceDTO.getAttachment());
                    if (StringUtil.isNotBlank(submitPriceDTO.getRemark())) {
                        ioEntity.setRemark(submitPriceDTO.getRemark());
                    }
                    ioService.updateById(ioEntity);
                }
            } else {
                // 里面的批量上传
                IoEntity ioEntity = ioService.getById(submitPriceDTO.getIoId());
                if (ioEntity == null) {
                    throw new RuntimeException("未找到小零件的IO：" + "Id：" + submitPriceDTO.getIoId());
                }
                ioEntity.setAttachment(submitPriceDTO.getAttachment());
                if (StringUtil.isNotBlank(submitPriceDTO.getRemark())) {
                    ioEntity.setRemark(submitPriceDTO.getRemark());
                }
                ioService.updateById(ioEntity);
            }
        }
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean flowBatchAudit(SubmitPriceReq submitPriceReq) {
        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();

        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
            Long id = submitPriceDTO.getId();
            U9PrEntity u9PrEntity = getById(id);
            if (u9PrEntity == null) {
                throw new RuntimeException("未找到PR：" + "id：" + id);
            }

            u9PrEntity.setStatus(STATUS_WINBID);
            updateById(u9PrEntity);
        }
        return true;
    }

    @Override
    public boolean flowAudit(IoEntity ioReq) {
        IoEntity ioEntity = ioService.getById(ioReq.getId());
        if (ioEntity == null) {
            throw new RuntimeException("未找到IO：" + ioReq.getId());
        }
        U9PrEntity u9PrEntity = getById(ioEntity.getPrId());
        if (u9PrEntity == null) {
            throw new RuntimeException("未找到PR：" + ioEntity.getPrId());
        }
        ioService.updateById(ioEntity);
        u9PrEntity.setStatus(STATUS_WINBID);
        return updateById(u9PrEntity);
    }

    @Override
    public IPage<U9PrDTO> inquiryPage(IPage<U9PrDTO> page, PrReq prReq) {
        Integer codeCount = 0;
        if (prReq.getItemCode() != null) {
            codeCount = prReq.getItemCode().split(",").length;
        }
        prReq.setItemCodeCount(codeCount);
        IPage<U9PrDTO> retPage = this.baseMapper.selectInquiryPage(page, prReq);
        if (retPage.getRecords() == null) {
            return retPage;
        }
        for (U9PrDTO dto : retPage.getRecords()) {
            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(), dto.getItemName());
            if (poItemEntity != null) {
                dto.setLastPrice(poItemEntity.getPrice());
                dto.setLastSupName(poItemEntity.getSupName());
            }
            if (dto.getPiLastSupName() != null && !dto.getPiLastSupName().isEmpty()) {
                dto.setLastSupName(dto.getPiLastSupName());
            }
            // zlw add 20210426 NO.10
            if ((!dto.getQoNo().isEmpty()) && (!dto.getItemName().isEmpty())) {
                String supRemark = this.baseMapper.selectSupRemarks(dto.getQoNo(), dto.getItemName());
                dto.setRequisitionRemark(supRemark);
            }
        }
        return retPage;
    }

    @Override
    public IPage<U9PrDTO> inquiryCheckPage(IPage<U9PrDTO> page, PrReq prReq) {
        String mRoleId = paramService.getValue("purch_manager.role_id");
        if (!StringUtil.containsAny(getUser().getRoleId(), mRoleId)) {
            prReq.setPurchCode(getUser().getAccount());
        }
        IPage<U9PrDTO> u9Page = this.baseMapper.selectInquiryCheckPage(page, prReq);
        getWinIo(prReq, u9Page);
        return u9Page;
    }

    @Override
    public IPage<U9PrDTO> flowPage(IPage<U9PrDTO> page, PrReq prReq) {

        if (prReq.getItemCode() != null && !prReq.getItemCode().isEmpty() && prReq.getItemCode().indexOf(",") > -1) {
            prReq.setItemCodeCount(1);
        }

        if (prReq.getPrCode() != null && !prReq.getPrCode().isEmpty() && prReq.getPrCode().indexOf(",") > -1) {
            prReq.setPrCodeCount(1);
        }

        if (prReq.getBizType() != null && prReq.getBizType().toString().equals("2")) {
            prReq.setIsVmi("1");
            prReq.setBizType(null);
        }

        IPage<U9PrDTO> retPage = baseMapper.selectFlowPage(page, prReq);

        List<U9PrDTO> retList = new ArrayList<>();
        if (retPage.getRecords() == null) {
            return retPage;
        }

        Map<String, U9PrDTO> qzfzMap = new LinkedHashMap<>();

        // 第一次将球座的阀座信息都带出来
        for (U9PrDTO dto : retPage.getRecords()) {
            // 先插入原始数据
            String key = dto.getPrCode() + dto.getPrLn() + dto.getItemCode();
            qzfzMap.put(key, dto);

            // 球座附带阀座信息新增逻辑
            String itemName = dto.getItemName();
            String FZItemInfo = "";
            if (itemName.indexOf("球") > -1) {
                FZItemInfo = this.getFZItemInfo(dto.getItemCode());
                dto.setRelationFzItemInfo(FZItemInfo);
            }
            if (!FZItemInfo.isEmpty()) {
                List<String> fzItemCodeList = Arrays.asList(FZItemInfo.split(","));
                for (String fzItemCode : fzItemCodeList) {
                    List<U9PrDTO> listFz = this.baseMapper.selectFzList(fzItemCode.split(":")[0], prReq.getStatuss());
                    if (listFz != null && listFz.size() > 0) {
                        for (U9PrDTO fzdto : listFz) {
                            String fzKey = fzdto.getPrCode() + fzdto.getPrLn() + fzdto.getItemCode();
                            if (qzfzMap.containsKey(fzKey)) {
                                // 防止数据重复
                                fzdto.setCodeType(qzfzMap.get(fzKey).getCodeType());
                                qzfzMap.remove(fzKey);
                                qzfzMap.put(fzKey, fzdto);
                            } else {
                                qzfzMap.put(fzKey, fzdto);
                            }
                        }
                    }
                }
            }
        }

        // 将排好序并且去重后的MAP转换成list
        retList = qzfzMap.values().stream().collect(Collectors.toList());

        // 第二次正常遍历
        for (U9PrDTO dto : retList) {
            // 承诺交期就是从U9那边传来的要求交期
            dto.setPromiseDate(dto.getReqDate());

            // 球座附带阀座信息新增逻辑
            String itemName = dto.getItemName();
            if (itemName.indexOf("球") > -1) {
                String FZItemInfo = this.getFZItemInfo(dto.getItemCode());
                dto.setRelationFzItemInfo(FZItemInfo);
            }

            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(), dto.getItemName());
            if (poItemEntity != null) {
                dto.setLastPrice(poItemEntity.getPrice());
                dto.setLastSupName(poItemEntity.getSupName());
            }


            if (dto.getPiLastSupName() != null && !dto.getPiLastSupName().isEmpty()) {
                dto.setLastSupName(dto.getPiLastSupName());
            }

            // 项目号如果是null或者是空，取 proNo
            if (dto.getProNo() == null || dto.getProNo().isEmpty()) {
                dto.setProNo(dto.getApsProNo());
            }
//            dto.setHighestPrice(poItemService.getHighestPrice(dto.getItemCode()));
//            dto.setLowestPrice(poItemService.getLowestPrice(dto.getItemCode()));
//            Item item = itemService.getByCode(dto.getItemCode());
//            if(item != null){
//                dto.setPurchMix(item.getPurchMix());
//                dto.setStockLowerLimit(item.getStockLowerLimit());
//            }
        }

        retPage.setRecords(retList);
        return retPage;
    }

    private String getFZItemInfo(String itemCode) {
        String fzItemInfo = "";
        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;
        try {
            Class.forName(driver);
            //1.获取连接
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            @Cleanup Statement stmt = conn.createStatement();
            //2.准备预编译的sql
            StringBuffer sql = new StringBuffer();
            sql.append("select * from atwplm.Cust_Tee_Relationship where ballcode = '").append(itemCode).append("'");

            //3.执行
            @Cleanup ResultSet resultSet = stmt.executeQuery(sql.toString());

            while (resultSet.next()) {
                String fzItemCode = resultSet.getString("valveseatcode");
                String fzItemName = resultSet.getString("valveseatname");
                fzItemInfo = fzItemInfo + fzItemCode + ":" + fzItemName + ",";
            }
        } catch (Exception e) {
            throw new RuntimeException("查询oracle数据库出错！");
        } finally {
            if (fzItemInfo.isEmpty()) {
                return "";
            } else {
                return fzItemInfo.substring(0, fzItemInfo.length() - 1);
            }
        }
    }


    @Override
    public IPage<U9PrDTO> flowPageOfOthers(IPage<U9PrDTO> page, PrReq prReq) {
        String mRoleId = paramService.getValue("purch_manager.role_id");
        if (!StringUtil.containsAny(getUser().getRoleId(), mRoleId)) {
            prReq.setPurchCode(getUser().getAccount());
        } else {
            prReq.setPurchCode(null);
        }

        IPage<U9PrDTO> retPage = baseMapper.selectFlowPageOfOthers(page, prReq);
        if (retPage.getRecords() == null) {
            return retPage;
        }
        for (U9PrDTO dto : retPage.getRecords()) {
            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(), dto.getItemName());
            if (poItemEntity != null) {
                dto.setLastPrice(poItemEntity.getPrice());
                dto.setLastSupName(poItemEntity.getSupName());
            }
            if (dto.getPiLastSupName() != null && !dto.getPiLastSupName().isEmpty()) {
                dto.setLastSupName(dto.getPiLastSupName());
            }
            dto.setStandardPrice(dto.getLaborCost().multiply(new BigDecimal("1.4")).add(dto.getMaterialCost()));
        }
        return retPage;
    }

    @Override
    public IPage<U9PrDTO> flowCheckPage(IPage<U9PrDTO> page, PrReq prReq) {
        String mRoleId = paramService.getValue("purch_manager.role_id");
        //211211 徐少勃  可以审核2002002
        if (!(StringUtil.containsAny(getUser().getRoleId(), mRoleId)||getUser().getAccount().equals("211211"))) {
            prReq.setPurchCode(getUser().getAccount());
        }
        if(getUser().getAccount().equals("211211")){
            prReq.setItemCode("20020002");
        }
        IPage<U9PrDTO> u9Page = this.baseMapper.selectFlowCheckPage(page, prReq);
        for (U9PrDTO dto : u9Page.getRecords()) {
            PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(), dto.getItemName());
            if (poItemEntity != null) {
                dto.setLastPrice(poItemEntity.getPrice());
                dto.setLastSupName(poItemEntity.getSupName());
            }
            if (dto.getPiLastSupName() != null && !dto.getPiLastSupName().isEmpty()) {
                dto.setLastSupName(dto.getPiLastSupName());
            }
        }

        getWinIo(prReq, u9Page);
        return u9Page;
    }


    @Override
    public IPage<U9PrDTO> flowNoSupPage(IPage<U9PrDTO> page, PrReq prReq) {
        // 如果是无供应商状态的，则 找出 流标类型 是no_sup 且是 流标状态 的
        if ((STATUS_FLOW_NOSUP.toString()).equals(prReq.getStatuss())) {
            prReq.setStatuss(STATUS_FLOW.toString());
            prReq.setFlowType(FLOW_TYPE_NOSUP);
        }
        IPage<U9PrDTO> u9Page = this.baseMapper.selectPageWithoutIo(page, prReq);
        getWinIo(prReq, u9Page);
        return u9Page;
    }

    /**
     * 获取中标的IO
     * [
     *
     * @param prReq  PrReq
     * @param u9Page IPage
     */
    private void getWinIo(PrReq prReq, IPage<U9PrDTO> u9Page) {
        for (U9PrDTO record : u9Page.getRecords()) {
            prReq.setId(record.getId());
            IoEntity ioEntity = ioService.getBySourceAndPrId(prReq);
            // zlw add 20210426 NO.10
            if (!record.getQoNo().isEmpty() && !record.getItemName().isEmpty()) {
                String supRemark = this.baseMapper.selectSupRemarks(record.getQoNo(), record.getItemName());
                if (supRemark != null) {
                    ioEntity.setSupRemark(supRemark);
                }
            }

            if (ioEntity != null) {
                record.setIoId(ioEntity.getId());
                record.setQuoteDate(ioEntity.getQuoteDate());
                record.setPromiseDate(ioEntity.getPromiseDate());
                record.setSupCode(ioEntity.getSupCode());
                record.setSupName(ioEntity.getSupName());
                record.setQuotePrice(ioEntity.getQuotePrice());
                record.setSupRemark(ioEntity.getSupRemark());
                record.setIsPersent(ioEntity.getIsPersent());
                record.setReferencePrice(mmVolumeCalculateService.getPrice(ioEntity.getItemCode(), ioEntity.getSupCode()));
                record.setRemark(ioEntity.getRemark());
                record.setAttachment(ioEntity.getAttachment());

                record.setHighestPrice(poItemService.getHighestPrice(record.getItemCode()));
                record.setLowestPrice(poItemService.getLowestPrice(record.getItemCode()));
                PoItemEntity poItemEntity = poItemService.getLastPoInfos(record.getItemCode(), record.getItemName());
                if (poItemEntity != null) {
                    record.setLastPrice(poItemEntity.getPrice());
                    record.setLastSupName(poItemEntity.getSupName());
                }

            }
        }
    }

    /**
     * 询价中台统计
     *
     * @param status String
     * @param check  Integer
     * @return int
     */
    private int getInquiryCount(String status, String type, Integer check) {
        PrReq prReq = new PrReq();
        prReq.setStatuss(status);
        prReq.setPurchaseType(type);
        String mRoleId = paramService.getValue("purch_manager.role_id");
        if (!StringUtil.containsAny(getUser().getRoleId(), mRoleId)) {
            prReq.setPurchCode(getUser().getAccount());
        }
        prReq.setInquiryWays("'" + INQUIRYWAY_EXCLUSIVE + "'" + "," + "'" + INQUIRYWAY_COMPETE + "'");
        if (status.equals(STATUS_INQUIRY.toString()) || status.equals(STATUS_QUOTED.toString())) {
            return this.baseMapper.selectInquiryList(prReq).size();
        }
        if (check != null) {
            prReq.setCheck(check);
        }
        prReq.setSource("quote");
        return this.baseMapper.selectInquiryCheckList(prReq).size();
    }

    /**
     * 流标中台统计
     *
     * @param status String
     * @return int
     */
    private int getFlowCount(String status, String type) {
        PrReq prReq = new PrReq();
        if (!status.equals(STATUS_FLOW_NOSUP)) {
            prReq.setStatuss(status);
        } else {
            // 无供应商流标
            prReq.setStatuss(STATUS_FLOW.toString());
            prReq.setFlowType("FLOW_TYPE_NOSUP");
        }
        prReq.setPurchaseType(type);
        String mRoleId = paramService.getValue("purch_manager.role_id");
        if (!StringUtil.containsAny(getUser().getRoleId(), mRoleId)) {
            prReq.setPurchCode(getUser().getAccount());
        }

        // inner
        if (PURCHASE_TYPE_INNER.equals(type)) {
            // 待处理、
            if (status.equals(STATUS_HANG + "," + STATUS_FLOW) || status.equals(STATUS_FLOW_SUBMIT.toString()) || status.equals(STATUS_WINBID.toString())) {
                int all = this.baseMapper.selectFlowPageList(prReq).size();
                int noSup = this.baseMapper.selectPageWithoutIo(new Page<>(), prReq).getRecords().size();
                return all - noSup;
            }
            // 待提交、待审核
            if (status.equals(STATUS_FLOW_SUBMIT.toString()) || status.equals(STATUS_WINBID.toString())) {
                return this.baseMapper.selectFlowPageList(prReq).size();
            }
            // 无供应商
            if (status.equals(STATUS_FLOW_NOSUP.toString())) {
                prReq.setStatuss("40");
                prReq.setPurchCode(null);
                return this.baseMapper.selectPageWithoutIo(new Page<>(), prReq).getRecords().size();
            }
            // 待下单
            prReq.setSource("purch_submit");
            return this.baseMapper.selectFlowPageOfOthers(new Page<>(), prReq).getRecords().size();

        } else {
            // normal
            if (status.equals(STATUS_HANG + "," + STATUS_FLOW)) {
                return this.baseMapper.selectFlowPageList(prReq).size();
            }
            // 待审核
            prReq.setSource("purch_submit");
            return this.baseMapper.selectFlowCheckList(prReq).size();
        }
    }

    /**
     * getCount
     *
     * @param status String
     * @param type   String
     * @return int
     */
    private int getCount(String status, String type) {
        PrReq prReq = new PrReq();
        prReq.setStatuss(status);
        String mRoleId = paramService.getValue("purch_manager.role_id");
        if (!StringUtil.containsAny(getUser().getRoleId(), mRoleId)) {
            prReq.setPurchCode(getUser().getAccount());
        }
        if ("inquiry".equals(type)) {
            prReq.setInquiryWays("'" + INQUIRYWAY_EXCLUSIVE + "'" + "," + "'" + INQUIRYWAY_COMPETE + "'");
            if (status.equals(STATUS_INQUIRY.toString()) || status.equals(STATUS_QUOTED.toString())) {
                return this.baseMapper.selectWaitListByReq(prReq).size();
            }
            prReq.setSource(IIoService.SOURCE_QUOTE);
            prReq.setIsFlow(0);
        } else {
            //流标待处理
            if (status.equals(STATUS_HANG + "," + STATUS_FLOW)) {
                return this.baseMapper.selectWaitListByReq(prReq).size();
            }
            prReq.setIsFlow(1);
            prReq.setSource(IIoService.SOURCE_PURCHSUBMIT);
        }
        return this.baseMapper.selectListByReq(prReq).size();
    }

    /**
     * @return
     */
    @Override
    public U9PrDTO getDtoById(Long id) {
        U9PrEntity entity = getById(id);
        U9PrDTO dto = BeanUtil.copy(entity, U9PrDTO.class);

        QueryWrapper<IoEntity> ioQw = Condition.getQueryWrapper(new IoEntity()).eq("pr_id", id);
        dto.setIoTotal(ioService.count(ioQw));
        ioQw.eq("status", IIoService.STATUS_QUOTED);
        dto.setQuotedIoTotal(ioService.count(ioQw));
        dto.setHighestPrice(poItemService.getHighestPrice(dto.getItemCode()));
        dto.setLowestPrice(poItemService.getLowestPrice(dto.getItemCode()));
        PoItemEntity poItemEntity = poItemService.getLastPoInfos(dto.getItemCode(), dto.getItemName());
        if (poItemEntity != null) {
            dto.setLastPrice(poItemEntity.getPrice());
            dto.setLastSupName(poItemEntity.getSupName());
        }
        return dto;
    }

    /**
     * 待处理询价单数量
     *
     * @return
     */
    @Override
    public int toProcessCount() {
        String mRoleId = paramService.getValue("purch_manager.role_id");
        if (StringUtil.containsAny(getUser().getRoleId(), mRoleId)) {
            PrReq prReq = new PrReq();
            prReq.setStatuss(STATUS_WINBID.toString());
            prReq.setInquiryWays("'" + INQUIRYWAY_EXCLUSIVE + "'" + "," + "'" + INQUIRYWAY_COMPETE + "'");
            prReq.setSource(IIoService.SOURCE_QUOTE);
            return this.baseMapper.selectInquiryCheckList(prReq).size();
        }
        QueryWrapper<U9PrEntity> query = Condition.getQueryWrapper(new U9PrEntity()).in("status", STATUS_INQUIRY, STATUS_QUOTED)
            .in("inquiry_way", INQUIRYWAY_EXCLUSIVE, INQUIRYWAY_COMPETE)
            .eq("purch_code", getUser().getAccount())
            .eq("purchase_type", PURCHASE_TYPE_NORMAL);
        return count(query);
    }

    /**
     * 待处理询价单数量-小零件
     *
     * @return
     */
    @Override
    public int toProcessCountOfOthers() {
        String mRoleId = paramService.getValue("purch_manager.role_id");
        if (StringUtil.containsAny(getUser().getRoleId(), mRoleId)) {
            PrReq prReq = new PrReq();
            prReq.setStatuss(STATUS_WINBID.toString());
            prReq.setInquiryWays("'" + INQUIRYWAY_EXCLUSIVE + "'" + "," + "'" + INQUIRYWAY_COMPETE + "'");
            prReq.setSource(IIoService.SOURCE_QUOTE);
            prReq.setPurchaseType(PURCHASE_TYPE_INNER);
            return this.baseMapper.selectInquiryCheckList(prReq).size();
        }
        QueryWrapper<U9PrEntity> query = Condition.getQueryWrapper(new U9PrEntity()).in("status", STATUS_INQUIRY, STATUS_QUOTED)
            .in("inquiry_way", INQUIRYWAY_EXCLUSIVE, INQUIRYWAY_COMPETE)
            .eq("purch_code", getUser().getAccount())
            .eq("purchase_type", PURCHASE_TYPE_INNER);
        return count(query);
    }

    /**
     * 待处理流标单数量
     *
     * @return
     */
    @Override
    public int flowCount() {
        String mRoleId = paramService.getValue("purch_manager.role_id");
        if (StringUtil.containsAny(getUser().getRoleId(), mRoleId)) {
            PrReq prReq = new PrReq();
            prReq.setStatuss(STATUS_WINBID.toString());
            prReq.setSource(IIoService.SOURCE_PURCHSUBMIT);
            return this.baseMapper.selectFlowCheckList(prReq).size();
        }
        QueryWrapper<U9PrEntity> query = Condition.getQueryWrapper(new U9PrEntity()).in("status", STATUS_HANG, STATUS_FLOW)
            .eq("purch_code", getUser().getAccount())
            .eq("purchase_type", PURCHASE_TYPE_NORMAL);
        return count(query);
    }


    /**
     * 待处理流标数量-小零件
     *
     * @return
     */
    @Override
    public int flowCountOfOthers() {
        String mRoleId = paramService.getValue("purch_manager.role_id");
        if (StringUtil.containsAny(getUser().getRoleId(), mRoleId)) {
            PrReq prReq = new PrReq();
            prReq.setStatuss(STATUS_WINBID.toString());
            prReq.setSource(IIoService.SOURCE_PURCHSUBMIT);
            prReq.setPurchaseType(PURCHASE_TYPE_INNER);
            return this.baseMapper.selectFlowCheckListOfOthers(prReq).size();
        }

        if (getUser().getAccount().equals("150323")) {
            PrReq prReq = new PrReq();
            prReq.setStatuss(STATUS_HANG.toString() + "," + STATUS_FLOW.toString());
            prReq.setSource(IIoService.SOURCE_PURCHSUBMIT);
            prReq.setPurchaseType(PURCHASE_TYPE_INNER);
            prReq.setPurchCode(getUser().getAccount());
            int all = this.baseMapper.selectFlowPageList(prReq).size();
            int noSup = this.baseMapper.selectPageWithoutIo(new Page<>(), prReq).getRecords().size();
            prReq.setPurchCode(null);
            int noSupOfAll = this.baseMapper.selectPageWithoutIo(new Page<>(), prReq).getRecords().size();
            return all - noSup + noSupOfAll;
        } else {
            PrReq prReq = new PrReq();
            prReq.setStatuss(STATUS_HANG.toString() + "," + STATUS_FLOW.toString());
            prReq.setSource(IIoService.SOURCE_PURCHSUBMIT);
            prReq.setPurchaseType(PURCHASE_TYPE_INNER);
            prReq.setPurchCode(getUser().getAccount());
            int all = this.baseMapper.selectFlowPageList(prReq).size();
            int noSup = this.baseMapper.selectPageWithoutIo(new Page<>(), prReq).getRecords().size();
            return all - noSup;
        }
    }


    @Override
    public void letPrFlow(U9PrEntity pr, String flowType) {
        pr.setStatus(STATUS_FLOW);
        pr.setFlowType(flowType);
        updateById(pr);
        //  修改io状态
        IoEntity upIo = new IoEntity();
        upIo.setStatus(IIoService.STATUS_CLOSE);
        IoEntity whereIo = new IoEntity();
        whereIo.setPrId(pr.getId());
        ioService.update(upIo, Condition.getQueryWrapper(whereIo));
    }

    @Override
    public boolean statusToFlow(Long prid) {
        U9PrEntity pr = getById(prid);
        pr.setStatus(STATUS_FLOW);
        pr.setFlowType(FLOW_TYPE_INQPRICE_REFUSE);

        IoEntity upIo = new IoEntity();
        upIo.setStatus(IIoService.STATUS_LOSEBID);

        ioService.update(upIo, Wrappers.<IoEntity>update().eq("pr_code", pr.getPrCode()).eq("pr_ln", pr.getPrLn()));
        return updateById(pr);
    }

    @Override
    public boolean updateBatch(List<U9PrEntity> prList) {
        for (U9PrEntity pr : prList) {
            U9PrEntity oldpr = getById(pr.getId());
            if (oldpr == null) {
                continue;
            }
            if (!Objects.equals(oldpr.getEndUser(), pr.getEndUser())) {
                pr.setEndUserUpdateTimes(oldpr.getEndUserUpdateTimes() + 1);
            }
            updateById(pr);
        }
        return true;
    }


    /**
     * 自动获取供应商信息
     *
     * @param u9PrDTOS
     * @return
     */
    @Override
    public List<ItemInfoDTO> autoRetrieve(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        // 球座组件  拆解成为 球体  阀座 20221205
        List<ItemInfoDTO> itemInfoDTOSOfQiuZuo = autoRetrieveOfQiuZuoNew(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfQiuZuo);

        // 联轴器  移出小零件 20221125
        List<ItemInfoDTO> itemInfoDTOSOfLZQ = autoRetriveOfLZQ(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfLZQ);

        // 包装箱
        List<ItemInfoDTO> itemInfoDTOSOfBZX = autoRetrieveOfBZX(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfBZX);

        // 法兰
        List<ItemInfoDTO> itemInfoDTOSOfFL = autoRetrieveOfFL(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfFL);

        // 底轴
        List<ItemInfoDTO> itemInfoDTOSOfDZ = autoRetrieveOfDZ(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfDZ);

        // 铸件
        List<ItemInfoDTO> itemInfoDTOSOfZhuJian = autoRetrieveOfZhuJian(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfZhuJian);

        // 锻件 或者  锻材
        List<ItemInfoDTO> itemInfoDTOSOfDuanJian = autoRetrieveOfDuanJian(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfDuanJian);


        // 全程委外
        List<ItemInfoDTO> itemInfoDTOSOfWW = autoRetrieveOfWW(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfWW);

        // 管棒料maily
        List<ItemInfoDTO> itemInfoDTOGuanBangLiao = authRetriveOfGuanBangLiao(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOGuanBangLiao);

        // 小零件
        List<ItemInfoDTO> itemInfoDTOSOfXLJ = autoRetriveOfXLJ(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfXLJ);

        /*List<ItemInfoDTO> itemInfoDTOSOfQiuZuo = autoRetrieveOfQiuZuo(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfQiuZuo);*/



        return itemInfoDTOS;
    }

    /**
     * 底轴自动下单
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfDZ(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        List<ItemInfoEntityOfDZ> itemInfoEntityOfDZInsert = new ArrayList<>();


        for (U9PrDTO u9Prs : u9PrDTOS) {
            List<PriceVO> priceVOS = new ArrayList<>();

            // 若不是球座，则下一条
            if (u9Prs.getItemName().indexOf("底轴") < 0) {
                continue;
            }
            //获取手动输入的参数
            String outersize="";
            String lengthsize="";
            String poutersize="";
            String plengthsize="";
            ItemInfoEntityBasOfDZ itemInfoEntityBasOfDZ = u9PrMapper.selectBasicItemInfoOfDZ(u9Prs.getItemCode());

            if (itemInfoEntityBasOfDZ == null) {
                continue;
            }

            outersize=itemInfoEntityBasOfDZ.getOuterSize();//外径
            lengthsize=itemInfoEntityBasOfDZ.getInnerSize();//长度
            poutersize=itemInfoEntityBasOfDZ.getPOuterSize();//喷涂外径
            plengthsize=itemInfoEntityBasOfDZ.getPInnerSize();//喷涂长度

            if(StringUtils.isBlank(outersize)||StringUtils.isBlank(lengthsize)||StringUtils.isBlank(poutersize)||StringUtils.isBlank(plengthsize)){
                continue;
            }

            //计算余量
            String outerSizeRule="";//外径公式
            String lengthSizeRule="";//余量表长度公式


            ItemInfoEntityOfDZ itemInfoEntityOfDZ=new ItemInfoEntityOfDZ();
            itemInfoEntityOfDZ.setOuterSize(outersize);
            List<ItemInfoEntityOfDZ> itemInfoEntityOfDZS = this.baseMapper.selectDZResItemInfo(itemInfoEntityOfDZ);//多家供应商

            ItemInfoDTO itemInfoDTO=new ItemInfoDTO();//一个pr行  一个返回


            for (ItemInfoEntityOfDZ infoEntityOfDZ:itemInfoEntityOfDZS) {

                ItemInfoEntityOfDZ dzReport=new ItemInfoEntityOfDZ();

                dzReport.setSupName(infoEntityOfDZ.getSupName());
                dzReport.setSupCode(infoEntityOfDZ.getSupCode());
                dzReport.setItemCode(u9Prs.getItemCode()); // (料号)
                dzReport.setItemName(u9Prs.getItemName()); // (料名)
                dzReport.setOuterSizeOld(outersize);
                dzReport.setQuantity(String.valueOf(u9Prs.getPriceNum()));


                itemInfoDTO.setId(u9Prs.getId().toString()); // (pr的id)
                itemInfoDTO.setItemCode(u9Prs.getItemCode()); // (料号)
                itemInfoDTO.setItemName(u9Prs.getItemName()); // (料名)

                if (itemInfoEntityOfDZS.size()>1){
                    itemInfoDTO.setSupColorType("1");
                }else{
                    itemInfoDTO.setSupColorType("0");
                }



                itemInfoEntityOfDZ.setSupCode(infoEntityOfDZ.getSupCode());//写入供应商

                outerSizeRule = infoEntityOfDZ.getOuterSize();
                lengthSizeRule=infoEntityOfDZ.getLengthSize();
                BigDecimal lengthSizeRes=new BigDecimal(lengthsize).add(new BigDecimal(lengthSizeRule));

                dzReport.setLengthSize(lengthsize);
                dzReport.setLengthRes(lengthSizeRule);
                dzReport.setHeightSize(String.valueOf(lengthSizeRes));

                String outerSizeRes=this.handleXLJOuterSize(outersize,outerSizeRule);//外径余量
                dzReport.setOuterRes(outerSizeRule);
                dzReport.setOuterSize(outerSizeRes);
                //计算材料费 D1^2*π/4*L1*7.98/1000000*材料单价*1.2
                String caizhi="";
                String material_price="";
                String k="";
                BigDecimal pai = new BigDecimal("3.14"); // π
                ItemInfoEntityOfDZ itemInfoEntityOfDZ1 = this.baseMapper.selectItemPriceOfDZ(itemInfoEntityOfDZ);
                BigDecimal material_cost=new BigDecimal("0");
                if (itemInfoEntityOfDZ1 != null) {
                    caizhi=itemInfoEntityOfDZ1.getMaterial();
                    material_price=itemInfoEntityOfDZ1.getSingleCost();
                    k=itemInfoEntityOfDZ1.getK();


                    //BigDecimal materialparam1 = new BigDecimal(outerSizeRes).multiply(new BigDecimal(outerSizeRes)).multiply(pai).divide(new BigDecimal("4"),2, BigDecimal.ROUND_HALF_UP).multiply(lengthSizeRes); // D1^2*π/4*L1
                    BigDecimal materialparam1 = new BigDecimal(outerSizeRes).multiply(new BigDecimal(outerSizeRes)).multiply(pai).divide(new BigDecimal("4")).multiply(lengthSizeRes); // D1^2*π/4*L1
                    BigDecimal materialparam2 = new BigDecimal("7.98").divide(new BigDecimal("1000000")); // 7.98/1000000
                    BigDecimal materialparam3 = new BigDecimal(material_price);//材料单价
                    BigDecimal singleWight=materialparam1.multiply(materialparam2).setScale(2,BigDecimal.ROUND_HALF_UP);
                    material_cost = singleWight.multiply(materialparam3).multiply(new BigDecimal(k)).setScale(2,BigDecimal.ROUND_HALF_UP);

                    dzReport.setWeight(String.valueOf(singleWight));
                    dzReport.setSingleCost(material_price);
                    dzReport.setMaterialCost(String.valueOf(material_cost));
                    dzReport.setK(k);
                }



                //计算喷涂费 d*π*L2/100*喷涂价格，G14计算喷涂价格小于55时，喷涂价格=55
                itemInfoEntityOfDZ.setMaterial(caizhi);
                ItemInfoEntityOfDZ itemInfoEntityOfDZ2 = this.baseMapper.selectItemSprayingOfDZ(itemInfoEntityOfDZ);
                BigDecimal spraying_cost=new BigDecimal("0");
                if (itemInfoEntityOfDZ2 != null) {
                    String spraying = itemInfoEntityOfDZ2.getSpraying();//涂层
                    String spraying_price = itemInfoEntityOfDZ2.getSprayingPrice();//涂层单价

                    BigDecimal sprayingparam1 = new BigDecimal(poutersize).multiply(pai).multiply(new BigDecimal(plengthsize)).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);//d*π*L2/100
                    BigDecimal sprayingparam2 = new BigDecimal(spraying_price);//喷涂价格

                    spraying_cost = sprayingparam1.multiply(sprayingparam2).setScale(2,RoundingMode.HALF_UP);//喷涂费

                    if("G14".equals(spraying)&&spraying_cost.compareTo(new BigDecimal("55"))==-1){//G14计算喷涂价格小于55时，喷涂价格=55
                        spraying_cost=new BigDecimal("55");
                    }

                    dzReport.setSprayingOuterSize(poutersize);
                    dzReport.setSprayingLengthSize(plengthsize);
                    dzReport.setSprayingArea(String.valueOf(sprayingparam1));
                    dzReport.setSprayingPrice(spraying_price);
                    dzReport.setSprayingFee(String.valueOf(spraying_cost));

                }



                //计算打磨费 外径D查找打磨费对应的价格


                ItemInfoEntityOfDZ itemInfoEntityOfDZ3 = this.baseMapper.selectItemGrindingOfDZ(itemInfoEntityOfDZ);

                String grindingprice="0";
                if (itemInfoEntityOfDZ3 != null) {
                    grindingprice = itemInfoEntityOfDZ3.getPrice();//打磨费单价


                    dzReport.setGrindingCost(grindingprice);

                }

                //计算加工费 外径D查找加工费对应的价格
                ItemInfoEntityOfDZ itemInfoEntityOfDZ4 = this.baseMapper.selectItemProcessingOfDZ(itemInfoEntityOfDZ);
                String processprice="0";
                if (itemInfoEntityOfDZ4 != null) {
                    processprice = itemInfoEntityOfDZ4.getProcessCost();//加工费

                    dzReport.setProcessCost(processprice);
                }


                //产品单价=材料费+加工费+喷涂费+打磨费
                BigDecimal total_price=material_cost.add(new BigDecimal(processprice)).add(new BigDecimal(grindingprice)).add(spraying_cost).setScale(1,BigDecimal.ROUND_HALF_UP);

                dzReport.setPrice(String.valueOf(total_price)+"0");
                dzReport.setTotalCost(String.valueOf(total_price.multiply(new BigDecimal(dzReport.getQuantity())).setScale(2,BigDecimal.ROUND_HALF_UP)));

                PriceVO priceVO=new PriceVO();

                priceVO.setSupName(infoEntityOfDZ.getSupName());
                priceVO.setSupCode(infoEntityOfDZ.getSupCode());
                priceVO.setPrice(total_price);

                if (priceVO.getPrice() != null ) {
                    priceVOS.add(priceVO);
                }

                itemInfoDTO.setSupAndPriceList(priceVOS);


                itemInfoEntityOfDZInsert.add(dzReport);




            }
            itemInfoDTOS.add(itemInfoDTO);


            // 插入xlj报表
            if (this.baseMapper.selectDZInfoReportExisted(u9Prs.getItemCode()) > 0) {
                this.baseMapper.deletedDZInfoReportByItemCode(u9Prs.getItemCode());
            }
            for (ItemInfoEntityOfDZ infoEntityOfDZ : itemInfoEntityOfDZInsert) {
                this.baseMapper.insertDZInfoReport(infoEntityOfDZ);
            }
            itemInfoEntityOfDZInsert.clear();

        }

        return itemInfoDTOS;
    }


    /**
     * 包装箱自动下单
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfBZX(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        //List<ItemInfoEntityOfFL> itemInfoEntityOfFLInsert = new ArrayList<>();


        for (U9PrDTO u9Prs : u9PrDTOS) {
            List<PriceVO> priceVOS = new ArrayList<>();

            // 包装箱的物料就是20020002
            if (!"20020002".equals(u9Prs.getItemCode())) {
                continue;
            }

            //拆包装箱 ATW-S220561-01,2100*1700*1000
            String itemName=u9Prs.getItemName();
            String[] CKG = itemName.split(",");//长宽高
            String[] CKGsplit = CKG[1].split("\\*");
            if (CKGsplit.length<3) {
                continue;
            }
            BigDecimal length =new BigDecimal(CKGsplit[0]);
            BigDecimal width =new BigDecimal(CKGsplit[1]);
            BigDecimal height=new BigDecimal(CKGsplit[2]);

            //计算面积
            BigDecimal areas=((length.multiply(width).multiply(new BigDecimal(2))).add
                (height.multiply(width).multiply(new BigDecimal(2))).add(length.multiply(height).multiply(new BigDecimal(2))))
                .divide(new BigDecimal(1000000));

            ItemInfoEntityOfBZX itemInfoEntityOfBZX=new ItemInfoEntityOfBZX();
            if(length.compareTo(width)>=0){
                itemInfoEntityOfBZX.setSize(String.valueOf(length));
            }else{
                itemInfoEntityOfBZX.setSize(String.valueOf(width));
            }

            List<ItemInfoEntityOfBZX> itemInfoEntityOfBZXES = this.baseMapper.selectItemPriceOfBZX(itemInfoEntityOfBZX);


            ItemInfoDTO itemInfoDTO=new ItemInfoDTO();//一个pr行  一个返回

            for (ItemInfoEntityOfBZX infoEntityOfBZX:itemInfoEntityOfBZXES) {

                itemInfoDTO.setId(u9Prs.getId().toString()); // (pr的id)
                itemInfoDTO.setItemCode(u9Prs.getItemCode()); // (料号)
                itemInfoDTO.setItemName(u9Prs.getItemName()); // (料名)


                if (itemInfoEntityOfBZXES.size()>1){
                    itemInfoDTO.setSupColorType("1");
                }else{
                    itemInfoDTO.setSupColorType("0");
                }

                //计算公式 价格=平方单价*面积
                BigDecimal total_price = areas.multiply(new BigDecimal(infoEntityOfBZX.getPrice())).setScale(2,BigDecimal.ROUND_HALF_UP);

                PriceVO priceVO=new PriceVO();

                priceVO.setSupName(infoEntityOfBZX.getSupName());
                priceVO.setSupCode(infoEntityOfBZX.getSupCode());
                priceVO.setPrice(total_price);
                itemInfoDTO.setSupAndPriceList(priceVOS);
                priceVOS.add(priceVO);
            }
            if (itemInfoDTO.getSupAndPriceList()!=null) {
                itemInfoDTOS.add(itemInfoDTO);
            }
        }

        return itemInfoDTOS;
    }


    /**
     * 法兰自动下单
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfFL(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        List<ItemInfoEntityOfFL> itemInfoEntityOfFLInsert = new ArrayList<>();


        for (U9PrDTO u9Prs : u9PrDTOS) {
            List<PriceVO> priceVOS = new ArrayList<>();

            // 若不是球座，则下一条
            if (u9Prs.getItemName().indexOf("法兰") < 0) {
                continue;
            }
            //自动拆解
            ItemInfoEntityOfFL itemInfoOfFL = getItemInfoOfFL(u9Prs.getItemName());


            if ("20".equals(itemInfoOfFL.getSize())) {
                //寸级是20的时候，当遇到20时，字母后面的数据是1，3，6，15就是美标
                int i = NumberSize(itemInfoOfFL.getPound());
                if (i ==1) {
                    if (itemInfoOfFL.getPound().indexOf("1")>-1||itemInfoOfFL.getPound().indexOf("3")>-1||itemInfoOfFL.getPound().indexOf("6")>-1){
                        ItemInfoEntityOfFL selectcjs = this.baseMapper.selectItemStandardOfFL(itemInfoOfFL);
                        if(selectcjs!=null){
                            itemInfoOfFL.setSize(selectcjs.getGb());
                        }
                    }

                }else{
                    if (itemInfoOfFL.getPound().indexOf("15")>-1){
                        ItemInfoEntityOfFL selectcjs = this.baseMapper.selectItemStandardOfFL(itemInfoOfFL);
                        if(selectcjs!=null){
                            itemInfoOfFL.setSize(selectcjs.getGb());
                        }
                    }

                }
            }else{
                ItemInfoEntityOfFL selectcjs = this.baseMapper.selectItemStandardOfFL(itemInfoOfFL);
                if(selectcjs!=null){
                    itemInfoOfFL.setSize(selectcjs.getGb());
                }

            }

            List<ItemInfoEntityOfFL> itemInfoEntityOfFLS = this.baseMapper.selectItemPriceOfFL(itemInfoOfFL);


            ItemInfoDTO itemInfoDTO=new ItemInfoDTO();//一个pr行  一个返回




            for (ItemInfoEntityOfFL itemInfoEntityOfFL:itemInfoEntityOfFLS) {

                ItemInfoEntityOfFL flReport=new ItemInfoEntityOfFL();

                flReport.setSupName(itemInfoEntityOfFL.getSupName());//供应商名称
                flReport.setSupCode(itemInfoEntityOfFL.getSupCode());//供应商编号
                flReport.setItemCode(u9Prs.getItemCode()); // (料号)
                flReport.setItemName(u9Prs.getItemName()); // (料名)
                flReport.setPrCode(u9Prs.getPrCode());//请购单号
                flReport.setPrLn(String.valueOf(u9Prs.getPrLn()));//请购行呈
                flReport.setQuantity(String.valueOf(u9Prs.getPriceNum()));//数量
                flReport.setPrice(itemInfoEntityOfFL.getSingleCost());

                itemInfoDTO.setId(u9Prs.getId().toString()); // (pr的id)
                itemInfoDTO.setItemCode(u9Prs.getItemCode()); // (料号)
                itemInfoDTO.setItemName(u9Prs.getItemName()); // (料名)



                if (itemInfoEntityOfFLS.size()>1){
                    itemInfoDTO.setSupColorType("1");
                }else{
                    itemInfoDTO.setSupColorType("0");
                }


                //查询单重

                ItemInfoEntityOfFL selectItemWeightOfFL = this.baseMapper.selectItemWeightOfFL(flReport);
                String weight="0";
                if (selectItemWeightOfFL != null) {
                    weight = selectItemWeightOfFL.getWeight();
                }

                flReport.setWeight(weight);

                //计算公式 成品价格=单价*重量

                BigDecimal total_price = new BigDecimal(itemInfoEntityOfFL.getSingleCost()).multiply(new BigDecimal(weight)).setScale(2,BigDecimal.ROUND_HALF_UP);


                flReport.setSingleCost(itemInfoEntityOfFL.getSingleCost());
                flReport.setPrice(total_price.toString());
                flReport.setTotalCost(total_price.multiply(new BigDecimal(flReport.getQuantity())).setScale(2,BigDecimal.ROUND_HALF_UP).toString());


                PriceVO priceVO=new PriceVO();

                priceVO.setSupName(itemInfoEntityOfFL.getSupName());
                priceVO.setSupCode(itemInfoEntityOfFL.getSupCode());
                priceVO.setPrice(total_price);

                if (priceVO.getPrice() != null &&  priceVO.getPrice().compareTo(BigDecimal.ZERO)!=0) {
                    priceVOS.add(priceVO);
                    itemInfoDTO.setSupAndPriceList(priceVOS);
                    itemInfoEntityOfFLInsert.add(flReport);
                }






            }

            if (itemInfoDTO.getSupAndPriceList()!=null) {
                itemInfoDTOS.add(itemInfoDTO);
            }



            // 插入xlj报表
            if (this.baseMapper.selectFLInfoReportExisted(u9Prs.getItemCode()) > 0) {
                this.baseMapper.deleteFLInfoReportByItemCode(u9Prs.getItemCode());
            }
            for (ItemInfoEntityOfFL itemInfoEntityOfFL : itemInfoEntityOfFLInsert) {
                this.baseMapper.insertFLInfoReport(itemInfoEntityOfFL);
            }
            itemInfoEntityOfFLInsert.clear();

        }

        return itemInfoDTOS;
    }


    /**
     * 自动获取小零件供应商信息
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetriveOfXLJ(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        List<ItemInfoEntityOfXLJ> itemInfoEntityOfXLJSToInsert = new ArrayList<>();
        String alltypes = "碟簧座,挡块,压环,填料压套,填料隔环,支撑套,长管,短管,接管,填料垫,蝶阀下盖,凸管,衬管,填料压板,凸板,下盖,填料函,连接盘,圆筒,套筒,底轴,阀杆";
        String notSplitTypes = "下盖,填料函,连接盘,圆筒,套筒,底轴,阀杆";

        // 获取所有符合 小零件 的物料信息
        for (U9PrDTO u9Prs : u9PrDTOS) {

            boolean isDJ = false;
            boolean isJXB = false;

            // 若不是 小零件，则下一条
            if ((alltypes.indexOf(u9Prs.getItemName().split("-")[0]) < 0 || (u9Prs.getItemName().split("\\+").length > 1 && "长管,短管,衬管".indexOf(u9Prs.getItemName().split("-")[0]) > -1))) {
                continue;
            }

            // 获取拆解后的信息
            ItemInfoEntityOfXLJ splitItemInfo = new ItemInfoEntityOfXLJ();
            if (notSplitTypes.indexOf(u9Prs.getItemName().split("-")[0]) < 0) {
                splitItemInfo = getItemInfoOfXLJ(u9Prs.getItemName());
            } else {
                splitItemInfo = this.baseMapper.selectNotSplitXLJItemInfo(u9Prs.getItemCode());
                if (splitItemInfo == null) {
                    continue;
                }
                String[] itemNameList = u9Prs.getItemName().split("-");
                int length = itemNameList.length;
                String mainType = itemNameList[0];
                String material = "";

                //联轴器的材质特殊计算逻辑
                if ("联轴器".equals(mainType)) {
                    if (StringUtil.isBlank(splitItemInfo.getInnerSize()) || StringUtil.isBlank(splitItemInfo.getOuterSize()) || StringUtil.isBlank(splitItemInfo.getHeightSize())) {
                        continue;
                    }
                    material = itemNameList[itemNameList.length - 1].substring(0, itemNameList[itemNameList.length - 1].indexOf("+"));
                } else {
                    material = itemNameList[length - 1];
                }


                // 套筒的特殊逻辑计算
                if (mainType.equals("套筒") && material.equals("630")) {
                    splitItemInfo.setMaterialType("棒料");
                } else if (mainType.equals("套筒") && !material.equals("630")) {
                    splitItemInfo.setMaterialType("管料");
                }

                splitItemInfo.setMainType(mainType);
                splitItemInfo.setMaterial(material);
            }


            String outerSizeOld = "";
            String innerSizeOld = "";
            String heightSizeOld = "";
            String R = "";
            String X = "";

            // 若拆解成功
            if (splitItemInfo != null) {

                outerSizeOld = splitItemInfo.getOuterSize();
                innerSizeOld = splitItemInfo.getInnerSize();
                heightSizeOld = splitItemInfo.getHeightSize();

                // 第 0 步： 寻找 材料类型表（表5）[根据 主分类 + 外径范围 + 材质 -> 材料类型]
                if (splitItemInfo.getMaterialType() == null || splitItemInfo.getMaterialType().isEmpty()) {
                    String materialType = "";

                    materialType = this.baseMapper.getMaterialTypeByItemWithMaterial(splitItemInfo);
                    if (materialType == null || materialType.isEmpty()) {
                        materialType = this.baseMapper.getMaterialTypeByItemWithOutMaterial(splitItemInfo);
                    }

                    if (materialType == null || materialType.isEmpty()) {
                        continue;
                    }

                    splitItemInfo.setMaterialType(materialType);

                    if (materialType.equals("锻件")) {
                        // @@@@@0000000@@@@
                        splitItemInfo = handleOfDJOfXLJItem(splitItemInfo);
                        if (splitItemInfo.getMaterialCost() == null || splitItemInfo.getMaterialCost().isEmpty()) {
                            continue;
                        }
                        isDJ = true;
                    }
                }

                // 第一步： 查找余量
                List<ItemInfoEntityOfXLJ> itemInfoEntitiesFromDataBase = new ArrayList<>();
                if (isDJ) {
                    // 如果是锻件，那么只要找出 几家 供应商，即可 【每一家供应商都是一样的价钱】
                    itemInfoEntitiesFromDataBase = this.baseMapper.selectXLJResItemInfoOfDJ(splitItemInfo);
                } else {
                    // 如果不是锻件、那就要走原来的 找余量逻辑 （包含了原始的 余量 ）

                    // 如果是 矩形板
                    if (splitItemInfo.getMaterialType().equals("板料") && (splitItemInfo.getInnerSize() == null || splitItemInfo.getInnerSize().isEmpty())) {

                        String L = splitItemInfo.getOuterSize();
                        String T = splitItemInfo.getHeightSize();
                        ItemInfoEntityBasOfXLJRX itemInfoEntityBasOfXLJRX = this.baseMapper.selectBasicItemInfoOfXLJRX(u9Prs.getItemCode());
                        if (itemInfoEntityBasOfXLJRX == null) {
                            continue;
                        }

                        R = itemInfoEntityBasOfXLJRX.getR();
                        X = itemInfoEntityBasOfXLJRX.getX();

                        String L1 = new BigDecimal(L).add(new BigDecimal("2").multiply(new BigDecimal(R))).add(new BigDecimal("15")).toString();
                        String W1 = new BigDecimal(X).add(new BigDecimal("15")).toString();

                        String T1 = this.baseMapper.selectThickness(T);

                        splitItemInfo.setHeightSize(T1);
                        splitItemInfo.setOuterSize(L1);
                        splitItemInfo.setInnerSize(W1);
                        splitItemInfo.setR(R);
                        splitItemInfo.setX(X);

                        isJXB = true;

                        // 找到不同供应商的单价，以区别有多少 供应商
                        List<ItemInfoEntityOfXLJ> list = this.baseMapper.selectBLItemPriceOfXLJ(splitItemInfo);

                        if (list.size() > 0) {
                            for (ItemInfoEntityOfXLJ item : list) {
                                ItemInfoEntityOfXLJ itemToNeed = new ItemInfoEntityOfXLJ();
                                itemToNeed.setSupCode(item.getSupCode());
                                itemToNeed.setSupName(item.getSupName());
                                itemInfoEntitiesFromDataBase.add(itemToNeed);
                            }
                        }
                    } else {
                        itemInfoEntitiesFromDataBase = this.baseMapper.selectXLJResItemInfo(splitItemInfo);
                    }
                }

                String singleCost = "";

                // 遍历每一家供应商
                if (itemInfoEntitiesFromDataBase.size() > 0) {
                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO(); // 返回给前端的
                    List<PriceVO> priceVOS = new ArrayList<>();

                    // id，用来识别前台的选中行
                    itemInfoDTO.setId(u9Prs.getId().toString()); // (pr的id)
                    itemInfoDTO.setItemCode(u9Prs.getItemCode()); // (料号)
                    itemInfoDTO.setItemName(u9Prs.getItemName()); // (料名)

                    // 算出 商品 采购价
                    for (ItemInfoEntityOfXLJ iteminfo : itemInfoEntitiesFromDataBase) {

                        // 第一步 ： 找出工时 【通用】
                        List<WorkTime> workTimes = this.getWorkTime(u9Prs.getItemCode());
                        BigDecimal JC = new BigDecimal("0");
                        BigDecimal CNC = new BigDecimal("0");
                        BigDecimal PRE = new BigDecimal("0");

                        if (workTimes.size() > 0) {
                            for (WorkTime workTime : workTimes) {
                                String workCode = workTime.getWorkCode();
                                if (workCode.indexOf("JC") > -1) {
                                    JC = JC.add(new BigDecimal(workTime.getPipTime()));
                                    if (Integer.valueOf(u9Prs.getPriceNum().toString()) < 5 && PRE.toString().equals("0")) {
                                        PRE = new BigDecimal(workTime.getPreTime());
                                    }
                                } else if (workCode.indexOf("CNC") > -1) {
                                    CNC = CNC.add(new BigDecimal(workTime.getPipTime()));
                                }
                            }
                        } else {
                            continue;
                        }
                        // 找出工时单价
                        String CNCWorkPrice = this.baseMapper.getCNCWorkPrice(iteminfo);
                        String JCWorkPrice = this.baseMapper.getJCWorkPrice(iteminfo);


                        if (CNCWorkPrice == null) {
                            CNCWorkPrice = "0";
                        }
                        if (JCWorkPrice == null) {
                            JCWorkPrice = "0";
                        }
                        BigDecimal CNCPrice = new BigDecimal(CNCWorkPrice);
                        BigDecimal JCPrice = new BigDecimal(JCWorkPrice);
                        // 直接查出的 ： (供应商编码) 、(供应商名称)、(材料类型)
                        iteminfo.setItemCode(u9Prs.getItemCode()); // (料号)
                        iteminfo.setItemName(u9Prs.getItemName()); // (料名)
                        iteminfo.setMaterial(splitItemInfo.getMaterial()); // (材质)
                        iteminfo.setOuterSizeOld(outerSizeOld); // (材料外径 - 拆)
                        iteminfo.setInnerSizeOld(innerSizeOld); // (材料内径  - 拆)
                        iteminfo.setHeightSizeOld(heightSizeOld); // (材料高度 - 拆)
                        iteminfo.setJcMinutes(JC.toString()); // JC工时
                        iteminfo.setCncMinutes(CNC.toString()); // CNC工时
                        iteminfo.setJcPrice(JCPrice.toString()); // JC工时单价
                        iteminfo.setCncPrice(CNCPrice.toString()); // CNC工时单价
                        iteminfo.setPreMinutes(PRE.toString()); // 准备工时
                        // 加工费 （准备工时+车床工时）/60*车床工时单价+CNC工时/60*CNC工时单价
                        BigDecimal param5 = (new BigDecimal(iteminfo.getJcMinutes()).add(new BigDecimal(iteminfo.getPreMinutes()))).multiply(new BigDecimal(iteminfo.getJcPrice())).divide(new BigDecimal("60"), 2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal param6 = new BigDecimal(iteminfo.getCncMinutes()).multiply(new BigDecimal(iteminfo.getCncPrice())).divide(new BigDecimal("60"), 2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal workPrice = param5.add(param6);
                        iteminfo.setProcessCost(workPrice.toString()); // (加工费)

                        if (!isDJ && !isJXB) {  // 如果第一次判定为 不是 锻件 且 不是 矩形板 的情况下，继续往下走 @@@@@111111@@@@  [找出 余量、和最终内外径]
                            String supCode = iteminfo.getSupCode();

                            // 材质类型 ： 棒料  、 管料 、 板料
                            String materialType = iteminfo.getMaterialType();
                            // 外径规则 (外径余量)
                            String outerSizeRule = iteminfo.getOuterSize();
                            iteminfo.setOuterRes(outerSizeRule);
                            // 内径规则 (内径余量)
                            String innerSizeRule = iteminfo.getInnerSize();
                            iteminfo.setInnerRes(innerSizeRule);
                            // 高度规则 (高度余量)
                            String heightSizeRule = iteminfo.getHeightSize();
                            iteminfo.setHeightRes(heightSizeRule);

                            // 计算出 第一步的 内、外、高
                            String outSize = this.handleXLJOuterSize(splitItemInfo.getOuterSize(), outerSizeRule);
                            String innerSize = "0";
                            if (!materialType.equals("棒料")) { // 所有的棒料的内径都是0
                                if (materialType.equals("板料")) {
                                    innerSize = new BigDecimal(splitItemInfo.getInnerSize()).add(new BigDecimal(innerSizeRule)).toString();   // 板料的 内径是 +
                                } else {
                                    innerSize = new BigDecimal(splitItemInfo.getInnerSize()).subtract(new BigDecimal(innerSizeRule)).toString(); // 管料的内径 是 -
                                }
                            }
                            String heightSize = new BigDecimal(splitItemInfo.getHeightSize()).add(new BigDecimal(heightSizeRule)).toString();

                            boolean innerFlag = false;
                            if (materialType.equals("管料")) { // 如果是 管料、需要先 向上找到最近的 外径、再向下找到 最近的内径
                                List<String> outSizeNewList = this.baseMapper.selectNewOuterSize(iteminfo.getSupCode(), outSize);
                                if (outSizeNewList.size() > 0) {
                                    for (String outSizeNew : outSizeNewList) {
                                        outSize = outSizeNew;
                                        String innerSizeNew = this.baseMapper.selectNewInnerSize(iteminfo.getSupCode(), outSize, innerSize);
                                        if (innerSizeNew != null) {
                                            innerSize = innerSizeNew;
                                            innerFlag = true;
                                            break; // 找到内径（不找了）
                                        } else {
                                            innerFlag = false; // 找不出内径（继续循环下一个外径，去查找内径）
                                        }
                                    }
                                } else {
                                    innerFlag = false; // 找不出外径（继续查找下一家供应商）
                                }
                            }

                            if (materialType.equals("管料") && !innerFlag) {
                                // 如果管料找不到的情况下,就按照锻件来查   @@@@@222222@@@@
                                splitItemInfo = handleOfDJOfXLJItem(splitItemInfo);

                                outSize = splitItemInfo.getOuterSize();
                                innerSize = splitItemInfo.getInnerSize();
                                heightSize = splitItemInfo.getHeightSize();

                                iteminfo.setOuterRes(splitItemInfo.getOuterRes());
                                iteminfo.setInnerRes(splitItemInfo.getInnerRes());
                                iteminfo.setHeightRes(splitItemInfo.getHeightRes());
                                isDJ = true;
                            }

                            iteminfo.setOuterSize(outSize); // (外径)[最终的]
                            iteminfo.setInnerSize(innerSize); // (内径)[最终的]
                            iteminfo.setHeightSize(heightSize); // (高度)[最终的]
                        }

                        String density = this.baseMapper.getDensity(iteminfo);
                        if (density == null && !isDJ) { // 如果不是锻件 且 找不到 密度，则直接 下一次循环、因为算不出来 材料费 了。
                            itemInfoEntityOfXLJSToInsert.add(iteminfo);
                            continue;
                        }

                        if (splitItemInfo.getMaterialType().equals("板料") && isJXB) {
                            iteminfo.setMaterialType(splitItemInfo.getMaterialType());  // (材料类型)[最终的]
                            iteminfo.setOuterSize(splitItemInfo.getOuterSize()); // (外径)[最终的]
                            iteminfo.setInnerSize(splitItemInfo.getInnerSize()); // (内径)[最终的]
                            iteminfo.setHeightSize(splitItemInfo.getHeightSize()); // (高度)[最终的]
                            iteminfo.setR(splitItemInfo.getR());
                            iteminfo.setX(splitItemInfo.getX());
                        }

                        ItemInfoEntityOfXLJ priceAndK = this.baseMapper.selectItemPriceOfXLJ(iteminfo);
                        if (priceAndK == null && !isDJ) { // 如果不是锻件 且 找不到 单价和K ，则直接 下一次循环、因为算不出来 材料费 了。
                            itemInfoEntityOfXLJSToInsert.add(iteminfo);
                            continue;
                        }

                        PriceVO priceVO = new PriceVO();
                        // 设值单价和供应商的连带关系
                        priceVO.setSupName(iteminfo.getSupName());
                        priceVO.setSupCode(iteminfo.getSupCode());
                        priceVO.setPriority(iteminfo.getPriority());

                        // 若能找出单价
                        if (priceAndK != null && !isDJ) {
                            BigDecimal materialPrice = new BigDecimal("0");
                            if (!isDJ) { // 不是锻件的情况

                                // 找 材料单价 和 K
                                singleCost = priceAndK.getSingleCost();
                                String k = priceAndK.getK();
                                iteminfo.setK(k);  // （系数K值）
                                iteminfo.setSingleCost(singleCost); // (材料单价)

                                if (!splitItemInfo.getMaterialType().equals("板料")) {
                                    // 材料费  (D1^2-d1^2)*π/4*L1*密度/1000000*材料单价*系数K值
                                    BigDecimal pai = new BigDecimal("3.14"); // π
                                    BigDecimal param1 = new BigDecimal(iteminfo.getOuterSize()).multiply(new BigDecimal(iteminfo.getOuterSize())); // D1^2
                                    BigDecimal param2 = new BigDecimal(iteminfo.getInnerSize()).multiply(new BigDecimal(iteminfo.getInnerSize())); // d1^2
                                    BigDecimal param3 = new BigDecimal(iteminfo.getHeightSize()); // L1
                                    BigDecimal param4 = new BigDecimal(density).divide(new BigDecimal("1000000")).multiply(new BigDecimal(singleCost)).multiply(new BigDecimal(k)); // 密度/1000000*材料单价*系数K值
                                    // 材料费 最终的价格
                                    materialPrice = (param1.subtract(param2)).multiply(pai).divide(new BigDecimal("4")).multiply(param3).multiply(param4);
                                    // （单重）
                                    iteminfo.setWeight((param1.subtract(param2)).multiply(pai).divide(new BigDecimal("4")).multiply(param3).multiply(new BigDecimal(density).divide(new BigDecimal("1000000"))).setScale(2, RoundingMode.HALF_UP).toString());
                                } else {
                                    // 如果是板料的情况
                                    BigDecimal L1 = new BigDecimal("0");
                                    BigDecimal W1 = new BigDecimal("0");
                                    BigDecimal T1 = new BigDecimal("0");
                                    if (isJXB) {
                                        L1 = new BigDecimal(splitItemInfo.getOuterSize());
                                        W1 = new BigDecimal(splitItemInfo.getInnerSize());
                                        T1 = new BigDecimal(splitItemInfo.getHeightSize());
                                    } else {
                                        L1 = new BigDecimal(iteminfo.getOuterSize());
                                        W1 = new BigDecimal(iteminfo.getInnerSize());
                                        String T = iteminfo.getHeightSize();
                                        String ttt = this.baseMapper.selectThickness(T);
                                        T1 = new BigDecimal(ttt);
                                    }

                                    iteminfo.setOuterSize(L1.toString());
                                    iteminfo.setInnerSize(W1.toString());
                                    iteminfo.setHeightSize(T1.toString());

                                    // （单重）
                                    iteminfo.setWeight(L1.multiply(W1).multiply(T1).multiply(new BigDecimal(density)).divide(new BigDecimal("1000000")).setScale(2, RoundingMode.HALF_UP).toString());
                                    // 材料费 最终的价格
                                    materialPrice = new BigDecimal(iteminfo.getWeight()).multiply(new BigDecimal(singleCost)).multiply(new BigDecimal(k).setScale(2, RoundingMode.HALF_UP));
                                }
                            } else {
                                // 是 锻件 的情况下
                                materialPrice = new BigDecimal(splitItemInfo.getMaterialCost());
                                iteminfo.setWeight(splitItemInfo.getWeight());  // （单重）
                                iteminfo.setSingleCost(splitItemInfo.getSingleCost());  // (材料单价)
                            }

                            // (材料费)
                            iteminfo.setMaterialCost(materialPrice.setScale(2, RoundingMode.HALF_UP).toString());
                            // 产品单价
                            BigDecimal productPrice = materialPrice.add(workPrice).setScale(1, RoundingMode.HALF_UP);
                            iteminfo.setPrice(productPrice.toString()); // (产品单价)
                            priceVO.setPrice(productPrice);
                        }

                        if (isDJ) {  // 锻件的情况下
                            iteminfo.setMaterialType("锻件");

                            iteminfo.setWeight(splitItemInfo.getWeight());
                            iteminfo.setSingleCost(splitItemInfo.getSingleCost());
                            iteminfo.setOuterRes(splitItemInfo.getOuterRes());
                            iteminfo.setInnerRes(splitItemInfo.getInnerRes());
                            iteminfo.setHeightRes(splitItemInfo.getHeightRes());
                            iteminfo.setOuterSize(splitItemInfo.getOuterSize()); // (外径)[最终的]
                            iteminfo.setInnerSize(splitItemInfo.getInnerSize()); // (内径)[最终的]
                            iteminfo.setHeightSize(splitItemInfo.getHeightSize()); // (高度)[最终的]
                            if (splitItemInfo.getMaterialCost() != null) {
                                // 产品单价
                                BigDecimal productPrice = new BigDecimal(splitItemInfo.getMaterialCost()).add(workPrice).setScale(1, RoundingMode.HALF_UP);
                                iteminfo.setMaterialCost(new BigDecimal(splitItemInfo.getMaterialCost()).setScale(2, RoundingMode.HALF_UP).toString());
                                iteminfo.setPrice(productPrice.toString());
                                priceVO.setPrice(productPrice);
                            }
                        }

                        if (priceVO.getPrice() != null) {
                            priceVOS.add(priceVO);
                        }
                        itemInfoDTO.setSupAndPriceList(priceVOS);
                        itemInfoEntityOfXLJSToInsert.add(iteminfo);
                    }

                    // 若一行物料对应的供应商有多行，supType变为 1
                    if ((itemInfoEntitiesFromDataBase != null) && (itemInfoEntitiesFromDataBase.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                        itemInfoDTO.setSupColorType("1");
                    } else {
                        itemInfoDTO.setSupColorType("0");
                    }
                    // 设值默认值
                    if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                        itemInfoDTOS.add(itemInfoDTO);
                    }
                }
            }
            // 插入xlj报表
            if (this.baseMapper.selectXLJInfoReportExisted(u9Prs.getItemCode()) > 0) {
                this.baseMapper.deletedXLJInfoReportByItemCode(u9Prs.getItemCode());
            }
            for (ItemInfoEntityOfXLJ itemInfoEntityOfXLJ : itemInfoEntityOfXLJSToInsert) {
                this.baseMapper.insertXLJInfoReport(itemInfoEntityOfXLJ);
            }
            itemInfoEntityOfXLJSToInsert.clear();
        }
        return itemInfoDTOS;
    }

    /**
     * 自动获取小零件供应商信息
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetriveOfLZQ(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        List<ItemInfoEntityOfLZQ> itemInfoEntityOfLZQToInsert = new ArrayList<>();

        // 获取所有符合 小零件 的物料信息
        for (U9PrDTO u9Prs : u9PrDTOS) {

            boolean isDJ = false;
            boolean isJXB = false;

            // 若不是 联轴器，则下一条
            if (("联轴器".indexOf(u9Prs.getItemName().split("-")[0]) < 0 )) {
                continue;
            }

            // 从表里获取信息
            ItemInfoEntityOfLZQ splitItemInfo = this.baseMapper.selectNotSplitLZQItemInfo(u9Prs.getItemCode());
            if (splitItemInfo == null) {
                continue;
            }
            String[] itemNameList = u9Prs.getItemName().split("-");
            int length = itemNameList.length;
            String mainType = itemNameList[0];
            String material = "";

            //联轴器的材质特殊计算逻辑
            if (StringUtil.isBlank(splitItemInfo.getInnerSize()) || StringUtil.isBlank(splitItemInfo.getOuterSize()) || StringUtil.isBlank(splitItemInfo.getHeightSize())) {
                continue;
            }
            try{
                if(itemNameList[itemNameList.length - 1].indexOf("+")>0){
                    material = itemNameList[itemNameList.length - 1].substring(0, itemNameList[itemNameList.length - 1].indexOf("+"));
                }else {
                    material = itemNameList[itemNameList.length - 1];
                }

            }catch (Exception e){
                log.info("获取材质失败");

            }

            String outerSizeOld = "";
            String innerSizeOld = "";
            String heightSizeOld = "";
            String xqgSizeOld = "";
            String cbkSizeOld = "";
            String cbzxjSizeOld = "";
            String xqgkSizeOld = "";

            // 若拆解成功
            if (splitItemInfo != null) {

                outerSizeOld = splitItemInfo.getOuterSize();
                innerSizeOld = splitItemInfo.getInnerSize();
                heightSizeOld = splitItemInfo.getHeightSize();
                xqgSizeOld = splitItemInfo.getXqgSize();
                cbkSizeOld = splitItemInfo.getCbkSize();
                cbzxjSizeOld = splitItemInfo.getCbzxjSize();
                xqgkSizeOld = splitItemInfo.getXqgkSize();



                // 第一步： 查找余量
                List<ItemInfoEntityOfLZQ> itemInfoEntitiesFromDataBase = this.baseMapper.selectLZQResItemInfo(splitItemInfo);



                String singleCost = "";

                // 遍历每一家供应商
                if (itemInfoEntitiesFromDataBase.size() > 0) {
                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO(); // 返回给前端的
                    List<PriceVO> priceVOS = new ArrayList<>();

                    // id，用来识别前台的选中行
                    itemInfoDTO.setId(u9Prs.getId().toString()); // (pr的id)
                    itemInfoDTO.setItemCode(u9Prs.getItemCode()); // (料号)
                    itemInfoDTO.setItemName(u9Prs.getItemName()); // (料名)

                    // 算出 商品 采购价
                    for (ItemInfoEntityOfLZQ iteminfo : itemInfoEntitiesFromDataBase) {

                        iteminfo.setPrCode(u9Prs.getPrCode());
                        iteminfo.setPrLn(String.valueOf(u9Prs.getPrLn()));
                        iteminfo.setQuantity(String.valueOf(u9Prs.getPriceNum()));
                        iteminfo.setXqgSize(xqgSizeOld);
                        iteminfo.setCbkSize(cbkSizeOld);
                        iteminfo.setCbzxjSize(cbzxjSizeOld);
                        iteminfo.setXqgkSize(xqgkSizeOld);


                        // 第一步 ： 找出工时 【通用】
                        List<WorkTime> workTimes = this.getWorkTime(u9Prs.getItemCode());
                        BigDecimal JC = new BigDecimal("0");
                        BigDecimal CNC = new BigDecimal("0");
                        BigDecimal PRE = new BigDecimal("0");

                        if (workTimes.size() > 0) {
                            for (WorkTime workTime : workTimes) {
                                String workCode = workTime.getWorkCode();
                                if (workCode.indexOf("JC") > -1||workCode.indexOf("CC") > -1) {
                                    JC = JC.add(new BigDecimal(workTime.getPipTime()));

                                } else if (workCode.indexOf("CNC") > -1||workCode.indexOf("ZK") > -1) {
                                    CNC = CNC.add(new BigDecimal(workTime.getPipTime()));
                                }

                                if (workCode.indexOf("JC") > -1&&Integer.valueOf(u9Prs.getPriceNum().toString()) < 5 && PRE.toString().equals("0")) {
                                    PRE = new BigDecimal(workTime.getPreTime());
                                }
                            }
                        } else {
                            continue;
                        }
                        // 找出工时单价
                        ItemInfoEntityOfLZQ lzqcncWorkPrice = this.baseMapper.getLZQCNCWorkPrice(iteminfo);
                        ItemInfoEntityOfLZQ lzqjcWorkPrice = this.baseMapper.getLZQJCWorkPrice(iteminfo);

                        String CNCWorkPrice = lzqcncWorkPrice.getWorkPrice();
                        String CNCK=lzqcncWorkPrice.getK();
                        String JCWorkPrice = lzqjcWorkPrice.getWorkPrice();
                        String JCK=lzqjcWorkPrice.getK();


                        if (CNCWorkPrice == null) {
                            CNCWorkPrice = "0";
                        }
                        if (JCWorkPrice == null) {
                            JCWorkPrice = "0";
                        }

                        BigDecimal CNCPrice = new BigDecimal(CNCWorkPrice);
                        BigDecimal JCPrice = new BigDecimal(JCWorkPrice);
                        // 直接查出的 ： (供应商编码) 、(供应商名称)、(材料类型)
                        iteminfo.setItemCode(u9Prs.getItemCode()); // (料号)
                        iteminfo.setItemName(u9Prs.getItemName()); // (料名)
                        iteminfo.setMaterial(material); // (材质)
                        iteminfo.setOuterSizeOld(outerSizeOld); // (材料外径 - 拆)
                        iteminfo.setInnerSizeOld(innerSizeOld); // (材料内径  - 拆)
                        iteminfo.setHeightSizeOld(heightSizeOld); // (材料高度 - 拆)
                        iteminfo.setJcMinutes(JC.toString()); // JC工时
                        iteminfo.setCncMinutes(CNC.toString()); // CNC工时
                        iteminfo.setJcPrice(JCPrice.toString()); // JC工时单价
                        iteminfo.setCncPrice(CNCPrice.toString()); // CNC工时单价
                        iteminfo.setPreMinutes(PRE.toString()); // 准备工时
                        // 加工费 （准备工时+车床工时）/60*车床工时单价+CNC工时/60*CNC工时单价*系数K值 20221128
                        BigDecimal param5 = (new BigDecimal(iteminfo.getJcMinutes()).add(new BigDecimal(iteminfo.getPreMinutes()))).multiply(new BigDecimal(iteminfo.getJcPrice())).divide(new BigDecimal("60"), 2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal param6 = new BigDecimal(iteminfo.getCncMinutes()).multiply(new BigDecimal(iteminfo.getCncPrice())).multiply(new BigDecimal(CNCK)).divide(new BigDecimal("60"), 2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal workPrice = param5.add(param6);
                        iteminfo.setProcessCost(workPrice.toString()); // (加工费)



                        PriceVO priceVO = new PriceVO();
                        // 设值单价和供应商的连带关系
                        priceVO.setSupName(iteminfo.getSupName());
                        priceVO.setSupCode(iteminfo.getSupCode());
                        priceVO.setPriority(iteminfo.getPriority());


                        // 外径规则 (外径余量)
                        String outerSizeRule = iteminfo.getOuterSize();
                        iteminfo.setOuterRes(outerSizeRule);
                        // 内径规则 (内径余量)
                        String innerSizeRule = iteminfo.getInnerSize();
                        iteminfo.setInnerRes(innerSizeRule);
                        // 高度规则 (高度余量)
                        String heightSizeRule = iteminfo.getHeightSize();
                        iteminfo.setHeightRes(heightSizeRule);

                        String outSize = this.handleXLJOuterSize(splitItemInfo.getOuterSize(), outerSizeRule);//原材料外径
                        String innerSize =  new BigDecimal(splitItemInfo.getInnerSize()).add(new BigDecimal(innerSizeRule)).toString();//原材料内径
                        String heightSize = new BigDecimal(splitItemInfo.getHeightSize()).add(new BigDecimal(heightSizeRule)).toString();//原材料高度

                        iteminfo.setOuterSize(outSize);
                        iteminfo.setInnerSize(innerSize);
                        iteminfo.setHeightSize(heightSize);

                        ItemInfoEntityOfXLJ xljinfo =new ItemInfoEntityOfXLJ();
                        BeanUtil.copy(iteminfo,xljinfo);
                        String densityVo=this.baseMapper.getDensity(xljinfo);
                        String density = StringUtils.isBlank(densityVo)? "0":densityVo;//获取密度


                        ItemInfoEntityOfLZQ priceAndK = this.baseMapper.selectItemPriceOfLZQ(iteminfo);//查询单价
                        // 若能找出单价
                        if (priceAndK != null ) {
                            BigDecimal materialPrice = new BigDecimal("0");

                            // 找 材料单价 和 K
                            singleCost = priceAndK.getSingleCost();
                            String k = priceAndK.getK();
                            iteminfo.setK(k);  // （系数K值）
                            iteminfo.setSingleCost(singleCost); // (材料单价)

                            // 材料费  D1^2*π/4*L1*7.85/1000000（密度表抓取对应的密度） 20221128
                            BigDecimal pai = new BigDecimal("3.14"); // π
                            BigDecimal param1 = new BigDecimal(iteminfo.getOuterSize()).multiply(new BigDecimal(iteminfo.getOuterSize())); // D1^2
                            BigDecimal param3 = new BigDecimal(iteminfo.getHeightSize()); // L1
                            //计算单重
                            BigDecimal weight= param1.multiply(pai).divide(new BigDecimal("4")).multiply(param3).multiply(new BigDecimal(density)).divide(new BigDecimal("1000000"));
                            // 材料费 原材料单重*单价（含镀锌）
                            materialPrice = weight.multiply(new BigDecimal(singleCost));
                            // （单重）
                            iteminfo.setWeight(weight.setScale(2, RoundingMode.HALF_UP).toString());
                            // (材料费)
                            iteminfo.setMaterialCost(materialPrice.setScale(2, RoundingMode.HALF_UP).toString());

                            String HJF="0";
                            String XQG="0";


                            String[] ItemNameSplit = iteminfo.getItemName().split("-");
                            //判断 线切割费  和  焊接费
                            if (ItemNameSplit[1].startsWith("KZ2A")||ItemNameSplit[1].startsWith("KXQ")){
                                //线切割
                                XQG=getXQG(iteminfo,splitItemInfo);//获取线切割

                                iteminfo.setXqgCost(XQG);

                            } else if (ItemNameSplit[1].startsWith("KZ1A")||ItemNameSplit[1].startsWith("KK")||ItemNameSplit[1].startsWith("KHJ")||
                                ItemNameSplit[1].startsWith("KCJ")||ItemNameSplit[1].startsWith("HJ")||ItemNameSplit[1].startsWith("Fk")||ItemNameSplit[1].startsWith("FZ")) {
                                //需要焊接，需要线切割
                                HJF=getHJF(iteminfo,splitItemInfo);//获取焊接费
                                XQG=getXQG(iteminfo,splitItemInfo);//获取线切割

                                iteminfo.setXqgCost(XQG);
                                iteminfo.setHjfCost(HJF);

                            }


                            // 产品单价=材料费+加工费+线切割费+焊接费 20221128
                            BigDecimal productPrice = materialPrice.add(workPrice).add(new BigDecimal(HJF)).add(new BigDecimal(XQG)).setScale(1, RoundingMode.HALF_UP);
                            iteminfo.setPrice(productPrice.toString()); // (产品单价)
                            iteminfo.setTotalCost(productPrice.multiply(new BigDecimal(iteminfo.getQuantity())).toString());
                            priceVO.setPrice(productPrice);
                        }
                        if (priceVO.getPrice() != null) {
                            priceVOS.add(priceVO);
                        }
                        itemInfoDTO.setSupAndPriceList(priceVOS);
                        itemInfoEntityOfLZQToInsert.add(iteminfo);
                    }

                    // 若一行物料对应的供应商有多行，supType变为 1
                    if ((itemInfoEntitiesFromDataBase != null) && (itemInfoEntitiesFromDataBase.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                        itemInfoDTO.setSupColorType("1");
                    } else {
                        itemInfoDTO.setSupColorType("0");
                    }
                    // 设值默认值
                    if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                        itemInfoDTOS.add(itemInfoDTO);
                    }
                }
            }
            // 插入xlj报表   改为联轴器自己的报表20221125
            if (this.baseMapper.selectLZQInfoReportExisted(u9Prs.getItemCode()) > 0) {
                this.baseMapper.deletedLZQInfoReportByItemCode(u9Prs.getItemCode());
            }
            for (ItemInfoEntityOfLZQ itemInfoEntityOfLZQ : itemInfoEntityOfLZQToInsert) {
                this.baseMapper.insertLZQInfoReport(itemInfoEntityOfLZQ);
            }
            itemInfoEntityOfLZQToInsert.clear();
        }
        return itemInfoDTOS;
    }

    private String getXQG(ItemInfoEntityOfLZQ iteminfo,ItemInfoEntityOfLZQ splitItemInfo) {
        String xqgSingleCost = this.baseMapper.selectItemXQGPriceOfLZQ(iteminfo);//线切割单价
        //计算公式 切割面积 (槽边宽*2+(槽边中心距-内径/2)*4)*线切割长度
        BigDecimal pram1=new BigDecimal(splitItemInfo.getCbzxjSize()).subtract(new BigDecimal(splitItemInfo.getInnerSize()).divide(new BigDecimal("2")));//槽边中心距-内径/2
        BigDecimal pram2=(new BigDecimal(splitItemInfo.getCbkSize()).multiply(new BigDecimal("2")).add(pram1.multiply(new BigDecimal("4")))).multiply(new BigDecimal(splitItemInfo.getXqgSize()));

        //计算公式 线切割面积*线切割单价
        BigDecimal xqgcost=pram2.multiply(new BigDecimal(xqgSingleCost));

        BigDecimal xqgk=new BigDecimal(splitItemInfo.getCbkSize()).multiply(new BigDecimal("2")).add(pram1.multiply(new BigDecimal("4")));
        iteminfo.setXqgkSize(xqgk.toString());

        return xqgcost.toString();
    }

    private String getHJF(ItemInfoEntityOfLZQ iteminfo,ItemInfoEntityOfLZQ splitItemInfo) {
        ItemInfoEntityOfLZQ itemInfoEntityOfLZQ = this.baseMapper.selectItemHJFPriceOfLZQ(iteminfo);//焊接费
        String hjfSingleCost=itemInfoEntityOfLZQ.getSingleCost();//焊接费单价
        return hjfSingleCost;

    }

    private List<WorkTime> getWorkTime(String itemCode) {
        List<WorkTime> workTimes = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;
        try {
            Class.forName(driver);
            //1.获取连接
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            @Cleanup Statement stmt = conn.createStatement();
            //2.准备预编译的sql
            StringBuffer sql = new StringBuffer();
            sql.append("select * from atwmos.v_bzgs where 产品代码 =  '").append(itemCode).append("'");

            //3.执行
            @Cleanup ResultSet resultSet = stmt.executeQuery(sql.toString());

            while (resultSet.next()) {
                String workCode = resultSet.getString("工序代码");
                String pipTime = resultSet.getString("标准工时");
                String preTime = resultSet.getString("准备工时");
                WorkTime workTime = new WorkTime();
                workTime.setItemCode(itemCode);
                workTime.setWorkCode(workCode);
                workTime.setPipTime(pipTime);
                workTime.setPreTime(preTime);
                workTimes.add(workTime);
            }
        } catch (Exception e) {
            throw new RuntimeException("查询oracle数据库出错！");
        } finally {
            return workTimes;
        }
    }

    private String handleXLJOuterSize(String outerSize, String outerSizeRule) {
        if (outerSizeRule.indexOf("加") > -1) {
            outerSizeRule = outerSizeRule.substring(1, outerSizeRule.length());
            String addNum = outerSizeRule.split("向上取")[0];
            Integer wholeNum = Integer.valueOf(outerSizeRule.split("向上取")[1]);
            // 先加
            outerSize = new BigDecimal(outerSize).add(new BigDecimal(addNum)).toString();
            double size = wholeNum * (Math.ceil(Math.abs(Double.parseDouble(outerSize) / wholeNum)));
            outerSize = String.valueOf(size);
        } else {
            outerSize = new BigDecimal(outerSize).add(new BigDecimal(outerSizeRule)).toString();
        }
        return outerSize;
    }


    //    @Scheduled(cron = "0 33 13 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void autoRetrieveRecord() {
        System.out.println("*************** 开始录入 自动获取信息 ***************");
        System.out.println("*************** 目的是将 每日可以使用 自动获取信息 的料号保存下来 ***************");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar calendar = new GregorianCalendar();
        String endTime = sdf.format(calendar.getTime());
        calendar.add(calendar.DATE, -2);//把日期往后增加一天.整数往后推,负数往前移动
        String time = sdf.format(calendar.getTime());
        List<U9PrDTO> u9PrDTOS = this.baseMapper.getOrderInfo(time);

        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        // 铸件
        List<ItemInfoDTO> itemInfoDTOSOfZhuJian = autoRetrieveOfZhuJian(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfZhuJian);
        // 锻件
        List<ItemInfoDTO> itemInfoDTOSOfDuanJian = autoRetrieveOfDuanJian(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfDuanJian);
        // 球座
        List<ItemInfoDTO> itemInfoDTOSOfQiuZuo = autoRetrieveOfQiuZuo(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfQiuZuo);
        // 全程委外
        List<ItemInfoDTO> itemInfoDTOSOfWW = autoRetrieveOfWW(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfWW);

        itemInfoDTOS.forEach(item -> {
            if (this.baseMapper.autoItemIsExisted(item.getItemCode()) == 0) {
                this.baseMapper.insertAutoItem(item.getItemCode(), item.getItemName());
            }
        });

        System.out.println("*************** 结束录入 自动获取信息 ***************");
    }

    /**
     * 自动获取球座供应商信息
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfQiuZuo(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        // 获取所有符合 球座 的物料信息
        for (U9PrDTO u9Prs : u9PrDTOS) {

            // 若不是球座，则下一条
            if (u9Prs.getItemName().indexOf("球座") < 0) {
                continue;
            }

            // 若 料号1249开头的组件&&pr是全程委外
            if (u9Prs.getItemCode().substring(0, 4).equals("1249") && u9Prs.getBizType().toString().equals("1")) {
                continue;
            }

            // 拆解
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(u9Prs.getItemName());

            // 若拆解成功
            if (itemInfoEntity.getItemize() != null) {

                // 第一步查找，可能出现多家供应商
                List<ItemInfoEntityOfQZ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfQZ(itemInfoEntity);

                String price = "";
                String ptPrice = "";
                String range = "";

                // 遍历每一家供应商
                if (itemInfoEntitiesFromDataBase.size() > 0) {

                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
                    List<PriceVO> priceVOS = new ArrayList<>();

                    // id，用来识别前台的选中行
                    itemInfoDTO.setId(u9Prs.getId().toString());
                    itemInfoDTO.setItemCode(u9Prs.getItemCode());
                    itemInfoDTO.setItemName(u9Prs.getItemName());

                    // 第二次查找，找到单价
                    for (ItemInfoEntityOfQZ iteminfo : itemInfoEntitiesFromDataBase) {
                        // 球体加工费
                        String qzCharge = iteminfo.getQzCharge();
                        if (StringUtils.isEmpty(qzCharge)) {
                            qzCharge = "0";
                        }
                        // 阀座加工费
                        String fzCharge = iteminfo.getFzCharge();
                        if (StringUtils.isEmpty(fzCharge)) {
                            fzCharge = "0";
                        }

                        price = this.baseMapper.selectItemPriceOfQZ(iteminfo);

                        if (Double.valueOf(iteminfo.getSize()) >= 0.5 && Double.valueOf(iteminfo.getSize()) <= 10) {
                            range = "0.5≤寸级≤10寸";
                        } else {
                            range = "寸级≥12寸";
                        }
                        ptPrice = this.baseMapper.selectPtPriceOfQZ(iteminfo, range);

                        // 若能找出单价
                        if (StringUtils.isNotEmpty(price) && StringUtils.isNotEmpty(ptPrice)) {
                            PriceVO priceVO = new PriceVO();
                            // 设值单价和供应商的连带关系
                            priceVO.setSupName(iteminfo.getSupName());
                            priceVO.setSupCode(iteminfo.getSupCode());
                            priceVO.setPriority(iteminfo.getPriority());

                            // 四舍五入 保留2位小数 （球座单重*单价+喷涂面积*喷涂单价+球体加工费）+ （阀座单重*单价+喷涂费+阀座加工费）* 2
                            BigDecimal qzPrice = new BigDecimal(iteminfo.getQzWeight().trim()).multiply(new BigDecimal(price.trim())).add(new BigDecimal(qzCharge.trim())).add(new BigDecimal(iteminfo.getQzSprayArea().trim()).multiply(new BigDecimal(ptPrice.trim())));
                            BigDecimal fzPrice = new BigDecimal(iteminfo.getFzWeight().trim()).multiply(new BigDecimal(price.trim())).add(new BigDecimal(fzCharge.trim())).add(new BigDecimal(iteminfo.getFzSprayCharge().trim()));
                            BigDecimal priceToReturn = qzPrice.add(fzPrice.multiply(new BigDecimal("2"))).setScale(0, RoundingMode.HALF_UP);
                            priceVO.setPrice(priceToReturn);

                            priceVOS.add(priceVO);
                            itemInfoDTO.setSupAndPriceList(priceVOS);
                        }
                    }
                    // 若一行物料对应的供应商有多行，supType变为 1
                    if ((itemInfoEntitiesFromDataBase != null) && (itemInfoEntitiesFromDataBase.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                        itemInfoDTO.setSupColorType("1");
                    } else {
                        itemInfoDTO.setSupColorType("0");
                    }

                    // 设值
                    if (itemInfoDTO.getSupAndPriceList().size() > 0) {
                        itemInfoDTOS.add(itemInfoDTO);
                    }
                }
            }
        }
        return itemInfoDTOS;
    }

    /**
     * 自动获取球座供应商信息  20221206
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfQiuZuoNew(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        List<ItemInfoEntityOfQZNew> itemInfoEntityOfLZQToInsert = new ArrayList<>();

        // 获取所有符合 球座 的物料信息
        for (U9PrDTO u9Prs : u9PrDTOS) {
            List<PriceVO> priceVOS = new ArrayList<>();

            // 若不是球座，则下一条
            if (u9Prs.getItemName().indexOf("球体") < 0 && u9Prs.getItemName().indexOf("阀座") < 0) {
                continue;
            }

            if(u9Prs.getItemName().indexOf("GP") > 0 || u9Prs.getItemName().indexOf("LT") > 0){
                continue;
            }

            // 拆解
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(u9Prs.getItemName());

            ItemInfoEntityOfQZNew itemInfoEntityOfQZNew=new ItemInfoEntityOfQZNew();
            BeanUtil.copy(itemInfoEntity,itemInfoEntityOfQZNew);

            // 若拆解成功
            if (itemInfoEntity.getItemize() != null) {
                //获取基础数据
                List<ItemInfoEntityOfQZNew> itemInfoEntityOfQZNews = this.baseMapper.selectBasicInfoOfQZ(itemInfoEntityOfQZNew);

                ItemInfoDTO itemInfoDTO=new ItemInfoDTO();//一个pr行  一个返回
                itemInfoDTO.setId(u9Prs.getId().toString());
                itemInfoDTO.setItemCode(u9Prs.getItemCode());
                itemInfoDTO.setItemName(u9Prs.getItemName());

                for (ItemInfoEntityOfQZNew item :itemInfoEntityOfQZNews) {
                    item.setItemName(u9Prs.getItemName());
                    item.setItemCode(u9Prs.getItemCode());
                    item.setPrCode(u9Prs.getPrCode());
                    item.setPrLn(String.valueOf(u9Prs.getPrLn()));
                    item.setQuantity(String.valueOf(u9Prs.getPriceNum()));


                    String qzWeight = item.getQzWeight();//球体单重
                    String qzSprayArea = item.getQzSprayArea();//球体喷涂面积
                    String qzCharge = item.getQzCharge();//球体加工费
                    String material = item.getMaterial();//材质
                    String size = item.getSize();//寸级
                    String k = item.getK();//系数K

                    BigDecimal fz_cost=new BigDecimal("0");
                    BigDecimal qt_cost=new BigDecimal("0");
                    BigDecimal spray_cost=new BigDecimal("0");

                    //球体计算 球体单价=(球体材料价格(含热处理)+球体喷涂价格+球体加工费)*系数
                    if(u9Prs.getItemName().indexOf("球体")>-1){
                        //球体材料价格(含热处理)=球体单重*材料单价
                        ItemInfoOfQZVO itemInfoOfQZVO = this.baseMapper.selectMaterialPriceOfQZ(item);
                        String qtPrice = itemInfoOfQZVO.getQtPrice();
                        BigDecimal material_cost=new BigDecimal(qzWeight).multiply(new BigDecimal(qtPrice));

                        item.setQtMaterialPrice(qtPrice);
                        item.setQzMaterialCost(String.valueOf(material_cost));

                        //球体喷涂价格=喷涂面积*喷涂单价
                        String price="0";
                        if(Double.valueOf(size)<=1){
                            ItemInfoOfQZVO itemInfoOfQZVO1 = this.baseMapper.selectSpraySmallOfQZ(item);
                            if (itemInfoOfQZVO1 != null) {
                                price = itemInfoOfQZVO1.getPrice();
                            }
                            spray_cost=new BigDecimal(price);


                        }else{
                            ItemInfoOfQZVO itemInfoOfQZVO1 = this.baseMapper.selectSprayOfQZ(item);
                            if (itemInfoOfQZVO1 != null) {
                                price = itemInfoOfQZVO1.getPrice();
                            }
                            item.setQzSprayPrice(price);
                            spray_cost=new BigDecimal(qzSprayArea).multiply(new BigDecimal(price)).setScale(2,BigDecimal.ROUND_HALF_UP);


                        }

                        item.setQzSprayCost(String.valueOf(spray_cost));
                        qt_cost=(spray_cost.add(material_cost).add(new BigDecimal(qzCharge))).multiply(new BigDecimal(k)).setScale(0,BigDecimal.ROUND_HALF_UP);
                        item.setQzCost(String.valueOf(qt_cost));

                        item.setTotalCost(String.valueOf(qt_cost.multiply(new BigDecimal(item.getQuantity()))));



                    }

                    //阀座计算 阀座单价=(阀座单重*材料单价+阀座喷涂价格+阀座加工费+阀座配磨费)*系数
                    if(u9Prs.getItemName().indexOf("阀座")>-1){
                        //阀座单重*材料单价
                        String fzWeight = item.getFzWeight();//阀座单重
                        ItemInfoOfQZVO itemInfoOfQZVO = this.baseMapper.selectMaterialPriceOfQZ(item);
                        String fzPrice = itemInfoOfQZVO.getFzPrice();
                        BigDecimal material_cost=new BigDecimal(fzWeight).multiply(new BigDecimal(fzPrice));

                        item.setFzMaterialCost(String.valueOf(material_cost));

                        //直接取 阀座喷涂价格
                        //直接取 阀座加工费
                        //直接取 阀座配磨费

                        String fzSprayCharge = item.getFzSprayCharge();
                        String fzCharge = item.getFzCharge();
                        String deliverCost = item.getDeliverCost();

                        fz_cost=(material_cost.add(new BigDecimal(fzSprayCharge)).add(new BigDecimal(fzCharge)).add(new BigDecimal(deliverCost))).multiply(new BigDecimal(k)).setScale(0,BigDecimal.ROUND_HALF_UP);

                        item.setFzPrice(String.valueOf(fz_cost));
                        item.setTotalCost(String.valueOf(fz_cost.multiply(new BigDecimal(item.getQuantity()))));

                    }

                    PriceVO priceVO=new PriceVO();
                    priceVO.setSupName(item.getSupName()+"--"+item.getGrade());
                    priceVO.setSupCode(item.getSupCode());
                    priceVO.setPrice(fz_cost.add(qt_cost));
                    priceVO.setGrade(item.getGrade());
                    itemInfoDTO.setSupAndPriceList(priceVOS);
                    priceVOS.add(priceVO);
                    itemInfoEntityOfLZQToInsert.add(item);

                }

                // 若一行物料对应的供应商有多行，supType变为 1
                if ((itemInfoEntityOfQZNews != null) && (itemInfoEntityOfQZNews.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                    itemInfoDTO.setSupColorType("1");
                } else {
                    itemInfoDTO.setSupColorType("0");
                }
                // 设值默认值
                if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                    itemInfoDTOS.add(itemInfoDTO);
                }
            }
            // 插入报表
            if (this.baseMapper.selectQZInfoReportExisted(u9Prs.getItemCode()) > 0) {
                this.baseMapper.deletedQZInfoReportByItemCode(u9Prs.getItemCode());
            }
            for (ItemInfoEntityOfQZNew itemInfoEntityOfQZ : itemInfoEntityOfLZQToInsert) {
                this.baseMapper.insertQZInfoReport(itemInfoEntityOfQZ);
            }
            itemInfoEntityOfLZQToInsert.clear();
        }
        return itemInfoDTOS;
    }


    /**
     * 自动获取铸件供应商信息
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfZhuJian(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        // 获取所有符合 铸件锻件 的物料信息
        for (U9PrDTO u9Prs : u9PrDTOS) {

            // 若不是铸件，则下一条
            if (u9Prs.getItemName().indexOf("铸件") < 0) {
                continue;
            }

            // 拆解
            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(u9Prs.getItemName());

            // 若拆解成功
            if (itemInfoEntity.getSeries() != null) {

                // 第一步查找，可能出现多家供应商
                List<ItemInfoEntityOfZDJ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfZDJ(itemInfoEntity);

                String price = "";

                // 遍历每一家供应商
                if (itemInfoEntitiesFromDataBase.size() > 0) {

                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
                    List<PriceVO> priceVOS = new ArrayList<>();

                    // id，用来识别前台的选中行
                    itemInfoDTO.setId(u9Prs.getId().toString());
                    itemInfoDTO.setItemCode(u9Prs.getItemCode());
                    itemInfoDTO.setItemName(u9Prs.getItemName());

                    // 第二次查找，找到单价
                    for (ItemInfoEntityOfZDJ iteminfo : itemInfoEntitiesFromDataBase) {
                        String extraPrice = iteminfo.getCharge();
                        if (StringUtils.isEmpty(extraPrice)) {
                            extraPrice = "0";
                        }
                        // 不管有没有 替换 材料，都用原来的物料描述里面的材料
                        iteminfo.setMaterial(itemInfoEntity.getMaterial());

                        price = this.baseMapper.selectItemPriceOfZDJ(iteminfo);
                        // 若能找出单价
                        if (StringUtils.isNotEmpty(price)) {

                            PriceVO priceVO = new PriceVO();
                            // 设值单价和供应商的连带关系
                            priceVO.setSupName(iteminfo.getSupName());
                            priceVO.setSupCode(iteminfo.getSupCode());
                            priceVO.setPriority(iteminfo.getPriority());
                            // 四舍五入 保留2位小数 单重*单价+加工费
                            BigDecimal priceToReturn = (new BigDecimal(price).multiply(new BigDecimal(iteminfo.getWeight())).add(new BigDecimal(extraPrice))).setScale(2, RoundingMode.HALF_UP);

                            priceVO.setPrice(priceToReturn);
                            priceVOS.add(priceVO);
                            itemInfoDTO.setSupAndPriceList(priceVOS);
                        }
                    }

                    // 若一行物料对应的供应商有多行，supType变为 1
                    if ((itemInfoEntitiesFromDataBase != null) && (itemInfoEntitiesFromDataBase.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                        itemInfoDTO.setSupColorType("1");
                    } else {
                        itemInfoDTO.setSupColorType("0");
                    }
                    // 设值默认值
                    if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                        itemInfoDTOS.add(itemInfoDTO);
                    }
                }
            }
        }
        return itemInfoDTOS;
    }

    /**
     * 自动获取管棒料信息
     *
     * @param u9PrDTOS
     * @return 物料描述 itemcode
     */
    private List<ItemInfoDTO> authRetriveOfGuanBangLiao(List<U9PrDTO> u9PrDTOS) {
        ArrayList<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        System.out.println("*************** 目的是将 每日可以使用 自动获取信息 的料号保存下来 ***************");
        MaterialMaliyVO maliyVO = new MaterialMaliyVO();
        //获取所有符合管棒料 的物料信息
        for (U9PrDTO u9Prs : u9PrDTOS) {
            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
            //若不是管棒料,则contiune 跳出
            if (u9Prs.getItemName().indexOf("长管") < 0 || u9Prs.getItemName().indexOf("短管") < 0 || u9Prs.getItemName().indexOf("衬管") < 0) {
                if (u9Prs.getItemName().split("\\+").length < 2 || u9Prs.getItemName().split("\\+")[1].indexOf("G") < 0) {
                    continue;
                }
            }
            //从页面获取值
            TubeMaterialInfoEntity infoEntity = new TubeMaterialInfoEntity();
            infoEntity.setItemDesc(u9Prs.getItemName());
            infoEntity.setItemCode(u9Prs.getItemCode());
            infoEntity.setPriceNum(u9Prs.getPriceNum());
            infoEntity.setPrLn(u9Prs.getPrLn());
            infoEntity.setPrCode(u9Prs.getPrCode());

            // 在这里存报表
            tubeMaterialInfoService.getInfoList(infoEntity);

            //管棒料拆解 获取材料对象名
            maliyVO = getItemInfoGuanBangLiao(u9Prs.getItemName());

            //如果拆解成功
            if (maliyVO.getItemName() != null) {
                //查找是不是有多家供应商
                List<MaterialMaliyVO> mailyVOList = materialMailyVoMapper.selectSupName(maliyVO);
                //遍历多个供应商
                if (mailyVOList.size() > 0) {
                    List<PriceVO> priceVOS = new ArrayList<>();
                    for (MaterialMaliyVO materialMaliyVO : mailyVOList) {
                        maliyVO.setSupplierCode(materialMaliyVO.getSupplierCode());
                        maliyVO.setSupplierName(materialMaliyVO.getSupplierName());

                        //获取余量
                        MaterialMaliyVO materialMaliyVO1 = materialMailyVoMapper.selectRes(materialMaliyVO.getSupplierCode(), maliyVO.getTheMaterial());

                        String outerSizeRes=materialMaliyVO1.getOuterSize();
                        String heightSizeRes=materialMaliyVO1.getHeightSize();
                        String innerSizeRes=materialMaliyVO1.getInnerSize();
                        Double Exter=maliyVO.getExternalDiameter()+ Double.valueOf(outerSizeRes)  ;//余量外径
                        Double Inner=maliyVO.getInternalDiamete()- Double.valueOf(innerSizeRes);//余量内径
                        Double Length=maliyVO.getLength()+ Double.valueOf(heightSizeRes);//余量长度

                        //判断外圆是否小于等于300MM
                        if (maliyVO.getExternalDiameter() <= 301) {
                            MaterialMaliyVO materialExternalDiameter = materialMailyVoMapper.selectExter(Exter);
                            //根据外径找内径
                            MaterialMaliyVO material85 = null;
                            if (materialExternalDiameter != null) {
                                material85 = materialMailyVoMapper.selectInner(materialExternalDiameter.getExternalDiameter(), Inner);
                            }
                            if (material85 != null) {
                                //材料费
                                MaterialMaliyVO materialList = getMaterialPrice(maliyVO, material85.getExternalDiameter(), material85.getInternalDiamete(), maliyVO.getTheMaterial(), Length, maliyVO.getSupplierCode());
                                if (materialList == null) {
                                    log.warn("材料费为空");
                                    continue;
                                }
                                maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
                                maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
                                maliyVO.setWeight(materialList.getWeight()); //单重

                                //喷涂费
                                MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
                                if (sprayList.equals(null)) {
                                    log.warn("喷涂费为空");
                                    continue;
                                }
                                maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //喷涂单价
                                maliyVO.setSprayPrice(sprayList.getSprayPrice());//喷涂费

                                // 加工费
                                MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                                if (proscessList == null) {
                                    log.info("加工费为空");
                                    continue;
                                }
                                maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
                                maliyVO.setPrice(proscessList.getPrice()); //切割费

                                // 产品单价   产品单价=材料费+喷涂费+加工费+切割费
                                BigDecimal productPrice = null;
                                if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                                    productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                                    maliyVO.setUnitPrice(productPrice);
                                }
                                maliyVO.setUnitPrice(productPrice);

                                // 若一行物料对应的供应商有多行，supType变为 1
                                if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                                    itemInfoDTO.setSupColorType("1");
                                } else {
                                    itemInfoDTO.setSupColorType("0");
                                }
                                // 设值默认值
                                if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                                    itemInfoDTOS.add(itemInfoDTO);
                                }
                                if (maliyVO.getProcessingFee() != null && maliyVO.getSprayPrice() != null && maliyVO.getMaterialPrice() != null) {
                                    //不显示
                                    PriceVO priceVO = new PriceVO();
                                    priceVO.setPrice(productPrice); //价格
                                    priceVO.setSupName(maliyVO.getSupplierName());  //供应商名称
                                    priceVO.setSupCode(maliyVO.getSupplierCode()); //供应商编码
                                    priceVO.setSupKey("1"); //供应商默认值
                                    priceVO.setType("类型"); //类型
                                    priceVO.setSingleWeight(String.valueOf(maliyVO.getWeight()));
                                    priceVO.setPriority("1");
                                    priceVOS.add(priceVO);
                                } else {
//                                    priceVOS.clear();
                                    continue;
                                }
                            } else {
                                Double exter = maliyVO.getExternalDiameter() + 10;
                                Double inner = maliyVO.getInternalDiamete() - 10;
                                //材料费
                                MaterialMaliyVO materialList = getMaterialPrice(maliyVO, exter, inner, maliyVO.getTheMaterial(), Length, maliyVO.getSupplierCode());
                                if (materialList == null) {
                                    log.info("材料费为空");
                                    continue;
                                }
                                maliyVO.setSupplierName(materialList.getSupplierName());
                                maliyVO.setSupplierCode(materialList.getSupplierCode());
                                maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
                                maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
                                maliyVO.setWeight(materialList.getWeight()); //单重

                                //喷涂费
                                MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
                                if (sprayList.equals(null)) {
                                    log.info("材料费为空");
                                    continue;
                                }
                                maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //喷涂单价
                                maliyVO.setSprayPrice(sprayList.getSprayPrice());//喷涂费

                                //加工费
                                MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                                if (proscessList == null) {
                                    log.info("加工费为空");
                                    continue;
                                }
                                maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
                                maliyVO.setPrice(proscessList.getPrice()); //切割费

                                //产品单价   产品单价=材料费+喷涂费+加工费+切割费
                                BigDecimal productPrice = null;
                                if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                                    productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                                    maliyVO.setUnitPrice(productPrice);
                                }
                                maliyVO.setUnitPrice(productPrice);

                                // 若一行物料对应的供应商有多行，supType变为 1
                                if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                                    itemInfoDTO.setSupColorType("1");
                                } else {
                                    itemInfoDTO.setSupColorType("0");
                                }
                                // 设值默认值
                                if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                                    itemInfoDTOS.add(itemInfoDTO);
                                }
                                if (maliyVO.getProcessingFee() != null && maliyVO.getSprayPrice() != null && maliyVO.getMaterialPrice() != null) {
                                    // 不显示
                                    priceVOS = new ArrayList<>();
                                    PriceVO priceVO = new PriceVO();
                                    priceVO.setPrice(productPrice); //价格
                                    priceVO.setSupName(maliyVO.getSupplierName());  //供应商名称
                                    priceVO.setSupCode(maliyVO.getSupplierCode()); //供应商编码
                                    priceVO.setSupKey("1"); //供应商默认值
                                    priceVO.setType("类型"); //类型
                                    priceVO.setSingleWeight(String.valueOf(maliyVO.getWeight()));
                                    priceVO.setPriority("1");
                                    priceVOS.add(priceVO);
                                } else {
//                                    priceVOS.clear();
                                    continue;
                                }
                            }
                        }
                        // 外园大于300mm
                        if (maliyVO.getExternalDiameter() > 301) {
                            //直接计算  外径+10 内径 -10
                            Double exter = maliyVO.getExternalDiameter() + 10;
                            Double inner = maliyVO.getInternalDiamete() - 10;
                            //材料费
                            MaterialMaliyVO materialList = getMaterialPrice(maliyVO, exter, inner, maliyVO.getTheMaterial(), Length, maliyVO.getSupplierCode());
                            if (materialList == null) {
                                continue;
                            }
                            maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
                            maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
                            maliyVO.setWeight(materialList.getWeight()); //单重

                            //喷涂费
                            MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
                            if (sprayList.equals(null)) {
                                log.info("喷涂费为空");
                                continue;
                            }
                            maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); // 喷涂单价
                            maliyVO.setSprayPrice(sprayList.getSprayPrice());// 喷涂费
                            log.info("喷涂单价" + maliyVO.getCoatingPrice());
                            log.info("喷涂费" + maliyVO.getSprayPrice());
                            //加工费
                            MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                            if (proscessList == null) {
                                log.info("加工费为空");
                                continue;
                            }
                            maliyVO.setProcessingFee(proscessList.getProcessingFee()); // 加工费
                            maliyVO.setPrice(proscessList.getPrice()); // 切割费
                            log.info("加工费" + maliyVO.getProcessingFee());
                            log.info("切割费" + maliyVO.getPrice());

                            //产品单价   产品单价=材料费+喷涂费+加工费+切割费
                            BigDecimal productPrice = null;
                            if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                                productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                                maliyVO.setUnitPrice(productPrice);
                            }
                            maliyVO.setUnitPrice(productPrice);

                            // 若一行物料对应的供应商有多行，supType变为 1
                            if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                                itemInfoDTO.setSupColorType("1");
                            } else {
                                itemInfoDTO.setSupColorType("0");
                            }
                            // 设值默认值
                            if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                                itemInfoDTOS.add(itemInfoDTO);
                            }
                            if (maliyVO.getProcessingFee() != null && maliyVO.getSprayPrice() != null && maliyVO.getMaterialPrice() != null) {
                                priceVOS = new ArrayList<>();
                                PriceVO priceVO = new PriceVO();
                                priceVO.setPrice(productPrice); // 价格
                                priceVO.setSupName(maliyVO.getSupplierName());  // 供应商名称
                                priceVO.setSupCode(maliyVO.getSupplierCode()); // 供应商编码
                                priceVO.setSupKey("1"); // 供应商默认值
                                priceVO.setType("类型"); // 类型
                                priceVO.setSingleWeight(String.valueOf(maliyVO.getWeight()));
                                priceVO.setPriority("1");
                                priceVOS.add(priceVO);
                            } else {
//                                priceVOS.clear();
                                continue;
                            }
                        }
                        itemInfoDTO.setSupAndPriceList(priceVOS);
                    }
                } else {
                    continue;
                }
            } else {
                continue;
            }
            if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                itemInfoDTO.setId(u9Prs.getId().toString());
                log.info("id值为" + u9Prs.getId().toString());
                itemInfoDTO.setItemName(u9Prs.getItemName());
                itemInfoDTO.setItemCode(u9Prs.getItemCode());
                itemInfoDTOS.add(itemInfoDTO);
            }
        }
        return itemInfoDTOS;
    }


    /**
     * 管棒料信息详情
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<MaterialMaliyVO> getitemInfoGBL(String itemCode, String itemName) {
        List<MaterialMaliyVO> maliyVOList = new ArrayList<>();
        //对物料进行拆解
        MaterialMaliyVO maliyVO = getItemInfoGuanBangLiao(itemName);
        //如果拆解不为空
        if (maliyVO.getItemName() != null) {
            //遍历供应商
            List<MaterialMaliyVO> mailyVOS = materialMailyVoMapper.selectMaterilaList(maliyVO);
            if (mailyVOS.size() > 0) {
                for (MaterialMaliyVO mailyVO : mailyVOS) {

                    maliyVO.setSupplierCode(mailyVO.getSupplierCode());
                    maliyVO.setSupplierName(mailyVO.getSupplierName());
                    if (maliyVO.getExternalDiameter() < 300) {
                        MaterialMaliyVO materialExternalDiameter = materialMailyVoMapper.selectExter(maliyVO.getExternalDiameter());
                        //根据外径找内径
                        MaterialMaliyVO material85 = materialMailyVoMapper.selectInner(materialExternalDiameter.getExternalDiameter(), maliyVO.getInternalDiamete());

                        if (material85 != null) {
                            //材料费
                            MaterialMaliyVO materialList = getMaterialPrice(maliyVO, material85.getExternalDiameter(), material85.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength(), maliyVO.getSupplierCode());
                            if (materialList == null) {
                                continue;
                            }
                            maliyVO.setSupplierName(materialList.getSupplierName());
                            maliyVO.setSupplierCode(materialList.getSupplierCode());
                            maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
                            maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
                            maliyVO.setWeight(materialList.getWeight()); //单重

                            //喷涂费
                            MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
                            if (sprayList.equals(null)) {
                                continue;
                            }
                            maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //喷涂单价
                            maliyVO.setSprayPrice(sprayList.getSprayPrice());//喷涂费

                            //加工费
                            MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                            if (proscessList == null) {
                                continue;
                            }
                            maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
                            maliyVO.setPrice(proscessList.getPrice()); //切割费

                            //产品单价   产品单价=材料费+喷涂费+加工费+切割费
                            BigDecimal productPrice = null;
                            if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                                productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                                maliyVO.setUnitPrice(productPrice);
                            }
                            maliyVO.setUnitPrice(productPrice);

                            MaterialMaliyVO vo = new MaterialMaliyVO();
                            if (maliyVO.getProcessingFee() != null && maliyVO.getSprayPrice() != null && maliyVO.getMaterialPrice() != null) {
                                vo.setSprayPrice(maliyVO.getSprayPrice()); // 喷涂费
                                vo.setPrice(maliyVO.getPrice());// 切割费
                                vo.setProcessingFee(maliyVO.getProcessingFee()); // 加工费
                                vo.setMaterialPrice(maliyVO.getMaterialPrice()); // 材料费
                                vo.setCoatingPrice(maliyVO.getCoatingPrice()); // 喷涂单价
                                vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice());// 材料单价
                                vo.setWeight(maliyVO.getWeight()); // 单重
                                vo.setUnitPrice(maliyVO.getUnitPrice()); // 产品单价
                                vo.setSupplierCode(maliyVO.getSupplierCode()); // 供应商编码
                                vo.setSupplierName(maliyVO.getSupplierName());  // 供应商名称
                                vo.setCoating(maliyVO.getCoating());  // 涂层
                                vo.setItemCode(itemCode); // 物料编号
                                vo.setItemName(itemName); // 物料描述
                                vo.setLength(maliyVO.getLength());  // 物料长度
                                vo.setTheMaterial(maliyVO.getTheMaterial()); // 物料材质
                                vo.setExternalDiameter(maliyVO.getExternalDiameter()); // 外径
                                vo.setInternalDiamete(maliyVO.getInternalDiamete()); // 内径
                                maliyVOList.add(vo);
                            } else {
                                continue;
                            }
                        } else {
                            Double exter = maliyVO.getExternalDiameter() + 10;
                            Double inner = maliyVO.getInternalDiamete() - 10;
                            //材料费
                            MaterialMaliyVO materialList = getMaterialPrice(maliyVO, exter, inner, maliyVO.getTheMaterial(), maliyVO.getLength(), maliyVO.getSupplierCode());
                            if (materialList == null) {
                                continue;
                            }
                            maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
                            maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
                            maliyVO.setWeight(materialList.getWeight()); //单重

                            //喷涂费
                            MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierName());
                            if (sprayList.equals(null)) {
                                continue;
                            }
                            maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //喷涂单价
                            maliyVO.setSprayPrice(sprayList.getSprayPrice());//喷涂费

                            //加工费
                            MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                            if (proscessList == null) {
                                continue;
                            }
                            maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
                            maliyVO.setPrice(proscessList.getPrice()); //切割费

                            //产品单价   产品单价=材料费+喷涂费+加工费+切割费
                            BigDecimal productPrice = null;
                            if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                                productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                                maliyVO.setUnitPrice(productPrice);
                            }
                            maliyVO.setUnitPrice(productPrice);

                            MaterialMaliyVO vo = new MaterialMaliyVO();
                            if (!(maliyVO.getProcessingFee().equals(new BigDecimal("0"))) && !(maliyVO.getSprayPrice().equals(new BigDecimal("0"))) && !(maliyVO.getMaterialPrice().equals(new BigDecimal("0")))) {
                                vo.setSprayPrice(maliyVO.getSprayPrice()); // 喷涂费
                                vo.setPrice(maliyVO.getPrice());// 切割费
                                vo.setProcessingFee(maliyVO.getProcessingFee()); // 加工费
                                vo.setMaterialPrice(maliyVO.getMaterialPrice()); // 材料费
                                vo.setCoatingPrice(maliyVO.getCoatingPrice()); // 喷涂单价
                                vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice());// 材料单价
                                vo.setWeight(maliyVO.getWeight()); // 单重
                                vo.setUnitPrice(maliyVO.getUnitPrice()); // 产品单价
                                vo.setSupplierCode(maliyVO.getSupplierCode()); // 供应商编码
                                vo.setSupplierName(maliyVO.getSupplierName());  // 供应商名称
                                vo.setCoating(maliyVO.getCoating());  // 涂层
                                vo.setItemCode(itemCode); // 物料编号
                                vo.setItemName(itemName); // 物料描述
                                vo.setLength(maliyVO.getLength());  // 物料长度
                                vo.setTheMaterial(maliyVO.getTheMaterial()); // 物料材质
                                vo.setExternalDiameter(maliyVO.getExternalDiameter()); // 外径
                                vo.setInternalDiamete(maliyVO.getInternalDiamete()); // 内径
                                maliyVOList.add(vo);
                            } else {
                                continue;
                            }
                        }
                    } else if (maliyVO.getExternalDiameter() > 300) {
                        //直接计算  外径+10 内径 -10
                        Double exter = maliyVO.getExternalDiameter() + 10;
                        Double inner = maliyVO.getInternalDiamete() - 10;
                        //材料费
                        MaterialMaliyVO materialList = getMaterialPrice(maliyVO, exter, inner, maliyVO.getTheMaterial(), maliyVO.getLength(), maliyVO.getSupplierCode());
                        if (materialList == null) {
                            continue;
                        }
                        maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //材料单价
                        maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //材料费
                        maliyVO.setWeight(materialList.getWeight()); //单重

                        //喷涂费
                        MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
                        if (sprayList.equals(null)) {
                            continue;
                        }
                        maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //喷涂单价
                        maliyVO.setSprayPrice(sprayList.getSprayPrice());//喷涂费

                        //加工费
                        MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                        if (proscessList == null) {
                            continue;
                        }
                        maliyVO.setProcessingFee(proscessList.getProcessingFee()); //加工费
                        maliyVO.setPrice(proscessList.getPrice()); //切割费

                        //产品单价   产品单价=材料费+喷涂费+加工费+切割费
                        BigDecimal productPrice = null;
                        if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                            productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                            maliyVO.setUnitPrice(productPrice);
                        }
                        maliyVO.setUnitPrice(productPrice);

                        MaterialMaliyVO vo = new MaterialMaliyVO();

                        if (!(maliyVO.getProcessingFee().equals(new BigDecimal("0"))) && !(maliyVO.getSprayPrice().equals(new BigDecimal("0"))) && !(maliyVO.getMaterialPrice().equals(new BigDecimal("0")))) {
                            vo.setSprayPrice(maliyVO.getSprayPrice()); // 喷涂费
                            vo.setPrice(maliyVO.getPrice());// 切割费
                            vo.setProcessingFee(maliyVO.getProcessingFee()); // 加工费
                            vo.setMaterialPrice(maliyVO.getMaterialPrice()); // 材料费
                            vo.setCoatingPrice(maliyVO.getCoatingPrice()); // 喷涂单价
                            vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice());// 材料单价
                            vo.setWeight(maliyVO.getWeight()); // 单重
                            vo.setUnitPrice(maliyVO.getUnitPrice()); // 产品单价
                            vo.setSupplierCode(maliyVO.getSupplierCode()); // 供应商编码
                            vo.setSupplierName(maliyVO.getSupplierName());  // 供应商名称
                            vo.setCoating(maliyVO.getCoating());  // 涂层
                            vo.setItemCode(itemCode); // 物料编号
                            vo.setItemName(itemName); // 物料描述
                            vo.setLength(maliyVO.getLength());  // 物料长度
                            vo.setTheMaterial(maliyVO.getTheMaterial()); // 物料材质
                            vo.setExternalDiameter(maliyVO.getExternalDiameter()); // 外径
                            vo.setInternalDiamete(maliyVO.getInternalDiamete()); // 内径
                            maliyVOList.add(vo);
                        } else {
                            continue;
                        }
                    }
                }
            }
        }
        return maliyVOList;
    }

    /**
     * 材料费
     *
     * @param maliyVO
     * @param externalDiameter
     * @param internalDiamete
     * @param theMaterial
     * @param length
     * @return
     */
    private MaterialMaliyVO getMaterialPrice(MaterialMaliyVO maliyVO, Double externalDiameter, Double internalDiamete, String theMaterial, Double length, String supplierCode) {
        String range = "";
        // 查找材质单价
        //Double itemprice = materialMailyVoMapper.selectTheMaterialPrice(theMaterial, externalDiameter.toString(), supplierCode);
        ItemInfoMaterialPriceMaliy itemInfoMaterialPriceMaliy = materialMailyVoMapper.selectMaterialPriceVO(theMaterial, externalDiameter.toString(), supplierCode);
        if (itemInfoMaterialPriceMaliy == null) {
            maliyVO.setTheMaterialPrice(null); // 材料单价
            maliyVO.setMaterialPrice(null);
            return maliyVO;
        }
        BigDecimal itemprice =itemInfoMaterialPriceMaliy.getTheMaterialPrice();
        maliyVO.setTheMaterialPrice(itemprice);

        String k=itemInfoMaterialPriceMaliy.getK();

        //计算单重
        BigDecimal price = new BigDecimal(externalDiameter).multiply(BigDecimal.valueOf(externalDiameter)).subtract(BigDecimal.valueOf(internalDiamete).multiply(BigDecimal.valueOf(internalDiamete)));
        BigDecimal weight = new BigDecimal(String.valueOf(price)).multiply(BigDecimal.valueOf(PI)).divide(BigDecimal.valueOf(4)).multiply(BigDecimal.valueOf(length)).multiply(BigDecimal.valueOf(8)).divide(BigDecimal.valueOf(1000000));
        //计算材料费
        BigDecimal MaterialPrice = new BigDecimal(String.valueOf(weight)).multiply(maliyVO.getTheMaterialPrice()).multiply(new BigDecimal(k));
        maliyVO.setWeight(weight.setScale(2, BigDecimal.ROUND_DOWN));  // 单重
        maliyVO.setMaterialPrice(MaterialPrice.setScale(2, BigDecimal.ROUND_DOWN)); // 材料费
        maliyVO.setTheMaterialPrice(maliyVO.getTheMaterialPrice()); // 材料单价
        return maliyVO;
    }

    /**
     * 加工费
     *
     * @param maliyVO
     * @param externalDiameter
     * @param internalDiamete
     * @param length
     * @return
     */
    private MaterialMaliyVO getProscessList(MaterialMaliyVO maliyVO, Double externalDiameter, Double internalDiamete, Double length, String supplierCode) {
        MaterialMaliyVO vo = materialMailyVoMapper.selectProcessPrices(externalDiameter, internalDiamete, length, supplierCode);
        if (vo == null) {
            maliyVO.setProcessingFee(null);  // 加工费
            return maliyVO;
        }
        length += 8;
        // 切割费
        if (length < 200) {
            //有切割费
            if (externalDiameter <= 80) {
                maliyVO.setPrice(BigDecimal.valueOf(6));
            }
            if (externalDiameter > 80 && externalDiameter <= 150) {
                maliyVO.setPrice(BigDecimal.valueOf(7.2));
            }
            if (externalDiameter > 150 && externalDiameter <= 200) {
                maliyVO.setPrice(BigDecimal.valueOf(15.6));
            }
            if (externalDiameter > 200) {
                maliyVO.setPrice(BigDecimal.valueOf(18)); // 切割费
            }
        } else {
            maliyVO.setPrice(new BigDecimal("0"));
        }

        maliyVO.setProcessingFee(vo.getProcessingFee().setScale(2, BigDecimal.ROUND_DOWN)); // 加工费
        return maliyVO;
    }

    /**
     * 计算喷涂费
     *
     * @param maliyVO
     * @param length
     * @param internalDiamete
     * @param coating
     * @param theMaterial
     * @return
     */
    private MaterialMaliyVO getSprayList(MaterialMaliyVO maliyVO, Double length, Double internalDiamete, String coating, String theMaterial, String supplierCode) {
        Double price = materialMailyVoMapper.selectBySprayPrice(coating, supplierCode, theMaterial);
        if (price == null) {
            maliyVO.setSprayPrice(null);  //喷涂费
            maliyVO.setCoatingPrice(null);
            return maliyVO;
        }
        maliyVO.setCoatingPrice(BigDecimal.valueOf(price));  //喷涂单价
        //计算喷涂费
        BigDecimal sprayPrice = new BigDecimal(internalDiamete).multiply(BigDecimal.valueOf(PI)).multiply(BigDecimal.valueOf(length)).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(price));


        if (("G05".equals(coating) || "G06".equals(coating)) && sprayPrice.doubleValue() < 40) {
            maliyVO.setSprayPrice(new BigDecimal(40));
        } else if (("G14".equals(coating) || "G20".equals(coating)) && sprayPrice.doubleValue() < 40) {
            maliyVO.setSprayPrice(new BigDecimal(55));
        } else {
            maliyVO.setSprayPrice(sprayPrice.setScale(2, BigDecimal.ROUND_DOWN));  // 喷涂费
        }
        return maliyVO;
    }

    /**
     * 获取 小零件 信息，弹出dialog
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoEntityOfXLJ> getItemInfoOfXLJVO(String itemCode, String itemName) {
        SubmitPriceReq req = new SubmitPriceReq();
        req.setItemCode(itemCode);

        // 注意只有自动获取的时候才可以更新数据，查看的时候不会。
        List<ItemInfoEntityOfXLJ> itemInfoOfXLJVOS = this.baseMapper.selectXLJReportList(req);
        if (itemInfoOfXLJVOS.size() > 0) {
            return itemInfoOfXLJVOS;
        }

        U9PrDTO u9Prs = new U9PrDTO();
        u9Prs.setId(1L);
        u9Prs.setItemCode(itemCode);
        u9Prs.setItemName(itemName);
        List<U9PrDTO> u9PrDTOList = new ArrayList<>();
        u9PrDTOList.add(u9Prs);

        // 分别处理计算价格并返回
        List<ItemInfoDTO> itemInfoDTOS = autoRetriveOfXLJ(u9PrDTOList);
        if (itemInfoDTOS.size() > 0) {
            itemInfoOfXLJVOS = this.baseMapper.selectXLJReportList(req);
        }

        return itemInfoOfXLJVOS;
    }

    /**
     * 获取 小零件 信息，弹出dialog
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoEntityOfDZ> getItemInfoOfDZVO(String itemCode, String itemName) {
        SubmitPriceReq req = new SubmitPriceReq();
        req.setItemCode(itemCode);

        // 注意只有自动获取的时候才可以更新数据，查看的时候不会。
        List<ItemInfoEntityOfDZ> itemInfoOfXLJVOS = this.baseMapper.selectDZReportList(req);

        return itemInfoOfXLJVOS;
    }


    /**
     * 获取 小零件 信息，弹出dialog
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoEntityOfFL> getItemInfoOfFLVO(String itemCode, String itemName) {
        SubmitPriceReq req = new SubmitPriceReq();
        req.setItemCode(itemCode);

        // 注意只有自动获取的时候才可以更新数据，查看的时候不会。
        List<ItemInfoEntityOfFL> itemInfoOfXLJVOS = this.baseMapper.selectFLReportList(req);

        return itemInfoOfXLJVOS;
    }

    /**
     * 获取 联轴器 信息，弹出dialog
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoEntityOfLZQ> getItemInfoOfLZQVO(String itemCode, String itemName) {
        SubmitPriceReq req = new SubmitPriceReq();
        req.setItemCode(itemCode);

        // 注意只有自动获取的时候才可以更新数据，查看的时候不会。
        List<ItemInfoEntityOfLZQ> itemInfoOfXLJVOS = this.baseMapper.selectLZQReportList(req);

        return itemInfoOfXLJVOS;
    }


    /**
     * 获取锻件信息，弹出dialog
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoOfZDJVO> getItemInfoOfDJVO(String itemCode, String itemName) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        List<ItemInfoOfZDJVO> itemInfoOfZDJVOS = new ArrayList<>();
        U9PrDTO u9Prs = new U9PrDTO();
        u9Prs.setId(1L);
        u9Prs.setItemCode(itemCode);
        u9Prs.setItemName(itemName);

        // 规格 & 材质
        String standards = "";
        String material = "";

        // 拆解
        ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(itemName);
        if (itemInfoEntity.getMaterial() == null) { // 正常拆解如果拆不出来，则 继续拆 材质
            itemInfoEntity = getMaterialItemInfoOfDuanJian(itemName);
            if (itemInfoEntity == null) { // 如果拆解还拆不出来，就返回null
                return null;
            }
        }
        // 规格 & 材质
        standards = itemInfoEntity.getSize() + "-" + itemInfoEntity.getPound();
        material = itemInfoEntity.getMaterial();

        // 分别处理计算价格并返回
        List<ItemInfoDTO> itemInfoDTOSOfOld = handleOfDJ(u9Prs, standards, material);
        List<ItemInfoDTO> itemInfoDTOSOfNew = handleOfDJOfNew(u9Prs, standards, material);

        // 新旧逻辑合并到旧的list里面
        if (itemInfoDTOSOfNew.size() > 0 && itemInfoDTOSOfOld.size() > 0) {
            itemInfoDTOSOfNew.get(0).getSupAndPriceList().stream().forEach(itemInfoDTO -> {
                itemInfoDTOSOfOld.get(0).getSupAndPriceList().add(itemInfoDTO);
            });
        } else if (itemInfoDTOSOfNew.size() > 0 && itemInfoDTOSOfOld.size() == 0) {
            itemInfoDTOSOfOld.addAll(itemInfoDTOSOfNew);
        }

        // 如果可以查到信息,处理数据进入到 最终的信息表中
        if (itemInfoDTOSOfOld.size() > 0) {
            this.handleDJReport(itemInfoDTOSOfOld, u9Prs);
        }

        itemInfoDTOS.addAll(itemInfoDTOSOfOld);

        if (itemInfoDTOS.size() > 0) {
            List<PriceVO> priceVOS = itemInfoDTOS.get(0).getSupAndPriceList();
            for (PriceVO priceVO : priceVOS) {
                ItemInfoOfZDJVO itemInfoOfZDJVO = BeanUtil.copy(priceVO, ItemInfoOfZDJVO.class);
                itemInfoOfZDJVO.setItemCode(itemCode);
                itemInfoOfZDJVO.setItemName(itemName);
                itemInfoOfZDJVO.setWeight(priceVO.getSingleWeight());
                itemInfoOfZDJVO.setPrice(priceVO.getSinglePrice());
                itemInfoOfZDJVO.setQuotePrice(priceVO.getPrice().setScale(2, RoundingMode.HALF_UP).toString());
                itemInfoOfZDJVOS.add(itemInfoOfZDJVO);
            }
        }
        return itemInfoOfZDJVOS;
    }


    /**
     * 自动获取锻件供应商信息
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfDuanJian(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        // 获取所有符合 锻件 的物料信息
        for (U9PrDTO u9Prs : u9PrDTOS) {
            // 若不是锻件，则下一条
            if (u9Prs.getItemName().indexOf("锻件") < 0 &&u9Prs.getItemName().indexOf("锻材") < 0) {
                continue;
            }
            // 规格 & 材质
            String standards = "";
            String material = "";

            // 拆解
            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(u9Prs.getItemName());
            if (itemInfoEntity.getMaterial() == null) { // 正常拆解如果拆不出来，则 继续拆 材质
                itemInfoEntity = getMaterialItemInfoOfDuanJian(u9Prs.getItemName());
                if (itemInfoEntity == null) { // 如果拆解还拆不出来，就返回null
                    return null;
                }
            }
            // 规格 & 材质
            standards = itemInfoEntity.getSize() + "-" + itemInfoEntity.getPound();
            material = itemInfoEntity.getMaterial();

            // 分别处理计算价格并返回
            List<ItemInfoDTO> itemInfoDTOSOfOld = handleOfDJ(u9Prs, standards, material); // 原有的逻辑（余量是分开的，根据 外圆 / 规格 来取余量）
            List<ItemInfoDTO> itemInfoDTOSOfNew = handleOfDJOfNew(u9Prs, standards, material); // 新有逻辑 （余量放在一起，根据 外圆 + 高度 来取余量）

            // 新旧逻辑合并
            if (itemInfoDTOSOfNew.size() > 0 && itemInfoDTOSOfOld.size() > 0) {
                itemInfoDTOSOfNew.get(0).getSupAndPriceList().stream().forEach(itemInfoDTO -> {
                    itemInfoDTOSOfOld.get(0).getSupAndPriceList().add(itemInfoDTO);
                });
            } else if (itemInfoDTOSOfNew.size() > 0 && itemInfoDTOSOfOld.size() == 0) {
                itemInfoDTOSOfOld.addAll(itemInfoDTOSOfNew);
            }
            if (itemInfoDTOSOfOld.size() > 0 && itemInfoDTOSOfOld.get(0).getSupAndPriceList().size() > 1) {
                itemInfoDTOSOfOld.get(0).setSupColorType("1");
            }

            // 如果可以查到信息,处理数据进入到 最终的信息表中
            if (itemInfoDTOSOfOld.size() > 0) {
                this.handleDJReport(itemInfoDTOSOfOld, u9Prs);
            }

            itemInfoDTOS.addAll(itemInfoDTOSOfOld);
        }
        return itemInfoDTOS;
    }

    // 处理最终信息进入到表中
    private void handleDJReport(List<ItemInfoDTO> itemInfoDTOSOfOld, U9PrDTO u9Prs) {
        // 获取信息后，往锻件最终的信息表里填写数据，方便日后查看。
        Integer count = this.baseMapper.itemCodeOfDJIsExisted(u9Prs.getItemCode());
        if (count == 0) { // 最终信息表中没有该数据
            itemInfoDTOSOfOld.get(0).getSupAndPriceList().stream().forEach(priceInfo -> {
                String itemCode = u9Prs.getItemCode();
                String itemName = u9Prs.getItemName();
                String supCode = priceInfo.getSupCode();
                String supName = priceInfo.getSupName();
                String weight = priceInfo.getSingleWeight();
                String materialPrice = priceInfo.getSinglePrice();
                String price = priceInfo.getPrice().toString();
                ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = this.baseMapper.selectBasicItemInfoOfDJ(u9Prs.getItemCode());
                String bigOuterSize = "";
                String bigHeightSize = "";
                String smallOuterSize = "";
                String totalHeightSize = "";
                String innerSize = "";
                String outerSize = "";
                String heightSize = "";
                String formulaType = itemInfoEntityBasOfDJ.getType();
                String res = itemInfoEntityBasOfDJ.getRes();
                if ("A".equals(formulaType)) { //大小头公式
                    bigOuterSize = itemInfoEntityBasOfDJ.getBigOuterSize();  // 大头外径
                    bigHeightSize = itemInfoEntityBasOfDJ.getBigHeightSize();  // 大头高度
                    smallOuterSize = itemInfoEntityBasOfDJ.getSmallOuterSize();// 小头外径
                    totalHeightSize = itemInfoEntityBasOfDJ.getTotalHeightSize(); // 总高度
                    innerSize = itemInfoEntityBasOfDJ.getInnerSize();// 内径
                } else { // 常规
                    innerSize = itemInfoEntityBasOfDJ.getInnerSize();// 内径
                    outerSize = itemInfoEntityBasOfDJ.getOuterSize();  // 外径
                    heightSize = itemInfoEntityBasOfDJ.getHeightSize();  // 高度
                }
                String outerRemain = priceInfo.getOuterRemain();
                String innerRemain = priceInfo.getInnerRemain();
                String heightRemain = priceInfo.getHeightRemain();
                String newInnerRemain = priceInfo.getNewInnerRemain();

                if (getUser() != null) {
                    String account = getUser().getAccount();
                    String time = DateUtil.format(new Date(), "yyyy-MM-dd");
                    this.baseMapper.insertDjInfoReport(itemCode, itemName, supCode, supName, weight, materialPrice, price, bigOuterSize, bigHeightSize, smallOuterSize, totalHeightSize, innerSize, outerSize, heightSize, formulaType, res, heightRemain, outerRemain, innerRemain, account, time, newInnerRemain);
                }
            });
        } else {
            this.baseMapper.deleteByItemCode(u9Prs.getItemCode());
            itemInfoDTOSOfOld.get(0).getSupAndPriceList().stream().forEach(priceInfo -> {
                String itemCode = u9Prs.getItemCode();
                String itemName = u9Prs.getItemName();
                String supCode = priceInfo.getSupCode();
                String supName = priceInfo.getSupName();
                String weight = priceInfo.getSingleWeight();
                String materialPrice = priceInfo.getSinglePrice();
                String price = priceInfo.getPrice().toString();
                ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = this.baseMapper.selectBasicItemInfoOfDJ(u9Prs.getItemCode());
                String bigOuterSize = "";
                String bigHeightSize = "";
                String smallOuterSize = "";
                String totalHeightSize = "";
                String innerSize = "";
                String outerSize = "";
                String heightSize = "";
                String formulaType = itemInfoEntityBasOfDJ.getType();
                String res = itemInfoEntityBasOfDJ.getRes();
                if ("A".equals(formulaType)) { //大小头公式
                    bigOuterSize = itemInfoEntityBasOfDJ.getBigOuterSize();  // 大头外径
                    bigHeightSize = itemInfoEntityBasOfDJ.getBigHeightSize();  // 大头高度
                    smallOuterSize = itemInfoEntityBasOfDJ.getSmallOuterSize();// 小头外径
                    totalHeightSize = itemInfoEntityBasOfDJ.getTotalHeightSize(); // 总高度
                    innerSize = itemInfoEntityBasOfDJ.getInnerSize();// 内径
                } else { // 常规
                    innerSize = itemInfoEntityBasOfDJ.getInnerSize();// 内径
                    outerSize = itemInfoEntityBasOfDJ.getOuterSize();  // 外径
                    heightSize = itemInfoEntityBasOfDJ.getHeightSize();  // 高度
                }
                String outerRemain = priceInfo.getOuterRemain();
                String innerRemain = priceInfo.getInnerRemain();
                String heightRemain = priceInfo.getHeightRemain();
                String newInnerRemain = priceInfo.getNewInnerRemain();

                if (getUser() != null) {
                    String account = getUser().getAccount();
                    String time = DateUtil.format(new Date(), "yyyy-MM-dd");
                    this.baseMapper.insertDjInfoReport(itemCode, itemName, supCode, supName, weight, materialPrice, price, bigOuterSize, bigHeightSize, smallOuterSize, totalHeightSize, innerSize, outerSize, heightSize, formulaType, res, heightRemain, outerRemain, innerRemain, account, time, newInnerRemain);
                }
            });
        }
    }


    /**
     * international修改
     * 自动获取全程委外的供应商信息
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfWW(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        // 获取所有符合 铸件锻件 的物料信息
        for (U9PrDTO u9Prs : u9PrDTOS) {
            // 若不是委外，则下一条
            if (!u9Prs.getBizType().toString().equals(WW)) {
                continue;
            }
            // 拆解
            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfQCWW(u9Prs.getItemName());
            // 若拆解成功
            if (itemInfoEntity.getSeries() != null) {
                // 第一步查找，可能出现多家供应商
                List<ItemInfoEntityOfWW> itemInfoEntitiesWW = this.baseMapper.selectSupAndWeightOfQCWW(itemInfoEntity);
                String price = "";

                // 遍历每一家供应商
                if (itemInfoEntitiesWW.size() > 0) {

                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
                    List<PriceVO> priceVOS = new ArrayList<>();

                    // id，用来识别前台的选中行
                    itemInfoDTO.setId(u9Prs.getId().toString());
                    itemInfoDTO.setItemCode(u9Prs.getItemCode());
                    itemInfoDTO.setItemName(u9Prs.getItemName());

                    // 第二次查找，找到单价
                    for (ItemInfoEntityOfWW iteminfo : itemInfoEntitiesWW) {
                        price = iteminfo.getPrice().toString();
                        // 若能找出单价
                        if (StringUtils.isNotEmpty(price)) {
                            PriceVO priceVO = new PriceVO();
                            // 设值单价和供应商的连带关系
                            priceVO.setSupName(iteminfo.getSupName());
                            priceVO.setSupCode(iteminfo.getSupCode());
                            priceVO.setPriority(iteminfo.getPriority());
                            // 四舍五入 保留2位小数 单重*单价+加工费
                            BigDecimal priceToReturn = (new BigDecimal(price));
                            priceVO.setPrice(priceToReturn);
                            priceVOS.add(priceVO);
                            itemInfoDTO.setSupAndPriceList(priceVOS);
                        }
                    }

                    // 若一行物料对应的供应商有多行，supType变为 1
                    if ((itemInfoEntitiesWW != null) && (itemInfoEntitiesWW.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                        itemInfoDTO.setSupColorType("1");
                    } else {
                        itemInfoDTO.setSupColorType("0");
                    }
                    // 设值默认值
                    if (itemInfoDTO.getSupAndPriceList().size() > 0) {
                        itemInfoDTOS.add(itemInfoDTO);
                    }
                }
            }
        }
        return itemInfoDTOS;
    }


    /**
     * 重置PR单的信息
     *
     * @param submitPriceReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removePrList(SubmitPriceReq submitPriceReq) {
        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();

        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
            this.baseMapper.updateU9Pr(submitPriceDTO.getId());
            this.baseMapper.deleteIo(submitPriceDTO.getIoId());
        }
        return true;
    }

    /**
     * 删除物料号的基础信息和报表信息
     *
     * @param submitPriceReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDJInfo(SubmitPriceReq submitPriceReq) {
        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();
        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
            this.baseMapper.deleteDjInfoBasic(submitPriceDTO.getItemCode());
            String account = getUser().getAccount();
            String time = DateUtil.format(new Date(), "yyyy-MM-dd");
            this.baseMapper.deleteDjInfoReport(submitPriceDTO.getItemCode(), account, time);
        }
        return true;
    }


    /**
     * 删除物料号的基础信息和报表信息  - 小零件
     *
     * @param submitPriceReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteXLJInfo(SubmitPriceReq submitPriceReq) {
        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();
        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
            this.baseMapper.deleteXLJInfoBasic(submitPriceDTO.getItemCode());
            this.baseMapper.deleteXLJInfoReport(submitPriceDTO.getItemCode());
        }
        return true;
    }

    /**
     * 获取铸件信息，弹出dialog
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoOfZDJVO> getItemInfoOfZDJVO(String itemCode, String itemName) {
        List<ItemInfoOfZDJVO> itemInfoOfCastVOS = new ArrayList<>();
        ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(itemName);
        // 若拆解成功
        if (itemInfoEntity.getSeries() != null) {
            // 第一步查找，可能出现多家供应商
            List<ItemInfoEntityOfZDJ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfZDJ(itemInfoEntity);

            // 遍历每一家供应商
            if (itemInfoEntitiesFromDataBase.size() > 0) {
                // 第二次查找，找到单价
                for (ItemInfoEntityOfZDJ iteminfo : itemInfoEntitiesFromDataBase) {
                    String extraPrice = iteminfo.getCharge();
                    if (StringUtils.isEmpty(extraPrice)) {
                        extraPrice = "0";
                    }

                    // 无论是否替换，都用原来的 材料
                    iteminfo.setMaterial(itemInfoEntity.getMaterial());

                    String price = this.baseMapper.selectItemPriceOfZDJ(iteminfo);
                    // 若能找出单价
                    if (StringUtils.isNotEmpty(price)) {
                        ItemInfoOfZDJVO itemInfoOfCastVO = new ItemInfoOfZDJVO();

                        itemInfoOfCastVO.setItemCode(itemCode);
                        itemInfoOfCastVO.setItemName(itemName);
                        itemInfoOfCastVO.setSupCode(iteminfo.getSupCode());
                        itemInfoOfCastVO.setSupName(iteminfo.getSupName());
                        itemInfoOfCastVO.setWeight(iteminfo.getWeight());
                        itemInfoOfCastVO.setPrice(price);
                        itemInfoOfCastVO.setCharge(extraPrice);
                        itemInfoOfCastVO.setQuotePrice((new BigDecimal(price).multiply(new BigDecimal(iteminfo.getWeight())).add(new BigDecimal(extraPrice))).setScale(2, RoundingMode.HALF_UP).toString());

                        itemInfoOfCastVOS.add(itemInfoOfCastVO);
                    }
                }
            }
        }
        return itemInfoOfCastVOS;
    }

    /**
     * 获取委外信息，弹出dialog（全程委外）
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoOfZDJVO> getItemInfoOfWWVO(String itemCode, String itemName) {

        List<ItemInfoOfZDJVO> itemInfoOfCastVOS = new ArrayList<>();
        ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfQCWW(itemName);
        // 若拆解成功
        if (itemInfoEntity.getSeries() != null) {
            // 第一步查找，可能出现多家供应商
            List<ItemInfoEntityOfWW> itemInfoEntitiesWW = this.baseMapper.selectSupAndWeightOfQCWW(itemInfoEntity);
            ;

            // 遍历每一家供应商
            if (itemInfoEntitiesWW.size() > 0) {
                // 第二次查找，找到单价
                for (ItemInfoEntityOfWW iteminfo : itemInfoEntitiesWW) {

                    // 无论是否替换，都用原来的 材料
                    iteminfo.setMaterial(itemInfoEntity.getMaterial());

                    String price = iteminfo.getPrice().toString();
                    // 若能找出单价
                    if (StringUtils.isNotEmpty(price)) {
                        ItemInfoOfZDJVO itemInfoOfCastVO = new ItemInfoOfZDJVO();

                        itemInfoOfCastVO.setItemCode(itemCode);
                        itemInfoOfCastVO.setItemName(itemName);
                        itemInfoOfCastVO.setSupCode(iteminfo.getSupCode());
                        itemInfoOfCastVO.setSupName(iteminfo.getSupName());
                        itemInfoOfCastVO.setPrice(price);
                        itemInfoOfCastVOS.add(itemInfoOfCastVO);
                    }
                }
            }
        }
        return itemInfoOfCastVOS;
    }

    /**
     * 获取球座信息，弹出dialog
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoOfQZVO> getItemInfoOfQZVO(String itemCode, String itemName) {
        List<ItemInfoOfQZVO> itemInfoOfCastVOS = new ArrayList<>();
        ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(itemName);
        // 若拆解成功
        if (itemInfoEntity.getItemize() != null) {
            // 第一步查找，可能出现多家供应商
            List<ItemInfoEntityOfQZ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfQZ(itemInfoEntity);

            // 遍历每一家供应商
            if (itemInfoEntitiesFromDataBase.size() > 0) {
                // 第二次查找，找到单价
                for (ItemInfoEntityOfQZ iteminfo : itemInfoEntitiesFromDataBase) {
                    // 球体加工费
                    String qzCharge = iteminfo.getQzCharge();
                    if (StringUtils.isEmpty(qzCharge)) {
                        qzCharge = "0";
                    }
                    // 阀座加工费
                    String fzCharge = iteminfo.getFzCharge();
                    if (StringUtils.isEmpty(fzCharge)) {
                        fzCharge = "0";
                    }

                    String price = this.baseMapper.selectItemPriceOfQZ(iteminfo);
                    String range = "";
                    if (Integer.valueOf(iteminfo.getSize()) >= 0.5 && Integer.valueOf(iteminfo.getSize()) <= 10) {
                        range = "0.5≤寸级≤10寸";
                    } else {
                        range = "寸级≥12寸";
                    }
                    String ptPrice = this.baseMapper.selectPtPriceOfQZ(iteminfo, range);

                    // 若能找出单价
                    if (StringUtils.isNotEmpty(price)) {
                        ItemInfoOfQZVO itemInfoOfCastVO = new ItemInfoOfQZVO();

                        // 四舍五入 保留2位小数 （球座单重*单价+球座加工费）+ （阀座单重*单价+阀座加工费）
                        BigDecimal qzPrice = new BigDecimal(iteminfo.getQzWeight().trim()).multiply(new BigDecimal(price.trim())).add(new BigDecimal(qzCharge.trim())).add(new BigDecimal(iteminfo.getQzSprayArea().trim()).multiply(new BigDecimal(ptPrice.trim())));
                        BigDecimal fzPrice = new BigDecimal(iteminfo.getFzWeight().trim()).multiply(new BigDecimal(price.trim())).add(new BigDecimal(fzCharge.trim())).add(new BigDecimal(iteminfo.getFzSprayCharge().trim()));
                        BigDecimal priceToReturn = qzPrice.add(fzPrice.multiply(new BigDecimal("2"))).setScale(0, RoundingMode.HALF_UP);
                        itemInfoOfCastVO.setItemCode(itemCode);
                        itemInfoOfCastVO.setItemName(itemName);
                        itemInfoOfCastVO.setSupCode(iteminfo.getSupCode());
                        itemInfoOfCastVO.setSupName(iteminfo.getSupName());
                        itemInfoOfCastVO.setFzCharge(iteminfo.getFzCharge());
                        itemInfoOfCastVO.setFzWeight(iteminfo.getFzWeight());
                        itemInfoOfCastVO.setQzCharge(iteminfo.getQzCharge());
                        itemInfoOfCastVO.setQzWeight(iteminfo.getQzWeight());
                        itemInfoOfCastVO.setPrice(price);
                        itemInfoOfCastVO.setFzSprayCharge(iteminfo.getFzSprayCharge());
                        itemInfoOfCastVO.setQzSprayArea(iteminfo.getQzSprayArea());
                        itemInfoOfCastVO.setPtPrice(ptPrice);
                        itemInfoOfCastVO.setQuotePrice(priceToReturn.toString());
                        itemInfoOfCastVOS.add(itemInfoOfCastVO);
                    }
                }
            }
        }
        return itemInfoOfCastVOS;
    }

    @Override
    public List<ItemInfoEntityOfQZNew> getItemInfoOfQZNew(SubmitPriceReq submitPriceReq) {
        return this.baseMapper.selectQZReportList(submitPriceReq);
    }

    /**
     * 保存，批量保存 - 外层
     *
     * @param SubmitPriceReq
     * @return
     */
    @Override
    public boolean saveBatchOfOthers(SubmitPriceReq SubmitPriceReq) {

        List<SubmitPriceDTO> submitPriceDTOs = SubmitPriceReq.getSubmitPriceDTOs();

        // 根据id来去重
        List<SubmitPriceDTO> uniqueSubmitPriceDTOs = submitPriceDTOs.stream().collect(collectingAndThen(
            toCollection(() -> new TreeSet<>(comparingLong(SubmitPriceDTO::getId))), ArrayList::new)
        );

        for (SubmitPriceDTO submitPriceDTO : uniqueSubmitPriceDTOs) {

            List<IoEntity> ios = ioService.getByPrId(submitPriceDTO.getId());

            Supplier sup = supplierService.getByName(submitPriceDTO.getSupName());
            if (sup == null) {
                return false;
            }

            U9PrEntity pr = this.getById(submitPriceDTO.getId());
            if (pr == null) {
                throw new RuntimeException("未找到PR：" + "id：" + submitPriceDTO.getId());
            }

            for (IoEntity io : ios) {
                io.setPrId(pr.getId());
                io.setPrLn(pr.getPrLn());
                io.setPrCode(pr.getPrCode());
                io.setItemCode(pr.getItemCode());
                io.setItemName(pr.getItemName());
                io.setPriceNum(pr.getPriceNum());
                io.setPriceUom(pr.getPriceUom());
                io.setTcNum(pr.getTcNum());
                io.setTcUom(pr.getTcUom());
                io.setReqDate(pr.getReqDate());
                //是否按重量计算
                io.setIsByWeight(submitPriceDTO.getIsByWeight());
                io.setIsPersent(submitPriceDTO.getIsPersent());
                io.setRemark(submitPriceDTO.getRemark());
                io.setQuotePrice(submitPriceDTO.getQuotePrice());
                io.setPromiseDate(submitPriceDTO.getPromiseDate().getTime() / 1000 + 8 * 3600); //这里用datePicker会存在8小时时间差
                io.setSource(IIoService.SOURCE_PURCHSUBMIT);

                if ("flowSubmit".equals(submitPriceDTO.getType())) {
                    //暂存（待处理画面的）
                    pr.setStatus(IU9PrService.STATUS_FLOW_SUBMIT);
                } else if ("winBid".equals(submitPriceDTO.getType())) {
                    //提交（待处理画面的）
                    pr.setStatus(IU9PrService.STATUS_WINBID);
                } else {
                    // 提交审核 （待提交画面的）
                    pr.setStatus(IU9PrService.STATUS_WINBID);
                }
                ioService.updateById(io);
            }
            updateById(pr);
        }
        return true;
    }


    /**
     * 保存，批量保存 - 弹出框
     *
     * @param SubmitPriceReq
     * @return
     */
    @Override
    public boolean saveBatchOfOthersForDialog(SubmitPriceReq SubmitPriceReq) {

        List<SubmitPriceDTO> submitPriceDTOs = SubmitPriceReq.getSubmitPriceDTOs();

        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {

            IoEntity io = ioService.getById(submitPriceDTO.getIoId());

            Supplier sup = supplierService.getByName(submitPriceDTO.getSupName());
            if (sup == null) {
                return false;
            }

            U9PrEntity pr = this.getById(submitPriceDTO.getId());
            if (pr == null) {
                throw new RuntimeException("未找到PR：" + "id：" + submitPriceDTO.getId());
            }

            io.setPrId(pr.getId());
            io.setPrLn(pr.getPrLn());
            io.setPrCode(pr.getPrCode());
            io.setItemCode(pr.getItemCode());
            io.setItemName(pr.getItemName());
            io.setPriceNum(pr.getPriceNum());
            io.setPriceUom(pr.getPriceUom());
            io.setTcNum(pr.getTcNum());
            io.setTcUom(pr.getTcUom());
            io.setReqDate(pr.getReqDate());
            //是否按重量计算
            io.setIsByWeight(submitPriceDTO.getIsByWeight());
            io.setIsPersent(submitPriceDTO.getIsPersent());
            io.setRemark(submitPriceDTO.getRemark());
            io.setQuotePrice(submitPriceDTO.getQuotePrice());
            io.setPromiseDate(submitPriceDTO.getPromiseDate().getTime() / 1000 + 8 * 3600);
            io.setSource(IIoService.SOURCE_PURCHSUBMIT);

            if ("flowSubmit".equals(submitPriceDTO.getType())) {
                //暂存（待处理画面的）
                pr.setStatus(IU9PrService.STATUS_FLOW_SUBMIT);
            } else if ("winBid".equals(submitPriceDTO.getType())) {
                Integer bidNum = this.baseMapper.countTheBid(submitPriceDTO.getId().toString());
                if (bidNum != 1) {
                    throw new RuntimeException("提交审核失败，请检查该料号的中标情况是否有错！");
                }
                //提交（待处理画面的）
                pr.setStatus(IU9PrService.STATUS_WINBID);
            } else {
                Integer bidNum = this.baseMapper.countTheBid(submitPriceDTO.getId().toString());
                if (bidNum > 0) {
                    throw new RuntimeException("提交审核失败，请检查该料号的中标情况是否有错！");
                }
                // 提交审核 （待提交画面的）
                pr.setStatus(IU9PrService.STATUS_WINBID);
            }
            ioService.updateById(io);
            updateById(pr);
        }
        return true;
    }

    /**
     * 重置PR单的信息 - 其他小零件
     *
     * @param submitPriceReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removePrListOfOthers(SubmitPriceReq submitPriceReq) {
        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();

        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
            this.baseMapper.updateU9Pr(submitPriceDTO.getId());
        }
        return true;
    }


    /**
     * 设为中标 - 其他小零件
     *
     * @param submitPriceReq
     * @return
     */
    @Override
    public boolean winTheBid(SubmitPriceReq submitPriceReq) {

        Integer bidNum = this.baseMapper.countTheBid(submitPriceReq.getSubmitPriceDTOs().get(0).getId().toString());
        if (bidNum > 0) {
            throw new RuntimeException("该料号已经有中标商家了");
        }
        return this.baseMapper.winTheBid(submitPriceReq.getSubmitPriceDTOs().get(0).getIoId().toString());
    }

    /**
     * 取消中标 - 其他小零件
     *
     * @param submitPriceReq
     * @return
     */
    @Override
    public boolean cancelTheBid(SubmitPriceReq submitPriceReq) {
        return this.baseMapper.cancelTheBid(submitPriceReq.getSubmitPriceDTOs().get(0).getIoId().toString());
    }


    /**
     * 移至无供应商
     *
     * @param submitPriceReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean moveToOthersOfNoSup(SubmitPriceReq submitPriceReq) {
        List<U9PrDTO> u9PrDTOS = submitPriceReq.getU9PrDTOS();

        for (U9PrDTO u9PrDTO : u9PrDTOS) {
            this.baseMapper.removeIo(u9PrDTO.getId());
            this.baseMapper.updatePrToOthers(u9PrDTO.getId());
        }
        return true;
    }

    /**
     * 添加供应商交叉关系
     *
     * @param supItemOthers
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addOtherInfos(SupItemOthers supItemOthers) {

        List<String> supCodes = Arrays.asList(supItemOthers.getSupCode().split(","));

        for (String supCode : supCodes) {
            Supplier sup = iSupplierService.getByCode(supCode);
            if (sup == null) {
                throw new RuntimeException("不存在该供应商、请检查供应商！");
            }
            supItemOthers.setSupName(sup.getName());
            // 零：暂时添加 不是生效状态
            supItemOthers.setStatus(0);

            supItemOthers.setSupCode(supCode);
            this.baseMapper.addOtherInfos(supItemOthers);
        }
        return true;
    }

    /**
     * 获取其他小零件信息
     *
     * @param page
     * @param
     * @return
     */
    @Override
    public IPage<SupItemOthers> getOthersInfo(IPage<SupItemOthers> page, SubmitPriceReq submitPriceReq) {

        String itemCode = submitPriceReq.getItemCode();
        String mainCode = itemCode.substring(0, 6);
        String matQuilty = itemService.getByCode(itemCode).getMatQuality();

        return this.baseMapper.getOthersInfo(page, mainCode, matQuilty);
    }

    /**
     * 更新供应商交叉关系
     *
     * @param supItemOthers
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOtherInfos(SupItemOthers supItemOthers) {

        String supCode = supItemOthers.getSupCode();

        Supplier sup = iSupplierService.getByCode(supCode);
        if (sup == null) {
            throw new RuntimeException("不存在该供应商、请检查供应商！");
        }
        supItemOthers.setSupName(sup.getName());

        this.baseMapper.updateOtherInfos(supItemOthers);

        return true;
    }

    /**
     * 移除供应商交叉关系
     *
     * @param supItemOthers
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeOtherInfos(SupItemOthers supItemOthers) {

        String supCode = supItemOthers.getSupCode();

        Supplier sup = iSupplierService.getByCode(supCode);
        if (sup == null) {
            throw new RuntimeException("不存在该供应商、请检查供应商！");
        }
        supItemOthers.setSupName(sup.getName());

        return this.baseMapper.removeOtherInfos(supItemOthers);
    }


    /**
     * 提交并询价
     *
     * @param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendAndInquiry(SubmitPriceReq submitPriceReq) {

        U9PrEntity u9PrEntity = this.baseMapper.selectById(submitPriceReq.getSubmitPriceDTOs().get(0).getId());

        Item item = itemService.getByCode(submitPriceReq.getSubmitPriceDTOs().get(0).getItemCode());
        Integer addTime = item.getQuoteLimit();
        // 往后顺延工作日
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        Long endTime = 0L;
        if (weekday == 5) {
            endTime = today.getTime() / 1000 + addTime + 2 * 24 * 3600;
        } else if (weekday == 6) {
            endTime = today.getTime() / 1000 + addTime + 24 * 3600;
        } else {
            endTime = today.getTime() / 1000 + addTime;
        }

        List<SupItemOthers> supItemOthers = this.baseMapper.getOthersInfoList(item.getCode().substring(0, 6), item.getMatQuality());

        for (SupItemOthers supItems : supItemOthers) {
            IoEntity ioEntity = new IoEntity();

            ioEntity.setPrId(u9PrEntity.getId());
            ioEntity.setPrCode(u9PrEntity.getPrCode());
            ioEntity.setPrLn(u9PrEntity.getPrLn());
            ioEntity.setItemCode(u9PrEntity.getItemCode());
            ioEntity.setItemName(u9PrEntity.getItemName());
            ioEntity.setSupCode(supItems.getSupCode());
            ioEntity.setSupName(supItems.getSupName());
            ioEntity.setQuoteEndtime(endTime);
            ioEntity.setPriceNum(u9PrEntity.getPriceNum());
            ioEntity.setPriceUom(u9PrEntity.getPriceUom());
            ioEntity.setTcNum(u9PrEntity.getTcNum());
            ioEntity.setTcUom(u9PrEntity.getTcUom());
            ioEntity.setReqDate(u9PrEntity.getReqDate());
            ioEntity.setStatus(ioService.STATUS_INIT);
            ioEntity.setSource(ioService.SOURCE_QUOTE);

            // 创建新的IO
            ioService.save(ioEntity);
            // 将暂时生效的供应商交叉关系生效
            supItems.setStatus(1);
            this.baseMapper.updateStatusOfOthers(supItems);
        }

        // 更新PR的状态
        u9PrEntity.setStatus(STATUS_INQUIRY);
        u9PrEntity.setInquiryWay(INQUIRYWAY_COMPETE);
        saveOrUpdate(u9PrEntity);
        return true;
    }


    /**
     * 立即评标 - 小零件
     *
     * @param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean evaluateBidOfOthers(Long id) {
        List<IoEntity> ioEntities = this.baseMapper.getIoListByPrId(id);
        U9PrEntity pr = this.baseMapper.getPrById(id);

        // 材料费
        BigDecimal matPrice = new BigDecimal("0");
        BigDecimal mat = this.baseMapper.getMaterialCostByItemCode(pr.getItemCode());
        if (mat != null) {
            matPrice = mat;
        } else {
            throw new RuntimeException("材料费还未传到、请稍等...");
        }

        // 加工费
        BigDecimal laborPrice = new BigDecimal("0");
        BigDecimal lab = this.baseMapper.getLaborCostByItemCode(pr.getItemCode());
        if (lab != null) {
            laborPrice = lab;
        } else {
            throw new RuntimeException("加工费还未传到、请稍等...");
        }
        pr.setMaterialCost(matPrice);
        pr.setLaborCost(laborPrice);
        saveOrUpdate(pr);

        // 标准要求交期
        Long reqDate = pr.getReqDate();
        // 标准采购成本
        BigDecimal standandPrice = laborPrice.multiply(new BigDecimal("1.4")).add(matPrice);

        // 计算得分
        for (IoEntity ioEntity : ioEntities) {
            // 报价价格
            BigDecimal quotoPrice = ioEntity.getQuotePrice();

            // 报价日期
            Long quotoDate = ioEntity.getQuoteDate();

            // 承诺日期
            Long promiseDate = ioEntity.getPromiseDate();

            // 记录得分
            if (quotoDate == null || quotoPrice == null) {
                this.baseMapper.updateEvaluateScore(new BigDecimal("0"), ioEntity.getId());
            } else if (promiseDate > reqDate || quotoDate > reqDate || quotoPrice.compareTo(standandPrice) > 0) {
                this.baseMapper.updateEvaluateScore(new BigDecimal("0"), ioEntity.getId());
            } else {
                // 得分是： 标准采购成本 和 报价价格 的差 + 1
                this.baseMapper.updateEvaluateScore(standandPrice.subtract(quotoPrice).add(new BigDecimal("1")), ioEntity.getId());
            }
        }

        // 取得计算得分后的LIST
        List<IoEntity> ioEntitiesAfterScore = this.baseMapper.getIoListByPrId(id);
        // 基础得分 第一条数据
        List<IoEntity> bestIoEntities = new ArrayList<>();
        IoEntity baseIo = ioEntitiesAfterScore.get(0);

        for (IoEntity ioEntity : ioEntitiesAfterScore) {
            BigDecimal baseScore = baseIo.getEvaluateScore();

            // 如果大于基础数据
            if (ioEntity.getEvaluateScore().compareTo(baseScore) > 0) {
                // 清空之前的最好的数据，新增当前最好的数据
                bestIoEntities.clear();
                bestIoEntities.add(ioEntity);
                baseIo = ioEntity;
            } else if (ioEntity.getEvaluateScore().compareTo(baseScore) == 0) {
                // 新增当前最好的数据
                bestIoEntities.add(ioEntity);
            }
        }

        // 如果只有一个，但是这个评分不合格，那就要清空
        if (bestIoEntities.size() == 1 && bestIoEntities.get(0).getEvaluateScore().compareTo(new BigDecimal("0")) == 0) {
            bestIoEntities.clear();
        }

        // 如果只有一个，那么将这个IO的状态改为 代下单、其他的 改为 未中标；如果不是只有一个，那么就不改变状态，流标转人工
        if (bestIoEntities.size() == 1 && bestIoEntities.get(0).getEvaluateScore().compareTo(new BigDecimal("0")) > 0) {
            for (IoEntity ioEntity : ioEntitiesAfterScore) {
                // 不是同一个ID，则流标
                if (!ioEntity.getId().toString().equals(bestIoEntities.get(0).getId().toString())) {
                    this.baseMapper.setLoseTheBid(ioEntity);
                } else {
                    this.baseMapper.setWinTheBid(ioEntity.getId(), new Date().getTime() / 1000);
                }
            }
            this.baseMapper.setPrToWait(pr.getId());
        }

        // 没有符合条件的供应商 ,按流标处理
        if (bestIoEntities.size() == 0) {
            this.baseMapper.setPrFlow(pr.getId());
        }

        // 最佳评标大于 1 个的时候
        if (bestIoEntities.size() > 1) {
            this.baseMapper.setPrToChoose(pr.getId());
        }

        return true;
    }

    /**
     * 锻件信息报表
     *
     * @param
     * @return
     */
    @Override
    public IPage<ItemInfoEntityDJReport> getItemInfoOfDJVOReport(IPage<ItemInfoEntityDJReport> page, SubmitPriceReq submitPriceReq) {
        // 以下3行代码，仅在出现问题清空表时使用，平时注释即可。
//        List<ItemInfoVO> itemInfoVOS = this.baseMapper.selectAllDjItemCodes();
//        itemInfoVOS.stream().forEach( item ->{
//            this.getItemInfoOfDJVO(item.getItemCode(),item.getItemName());
//        });

//        IPage<ItemInfoEntityDJReport> itemInfoEntityDJReportPage = this.baseMapper.selectDjReportPage(page,submitPriceReq);
        IPage<ItemInfoEntityDJReport> itemInfoEntityDJReportPage = this.baseMapper.selectDjReportPage(page, submitPriceReq);
        return itemInfoEntityDJReportPage;
    }

    /**
     * 小零件信息报表
     *
     * @param
     * @return
     */
    @Override
    public IPage<ItemInfoEntityOfXLJ> getItemInfoOfXLJVOReport(IPage<ItemInfoEntityOfXLJ> page, SubmitPriceReq submitPriceReq) {
        IPage<ItemInfoEntityOfXLJ> itemInfoEntityXLJReportPage = this.baseMapper.selectXLJReportPage(page, submitPriceReq);
        for (ItemInfoEntityOfXLJ itemInfoEntityOfXLJ : itemInfoEntityXLJReportPage.getRecords()) {
            PoItemEntity poItemEntity = poItemService.getLastPoInfos(itemInfoEntityOfXLJ.getItemCode(), itemInfoEntityOfXLJ.getItemName());
            if (poItemEntity != null) {
                itemInfoEntityOfXLJ.setLastPrice(poItemEntity.getPrice().toString());
            }
        }
        return itemInfoEntityXLJReportPage;
    }

    /**
     * 联轴器信息报表
     *
     * @param
     * @return
     */
    @Override
    public IPage<ItemInfoEntityOfLZQ> getItemInfoOfLZQVOReport(IPage<ItemInfoEntityOfLZQ> page, SubmitPriceReq submitPriceReq) {
        IPage<ItemInfoEntityOfLZQ> itemInfoEntityLZQReportPage = this.baseMapper.selectLZQReportPage(page, submitPriceReq);
        return itemInfoEntityLZQReportPage;
    }

    @Override
    public IPage<ItemInfoEntityOfQZNew> getItemInfoOfQZReport(IPage<ItemInfoEntityOfQZNew> page, SubmitPriceReq submitPriceReq) {
        IPage<ItemInfoEntityOfQZNew> itemInfoEntityLZQReportPage = this.baseMapper.selectQZReportPage(page, submitPriceReq);
        return itemInfoEntityLZQReportPage;
    }


    /**
     * 底轴信息报表
     *
     * @param
     * @return
     */
    @Override
    public IPage<ItemInfoEntityOfDZ> getItemInfoOfDZVOReport(IPage<ItemInfoEntityOfDZ> page, SubmitPriceReq submitPriceReq) {
        IPage<ItemInfoEntityOfDZ> itemInfoEntityDZReportPage = this.baseMapper.selectDZReportPage(page, submitPriceReq);
        return itemInfoEntityDZReportPage;
    }

    /**
     * 法兰信息报表
     *
     * @param
     * @return
     */
    @Override
    public IPage<ItemInfoEntityOfFL> getItemInfoOfFLVOReport(IPage<ItemInfoEntityOfFL> page, SubmitPriceReq submitPriceReq) {
        IPage<ItemInfoEntityOfFL> itemInfoEntityFLReportPage = this.baseMapper.selectFLReportPage(page, submitPriceReq);
        return itemInfoEntityFLReportPage;
    }


    @Override
    public <T> T getBasicItemInfoOfDJ(String itemCode, String itemName) {
        ItemInfoEntityBasOfDJ itemInfoEntityBasZYDOfDJ = this.baseMapper.selectBasicItemInfoOfDJ(itemCode);
        return (T) itemInfoEntityBasZYDOfDJ;
    }

    @Override
    public <T> T getBasicItemInfoOfXLJ(String itemCode, String itemName) {
        ItemInfoEntityBasOfXLJ itemInfoEntityBasZYDOfXLJ = this.baseMapper.selectBasicItemInfoOfXLJ(itemCode);
        return (T) itemInfoEntityBasZYDOfXLJ;
    }

    @Override
    public <T> T getBasicItemInfoOfXLJRX(String itemCode, String itemName) {
        ItemInfoEntityBasOfXLJRX itemInfoEntityBasZYDOfXLJRX = this.baseMapper.selectBasicItemInfoOfXLJRX(itemCode);
        return (T) itemInfoEntityBasZYDOfXLJRX;
    }

    @Override
    public <T> T getBasicItemInfoOfDZ(String itemCode, String itemName) {
        ItemInfoEntityBasOfDZ itemInfoEntityBasOfDZ = this.baseMapper.selectBasicItemInfoOfDZ(itemCode);
        return (T) itemInfoEntityBasOfDZ;
    }







    /**
     * 锻件旧逻辑
     *
     * @param u9Prs
     * @param standards
     * @param material
     * @return
     */
    private List<ItemInfoDTO> handleOfDJ(U9PrDTO u9Prs, String standards, String material) {
        List<ItemInfoEntityResZYDOfDJ> itemInfoEntityResZYDOfDJList = new ArrayList<>();
        List<ItemInfoEntityResFMOfDJ> itemInfoEntityResFMOfDJList = new ArrayList<>();
        List<ItemInfoEntityResZFLOfDJ> itemInfoEntityResZFLOfDJList = new ArrayList<>();
        List<ItemInfoEntityResALLOfDJ> itemInfoEntityResALLOfDJList = new ArrayList<>();
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        Double bigOuterSize = 0D;
        Double bigHeightSize = 0D;
        Double smallOuterSize = 0D;
        Double totalHeightSize = 0D;
        Double innerSize = 0D;
        Double outerSize = 0D;
        Double heightSize = 0D;

        Double outSizeToSearch = 0D;

        // 锻件基础信息 根据物料名称找到物料
        ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = this.baseMapper.selectBasicItemInfoOfDJ(u9Prs.getItemCode());
        if (itemInfoEntityBasOfDJ == null) {   // 如果没有找到锻件物料,则返回空
            return itemInfoDTOS;
        }

        if (itemInfoEntityBasOfDJ.getBigOuterSize() != null && !itemInfoEntityBasOfDJ.getBigOuterSize().isEmpty()) {
            bigOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigOuterSize()).doubleValue();  // 大头外径
        }
        if (itemInfoEntityBasOfDJ.getBigHeightSize() != null && !itemInfoEntityBasOfDJ.getBigHeightSize().isEmpty()) {
            bigHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigHeightSize()).doubleValue();  // 大头高度
        }
        if (itemInfoEntityBasOfDJ.getSmallOuterSize() != null && !itemInfoEntityBasOfDJ.getSmallOuterSize().isEmpty()) {
            smallOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getSmallOuterSize()).doubleValue();// 小头外径
        }
        if (itemInfoEntityBasOfDJ.getTotalHeightSize() != null && !itemInfoEntityBasOfDJ.getTotalHeightSize().isEmpty()) {
            totalHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getTotalHeightSize()).doubleValue(); // 总高度
        }

        if (itemInfoEntityBasOfDJ.getInnerSize() != null && !itemInfoEntityBasOfDJ.getInnerSize().isEmpty()) {
            innerSize = Double.valueOf(itemInfoEntityBasOfDJ.getInnerSize()).doubleValue();// 内径
        }

        if (itemInfoEntityBasOfDJ.getOuterSize() != null && !itemInfoEntityBasOfDJ.getOuterSize().isEmpty()) {
            outerSize = Double.valueOf(itemInfoEntityBasOfDJ.getOuterSize()).doubleValue();  // 外径
        }
        if (itemInfoEntityBasOfDJ.getHeightSize() != null && !itemInfoEntityBasOfDJ.getHeightSize().isEmpty()) {
            heightSize = Double.valueOf(itemInfoEntityBasOfDJ.getHeightSize()).doubleValue();  // 高度
        }

        String formulaType = itemInfoEntityBasOfDJ.getType();
        if ("A".equals(formulaType)) { //大小头公式
            outSizeToSearch = bigOuterSize;
        } else {
            outSizeToSearch = outerSize;
        }

        // 余量选择
        if (itemInfoEntityBasOfDJ.getRes().equals("fm")) { // (阀帽的情况）
            itemInfoEntityResFMOfDJList = this.baseMapper.selectResItemInfoOfFm(standards, material);
            if (itemInfoEntityResFMOfDJList.size() > 0) {
                itemInfoEntityResFMOfDJList.stream().forEach(itemInfoEntityResFMOfDJ -> {
                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResFMOfDJ, ItemInfoEntityResALLOfDJ.class);
                    itemInfoEntityResALLOfDJList.add(iteminfo);
                });
            }
        } else if (itemInfoEntityBasOfDJ.getRes().equals("zfl")) {// (中法兰的情况)
            itemInfoEntityResZFLOfDJList = this.baseMapper.selectResItemInfoOfZfl(outSizeToSearch, material);
            if (itemInfoEntityResZFLOfDJList.size() > 0) {
                itemInfoEntityResZFLOfDJList.stream().forEach(itemInfoEntityResFMOfDJ -> {
                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResFMOfDJ, ItemInfoEntityResALLOfDJ.class);
                    itemInfoEntityResALLOfDJList.add(iteminfo);
                });
            }
        } else {// (自由锻的情况)
            itemInfoEntityResZYDOfDJList = this.baseMapper.selectResItemInfoOfZyd(outSizeToSearch, material);
            if (itemInfoEntityResZYDOfDJList.size() > 0) {
                itemInfoEntityResZYDOfDJList.stream().forEach(itemInfoEntityResZYDOfDJ -> {
                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResZYDOfDJ, ItemInfoEntityResALLOfDJ.class);
                    itemInfoEntityResALLOfDJList.add(iteminfo);
                });
            }
        }

        // 遍历每一家供应商
        if (itemInfoEntityResALLOfDJList.size() > 0) {
            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
            List<PriceVO> priceVOS = new ArrayList<>();
            // id，用来识别前台的选中行
            itemInfoDTO.setId(u9Prs.getId().toString());
            itemInfoDTO.setItemCode(u9Prs.getItemCode());
            itemInfoDTO.setItemName(u9Prs.getItemName());

            // 根据每一家供应商计算价格
            for (ItemInfoEntityResALLOfDJ iteminfo : itemInfoEntityResALLOfDJList) {
                Double outerRemain = Double.valueOf(iteminfo.getOuterRemain()).doubleValue(); // 外径余量
                Double innerRemain = Double.valueOf(iteminfo.getInnerRemain()).doubleValue(); // 内径余量
                Double heightRemain = Double.valueOf(iteminfo.getHeightRemain()).doubleValue(); // 高度余量

                Double weight = 0D;
                Double weightOfBasic = 0D;

                if (itemInfoEntityBasOfDJ.getType().equals("A")) { // --大小头公式--
                    // 重量=(((大头外径/2+外径余量/2)^2 – (内径/2-内径余量/2)^2 )) * PI * (大头高度+高度余量)+((小头外径/2 + 外径余量/2)^2-(内径/2 – 内径余量/2)^2)*PI*(总高度-大头高度))*7.856/1000000
                    if ((innerSize - innerRemain) >= 80) {
                        weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * PI * (totalHeightSize - bigHeightSize)) * 7.856 / 1000000;
                    } else {
                        weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) * PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2)) * PI * (totalHeightSize - bigHeightSize))) * 7.856 / 1000000;
                    }
                } else { // --常规公式--
                    // 重量 = PI*((外圆直径/2 +外圆余量/2)^2 – (内孔直径/2 - 内孔余量/2)^2)*(高度 + 高度余量)*7.856/1000000
                    if ((innerSize - innerRemain) >= 80) {
                        weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
                    } else {
                        weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
                    }
                }

                BigDecimal b = new BigDecimal(weightOfBasic);
                weight = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                // 单价
                String singlePrice = this.baseMapper.selectSinglePrice(iteminfo.getSupCode(), material);
                if (singlePrice == null) {
                    continue;
                }

                // 价格 = 重量 * 单价
                Double price = weight * Double.valueOf(singlePrice);

                // 设值单价和供应商的连带关系
                PriceVO priceVO = new PriceVO();
                priceVO.setSingleWeight(weight.toString());
                priceVO.setSupName(iteminfo.getSupName());
                priceVO.setSupCode(iteminfo.getSupCode());
                priceVO.setSinglePrice(singlePrice);
                priceVO.setPrice(new BigDecimal(price.toString()).setScale(1, BigDecimal.ROUND_HALF_UP));
                priceVOS.add(priceVO);

                priceVO.setHeightRemain(heightRemain.toString());
                priceVO.setInnerRemain(innerRemain.toString()); // 内径余量
                priceVO.setOuterRemain(outerRemain.toString()); // 外径余量

                itemInfoDTO.setSupAndPriceList(priceVOS);
            }

            // 若一行物料对应的供应商有多行，supType变为 1
            if ((itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                itemInfoDTO.setSupColorType("1");
            } else {
                itemInfoDTO.setSupColorType("0");
            }
            // 赋值（返回值）
            if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                itemInfoDTOS.add(itemInfoDTO);
            }
        }

        return itemInfoDTOS;
    }

    /**
     * 锻件新逻辑
     *
     * @param u9Prs
     * @param standards
     * @param material
     * @return
     */
    private List<ItemInfoDTO> handleOfDJOfNew(U9PrDTO u9Prs, String standards, String material) {
        List<ItemInfoEntityResCommonOfDJ> itemInfoEntityResCommonOfDJList = new ArrayList<>();
        List<ItemInfoEntityResALLOfDJ> itemInfoEntityResALLOfDJList = new ArrayList<>();
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        Double bigOuterSize = 0D;  // 大头外径
        Double bigHeightSize = 0D; // 大头高度
        Double smallOuterSize = 0D;  //小头外径
        Double totalHeightSize = 0D; // 总高度
        Double innerSize = 0D;  // 锻件内径
        Double outerSize = 0D;   // 锻件外圆
        Double heightSize = 0D;   // 锻件高度
        Double outSizeToSearch = 0D; // 外径-b

        Double height = 0D; // 高度
        Double waijing = 0D; // 外径
        Double neijing = 0D; // 内径

        // 锻件基础信息
        ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = this.baseMapper.selectBasicItemInfoOfDJ(u9Prs.getItemCode());
        if (itemInfoEntityBasOfDJ == null) {
            return itemInfoDTOS;
        }

        if (itemInfoEntityBasOfDJ.getBigOuterSize() != null && !itemInfoEntityBasOfDJ.getBigOuterSize().isEmpty()) {
            bigOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigOuterSize()).doubleValue();  // 大头外径
        }
        if (itemInfoEntityBasOfDJ.getBigHeightSize() != null && !itemInfoEntityBasOfDJ.getBigHeightSize().isEmpty()) {
            bigHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigHeightSize()).doubleValue();  // 大头高度
        }
        if (itemInfoEntityBasOfDJ.getSmallOuterSize() != null && !itemInfoEntityBasOfDJ.getSmallOuterSize().isEmpty()) {
            smallOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getSmallOuterSize()).doubleValue();// 小头外径
        }
        if (itemInfoEntityBasOfDJ.getTotalHeightSize() != null && !itemInfoEntityBasOfDJ.getTotalHeightSize().isEmpty()) {
            totalHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getTotalHeightSize()).doubleValue(); // 总高度
        }
        if (itemInfoEntityBasOfDJ.getInnerSize() != null && !itemInfoEntityBasOfDJ.getInnerSize().isEmpty()) {
            innerSize = Double.valueOf(itemInfoEntityBasOfDJ.getInnerSize()).doubleValue();// 内径
        }
        if (itemInfoEntityBasOfDJ.getOuterSize() != null && !itemInfoEntityBasOfDJ.getOuterSize().isEmpty()) {
            outerSize = Double.valueOf(itemInfoEntityBasOfDJ.getOuterSize()).doubleValue();  // 外径
        }
        if (itemInfoEntityBasOfDJ.getHeightSize() != null && !itemInfoEntityBasOfDJ.getHeightSize().isEmpty()) {
            heightSize = Double.valueOf(itemInfoEntityBasOfDJ.getHeightSize()).doubleValue();  // 高度
        }

        String formulaType = itemInfoEntityBasOfDJ.getType();
        if ("A".equals(formulaType)) { //大小头公式
            outSizeToSearch = bigOuterSize;
            height = totalHeightSize;
            waijing = bigOuterSize;
            neijing = innerSize;
        } else { // 常规
            outSizeToSearch = outerSize;
            height = heightSize;
            waijing = outerSize;
            neijing = innerSize;
        }

        // 余量选择 根据外径范围,高度范围及材质查找供应商
        itemInfoEntityResCommonOfDJList = this.baseMapper.selectResItemInfoOfNew(outSizeToSearch, material, height);
        if (itemInfoEntityResCommonOfDJList.size() > 0) {
            itemInfoEntityResCommonOfDJList.stream().forEach(itemInfoEntityResCommonOfDJ -> {
                ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResCommonOfDJ, ItemInfoEntityResALLOfDJ.class);
                itemInfoEntityResALLOfDJList.add(iteminfo);
            });
        }

        // 遍历每一家供应商
        if (itemInfoEntityResALLOfDJList.size() > 0) {
            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
            List<PriceVO> priceVOS = new ArrayList<>();
            // id，用来识别前台的选中行
            itemInfoDTO.setId(u9Prs.getId().toString());
            itemInfoDTO.setItemCode(u9Prs.getItemCode());
            itemInfoDTO.setItemName(u9Prs.getItemName());

            // 根据每一家供应商计算价格
            for (ItemInfoEntityResALLOfDJ iteminfo : itemInfoEntityResALLOfDJList) {
                //如果供应商为鑫裕,则更改逻辑 ,其他供应商逻辑不变
                ArrayList<Double> list = new ArrayList<>();
                Double wallThickness = 0D;  // 壁厚
                Double newInner = 0D; // 新内径
                Double newInnerRemain = 0D; // 新内径余量
                Double outerRemain = Double.valueOf(iteminfo.getOuterRemain()).doubleValue(); // 外径余量
                Double innerRemain = Double.valueOf(iteminfo.getInnerRemain()).doubleValue(); // 内径余量
                Double heightRemain = Double.valueOf(iteminfo.getHeightRemain()).doubleValue(); // 高度余量
                Double weight = 0D;   //单重
                Double weightOfBasic = 0D;


                U9DjPriceDTO dto = new U9DjPriceDTO();
                dto.setInnerRemain(innerRemain);   // 内径余量
                dto.setHeightRemain(heightRemain); // 高度余量
                dto.setOuterRemain(outerRemain);   // 外径余量

                dto.setInnerSize(innerSize);  // 锻件内径
                dto.setWeightOfBasic(weightOfBasic);
                dto.setBigHeightSize(bigHeightSize); // 大头高度
                dto.setBigOuterSize(bigOuterSize); // 大头外径
                dto.setSmallOuterSize(smallOuterSize); // 小头外径
                dto.setHeightSize(heightSize); // 锻件高度
                dto.setMaterial(material); // 材质
                dto.setTotalHeightSize(totalHeightSize); // 总高度
                dto.setOuterSize(outerSize); // 锻件外圆

                if ("MAT-004".equals(iteminfo.getSupCode())) {
                    // 1、如果外圆<=300
                    if (outerSize <= 300) {
                        // 计算壁厚  如果壁厚<30     ((外径+外径余量)-(内径-内径余量)) /2
                        if ((((waijing + outerRemain) - (neijing - innerRemain)) / 2) < 30) {
                            wallThickness = 30D;
                            // 新内径  (外圆+外圆余量)-壁厚*2
                            newInner = (waijing + outerRemain) - wallThickness * 2;
                            // 新内径余量   内径-新内径
                            newInnerRemain = neijing - newInner;
                            dto.setNewInner(newInner);  // 新内径
                            dto.setNewInnerRemain(newInnerRemain);   // 新内径余量
                            // 计算价格
                            itemInfoDTO = getPriceOfDJ(itemInfoEntityBasOfDJ, itemInfoDTO, iteminfo, priceVOS, dto);
                        } else {
                            newInnerRemain = 0D;
                            dto.setNewInner(newInner);  // 新内径
                            dto.setNewInnerRemain(newInnerRemain);   // 新内径余量
                            // 如果没有新内径余量 按照原来逻辑计算
                            itemInfoDTO = getPriceOfDJOld(itemInfoEntityBasOfDJ, itemInfoDTO, iteminfo, priceVOS, dto);
                        }
                    } else if (outerSize > 300) {

                        //因为要提前计算重量逻辑，所以这段代码和后面重复了，后面可以优化掉
                        if (itemInfoEntityBasOfDJ.getType().equals("A")) { // --大小头公式--
                            // 重量=(((大头外径/2+外径余量/2)^2 – (内径/2-内径余量/2)^2 )) * PI * (大头高度+高度余量)+((小头外径/2 + 外径余量/2)^2-(内径/2 – 内径余量/2)^2)*PI*(总高度-大头高度))*7.856/1000000
                            if ((innerSize - innerRemain) >= 80) {
                                weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * PI * (totalHeightSize - bigHeightSize)) * 7.856 / 1000000;
                            } else {
                                weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) * PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2)) * PI * (totalHeightSize - bigHeightSize))) * 7.856 / 1000000;
                            }
                        } else { // --常规公式--
                            // 重量 = PI*((外圆直径/2 +外圆余量/2)^2 – (内孔直径/2 - 内孔余量/2)^2)*(高度 + 高度余量)*7.856/1000000
                            if ((innerSize - innerRemain) >= 80) {
                                weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
                            } else {
                                weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
                            }
                        }

                        log.debug("重量   {}", weightOfBasic);

                        // 判断条件壁厚
                        wallThickness = ((waijing + outerRemain) - (neijing - innerRemain)) / 2;
                        // 壁厚 壁厚小于外圆的百分之10  且重量＜200KG
                        /*if (wallThickness < ((waijing + outerRemain) * 0.1) && weightOfBasic < 200) {
                            // 壁厚
                            wallThickness = (waijing + outerRemain) * 0.1;
                            // 新内径  (外圆+外圆余量)-壁厚*2
                            newInner = (waijing + outerRemain) - wallThickness * 2;
                            // 新内径余量   内径-新内径
                            newInnerRemain = neijing - newInner;
                            dto.setNewInner(newInner);  // 新内径
                            dto.setNewInnerRemain(newInnerRemain);   // 新内径余量
                            // 计算价格
                            itemInfoDTO = getPriceOfDJ(itemInfoEntityBasOfDJ, itemInfoDTO, iteminfo, priceVOS, dto);
                        } else */

                        if (wallThickness < 55) {
                            // 壁厚
                            wallThickness = Double.valueOf(55);
                            // 新内径  (外圆+外圆余量)-壁厚*2
                            newInner = (waijing + outerRemain) - wallThickness * 2;
                            // 新内径余量   内径-新内径
                            newInnerRemain = neijing - newInner;
                            dto.setNewInner(newInner);  // 新内径
                            dto.setNewInnerRemain(newInnerRemain);   // 新内径余量
                            // 计算价格
                            itemInfoDTO = getPriceOfDJ(itemInfoEntityBasOfDJ, itemInfoDTO, iteminfo, priceVOS, dto);

                        } else {
                            newInnerRemain = 0D;
                            dto.setNewInner(newInner);  // 新内径
                            dto.setNewInnerRemain(newInnerRemain);   // 新内径余量
                            // 如果没有新内径余量 按照原来逻辑计算
                            itemInfoDTO = getPriceOfDJOld(itemInfoEntityBasOfDJ, itemInfoDTO, iteminfo, priceVOS, dto);
                        }
                    }
                }
            }
            // 若一行物料对应的供应商有多行，supType变为 1
            if ((itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                itemInfoDTO.setSupColorType("1");
            } else {
                itemInfoDTO.setSupColorType("0");
            }
            // 赋值（返回值）
            if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                itemInfoDTOS.add(itemInfoDTO);
            }
        }
        return itemInfoDTOS;
    }


    /**
     * 锻件（小零件专用）
     *
     * @return
     */
    private ItemInfoEntityOfXLJ handleOfDJOfXLJItem(ItemInfoEntityOfXLJ itemInfoEntityOfXLJ) {
        List<ItemInfoEntityResZYDOfDJ> itemInfoEntityResZYDOfDJList = new ArrayList<>();
        List<ItemInfoEntityResALLOfDJ> itemInfoEntityResALLOfDJList = new ArrayList<>();
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        Double innerSize = 0D;  // 锻件内径
        Double outerSize = 0D;   // 锻件外圆
        Double heightSize = 0D;   // 锻件高度
        Double outSizeToSearch = 0D; // 外径-b

        Double height = 0D; // 高度
        Double waijing = 0D; // 外径
        Double neijing = 0D; // 内径

        if (itemInfoEntityOfXLJ.getInnerSize() != null && !itemInfoEntityOfXLJ.getInnerSize().isEmpty()) {
            innerSize = Double.valueOf(itemInfoEntityOfXLJ.getInnerSize()).doubleValue();// 内径
        }
        if (itemInfoEntityOfXLJ.getOuterSize() != null && !itemInfoEntityOfXLJ.getOuterSize().isEmpty()) {
            outerSize = Double.valueOf(itemInfoEntityOfXLJ.getOuterSize()).doubleValue();  // 外径
        }
        if (itemInfoEntityOfXLJ.getHeightSize() != null && !itemInfoEntityOfXLJ.getHeightSize().isEmpty()) {
            heightSize = Double.valueOf(itemInfoEntityOfXLJ.getHeightSize()).doubleValue();  // 高度
        }

        String formulaType = "B";
        outSizeToSearch = outerSize;
        height = heightSize;
        waijing = outerSize;
        neijing = innerSize;

        // 余量选择 根据外径范围,高度范围及材质查找供应商
        itemInfoEntityResZYDOfDJList = this.baseMapper.selectResItemInfoOfZyd(outSizeToSearch, itemInfoEntityOfXLJ.getMaterial());
        if (itemInfoEntityResZYDOfDJList.size() > 0) {
            itemInfoEntityResZYDOfDJList.stream().forEach(itemInfoEntityResZYDOfDJ -> {
                if ("MAT-045".equals(itemInfoEntityResZYDOfDJ.getSupCode())) {
                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResZYDOfDJ, ItemInfoEntityResALLOfDJ.class);
                    itemInfoEntityResALLOfDJList.add(iteminfo);
                }
            });
        }

        // 遍历每一家供应商
        if (itemInfoEntityResALLOfDJList.size() > 0) {
            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
            List<PriceVO> priceVOS = new ArrayList<>();

            // 根据每一家供应商计算价格
            for (ItemInfoEntityResALLOfDJ iteminfo : itemInfoEntityResALLOfDJList) {
                Double outerRemain = Double.valueOf(iteminfo.getOuterRemain()).doubleValue(); // 外径余量
                Double innerRemain = Double.valueOf(iteminfo.getInnerRemain()).doubleValue(); // 内径余量
                Double heightRemain = Double.valueOf(iteminfo.getHeightRemain()).doubleValue(); // 高度余量

                itemInfoEntityOfXLJ.setOuterRes(outerRemain.toString());
                itemInfoEntityOfXLJ.setInnerRes(innerRemain.toString());
                itemInfoEntityOfXLJ.setHeightRes(heightRemain.toString());

                itemInfoEntityOfXLJ.setOuterSize(String.valueOf(outerSize + outerRemain));
                itemInfoEntityOfXLJ.setInnerSize(String.valueOf(innerSize - innerRemain));
                itemInfoEntityOfXLJ.setHeightSize(String.valueOf(heightSize + heightRemain));

                Double weight = 0D;
                Double weightOfBasic = 0D;

                // 重量 = PI*((外圆直径/2 +外圆余量/2)^2 – (内孔直径/2 - 内孔余量/2)^2)*(高度 + 高度余量)*7.856/1000000 [常规公式]
                if ((innerSize - innerRemain) >= 80) {
                    weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
                } else {
                    weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
                }

                BigDecimal b = new BigDecimal(weightOfBasic);
                weight = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                itemInfoEntityOfXLJ.setWeight(String.valueOf(weight));

                // 单价
                String singlePrice = this.baseMapper.selectSinglePrice(iteminfo.getSupCode(), itemInfoEntityOfXLJ.getMaterial());
                if (singlePrice == null) {
                    return itemInfoEntityOfXLJ;
                }
                itemInfoEntityOfXLJ.setSingleCost(singlePrice);

                // 价格 = 重量 * 单价
                Double price = weight * Double.valueOf(singlePrice);
                itemInfoEntityOfXLJ.setMaterialCost(price.toString());
            }
        }
        return itemInfoEntityOfXLJ;
    }


    /**
     * 不满足条件旧逻辑计算
     *
     * @param itemInfoEntityBasOfDJ
     * @param itemInfoDTO
     * @param iteminfo
     * @param priceVOS
     * @param dto
     * @return
     */
    private ItemInfoDTO getPriceOfDJOld(ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ, ItemInfoDTO itemInfoDTO, ItemInfoEntityResALLOfDJ iteminfo, List<PriceVO> priceVOS, U9DjPriceDTO dto) {
        // 计算价格
        Double weightOfBasic = 0D;
        Double weight = 0D;   //单重
        if (itemInfoEntityBasOfDJ.getType().equals("A")) { // --大小头公式--
            // 重量=(((大头外径/2+外径余量/2)^2 – (内径/2-内径余量/2)^2 )) * PI * (大头高度+高度余量)+((小头外径/2 + 外径余量/2)^2-(内径/2 – 内径余量/2)^2)*PI*(总高度-大头高度))*7.856/1000000
            if ((dto.getInnerSize() - dto.getInnerRemain()) >= 80) {
                weightOfBasic = (((dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) - (dto.getInnerSize() / 2 - dto.getInnerRemain() / 2) * (dto.getInnerSize() / 2 - dto.getInnerRemain() / 2)) * PI * (dto.getBigHeightSize() + dto.getHeightRemain()) + ((dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2) - (dto.getInnerSize() / 2 - dto.getInnerRemain() / 2) * (dto.getInnerSize() / 2 - dto.getInnerRemain() / 2)) * PI * (dto.getTotalHeightSize() - dto.getBigHeightSize())) * 7.856 / 1000000;
            } else {
                weightOfBasic = (((dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) * PI * (dto.getBigHeightSize() + dto.getHeightRemain()) + ((dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2)) * PI * (dto.getTotalHeightSize() - dto.getBigHeightSize()))) * 7.856 / 1000000;
            }
        } else { // --常规公式--
            // 重量 = PI*((外圆直径/2 +外圆余量/2)^2 – (内孔直径/2 - 内孔余量/2)^2)*(高度 + 高度余量)*7.856/1000000
            if ((dto.getInnerSize() - dto.getInnerRemain()) >= 80) {
                weightOfBasic = PI * ((dto.getOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getOuterSize() / 2 + dto.getOuterRemain() / 2) - (dto.getInnerSize() / 2 - dto.getInnerRemain() / 2) * (dto.getInnerSize() / 2 - dto.getInnerRemain() / 2)) * (dto.getHeightSize() + dto.getHeightRemain()) * 7.856 / 1000000;
            } else {
                weightOfBasic = PI * ((dto.getOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getOuterSize() / 2 + dto.getOuterRemain() / 2)) * (dto.getHeightSize() + dto.getHeightRemain()) * 7.856 / 1000000;
            }
        }

        BigDecimal b = new BigDecimal(weightOfBasic);
        weight = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        String singlePrice = this.baseMapper.selectSinglePrice(iteminfo.getSupCode(), dto.getMaterial()); //单价
        if (singlePrice == null) {
            return itemInfoDTO;
        }
        Double price = weight * Double.valueOf(singlePrice); // 价格 = 重量 * 单价
        PriceVO priceVO = new PriceVO();
        priceVO.setNewInnerRemain(String.valueOf(dto.getNewInnerRemain()));
        priceVO.setSingleWeight(weight.toString());
        priceVO.setSupName(iteminfo.getSupName());
        priceVO.setSupCode(iteminfo.getSupCode());
        priceVO.setSinglePrice(singlePrice);
        priceVO.setPrice(new BigDecimal(price.toString()).setScale(1, BigDecimal.ROUND_HALF_UP));
        priceVO.setHeightRemain(dto.getHeightRemain().toString());
        priceVO.setInnerRemain(dto.getInnerRemain().toString());
        priceVO.setOuterRemain(dto.getOuterRemain().toString());
        priceVOS.add(priceVO);
        itemInfoDTO.setSupAndPriceList(priceVOS);
        return itemInfoDTO;
    }

    /**
     * 锻件新逻辑  根据新余量
     *
     * @param itemInfoEntityBasOfDJ
     * @param itemInfoDTO
     * @param iteminfo
     * @param priceVOS
     * @param dto
     * @return
     */
    private ItemInfoDTO getPriceOfDJ(ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ, ItemInfoDTO itemInfoDTO, ItemInfoEntityResALLOfDJ iteminfo, List<PriceVO> priceVOS, U9DjPriceDTO dto) {

        // 计算价格
        Double weightOfBasic = 0D;
        Double weight = 0D;   //单重
        if (itemInfoEntityBasOfDJ.getType().equals("A")) { // --大小头公式--
            // 重量=(((大头外径/2+外径余量/2)^2 – (内径/2-内径余量/2)^2 )) * PI * (大头高度+高度余量)+((小头外径/2 + 外径余量/2)^2-(内径/2 – 内径余量/2)^2)*PI*(总高度-大头高度))*7.856/1000000
            if ((dto.getInnerSize() - dto.getInnerRemain()) >= 80) {
                weightOfBasic = (((dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) - (dto.getInnerSize() / 2 - dto.getNewInnerRemain() / 2) * (dto.getInnerSize() / 2 - dto.getNewInnerRemain() / 2)) * PI * (dto.getBigHeightSize() + dto.getHeightRemain()) + ((dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2) - (dto.getInnerSize() / 2 - dto.getNewInnerRemain() / 2) * (dto.getInnerSize() / 2 - dto.getNewInnerRemain() / 2)) * PI * (dto.getTotalHeightSize() - dto.getBigHeightSize())) * 7.856 / 1000000;
            } else {
                weightOfBasic = (((dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) * PI * (dto.getBigHeightSize() + dto.getHeightRemain()) + ((dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2)) * PI * (dto.getTotalHeightSize() - dto.getBigHeightSize()))) * 7.856 / 1000000;
            }
        } else { // --常规公式--
            // 重量 = PI*((外圆直径/2 +外圆余量/2)^2 – (内孔直径/2 - 内孔余量/2)^2)*(高度 + 高度余量)*7.856/1000000
            if ((dto.getInnerSize() - dto.getInnerRemain()) >= 80) {
                weightOfBasic = PI * ((dto.getOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getOuterSize() / 2 + dto.getOuterRemain() / 2) - (dto.getInnerSize() / 2 - dto.getNewInnerRemain() / 2) * (dto.getInnerSize() / 2 - dto.getNewInnerRemain() / 2)) * (dto.getHeightSize() + dto.getHeightRemain()) * 7.856 / 1000000;
            } else {
                weightOfBasic = PI * ((dto.getOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getOuterSize() / 2 + dto.getOuterRemain() / 2)) * (dto.getHeightSize() + dto.getHeightRemain()) * 7.856 / 1000000;
            }
        }

        BigDecimal b = new BigDecimal(weightOfBasic);
        weight = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        String singlePrice = this.baseMapper.selectSinglePrice(iteminfo.getSupCode(), dto.getMaterial()); //单价
        if (singlePrice == null) {
            return itemInfoDTO;
        }
        // 去新内径余量前两位
        BigDecimal inner = new BigDecimal(dto.getNewInnerRemain());
        dto.setNewInnerRemain(inner.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        Double price = weight * Double.valueOf(singlePrice); // 价格 = 重量 * 单价
        PriceVO priceVO = new PriceVO();
        priceVO.setNewInnerRemain(String.valueOf(dto.getNewInnerRemain()));
        priceVO.setSingleWeight(weight.toString());
        priceVO.setSupName(iteminfo.getSupName());
        priceVO.setSupCode(iteminfo.getSupCode());
        priceVO.setSinglePrice(singlePrice);
        priceVO.setPrice(new BigDecimal(price.toString()).setScale(1, BigDecimal.ROUND_HALF_UP));
        priceVO.setHeightRemain(dto.getHeightRemain().toString());
        priceVO.setInnerRemain(dto.getInnerRemain().toString());
        priceVO.setOuterRemain(dto.getOuterRemain().toString());
        priceVOS.add(priceVO);
        itemInfoDTO.setSupAndPriceList(priceVOS);
        return itemInfoDTO;
    }

    @Override
    public boolean setBasicItemInfoOfDJ(SubmitPriceReq submitPriceReq) {
        if (submitPriceReq.getItemInfoEntityBasOfDJList() != null) {
            // 锻件基础信息
            ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = submitPriceReq.getItemInfoEntityBasOfDJList().get(0);
            if (this.baseMapper.selectBasicItemInfoOfDJ(itemInfoEntityBasOfDJ.getItemCode()) == null) {
                this.baseMapper.insertBasicItemInfoOfDJ(itemInfoEntityBasOfDJ);
            } else {
                this.baseMapper.updateBasicItemInfoOfDJ(itemInfoEntityBasOfDJ);
                String account = getUser().getAccount();
                String time = DateUtil.format(new Date(), "yyyy-MM-dd");
                this.baseMapper.deleteDjInfoReport(itemInfoEntityBasOfDJ.getItemCode(), account, time); // 更新时、需要删除原有的最终信息表，待自动获取信息后，再插入新的。
            }
        }
        return true;
    }


    @Override
    public boolean setBasicItemInfoOfXLJ(SubmitPriceReq submitPriceReq) {
        if (submitPriceReq.getItemInfoEntityBasOfXLJList() != null) {
            // 小零件基础信息
            ItemInfoEntityBasOfXLJ itemInfoEntityBasOfXLJ = submitPriceReq.getItemInfoEntityBasOfXLJList().get(0);
            if (this.baseMapper.selectBasicItemInfoOfXLJ(itemInfoEntityBasOfXLJ.getItemCode()) == null) {
                this.baseMapper.insertBasicItemInfoOfXLJ(itemInfoEntityBasOfXLJ);
            } else {
                this.baseMapper.updateBasicItemInfoOfXLJ(itemInfoEntityBasOfXLJ);
                this.baseMapper.deleteXLJInfoReport(itemInfoEntityBasOfXLJ.getItemCode()); // 更新时、需要删除原有的最终信息表，待自动获取信息后，再插入新的。
            }
        }
        return true;
    }

    @Override
    public boolean setBasicItemInfoOfXLJRX(SubmitPriceReq submitPriceReq) {
        if (submitPriceReq.getItemInfoEntityBasOfXLJRXList() != null) {
            // 小零件RX基础信息
            ItemInfoEntityBasOfXLJRX itemInfoEntityBasOfXLJ = submitPriceReq.getItemInfoEntityBasOfXLJRXList().get(0);

            if (this.baseMapper.selectBasicItemInfoOfXLJRX(itemInfoEntityBasOfXLJ.getItemCode()) == null) {
                this.baseMapper.insertBasicItemInfoOfXLJRX(itemInfoEntityBasOfXLJ);
            } else {
                this.baseMapper.updateBasicItemInfoOfXLJRX(itemInfoEntityBasOfXLJ);
                this.baseMapper.deleteXLJInfoReport(itemInfoEntityBasOfXLJ.getItemCode()); // 更新时、需要删除原有的最终信息表，待自动获取信息后，再插入新的。
            }
        }
        return true;
    }

    @Override
    public boolean setBasicItemInfoOfDZ(SubmitPriceReq submitPriceReq) {
        if (submitPriceReq.getItemInfoEntityBasOfDZList() != null) {
            // 底轴基础信息
            /*ItemInfoEntityBasOfXLJRX itemInfoEntityBasOfXLJ = submitPriceReq.getItemInfoEntityBasOfXLJRXList().get(0);

            if (this.baseMapper.selectBasicItemInfoOfXLJRX(itemInfoEntityBasOfXLJ.getItemCode()) == null) {
                this.baseMapper.insertBasicItemInfoOfXLJRX(itemInfoEntityBasOfXLJ);
            } else {
                this.baseMapper.updateBasicItemInfoOfXLJRX(itemInfoEntityBasOfXLJ);
                this.baseMapper.deleteXLJInfoReport(itemInfoEntityBasOfXLJ.getItemCode()); // 更新时、需要删除原有的最终信息表，待自动获取信息后，再插入新的。
            }*/

            ItemInfoEntityBasOfDZ itemInfoEntityBasOfDZ=submitPriceReq.getItemInfoEntityBasOfDZList().get(0);
            if (this.baseMapper.selectBasicItemInfoOfDZ(itemInfoEntityBasOfDZ.getItemCode()) == null) {
                this.baseMapper.insertBasicItemInfoOfDZ(itemInfoEntityBasOfDZ);
            }else {
                this.baseMapper.updateBasicItemInfoOfDZ(itemInfoEntityBasOfDZ);
            }

        }
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addBasicItemInfoOfDJ(SubmitPriceReq submitPriceReq) {
        if (submitPriceReq.getItemInfoEntityBasOfDJList() != null) {
            // 锻件基础信息
            ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = submitPriceReq.getItemInfoEntityBasOfDJList().get(0);
            if (itemInfoEntityBasOfDJ.getItemCode() == null || itemInfoEntityBasOfDJ.getItemCode().isEmpty()) {
                String itemCode = this.baseMapper.getItemInfoByItemName(itemInfoEntityBasOfDJ.getItemName());
                if (itemCode == null) {
                    throw new RuntimeException("根据该物料的 物料描述，找不到对应的 物料号，请确认。");
                }
                itemInfoEntityBasOfDJ.setItemCode(itemCode);
            }
            if (itemInfoEntityBasOfDJ.getItemName() == null || itemInfoEntityBasOfDJ.getItemName().isEmpty()) {
                String itemName = this.baseMapper.getItemInfoByItemCode(itemInfoEntityBasOfDJ.getItemCode());
                if (itemName == null) {
                    throw new RuntimeException("根据该物料的 物料号，找不到对应的 物料描述，请确认。");
                }
                itemInfoEntityBasOfDJ.setItemName(itemName);
            }
            this.baseMapper.deleteOldBasicDJInfo(itemInfoEntityBasOfDJ.getItemCode());
            this.baseMapper.insertBasicItemInfoOfDJ(itemInfoEntityBasOfDJ);
            this.getItemInfoOfDJVO(itemInfoEntityBasOfDJ.getItemCode(), itemInfoEntityBasOfDJ.getItemName());
            Integer count = this.baseMapper.itemCodeOfDJIsExisted(itemInfoEntityBasOfDJ.getItemCode());
            if (count == 0) { // 最终信息表中没有该数据
                throw new RuntimeException("该物料号的基础信息 经过程序计算,不能算出最后结果，请确认。");
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addBasicItemInfoOfXLJ(SubmitPriceReq submitPriceReq) {
        String notSplitTypes = "下盖,填料函,连接盘,圆筒,套筒,填料压板,凸板";

        if (submitPriceReq.getItemInfoEntityBasOfXLJList() != null) {
            // 锻件基础信息
            ItemInfoEntityBasOfXLJ itemInfoEntityBasOfXLJ = submitPriceReq.getItemInfoEntityBasOfXLJList().get(0);
            if (itemInfoEntityBasOfXLJ.getItemCode() == null || itemInfoEntityBasOfXLJ.getItemCode().isEmpty()) {
                String itemCode = this.baseMapper.getItemInfoByItemName(itemInfoEntityBasOfXLJ.getItemName());
                if (itemCode == null) {
                    throw new RuntimeException("根据该物料的 物料描述，找不到对应的 物料号，请确认。");
                }
                itemInfoEntityBasOfXLJ.setItemCode(itemCode);
            }
            if (itemInfoEntityBasOfXLJ.getItemName() == null || itemInfoEntityBasOfXLJ.getItemName().isEmpty()) {
                String itemName = this.baseMapper.getItemInfoByItemCode(itemInfoEntityBasOfXLJ.getItemCode());
                if (itemName == null) {
                    throw new RuntimeException("根据该物料的 物料号，找不到对应的 物料描述，请确认。");
                }
                itemInfoEntityBasOfXLJ.setItemName(itemName);
            }

            if (notSplitTypes.indexOf(itemInfoEntityBasOfXLJ.getItemName().split("-")[0]) < 0) {
                throw new RuntimeException(itemInfoEntityBasOfXLJ.getItemCode() + " - " + itemInfoEntityBasOfXLJ.getItemName() + " : 该物料可以拆出内外径、不能直接输入");
            }


            this.baseMapper.deleteOldBasicXLJInfo(itemInfoEntityBasOfXLJ.getItemCode());
            this.baseMapper.insertBasicItemInfoOfXLJ(itemInfoEntityBasOfXLJ);
            this.getItemInfoOfXLJVO(itemInfoEntityBasOfXLJ.getItemCode(), itemInfoEntityBasOfXLJ.getItemName());

            Integer count = this.baseMapper.itemCodeOfXLJIsExisted(itemInfoEntityBasOfXLJ.getItemCode());
            if (count == 0) { // 最终信息表中没有该数据
                throw new RuntimeException("该物料号的基础信息 经过程序计算,不能算出最后结果，请确认。");
            }
        }
        return true;
    }

    @Override
    public void exportAllItemInfo(SubmitPriceReq submitPriceReq, HttpServletResponse response) {

        List<ItemInfoEntityDJReport> itemInfoEntityDJReportList = this.baseMapper.selectDjReportList(submitPriceReq);

        if (itemInfoEntityDJReportList.size() > 0) {
            itemInfoEntityDJReportList.stream().forEach(item -> {
                if (item.getType().equals("A")) {
                    item.setType("大小头公式");
                    item.setOuterSize("");
                    item.setHeightSize("");
                } else {
                    item.setType("常规公式");
                    item.setBigOuterSize("");
                    item.setBigHeightSize("");
                    item.setSmallOuterSize("");
                    item.setTotalHeightSize("");
                }

                if (item.getRes().equals("fm")) {
                    item.setRes("阀帽");
                } else if (item.getRes().equals("zyd")) {
                    item.setRes("自由锻");
                } else {
                    item.setRes("中法兰");
                }
            });
        }
        ExcelUtils.defaultExport(itemInfoEntityDJReportList, ItemInfoEntityDJReport.class, "物料获取信息表" + DateUtil.formatDate(new Date()), response);
    }


    @Override
    public void exportAllItemInfoXLJ(SubmitPriceReq submitPriceReq, HttpServletResponse response) {

        List<ItemInfoEntityOfXLJ> itemInfoEntityXLJReportList = this.baseMapper.selectXLJReportList(submitPriceReq);

        if (itemInfoEntityXLJReportList.get(0) != null) {
            for (ItemInfoEntityOfXLJ itemInfoEntityOfXLJ : itemInfoEntityXLJReportList) {
                PoItemEntity poItemEntity = poItemService.getLastPoInfos(itemInfoEntityOfXLJ.getItemCode(), itemInfoEntityOfXLJ.getItemName());
                if (poItemEntity != null) {
                    itemInfoEntityOfXLJ.setLastPrice(poItemEntity.getPrice().toString());
                }
            }
        } else {
            throw new RuntimeException("找不到任何数据");
        }
        ExcelUtils.defaultExport(itemInfoEntityXLJReportList, ItemInfoEntityOfXLJ.class, "小零件获取信息表" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public void exportAllItemInfoLZQ(SubmitPriceReq submitPriceReq, HttpServletResponse response) {

        List<ItemInfoEntityOfLZQ> itemInfoEntityXLJReportList = this.baseMapper.selectLZQReportList(submitPriceReq);

        if (itemInfoEntityXLJReportList.get(0) == null) {
            throw new RuntimeException("找不到任何数据");
        }
        ExcelUtils.defaultExport(itemInfoEntityXLJReportList, ItemInfoEntityOfLZQ.class, "联轴器获取信息表" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public void exportAllItemInfoQZNew(SubmitPriceReq submitPriceReq, HttpServletResponse response) {
        List<ItemInfoEntityOfQZNew> itemInfoEntityOfQZNews = this.baseMapper.selectQZReportList(submitPriceReq);

        if (itemInfoEntityOfQZNews.get(0) == null) {
            throw new RuntimeException("找不到任何数据");
        }
        ExcelUtils.defaultExport(itemInfoEntityOfQZNews, ItemInfoEntityOfQZNew.class, "球座获取信息表" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public void exportAllItemInfoDZ(SubmitPriceReq submitPriceReq, HttpServletResponse response) {

        List<ItemInfoEntityOfDZ> itemInfoEntityDZReportList = this.baseMapper.selectDZReportList(submitPriceReq);

        if (itemInfoEntityDZReportList.get(0) != null) {
        } else {
            throw new RuntimeException("找不到任何数据");
        }

        ExcelUtils.defaultExport(itemInfoEntityDZReportList, ItemInfoEntityOfDZ.class, "底轴获取信息表" + DateUtil.formatDate(new Date()), response);
    }


    @Override
    public void exportAllItemInfoFL(SubmitPriceReq submitPriceReq, HttpServletResponse response) {

        List<ItemInfoEntityOfFL> itemInfoEntityFLReportList = this.baseMapper.selectFLReportList(submitPriceReq);

        if (itemInfoEntityFLReportList.get(0) != null) {
        } else {
            throw new RuntimeException("找不到任何数据");
        }

        ExcelUtils.defaultExport(itemInfoEntityFLReportList, ItemInfoEntityOfFL.class, "法兰获取信息表" + DateUtil.formatDate(new Date()), response);
    }


    /**
     * 自动下单报表 - 锻件
     *
     * @param
     * @return
     */
    @Override
    public IPage<AutoOrderOfDJ> autoOrderOfDJ(IPage<AutoOrderOfDJ> page, SubmitPriceReq submitPriceReq) {
        IPage<AutoOrderOfDJ> autoOrderOfDJIPage = this.baseMapper.getAutoOrderOfDJ(page, submitPriceReq);
        return autoOrderOfDJIPage;
    }


    @Override
    public void exportAutoOrderOfDJ(SubmitPriceReq submitPriceReq, HttpServletResponse response) {

        List<AutoOrderOfDJ> autoOrderOfDJS = this.baseMapper.selectAutoOrderOfDJList(submitPriceReq);

        if (autoOrderOfDJS.size() > 0) {
            autoOrderOfDJS.stream().forEach(item -> {
                if (item.getType().equals("A")) {
                    item.setType("大小头公式");
                    item.setOuterSize("");
                    item.setHeightSize("");
                } else {
                    item.setType("常规公式");
                    item.setBigOuterSize("");
                    item.setBigHeightSize("");
                    item.setSmallOuterSize("");
                    item.setTotalHeightSize("");
                }

                if (item.getRes().equals("fm")) {
                    item.setRes("阀帽");
                } else if (item.getRes().equals("zyd")) {
                    item.setRes("自由锻");
                } else {
                    item.setRes("中法兰");
                }
            });
        }
        ExcelUtils.defaultExport(autoOrderOfDJS, AutoOrderOfDJ.class, "锻件自动下单报表" + DateUtil.formatDate(new Date()), response);
    }


    @Override
    public IPage<AutoOrderOfXLJ> autoOrderOfXLJ(IPage<AutoOrderOfXLJ> page, AutoOrderOfXLJ autoOrderOfXLJ) {
        IPage<AutoOrderOfXLJ> autoOrderOfXLJIPage = this.baseMapper.getAutoOrderOfXLJ(page, autoOrderOfXLJ);
        return autoOrderOfXLJIPage;
    }


    @Override
    public void exportAutoOrderOfXLJ(AutoOrderOfXLJ autoOrderOfXLJ, HttpServletResponse response) {
        List<AutoOrderOfXLJ> autoOrderOfXLJS = this.baseMapper.selectAutoOrderOfXLJList(autoOrderOfXLJ);
        ExcelUtils.defaultExport(autoOrderOfXLJS, AutoOrderOfXLJ.class, "小零件自动下单报表" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public String handleExceptDataWithNoProject(List<U9PrFromPhpDTO> u9list) {

        for (U9PrFromPhpDTO u9fromphp:u9list) {
            //判断是否今天的PR
            Long prDate = Long.valueOf(u9fromphp.getPRDate());
            Date date = cn.hutool.core.date.DateUtil.date(prDate*1000);
            if(!cn.hutool.core.date.DateUtil.isSameDay(new Date(),date)){
                continue;
            }

            //17 20 不卡控
            if(u9fromphp.getItemCode().indexOf("17")==0||u9fromphp.getItemCode().indexOf("20")==0){
                continue;
            }

            //判断是否存在项目
            int projectnum = this.baseMapper.SelectIsProject(u9fromphp.getPRNO()+"-"+u9fromphp.getPRLineNo());
            if(projectnum==0){
                //要把这笔数据先存下来 php实现
                /*U9PrFromPhpDTO u9PrFromPhpDTO = this.baseMapper.selectPrNoProject(u9fromphp);
                if (u9PrFromPhpDTO == null) {
                    this.baseMapper.insertPrNoProject(u9fromphp);
                }*/
                return "无项目";
            }

        }
        return "有项目";
    }


    public static void main(String[] args) throws ParseException {
        int i = WillDateUtil.daysBetween(new Date(),  DateUtil.parse("2022-08-02", "yyyy-MM-dd"));
        System.out.println(i);
    }


    @Override
    public List<U9PrFromPhpDTO> handleExceptData(List<U9PrFromPhpDTO> u9list) {
        log.info("======================开始进行PR异常过滤,执行{}条数据====================================",u9list.size());

        List<U9PrFromPhpDTO> repU9List = new ArrayList<>();



        long begin = System.currentTimeMillis();

        for (U9PrFromPhpDTO u9fromphp : u9list) {

            if(u9fromphp.getItemCode().indexOf("17")==0||u9fromphp.getItemCode().indexOf("20")==0){
                continue;
            }
            Date prspdate= this.baseMapper.selectPrdata(u9fromphp.getPRNO());
            if (prspdate != null) {
                u9fromphp.setPrspDate(prspdate);
            }else{
                continue;
            }

            //标准交期
            Item item = itemService.getByCode(u9fromphp.getItemCode());

            int sd = 0;
            if (item != null) {
                u9fromphp.setAbc_type(item.getSOrN());
                sd = item.getPurchAfterDate() + item.getPurchBeforeDate() + item.getPurchDisposeDate();//标准交期
            }else {
                continue;
            }

            u9fromphp.setBzReqDate(Long.valueOf(sd));
            //获取 项目需求交期  项目计划交期
            PrFromOracleDTO prFromOracleDTO = this.baseMapper.SelectSrmRpt(u9fromphp.getPRNO() + "-" + Integer.toString(u9fromphp.getPRLineNo()));
            if (prFromOracleDTO != null) {
                u9fromphp.setPlan_date(prFromOracleDTO.getPlandate());
            }


            //异常问题描述
            StringBuilder sb = new StringBuilder();

            //begin
            Long itemid = this.baseMapper.selectItemIdByCode(u9fromphp.getItemCode());
            if (itemid == null) {
               continue;
            }
            int ReplaceNum = this.baseMapper.SelectIsReplace(itemid);

            //判断bom是否为代替
            if (ReplaceNum > 0) {
                sb.append("bom为代替,");
                //查明根因，并给出解决方案
                u9fromphp.setDuty_dept(DutyDeptEnum.技术.getName());
                u9fromphp.setHandle_dept(DutyDeptEnum.计划.getName());
                u9fromphp.setProblemDesc(sb.toString());
                interceptPR(u9fromphp, repU9List);


            } else {
                //sb.append("bom不为代替->");
                int projectnum = this.baseMapper.SelectIsProject(u9fromphp.getPRNO()+"-"+u9fromphp.getPRLineNo());
                //确认齐套是否匹配项目
                if (projectnum > 0) {
                    //sb.append("确认齐套匹配项目->");

                    long diffdays = 0;
                    if (prFromOracleDTO != null) {
                        Date newdate = new Date();
                        //diffdays = (prFromOracleDTO.getNeedtime().getTime() - u9fromphp.getPrspDate().getTime()) / 24 / 60 / 60 / 1000;//需求日期-今天
                        try {
                            diffdays = WillDateUtil.daysBetween(u9fromphp.getPrspDate(),prFromOracleDTO.getNeedtime());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    //确认标准交期是否满足项目需求交期 项目需求日期-今天 >标准交期
                    if (diffdays >= Long.valueOf(sd)) {
                        //sb.append("确认标准交期满足项目需求交期1->");
                        //自动推送到SRM
                        SendPR(u9fromphp);

                    } else {
                        //sb.append("确认标准交期不满足项目需求交期1->");
                        //反查需求来源，项目、计划交期显示在报表中
                        PrFromOracleDTO prfromoracle = this.baseMapper.SelectQTDataByPrLn(u9fromphp.getPRNO()+"-"+u9fromphp.getPRLineNo());
                        String jhjq=prfromoracle.getXmjq()==null?"无":WillDateUtil.dateFormat(prfromoracle.getXmjq());
                        String pd = "紧急采购,项目号:" + prfromoracle.getZxmh() + " 计划交期：" + jhjq +",计划查原因";
                        u9fromphp.setProblemDesc2(pd);
                        //查明根因，并给出解决方案
                        u9fromphp.setProblemDesc(sb.toString());
                        u9fromphp.setDuty_dept(DutyDeptEnum.计划.getName());
                        u9fromphp.setHandle_dept(DutyDeptEnum.计划.getName());
                        interceptPR(u9fromphp, repU9List);


                    }

                } else {
                    //sb.append("确认齐套没有匹配项目->");
                    //确认是否补安全库存
                    Double SafeStockNum = this.baseMapper.SelectIsSafeStock(itemid);
                    SafeStockNum = SafeStockNum == null ? 0 : SafeStockNum;
                    System.out.println("u9fromphp.getItemCode()"+itemid);
                    Double StockdiffNum = this.baseMapper.selectSafeStockDiff(itemid);
                    StockdiffNum = StockdiffNum == null ? 0 : StockdiffNum;
                    if (StockdiffNum > 0 && SafeStockNum > 0) {

                        sb.append("补安全库,");

                        Long reqdate= u9fromphp.getRequireDate()*1000;
                        Date xqjq = new Date(reqdate);
                        long diffdays = 0;
                        if (xqjq != null) {
                            Date newdate = new Date();
                            //diffdays = (xqjq.getTime() - u9fromphp.getPrspDate().getTime()) / 24 / 60 / 60 / 1000;//需求日期-今天
                            try {
                                diffdays = WillDateUtil.daysBetween(u9fromphp.getPrspDate(),xqjq);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                        }
                        //确认标准交期是否满足请购单需求交期 需求日期-今天 >标准交期
                        if (diffdays >= Long.valueOf(sd)) {
                            //sb.append("确认标准交期满足请购单需求交期2->");
                            sb.append("标准交期满足需求交期,");
                            //自动推送到SRM
                            SendPR(u9fromphp);

                        } else {
                            //sb.append("确认标准交期不满足请购单需求交期2->");
                            sb.append("标准交期不满足需求交期,");
                            //确认采购处理提前期是否为零
                            if (item.getPurchDisposeDate() == 0) {
                                sb.append("无标准交期，采购设置标准交期,");
                                //不推到SRM
                                //采部核查标准交期处理规则交补齐数据
                                //手动推送到SRM
                                u9fromphp.setProblemDesc(sb.toString());
                                u9fromphp.setDuty_dept(DutyDeptEnum.采购.getName());
                                u9fromphp.setHandle_dept(DutyDeptEnum.采购.getName());
                                interceptPR(u9fromphp, repU9List);

                            } else {
                                sb.append("有标准交期,IT核查原因,");
                                //查明根因，并给出解决方案
                                u9fromphp.setProblemDesc(sb.toString());
                                u9fromphp.setDuty_dept(DutyDeptEnum.快反中心.getName());
                                u9fromphp.setHandle_dept(DutyDeptEnum.计划.getName());
                                interceptPR(u9fromphp, repU9List);

                            }
                        }


                    } else {
                        //sb.append("确认不是补安全库存->");
                        //不推送到SRM

                        //反查需求来源工单，并将反查到工单、项目、计划交期、13料号显示在报表中
                        StringBuilder problemdesc = new StringBuilder();
                        List<PrFromOracleDTO> fcdata = new ArrayList<>();//循环工单反查到的数据
                        List<PrFromOracleDTO> dstreelist = this.baseMapper.selectDSTreeByItemId(itemid);
                        if (dstreelist.size() > 0) {
                            //循环工单
                            for (PrFromOracleDTO gd : dstreelist) {
                                String parentdsinfo = gd.getParentdsinfo();
                                while (StringUtils.isNotBlank(parentdsinfo) && !"0".equals(parentdsinfo)) {
                                    PrFromOracleDTO dsinfo = this.baseMapper.selectDSInfo(parentdsinfo);//找到dsinfo
                                    if (dsinfo != null && StringUtils.isNotBlank(dsinfo.getProjectcode())) {//判断有没有项目号
                                        List<PrFromOracleDTO> qtdatalist = this.baseMapper.SelectQTDataByProject(dsinfo.getProjectcode(), Long.valueOf(dsinfo.getCode()));
                                        if (qtdatalist.size() > 0) {//通过项目号和物料编号反查齐套数据

                                            String zxmh = qtdatalist.get(0).getZxmh();
                                            Date xmjq = qtdatalist.get(0).getXmjq();
                                            String wlbh = dsinfo.getCode();
                                            String gdcode = gd.getCode();

                                            PrFromOracleDTO fctemp = new PrFromOracleDTO();
                                            fctemp.setZxmh(zxmh);
                                            fctemp.setXmjq(xmjq);
                                            fctemp.setWlbh(wlbh);
                                            fctemp.setGd(gdcode);
                                            fctemp.setGylx(qtdatalist.get(0).getGylx());
                                            fcdata.add(fctemp);
                                        }else {
                                            //如果没有项目号和物料编号对应的齐套数据
                                            List<PrFromOracleDTO> prFromOracleDTOList = this.baseMapper.SelectQTDataByProjectWGY(dsinfo.getProjectcode());
                                            if(prFromOracleDTOList.size()>0){
                                                String zxmh = prFromOracleDTOList.get(0).getZxmh();
                                                Date xmjq = prFromOracleDTOList.get(0).getXmjq();
                                                String wlbh = dsinfo.getCode();
                                                String gdcode = gd.getCode();

                                                PrFromOracleDTO fctemp = new PrFromOracleDTO();
                                                fctemp.setZxmh(zxmh);
                                                fctemp.setXmjq(xmjq);
                                                fctemp.setWlbh(wlbh);
                                                fctemp.setGd(gdcode);
                                                fctemp.setGylx(prFromOracleDTOList.get(0).getGylx());
                                                fcdata.add(fctemp);
                                            }

                                        }
                                        break;

                                    } else {
                                        //如果不是13料号继续循环
                                        PrFromOracleDTO parentdstree = this.baseMapper.selectDSTreeByDSinfo(parentdsinfo);
                                        if (parentdstree != null && StringUtils.isNotBlank(parentdstree.getParentdsinfo()) && !"0".equals(parentdstree.getParentdsinfo())) {
                                            parentdsinfo = parentdstree.getParentdsinfo();
                                        } else {
                                            break;
                                        }

                                    }

                                }

                            }
                            //找出pr时间>bom时间  并且  pr时间和bom时间最近的一个工单
                            Long prdate = Long.valueOf(u9fromphp.getPRDate())*1000;
                            PrFromOracleDTO fgAfterCompare = null;
                            for (PrFromOracleDTO fc : fcdata) {
                                Date fcbomdate = this.baseMapper.selectBomDateByProject(fc.getZxmh());
                                fc.setBomdate(fcbomdate);
                                Long time = fcbomdate.getTime() - ((fcbomdate.getTime() + 28800000) % (86400000));
                                if (prdate >= time) {
                                    if (fgAfterCompare==null) {
                                        fgAfterCompare = fc;
                                    } else if (fc.getBomdate().getTime() > fgAfterCompare.getBomdate().getTime()) {
                                        fgAfterCompare = fc;
                                    }

                                }
                            }
                            //如果有反查出来的项目数据
                            if(fgAfterCompare!=null){
                                //sb.append("确认反查母阶物料有项目需求->");
                                String jhjq=fgAfterCompare.getXmjq()==null?"无":WillDateUtil.dateFormat(fgAfterCompare.getXmjq());
                                problemdesc.append("工单：" + fgAfterCompare.getGd()+" 项目：" + fgAfterCompare.getZxmh() + " 计划交期：" + jhjq+ " 母阶料号：" + fgAfterCompare.getWlbh()  + ";");
                                u9fromphp.setProblemDesc2(problemdesc.toString());
                                if ("采购".equals(fgAfterCompare.getGylx())) {
                                    sb.append("齐套供应类型为" + fgAfterCompare.getGylx() + ",");
                                    //采购
                                    //查明根因，并给出解决方案
                                    u9fromphp.setProblemDesc(sb.toString());
                                    u9fromphp.setDuty_dept(DutyDeptEnum.快反中心.getName());
                                    u9fromphp.setHandle_dept(DutyDeptEnum.计划.getName());
                                    interceptPR(u9fromphp, repU9List);


                                } else if ("无供应".equals(fgAfterCompare.getGylx())) {
                                    sb.append("齐套供应类型为" + fgAfterCompare.getGylx() + ",");
                                    //无供应
                                    //查明根因，并给出解决方案
                                    u9fromphp.setProblemDesc(sb.toString());
                                    u9fromphp.setDuty_dept(DutyDeptEnum.精益.getName());
                                    u9fromphp.setHandle_dept(DutyDeptEnum.计划.getName());
                                    interceptPR(u9fromphp, repU9List);
                                } else {
                                    sb.append("齐套供应类型为" + fgAfterCompare.getGylx() + ",");
                                    //查明根因，并给出解决方案
                                    u9fromphp.setProblemDesc(sb.toString());
                                    u9fromphp.setDuty_dept(DutyDeptEnum.计划.getName());
                                    u9fromphp.setHandle_dept(DutyDeptEnum.计划.getName());
                                    interceptPR(u9fromphp, repU9List);

                                }

                            }else{
                                //sb.append("确认反查母阶物料没有项目需求->");
                                //查明根因，并给出解决方案
                                sb.append("无项目需求，计划查原因,");
                                u9fromphp.setProblemDesc(sb.toString());
                                u9fromphp.setDuty_dept(DutyDeptEnum.计划.getName());
                                u9fromphp.setHandle_dept(DutyDeptEnum.计划.getName());
                                interceptPR(u9fromphp, repU9List);

                            }



                        } else {
                            sb.append("无项目需求，计划查原因,");
                            //查明根因，并给出解决方案
                            u9fromphp.setProblemDesc(sb.toString());
                            u9fromphp.setDuty_dept(DutyDeptEnum.计划.getName());
                            u9fromphp.setHandle_dept(DutyDeptEnum.计划.getName());
                            interceptPR(u9fromphp, repU9List);
                        }
                        u9fromphp.setProblemDesc2(problemdesc.toString());


                    }

                }

            }


        }

        long end = System.currentTimeMillis();
        log.info("======================执行{}条数据==============================耗时{}}ms",u9list.size(),(end - begin));
        log.info("======================异常{}条数据==============================",repU9List.size());
        log.info(repU9List.toString());
        log.info("======================异常{}条数据==============================",repU9List.size());

        return repU9List;
    }

    @Override
    public List<U9PrFromPhpDTO> handleExceptData2(List<U9PrFromPhpDTO> u9list) {
        long begin = System.currentTimeMillis();
        log.info("======================开始进行PR异常过滤,执行{}条数据====================================",u9list.size());

        List<U9PrFromPhpDTO> repU9List = new ArrayList<>();

        for (U9PrFromPhpDTO u9fromphp : u9list){

            u9fromphp.setPrspDate(u9fromphp.getPrspDate());

            if(u9fromphp.getItemCode().indexOf("17")==0||u9fromphp.getItemCode().indexOf("20")==0){
                continue;
            }

            int projectnum = this.baseMapper.SelectIsProject(u9fromphp.getPRNO()+"-"+u9fromphp.getPRLineNo());

            if (projectnum > 0) {

            }else{

                //判断安全库存
                Item byCode = itemService.getByCode(u9fromphp.getItemCode());
                if(byCode.getStockLowerLimit().compareTo(new BigDecimal("0"))>0){
                    //安全库存

                }else {
                    //异常
                    u9fromphp.setProblemDesc("异常PR");
                    interceptPR(u9fromphp, repU9List);

                }


            }





        }

        long end = System.currentTimeMillis();
        log.info("======================执行{}条数据==============================耗时{}}ms",u9list.size(),(end - begin));
        log.info("======================异常{}条数据==============================",repU9List.size());
        log.info(repU9List.toString());
        log.info("======================异常{}条数据==============================",repU9List.size());

        return repU9List;


    }

    /**
     * 不推送SRM
     * <p>
     * 2.补充异常数据
     * 3.数据存到异常表
     * 1.atw_u9_pr表的isdelete字段置1
     */
    @Transactional
    public void interceptPR(U9PrFromPhpDTO u9fromphp, List<U9PrFromPhpDTO> repU9List) {

        repU9List.add(u9fromphp);

        U9PrEntity u9PrEntity = this.baseMapper.getPrLn(u9fromphp.getPRNO(), u9fromphp.getPRLineNo());
        U9PrEntityEx u9PrEntityEx = null;
        if (u9PrEntity != null) {
            //2.补充报表数据
            u9PrEntityEx = new U9PrEntityEx();
            BeanUtil.copy(u9PrEntity, u9PrEntityEx);
            u9PrEntityEx.setCreateTime(new Date());
            u9PrEntityEx.setIsDeleted(0);
            u9PrEntityEx.setTransferPoNum(0);
            u9PrEntityEx.setProblemDesc(u9fromphp.getProblemDesc());
            u9PrEntityEx.setHandleDept(u9fromphp.getHandle_dept());
            u9PrEntityEx.setDutyDept(u9fromphp.getDuty_dept());
            u9PrEntityEx.setAbcType("");
            u9PrEntityEx.setProblemType("");
            String chooseline = u9fromphp.getProblemDesc();
            String prodesc = u9fromphp.getProblemDesc2() == null ? chooseline : chooseline + " " + u9fromphp.getProblemDesc2();
            u9PrEntityEx.setProblemDesc(prodesc);
            u9PrEntityEx.setProblemAnal("");//手填
            u9PrEntityEx.setSolution("");//手填
            //u9PrEntityEx.setFinishDate(new Date());//手填
            u9PrEntityEx.setIsAps(u9fromphp.getIsAps());
            u9PrEntityEx.setPrCreateUser(u9fromphp.getCreateUser());
            u9PrEntityEx.setBzReqDate(u9fromphp.getBzReqDate());
            u9PrEntityEx.setAbcType(u9fromphp.getAbc_type());
            u9PrEntityEx.setPrspDate(u9fromphp.getPrspDate());


            PrFromOracleDTO prfromoracledto = this.baseMapper.SelectQTDataByPrLn(u9fromphp.getPRNO()+"-"+u9fromphp.getPRLineNo());
            if (prfromoracledto!=null) {
                u9PrEntityEx.setSubproject(prfromoracledto.getZxmh());
                u9PrEntityEx.setPlanDate(prfromoracledto.getXmjq());
                u9PrEntityEx.setProNeedDate(prfromoracledto.getXqsj());
            }
        } else {
            return;
        }

        U9PrExReq prReq=new U9PrExReq();
        prReq.setPrCode(u9fromphp.getPRNO());
        prReq.setPrLn(u9fromphp.getPRLineNo());
        List<U9PrEntityEx>  u9PrEntityEx2 = u9PrEntityExMapper.selectListByReq(prReq);
        if (u9PrEntityEx2.size()>0) {
            /*u9PrEntityEx.setIsDeleted(u9PrEntityEx2.get(0).getIsDeleted());
            u9PrEntityEx.setCreateTime(u9PrEntityEx2.get(0).getCreateTime());
            u9PrEntityEx.setUpdateTime(new Date());
            u9PrEntityExMapper.updateById(u9PrEntityEx);*/

        }else{
            //3.数据存到异常表
            u9PrEntityExMapper.insert(u9PrEntityEx);
            //1.atw_u9_pr表的isdelete字段置1
            //this.baseMapper.deletedPrByPrLn(u9fromphp.getPRNO(), u9fromphp.getPRLineNo());
        }




    }

    @Override
    @Transactional
    public void SendPR(U9PrFromPhpDTO u9fromphp) {

        /*//释放PR
        this.u9PrEntityExMapper.freePrByPrLn(u9fromphp.getPRNO(), u9fromphp.getPRLineNo());

        //锁住ExPR
        this.u9PrEntityExMapper.deletedExPrByPrLn(u9fromphp.getPRNO(), u9fromphp.getPRLineNo(),"自动释放"+WillDateUtil.dateFormat(new Date()));*/


    }


    /**
     * 释放在atw_u9_pr_no_project表的无项目PR
     */
    @Override
    public void releaseNoProPR() {

        List<U9PrEntityNoPro> u9PrEntityNoPros = u9PrEntityNoProMapper.selectListByReq();
        //查到的数据全部释放掉
        for (U9PrEntityNoPro item:u9PrEntityNoPros) {
            //释放成功需要重新同步接口
            String u9Data = item.getU9Data();
            JSONArray jsonArray = JSONObject.parseArray(u9Data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            jsonObject.put("repush","Y");
            String sendermsg=JSON.toJSONString(jsonArray);
            String post = HttpUtil.post(bizapiUrl+"/OpenApi/insertPR", sendermsg);
            JSONObject rep = JSONObject.parseObject(post);
            String code = rep.getString("code");
            if ("2000".equals(code)) {
                        //接口同步成功之后，释放掉表中中的PR
                        u9PrEntityNoProMapper.releaseNoProPRByPrLn(item.getPrCode(), item.getPrLn());
                        log.info("释放在atw_u9_pr_no_project表的无项目PR:"+item.getPrCode()+"-"+item.getPrLn());
            }
        }
    }


    /**
     * APS执行过后再次执行，先检查有没有项目，有项目就直接释放
     */
    @Override
    public void releaseNoProPR2() {

        List<U9PrEntityNoPro> u9PrEntityNoPros = u9PrEntityNoProMapper.selectListByReq();
        //遍历所以拦截的数据
        for (U9PrEntityNoPro item:u9PrEntityNoPros) {

            int projectnum = this.baseMapper.SelectIsProject(item.getPrCode()+"-"+item.getPrLn());
            if(projectnum==0){
                continue;
            }else{
                String u9Data = item.getU9Data();
                JSONArray jsonArray = JSONObject.parseArray(u9Data);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                jsonObject.put("repush","Y");
                String sendermsg=JSON.toJSONString(jsonArray);
                String post = HttpUtil.post(bizapiUrl+"/OpenApi/insertPR", sendermsg);
                JSONObject rep = JSONObject.parseObject(post);
                String code = rep.getString("code");
                if ("2000".equals(code)) {
                    //接口同步成功之后，这些都是有项目的PR，所以要直接删除
                    u9PrEntityNoProMapper.deleteNoProPRByPrLn(item.getPrCode(), item.getPrLn());
                    log.info("删除在atw_u9_pr_no_project表的无项目PR:"+item.getPrCode()+"-"+item.getPrLn());
                }

            }

        }
    }


    /**
     * 释放在atw_u9_pr_no_project表的无项目PR
     */
    @Override
    public Boolean releaseNoProPRByPrLn( List<U9PrEntityNoPro> u9PrEntityNoPros) {

        //查到的数据全部释放掉
        for (U9PrEntityNoPro item:u9PrEntityNoPros) {
            U9PrEntityNoPro u9PrEntityNoPro = u9PrEntityNoProMapper.selectListByPrLn(item.getPrCode(),item.getPrLn());
            //释放成功需要重新同步接口
            String u9Data = u9PrEntityNoPro.getU9Data();
            JSONArray jsonArray = JSONObject.parseArray(u9Data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            jsonObject.put("repush","Y");
            String sendermsg=JSON.toJSONString(jsonArray);
            String post = HttpUtil.post(bizapiUrl+"/OpenApi/insertPR", sendermsg);
            JSONObject rep = JSONObject.parseObject(post);
            String code = rep.getString("code");
            if ("2000".equals(code)) {
                //接口同步成功之后，释放掉表中中的PR
                u9PrEntityNoProMapper.releaseNoProPRByPrLn(u9PrEntityNoPro.getPrCode(), u9PrEntityNoPro.getPrLn());
                log.info("释放在atw_u9_pr_no_project表的无项目PR:"+u9PrEntityNoPro.getPrCode()+"-"+u9PrEntityNoPro.getPrLn());
            }
        }
        return true;
    }


}
