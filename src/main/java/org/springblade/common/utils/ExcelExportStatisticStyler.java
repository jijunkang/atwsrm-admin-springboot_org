package org.springblade.common.utils;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.export.styler.ExcelExportStylerDefaultImpl;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;

/**
 * Excel 自定义styler
 *
 * @author
 *
 */
public class ExcelExportStatisticStyler extends ExcelExportStylerDefaultImpl {

    private CellStyle numberCellStyle;

    public ExcelExportStatisticStyler(Workbook workbook) {
        super(workbook);
        createNumberCellStyler();
    }

    private void createNumberCellStyler() {
        numberCellStyle = workbook.createCellStyle();
        numberCellStyle.setAlignment(HorizontalAlignment.CENTER);
        numberCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        numberCellStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        numberCellStyle.setFont(font);
    }

    @Override
    public CellStyle getStyles(boolean noneStyler, ExcelExportEntity entity) {
        //numberCellStyle.setFillForegroundColor(noneStyler? IndexedColors.RED.getIndex() : IndexedColors.BLUE.getIndex());
        //当单元格是默认为换行时，设置自定义字体颜色
        if(entity != null && entity.isWrap()){
            return numberCellStyle;
        }
        return super.getStyles(noneStyler, entity);
    }

}
