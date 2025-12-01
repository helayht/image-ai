package com.yht.image.ai.config;

import lombok.AllArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

import static me.chanjar.weixin.common.api.WxConsts.EventType.SCAN;
import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType.EVENT;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2025/1/24
 **/
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class WxMpConfiguration {

    private final WxMpProperties properties;

    @Bean
    public WxMpService wxMpService(WxMpProperties properties) {
        // 这里是为了适配多个公众号的情况
        final List<WxMpProperties.MpConfig> configs = properties.getConfigs();
        if (configs == null) {
            throw new RuntimeException("微信相关配置有误，请仔细核对自己的公众号参数");
        }

        WxMpService service = new WxMpServiceImpl();
        service.setMultiConfigStorages(configs
                .stream().map(a -> {
                    WxMpDefaultConfigImpl configStorage;
                    configStorage = new WxMpDefaultConfigImpl();

                    configStorage.setAppId(a.getAppId());
                    configStorage.setSecret(a.getSecret());
                    configStorage.setToken(a.getToken());
                    configStorage.setAesKey(a.getAesKey());
                    return configStorage;
                }).collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, a -> a, (o, n) -> o)));
        return service;
    }

}
