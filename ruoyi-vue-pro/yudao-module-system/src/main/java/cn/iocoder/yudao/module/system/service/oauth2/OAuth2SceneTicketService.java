package cn.iocoder.yudao.module.system.service.oauth2;

import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2SceneTicketCreateReqDTO;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2SceneTicketRespDTO;

import javax.validation.Valid;

public interface OAuth2SceneTicketService {

    OAuth2SceneTicketRespDTO createSceneTicket(@Valid OAuth2SceneTicketCreateReqDTO reqDTO);

    OAuth2AccessTokenCheckRespDTO checkSceneTicket(String ticket, String scene);

    OAuth2AccessTokenCheckRespDTO consumeSceneTicket(String ticket, String scene);

}
