package com.liang.api;

import com.mongodb.client.MongoDatabase;

public interface OperateDatabase {
    void dropDB(String dbName);

    MongoDatabase creatDatabase(String dbName);
}
