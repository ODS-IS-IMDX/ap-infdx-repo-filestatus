// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.api.constants;

/**
 * アクセス制限APIのURIを管理する定数クラス。
 * 
 * @author kawashima naoya
 * @version 1.1 2024/09/20
 * 
 */
public class AccessControlInterceptorConstants {

    /** アクセス制限のベースパス */
    public static final String BASE_PATH = "/gen/api/generic/v1";

    /** アクセス制限のエンドポイント */
    public static final String API_AUTH_ENDPOINT = "/api-auth?servicerId=";

    /** API利用権限参照のAPI名 */
    public static final String API_AUTH_NAME = "API利用権限参照";

}