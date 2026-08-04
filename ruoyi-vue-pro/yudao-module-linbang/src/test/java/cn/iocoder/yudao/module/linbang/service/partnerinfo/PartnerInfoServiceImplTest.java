package cn.iocoder.yudao.module.linbang.service.partnerinfo;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnerinfo.PartnerInfoMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PartnerInfoServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private PartnerInfoServiceImpl partnerInfoService;
    @Mock private PartnerInfoMapper partnerInfoMapper;
    @Mock private MemberUserMapper memberUserMapper;
    @Mock private MemberUserRealNameMapper memberUserRealNameMapper;

    @Test
    void getOrCreatePartner_returnsConcurrentInsertAfterDuplicateKey() {
        PartnerInfoDO concurrent = PartnerInfoDO.builder().id(7L).userId(30L).partnerName("existing").build();
        when(partnerInfoMapper.selectByUserId(30L)).thenReturn(null);
        when(memberUserMapper.selectById(30L))
                .thenReturn(MemberUserDO.builder().id(30L).nickname("user").mobile("13800138000").build());
        when(memberUserRealNameMapper.selectByUserId(30L))
                .thenReturn(MemberUserRealNameDO.builder().userId(30L).realName("name").build());
        when(partnerInfoMapper.insert(any(PartnerInfoDO.class)))
                .thenThrow(new DuplicateKeyException("duplicate partner"));
        when(partnerInfoMapper.selectByUserIdForUpdate(30L)).thenReturn(concurrent);

        assertSame(concurrent, partnerInfoService.getOrCreatePartner(30L));
    }
}
