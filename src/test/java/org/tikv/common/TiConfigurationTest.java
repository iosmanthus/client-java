/*
 * Copyright 2021 TiKV Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.tikv.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.tikv.common.ConfigUtils.TIKV_GRPC_HEALTH_CHECK_TIMEOUT;
import static org.tikv.common.ConfigUtils.TIKV_HEALTH_CHECK_PERIOD_DURATION;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.tikv.common.TiConfiguration.ApiVersion;
import org.tikv.kvproto.Kvrpcpb.APIVersion;

public class TiConfigurationTest {

  @Test
  public void configFileTest() {
    TiConfiguration conf = TiConfiguration.createRawDefault();
    assertEquals("configFileTest", conf.getDBPrefix());
  }

  @Test
  public void tiFlashDefaultValueTest() {
    TiConfiguration conf = TiConfiguration.createRawDefault();
    assertFalse(conf.isTiFlashEnabled());
  }

  @Test
  public void testGrpcHealthCheckTimeoutValue() {
    TiConfiguration conf = TiConfiguration.createDefault();
    // default value
    Assert.assertEquals(
        TiConfiguration.getInt(TIKV_GRPC_HEALTH_CHECK_TIMEOUT), conf.getGrpcHealthCheckTimeout());
    // new value
    int newValue = 100000;
    conf.setGrpcHealthCheckTimeout(newValue);
    Assert.assertEquals(newValue, conf.getGrpcHealthCheckTimeout());
  }

  @Test
  public void testHealthCheckPeriodDurationValue() {
    TiConfiguration conf = TiConfiguration.createDefault();
    // default value
    Assert.assertEquals(
        TiConfiguration.getInt(TIKV_HEALTH_CHECK_PERIOD_DURATION),
        conf.getHealthCheckPeriodDuration());
    // new value
    int newValue = 100000;
    conf.setHealthCheckPeriodDuration(newValue);
    Assert.assertEquals(newValue, conf.getHealthCheckPeriodDuration());
  }

  @Test
  public void testGrpcIdleTimeoutValue() {
    TiConfiguration conf = TiConfiguration.createDefault();
    // default value
    assertEquals(TiConfiguration.getInt(ConfigUtils.TIKV_GRPC_IDLE_TIMEOUT), conf.getIdleTimeout());
    // new value
    int newValue = 100000;
    conf.setIdleTimeout(newValue);
    assertEquals(newValue, conf.getIdleTimeout());
  }

  @Test
  public void tiJksDefaultValueTest() {
    TiConfiguration conf = TiConfiguration.createRawDefault();
    assertFalse(conf.isJksEnable());
  }

  @Test
  public void slowLogDefaultValueTest() {
    TiConfiguration conf = TiConfiguration.createRawDefault();
    assertEquals(conf.getTimeout() * 2, conf.getRawKVReadSlowLogInMS().longValue());
    assertEquals(conf.getTimeout() * 2, conf.getRawKVWriteSlowLogInMS().longValue());
    assertEquals(conf.getTimeout() * 2, conf.getRawKVBatchReadSlowLogInMS().longValue());
    assertEquals(conf.getTimeout() * 2, conf.getRawKVBatchWriteSlowLogInMS().longValue());
  }

  @Test
  public void serializeTest() throws IOException {
    TiConfiguration conf = TiConfiguration.createDefault();
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(conf);
      oos.flush();
    }
  }

  @Test
  public void testApiVersion() {
    TiConfiguration conf = TiConfiguration.createDefault();
    assertTrue(conf.getApiVersion().isV1());
    assertTrue(conf.setApiVersion(ApiVersion.V2).getApiVersion().isV2());
  }

  @Test
  public void testApiVersionToPb() {
    assertEquals(APIVersion.V1, ApiVersion.V1.toPb());
    assertEquals(APIVersion.V2, ApiVersion.V2.toPb());
  }
}
