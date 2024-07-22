package com.lw.dillon.admin.module.bpm.framework.flowable.core.candidate;

import cn.hutool.core.map.MapUtil;
import com.lw.dillon.admin.framework.common.enums.CommonStatusEnum;
import com.lw.dillon.admin.framework.test.core.ut.BaseMockitoUnitTest;
import com.lw.dillon.admin.module.bpm.framework.flowable.core.candidate.strategy.BpmTaskCandidateUserStrategy;
import com.lw.dillon.admin.module.bpm.framework.flowable.core.enums.BpmTaskCandidateStrategyEnum;
import com.lw.dillon.admin.module.bpm.framework.flowable.core.enums.BpmnModelConstants;
import com.lw.dillon.admin.module.system.api.user.AdminUserApi;
import com.lw.dillon.admin.module.system.api.user.dto.AdminUserRespDTO;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.lw.dillon.admin.framework.common.util.collection.SetUtils.asSet;
import static com.lw.dillon.admin.framework.test.core.util.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link BpmTaskCandidateInvoker} 的单元测试
 *
 * @author 芋道源码
 */
public class BpmTaskCandidateInvokerTest extends BaseMockitoUnitTest {

    @InjectMocks
    private BpmTaskCandidateInvoker taskCandidateInvoker;

    @Mock
    private AdminUserApi adminUserApi;
    @Spy
    private BpmTaskCandidateStrategy strategy = new BpmTaskCandidateUserStrategy();
    @Spy
    private List<BpmTaskCandidateStrategy> strategyList = Collections.singletonList(strategy);

    @Test
    public void testCalculateUsers() {
        // 准备参数
        String param = "1,2";
        DelegateExecution execution = mock(DelegateExecution.class);
        // mock 方法（DelegateExecution）
        UserTask userTask = mock(UserTask.class);
        when(execution.getCurrentFlowElement()).thenReturn(userTask);
        when(userTask.getAttributeValue(eq(BpmnModelConstants.NAMESPACE), eq(BpmnModelConstants.USER_TASK_CANDIDATE_STRATEGY)))
                .thenReturn(BpmTaskCandidateStrategyEnum.USER.getStrategy().toString());
        when(userTask.getAttributeValue(eq(BpmnModelConstants.NAMESPACE), eq(BpmnModelConstants.USER_TASK_CANDIDATE_PARAM)))
                .thenReturn(param);
        // mock 方法（adminUserApi）
        AdminUserRespDTO user1 = randomPojo(AdminUserRespDTO.class, o -> o.setId(1L)
                .setStatus(CommonStatusEnum.ENABLE.getStatus()));
        AdminUserRespDTO user2 = randomPojo(AdminUserRespDTO.class, o -> o.setId(2L)
                .setStatus(CommonStatusEnum.ENABLE.getStatus()));
        Map<Long, AdminUserRespDTO> userMap = MapUtil.builder(user1.getId(), user1)
                .put(user2.getId(), user2).build();
        when(adminUserApi.getUserMap(eq(asSet(1L, 2L)))).thenReturn(userMap);

        // 调用
        Set<Long> results = taskCandidateInvoker.calculateUsers(execution);
        // 断言
        assertEquals(asSet(1L, 2L), results);
    }

    @Test
    public void testRemoveDisableUsers() {
        // 准备参数. 1L 可以找到；2L 是禁用的；3L 找不到
        Set<Long> assigneeUserIds = asSet(1L, 2L, 3L);
        // mock 方法
        AdminUserRespDTO user1 = randomPojo(AdminUserRespDTO.class, o -> o.setId(1L)
                .setStatus(CommonStatusEnum.ENABLE.getStatus()));
        AdminUserRespDTO user2 = randomPojo(AdminUserRespDTO.class, o -> o.setId(2L)
                .setStatus(CommonStatusEnum.DISABLE.getStatus()));
        Map<Long, AdminUserRespDTO> userMap = MapUtil.builder(user1.getId(), user1)
                .put(user2.getId(), user2).build();
        when(adminUserApi.getUserMap(eq(assigneeUserIds))).thenReturn(userMap);

        // 调用
        taskCandidateInvoker.removeDisableUsers(assigneeUserIds);
        // 断言
        assertEquals(asSet(1L), assigneeUserIds);
    }

}
