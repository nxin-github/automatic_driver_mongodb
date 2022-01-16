package com.liang.api;

public interface OperateDocument {
    void createDocument(String dbName, String collName, String content);

    boolean deleteDocument(String dbName, String collName, String condition);

    boolean updateDocument(String dbName, String collName, String condition, String content);
}
