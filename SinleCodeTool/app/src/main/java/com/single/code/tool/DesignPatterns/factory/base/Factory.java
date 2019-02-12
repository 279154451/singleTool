package com.single.code.tool.DesignPatterns.factory.base;

/**
 * 工厂
 * Created by czf on 2019/1/29.
 */

public abstract class Factory {
    /**
     *
     * @param clz
     * @param <T> 生产车间
     * @return
     */
    public abstract <T extends IoProduct> T createProduct(Class<T> clz);
}
