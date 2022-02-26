### TDengine版ORM框架

##### 一、简介

框架基于 spring-boot 2.3.7.RELEASE, taos-jdbcdriver 2.0.34版本 支持TDengine2.2.2.0版本，实现实体查询即注解查询功能。(理论上支持JDBC且与mysql操作方式类似的时序库都支持)

说明：时序可一般情况下不允许删除和修改操作，所以本框架暂时支持查询和添加功能， 实体查询支持查询和插入，注解方式只支持查询

开发调试: 由于涛思官方对mac版没有提供client端，所以mac开发是能使用restful方式单点连接数据库

提示：taos相关问题请参考官方文档https://www.taosdata.com/cn/documentation/



##### 二、使用及配置

在spring boot项目中引入依赖 注：此项目为上传至maven库。如果使用请上传至私有库或者jar引入方式
```xml
<dependency>
  <groupId>xxxx</groupId>
  <artifactId>tdengine-orm</artifactId>
  <version>xxx</version>
</dependency>
```


```yaml
tdengine:
  #驱动
  #driverClassName: com.taosdata.jdbc.TSDBDriver
  #集群连接地址
  #url: jdbc:TAOS://:6030/test?charset=UTF-8&locale=en_US.UTF-8&timezone=UTC-8 
  #rs模式驱动
  driverClassName: com.taosdata.jdbc.rs.RestfulDriver
  #restFull单点连接配置
  url: jdbc:TAOS-RS://127.0.0.1:6041/test?charset=UTF-8&locale=en_US.UTF-8&timezone=UTC-8
  #数据库配置 Restful连接模式下需要配置 否则会出现 错误 切支持实体查询自动补全数据前缀 注解方式需在sql里自行添加 （普通和集群模式下无需设置此项系统会自动读取url的数据库）
  database: test
  #用户名
  username: test
  #密码
  password: test
  #最小空闲时间
  minimumIdle: 1000
  #最大池大小
  maximumPoolSize: 1000
  #连接超时
  connectionTimeout: 3000
  #最大寿命
  maxLifetime: 0
  #空闲超时
  idleTimeout: 0
  #注解方式必须设置此项否则无法扫描到mapper接口
  mapperPackage: xx.xxx.xxx.taostest
  #是否打印sql日志
  printSql: true
```



##### 三、核心类

| name                           | decription                                                   |
| ------------------------------ | ------------------------------------------------------------ |
| TDengineModel                  | 实体模型查询接口, 所有实体查询的方法都该接口提供，通过       |
| TDengineModelImpl              | 实体模型查询实现类提供executeQuery、executeQueryOne、executeInsert等方法其中executeInsert方法支持单条或多条插入,注：（如果想自定义查询逻辑请自己实现TDengineModel接口，并在spring boot启动中屏蔽TDengineModelImpl） |
| TDengineGenerateStatement      | sql语句生成接口 处理实体查询和注解查询的sql语句生成和解析    |
| TDengineGenerateStatementImpl  | sql已经生成实现类 ,注：（如果想自定义解析逻辑请自己实现TDengineGenerateStatement接口，并在spring boot启动中屏蔽TDengineGenerateStatementImpl） |
| TDStatementTool                | 核心工具类仅提供对sql相关方法中的逻辑支持，包访问权限 不对外开放 |
| TDengineBeanDefinitionRegister | 扫描指定包下的mapper接口，JDK动态代理实现接口 并注册到spring容器的主要核心类 |
| TDengineMapperFactory          | Mapper代理类生成工厂                                         |
| TDengineProxy                  | 动态代理实现                                                 |
| TDengineEntity                 | 实体基类，实体方式插入数据的必须继承该类 该类属性tablePrefix tableName tagsNames tagsValue，提供表和TAGS相关的配置 |
| TDengineFactor                 | 查询条件类，实体查询方式的sql条件都是通过该类解析出来的      |

##### 四、注解

| name                | decription                                                   |
| ------------------- | ------------------------------------------------------------ |
| @TDengineRepository | 此注解为组合注解，用于mapper接口的注解并引入@Repository, 使用注解查询的方式必须使用该注解 |
| @TDengineSelect     | 用于mapper接口的方法的注解，注解只接收一个参数 就是sql语句   |

##### 五、示例（查询集合只会返回List集合）

###### 实体查询（合法sql条件都可以随意组合）

```java
import io.shun.tdengine.entity.TDengineFactor;
import io.shun.tdengine.processor.TDengineModel;

/**
 * Created by shunZ on 2021/12/30 上午10:05
 */
public class TestService {
   @Autowired
    TDengineModel tDengineModel;
  
 
  	// 查询多个条记录 PointValue为自定义实体 TDengineFactor查询条件定义类, 查询条件聚合函数等都已String数组形式传入
    public List<PointValue> testExecuteQuery() {
              TDengineFactor tDengineFactor = TDengineFactor.builder()
//.tableName("")  // 不提供表名则以实体类名为表名 
                .startTime(1637251200000L) // 查询开始时间
                .endTime(1638201600000L) // 查询结束时间
              //.aggregationType(new String[]{"SUM(value)"}) // 聚合查询函数 如果不设置此项则将实体类所有属性解析为查询字段
              //.aggregationAlias(new String[]{"value"})// 指定查询函数时必须指定别名，否则会抛异常
              //.appendWhere(new String[]{"value = 230"}) // 附加查询条件 比如"id = 1"等，提示如果条件字段为字符串请定位 "value = 'str'" 
              //.interval(new String[]{"1d"}) // 统计间隔周期 请参考taos文档
              //.groupBy(new String[]{"ts"}) // 分组 注意: 分组和interval不能同时使用，请参考taos文档
              //.orderBy(new String[]{"ts"}) // 排序
              //.index(0) // 分页
              //.size(10)
              	.build();

        return tDengineModel.executeQuery(PointValue.class, tDengineFactor);
    }
  
  // 查询单条记录 类似多条查询 合法sql条件都可以随意组合
    public PointValue testExecuteQueryOne() {
          TDengineFactor tDengineFactor = TDengineFactor.builder()
            .tableName("c_hour_56865521584455_3_101")
            .aggregationType(new String[]{"SUM(value)"})
            .aggregationAlias(new String[]{"value"})
            .appendWhere(new String[]{"ts = 1637732618000"})
            .interval(new String[]{"1d"})
            .build();
      
      return tDengineModel.executeQueryOne(PointValue.class, tDengineFactor)
    }
}
```

