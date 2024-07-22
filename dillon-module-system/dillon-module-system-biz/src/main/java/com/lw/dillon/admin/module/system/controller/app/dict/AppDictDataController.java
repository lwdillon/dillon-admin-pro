package com.lw.dillon.admin.module.system.controller.app.dict;

import com.lw.dillon.admin.framework.common.enums.CommonStatusEnum;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.util.object.BeanUtils;
import com.lw.dillon.admin.module.system.controller.app.dict.vo.AppDictDataRespVO;
import com.lw.dillon.admin.module.system.dal.dataobject.dict.DictDataDO;
import com.lw.dillon.admin.module.system.service.dict.DictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static com.lw.dillon.admin.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 字典数据")
@RestController
@RequestMapping("/system/dict-data")
@Validated
public class AppDictDataController {

    @Resource
    private DictDataService dictDataService;

    @GetMapping("/type")
    @Operation(summary = "根据字典类型查询字典数据信息")
    @Parameter(name = "type", description = "字典类型", required = true, example = "common_status")
    public CommonResult<List<AppDictDataRespVO>> getDictDataListByType(@RequestParam("type") String type) {
        List<DictDataDO> list = dictDataService.getDictDataList(
                CommonStatusEnum.ENABLE.getStatus(), type);
        return success(BeanUtils.toBean(list, AppDictDataRespVO.class));
    }

}
