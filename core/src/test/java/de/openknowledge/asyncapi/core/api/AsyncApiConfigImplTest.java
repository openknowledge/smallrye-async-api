/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 *
 */
package de.openknowledge.asyncapi.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;

import io.smallrye.asyncapi.spec.AASConfig;

public class AsyncApiConfigImplTest {

    private static final String TEST_PROPERTY = AASConfig.EXTENSIONS_PREFIX + "AsyncApiConfigImplTest";

    @Test
    public void testGetStringConfigValueMissingIsNull() {
        System.clearProperty(TEST_PROPERTY);
        Config config = ConfigProvider.getConfig();
        AsyncApiConfigImpl asyncApiConfig = new AsyncApiConfigImpl(config);
        assertNull(asyncApiConfig.getStringConfigValue(TEST_PROPERTY));
    }

    @Test
    public void testGetStringConfigValueBlankIsNull() {
        System.setProperty(TEST_PROPERTY, "\t \n\r");

        try {
            Config config = ConfigProvider.getConfig();
            AsyncApiConfigImpl asyncApiConfig = new AsyncApiConfigImpl(config);
            // White-space only value is treated as absent value
            assertNull(asyncApiConfig.getStringConfigValue(TEST_PROPERTY));
        } finally {
            System.clearProperty(TEST_PROPERTY);
        }
    }

    @Test
    public void testGetStringConfigValuePresent() {
        System.setProperty(TEST_PROPERTY, "  VALUE  \t");

        try {
            Config config = ConfigProvider.getConfig();
            AsyncApiConfigImpl asyncApiConfig = new AsyncApiConfigImpl(config);
            // Trim is only used to determine if the value is blank. Full value returned for app use
            assertEquals("  VALUE  \t", asyncApiConfig.getStringConfigValue(TEST_PROPERTY));
        } finally {
            System.clearProperty(TEST_PROPERTY);
        }
    }

}
