package com.aliax.qlky.bean.qlky;

import com.aliax.qlky.entity.CrawlerSystemMessage;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.time.Duration;
import java.util.List;

public class PCSystemBean {
    private String systemName;    // 系统名称（自定义）
    private String systemUrl;    // 系统监控地址
    private String systemDesc;    // 系统描述

    // 新增系统指标字段
    private String osName;        // 操作系统名称
    private String osVersion;    // 操作系统版本
    private int cpuCores;        // CPU核心数
    private double cpuUsage;     // CPU使用率（百分比）
    private long totalMemory;    // 总内存（单位：MB）
    private long usedMemory;     // 已用内存（单位：MB）
    private long freeMemory;     // 空闲内存（单位：MB）
    private long diskTotal;      // 磁盘总空间（单位：GB）
    private long diskFree;       // 磁盘剩余空间（单位：GB）
    private String uptime;       // 系统运行时间

    private List<CrawlerSystemMessage> crawlerSystemMessageList;


    // 通过静态方法获取系统指标并填充对象
    public static PCSystemBean getSystemInfo() {
        PCSystemBean bean = new PCSystemBean();

        // 获取操作系统信息
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        bean.setOsName(osBean.getName());
        bean.setOsVersion(osBean.getVersion());
        bean.setCpuCores(osBean.getAvailableProcessors());

        // 获取内存信息（JVM内存）
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        bean.setTotalMemory(heapUsage.getCommitted() / (1024 * 1024));
        bean.setUsedMemory(heapUsage.getUsed() / (1024 * 1024));
        bean.setFreeMemory((heapUsage.getCommitted() - heapUsage.getUsed()) / (1024 * 1024));

        // 获取磁盘信息（示例取根目录磁盘）
        File root = new File("/");
        bean.setDiskTotal(root.getTotalSpace() / (1024 * 1024 * 1024));
        bean.setDiskFree(root.getFreeSpace() / (1024 * 1024 * 1024));

        // 计算系统运行时间
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        Duration duration = Duration.ofMillis(uptimeMs);
        bean.setUptime(String.format("%d天 %d小时 %d分钟",
                duration.toDays(),
                duration.toHours() % 24,
                duration.toMinutes() % 60));

        return bean;
    }

    public List<CrawlerSystemMessage> getCrawlerSystemMessageList() {
        return crawlerSystemMessageList;
    }

    public void setCrawlerSystemMessageList(List<CrawlerSystemMessage> crawlerSystemMessageList) {
        this.crawlerSystemMessageList = crawlerSystemMessageList;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemUrl() {
        return systemUrl;
    }

    public void setSystemUrl(String systemUrl) {
        this.systemUrl = systemUrl;
    }

    public String getSystemDesc() {
        return systemDesc;
    }

    public void setSystemDesc(String systemDesc) {
        this.systemDesc = systemDesc;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getDiskTotal() {
        return diskTotal;
    }

    public void setDiskTotal(long diskTotal) {
        this.diskTotal = diskTotal;
    }

    public long getDiskFree() {
        return diskFree;
    }

    public void setDiskFree(long diskFree) {
        this.diskFree = diskFree;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }
}
