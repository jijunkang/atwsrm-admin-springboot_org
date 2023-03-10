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
package org.springblade.modules.resource.config;

import lombok.AllArgsConstructor;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.modules.resource.builder.OssBuilder;
import org.springblade.modules.resource.mapper.OssMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Oss配置类
 *
 * @author Will
 */
@Configuration
@AllArgsConstructor
public class BladeOssConfiguration {

	private OssProperties ossProperties;

	private OssMapper ossMapper;

	@Bean
	public OssBuilder ossBuilder() {
		return new OssBuilder(ossProperties, ossMapper);
	}

}
