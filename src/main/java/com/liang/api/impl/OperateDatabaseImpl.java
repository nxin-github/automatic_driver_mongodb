package com.liang.api.impl;

import com.liang.api.OperateDatabase;
import com.liang.internal.AutomaticDriveException;
import com.liang.internal.MongoUtil;
import com.mongodb.client.MongoDatabase;

/**
 * @author ：宁鑫
 * @date ：2022/1/14 16:35
 * @description：针对数据库的操作
 */
public class OperateDatabaseImpl implements OperateDatabase {

    public MongoDatabase creatDatabase(String dbName) {
        try {
            MongoDatabase db = getDB(dbName);
            return db;
        } catch (Exception e) {
            throw new AutomaticDriveException("create database erro", e);
        }
    }

    @Override
    public void dropDB(String dbName) {
        getDB(dbName).drop();
    }

    public static MongoDatabase getDB(String dbName) {
        if (dbName != null && !"".equals(dbName)) {
            MongoDatabase database = MongoUtil.mongoClient.getDatabase(dbName);
            return database;
        }
        return null;
    }
}
