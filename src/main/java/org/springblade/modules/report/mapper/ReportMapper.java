package org.springblade.modules.report.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.*;
import org.springblade.modules.report.dto.*;
import org.springblade.modules.report.entity.*;
import org.springblade.modules.report.vo.KeyItemFixedExcel;
import org.springblade.modules.report.vo.SupplierOutputVo;
import org.springblade.modules.supplier.dto.CaiGouScheduleReq;
import org.springblade.modules.supplier.dto.SupplierScheduleReq;
import org.springblade.modules.supplier.entity.CaiGouSchedule;

import java.util.List;

/**
 * 报表 Mapper 接口
 *
 * @author Will
 */
@Mapper
public interface ReportMapper {


    @Insert("Insert into atw_item_otd(sub_code,item_code,item_name,sup_code,sup_name,req_date,is_otd,biz_type,pr_code,plan_date,person,check_update_time,aps_end_date,is_otd_seven) value (#{subCode},#{itemCode},#{itemName},#{supCode},#{supName},#{reqDate},#{isOtd},#{bizType},#{prCode},#{planDate},#{person},#{checkUpdateTime},#{apsEndDate},#{isOtdSeven})")
    boolean InsertOtd(@Param("subCode") String subCode, @Param("itemCode") String itemCode,@Param("itemName") String itemName,@Param("supCode") String supCode,@Param("supName") String supName,@Param("reqDate") String reqDate,@Param("isOtd") String isOtd,@Param("bizType") String bizType,@Param("prCode") String prCode,@Param("planDate") String planDate,@Param("person") String person,@Param("checkUpdateTime") String checkUpdateTime,@Param("apsEndDate") String apsEndDate,@Param("isOtdSeven") String isOtdSeven);

    @Update("update atw_item_otd set is_otd = #{isOtd},plan_date=#{planDate},person=#{person},check_update_time=#{checkUpdateTime},aps_end_date=#{apsEndDate},req_date = #{reqDate},is_otd_seven=#{isOtdSeven} where sub_code =#{subCode} and item_code =#{itemCode}")
    boolean updateOtd(@Param("subCode") String subCode, @Param("itemCode") String itemCode,@Param("isOtd") String isOtd,@Param("planDate") String planDate,@Param("person") String person,@Param("checkUpdateTime") String checkUpdateTime,@Param("apsEndDate") String apsEndDate,@Param("reqDate") String reqDate,@Param("isOtdSeven") String isOtdSeven);

