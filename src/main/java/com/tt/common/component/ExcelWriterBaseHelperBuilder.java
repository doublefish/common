package com.tt.common.component;

import com.alibaba.excel.ExcelWriter;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * ExcelWriterBaseHelperBuilder
 *
 * @author Shuang Yu
 */
public class ExcelWriterBaseHelperBuilder {

    private ExcelWriter writer;
    private String sheetName;
    private int sheetRows;
    private File file;
    private List<List<String>> head;
    private int columnWidth;
    private int totalRows;
    private int batchRows;
    private Supplier<List<?>> scroll;
    private Function<List<?>, List<?>> converter;
    private BiConsumer<ExcelWriterBaseHelper, Integer> afterWrite;
    private BiConsumer<ExcelWriterBaseHelper, Integer> beforeFinish;

    public ExcelWriterBaseHelperBuilder() {
    }

    public ExcelWriterBaseHelperBuilder writer(ExcelWriter writer) {
        this.writer = writer;
        return this;
    }

    public ExcelWriterBaseHelperBuilder sheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public ExcelWriterBaseHelperBuilder sheetRows(int sheetRows) {
        this.sheetRows = sheetRows;
        return this;
    }

    public ExcelWriterBaseHelperBuilder file(File file) {
        this.file = file;
        return this;
    }

    public ExcelWriterBaseHelperBuilder head(List<List<String>> head) {
        this.head = head;
        return this;
    }

    public ExcelWriterBaseHelperBuilder columnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
        return this;
    }

    public ExcelWriterBaseHelperBuilder totalRows(int totalRows) {
        this.totalRows = totalRows;
        return this;
    }

    public ExcelWriterBaseHelperBuilder batchRows(int batchRows) {
        this.batchRows = batchRows;
        return this;
    }

    public ExcelWriterBaseHelperBuilder scroll(Supplier<List<?>> scroll) {
        this.scroll = scroll;
        return this;
    }

    public ExcelWriterBaseHelperBuilder converter(Function<List<?>, List<?>> converter) {
        this.converter = converter;
        return this;
    }

    public ExcelWriterBaseHelperBuilder afterWrite(BiConsumer<ExcelWriterBaseHelper, Integer> afterWrite) {
        this.afterWrite = afterWrite;
        return this;
    }

    public ExcelWriterBaseHelperBuilder beforeFinish(BiConsumer<ExcelWriterBaseHelper, Integer> beforeFinish) {
        this.beforeFinish = beforeFinish;
        return this;
    }

    public ExcelWriterBaseHelper build() {
        return new ExcelWriterBaseHelper(this.writer, this.sheetName, this.sheetRows, this.file, this.head, this.columnWidth, this.totalRows, this.batchRows, this.scroll, this.converter, this.afterWrite, this.beforeFinish);
    }

    public String toString() {
        return "ExcelWriterBaseHelperBuilder(writer=" + this.writer + ", sheetName=" + this.sheetName + ", sheetRows=" + this.sheetRows + ", file=" + this.file + ", head=" + this.head + ", columnWidth=" + this.columnWidth + ", totalRows=" + this.totalRows + ", batchRows=" + this.batchRows + ")";
    }
}
