// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * api.propertiesの値を保持するクラス．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/10/08
 */
@ConfigurationProperties(prefix = "api.common")
@RequiredArgsConstructor
@Getter
public class RestClientProperty {

    /**
     * 接続待機時間．
     */
    private final int connectionTimeout;

    /**
     * レスポンス待機時間．
     */
    private final int readTimeout;

    /**
     * 通信プロトコル．
     */
    private final String protocol;

    /**
     * 通信プロトコル(SSL)．
     */
    private final String sslProtocol;

    /**
     * 通信ポート．
     */
    private final String port;

    /**
     * 通信ポート(SSL)．
     */
    private final String sslPort;

}