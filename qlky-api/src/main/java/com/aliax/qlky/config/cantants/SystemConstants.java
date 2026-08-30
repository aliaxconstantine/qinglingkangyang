package com.aliax.qlky.config.cantants;

/**
 * @author 艾莉希雅
 */
public class SystemConstants {
    public static final int SAVE_MESSAGE_TIME = 1000 * (60 * 60 * 24);
    public static final String USER_NICK_NAME_PREFIX = "user_";
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE_MESSAGE = 15;

    //上传文件地址
    public static final String FILE_UPLOAD_DIR = "D:/code/qlky-project/file";


    public static final String IMAGE_UPLOAD_DIR = "D:/code/qlky-project/file/image";

    public static final String CRAWLER_UPLOAD_DIR = FILE_UPLOAD_DIR + "/crawler";

    /**
     * 默认登录用户账号
     */
    public static final String DEFAULT_LOGIN_USER_ACCOUNT = "qlkymain";

    /**
     * 默认登录用户密码
     */
    public static final String DEFAULT_LOGIN_USER_PASSWORD = "admin123";

    /**
     * 表示默认活跃状态
     */
    public static final String ACTIVE_STATE  = "1";
    /**
     * 表示默认不活跃状态
     */
    public static final String INACTIVE_STATE = "0";

    public static final String ERROR_MESSAGE = "系统错误，请稍后重试";
}
