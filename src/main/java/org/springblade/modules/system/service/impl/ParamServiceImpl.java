/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author:  William Wang (wxx@idwsoft.com)
 */
package org.springblade.modules.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.modules.system.entity.Param;
import org.springblade.modules.system.mapper.ParamMapper;
import org.springblade.modules.system.service.IParamService;
import org.springblade.modules.system.vo.ParamVO;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author Will
 */
@Service
public
class ParamServiceImpl extends BaseServiceImpl<ParamMapper, Param> implements IParamService{

	@Override
	public
	IPage<ParamVO> selectParamPage(IPage<ParamVO> page, ParamVO param){
		return page.setRecords(baseMapper.selectParamPage(page, param));
	}


	@Override public
	String getValue(String key){
		Param param = getOne(Condition.getQueryWrapper(new Param()).eq("param_key", key));
		if(param == null){
			return null;
		}
		return param.getParamValue();
	}


}
