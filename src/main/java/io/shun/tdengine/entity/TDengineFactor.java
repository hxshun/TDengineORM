package io.shun.tdengine.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @ClassName: TDengineFactor
 * @Description: 查询条件
 * @Author: ShunZ
 * @CreatedAt: 2021-12-03 14:53
 * @Version: 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class TDengineFactor extends TDengineEntity{
    // 开始时间
    private Long startTime;

    // 结束时间
    private Long endTime;

    // 聚合方式
    private String[] aggregationType;

    // 聚合别名
    private String[] aggregationAlias;

    // 附加条件
    private String[] appendWhere;

    // 统计周期
    private String[] interval;

    // 分组
    private String[] groupBy;

    // 排序
    private String[] orderBy;

    // 排序类型
    private String orderByType;

    // 分页
    private Integer index;

    private Integer size;


}
