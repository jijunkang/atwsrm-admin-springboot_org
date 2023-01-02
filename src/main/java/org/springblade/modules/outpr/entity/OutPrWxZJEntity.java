package org.springblade.modules.outpr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;


/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_out_pr_wx_zj")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OutPrWxZJEntity对象", description = "")
public class OutPrWxZJEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

    /**
     * 物料分类
     */
    private String itemize;
    /**
     * 尺寸
     */
    private String size;
    /**
     * 形式
     */
    private String form;
    /**
     * 磅级
     */
    private String pound;
    /**
     * 法兰结构
     */
    private String flange;
    /**
     * 系列
     */
    private String series;
    /**
     * 材质
     */
    private String material;
    /**
     * 供应商名称
     */
    private String supName;
    /**
     * 供应商编码
     */
    private String supCode;
    /**
     * 铸造工艺
     */
    private String technology;
    /**
     * 优先级
     */
    private String priority;
    /**
     * 是否外协1外协0不外协
     */
    private String isOut;
    /**
     * 外协供应商名称
     */
    private String outSupName;
    /**
     * 外协供应商编码
     */
    private String outSupCode;
    /**
     * 精加工粗加工
     */
    private String remark;
}
