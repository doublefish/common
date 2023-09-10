package com.tt.common.excel;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.time.Instant;
import java.util.Date;

/**
 * ExcelHelper
 *
 * @author Shuang Yu
 */
@Slf4j
public class ExcelHelper {

    private static final Date zeroDate = Date.from(Instant.EPOCH);
    private static HSSFFont defaultHeadFont;
    private static HSSFFont redHeadFont;

    public static HSSFFont getDefaultHeadFont(HSSFWorkbook workbook) {
        if (defaultHeadFont == null) {
            var font = workbook.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 14);
            font.setBold(true);
            defaultHeadFont = font;
        }
        return defaultHeadFont;
    }

    public static HSSFFont getRedHeadFont(HSSFWorkbook workbook) {
        if (redHeadFont == null) {
            var font = workbook.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 14);
            font.setBold(true);
            font.setColor(IndexedColors.RED.index);
            redHeadFont = font;
        }
        return redHeadFont;
    }

}
