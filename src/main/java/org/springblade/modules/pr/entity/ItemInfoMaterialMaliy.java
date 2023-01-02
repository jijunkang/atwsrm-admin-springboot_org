package org.springblade.modules.pr.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springblade.core.mp.base.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: 昕月
 * Date：2022/5/12 15:48
 * Desc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "atw_maily_material")
@ApiModel(value = "管棒料信息对象" ,description = "")
public class ItemInfoMaterialMaliy extends BaseEntity implements Serializable {

    private  static  final  long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)//在自增主键的变量加上即可
    @ApiModelProperty(value = "序号id")
    private  Long  id;

    @ApiModelProperty(value = "物料描述")
    private  String  itemDesc;

    @ApiModelProperty(value = "供应商名称")
    private  String supplierName;

    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    @ApiModelProperty(value ="外径" )
    private int externalDiameter;

    @ApiModelProperty(value ="内径" )
    private int internalDiamete;

    @ApiModelProperty(value ="壁厚" )
    private int wallThickness;

    @ApiModelProperty(value = "创建人")
    private String createUsers;

    @ApiModelProperty(value = "修改人")
    private String updateUsers;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

}
