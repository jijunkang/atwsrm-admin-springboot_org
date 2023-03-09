package org.springblade.modules.outpr.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.api.client.util.Lists;
import lombok.extern.java.Log;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONObject;
import org.springblade.common.config.AtwSrmConfiguration;
import org.springblade.common.utils.WillHttpUtil;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.OkHttpUtil;
import org.springblade.modules.outpr.dto.*;
import org.springblade.modules.outpr.entity.OutPrReportFormsEntity;
import org.springblade.modules.outpr.mapper.OutPrReportFormsMapper;
import org.springblade.modules.outpr.service.IOutPrReportFormsService;
import org.springblade.modules.outpr.vo.MesVo;
import org.springblade.modules.outpr.vo.ParamsVO;
import org.springblade.modules.outpr.vo.ResultVO;
import org.springblade.modules.po.entity.PoReceiveEntity;
import org.springblade.modules.po.mapper.PoReceiveMapper;
import org.springblade.modules.system.entity.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
@Log
public class OutPrReportFormsServiceImpl extends BaseServiceImpl<OutPrReportFormsMapper, OutPrReportFormsEntity> implements IOutPrReportFormsService {


    @Value("${sqlServer.url}")
    private  String sqlServerUrl;

    @Value("${sqlServer.user}")
    private  String sqlServerUser;

    @Value("${sqlServer.password}")
    private  String sqlServerPassword;

    @Value("${sqlServer.driver}")
    private  String sqlServerDriver;

    @Autowired
    AtwSrmConfiguration atwSrmConfiguration;

    @Autowired
    private PoReceiveMapper poReceiveMapper;

