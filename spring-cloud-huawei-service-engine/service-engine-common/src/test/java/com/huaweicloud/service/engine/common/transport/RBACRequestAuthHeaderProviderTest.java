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

package com.huaweicloud.service.engine.common.transport;

import java.util.Map;

import org.apache.servicecomb.service.center.client.exception.OperationException;
import org.apache.servicecomb.service.center.client.model.RbacTokenResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.huaweicloud.service.engine.common.configration.bootstrap.BootstrapProperties;
import com.huaweicloud.service.engine.common.configration.bootstrap.DiscoveryBootstrapProperties;
import com.huaweicloud.service.engine.common.configration.bootstrap.ServiceCombRBACProperties;
import com.huaweicloud.service.engine.common.configration.bootstrap.ServiceCombSSLProperties;

public class RBACRequestAuthHeaderProviderTest {
  private final BootstrapProperties bootstrapProperties = Mockito.mock(BootstrapProperties.class);

  private final DiscoveryBootstrapProperties discoveryProperties = new DiscoveryBootstrapProperties();

  private final ServiceCombSSLProperties serviceCombSSLProperties = Mockito.mock(ServiceCombSSLProperties.class);

  private final ServiceCombRBACProperties serviceCombRBACProperties = Mockito.mock(ServiceCombRBACProperties.class);

  @BeforeEach
  public void setUp() {
    discoveryProperties.setAddress("http://127.0.0.1:30100");
    Mockito.when(bootstrapProperties.getDiscoveryBootstrapProperties()).thenReturn(discoveryProperties);
    Mockito.when(bootstrapProperties.getServiceCombSSLProperties()).thenReturn(serviceCombSSLProperties);
    Mockito.when(bootstrapProperties.getServiceCombRBACProperties()).thenReturn(serviceCombRBACProperties);

    Mockito.when(serviceCombRBACProperties.getName()).thenReturn("test_name");
    Mockito.when(serviceCombRBACProperties.getPassword()).thenReturn("test_password");
  }

  static class FirstTimeSuccessRBACRequestAuthHeaderProvider extends RBACRequestAuthHeaderProvider {
    public FirstTimeSuccessRBACRequestAuthHeaderProvider(BootstrapProperties bootstrapProperties) {
      super(bootstrapProperties);
    }

    @Override
    protected RbacTokenResponse callCreateHeaders() {
      RbacTokenResponse response = new RbacTokenResponse();
      response.setStatusCode(200);
      response.setToken("test_token");
      return response;
    }
  }

  static class SecondTimeSuccessRBACRequestAuthHeaderProvider extends RBACRequestAuthHeaderProvider {
    private boolean first = true;

    public SecondTimeSuccessRBACRequestAuthHeaderProvider(BootstrapProperties bootstrapProperties) {
      super(bootstrapProperties);
    }

    @Override
    protected RbacTokenResponse callCreateHeaders() {
      if (first) {
        first = false;
        throw new OperationException("query token failed");
      }
      RbacTokenResponse response = new RbacTokenResponse();
      response.setStatusCode(200);
      response.setToken("test_token");
      return response;
    }
  }

  static class SecondTimeFirstNullSuccessRBACRequestAuthHeaderProvider extends RBACRequestAuthHeaderProvider {
    private int count = 0;

    public SecondTimeFirstNullSuccessRBACRequestAuthHeaderProvider(BootstrapProperties bootstrapProperties) {
      super(bootstrapProperties);
    }

    @Override
    protected long refreshTime() {
      return 100;
    }

    @Override
    protected RbacTokenResponse callCreateHeaders() {
      RbacTokenResponse response = new RbacTokenResponse();
      count++;
      if (count == 1) {
        response.setStatusCode(400);
        return response;
      }
      if (count == 2) {
        response.setStatusCode(200);
        response.setToken("test_token");
        return response;
      }
      response.setStatusCode(200);
      response.setToken("test_token_2");
      return response;
    }
  }

  @Test
  public void testFirstTimeSuccess() {
    RBACRequestAuthHeaderProvider provider = new FirstTimeSuccessRBACRequestAuthHeaderProvider(bootstrapProperties);
    Map<String, String> result = provider.authHeaders();
    Assertions.assertEquals("Bearer test_token", result.get(RBACRequestAuthHeaderProvider.AUTH_HEADER));
    result = provider.authHeaders();
    Assertions.assertEquals("Bearer test_token", result.get(RBACRequestAuthHeaderProvider.AUTH_HEADER));
  }

  @Test
  public void testSecondTimeSuccess() {
    RBACRequestAuthHeaderProvider provider = new SecondTimeSuccessRBACRequestAuthHeaderProvider(bootstrapProperties);
    Map<String, String> result = provider.authHeaders();
    Assertions.assertTrue(result.isEmpty());
    result = provider.authHeaders();
    Assertions.assertEquals("Bearer test_token", result.get(RBACRequestAuthHeaderProvider.AUTH_HEADER));
    result = provider.authHeaders();
    Assertions.assertEquals("Bearer test_token", result.get(RBACRequestAuthHeaderProvider.AUTH_HEADER));
  }

  @Test
  public void testSecondTimeSuccessFirstNull() throws Exception {
    RBACRequestAuthHeaderProvider provider = new SecondTimeFirstNullSuccessRBACRequestAuthHeaderProvider(
        bootstrapProperties);
    Map<String, String> result = provider.authHeaders();
    Assertions.assertTrue(result.isEmpty());
    result = provider.authHeaders();
    Assertions.assertEquals("Bearer test_token", result.get(RBACRequestAuthHeaderProvider.AUTH_HEADER));
    result = provider.authHeaders();
    Assertions.assertEquals("Bearer test_token", result.get(RBACRequestAuthHeaderProvider.AUTH_HEADER));
    for (int i = 0; i < 20; i++) {
      Thread.sleep(100);
      result = provider.authHeaders();// wait a while
      if ("Bearer test_token_2".equals(result.get(RBACRequestAuthHeaderProvider.AUTH_HEADER))) {
        break;
      }
    }
    result = provider.authHeaders();
    Assertions.assertEquals("Bearer test_token_2", result.get(RBACRequestAuthHeaderProvider.AUTH_HEADER));
  }
}
