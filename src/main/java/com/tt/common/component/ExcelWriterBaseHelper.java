package com.tt.common.component;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.tt.common.excel.EasyExcelHelper;
import lombok.Getter;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * ExcelWriterBaseHelper
 * 通过依赖EasyExcel滚动写入excel功能，实现低能耗高性能的大数据量excel导出功能
 *
 * @author Shuang Yu
 */
public class ExcelWriterBaseHelper {

    /**
     * 可以不指定
     */
    private final ExcelWriter writer;
    /**
     * WriteSheets
     */
    private final List<WriteSheet> sheets;
    /**
     * Sheet前缀
     */
    private final String sheetName;
    /**
     * 单个Sheet最大行数=1048575，默认：1000000
     */
    private final int sheetRows;
    /**
     * 预计总行数
     */
    private final int totalRows;
    /**
     * 预计每批行数
     */
    private final int batchRows;
    /**
     * 已写入行数
     */
    @Getter
    private int rows;
    /**
     * 滚动获取数据方法，返回值须是嵌套List
     */
    private final Supplier<List<?>> scroll;
    /**
     * 转换数据
     */
    private final Function<List<?>, List<?>> converter;
    /**
     * 每次写入滚动获取的数据之后执行，入参：已写入行数
     */
    private final BiConsumer<ExcelWriterBaseHelper, Integer> afterWrite;
    /**
     * 完成之前执行，入参：已写入行数
     */
    private final BiConsumer<ExcelWriterBaseHelper, Integer> beforeFinish;

    /**
     * 构造函数
     *
     * @param writer       writer：默认自动生成，集合类型的属性值会自动用顿号拼接，Instant类型的属性会自动转为yyyy-MM-dd HH:mm:ss格式字符串
     * @param sheetName    sheetName
     * @param sheetRows    单个Sheet最大行数=1048575，默认：1000000
     * @param file         excel文件对象
     * @param head         列头：嵌套List
     * @param columnWidth  列宽：默认20，对默认writer有效
     * @param totalRows    预计总行数：达到目标行数自动停止，默认：10000000
     * @param batchRows    预计每批行数：默认：1000
     * @param scroll       滚动获取数据方法：返回值须是嵌套List
     * @param converter    转换数据
     * @param afterWrite   每次写入滚动获取的数据之后执行，入参：已写入行数
     * @param beforeFinish 完成之前执行，入参：已写入行数
     */
    public ExcelWriterBaseHelper(@Nullable ExcelWriter writer, String sheetName, @Nullable Integer sheetRows, File file,
                                 List<List<String>> head, @Nullable Integer columnWidth, @Nullable Integer totalRows,
                                 @Nullable Integer batchRows, @Nullable Supplier<List<?>> scroll, Function<List<?>, List<?>> converter,
                                 @Nullable BiConsumer<ExcelWriterBaseHelper, Integer> afterWrite,
                                 @Nullable BiConsumer<ExcelWriterBaseHelper, Integer> beforeFinish) {
        //Assert.notNull(scroll, "scroll must not be null");
        this.sheetName = sheetName;
        this.sheetRows = sheetRows != null && sheetRows > 0 ? sheetRows : 1000000;
        this.totalRows = totalRows != null && totalRows > 0 ? totalRows : 10000000;
        this.batchRows = batchRows != null && batchRows > 0 ? batchRows : 1000;
        this.scroll = scroll;
        this.converter = converter;
        this.afterWrite = afterWrite;
        this.beforeFinish = beforeFinish;
        if (writer == null) {
            Assert.notNull(file, "file must not be null");
            Assert.notNull(head, "head must not be null");
            this.writer = EasyExcelHelper.generateWriterBuilder(file).head(head)
                    .registerWriteHandler(new SimpleColumnWidthStyleStrategy(columnWidth != null && columnWidth > 0 ? columnWidth : 20)).build();
        } else {
            this.writer = writer;
        }
        sheets = new ArrayList<>();
    }

    /**
     * 开始
     */
    public void start() {
        Assert.notNull(scroll, "scroll must not be null");
        while (true) {
            var data = scroll.get();
            if (CollectionUtils.isEmpty(data)) {
                break;
            }
            if (converter != null) {
                write(converter.apply(data));
            } else {
                write(data);
            }
            if (data.size() < batchRows || rows >= totalRows) {
                break;
            }
        }
        finish();
    }

    /**
     * 开始
     */
    public void start(List<?> data) {
        if (converter != null) {
            write(converter.apply(data));
        } else {
            write(data);
        }
        finish();
    }

    /**
     * 写数据
     *
     * @param data data
     */
    public void write(Collection<?> data) {
        write(data, null);
    }

    /**
     * 写数据
     *
     * @param data       data
     * @param writeTable writeTable
     */
    public void write(Collection<?> data, WriteTable writeTable) {
        var sheet = getSheet();
        writer.write(data, sheet, writeTable);
        rows += data.size();
        if (afterWrite != null) {
            afterWrite.accept(this, rows);
        }
    }

    /**
     * 完成
     */
    public void finish() {
        if (beforeFinish != null) {
            beforeFinish.accept(this, rows);
        }
        writer.finish();
    }

    /**
     * 获取当前Sheet
     *
     * @return WriteSheet
     */
    public WriteSheet getSheet() {
        var remain = rows % sheetRows;
        if (remain == 0 || remain + batchRows > sheetRows) {
            var sheet = EasyExcelFactory.writerSheet(sheetName + (sheets.size() + 1)).build();
            sheets.add(sheet);
        }
        return sheets.get(sheets.size() - 1);
    }

}

