package com.tt.common.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


/**
 * StringListConverter
 *
 * @author Shuang Yu
 */
@Component
public class ListConverter extends BaseCollectionConverter implements Converter<List<?>> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return List.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public List<?> convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return Arrays.asList(cellData.getStringValue().split("、"));
    }

    @Override
    public WriteCellData<?> convertToExcelData(List<?> value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return super.convertToExcelData(value);
    }

}
