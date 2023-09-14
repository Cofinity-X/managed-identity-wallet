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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Identifier is a unique identifier for a wallet.
 */
public class Identifier {
    private static final String PATTERN = "^did:web:([a-z\\\\.]*\\b(%3A\\d{2,5})?\\b)\\b:BPNL[0-9a-f]{12}$";

    private final String value;

    public Identifier(final String identifier) {

        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(identifier);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("identifier %s is not valid".formatted(identifier));
        }

        this.value = identifier;
    }

    public String value() {
        return value;
    }
}
