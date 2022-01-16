package com.liang.api.impl;

import com.liang.api.OperateCollection;
import com.liang.internal.AutomaticDriveException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * @author ：宁鑫
 * @date ：2022/1/15 21:20
 * @description：
 */
public class OperateCollectionImpl implements OperateCollection {
    @Override
    public MongoCollection<Document> createCollection(String dbName, String collName, String content) {
        if (null == collName || "".equals(collName)) {
            throw new AutomaticDriveException("Collection name cannot be empty");
        }
        if (null == dbName || "".equals(dbName)) {
            throw new AutomaticDriveException("Database name cannot be empty");
        }
        MongoCollection<Document> collection = getCollection(dbName, collName);

        OperateDocumentImpl operateDocument = new OperateDocumentImpl();
        operateDocument.createDocument(dbName, collName, content);
        return collection;
    }

    @Override
    public void deleteCollection(String dbName, String collName) {
        getCollection(dbName, collName).drop();
    }


    public static MongoCollection<Document> getCollection(String dbName, String collName) {
        if (null == collName || "".equals(collName)) {
            throw new AutomaticDriveException("Collection name cannot be empty");
        }
        if (null == dbName || "".equals(dbName)) {
            throw new AutomaticDriveException("Database name cannot be empty");
        }
        return OperateDatabaseImpl.getDB(dbName).getCollection(collName);
    }
}
