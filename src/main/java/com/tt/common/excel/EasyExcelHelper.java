package com.tt.common.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.tt.common.util.DateTimeUtils;
import com.tt.common.util.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * EasyExcelHelper
 *
 * @author Shuang Yu
 */
@Slf4j
public class EasyExcelHelper {

    private static final Date zeroDate = Date.from(Instant.EPOCH);

    /**
     * generateWriterBuilder
     *
     * @param file file
     */
    public static ExcelWriterBuilder generateWriterBuilder(File file) {
        return EasyExcelFactory.write(file)
                .registerConverter(new InstantConverter())
                .registerConverter(new StringArrayConverter())
                .registerConverter(new ListConverter())
                .registerConverter(new ArrayListConverter())
                //.registerWriteHandler(new MyCellWriteHandler(specialCells, null))
                .excelType(ExcelTypeEnum.XLSX);
    }

    /**
     * 生成文件
     *
     * @param header       列头
     * @param specialCells 特殊列
     * @param file         文件
     */
    public static void generateExcel(List<List<String>> header, Map<Integer, List<Integer>> specialCells, File file) {
        var wb = new HSSFWorkbook();

        var format = wb.createDataFormat();
        var defaultFont = ExcelHelper.getDefaultHeadFont(wb);
        var readFont = ExcelHelper.getRedHeadFont(wb);
        var defaultCellStyle = wb.createCellStyle();
        defaultCellStyle.setDataFormat(format.getFormat("@"));
        defaultCellStyle.setFont(defaultFont);
        var readCellStyle = wb.createCellStyle();
        readCellStyle.setDataFormat(format.getFormat("@"));
        readCellStyle.setFont(readFont);

        var specialHead = specialCells.get(0);
        var sheet = wb.createSheet();
        var rowIndex = 0;
        var row = sheet.createRow(rowIndex);
        for (var i = 0; i < header.size(); i++) {
            var h = header.get(i);
            var cell = row.createCell(i);
            cell.setCellValue(h.get(0));
            if (specialHead.contains(i)) {
                sheet.setDefaultColumnStyle(i, defaultCellStyle);
            } else {
                sheet.setDefaultColumnStyle(i, readCellStyle);
            }
        }

        try (var fos = new FileOutputStream(file.getName())) {
            wb.write(fos);
            wb.write(file);
        } catch (Exception e) {
            throw new RuntimeException("生成文件发生异常：" + e.getMessage(), e);
        } finally {
            try {
                wb.close();
            } catch (Exception e) {
                log.info("关闭工作簿发生异常：" + e.getMessage(), e);
            }
        }
    }

    /**
     * read
     *
     * @param file    file
     * @param headers headers
     * @return List<LinkedHashMap < Integer, String>>
     */
    public static List<LinkedHashMap<Integer, String>> read(File file, @Nullable Map<Integer, String> headers) {
        return read(null, file, headers);
    }

    /**
     * read
     *
     * @param head    head
     * @param file    file
     * @param headers headers
     * @return List<T>
     */
    public static <T> List<T> read(@Nullable Class<?> head, File file, @Nullable Map<Integer, String> headers) {
        var data = new ArrayList<T>();
        EasyExcel.read(file, head, new ReadListener<T>() {

            final int BATCH_COUNT = 1000;

            private List<T> cachedData = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

            @Override
            public void invoke(T data, AnalysisContext context) {
                cachedData.add(data);
                if (cachedData.size() >= BATCH_COUNT) {
                    saveData();
                    // 存储完成清理 list
                    cachedData = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                saveData();
            }

            @Override
            public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                if (headers == null) {
                    return;
                }
                for (var k : headMap.keySet()) {
                    headers.put(k, headMap.get(k).getStringValue());
                }
            }

            private void saveData() {
                data.addAll(cachedData);
            }
        }).sheet().doRead();
        return data;
    }

    /**
     * write
     *
     * @param head         head
     * @param headers      headers
     * @param specialCells specialCells
     * @param data         data
     * @param file         file
     */
    public static void write(@Nullable Class<?> head, @Nullable Map<String, String> headers
            , @Nullable Map<Integer, List<Integer>> specialCells, @Nullable Collection<?> data, File file) {
        Assert.notNull(file, "file must not be null");
        var builder = generateWriterBuilder(file).head(head)
                .registerWriteHandler(new MyCellWriteHandler(specialCells, null));
        if (head != null && headers != null) {
            var fields = ReflectUtils.getDeclaredFields(head);
            var fieldsMap = new HashMap<Integer, String>(0);
            var orders = new ArrayList<Integer>();
            for (var field : fields) {
                var excelProperty = field.getAnnotation(ExcelProperty.class);
                if (excelProperty == null) {
                    continue;
                }
                fieldsMap.put(excelProperty.order(), field.getName());
                orders.add(excelProperty.order());
            }
            orders.sort(Comparator.naturalOrder());
            var headFields = new ArrayList<String>();
            var headNames = new ArrayList<List<String>>();
            for (var order : orders) {
                var field = fieldsMap.get(order);
                if (headers.containsKey(field)) {
                    headFields.add(field);
                    headNames.add(Collections.singletonList(headers.get(field)));
                }
            }
            builder.includeColumnFieldNames(headFields).head(headNames);
        }
        builder.sheet("Sheet1").doWrite(data);
    }

