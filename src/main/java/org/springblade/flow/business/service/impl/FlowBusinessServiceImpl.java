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
package org.springblade.flow.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.flow.business.service.FlowBusinessService;
import org.springblade.flow.core.constant.ProcessConstant;
import org.springblade.flow.core.entity.BladeFlow;
import org.springblade.flow.core.utils.TaskUtil;
import org.springblade.flow.engine.constant.FlowEngineConstant;
import org.springblade.flow.engine.utils.FlowCache;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ?????????????????????
 *
 * @author Will
 */
@Service
@AllArgsConstructor
public class FlowBusinessServiceImpl implements FlowBusinessService {

	private TaskService taskService;
	private HistoryService historyService;

	@Override
	public IPage<BladeFlow> selectClaimPage(IPage<BladeFlow> page, BladeFlow bladeFlow) {
		String taskUser = TaskUtil.getTaskUser();
		String taskGroup = TaskUtil.getCandidateGroup();
		List<BladeFlow> flowList = new LinkedList<>();

		// ?????????????????????
		TaskQuery claimUserQuery = taskService.createTaskQuery().taskCandidateUser(taskUser)
			.includeProcessVariables().active().orderByTaskCreateTime().desc();
		// ?????????????????????
		TaskQuery claimRoleQuery = taskService.createTaskQuery().taskCandidateGroup(taskGroup)
			.includeProcessVariables().active().orderByTaskCreateTime().desc();

		// ??????????????????
		buildFlowTaskList(bladeFlow, flowList, claimUserQuery, FlowEngineConstant.STATUS_CLAIM);
		buildFlowTaskList(bladeFlow, flowList, claimRoleQuery, FlowEngineConstant.STATUS_CLAIM);

		// ????????????
		long count = claimUserQuery.count() + claimRoleQuery.count();
		// ????????????
		page.setSize(count);
		// ????????????
		page.setTotal(count);
		// ????????????
		page.setRecords(flowList);
		return page;
	}

	@Override
	public IPage<BladeFlow> selectTodoPage(IPage<BladeFlow> page, BladeFlow bladeFlow) {
		String taskUser = TaskUtil.getTaskUser();
		List<BladeFlow> flowList = new LinkedList<>();

		// ??????????????????
		TaskQuery todoQuery = taskService.createTaskQuery().taskAssignee(taskUser).active()
			.includeProcessVariables().orderByTaskCreateTime().desc();

		// ??????????????????
		buildFlowTaskList(bladeFlow, flowList, todoQuery, FlowEngineConstant.STATUS_TODO);

		// ????????????
		long count = todoQuery.count();
		// ????????????
		page.setSize(count);
		// ????????????
		page.setTotal(count);
		// ????????????
		page.setRecords(flowList);
		return page;
	}

