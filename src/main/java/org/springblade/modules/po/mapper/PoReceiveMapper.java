package org.springblade.modules.po.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springblade.modules.po.dto.PoReceiveDTO;
import org.springblade.modules.po.entity.PoReceiveEntity;
import org.springblade.modules.po.vo.PoReceiveVO;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.pr.dto.U9PrDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface PoReceiveMapper extends BaseMapper<PoReceiveEntity> {

    Integer getMaxSeq(Date start, Date end);

    @Select("select * from atw_po_receive where rcv_code = #{rcvCode} and is_deleted = 0 and status in ('20','22','24','25')")
    List<PoReceiveEntity> getOldDOInfo(@Param("rcvCode") String  rcvCode);

    @Select("select count(*) from atw_po_sn where rcv_code = #{rcvCode} and is_deleted = 0")
    Integer isSNExisted(@Param("rcvCode") String  rcvCode);

    @Update("update atw_po_receive set status = 40 where rcv_code = #{rcvCode} and is_deleted = 0")
    boolean deleteByRcvCode(@Param("rcvCode") String  rcvCode);

    @Update("update atw_do_out_check set is_deleted = 1,update_user = #{updater},update_time = #{updateTime} where rcv_code = #{rcvCode} and is_deleted = 0")
    boolean deleteDoOutCheckByRcvCode(@Param("rcvCode") String  rcvCode, @Param("updater") String updater, @Param("updateTime") String updateTime);

    @Update("update atw_po_receive set status = 30 where rcv_code = #{rcvCode} and is_deleted = 0")
    boolean closeByRcvCode(@Param("rcvCode") String  rcvCode);

    @Update("update atw_po_receive set status = 20 where rcv_code = #{rcvCode} and is_deleted = 0")
    boolean recoveryByRcvCode(@Param("rcvCode") String  rcvCode);

    @Update("update atw_po_receive set business_code = #{businessCode} , business_name = #{businessName} where rcv_code = #{rcvCode} and is_deleted = 0")
    boolean updateBusinessByRcvCode(@Param("rcvCode") String  rcvCode,@Param("businessCode") String  businessCode,@Param("businessName") String  businessName);

    @Select("select * from atw_po_receive where is_deleted = 0 and status <= 40  order by rcv_code")
    List<PoReceiveEntity>  getAllDo();

    @Select("select * from atw_po_receive where find_in_set(rcv_code,#{rcvCodes}) and is_deleted = 0 and status <= 30 order by rcv_code")
    List<PoReceiveEntity>  getPartDoByRcvCodes( @Param("rcvCodes") String  rcvCodes);


    @Select("select * from atw_po_receive where rcv_code=#{rcvCode} and is_deleted = 0 and status <= 30")
    List<PoReceiveEntity>  getDoInfoByRcvCode( @Param("rcvCode") String  rcvCode);

    @Select("select count(*) from atw_do_out_check where rcv_code = #{rcvCode} and status = '1' and is_deleted = 0")
    int isOpen(@Param("rcvCode") String  rcvCode);

    @Select("select is_out_check from atw_po_receive where rcv_code = #{rcvCode} and is_deleted = 0 group by is_out_check")
    String getIsOutCheck(@Param("rcvCode") String  rcvCode);

    @Select("SELECT\n" +
        "\t*\n" +
        "FROM\n" +
        "\tatw_po_item \n" +
        "WHERE\n" +
        "\tis_deleted = 0 \n" +
        "\tAND ( pro_goods_num > 0 AND STATUS = 20 AND sup_code = #{sup_code} AND item_code = #{item_code} )\n" +
        "\tAND\t\tsup_confirm_date<(select sup_confirm_date  from  atw_po_item where po_code=#{po_code} and po_ln=#{po_ln})")
    List<PoReceiveDTO>  checkLastestPO(@Param("sup_code") String  sup_code,@Param("item_code") String  item_code,@Param("po_code") String  po_code,@Param("po_ln") String  po_ln);

    @Select("select count(*) from atw_po_receive where rcv_code = #{rcvCode} and is_deleted = 0 and status in ('20','22','24','25')")
    int  getCountByRcvCode( @Param("rcvCode") String  rcvCode);

    @Select("select IFNULL(unqualified_num,0) from atw_po_sn where rcv_code = #{rcvCode} and po_code = #{poCode} and po_ln = #{poLn} and heat_code = #{heatCode} and is_deleted = 0")
    Integer getUnqualifiedNum(@Param("rcvCode") String  rcvCode,@Param("poCode") String  poCode,@Param("poLn") Integer poLn,@Param("heatCode") String heatCode);

    @Insert("insert into atw_do_out_check(rcv_code,status,create_time,update_time,create_user,update_user) VALUES(#{rcvCode},'0',#{createTime},#{createTime},#{account},#{account})")
    void insertDoOutCheck(@Param("rcvCode") String  rcvCode, @Param("createTime") String createTime, @Param("account") String account);

    @Update("update atw_po_receive set is_out_check = '1' where rcv_code = #{rcvCode} and is_deleted = 0")
    boolean setOut(@Param("rcvCode") String  rcvCode);

    @Select("select count(*) from atw_po_sn where rcv_code = #{rcvCode} and is_deleted = 0")
    int  getSnCount( @Param("rcvCode") String  rcvCode);

    IPage<PoReceiveVO> selectPageOfParams(IPage<PoReceiveDTO> page, @Param("dto") PoReceiveDTO poReceive);

}
