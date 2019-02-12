package com.single.code.tool.DesignPatterns.proxy;

/**
 * Created by czf on 2019/2/1.
 */

public interface DbProxy {
    void insert(Object object);
    void delete(Object object);
    void update(Object object);
    Object query(String selet);
}
