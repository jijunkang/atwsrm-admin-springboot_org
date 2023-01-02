package org.springblade.modules.material.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.api.client.util.Lists;
import lombok.extern.java.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.bizinquiry.service.IBizInquiryService;
import org.springblade.modules.material.dto.MaterialPriceExcelNewDTO;
import org.springblade.modules.material.dto.MaterialPriceNewDTO;
import org.springblade.modules.material.dto.MaterialPriceExcelDTO;
import org.springblade.modules.material.entity.MaterialPriceNewEntity;
import org.springblade.modules.material.entity.MaterialPriceNewEntity;
import org.springblade.modules.material.mapper.MaterialPriceMapper;
import org.springblade.modules.material.mapper.MaterialPriceNewMapper;
import org.springblade.modules.material.service.IMaterialPriceNewService;
import org.springblade.modules.material.service.IMaterialPriceService;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.queue.entity.QueueEmailEntity;
import org.springblade.modules.queue.service.IQueueEmailService;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 服务实现类
 * @author Will
 */
@Service
@Log
public
class MaterialPriceNewServiceImpl extends BaseServiceImpl<MaterialPriceNewMapper, MaterialPriceNewEntity> implements IMaterialPriceNewService {

    @Autowired
    ISupplierService supplierService;

    @Autowired
    private IQueueEmailService queueEmailService;

    @Value("${sendCloud.url}")
    private String url;

    @Value("${sendCloud.apiUser}")
    private String apiUser;

    @Value("${sendCloud.apiKey}")
    private String apiKey;

    @Value("${WWEMAILS.FROM}")
    private  String from;

    @Value("${WWEMAILS.PriceTO}")
    private  String PriceTO;

    @Value("${WWEMAILS.PriceCC}")
    private  String PriceCC;


    @Override
    public
    boolean save(MaterialPriceNewEntity entity){
        return super.save(entity);
    }

    @Override
    public
    QueryWrapper<MaterialPriceNewEntity> getQueryWrapper(MaterialPriceNewDTO materialPrice){
        return Wrappers.<MaterialPriceNewEntity>query().eq(materialPrice.getStatus() != null, "status", materialPrice.getStatus())
                                  .eq(StringUtil.isNotBlank(materialPrice.getMaterial()), "material", materialPrice.getMaterial())
                                  .like(StringUtil.isNotBlank(materialPrice.getMaterialDesc()), "material_desc", materialPrice.getMaterialDesc())
                                  .like(StringUtil.isNotBlank(materialPrice.getTechnic()), "technic", materialPrice.getTechnic());
    }


    @Override
    public
    boolean importExcel(MultipartFile file) throws RuntimeException{
        List<MaterialPriceExcelNewDTO> dtoList    = ExcelUtils.importExcel(file, 0, 1, MaterialPriceExcelNewDTO.class);
        List<MaterialPriceNewEntity>   entityList = BeanUtil.copy(dtoList, MaterialPriceNewEntity.class);
        boolean ismail=false;
        for(MaterialPriceNewEntity entity : entityList){
            List<MaterialPriceNewEntity> list=this.baseMapper.getlistnohistory(entity);
            if (list.size()>0){
                continue;
            }else{
                entity.setStatus(30);
                save(entity);
                ismail=true;
            }

        }
        if(ismail){

            sendEmail();
        }


        return true;
    }

    public boolean sendEmail() {


        try {
            // 处理文件
            HttpPost httpPost = new HttpPost(url);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            // 主题
            String subject = "原材料价格审批邮件推送";
            // 内容
            StringBuilder content = new StringBuilder(new StringBuilder("<html>" +
                "<head></head>" +
                "<body>" +
                "<p>您好，SRM有新的原材料价格需要审批~</p>"));
            content.append("</body></html>");

            JSONObject jsonObject=new JSONObject();
            jsonObject.put("apiUser", apiUser);
            jsonObject.put("apiKey", apiKey);
            jsonObject.put("to", "jijunkang@antiwearvalve.com");
            jsonObject.put("cc", "jijunkang@antiwearvalve.com");
            jsonObject.put("fromName", "SRM");
            jsonObject.put("subject", subject);
            jsonObject.put("text", content.toString());

            System.out.println(jsonObject.toJSONString());


            // 如果有附件，需要 用 MultipartEntityBuilder
            MultipartEntityBuilder entity = MultipartEntityBuilder.create();
            ContentType TEXT_PLAIN = ContentType.create("text/plain", Charset.forName("UTF-8"));
            entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.setCharset(Charset.forName("UTF-8"));
            entity.addTextBody("apiUser", apiUser, TEXT_PLAIN);
            entity.addTextBody("apiKey", apiKey, TEXT_PLAIN);
            entity.addTextBody("to", PriceTO, TEXT_PLAIN);
            entity.addTextBody("cc", PriceCC, TEXT_PLAIN);
            entity.addTextBody("from", from, TEXT_PLAIN);
            entity.addTextBody("fromName", "SRM", TEXT_PLAIN);
            entity.addTextBody("subject", subject, TEXT_PLAIN);
            entity.addTextBody("html", content.toString(), TEXT_PLAIN);

            // 发送邮件
            httpPost.setEntity(entity.build());
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println(EntityUtils.toString(response.getEntity()));
            } else {
                System.err.println("error");
            }
            httpPost.releaseConnection();

        } catch (Exception e) {
            log.info("发送邮件失败！");
            throw new RuntimeException("发送邮件失败！");
        }  finally {

        }
        return true;
    }


    @Override
    public
    void exportExcel(MaterialPriceNewDTO dto, HttpServletResponse response) throws RuntimeException{
        QueryWrapper<MaterialPriceNewEntity> qw         = getQueryWrapper(dto);
        List<MaterialPriceNewEntity>         entityList = list(qw);
        if(entityList == null){
            throw new RuntimeException("暂无数据");
        }
        List<MaterialPriceExcelNewDTO> excelList = Lists.newArrayList();
        for(MaterialPriceNewEntity entity : entityList){
            MaterialPriceExcelNewDTO excelDTO = BeanUtil.copy(entity, MaterialPriceExcelNewDTO.class);
            excelList.add(excelDTO);
        }

        ExcelUtils.defaultExport(excelList, MaterialPriceExcelNewDTO.class, "原材料价格表" + DateUtil.formatDate(new Date()), response);

    }

    @Override
    public boolean deleteById(List<Long> ids) {
        for (Long id:ids) {
            if (this.baseMapper.deleteById(id)){

            }else {
                return false;
            }
        }
        return true;

    }

    @Override
    public boolean passMateralPrice(List<MaterialPriceNewEntity> entityList) {
        //老单子改成历史40
        for (MaterialPriceNewEntity materialPriceNew:entityList) {
            this.baseMapper.passMaterialPrice(materialPriceNew.getMaterial(),materialPriceNew.getTechnic());
            //改成新状态
            this.updateById(materialPriceNew);
        }

        return true;

    }


}
