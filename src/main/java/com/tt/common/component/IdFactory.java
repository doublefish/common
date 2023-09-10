package com.tt.common.component;


import com.tt.common.util.IdUtils;
import lombok.var;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * IdFactory
 *
 * @author Shuang Yu
 */
public class IdFactory extends IdFactoryBase {

    protected final JdbcTemplate jdbcTemplate;

    /**
     * 构造函数
     */
    public IdFactory(StringRedisTemplate redisTemplate, JdbcTemplate jdbcTemplate) {
        super(redisTemplate);
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * getId
     *
     * @param key     key
     * @param hashKey hashKey
     * @param delta   delta
     * @param table   table
     * @return startId
     */
    public long getId(String key, String hashKey, long delta, String table) {
        return IdUtils.getId(redisTemplate, key, hashKey, delta, this::getMaxId, table);
    }

    protected long getMaxId(String table) {
        var sql = String.format("SELECT MAX(id) FROM %s", table);
        var res = jdbcTemplate.queryForObject(sql, Long.class);
        return res != null ? res : 0;
    }

}