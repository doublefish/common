# 物流业务公共组件：logistics-common

## 一、引用方法

### 1. 默认用法

```xml

<dependency>
    <groupId>com.tt</groupId>
    <artifactId>common</artifactId>
    <version>1.0.8</version>
</dependency>
```

### 2. 高级用法：排除不必要的引用，避免健康检查报错

```xml

<dependency>
    <groupId>com.tt</groupId>
    <artifactId>common</artifactId>
    <version>1.0.8</version>
    <exclusions>
        <exclusion>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.baomidou</groupId>
            <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 二、组件介绍

### IdFactoryBase

#### 简介：支持多实例主键自增，解决使用事务插入多个表时，关联表需要先生成Id的场景。

#### 原理：依赖Redis的Hash实现

使用样例：

```java
import com.tt.common.component.IdFactoryBase;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

public class example {

    private final StringRedisTemplate redisTemplate;
    private final IdFactoryBase idFactory;

    public example(ApplicationContext context) {
        redisTemplate = context.getBean(StringRedisTemplate.class);
        idFactory = new IdFactoryBase(redisTemplate);
    }

    protected long getId(String enterpriseNo, long delta) {
        var redisKey = "tt:example:id";
        return idFactory.getId(redisKey, enterpriseNo, delta, this::getMaxId, enterpriseNo);
    }

    protected long getMaxId(String enterpriseNo) {
        // 查询档期那实际最大ID，用于补缓存
        return System.currentTimeMillis();
    }

    public void insert(String enterpriseNo, List<?> entities) {
        // 一次性获取entities.size()个Id
        var id0 = getId(enterpriseNo, entities.size());
        for (var entity : entities) {
            entity.setId(id0);
            id0++;
        }
        // 后续操作
    }

}
```

### IdFactory

#### 简介：在Base的基础上，自动获取实际最大Id，补缓存

使用样例：

```java
import com.tt.common.component.IdFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class example {

    private final StringRedisTemplate redisTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final IdFactoryBase idFactory;

    public example(ApplicationContext context) {
        redisTemplate = context.getBean(StringRedisTemplate.class);
        jdbcTemplate = context.getBean(JdbcTemplate.class);
        idFactory = new IdFactory(redisTemplate, jdbcTemplate);
    }

    protected long getId(String enterpriseNo, long delta) {
        var redisKey = "tt:example:id";
        var table = String.format("tb_example_%s", enterpriseNo);
        return idFactory.getId(redisKey, enterpriseNo, delta, table);
    }

    public void insert(String enterpriseNo, List<?> entities) {
        // 一次性获取entities.size()个Id
        var id0 = getId(enterpriseNo, entities.size());
        for (var entity : entities) {
            entity.setId(id0);
            id0++;
        }
        // 后续操作
    }

}
```

### DbHelper

#### 简介：自动生成批量插入语句，提升大数据量插入数据库性能，插入1W条数据，平均耗时1s，比MyBatis批量插入快10倍。

#### 未来目标：对增删改查操作简单封装，实现接近原生操作数据库的性能

```java
/**
 * DbHelper
 * 自动生成SQL语句，未来目标：对增删改查操作简单封装，实现接近原生操作数据库的性能
 * 支持类型：String，继承Number类的数字类型，实现Temporal接口的时间类型
 * 实现Iterable或Map接口的类型的属性自动忽略
 */
public class DbHelper<T>() {

    /**
     * 构造函数
     *
     * @param clazz             类型
     * @param tableName         表名：未声明时，须为实体类添加@com.tt.common.annotation.Table注解声明表名
     * @param mappings          属性和列名映射关系：未声明时，须为每个字段添加@com.baomidou.mybatisplus.annotation.TableField注解声明列名；<br/>如果每个字段都有对应的列，且符合驼峰规范，也可忽略此参数
     * @param includeId         包含Id字段：生成的插入语句中包含Id字段
     * @param includeFields     包含的字段：未声明mappings参数时，可通过此参数声明需要包含的列
     * @param excludeFields     排除的字段：未声明mappings参数时，可通过此参数声明需要排除的列
     * @param dateTimeFormatter 时间格式化工具：时间格式化工具默认格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    public DbHelper(Class<T> clazz, @Nullable String tableName, @Nullable Map<String, String> mappings, @Nullable Boolean includeId, @Nullable Collection<String> includeFields, @Nullable Collection<String> excludeFields, @Nullable DateTimeFormatter dateTimeFormatter) {
    }

    /**
     * 生成插入语句
     *
     * @param entities entities
     * @return INSERT INTO table (id,c1,c2) VALUES (1,'c1','c2'),...
     */
    public String getInsertSQL(Collection<T> entities) {
    }

