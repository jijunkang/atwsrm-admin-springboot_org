package org.springblade.modules.supplier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.modules.supplier.dto.SupplierUpdateReq;
import org.springblade.modules.supplier.entity.SupUser;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.entity.SupplierSchedule;
import org.springblade.modules.supplier.vo.SupplierVO;

import java.util.List;

/**
 * 供应商 Mapper 接口
 *
 * @author Will
 */
public interface SupplierMapper extends BaseMapper<Supplier> {
    /**
     * 自定义分页
     *
     * @param page
     * @param supplier
     * @return
     */
    IPage<SupplierVO> selectSupplierPage(IPage page, SupplierVO supplier);


    /**
     * 自定义分页
     *
     * @param page
     * @param supplier
     * @return
     */
    IPage<SupplierVO> selectSupplierPageNew(IPage page, SupplierVO supplier);

    /**
     * 取得其他联系人信息
     * @param page
     * @param supCode
     * @return
     */
    List<SupplierVO> selectOtherCtcInfos(IPage page, String supCode);

    /**
     * 取得主联系人信息
     * @param supCode
     * @return
     */
    SupplierVO selectMainCtcInfos(String supCode);

    /**
     * 编辑主联系人信息
     * @param supplier
     * @return
     */
    boolean updateOtherCtcInfos(@Param("supplier") Supplier supplier);

    /**
     * 取得联系人的人数
     * @param supCode
     * @return
     */
    int selectCountCtcInfos(String supCode);

    /**
     * 删除联系人信息
     * @param supplier
     * @return
     */
    boolean delOhterCtcInfos(@Param("supplier") Supplier supplier);

    /**
     * 更新主联系人的职务
     * @param code
     * @param ctcDuty
     * @return
     */
    boolean updatePrimaryCtcDuty(String code, String ctcDuty);

    /**
     * 根据code获得供应商信息
     * @param code
     * @return
     */
    Supplier getSupplierByCode(String code);

    @Select("SELECT * FROM bi_delivrpt_data")
    List<SupplierSchedule> getAllBIInfo();

    @Select("SELECT name FROM blade_user where account = #{account}")
    String getName(@Param("account")String account);

    @Select("SELECT * FROM atw_supplier where primary_contact = '1' and  is_deleted = 0")
    List<Supplier> getAllSuppliers();

    @Select("SELECT type_name FROM atw_payway where sup_code = #{code} and is_default = 1 and is_deleted = 0")
    String getPayWay(@Param("code")String code);

    @Select("SELECT * FROM sup_user where is_deleted = 0 order by tenant_id")
    List<SupUser> getAllSupplierUsers();

    Boolean updateMore(@Param("req") SupplierUpdateReq req);

    @Select("SELECT a.*,b.initial_password FROM atw_supplier a left join sup_user b on  a.code = b.tenant_id where a.primary_contact='1' and a.id = #{id}")
    SupplierVO getDetails(@Param("id") String id);

    @Select("SELECT count(*) FROM atw_supplier where sup_brief = #{supBrief} and code!= #{code}")
    Integer getCountSupBriefByCodeAndSupBrief(@Param("supBrief")String supBrief,@Param("code")String code);

    @Select("SELECT count(*) FROM atw_supplier where sup_brief = #{supBrief} ")
    Integer getCountSupBrief(@Param("supBrief")String supBrief);
}
