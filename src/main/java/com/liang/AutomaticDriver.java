package com.liang;

import com.liang.api.impl.OperateCollectionImpl;
import com.liang.api.impl.OperateDatabaseImpl;
import com.liang.api.impl.OperateDocumentImpl;
import com.liang.config.ParameterObject;
import com.liang.internal.AutomaticDriveException;
import com.liang.internal.MongoUtil;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author ：宁鑫
 * @date ：2022/1/13 22:24
 * @description：
 */
@Service
public class AutomaticDriver {
    private final String AUTOMATIC_DRIVE_DOCUMENTATION = "automatic_drive";
    private final String FOLDER = "classpath*:/mongofile/*.properties";
    private boolean dropDatabase = false;
    @Autowired
    private MongoUtil mongoUtil;

    /**
     * @描述 初始化方法
     */
    public void init(String uriNullable, String ip, Integer prot) throws IOException {
        if (mongoUtil == null) {
            mongoUtil = new MongoUtil();
        }
        //获取连接
        mongoUtil.getMongoClient(uriNullable, ip, prot);
        System.out.println("automatic_driver_mongodb:获取到了连接");
        //获取所有文件
        Resource[] resourcesDocuments = new PathMatchingResourcePatternResolver().getResources(FOLDER);
        if (resourcesDocuments.length == 0) {
            return;
        }
        //对文件进行排序
        Resource[] resources = sortDocuments(resourcesDocuments);

        Properties properties = new Properties();
        for (Resource resource : resources) {
            ParameterObject documentMessage = getDocumentMessage(resource, properties);
            String dbName = documentMessage.getDbName();
            // 获取工具表
            OperateDocumentImpl operateDocument = new OperateDocumentImpl();
            Document automaticDrive = operateDocument.getDocument(dbName, AUTOMATIC_DRIVE_DOCUMENTATION, null).first();
            // 获取已经执行的版本集合
            String version;
            if (automaticDrive == null) {
                version = null;
            } else {
                version = (String) automaticDrive.get("version");
            }
            // 获取当前文件版本号
            int indexOf = StringUtils.ordinalIndexOf(resource.getFilename(), "-", 1);
            String execteVersion = StringUtils.substring(resource.getFilename(), 0, indexOf);
            if (version != null) {
                assert execteVersion != null;
                if (version.compareTo(execteVersion)>0) {
                    continue;
                }

            }
            // 执行这些文件
            execteDocument(documentMessage, resource.getFilename());
        }

        mongoUtil.close();
    }

    /**
     *@描述   对文件数组进行排序
     *@参数  org.springframework.core.io.Resource[]
     *@返回值  org.springframework.core.io.Resource[]
     */
    private Resource[] sortDocuments(Resource[] resources) {
        int len = resources.length;
        int preIndex;
        Resource current;

        for (int i = 1; i < len; i++) {
            preIndex = i - 1;
            current = resources[i];
            while (preIndex >= 0 && Objects.requireNonNull(resources[preIndex].getFilename()).compareTo(Objects.requireNonNull(current.getFilename())) > 0) {
                resources[preIndex + 1] = resources[preIndex];
                preIndex--;
            }
            resources[preIndex + 1] = current;
        }
        return resources;
    }

    /**
     *@描述  获取执行文件内部信息
     *@参数  com.liang.config.ParameterObject
     *@返回值  com.liang.config.ParameterObject
     */
    private ParameterObject getDocumentMessage(Resource resource, Properties properties) throws IOException {
        // 使用properties对象加载输入流
        properties.load(new InputStreamReader(resource.getInputStream(), "UTF-8"));
        return ParameterObject.builder()
                .dbName(properties.getProperty("dbName"))
                .condition(properties.getProperty("condition"))
                .content(properties.getProperty("content"))
                .operation(properties.getProperty("operation"))
                .terget(properties.getProperty("terget"))
                .tergetname(properties.getProperty("tergetname"))
                .build();
    }

