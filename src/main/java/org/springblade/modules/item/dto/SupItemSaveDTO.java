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
package org.springblade.modules.item.dto;

import lombok.Data;
import org.springblade.modules.supitem.dto.SupItemDTO;

import java.util.List;

/**
 * 保存 供应商物料 交叉关系 数据传输对象实体类
 *
 * @author Blade
 * @since 2019-11-27
 */
@Data
public class SupItemSaveDTO{
    private static final long serialVersionUID = 1L;

    private String itemCode;
    private List<SupItemDTO> SupItems;
}
