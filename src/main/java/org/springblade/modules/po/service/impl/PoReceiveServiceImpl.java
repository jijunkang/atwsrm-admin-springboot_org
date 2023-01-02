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
 * 服务实现类
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
            //多组织判断
            checkOrganize(poReceiveList, poReceiveDTO);

            //送货单打印顺序
            //checkOrder(poReceiveList, poReceiveDTO);


            String itemName = poReceiveDTO.getItemName();

            //补历史数据的orgcode
            poReceiveDTO.setOrgCode(StringUtil.isBlank(poReceiveDTO.getOrgCode())?"001":poReceiveDTO.getOrgCode());

            // 如果是铸件，则先拆解 物料描述
            if (itemName.indexOf("铸件") >= 0) {
                ItemInfoEntityOfZDJ itemInfoEntityOfZDJ = ItemAnalysisUtil.getItemInfoOfZhuDuanJian(itemName);
                // 如果能拆解出来，则去查询是否需要外协
                if (itemInfoEntityOfZDJ.getPound() != null) {
                    List<OutPrWxZJEntity> outPrWxZJEntityList = outPrWxZJMapper.getOutPrWxZJInfo(itemInfoEntityOfZDJ,poReceiveDTO.getSupName());
                    // 如果可以查到信息，则是需要外协的
                    if (outPrWxZJEntityList.size()>0 && outPrWxZJEntityList.get(0).getIsOut().equals("Y")) {
                        poReceiveDTO.setOutSupCode(outPrWxZJEntityList.get(0).getOutSupCode());
                        poReceiveDTO.setOutSupName(outPrWxZJEntityList.get(0).getOutSupName());
                        poReceiveDTO.setProcess(outPrWxZJEntityList.get(0).getRemark());
                        poReceiveListOfOthers.add(poReceiveDTO);
                    } else {
                        // 否则回ATW
                        poReceiveListOfAtw.add(poReceiveDTO);
                    }
                } else {
                    // 否则回ATW
                    poReceiveListOfAtw.add(poReceiveDTO);
                }
            } else {
                // 否则回ATW
                poReceiveListOfAtw.add(poReceiveDTO);
            }
        }

        String rcvNo = "";
        // 回厂的
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

            // 2者都有（内外仓）
            if(poReceiveListOfAtwForOut.size()>0 && poReceiveListOfAtwForIn.size()>0) {
                Integer seq1 = getMaxSeq() + 1;
                String rcvNum1 = "DO"+ poReceiveListOfAtwForOut.get(0).getOrgCode() + DateUtil.format(new Date(), "yyyyMMdd") + String.format("%03d", seq1);
                String rcvNo1 = getAndSaveDo(poReceiveListOfAtwForOut, "N", rcvNum1, seq1,new Date(),SecureUtil.getUserId(),false,true,20);

                Integer seq2 = getMaxSeq() + 1;
                String rcvNum2 = "DO"+poReceiveListOfAtwForIn.get(0).getOrgCode() + DateUtil.format(new Date(), "yyyyMMdd") + String.format("%03d", seq2);
                String rcvNo2 = getAndSaveDo(poReceiveListOfAtwForIn, "N", rcvNum2, seq2,new Date(),SecureUtil.getUserId(),false,true,20);

                rcvNo = rcvNo1 + "," + rcvNo2;
            } else { // 只有一个仓
                Integer seq = getMaxSeq() + 1;
                String rcvNum = "DO" +poReceiveListOfAtw.get(0).getOrgCode()+ DateUtil.format(new Date(), "yyyyMMdd") + String.format("%03d", seq);
                rcvNo = getAndSaveDo(poReceiveListOfAtw, "N", rcvNum, seq,new Date(),SecureUtil.getUserId(),false,true,20);
            }
        }

        // 外协的
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


    private void checkOrder(List<PoReceiveDTO> poReceiveList, PoReceiveDTO poReceiveDTO) {

        //PR3物料不做卡控
        if (poReceiveDTO.getItemCode().length()>=3){
            if (poReceiveDTO.getItemCode().startsWith("200")){
                return;
            }
        }


        //用供应商和料号搜索
        QueryWrapper query = Wrappers.<PoItemEntity>query()
            .gt("pro_goods_num", 0)
            .eq("status", STATUS_ORDER)
            .eq("sup_code", poReceiveDTO.getSupCode())
            .eq("item_code", poReceiveDTO.getItemCode())
            .orderByAsc("po_code","po_ln","tc_num","item_code")
            .last("limit 1");

        PoItemEntity lastest = poItemService.getOne(query);

        boolean iscontains=false;

        //判断最早的订单行是否已经在打印列表中
        if (lastest != null   ) {
            for (PoReceiveDTO item:poReceiveList) {
                if (item.getPiId() .equals(lastest.getId())) {
                    iscontains=true;
                }
            }

            if (iscontains == false) {
                throw new RuntimeException("物料:"+lastest.getItemCode()+"存在更早的订单行： " + lastest.getPoCode()+" "+ lastest.getPoLn());
            }


        }
    }




    private void checkOrganize(List<PoReceiveDTO> poReceiveList, PoReceiveDTO poReceiveDTO) {
        String orgCode = StringUtil.isBlank(poReceiveDTO.getOrgCode())?"001":poReceiveDTO.getOrgCode();
        if(orgCode!=null&&!orgCode.equals(poReceiveList.get(0).getOrgCode())){
            throw new RuntimeException("不同组织的订单，不允许一起打印： " + poReceiveDTO.getPoCode()+" "+ poReceiveDTO.getPoLn());
        }
    }

    /**
     * 补打
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
                throw  new RuntimeException("该送货单是外检送货单,且已经扫码过，外检还未结束，不能补打！");
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

        // 报检后，不能补打，直接返回原有的单号
        if(status > STATUS_ORDER &&  !STATUS_TOHANDLE.equals(status)){
            retMap.put("rcvCode", rcvCode);
            return R.data(retMap);
        }

        // 删除原有的po_item
        for (PoReceiveEntity info : oldDOInfo) {
            removeById(info.getId());
        }

        List<PoReceiveDTO> poReceiveListOfAtw = new ArrayList<>();
        List<PoReceiveDTO> poReceiveListOfOthers = new ArrayList<>();

        for (PoReceiveDTO poReceiveDTO : poReceiveList) {

            //多组织判断
            checkOrganize(poReceiveList, poReceiveDTO);

            String itemName = poReceiveDTO.getItemName();
            // 如果是铸件，则先拆解 物料描述
            if (itemName.indexOf("铸件") >= 0) {
                ItemInfoEntityOfZDJ itemInfoEntityOfZDJ = ItemAnalysisUtil.getItemInfoOfZhuDuanJian(itemName);
                // 如果能拆解出来，则去查询是否需要外协
                if (itemInfoEntityOfZDJ.getPound() != null) {
                    List<OutPrWxZJEntity> outPrWxZJEntityList = outPrWxZJMapper.getOutPrWxZJInfo(itemInfoEntityOfZDJ,poReceiveDTO.getSupName());
                    // 如果可以查到信息，则是需要外协的
                    if (outPrWxZJEntityList.size()>0 && outPrWxZJEntityList.get(0).getIsOut().equals("Y")) {
                        poReceiveDTO.setOutSupCode(outPrWxZJEntityList.get(0).getOutSupCode());
                        poReceiveDTO.setOutSupName(outPrWxZJEntityList.get(0).getOutSupName());
                        poReceiveDTO.setProcess(outPrWxZJEntityList.get(0).getRemark());
                        poReceiveListOfOthers.add(poReceiveDTO);
                    } else {
                        // 否则回ATW
                        poReceiveListOfAtw.add(poReceiveDTO);
                    }
                } else {
                    // 否则回ATW
                    poReceiveListOfAtw.add(poReceiveDTO);
                }
            } else {
                // 否则回ATW
                poReceiveListOfAtw.add(poReceiveDTO);
            }
        }

        String rcvNo = "";
        // 回厂的
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

            // 2者都有（内外仓）
            if(poReceiveListOfAtwForOut.size()>0 && poReceiveListOfAtwForIn.size()>0) {
                String rcvNo1 = getAndSaveDo(poReceiveListOfAtwForIn,"N",rcvCode,seq,createTime,createUser,isToHandle,false,status);

                Integer seq2 = getMaxSeq() + 1;
                String rcvNum2 = "DO"+poReceiveListOfAtwForOut.get(0).getOrgCode() + DateUtil.format(new Date(), "yyyyMMdd") + String.format("%03d", seq2);
                String rcvNo2 = getAndSaveDo(poReceiveListOfAtwForOut, "N", rcvNum2, seq2,new Date(),SecureUtil.getUserId(),false,true,20);

                rcvNo = rcvNo1 + "," + rcvNo2;
            } else { // 只有一个仓
                rcvNo = getAndSaveDo(poReceiveListOfAtw,"N",rcvCode,seq,createTime,createUser,isToHandle,false,status);
            }
        }

        // 外协的
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

        // 判断是否外检、如果是、则插入数据库等待检验结束放行
        String account = getUser()==null?"ESB":getUser().getAccount();
        Boolean isOutCheck = false;
        if(isNew){
            isOutCheck = judgeIsOutCheck(account);
            if(isOutCheck){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // 插入数据库（等待放行）
                this.baseMapper.insertDoOutCheck(rcvNo,df.format(new Date()),account);
            }
        }

        for (PoReceiveDTO dto : poReceiveList) {
            PoItemEntity poItem = poItemService.getById(dto.getPiId());
            if (poItem == null) {
                throw new RuntimeException("poId不存在：" + dto.getPiId());
            }
            if (poItem.getU9StatusCode().equals("0")) {
                throw new RuntimeException("订单已弃审，不能打印送货单： " + poItem.getPoCode());
            }

            // 暂时只卡控供应商端
//            if(poItem.getItemName().indexOf("阀体")>-1 || poItem.getItemName().indexOf("阀帽")>-1 ||poItem.getItemName().indexOf("蝶板")>-1 ||poItem.getItemName().indexOf("蝶阀阀体")>-1){
//                // 先查出后面30天之前项目占用的数量
//                Integer isPro = poItemMapper.getUstPrintZJ(poItem.getPoCode(),poItem.getPoLn().toString());
//                if (isPro == 0 ) {
//                    String confirmDate = poItem.getSupConfirmDate().toString();
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                    confirmDate = simpleDateFormat.format(new Date(Long.valueOf(confirmDate+"000")));
//                    String nowDate = simpleDateFormat.format(new Date());
//                    if(confirmDate.compareTo(nowDate) > 0) {
//                        throw  new RuntimeException(poItem.getPoCode()+"-"+poItem.getPoLn()+" : "+poItem.getItemCode() + " --> 该PO行还未到合同交期，且无项目占用、故不可打印!");
//                    }
//                }
//            }
            // 委外的卡控
            if(poItem.getPoCode().indexOf("WW")>-1) {
                String rcvNum = dto.getRcvNum().toString();
                String canPrintNum = this.getMaxWWCanPrintNum(poItem.getPoCode(),poItem.getPoLn());
                if(!canPrintNum.isEmpty()) {
                    if(new BigDecimal(rcvNum).compareTo(new BigDecimal(canPrintNum))>0) {
                        throw new RuntimeException(poItem.getPoCode()+ "-" + poItem.getPoLn() + " : 该po行 原材料入库扣账成品阀体的数量 最大可以打印： " + canPrintNum + "个");
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
                        throw new RuntimeException(poItem.getPoCode() + "-" + poItem.getItemCode() + ": 送货数量不得大于未送货数量!");
                    }
                }
            }

            String preg = "^(15|1404|13).*";
            boolean isNeed = poItem.getItemCode().matches(preg);
            if (isNeed && StringUtil.isBlank(dto.getHeatCode())) {
                throw new RuntimeException("炉号必填");
            }
            PoReceiveEntity entity = BeanUtil.copy(poItem, PoReceiveEntity.class);

            // 判断是否外检(只在新打印的时候变更)
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

        // 需要注意的是，表名不能作为SQL的参数
        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;
        String maxWWCanPrintNum ="";


        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            Class.forName(driver);
            //1.获取连接
            conn = DriverManager.getConnection(url, user, password);
            Statement s = conn.createStatement();

            //2.准备预编译的sql
            String sql = "select * from ATWERP.v_PO_SetableQty where docno = ?" + " and doclineno = ?";
            //3.执行预编译sql语句(检查语法)
            stmt = conn.prepareStatement(sql);
            //4.设置参数值
            stmt.setString(1, poCode);
            stmt.setString(2, poLn.toString());

            ResultSet rs = stmt.executeQuery();// 执行

            Integer urgent = 0;
            String pbc = "";

            if (rs.next()) {
                maxWWCanPrintNum = rs.getString("issuedsetableqty");
            }
            return maxWWCanPrintNum;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询出错");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("查询出错");
                }
            }
        }
    }


    /**
     * 检索送货单上的该料号是否紧急
     * @param account
     * @return
     */
    private Integer judgeIsUrgent(String itemCode,String OrgCode) {
        // 需要注意的是，表名不能作为SQL的参数
        String driver = oracleDriver;
        String url = oracleUrl; //mydb为数据库名
        String user = oracleUser;
        String password = oraclePassword;

        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            Class.forName(driver);
            //1.获取连接
            conn = DriverManager.getConnection(url, user, password);
            Statement s = conn.createStatement();

            //2.准备预编译的sql
            String sql = "select * from atwmos.aps_itemcode_pc where itemcode = ? and orgcode=?";
            //3.执行预编译sql语句(检查语法)
            stmt = conn.prepareStatement(sql);
            //4.设置参数值
            stmt.setString(1, itemCode);
            stmt.setString(2, OrgCode);

            ResultSet rs = stmt.executeQuery();// 执行

            Integer urgent = 0;
            String pbc = "";

            if (rs.next()) {
                pbc = rs.getString("pbc");
            } else {
                pbc = "100+";
            }

            if(pbc.equals("80分")) {
                urgent = 2;
            } else if (pbc.equals("100分")) {
                urgent = 1;
            } else {
                urgent = 0;
            }
            return urgent;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询出错");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("查询出错");
                }
            }
        }
    }

    /**
     * 检索MES的外检人员库，来判断是否是外检人员
     * @param account
     * @return
     */
    private Boolean judgeIsOutCheck(String account) {
        // 需要注意的是，表名不能作为SQL的参数
        String driver = sqlServerDriver;
        String url = sqlServerUrl; //mydb为数据库名
        String user = sqlServerUser;
        String password = sqlServerPassword;

        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            Class.forName(driver);
            //1.获取连接
            conn = DriverManager.getConnection(url, user, password);
            Statement s = conn.createStatement();

            //2.准备预编译的sql
            String sql = "select * from Sys_EnumValues f11 where EnumKey='080' and EnumValue = ?";
            //3.执行预编译sql语句(检查语法)
            stmt = conn.prepareStatement(sql);
            //4.设置参数值
            stmt.setString(1, account);

            ResultSet rs = stmt.executeQuery();// 执行

            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询出错");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("查询出错");
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
                throw new RuntimeException(poReceiveDTO.getRcvCode() + " ： 该单号已经报检过了，不能作废！ ");
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

        // 全部导出
        if (rcvCodes.isEmpty()) {
            poReceiveEntities = this.baseMapper.getAllDo();
        } else {
            // 部分导出
            rcvCodes  = rcvCodes.substring(1,rcvCodes.length());
            poReceiveEntities = this.baseMapper.getPartDoByRcvCodes(rcvCodes);
        }

        // 处理导出数据
        for (PoReceiveEntity entity : poReceiveEntities) {
            PoItemEntity poItemEntity = poItemService.getById(entity.getPiId());
            if(poItemEntity==null){
                //throw new RuntimeException(entity.getSupCode()+ " - " +entity.getRcvCode() + "-" + entity.getPiId() + " :找不到该DO单的相关信息！");
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

        // 设计表
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("送货单");//建立sheet对象
        HSSFRow row1 = sheet.createRow(0); //在sheet里创建第一行，参数为行索引
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT); //设置水平方向的对其方式
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP); //设置垂直方法的对齐方式
        // 表头
        row1.createCell(0).setCellStyle(cellStyle);
        row1.createCell(0).setCellValue("送货单号");
        row1.createCell(1).setCellValue("供应商编码");
        row1.createCell(2).setCellValue("供应商名称");
        row1.createCell(3).setCellValue("采购单号");
        row1.createCell(4).setCellValue("采购行号");
        row1.createCell(5).setCellValue("物料编号");
        row1.createCell(6).setCellValue("物料名称");
        row1.createCell(7).setCellValue("送货数量");
        row1.createCell(8).setCellValue("炉批号");
        row1.createCell(9).setCellValue("创建时间");
        row1.createCell(10).setCellValue("状态");
        int i = 1;
        for (PoReceiveExcelDTO dto : excelDTOS) {
            HSSFRow row = sheet.createRow(i);

            //设置样式
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
                case 20: statusValue = "送货中";break;
                case 21: statusValue = "厂内已报检";break;
                case 22: statusValue = "厂外已报检";break;
                case 23: statusValue = "厂内已检验";break;
                case 24: statusValue = "厂外已检验";break;
                case 25: statusValue = "处理中";break;
                case 26: statusValue = "已点收";break;
                case 27: statusValue = "虚拟已入库";break;
                case 30: statusValue = "已关闭";break;
                case 40: statusValue = "已作废";break;
            }
            row.getCell(10).setCellValue(statusValue);
            i++;
        }

        String fileName = "送货单" + DateUtil.formatDate(new Date()) + ".xls";

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            wb.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("导出失败！");
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
                throw new RuntimeException(rcvNo + " ：该单子已经是外检单子了，请勿随意操作！");
            } else {
                int doCount = this.baseMapper.getCountByRcvCode(rcvNo);
                if (doCount == 0) {
                    throw new RuntimeException(rcvNo + " ：该单子关闭和或者作废了，请确认！");
                } else {
                    int snCount = this.baseMapper.getSnCount(rcvNo);
                    if(snCount > 0) {
                        throw new RuntimeException(rcvNo + " ：该单子已经产生SN了，请找IT确认！");
                    }
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    // 插入数据库（等待放行）
                    this.baseMapper.insertDoOutCheck(rcvNo,df.format(new Date()),getUser().getAccount());
                    this.baseMapper.setOut(rcvNo);
                }
            }
        }
        return R.status(true);
    }
}
