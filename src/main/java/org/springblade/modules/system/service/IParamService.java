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
package org.springblade.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.system.entity.Param;
import org.springblade.modules.system.vo.ParamVO;

/**
 * 服务类
 *
 * @author Will
 */
public
interface IParamService extends BaseService<Param>{

	/***
	 * 自定义分页
	 * @param page
	 * @param param
	 * @return
	 */
	IPage<ParamVO> selectParamPage(IPage<ParamVO> page, ParamVO param);

	String getValue(String key);

}
