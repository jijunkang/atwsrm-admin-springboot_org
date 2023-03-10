package org.springblade.modules.po.service.impl;

import com.aliyun.oss.common.utils.AuthUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import com.sun.org.apache.xerces.internal.xs.StringList;
import io.swagger.models.auth.In;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springblade.common.utils.ItemAnalysisUtil;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.outpr.entity.OutPrReportFormsEntity;
import org.springblade.modules.outpr.entity.OutPrWxZJEntity;
import org.springblade.modules.outpr.mapper.OutPrWxZJMapper;
import org.springblade.modules.po.dto.PoReceiveDTO;
import org.springblade.modules.po.dto.PoReceiveExcelDTO;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.entity.PoReceiveEntity;
import org.springblade.modules.po.mapper.PoItemMapper;
import org.springblade.modules.po.mapper.PoReceiveMapper;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.po.service.IPoReceiveService;
import org.springblade.modules.po.vo.PoReceiveVO;
import org.springblade.modules.pr.entity.ItemInfoEntityOfZDJ;
import org.springblade.modules.supplier.entity.SupplierSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static org.springblade.core.secure.utils.AuthUtil.getUser;

/**
 * ???????????????
 *
 * @author Will
 */
@Service
public
class PoReceiveServiceImpl extends BaseServiceImpl<PoReceiveMapper, PoReceiveEntity> implements IPoReceiveService {

    @Autowired
    IPoItemService poItemService;

    @Autowired
    PoItemMapper poItemMapper;

    @Autowired
    OutPrWxZJMapper outPrWxZJMapper;

    @Value("${sqlServerOfMes.url}")
    private String sqlServerUrl;

    @Value("${sqlServerOfMes.user}")
    private String sqlServerUser;

    @Value("${sqlServerOfMes.password}")
    private String sqlServerPassword;

    @Value("${sqlServerOfMes.driver}")
    private String sqlServerDriver;

    @Value("${oracle.url}")
    private String oracleUrl;

    @Value("${oracle.user}")
    private String oracleUser;

    @Value("${oracle.password}")
    private String oraclePassword;

    @Value("${oracle.driver}")
    private String oracleDriver;

