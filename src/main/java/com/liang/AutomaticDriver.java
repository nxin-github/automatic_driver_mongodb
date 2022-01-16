package com.liang;

import com.liang.api.impl.OperateCollectionImpl;
import com.liang.api.impl.OperateDatabaseImpl;
import com.liang.api.impl.OperateDocumentImpl;
import com.liang.internal.AutomaticDriveException;
import com.liang.internal.MongoUtil;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author ：宁鑫
 * @date ：2022/1/13 22:24
 * @description：
 */
@Service
public class AutomaticDriver {
    private final String AUTOMATIC_DRIVE_DOCUMENTATION = "automatic_drive";
    private final String FOLDER = "mongofile/*";
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
        //处理每个文件夹
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(FOLDER);
        if (resources.length == 0) {
            return;
        }

        for (Resource resource : resources) {
            List<String> needExecuteList;
            Map<String, Object> stringArrayListMap = processFile(resource);
            // 获取数据库名
            String dbName = (String) stringArrayListMap.get("fileName");
            //
            String path = (String) stringArrayListMap.get("filePath");
            //文件夹下所有文件
            ArrayList<String> allDocuments = (ArrayList<String>) stringArrayListMap.get("resultArray");

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
            if (version != null) {
                needExecuteList = filterExecutableFiles(version, allDocuments);
            } else {
                Collections.sort(allDocuments);
                Collections.reverse(allDocuments);
                needExecuteList = allDocuments;
            }
            // 执行这些文件
            execteDocument(dbName, path, needExecuteList);
        }

        mongoUtil.close();
    }

    /**
     *@描述  加载执行文件
     *@参数  void
     *@返回值  void
     */
    private void execteDocument(String dbName ,String path, List<String> needExecuteList) {
        Properties properties = new Properties();
        for (String fileName : needExecuteList) {
            dropDatabase = false;
            try {
                // 使用ClassLoader加载properties配置文件生成对应的输入流
                InputStream in = AutomaticDriver.class.getClassLoader().getResourceAsStream(path + "\\" + fileName);
                // 使用properties对象加载输入流
                properties.load(in);
                String terget = (String) properties.get("terget");
                String tergetname = (String) properties.get("tergetname");
                String operation = (String) properties.get("operation");
                String content = (String) properties.get("content");
                String condition = (String) properties.get("condition");
                // 执行文件
                executeFile(dbName, terget, operation, content, condition, tergetname);
                if (!dropDatabase) {
                    // 更新工具表
                    int indexOf = StringUtils.ordinalIndexOf(fileName, "-", 1);
                    String execteVersion = StringUtils.substring(fileName, 0, indexOf);
                    OperateDocumentImpl operateDocument = new OperateDocumentImpl();
                    String utilFileContent = "{\"version\":\"" + execteVersion + "\",\"scriptName\":\"" + fileName + "\",\"executionTime\":\"" + System.currentTimeMillis() + "\"}";
                    operateDocument.createDocument(dbName, AUTOMATIC_DRIVE_DOCUMENTATION, utilFileContent);
                }
            } catch (IOException e) {
                throw new AutomaticDriveException("AutomaticDiver：properties load inputStream error", e);
            }
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
    private void executeFile(String dbName ,
                             String terget ,
                             String operation,
                             String content,
                             String condition,
                             String tergetname) {
        if (terget == null) {
            throw new AutomaticDriveException("Terget cannot be empty");
        }
        switch (terget) {
            // 数据库操作
            case "database":
                OperateDatabaseImpl operateDatabase = new OperateDatabaseImpl();
                switch (operation) {
                    case "create":
                        operateDatabase.creatDatabase(tergetname);
                        return;
                    case "drop":
                        operateDatabase.dropDB(tergetname);
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
                switch (operation) {
                    case "create":
                        operateCollection.createCollection(dbName, tergetname, content);
                        return;
                    case "drop":
                        operateCollection.deleteCollection(dbName, tergetname);
                        return;
                    case "update":
                        throw new AutomaticDriveException("The collection cannot be renamed");
                    default:
                        throw new AutomaticDriveException("operation type erro");
                }
            case "document":
                OperateDocumentImpl operateDocument = new OperateDocumentImpl();
                switch (operation) {
                    case "create":
                        operateDocument.createDocument(dbName, tergetname, content);
                        return;
                    case "drop":
                        operateDocument.deleteDocument(dbName, tergetname, condition);
                        return;
                    case "update":
                        operateDocument.updateDocument(dbName, tergetname, condition, content);
                        return;
                    default:
                        throw new AutomaticDriveException("operation type erro");
                }
            case "field":
                switch (operation) {
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

    /**
     *@描述   筛选掉不需要执行的文件
     *@参数  java.util.List<java.lang.String>
     *@返回值  java.util.List<java.lang.String>
     */
    private List<String> filterExecutableFiles(String version, ArrayList<String> allDocuments) {
        allDocuments.add(version);
        Collections.sort(allDocuments);
        Collections.reverse(allDocuments);
        int indexOfVersion = allDocuments.indexOf(version);
        if (indexOfVersion <= 1) {
            return allDocuments.subList(0, 0);
        }
        return allDocuments.subList(0, indexOfVersion - 1);
    }

    /**
     * @描述 处理文件夹下的子文件
     * @参数 java.util.ArrayList<java.io.InputStream>
     * @返回值 java.util.ArrayList<java.io.InputStream>
     */
    private static Map<String, Object> processFile(Resource resource) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        ArrayList<String> subFileNameList = new ArrayList<>();

        File file = new File(resource.getURI());
        String filePath = file.getPath();
        String fileName = file.getName();
        String path = StringUtils.substringAfter(filePath, "classes\\");

        //如果是一个文件夹
        if (file.isDirectory()) {
            String[] subFlieList = file.list();
            assert (subFlieList != null);
            for (String subFileName : subFlieList) {
                subFileNameList.add(subFileName);
            }
        } else {
            subFileNameList.add(fileName);
        }
        resultMap.put("fileName", fileName);
        resultMap.put("resultArray", subFileNameList);
        resultMap.put("filePath", path);
        return resultMap;
    }
}
