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
package org.springblade.modules.supplier.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.modules.supplier.entity.SupDept;

import java.util.List;

/**
 * 服务类
 *
 * @author Will
 */
public interface ISupDeptService extends IService<SupDept> {


    /**
     * 根据供应商编号和 名称 创建部门
     * @param supCode
     * @param supName
     * @return
     */
    SupDept create(String supCode, String supName);
}
