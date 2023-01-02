package org.springblade.modules.pr.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.pr.dto.*;
import org.springblade.modules.pr.entity.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 请购单 服务类
 * @author Will
 */
public
interface IU9PrExService extends BaseService<U9PrEntityEx>{

    Wrapper<U9PrEntityEx> getQueryWrapper(U9PrEntityEx u9PrEntityEx);

    IPage<U9PrEntityEx> getU9PrEx(IPage<U9PrEntityEx> page, U9PrExReq u9PrExReq);

    IPage<U9PrEntityNoPro> getU9PrNoPro(IPage<U9PrEntityNoPro> page, U9PrExReq u9PrExReq);

    boolean freePr(List<U9PrEntityEx> u9PrEntityExList);

    void exportExcel(U9PrExReq u9PrExReq, HttpServletResponse response);




}

