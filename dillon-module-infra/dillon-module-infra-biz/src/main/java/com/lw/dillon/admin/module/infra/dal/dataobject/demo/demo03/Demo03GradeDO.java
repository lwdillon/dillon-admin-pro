package com.lw.dillon.admin.module.infra.dal.dataobject.demo.demo03;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lw.dillon.admin.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 学生班级 DO
 *
 * @author 芋道源码
 */
@TableName("dillon_demo03_grade")
@KeySequence("dillon_demo03_grade_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Demo03GradeDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 学生编号
     */
    private Long studentId;
    /**
     * 名字
     */
    private String name;
    /**
     * 班主任
     */
    private String teacher;

}