    /**
     * resultSet转换为实体：适用查询所有字段
     *
     * @param resultSet java.sql.ResultSet.ResultSet
     * @param rowNum    rowNum
     */
    public T convert(ResultSet resultSet, int rowNum) {
    }

}
```

使用样例：

```java
import com.tt.common.RequestDataHelper;
import com.tt.common.component.DbHelper;
import com.tt.common.component.DbHelperBuilder;
import com.tt.common.test.OrgEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class example {

    private final JdbcTemplate jdbcTemplate;
    private final DbHelper<OrgEntity> dbHelper;

    public example(ApplicationContext context) {
        var jdbc = context.getBean(JdbcTemplate.class);
        this.dbHelper = new DbHelperBuilder<OrgEntity>().clazz(OrgEntity.class).build();
    }

    public int insert(String enterpriseNo, Collection<OrgEntity> entities) {
        // 设置表名后缀，生成SQL时自动拼接表名后缀
        var sql = dbHelper.getInsertSQL(entities, enterpriseNo);
        return jdbcTemplate.update(sql);
    }

    public List<OrgEntity> selectList(String enterpriseNo) {
        var sql = String.format("SELECT * FROM tb_org_%s WHERE row_deleted = 0 LIMIT 1000;", enterpriseNo);
        return jdbc.query(sql, dbHelper::convert);
    }

}
```

备注：也可以通过继承此类定制个性化的DbHelper，可参考下方：MyBatisPlusDbHelper。

### MyBatisPlusDbHelper

#### 在DbHelper的基础上，兼容MyBatis的注解：@TableName、@TableField、@TableId

```java
/**
 * MyBatisPlusDbHelper
 * 继承DbHelper，主要为兼容MyBatis的注解
 */
public class MyBatisPlusDbHelper<T> extends DbHelper<T> {


    /**
     * 构造函数
     *
     * @param clazz             类型：须为实体类添加@com.tt.common.annotation.TableName注解声明表名
     * @param mappings          属性和列名映射关系：未声明时，须为每个字段添加@com.baomidou.mybatisplus.annotation.TableField注解声明列名；<br/>如果每个字段都有对应的列，且符合驼峰规范，也可忽略此参数
     * @param includeId         包含Id字段：生成的插入语句中包含Id字段
     * @param includeFields     包含的字段：未声明mappings参数时，可通过此参数声明需要包含的列
     * @param excludeFields     排除的字段：未声明mappings参数时，可通过此参数声明需要排除的列
     * @param dateTimeFormatter 时间格式化工具：时间格式化工具默认格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    public MyBatisPlusDbHelper(Class<T> clazz
            , @Nullable Map<String, String> mappings
            , @Nullable Boolean includeId
            , @Nullable Collection<String> includeFields
            , @Nullable Collection<String> excludeFields
            , @Nullable DateTimeFormatter dateTimeFormatter) {
        super(clazz, getTableName(clazz), mappings, includeId, includeFields, excludeFields, dateTimeFormatter);
    }
}
```

使用样例：

```
var dbHelper = new MyBatisPlusDbHelperBuilder<OrgEntity>().clazz(OrgEntity.class).build();
```

### ExcelWriterBaseHelper

#### 简介：实现低能耗高性能的大数据量excel导出功能，支持千万级数据导出，自动分Sheet；解决一次性取出数据量过大，资源不足的问题。

```java
/**
 * ExcelWriterBaseHelper
 * 通过依赖EasyExcel滚动写入excel功能，实现低能耗高性能的大数据量excel导出功能
 */
public class ExcelWriterBaseHelper {

