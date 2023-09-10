package com.tt.common.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * ArrayListConverter
 *
 * @author Shuang Yu
 */
@Component
public class ArrayListConverter extends BaseCollectionConverter implements Converter<ArrayList<?>> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return ArrayList.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public ArrayList<?> convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new ArrayList<>(Arrays.asList(cellData.getStringValue().split("、")));
    }

    @Override
    public WriteCellData<?> convertToExcelData(ArrayList<?> value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return super.convertToExcelData(value);
    }

}
