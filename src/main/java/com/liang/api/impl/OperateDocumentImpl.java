package com.liang.api.impl;

import com.liang.api.OperateDocument;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import static com.mongodb.client.model.Sorts.descending;


/**
 * @author ：宁鑫
 * @date ：2022/1/14 17:53
 * @description：针对表操作
 */
public class OperateDocumentImpl implements OperateDocument {

    @Override
    public void createDocument(String dbName, String collName, String content) {
        Document document = handleContent(content);
        OperateCollectionImpl.getCollection(dbName, collName).insertOne(document);
    }

    @Override
    public boolean deleteDocument(String dbName, String collName, String condition) {
        Bson bson = handleCondition(condition);
        DeleteResult deleteResult = OperateCollectionImpl.getCollection(dbName, collName).deleteMany(bson);
        return deleteResult.wasAcknowledged();
    }

    @Override
    public boolean updateDocument(String dbName, String collName, String condition, String content) {
        Bson bson = handleCondition(condition);
        Document document = handleContent(content);
        UpdateResult updateResult = OperateCollectionImpl.getCollection(dbName, collName).updateMany(bson, new Document("$set", document));
        return updateResult.wasAcknowledged();
    }

    public FindIterable<Document> getDocument(String dbName, String collName, String condition) {
        MongoCollection<Document> collection = OperateCollectionImpl.getCollection(dbName, collName);

        Bson bson = handleCondition(condition);
        if (bson == null) {
            return collection.find().sort(descending("executionTime"));
        } else {
            return collection.find(bson).sort(descending("executionTime"));
        }
    }

    private static Bson handleCondition(String condition) {
        if (StringUtils.isBlank(condition)) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(condition);
        Map<String, Object> conditionMap = jsonObject.toMap();
        Bson[] bsons = new Bson[conditionMap.size()];
        Iterator<Map.Entry<String, Object>> iterator = conditionMap.entrySet().iterator();

        for (int i = 0; i < conditionMap.size(); i++) {
            if (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                String key = next.getKey();
                Object value = next.getValue();
                bsons[i] = Filters.eq(key, value);
            }
        }
        return Filters.and(bsons);
    }

    private static Document handleContent(String content) {
        Document document = new Document();
        JSONObject jsonObject = new JSONObject(content);
        Map<String, Object> conditionMap = jsonObject.toMap();
        Iterator<Map.Entry<String, Object>> iterator = conditionMap.entrySet().iterator();

        for (int i = 0; i < conditionMap.size(); i++) {
            if (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                String key = next.getKey();
                Object value = next.getValue();
                document.put(key, value);
            }
        }
        return document;
    }
}
