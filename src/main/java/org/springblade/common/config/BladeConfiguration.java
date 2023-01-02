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
package org.springblade.common.config;


import org.springblade.core.secure.registry.SecureRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Blade配置
 *
 * @author Will
 */
@Configuration
public class BladeConfiguration implements WebMvcConfigurer {

	@Bean
	public SecureRegistry secureRegistry() {
		SecureRegistry secureRegistry = new SecureRegistry();
		secureRegistry.setEnable(true);
		secureRegistry.excludePathPatterns("/blade-auth/**");
		secureRegistry.excludePathPatterns("/blade-log/**");
		secureRegistry.excludePathPatterns("/blade-system/menu/routes");
		secureRegistry.excludePathPatterns("/blade-system/menu/auth-routes");
		secureRegistry.excludePathPatterns("/blade-system/menu/top-menu");
		secureRegistry.excludePathPatterns("/blade-flow/process/resource-view");
		secureRegistry.excludePathPatterns("/blade-flow/process/diagram-view");
		secureRegistry.excludePathPatterns("/blade-flow/manager/check-upload");
		secureRegistry.excludePathPatterns("/doc.html");
		secureRegistry.excludePathPatterns("/js/**");
		secureRegistry.excludePathPatterns("/webjars/**");
		secureRegistry.excludePathPatterns("/swagger-resources/**");
		secureRegistry.excludePathPatterns("/druid/**");
        secureRegistry.excludePathPatterns("/blade-ap/ap/print");
        secureRegistry.excludePathPatterns("/finance/prepayorder/print");
        secureRegistry.excludePathPatterns("/finance/prepayorder/updateprint");
        secureRegistry.excludePathPatterns("/blade-ap/apBill/print");
        secureRegistry.excludePathPatterns("/blade-poitem/po_item/putcache");
        secureRegistry.excludePathPatterns("/blade-poitem/po_item/planreqputcache");
        secureRegistry.excludePathPatterns("/ncr/creatercvbatch");
        secureRegistry.excludePathPatterns("/blade-pr/u9_pr/handleExceptData");
        secureRegistry.excludePathPatterns("/blade-pr/u9_pr_ex/reHandleExceptData");
        secureRegistry.excludePathPatterns("/blade-po/po/downLoadPo");
        secureRegistry.excludePathPatterns("/blade-pricelib/pricelib/importexcelfromESB");
        secureRegistry.excludePathPatterns("/blade-poreceive/poReceive/createFromESB");
		return secureRegistry;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/cors/**")
			.allowedOrigins("*")
			.allowedHeaders("*")
			.allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
			.maxAge(3600)
			.allowCredentials(true);
	}

}
