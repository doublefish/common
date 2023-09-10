package com.tt.common.excel;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;

import java.util.List;
import java.util.Map;

/**
 * MyCellWriteHandler
 *
 * @author Shuang Yu
 */
public class MyCellWriteHandler implements CellWriteHandler {

    private final Map<Integer, List<Integer>> specialCells;
    private final Map<Integer, String> commMap;

    public MyCellWriteHandler(Map<Integer, List<Integer>> specialCells, Map<Integer, String> commMap) {
        this.specialCells = specialCells;
        this.commMap = commMap;
    }

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        // 设置单元格颜色、批注
        CellHandlerHelper.handleCell(context, this.specialCells, this.commMap);
    }

}
