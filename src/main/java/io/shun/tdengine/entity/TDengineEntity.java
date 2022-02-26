package io.shun.tdengine.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @ClassName: TDengineEntity
 * @Description: TD数据对象基类
 * @Author: ShunZ
 * @CreatedAt: 2021-12-23 17:35
 * @Version: 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class TDengineEntity {

    // 表前缀
    private String tablePrefix;

    // 表名
    private String tableName;

    // 标签名称 针对于指定某个标签插入数据
    private String[] tagsNames;

    // 标签值
    private String[] tagsValue;


}
