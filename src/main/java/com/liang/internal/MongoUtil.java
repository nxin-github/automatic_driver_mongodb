package com.liang.internal;

import com.mongodb.MongoClientURI;
import org.apache.commons.lang3.StringUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.WriteConcern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MongoUtil {
    @Value("${spring.data.mongodb.uri}")
    private String mongdbURI;
    private String ip;
    private int port;
    private String username;
    private String password;
    private String db;

    public static MongoClient mongoClient;

    public void getMongoClient(String uri, String address, Integer prot) {
        if (StringUtils.isNotBlank(ip) && this.port != 0) {
            mongoClient = new MongoClient(ip, port);
        }
        //如果注入了uri
        if (StringUtils.isNotBlank(mongdbURI)) {
            mongoClient = new MongoClient(new MongoClientURI(mongdbURI));
        }
        if (StringUtils.isNotBlank(address) && prot != null) {
            ip = address;
            port = prot;
            mongoClient = new MongoClient(ip, port);
        }
        if (StringUtils.isNotBlank(uri)) {
            mongoClient = new MongoClient(new MongoClientURI(uri));
        }

        Builder options = new MongoClientOptions.Builder();
        options.connectionsPerHost(300);// 连接池设置为300个连接,默认为100
        options.connectTimeout(30000);// 连接超时，推荐>3000毫秒
        options.maxWaitTime(5000); //
        options.socketTimeout(0);// 套接字超时时间，0无限制
        options.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
        options.writeConcern(WriteConcern.SAFE);//
        options.build();
    }

    /**
     * 关闭Mongodb
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}