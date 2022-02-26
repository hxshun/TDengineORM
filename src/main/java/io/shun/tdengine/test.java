package io.shun.tdengine;


import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName: test
 * @Description:
 * @Author: ShunZ
 * @CreatedAt: 2021-11-30 14:52
 * @Version: 1.0
 **/
@RunWith(SpringRunner.class) //添加
@SpringBootTest
public class test {

//    @Autowired
//    TDengineModelImpl tDengineModel;
//
//    @Autowired
//    TestMapper testMapper;
//
//    @Test
//    public void test1() {
//
////        List<PointValue> list = testMapper.selectPointValueList();
//
////        PointValue p = testMapper.selectPointValue(1637251200000L,37.37);
////        PointValue p = testMapper.selectPointValue(1640337642000L,"测试1");
////        List<PointValue> p = testMapper.selectPointValues(1637251200000L,37.37);
//
//          List<String> inParams = new ArrayList<String>(){{
//              add("测试1");
//              add("测试2");
//              add("测试3");
//          }};
//
//          List<PointValue> pointValues = testMapper.selectPointValueByIn(1640337642000L, inParams);
//
//        String a = "";
//    }
//
//
//    @Test
//    public void test() {
////        System.out.println(TDen.camelToUnderline("adminAdminParam", false));
//
////        System.out.println(String.valueOf(null));
////        TDengineFactor tDengineFactor = TDengineFactor.builder()
////                .tableName("c_hour_56865521584455_3_101")
////                .startTime(1637251200000L)
////                .endTime(1638201600000L)
////                .aggregationType(new String[]{"SUM(value)"})
////                .aggregationAlias(new String[]{"value"})
////                .appendWhere(new String[]{"value = 230"})
////                .interval(new String[]{"1d"})
////                .groupBy(new String[]{"ts"})
////                .orderBy(new String[]{"ts"})
////                .index(0)
////                .size(10)
////                .build();
////        "hour_56865521584455_3_101"
////        List<PointValue> list = tDengineModel.executeQuery(PointValue.class, tDengineFactor);
////
////        TDengineFactor tDengineFactor2 = TDengineFactor.builder()
////                .tableName("c_hour_56865521584455_3_101")
//////                .aggregationType(new String[]{"SUM(value)"})
//////                .aggregationAlias(new String[]{"value"})
////                .appendWhere(new String[]{"ts = 1637732618000"})
//////                .interval(new String[]{"1d"})
////                .build();
//////
////        PointValue pointValue = tDengineModel.executeQueryOne(PointValue.class, tDengineFactor2);
////        String aaaaa = "asdas";
//
////          PointValue pointValue = PointValue.builder()
////                  .tableName("test777_code_0077")
////                  .tablePrefix("test.")
////                  .tagsNames(new String[]{"aaa", "bbb"})
////                  .tagsValue(new String[]{"5"})
////                  .ts(1640337642000L)
////                  .value(37.17)
////                  .remark("测试1")
////                  .build();
//        PointValue pointValue2 = PointValue.builder()
////                  .tableName("c_hour_56865521584455_3_101")
////                   .tablePrefix("test.")
////                  .tagsNames(new String[]{"aaa", "bbb"})
////                .tagsValue(new String[]{"111", "222"})
//                .ts(1640764177000L)
//                .value(37.47)
//                .remark("测试4")
//                .build();
////        PointValue pointValue3 = PointValue.builder()
//////                  .tableName("c_hour_56865521584455_3_101")
////                   .tablePrefix("test.")
//////                  .tagsNames(new String[]{"aaa", "bbb"})
//////                .tagsValue(new String[]{"111", "222"})
////                .ts(1640337668000L)
////                .value(37.37)
////                .remark("测试3")
////                .build();
//////
//          int a = tDengineModel.executeInsert(pointValue2);
////
////
////
////            List<PointValue> list = new ArrayList<>();
////            list.add(pointValue);
////            list.add(pointValue2);
////            list.add(pointValue3);
////
////        int a = tDengineModel.executeInsert(list);
//
//    }

}
