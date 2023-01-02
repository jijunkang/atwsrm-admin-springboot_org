package org.springblade.modules.pr.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.pr.dto.U9PrDTO;
import org.springblade.modules.pr.dto.U9PrExReq;
import org.springblade.modules.pr.entity.U9PrEntityEx;
import org.springblade.modules.supplier.dto.CaiGouScheduleReq;
import org.springblade.modules.supplier.entity.CaiGouSchedule;

import java.util.List;

@Mapper

public interface U9PrEntityExMapper extends BaseMapper<U9PrEntityEx> {


    IPage<U9PrEntityEx> selectPageByReq(IPage page, @Param("prReq") U9PrExReq prReq);


    List<U9PrEntityEx> selectListByReq( @Param("prReq") U9PrExReq prReq);

    List<U9PrDTO> selectU9ListByReq(@Param("prReq") PrReq prReq);



    @Update("update atw_u9_pr_abnormal set is_deleted=1 where id=#{id}")
    boolean lockExPrById(@Param("id") Long id);

    @Update("update atw_u9_pr set is_deleted=0,update_time=NOW() where pr_code=#{prCode} and pr_ln=#{PrLn}")
    boolean freePrByPrLn(@Param("prCode") String prCode,@Param("PrLn") Integer PrLn);

    @Update("update atw_u9_pr_abnormal set is_deleted=1,update_time=NOW(),solution=#{solution}  where pr_code=#{prCode} and pr_ln=#{prLn}")
    boolean deletedExPrByPrLn(@Param("prCode") String prCode,@Param("prLn") int prLn,@Param("solution") String solution);



}
