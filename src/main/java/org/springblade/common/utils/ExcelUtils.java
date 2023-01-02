package org.springblade.common.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: ExcelUtils
 * @description: Excel工具类
 * @author: yh
 * @create: 2019-12-05 11:55
 **/
public class ExcelUtils {
    public static void exportExcel(
        List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, boolean isCreateHeader, HttpServletResponse response) {
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    public static void exportExcel(
        List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, HttpServletResponse response) {
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
    }

    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        defaultExport(list, fileName, response);
    }

    private static void defaultExport(
        List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        if (workbook != null) {
            ;
        }
        downLoadExcel(fileName, response, workbook);
    }

    public static  void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), pojoClass, list);
        downLoadExcel(fileName+".xls", response, workbook);
    }
    /**
     * <p>
     * 通用Excel导出方法,利用反射机制遍历对象的所有字段，将数据写入Excel文件中 <br>
     * 此版本生成2007以上版本的文件 (文件后缀：xlsx)
     * </p>
     *
     * @param headers
     *            表格头部标题集合
     * @param dataset
     *            需要显示的数据集合,集合中一定要放置符合JavaBean风格的类的对象。此方法支持的
     *            JavaBean属性的数据类型有基本数据类型及String,Date
     * @param response
     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     */
    public static
    void exportExcel2007(Collection<?> dataset, String fileName, List<ExcelHeader> headers, HttpServletResponse response) {
        // 声明一个工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 生成一个表格
        XSSFSheet sheet = workbook.createSheet();
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(20);

        // 产生表格标题行
        XSSFRow row = sheet.createRow(0);
        XSSFCell cellHeader;
        for (int i = 0; i < headers.size(); i++) {
            ExcelHeader excelHeader = headers.get(i);
            cellHeader = row.createCell(i);
            cellHeader.setCellValue(new XSSFRichTextString(excelHeader.getTitle()));
        }

        // 遍历集合数据，产生数据行
        Iterator<?> it    = dataset.iterator();
        int         index = 0;
        Object t;
        Field[]  fields;
        Field    field;
        XSSFRichTextString richString;
        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
        Matcher matcher;
        String  fieldName;
        String getMethodName;
        XSSFCell cell;
        Class  tCls;
        Method getMethod;
        Object value;
        String           textValue;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            t =  it.next();
            // 利用反射，根据JavaBean属性的先后顺序，动态调用getXxx()方法得到属性值
//            fields = t.getClass().getDeclaredFields();
            for (int i = 0; i < headers.size(); i++) {
                cell = row.createCell(i);
                ExcelHeader excelHeader = headers.get(i);
                fieldName = excelHeader.getFieldName();
                getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                try {
                    field = t.getClass().getDeclaredField(fieldName);
                    //过滤选调生bean中的set对象
                    if(field.getType() != java.util.Set.class && !"getSerialVersionUID".equals(getMethodName)){
                        tCls = t.getClass();
                        getMethod = tCls.getMethod(getMethodName, new Class[] {});
                        value = getMethod.invoke(t, new Object[] {});
                        // 判断值的类型后进行强制类型转换
                        textValue = null;
                        if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else if (value instanceof Float) {
                            textValue = String.valueOf((Float) value);
                            cell.setCellValue(textValue);
                        } else if (value instanceof Double) {
                            textValue = String.valueOf((Double) value);
                            cell.setCellValue(textValue);
                        } else if (value instanceof Long) {
                            cell.setCellValue((Long) value);
                        }
                        if (value instanceof Boolean) {
                            textValue = "是";
                            if (!(Boolean) value) {
                                textValue = "否";
                            }
                        } else if (value instanceof Date) {
                            textValue = sdf.format((Date) value);
                        } else {
                            // 其它数据类型都当作字符串简单处理
                            if (value != null) {
                                textValue = value.toString();
                            }
                        }
                        if (textValue != null) {
                            matcher = p.matcher(textValue);
                            if (matcher.matches()) {
                                // 是数字当作double处理
                                cell.setCellValue(Double.parseDouble(textValue));
                            } else {
                                richString = new XSSFRichTextString(textValue);
                                cell.setCellValue(richString);
                            }
                        }

                    }

                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }catch(NoSuchFieldException e){
                    e.printStackTrace();
                }finally {
                    // 清理资源
                }
            }
        }
        downLoadExcel(fileName+".xlsx",response,workbook);
    }



    private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            //throw new NormalException(e.getMessage());
        }
    }

    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        if (workbook != null) {
            ;
        }
        downLoadExcel(fileName+".xls", response, workbook);
    }





    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(String.valueOf(filePath)), pojoClass, params);
        } catch (NoSuchElementException e) {
            //throw new NormalException("模板不能为空");
        } catch (Exception e) {
            e.printStackTrace();
            //throw new NormalException(e.getMessage());
        }
        return list;
    }

    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws RuntimeException {
        if (file == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("excel文件不能为空");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return list;
    }



}