	@Override
	public IPage<BladeFlow> selectSendPage(IPage<BladeFlow> page, BladeFlow bladeFlow) {
		String taskUser = TaskUtil.getTaskUser();
		List<BladeFlow> flowList = new LinkedList<>();

		HistoricProcessInstanceQuery historyQuery = historyService.createHistoricProcessInstanceQuery().startedBy(taskUser).orderByProcessInstanceStartTime().desc();

		if (bladeFlow.getCategory() != null) {
			historyQuery.processDefinitionCategory(bladeFlow.getCategory());
		}
		if (bladeFlow.getBeginDate() != null) {
			historyQuery.startedAfter(bladeFlow.getBeginDate());
		}
		if (bladeFlow.getEndDate() != null) {
			historyQuery.startedBefore(bladeFlow.getEndDate());
		}

		// ????????????
		List<HistoricProcessInstance> historyList = historyQuery.listPage(Func.toInt((page.getCurrent() - 1) * page.getSize()), Func.toInt(page.getSize()));

		historyList.forEach(historicProcessInstance -> {
			BladeFlow flow = new BladeFlow();
			// historicProcessInstance
			flow.setCreateTime(historicProcessInstance.getStartTime());
			flow.setEndTime(historicProcessInstance.getEndTime());
			flow.setVariables(historicProcessInstance.getProcessVariables());
			String[] businessKey = Func.toStrArray(StringPool.COLON, historicProcessInstance.getBusinessKey());
			if (businessKey.length > 1) {
				flow.setBusinessTable(businessKey[0]);
				flow.setBusinessId(businessKey[1]);
			}
			flow.setHistoryActivityName(historicProcessInstance.getName());
			flow.setProcessInstanceId(historicProcessInstance.getId());
			flow.setHistoryProcessInstanceId(historicProcessInstance.getId());
			// ProcessDefinition
			ProcessDefinition processDefinition = FlowCache.getProcessDefinition(historicProcessInstance.getProcessDefinitionId());
			flow.setProcessDefinitionId(processDefinition.getId());
			flow.setProcessDefinitionName(processDefinition.getName());
			flow.setProcessDefinitionVersion(processDefinition.getVersion());
			flow.setProcessDefinitionKey(processDefinition.getKey());
			flow.setCategory(processDefinition.getCategory());
			flow.setCategoryName(FlowCache.getCategoryName(processDefinition.getCategory()));
			flow.setProcessInstanceId(historicProcessInstance.getId());
			// HistoricTaskInstance
			List<HistoricTaskInstance> historyTasks = historyService.createHistoricTaskInstanceQuery().processInstanceId(historicProcessInstance.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
			if (Func.isNotEmpty(historyTasks)) {
				HistoricTaskInstance historyTask = historyTasks.iterator().next();
				flow.setTaskId(historyTask.getId());
				flow.setTaskName(historyTask.getName());
				flow.setTaskDefinitionKey(historyTask.getTaskDefinitionKey());
			}
			// Status
			if (historicProcessInstance.getEndActivityId() != null) {
				flow.setProcessIsFinished(FlowEngineConstant.STATUS_FINISHED);
			} else {
				flow.setProcessIsFinished(FlowEngineConstant.STATUS_UNFINISHED);
			}
			flow.setStatus(FlowEngineConstant.STATUS_FINISH);
			flowList.add(flow);
		});

		// ????????????
		long count = historyQuery.count();
		// ????????????
		page.setTotal(count);
		page.setRecords(flowList);
		return page;
	}

	@Override
	public IPage<BladeFlow> selectDonePage(IPage<BladeFlow> page, BladeFlow bladeFlow) {
		String taskUser = TaskUtil.getTaskUser();
		List<BladeFlow> flowList = new LinkedList<>();

		HistoricTaskInstanceQuery doneQuery = historyService.createHistoricTaskInstanceQuery().taskAssignee(taskUser).finished()
			.includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();

		if (bladeFlow.getCategory() != null) {
			doneQuery.processCategoryIn(Func.toStrList(bladeFlow.getCategory()));
		}
		if (bladeFlow.getBeginDate() != null) {
			doneQuery.taskCompletedAfter(bladeFlow.getBeginDate());
		}
		if (bladeFlow.getEndDate() != null) {
			doneQuery.taskCompletedBefore(bladeFlow.getEndDate());
		}

		// ????????????
		List<HistoricTaskInstance> doneList = doneQuery.listPage(Func.toInt((page.getCurrent() - 1) * page.getSize()), Func.toInt(page.getSize()));
		doneList.forEach(historicTaskInstance -> {
			BladeFlow flow = new BladeFlow();
			flow.setTaskId(historicTaskInstance.getId());
			flow.setTaskDefinitionKey(historicTaskInstance.getTaskDefinitionKey());
			flow.setTaskName(historicTaskInstance.getName());
			flow.setAssignee(historicTaskInstance.getAssignee());
			flow.setCreateTime(historicTaskInstance.getCreateTime());
			flow.setExecutionId(historicTaskInstance.getExecutionId());
			flow.setHistoryTaskEndTime(historicTaskInstance.getEndTime());
			flow.setVariables(historicTaskInstance.getProcessVariables());

			ProcessDefinition processDefinition = FlowCache.getProcessDefinition(historicTaskInstance.getProcessDefinitionId());
			flow.setProcessDefinitionId(processDefinition.getId());
			flow.setProcessDefinitionName(processDefinition.getName());
			flow.setProcessDefinitionKey(processDefinition.getKey());
			flow.setProcessDefinitionVersion(processDefinition.getVersion());
			flow.setCategory(processDefinition.getCategory());
			flow.setCategoryName(FlowCache.getCategoryName(processDefinition.getCategory()));

			flow.setProcessInstanceId(historicTaskInstance.getProcessInstanceId());
			flow.setHistoryProcessInstanceId(historicTaskInstance.getProcessInstanceId());
			HistoricProcessInstance historicProcessInstance = getHistoricProcessInstance((historicTaskInstance.getProcessInstanceId()));
			if (Func.isNotEmpty(historicProcessInstance)) {
				String[] businessKey = Func.toStrArray(StringPool.COLON, historicProcessInstance.getBusinessKey());
				flow.setBusinessTable(businessKey[0]);
				flow.setBusinessId(businessKey[1]);
				if (historicProcessInstance.getEndActivityId() != null) {
					flow.setProcessIsFinished(FlowEngineConstant.STATUS_FINISHED);
				} else {
					flow.setProcessIsFinished(FlowEngineConstant.STATUS_UNFINISHED);
				}
			}
			flow.setStatus(FlowEngineConstant.STATUS_FINISH);
			flowList.add(flow);
		});
		// ????????????
		long count = doneQuery.count();
		// ????????????
		page.setTotal(count);
		page.setRecords(flowList);
		return page;
	}

	@Override
	public boolean completeTask(BladeFlow flow) {
		String taskId = flow.getTaskId();
		String processInstanceId = flow.getProcessInstanceId();
		String comment = Func.toStr(flow.getComment(), ProcessConstant.PASS_COMMENT);
		// ????????????
		if (StringUtil.isNoneBlank(processInstanceId, comment)) {
			taskService.addComment(taskId, processInstanceId, comment);
		}
		// ????????????
		Map<String, Object> variables = flow.getVariables();
		if (variables == null) {
			variables = Kv.create();
		}
		variables.put(ProcessConstant.PASS_KEY, flow.isPass());
		// ????????????
		taskService.complete(taskId, variables);
		return true;
	}

	/**
	 * ????????????
	 *
	 * @param bladeFlow ???????????????
	 * @param flowList  ????????????
	 * @param taskQuery ???????????????
	 * @param status    ??????
	 */
	private void buildFlowTaskList(BladeFlow bladeFlow, List<BladeFlow> flowList, TaskQuery taskQuery, String status) {
		if (bladeFlow.getCategory() != null) {
			taskQuery.processCategoryIn(Func.toStrList(bladeFlow.getCategory()));
		}
		if (bladeFlow.getBeginDate() != null) {
			taskQuery.taskCreatedAfter(bladeFlow.getBeginDate());
		}
		if (bladeFlow.getEndDate() != null) {
			taskQuery.taskCreatedBefore(bladeFlow.getEndDate());
		}
		taskQuery.list().forEach(task -> {
			BladeFlow flow = new BladeFlow();
			flow.setTaskId(task.getId());
			flow.setTaskDefinitionKey(task.getTaskDefinitionKey());
			flow.setTaskName(task.getName());
			flow.setAssignee(task.getAssignee());
			flow.setCreateTime(task.getCreateTime());
			flow.setClaimTime(task.getClaimTime());
			flow.setExecutionId(task.getExecutionId());
			flow.setVariables(task.getProcessVariables());

			HistoricProcessInstance historicProcessInstance = getHistoricProcessInstance(task.getProcessInstanceId());
			if (Func.isNotEmpty(historicProcessInstance)) {
				String[] businessKey = Func.toStrArray(StringPool.COLON, historicProcessInstance.getBusinessKey());
				flow.setBusinessTable(businessKey[0]);
				flow.setBusinessId(businessKey[1]);
			}

			ProcessDefinition processDefinition = FlowCache.getProcessDefinition(task.getProcessDefinitionId());
			flow.setCategory(processDefinition.getCategory());
			flow.setCategoryName(FlowCache.getCategoryName(processDefinition.getCategory()));
			flow.setProcessDefinitionId(processDefinition.getId());
			flow.setProcessDefinitionName(processDefinition.getName());
			flow.setProcessDefinitionKey(processDefinition.getKey());
			flow.setProcessDefinitionVersion(processDefinition.getVersion());
			flow.setProcessInstanceId(task.getProcessInstanceId());
			flow.setStatus(status);
			flowList.add(flow);
		});
	}

	/**
	 * ??????????????????
	 *
	 * @param processInstanceId ????????????id
	 * @return HistoricProcessInstance
	 */
	private HistoricProcessInstance getHistoricProcessInstance(String processInstanceId) {
		return historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
	}

}
