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

package org.eclipse.tractusx.managedidentitywallets.domain;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class CredentialId {

    private final String value;

    private static final String PATTERN = "^did:web:([a-z\\\\.]*):BPNL[0-9a-f]{12}\\b#[0-9a-f]{8}\\b-[0-9a-f]{4}\\b-[0-9a-f]{4}\\b-[0-9a-f]{4}\\b-([0-9a-f]{12})$";

    public CredentialId(final String credentialId) {
        if (Objects.nonNull(credentialId)) {
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher matcher = pattern.matcher(credentialId);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("credentialId is not valid");
            }
        }

        this.value = credentialId;
    }

    public String value() {
        return value;
    }
}