    /**
     * 构造函数
     *
     * @param writer      writer：默认自动生成，集合类型的属性值会自动用顿号拼接，Instant类型的属性会自动转为yyyy-MM-dd HH:mm:ss格式字符串
     * @param sheetName   sheetName
     * @param sheetRows   单个Sheet最大行数=1048575，默认：1000000
     * @param file        excel文件对象
     * @param head        列头：嵌套List
     * @param columnWidth 列宽：默认20，对默认writer有效
     * @param totalRows   预计总行数：达到目标行数自动停止，默认：10000000
     * @param batchRows   预计每批行数：默认：1000
     * @param scroll      滚动获取数据方法：返回值须是嵌套List
     * @param converter   转换数据
     * @param afterWrite  每次写入滚动获取的数据之后执行，入参：已写入行数
     */
    public ExcelWriterBaseHelper(@Nullable ExcelWriter writer, String sheetName, @Nullable Integer sheetRows, File file, List<List<String>> head
            , @Nullable Integer columnWidth, @Nullable Integer totalRows, @Nullable Integer batchRows, Supplier<List<?>> scroll, Function<List<?>, List<?>> converter
            , @Nullable Consumer<Integer> afterWrite) {
    }

    /**
     * 开始
     */
    public void start() {
    }

}
```

默认writer：

```
var writer = EasyExcelFactory.write(file).head(head)
                .registerConverter(new InstantConverter())
                .registerConverter(new StringArrayConverter())
                .registerConverter(new ListConverter())
                .registerConverter(new ArrayListConverter())
                //.registerWriteHandler(new MyCellWriteHandler(specialCells, null))
                .registerWriteHandler(new SimpleColumnWidthStyleStrategy(20))
                .excelType(ExcelTypeEnum.XLSX).build();
```

使用样例：

```java
import com.tt.common.component.ExcelWriterBaseHelperBuilder;
import com.tt.common.test.OrgEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class example {

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
                .totalRows(5000000).batchRows(1000).scroll(scrollData).afterWrite(rows -> {
                    if (rows % 10000 == 0) {
                        log.info("累计导出：{}", rows);
                    }
                }).build();
        return file;
        // 上传文件及后续操作
    }

}
```

### ExcelWriterHelper

#### 简介：在ExcelWriterBaseHelper的基础上，可传入列的字段和列名的map，以及对应的对象集合，自动实现动态头功能。

```java
/**
 * ExcelWriterBaseHelper
 * 通过依赖EasyExcel滚动写入excel功能，实现低能耗高性能的大数据量excel导出功能
 */
public class ExcelWriterHelper {

    /**
     * 构造函数
     *
     * @param writer      writer：默认自动生成，集合类型的属性值会自动用顿号拼接，Instant类型的属性会自动转为yyyy-MM-dd HH:mm:ss格式字符串
     * @param sheetName   sheetName
     * @param sheetRows   单个Sheet最大行数=1048575，默认：1000000
     * @param file        excel文件对象
     * @param headers     列头：字段，列头Map
     * @param columnWidth 列宽：默认20，对默认writer有效
     * @param totalRows   预计总行数：达到目标行数自动停止，默认：10000000
     * @param batchRows   预计每批行数：默认：1000
     * @param scroll      滚动获取数据方法：返回值须是嵌套List
     * @param dataClass   dataClass
     * @param afterWrite  每次写入滚动获取的数据之后执行，入参：已写入行数
     */
    public ExcelWriterHelper(@Nullable ExcelWriter writer, String sheetName, @Nullable Integer sheetRows, File file, Map<String, String> headers, Class<?> dataClass
            , @Nullable Integer columnWidth, @Nullable Integer totalRows, @Nullable Integer batchRows, Supplier<List<?>> scroll, @Nullable Consumer<Integer> afterWrite) {
    }

    /**
     * 开始
     */
    public void start() {
    }

}
```

使用样例：

```java
import com.tt.common.component.ExcelWriterHelperBuilder;
import com.tt.common.test.OrgEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class example {

    public File export(Collection<OrgEntity> entities) {
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
                .totalRows(5000000).batchRows(1000).scroll(scrollData).afterWrite(rows -> {
                    if (rows % 10000 == 0) {
                        log.info("累计导出：{}", rows);
                    }
                }).build();
        return file;
        // 上传文件及后续操作
    }

}
```

## 三、工具类

### ThreadUtils

#### 简介：多线程工具类：主线程的线程变量RequestDataHelper.getData()和链路Id自动带入子线程

使用样例：**请务必在阅读源码后，再使用此类。**

```java
import com.tt.common.util.ThreadUtils;

