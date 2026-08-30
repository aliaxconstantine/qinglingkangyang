package com.aliax.qlky.config.appconfig.configuration;

import org.springframework.beans.factory.annotation.Value;


public class RedisRedissonConfiguration {

    @Value("${redisson.address}")
    private String address;

    @Value("${redisson.password}")
    private String password;

    @Value("${redisson.connectionPoolSize}")
    private int connectionPoolSize;

    @Value("${redisson.connectionMinimumIdleSize}")
    private int connectionMinimumIdleSize;

    @Value("${redisson.idleConnectionTimeout}")
    private int idleConnectionTimeout;

    @Value("${redisson.connectTimeout}")
    private int connectTimeout;

    @Value("${redisson.timeout}")
    private int timeout;

    @Value("${redisson.retryAttempts}")
    private int retryAttempts;

    @Value("${redisson.retryInterval}")
    private int retryInterval;

    @Value("${redisson.subscriptionsPerConnection}")
    private int subscriptionsPerConnection;

    @Value("${redisson.clientName}")
    private String clientName;

    @Value("${redisson.database}")
    private int database;

    @Value("${redisson.dnsMonitoringInterval}")
    private int dnsMonitoringInterval;

    @Value("${redisson.threads}")
    private int threads;

    @Value("${redisson.nettyThreads}")
    private int nettyThreads;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public int getConnectionMinimumIdleSize() {
        return connectionMinimumIdleSize;
    }

    public void setConnectionMinimumIdleSize(int connectionMinimumIdleSize) {
        this.connectionMinimumIdleSize = connectionMinimumIdleSize;
    }

    public int getIdleConnectionTimeout() {
        return idleConnectionTimeout;
    }

    public void setIdleConnectionTimeout(int idleConnectionTimeout) {
        this.idleConnectionTimeout = idleConnectionTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public int getSubscriptionsPerConnection() {
        return subscriptionsPerConnection;
    }

    public void setSubscriptionsPerConnection(int subscriptionsPerConnection) {
        this.subscriptionsPerConnection = subscriptionsPerConnection;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public int getDnsMonitoringInterval() {
        return dnsMonitoringInterval;
    }

    public void setDnsMonitoringInterval(int dnsMonitoringInterval) {
        this.dnsMonitoringInterval = dnsMonitoringInterval;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getNettyThreads() {
        return nettyThreads;
    }

    public void setNettyThreads(int nettyThreads) {
        this.nettyThreads = nettyThreads;
    }
}