    /**
     * 生成动态头
     *
     * @param headers 列头
     * @return 动态头
     */
    public static List<List<String>> generateDynamicHead(Collection<String> headers) {
        var list = new ArrayList<List<String>>();
        for (var h : headers) {
            list.add(Collections.singletonList(h));
        }
        return list;
    }

    /**
     * 生成动态内容
     *
     * @param data  data
     * @param clazz clazz
     * @return 动态内容
     */
    public static List<?> generateDynamicContent(Collection<?> data, Collection<String> headers, Class<?> clazz) {
        if (CollectionUtils.isEmpty(data)) {
            return new ArrayList<>();
        }
        var pds = BeanUtils.getPropertyDescriptors(clazz);
        var pdMap = Arrays.stream(pds).collect(Collectors.toMap(PropertyDescriptor::getName, Function.identity(), (e, n) -> n));
        var childrenPdMap = new HashMap<String, Map<String, PropertyDescriptor>>(0);
        var body = new ArrayList<>();
        for (var d : data) {
            var objs = new ArrayList<>();
            var tempObjs = new HashMap<String, Object>(0);
            for (var header : headers) {
                var pd = pdMap.get(header);
                if (pd != null) {
                    var value = getValue(d, header, pd.getReadMethod());
                    objs.add(value);
                    continue;
                }
                if (header.contains(".")) {
                    var temps = header.split("\\.");
                    var field = temps[0];
                    var childField = temps[1];
                    var value = tempObjs.get(field);
                    // 如果未读取过，则读取一次
                    if (value == null && !tempObjs.containsKey(field)) {
                        var reader = pdMap.get(field).getReadMethod();
                        value = getValue(d, header, reader);
                        tempObjs.put(field, value);
                    }
                    if (value == null) {
                        objs.add(null);
                        continue;
                    }
                    var childPdMap = childrenPdMap.get(field);
                    if (childPdMap == null) {
                        var childPds = BeanUtils.getPropertyDescriptors(value.getClass());
                        childPdMap = Arrays.stream(childPds).collect(Collectors.toMap(PropertyDescriptor::getName, Function.identity(), (e, n) -> n));
                        childrenPdMap.put(field, childPdMap);
                    }
                    var childPd = childPdMap.get(childField);
                    if (childPd != null) {
                        var childValue = getValue(value, header, childPd.getReadMethod());
                        objs.add(childValue);
                        continue;
                    }
                }
                objs.add(null);
                //throw new RuntimeException("无效的字段：" + header);
            }
            body.add(objs);
        }
        return body;
    }

    /**
     * 写入（支持动态头）
     *
     * @param data         data
     * @param dataClass    dataClass
     * @param headers      headers
     * @param specialCells specialCells
     * @param file         file
     */
    public static void writeWithDynamicHead(Collection<?> data, Class<?> dataClass, Map<String, String> headers, Map<Integer, List<Integer>> specialCells, File file) {
        var head = generateDynamicHead(headers.values());
        var content = generateDynamicContent(data, headers.keySet(), dataClass);
        writeWithDynamicHead(content, head, specialCells, file);
    }

    /**
     * 写入（支持动态头）
     *
     * @param data         data
     * @param head         head
     * @param specialCells specialCells
     * @param file         file
     */
    public static void writeWithDynamicHead(Collection<?> data, List<List<String>> head, Map<Integer, List<Integer>> specialCells, File file) {
        var builder = generateWriterBuilder(file).head(head)
                .registerWriteHandler(new MyCellWriteHandler(specialCells, null));
        builder.sheet("Sheet1").doWrite(data);
    }

    private static <T> Object getValue(T data, String fieldName, Method reader) {
        try {
            var value = reader.invoke(data);
            if (value instanceof Date) {
                var date = (Date) value;
                if (zeroDate.equals(date)) {
                    value = "";
                } else {
                    value = DateTimeUtils.toString(date);
                }
            }
            return value;
        } catch (Exception e) {
            throw new RuntimeException("读取字段【" + fieldName + "】的值发生异常:" + e.getMessage(), e);
        }
    }

}
