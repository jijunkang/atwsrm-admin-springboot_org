package org.springblade.modules.bizinquiry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springblade.common.config.AtwSrmConfiguration;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.common.utils.WillHttpUtil;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.bizinquiry.dto.BizInquiryExcelDTO;
import org.springblade.modules.bizinquiry.dto.BizInquiryIoToEsbDTO;
import org.springblade.modules.bizinquiry.dto.BizInquiryReq;
import org.springblade.modules.bizinquiry.entity.BizInquiryEntity;
import org.springblade.modules.bizinquiry.entity.BizInquiryIoEntity;
import org.springblade.modules.bizinquiry.entity.BizInquiryIoFileListEntity;
import org.springblade.modules.bizinquiry.mapper.BizInquiryIoFileNameMapper;
import org.springblade.modules.bizinquiry.mapper.BizInquiryMapper;
import org.springblade.modules.bizinquiry.service.IBizInquiryIoService;
import org.springblade.modules.bizinquiry.service.IBizInquiryService;
import org.springblade.modules.bizinquiry.vo.BizInquiryVO;
import org.springblade.modules.queue.entity.QueueEmailEntity;
import org.springblade.modules.queue.service.IQueueEmailService;
import org.springblade.modules.system.service.IDictBizService;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springblade.core.secure.utils.AuthUtil.getUser;

/**
 * ???????????????
 * @author Will
 */
@Service
public
class BizInquiryServiceImpl extends BaseServiceImpl<BizInquiryMapper, BizInquiryEntity> implements IBizInquiryService{

    @Lazy
    @Autowired
    IBizInquiryIoService bizInquiryIoService;

    @Autowired
    AtwSrmConfiguration atwSrmConfiguration;

    @Autowired
    IDictBizService dictBizService;

    @Autowired
    private IQueueEmailService queueEmailService;

    @Autowired
    private IParamService paramService;

    @Autowired
    private BizInquiryIoFileNameMapper bizInquiryIoFileNameMapper;


    @Override
    public
    IPage<BizInquiryVO> list(IPage<BizInquiryEntity> page, BizInquiryReq bizInquiryReq){
        return this.baseMapper.list(page, bizInquiryReq);
    }

    @Override
    public
    boolean push(BizInquiryReq bizInquiryReq){
        BizInquiryEntity bizInquiryEntity = getById(bizInquiryReq.getQoId());
        bizInquiryEntity.setWinioId(bizInquiryReq.getWinioId());
        bizInquiryEntity.setStatus(bizInquiryReq.getStatus());
        return updateById(bizInquiryEntity);
    }

