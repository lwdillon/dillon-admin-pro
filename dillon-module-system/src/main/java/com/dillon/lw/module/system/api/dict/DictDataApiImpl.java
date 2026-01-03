package com.dillon.lw.module.system.api.dict;

import com.dillon.lw.framework.common.biz.system.dict.dto.DictDataRespDTO;
import com.dillon.lw.framework.common.util.object.BeanUtils;
import com.dillon.lw.module.system.dal.dataobject.dict.DictDataDO;
import com.dillon.lw.module.system.service.dict.DictDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 字典数据 API 实现类
 *
 * @author liwen
 */
@Service
public class DictDataApiImpl implements DictDataApi {

    @Resource
    private DictDataService dictDataService;

    @Override
    public void validateDictDataList(String dictType, Collection<String> values) {
        dictDataService.validateDictDataList(dictType, values);
    }

    @Override
    public List<DictDataRespDTO> getDictDataList(String dictType) {
        List<DictDataDO> list = dictDataService.getDictDataListByDictType(dictType);
        return BeanUtils.toBean(list, DictDataRespDTO.class);
    }

}
