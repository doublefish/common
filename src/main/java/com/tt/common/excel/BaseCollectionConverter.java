package com.tt.common.excel;

import com.alibaba.excel.metadata.data.WriteCellData;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;

/**
 * BaseCollectionConverter
 *
 * @author Shuang Yu
 */
public class BaseCollectionConverter {

    /**
     * convertToExcelData
     *
     * @param value value
     */
    protected WriteCellData<?> convertToExcelData(Collection<?> value) {
        var stringValue = "";
        if (CollectionUtils.isNotEmpty(value)) {
            var builder = new StringBuilder();
            for (var v : value) {
                builder.append("、").append(v);
            }
            stringValue = builder.substring(1);
        }
        return new WriteCellData<>(stringValue);
    }
}
