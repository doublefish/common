package com.tt.common.component;

import com.alibaba.excel.ExcelWriter;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * ExcelWriterHelperBuilder
 *
 * @author Shuang Yu
 */
public class ExcelWriterHelperBuilder {

    private ExcelWriter writer;
    private String sheetName;
    private int sheetRows;
    private File file;
    protected Map<String, String> headers;
    protected Class<?> dataClass;
    private int columnWidth;
    private int totalRows;
    private int batchRows;
    private Supplier<List<?>> scroll;
    private Function<List<?>, List<?>> converter;
    private BiConsumer<ExcelWriterBaseHelper, Integer> afterWrite;
    private BiConsumer<ExcelWriterBaseHelper, Integer> beforeFinish;

    public ExcelWriterHelperBuilder() {
    }

    public ExcelWriterHelperBuilder writer(ExcelWriter writer) {
        this.writer = writer;
        return this;
    }

    public ExcelWriterHelperBuilder sheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public ExcelWriterHelperBuilder sheetRows(int sheetRows) {
        this.sheetRows = sheetRows;
        return this;
    }

    public ExcelWriterHelperBuilder file(File file) {
        this.file = file;
        return this;
    }

    public ExcelWriterHelperBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public ExcelWriterHelperBuilder dataClass(Class<?> dataClass) {
        this.dataClass = dataClass;
        return this;
    }

    public ExcelWriterHelperBuilder columnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
        return this;
    }

    public ExcelWriterHelperBuilder totalRows(int totalRows) {
        this.totalRows = totalRows;
        return this;
    }

    public ExcelWriterHelperBuilder batchRows(int batchRows) {
        this.batchRows = batchRows;
        return this;
    }

    public ExcelWriterHelperBuilder scroll(Supplier<List<?>> scroll) {
        this.scroll = scroll;
        return this;
    }

    public ExcelWriterHelperBuilder afterWrite(BiConsumer<ExcelWriterBaseHelper, Integer> afterWrite) {
        this.afterWrite = afterWrite;
        return this;
    }

    public ExcelWriterHelperBuilder beforeFinish(BiConsumer<ExcelWriterBaseHelper, Integer> beforeFinish) {
        this.beforeFinish = beforeFinish;
        return this;
    }

    public ExcelWriterHelper build() {
        return new ExcelWriterHelper(this.writer, this.sheetName, this.sheetRows, this.file, this.headers, this.dataClass, this.columnWidth, this.totalRows, this.batchRows, this.scroll, this.afterWrite, this.beforeFinish);
    }

    public String toString() {
        return "ExcelWriterHelperBuilder(writer=" + this.writer + ", sheetName=" + this.sheetName + ", sheetRows=" + this.sheetRows + ", file=" + this.file + ", headers=" + this.headers + ", dataClass=" + this.dataClass + ", columnWidth=" + this.columnWidth + ", totalRows=" + this.totalRows + ", batchRows=" + this.batchRows + ")";
    }
}