import java.time.LocalDateTime;

public class example {

    public void execute() {
        // 可传入多种类型的函数，详见源码
        var tasks = new ArrayList<Runnable>();
        tasks.add(() -> {
            var time = LocalDateTime.now();
            System.out.printf("子线程：%s%n", time);
        });
        ThreadUtils.submit(tasks, 3000);
    }
}
```

## 五、其他

### BaseListener

#### 简介：订阅默认消息类型为String，可手动应答；发生异常时，默认丢弃消息。

处理结果对应的答复类型：

* ResultType.SUCCESS
    - channel.basicAck(deliveryTag, false); // 成功：把消息从队列中移除
* ResultType.FAIL：
    - channel.basicNack(deliveryTag, false, false); // 重试：把消息追加到队尾
* ResultType.RETRY：
    - channel.basicNack(deliveryTag, false, true); // 失败：把消息从队列中移除
* ResultType.EXCEPTION：
    - channel.basicNack(deliveryTag, false, false); // 异常：把消息从队列中移除

使用样例：**请务必在阅读源码后，再使用此类。**

```java

import com.tt.common.amqp.BaseListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "#{rabbitUtil.queue('mq.queue.my')}", messageConverter = "simpleMessageConverter")
public class MyListener extends BaseListener {

    public MyListener() {
    }

    @Override
    protected ResultType execute(String message, Map<String, Object> headers) {
        var msg = JSONUtils.parse(message, MyDto.class);
        //service.execute(msg);
        return ResultType.SUCCESS;
    }

    @Override
    protected void beforeExecute() {
    }
}
```

### BaseClientService

#### 简介：统一捕获处理异常

使用样例：

```java
import com.tt.common.feign.BaseClientService;
import org.springframework.stereotype.Component;

@Component
public class OrganizationClientService extends BaseClientService<OrganizationClient> {

}
```

### BaseClient

#### 简介：动态调用Feign服务，可动态路径和参数发送GET/POST/PUT/DELETE请求

使用样例：

```java
import com.fasterxml.jackson.core.type.TypeReference;
import com.tt.common.ResultData;
import com.tt.common.feign.BaseDynamicClient;
import com.tt.common.sup8.vo.DictionaryFeignVo;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class example {

    private final BaseDynamicClient client;

    public example(ApplicationContext context) {
        client = new BaseDynamicClient(context, "dictionary");
    }

    public void execute() {
        var type = new TypeReference<ResultData<List<DictionaryFeignVo>>>() {
        };
        var params = new HashMap<String, Object>(0);
        params.put("code", code);
        params.put("enterpriseNo", enterpriseNo);
        var res = get(type, "v1/info/items", params);
        System.out.println(res.toString());
    }
}
```

### DictionaryFeignClient/LanguageFeignClient/HiveBasicFeignClient

#### 简介：常用Feign服务，几乎每个服务都会用到，避免重复代码

使用样例：

```java
import com.tt.common.sup8.DictionaryFeignClient;
import com.tt.common.sup8.HiveBasicFeignClient;
import com.tt.common.sup8.LanguageFeignClient;
import lombok.var;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class example {

    private final HiveBasicFeignClient hiveBasicFeignClient;
    private final DictionaryFeignClient dictionaryFeignClient;
    private final LanguageFeignClient languageFeignClient;

    public example(ApplicationContext context) {
        hiveBasicFeignClient = new HiveBasicFeignClient(context);
        dictionaryFeignClient = new DictionaryFeignClient(context);
        languageFeignClient = new LanguageFeignClient(context);
    }

    public void execute() {

        var eno = "000000000000";
        var areas = hiveBasicFeignClient.getByParent("340000000000", 2);

        var area = hiveBasicFeignClient.getByNames(Collections.singleton("长淮街道"));
        var areaDetail = hiveBasicFeignClient.getDetailByCodes(Collections.singleton("340102011000"));

        var items = dictionaryFeignClient.getDictItems("org_activation_status", eno);
        var config = dictionaryFeignClient.getConfigValues("org_activation_process", eno);

        var languages = languageFeignClient.getKeyData("organization", Arrays.asList("20500000", "20500001", "20500002"));

    }
}
```