package com.example.borrowingbooks.common;

public class RedisKeyConstants {

    private RedisKeyConstants() {
        // 私有构造防止实例化
    }

    // 公共常量 ==============================================
    /**
     * 分布式锁前缀
     * 用法：String.format(DISTRIBUTED_LOCK, "order_operate", orderId)
     */
    public static final String DISTRIBUTED_LOCK = "LOCK:%s:%s";

    /**
     * 限流器前缀
     * 用法：String.format(RATE_LIMITER, "login_api", userId)
     */
    public static final String RATE_LIMITER = "RATE_LIMIT:%s:%s";

    // 用户模块 ==============================================
    public static final class User {
        /**
         * 用户信息
         * 格式：USER:INFO:1001
         * 值：用户JSON数据
         */
        public static final String INFO = "USER:INFO:%s";

        /**
         * 用户令牌
         * 格式：USER:TOKEN:abcdef12345
         */
        public static final String TOKEN = "USER:TOKEN:%s";

        /**
         * 用户权限列表
         * 格式：USER:PERM:1001
         */
        public static final String PERMISSION = "USER:PERM:%s";

        /**
         * 用户登录错误次数（防暴力破解）
         * 格式：USER:LOGIN_ERR_COUNT:admin
         */
        public static final String LOGIN_ERROR_COUNT = "USER:LOGIN_ERR_COUNT:%s";
    }

    // 订单模块 ==============================================
    public static final class Order {
        /**
         * 订单详情缓存
         * 格式：ORDER:DETAIL:2001
         */
        public static final String DETAIL = "ORDER:DETAIL:%s";

        /**
         * 用户订单列表
         * 格式：ORDER:LIST:1001
         */
        public static final String LIST = "ORDER:LIST:%s";

        /**
         * 订单状态锁
         * 格式：ORDER:STATUS_LOCK:2001
         */
        public static final String STATUS_LOCK = "ORDER:STATUS_LOCK:%s";
    }

    // 商品模块 ==============================================
    public static final class Product {
        /**
         * 商品详情缓存
         * 格式：PRODUCT:INFO:3001
         */
        public static final String INFO = "PRODUCT:INFO:%s";

        /**
         * 商品库存
         * 格式：PRODUCT:STOCK:3001
         */
        public static final String STOCK = "PRODUCT:STOCK:%s";

        /**
         * 秒杀商品标记
         * 格式：PRODUCT:SECKILL:20231001
         */
        public static final String SECKILL = "PRODUCT:SECKILL:%s";
    }

    // 系统级常量 ============================================
    public static final class System {
        /**
         * 全局配置
         * 格式：SYS:CONFIG:site_name
         */
        public static final String CONFIG = "SYS:CONFIG:%s";

        /**
         * 字典数据
         * 格式：SYS:DICT:gender
         */
        public static final String DICT = "SYS:DICT:%s";

        /**
         * 接口调用统计
         * 格式：SYS:API_STAT:/api/user/list
         */
        public static final String API_STATISTICS = "SYS:API_STAT:%s";
    }

    // 缓存穿透保护 ==========================================
    public static final class Protection {
        /**
         * 空值缓存（防缓存穿透）
         * 格式：PROTECTION:NULL:USER:INFO:1001
         */
        public static final String NULL_CACHE = "PROTECTION:NULL:%s";

        /**
         * 布隆过滤器
         * 格式：PROTECTION:BLOOM:USER_ID
         */
        public static final String BLOOM_FILTER = "PROTECTION:BLOOM:%s";
    }

    // 计数器类 ==============================================
    public static final class Counter {
        /**
         * 文章阅读量
         * 格式：COUNTER:ARTICLE_READ:4001
         */
        public static final String ARTICLE_READ = "COUNTER:ARTICLE_READ:%s";

        /**
         * 用户行为统计
         * 格式：COUNTER:USER_ACTION:1001:login
         */
        public static final String USER_ACTION = "COUNTER:USER_ACTION:%s:%s";
    }

    // 社交模块 ==============================================
    public static final class Social {
        /**
         * 用户关注列表
         * 格式：SOCIAL:FOLLOWING:1001
         */
        public static final String FOLLOWING = "SOCIAL:FOLLOWING:%s";

        /**
         * 用户粉丝列表
         * 格式：SOCIAL:FOLLOWERS:1001
         */
        public static final String FOLLOWERS = "SOCIAL:FOLLOWERS:%s";

        /**
         * 点赞记录
         * 格式：SOCIAL:LIKE:comment:5001
         */
        public static final String LIKE = "SOCIAL:LIKE:%s:%s";
    }

    // 会话管理 ==============================================
    public static final class Session {
        /**
         * WebSocket会话
         * 格式：SESSION:WS:1001
         */
        public static final String WEBSOCKET = "SESSION:WS:%s";

        /**
         * 临时会话凭证
         * 格式：SESSION:TEMP:upload_12345
         */
        public static final String TEMPORARY = "SESSION:TEMP:%s";
    }
}