    @Select("select count(*) from atw_item_otd where sub_code =#{subCode} and item_code =#{itemCode}")
    Integer SelectIsOtdCount(@Param("subCode") String subCode, @Param("itemCode") String itemCode);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    List<KeyScheduleReport> getAllKeyItemOfAfterToday(@Param("caiGouScheduleReq")KeyItemReportReq keyItemReportReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    List<KeyScheduleReport> getAllWWItemOfAfterToday(@Param("caiGouScheduleReq")KeyItemReportReq keyItemReportReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    List<KeyScheduleReport> getAllNotKeyItemOfAfterToday(@Param("caiGouScheduleReq")KeyItemReportReq keyItemReportReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    IPage<CaiGouSchedule> getItemDailyDetailReport(IPage page, @Param("caiGouScheduleReq") CaiGouScheduleReq caiGouScheduleReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    List<CaiGouSchedule> getItemDailyDetailList(@Param("caiGouScheduleReq") CaiGouScheduleReq caiGouScheduleReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    List<KeyItemFixedExcel> getKeyItemFixedExcel(@Param("caiGouScheduleReq") KeyItemReportReq keyItemReportReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    IPage<OrderOtdReport> getOrderOtdBasicInfo(IPage page, @Param("req") OrderOtdReq orderOtdReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    List<OrderOtdReport> getOrderOtdBasicInfoList(@Param("req") OrderOtdReq orderOtdReq);

    @DS("oracle")
    IPage<AutoOrderOtdReport> getAutoOrderOtdInfo(IPage page,@Param("req")AutoOrderOtdReq autoOrderOtdReq);

    @DS("oracle")
    List<AutoOrderOtdReport> getAutoOrderOtdList(@Param("req")AutoOrderOtdReq autoOrderOtdReq);

    IPage<OrderAmountOtdReport> getOrderAmountOtdInfo(IPage page, @Param("req") OrderAmountOtdReportReq orderAmountOtdReportReq);

    List<OrderAmountOtdReport> getOrderAmountOtdList(@Param("req") OrderAmountOtdReportReq orderAmountOtdReportReq);

    List<OrderAmountOtdReport> getOrderAmountOtdLastYearList(@Param("req") OrderAmountOtdReportReq orderAmountOtdReportReq);
    List<OrderAmountOtdReport> getOrderAmountOtdLastYear2List(@Param("req") OrderAmountOtdReportReq orderAmountOtdReportReq);

    List<OrderAmountOtdReport> getOrderAmountOtdtotalList(@Param("req") OrderAmountOtdReportReq orderAmountOtdReportReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    IPage<MouldManagementEntity> getMouldManagementReport(IPage page, @Param("req") MouldManagementEntity mouldManagement);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    IPage<MouldManagementWholeEntity> getMouldManagementWholeReport(IPage page,  @Param("req")MouldManagementWholeEntity mouldManagement);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    List<MouldManagementEntity> getMouldManagementReportList(@Param("req") MouldManagementEntity mouldManagement);

    @Delete("delete from mould_management where id =#{req.id}")
    Boolean deleteMouldManagementReport(@Param("req") MouldManagementEntity mouldManagement);


    @Update("update mould_management set mould_status =#{req.mouldStatus},remark=#{req.remark} where  id =#{req.id}")
    Boolean updateMouldManagementReport(@Param("req") MouldManagementEntity mouldManagement);



    @Insert("INSERT INTO mould_management (  " +
        " mould_status,  " +
        " standard,  " +
        " seal_type,  " +
        " float_or_fixed,  " +
        " valve_type,  " +
        " product_material,  " +
        " caliber_size,  " +
        " pressure_size,  " +
        " mould_standards,  " +
        " fixed_assets_code,  " +
        " zj_supplier,  " +
        " mould_supplier,  " +
        " is_series,  " +
        " mould_material,  " +
        " remark   " +
        ")   " +
        "VALUE  " +
        " (#{req.mouldStatus},#{req.standard},#{req.sealType},#{req.floatOrFixed},#{req.valveType},#{req.productMaterial},#{req.caliberSize},#{req.pressureSize},#{req.mouldStandards},#{req.fixedAssetsCode},#{req.zjSupplier},#{req.mouldSupplier},#{req.isSeries},#{req.mouldMaterial},#{req.remark})")
    Boolean insertMouldManagementReport(@Param("req") MouldManagementEntity mouldManagement);

    Boolean insertMouldManagementWholeReport(MouldManagementWholeEntity mouldManagementWholeEntity);

    Boolean updateMouldManagementWholeReport(MouldManagementWholeEntity mouldManagementWholeEntity);



    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    IPage<JitManagementEntity> getJitManagementReport(IPage page, @Param("req") JitManagementEntity JitManagement);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    List<JitManagementEntity> getJitManagementReportList(@Param("req") JitManagementEntity JitManagement);

    @Delete("delete from jit_management where id =#{req.id}")
    Boolean deleteJitManagementReport(@Param("req") JitManagementEntity JitManagement);


    @Update("update jit_management set actual_rcv_num =#{req.actualRcvNum},req_perpare_date=#{req.reqPerpareDate} where  id =#{req.id}")
    Boolean updateJitManagementReport(@Param("req") JitManagementEntity JitManagement);



    @Insert("INSERT INTO    jit_management    (   " +
        "     sup_code   ,   " +
        "     sup_name   ,   " +
        "     item_code   ,   " +
        "     item_name   ,   " +
        "     req_perpare_num   ,   " +
        "     actual_rcv_num   ,   " +
        "     rcv_warn_num   ,   " +
        "     req_perpare_date   ,   " +
        "     expect_money   ,   " +
        "     jit_date   ,   " +
        "     remark   ,   " +
        "     create_user   ,   " +
        "     create_time   ,   " +
        "     update_user   ,   " +
        "     update_time       " +
        ")   " +
        "VALUES   " +
        "  (   " +
        "    #{req.supCode},   " +
        "    #{req.supName},   " +
        "    #{req.itemCode},   " +
        "    #{req.itemName},   " +
        "    #{req.reqPerpareNum},   " +
        "    #{req.actualRcvNum},   " +
        "    #{req.rcvWarnNum},   " +
        "    #{req.reqPerpareDate},   " +
        "    #{req.expectMoney},   " +
        "    #{req.jitDate},   " +
        "    #{req.remark},   " +
        "    #{req.createUser},   " +
        "    #{req.createTime},   " +
        "    #{req.updateUser},   " +
        "    #{req.updateTime})")
    Boolean insertJitManagementReport(@Param("req") JitManagementEntity JitManagement);

    @Insert("INSERT INTO    jit_management_history    (   " +
        "     sup_code   ,   " +
        "     sup_name   ,   " +
        "     item_code   ,   " +
        "     item_name   ,   " +
        "     req_perpare_num   ,   " +
        "     actual_rcv_num   ,   " +
        "     rcv_warn_num   ,   " +
        "     req_perpare_date   ,   " +
        "     expect_money   ,   " +
        "     jit_date   ,   " +
        "     remark   ,   " +
        "     create_user   ,   " +
        "     create_time   ,   " +
        "     update_user   ,   " +
        "     update_time       " +
        ")   " +
        "VALUES   " +
        "  (   " +
        "    #{req.supCode},   " +
        "    #{req.supName},   " +
        "    #{req.itemCode},   " +
        "    #{req.itemName},   " +
        "    #{req.reqPerpareNum},   " +
        "    #{req.actualRcvNum},   " +
        "    #{req.rcvWarnNum},   " +
        "    #{req.reqPerpareDate},   " +
        "    #{req.expectMoney},   " +
        "    #{req.jitDate},   " +
        "    #{req.remark},   " +
        "    #{req.createUser},   " +
        "    #{req.createTime},   " +
        "    #{req.updateUser},   " +
        "    #{req.updateTime})")
    Boolean insertJitManagementHistory(@Param("req") JitManagementEntity JitManagement);



    @Select("select sup_code,item_code,item_name,sum(num) num  \n" +
        "from (\n" +
        "SELECT\n" +
        "\ta.sup_code,a.item_code,a.item_name,sum(a.req_num) num \n" +
        "from\n" +
        "\tBI_Supdeliv_Plan_Data_Detail_View a   left join atw_po_item b on a.po_code=b.po_code and a.po_ln = b.po_ln\n" +
        "WHERE\n" +
        "\tsubstring_index( a.item_name, '-', - 1 ) IN ( 'WCB', 'WCC', 'LCB', 'LCC', 'C5', 'C12', 'CF8', 'CF3', 'CF8M', 'CF3M', 'CF8C', '4A', '5A' ) and a.pro_no is not null \n" +
        "\tand a.sup_code is not null  and date_format(date_add(NOW(), interval 1 month),'%Y-%m')=date_format(a.plan_date, '%Y-%m') \n" +
        "\tand date_sub(a.plan_date, interval 30 day)>= date_format(b.create_time, '%Y-%m-d')\n" +
        "\tGROUP BY a.sup_code,a.item_code\n" +
        "\t\n" +
        "\tunion\n" +
        "\t\n" +
        "\n" +
        "SELECT\n" +
        "  sup_code,item_code,a.item_name,sum(not_rcv_num)*0.4 num \n" +
        "FROM\n" +
        "\tBI_Supdeliv_Plan_Data_Detail_View a \n" +
        "WHERE\n" +
        "\tsubstring_index( a.item_name, '-', - 1 ) IN ( 'WCB', 'WCC', 'LCB', 'LCC', 'C5', 'C12', 'CF8', 'CF3', 'CF8M', 'CF3M', 'CF8C', '4A', '5A' ) and pro_no is  null and sup_code is not null\n" +
        "\tand date_format(a.wwpo_date, '%Y-%m-%d') <=date_format(date_add(now(), interval 1 month), '%Y-%m-%d')\n" +
        "\tGROUP BY sup_code,item_code) x \n" +
        "\tGROUP BY sup_code,item_code")
    List<SupplierOutputFromOracle> getSupplierOutputFromOracleList();







    @Insert("insert atw_supplier_output_info (sup_name,sup_code,item_code,item_name,output_cent,weight_cent,material_type,cj,type,bj,fl_struct,series,material,number,casting_process,bottleneck_processes) " +
        "   VALUES (    " +
        "#{req.supName},#{req.supCode},#{req.itemCode},#{req.itemName},#{req.outputCent},#{req.weightCent},#{req.materialType},#{req.cj},#{req.type},#{req.bj}, " +
        "#{req.flStruct},#{req.series},#{req.material},#{req.number},#{req.castingProcess},#{req.bottleneckProcesses})")
    void insertSupplierOutputInfo(@Param("req")SupplierOutputInfoEntity supplierOutputInfoEntity);


    @Delete("DELETE FROM atw_supplier_output_info")
    void deleteSupplierOutputInfo();



    @Select("select weight,production_capacity2,casting_process,bottleneck_processes from atw_supplier_output_match where material_type LIKE concat('%', #{req.materialType} ,'%') and cj=#{req.cj} " +
        " and type=#{req.type} and bj=#{req.bj} and  series=#{req.series} and sup_code= #{req.supCode}  and   material = (select material_belong from atw_supplier_output_zj_material where material_type=#{req.material}) LIMIT 1")
    SupplierOutputInfoDto getProductionCapacity(@Param("req")SupplierOutputInfoDto supplierOutputInfoDto);

    @Select("select weight,production_capacity2,casting_process,bottleneck_processes from atw_supplier_output_match where material_type LIKE concat('%', #{req.materialType} ,'%') and cj=#{req.cj} " +
        " and type=#{req.type} and bj=#{req.bj} and  series=#{req.series} and priority= 1  and   material = (select material_belong from atw_supplier_output_zj_material where material_type=#{req.material}) LIMIT 1")
    SupplierOutputInfoDto getProductionCapacityByPriority(@Param("req")SupplierOutputInfoDto supplierOutputInfoDto);

    @Select("select gy,zgs,zcz,sup_name,round(SUM(output),2) zygs,round(SUM(weight_output)/1000,2) zycn ,round(SUM(output)-zgs,2) ycgs , round(SUM(weight_output)/1000- zcz,2)  yccn   \n" +
        ", CONCAT(round((SUM(output)-zgs)*100/zgs,2),'%') gysycbl  from (SELECT\n" +
        "\tb.sup_name,b.gy,b.zgs,b.zcz,(a.output_cent*number) output,(a.weight_cent*number) weight_output\n" +
        "FROM\n" +
        "\tatw_supplier_output_info a\n" +
        "\tLEFT JOIN atw_supplier_output b ON a.sup_code = b.sup_code \n" +
        "\tAND a.casting_process = b.gy2 \n" +
        "WHERE\n" +
        "\ta.weight_cent IS NOT NULL  and b.zgs is not null) z  group by gy,zgs,zcz,sup_name  ORDER BY gy")
    IPage<SupplierOutputVo> getProductionCapacityList(IPage page,@Param("req")SupplierOutputVo supplierOutputVo);


    @Select("select totalpo.*,casting_process,bottleneck_processes,output_cent,(weight_cent/1000) weight_cent,gy,b.zgs,b.zcz from (\n" +
        "\tSELECT\n" +
        "\ta.*\n" +
        "from\n" +
        "\tBI_Supdeliv_Plan_Data_Detail_View a   left join atw_po_item b on a.po_code=b.po_code and a.po_ln = b.po_ln\n" +
        "WHERE\n" +
        "\tsubstring_index( a.item_name, '-', - 1 ) IN ( 'WCB', 'WCC', 'LCB', 'LCC', 'C5', 'C12', 'CF8', 'CF3', 'CF8M', 'CF3M', 'CF8C', '4A', '5A' ) and a.pro_no is not null \n" +
        "\tand a.sup_code is not null  and date_format(date_add(NOW(), interval 1 month),'%Y-%m')=date_format(a.plan_date, '%Y-%m') \n" +
        "\tand date_sub(a.plan_date, interval 30 day)>= date_format(b.create_time, '%Y-%m-d')\n" +
        "\t\n" +
        "\tunion \n" +
        "\t\n" +
        "\n" +
        "SELECT\n" +
        "  a.*\n" +
        "FROM\n" +
        "\tBI_Supdeliv_Plan_Data_Detail_View a \n" +
        "WHERE\n" +
        "\tsubstring_index( a.item_name, '-', - 1 ) IN ( 'WCB', 'WCC', 'LCB', 'LCC', 'C5', 'C12', 'CF8', 'CF3', 'CF8M', 'CF3M', 'CF8C', '4A', '5A' ) and pro_no is  null and sup_code is not null\n" +
        "\tand date_format(a.wwpo_date, '%Y-%m-%d') <=date_format(date_add(NOW(), interval 1 month), '%Y-%m-%d')\n" +
        "\t) totalpo left join atw_supplier_output_info info on totalpo.item_name=info.item_name and totalpo.sup_code=info.sup_code\n" +
        "\tLEFT JOIN atw_supplier_output b ON totalpo.sup_code = b.sup_code \n" +
        "\tAND info.casting_process = b.gy2 \n" +
        "\t where output_cent is not NULL  and totalpo.sup_code=#{req.supCode}")
    List<SupplierOutputFromOracle> getSupplierOutputOfEcharts(@Param("req") SupplierScheduleReq supplierScheduleReq);

    @Select("select  zgs, zcz from atw_supplier_output where sup_code=#{req.supCode} limit 1")
    SupplierOutputFromOracle getProductionCapacityWithSup(@Param("req")SupplierScheduleReq supplierScheduleReq);

    @Select("select sup_code,gy,zgs,zcz from atw_supplier_output group by sup_code,gy ")
    List<SupplierOutputFromOracle> getProductionCapacityWithGy(@Param("req")SupplierScheduleReq supplierScheduleReq);








}
