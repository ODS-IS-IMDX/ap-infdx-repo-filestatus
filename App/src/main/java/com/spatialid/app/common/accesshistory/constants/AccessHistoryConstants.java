// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.accesshistory.constants;

import java.util.List;

/**
 * アクセス履歴APIの定数を定義したクラス．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/11/22
 */
public class AccessHistoryConstants {
    
    /**
     * Filterを適用しないURIパターンのリスト．
     */
    public static final List<String> EXCLUDE_URI_PATTERN = List.of("/gen/api/generic/v1/*", "/health");
    
    /**
     * アクセス履歴登録APIのパス．
     */
    public static final String UPSERT_ACCESS_HISTORY_URI = "/gen/api/generic/v1/create-axs-history";
    
    /**
     * アクセス履歴登録のAPI名．
     */
    public static final String UPSERT_ACCESS_HISTORY_NAME = "アクセス履歴登録";
    
    /**
     * ファクトリクラスに渡すマップにおいて、アクセス履歴登録時に渡すリクエスト情報に対応するキー名．
     */
    public static final String FACTORY_PARAM_KEY_REQUEST = "registRequest";
    
    /**
     * ファクトリクラスに渡すマップにおいて、アクセス履歴更新時に渡す登録済みの情報に対応するキー名．
     */
    public static final String FACTORY_PARAM_KEY_REGISTED_PARAM = "registedParam";
    
    /**
     * ファクトリクラスに渡すマップにおいて、アクセス履歴更新時に渡すレスポンス情報に対応するキー名．
     */
    public static final String FACTORY_PARAM_KEY_RESPONSE = "updateResponse";
    
}
