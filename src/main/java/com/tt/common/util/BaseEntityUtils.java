package com.tt.common.util;

import com.tt.common.User;
import com.tt.common.model.Entity;
import lombok.var;

import java.time.Instant;
import java.util.Collection;

/**
 * BaseEntityUtils
 *
 * @author Shuang Yu
 */
public class BaseEntityUtils {

    private BaseEntityUtils() {
    }

    /**
     * 设置创建信息
     *
     * @param <P>    P
     * @param <T>    T
     * @param entity 实体
     * @param user   用户
     */
    public static <P, T extends Entity<P>> void setCreateInfo(T entity, User user) {
        setCreateInfo(entity, user, Instant.now());
    }

    /**
     * 设置创建信息
     *
     * @param <P>    P
     * @param <T>    T
     * @param entity 实体
     * @param user   用户
     * @param date   时间
     */
    public static <P, T extends Entity<P>> void setCreateInfo(T entity, User user, Instant date) {
        entity.setRowVersion(0);
        entity.setRowCreateDate(date);
        entity.setRowCreateUser(user.getId());
        entity.setRowCreateUsername(user.getName());
        entity.setRowUpdateDate(date);
        entity.setRowUpdateUser(user.getId());
        entity.setRowUpdateUsername(user.getName());
        entity.setRowDeleted(false);
        entity.setRowDeletedDate(Instant.EPOCH);
        entity.setRowDeletedUser("");
    }

    /**
     * 设置修改信息
     *
     * @param <P>      P
     * @param <T>      T
     * @param entities 实体
     * @param user     用户
     */
    public static <P, T extends Entity<P>> void setCreateInfo(Collection<T> entities, User user) {
        setCreateInfo(entities, user, Instant.now());
    }

    /**
     * 设置修改信息
     *
     * @param <P>      P
     * @param <T>      T
     * @param entities 实体
     * @param user     用户
     * @param date     时间
     */
    public static <P, T extends Entity<P>> void setCreateInfo(Collection<T> entities, User user, Instant date) {
        for (var entity : entities) {
            setCreateInfo(entity, user, date);
        }
    }

    /**
     * 设置修改信息
     *
     * @param <P>    P
     * @param <T>    T
     * @param entity 实体
     * @param user   用户
     */
    public static <P, T extends Entity<P>> void setUpdateInfo(T entity, User user) {
        setUpdateInfo(entity, user, Instant.now());
    }

    /**
     * 设置修改信息
     *
     * @param <P>    P
     * @param <T>    T
     * @param entity 实体
     * @param user   用户
     * @param date   时间
     */
    public static <P, T extends Entity<P>> void setUpdateInfo(T entity, User user, Instant date) {
        entity.setRowUpdateDate(date);
        entity.setRowUpdateUser(user.getId());
        entity.setRowUpdateUsername(user.getName());
    }

    /**
     * 设置修改信息
     *
     * @param <P>      P
     * @param <T>      T
     * @param entities 实体
     * @param user     用户
     */
    public static <P, T extends Entity<P>> void setUpdateInfo(Collection<T> entities, User user) {
        setUpdateInfo(entities, user, Instant.now());
    }

    /**
     * 设置修改信息
     *
     * @param <P>      P
     * @param <T>      T
     * @param entities 实体
     * @param user     用户
     * @param date     时间
     */
    public static <P, T extends Entity<P>> void setUpdateInfo(Collection<T> entities, User user, Instant date) {
        for (var entity : entities) {
            setUpdateInfo(entity, user, date);
        }
    }

    /**
     * 设置修改信息
     *
     * @param <P>    P
     * @param <T>    T
     * @param entity 实体
     * @param user   用户
     */
    public static <P, T extends Entity<P>> void setDeleteInfo(T entity, User user) {
        setDeleteInfo(entity, user, Instant.now());
    }

    /**
     * 设置修改信息
     *
     * @param <P>    P
     * @param <T>    T
     * @param entity 实体
     * @param user   用户
     * @param date   时间
     */
    public static <P, T extends Entity<P>> void setDeleteInfo(T entity, User user, Instant date) {
        entity.setRowDeletedDate(date);
        entity.setRowDeletedUser(user.getId());
    }

    /**
     * 设置修改信息
     *
     * @param <P>      P
     * @param <T>      T
     * @param entities 实体
     * @param user     用户
     */
    public static <P, T extends Entity<P>> void setDeleteInfo(Collection<T> entities, User user) {
        setDeleteInfo(entities, user, Instant.now());
    }

    /**
     * 设置修改信息
     *
     * @param <P>      P
     * @param <T>      T
     * @param entities 实体
     * @param user     用户
     * @param date     时间
     */
    public static <P, T extends Entity<P>> void setDeleteInfo(Collection<T> entities, User user, Instant date) {
        for (var entity : entities) {
            setDeleteInfo(entity, user, date);
        }
    }

}
