package org.springblade.modules.queue.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.queue.entity.QueueEmailEntity;

/**
 * 服务类
 *
 * @author Will
 */
public interface IQueueEmailService extends BaseService<QueueEmailEntity> {

    Integer STATUS_INIT = 10;
    Integer STATUS_SENT = 20;

    String AP_INTI_SENDER = "srm3@antiwearvalve.com";
    String AP_SUBJECT_DP = "来自安特威的应付单";

    String AP_SUBJECT_MSG= "来自安特威的消息";
    String AP_CONTENT_DP = "您有新的应付单，请不要错过，传送门：http://atwsrm.oms.antiwearvalve.com";
    String AP_PARAM_KEY = "ap_financer_email";


    String PRE_INTI_SENDER = "srm3@antiwearvalve.com";
    String PRE_SUBJECT_DP = "来自安特威的预付单";
    String PRE_CONTENT_DP = "您有新的预付单，请不要错过，传送门：http://atwsrm.oms.antiwearvalve.com";
    String PRE_PARAM_KEY = "pre_financer_email";

    String BIZ_INTI_SENDER = "srm3@antiwearvalve.com";
    String BIZ_SUBJECT_DP = "来自安特威的商务询价附件";
    String BIZ_PARAM_KEY = "biz_business_email";
}
