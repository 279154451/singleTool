package com.single.code.tool.factory.base;

/**
 * Created by czf on 2019/1/29.
 */

public interface IoProduct <T> {

    void add(T data);
    void remove(T data);
    T query(T data);
    void update(T data);
}
