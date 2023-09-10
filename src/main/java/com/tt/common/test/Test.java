package com.tt.common.test;

import com.tt.common.Constants;
import com.tt.common.User;
import com.tt.common.component.ElasticsearchHelper;
import com.tt.common.component.ExcelWriterBaseHelperBuilder;
import com.tt.common.component.ExcelWriterHelperBuilder;
import com.tt.common.component.MyBatisPlusDbHelperBuilder;
import com.tt.common.util.BaseEntityUtils;
import com.tt.common.util.DateTimeUtils;
import com.tt.common.util.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.elasticsearch.common.settings.Settings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Test
 *
 * @author Shuang.Yu
 */
@Slf4j
@SpringBootApplication
@EnableFeignClients
public class Test {

    static ApplicationContext context;
    static DateTimeFormatter dateTimeFormatter;

    public static void main(String[] args) {
        try {
            context = SpringApplication.run(Test.class, args);
            log.info("启动成功");
        } catch (Exception e) {
            log.error("启动发生异常：" + e, e);
            throw e;
        }
        dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT_MILLS).withZone(ZoneId.systemDefault());


        try {
            test2();
            log.info("执行成功");
        } catch (Exception e) {
            log.error("执行发生异常：" + e, e);
        }
    }

    static void test2() {
        var jdbc = context.getBean(JdbcTemplate.class);
        var table = "tb_org_151666005646";
        var sql = String.format("SELECT * FROM %s LIMIT 1000", table);
        var entities = jdbc.query(sql, new BeanPropertyRowMapper<>(TestModel1.class));
    }


    static void test1() {
        var time = LocalDateTime.now();
        var formats = Arrays.asList("yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS", "yyyyMMdd", "yyyyMMddHHmmss", "yyyyMMddHHmmssSSS");
        var strings = new HashMap<String, Object>(0);
        var times = new HashMap<String, Object>(0);
        for (var format : formats) {
            try {
                strings.put(format, DateTimeUtils.toString(time, format));
            } catch (Exception e) {
                strings.put(format, e);
            }
        }
        for (var format : formats) {
            var string = strings.get(format).toString();
            try {
                times.put(format, DateTimeUtils.toInstant(string, format));
            } catch (Exception e) {
                times.put(format, e);
            }
        }

        var str = "2020-12-01 12:00:00";
        var instant = DateTimeUtils.toInstant(str, "yyyy-MM-dd HH:mm:ss");
        log.info("{}", times);
    }


    static void test() throws SQLException {

        var user = new User();
        user.setId("1");
        user.setName("test");

        var mills = System.currentTimeMillis();
        var dbHelper = new MyBatisPlusDbHelperBuilder<OrgEntity>().clazz(OrgEntity.class).build();
        log.info("初始化耗时：{}ms", System.currentTimeMillis() - mills);

        var jdbc = context.getBean(JdbcTemplate.class);
        var table = "tb_org_151666005646";
        var sql = String.format("SELECT * FROM %s LIMIT 1000", table);
        var entities = jdbc.query(sql, dbHelper::convert);
        var u = new User();
        u.setId("user");
        u.setName("username");
        BaseEntityUtils.setCreateInfo(entities, u);

        dbHelper.setTableSuffix("123");

        log.info("初始化耗时：{}ms", System.currentTimeMillis() - mills);

        var models = getModels(1, 1000);
        var ids = models.stream().map(TestModel::getId).collect(Collectors.toList());

        mills = System.currentTimeMillis();
        var sql1 = dbHelper.getInsertSQL(Collections.singleton(entities.get(0)));
        log.info("getInsertSQL：{}ms", System.currentTimeMillis() - mills);

        mills = System.currentTimeMillis();
        var sql2 = dbHelper.getInsertSQL(entities);
        log.info("getInsertSQL：{}ms", System.currentTimeMillis() - mills);

        var ds = jdbc.getDataSource();
        if (ds == null) {
            throw new RuntimeException("ds is null");
        }
        var conn = ds.getConnection();
        var sql21 = dbHelper.getInsertPreparedStatement(conn, entities);
        log.info("getInsertSQL：{}ms，{}", System.currentTimeMillis() - mills, sql21);

        mills = System.currentTimeMillis();
        var sql31 = dbHelper.getDeleteSQL(1, "user");
        var sql32 = dbHelper.getDeleteSQL("1", "user");
        log.info("getDeleteSQL：{}ms", System.currentTimeMillis() - mills);

        mills = System.currentTimeMillis();
        var sql41 = dbHelper.getDeleteSQL(Arrays.asList(1, 2), "user");
        var sql42 = dbHelper.getDeleteSQL(Arrays.asList("1", "2"), "user");
        log.info("getDeleteSQL：{}ms", System.currentTimeMillis() - mills);

        mills = System.currentTimeMillis();
        var sql5 = dbHelper.getUpdateByIdSQL(entities.get(0));
        log.info("getUpdateByIdSQL：{}ms", System.currentTimeMillis() - mills);

    }

    static void es() {
        var helper = new ElasticsearchHelper(context);
        var clazz = TestModel.class;
        var index = "test-unofficial";
        if (!helper.indexExists(index)) {
            var settings = Settings.builder().put("max_result_window", 1000000L).build();
            var res = helper.create(index, settings, clazz);
            log.info("创建索引：{}", JSONUtils.toJSONString(res));
        }

        var models = getModels(120001, 10000);
        try {
            var mills = System.currentTimeMillis();
            var res = helper.index(index, models);
            log.info("批量保存：{}，耗时：{}ms", res.hasFailures(), System.currentTimeMillis() - mills);
        } catch (Exception e) {
            log.info("批量保存发生异常：" + e.getMessage(), e);
        }

        for (var i = 0; i < 10; i++) {
            try {
                var model = helper.searchById("83", clazz, index);
                log.info("res: {}", model);
            } catch (Exception e) {
                log.error("error: " + e, e);
            }
        }

    }

    public File export(Collection<OrgEntity> entities) {
        var file = new File("export.xlsx");
        Supplier<List<?>> scrollData = () -> {
            // 滚动查询数据
            var data = new ArrayList<List<String>>();
            data.add(Collections.singletonList("0001"));
            data.add(Collections.singletonList("工厂"));
            return data;
        };
        // 生成动态头
        var head = new ArrayList<List<String>>();
        head.add(Collections.singletonList("编码"));
        head.add(Collections.singletonList("名称"));
        var exporter = new ExcelWriterBaseHelperBuilder().sheetName("Sheet").file(file).head(head)
                .totalRows(5000000).batchRows(1000).scroll(scrollData).afterWrite((e, rows) -> {
                    if (rows % 10000 == 0) {
                        log.info("累计导出：{}", rows);
                    }
                }).build();
        return file;
        // 上传文件及后续操作
    }

    public File export2(Collection<OrgEntity> entities) {
        var file = new File("export2.xlsx");
        Supplier<List<?>> scrollData = () -> {
            // 滚动查询数据
            var data = new ArrayList<List<String>>();
            data.add(Collections.singletonList("0001"));
            data.add(Collections.singletonList("工厂"));
            return data;
        };
        // 生成动态头：字段，列头
        var headers = new LinkedHashMap<String, String>(0);
        headers.put("code", "编码");
        headers.put("name", "名称");
        var exporter = new ExcelWriterHelperBuilder().sheetName("Sheet").file(file).headers(headers).dataClass(OrgEntity.class)
                .totalRows(5000000).batchRows(1000).scroll(scrollData).afterWrite((e, rows) -> {
                    if (rows % 10000 == 0) {
                        log.info("累计导出：{}", rows);
                    }
                }).build();
        return file;
        // 上传文件及后续操作

    }

    static List<TestModel> getModels(long startId, int size) {
        var user = new User();
        user.setId("1");
        user.setName("test");
        var models = new ArrayList<TestModel>();
        for (var i = 0; i < size; i++) {
            var model1 = new TestModel();
            var id = startId + i;
            model1.setId(id);
            model1.setCode("code" + id);
            model1.setName("name" + id);
            BaseEntityUtils.setCreateInfo(model1, user);
            models.add(model1);
        }
        return models;
    }

}