###### 注解方式查询（注解的mapper接口只支持单个和多个参数的入参数，不支持对象形式入参）

```Java
import io.shun.tdengine.annotation.TDengineRepository;
import io.shun.tdengine.annotation.TDengineSelect;

/**
 * Created by shunZ on 2021/12/30 上午10:05
 * 示例注解mapper类
 * 方法参数名必须与sql中的参数名一致
 * 必须使用TDengineRepository注解 否则扫描不到mapper接口
 */

@TDengineRepository 
public interface TaskTestMapper {

    // 此注解也是必须加入的否则调用mapper方法会报 Annotation TDengineSelect Not Fount异常
    @TDengineSelect("select * from test.point_value") 
    List<PointValue> selectPointValueList();
  
  
    @TDengineSelect("select * from test.point_value where ts = #{ts} and remark = #{remark}")
    PointValue selectPointValue(Long ts, String remark);

		// in 参数 只支持 list集合及子类的类型传入
    @TDengineSelect("select * from test.point_value where ts = #{ts} and remark in (#{remarks})")
    List<PointValue>  selectPointValueByIn(Long ts, List<String> remarks);
}


/**
 * Created by shunZ on 2021/12/30 上午10:05
 * 调用示例
 */
public class TestService {
   @Autowired
    TaskTestMapper tDengineModel;
  	
    // 查询集合
    public List<PointValue> getList() { 
     List<PointValue> pointValues = testMapper.selectPointValues(1637251200000L,37.37);
    	// List<String> inParams = new ArrayList<String>(){{
      //add("测试1");
      //add("测试2");
      //add("测试3");
      //}};
      //List<PointValue> pointValues = testMapper.selectPointValueByIn(1640337642000L, inParams);
      
      return pointValues;
    } 
  
    // 查询单个对象
    public PointValue getOne() { 
       PointValue pvalue = testMapper.selectPointValue(1637251200000L,37.37) 
       return pvalue;
    } 
}

```

###### 插入数据

```java
import io.shun.tdengine.processor.TDengineModel;

/**
 * Created by shunZ on 2021/12/30 上午10:05
 */

public class TestService {
   @Autowired
    TDengineModel tDengineModel;
  
  // 单条插入  PointValue为自定义实体类 需要继承TDengineEntity类 该基类提供tableName tablePrefix tagsNames tagsValue 等设置方法
  public int insertPointValue() {
      PointValue pointValue = PointValue.builder()
      .tableName("test777_code_0077") // 表名如果不传则将实体类名作为表名 如果传则将实体类名解析超级表名 该表会根据继承超级表自动创建
      .tablePrefix("stat_") // 根据业务需要统计数据表的前缀 非必选项
      .tagsNames(new String[]{"aaa", "bbb"}) // 如果继承超级表 指定TAGS的数据插入 需设置 非必选项 请参开taos文档
      .tagsValue(new String[]{"5"}) // 如果继承超级表 必须设置此项  如果tagsNames被设置 tagsValue的值要与其一一对应
      .ts(1640337642000L)
      .value(37.17)
      .remark("测试1")
      .build();
    return tDengineModel.executeInsert(pointValue);
  } 
  
  // 多条插入
  public int insertPointValueList() { 
      PointValue pointValue = PointValue.builder()
          .tableName("test777_code_0077")
          .tablePrefix("test.")
          .tagsNames(new String[]{"aaa", "bbb"})
          .tagsValue(new String[]{"5"})
          .ts(1640337642000L)
          .value(37.17)
          .remark("测试1")
          .build();
      PointValue pointValue2 = PointValue.builder()
          .tableName("c_hour_56865521584455_3_101")
          .tablePrefix("test.")
          .tagsNames(new String[]{"aaa", "bbb"})
          .tagsValue(new String[]{"111", "222"})
          .ts(1640764177000L)
          .value(37.47)
          .remark("测试4")
          .build();
      PointValue pointValue3 = PointValue.builder()
          .tableName("c_hour_56865521584455_3_101")
          .tablePrefix("test.")
          .tagsNames(new String[]{"aaa", "bbb"})
          .tagsValue(new String[]{"111", "222"})
          .ts(1640337668000L)
          .value(37.37)
          .remark("测试3")
          .build();
    
    List<PointValue> list = new ArrayList<>();
    list.add(pointValue);
    list.add(pointValue2);
    list.add(pointValue3);
    
    return tDengineModel.executeInsert(list);
  }
}
```


