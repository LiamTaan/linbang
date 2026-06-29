package cn.iocoder.yudao.module.linbang.service.certexemption;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionRespVO;

import javax.validation.Valid;

public interface CertExemptionService {

    PageResult<CertExemptionRespVO> getPage(CertExemptionPageReqVO reqVO);

    CertExemptionDetailRespVO getDetail(Long id);

    void audit(@Valid CertExemptionAuditReqVO reqVO);
}
