package com.liang.config;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * @author ：宁鑫
 * @date ：2022/1/19 21:52
 * @description：参数对象
 */
@Data
@Builder
public class ParameterObject {
    @Tolerate
    ParameterObject() {

    }
    //等级,database,collection,document,field
    private String terget;

    //目标名字
    private String tergetname;

    //操作种类,create,drop,update
    private String operation;

    //内容
    private String content;

    //条件
    private String condition;

    //数据库名
    private String dbName;
}
