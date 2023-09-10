package com.tt.common.excel;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Supplier;

/**
 * EasyExcelWriter
 *
 * @author Shuang Yu
 */
@Getter
@Builder
public class EasyExcelWriter {

    @Setter
    private ExcelWriter writer;

    @Setter
    private String sheetName;

    @Setter
    private Supplier<List<?>> getData;

    private long dataCount;

    private boolean stop;

    /**
     * 主动停止
     */
    public void stop() {
        this.stop = true;
    }

    /**
     * 开始
     */
    public void start() {
        var sheet = EasyExcelFactory.writerSheet(sheetName).build();
        while (true) {
            var data = getData.get();
            if (CollectionUtils.isEmpty(data)) {
                break;
            }
            dataCount += data.size();
            writer.write(data, sheet);
            if (stop) {
                break;
            }
        }
        writer.finish();
    }


}
