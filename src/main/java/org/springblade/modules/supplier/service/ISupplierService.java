package org.springblade.modules.supplier.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.SneakyThrows;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.material.dto.MaterialPriceDTO;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.supplier.dto.CaiGouScheduleReq;
import org.springblade.modules.supplier.dto.SupplierDTO;
import org.springblade.modules.supplier.dto.SupplierScheduleReq;
import org.springblade.modules.supplier.dto.SupplierUpdateReq;
import org.springblade.modules.supplier.entity.CaiGouSchedule;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.entity.SupplierSchedule;
import org.springblade.modules.supplier.vo.OmsEchrtsOfSupplierVO;
import org.springblade.modules.supplier.vo.SupplierScheduleVO;
import org.springblade.modules.supplier.vo.SupplierVO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 供应商 服务类
 *
 * @author xianboss
 */
public interface ISupplierService extends BaseService<Supplier> {

    String THIS_WEEK         = "week1";          //  本周
    String SECOND_WEEK      = "week2";       //  第二周
    String THIRD_WEEK      = "week3";      //  第三周
    String FORTH_WEEK = "week4"; //  第四周
    String FIFTH_WEEK      = "week5";      //  第五周
    String FUTURE_WEEK   = "week6";   //  未来周
    String LAST_WEEK        = "week0";         //  过去周

    String THREE_WEEK        = "threeWeeks";         //  三周、用来发邮件的

    /**
     * 自定义分页
     *
     * @param page
     * @param supplier
     * @return
     */
    IPage<SupplierVO> selectSupplierPage(IPage<SupplierVO> page, SupplierVO supplier);

    List<Map<String, Object>> getWeekCount(SupplierScheduleReq supplierScheduleReq);

    SupplierVO getDetails(Supplier supplier);

    @Transactional
    boolean save(Supplier entity);

    @SneakyThrows
    boolean save(SupplierDTO supplier);

    List<Supplier> listByCode(String code);

    Supplier getByCode(String code);

    Supplier getByName(String name);

    boolean resetPassword(Supplier supplier);

    /**
     * 修改
     * @param supplier
     * @return
     */
    boolean updateBiz(SupplierUpdateReq supplier);


    /**
     * 修改 （采购员、资源员）
     * @param supplier
     * @return
     */
    boolean updateMore(SupplierUpdateReq supplier);



    /**
     * 取得其他联系人的信息
     *
     * @param page
     * @param supCode
     * @return
     */
    IPage<SupplierVO> getOhterCtcInfos(IPage<SupplierVO> page, String supCode);

    boolean saveOhterCtcInfos(SupplierDTO supplier);

    boolean updateOhterCtcInfos(Supplier supplier);

    boolean delOhterCtcInfos(Supplier supplier);

    void exportExcel(String supCode, HttpServletResponse response) throws RuntimeException;

    void exportExcelOtd(String supCode, HttpServletResponse response) throws RuntimeException;

    IPage<SupplierSchedule> getSchedule(IPage<SupplierSchedule> page, SupplierSchedule supplierSchedule);

    IPage<SupplierSchedule> getScheduleOfOms(IPage<SupplierSchedule> page, SupplierScheduleReq supplierScheduleReq);

    IPage<CaiGouSchedule> getCaiGouSchedules(IPage<CaiGouSchedule> page, CaiGouScheduleReq caiGouScheduleReq);

    IPage<CaiGouSchedule> getCaiGouSchedulesUnchecked(IPage<CaiGouSchedule> page, CaiGouScheduleReq caiGouScheduleReq);

    IPage<CaiGouSchedule> getCaiGouSchedulesOffset(IPage<CaiGouSchedule> page, CaiGouScheduleReq caiGouScheduleReq);

    IPage<CaiGouSchedule> getCaiGouSchedulesUnpip(IPage<CaiGouSchedule> page, CaiGouScheduleReq caiGouScheduleReq);

    boolean batchSendEmail(List<SupplierSchedule> scheduleList);

    boolean saveData(SupplierScheduleReq supplierScheduleReq);

    boolean saveDataOfCaiGou(CaiGouScheduleReq caiGouScheduleReq);

    boolean lockPro(CaiGouScheduleReq caiGouScheduleReq);

    boolean freePro(CaiGouScheduleReq caiGouScheduleReq);

    void exportAll(SupplierScheduleReq supplierScheduleReq, HttpServletResponse response);

    void exportCaiGouAll(CaiGouScheduleReq caiGouScheduleReq, HttpServletResponse response);
    void exportCaiGouAllOffset(CaiGouScheduleReq caiGouScheduleReq, HttpServletResponse response);
    void exportCaiGouAllUnchecked(CaiGouScheduleReq caiGouScheduleReq, HttpServletResponse response);
    void exportCaiGouAllUnpip(CaiGouScheduleReq caiGouScheduleReq, HttpServletResponse response);

    List<OmsEchrtsOfSupplierVO> getBarEchartsNum(SupplierScheduleReq supplierScheduleReq);

    OmsEchrtsOfSupplierVO getLineEchartsNum(SupplierScheduleReq supplierScheduleReq);

    List<OmsEchrtsOfSupplierVO> getMainData(SupplierScheduleReq supplierScheduleReq);

    List<OmsEchrtsOfSupplierVO> getMainDataWeek(SupplierScheduleReq supplierScheduleReq);

    List<OmsEchrtsOfSupplierVO> getMainDataForest(SupplierScheduleReq supplierScheduleReq);

    int getTraceTabCount();

    void otdExport(SupplierScheduleReq supplierScheduleReq, HttpServletResponse response) throws Exception;
}
