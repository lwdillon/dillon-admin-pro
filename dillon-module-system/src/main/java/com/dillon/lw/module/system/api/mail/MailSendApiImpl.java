package com.dillon.lw.module.system.api.mail;

import com.dillon.lw.module.system.api.mail.dto.MailSendSingleToUserReqDTO;
import com.dillon.lw.module.system.service.mail.MailSendService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 邮件发送 API 实现类
 *
 * @author wangjingyi
 */
@Service
@Validated
public class MailSendApiImpl implements MailSendApi {

    @Resource
    private MailSendService mailSendService;

    @Override
    public Long sendSingleMailToAdmin(MailSendSingleToUserReqDTO reqDTO) {
        return mailSendService.sendSingleMailToAdmin(reqDTO.getUserId(),
                reqDTO.getToMails(), reqDTO.getCcMails(), reqDTO.getBccMails(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams());
    }

    @Override
    public Long sendSingleMailToMember(MailSendSingleToUserReqDTO reqDTO) {
        return mailSendService.sendSingleMailToMember(reqDTO.getUserId(),
                reqDTO.getToMails(), reqDTO.getCcMails(), reqDTO.getBccMails(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams());
    }

}
