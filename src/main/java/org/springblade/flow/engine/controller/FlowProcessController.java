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
package org.springblade.flow.engine.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.flow.core.entity.BladeFlow;
import org.springblade.flow.engine.service.FlowEngineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ?????????????????????
 *
 * @author Will
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(AppConstant.APPLICATION_FLOW_NAME + "/process")
@ApiIgnore
public class FlowProcessController {

	private static final String IMAGE_NAME = "image";
	private static final String XML_NAME = "xml";
	private static final Integer INT_1024 = 1024;

	private RepositoryService repositoryService;
	private RuntimeService runtimeService;
	private HistoryService historyService;
	private ProcessEngine processEngine;
	private FlowEngineService flowEngineService;

	/**
	 * ????????????????????????
	 *
	 * @param processInstanceId ????????????id
	 * @param startActivityId   ????????????id
	 * @param endActivityId     ????????????id
	 */
	@GetMapping(value = "history-flow-list")
	public R<List<BladeFlow>> historyFlowList(@RequestParam String processInstanceId, String startActivityId, String endActivityId) {
		return R.data(flowEngineService.historyFlowList(processInstanceId, startActivityId, endActivityId));
	}

	/**
	 * ???????????????
	 *
	 * @param processDefinitionId ??????id
	 * @param processInstanceId   ??????id
	 * @param resourceType        ????????????
	 * @param response            ??????
	 */
	@GetMapping("resource-view")
	public void resourceView(@RequestParam String processDefinitionId, String processInstanceId, @RequestParam(defaultValue = IMAGE_NAME) String resourceType, HttpServletResponse response) throws Exception {
		if (StringUtil.isAllBlank(processDefinitionId, processInstanceId)) {
			return;
		}
		if (StringUtil.isBlank(processDefinitionId)) {
			ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
			processDefinitionId = processInstance.getProcessDefinitionId();
		}
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
		String resourceName = "";
		if (resourceType.equals(IMAGE_NAME)) {
			resourceName = processDefinition.getDiagramResourceName();
		} else if (resourceType.equals(XML_NAME)) {
			resourceName = processDefinition.getResourceName();
		}
		InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
		byte[] b = new byte[1024];
		int len;
		while ((len = resourceAsStream.read(b, 0, INT_1024)) != -1) {
			response.getOutputStream().write(b, 0, len);
		}
	}

	/**
	 * ???????????????????????????
	 *
	 * @param processInstanceId   ????????????id
	 * @param httpServletResponse http??????
	 */
	@GetMapping(value = "diagram-view")
	public void diagramView(String processInstanceId, HttpServletResponse httpServletResponse) {
		diagram(processInstanceId, httpServletResponse);
	}

	/**
	 * ????????????????????????
	 *
	 * @param processInstanceId   ????????????id
	 * @param httpServletResponse http??????
	 */
	private void diagram(String processInstanceId, HttpServletResponse httpServletResponse) {
		// ???????????????????????????
		String processDefinitionId;
		// ????????????????????????????????????????????????
		if (this.isFinished(processInstanceId)) {
			HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
			processDefinitionId = pi.getProcessDefinitionId();
		} else {
			// ???????????????????????????????????????????????????
			// ??????????????????ID?????????????????????????????????ActivityId??????
			ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
			processDefinitionId = pi.getProcessDefinitionId();
		}
		List<String> highLightedActivities = new ArrayList<>();

		// ?????????????????????
		List<HistoricActivityInstance> highLightedActivityList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();

		for (HistoricActivityInstance tempActivity : highLightedActivityList) {
			String activityId = tempActivity.getActivityId();
			highLightedActivities.add(activityId);
		}

		List<String> flows = new ArrayList<>();
		// ???????????????
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		ProcessEngineConfiguration engConf = processEngine.getProcessEngineConfiguration();

		ProcessDiagramGenerator diagramGenerator = engConf.getProcessDiagramGenerator();
		InputStream in = diagramGenerator.generateDiagram(bpmnModel, "bmp", highLightedActivities, flows, engConf.getActivityFontName(),
			engConf.getLabelFontName(), engConf.getAnnotationFontName(), engConf.getClassLoader(), 1.0, true);
		OutputStream out = null;
		byte[] buf = new byte[1024];
		int length;
		try {
			out = httpServletResponse.getOutputStream();
			while ((length = in.read(buf)) != -1) {
				out.write(buf, 0, length);
			}
		} catch (IOException e) {
			log.error("????????????", e);
		} finally {
			IoUtil.closeSilently(out);
			IoUtil.closeSilently(in);
		}
	}

	/**
	 * ???????????????
	 *
	 * @param processInstanceId ????????????id
	 * @return bool
	 */
	private boolean isFinished(String processInstanceId) {
		return historyService.createHistoricProcessInstanceQuery().finished()
			.processInstanceId(processInstanceId).count() > 0;
	}


}
