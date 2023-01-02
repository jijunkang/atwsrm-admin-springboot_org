package org.springblade.modules.pr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.*;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.pr.dto.U9PrDTO;
import org.springblade.modules.pr.dto.U9PrExReq;
import org.springblade.modules.pr.entity.U9PrEntityEx;
import org.springblade.modules.pr.entity.U9PrEntityNoPro;

import java.util.List;

@Mapper

public interface U9PrEntityNoProMapper extends BaseMapper<U9PrEntityNoPro> {
    @Select("select * from atw_u9_pr_no_project where is_deleted=0")
    List<U9PrEntityNoPro> selectListByReq();

    @Select("select * from atw_u9_pr_no_project where is_deleted=0")
    IPage<U9PrEntityNoPro> selectPageByReq(IPage page, @Param("prReq") U9PrExReq prReq);


    @Select("select * from atw_u9_pr_no_project where  pr_code=#{prCode}  and pr_ln=#{prLn} ")
    U9PrEntityNoPro selectListByPrLn(@Param("prCode") String prCode,@Param("prLn") int prLn);


    @Delete("update atw_u9_pr_no_project set is_deleted=1,update_time=NOW() where  pr_code=#{prCode}  and pr_ln=#{prLn}  ")
    boolean releaseNoProPRByPrLn(@Param("prCode") String prCode,@Param("prLn") int prLn);

    @Delete("delete from atw_u9_pr_no_project where  pr_code=#{prCode}  and pr_ln=#{prLn}  ")
    boolean deleteNoProPRByPrLn(@Param("prCode") String prCode,@Param("prLn") int prLn);





}
