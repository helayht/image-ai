package com.yht.image.ai.controller;

import com.yht.image.ai.service.IAuthService;
import com.yht.image.ai.service.entity.UserMessageEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description 微信公众号，验签和请求应答
 * @Author hela
 * @Date 2025/11/28
 */
@Slf4j
@RestController
@RequestMapping("/image/ai/wx/portal")
public class WeiXinPortalController {

    @Resource
    private WxMpService wxMpService;
    @Resource
    private IAuthService authService;

    /**
     * 处理微信服务器发来的get请求，进行签名的验证【apix.natapp1.cc 是我在 <a href="https://natapp.cn/">https://natapp.cn</a> 购买的渠道，你需要自己购买一个使用】
     * <a href="http://apix.natapp1.cc/api/v1/wx/portal/wxad979c0307864a66">http://apix.natapp1.cc/api/v1/wx/portal/wxad979c0307864a66</a>
     * <p>
     * appid     微信端AppID
     * signature 微信端发来的签名
     * timestamp 微信端发来的时间戳
     * nonce     微信端发来的随机字符串
     * echostr   微信端发来的验证字符串
     */
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String validate(@RequestParam(value = "signature", required = false) String signature,
                           @RequestParam(value = "timestamp", required = false) String timestamp,
                           @RequestParam(value = "nonce", required = false) String nonce,
                           @RequestParam(value = "echostr", required = false) String echostr) {
        try {
            log.info("微信公众号验签信息开始 [{}, {}, {}, {}]", signature, timestamp, nonce, echostr);
            if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
                throw new IllegalArgumentException("请求参数非法，请核实!");
            }
            boolean check = wxMpService.checkSignature(timestamp, nonce, signature);
            log.info("微信公众号验签信息完成 check：{}", check);
            if (!check) {
                return null;
            }
            return echostr;
        } catch (Exception e) {
            log.error("微信公众号验签信息失败 [{}, {}, {}, {}]", signature, timestamp, nonce, echostr, e);
            return null;
        }
    }


    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("openid") String openid,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature){
        // 解析消息体，封装为对象
        log.info("微信公众号请求开始 [{}, {}, {}, {}, {}, {}]", requestBody, signature, timestamp, nonce, openid, encType, msgSignature);
        WxMpXmlMessage xmlMessage = WxMpXmlMessage.fromXml(requestBody);
        // 接收消息内容
        String inContent = xmlMessage.getContent();
        String outContent;
        UserMessageEntity userMessageEntity = new UserMessageEntity();
        userMessageEntity.setOpenId(xmlMessage.getFromUser());
        if(inContent.equals("验证码")){
            String verifyCode = authService.createVerifyCode(userMessageEntity);
            outContent =String.format("您的验证码为：%s 有效期%d分钟！", verifyCode, 5);
        }else{
            outContent = "请回复【验证码】获取验证码";
        }
        // 构造响应消息对象
        WxMpXmlOutTextMessage outTextMessage = WxMpXmlOutMessage.TEXT().content(outContent).fromUser(xmlMessage.getToUser())
                .toUser(xmlMessage.getFromUser()).build();
        // 将响应消息转换为xml格式返回
        return outTextMessage.toXml();
    }

}
