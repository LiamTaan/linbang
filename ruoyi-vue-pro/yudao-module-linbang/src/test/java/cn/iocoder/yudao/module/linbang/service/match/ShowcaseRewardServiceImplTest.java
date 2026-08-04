package cn.iocoder.yudao.module.linbang.service.match;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.ShowcaseRewardRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.showcasereward.ShowcaseRewardDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.showcasereward.ShowcaseRewardMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class ShowcaseRewardServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ShowcaseRewardServiceImpl service;

    @Mock
    private ShowcaseRewardMapper showcaseRewardMapper;

    @Test
    void getRewardPage_returnsResponseViewsInsteadOfEntities() {
        PageParam pageParam = new PageParam();
        ShowcaseRewardDO reward = ShowcaseRewardDO.builder()
                .id(1L)
                .merchantId(2L)
                .userId(3L)
                .title("title")
                .auditStatus("APPROVED")
                .priorityEnabled(Boolean.TRUE)
                .build();
        when(showcaseRewardMapper.selectPage(pageParam, 2L, "APPROVED"))
                .thenReturn(new PageResult<>(Collections.singletonList(reward), 1L));

        PageResult<ShowcaseRewardRespVO> result = service.getRewardPage(pageParam, 2L, "APPROVED");

        assertEquals(1L, result.getTotal());
        assertEquals(1L, result.getList().get(0).getId());
        assertEquals(2L, result.getList().get(0).getMerchantId());
        assertEquals("APPROVED", result.getList().get(0).getAuditStatus());
    }

    @Test
    void getReward_returnsNullWhenMissing() {
        when(showcaseRewardMapper.selectById(9L)).thenReturn(null);

        assertNull(service.getReward(9L));
    }
}
