package com.single.code.tool.DesignPatterns.factory;

import com.single.code.tool.DesignPatterns.factory.base.IoProduct;
import com.single.code.tool.DesignPatterns.factory.entity.FileIoEntity;

/**
 * Created by czf on 2019/1/29.
 */

public class FileIoProduct implements IoProduct<FileIoEntity> {

    @Override
    public void add(FileIoEntity data) {

    }

    @Override
    public void remove(FileIoEntity data) {

    }

    @Override
    public FileIoEntity query(FileIoEntity data) {

        return null;
    }

    @Override
    public void update(FileIoEntity data) {

    }
}
