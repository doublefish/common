package com.tt.common.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.tt.common.Constants;
import lombok.var;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * InstantConverter
 *
 * @author Shuang Yu
 */
@Component
public class InstantConverter implements Converter<Instant> {

    protected final DateTimeFormatter formatter;

    public InstantConverter() {
        this(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT).withZone(ZoneId.systemDefault()));
    }

    public InstantConverter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public Class<Instant> supportJavaTypeKey() {
        return Instant.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Instant convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        var time = formatter.parse(cellData.getStringValue());
        var ldt = LocalDateTime.from(time);
        return ldt.toInstant(ZoneOffset.UTC);
    }

    @Override
    public WriteCellData<?> convertToExcelData(Instant value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value.compareTo(Instant.EPOCH) <= 0) {
            return new WriteCellData<>("");
        }
        var ldt = LocalDateTime.ofInstant(value, ZoneId.systemDefault());
        return new WriteCellData<>(formatter.format(ldt));
    }
}
