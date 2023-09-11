/*
 * *******************************************************************************
 *  Copyright (c) 2021,2023 Contributors to the Eclipse Foundation
 *
 *  See the NOTICE file(s) distributed with this work for additional
 *  information regarding copyright ownership.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 * ******************************************************************************
 */

package org.eclipse.tractusx.managedidentitywallets.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
@Component
@ConfigurationPropertiesBinding
public class EncryptionKeyConverter implements Converter<String, SecretKey> {


    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public SecretKey convert(final String source) {
        System.out.println("%s source is".formatted(source));
        Resource resource = applicationContext.getResource(source);
        byte[] decodedKey;
        try (InputStream inputStream = resource.getInputStream()) {
            decodedKey = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}
