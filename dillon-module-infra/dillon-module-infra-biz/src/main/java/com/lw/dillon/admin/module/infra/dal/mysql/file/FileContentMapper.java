package com.lw.dillon.admin.module.infra.dal.mysql.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lw.dillon.admin.module.infra.dal.dataobject.file.FileContentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileContentMapper extends BaseMapper<FileContentDO> {

    default void deleteByConfigIdAndPath(Long configId, String path) {
        this.delete(new LambdaQueryWrapper<FileContentDO>()
                .eq(FileContentDO::getConfigId, configId)
                .eq(FileContentDO::getPath, path));
    }

    default List<FileContentDO> selectListByConfigIdAndPath(Long configId, String path) {
        return selectList(new LambdaQueryWrapper<FileContentDO>()
                .eq(FileContentDO::getConfigId, configId)
                .eq(FileContentDO::getPath, path));
    }

}
