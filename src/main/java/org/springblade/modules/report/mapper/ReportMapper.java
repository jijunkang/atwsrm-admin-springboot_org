package org.springblade.modules.report.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.*;
import org.springblade.modules.report.dto.*;
import org.springblade.modules.report.entity.*;
import org.springblade.modules.report.vo.KeyItemFixedExcel;
import org.springblade.modules.report.vo.SupplierOutputQZVo;
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

    @SqlParser(filter = true)
    @Select("SELECT \n" +
        "\tplan.sup_code,plan.item_code,plan.item_name, qt.sup_name,\n" +
        "\tif(plan.pro_no is null,pi.pro_goods_num,if( left(wwpo_date,7) > date_format( DATE_SUB( curdate( ), INTERVAL -1 MONTH ), '%Y-%m' ),pi.pro_goods_num, req_num)) req_num, \n" +
        "\tplan.po_code ,plan.pro_no,\n" +
        "\tif(plan.pro_no is not null,left(plan.req_date,10),left(plan.wwpo_date,10)) date\n" +
        "\tFROM \n" +
        "\tatw_caigou_plan_data_lock_view plan \n" +
        "\tLEFT JOIN atw_item item ON plan.item_code = item.CODE and item.is_deleted = 0 \n" +
        "\tleft join atw_po_item pi on plan.po_code = pi.po_code and plan.po_ln = pi.po_ln \n" +
        "\tleft join atw_supplier_output_info_qz_total qt on plan.sup_code = qt.sup_code\n" +
        "\tWHERE \n" +
        "\titem.main_code = '130301'  \n" +
        "\tand plan.po_code like 'PO%' \n" +
        "\tand plan.sup_code in (SELECT DISTINCT sup_code FROM atw_supplier_output_match_qz) \n" +
        "\tAND \n" +
        "\tIF \n" +
        "\t( \n" +
        "\tplan.pro_no IS NOT NULL, \n" +
        "\tplan_date >= date_format( DATE_SUB( curdate( ), INTERVAL 0 MONTH ), '%Y-%m-20' ) AND plan_date <= date_format( DATE_SUB( curdate( ), INTERVAL - 2 MONTH ), '%Y-%m-10' ) , \n" +
        "\twwpo_date >= date_format( DATE_SUB( curdate( ), INTERVAL 0 MONTH ), '%Y-%m-20' ) AND wwpo_date <= date_format( DATE_SUB( curdate( ), INTERVAL - 2 MONTH ), '%Y-%m-10' )  \n" +
        "\t)")
    List<SupplierOutputFromOracle> getSupplierOutputFromOracleListQZ();

    @Insert("insert atw_supplier_output_info (sup_name,sup_code,item_code,item_name,output_cent,weight_cent,material_type,cj,type,bj,fl_struct,series,material,number,casting_process,bottleneck_processes) " +
        "   VALUES (    " +
        "#{req.supName},#{req.supCode},#{req.itemCode},#{req.itemName},#{req.outputCent},#{req.weightCent},#{req.materialType},#{req.cj},#{req.type},#{req.bj}, " +
        "#{req.flStruct},#{req.series},#{req.material},#{req.number},#{req.castingProcess},#{req.bottleneckProcesses})")
    void insertSupplierOutputInfo(@Param("req")SupplierOutputInfoEntity supplierOutputInfoEntity);


    @Insert("INSERT atw_supplier_output_info_qz (\n" +
        "\tsup_name,\n" +
        "\tsup_code,\n" +
        "\titem_code,\n" +
        "\titem_name,\n" +
        "\tproduce_capacity,\n" +
        "\tsingle_price,\n" +
        "\tdate,\n" +
        "\tmaterial_type,\n" +
        "\tcj,\n" +
        "\ttype,\n" +
        "\tbj,\n" +
        "\tdj,\n" +
        "\ttc,\n" +
        "\tmaterial,\n" +
        "\tnumber,\n" +
        "\tcasting_process,\n" +
        "\tbottleneck_processes \n" +
        ")\n" +
        "VALUES\n" +
        "\t(\t\n" +
        "\t\t#{req.supName},\n" +
        "\t\t#{req.supCode},\n" +
        "\t\t#{req.itemCode},\n" +
        "\t\t#{req.itemName},\n" +
        "\t\t#{req.produceCapacity},\n" +
        "\t\t#{req.singlePrice},\n" +
        "\t\t#{req.date},\n" +
        "\t\t#{req.materialType},\n" +
        "\t\t#{req.cj},\n" +
        "\t\t#{req.type},\n" +
        "\t\t#{req.bj},\n" +
        "\t\t#{req.dj},\n" +
        "\t\t#{req.tc},\n" +
        "\t\t#{req.material},\n" +
        "\t\t#{req.number},\n" +
        "\t\t#{req.castingProcess},\n" +
        "\t\t#{req.bottleneckProcesses}\n" +
        "\t)")
    void insertSupplierOutputInfoQZ(@Param("req")SupplierOutputInfoQZEntity supplierOutputInfoEntity);


    @Delete("DELETE FROM atw_supplier_output_info")
    void deleteSupplierOutputInfo();

    @Delete("DELETE FROM atw_supplier_output_info_qz")
    void deleteSupplierOutputInfoQZ();



    @Select("select weight,production_capacity2,casting_process,bottleneck_processes from atw_supplier_output_match where material_type LIKE concat('%', #{req.materialType} ,'%') and cj=#{req.cj} " +
        " and type=#{req.type} and bj=#{req.bj} and  series=#{req.series} and sup_code= #{req.supCode}  and   material = (select material_belong from atw_supplier_output_zj_material where material_type=#{req.material}) LIMIT 1")
    SupplierOutputInfoDto getProductionCapacity(@Param("req")SupplierOutputInfoDto supplierOutputInfoDto);


    @Select("SELECT\n" +
        "\tconcat(production_capacity1 , \"-\" , price )\n" +
        "FROM\n" +
        "\tatw_supplier_output_match_qz\n" +
        "WHERE\n" +
        "  material_type = '球体'\n" +
        "\tand cj = #{req.cj} \n" +
        "\tand type like concat('%', #{req.type}, '%')\n" +
        "\tand bj=#{req.bj} \n" +
        "\tand dj=#{req.dj} \n" +
        "\tand tc=#{req.tc}\n" +
        "\tand material=#{req.material}\n" +
        "\tand sup_code= #{req.supCode} "
        )
    String getProductionCapacityQZ(@Param("req")SupplierOutputInfoQZEntity supplierOutputInfoQZEntity);


    @Select("select weight,production_capacity2,casting_process,bottleneck_processes from atw_supplier_output_match where material_type LIKE concat('%', #{req.materialType} ,'%') and cj=#{req.cj} " +
        " and type=#{req.type} and bj=#{req.bj} and  series=#{req.series} and priority= 1  and   material = (select material_belong from atw_supplier_output_zj_material where material_type=#{req.material}) LIMIT 1")
    SupplierOutputInfoDto getProductionCapacityByPriority(@Param("req")SupplierOutputInfoDto supplierOutputInfoDto);

    @Select("SELECT\n" +
        "\tgy,\n" +
        "\tsup_code,\n" +
        "\tsup_name,\n" +
        "\tsum( price ) zyje ,\n" +
        "\tsum( capacity) zygs\n" +
        "FROM\n" +
        "\t(\n" +
        "\tSELECT\n" +
        "\t\tsup_code,\n" +
        "\t\tsup_name,\n" +
        "\t\ttc,\n" +
        "\t\tround( sum( single_price * number/10000 ), 2 ) price,\n" +
        "\t\tround( sum( produce_capacity * number / 60),2) capacity,\n" +
        "\t\tIF( FIND_IN_SET( tc, 'G14,G20' ), '冷喷', '热喷' ) gy \n" +
        "\tFROM\n" +
        "\t\t`atw_supplier_output_info_qz` \n" +
        "\tWHERE\n" +
        "\t\tsingle_price IS NOT NULL \n" +
        "\tGROUP BY\n" +
        "\t\tsup_code,\n" +
        "\t\ttc \n" +
        "\t) t \n" +
        "GROUP BY\n" +
        "\tgy,\n" +
        "\tsup_code,\n" +
        "\tsup_name\n" +
        "ORDER BY\n" +
        "\tsup_code")
    List<SupplierOutputQZVo>  getSupplierOutputInfoQZList(IPage page, @Param("req")SupplierOutputVo supplierOutputVo);


    @Select("select concat(total_produce_capacity,'-',total_price) from atw_supplier_output_info_qz_total where sup_name = #{supName}")
    String getZgsAndZjeBySupName(String supName);

    @Select("select concat(total_produce_capacity,'-',total_price) from atw_supplier_output_info_qz_total where sup_code = #{supName}")
    String getZgsAndZjeBySupCode(String supCode);


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

    @Select("select b.gscn output_cent,a.* from atw_caigou_plan_data_lock_view a left join atw_supplier_output_ptph b on a.item_code=b.item_code where b.item_code is not null and pro_no is NULL and (wwpo_date>=#{Startdate} and wwpo_date<=#{Enddate})\n" +
        "UNION\n" +
        "select b.gscn output_cent,a.* from atw_caigou_plan_data_lock_view a left join atw_supplier_output_ptph b on a.item_code=b.item_code where b.item_code is not null and pro_no is not NULL and (req_date>=#{Startdate} and req_date<=#{Enddate})")
    List<SupplierOutputFromOracle> getPtphOutputOfEcharts(@Param("Startdate") String Startdate,@Param("Enddate") String Enddate);

    @Select("SELECT * FROM atw_caigou_plan_data_lock_view where (item_name like '%中法兰锻件%' or item_name like '%阀盘锻件%' or item_name like '%阀体锻件%' or item_name like '%阀帽锻件%')\n" +
        "and pro_no is NULL and (wwpo_date>=#{Startdate} and wwpo_date<=#{Enddate})\n" +
        "\n" +
        "UNION\n" +
        "\n" +
        "SELECT * FROM atw_caigou_plan_data_lock_view where (item_name like '%中法兰锻件%' or item_name like '%阀盘锻件%' or item_name like '%阀体锻件%' or item_name like '%阀帽锻件%')\n" +
        "and pro_no is not NULL and (req_date>=#{Startdate} and req_date<=#{Enddate})")
    List<SupplierOutputFromOracle> getDjOutputOfEcharts(@Param("Startdate") String Startdate,@Param("Enddate") String Enddate);


    @Select("select gscn output_cent,zgs from atw_supplier_output_dj where item_type=#{itemType} and kj=#{kj} and bj=#{bj} and cz=#{cz} and  sup_code=#{supCode}  LIMIT 1 ")
    SupplierOutputFromOracle getDjOutput(@Param("itemType") String itemType,@Param("kj") String kj,@Param("bj") String bj,@Param("cz") String cz,@Param("supCode") String supCode);

    @Select("select  zgs, zcz from atw_supplier_output where sup_code=#{req.supCode} limit 1")
    SupplierOutputFromOracle getProductionCapacityWithSup(@Param("req")SupplierScheduleReq supplierScheduleReq);

    @Select("select sup_code,gy,zgs,zcz from atw_supplier_output group by sup_code,gy ")
    List<SupplierOutputFromOracle> getProductionCapacityWithGy(@Param("req")SupplierScheduleReq supplierScheduleReq);

    @Select("select * from atw_supplier_output_info_qz where single_price is not null and sup_code = #{supCode} ORDER BY DATE")
    List<SupplierOutputInfoQZEntity> getQZOutputOfEcharts(@Param("supCode")String supCode);
}
