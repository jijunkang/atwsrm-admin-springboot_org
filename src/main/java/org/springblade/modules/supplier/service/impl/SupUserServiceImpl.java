package org.springblade.modules.supplier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springblade.common.constant.CommonConstant;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.DigestUtil;
import org.springblade.modules.supplier.entity.SupDept;
import org.springblade.modules.supplier.entity.SupUser;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.mapper.SupUserMapper;
import org.springblade.modules.supplier.service.ISupDeptService;
import org.springblade.modules.supplier.service.ISupUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 供应商 服务实现类
 * @author Will
 */
@Service
public
class SupUserServiceImpl extends BaseServiceImpl<SupUserMapper, SupUser> implements ISupUserService{

    @Autowired
    ISupDeptService supDeptService;


    @Override
    public
    SupUser create(Supplier entity){
        SupDept dept    = supDeptService.create(entity.getCode(), entity.getName());
        SupUser supUser = new SupUser();
        supUser.setTenantId(entity.getCode());
        supUser.setAccount(CommonConstant.SUP_DEFAULT_ACCOUNT);
        String initialPassword = String.valueOf((int)((Math.random() * 9 + 1) * 100000)); // 随机生成6位数
        supUser.setInitialPassword(initialPassword);
        supUser.setPassword(DigestUtil.encrypt(initialPassword));
        supUser.setName(entity.getName());
        supUser.setRealName(entity.getCode());
        supUser.setEmail(entity.getEmail());
        supUser.setDeptId(String.valueOf(dept.getId()));
        supUser.setRoleId("1123598816738675201"); //todo
        save(supUser);
        return supUser;
    }

    /**
     * 根据供应商编号查找
     * @return
     */
    @Override
    public
    SupUser getBySupCode(String supCode){
        QueryWrapper<SupUser> query = Wrappers.<SupUser>query().eq("tenant_id", supCode)
                                                               .eq("account", CommonConstant.SUP_DEFAULT_ACCOUNT);
        return getOne(query);
    }
}