    /**
     *@描述  加载执行文件
     *@参数  void
     *@返回值  void
     */
    private void execteDocument(ParameterObject parameterObject, String fileName) {
        dropDatabase = false;
            // 执行文件
        executeFile(parameterObject);
        if (!dropDatabase) {
            // 更新工具表
            int indexOf = StringUtils.ordinalIndexOf(fileName, "-", 1);
            String execteVersion = StringUtils.substring(fileName, 0, indexOf);
            OperateDocumentImpl operateDocument = new OperateDocumentImpl();
            String utilFileContent = "{\"version\":\"" + execteVersion + "\",\"scriptName\":\"" + fileName + "\",\"executionTime\":\"" + System.currentTimeMillis() + "\",\"executionDate\":\"" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + "\"}";
            operateDocument.createDocument(parameterObject.getDbName(), AUTOMATIC_DRIVE_DOCUMENTATION, utilFileContent);
            System.out.println("automatic_driver_mongodb：更新工具表");
        }
    }

    /**
     *@描述 判断需要执行的操作
     *@参数 String terget 目标级别,database,collection,document,field
     *     String operation 增删改查,create,drop,update
     *     String content {内容}
     *     String condition {筛选条件}
     *     String tergetname 目标内容,名字
     *@返回值
     */
    private void executeFile(ParameterObject documentMessage) {
        if (documentMessage.getTerget() == null) {
            throw new AutomaticDriveException("Terget cannot be empty");
        }

        switch (documentMessage.getTerget()) {
            // 数据库操作
            case "database":
                OperateDatabaseImpl operateDatabase = new OperateDatabaseImpl();
                switch (documentMessage.getOperation()) {
                    case "create":
                        operateDatabase.creatDatabase(documentMessage.getTergetname());
                        return;
                    case "drop":
                        operateDatabase.dropDB(documentMessage.getTergetname());
                        dropDatabase = true;
                        return;
                    case "update":
                        throw new AutomaticDriveException("The database cannot be renamed");
                    default:
                        throw new AutomaticDriveException("operation type erro");
                }
            // 集合操作
            case "collection":
                OperateCollectionImpl operateCollection = new OperateCollectionImpl();
                switch (documentMessage.getOperation()) {
                    case "create":
                        operateCollection.createCollection(documentMessage.getDbName(), documentMessage.getTergetname(), documentMessage.getContent());
                        return;
                    case "drop":
                        operateCollection.deleteCollection(documentMessage.getDbName(), documentMessage.getTergetname());
                        return;
                    case "update":
                        throw new AutomaticDriveException("The collection cannot be renamed");
                    default:
                        throw new AutomaticDriveException("operation type erro");
                }
            case "document":
                OperateDocumentImpl operateDocument = new OperateDocumentImpl();
                switch (documentMessage.getOperation()) {
                    case "create":
                        operateDocument.createDocument(documentMessage.getDbName(), documentMessage.getTergetname(), documentMessage.getContent());
                        return;
                    case "drop":
                        operateDocument.deleteDocument(documentMessage.getDbName(), documentMessage.getTergetname(), documentMessage.getCondition());
                        return;
                    case "update":
                        operateDocument.updateDocument(documentMessage.getDbName(), documentMessage.getTergetname(), documentMessage.getCondition(), documentMessage.getContent());
                        return;
                    default:
                        throw new AutomaticDriveException("operation type erro");
                }
            case "field":
                switch (documentMessage.getOperation()) {
                    case "create":
                        throw new AutomaticDriveException("The field cannot be create");
                    case "drop":
                        throw new AutomaticDriveException("The field cannot be drop");
                    case "update":
                        throw new AutomaticDriveException("The field cannot be update");
                    default:
                        throw new AutomaticDriveException("operation type erro");
                }
            default:
                throw new AutomaticDriveException("terget type erro");
        }
    }
}
