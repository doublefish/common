package com.tt.common.component;

import com.alibaba.excel.ExcelWriter;
import com.tt.common.excel.EasyExcelHelper;
import org.springframework.lang.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * ExcelWriterHelper
 *
 * @author Shuang Yu
 */
public class ExcelWriterHelper extends ExcelWriterBaseHelper {

    protected Map<String, String> headers;

    protected Class<?> dataClass;

    /**
     * 构造函数
     *
     * @param writer       writer：默认自动生成，集合类型的属性值会自动用顿号拼接，Instant类型的属性会自动转为yyyy-MM-dd HH:mm:ss格式字符串
     * @param sheetName    sheetName
     * @param sheetRows    单个Sheet最大行数=1048575，默认：1000000
     * @param file         excel文件对象
     * @param headers      列头：字段，列头Map
     * @param columnWidth  列宽：默认20，对默认writer有效
     * @param totalRows    预计总行数：达到目标行数自动停止，默认：10000000
     * @param batchRows    预计每批行数：默认：1000
     * @param scroll       滚动获取数据方法：返回值须是嵌套List
     * @param dataClass    dataClass
     * @param afterWrite   每次写入滚动获取的数据之后执行，入参：已写入行数
     * @param beforeFinish 完成之前执行，入参：已写入行数
     */
    public ExcelWriterHelper(@Nullable ExcelWriter writer, String sheetName, @Nullable Integer sheetRows, File file,
                             Map<String, String> headers, Class<?> dataClass, @Nullable Integer columnWidth,
                             @Nullable Integer totalRows, @Nullable Integer batchRows, Supplier<List<?>> scroll,
                             @Nullable BiConsumer<ExcelWriterBaseHelper, Integer> afterWrite,
                             @Nullable BiConsumer<ExcelWriterBaseHelper, Integer> beforeFinish) {
        super(writer, sheetName, sheetRows, file,
                EasyExcelHelper.generateDynamicHead(headers.values()),
                columnWidth, totalRows, batchRows, scroll,
                data -> EasyExcelHelper.generateDynamicContent(data, headers.keySet(), dataClass),
                afterWrite, beforeFinish);
        this.headers = headers;
        this.dataClass = dataClass;
    }

    @Override
    public void start() {
        super.start();
    }

}
