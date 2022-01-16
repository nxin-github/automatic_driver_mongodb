package com.liang.api;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public interface OperateCollection {
    MongoCollection<Document> createCollection(String dbName, String collName, String content);

    void deleteCollection(String dbName, String collName);
}