    @Override
    public
    List<Map<String, Object>> countList(){
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new ArrayMap<String, Object>(){{
            put("statuss", STATUS_WAIT + "," + STATUS_SUB + "," + STATUS_BACK);
            put("title", "?????????");
            put("count", countByStatus(STATUS_WAIT));
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("statuss", STATUS_AUDIT + "");
            put("title", "?????????");
            put("count", countByStatus(STATUS_AUDIT));
        }});
        result.add(new ArrayMap<String, Object>(){{
            put("statuss", STATUS_SUCCESS + "");
            put("title", "?????????");
            put("count", countByStatus(STATUS_SUCCESS));
        }});
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public
    boolean audits(BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException{
        boolean                    result               = false;
        List<BizInquiryIoEntity>   bizInquiryIoEntities = bizInquiryReq.getBizInquiryIoEntities();
        List<BizInquiryIoToEsbDTO> pushToEsbIos         = Lists.newArrayList();
        for(BizInquiryIoEntity temp : bizInquiryIoEntities){
            BizInquiryEntity bizInquiryEntity = getById(temp.getId());
            //??????????????????
            if(STATUS_SUB.equals(temp.getStatus())){
                if(STATUS_WAIT.equals(bizInquiryEntity.getStatus())){
                    //?????????
                    BizInquiryIoEntity bizInquiryIoEntity = new BizInquiryIoEntity();
                    bizInquiryIoEntity.setQoId(bizInquiryEntity.getId());
                    bizInquiryIoEntity.setQoCode(bizInquiryEntity.getQoCode());
                    updateEntity(temp, bizInquiryIoEntity);
                    result = bizInquiryIoService.save(bizInquiryIoEntity);
                }else if(STATUS_SUB.equals(bizInquiryEntity.getStatus())){
                    //?????????
                    BizInquiryIoEntity bizInquiryIoEntity = bizInquiryIoService.getByQoId(bizInquiryEntity.getId());
                    checkIoEntity(temp, bizInquiryEntity, bizInquiryIoEntity);
                }else if(STATUS_BACK.equals(bizInquiryEntity.getStatus())){
                    //?????????
                    BizInquiryIoEntity bizInquiryIoEntity = bizInquiryIoService.getByQoId(bizInquiryEntity.getId());
                    updateEntity(temp, bizInquiryIoEntity);
                    bizInquiryIoService.updateById(bizInquiryIoEntity);
                }
                result = updateStatus(temp, bizInquiryEntity);
            }else if(STATUS_AUDIT.equals(temp.getStatus())){
                //????????????
                if(STATUS_WAIT.equals(bizInquiryEntity.getStatus())){
                    //?????????????????????
                    BizInquiryIoEntity bizInquiryIoEntity = new BizInquiryIoEntity();
                    bizInquiryIoEntity.setQoId(bizInquiryEntity.getId());
                    bizInquiryIoEntity.setQoCode(bizInquiryEntity.getQoCode());
                    updateEntity(temp, bizInquiryIoEntity);
                    bizInquiryIoService.save(bizInquiryIoEntity);
                }else{
                    BizInquiryIoEntity bizInquiryIoEntity = bizInquiryIoService.getByQoId(bizInquiryEntity.getId());
                    checkIoEntity(temp, bizInquiryEntity, bizInquiryIoEntity);
                }
                result = updateStatus(temp, bizInquiryEntity);
            }else if(STATUS_BACK.equals(temp.getStatus())){
                //????????????
                updateStatus(temp, bizInquiryEntity);
                BizInquiryIoEntity bizInquiryIoEntity = bizInquiryIoService.getByQoId(bizInquiryEntity.getId());
                bizInquiryIoEntity.setBackReason(temp.getBackReason());
                result = bizInquiryIoService.updateById(bizInquiryIoEntity);
            }else{
                //????????????
                BizInquiryEntity   entity   = getById(temp.getId());
                BizInquiryIoEntity ioEntity = bizInquiryIoService.getByQoId(entity.getId());
                //????????????
                ioEntity.setConfirmDate(WillDateUtil.getTodayStart().getTime()/1000);

                bizInquiryIoService.updateById(ioEntity);
                result = updateStatus(temp, entity);
                BizInquiryIoToEsbDTO toEsbDTO = new BizInquiryIoToEsbDTO();
                toEsbDTO.setFeedbackName(getUser().getUserName());
                toEsbDTO.setProjectnumber(entity.getQoCode());
                toEsbDTO.setModelnumber(entity.getModel());
                toEsbDTO.setUnitprice(ioEntity.getPrice() == null ? "0" : String.valueOf(ioEntity.getPrice()));
                toEsbDTO.setDeliverydate(ioEntity.getDeliveryDate());
                toEsbDTO.setOffervalidity(ioEntity.getOfferValidity());
                toEsbDTO.setAttribution(ioEntity.getAttribution() == null ? "0" : String.valueOf(ioEntity.getAttribution()));
                // zlw add 20210425 NO.17
                //toEsbDTO.setSupFeedback(ioEntity.getSupFeedback());
                toEsbDTO.setSupFeedback(temp.getSupFeedback());
                if(!temp.getSupFeedback().equals(ioEntity.getSupFeedback())){
                    temp.setQoId(ioEntity.getQoId());
                    this.baseMapper.updateSupFeedbackById(temp);
                }
                // ????????????????????? SQS?????????
                pushToEsbIos.add(toEsbDTO);
                pushToEsb(pushToEsbIos);

                //???????????? 1.????????? 2.?????????
                if(temp.getAttachment().equals("1")){
                    String emailContent = "<br>\n ???????????????" + entity.getQoCode();
                    emailContent += "<br>\n ?????????" + entity.getModel();
                    emailContent += "<br>\n ??????????????????" + entity.getCdAttachment();

                    List<BizInquiryIoFileListEntity> filelists = bizInquiryIoService.getFileList(temp.getId());
                    emailContent += "<br>\n ????????????????????????";
                    for (BizInquiryIoFileListEntity fileToSend:filelists) {
                        emailContent +=  "<br>\n" + fileToSend.getFileName()  + "???" + fileToSend.getFileUrl();
                    }
                    QueueEmailEntity queueEmailEntity = new QueueEmailEntity();
                    queueEmailEntity.setSender(IQueueEmailService.BIZ_INTI_SENDER);
                    queueEmailEntity.setReceiver(entity.getApplyEmail());
                    queueEmailEntity.setSubject(entity.getQoCode() + "????????????");
                    queueEmailEntity.setContent(emailContent);
                    queueEmailEntity.setSendCount(0);
                    queueEmailEntity.setStatus(IQueueEmailService.STATUS_INIT);
                    queueEmailService.save(queueEmailEntity);
                }
            }
        }
        return result;
    }

    /**
     * checkIoEntity
     *
     * @param temp BizInquiryIoEntity
     * @param bizInquiryEntity BizInquiryEntity
     * @param bizInquiryIoEntity BizInquiryIoEntity
     */
    private void checkIoEntity(BizInquiryIoEntity temp, BizInquiryEntity bizInquiryEntity, BizInquiryIoEntity bizInquiryIoEntity) {
        if (StringUtil.isEmpty(bizInquiryIoEntity)) {
            //?????????????????????????????????
            bizInquiryIoEntity = new BizInquiryIoEntity();
            bizInquiryIoEntity.setQoId(bizInquiryEntity.getId());
            bizInquiryIoEntity.setQoCode(bizInquiryEntity.getQoCode());
            updateEntity(temp, bizInquiryIoEntity);
            bizInquiryIoService.save(bizInquiryIoEntity);
        } else {
            updateEntity(temp, bizInquiryIoEntity);
            bizInquiryIoService.updateById(bizInquiryIoEntity);
        }
    }

    private
    void pushToEsb(List<BizInquiryIoToEsbDTO> pushToEsbIos) throws RuntimeException, JsonProcessingException, JSONException{
        if(pushToEsbIos == null || pushToEsbIos.isEmpty()){
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        final String apiUrl = atwSrmConfiguration.getSqsApiDomain() + "/sqs/business/inquiry/batchUpdate";
        String       res    = WillHttpUtil.postJson(apiUrl, mapper.writeValueAsString(pushToEsbIos));
        if(StringUtil.isBlank(res)){
            throw new RuntimeException("??????????????????????????????ESB????????????????????????????????????????????????" + apiUrl);
        }
        JSONObject jsonObject = new JSONObject(res);
        if(!"2000".equals(String.valueOf(jsonObject.get("code")))){
            throw new RuntimeException("????????????:" + jsonObject.get("msg"));
        }
    }

    @Override
    public
    void export(HttpServletResponse response, BizInquiryReq bizInquiryReq){
        List<BizInquiryVO> bizInquiryVos = this.baseMapper.getList(bizInquiryReq);
        if(StringUtil.isEmpty(bizInquiryVos)){
            throw new RuntimeException("????????????");
        }
        List<BizInquiryExcelDTO> bizInquiryExcelDTOS = Lists.newArrayList();
        for(BizInquiryVO bizInquiryVo : bizInquiryVos){
            BizInquiryExcelDTO bizInquiryExcelDTO = BeanUtil.copy(bizInquiryVo, BizInquiryExcelDTO.class);
            if(!StringUtil.isEmpty(bizInquiryVo.getAttribution())){
                String attribution = dictBizService.getValue(DIC_VAL, bizInquiryVo.getAttribution().toString());
                bizInquiryExcelDTO.setAttribution(StringUtil.isNotBlank(attribution) ? attribution : null);
            }
            bizInquiryExcelDTOS.add(bizInquiryExcelDTO);
        }
        ExcelUtils.defaultExport(bizInquiryExcelDTOS, BizInquiryExcelDTO.class,
                "???????????????????????????" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public
    void exportWait(HttpServletResponse response, BizInquiryReq bizInquiryReq){
        List<BizInquiryExcelDTO> bizInquiryExcelDTOS = Lists.newArrayList();

        if(!StringUtil.isEmpty(bizInquiryReq.getIds())){
            for(String id : bizInquiryReq.getIdList()){
                BizInquiryVO       bizInquiryVo       = this.baseMapper.getWaitById(Long.valueOf(id));
                BizInquiryExcelDTO bizInquiryExcelDTO = BeanUtil.copy(bizInquiryVo, BizInquiryExcelDTO.class);
                if(!StringUtil.isEmpty(bizInquiryVo.getAttribution())){
                    String attribution = dictBizService.getValue(DIC_VAL, bizInquiryVo.getAttribution().toString());
                    bizInquiryExcelDTO.setAttribution(StringUtil.isNotBlank(attribution) ? attribution : null);
                }
                bizInquiryExcelDTOS.add(bizInquiryExcelDTO);
            }
        }else{
            List<BizInquiryVO> voList = this.baseMapper.getWaitList(bizInquiryReq);
            if(StringUtil.isEmpty(voList)){
                throw new RuntimeException("????????????");
            }
            for(BizInquiryVO bizInquiryVo : voList){
                BizInquiryExcelDTO bizInquiryExcelDTO = BeanUtil.copy(bizInquiryVo, BizInquiryExcelDTO.class);
                if(!StringUtil.isEmpty(bizInquiryVo.getAttribution())){
                    String attribution = dictBizService.getValue(DIC_VAL, bizInquiryVo.getAttribution().toString());
                    bizInquiryExcelDTO.setAttribution(StringUtil.isNotBlank(attribution) ? attribution : null);
                }
                bizInquiryExcelDTOS.add(bizInquiryExcelDTO);
            }
        }
        ExcelUtils.defaultExport(bizInquiryExcelDTOS, BizInquiryExcelDTO.class,
                "???????????????????????????" + DateUtil.formatDate(new Date()), response);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public
    boolean importExcel(MultipartFile file) throws ParseException{
        List<BizInquiryExcelDTO> dtoList = ExcelUtils.importExcel(file, 0, 1, BizInquiryExcelDTO.class);
        if(StringUtil.isEmpty(dtoList)){
            throw new RuntimeException("????????????");
        }
        for(BizInquiryExcelDTO dto : dtoList){
            BizInquiryEntity bizInquiryEntity = getById(dto.getId());
            if(bizInquiryEntity == null){
                throw new RuntimeException("???????????????ID???" + dto.getId());
            }
            BizInquiryIoEntity bizInquiryIoEntity = bizInquiryIoService.getByQoId(bizInquiryEntity.getId());
            if(bizInquiryIoEntity == null){
                BizInquiryIoEntity ioEntity = new BizInquiryIoEntity();
                setEntity(dto, bizInquiryEntity, ioEntity);
                bizInquiryIoService.save(ioEntity);
            }else{
                setEntity(dto, bizInquiryEntity, bizInquiryIoEntity);
                bizInquiryIoService.updateById(bizInquiryIoEntity);
            }
            //?????????????????????
            bizInquiryEntity.setStatus(STATUS_SUB);
            updateById(bizInquiryEntity);
        }
        return true;
    }

    /**
     * ????????????
     * @param dto              BizInquiryExcelDTO
     * @param bizInquiryEntity BizInquiryEntity
     * @param ioEntity         BizInquiryIoEntity
     */
    private
    void setEntity(BizInquiryExcelDTO dto, BizInquiryEntity bizInquiryEntity, BizInquiryIoEntity ioEntity){
        ioEntity.setQoId(bizInquiryEntity.getId());
        ioEntity.setQoCode(bizInquiryEntity.getQoCode());
        ioEntity.setSupName(dto.getSupName());
        ioEntity.setPrice(dto.getPrice());
        ioEntity.setDeliveryDate(dto.getDeliveryDate());
        ioEntity.setOfferValidity(dto.getOfferValidity());
        ioEntity.setBackReason(StringUtil.isNotBlank(dto.getBackReason()) ? dto.getBackReason() : null);
        ioEntity.setSupFeedback(StringUtil.isNotBlank(dto.getSupFeedback()) ? dto.getSupFeedback() : null);
        if(StringUtil.isNotBlank(dto.getAttribution())){
            dictBizService.getList(DIC_VAL).forEach(temp->{
                if(dto.getAttribution().equals(temp.getDictValue())){
                    ioEntity.setAttribution(Integer.parseInt(temp.getDictKey()));
                }
            });
        }
    }

    @Override
    public
    int getCount(){
        //???????????????ID
        String pRoleId = paramService.getValue("purch_user.role_id");
        //????????????ID
        String mRoleId = paramService.getValue("purch_manager.role_id");

        BizInquiryReq bizInquiryReq = new BizInquiryReq();
        if(StringUtil.containsAny(getUser().getRoleId(), pRoleId)){
            //?????????
            bizInquiryReq.setStatuss(STATUS_WAIT + "," + STATUS_SUB + "," + STATUS_BACK);
        }else if(StringUtil.containsAny(getUser().getRoleId(), mRoleId)){
            //????????????
            bizInquiryReq.setStatuss(STATUS_WAIT + "," + STATUS_SUB + "," + STATUS_BACK + "," + STATUS_AUDIT);
        }else{
            return 0;
        }
        return this.baseMapper.getList(bizInquiryReq).size();
    }


    /**
     * ????????????
     * @param temp               BizInquiryIoEntity
     * @param bizInquiryIoEntity BizInquiryIoEntity
     */
    private
    void updateEntity(BizInquiryIoEntity temp, BizInquiryIoEntity bizInquiryIoEntity){
        bizInquiryIoEntity.setSupName(temp.getSupName());
        bizInquiryIoEntity.setPrice(temp.getPrice());
        bizInquiryIoEntity.setConfirmDate(temp.getConfirmDate());
        bizInquiryIoEntity.setAttachment(temp.getAttachment());
        bizInquiryIoEntity.setDeliveryDate(temp.getDeliveryDate());
        bizInquiryIoEntity.setOfferValidity(temp.getOfferValidity());
        bizInquiryIoEntity.setAttribution(temp.getAttribution());
        bizInquiryIoEntity.setSupFeedback(temp.getSupFeedback());
    }

    /**
     * ????????????
     * @param temp             BizInquiryIoEntity
     * @param bizInquiryEntity bizInquiryEntity
     * @return boolean
     */
    private
    boolean updateStatus(BizInquiryIoEntity temp, BizInquiryEntity bizInquiryEntity){
        bizInquiryEntity.setStatus(temp.getStatus());
        return updateById(bizInquiryEntity);
    }


    /**
     * ????????????????????????
     * @param status String
     * @return int
     */
    private
    int countByStatus(Integer status){
        if(STATUS_WAIT.equals(status)){
            QueryWrapper<BizInquiryEntity> queryWrapper = Wrappers.<BizInquiryEntity>query()
                    .in("status", status, STATUS_SUB, STATUS_BACK);
            return count(queryWrapper);
        }
        QueryWrapper<BizInquiryEntity> queryWrapper = Wrappers.<BizInquiryEntity>query().in("status", status);
        return count(queryWrapper);
    }

    /**
     * ?????????????????????
     * @param bizInquiryReq
     * @return
     * @throws JsonProcessingException
     * @throws JSONException
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteList(BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException {
        if(this.baseMapper.deleteList(bizInquiryReq) > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ??????????????????
     * @param bizInquiryReq
     * @return
     * @throws JsonProcessingException
     * @throws JSONException
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveRemarks(BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException {
        boolean                    result               = false;
        List<BizInquiryIoEntity>   bizInquiryIoEntities = bizInquiryReq.getBizInquiryIoEntities();
        for(BizInquiryIoEntity temp : bizInquiryIoEntities){
            BizInquiryEntity   entity   = getById(temp.getId());
            BizInquiryIoEntity ioEntity = bizInquiryIoService.getByQoId(entity.getId());
            result = updateStatus(temp, entity);
            if(!temp.getSupFeedback().equals(ioEntity.getSupFeedback())){
                temp.setQoId(ioEntity.getQoId());
                this.baseMapper.updateSupFeedbackById(temp);
            }
        }
        return result;
    }

    /**
     * ??????????????????
     * @param bizInquiryReq
     * @return
     * @throws JsonProcessingException
     * @throws JSONException
     */
    @Override
    public boolean sendEmail(BizInquiryReq bizInquiryReq) throws JsonProcessingException, JSONException {
        boolean result = false;
        List<BizInquiryIoEntity> bizInquiryIoEntities = bizInquiryReq.getBizInquiryIoEntities();
        for (BizInquiryIoEntity temp : bizInquiryIoEntities) {
            BizInquiryEntity entity = getById(temp.getId());
            BizInquiryIoEntity ioEntity = bizInquiryIoService.getByQoId(entity.getId());
            result = updateStatus(temp, entity);
            //????????????
            String emailContent = "<br>\n ???????????????" + entity.getQoCode();
            emailContent += "<br>\n ?????????" + entity.getModel();
            emailContent += "<br>\n ??????????????????" + entity.getCdAttachment();
            emailContent += "<br>\n ????????????????????????" + ioEntity.getAttachment();
            QueueEmailEntity queueEmailEntity = new QueueEmailEntity();
            queueEmailEntity.setSender("zhouliangwei@antiwearvalve.com");
            queueEmailEntity.setReceiver("zhouliangwei@antiwearvalve.com");
            queueEmailEntity.setSubject(entity.getQoCode() + "????????????");
            queueEmailEntity.setContent(emailContent);
            queueEmailEntity.setSendCount(0);
            queueEmailEntity.setStatus(IQueueEmailService.STATUS_INIT);
            queueEmailService.save(queueEmailEntity);
        }
        return result;
    }


    /**
     * ?????????????????????????????????????????????
     * @param bizInquiryReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean listToEndButNotSend(BizInquiryReq bizInquiryReq) {
        List<BizInquiryEntity> bizInquiryEntities = bizInquiryReq.getBizInquiryEntities();
        for(BizInquiryEntity bizInquiryEntity : bizInquiryEntities) {
                this.baseMapper.updateStatusToEndById(bizInquiryEntity.getId());
        }
        return true;
    }
}
