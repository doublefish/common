package com.tt.common.excel;


import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteFont;
import lombok.var;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.util.List;
import java.util.Map;

/**
 * CellHandlerHelper
 *
 * @author Shuang Yu
 */
public class CellHandlerHelper {

    private static WriteFont defaultHeadFont;
    private static WriteFont redHeadFont;

    private static WriteFont getDefaultHeadFont() {
        if (defaultHeadFont == null) {
            var writeFont = new WriteFont();
            writeFont.setFontName("宋体");
            writeFont.setFontHeightInPoints((short) 14);
            writeFont.setBold(true);
            //writeFont.setColor(IndexedColors.RED.index);
            defaultHeadFont = writeFont;
        }
        return defaultHeadFont;
    }

    private static WriteFont getRedHeadFont() {
        if (redHeadFont == null) {
            var writeFont = new WriteFont();
            writeFont.setFontName("宋体");
            writeFont.setFontHeightInPoints((short) 14);
            writeFont.setBold(true);
            writeFont.setColor(IndexedColors.RED.index);
            redHeadFont = writeFont;
        }
        return redHeadFont;
    }

    /**
     * 处理单元格
     *
     * @param context      context
     * @param specialCells specialCells
     * @param commMap      commMap
     */
    public static void handleCell(CellWriteHandlerContext context, Map<Integer, List<Integer>> specialCells, Map<Integer, String> commMap) {
        // 处理表头
        if (Boolean.TRUE.equals(context.getHead())) {
            var cell = context.getCell();
            var writeCellData = context.getFirstCellData();
            var writeCellStyle = writeCellData.getWriteCellStyle();
            // 添加批注
            if (MapUtils.isNotEmpty(commMap)) {
                var drawing = context.getWriteSheetHolder().getSheet().createDrawingPatriarch();
                var desc = commMap.get(cell.getColumnIndex());
                if (StringUtils.isNotEmpty(desc)) {
                    // 参数：dx1：起始单元格的x偏移量
                    //dy1：起始单元格的y偏移量
                    //dx2：终止单元格的x偏移量
                    //dy2：终止单元格的y偏移量
                    //col1：起始单元格列序号，从0开始计算；
                    //row1：起始单元格行序号，从0开始计算，如：col1=0,row1=0就表示起始单元格为A1；
                    //col2：终止单元格列序号，从0开始计算；
                    //row2：终止单元格行序号，从0开始计算，如：col2=2,row2=2就表示终止单元格为C3；
                    var comment = drawing.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, cell.getColumnIndex(), 0, cell.getColumnIndex() + 1, 3));
                    comment.setString(new XSSFRichTextString(desc));
                    cell.setCellComment(comment);
                }
            }
            // 对specialCells中存在的需要标红的单元格进行标红
            if (MapUtils.isNotEmpty(specialCells) && specialCells.containsKey(cell.getRowIndex())
                    && specialCells.get(cell.getRowIndex()).contains(cell.getColumnIndex())) {
                writeCellStyle.setWriteFont(getRedHeadFont());
            } else {
                writeCellStyle.setWriteFont(getDefaultHeadFont());
            }

            var wb = context.getWriteContext().writeWorkbookHolder().getWorkbook();
            var sheet = wb.getSheetAt(0);
            var format = wb.createDataFormat();
            var cellStyle = wb.createCellStyle();
            cellStyle.setDataFormat(format.getFormat("@"));
            sheet.setDefaultColumnStyle(cell.getColumnIndex(), cellStyle);
        }
        /* 注释 else {
            // 对specialCells中存在的需要标红的单元格进行标红
            if (MapUtils.isNotEmpty(specialCells) && specialCells.containsKey(cell.getRowIndex())
                    && specialCells.get(cell.getRowIndex()).contains(cell.getColumnIndex())) {
                setCellColor(writeSheetHolder, cell, IndexedColors.RED.index);
            }
        }*/
    }


}
