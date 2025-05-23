/*

 * Copyright (C) 2020-2024 Huawei Technologies Co., Ltd. All rights reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.governance;

public class GovernanceConst {
  public static final String AUTH_TOKEN = "x-auth-token";

  public static final String INSTANCE_PUBKEY_PRO = "publickey";

  public static final String AUTH_SERVICE_NAME = "serviceName";

  public static final String AUTH_TOKEN_CHECK_ENABLED = "spring.cloud.servicecomb.webmvc.publicKey.tokenCheckEnabled";

  public static final String AUTH_TOKEN_HEADER_KEY = "spring.cloud.servicecomb.webmvc.publicKey.headerTokenKey";

  public static final String AUTH_API_PATH_WHITELIST = "spring.cloud.servicecomb.webmvc.publicKey.apiPathWhitelist";

  public static final String AUTH_API_PATH_INCLUDE = "spring.cloud.servicecomb.webmvc.publicKey.includePathPatterns";

  public static final String AUTH_API_PATH_EXCLUDE = "spring.cloud.servicecomb.webmvc.publicKey.excludePathPatterns";

  public static final String CONTEXT_CURRENT_INSTANCE = "x-current-instance";

  public static final String FEIGN_METHOD_OPTIONS_ENABLED = "feign.client.method.request.options.enabled";
}
