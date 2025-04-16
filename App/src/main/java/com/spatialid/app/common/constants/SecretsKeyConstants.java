// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.constants;

public class SecretsKeyConstants {

    /** Secrets Managerから取得したリージョン名 */
    public final static String REGION = "S3-B4-OUTPUT-REGION";

    /** Secrets Managerから取得したバケット */
    public final static String BUCKET = "S3-B4-OUTPUT-BUCKET";

    /** Secrets Managerから取得したドメイン名 */
    public final static String COMMON_DOMAIN = "ECS-COMMON-DOMAIN";

    /**API利用権限コンテナのドメイン．*/
    public final static String AUTHORITY_DOMAIN = "ECS-AUTHORITY-DOMAIN";

    /**アクセス履歴コンテナのドメインに対応したキー名．*/
    public static final String AC_HISTORY_DOMAIN = "ECS-ACCESS-HISTORY-DOMAIN";
    
}