    @Override
    public QueryWrapper<PoReceiveEntity> getQueryWrapper(PoReceiveEntity poReceive) {
        QueryWrapper<PoReceiveEntity> queryWrapper = Wrappers.<PoReceiveEntity>query().like("sup_code", poReceive.getSupCode())
            .like("rcv_code", poReceive.getRcvCode())
            .groupBy("rcv_code");
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R createAsR(List<PoReceiveDTO> poReceiveList) {



        HashMap<String, List<PoReceiveDTO>> supMap = new HashMap<>();
        List<PoReceiveDTO> poReceiveListOfAtw = new ArrayList<>();
        List<PoReceiveDTO> poReceiveListOfOthers = new ArrayList<>();

        for (PoReceiveDTO poReceiveDTO : poReceiveList) {
            //???????????????
            checkOrganize(poReceiveList, poReceiveDTO);

            //?????????????????????
            //checkOrder(poReceiveList, poReceiveDTO);


            String itemName = poReceiveDTO.getItemName();

            //??????????????????orgcode
            poReceiveDTO.setOrgCode(StringUtil.isBlank(poReceiveDTO.getOrgCode())?"001":poReceiveDTO.getOrgCode());

            // ?????????????????????????????? ????????????
            if (itemName.indexOf("??????") >= 0) {
                ItemInfoEntityOfZDJ itemInfoEntityOfZDJ = ItemAnalysisUtil.getItemInfoOfZhuDuanJian(itemName);
                // ??????????????????????????????????????????????????????
                if (itemInfoEntityOfZDJ.getPound() != null) {
                    List<OutPrWxZJEntity> outPrWxZJEntityList = outPrWxZJMapper.getOutPrWxZJInfo(itemInfoEntityOfZDJ,poReceiveDTO.getSupName());
                    // ????????????????????????????????????????????????
                    if (outPrWxZJEntityList.size()>0 && outPrWxZJEntityList.get(0).getIsOut().equals("Y")) {
                        poReceiveDTO.setOutSupCode(outPrWxZJEntityList.get(0).getOutSupCode());
                        poReceiveDTO.setOutSupName(outPrWxZJEntityList.get(0).getOutSupName());
                        poReceiveDTO.setProcess(outPrWxZJEntityList.get(0).getRemark());
                        poReceiveListOfOthers.add(poReceiveDTO);
                    } else {
                        // ?????????ATW
                        poReceiveListOfAtw.add(poReceiveDTO);
                    }
                } else {
                    // ?????????ATW
                    poReceiveListOfAtw.add(poReceiveDTO);
                }
            } else {
                // ?????????ATW
                poReceiveListOfAtw.add(poReceiveDTO);
            }
        }

        String rcvNo = "";
        // ?????????
        if(poReceiveListOfAtw.size() > 0){
            List<PoReceiveDTO> poReceiveListOfAtwForIn = new ArrayList<>();
            List<PoReceiveDTO> poReceiveListOfAtwForOut = new ArrayList<>();
            poReceiveListOfAtw.forEach(item->{
                if(item.getTemplateType().indexOf("W")>-1) {
                    poReceiveListOfAtwForOut.add(item);
                } else {
                    poReceiveListOfAtwForIn.add(item);
                }
            });

            // 2????????????????????????
            if(poReceiveListOfAtwForOut.size()>0 && poReceiveListOfAtwForIn.size()>0) {
                Integer seq1 = getMaxSeq() + 1;
                String rcvNum1 = "DO"+ poReceiveListOfAtwForOut.get(0).getOrgCode() + DateUtil.format(new Date(), "yyyyMMdd") + String.format("%03d", seq1);
                String rcvNo1 = getAndSaveDo(poReceiveListOfAtwForOut, "N", rcvNum1, seq1,new Date(),SecureUtil.getUserId(),false,true,20);

                Integer seq2 = getMaxSeq() + 1;
                String rcvNum2 = "DO"+poReceiveListOfAtwForIn.get(0).getOrgCode() + DateUtil.format(new Date(), "yyyyMMdd") + String.format("%03d", seq2);
                String rcvNo2 = getAndSaveDo(poReceiveListOfAtwForIn, "N", rcvNum2, seq2,new Date(),SecureUtil.getUserId(),false,true,20);

                rcvNo = rcvNo1 + "," + rcvNo2;
            } else { // ???????????????
                Integer seq = getMaxSeq() + 1;
                String rcvNum = "DO" +poReceiveListOfAtw.get(0).getOrgCode()+ DateUtil.format(new Date(), "yyyyMMdd") + String.format("%03d", seq);
                rcvNo = getAndSaveDo(poReceiveListOfAtw, "N", rcvNum, seq,new Date(),SecureUtil.getUserId(),false,true,20);
            }
        }

        // ?????????
        if (poReceiveListOfOthers.size() > 0) {
            Integer seqOfWX = getMaxSeq() + 1;
            String rcvNumOfWX = "DO"+poReceiveListOfOthers.get(0).getOrgCode() + DateUtil.format(new Date(), "yyyyMMdd") + String.format("%03d", seqOfWX);
            if(rcvNo.isEmpty()){
                rcvNo = getAndSaveDo(poReceiveListOfOthers, "Y", rcvNumOfWX, seqOfWX,new Date(),SecureUtil.getUserId(),false,true,20);
            } else {
                rcvNo = rcvNo + ',' + getAndSaveDo(poReceiveListOfOthers, "Y", rcvNumOfWX, seqOfWX,new Date(),SecureUtil.getUserId(),false,true,20);
            }
        }

        Map<String, String> retMap = Maps.newHashMap();
        retMap.put("rcvCode", rcvNo);
        return R.data(rcvNo);
    }

    @Override
    public R checkPO(List<PoReceiveDTO> poReceiveList) {
        for (PoReceiveDTO item : poReceiveList) {
            if ("Y".equals(item.getIsNew()) && !item.getItemCode().startsWith("20") && !item.getItemCode().startsWith("17")) {
                //???????????????????????????
                List<PoReceiveDTO> existItemList = this.baseMapper.checkLastestPO(item.getSupCode(), item.getItemCode(), item.getPoCode(), item.getPoLn());
                if (existItemList.size() <= 0) {
                    //????????????????????????
                } else {

                    //????????????????????????
                    for (PoReceiveDTO existItem : existItemList) {
                        Boolean isExistInList = isExistInList(poReceiveList, existItem);
                        if(isExistInList==false){
                            return R.fail("???????????????,????????????"+existItem.getItemCode()+"????????????????????????????????????????????????");
                        }
                    }
                }
            }
        }
        return R.success("????????????");
    }

    private Boolean isExistInList(List<PoReceiveDTO> poReceiveList, PoReceiveDTO existItem) {
        for (PoReceiveDTO selectItem : poReceiveList) {
            if(selectItem.getPoCode()==null){
                continue;
            }
            if (selectItem.getPoLn().equals(existItem.getPoLn()) && selectItem.getPoCode().equals(existItem.getPoCode())) {
                return true;
            }
        }
        return false;
    }


    private void checkOrder(List<PoReceiveDTO> poReceiveList, PoReceiveDTO poReceiveDTO) {

        //PR3??????????????????
        if (poReceiveDTO.getItemCode().length()>=3){
            if (poReceiveDTO.getItemCode().startsWith("200")){
                return;
            }
        }


        //???????????????????????????
        QueryWrapper query = Wrappers.<PoItemEntity>query()
            .gt("pro_goods_num", 0)
            .eq("status", STATUS_ORDER)
            .eq("sup_code", poReceiveDTO.getSupCode())
            .eq("item_code", poReceiveDTO.getItemCode())
            .orderByAsc("po_code","po_ln","tc_num","item_code")
            .last("limit 1");

        PoItemEntity lastest = poItemService.getOne(query);

        boolean iscontains=false;

        //??????????????????????????????????????????????????????
        if (lastest != null   ) {
            for (PoReceiveDTO item:poReceiveList) {
                if (item.getPiId() .equals(lastest.getId())) {
                    iscontains=true;
                }
            }

            if (iscontains == false) {
                throw new RuntimeException("??????:"+lastest.getItemCode()+"??????????????????????????? " + lastest.getPoCode()+" "+ lastest.getPoLn());
            }


        }
    }




    private void checkOrganize(List<PoReceiveDTO> poReceiveList, PoReceiveDTO poReceiveDTO) {
        String orgCode = StringUtil.isBlank(poReceiveDTO.getOrgCode())?"001":poReceiveDTO.getOrgCode();
        String POorgCode = StringUtil.isBlank(poReceiveList.get(0).getOrgCode()) ?"001":poReceiveList.get(0).getOrgCode();
        if(orgCode!=null&&!orgCode.equals(POorgCode)){
            throw new RuntimeException("???????????????????????????????????????????????? " + poReceiveDTO.getPoCode()+" "+ poReceiveDTO.getPoLn());
        }
    }

    /**
     * ??????
     *
     * @param poReceiveList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R reCreateAsR(List<PoReceiveDTO> poReceiveList) {

        boolean isToHandle = false;
        String rcvCode = poReceiveList.get(0).getRcvCode();
        String isOutCheck = this.baseMapper.getIsOutCheck(rcvCode);
        Map<String, String> retMap = Maps.newHashMap();

        if(OUT.equals(isOutCheck)){
            int isOpend = this.baseMapper.isOpen(rcvCode);
            int snCount = this.baseMapper.getSnCount(rcvCode);
            if (isOpend == 0 && snCount>0) {
                throw  new RuntimeException("??????????????????????????????,?????????????????????????????????????????????????????????");
            }
        }

        Integer seq = Integer.parseInt(rcvCode.substring(rcvCode.length()-3, rcvCode.length()));
        List<PoReceiveEntity> oldDOInfo = this.baseMapper.getOldDOInfo(rcvCode);

        Date createTime = oldDOInfo.get(0).getCreateTimeRecord() == null ? oldDOInfo.get(0).getCreateTime() : oldDOInfo.get(0).getCreateTimeRecord();
        Long createUser = oldDOInfo.get(0).getCreateUserRecord() == null ? oldDOInfo.get(0).getCreateUser() : oldDOInfo.get(0).getCreateUserRecord();
        Integer status = oldDOInfo.get(0).getStatus();
        if(STATUS_TOHANDLE.equals(status)){
            isToHandle = true;
        }

        // ??????????????????????????????????????????????????????
        if(status > STATUS_ORDER &&  !STATUS_TOHANDLE.equals(status)){
            retMap.put("rcvCode", rcvCode);
            return R.data(retMap);
        }

        // ???????????????po_item
        for (PoReceiveEntity info : oldDOInfo) {
            removeById(info.getId());
        }

        List<PoReceiveDTO> poReceiveListOfAtw = new ArrayList<>();
        List<PoReceiveDTO> poReceiveListOfOthers = new ArrayList<>();

        for (PoReceiveDTO poReceiveDTO : poReceiveList) {

            //???????????????
            checkOrganize(poReceiveList, poReceiveDTO);

            String itemName = poReceiveDTO.getItemName();
            // ?????????????????????????????? ????????????
            if (itemName.indexOf("??????") >= 0) {
                ItemInfoEntityOfZDJ itemInfoEntityOfZDJ = ItemAnalysisUtil.getItemInfoOfZhuDuanJian(itemName);
                // ??????????????????????????????????????????????????????
                if (itemInfoEntityOfZDJ.getPound() != null) {
                    List<OutPrWxZJEntity> outPrWxZJEntityList = outPrWxZJMapper.getOutPrWxZJInfo(itemInfoEntityOfZDJ,poReceiveDTO.getSupName());
                    // ????????????????????????????????????????????????
                    if (outPrWxZJEntityList.size()>0 && outPrWxZJEntityList.get(0).getIsOut().equals("Y")) {
                        poReceiveDTO.setOutSupCode(outPrWxZJEntityList.get(0).getOutSupCode());
                        poReceiveDTO.setOutSupName(outPrWxZJEntityList.get(0).getOutSupName());
                        poReceiveDTO.setProcess(outPrWxZJEntityList.get(0).getRemark());
                        poReceiveListOfOthers.add(poReceiveDTO);
                    } else {
                        // ?????????ATW
                        poReceiveListOfAtw.add(poReceiveDTO);
                    }
                } else {
                    // ?????????ATW
                    poReceiveListOfAtw.add(poReceiveDTO);
                }
            } else {
                // ?????????ATW
                poReceiveListOfAtw.add(poReceiveDTO);
            }
        }

        String rcvNo = "";
        // ?????????
        if(poReceiveListOfAtw.size() > 0){
            List<PoReceiveDTO> poReceiveListOfAtwForIn = new ArrayList<>();
            List<PoReceiveDTO> poReceiveListOfAtwForOut = new ArrayList<>();
            poReceiveListOfAtw.forEach(item->{
                if(item.getTemplateType().indexOf("W")>-1) {
                    poReceiveListOfAtwForOut.add(item);
                } else {
                    poReceiveListOfAtwForIn.add(item);
                }
            });

            // 2????????????????????????
            if(poReceiveListOfAtwForOut.size()>0 && poReceiveListOfAtwForIn.size()>0) {
                String rcvNo1 = getAndSaveDo(poReceiveListOfAtwForIn,"N",rcvCode,seq,createTime,createUser,isToHandle,false,status);

                Integer seq2 = getMaxSeq() + 1;
                String rcvNum2 = "DO"+poReceiveListOfAtwForOut.get(0).getOrgCode() + DateUtil.format(new Date(), "yyyyMMdd") + String.format("%03d", seq2);
                String rcvNo2 = getAndSaveDo(poReceiveListOfAtwForOut, "N", rcvNum2, seq2,new Date(),SecureUtil.getUserId(),false,true,20);

                rcvNo = rcvNo1 + "," + rcvNo2;
            } else { // ???????????????
                rcvNo = getAndSaveDo(poReceiveListOfAtw,"N",rcvCode,seq,createTime,createUser,isToHandle,false,status);
            }
        }

        // ?????????
        if (poReceiveListOfOthers.size() > 0) {
            if(rcvNo.isEmpty()){
                rcvNo = getAndSaveDo(poReceiveListOfOthers, "Y", rcvCode, seq, createTime, createUser, isToHandle,false,status);
            } else {
                Integer seqOfWX = getMaxSeq() + 1;
                String rcvNumOfWX = "DO" +poReceiveListOfOthers.get(0).getOrgCode()+ DateUtil.format(new Date(), "yyyyMMdd") + String.format("%03d", seqOfWX);
                rcvNo = rcvNo + "," + getAndSaveDo(poReceiveListOfOthers, "Y", rcvNumOfWX, seqOfWX, new Date(), createUser, isToHandle,true,status);
            }
        }

        retMap.put("rcvCode", rcvNo);
        return R.data(retMap);
    }

    private String getAndSaveDo(List<PoReceiveDTO> poReceiveList, String isOut, String rcvNo, Integer seq,Date createTime,Long createUser,boolean isToHandle,boolean isNew,Integer status) {
        List<PoReceiveEntity> entities = Lists.newArrayList();

        // ???????????????????????????????????????????????????????????????????????????
        String account = getUser()==null?"ESB":getUser().getAccount();
        Boolean isOutCheck = false;
        if(isNew){
            isOutCheck = judgeIsOutCheck(account);
            if(isOutCheck){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // ?????????????????????????????????
                this.baseMapper.insertDoOutCheck(rcvNo,df.format(new Date()),account);
            }
        }

        for (PoReceiveDTO dto : poReceiveList) {
            PoItemEntity poItem = poItemService.getById(dto.getPiId());
            if (poItem == null) {
                throw new RuntimeException("poId????????????" + dto.getPiId());
            }
            if (poItem.getU9StatusCode().equals("0")) {
                throw new RuntimeException("?????????????????????????????????????????? " + poItem.getPoCode());
            }

            // ???????????????????????????
//            if(poItem.getItemName().indexOf("??????")>-1 || poItem.getItemName().indexOf("??????")>-1 ||poItem.getItemName().indexOf("??????")>-1 ||poItem.getItemName().indexOf("????????????")>-1){
//                // ???????????????30??????????????????????????????
//                Integer isPro = poItemMapper.getUstPrintZJ(poItem.getPoCode(),poItem.getPoLn().toString());
//                if (isPro == 0 ) {
//                    String confirmDate = poItem.getSupConfirmDate().toString();
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                    confirmDate = simpleDateFormat.format(new Date(Long.valueOf(confirmDate+"000")));
//                    String nowDate = simpleDateFormat.format(new Date());
//                    if(confirmDate.compareTo(nowDate) > 0) {
//                        throw  new RuntimeException(poItem.getPoCode()+"-"+poItem.getPoLn()+" : "+poItem.getItemCode() + " --> ???PO???????????????????????????????????????????????????????????????!");
//                    }
//                }
//            }
            // ???????????????
            if(poItem.getPoCode().indexOf("WW")>-1) {
                String rcvNum = dto.getRcvNum().toString();
                String canPrintNum = this.getMaxWWCanPrintNum(poItem.getPoCode(),poItem.getPoLn());
                if(!canPrintNum.isEmpty()) {
                    if(new BigDecimal(rcvNum).compareTo(new BigDecimal(canPrintNum))>0) {
                        throw new RuntimeException(poItem.getPoCode()+ "-" + poItem.getPoLn() + " : ???po??? ?????????????????????????????????????????? ????????????????????? " + canPrintNum + "???");
                    }
                }
            }

            if(poItem != null){
                Integer rcvNumAll = poItemMapper.getRcvAllNumByPiId(dto.getPiId().toString());
                BigDecimal num = poItem.getTcNum().add(poItem.getFillGoodsNum().subtract(new BigDecimal(rcvNumAll)));
                if(new BigDecimal(dto.getRcvNum()).compareTo(num)>0){
                    Integer overCharge = poItemMapper.getOverCharge(dto.getItemCode().substring(0,4));
                    BigDecimal overNum = num.multiply(new BigDecimal("1.2"));
                    if (overCharge == 0 || (new BigDecimal(dto.getRcvNum()).compareTo(overNum) > 0)) {
                        throw new RuntimeException(poItem.getPoCode() + "-" + poItem.getItemCode() + ": ???????????????????????????????????????!");
                    }
                }
            }

            String preg = "^(15|1404|13).*";
            boolean isNeed = poItem.getItemCode().matches(preg);
            if (isNeed && StringUtil.isBlank(dto.getHeatCode())) {
                throw new RuntimeException("????????????");
            }
            PoReceiveEntity entity = BeanUtil.copy(poItem, PoReceiveEntity.class);

            // ??????????????????(??????????????????????????????)
            if (isNew) {
                if(isOutCheck){
                    entity.setIsOutCheck(OUT);
                } else {
                    entity.setIsOutCheck(INNER);
                }
            } else {
                entity.setIsOutCheck(dto.getIsOutCheck());
            }

            entity.setPiId(dto.getPiId());
            entity.setRcvCode(rcvNo);
            entity.setSeq(seq);
            entity.setRcvNum(dto.getRcvNum());
            entity.setHeatCode(dto.getHeatCode());
            entity.setId(dto.getId());
            entity.setRemark(dto.getRemark());
            entity.setCreateDept(StringUtil.isBlank(SecureUtil.getDeptId())? Long.valueOf("9999999999") :Long.valueOf(SecureUtil.getDeptId()) );
            entity.setCreateUserRecord(createUser);
            entity.setCreateTimeRecord(createTime);
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(SecureUtil.getUserId());
            entity.setIsOut(isOut);
            if(isToHandle){
                entity.setStatus(STATUS_TOHANDLE);
            }

            if(!isNew) {
                entity.setStatus(status);
            }


            String OrgCode = StringUtil.isBlank(poItem.getOrgCode())?"001":poItem.getOrgCode();

            Integer urgent = judgeIsUrgent(poItem.getItemCode(),OrgCode);
            entity.setUrgent(urgent);

            if (dto.getRemark()!=null && !dto.getRemark().isEmpty() && isOut.equals("Y")) {
                entity.setProcess(dto.getProcess());
            }
            if(dto.getId()==null){
                save(entity);
            } else {
                updateById(entity);
            }
        }
        return rcvNo;
    }

    private String getMaxWWCanPrintNum(String poCode, Integer poLn) {

        // ???????????????????????????????????????SQL?????????
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;
        String maxWWCanPrintNum ="";


        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            Class.forName(driver);
            //1.????????????
            conn = DriverManager.getConnection(url, user, password);
            Statement s = conn.createStatement();

            //2.??????????????????sql
            String sql = "select * from ATWERP.v_PO_SetableQty where docno = ?" + " and doclineno = ?";
            //3.???????????????sql??????(????????????)
            stmt = conn.prepareStatement(sql);
            //4.???????????????
            stmt.setString(1, poCode);
            stmt.setString(2, poLn.toString());

            ResultSet rs = stmt.executeQuery();// ??????

            Integer urgent = 0;
            String pbc = "";

            if (rs.next()) {
                maxWWCanPrintNum = rs.getString("issuedsetableqty");
            }
            return maxWWCanPrintNum;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("????????????");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("????????????");
                }
            }
        }
    }


    /**
     * ??????????????????????????????????????????
     * @param account
     * @return
     */
    private Integer judgeIsUrgent(String itemCode,String OrgCode) {
        // ???????????????????????????????????????SQL?????????
        String driver = oracleDriver;
        String url = oracleUrl; //mydb???????????????
        String user = oracleUser;
        String password = oraclePassword;

        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            Class.forName(driver);
            //1.????????????
            conn = DriverManager.getConnection(url, user, password);
            Statement s = conn.createStatement();

            //2.??????????????????sql
            String sql = "select * from atwmos.aps_itemcode_pc where itemcode = ? and orgcode=?";
            //3.???????????????sql??????(????????????)
            stmt = conn.prepareStatement(sql);
            //4.???????????????
            stmt.setString(1, itemCode);
            stmt.setString(2, OrgCode);

            ResultSet rs = stmt.executeQuery();// ??????

            Integer urgent = 0;
            String pbc = "";

            if (rs.next()) {
                pbc = rs.getString("pbc");
            } else {
                pbc = "100+";
            }

            if(pbc.equals("80???")) {
                urgent = 2;
            } else if (pbc.equals("100???")) {
                urgent = 1;
            } else {
                urgent = 0;
            }
            return urgent;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("????????????");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("????????????");
                }
            }
        }
    }

    /**
     * ??????MES???????????????????????????????????????????????????
     * @param account
     * @return
     */
    private Boolean judgeIsOutCheck(String account) {
        // ???????????????????????????????????????SQL?????????
        String driver = sqlServerDriver;
        String url = sqlServerUrl; //mydb???????????????
        String user = sqlServerUser;
        String password = sqlServerPassword;

        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            Class.forName(driver);
            //1.????????????
            conn = DriverManager.getConnection(url, user, password);
            Statement s = conn.createStatement();

            //2.??????????????????sql
            String sql = "select * from Sys_EnumValues f11 where EnumKey='080' and EnumValue = ?";
            //3.???????????????sql??????(????????????)
            stmt = conn.prepareStatement(sql);
            //4.???????????????
            stmt.setString(1, account);

            ResultSet rs = stmt.executeQuery();// ??????

            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("????????????");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("????????????");
                }
            }
        }
    }

    private int getMaxSeq() {
        Date tStart = WillDateUtil.getTodayStart();
        Date tEnd = WillDateUtil.getTodayEnd();
        Integer seq = baseMapper.getMaxSeq(tStart, tEnd);
        return seq == null ? 0 : seq;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R doCancel(List<PoReceiveDTO> poReceiveList) {
        for(PoReceiveDTO poReceiveDTO: poReceiveList) {
            if(this.baseMapper.isSNExisted(poReceiveDTO.getRcvCode())>0){
                throw new RuntimeException(poReceiveDTO.getRcvCode() + " ??? ????????????????????????????????????????????? ");
            }
            this.baseMapper.deleteByRcvCode(poReceiveDTO.getRcvCode());
            if(poReceiveDTO.getIsOutCheck().equals(OUT)){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                this.baseMapper.deleteDoOutCheckByRcvCode(poReceiveDTO.getRcvCode(),getUser().getAccount(),df.format(new Date()));
            }
        }
        return R.status(true);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public R doClose(List<PoReceiveDTO> poReceiveList) {
        for(PoReceiveDTO poReceiveDTO: poReceiveList) {
            this.baseMapper.closeByRcvCode(poReceiveDTO.getRcvCode());
        }
        return R.status(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R doBusiness(List<PoReceiveDTO> poReceiveList) {
        for(PoReceiveDTO poReceiveDTO: poReceiveList) {
            this.baseMapper.updateBusinessByRcvCode(poReceiveDTO.getRcvCode(),poReceiveDTO.getBusinessCode(),poReceiveDTO.getBusinessName());
        }
        return R.status(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R doRecovery(List<PoReceiveDTO> poReceiveList) {
        for(PoReceiveDTO poReceiveDTO: poReceiveList) {
            this.baseMapper.recoveryByRcvCode(poReceiveDTO.getRcvCode());
        }
        return R.status(true);
    }

    @Override
    public void export(PoReceiveDTO poReceiveDTO, HttpServletResponse response) {
        String rcvCodes = poReceiveDTO.getRcvCodes();
        List<PoReceiveExcelDTO> excelDTOS = new ArrayList<>();

        List<PoReceiveEntity> poReceiveEntities = new ArrayList<>();

        // ????????????
        if (rcvCodes.isEmpty()) {
            poReceiveEntities = this.baseMapper.getAllDo();
        } else {
            // ????????????
            rcvCodes  = rcvCodes.substring(1,rcvCodes.length());
            poReceiveEntities = this.baseMapper.getPartDoByRcvCodes(rcvCodes);
        }

        // ??????????????????
        for (PoReceiveEntity entity : poReceiveEntities) {
            PoItemEntity poItemEntity = poItemService.getById(entity.getPiId());
            if(poItemEntity==null){
                //throw new RuntimeException(entity.getSupCode()+ " - " +entity.getRcvCode() + "-" + entity.getPiId() + " :????????????DO?????????????????????");
                continue;
            }
            PoReceiveExcelDTO excelDTO = BeanUtil.copy(entity, PoReceiveExcelDTO.class);
            excelDTO.setPoCode(poItemEntity.getPoCode());
            excelDTO.setPoLn(poItemEntity.getPoLn());
            excelDTO.setItemCode(poItemEntity.getItemCode());
            excelDTO.setItemName(poItemEntity.getItemName());
            excelDTOS.add(excelDTO);
        }
        Set<String> keySets = new HashSet<>();
        for(PoReceiveExcelDTO dto : excelDTOS){
            if(keySets.contains(dto.getRcvCode())){
                dto.setRcvCode("");
            } else {
                keySets.add(dto.getRcvCode());
            }
        }

        // ?????????
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("?????????");//??????sheet??????
        HSSFRow row1 = sheet.createRow(0); //???sheet???????????????????????????????????????
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT); //?????????????????????????????????
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP); //?????????????????????????????????
        // ??????
        row1.createCell(0).setCellStyle(cellStyle);
        row1.createCell(0).setCellValue("????????????");
        row1.createCell(1).setCellValue("???????????????");
        row1.createCell(2).setCellValue("???????????????");
        row1.createCell(3).setCellValue("????????????");
        row1.createCell(4).setCellValue("????????????");
        row1.createCell(5).setCellValue("????????????");
        row1.createCell(6).setCellValue("????????????");
        row1.createCell(7).setCellValue("????????????");
        row1.createCell(8).setCellValue("?????????");
        row1.createCell(9).setCellValue("????????????");
        row1.createCell(10).setCellValue("??????");
        int i = 1;
        for (PoReceiveExcelDTO dto : excelDTOS) {
            HSSFRow row = sheet.createRow(i);

            //????????????
            for (int x = 0; x < 11; x++) {
                row.createCell(x).setCellStyle(cellStyle);
            }
            row.getCell(0).setCellValue(dto.getRcvCode());
            row.getCell(1).setCellValue(dto.getSupCode());
            row.getCell(2).setCellValue(dto.getSupName());
            row.getCell(3).setCellValue(dto.getPoCode());
            row.getCell(4).setCellValue(dto.getPoLn());
            row.getCell(5).setCellValue(dto.getItemCode());
            row.getCell(6).setCellValue(dto.getItemName());
            row.getCell(7).setCellValue(dto.getRcvNum());
            row.getCell(8).setCellValue(dto.getHeatCode());
            row.getCell(9).setCellValue(dto.getCreateTimeRecord() != null ? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dto.getCreateTimeRecord()) : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dto.getCreateTime()));
            Integer status = dto.getStatus();
            String statusValue = "";
            switch (status){
                case 20: statusValue = "?????????";break;
                case 21: statusValue = "???????????????";break;
                case 22: statusValue = "???????????????";break;
                case 23: statusValue = "???????????????";break;
                case 24: statusValue = "???????????????";break;
                case 25: statusValue = "?????????";break;
                case 26: statusValue = "?????????";break;
                case 27: statusValue = "???????????????";break;
                case 30: statusValue = "?????????";break;
                case 40: statusValue = "?????????";break;
            }
            row.getCell(10).setCellValue(statusValue);
            i++;
        }

        String fileName = "?????????" + DateUtil.formatDate(new Date()) + ".xls";

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            wb.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("???????????????");
        }
    }


    @Override
    public IPage<PoReceiveVO> selectPageOfParams(IPage<PoReceiveDTO> page, PoReceiveDTO poReceive) {
        return this.baseMapper.selectPageOfParams(page, poReceive);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R setOut(List<PoReceiveDTO> poReceiveList) {
        for(PoReceiveDTO poReceiveDTO: poReceiveList) {
            String rcvNo = poReceiveDTO.getRcvCode();
            String  isOutCheck = poReceiveDTO.getIsOutCheck();
            if(OUT.equals(isOutCheck)){
                throw new RuntimeException(rcvNo + " ????????????????????????????????????????????????????????????");
            } else {
                int doCount = this.baseMapper.getCountByRcvCode(rcvNo);
                if (doCount == 0) {
                    throw new RuntimeException(rcvNo + " ???????????????????????????????????????????????????");
                } else {
                    int snCount = this.baseMapper.getSnCount(rcvNo);
                    if(snCount > 0) {
                        throw new RuntimeException(rcvNo + " ????????????????????????SN????????????IT?????????");
                    }
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    // ?????????????????????????????????
                    this.baseMapper.insertDoOutCheck(rcvNo,df.format(new Date()),getUser().getAccount());
                    this.baseMapper.setOut(rcvNo);
                }
            }
        }
        return R.status(true);
    }
}
