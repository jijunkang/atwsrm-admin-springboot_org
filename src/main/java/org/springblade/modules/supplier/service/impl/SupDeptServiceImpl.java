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
package org.springblade.modules.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.modules.supplier.entity.SupDept;
import org.springblade.modules.supplier.mapper.SupDeptMapper;
import org.springblade.modules.supplier.service.ISupDeptService;
import org.springblade.modules.system.mapper.DeptMapper;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 * @author Will
 */
@Service
public
class SupDeptServiceImpl extends ServiceImpl<SupDeptMapper, SupDept> implements ISupDeptService{


    /**
     * 根据供应商编号和 名称 创建部门
     * @return
     */
    @Override
    public
    SupDept create(String supCode, String supName){
        SupDept dept = new SupDept();
        dept.setTenantId(supCode);
        dept.setDeptName(supName);
        dept.setFullName(supName);
        save(dept);
        return dept;
    }
}
