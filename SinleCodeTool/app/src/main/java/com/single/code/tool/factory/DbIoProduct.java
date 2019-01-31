package com.single.code.tool.factory;

import com.single.code.tool.factory.base.IoProduct;
import com.single.code.tool.factory.entity.DbIoEntity;

/**
 * Created by czf on 2019/1/29.
 */

public class DbIoProduct implements IoProduct<DbIoEntity> {
    @Override
    public void add(DbIoEntity data) {

    }

    @Override
    public void remove(DbIoEntity data) {

    }

    @Override
    public DbIoEntity query(DbIoEntity data) {

        return null;
    }

    @Override
    public void update(DbIoEntity data) {

    }
}
