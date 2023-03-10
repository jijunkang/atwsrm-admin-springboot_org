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
 * ????????? ???????????????
 *
 * @author Will
 */
@Service
@Slf4j
public
class U9PrServiceImpl extends BaseServiceImpl<U9PrMapper, U9PrEntity> implements IU9PrService {

    @Autowired
    @Lazy  //  @Lazy  ??????service ????????????
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
            .eq(StringUtils.isNotBlank(u9Pr.getOrgcode()), "OrgCode", u9Pr.getOrgcode())
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
            throw new RuntimeException("?????????PR???" + "id???" + dto.getId());
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
        //?????????????????????
        io.setIsByWeight(dto.getIsByWeight());
        if(dto.getPromiseDate()!=null){
            io.setPromiseDate(dto.getPromiseDate().getTime() / 1000 + 8 * 3600);
        }

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
            //??????
            pr.setStatus(IU9PrService.STATUS_FLOW_SUBMIT);
        } else if (WIN_BID.equals(dto.getType())) {
            //??????
            pr.setStatus(IU9PrService.STATUS_WINBID);
        } else {
            // ???????????? ???????????????
            pr.setStatus(IU9PrService.STATUS_WINBID);
            String attachMent = this.baseMapper.selectAttachment(dto.getIoId());
            if (StringUtils.isNotEmpty(attachMent)) {
                io.setAttachment(attachMent);
            }
            this.baseMapper.deleteIo(dto.getIoId());
        }

        ioService.save(io);
        String alltypes = "?????????,??????,??????,????????????,????????????,?????????,??????,??????,??????,?????????,??????,??????,?????????,?????????,??????,??????,????????????,??????";
        if (pr.getItemName().contains("??????") || (alltypes.indexOf(pr.getItemName().split("-")[0]) > 0 && pr.getItemName().split("\\+").length < 2)) {
            if (dto.getIsNeedCheck().equals("1")) {
                pr.setIsNeedCheck("2");
                dto.setIsNeedCheck("2");
            }
        } else {
            pr.setIsNeedCheck(dto.getIsNeedCheck());
        }
        this.updateById(pr);

        //????????????PR??????IO?????????  ???????????????????????????IO?????????ID???
        List<IoEntity> ioEntities = ioService.getByPrId(pr.getId());
        ioEntities.forEach(ioEntity -> {
            if (!ioEntity.getId().equals(io.getId())) {
                ioEntity.setRemark("???????????????????????????IO?????????ID???" + io.getId());
                ioService.updateById(ioEntity);
                ioService.removeById(ioEntity);
            }
        });

        // ?????????????????????(???????????????????????????)[??????????????????????????????????????????]
        if ((!FLOW_SUBMIT.equals(dto.getType())) && NOT_NEED_CHECK.equals(dto.getIsNeedCheck()) && (!pr.getItemName().contains("??????"))) {
            CheckDTO checkDTO = new CheckDTO();
            checkDTO.setId(dto.getId());
            checkDTO.setIoId(io.getId());
            checkDTO.setStatus(IU9PrService.STATUS_ORDER);
            ioService.check2(checkDTO);
        }

        if (NOT_NEED_CHECK.equals(dto.getIsNeedCheck())) { // ????????????????????????,??????????????????
            if (pr.getItemName().indexOf("??????") > -1) {
                this.insertAutoOrderOfDJ(sup, pr, dto);
            }

            if ((pr.getItemName().indexOf("??????") > -1 || pr.getItemName().indexOf("??????") > -1 || pr.getItemName().indexOf("??????") > -1) && (pr.getItemName().split("\\+").length > 1)) {
                this.insertAutoOrderOfGBL(sup, pr, dto, io);
            }

            if (pr.getItemName().indexOf("??????") > -1) { // ??????
                this.insertAutoOrderOfZJ(sup, pr, dto, io);
            }

            if (alltypes.indexOf(pr.getItemName().split("-")[0]) > -1 && pr.getItemName().split("\\+").length < 2) {  // ?????????
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
        totalEntity.setSupplierCode(sup.getCode());  //???????????????
        totalEntity.setItemCode(pr.getItemCode()); //????????????
        TubeMaterialInfoEntity maliyVO = tubeMaterialInfoService.getOne(Condition.getQueryWrapper(totalEntity));

        totalEntity.setPrCode(pr.getPrCode());  // ????????????
        totalEntity.setPrLn(pr.getPrLn());  // ???????????????

        MailyMaterialTotalEntity entity = new MailyMaterialTotalEntity();
        entity.setPrLn(totalEntity.getPrLn());  // ??????
        entity.setPrCode(totalEntity.getPrCode()); //prcode
        entity.setSupplierCode(totalEntity.getSupplierCode()); //???????????????
        entity.setCoatingLength(maliyVO.getCoatingLength()); // ????????????
        entity.setCoatingInternalDiameter(maliyVO.getCoatingInternalDiameter()); // ????????????
        entity.setCoatingArea(maliyVO.getCoatingArea()); // ????????????
        entity.setDiameterAllowance(maliyVO.getDiameterAllowance()); // ????????????
        entity.setApertureAllowance(maliyVO.getApertureAllowance()); // ????????????
        entity.setSupplierName(maliyVO.getSupplierName());  // ???????????????
        entity.setItemCode(maliyVO.getItemCode()); // ????????????
        entity.setItemDesc(maliyVO.getItemDesc());// ????????????
        entity.setExternalDiameter(maliyVO.getExternalDiameter());  //??????
        entity.setInternalDiamete(maliyVO.getInternalDiamete()); //??????
        entity.setLength(maliyVO.getLength()); // ???????????????
        entity.setWeight(maliyVO.getWeight()); // ???????????????
        entity.setTheMaterialPrice(maliyVO.getTheMaterialPrice().setScale(2, BigDecimal.ROUND_DOWN));// ????????????
        entity.setMaterialPrice(maliyVO.getMaterialPrice()); // ?????????
        entity.setProcessingFee(maliyVO.getProcessingFee()); // ?????????
        entity.setPrice(maliyVO.getPrice());// ?????????
        entity.setCoatingPrice(maliyVO.getCoatingPrice()); // ????????????
        entity.setSprayPrice(maliyVO.getSprayPrice()); // ?????????
        entity.setUnitPrice(maliyVO.getUnitPrice()); // ????????????
        entity.setPriceNum(Integer.valueOf(io.getPriceNum().toString())); //??????
        entity.setTotalPrice(maliyVO.getUnitPrice().multiply(BigDecimal.valueOf(entity.getPriceNum()))); //??????
        tubeMaterialMapper.insert(entity);
    }

    private void insertAutoOrderOfDJ(Supplier sup, U9PrEntity pr, SubmitPriceDTO dto) {
        SubmitPriceReq req = new SubmitPriceReq();
        req.setItemName(pr.getItemName());
        req.setItemCode(pr.getItemCode());
        req.setSupCode(sup.getCode());
        String chooseSupCode = dto.getSupCode();
        // ?????????????????????????????????  ???????????????????????????

        List<ItemInfoEntityDJReport> NoList = this.baseMapper.selectNoSwnningBid(req);
        for (ItemInfoEntityDJReport entity : NoList) {
            if (sup.getCode().equals(entity.getSupCode())) {
                entity.setSwnningBid("?????????");
                this.baseMapper.insertDjAutoOrder(pr.getPrCode(), pr.getPrLn().toString(), entity.getItemCode(), entity.getItemName(), entity.getSupCode(), entity.getSupName(), entity.getWeight(),
                    entity.getMaterialPrice(), entity.getPrice(), entity.getBigOuterSize(), entity.getBigHeightSize(), entity.getSmallOuterSize(), entity.getTotalHeightSize(), entity.getInnerSize(),
                    entity.getOuterSize(), entity.getHeightSize(), entity.getType(), entity.getRes(), entity.getHeightRemain(), entity.getOuterRemain(), entity.getInnerRemain(), entity.getSwnningBid(), entity.getNewInnerRemain());
            } else {
                entity.setSwnningBid("?????????");
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
     * ??????
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
            // ??????????????????null?????????????????? proNo
            if (dto.getProNo() == null || dto.getProNo().isEmpty()) {
                dto.setProNo(dto.getApsProNo());
            }
            excelDTO.setFlowType(dictBizService.getValue("pr_flow_type", excelDTO.getFlowType()));
            excelDTO.setStatusFmt(dictBizService.getValue("pr_status", excelDTO.getStatus() + ""));
            excelDTO.setInquiryWayFmt(dictBizService.getValue("pr_inquiry_way", excelDTO.getInquiryWay()));
            excelList.add(excelDTO);
        }
        ExcelUtils.defaultExport(excelList, U9PrExcelDTO.class, "?????????" + DateUtil.formatDate(new Date()), response);

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
     * ??????
     *
     * @param submitPriceReq
     * @return ??????????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitBatch(SubmitPriceReq submitPriceReq) {
        boolean result = false;
        // ????????????
        List<SubmitPriceDTO> items = submitPriceReq.getSubmitPriceDTOs();
        for (SubmitPriceDTO item : items) {
            if (!item.getItemCode().startsWith("15110")
                && !item.getItemCode().startsWith("130301")
                && !item.getItemCode().startsWith("130302")
                && !item.getItemCode().startsWith("130101")
                && !item.getItemCode().startsWith("130102")
                && !item.getItemCode().startsWith("131111")
                && !item.getItemCode().startsWith("131106")
                && item.getItemName().indexOf("???")<0
            ) {
                //?????????????????????
                if (item.getPromiseDate()==null){
                    throw new RuntimeException("????????????????????????????????????");
                }

            }

            result = submitPrice(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getInquiryCount(String purchType) {
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_INQUIRY);
            put("title", "?????????");
            put("count", getInquiryCount(STATUS_INQUIRY.toString(), purchType, null));
        }});
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_QUOTED);
            put("title", "?????????");
            put("count", getInquiryCount(STATUS_QUOTED.toString(), purchType, null));
        }});
        // ???????????????????????????????????????
        if (PURCHASE_TYPE_NORMAL.equals(purchType)) {
            result.add(new HashMap<String, Object>(3) {{
                put("status", STATUS_WINBID);
                put("title", "?????????");
                put("count", getInquiryCount(STATUS_WINBID.toString(), purchType, null));
            }});
        }
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_WAIT);
            put("title", "?????????");
            put("count", getInquiryCount(STATUS_WAIT.toString(), purchType, 1));
        }});
        return result;
    }

    @Override
    public List<Map<String, Object>> getFlowCount(String purchType) {
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_HANG + "," + STATUS_FLOW);
            put("title", "?????????");
            put("count", getFlowCount(STATUS_HANG + "," + STATUS_FLOW, purchType));
        }});
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_FLOW_SUBMIT);
            put("title", "?????????");
            put("count", getFlowCount(STATUS_FLOW_SUBMIT.toString(), purchType));
        }});
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_WINBID);
            put("title", "?????????");
            put("count", getFlowCount(STATUS_WINBID.toString(), purchType));
        }});
        result.add(new HashMap<String, Object>(3) {{
            put("status", STATUS_WAIT);
            put("title", "?????????");
            put("count", getFlowCount(STATUS_WAIT.toString(), purchType));
        }});
        // ???????????????????????????????????????tab
        if (PURCHASE_TYPE_INNER.equals(purchType)) {
            result.add(new HashMap<String, Object>(3) {{
                put("status", STATUS_FLOW_NOSUP);
                put("title", "????????????");
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
        highestPrice.setType("???????????????");
        highestPrice.setSupName("-");
        highestPrice.setNumberInterval("-");
        highestPrice.setPrice(poItemService.getHighestPrice(u9PrVO.getItemCode()));
        voList.add(highestPrice);
        PriceVO lowestPrice = new PriceVO();
        lowestPrice.setType("???????????????");
        lowestPrice.setSupName("-");
        lowestPrice.setNumberInterval("-");
        lowestPrice.setPrice(poItemService.getLowestPrice(u9PrVO.getItemCode()));
        voList.add(lowestPrice);
        PriceVO lastPrice = new PriceVO();
        lastPrice.setType("?????????");
        lastPrice.setSupName("-");
        lastPrice.setNumberInterval("-");
        PoItemEntity poItemEntity = poItemService.getLastPoInfos(u9PrVO.getItemCode(), u9PrVO.getItemName());
        if (poItemEntity != null) {
            lastPrice.setPrice(poItemEntity.getPrice());
        }
        voList.add(lastPrice);

        //????????????
        u9PrVO.getPriceNumList().forEach(entity -> {
            //??????????????????????????????????????????????????????
            List<PriceFrameEntity> priceFrameEntities = priceFrameService.getNumberInterval(u9PrVO.getItemCode(), new BigDecimal(entity));
            if (priceFrameEntities.size() > 0) {
                priceFrameEntities.forEach(priceFrameEntity -> {
                    PriceVO u9Price = new PriceVO();
                    u9Price.setType("????????????");
                    u9Price.setSupName(priceFrameEntity.getSupName());
                    u9Price.setPrice(priceFrameEntity.getPrice());
                    u9Price.setNumberInterval(priceFrameEntity.getLimitMin() + "~" + priceFrameEntity.getLimitMax());
                    voList.add(u9Price);
                });
            }
        });
        //????????????
        List<MmVolumeCalculateEntity> mmVolumeCalculateEntities = mmVolumeCalculateService.getByItemCode(u9PrVO.getItemCode());
        if (mmVolumeCalculateEntities.size() > 0) {
            mmVolumeCalculateEntities.forEach(entity -> {
                PriceVO u9Price = new PriceVO();
                u9Price.setType("????????????");
                u9Price.setSupName(entity.getSupName());
                u9Price.setNumberInterval("-");
                u9Price.setPrice(entity.getPrice());
                voList.add(u9Price);
            });
        }

        //??????
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
        ExcelUtils.defaultExport(dtoList, U9PrInquiryExcelDTO.class, "?????????" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public void inquiryExportOfOthers(PrReq prReq, HttpServletResponse response) {
        List<U9PrDTO> list = this.baseMapper.selectListByReqOfOthers(prReq);
        List<U9PrInquiryExcelDTO> dtoList = Lists.newArrayList();
        list.forEach(temp -> {
            U9PrInquiryExcelDTO dto = BeanUtil.copy(temp, U9PrInquiryExcelDTO.class);
            dtoList.add(dto);
        });
        ExcelUtils.defaultExport(dtoList, U9PrInquiryExcelDTO.class, "?????????" + DateUtil.formatDate(new Date()), response);
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
        ExcelUtils.defaultExport(dtoList, U9PrFlowExcelDTO.class, "?????????" + DateUtil.formatDate(new Date()), response);
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
        ExcelUtils.defaultExport(dtoList, U9PrFlowExcelDTO.class, "?????????" + DateUtil.formatDate(new Date()), response);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean flowBatchExcel(SubmitPriceReq submitPriceReq) {
        List<SubmitPriceDTO> submitPriceDTOs = submitPriceReq.getSubmitPriceDTOs();
        for (SubmitPriceDTO submitPriceDTO : submitPriceDTOs) {
            Long id = submitPriceDTO.getId();
            U9PrEntity u9PrEntity = getById(id);
            if (u9PrEntity == null) {
                throw new RuntimeException("?????????PR???" + "id???" + id);
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
                    throw new RuntimeException("?????????IO???" + "prId???" + id + "?????????" + STATUS_WINBID);
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
                // ?????????????????????
                List<IoEntity> ioEntities = ioService.getByPrId(submitPriceDTO.getId());
                for (IoEntity ioEntity : ioEntities) {
                    ioEntity.setAttachment(submitPriceDTO.getAttachment());
                    if (StringUtil.isNotBlank(submitPriceDTO.getRemark())) {
                        ioEntity.setRemark(submitPriceDTO.getRemark());
                    }
                    ioService.updateById(ioEntity);
                }
            } else {
                // ?????????????????????
                IoEntity ioEntity = ioService.getById(submitPriceDTO.getIoId());
                if (ioEntity == null) {
                    throw new RuntimeException("?????????????????????IO???" + "Id???" + submitPriceDTO.getIoId());
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
                throw new RuntimeException("?????????PR???" + "id???" + id);
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
            throw new RuntimeException("?????????IO???" + ioReq.getId());
        }
        U9PrEntity u9PrEntity = getById(ioEntity.getPrId());
        if (u9PrEntity == null) {
            throw new RuntimeException("?????????PR???" + ioEntity.getPrId());
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
        Map<String, U9PrDTO> fzMap = new LinkedHashMap<>();
        Map<String, U9PrDTO> qzMap = new LinkedHashMap<>();

        boolean isExistQZ = false;

        // ????????????
        if(prReq.getItemName()!=null && !prReq.getItemName().isEmpty() && prReq.getItemName().indexOf("???")>-1) {
            isExistQZ = true;
            fzMap = this.getFzMap(prReq);
            int name6 = 0;
            for(U9PrDTO dto : retPage.getRecords()) {

                if(!dto.getItemName().split("-")[0].equals("??????")) {
                    String qzKey = "????????????" + name6;
                    qzMap.put(qzKey, dto);
                    name6++;
                    continue;
                }

                // ??????
                ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(dto.getItemName());
                if(itemInfoEntity==null || itemInfoEntity.getMaterial()==null || itemInfoEntity.getCoat()==null) {
                    String qzKey = "????????????" + name6;
                    qzMap.put(qzKey, dto);
                    name6++;
                    continue;
                }
                String name1 = "??????";
                String name2 = dto.getItemName().split("-")[1]; // ??????
                String name3 = itemInfoEntity.getMaterial(); // ??????
                String name4 = itemInfoEntity.getCoat(); // ????????????
                if(name4.equals("G20")) {
                    name4 = "G14";
                }
                String name5 = "Y";
                if(dto.getItemName().split("-")[1].indexOf("F") > -1) {
                    name5 = "F";
                }
                String qzKey = name1+name2+name3+name4+name5+"-"+name6;
                qzMap.put(qzKey,dto);
                name6++;
            }

            // ??????????????????????????????MAP?????????list
            Map<String, U9PrDTO> sortedQzMap = qzMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldVal, newVal) -> oldVal,
                LinkedHashMap::new
            ));
            retList = sortedQzMap.values().stream().collect(Collectors.toList());
            retPage.setRecords(retList);
        }

        // ?????????????????????????????????????????????
        for (U9PrDTO dto : retPage.getRecords()) {
            // ?????????????????????
            String key = dto.getPrCode() + dto.getPrLn() + dto.getItemCode();
            qzfzMap.put(key, dto);

            // ????????????????????????????????????
            String itemName = dto.getItemName();
            if (itemName.split("-").length > 2 && itemName.split("-")[0].toString().equals("??????") && itemName.split("-")[1].indexOf("F") < 0 && isExistQZ ) { // ????????????????????????????????????????????? F???
                Map<String, U9PrDTO> FZMapTemp = this.getFZItemInfoNew(itemName,fzMap);
                if(!FZMapTemp.isEmpty()) {
                    for (String tempKey : FZMapTemp.keySet()) {
                        // ?????????????????????????????????
                        U9PrDTO fzU9PrDto = FZMapTemp.get(tempKey);
                        // fz???key?????? prCode + prLn + itemCode
                        String fzKey = fzU9PrDto.getPrCode() + fzU9PrDto.getPrLn() + fzU9PrDto.getItemCode();
                        // ?????????????????????&&?????????????????????????????????????????????????????????
                        if(qzfzMap.containsKey(fzKey)) {
                            qzfzMap.remove(fzKey);
                        }
                        // ??????????????????????????? ????????????
                        qzfzMap.put(fzKey,fzU9PrDto);
                    }
                }
            }
//            if (!FZItemInfo.isEmpty()) {
//                List<String> fzItemCodeList = Arrays.asList(FZItemInfo.split(","));
//                for (String fzItemCode : fzItemCodeList) {
//                    List<U9PrDTO> listFz = this.baseMapper.selectFzList(fzItemCode.split(":")[0], prReq.getStatuss());
//                    if (listFz != null && listFz.size() > 0) {
//                        for (U9PrDTO fzdto : listFz) {
//                            String fzKey = fzdto.getPrCode() + fzdto.getPrLn() + fzdto.getItemCode();
//                            if (qzfzMap.containsKey(fzKey)) {
//                                // ??????????????????
//                                fzdto.setCodeType(qzfzMap.get(fzKey).getCodeType());
//                                qzfzMap.remove(fzKey);
//                                qzfzMap.put(fzKey, fzdto);
//                            } else {
//                                qzfzMap.put(fzKey, fzdto);
//                            }
//                        }
//                    }
//                }
//            }
        }

        // ??????????????????????????????MAP?????????list
        retList = qzfzMap.values().stream().collect(Collectors.toList());

        // ?????????????????????
        for (U9PrDTO dto : retList) {
            // ?????????????????????U9???????????????????????????
            dto.setPromiseDate(dto.getReqDate());
            // ???????????????????????????????????????????????????????????? 20230228
            if (!dto.getItemCode().startsWith("15110")
                && !dto.getItemCode().startsWith("130301")
                && !dto.getItemCode().startsWith("130302")
                && !dto.getItemCode().startsWith("130101")
                && !dto.getItemCode().startsWith("130102")
                && !dto.getItemCode().startsWith("131111")
                && !dto.getItemCode().startsWith("131106")
                && dto.getItemName().indexOf("???")<0
            ) {
                dto.setPromiseDate(dto.getReqDate());

            }else{
                if (dto.getPromiseDateFromQt()==null){
                    //dto.setPromiseDate(null);
                }else{
                    Date promiseDateFromQt = cn.hutool.core.date.DateUtil.offsetDay(dto.getPromiseDateFromQt(),-25) ;
                    dto.setPromiseDate(promiseDateFromQt.getTime()/1000);
                }
            }



            // ????????????????????????????????????
            String itemName = dto.getItemName();
            if (itemName.indexOf("???") > -1) {
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

            // ??????????????????null?????????????????? proNo
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

    private Map<String, U9PrDTO> getFZItemInfoNew(String itemname, Map<String, U9PrDTO> fzMap) {
        Map<String, U9PrDTO> qzMap = new LinkedHashMap<>();
        // ??????
        ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(itemname);
        String name1 = "??????";
        String name2 = itemname.split("-")[1]; // ??????
        String name3 = itemInfoEntity.getMaterial(); // ??????
        String name4 = itemInfoEntity.getCoat(); // ????????????
        String name5 = "G50";// ????????????

        if("G20".equals(name4) ||"G14".equals(name4) ) {
            name4 = "G06";
        } else if("G06".equals(name4)) {
            name4 = "G05";
        } else {
            name4 = "";
        }
        // ???
        String key1 = name1 + name2 + name3 + name4;
        String key2 = name1 + name2 + name3 + name4 + "+" + name5;
        if(fzMap.containsKey(key1)) {
            qzMap.put(key1,fzMap.get(key1));
        }
        if(fzMap.containsKey(key2)) {
            qzMap.put(key2,fzMap.get(key2));
        }
        return qzMap;
    }

    /**
     * ???????????????map
     * @param prReq
     * @return
     */
    public Map<String, U9PrDTO> getFzMap(PrReq prReq){
        Map<String, U9PrDTO> fzMap = new LinkedHashMap<>();
        List<U9PrDTO> allListFz = this.baseMapper.selectAllFzList(prReq.getStatuss());
        for(U9PrDTO item : allListFz) {
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfFZ(item.getItemName());
            String name1 = "??????"; // ??????
            String name2 = item.getItemName().split("-")[1]; // ??????
            String name3 = itemInfoEntity.getMaterial(); // ??????
            if("Monel400".equals(name3)) {
                name3 = "MonelK500";  // 3???????????????MonelK500??????Monel400??????????????????
            }
            String name4 = itemInfoEntity.getFzCoat(); // ????????????
            String key = name1+name2+name3+name4;
            fzMap.put(key,item);
        }
        return fzMap;
    }

    private String getFZItemInfo(String itemCode) {
        String fzItemInfo = "";
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;
        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            @Cleanup Statement stmt = conn.createStatement();
            //2.??????????????????sql
            StringBuffer sql = new StringBuffer();
            sql.append("select * from atwplm.Cust_Tee_Relationship where ballcode = '").append(itemCode).append("'");

            //3.??????
            @Cleanup ResultSet resultSet = stmt.executeQuery(sql.toString());

            while (resultSet.next()) {
                String fzItemCode = resultSet.getString("valveseatcode");
                String fzItemName = resultSet.getString("valveseatname");
                fzItemInfo = fzItemInfo + fzItemCode + ":" + fzItemName + ",";
            }
        } catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
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
        //211211 ?????????  ????????????2002002
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
        // ???????????????????????????????????? ?????? ???????????? ???no_sup ?????? ???????????? ???
        if ((STATUS_FLOW_NOSUP.toString()).equals(prReq.getStatuss())) {
            prReq.setStatuss(STATUS_FLOW.toString());
            prReq.setFlowType(FLOW_TYPE_NOSUP);
        }
        IPage<U9PrDTO> u9Page = this.baseMapper.selectPageWithoutIo(page, prReq);
        getWinIo(prReq, u9Page);
        return u9Page;
    }

    /**
     * ???????????????IO
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
     * ??????????????????
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
     * ??????????????????
     *
     * @param status String
     * @return int
     */
    private int getFlowCount(String status, String type) {
        PrReq prReq = new PrReq();
        if (!status.equals(STATUS_FLOW_NOSUP)) {
            prReq.setStatuss(status);
        } else {
            // ??????????????????
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
            // ????????????
            if (status.equals(STATUS_HANG + "," + STATUS_FLOW) || status.equals(STATUS_FLOW_SUBMIT.toString()) || status.equals(STATUS_WINBID.toString())) {
                int all = this.baseMapper.selectFlowPageList(prReq).size();
                int noSup = this.baseMapper.selectPageWithoutIo(new Page<>(), prReq).getRecords().size();
                return all - noSup;
            }
            // ?????????????????????
            if (status.equals(STATUS_FLOW_SUBMIT.toString()) || status.equals(STATUS_WINBID.toString())) {
                return this.baseMapper.selectFlowPageList(prReq).size();
            }
            // ????????????
            if (status.equals(STATUS_FLOW_NOSUP.toString())) {
                prReq.setStatuss("40");
                prReq.setPurchCode(null);
                return this.baseMapper.selectPageWithoutIo(new Page<>(), prReq).getRecords().size();
            }
            // ?????????
            prReq.setSource("purch_submit");
            return this.baseMapper.selectFlowPageOfOthers(new Page<>(), prReq).getRecords().size();

        } else {
            // normal
            if (status.equals(STATUS_HANG + "," + STATUS_FLOW)) {
                return this.baseMapper.selectFlowPageList(prReq).size();
            }
            // ?????????
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
            //???????????????
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
     * ????????????????????????
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
     * ????????????????????????-?????????
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
     * ????????????????????????
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

    @Override
    public int poReserveCount() {

        return this.baseMapper.getPoReserveCount();
    }


    /**
     * ?????????????????????-?????????
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
        //  ??????io??????
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
     * ???????????????????????????
     *
     * @param u9PrDTOS
     * @return
     */
    @Override
    public List<ItemInfoDTO> autoRetrieve(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        // ????????????  ???????????? ??????  ?????? 20221205
        List<ItemInfoDTO> itemInfoDTOSOfQiuZuo = autoRetrieveOfQiuZuoNew(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfQiuZuo);

        // ?????????  ??????????????? 20221125
        List<ItemInfoDTO> itemInfoDTOSOfLZQ = autoRetriveOfLZQ(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfLZQ);

        // ?????????
        List<ItemInfoDTO> itemInfoDTOSOfBZX = autoRetrieveOfBZX(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfBZX);

        // ??????
        List<ItemInfoDTO> itemInfoDTOSOfFL = autoRetrieveOfFL(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfFL);

        // ??????
        List<ItemInfoDTO> itemInfoDTOSOfDZ = autoRetrieveOfDZ(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfDZ);

        // ??????
        List<ItemInfoDTO> itemInfoDTOSOfZhuJian = autoRetrieveOfZhuJian(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfZhuJian);

        // ?????? ??????  ??????
        List<ItemInfoDTO> itemInfoDTOSOfDuanJian = autoRetrieveOfDuanJian(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfDuanJian);


        // ????????????
        List<ItemInfoDTO> itemInfoDTOSOfWW = autoRetrieveOfWW(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfWW);

        // ?????????maily
        List<ItemInfoDTO> itemInfoDTOGuanBangLiao = authRetriveOfGuanBangLiao(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOGuanBangLiao);

        // ?????????
        List<ItemInfoDTO> itemInfoDTOSOfXLJ = autoRetriveOfXLJ(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfXLJ);

        /*List<ItemInfoDTO> itemInfoDTOSOfQiuZuo = autoRetrieveOfQiuZuo(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfQiuZuo);*/



        return itemInfoDTOS;
    }

    /**
     * ??????????????????
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfDZ(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        List<ItemInfoEntityOfDZ> itemInfoEntityOfDZInsert = new ArrayList<>();


        for (U9PrDTO u9Prs : u9PrDTOS) {
            List<PriceVO> priceVOS = new ArrayList<>();

            // ??????????????????????????????
            if (u9Prs.getItemName().indexOf("??????") < 0) {
                continue;
            }
            //???????????????????????????
            String outersize="";
            String lengthsize="";
            String poutersize="";
            String plengthsize="";
            ItemInfoEntityBasOfDZ itemInfoEntityBasOfDZ = u9PrMapper.selectBasicItemInfoOfDZ(u9Prs.getItemCode());

            if (itemInfoEntityBasOfDZ == null) {
                continue;
            }

            outersize=itemInfoEntityBasOfDZ.getOuterSize();//??????
            lengthsize=itemInfoEntityBasOfDZ.getInnerSize();//??????
            poutersize=itemInfoEntityBasOfDZ.getPOuterSize();//????????????
            plengthsize=itemInfoEntityBasOfDZ.getPInnerSize();//????????????

            if(StringUtils.isBlank(outersize)||StringUtils.isBlank(lengthsize)||StringUtils.isBlank(poutersize)||StringUtils.isBlank(plengthsize)){
                continue;
            }

            //????????????
            String outerSizeRule="";//????????????
            String lengthSizeRule="";//?????????????????????


            ItemInfoEntityOfDZ itemInfoEntityOfDZ=new ItemInfoEntityOfDZ();
            itemInfoEntityOfDZ.setOuterSize(outersize);
            List<ItemInfoEntityOfDZ> itemInfoEntityOfDZS = this.baseMapper.selectDZResItemInfo(itemInfoEntityOfDZ);//???????????????

            ItemInfoDTO itemInfoDTO=new ItemInfoDTO();//??????pr???  ????????????


            for (ItemInfoEntityOfDZ infoEntityOfDZ:itemInfoEntityOfDZS) {

                ItemInfoEntityOfDZ dzReport=new ItemInfoEntityOfDZ();

                dzReport.setSupName(infoEntityOfDZ.getSupName());
                dzReport.setSupCode(infoEntityOfDZ.getSupCode());
                dzReport.setItemCode(u9Prs.getItemCode()); // (??????)
                dzReport.setItemName(u9Prs.getItemName()); // (??????)
                dzReport.setOuterSizeOld(outersize);
                dzReport.setQuantity(String.valueOf(u9Prs.getPriceNum()));


                itemInfoDTO.setId(u9Prs.getId().toString()); // (pr???id)
                itemInfoDTO.setItemCode(u9Prs.getItemCode()); // (??????)
                itemInfoDTO.setItemName(u9Prs.getItemName()); // (??????)

                if (itemInfoEntityOfDZS.size()>1){
                    itemInfoDTO.setSupColorType("1");
                }else{
                    itemInfoDTO.setSupColorType("0");
                }



                itemInfoEntityOfDZ.setSupCode(infoEntityOfDZ.getSupCode());//???????????????

                outerSizeRule = infoEntityOfDZ.getOuterSize();
                lengthSizeRule=infoEntityOfDZ.getLengthSize();
                BigDecimal lengthSizeRes=new BigDecimal(lengthsize).add(new BigDecimal(lengthSizeRule));

                dzReport.setLengthSize(lengthsize);
                dzReport.setLengthRes(lengthSizeRule);
                dzReport.setHeightSize(String.valueOf(lengthSizeRes));

                String outerSizeRes=this.handleXLJOuterSize(outersize,outerSizeRule);//????????????
                dzReport.setOuterRes(outerSizeRule);
                dzReport.setOuterSize(outerSizeRes);
                //??????????????? D1^2*??/4*L1*7.98/1000000*????????????*1.2
                String caizhi="";
                String material_price="";
                String k="";
                BigDecimal pai = new BigDecimal("3.14"); // ??
                ItemInfoEntityOfDZ itemInfoEntityOfDZ1 = this.baseMapper.selectItemPriceOfDZ(itemInfoEntityOfDZ);
                BigDecimal material_cost=new BigDecimal("0");
                if (itemInfoEntityOfDZ1 != null) {
                    caizhi=itemInfoEntityOfDZ1.getMaterial();
                    material_price=itemInfoEntityOfDZ1.getSingleCost();
                    k=itemInfoEntityOfDZ1.getK();


                    //BigDecimal materialparam1 = new BigDecimal(outerSizeRes).multiply(new BigDecimal(outerSizeRes)).multiply(pai).divide(new BigDecimal("4"),2, BigDecimal.ROUND_HALF_UP).multiply(lengthSizeRes); // D1^2*??/4*L1
                    BigDecimal materialparam1 = new BigDecimal(outerSizeRes).multiply(new BigDecimal(outerSizeRes)).multiply(pai).divide(new BigDecimal("4")).multiply(lengthSizeRes); // D1^2*??/4*L1
                    BigDecimal materialparam2 = new BigDecimal("7.98").divide(new BigDecimal("1000000")); // 7.98/1000000
                    BigDecimal materialparam3 = new BigDecimal(material_price);//????????????
                    BigDecimal singleWight=materialparam1.multiply(materialparam2).setScale(2,BigDecimal.ROUND_HALF_UP);
                    material_cost = singleWight.multiply(materialparam3).multiply(new BigDecimal(k)).setScale(2,BigDecimal.ROUND_HALF_UP);

                    dzReport.setWeight(String.valueOf(singleWight));
                    dzReport.setSingleCost(material_price);
                    dzReport.setMaterialCost(String.valueOf(material_cost));
                    dzReport.setK(k);
                }



                //??????????????? d*??*L2/100*???????????????G14????????????????????????55??????????????????=55
                itemInfoEntityOfDZ.setMaterial(caizhi);
                ItemInfoEntityOfDZ itemInfoEntityOfDZ2 = this.baseMapper.selectItemSprayingOfDZ(itemInfoEntityOfDZ);
                BigDecimal spraying_cost=new BigDecimal("0");
                if (itemInfoEntityOfDZ2 != null) {
                    String spraying = itemInfoEntityOfDZ2.getSpraying();//??????
                    String spraying_price = itemInfoEntityOfDZ2.getSprayingPrice();//????????????

                    BigDecimal sprayingparam1 = new BigDecimal(poutersize).multiply(pai).multiply(new BigDecimal(plengthsize)).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);//d*??*L2/100
                    BigDecimal sprayingparam2 = new BigDecimal(spraying_price);//????????????

                    spraying_cost = sprayingparam1.multiply(sprayingparam2).setScale(2,RoundingMode.HALF_UP);//?????????

                    if("G14".equals(spraying)&&spraying_cost.compareTo(new BigDecimal("55"))==-1){//G14????????????????????????55??????????????????=55
                        spraying_cost=new BigDecimal("55");
                    }

                    dzReport.setSprayingOuterSize(poutersize);
                    dzReport.setSprayingLengthSize(plengthsize);
                    dzReport.setSprayingArea(String.valueOf(sprayingparam1));
                    dzReport.setSprayingPrice(spraying_price);
                    dzReport.setSprayingFee(String.valueOf(spraying_cost));

                }



                //??????????????? ??????D??????????????????????????????


                ItemInfoEntityOfDZ itemInfoEntityOfDZ3 = this.baseMapper.selectItemGrindingOfDZ(itemInfoEntityOfDZ);

                String grindingprice="0";
                if (itemInfoEntityOfDZ3 != null) {
                    grindingprice = itemInfoEntityOfDZ3.getPrice();//???????????????


                    dzReport.setGrindingCost(grindingprice);

                }

                //??????????????? ??????D??????????????????????????????
                ItemInfoEntityOfDZ itemInfoEntityOfDZ4 = this.baseMapper.selectItemProcessingOfDZ(itemInfoEntityOfDZ);
                String processprice="0";
                if (itemInfoEntityOfDZ4 != null) {
                    processprice = itemInfoEntityOfDZ4.getProcessCost();//?????????

                    dzReport.setProcessCost(processprice);
                }


                //????????????=?????????+?????????+?????????+?????????
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


            // ??????xlj??????
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
     * ?????????????????????
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfBZX(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        //List<ItemInfoEntityOfFL> itemInfoEntityOfFLInsert = new ArrayList<>();


        for (U9PrDTO u9Prs : u9PrDTOS) {
            List<PriceVO> priceVOS = new ArrayList<>();

            // ????????????????????????20020002
            if (!"20020002".equals(u9Prs.getItemCode())) {
                continue;
            }

            //???????????? ATW-S220561-01,2100*1700*1000
            String itemName=u9Prs.getItemName();
            String[] CKG = itemName.split(",");//?????????
            String[] CKGsplit = CKG[1].split("\\*");
            if (CKGsplit.length<3) {
                continue;
            }
            BigDecimal length =new BigDecimal(CKGsplit[0]);
            BigDecimal width =new BigDecimal(CKGsplit[1]);
            BigDecimal height=new BigDecimal(CKGsplit[2]);

            //????????????
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


            ItemInfoDTO itemInfoDTO=new ItemInfoDTO();//??????pr???  ????????????

            for (ItemInfoEntityOfBZX infoEntityOfBZX:itemInfoEntityOfBZXES) {

                itemInfoDTO.setId(u9Prs.getId().toString()); // (pr???id)
                itemInfoDTO.setItemCode(u9Prs.getItemCode()); // (??????)
                itemInfoDTO.setItemName(u9Prs.getItemName()); // (??????)


                if (itemInfoEntityOfBZXES.size()>1){
                    itemInfoDTO.setSupColorType("1");
                }else{
                    itemInfoDTO.setSupColorType("0");
                }

                //???????????? ??????=????????????*??????
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
     * ??????????????????
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfFL(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        List<ItemInfoEntityOfFL> itemInfoEntityOfFLInsert = new ArrayList<>();


        for (U9PrDTO u9Prs : u9PrDTOS) {
            List<PriceVO> priceVOS = new ArrayList<>();

            // ??????????????????????????????
            if (u9Prs.getItemName().indexOf("??????") < 0) {
                continue;
            }
            //????????????
            ItemInfoEntityOfFL itemInfoOfFL = getItemInfoOfFL(u9Prs.getItemName());


            if ("20".equals(itemInfoOfFL.getSize())) {
                //?????????20?????????????????????20??????????????????????????????1???3???6???15????????????
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


            ItemInfoDTO itemInfoDTO=new ItemInfoDTO();//??????pr???  ????????????




            for (ItemInfoEntityOfFL itemInfoEntityOfFL:itemInfoEntityOfFLS) {

                ItemInfoEntityOfFL flReport=new ItemInfoEntityOfFL();

                flReport.setSupName(itemInfoEntityOfFL.getSupName());//???????????????
                flReport.setSupCode(itemInfoEntityOfFL.getSupCode());//???????????????
                flReport.setItemCode(u9Prs.getItemCode()); // (??????)
                flReport.setItemName(u9Prs.getItemName()); // (??????)
                flReport.setPrCode(u9Prs.getPrCode());//????????????
                flReport.setPrLn(String.valueOf(u9Prs.getPrLn()));//????????????
                flReport.setQuantity(String.valueOf(u9Prs.getPriceNum()));//??????
                flReport.setPrice(itemInfoEntityOfFL.getSingleCost());

                itemInfoDTO.setId(u9Prs.getId().toString()); // (pr???id)
                itemInfoDTO.setItemCode(u9Prs.getItemCode()); // (??????)
                itemInfoDTO.setItemName(u9Prs.getItemName()); // (??????)



                if (itemInfoEntityOfFLS.size()>1){
                    itemInfoDTO.setSupColorType("1");
                }else{
                    itemInfoDTO.setSupColorType("0");
                }


                //????????????

                ItemInfoEntityOfFL selectItemWeightOfFL = this.baseMapper.selectItemWeightOfFL(flReport);
                String weight="0";
                if (selectItemWeightOfFL != null) {
                    weight = selectItemWeightOfFL.getWeight();
                }

                flReport.setWeight(weight);

                //???????????? ????????????=??????*??????

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



            // ??????xlj??????
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
     * ????????????????????????????????????
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetriveOfXLJ(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        List<ItemInfoEntityOfXLJ> itemInfoEntityOfXLJSToInsert = new ArrayList<>();
        String alltypes = "?????????,??????,??????,????????????,????????????,?????????,??????,??????,??????,?????????,????????????,??????,??????,????????????,??????,??????,?????????,?????????,??????,??????,??????,??????";
        String notSplitTypes = "??????,?????????,?????????,??????,??????,??????,??????";

        // ?????????????????? ????????? ???????????????
        for (U9PrDTO u9Prs : u9PrDTOS) {

            boolean isDJ = false;
            boolean isJXB = false;

            // ????????? ????????????????????????
            if ((alltypes.indexOf(u9Prs.getItemName().split("-")[0]) < 0 || (u9Prs.getItemName().split("\\+").length > 1 && "??????,??????,??????".indexOf(u9Prs.getItemName().split("-")[0]) > -1))) {
                continue;
            }

            // ????????????????????????
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

                //????????????????????????????????????
                if ("?????????".equals(mainType)) {
                    if (StringUtil.isBlank(splitItemInfo.getInnerSize()) || StringUtil.isBlank(splitItemInfo.getOuterSize()) || StringUtil.isBlank(splitItemInfo.getHeightSize())) {
                        continue;
                    }
                    material = itemNameList[itemNameList.length - 1].substring(0, itemNameList[itemNameList.length - 1].indexOf("+"));
                } else {
                    material = itemNameList[length - 1];
                }


                // ???????????????????????????
                if (mainType.equals("??????") && material.equals("630")) {
                    splitItemInfo.setMaterialType("??????");
                } else if (mainType.equals("??????") && !material.equals("630")) {
                    splitItemInfo.setMaterialType("??????");
                }

                splitItemInfo.setMainType(mainType);
                splitItemInfo.setMaterial(material);
            }


            String outerSizeOld = "";
            String innerSizeOld = "";
            String heightSizeOld = "";
            String R = "";
            String X = "";

            // ???????????????
            if (splitItemInfo != null) {

                outerSizeOld = splitItemInfo.getOuterSize();
                innerSizeOld = splitItemInfo.getInnerSize();
                heightSizeOld = splitItemInfo.getHeightSize();

                // ??? 0 ?????? ?????? ?????????????????????5???[?????? ????????? + ???????????? + ?????? -> ????????????]
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

                    if (materialType.equals("??????")) {
                        // @@@@@0000000@@@@
                        splitItemInfo = handleOfDJOfXLJItem(splitItemInfo);
                        if (splitItemInfo.getMaterialCost() == null || splitItemInfo.getMaterialCost().isEmpty()) {
                            continue;
                        }
                        isDJ = true;
                    }
                }

                // ???????????? ????????????
                List<ItemInfoEntityOfXLJ> itemInfoEntitiesFromDataBase = new ArrayList<>();
                if (isDJ) {
                    // ???????????????????????????????????? ?????? ?????????????????? ?????????????????????????????????????????????
                    itemInfoEntitiesFromDataBase = this.baseMapper.selectXLJResItemInfoOfDJ(splitItemInfo);
                } else {
                    // ?????????????????????????????????????????? ??????????????? ????????????????????? ?????? ???

                    // ????????? ?????????
                    if (splitItemInfo.getMaterialType().equals("??????") && (splitItemInfo.getInnerSize() == null || splitItemInfo.getInnerSize().isEmpty())) {

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

                        // ??????????????????????????????????????????????????? ?????????
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

                // ????????????????????????
                if (itemInfoEntitiesFromDataBase.size() > 0) {
                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO(); // ??????????????????
                    List<PriceVO> priceVOS = new ArrayList<>();

                    // id?????????????????????????????????
                    itemInfoDTO.setId(u9Prs.getId().toString()); // (pr???id)
                    itemInfoDTO.setItemCode(u9Prs.getItemCode()); // (??????)
                    itemInfoDTO.setItemName(u9Prs.getItemName()); // (??????)

                    // ?????? ?????? ?????????
                    for (ItemInfoEntityOfXLJ iteminfo : itemInfoEntitiesFromDataBase) {

                        // ????????? ??? ???????????? ????????????
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
                        // ??????????????????
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
                        // ??????????????? ??? (???????????????) ???(???????????????)???(????????????)
                        iteminfo.setItemCode(u9Prs.getItemCode()); // (??????)
                        iteminfo.setItemName(u9Prs.getItemName()); // (??????)
                        iteminfo.setMaterial(splitItemInfo.getMaterial()); // (??????)
                        iteminfo.setOuterSizeOld(outerSizeOld); // (???????????? - ???)
                        iteminfo.setInnerSizeOld(innerSizeOld); // (????????????  - ???)
                        iteminfo.setHeightSizeOld(heightSizeOld); // (???????????? - ???)
                        iteminfo.setJcMinutes(JC.toString()); // JC??????
                        iteminfo.setCncMinutes(CNC.toString()); // CNC??????
                        iteminfo.setJcPrice(JCPrice.toString()); // JC????????????
                        iteminfo.setCncPrice(CNCPrice.toString()); // CNC????????????
                        iteminfo.setPreMinutes(PRE.toString()); // ????????????
                        // ????????? ???????????????+???????????????/60*??????????????????+CNC??????/60*CNC????????????
                        BigDecimal param5 = (new BigDecimal(iteminfo.getJcMinutes()).add(new BigDecimal(iteminfo.getPreMinutes()))).multiply(new BigDecimal(iteminfo.getJcPrice())).divide(new BigDecimal("60"), 2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal param6 = new BigDecimal(iteminfo.getCncMinutes()).multiply(new BigDecimal(iteminfo.getCncPrice())).divide(new BigDecimal("60"), 2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal workPrice = param5.add(param6);
                        iteminfo.setProcessCost(workPrice.toString()); // (?????????)

                        if (!isDJ && !isJXB) {  // ???????????????????????? ?????? ?????? ??? ?????? ????????? ?????????????????????????????? @@@@@111111@@@@  [?????? ???????????????????????????]
                            String supCode = iteminfo.getSupCode();

                            // ???????????? ??? ??????  ??? ?????? ??? ??????
                            String materialType = iteminfo.getMaterialType();
                            // ???????????? (????????????)
                            String outerSizeRule = iteminfo.getOuterSize();
                            iteminfo.setOuterRes(outerSizeRule);
                            // ???????????? (????????????)
                            String innerSizeRule = iteminfo.getInnerSize();
                            iteminfo.setInnerRes(innerSizeRule);
                            // ???????????? (????????????)
                            String heightSizeRule = iteminfo.getHeightSize();
                            iteminfo.setHeightRes(heightSizeRule);

                            // ????????? ???????????? ???????????????
                            String outSize = this.handleXLJOuterSize(splitItemInfo.getOuterSize(), outerSizeRule);
                            String innerSize = "0";
                            if (!materialType.equals("??????")) { // ??????????????????????????????0
                                if (materialType.equals("??????")) {
                                    innerSize = new BigDecimal(splitItemInfo.getInnerSize()).add(new BigDecimal(innerSizeRule)).toString();   // ????????? ????????? +
                                } else {
                                    innerSize = new BigDecimal(splitItemInfo.getInnerSize()).subtract(new BigDecimal(innerSizeRule)).toString(); // ??????????????? ??? -
                                }
                            }
                            String heightSize = new BigDecimal(splitItemInfo.getHeightSize()).add(new BigDecimal(heightSizeRule)).toString();

                            boolean innerFlag = false;
                            if (materialType.equals("??????")) { // ????????? ?????????????????? ????????????????????? ???????????????????????? ???????????????
                                List<String> outSizeNewList = this.baseMapper.selectNewOuterSize(iteminfo.getSupCode(), outSize);
                                if (outSizeNewList.size() > 0) {
                                    for (String outSizeNew : outSizeNewList) {
                                        outSize = outSizeNew;
                                        String innerSizeNew = this.baseMapper.selectNewInnerSize(iteminfo.getSupCode(), outSize, innerSize);
                                        if (innerSizeNew != null) {
                                            innerSize = innerSizeNew;
                                            innerFlag = true;
                                            break; // ???????????????????????????
                                        } else {
                                            innerFlag = false; // ??????????????????????????????????????????????????????????????????
                                        }
                                    }
                                } else {
                                    innerFlag = false; // ???????????????????????????????????????????????????
                                }
                            }

                            if (materialType.equals("??????") && !innerFlag) {
                                // ?????????????????????????????????,?????????????????????   @@@@@222222@@@@
                                splitItemInfo = handleOfDJOfXLJItem(splitItemInfo);

                                outSize = splitItemInfo.getOuterSize();
                                innerSize = splitItemInfo.getInnerSize();
                                heightSize = splitItemInfo.getHeightSize();

                                iteminfo.setOuterRes(splitItemInfo.getOuterRes());
                                iteminfo.setInnerRes(splitItemInfo.getInnerRes());
                                iteminfo.setHeightRes(splitItemInfo.getHeightRes());
                                isDJ = true;
                            }

                            iteminfo.setOuterSize(outSize); // (??????)[?????????]
                            iteminfo.setInnerSize(innerSize); // (??????)[?????????]
                            iteminfo.setHeightSize(heightSize); // (??????)[?????????]
                        }

                        String density = this.baseMapper.getDensity(iteminfo);
                        if (density == null && !isDJ) { // ?????????????????? ??? ????????? ?????????????????? ???????????????????????????????????? ????????? ??????
                            itemInfoEntityOfXLJSToInsert.add(iteminfo);
                            continue;
                        }

                        if (splitItemInfo.getMaterialType().equals("??????") && isJXB) {
                            iteminfo.setMaterialType(splitItemInfo.getMaterialType());  // (????????????)[?????????]
                            iteminfo.setOuterSize(splitItemInfo.getOuterSize()); // (??????)[?????????]
                            iteminfo.setInnerSize(splitItemInfo.getInnerSize()); // (??????)[?????????]
                            iteminfo.setHeightSize(splitItemInfo.getHeightSize()); // (??????)[?????????]
                            iteminfo.setR(splitItemInfo.getR());
                            iteminfo.setX(splitItemInfo.getX());
                        }

                        ItemInfoEntityOfXLJ priceAndK = this.baseMapper.selectItemPriceOfXLJ(iteminfo);
                        if (priceAndK == null && !isDJ) { // ?????????????????? ??? ????????? ?????????K ???????????? ???????????????????????????????????? ????????? ??????
                            itemInfoEntityOfXLJSToInsert.add(iteminfo);
                            continue;
                        }

                        PriceVO priceVO = new PriceVO();
                        // ???????????????????????????????????????
                        priceVO.setSupName(iteminfo.getSupName());
                        priceVO.setSupCode(iteminfo.getSupCode());
                        priceVO.setPriority(iteminfo.getPriority());

                        // ??????????????????
                        if (priceAndK != null && !isDJ) {
                            BigDecimal materialPrice = new BigDecimal("0");
                            if (!isDJ) { // ?????????????????????

                                // ??? ???????????? ??? K
                                singleCost = priceAndK.getSingleCost();
                                String k = priceAndK.getK();
                                iteminfo.setK(k);  // ?????????K??????
                                iteminfo.setSingleCost(singleCost); // (????????????)

                                if (!splitItemInfo.getMaterialType().equals("??????")) {
                                    // ?????????  (D1^2-d1^2)*??/4*L1*??????/1000000*????????????*??????K???
                                    BigDecimal pai = new BigDecimal("3.14"); // ??
                                    BigDecimal param1 = new BigDecimal(iteminfo.getOuterSize()).multiply(new BigDecimal(iteminfo.getOuterSize())); // D1^2
                                    BigDecimal param2 = new BigDecimal(iteminfo.getInnerSize()).multiply(new BigDecimal(iteminfo.getInnerSize())); // d1^2
                                    BigDecimal param3 = new BigDecimal(iteminfo.getHeightSize()); // L1
                                    BigDecimal param4 = new BigDecimal(density).divide(new BigDecimal("1000000")).multiply(new BigDecimal(singleCost)).multiply(new BigDecimal(k)); // ??????/1000000*????????????*??????K???
                                    // ????????? ???????????????
                                    materialPrice = (param1.subtract(param2)).multiply(pai).divide(new BigDecimal("4")).multiply(param3).multiply(param4);
                                    // ????????????
                                    iteminfo.setWeight((param1.subtract(param2)).multiply(pai).divide(new BigDecimal("4")).multiply(param3).multiply(new BigDecimal(density).divide(new BigDecimal("1000000"))).setScale(2, RoundingMode.HALF_UP).toString());
                                } else {
                                    // ????????????????????????
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

                                    // ????????????
                                    iteminfo.setWeight(L1.multiply(W1).multiply(T1).multiply(new BigDecimal(density)).divide(new BigDecimal("1000000")).setScale(2, RoundingMode.HALF_UP).toString());
                                    // ????????? ???????????????
                                    materialPrice = new BigDecimal(iteminfo.getWeight()).multiply(new BigDecimal(singleCost)).multiply(new BigDecimal(k).setScale(2, RoundingMode.HALF_UP));
                                }
                            } else {
                                // ??? ?????? ????????????
                                materialPrice = new BigDecimal(splitItemInfo.getMaterialCost());
                                iteminfo.setWeight(splitItemInfo.getWeight());  // ????????????
                                iteminfo.setSingleCost(splitItemInfo.getSingleCost());  // (????????????)
                            }

                            // (?????????)
                            iteminfo.setMaterialCost(materialPrice.setScale(2, RoundingMode.HALF_UP).toString());
                            // ????????????
                            BigDecimal productPrice = materialPrice.add(workPrice).setScale(1, RoundingMode.HALF_UP);
                            iteminfo.setPrice(productPrice.toString()); // (????????????)
                            priceVO.setPrice(productPrice);
                        }

                        if (isDJ) {  // ??????????????????
                            iteminfo.setMaterialType("??????");

                            iteminfo.setWeight(splitItemInfo.getWeight());
                            iteminfo.setSingleCost(splitItemInfo.getSingleCost());
                            iteminfo.setOuterRes(splitItemInfo.getOuterRes());
                            iteminfo.setInnerRes(splitItemInfo.getInnerRes());
                            iteminfo.setHeightRes(splitItemInfo.getHeightRes());
                            iteminfo.setOuterSize(splitItemInfo.getOuterSize()); // (??????)[?????????]
                            iteminfo.setInnerSize(splitItemInfo.getInnerSize()); // (??????)[?????????]
                            iteminfo.setHeightSize(splitItemInfo.getHeightSize()); // (??????)[?????????]
                            if (splitItemInfo.getMaterialCost() != null) {
                                // ????????????
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

                    // ?????????????????????????????????????????????supType?????? 1
                    if ((itemInfoEntitiesFromDataBase != null) && (itemInfoEntitiesFromDataBase.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                        itemInfoDTO.setSupColorType("1");
                    } else {
                        itemInfoDTO.setSupColorType("0");
                    }
                    // ???????????????
                    if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                        itemInfoDTOS.add(itemInfoDTO);
                    }
                }
            }
            // ??????xlj??????
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
     * ????????????????????????????????????
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetriveOfLZQ(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        List<ItemInfoEntityOfLZQ> itemInfoEntityOfLZQToInsert = new ArrayList<>();

        // ?????????????????? ????????? ???????????????
        for (U9PrDTO u9Prs : u9PrDTOS) {

            boolean isDJ = false;
            boolean isJXB = false;

            // ????????? ????????????????????????
            if (("?????????".indexOf(u9Prs.getItemName().split("-")[0]) < 0 )) {
                continue;
            }

            // ?????????????????????
            ItemInfoEntityOfLZQ splitItemInfo = this.baseMapper.selectNotSplitLZQItemInfo(u9Prs.getItemCode());
            if (splitItemInfo == null) {
                continue;
            }
            String[] itemNameList = u9Prs.getItemName().split("-");
            int length = itemNameList.length;
            String mainType = itemNameList[0];
            String material = "";

            //????????????????????????????????????
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
                log.info("??????????????????");

            }

            String outerSizeOld = "";
            String innerSizeOld = "";
            String heightSizeOld = "";
            String xqgSizeOld = "";
            String cbkSizeOld = "";
            String cbzxjSizeOld = "";
            String xqgkSizeOld = "";

            // ???????????????
            if (splitItemInfo != null) {

                outerSizeOld = splitItemInfo.getOuterSize();
                innerSizeOld = splitItemInfo.getInnerSize();
                heightSizeOld = splitItemInfo.getHeightSize();
                xqgSizeOld = splitItemInfo.getXqgSize();
                cbkSizeOld = splitItemInfo.getCbkSize();
                cbzxjSizeOld = splitItemInfo.getCbzxjSize();
                xqgkSizeOld = splitItemInfo.getXqgkSize();



                // ???????????? ????????????
                List<ItemInfoEntityOfLZQ> itemInfoEntitiesFromDataBase = this.baseMapper.selectLZQResItemInfo(splitItemInfo);



                String singleCost = "";

                // ????????????????????????
                if (itemInfoEntitiesFromDataBase.size() > 0) {
                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO(); // ??????????????????
                    List<PriceVO> priceVOS = new ArrayList<>();

                    // id?????????????????????????????????
                    itemInfoDTO.setId(u9Prs.getId().toString()); // (pr???id)
                    itemInfoDTO.setItemCode(u9Prs.getItemCode()); // (??????)
                    itemInfoDTO.setItemName(u9Prs.getItemName()); // (??????)

                    // ?????? ?????? ?????????
                    for (ItemInfoEntityOfLZQ iteminfo : itemInfoEntitiesFromDataBase) {

                        iteminfo.setPrCode(u9Prs.getPrCode());
                        iteminfo.setPrLn(String.valueOf(u9Prs.getPrLn()));
                        iteminfo.setQuantity(String.valueOf(u9Prs.getPriceNum()));
                        iteminfo.setXqgSize(xqgSizeOld);
                        iteminfo.setCbkSize(cbkSizeOld);
                        iteminfo.setCbzxjSize(cbzxjSizeOld);
                        iteminfo.setXqgkSize(xqgkSizeOld);


                        // ????????? ??? ???????????? ????????????
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
                        // ??????????????????
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
                        // ??????????????? ??? (???????????????) ???(???????????????)???(????????????)
                        iteminfo.setItemCode(u9Prs.getItemCode()); // (??????)
                        iteminfo.setItemName(u9Prs.getItemName()); // (??????)
                        iteminfo.setMaterial(material); // (??????)
                        iteminfo.setOuterSizeOld(outerSizeOld); // (???????????? - ???)
                        iteminfo.setInnerSizeOld(innerSizeOld); // (????????????  - ???)
                        iteminfo.setHeightSizeOld(heightSizeOld); // (???????????? - ???)
                        iteminfo.setJcMinutes(JC.toString()); // JC??????
                        iteminfo.setCncMinutes(CNC.toString()); // CNC??????
                        iteminfo.setJcPrice(JCPrice.toString()); // JC????????????
                        iteminfo.setCncPrice(CNCPrice.toString()); // CNC????????????
                        iteminfo.setPreMinutes(PRE.toString()); // ????????????
                        // ????????? ???????????????+???????????????/60*??????????????????+CNC??????/60*CNC????????????*??????K??? 20221128
                        BigDecimal param5 = (new BigDecimal(iteminfo.getJcMinutes()).add(new BigDecimal(iteminfo.getPreMinutes()))).multiply(new BigDecimal(iteminfo.getJcPrice())).divide(new BigDecimal("60"), 2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal param6 = new BigDecimal(iteminfo.getCncMinutes()).multiply(new BigDecimal(iteminfo.getCncPrice())).multiply(new BigDecimal(CNCK)).divide(new BigDecimal("60"), 2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal workPrice = param5.add(param6);
                        iteminfo.setProcessCost(workPrice.toString()); // (?????????)



                        PriceVO priceVO = new PriceVO();
                        // ???????????????????????????????????????
                        priceVO.setSupName(iteminfo.getSupName());
                        priceVO.setSupCode(iteminfo.getSupCode());
                        priceVO.setPriority(iteminfo.getPriority());


                        // ???????????? (????????????)
                        String outerSizeRule = iteminfo.getOuterSize();
                        iteminfo.setOuterRes(outerSizeRule);
                        // ???????????? (????????????)
                        String innerSizeRule = iteminfo.getInnerSize();
                        iteminfo.setInnerRes(innerSizeRule);
                        // ???????????? (????????????)
                        String heightSizeRule = iteminfo.getHeightSize();
                        iteminfo.setHeightRes(heightSizeRule);

                        String outSize = this.handleXLJOuterSize(splitItemInfo.getOuterSize(), outerSizeRule);//???????????????
                        String innerSize =  new BigDecimal(splitItemInfo.getInnerSize()).add(new BigDecimal(innerSizeRule)).toString();//???????????????
                        String heightSize = new BigDecimal(splitItemInfo.getHeightSize()).add(new BigDecimal(heightSizeRule)).toString();//???????????????

                        iteminfo.setOuterSize(outSize);
                        iteminfo.setInnerSize(innerSize);
                        iteminfo.setHeightSize(heightSize);

                        ItemInfoEntityOfXLJ xljinfo =new ItemInfoEntityOfXLJ();
                        BeanUtil.copy(iteminfo,xljinfo);
                        String densityVo=this.baseMapper.getDensity(xljinfo);
                        String density = StringUtils.isBlank(densityVo)? "0":densityVo;//????????????


                        ItemInfoEntityOfLZQ priceAndK = this.baseMapper.selectItemPriceOfLZQ(iteminfo);//????????????
                        // ??????????????????
                        if (priceAndK != null ) {
                            BigDecimal materialPrice = new BigDecimal("0");

                            // ??? ???????????? ??? K
                            singleCost = priceAndK.getSingleCost();
                            String k = priceAndK.getK();
                            iteminfo.setK(k);  // ?????????K??????
                            iteminfo.setSingleCost(singleCost); // (????????????)

                            // ?????????  D1^2*??/4*L1*7.85/1000000???????????????????????????????????? 20221128
                            BigDecimal pai = new BigDecimal("3.14"); // ??
                            BigDecimal param1 = new BigDecimal(iteminfo.getOuterSize()).multiply(new BigDecimal(iteminfo.getOuterSize())); // D1^2
                            BigDecimal param3 = new BigDecimal(iteminfo.getHeightSize()); // L1
                            //????????????
                            BigDecimal weight= param1.multiply(pai).divide(new BigDecimal("4")).multiply(param3).multiply(new BigDecimal(density)).divide(new BigDecimal("1000000"));
                            // ????????? ???????????????*?????????????????????
                            materialPrice = weight.multiply(new BigDecimal(singleCost));
                            // ????????????
                            iteminfo.setWeight(weight.setScale(2, RoundingMode.HALF_UP).toString());
                            // (?????????)
                            iteminfo.setMaterialCost(materialPrice.setScale(2, RoundingMode.HALF_UP).toString());

                            String HJF="0";
                            String XQG="0";


                            String[] ItemNameSplit = iteminfo.getItemName().split("-");
                            //?????? ????????????  ???  ?????????
                            if (ItemNameSplit[1].startsWith("KZ2A")||ItemNameSplit[1].startsWith("KXQ")){
                                //?????????
                                XQG=getXQG(iteminfo,splitItemInfo);//???????????????

                                iteminfo.setXqgCost(XQG);

                            } else if (ItemNameSplit[1].startsWith("KZ1A")||ItemNameSplit[1].startsWith("KK")||ItemNameSplit[1].startsWith("KHJ")||
                                ItemNameSplit[1].startsWith("KCJ")||ItemNameSplit[1].startsWith("HJ")||ItemNameSplit[1].startsWith("Fk")||ItemNameSplit[1].startsWith("FZ")) {
                                //??????????????????????????????
                                HJF=getHJF(iteminfo,splitItemInfo);//???????????????
                                XQG=getXQG(iteminfo,splitItemInfo);//???????????????

                                iteminfo.setXqgCost(XQG);
                                iteminfo.setHjfCost(HJF);

                            }


                            // ????????????=?????????+?????????+????????????+????????? 20221128
                            BigDecimal productPrice = materialPrice.add(workPrice).add(new BigDecimal(HJF)).add(new BigDecimal(XQG)).setScale(1, RoundingMode.HALF_UP);
                            iteminfo.setPrice(productPrice.toString()); // (????????????)
                            iteminfo.setTotalCost(productPrice.multiply(new BigDecimal(iteminfo.getQuantity())).toString());
                            priceVO.setPrice(productPrice);
                        }
                        if (priceVO.getPrice() != null) {
                            priceVOS.add(priceVO);
                        }
                        itemInfoDTO.setSupAndPriceList(priceVOS);
                        itemInfoEntityOfLZQToInsert.add(iteminfo);
                    }

                    // ?????????????????????????????????????????????supType?????? 1
                    if ((itemInfoEntitiesFromDataBase != null) && (itemInfoEntitiesFromDataBase.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                        itemInfoDTO.setSupColorType("1");
                    } else {
                        itemInfoDTO.setSupColorType("0");
                    }
                    // ???????????????
                    if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                        itemInfoDTOS.add(itemInfoDTO);
                    }
                }
            }
            // ??????xlj??????   ??????????????????????????????20221125
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
        String xqgSingleCost = this.baseMapper.selectItemXQGPriceOfLZQ(iteminfo);//???????????????
        //???????????? ???????????? (?????????*2+(???????????????-??????/2)*4)*???????????????
        BigDecimal pram1=new BigDecimal(splitItemInfo.getCbzxjSize()).subtract(new BigDecimal(splitItemInfo.getInnerSize()).divide(new BigDecimal("2")));//???????????????-??????/2
        BigDecimal pram2=(new BigDecimal(splitItemInfo.getCbkSize()).multiply(new BigDecimal("2")).add(pram1.multiply(new BigDecimal("4")))).multiply(new BigDecimal(splitItemInfo.getXqgSize()));

        //???????????? ???????????????*???????????????
        BigDecimal xqgcost=pram2.multiply(new BigDecimal(xqgSingleCost));

        BigDecimal xqgk=new BigDecimal(splitItemInfo.getCbkSize()).multiply(new BigDecimal("2")).add(pram1.multiply(new BigDecimal("4")));
        iteminfo.setXqgkSize(xqgk.toString());

        return xqgcost.toString();
    }

    private String getHJF(ItemInfoEntityOfLZQ iteminfo,ItemInfoEntityOfLZQ splitItemInfo) {
        ItemInfoEntityOfLZQ itemInfoEntityOfLZQ = this.baseMapper.selectItemHJFPriceOfLZQ(iteminfo);//?????????
        String hjfSingleCost=itemInfoEntityOfLZQ.getSingleCost();//???????????????
        return hjfSingleCost;

    }

    private List<WorkTime> getWorkTime(String itemCode) {
        List<WorkTime> workTimes = new ArrayList<>();
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;
        try {
            Class.forName(driver);
            //1.????????????
            @Cleanup Connection conn = DriverManager.getConnection(url, user, password);
            @Cleanup Statement stmt = conn.createStatement();
            //2.??????????????????sql
            StringBuffer sql = new StringBuffer();
            sql.append("select * from atwmos.v_bzgs where ???????????? =  '").append(itemCode).append("'");

            //3.??????
            @Cleanup ResultSet resultSet = stmt.executeQuery(sql.toString());

            while (resultSet.next()) {
                String workCode = resultSet.getString("????????????");
                String pipTime = resultSet.getString("????????????");
                String preTime = resultSet.getString("????????????");
                WorkTime workTime = new WorkTime();
                workTime.setItemCode(itemCode);
                workTime.setWorkCode(workCode);
                workTime.setPipTime(pipTime);
                workTime.setPreTime(preTime);
                workTimes.add(workTime);
            }
        } catch (Exception e) {
            throw new RuntimeException("??????oracle??????????????????");
        } finally {
            return workTimes;
        }
    }

    private String handleXLJOuterSize(String outerSize, String outerSizeRule) {
        if (outerSizeRule.indexOf("???") > -1) {
            outerSizeRule = outerSizeRule.substring(1, outerSizeRule.length());
            String addNum = outerSizeRule.split("?????????")[0];
            Integer wholeNum = Integer.valueOf(outerSizeRule.split("?????????")[1]);
            // ??????
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
        System.out.println("*************** ???????????? ?????????????????? ***************");
        System.out.println("*************** ???????????? ?????????????????? ?????????????????? ????????????????????? ***************");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar calendar = new GregorianCalendar();
        String endTime = sdf.format(calendar.getTime());
        calendar.add(calendar.DATE, -2);//???????????????????????????.???????????????,??????????????????
        String time = sdf.format(calendar.getTime());
        List<U9PrDTO> u9PrDTOS = this.baseMapper.getOrderInfo(time);

        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        // ??????
        List<ItemInfoDTO> itemInfoDTOSOfZhuJian = autoRetrieveOfZhuJian(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfZhuJian);
        // ??????
        List<ItemInfoDTO> itemInfoDTOSOfDuanJian = autoRetrieveOfDuanJian(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfDuanJian);
        // ??????
        List<ItemInfoDTO> itemInfoDTOSOfQiuZuo = autoRetrieveOfQiuZuo(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfQiuZuo);
        // ????????????
        List<ItemInfoDTO> itemInfoDTOSOfWW = autoRetrieveOfWW(u9PrDTOS);
        itemInfoDTOS.addAll(itemInfoDTOSOfWW);

        itemInfoDTOS.forEach(item -> {
            if (this.baseMapper.autoItemIsExisted(item.getItemCode()) == 0) {
                this.baseMapper.insertAutoItem(item.getItemCode(), item.getItemName());
            }
        });

        System.out.println("*************** ???????????? ?????????????????? ***************");
    }

    /**
     * ?????????????????????????????????
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfQiuZuo(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        // ?????????????????? ?????? ???????????????
        for (U9PrDTO u9Prs : u9PrDTOS) {

            // ??????????????????????????????
            if (u9Prs.getItemName().indexOf("??????") < 0) {
                continue;
            }

            // ??? ??????1249???????????????&&pr???????????????
            if (u9Prs.getItemCode().substring(0, 4).equals("1249") && u9Prs.getBizType().toString().equals("1")) {
                continue;
            }

            // ??????
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(u9Prs.getItemName());

            // ???????????????
            if (itemInfoEntity.getItemize() != null) {

                // ?????????????????????????????????????????????
                List<ItemInfoEntityOfQZ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfQZ(itemInfoEntity);

                String price = "";
                String ptPrice = "";
                String range = "";

                // ????????????????????????
                if (itemInfoEntitiesFromDataBase.size() > 0) {

                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
                    List<PriceVO> priceVOS = new ArrayList<>();

                    // id?????????????????????????????????
                    itemInfoDTO.setId(u9Prs.getId().toString());
                    itemInfoDTO.setItemCode(u9Prs.getItemCode());
                    itemInfoDTO.setItemName(u9Prs.getItemName());

                    // ??????????????????????????????
                    for (ItemInfoEntityOfQZ iteminfo : itemInfoEntitiesFromDataBase) {
                        // ???????????????
                        String qzCharge = iteminfo.getQzCharge();
                        if (StringUtils.isEmpty(qzCharge)) {
                            qzCharge = "0";
                        }
                        // ???????????????
                        String fzCharge = iteminfo.getFzCharge();
                        if (StringUtils.isEmpty(fzCharge)) {
                            fzCharge = "0";
                        }

                        price = this.baseMapper.selectItemPriceOfQZ(iteminfo);

                        if (Double.valueOf(iteminfo.getSize()) >= 0.5 && Double.valueOf(iteminfo.getSize()) <= 10) {
                            range = "0.5????????????10???";
                        } else {
                            range = "?????????12???";
                        }
                        ptPrice = this.baseMapper.selectPtPriceOfQZ(iteminfo, range);

                        // ??????????????????
                        if (StringUtils.isNotEmpty(price) && StringUtils.isNotEmpty(ptPrice)) {
                            PriceVO priceVO = new PriceVO();
                            // ???????????????????????????????????????
                            priceVO.setSupName(iteminfo.getSupName());
                            priceVO.setSupCode(iteminfo.getSupCode());
                            priceVO.setPriority(iteminfo.getPriority());

                            // ???????????? ??????2????????? ???????????????*??????+????????????*????????????+??????????????????+ ???????????????*??????+?????????+??????????????????* 2
                            BigDecimal qzPrice = new BigDecimal(iteminfo.getQzWeight().trim()).multiply(new BigDecimal(price.trim())).add(new BigDecimal(qzCharge.trim())).add(new BigDecimal(iteminfo.getQzSprayArea().trim()).multiply(new BigDecimal(ptPrice.trim())));
                            BigDecimal fzPrice = new BigDecimal(iteminfo.getFzWeight().trim()).multiply(new BigDecimal(price.trim())).add(new BigDecimal(fzCharge.trim())).add(new BigDecimal(iteminfo.getFzSprayCharge().trim()));
                            BigDecimal priceToReturn = qzPrice.add(fzPrice.multiply(new BigDecimal("2"))).setScale(0, RoundingMode.HALF_UP);
                            priceVO.setPrice(priceToReturn);

                            priceVOS.add(priceVO);
                            itemInfoDTO.setSupAndPriceList(priceVOS);
                        }
                    }
                    // ?????????????????????????????????????????????supType?????? 1
                    if ((itemInfoEntitiesFromDataBase != null) && (itemInfoEntitiesFromDataBase.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                        itemInfoDTO.setSupColorType("1");
                    } else {
                        itemInfoDTO.setSupColorType("0");
                    }

                    // ??????
                    if (itemInfoDTO.getSupAndPriceList().size() > 0) {
                        itemInfoDTOS.add(itemInfoDTO);
                    }
                }
            }
        }
        return itemInfoDTOS;
    }

    /**
     * ?????????????????????????????????  20221206
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfQiuZuoNew(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        List<ItemInfoEntityOfQZNew> itemInfoEntityOfLZQToInsert = new ArrayList<>();

        // ?????????????????? ?????? ???????????????
        for (U9PrDTO u9Prs : u9PrDTOS) {
            List<PriceVO> priceVOS = new ArrayList<>();

            // ??????????????????????????????
            if (u9Prs.getItemName().indexOf("??????") < 0 && u9Prs.getItemName().indexOf("??????") < 0) {
                continue;
            }

            if(u9Prs.getItemName().indexOf("GP") > 0 || u9Prs.getItemName().indexOf("LT") > 0){
                continue;
            }

            // ??????
            ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(u9Prs.getItemName());

            ItemInfoEntityOfQZNew itemInfoEntityOfQZNew=new ItemInfoEntityOfQZNew();
            BeanUtil.copy(itemInfoEntity,itemInfoEntityOfQZNew);

            // ???????????????
            if (itemInfoEntity.getItemize() != null) {
                //??????????????????
                List<ItemInfoEntityOfQZNew> itemInfoEntityOfQZNews = this.baseMapper.selectBasicInfoOfQZ(itemInfoEntityOfQZNew);

                ItemInfoDTO itemInfoDTO=new ItemInfoDTO();//??????pr???  ????????????
                itemInfoDTO.setId(u9Prs.getId().toString());
                itemInfoDTO.setItemCode(u9Prs.getItemCode());
                itemInfoDTO.setItemName(u9Prs.getItemName());

                for (ItemInfoEntityOfQZNew item :itemInfoEntityOfQZNews) {
                    item.setItemName(u9Prs.getItemName());
                    item.setItemCode(u9Prs.getItemCode());
                    item.setPrCode(u9Prs.getPrCode());
                    item.setPrLn(String.valueOf(u9Prs.getPrLn()));
                    item.setQuantity(String.valueOf(u9Prs.getPriceNum()));


                    String qzWeight = item.getQzWeight();//????????????
                    String qzSprayArea = item.getQzSprayArea();//??????????????????
                    String qzCharge = item.getQzCharge();//???????????????
                    String material = item.getMaterial();//??????
                    String size = item.getSize();//??????
                    String k = item.getK();//??????K

                    BigDecimal fz_cost=new BigDecimal("0");
                    BigDecimal qt_cost=new BigDecimal("0");
                    BigDecimal spray_cost=new BigDecimal("0");

                    //???????????? ????????????=(??????????????????(????????????)+??????????????????+???????????????)*??????
                    if(u9Prs.getItemName().indexOf("??????")>-1){
                        //??????????????????(????????????)=????????????*????????????
                        ItemInfoOfQZVO itemInfoOfQZVO = this.baseMapper.selectMaterialPriceOfQZ(item);
                        String qtPrice = itemInfoOfQZVO.getQtPrice();
                        BigDecimal material_cost=new BigDecimal(qzWeight).multiply(new BigDecimal(qtPrice));

                        item.setQtMaterialPrice(qtPrice);
                        item.setQzMaterialCost(String.valueOf(material_cost));

                        //??????????????????=????????????*????????????
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

                    //???????????? ????????????=(????????????*????????????+??????????????????+???????????????+???????????????)*??????
                    if(u9Prs.getItemName().indexOf("??????")>-1){
                        //????????????*????????????
                        String fzWeight = item.getFzWeight();//????????????
                        ItemInfoOfQZVO itemInfoOfQZVO = this.baseMapper.selectMaterialPriceOfQZ(item);
                        String fzPrice = itemInfoOfQZVO.getFzPrice();
                        BigDecimal material_cost=new BigDecimal(fzWeight).multiply(new BigDecimal(fzPrice));

                        item.setFzMaterialCost(String.valueOf(material_cost));

                        //????????? ??????????????????
                        //????????? ???????????????
                        //????????? ???????????????

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

                // ?????????????????????????????????????????????supType?????? 1
                if ((itemInfoEntityOfQZNews != null) && (itemInfoEntityOfQZNews.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                    itemInfoDTO.setSupColorType("1");
                } else {
                    itemInfoDTO.setSupColorType("0");
                }
                // ???????????????
                if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                    itemInfoDTOS.add(itemInfoDTO);
                }
            }
            // ????????????
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
     * ?????????????????????????????????
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfZhuJian(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        // ?????????????????? ???????????? ???????????????
        for (U9PrDTO u9Prs : u9PrDTOS) {

            // ??????????????????????????????
            if (u9Prs.getItemName().indexOf("??????") < 0) {
                continue;
            }

            // ??????
            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(u9Prs.getItemName());

            // ???????????????
            if (itemInfoEntity.getSeries() != null) {

                // ?????????????????????????????????????????????
                List<ItemInfoEntityOfZDJ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfZDJ(itemInfoEntity);

                String price = "";

                // ????????????????????????
                if (itemInfoEntitiesFromDataBase.size() > 0) {

                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
                    List<PriceVO> priceVOS = new ArrayList<>();

                    // id?????????????????????????????????
                    itemInfoDTO.setId(u9Prs.getId().toString());
                    itemInfoDTO.setItemCode(u9Prs.getItemCode());
                    itemInfoDTO.setItemName(u9Prs.getItemName());

                    // ??????????????????????????????
                    for (ItemInfoEntityOfZDJ iteminfo : itemInfoEntitiesFromDataBase) {
                        String extraPrice = iteminfo.getCharge();
                        if (StringUtils.isEmpty(extraPrice)) {
                            extraPrice = "0";
                        }
                        // ??????????????? ?????? ???????????????????????????????????????????????????
                        iteminfo.setMaterial(itemInfoEntity.getMaterial());

                        price = this.baseMapper.selectItemPriceOfZDJ(iteminfo);
                        // ??????????????????
                        if (StringUtils.isNotEmpty(price)) {

                            PriceVO priceVO = new PriceVO();
                            // ???????????????????????????????????????
                            priceVO.setSupName(iteminfo.getSupName());
                            priceVO.setSupCode(iteminfo.getSupCode());
                            priceVO.setPriority(iteminfo.getPriority());
                            // ???????????? ??????2????????? ??????*??????+?????????
                            BigDecimal priceToReturn = (new BigDecimal(price).multiply(new BigDecimal(iteminfo.getWeight())).add(new BigDecimal(extraPrice))).setScale(2, RoundingMode.HALF_UP);

                            priceVO.setPrice(priceToReturn);
                            priceVOS.add(priceVO);
                            itemInfoDTO.setSupAndPriceList(priceVOS);
                        }
                    }

                    // ?????????????????????????????????????????????supType?????? 1
                    if ((itemInfoEntitiesFromDataBase != null) && (itemInfoEntitiesFromDataBase.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                        itemInfoDTO.setSupColorType("1");
                    } else {
                        itemInfoDTO.setSupColorType("0");
                    }
                    // ???????????????
                    if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                        itemInfoDTOS.add(itemInfoDTO);
                    }
                }
            }
        }
        return itemInfoDTOS;
    }

    /**
     * ???????????????????????????
     *
     * @param u9PrDTOS
     * @return ???????????? itemcode
     */
    private List<ItemInfoDTO> authRetriveOfGuanBangLiao(List<U9PrDTO> u9PrDTOS) {
        ArrayList<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        System.out.println("*************** ???????????? ?????????????????? ?????????????????? ????????????????????? ***************");
        MaterialMaliyVO maliyVO = new MaterialMaliyVO();
        //??????????????????????????? ???????????????
        for (U9PrDTO u9Prs : u9PrDTOS) {
            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
            //??????????????????,???contiune ??????
            if (u9Prs.getItemName().indexOf("??????") < 0 || u9Prs.getItemName().indexOf("??????") < 0 || u9Prs.getItemName().indexOf("??????") < 0) {
                if (u9Prs.getItemName().split("\\+").length < 2 || u9Prs.getItemName().split("\\+")[1].indexOf("G") < 0) {
                    continue;
                }
            }
            //??????????????????
            TubeMaterialInfoEntity infoEntity = new TubeMaterialInfoEntity();
            infoEntity.setItemDesc(u9Prs.getItemName());
            infoEntity.setItemCode(u9Prs.getItemCode());
            infoEntity.setPriceNum(u9Prs.getPriceNum());
            infoEntity.setPrLn(u9Prs.getPrLn());
            infoEntity.setPrCode(u9Prs.getPrCode());

            // ??????????????????
            tubeMaterialInfoService.getInfoList(infoEntity);

            //??????????????? ?????????????????????
            maliyVO = getItemInfoGuanBangLiao(u9Prs.getItemName());

            //??????????????????
            if (maliyVO.getItemName() != null) {
                //?????????????????????????????????
                List<MaterialMaliyVO> mailyVOList = materialMailyVoMapper.selectSupName(maliyVO);
                //?????????????????????
                if (mailyVOList.size() > 0) {
                    List<PriceVO> priceVOS = new ArrayList<>();
                    for (MaterialMaliyVO materialMaliyVO : mailyVOList) {
                        maliyVO.setSupplierCode(materialMaliyVO.getSupplierCode());
                        maliyVO.setSupplierName(materialMaliyVO.getSupplierName());

                        //????????????
                        MaterialMaliyVO materialMaliyVO1 = materialMailyVoMapper.selectRes(materialMaliyVO.getSupplierCode(), maliyVO.getTheMaterial());

                        String outerSizeRes=materialMaliyVO1.getOuterSize();
                        String heightSizeRes=materialMaliyVO1.getHeightSize();
                        String innerSizeRes=materialMaliyVO1.getInnerSize();
                        Double Exter=maliyVO.getExternalDiameter()+ Double.valueOf(outerSizeRes)  ;//????????????
                        Double Inner=maliyVO.getInternalDiamete()- Double.valueOf(innerSizeRes);//????????????
                        Double Length=maliyVO.getLength()+ Double.valueOf(heightSizeRes);//????????????

                        //??????????????????????????????300MM
                        if (maliyVO.getExternalDiameter() <= 301) {
                            MaterialMaliyVO materialExternalDiameter = materialMailyVoMapper.selectExter(Exter);
                            //?????????????????????
                            MaterialMaliyVO material85 = null;
                            if (materialExternalDiameter != null) {
                                material85 = materialMailyVoMapper.selectInner(materialExternalDiameter.getExternalDiameter(), Inner);
                            }
                            if (material85 != null) {
                                //?????????
                                MaterialMaliyVO materialList = getMaterialPrice(maliyVO, material85.getExternalDiameter(), material85.getInternalDiamete(), maliyVO.getTheMaterial(), Length, maliyVO.getSupplierCode());
                                if (materialList == null) {
                                    log.warn("???????????????");
                                    continue;
                                }
                                maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
                                maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
                                maliyVO.setWeight(materialList.getWeight()); //??????

                                //?????????
                                MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
                                if (sprayList.equals(null)) {
                                    log.warn("???????????????");
                                    continue;
                                }
                                maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //????????????
                                maliyVO.setSprayPrice(sprayList.getSprayPrice());//?????????

                                // ?????????
                                MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                                if (proscessList == null) {
                                    log.info("???????????????");
                                    continue;
                                }
                                maliyVO.setProcessingFee(proscessList.getProcessingFee()); //?????????
                                maliyVO.setPrice(proscessList.getPrice()); //?????????

                                // ????????????   ????????????=?????????+?????????+?????????+?????????
                                BigDecimal productPrice = null;
                                if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                                    productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                                    maliyVO.setUnitPrice(productPrice);
                                }
                                maliyVO.setUnitPrice(productPrice);

                                // ?????????????????????????????????????????????supType?????? 1
                                if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                                    itemInfoDTO.setSupColorType("1");
                                } else {
                                    itemInfoDTO.setSupColorType("0");
                                }
                                // ???????????????
                                if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                                    itemInfoDTOS.add(itemInfoDTO);
                                }
                                if (maliyVO.getProcessingFee() != null && maliyVO.getSprayPrice() != null && maliyVO.getMaterialPrice() != null) {
                                    //?????????
                                    PriceVO priceVO = new PriceVO();
                                    priceVO.setPrice(productPrice); //??????
                                    priceVO.setSupName(maliyVO.getSupplierName());  //???????????????
                                    priceVO.setSupCode(maliyVO.getSupplierCode()); //???????????????
                                    priceVO.setSupKey("1"); //??????????????????
                                    priceVO.setType("??????"); //??????
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
                                //?????????
                                MaterialMaliyVO materialList = getMaterialPrice(maliyVO, exter, inner, maliyVO.getTheMaterial(), Length, maliyVO.getSupplierCode());
                                if (materialList == null) {
                                    log.info("???????????????");
                                    continue;
                                }
                                maliyVO.setSupplierName(materialList.getSupplierName());
                                maliyVO.setSupplierCode(materialList.getSupplierCode());
                                maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
                                maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
                                maliyVO.setWeight(materialList.getWeight()); //??????

                                //?????????
                                MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
                                if (sprayList.equals(null)) {
                                    log.info("???????????????");
                                    continue;
                                }
                                maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //????????????
                                maliyVO.setSprayPrice(sprayList.getSprayPrice());//?????????

                                //?????????
                                MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                                if (proscessList == null) {
                                    log.info("???????????????");
                                    continue;
                                }
                                maliyVO.setProcessingFee(proscessList.getProcessingFee()); //?????????
                                maliyVO.setPrice(proscessList.getPrice()); //?????????

                                //????????????   ????????????=?????????+?????????+?????????+?????????
                                BigDecimal productPrice = null;
                                if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                                    productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                                    maliyVO.setUnitPrice(productPrice);
                                }
                                maliyVO.setUnitPrice(productPrice);

                                // ?????????????????????????????????????????????supType?????? 1
                                if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                                    itemInfoDTO.setSupColorType("1");
                                } else {
                                    itemInfoDTO.setSupColorType("0");
                                }
                                // ???????????????
                                if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                                    itemInfoDTOS.add(itemInfoDTO);
                                }
                                if (maliyVO.getProcessingFee() != null && maliyVO.getSprayPrice() != null && maliyVO.getMaterialPrice() != null) {
                                    // ?????????
                                    priceVOS = new ArrayList<>();
                                    PriceVO priceVO = new PriceVO();
                                    priceVO.setPrice(productPrice); //??????
                                    priceVO.setSupName(maliyVO.getSupplierName());  //???????????????
                                    priceVO.setSupCode(maliyVO.getSupplierCode()); //???????????????
                                    priceVO.setSupKey("1"); //??????????????????
                                    priceVO.setType("??????"); //??????
                                    priceVO.setSingleWeight(String.valueOf(maliyVO.getWeight()));
                                    priceVO.setPriority("1");
                                    priceVOS.add(priceVO);
                                } else {
//                                    priceVOS.clear();
                                    continue;
                                }
                            }
                        }
                        // ????????????300mm
                        if (maliyVO.getExternalDiameter() > 301) {
                            //????????????  ??????+10 ?????? -10
                            Double exter = maliyVO.getExternalDiameter() + 10;
                            Double inner = maliyVO.getInternalDiamete() - 10;
                            //?????????
                            MaterialMaliyVO materialList = getMaterialPrice(maliyVO, exter, inner, maliyVO.getTheMaterial(), Length, maliyVO.getSupplierCode());
                            if (materialList == null) {
                                continue;
                            }
                            maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
                            maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
                            maliyVO.setWeight(materialList.getWeight()); //??????

                            //?????????
                            MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
                            if (sprayList.equals(null)) {
                                log.info("???????????????");
                                continue;
                            }
                            maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); // ????????????
                            maliyVO.setSprayPrice(sprayList.getSprayPrice());// ?????????
                            log.info("????????????" + maliyVO.getCoatingPrice());
                            log.info("?????????" + maliyVO.getSprayPrice());
                            //?????????
                            MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                            if (proscessList == null) {
                                log.info("???????????????");
                                continue;
                            }
                            maliyVO.setProcessingFee(proscessList.getProcessingFee()); // ?????????
                            maliyVO.setPrice(proscessList.getPrice()); // ?????????
                            log.info("?????????" + maliyVO.getProcessingFee());
                            log.info("?????????" + maliyVO.getPrice());

                            //????????????   ????????????=?????????+?????????+?????????+?????????
                            BigDecimal productPrice = null;
                            if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                                productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                                maliyVO.setUnitPrice(productPrice);
                            }
                            maliyVO.setUnitPrice(productPrice);

                            // ?????????????????????????????????????????????supType?????? 1
                            if ((mailyVOList != null) && (mailyVOList.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                                itemInfoDTO.setSupColorType("1");
                            } else {
                                itemInfoDTO.setSupColorType("0");
                            }
                            // ???????????????
                            if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                                itemInfoDTOS.add(itemInfoDTO);
                            }
                            if (maliyVO.getProcessingFee() != null && maliyVO.getSprayPrice() != null && maliyVO.getMaterialPrice() != null) {
                                priceVOS = new ArrayList<>();
                                PriceVO priceVO = new PriceVO();
                                priceVO.setPrice(productPrice); // ??????
                                priceVO.setSupName(maliyVO.getSupplierName());  // ???????????????
                                priceVO.setSupCode(maliyVO.getSupplierCode()); // ???????????????
                                priceVO.setSupKey("1"); // ??????????????????
                                priceVO.setType("??????"); // ??????
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
                log.info("id??????" + u9Prs.getId().toString());
                itemInfoDTO.setItemName(u9Prs.getItemName());
                itemInfoDTO.setItemCode(u9Prs.getItemCode());
                itemInfoDTOS.add(itemInfoDTO);
            }
        }
        return itemInfoDTOS;
    }


    /**
     * ?????????????????????
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<MaterialMaliyVO> getitemInfoGBL(String itemCode, String itemName) {
        List<MaterialMaliyVO> maliyVOList = new ArrayList<>();
        //?????????????????????
        MaterialMaliyVO maliyVO = getItemInfoGuanBangLiao(itemName);
        //?????????????????????
        if (maliyVO.getItemName() != null) {
            //???????????????
            List<MaterialMaliyVO> mailyVOS = materialMailyVoMapper.selectMaterilaList(maliyVO);
            if (mailyVOS.size() > 0) {
                for (MaterialMaliyVO mailyVO : mailyVOS) {

                    maliyVO.setSupplierCode(mailyVO.getSupplierCode());
                    maliyVO.setSupplierName(mailyVO.getSupplierName());
                    if (maliyVO.getExternalDiameter() < 300) {
                        MaterialMaliyVO materialExternalDiameter = materialMailyVoMapper.selectExter(maliyVO.getExternalDiameter());
                        //?????????????????????
                        MaterialMaliyVO material85 = materialMailyVoMapper.selectInner(materialExternalDiameter.getExternalDiameter(), maliyVO.getInternalDiamete());

                        if (material85 != null) {
                            //?????????
                            MaterialMaliyVO materialList = getMaterialPrice(maliyVO, material85.getExternalDiameter(), material85.getInternalDiamete(), maliyVO.getTheMaterial(), maliyVO.getLength(), maliyVO.getSupplierCode());
                            if (materialList == null) {
                                continue;
                            }
                            maliyVO.setSupplierName(materialList.getSupplierName());
                            maliyVO.setSupplierCode(materialList.getSupplierCode());
                            maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
                            maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
                            maliyVO.setWeight(materialList.getWeight()); //??????

                            //?????????
                            MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
                            if (sprayList.equals(null)) {
                                continue;
                            }
                            maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //????????????
                            maliyVO.setSprayPrice(sprayList.getSprayPrice());//?????????

                            //?????????
                            MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                            if (proscessList == null) {
                                continue;
                            }
                            maliyVO.setProcessingFee(proscessList.getProcessingFee()); //?????????
                            maliyVO.setPrice(proscessList.getPrice()); //?????????

                            //????????????   ????????????=?????????+?????????+?????????+?????????
                            BigDecimal productPrice = null;
                            if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                                productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                                maliyVO.setUnitPrice(productPrice);
                            }
                            maliyVO.setUnitPrice(productPrice);

                            MaterialMaliyVO vo = new MaterialMaliyVO();
                            if (maliyVO.getProcessingFee() != null && maliyVO.getSprayPrice() != null && maliyVO.getMaterialPrice() != null) {
                                vo.setSprayPrice(maliyVO.getSprayPrice()); // ?????????
                                vo.setPrice(maliyVO.getPrice());// ?????????
                                vo.setProcessingFee(maliyVO.getProcessingFee()); // ?????????
                                vo.setMaterialPrice(maliyVO.getMaterialPrice()); // ?????????
                                vo.setCoatingPrice(maliyVO.getCoatingPrice()); // ????????????
                                vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice());// ????????????
                                vo.setWeight(maliyVO.getWeight()); // ??????
                                vo.setUnitPrice(maliyVO.getUnitPrice()); // ????????????
                                vo.setSupplierCode(maliyVO.getSupplierCode()); // ???????????????
                                vo.setSupplierName(maliyVO.getSupplierName());  // ???????????????
                                vo.setCoating(maliyVO.getCoating());  // ??????
                                vo.setItemCode(itemCode); // ????????????
                                vo.setItemName(itemName); // ????????????
                                vo.setLength(maliyVO.getLength());  // ????????????
                                vo.setTheMaterial(maliyVO.getTheMaterial()); // ????????????
                                vo.setExternalDiameter(maliyVO.getExternalDiameter()); // ??????
                                vo.setInternalDiamete(maliyVO.getInternalDiamete()); // ??????
                                maliyVOList.add(vo);
                            } else {
                                continue;
                            }
                        } else {
                            Double exter = maliyVO.getExternalDiameter() + 10;
                            Double inner = maliyVO.getInternalDiamete() - 10;
                            //?????????
                            MaterialMaliyVO materialList = getMaterialPrice(maliyVO, exter, inner, maliyVO.getTheMaterial(), maliyVO.getLength(), maliyVO.getSupplierCode());
                            if (materialList == null) {
                                continue;
                            }
                            maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
                            maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
                            maliyVO.setWeight(materialList.getWeight()); //??????

                            //?????????
                            MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierName());
                            if (sprayList.equals(null)) {
                                continue;
                            }
                            maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //????????????
                            maliyVO.setSprayPrice(sprayList.getSprayPrice());//?????????

                            //?????????
                            MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                            if (proscessList == null) {
                                continue;
                            }
                            maliyVO.setProcessingFee(proscessList.getProcessingFee()); //?????????
                            maliyVO.setPrice(proscessList.getPrice()); //?????????

                            //????????????   ????????????=?????????+?????????+?????????+?????????
                            BigDecimal productPrice = null;
                            if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                                productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                                maliyVO.setUnitPrice(productPrice);
                            }
                            maliyVO.setUnitPrice(productPrice);

                            MaterialMaliyVO vo = new MaterialMaliyVO();
                            if (!(maliyVO.getProcessingFee().equals(new BigDecimal("0"))) && !(maliyVO.getSprayPrice().equals(new BigDecimal("0"))) && !(maliyVO.getMaterialPrice().equals(new BigDecimal("0")))) {
                                vo.setSprayPrice(maliyVO.getSprayPrice()); // ?????????
                                vo.setPrice(maliyVO.getPrice());// ?????????
                                vo.setProcessingFee(maliyVO.getProcessingFee()); // ?????????
                                vo.setMaterialPrice(maliyVO.getMaterialPrice()); // ?????????
                                vo.setCoatingPrice(maliyVO.getCoatingPrice()); // ????????????
                                vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice());// ????????????
                                vo.setWeight(maliyVO.getWeight()); // ??????
                                vo.setUnitPrice(maliyVO.getUnitPrice()); // ????????????
                                vo.setSupplierCode(maliyVO.getSupplierCode()); // ???????????????
                                vo.setSupplierName(maliyVO.getSupplierName());  // ???????????????
                                vo.setCoating(maliyVO.getCoating());  // ??????
                                vo.setItemCode(itemCode); // ????????????
                                vo.setItemName(itemName); // ????????????
                                vo.setLength(maliyVO.getLength());  // ????????????
                                vo.setTheMaterial(maliyVO.getTheMaterial()); // ????????????
                                vo.setExternalDiameter(maliyVO.getExternalDiameter()); // ??????
                                vo.setInternalDiamete(maliyVO.getInternalDiamete()); // ??????
                                maliyVOList.add(vo);
                            } else {
                                continue;
                            }
                        }
                    } else if (maliyVO.getExternalDiameter() > 300) {
                        //????????????  ??????+10 ?????? -10
                        Double exter = maliyVO.getExternalDiameter() + 10;
                        Double inner = maliyVO.getInternalDiamete() - 10;
                        //?????????
                        MaterialMaliyVO materialList = getMaterialPrice(maliyVO, exter, inner, maliyVO.getTheMaterial(), maliyVO.getLength(), maliyVO.getSupplierCode());
                        if (materialList == null) {
                            continue;
                        }
                        maliyVO.setTheMaterialPrice(materialList.getTheMaterialPrice()); //????????????
                        maliyVO.setMaterialPrice(materialList.getMaterialPrice());  //?????????
                        maliyVO.setWeight(materialList.getWeight()); //??????

                        //?????????
                        MaterialMaliyVO sprayList = getSprayList(maliyVO, maliyVO.getLength(), maliyVO.getInternalDiamete(), maliyVO.getCoating(), maliyVO.getTheMaterial(), maliyVO.getSupplierCode());
                        if (sprayList.equals(null)) {
                            continue;
                        }
                        maliyVO.setCoatingPrice(sprayList.getCoatingPrice()); //????????????
                        maliyVO.setSprayPrice(sprayList.getSprayPrice());//?????????

                        //?????????
                        MaterialMaliyVO proscessList = getProscessList(maliyVO, maliyVO.getExternalDiameter(), maliyVO.getInternalDiamete(), maliyVO.getLength(), maliyVO.getSupplierCode());
                        if (proscessList == null) {
                            continue;
                        }
                        maliyVO.setProcessingFee(proscessList.getProcessingFee()); //?????????
                        maliyVO.setPrice(proscessList.getPrice()); //?????????

                        //????????????   ????????????=?????????+?????????+?????????+?????????
                        BigDecimal productPrice = null;
                        if (materialList.getMaterialPrice() != null && sprayList.getSprayPrice() != null && proscessList.getPrice() != null && proscessList.getProcessingFee() != null) {
                            productPrice = new BigDecimal(String.valueOf(materialList.getMaterialPrice().add(sprayList.getSprayPrice()).add(proscessList.getPrice()).add(proscessList.getProcessingFee())));
                            maliyVO.setUnitPrice(productPrice);
                        }
                        maliyVO.setUnitPrice(productPrice);

                        MaterialMaliyVO vo = new MaterialMaliyVO();

                        if (!(maliyVO.getProcessingFee().equals(new BigDecimal("0"))) && !(maliyVO.getSprayPrice().equals(new BigDecimal("0"))) && !(maliyVO.getMaterialPrice().equals(new BigDecimal("0")))) {
                            vo.setSprayPrice(maliyVO.getSprayPrice()); // ?????????
                            vo.setPrice(maliyVO.getPrice());// ?????????
                            vo.setProcessingFee(maliyVO.getProcessingFee()); // ?????????
                            vo.setMaterialPrice(maliyVO.getMaterialPrice()); // ?????????
                            vo.setCoatingPrice(maliyVO.getCoatingPrice()); // ????????????
                            vo.setTheMaterialPrice(maliyVO.getTheMaterialPrice());// ????????????
                            vo.setWeight(maliyVO.getWeight()); // ??????
                            vo.setUnitPrice(maliyVO.getUnitPrice()); // ????????????
                            vo.setSupplierCode(maliyVO.getSupplierCode()); // ???????????????
                            vo.setSupplierName(maliyVO.getSupplierName());  // ???????????????
                            vo.setCoating(maliyVO.getCoating());  // ??????
                            vo.setItemCode(itemCode); // ????????????
                            vo.setItemName(itemName); // ????????????
                            vo.setLength(maliyVO.getLength());  // ????????????
                            vo.setTheMaterial(maliyVO.getTheMaterial()); // ????????????
                            vo.setExternalDiameter(maliyVO.getExternalDiameter()); // ??????
                            vo.setInternalDiamete(maliyVO.getInternalDiamete()); // ??????
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
     * ?????????
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
        // ??????????????????
        //Double itemprice = materialMailyVoMapper.selectTheMaterialPrice(theMaterial, externalDiameter.toString(), supplierCode);
        ItemInfoMaterialPriceMaliy itemInfoMaterialPriceMaliy = materialMailyVoMapper.selectMaterialPriceVO(theMaterial, externalDiameter.toString(), supplierCode);
        if (itemInfoMaterialPriceMaliy == null) {
            maliyVO.setTheMaterialPrice(null); // ????????????
            maliyVO.setMaterialPrice(null);
            return maliyVO;
        }
        BigDecimal itemprice =itemInfoMaterialPriceMaliy.getTheMaterialPrice();
        maliyVO.setTheMaterialPrice(itemprice);

        String k=itemInfoMaterialPriceMaliy.getK();

        //????????????
        BigDecimal price = new BigDecimal(externalDiameter).multiply(BigDecimal.valueOf(externalDiameter)).subtract(BigDecimal.valueOf(internalDiamete).multiply(BigDecimal.valueOf(internalDiamete)));
        BigDecimal weight = new BigDecimal(String.valueOf(price)).multiply(BigDecimal.valueOf(PI)).divide(BigDecimal.valueOf(4)).multiply(BigDecimal.valueOf(length)).multiply(BigDecimal.valueOf(8)).divide(BigDecimal.valueOf(1000000));
        //???????????????
        BigDecimal MaterialPrice = new BigDecimal(String.valueOf(weight)).multiply(maliyVO.getTheMaterialPrice()).multiply(new BigDecimal(k));
        maliyVO.setWeight(weight.setScale(2, BigDecimal.ROUND_DOWN));  // ??????
        maliyVO.setMaterialPrice(MaterialPrice.setScale(2, BigDecimal.ROUND_DOWN)); // ?????????
        maliyVO.setTheMaterialPrice(maliyVO.getTheMaterialPrice()); // ????????????
        return maliyVO;
    }

    /**
     * ?????????
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
            maliyVO.setProcessingFee(null);  // ?????????
            return maliyVO;
        }
        length += 8;
        // ?????????
        if (length < 200) {
            //????????????
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
                maliyVO.setPrice(BigDecimal.valueOf(18)); // ?????????
            }
        } else {
            maliyVO.setPrice(new BigDecimal("0"));
        }

        maliyVO.setProcessingFee(vo.getProcessingFee().setScale(2, BigDecimal.ROUND_DOWN)); // ?????????
        return maliyVO;
    }

    /**
     * ???????????????
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
            maliyVO.setSprayPrice(null);  //?????????
            maliyVO.setCoatingPrice(null);
            return maliyVO;
        }
        maliyVO.setCoatingPrice(BigDecimal.valueOf(price));  //????????????
        //???????????????
        BigDecimal sprayPrice = new BigDecimal(internalDiamete).multiply(BigDecimal.valueOf(PI)).multiply(BigDecimal.valueOf(length)).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(price));


        if (("G05".equals(coating) || "G06".equals(coating)) && sprayPrice.doubleValue() < 40) {
            maliyVO.setSprayPrice(new BigDecimal(40));
        } else if (("G14".equals(coating) || "G20".equals(coating)) && sprayPrice.doubleValue() < 40) {
            maliyVO.setSprayPrice(new BigDecimal(55));
        } else {
            maliyVO.setSprayPrice(sprayPrice.setScale(2, BigDecimal.ROUND_DOWN));  // ?????????
        }
        return maliyVO;
    }

    /**
     * ?????? ????????? ???????????????dialog
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoEntityOfXLJ> getItemInfoOfXLJVO(String itemCode, String itemName) {
        SubmitPriceReq req = new SubmitPriceReq();
        req.setItemCode(itemCode);

        // ?????????????????????????????????????????????????????????????????????????????????
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

        // ?????????????????????????????????
        List<ItemInfoDTO> itemInfoDTOS = autoRetriveOfXLJ(u9PrDTOList);
        if (itemInfoDTOS.size() > 0) {
            itemInfoOfXLJVOS = this.baseMapper.selectXLJReportList(req);
        }

        return itemInfoOfXLJVOS;
    }

    /**
     * ?????? ????????? ???????????????dialog
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoEntityOfDZ> getItemInfoOfDZVO(String itemCode, String itemName) {
        SubmitPriceReq req = new SubmitPriceReq();
        req.setItemCode(itemCode);

        // ?????????????????????????????????????????????????????????????????????????????????
        List<ItemInfoEntityOfDZ> itemInfoOfXLJVOS = this.baseMapper.selectDZReportList(req);

        return itemInfoOfXLJVOS;
    }


    /**
     * ?????? ????????? ???????????????dialog
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoEntityOfFL> getItemInfoOfFLVO(String itemCode, String itemName) {
        SubmitPriceReq req = new SubmitPriceReq();
        req.setItemCode(itemCode);

        // ?????????????????????????????????????????????????????????????????????????????????
        List<ItemInfoEntityOfFL> itemInfoOfXLJVOS = this.baseMapper.selectFLReportList(req);

        return itemInfoOfXLJVOS;
    }

    /**
     * ?????? ????????? ???????????????dialog
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoEntityOfLZQ> getItemInfoOfLZQVO(String itemCode, String itemName) {
        SubmitPriceReq req = new SubmitPriceReq();
        req.setItemCode(itemCode);

        // ?????????????????????????????????????????????????????????????????????????????????
        List<ItemInfoEntityOfLZQ> itemInfoOfXLJVOS = this.baseMapper.selectLZQReportList(req);

        return itemInfoOfXLJVOS;
    }


    /**
     * ???????????????????????????dialog
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

        // ?????? & ??????
        String standards = "";
        String material = "";

        // ??????
        ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(itemName);
        if (itemInfoEntity.getMaterial() == null) { // ???????????????????????????????????? ????????? ??????
            itemInfoEntity = getMaterialItemInfoOfDuanJian(itemName);
            if (itemInfoEntity == null) { // ???????????????????????????????????????null
                return null;
            }
        }
        // ?????? & ??????
        standards = itemInfoEntity.getSize() + "-" + itemInfoEntity.getPound();
        material = itemInfoEntity.getMaterial();

        // ?????????????????????????????????
        List<ItemInfoDTO> itemInfoDTOSOfOld = handleOfDJ(u9Prs, standards, material);
        List<ItemInfoDTO> itemInfoDTOSOfNew = handleOfDJOfNew(u9Prs, standards, material);

        // ???????????????????????????list??????
        if (itemInfoDTOSOfNew.size() > 0 && itemInfoDTOSOfOld.size() > 0) {
            itemInfoDTOSOfNew.get(0).getSupAndPriceList().stream().forEach(itemInfoDTO -> {
                itemInfoDTOSOfOld.get(0).getSupAndPriceList().add(itemInfoDTO);
            });
        } else if (itemInfoDTOSOfNew.size() > 0 && itemInfoDTOSOfOld.size() == 0) {
            itemInfoDTOSOfOld.addAll(itemInfoDTOSOfNew);
        }

        // ????????????????????????,????????????????????? ?????????????????????
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
     * ?????????????????????????????????
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfDuanJian(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        // ?????????????????? ?????? ???????????????
        for (U9PrDTO u9Prs : u9PrDTOS) {
            // ??????????????????????????????
            if (u9Prs.getItemName().indexOf("??????") < 0 &&u9Prs.getItemName().indexOf("??????") < 0) {
                continue;
            }
            // ?????? & ??????
            String standards = "";
            String material = "";

            // ??????
            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(u9Prs.getItemName());
            if (itemInfoEntity.getMaterial() == null) { // ???????????????????????????????????? ????????? ??????
                itemInfoEntity = getMaterialItemInfoOfDuanJian(u9Prs.getItemName());
                if (itemInfoEntity == null) { // ???????????????????????????????????????null
                    return null;
                }
            }
            // ?????? & ??????
            standards = itemInfoEntity.getSize() + "-" + itemInfoEntity.getPound();
            material = itemInfoEntity.getMaterial();

            // ?????????????????????????????????
            List<ItemInfoDTO> itemInfoDTOSOfOld = handleOfDJ(u9Prs, standards, material); // ????????????????????????????????????????????? ?????? / ?????? ???????????????
            List<ItemInfoDTO> itemInfoDTOSOfNew = handleOfDJOfNew(u9Prs, standards, material); // ???????????? ?????????????????????????????? ?????? + ?????? ???????????????

            // ??????????????????
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

            // ????????????????????????,????????????????????? ?????????????????????
            if (itemInfoDTOSOfOld.size() > 0) {
                this.handleDJReport(itemInfoDTOSOfOld, u9Prs);
            }

            itemInfoDTOS.addAll(itemInfoDTOSOfOld);
        }
        return itemInfoDTOS;
    }

    // ?????????????????????????????????
    private void handleDJReport(List<ItemInfoDTO> itemInfoDTOSOfOld, U9PrDTO u9Prs) {
        // ????????????????????????????????????????????????????????????????????????????????????
        Integer count = this.baseMapper.itemCodeOfDJIsExisted(u9Prs.getItemCode());
        if (count == 0) { // ?????????????????????????????????
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
                if ("A".equals(formulaType)) { //???????????????
                    bigOuterSize = itemInfoEntityBasOfDJ.getBigOuterSize();  // ????????????
                    bigHeightSize = itemInfoEntityBasOfDJ.getBigHeightSize();  // ????????????
                    smallOuterSize = itemInfoEntityBasOfDJ.getSmallOuterSize();// ????????????
                    totalHeightSize = itemInfoEntityBasOfDJ.getTotalHeightSize(); // ?????????
                    innerSize = itemInfoEntityBasOfDJ.getInnerSize();// ??????
                } else { // ??????
                    innerSize = itemInfoEntityBasOfDJ.getInnerSize();// ??????
                    outerSize = itemInfoEntityBasOfDJ.getOuterSize();  // ??????
                    heightSize = itemInfoEntityBasOfDJ.getHeightSize();  // ??????
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
                if ("A".equals(formulaType)) { //???????????????
                    bigOuterSize = itemInfoEntityBasOfDJ.getBigOuterSize();  // ????????????
                    bigHeightSize = itemInfoEntityBasOfDJ.getBigHeightSize();  // ????????????
                    smallOuterSize = itemInfoEntityBasOfDJ.getSmallOuterSize();// ????????????
                    totalHeightSize = itemInfoEntityBasOfDJ.getTotalHeightSize(); // ?????????
                    innerSize = itemInfoEntityBasOfDJ.getInnerSize();// ??????
                } else { // ??????
                    innerSize = itemInfoEntityBasOfDJ.getInnerSize();// ??????
                    outerSize = itemInfoEntityBasOfDJ.getOuterSize();  // ??????
                    heightSize = itemInfoEntityBasOfDJ.getHeightSize();  // ??????
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
     * international??????
     * ??????????????????????????????????????????
     *
     * @param u9PrDTOS
     * @return
     */
    public List<ItemInfoDTO> autoRetrieveOfWW(List<U9PrDTO> u9PrDTOS) {
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();

        // ?????????????????? ???????????? ???????????????
        for (U9PrDTO u9Prs : u9PrDTOS) {
            // ??????????????????????????????
            if (!u9Prs.getBizType().toString().equals(WW)) {
                continue;
            }
            // ??????
            ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfQCWW(u9Prs.getItemName());
            // ???????????????
            if (itemInfoEntity.getSeries() != null) {
                // ?????????????????????????????????????????????
                List<ItemInfoEntityOfWW> itemInfoEntitiesWW = this.baseMapper.selectSupAndWeightOfQCWW(itemInfoEntity);
                String price = "";

                // ????????????????????????
                if (itemInfoEntitiesWW.size() > 0) {

                    ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
                    List<PriceVO> priceVOS = new ArrayList<>();

                    // id?????????????????????????????????
                    itemInfoDTO.setId(u9Prs.getId().toString());
                    itemInfoDTO.setItemCode(u9Prs.getItemCode());
                    itemInfoDTO.setItemName(u9Prs.getItemName());

                    // ??????????????????????????????
                    for (ItemInfoEntityOfWW iteminfo : itemInfoEntitiesWW) {
                        price = iteminfo.getPrice().toString();
                        // ??????????????????
                        if (StringUtils.isNotEmpty(price)) {
                            PriceVO priceVO = new PriceVO();
                            // ???????????????????????????????????????
                            priceVO.setSupName(iteminfo.getSupName());
                            priceVO.setSupCode(iteminfo.getSupCode());
                            priceVO.setPriority(iteminfo.getPriority());
                            // ???????????? ??????2????????? ??????*??????+?????????
                            BigDecimal priceToReturn = (new BigDecimal(price));
                            priceVO.setPrice(priceToReturn);
                            priceVOS.add(priceVO);
                            itemInfoDTO.setSupAndPriceList(priceVOS);
                        }
                    }

                    // ?????????????????????????????????????????????supType?????? 1
                    if ((itemInfoEntitiesWW != null) && (itemInfoEntitiesWW.size() > 1) && (itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                        itemInfoDTO.setSupColorType("1");
                    } else {
                        itemInfoDTO.setSupColorType("0");
                    }
                    // ???????????????
                    if (itemInfoDTO.getSupAndPriceList().size() > 0) {
                        itemInfoDTOS.add(itemInfoDTO);
                    }
                }
            }
        }
        return itemInfoDTOS;
    }


    /**
     * ??????PR????????????
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
     * ?????????????????????????????????????????????
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
     * ?????????????????????????????????????????????  - ?????????
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
     * ???????????????????????????dialog
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoOfZDJVO> getItemInfoOfZDJVO(String itemCode, String itemName) {
        List<ItemInfoOfZDJVO> itemInfoOfCastVOS = new ArrayList<>();
        ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfZhuDuanJian(itemName);
        // ???????????????
        if (itemInfoEntity.getSeries() != null) {
            // ?????????????????????????????????????????????
            List<ItemInfoEntityOfZDJ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfZDJ(itemInfoEntity);

            // ????????????????????????
            if (itemInfoEntitiesFromDataBase.size() > 0) {
                // ??????????????????????????????
                for (ItemInfoEntityOfZDJ iteminfo : itemInfoEntitiesFromDataBase) {
                    String extraPrice = iteminfo.getCharge();
                    if (StringUtils.isEmpty(extraPrice)) {
                        extraPrice = "0";
                    }

                    // ???????????????????????????????????? ??????
                    iteminfo.setMaterial(itemInfoEntity.getMaterial());

                    String price = this.baseMapper.selectItemPriceOfZDJ(iteminfo);
                    // ??????????????????
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
     * ???????????????????????????dialog??????????????????
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoOfZDJVO> getItemInfoOfWWVO(String itemCode, String itemName) {

        List<ItemInfoOfZDJVO> itemInfoOfCastVOS = new ArrayList<>();
        ItemInfoEntityOfZDJ itemInfoEntity = getItemInfoOfQCWW(itemName);
        // ???????????????
        if (itemInfoEntity.getSeries() != null) {
            // ?????????????????????????????????????????????
            List<ItemInfoEntityOfWW> itemInfoEntitiesWW = this.baseMapper.selectSupAndWeightOfQCWW(itemInfoEntity);
            ;

            // ????????????????????????
            if (itemInfoEntitiesWW.size() > 0) {
                // ??????????????????????????????
                for (ItemInfoEntityOfWW iteminfo : itemInfoEntitiesWW) {

                    // ???????????????????????????????????? ??????
                    iteminfo.setMaterial(itemInfoEntity.getMaterial());

                    String price = iteminfo.getPrice().toString();
                    // ??????????????????
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
     * ???????????????????????????dialog
     *
     * @param itemCode
     * @param itemName
     * @return
     */
    @Override
    public List<ItemInfoOfQZVO> getItemInfoOfQZVO(String itemCode, String itemName) {
        List<ItemInfoOfQZVO> itemInfoOfCastVOS = new ArrayList<>();
        ItemInfoEntityOfQZ itemInfoEntity = getItemInfoOfQiuZuo(itemName);
        // ???????????????
        if (itemInfoEntity.getItemize() != null) {
            // ?????????????????????????????????????????????
            List<ItemInfoEntityOfQZ> itemInfoEntitiesFromDataBase = this.baseMapper.selectSupAndWeightOfQZ(itemInfoEntity);

            // ????????????????????????
            if (itemInfoEntitiesFromDataBase.size() > 0) {
                // ??????????????????????????????
                for (ItemInfoEntityOfQZ iteminfo : itemInfoEntitiesFromDataBase) {
                    // ???????????????
                    String qzCharge = iteminfo.getQzCharge();
                    if (StringUtils.isEmpty(qzCharge)) {
                        qzCharge = "0";
                    }
                    // ???????????????
                    String fzCharge = iteminfo.getFzCharge();
                    if (StringUtils.isEmpty(fzCharge)) {
                        fzCharge = "0";
                    }

                    String price = this.baseMapper.selectItemPriceOfQZ(iteminfo);
                    String range = "";
                    if (Integer.valueOf(iteminfo.getSize()) >= 0.5 && Integer.valueOf(iteminfo.getSize()) <= 10) {
                        range = "0.5????????????10???";
                    } else {
                        range = "?????????12???";
                    }
                    String ptPrice = this.baseMapper.selectPtPriceOfQZ(iteminfo, range);

                    // ??????????????????
                    if (StringUtils.isNotEmpty(price)) {
                        ItemInfoOfQZVO itemInfoOfCastVO = new ItemInfoOfQZVO();

                        // ???????????? ??????2????????? ???????????????*??????+??????????????????+ ???????????????*??????+??????????????????
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
     * ????????????????????? - ??????
     *
     * @param SubmitPriceReq
     * @return
     */
    @Override
    public boolean saveBatchOfOthers(SubmitPriceReq SubmitPriceReq) {

        List<SubmitPriceDTO> submitPriceDTOs = SubmitPriceReq.getSubmitPriceDTOs();

        // ??????id?????????
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
                throw new RuntimeException("?????????PR???" + "id???" + submitPriceDTO.getId());
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
                //?????????????????????
                io.setIsByWeight(submitPriceDTO.getIsByWeight());
                io.setIsPersent(submitPriceDTO.getIsPersent());
                io.setRemark(submitPriceDTO.getRemark());
                io.setQuotePrice(submitPriceDTO.getQuotePrice());
                io.setPromiseDate(submitPriceDTO.getPromiseDate().getTime() / 1000 + 8 * 3600); //?????????datePicker?????????8???????????????
                io.setSource(IIoService.SOURCE_PURCHSUBMIT);

                if ("flowSubmit".equals(submitPriceDTO.getType())) {
                    //??????????????????????????????
                    pr.setStatus(IU9PrService.STATUS_FLOW_SUBMIT);
                } else if ("winBid".equals(submitPriceDTO.getType())) {
                    //??????????????????????????????
                    pr.setStatus(IU9PrService.STATUS_WINBID);
                } else {
                    // ???????????? ????????????????????????
                    pr.setStatus(IU9PrService.STATUS_WINBID);
                }
                ioService.updateById(io);
            }
            updateById(pr);
        }
        return true;
    }


    /**
     * ????????????????????? - ?????????
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
                throw new RuntimeException("?????????PR???" + "id???" + submitPriceDTO.getId());
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
            //?????????????????????
            io.setIsByWeight(submitPriceDTO.getIsByWeight());
            io.setIsPersent(submitPriceDTO.getIsPersent());
            io.setRemark(submitPriceDTO.getRemark());
            io.setQuotePrice(submitPriceDTO.getQuotePrice());
            io.setPromiseDate(submitPriceDTO.getPromiseDate().getTime() / 1000 + 8 * 3600);
            io.setSource(IIoService.SOURCE_PURCHSUBMIT);

            if ("flowSubmit".equals(submitPriceDTO.getType())) {
                //??????????????????????????????
                pr.setStatus(IU9PrService.STATUS_FLOW_SUBMIT);
            } else if ("winBid".equals(submitPriceDTO.getType())) {
                Integer bidNum = this.baseMapper.countTheBid(submitPriceDTO.getId().toString());
                if (bidNum != 1) {
                    throw new RuntimeException("?????????????????????????????????????????????????????????????????????");
                }
                //??????????????????????????????
                pr.setStatus(IU9PrService.STATUS_WINBID);
            } else {
                Integer bidNum = this.baseMapper.countTheBid(submitPriceDTO.getId().toString());
                if (bidNum > 0) {
                    throw new RuntimeException("?????????????????????????????????????????????????????????????????????");
                }
                // ???????????? ????????????????????????
                pr.setStatus(IU9PrService.STATUS_WINBID);
            }
            ioService.updateById(io);
            updateById(pr);
        }
        return true;
    }

    /**
     * ??????PR???????????? - ???????????????
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
     * ???????????? - ???????????????
     *
     * @param submitPriceReq
     * @return
     */
    @Override
    public boolean winTheBid(SubmitPriceReq submitPriceReq) {

        Integer bidNum = this.baseMapper.countTheBid(submitPriceReq.getSubmitPriceDTOs().get(0).getId().toString());
        if (bidNum > 0) {
            throw new RuntimeException("?????????????????????????????????");
        }
        return this.baseMapper.winTheBid(submitPriceReq.getSubmitPriceDTOs().get(0).getIoId().toString());
    }

    /**
     * ???????????? - ???????????????
     *
     * @param submitPriceReq
     * @return
     */
    @Override
    public boolean cancelTheBid(SubmitPriceReq submitPriceReq) {
        return this.baseMapper.cancelTheBid(submitPriceReq.getSubmitPriceDTOs().get(0).getIoId().toString());
    }


    /**
     * ??????????????????
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
     * ???????????????????????????
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
                throw new RuntimeException("?????????????????????????????????????????????");
            }
            supItemOthers.setSupName(sup.getName());
            // ?????????????????? ??????????????????
            supItemOthers.setStatus(0);

            supItemOthers.setSupCode(supCode);
            this.baseMapper.addOtherInfos(supItemOthers);
        }
        return true;
    }

    /**
     * ???????????????????????????
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
     * ???????????????????????????
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
            throw new RuntimeException("?????????????????????????????????????????????");
        }
        supItemOthers.setSupName(sup.getName());

        this.baseMapper.updateOtherInfos(supItemOthers);

        return true;
    }

    /**
     * ???????????????????????????
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
            throw new RuntimeException("?????????????????????????????????????????????");
        }
        supItemOthers.setSupName(sup.getName());

        return this.baseMapper.removeOtherInfos(supItemOthers);
    }


    /**
     * ???????????????
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
        // ?????????????????????
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

            // ????????????IO
            ioService.save(ioEntity);
            // ?????????????????????????????????????????????
            supItems.setStatus(1);
            this.baseMapper.updateStatusOfOthers(supItems);
        }

        // ??????PR?????????
        u9PrEntity.setStatus(STATUS_INQUIRY);
        u9PrEntity.setInquiryWay(INQUIRYWAY_COMPETE);
        saveOrUpdate(u9PrEntity);
        return true;
    }


    /**
     * ???????????? - ?????????
     *
     * @param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean evaluateBidOfOthers(Long id) {
        List<IoEntity> ioEntities = this.baseMapper.getIoListByPrId(id);
        U9PrEntity pr = this.baseMapper.getPrById(id);

        // ?????????
        BigDecimal matPrice = new BigDecimal("0");
        BigDecimal mat = this.baseMapper.getMaterialCostByItemCode(pr.getItemCode());
        if (mat != null) {
            matPrice = mat;
        } else {
            throw new RuntimeException("?????????????????????????????????...");
        }

        // ?????????
        BigDecimal laborPrice = new BigDecimal("0");
        BigDecimal lab = this.baseMapper.getLaborCostByItemCode(pr.getItemCode());
        if (lab != null) {
            laborPrice = lab;
        } else {
            throw new RuntimeException("?????????????????????????????????...");
        }
        pr.setMaterialCost(matPrice);
        pr.setLaborCost(laborPrice);
        saveOrUpdate(pr);

        // ??????????????????
        Long reqDate = pr.getReqDate();
        // ??????????????????
        BigDecimal standandPrice = laborPrice.multiply(new BigDecimal("1.4")).add(matPrice);

        // ????????????
        for (IoEntity ioEntity : ioEntities) {
            // ????????????
            BigDecimal quotoPrice = ioEntity.getQuotePrice();

            // ????????????
            Long quotoDate = ioEntity.getQuoteDate();

            // ????????????
            Long promiseDate = ioEntity.getPromiseDate();

            // ????????????
            if (quotoDate == null || quotoPrice == null) {
                this.baseMapper.updateEvaluateScore(new BigDecimal("0"), ioEntity.getId());
            } else if (promiseDate > reqDate || quotoDate > reqDate || quotoPrice.compareTo(standandPrice) > 0) {
                this.baseMapper.updateEvaluateScore(new BigDecimal("0"), ioEntity.getId());
            } else {
                // ???????????? ?????????????????? ??? ???????????? ?????? + 1
                this.baseMapper.updateEvaluateScore(standandPrice.subtract(quotoPrice).add(new BigDecimal("1")), ioEntity.getId());
            }
        }

        // ????????????????????????LIST
        List<IoEntity> ioEntitiesAfterScore = this.baseMapper.getIoListByPrId(id);
        // ???????????? ???????????????
        List<IoEntity> bestIoEntities = new ArrayList<>();
        IoEntity baseIo = ioEntitiesAfterScore.get(0);

        for (IoEntity ioEntity : ioEntitiesAfterScore) {
            BigDecimal baseScore = baseIo.getEvaluateScore();

            // ????????????????????????
            if (ioEntity.getEvaluateScore().compareTo(baseScore) > 0) {
                // ????????????????????????????????????????????????????????????
                bestIoEntities.clear();
                bestIoEntities.add(ioEntity);
                baseIo = ioEntity;
            } else if (ioEntity.getEvaluateScore().compareTo(baseScore) == 0) {
                // ???????????????????????????
                bestIoEntities.add(ioEntity);
            }
        }

        // ??????????????????????????????????????????????????????????????????
        if (bestIoEntities.size() == 1 && bestIoEntities.get(0).getEvaluateScore().compareTo(new BigDecimal("0")) == 0) {
            bestIoEntities.clear();
        }

        // ????????????????????????????????????IO??????????????? ????????????????????? ?????? ?????????????????????????????????????????????????????????????????????????????????
        if (bestIoEntities.size() == 1 && bestIoEntities.get(0).getEvaluateScore().compareTo(new BigDecimal("0")) > 0) {
            for (IoEntity ioEntity : ioEntitiesAfterScore) {
                // ???????????????ID????????????
                if (!ioEntity.getId().toString().equals(bestIoEntities.get(0).getId().toString())) {
                    this.baseMapper.setLoseTheBid(ioEntity);
                } else {
                    this.baseMapper.setWinTheBid(ioEntity.getId(), new Date().getTime() / 1000);
                }
            }
            this.baseMapper.setPrToWait(pr.getId());
        }

        // ?????????????????????????????? ,???????????????
        if (bestIoEntities.size() == 0) {
            this.baseMapper.setPrFlow(pr.getId());
        }

        // ?????????????????? 1 ????????????
        if (bestIoEntities.size() > 1) {
            this.baseMapper.setPrToChoose(pr.getId());
        }

        return true;
    }

    /**
     * ??????????????????
     *
     * @param
     * @return
     */
    @Override
    public IPage<ItemInfoEntityDJReport> getItemInfoOfDJVOReport(IPage<ItemInfoEntityDJReport> page, SubmitPriceReq submitPriceReq) {
        // ??????3????????????????????????????????????????????????????????????????????????
//        List<ItemInfoVO> itemInfoVOS = this.baseMapper.selectAllDjItemCodes();
//        itemInfoVOS.stream().forEach( item ->{
//            this.getItemInfoOfDJVO(item.getItemCode(),item.getItemName());
//        });

//        IPage<ItemInfoEntityDJReport> itemInfoEntityDJReportPage = this.baseMapper.selectDjReportPage(page,submitPriceReq);
        IPage<ItemInfoEntityDJReport> itemInfoEntityDJReportPage = this.baseMapper.selectDjReportPage(page, submitPriceReq);
        return itemInfoEntityDJReportPage;
    }

    /**
     * ?????????????????????
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
     * ?????????????????????
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
     * ??????????????????
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
     * ??????????????????
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
     * ???????????????
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

        // ?????????????????? ??????????????????????????????
        ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = this.baseMapper.selectBasicItemInfoOfDJ(u9Prs.getItemCode());
        if (itemInfoEntityBasOfDJ == null) {   // ??????????????????????????????,????????????
            return itemInfoDTOS;
        }

        if (itemInfoEntityBasOfDJ.getBigOuterSize() != null && !itemInfoEntityBasOfDJ.getBigOuterSize().isEmpty()) {
            bigOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigOuterSize()).doubleValue();  // ????????????
        }
        if (itemInfoEntityBasOfDJ.getBigHeightSize() != null && !itemInfoEntityBasOfDJ.getBigHeightSize().isEmpty()) {
            bigHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigHeightSize()).doubleValue();  // ????????????
        }
        if (itemInfoEntityBasOfDJ.getSmallOuterSize() != null && !itemInfoEntityBasOfDJ.getSmallOuterSize().isEmpty()) {
            smallOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getSmallOuterSize()).doubleValue();// ????????????
        }
        if (itemInfoEntityBasOfDJ.getTotalHeightSize() != null && !itemInfoEntityBasOfDJ.getTotalHeightSize().isEmpty()) {
            totalHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getTotalHeightSize()).doubleValue(); // ?????????
        }

        if (itemInfoEntityBasOfDJ.getInnerSize() != null && !itemInfoEntityBasOfDJ.getInnerSize().isEmpty()) {
            innerSize = Double.valueOf(itemInfoEntityBasOfDJ.getInnerSize()).doubleValue();// ??????
        }

        if (itemInfoEntityBasOfDJ.getOuterSize() != null && !itemInfoEntityBasOfDJ.getOuterSize().isEmpty()) {
            outerSize = Double.valueOf(itemInfoEntityBasOfDJ.getOuterSize()).doubleValue();  // ??????
        }
        if (itemInfoEntityBasOfDJ.getHeightSize() != null && !itemInfoEntityBasOfDJ.getHeightSize().isEmpty()) {
            heightSize = Double.valueOf(itemInfoEntityBasOfDJ.getHeightSize()).doubleValue();  // ??????
        }

        String formulaType = itemInfoEntityBasOfDJ.getType();
        if ("A".equals(formulaType)) { //???????????????
            outSizeToSearch = bigOuterSize;
        } else {
            outSizeToSearch = outerSize;
        }

        // ????????????
        if (itemInfoEntityBasOfDJ.getRes().equals("fm")) { // (??????????????????
            itemInfoEntityResFMOfDJList = this.baseMapper.selectResItemInfoOfFm(standards, material);
            if (itemInfoEntityResFMOfDJList.size() > 0) {
                itemInfoEntityResFMOfDJList.stream().forEach(itemInfoEntityResFMOfDJ -> {
                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResFMOfDJ, ItemInfoEntityResALLOfDJ.class);
                    itemInfoEntityResALLOfDJList.add(iteminfo);
                });
            }
        } else if (itemInfoEntityBasOfDJ.getRes().equals("zfl")) {// (??????????????????)
            itemInfoEntityResZFLOfDJList = this.baseMapper.selectResItemInfoOfZfl(outSizeToSearch, material);
            if (itemInfoEntityResZFLOfDJList.size() > 0) {
                itemInfoEntityResZFLOfDJList.stream().forEach(itemInfoEntityResFMOfDJ -> {
                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResFMOfDJ, ItemInfoEntityResALLOfDJ.class);
                    itemInfoEntityResALLOfDJList.add(iteminfo);
                });
            }
        } else {// (??????????????????)
            itemInfoEntityResZYDOfDJList = this.baseMapper.selectResItemInfoOfZyd(outSizeToSearch, material);
            if (itemInfoEntityResZYDOfDJList.size() > 0) {
                itemInfoEntityResZYDOfDJList.stream().forEach(itemInfoEntityResZYDOfDJ -> {
                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResZYDOfDJ, ItemInfoEntityResALLOfDJ.class);
                    itemInfoEntityResALLOfDJList.add(iteminfo);
                });
            }
        }

        // ????????????????????????
        if (itemInfoEntityResALLOfDJList.size() > 0) {
            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
            List<PriceVO> priceVOS = new ArrayList<>();
            // id?????????????????????????????????
            itemInfoDTO.setId(u9Prs.getId().toString());
            itemInfoDTO.setItemCode(u9Prs.getItemCode());
            itemInfoDTO.setItemName(u9Prs.getItemName());

            // ????????????????????????????????????
            for (ItemInfoEntityResALLOfDJ iteminfo : itemInfoEntityResALLOfDJList) {
                Double outerRemain = Double.valueOf(iteminfo.getOuterRemain()).doubleValue(); // ????????????
                Double innerRemain = Double.valueOf(iteminfo.getInnerRemain()).doubleValue(); // ????????????
                Double heightRemain = Double.valueOf(iteminfo.getHeightRemain()).doubleValue(); // ????????????

                Double weight = 0D;
                Double weightOfBasic = 0D;

                if (itemInfoEntityBasOfDJ.getType().equals("A")) { // --???????????????--
                    // ??????=(((????????????/2+????????????/2)^2 ??? (??????/2-????????????/2)^2 )) * PI * (????????????+????????????)+((????????????/2 + ????????????/2)^2-(??????/2 ??? ????????????/2)^2)*PI*(?????????-????????????))*7.856/1000000
                    if ((innerSize - innerRemain) >= 80) {
                        weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * PI * (totalHeightSize - bigHeightSize)) * 7.856 / 1000000;
                    } else {
                        weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) * PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2)) * PI * (totalHeightSize - bigHeightSize))) * 7.856 / 1000000;
                    }
                } else { // --????????????--
                    // ?????? = PI*((????????????/2 +????????????/2)^2 ??? (????????????/2 - ????????????/2)^2)*(?????? + ????????????)*7.856/1000000
                    if ((innerSize - innerRemain) >= 80) {
                        weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
                    } else {
                        weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
                    }
                }

                BigDecimal b = new BigDecimal(weightOfBasic);
                weight = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                // ??????
                String singlePrice = this.baseMapper.selectSinglePrice(iteminfo.getSupCode(), material);
                if (singlePrice == null) {
                    continue;
                }

                // ?????? = ?????? * ??????
                Double price = weight * Double.valueOf(singlePrice);

                // ???????????????????????????????????????
                PriceVO priceVO = new PriceVO();
                priceVO.setSingleWeight(weight.toString());
                priceVO.setSupName(iteminfo.getSupName());
                priceVO.setSupCode(iteminfo.getSupCode());
                priceVO.setSinglePrice(singlePrice);
                priceVO.setPrice(new BigDecimal(price.toString()).setScale(1, BigDecimal.ROUND_HALF_UP));
                priceVOS.add(priceVO);

                priceVO.setHeightRemain(heightRemain.toString());
                priceVO.setInnerRemain(innerRemain.toString()); // ????????????
                priceVO.setOuterRemain(outerRemain.toString()); // ????????????

                itemInfoDTO.setSupAndPriceList(priceVOS);
            }

            // ?????????????????????????????????????????????supType?????? 1
            if ((itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                itemInfoDTO.setSupColorType("1");
            } else {
                itemInfoDTO.setSupColorType("0");
            }
            // ?????????????????????
            if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                itemInfoDTOS.add(itemInfoDTO);
            }
        }

        return itemInfoDTOS;
    }

    /**
     * ???????????????
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
        Double bigOuterSize = 0D;  // ????????????
        Double bigHeightSize = 0D; // ????????????
        Double smallOuterSize = 0D;  //????????????
        Double totalHeightSize = 0D; // ?????????
        Double innerSize = 0D;  // ????????????
        Double outerSize = 0D;   // ????????????
        Double heightSize = 0D;   // ????????????
        Double outSizeToSearch = 0D; // ??????-b

        Double height = 0D; // ??????
        Double waijing = 0D; // ??????
        Double neijing = 0D; // ??????

        // ??????????????????
        ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = this.baseMapper.selectBasicItemInfoOfDJ(u9Prs.getItemCode());
        if (itemInfoEntityBasOfDJ == null) {
            return itemInfoDTOS;
        }

        if (itemInfoEntityBasOfDJ.getBigOuterSize() != null && !itemInfoEntityBasOfDJ.getBigOuterSize().isEmpty()) {
            bigOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigOuterSize()).doubleValue();  // ????????????
        }
        if (itemInfoEntityBasOfDJ.getBigHeightSize() != null && !itemInfoEntityBasOfDJ.getBigHeightSize().isEmpty()) {
            bigHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getBigHeightSize()).doubleValue();  // ????????????
        }
        if (itemInfoEntityBasOfDJ.getSmallOuterSize() != null && !itemInfoEntityBasOfDJ.getSmallOuterSize().isEmpty()) {
            smallOuterSize = Double.valueOf(itemInfoEntityBasOfDJ.getSmallOuterSize()).doubleValue();// ????????????
        }
        if (itemInfoEntityBasOfDJ.getTotalHeightSize() != null && !itemInfoEntityBasOfDJ.getTotalHeightSize().isEmpty()) {
            totalHeightSize = Double.valueOf(itemInfoEntityBasOfDJ.getTotalHeightSize()).doubleValue(); // ?????????
        }
        if (itemInfoEntityBasOfDJ.getInnerSize() != null && !itemInfoEntityBasOfDJ.getInnerSize().isEmpty()) {
            innerSize = Double.valueOf(itemInfoEntityBasOfDJ.getInnerSize()).doubleValue();// ??????
        }
        if (itemInfoEntityBasOfDJ.getOuterSize() != null && !itemInfoEntityBasOfDJ.getOuterSize().isEmpty()) {
            outerSize = Double.valueOf(itemInfoEntityBasOfDJ.getOuterSize()).doubleValue();  // ??????
        }
        if (itemInfoEntityBasOfDJ.getHeightSize() != null && !itemInfoEntityBasOfDJ.getHeightSize().isEmpty()) {
            heightSize = Double.valueOf(itemInfoEntityBasOfDJ.getHeightSize()).doubleValue();  // ??????
        }

        String formulaType = itemInfoEntityBasOfDJ.getType();
        if ("A".equals(formulaType)) { //???????????????
            outSizeToSearch = bigOuterSize;
            height = totalHeightSize;
            waijing = bigOuterSize;
            neijing = innerSize;
        } else { // ??????
            outSizeToSearch = outerSize;
            height = heightSize;
            waijing = outerSize;
            neijing = innerSize;
        }

        // ???????????? ??????????????????,????????????????????????????????????
        itemInfoEntityResCommonOfDJList = this.baseMapper.selectResItemInfoOfNew(outSizeToSearch, material, height);
        if (itemInfoEntityResCommonOfDJList.size() > 0) {
            itemInfoEntityResCommonOfDJList.stream().forEach(itemInfoEntityResCommonOfDJ -> {
                ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResCommonOfDJ, ItemInfoEntityResALLOfDJ.class);
                itemInfoEntityResALLOfDJList.add(iteminfo);
            });
        }

        // ????????????????????????
        if (itemInfoEntityResALLOfDJList.size() > 0) {
            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
            List<PriceVO> priceVOS = new ArrayList<>();
            // id?????????????????????????????????
            itemInfoDTO.setId(u9Prs.getId().toString());
            itemInfoDTO.setItemCode(u9Prs.getItemCode());
            itemInfoDTO.setItemName(u9Prs.getItemName());

            // ????????????????????????????????????
            for (ItemInfoEntityResALLOfDJ iteminfo : itemInfoEntityResALLOfDJList) {
                //????????????????????????,??????????????? ,???????????????????????????
                ArrayList<Double> list = new ArrayList<>();
                Double wallThickness = 0D;  // ??????
                Double newInner = 0D; // ?????????
                Double newInnerRemain = 0D; // ???????????????
                Double outerRemain = Double.valueOf(iteminfo.getOuterRemain()).doubleValue(); // ????????????
                Double innerRemain = Double.valueOf(iteminfo.getInnerRemain()).doubleValue(); // ????????????
                Double heightRemain = Double.valueOf(iteminfo.getHeightRemain()).doubleValue(); // ????????????
                Double weight = 0D;   //??????
                Double weightOfBasic = 0D;


                U9DjPriceDTO dto = new U9DjPriceDTO();
                dto.setInnerRemain(innerRemain);   // ????????????
                dto.setHeightRemain(heightRemain); // ????????????
                dto.setOuterRemain(outerRemain);   // ????????????

                dto.setInnerSize(innerSize);  // ????????????
                dto.setWeightOfBasic(weightOfBasic);
                dto.setBigHeightSize(bigHeightSize); // ????????????
                dto.setBigOuterSize(bigOuterSize); // ????????????
                dto.setSmallOuterSize(smallOuterSize); // ????????????
                dto.setHeightSize(heightSize); // ????????????
                dto.setMaterial(material); // ??????
                dto.setTotalHeightSize(totalHeightSize); // ?????????
                dto.setOuterSize(outerSize); // ????????????

                if ("MAT-004".equals(iteminfo.getSupCode())) {
                    // 1???????????????<=300
                    if (outerSize <= 300) {
                        // ????????????  ????????????<30     ((??????+????????????)-(??????-????????????)) /2
                        if ((((waijing + outerRemain) - (neijing - innerRemain)) / 2) < 30) {
                            wallThickness = 30D;
                            // ?????????  (??????+????????????)-??????*2
                            newInner = (waijing + outerRemain) - wallThickness * 2;
                            // ???????????????   ??????-?????????
                            newInnerRemain = neijing - newInner;
                            dto.setNewInner(newInner);  // ?????????
                            dto.setNewInnerRemain(newInnerRemain);   // ???????????????
                            // ????????????
                            itemInfoDTO = getPriceOfDJ(itemInfoEntityBasOfDJ, itemInfoDTO, iteminfo, priceVOS, dto);
                        } else {
                            newInnerRemain = 0D;
                            dto.setNewInner(newInner);  // ?????????
                            dto.setNewInnerRemain(newInnerRemain);   // ???????????????
                            // ??????????????????????????? ????????????????????????
                            itemInfoDTO = getPriceOfDJOld(itemInfoEntityBasOfDJ, itemInfoDTO, iteminfo, priceVOS, dto);
                        }
                    } else if (outerSize > 300) {

                        //????????????????????????????????????????????????????????????????????????????????????????????????
                        if (itemInfoEntityBasOfDJ.getType().equals("A")) { // --???????????????--
                            // ??????=(((????????????/2+????????????/2)^2 ??? (??????/2-????????????/2)^2 )) * PI * (????????????+????????????)+((????????????/2 + ????????????/2)^2-(??????/2 ??? ????????????/2)^2)*PI*(?????????-????????????))*7.856/1000000
                            if ((innerSize - innerRemain) >= 80) {
                                weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * PI * (totalHeightSize - bigHeightSize)) * 7.856 / 1000000;
                            } else {
                                weightOfBasic = (((bigOuterSize / 2 + outerRemain / 2) * (bigOuterSize / 2 + outerRemain / 2) * PI * (bigHeightSize + heightRemain) + ((smallOuterSize / 2 + outerRemain / 2) * (smallOuterSize / 2 + outerRemain / 2)) * PI * (totalHeightSize - bigHeightSize))) * 7.856 / 1000000;
                            }
                        } else { // --????????????--
                            // ?????? = PI*((????????????/2 +????????????/2)^2 ??? (????????????/2 - ????????????/2)^2)*(?????? + ????????????)*7.856/1000000
                            if ((innerSize - innerRemain) >= 80) {
                                weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
                            } else {
                                weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
                            }
                        }

                        log.debug("??????   {}", weightOfBasic);

                        // ??????????????????
                        wallThickness = ((waijing + outerRemain) - (neijing - innerRemain)) / 2;
                        // ?????? ??????????????????????????????10  ????????????200KG
                        /*if (wallThickness < ((waijing + outerRemain) * 0.1) && weightOfBasic < 200) {
                            // ??????
                            wallThickness = (waijing + outerRemain) * 0.1;
                            // ?????????  (??????+????????????)-??????*2
                            newInner = (waijing + outerRemain) - wallThickness * 2;
                            // ???????????????   ??????-?????????
                            newInnerRemain = neijing - newInner;
                            dto.setNewInner(newInner);  // ?????????
                            dto.setNewInnerRemain(newInnerRemain);   // ???????????????
                            // ????????????
                            itemInfoDTO = getPriceOfDJ(itemInfoEntityBasOfDJ, itemInfoDTO, iteminfo, priceVOS, dto);
                        } else */

                        if (wallThickness < 55) {
                            // ??????
                            wallThickness = Double.valueOf(55);
                            // ?????????  (??????+????????????)-??????*2
                            newInner = (waijing + outerRemain) - wallThickness * 2;
                            // ???????????????   ??????-?????????
                            newInnerRemain = neijing - newInner;
                            dto.setNewInner(newInner);  // ?????????
                            dto.setNewInnerRemain(newInnerRemain);   // ???????????????
                            // ????????????
                            itemInfoDTO = getPriceOfDJ(itemInfoEntityBasOfDJ, itemInfoDTO, iteminfo, priceVOS, dto);

                        } else {
                            newInnerRemain = 0D;
                            dto.setNewInner(newInner);  // ?????????
                            dto.setNewInnerRemain(newInnerRemain);   // ???????????????
                            // ??????????????????????????? ????????????????????????
                            itemInfoDTO = getPriceOfDJOld(itemInfoEntityBasOfDJ, itemInfoDTO, iteminfo, priceVOS, dto);
                        }
                    }
                }
            }
            // ?????????????????????????????????????????????supType?????? 1
            if ((itemInfoDTO.getSupAndPriceList() != null) && (itemInfoDTO.getSupAndPriceList().size() > 1)) {
                itemInfoDTO.setSupColorType("1");
            } else {
                itemInfoDTO.setSupColorType("0");
            }
            // ?????????????????????
            if (itemInfoDTO.getSupAndPriceList() != null && itemInfoDTO.getSupAndPriceList().size() > 0) {
                itemInfoDTOS.add(itemInfoDTO);
            }
        }
        return itemInfoDTOS;
    }


    /**
     * ???????????????????????????
     *
     * @return
     */
    private ItemInfoEntityOfXLJ handleOfDJOfXLJItem(ItemInfoEntityOfXLJ itemInfoEntityOfXLJ) {
        List<ItemInfoEntityResZYDOfDJ> itemInfoEntityResZYDOfDJList = new ArrayList<>();
        List<ItemInfoEntityResALLOfDJ> itemInfoEntityResALLOfDJList = new ArrayList<>();
        List<ItemInfoDTO> itemInfoDTOS = new ArrayList<>();
        Double innerSize = 0D;  // ????????????
        Double outerSize = 0D;   // ????????????
        Double heightSize = 0D;   // ????????????
        Double outSizeToSearch = 0D; // ??????-b

        Double height = 0D; // ??????
        Double waijing = 0D; // ??????
        Double neijing = 0D; // ??????

        if (itemInfoEntityOfXLJ.getInnerSize() != null && !itemInfoEntityOfXLJ.getInnerSize().isEmpty()) {
            innerSize = Double.valueOf(itemInfoEntityOfXLJ.getInnerSize()).doubleValue();// ??????
        }
        if (itemInfoEntityOfXLJ.getOuterSize() != null && !itemInfoEntityOfXLJ.getOuterSize().isEmpty()) {
            outerSize = Double.valueOf(itemInfoEntityOfXLJ.getOuterSize()).doubleValue();  // ??????
        }
        if (itemInfoEntityOfXLJ.getHeightSize() != null && !itemInfoEntityOfXLJ.getHeightSize().isEmpty()) {
            heightSize = Double.valueOf(itemInfoEntityOfXLJ.getHeightSize()).doubleValue();  // ??????
        }

        String formulaType = "B";
        outSizeToSearch = outerSize;
        height = heightSize;
        waijing = outerSize;
        neijing = innerSize;

        // ???????????? ??????????????????,????????????????????????????????????
        itemInfoEntityResZYDOfDJList = this.baseMapper.selectResItemInfoOfZyd(outSizeToSearch, itemInfoEntityOfXLJ.getMaterial());
        if (itemInfoEntityResZYDOfDJList.size() > 0) {
            itemInfoEntityResZYDOfDJList.stream().forEach(itemInfoEntityResZYDOfDJ -> {
                if ("MAT-045".equals(itemInfoEntityResZYDOfDJ.getSupCode())) {
                    ItemInfoEntityResALLOfDJ iteminfo = BeanUtil.copy(itemInfoEntityResZYDOfDJ, ItemInfoEntityResALLOfDJ.class);
                    itemInfoEntityResALLOfDJList.add(iteminfo);
                }
            });
        }

        // ????????????????????????
        if (itemInfoEntityResALLOfDJList.size() > 0) {
            ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
            List<PriceVO> priceVOS = new ArrayList<>();

            // ????????????????????????????????????
            for (ItemInfoEntityResALLOfDJ iteminfo : itemInfoEntityResALLOfDJList) {
                Double outerRemain = Double.valueOf(iteminfo.getOuterRemain()).doubleValue(); // ????????????
                Double innerRemain = Double.valueOf(iteminfo.getInnerRemain()).doubleValue(); // ????????????
                Double heightRemain = Double.valueOf(iteminfo.getHeightRemain()).doubleValue(); // ????????????

                itemInfoEntityOfXLJ.setOuterRes(outerRemain.toString());
                itemInfoEntityOfXLJ.setInnerRes(innerRemain.toString());
                itemInfoEntityOfXLJ.setHeightRes(heightRemain.toString());

                itemInfoEntityOfXLJ.setOuterSize(String.valueOf(outerSize + outerRemain));
                itemInfoEntityOfXLJ.setInnerSize(String.valueOf(innerSize - innerRemain));
                itemInfoEntityOfXLJ.setHeightSize(String.valueOf(heightSize + heightRemain));

                Double weight = 0D;
                Double weightOfBasic = 0D;

                // ?????? = PI*((????????????/2 +????????????/2)^2 ??? (????????????/2 - ????????????/2)^2)*(?????? + ????????????)*7.856/1000000 [????????????]
                if ((innerSize - innerRemain) >= 80) {
                    weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2) - (innerSize / 2 - innerRemain / 2) * (innerSize / 2 - innerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
                } else {
                    weightOfBasic = PI * ((outerSize / 2 + outerRemain / 2) * (outerSize / 2 + outerRemain / 2)) * (heightSize + heightRemain) * 7.856 / 1000000;
                }

                BigDecimal b = new BigDecimal(weightOfBasic);
                weight = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                itemInfoEntityOfXLJ.setWeight(String.valueOf(weight));

                // ??????
                String singlePrice = this.baseMapper.selectSinglePrice(iteminfo.getSupCode(), itemInfoEntityOfXLJ.getMaterial());
                if (singlePrice == null) {
                    return itemInfoEntityOfXLJ;
                }
                itemInfoEntityOfXLJ.setSingleCost(singlePrice);

                // ?????? = ?????? * ??????
                Double price = weight * Double.valueOf(singlePrice);
                itemInfoEntityOfXLJ.setMaterialCost(price.toString());
            }
        }
        return itemInfoEntityOfXLJ;
    }


    /**
     * ??????????????????????????????
     *
     * @param itemInfoEntityBasOfDJ
     * @param itemInfoDTO
     * @param iteminfo
     * @param priceVOS
     * @param dto
     * @return
     */
    private ItemInfoDTO getPriceOfDJOld(ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ, ItemInfoDTO itemInfoDTO, ItemInfoEntityResALLOfDJ iteminfo, List<PriceVO> priceVOS, U9DjPriceDTO dto) {
        // ????????????
        Double weightOfBasic = 0D;
        Double weight = 0D;   //??????
        if (itemInfoEntityBasOfDJ.getType().equals("A")) { // --???????????????--
            // ??????=(((????????????/2+????????????/2)^2 ??? (??????/2-????????????/2)^2 )) * PI * (????????????+????????????)+((????????????/2 + ????????????/2)^2-(??????/2 ??? ????????????/2)^2)*PI*(?????????-????????????))*7.856/1000000
            if ((dto.getInnerSize() - dto.getInnerRemain()) >= 80) {
                weightOfBasic = (((dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) - (dto.getInnerSize() / 2 - dto.getInnerRemain() / 2) * (dto.getInnerSize() / 2 - dto.getInnerRemain() / 2)) * PI * (dto.getBigHeightSize() + dto.getHeightRemain()) + ((dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2) - (dto.getInnerSize() / 2 - dto.getInnerRemain() / 2) * (dto.getInnerSize() / 2 - dto.getInnerRemain() / 2)) * PI * (dto.getTotalHeightSize() - dto.getBigHeightSize())) * 7.856 / 1000000;
            } else {
                weightOfBasic = (((dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) * PI * (dto.getBigHeightSize() + dto.getHeightRemain()) + ((dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2)) * PI * (dto.getTotalHeightSize() - dto.getBigHeightSize()))) * 7.856 / 1000000;
            }
        } else { // --????????????--
            // ?????? = PI*((????????????/2 +????????????/2)^2 ??? (????????????/2 - ????????????/2)^2)*(?????? + ????????????)*7.856/1000000
            if ((dto.getInnerSize() - dto.getInnerRemain()) >= 80) {
                weightOfBasic = PI * ((dto.getOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getOuterSize() / 2 + dto.getOuterRemain() / 2) - (dto.getInnerSize() / 2 - dto.getInnerRemain() / 2) * (dto.getInnerSize() / 2 - dto.getInnerRemain() / 2)) * (dto.getHeightSize() + dto.getHeightRemain()) * 7.856 / 1000000;
            } else {
                weightOfBasic = PI * ((dto.getOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getOuterSize() / 2 + dto.getOuterRemain() / 2)) * (dto.getHeightSize() + dto.getHeightRemain()) * 7.856 / 1000000;
            }
        }

        BigDecimal b = new BigDecimal(weightOfBasic);
        weight = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        String singlePrice = this.baseMapper.selectSinglePrice(iteminfo.getSupCode(), dto.getMaterial()); //??????
        if (singlePrice == null) {
            return itemInfoDTO;
        }
        Double price = weight * Double.valueOf(singlePrice); // ?????? = ?????? * ??????
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
     * ???????????????  ???????????????
     *
     * @param itemInfoEntityBasOfDJ
     * @param itemInfoDTO
     * @param iteminfo
     * @param priceVOS
     * @param dto
     * @return
     */
    private ItemInfoDTO getPriceOfDJ(ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ, ItemInfoDTO itemInfoDTO, ItemInfoEntityResALLOfDJ iteminfo, List<PriceVO> priceVOS, U9DjPriceDTO dto) {

        // ????????????
        Double weightOfBasic = 0D;
        Double weight = 0D;   //??????
        if (itemInfoEntityBasOfDJ.getType().equals("A")) { // --???????????????--
            // ??????=(((????????????/2+????????????/2)^2 ??? (??????/2-????????????/2)^2 )) * PI * (????????????+????????????)+((????????????/2 + ????????????/2)^2-(??????/2 ??? ????????????/2)^2)*PI*(?????????-????????????))*7.856/1000000
            if ((dto.getInnerSize() - dto.getInnerRemain()) >= 80) {
                weightOfBasic = (((dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) - (dto.getInnerSize() / 2 - dto.getNewInnerRemain() / 2) * (dto.getInnerSize() / 2 - dto.getNewInnerRemain() / 2)) * PI * (dto.getBigHeightSize() + dto.getHeightRemain()) + ((dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2) - (dto.getInnerSize() / 2 - dto.getNewInnerRemain() / 2) * (dto.getInnerSize() / 2 - dto.getNewInnerRemain() / 2)) * PI * (dto.getTotalHeightSize() - dto.getBigHeightSize())) * 7.856 / 1000000;
            } else {
                weightOfBasic = (((dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getBigOuterSize() / 2 + dto.getOuterRemain() / 2) * PI * (dto.getBigHeightSize() + dto.getHeightRemain()) + ((dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getSmallOuterSize() / 2 + dto.getOuterRemain() / 2)) * PI * (dto.getTotalHeightSize() - dto.getBigHeightSize()))) * 7.856 / 1000000;
            }
        } else { // --????????????--
            // ?????? = PI*((????????????/2 +????????????/2)^2 ??? (????????????/2 - ????????????/2)^2)*(?????? + ????????????)*7.856/1000000
            if ((dto.getInnerSize() - dto.getInnerRemain()) >= 80) {
                weightOfBasic = PI * ((dto.getOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getOuterSize() / 2 + dto.getOuterRemain() / 2) - (dto.getInnerSize() / 2 - dto.getNewInnerRemain() / 2) * (dto.getInnerSize() / 2 - dto.getNewInnerRemain() / 2)) * (dto.getHeightSize() + dto.getHeightRemain()) * 7.856 / 1000000;
            } else {
                weightOfBasic = PI * ((dto.getOuterSize() / 2 + dto.getOuterRemain() / 2) * (dto.getOuterSize() / 2 + dto.getOuterRemain() / 2)) * (dto.getHeightSize() + dto.getHeightRemain()) * 7.856 / 1000000;
            }
        }

        BigDecimal b = new BigDecimal(weightOfBasic);
        weight = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        String singlePrice = this.baseMapper.selectSinglePrice(iteminfo.getSupCode(), dto.getMaterial()); //??????
        if (singlePrice == null) {
            return itemInfoDTO;
        }
        // ???????????????????????????
        BigDecimal inner = new BigDecimal(dto.getNewInnerRemain());
        dto.setNewInnerRemain(inner.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        Double price = weight * Double.valueOf(singlePrice); // ?????? = ?????? * ??????
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
            // ??????????????????
            ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = submitPriceReq.getItemInfoEntityBasOfDJList().get(0);
            if (this.baseMapper.selectBasicItemInfoOfDJ(itemInfoEntityBasOfDJ.getItemCode()) == null) {
                this.baseMapper.insertBasicItemInfoOfDJ(itemInfoEntityBasOfDJ);
            } else {
                this.baseMapper.updateBasicItemInfoOfDJ(itemInfoEntityBasOfDJ);
                String account = getUser().getAccount();
                String time = DateUtil.format(new Date(), "yyyy-MM-dd");
                this.baseMapper.deleteDjInfoReport(itemInfoEntityBasOfDJ.getItemCode(), account, time); // ????????????????????????????????????????????????????????????????????????????????????????????????
            }
        }
        return true;
    }


    @Override
    public boolean setBasicItemInfoOfXLJ(SubmitPriceReq submitPriceReq) {
        if (submitPriceReq.getItemInfoEntityBasOfXLJList() != null) {
            // ?????????????????????
            ItemInfoEntityBasOfXLJ itemInfoEntityBasOfXLJ = submitPriceReq.getItemInfoEntityBasOfXLJList().get(0);
            if (this.baseMapper.selectBasicItemInfoOfXLJ(itemInfoEntityBasOfXLJ.getItemCode()) == null) {
                this.baseMapper.insertBasicItemInfoOfXLJ(itemInfoEntityBasOfXLJ);
            } else {
                this.baseMapper.updateBasicItemInfoOfXLJ(itemInfoEntityBasOfXLJ);
                this.baseMapper.deleteXLJInfoReport(itemInfoEntityBasOfXLJ.getItemCode()); // ????????????????????????????????????????????????????????????????????????????????????????????????
            }
        }
        return true;
    }

    @Override
    public boolean setBasicItemInfoOfXLJRX(SubmitPriceReq submitPriceReq) {
        if (submitPriceReq.getItemInfoEntityBasOfXLJRXList() != null) {
            // ?????????RX????????????
            ItemInfoEntityBasOfXLJRX itemInfoEntityBasOfXLJ = submitPriceReq.getItemInfoEntityBasOfXLJRXList().get(0);

            if (this.baseMapper.selectBasicItemInfoOfXLJRX(itemInfoEntityBasOfXLJ.getItemCode()) == null) {
                this.baseMapper.insertBasicItemInfoOfXLJRX(itemInfoEntityBasOfXLJ);
            } else {
                this.baseMapper.updateBasicItemInfoOfXLJRX(itemInfoEntityBasOfXLJ);
                this.baseMapper.deleteXLJInfoReport(itemInfoEntityBasOfXLJ.getItemCode()); // ????????????????????????????????????????????????????????????????????????????????????????????????
            }
        }
        return true;
    }

    @Override
    public boolean setBasicItemInfoOfDZ(SubmitPriceReq submitPriceReq) {
        if (submitPriceReq.getItemInfoEntityBasOfDZList() != null) {
            // ??????????????????
            /*ItemInfoEntityBasOfXLJRX itemInfoEntityBasOfXLJ = submitPriceReq.getItemInfoEntityBasOfXLJRXList().get(0);

            if (this.baseMapper.selectBasicItemInfoOfXLJRX(itemInfoEntityBasOfXLJ.getItemCode()) == null) {
                this.baseMapper.insertBasicItemInfoOfXLJRX(itemInfoEntityBasOfXLJ);
            } else {
                this.baseMapper.updateBasicItemInfoOfXLJRX(itemInfoEntityBasOfXLJ);
                this.baseMapper.deleteXLJInfoReport(itemInfoEntityBasOfXLJ.getItemCode()); // ????????????????????????????????????????????????????????????????????????????????????????????????
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
            // ??????????????????
            ItemInfoEntityBasOfDJ itemInfoEntityBasOfDJ = submitPriceReq.getItemInfoEntityBasOfDJList().get(0);
            if (itemInfoEntityBasOfDJ.getItemCode() == null || itemInfoEntityBasOfDJ.getItemCode().isEmpty()) {
                String itemCode = this.baseMapper.getItemInfoByItemName(itemInfoEntityBasOfDJ.getItemName());
                if (itemCode == null) {
                    throw new RuntimeException("?????????????????? ????????????????????????????????? ????????????????????????");
                }
                itemInfoEntityBasOfDJ.setItemCode(itemCode);
            }
            if (itemInfoEntityBasOfDJ.getItemName() == null || itemInfoEntityBasOfDJ.getItemName().isEmpty()) {
                String itemName = this.baseMapper.getItemInfoByItemCode(itemInfoEntityBasOfDJ.getItemCode());
                if (itemName == null) {
                    throw new RuntimeException("?????????????????? ?????????????????????????????? ???????????????????????????");
                }
                itemInfoEntityBasOfDJ.setItemName(itemName);
            }
            this.baseMapper.deleteOldBasicDJInfo(itemInfoEntityBasOfDJ.getItemCode());
            this.baseMapper.insertBasicItemInfoOfDJ(itemInfoEntityBasOfDJ);
            this.getItemInfoOfDJVO(itemInfoEntityBasOfDJ.getItemCode(), itemInfoEntityBasOfDJ.getItemName());
            Integer count = this.baseMapper.itemCodeOfDJIsExisted(itemInfoEntityBasOfDJ.getItemCode());
            if (count == 0) { // ?????????????????????????????????
                throw new RuntimeException("??????????????????????????? ??????????????????,???????????????????????????????????????");
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addBasicItemInfoOfXLJ(SubmitPriceReq submitPriceReq) {
        String notSplitTypes = "??????,?????????,?????????,??????,??????,????????????,??????";

        if (submitPriceReq.getItemInfoEntityBasOfXLJList() != null) {
            // ??????????????????
            ItemInfoEntityBasOfXLJ itemInfoEntityBasOfXLJ = submitPriceReq.getItemInfoEntityBasOfXLJList().get(0);
            if (itemInfoEntityBasOfXLJ.getItemCode() == null || itemInfoEntityBasOfXLJ.getItemCode().isEmpty()) {
                String itemCode = this.baseMapper.getItemInfoByItemName(itemInfoEntityBasOfXLJ.getItemName());
                if (itemCode == null) {
                    throw new RuntimeException("?????????????????? ????????????????????????????????? ????????????????????????");
                }
                itemInfoEntityBasOfXLJ.setItemCode(itemCode);
            }
            if (itemInfoEntityBasOfXLJ.getItemName() == null || itemInfoEntityBasOfXLJ.getItemName().isEmpty()) {
                String itemName = this.baseMapper.getItemInfoByItemCode(itemInfoEntityBasOfXLJ.getItemCode());
                if (itemName == null) {
                    throw new RuntimeException("?????????????????? ?????????????????????????????? ???????????????????????????");
                }
                itemInfoEntityBasOfXLJ.setItemName(itemName);
            }

            if (notSplitTypes.indexOf(itemInfoEntityBasOfXLJ.getItemName().split("-")[0]) < 0) {
                throw new RuntimeException(itemInfoEntityBasOfXLJ.getItemCode() + " - " + itemInfoEntityBasOfXLJ.getItemName() + " : ???????????????????????????????????????????????????");
            }


            this.baseMapper.deleteOldBasicXLJInfo(itemInfoEntityBasOfXLJ.getItemCode());
            this.baseMapper.insertBasicItemInfoOfXLJ(itemInfoEntityBasOfXLJ);
            this.getItemInfoOfXLJVO(itemInfoEntityBasOfXLJ.getItemCode(), itemInfoEntityBasOfXLJ.getItemName());

            Integer count = this.baseMapper.itemCodeOfXLJIsExisted(itemInfoEntityBasOfXLJ.getItemCode());
            if (count == 0) { // ?????????????????????????????????
                throw new RuntimeException("??????????????????????????? ??????????????????,???????????????????????????????????????");
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
                    item.setType("???????????????");
                    item.setOuterSize("");
                    item.setHeightSize("");
                } else {
                    item.setType("????????????");
                    item.setBigOuterSize("");
                    item.setBigHeightSize("");
                    item.setSmallOuterSize("");
                    item.setTotalHeightSize("");
                }

                if (item.getRes().equals("fm")) {
                    item.setRes("??????");
                } else if (item.getRes().equals("zyd")) {
                    item.setRes("?????????");
                } else {
                    item.setRes("?????????");
                }
            });
        }
        ExcelUtils.defaultExport(itemInfoEntityDJReportList, ItemInfoEntityDJReport.class, "?????????????????????" + DateUtil.formatDate(new Date()), response);
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
            throw new RuntimeException("?????????????????????");
        }
        ExcelUtils.defaultExport(itemInfoEntityXLJReportList, ItemInfoEntityOfXLJ.class, "????????????????????????" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public void exportAllItemInfoLZQ(SubmitPriceReq submitPriceReq, HttpServletResponse response) {

        List<ItemInfoEntityOfLZQ> itemInfoEntityXLJReportList = this.baseMapper.selectLZQReportList(submitPriceReq);

        if (itemInfoEntityXLJReportList.get(0) == null) {
            throw new RuntimeException("?????????????????????");
        }
        ExcelUtils.defaultExport(itemInfoEntityXLJReportList, ItemInfoEntityOfLZQ.class, "????????????????????????" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public void exportAllItemInfoQZNew(SubmitPriceReq submitPriceReq, HttpServletResponse response) {
        List<ItemInfoEntityOfQZNew> itemInfoEntityOfQZNews = this.baseMapper.selectQZReportList(submitPriceReq);

        if (itemInfoEntityOfQZNews.get(0) == null) {
            throw new RuntimeException("?????????????????????");
        }
        ExcelUtils.defaultExport(itemInfoEntityOfQZNews, ItemInfoEntityOfQZNew.class, "?????????????????????" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public void exportAllItemInfoDZ(SubmitPriceReq submitPriceReq, HttpServletResponse response) {

        List<ItemInfoEntityOfDZ> itemInfoEntityDZReportList = this.baseMapper.selectDZReportList(submitPriceReq);

        if (itemInfoEntityDZReportList.get(0) != null) {
        } else {
            throw new RuntimeException("?????????????????????");
        }

        ExcelUtils.defaultExport(itemInfoEntityDZReportList, ItemInfoEntityOfDZ.class, "?????????????????????" + DateUtil.formatDate(new Date()), response);
    }


    @Override
    public void exportAllItemInfoFL(SubmitPriceReq submitPriceReq, HttpServletResponse response) {

        List<ItemInfoEntityOfFL> itemInfoEntityFLReportList = this.baseMapper.selectFLReportList(submitPriceReq);

        if (itemInfoEntityFLReportList.get(0) != null) {
        } else {
            throw new RuntimeException("?????????????????????");
        }

        ExcelUtils.defaultExport(itemInfoEntityFLReportList, ItemInfoEntityOfFL.class, "?????????????????????" + DateUtil.formatDate(new Date()), response);
    }


    /**
     * ?????????????????? - ??????
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
                    item.setType("???????????????");
                    item.setOuterSize("");
                    item.setHeightSize("");
                } else {
                    item.setType("????????????");
                    item.setBigOuterSize("");
                    item.setBigHeightSize("");
                    item.setSmallOuterSize("");
                    item.setTotalHeightSize("");
                }

                if (item.getRes().equals("fm")) {
                    item.setRes("??????");
                } else if (item.getRes().equals("zyd")) {
                    item.setRes("?????????");
                } else {
                    item.setRes("?????????");
                }
            });
        }
        ExcelUtils.defaultExport(autoOrderOfDJS, AutoOrderOfDJ.class, "????????????????????????" + DateUtil.formatDate(new Date()), response);
    }


    @Override
    public IPage<AutoOrderOfXLJ> autoOrderOfXLJ(IPage<AutoOrderOfXLJ> page, AutoOrderOfXLJ autoOrderOfXLJ) {
        IPage<AutoOrderOfXLJ> autoOrderOfXLJIPage = this.baseMapper.getAutoOrderOfXLJ(page, autoOrderOfXLJ);
        return autoOrderOfXLJIPage;
    }


    @Override
    public void exportAutoOrderOfXLJ(AutoOrderOfXLJ autoOrderOfXLJ, HttpServletResponse response) {
        List<AutoOrderOfXLJ> autoOrderOfXLJS = this.baseMapper.selectAutoOrderOfXLJList(autoOrderOfXLJ);
        ExcelUtils.defaultExport(autoOrderOfXLJS, AutoOrderOfXLJ.class, "???????????????????????????" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public String handleExceptDataWithNoProject(List<U9PrFromPhpDTO> u9list) {

        for (U9PrFromPhpDTO u9fromphp:u9list) {
            //?????????????????????PR
            Long prDate = Long.valueOf(u9fromphp.getPRDate());
            Date date = cn.hutool.core.date.DateUtil.date(prDate*1000);
            if(!cn.hutool.core.date.DateUtil.isSameDay(new Date(),date)){
                continue;
            }

            //17 20 ?????????
            if(u9fromphp.getItemCode().indexOf("17")==0||u9fromphp.getItemCode().indexOf("20")==0){
                continue;
            }

            //????????????????????????
            int projectnum = this.baseMapper.SelectIsProject(u9fromphp.getPRNO()+"-"+u9fromphp.getPRLineNo());
            if(projectnum==0){
                //?????????????????????????????? php??????
                /*U9PrFromPhpDTO u9PrFromPhpDTO = this.baseMapper.selectPrNoProject(u9fromphp);
                if (u9PrFromPhpDTO == null) {
                    this.baseMapper.insertPrNoProject(u9fromphp);
                }*/
                return "?????????";
            }

        }
        return "?????????";
    }


    public static void main(String[] args) throws ParseException {
        int i = WillDateUtil.daysBetween(new Date(),  DateUtil.parse("2022-08-02", "yyyy-MM-dd"));
        System.out.println(i);
    }


    @Override
    public List<U9PrFromPhpDTO> handleExceptData(List<U9PrFromPhpDTO> u9list) {
        log.info("======================????????????PR????????????,??????{}?????????====================================",u9list.size());

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

            //????????????
            Item item = itemService.getByCode(u9fromphp.getItemCode());

            int sd = 0;
            if (item != null) {
                u9fromphp.setAbc_type(item.getSOrN());
                sd = item.getPurchAfterDate() + item.getPurchBeforeDate() + item.getPurchDisposeDate();//????????????
            }else {
                continue;
            }

            u9fromphp.setBzReqDate(Long.valueOf(sd));
            //?????? ??????????????????  ??????????????????
            PrFromOracleDTO prFromOracleDTO = this.baseMapper.SelectSrmRpt(u9fromphp.getPRNO() + "-" + Integer.toString(u9fromphp.getPRLineNo()));
            if (prFromOracleDTO != null) {
                u9fromphp.setPlan_date(prFromOracleDTO.getPlandate());
            }


            //??????????????????
            StringBuilder sb = new StringBuilder();

            //begin
            Long itemid = this.baseMapper.selectItemIdByCode(u9fromphp.getItemCode());
            if (itemid == null) {
               continue;
            }
            int ReplaceNum = this.baseMapper.SelectIsReplace(itemid);

            //??????bom???????????????
            if (ReplaceNum > 0) {
                sb.append("bom?????????,");
                //????????????????????????????????????
                u9fromphp.setDuty_dept(DutyDeptEnum.??????.getName());
                u9fromphp.setHandle_dept(DutyDeptEnum.??????.getName());
                u9fromphp.setProblemDesc(sb.toString());
                interceptPR(u9fromphp, repU9List);


            } else {
                //sb.append("bom????????????->");
                int projectnum = this.baseMapper.SelectIsProject(u9fromphp.getPRNO()+"-"+u9fromphp.getPRLineNo());
                //??????????????????????????????
                if (projectnum > 0) {
                    //sb.append("????????????????????????->");

                    long diffdays = 0;
                    if (prFromOracleDTO != null) {
                        Date newdate = new Date();
                        //diffdays = (prFromOracleDTO.getNeedtime().getTime() - u9fromphp.getPrspDate().getTime()) / 24 / 60 / 60 / 1000;//????????????-??????
                        try {
                            diffdays = WillDateUtil.daysBetween(u9fromphp.getPrspDate(),prFromOracleDTO.getNeedtime());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    //???????????????????????????????????????????????? ??????????????????-?????? >????????????
                    if (diffdays >= Long.valueOf(sd)) {
                        //sb.append("??????????????????????????????????????????1->");
                        //???????????????SRM
                        SendPR(u9fromphp);

                    } else {
                        //sb.append("?????????????????????????????????????????????1->");
                        //????????????????????????????????????????????????????????????
                        PrFromOracleDTO prfromoracle = this.baseMapper.SelectQTDataByPrLn(u9fromphp.getPRNO()+"-"+u9fromphp.getPRLineNo());
                        String jhjq=prfromoracle.getXmjq()==null?"???":WillDateUtil.dateFormat(prfromoracle.getXmjq());
                        String pd = "????????????,?????????:" + prfromoracle.getZxmh() + " ???????????????" + jhjq +",???????????????";
                        u9fromphp.setProblemDesc2(pd);
                        //????????????????????????????????????
                        u9fromphp.setProblemDesc(sb.toString());
                        u9fromphp.setDuty_dept(DutyDeptEnum.??????.getName());
                        u9fromphp.setHandle_dept(DutyDeptEnum.??????.getName());
                        interceptPR(u9fromphp, repU9List);


                    }

                } else {
                    //sb.append("??????????????????????????????->");
                    //???????????????????????????
                    Double SafeStockNum = this.baseMapper.SelectIsSafeStock(itemid);
                    SafeStockNum = SafeStockNum == null ? 0 : SafeStockNum;
                    System.out.println("u9fromphp.getItemCode()"+itemid);
                    Double StockdiffNum = this.baseMapper.selectSafeStockDiff(itemid);
                    StockdiffNum = StockdiffNum == null ? 0 : StockdiffNum;
                    if (StockdiffNum > 0 && SafeStockNum > 0) {

                        sb.append("????????????,");

                        Long reqdate= u9fromphp.getRequireDate()*1000;
                        Date xqjq = new Date(reqdate);
                        long diffdays = 0;
                        if (xqjq != null) {
                            Date newdate = new Date();
                            //diffdays = (xqjq.getTime() - u9fromphp.getPrspDate().getTime()) / 24 / 60 / 60 / 1000;//????????????-??????
                            try {
                                diffdays = WillDateUtil.daysBetween(u9fromphp.getPrspDate(),xqjq);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                        }
                        //??????????????????????????????????????????????????? ????????????-?????? >????????????
                        if (diffdays >= Long.valueOf(sd)) {
                            //sb.append("?????????????????????????????????????????????2->");
                            sb.append("??????????????????????????????,");
                            //???????????????SRM
                            SendPR(u9fromphp);

                        } else {
                            //sb.append("????????????????????????????????????????????????2->");
                            sb.append("?????????????????????????????????,");
                            //???????????????????????????????????????
                            if (item.getPurchDisposeDate() == 0) {
                                sb.append("??????????????????????????????????????????,");
                                //?????????SRM
                                //???????????????????????????????????????????????????
                                //???????????????SRM
                                u9fromphp.setProblemDesc(sb.toString());
                                u9fromphp.setDuty_dept(DutyDeptEnum.??????.getName());
                                u9fromphp.setHandle_dept(DutyDeptEnum.??????.getName());
                                interceptPR(u9fromphp, repU9List);

                            } else {
                                sb.append("???????????????,IT????????????,");
                                //????????????????????????????????????
                                u9fromphp.setProblemDesc(sb.toString());
                                u9fromphp.setDuty_dept(DutyDeptEnum.????????????.getName());
                                u9fromphp.setHandle_dept(DutyDeptEnum.??????.getName());
                                interceptPR(u9fromphp, repU9List);

                            }
                        }


                    } else {
                        //sb.append("???????????????????????????->");
                        //????????????SRM

                        //???????????????????????????????????????????????????????????????????????????13????????????????????????
                        StringBuilder problemdesc = new StringBuilder();
                        List<PrFromOracleDTO> fcdata = new ArrayList<>();//??????????????????????????????
                        List<PrFromOracleDTO> dstreelist = this.baseMapper.selectDSTreeByItemId(itemid);
                        if (dstreelist.size() > 0) {
                            //????????????
                            for (PrFromOracleDTO gd : dstreelist) {
                                String parentdsinfo = gd.getParentdsinfo();
                                while (StringUtils.isNotBlank(parentdsinfo) && !"0".equals(parentdsinfo)) {
                                    PrFromOracleDTO dsinfo = this.baseMapper.selectDSInfo(parentdsinfo);//??????dsinfo
                                    if (dsinfo != null && StringUtils.isNotBlank(dsinfo.getProjectcode())) {//????????????????????????
                                        List<PrFromOracleDTO> qtdatalist = this.baseMapper.SelectQTDataByProject(dsinfo.getProjectcode(), Long.valueOf(dsinfo.getCode()));
                                        if (qtdatalist.size() > 0) {//????????????????????????????????????????????????

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
                                            //?????????????????????????????????????????????????????????
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
                                        //????????????13??????????????????
                                        PrFromOracleDTO parentdstree = this.baseMapper.selectDSTreeByDSinfo(parentdsinfo);
                                        if (parentdstree != null && StringUtils.isNotBlank(parentdstree.getParentdsinfo()) && !"0".equals(parentdstree.getParentdsinfo())) {
                                            parentdsinfo = parentdstree.getParentdsinfo();
                                        } else {
                                            break;
                                        }

                                    }

                                }

                            }
                            //??????pr??????>bom??????  ??????  pr?????????bom???????????????????????????
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
                            //????????????????????????????????????
                            if(fgAfterCompare!=null){
                                //sb.append("???????????????????????????????????????->");
                                String jhjq=fgAfterCompare.getXmjq()==null?"???":WillDateUtil.dateFormat(fgAfterCompare.getXmjq());
                                problemdesc.append("?????????" + fgAfterCompare.getGd()+" ?????????" + fgAfterCompare.getZxmh() + " ???????????????" + jhjq+ " ???????????????" + fgAfterCompare.getWlbh()  + ";");
                                u9fromphp.setProblemDesc2(problemdesc.toString());
                                if ("??????".equals(fgAfterCompare.getGylx())) {
                                    sb.append("?????????????????????" + fgAfterCompare.getGylx() + ",");
                                    //??????
                                    //????????????????????????????????????
                                    u9fromphp.setProblemDesc(sb.toString());
                                    u9fromphp.setDuty_dept(DutyDeptEnum.????????????.getName());
                                    u9fromphp.setHandle_dept(DutyDeptEnum.??????.getName());
                                    interceptPR(u9fromphp, repU9List);


                                } else if ("?????????".equals(fgAfterCompare.getGylx())) {
                                    sb.append("?????????????????????" + fgAfterCompare.getGylx() + ",");
                                    //?????????
                                    //????????????????????????????????????
                                    u9fromphp.setProblemDesc(sb.toString());
                                    u9fromphp.setDuty_dept(DutyDeptEnum.??????.getName());
                                    u9fromphp.setHandle_dept(DutyDeptEnum.??????.getName());
                                    interceptPR(u9fromphp, repU9List);
                                } else {
                                    sb.append("?????????????????????" + fgAfterCompare.getGylx() + ",");
                                    //????????????????????????????????????
                                    u9fromphp.setProblemDesc(sb.toString());
                                    u9fromphp.setDuty_dept(DutyDeptEnum.??????.getName());
                                    u9fromphp.setHandle_dept(DutyDeptEnum.??????.getName());
                                    interceptPR(u9fromphp, repU9List);

                                }

                            }else{
                                //sb.append("??????????????????????????????????????????->");
                                //????????????????????????????????????
                                sb.append("?????????????????????????????????,");
                                u9fromphp.setProblemDesc(sb.toString());
                                u9fromphp.setDuty_dept(DutyDeptEnum.??????.getName());
                                u9fromphp.setHandle_dept(DutyDeptEnum.??????.getName());
                                interceptPR(u9fromphp, repU9List);

                            }



                        } else {
                            sb.append("?????????????????????????????????,");
                            //????????????????????????????????????
                            u9fromphp.setProblemDesc(sb.toString());
                            u9fromphp.setDuty_dept(DutyDeptEnum.??????.getName());
                            u9fromphp.setHandle_dept(DutyDeptEnum.??????.getName());
                            interceptPR(u9fromphp, repU9List);
                        }
                        u9fromphp.setProblemDesc2(problemdesc.toString());


                    }

                }

            }


        }

        long end = System.currentTimeMillis();
        log.info("======================??????{}?????????==============================??????{}}ms",u9list.size(),(end - begin));
        log.info("======================??????{}?????????==============================",repU9List.size());
        log.info(repU9List.toString());
        log.info("======================??????{}?????????==============================",repU9List.size());

        return repU9List;
    }

    @Override
    public List<U9PrFromPhpDTO> handleExceptData2(List<U9PrFromPhpDTO> u9list) {
        long begin = System.currentTimeMillis();
        log.info("======================????????????PR????????????,??????{}?????????====================================",u9list.size());

        List<U9PrFromPhpDTO> repU9List = new ArrayList<>();

        for (U9PrFromPhpDTO u9fromphp : u9list){

            u9fromphp.setPrspDate(u9fromphp.getPrspDate());

            if(u9fromphp.getItemCode().indexOf("17")==0||u9fromphp.getItemCode().indexOf("20")==0){
                continue;
            }

            int projectnum = this.baseMapper.SelectIsProject(u9fromphp.getPRNO()+"-"+u9fromphp.getPRLineNo());

            if (projectnum > 0) {

            }else{

                //??????????????????
                Item byCode = itemService.getByCode(u9fromphp.getItemCode());
                if(byCode.getStockLowerLimit().compareTo(new BigDecimal("0"))>0){
                    //????????????

                }else {
                    //??????
                    u9fromphp.setProblemDesc("??????PR");
                    interceptPR(u9fromphp, repU9List);

                }


            }





        }

        long end = System.currentTimeMillis();
        log.info("======================??????{}?????????==============================??????{}}ms",u9list.size(),(end - begin));
        log.info("======================??????{}?????????==============================",repU9List.size());
        log.info(repU9List.toString());
        log.info("======================??????{}?????????==============================",repU9List.size());

        return repU9List;


    }

    /**
     * ?????????SRM
     * <p>
     * 2.??????????????????
     * 3.?????????????????????
     * 1.atw_u9_pr??????isdelete?????????1
     */
    @Transactional
    public void interceptPR(U9PrFromPhpDTO u9fromphp, List<U9PrFromPhpDTO> repU9List) {

        repU9List.add(u9fromphp);

        U9PrEntity u9PrEntity = this.baseMapper.getPrLn(u9fromphp.getPRNO(), u9fromphp.getPRLineNo());
        U9PrEntityEx u9PrEntityEx = null;
        if (u9PrEntity != null) {
            //2.??????????????????
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
            u9PrEntityEx.setProblemAnal("");//??????
            u9PrEntityEx.setSolution("");//??????
            //u9PrEntityEx.setFinishDate(new Date());//??????
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
            //3.?????????????????????
            u9PrEntityExMapper.insert(u9PrEntityEx);
            //1.atw_u9_pr??????isdelete?????????1
            //this.baseMapper.deletedPrByPrLn(u9fromphp.getPRNO(), u9fromphp.getPRLineNo());
        }




    }

    @Override
    @Transactional
    public void SendPR(U9PrFromPhpDTO u9fromphp) {

        /*//??????PR
        this.u9PrEntityExMapper.freePrByPrLn(u9fromphp.getPRNO(), u9fromphp.getPRLineNo());

        //??????ExPR
        this.u9PrEntityExMapper.deletedExPrByPrLn(u9fromphp.getPRNO(), u9fromphp.getPRLineNo(),"????????????"+WillDateUtil.dateFormat(new Date()));*/


    }


    /**
     * ?????????atw_u9_pr_no_project???????????????PR
     */
    @Override
    public void releaseNoProPR() {

        List<U9PrEntityNoPro> u9PrEntityNoPros = u9PrEntityNoProMapper.selectListByReq();
        //??????????????????????????????
        for (U9PrEntityNoPro item:u9PrEntityNoPros) {
            //????????????????????????????????????
            String u9Data = item.getU9Data();
            JSONArray jsonArray = JSONObject.parseArray(u9Data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            jsonObject.put("repush","Y");
            String sendermsg=JSON.toJSONString(jsonArray);
            String post = HttpUtil.post(bizapiUrl+"/OpenApi/insertPR", sendermsg);
            JSONObject rep = JSONObject.parseObject(post);
            String code = rep.getString("code");
            if ("2000".equals(code)) {
                        //????????????????????????????????????????????????PR
                        u9PrEntityNoProMapper.releaseNoProPRByPrLn(item.getPrCode(), item.getPrLn());
                        log.info("?????????atw_u9_pr_no_project???????????????PR:"+item.getPrCode()+"-"+item.getPrLn());
            }
        }
    }


    /**
     * APS??????????????????????????????????????????????????????????????????????????????
     */
    @Override
    public void releaseNoProPR2() {

        List<U9PrEntityNoPro> u9PrEntityNoPros = u9PrEntityNoProMapper.selectListByReq();
        //???????????????????????????
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
                    //???????????????????????????????????????????????????PR????????????????????????
                    u9PrEntityNoProMapper.deleteNoProPRByPrLn(item.getPrCode(), item.getPrLn());
                    log.info("?????????atw_u9_pr_no_project???????????????PR:"+item.getPrCode()+"-"+item.getPrLn());
                }

            }

        }
    }


    /**
     * ?????????atw_u9_pr_no_project???????????????PR
     */
    @Override
    public Boolean releaseNoProPRByPrLn( List<U9PrEntityNoPro> u9PrEntityNoPros) {

        //??????????????????????????????
        for (U9PrEntityNoPro item:u9PrEntityNoPros) {
            U9PrEntityNoPro u9PrEntityNoPro = u9PrEntityNoProMapper.selectListByPrLn(item.getPrCode(),item.getPrLn());
            //????????????????????????????????????
            String u9Data = u9PrEntityNoPro.getU9Data();
            JSONArray jsonArray = JSONObject.parseArray(u9Data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            jsonObject.put("repush","Y");
            String sendermsg=JSON.toJSONString(jsonArray);
            String post = HttpUtil.post(bizapiUrl+"/OpenApi/insertPR", sendermsg);
            JSONObject rep = JSONObject.parseObject(post);
            String code = rep.getString("code");
            if ("2000".equals(code)) {
                //????????????????????????????????????????????????PR
                u9PrEntityNoProMapper.releaseNoProPRByPrLn(u9PrEntityNoPro.getPrCode(), u9PrEntityNoPro.getPrLn());
                log.info("?????????atw_u9_pr_no_project???????????????PR:"+u9PrEntityNoPro.getPrCode()+"-"+u9PrEntityNoPro.getPrLn());
            }
        }
        return true;
    }


}