    /**
     * 导出
     *
     * @param outPrReportFormsDTO
     * @param response
     */
    @Override
    public void export(OutPrReportFormsDTO outPrReportFormsDTO, HttpServletResponse response) {
        String doCodes = outPrReportFormsDTO.getDoCodes();
        String itemCodes = outPrReportFormsDTO.getItemCodes();

        List<OutPrReportFormsEntity> outPrReportFormsListToHandle = new ArrayList<>();

        if(!doCodes.isEmpty()){

            String[] doCodeArr = doCodes.split(",");
            String[] itemCodeArr = itemCodes.split(",");

            // 获取第一阶段的数据
            if(doCodeArr.length > 0){
                OutPrReportFormsDTO dto = new OutPrReportFormsDTO();
                for(int i=1;i<doCodeArr.length;i++){
                    dto.setDoCode(doCodeArr[i]);
                    dto.setOldItemCode(itemCodeArr[i]);
                    List<OutPrReportFormsEntity> outPrReportFormsList = this.baseMapper.getDOListOfWW(dto);
                    if(outPrReportFormsList.size()>0){
                        outPrReportFormsListToHandle.addAll(outPrReportFormsList);
                    }
                }
            }
        } else {
            outPrReportFormsListToHandle = this.baseMapper.getDOListOfWW(outPrReportFormsDTO);
        }

        // 获得完整数据
        List<OutPrReportFormsEntity> outPrReportFormsEntities = this.getOutDatabaseInfo(outPrReportFormsListToHandle);

        List<OutPrReportFormsExcelDTO> excelList = new ArrayList<>();
        Set<String> keySets = new HashSet<>();
        for(OutPrReportFormsEntity dto : outPrReportFormsEntities){
            OutPrReportFormsExcelDTO excelDTO = BeanUtil.copy(dto, OutPrReportFormsExcelDTO.class);
            if(keySets.contains(excelDTO.getOldItemCode()+excelDTO.getOldItemName()+excelDTO.getDeliverNum()+excelDTO.getDoCode())){
                excelDTO.setOldItemCode("");
                excelDTO.setOldItemName("");
                excelDTO.setDeliverNum(null);
                excelDTO.setDoCode("");
            } else {
                keySets.add(excelDTO.getOldItemCode()+excelDTO.getOldItemName()+excelDTO.getDeliverNum()+excelDTO.getDoCode());
            }
            excelList.add(excelDTO);
        }

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet=wb.createSheet("外协物料报表");//建立sheet对象
        HSSFRow row1=sheet.createRow(0); //在sheet里创建第一行，参数为行索引
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT); //设置水平方向的对其方式
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP); //设置垂直方法的对齐方式
        // 表头
        row1.createCell(0).setCellStyle(cellStyle);
        row1.createCell(0).setCellValue("原料号");
        row1.createCell(1).setCellValue("原料品描述");
        row1.createCell(2).setCellValue("送货数量");
        row1.createCell(3).setCellValue("送货单单号");
        row1.createCell(4).setCellValue("子项目号");
        row1.createCell(5).setCellValue("项目交期");
        row1.createCell(6).setCellValue("mo工单");
        row1.createCell(7).setCellValue("新料号");
        row1.createCell(8).setCellValue("新料品描述");
        row1.createCell(9).setCellValue("数量");
        row1.createCell(10).setCellValue("备注");
        int i = 1;
        for(OutPrReportFormsExcelDTO dto : excelList){
            HSSFRow row =sheet.createRow(i);
            //设置样式
            for(int x = 0;x<11;x++){
                row.createCell(x).setCellStyle(cellStyle);
            }
            row.getCell(0).setCellValue(dto.getOldItemCode());
            row.getCell(1).setCellValue(dto.getOldItemName());
            row.getCell(2).setCellValue(dto.getDeliverNum()==null?"":dto.getDeliverNum().toString());
            row.getCell(3).setCellValue(dto.getDoCode());
            row.getCell(4).setCellValue(dto.getQoCode());
            row.getCell(5).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(new Date(dto.getProDate())));
            row.getCell(6).setCellValue(dto.getMoCode());
            row.getCell(7).setCellValue(dto.getNewItemCode());
            row.getCell(8).setCellValue(dto.getNewItemName());
            row.getCell(9).setCellValue(dto.getRecNum());
            row.getCell(10).setCellValue(dto.getRemark());
            i++;
        }

        String fileName = "外协物料报表" + DateUtil.formatDate(new Date()) + ".xls";

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            wb.write(response.getOutputStream());
        } catch (IOException e) {
            throw  new RuntimeException("导出失败！");
        }

        //ExcelUtils.defaultExport(excelList, OutPrReportFormsExcelDTO.class, "外协物料报表" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public void exportDo(OutPrReportFormsDTO outPrReportFormsDTO, HttpServletResponse response) {
        String doCodes = outPrReportFormsDTO.getDoCodes();
        if(!doCodes.isEmpty()){
            outPrReportFormsDTO.setRcvCode(doCodes.substring(0,doCodes.length()-1));
        }
        outPrReportFormsDTO.setCheckStatus(HAVE_CHECK);
        List<DoDTO> doDTOS = this.baseMapper.getDoList(outPrReportFormsDTO);

        List<DoDTO> excelList = new ArrayList<>();
        Set<String> keySets = new HashSet<>();

        for(DoDTO dto : doDTOS){
            if(keySets.contains(dto.getRcvCode())){
                dto.setRcvCode("");
            } else {
                keySets.add(dto.getRcvCode());
            }
            excelList.add(dto);
        }

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("虚拟入库送货单");//建立sheet对象
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
        row1.createCell(7).setCellValue("数量");
        row1.createCell(8).setCellValue("生产订单号");
        row1.createCell(9).setCellValue("炉批号");
        row1.createCell(10).setCellValue("备注");
        int i = 1;
        for (DoDTO dto : excelList) {
            HSSFRow row = sheet.createRow(i);

            //设置样式
            for (int x = 0; x < 11; x++) {
                row.createCell(x).setCellStyle(cellStyle);
            }
            row.getCell(0).setCellValue(dto.getRcvCode());
            row.getCell(1).setCellValue(dto.getItemCode());
            row.getCell(2).setCellValue(dto.getItemName());
            row.getCell(3).setCellValue(dto.getPoCode());
            row.getCell(4).setCellValue(dto.getPoLn());
            row.getCell(5).setCellValue(dto.getItemCode());
            row.getCell(6).setCellValue(dto.getItemName());
            row.getCell(7).setCellValue(dto.getRcvNum());
            row.getCell(8).setCellValue(dto.getMoNo());
            row.getCell(9).setCellValue(dto.getHeatCode());
            row.getCell(10).setCellValue(dto.getRemark());
            i++;
        }

        String fileName = "虚拟入库送货单" + DateUtil.formatDate(new Date()) + ".xls";

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            wb.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("导出失败！");
        }
    }


    /**
     * 搜索
     *
     * @param page
     * @param outPrReportFormsDTO
     * @return
     */
    @Override
    public IPage<OutPrReportFormsEntity> voPage(IPage<OutPrReportFormsEntity> page, OutPrReportFormsDTO outPrReportFormsDTO) {

        // 获取第一阶段的数据
        IPage<OutPrReportFormsEntity> outPrReportFormsListToHandle = this.baseMapper.getDOPageOfWW(page,outPrReportFormsDTO);

        // 获得完整数据
        List<OutPrReportFormsEntity> outPrReportFormsList = this.getOutDatabaseInfo(outPrReportFormsListToHandle.getRecords());

        page.setRecords(outPrReportFormsList);
        page.setTotal(this.baseMapper.getDOListOfWW(outPrReportFormsDTO).size());

        return page;
    }

    private List<OutPrReportFormsEntity> getOutDatabaseInfo(List<OutPrReportFormsEntity> outPrReportFormsList) {

        List<OutPrReportFormsEntity> outPrReportFormsListToReturn = new ArrayList<>();
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

            for (OutPrReportFormsEntity outPrReportFormsEntity : outPrReportFormsList) {

                //2.准备预编译的sql
                String sql = "SELECT 子项目号 qo_code,项目交期 pro_date,供应号 mo_code,物料编号 item_code,物料名称 item_name,需求数量 rec_num  FROM peg_result_all_by_column WHERE 行号 IN (SELECT DISTINCT 父行号 FROM peg_result_all_by_column WHERE 物料编号 = ?)";
                //3.执行预编译sql语句(检查语法)
                stmt = conn.prepareStatement(sql);
                //4.设置参数值
                stmt.setString(1, outPrReportFormsEntity.getOldItemCode());

                ResultSet rs = stmt.executeQuery();// 执行

                while (rs.next()) {
                    OutPrReportFormsEntity entity = BeanUtil.copy(outPrReportFormsEntity, OutPrReportFormsEntity.class);
                    entity.setQoCode(rs.getString("qo_code"));
                    entity.setProDate(rs.getDate("pro_date").getTime()/1000);
                    entity.setMoCode(rs.getString("mo_code"));
                    entity.setNewItemCode(rs.getString("item_code"));
                    entity.setNewItemName(rs.getString("item_name"));
                    entity.setRecNum(rs.getInt("rec_num"));
                    outPrReportFormsListToReturn.add(entity);
                }
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
        return outPrReportFormsListToReturn;
    }

    @Override
    public R pass(OutPrReportFormsReq outPrReportFormsReq) {
        // 判断是否 外检，是否 检验结束放行
        //for(PoReceiveEntity poReceiveEntity : outPrReportFormsReq.getPoReceiveEntities()){
            //String doCode = poReceiveEntity.getRcvCode();
            String doCode = outPrReportFormsReq.getPoReceiveEntities().get(0).getRcvCode();
            List<PoReceiveEntity> rcvInfo  = poReceiveMapper.getDoInfoByRcvCode(doCode);
            if(rcvInfo.size() == 0){
                throw  new RuntimeException(doCode + " :该送货单不存在！");
            }
            String isOutCheck = rcvInfo.get(0).getIsOutCheck();
            String isOut = rcvInfo.get(0).getIsOut();

            // 如果 外协 且 外检
            /*if(isOutCheck.equals("1") && isOut.equals("Y")) {
                int isOpend = poReceiveMapper.isOpen(doCode);
                if (isOpend == 0) {
                    throw  new RuntimeException(doCode + " :该送货单是外协外检送货单，但外检还未结束，不能虚拟入库！");
                }
            } else {
                throw  new RuntimeException(doCode + " :该送货单不满足虚拟入库条件，请检查数据是否是 外协 且 外检！");
            }*/

            MesVo mesVo =  getParams(doCode);
            if(MES_FAIL.equals(mesVo.getCode())){
                throw new RuntimeException(doCode + "出错了，错误信息：" + mesVo.getMsg());
            }

            if(MES_SUCC.equals(mesVo.getCode())){
                // 改变虚拟入库标志(已入库)、且关闭该送货单
                this.baseMapper.virtualWareById(doCode);
            }


        //}
        return R.status(true);
    }

    private MesVo getParams(String doCode) {
        List<DoDTO> doDTOS = this.baseMapper.getParams(doCode);

        List<ParamsVO> paramsVOS = new ArrayList<>();

        ResultVO resultVo = new ResultVO();

        if (doDTOS.size() > 0) {
            for(DoDTO dto:doDTOS){
                ParamsVO vo = new ParamsVO();
                vo.setItem_code(dto.getItemCode());
                vo.setItem_name(dto.getItemName());
                vo.setHeat_code(dto.getHeatCode());
                vo.setRcv_code(dto.getRcvCode());
                vo.setRcv_num(dto.getRcvNum());
                vo.setMat_quality(dto.getMatQuality());
                vo.setPo_code(dto.getPoCode());
                vo.setPo_ln(dto.getPoLn());
                vo.setRemark(dto.getRemark());
                vo.setPrice_uom(dto.getPriceUom());
                vo.setSup_code(dto.getSupCode());
                vo.setSup_name(dto.getSupName());
                vo.setProduce_date(dto.getProduceDate());
                vo.setSpecs(dto.getSpecs());
                paramsVOS.add(vo);
            }
            resultVo.setCode("2000");
            resultVo.setMsg("SUCCESS");
            resultVo.setResult(paramsVOS);
        } else {
            resultVo.setCode("4004");
        }

        String json = JSON.toJSONString(resultVo, SerializerFeature.WriteMapNullValue);

        MesVo mesVo = new MesVo();
        log.info("/api/APICreateU9RCV"+json);


        //String res = OkHttpUtil.postJson(atwSrmConfiguration.getMesApiDomain() + "/api/APICreateU9RCV", json);

        String res = WillHttpUtil.postJson(atwSrmConfiguration.getMesApiDomain() + "/api/APICreateU9RCV", json,600L);

        log.info("/api/APICreateU9RCV  res:"+res);

        //20230207  接口处理逻辑修改
        /*if(res.isEmpty() || new JSONObject(res).getString("code").equals("1")){
            mesVo.setCode("1");

        } else {
            JSONObject returnJson = new JSONObject(res);
            String code = returnJson.getString("code");
            String msg = returnJson.getString("msg");
            String RCVDocNo = returnJson.getString("RCVDocNo");
            String U9RCVDocNo = returnJson.getString("U9RCVDocNo");
            mesVo.setCode(code);
            mesVo.setMsg(msg);
            mesVo.setRCVDocNo(RCVDocNo);
            mesVo.setU9RCVDocNo(U9RCVDocNo);
        }*/

        if(res.isEmpty() ){
            mesVo.setCode("0");
            mesVo.setMsg("MES接口超时,请稍后再试");

        } else {
            JSONObject returnJson = new JSONObject(res);
            String code = returnJson.getString("code");
            String msg = returnJson.getString("msg");
            String RCVDocNo = returnJson.getString("RCVDocNo");
            String U9RCVDocNo = returnJson.getString("U9RCVDocNo");
            mesVo.setCode(code);
            mesVo.setMsg(msg);
            mesVo.setRCVDocNo(RCVDocNo);
            mesVo.setU9RCVDocNo(U9RCVDocNo);
        }

        return mesVo;
    }


    /**
     * tab标签页统计
     * @param outPrReportFormsDTO
     * @return
     */
    @Override
    public List<Map<String, Object>> getDoCount(OutPrReportFormsDTO outPrReportFormsDTO) {
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new HashMap<String, Object>(3){{
            put("status", TO_CHECK);
            put("title", "待审核");
            put("count", baseMapper.getToCheckCount());
        }});
        result.add(new HashMap<String, Object>(3){{
            put("status", HAVE_CHECK);
            put("title", "审核通过");
            put("count", baseMapper.getHaveCheckCount());
        }});
        return result;
    }

    /**
     * 菜单上面的红点
     * @return
     */
    @Override
    public int getDoTabCount() {
        return baseMapper.getToCheckCount();
    }


    @Override
    public IPage<DoDTO> doPage(IPage<DoDTO> page, OutPrReportFormsDTO outPrReportFormsDTO) {
        return this.baseMapper.getDoPage(page,outPrReportFormsDTO);
    }
}



