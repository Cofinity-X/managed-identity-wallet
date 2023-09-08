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

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public enum SortColumn {

    CREATED_AT("createdAt"),
    ISSUER_DID("issuerDid"),
    HOLDER_DID("holderDid"),
    TYPE("type"),

    CREDENTIAL_ID("credentialId"),

    SELF_ISSUED("selfIssued"),

    STORED("stored");

    public final String value;

    SortColumn(final String value) {
        this.value = value;
    }

    public static SortColumn valueOfColumn(String label) {
        for (SortColumn e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        throw new IllegalArgumentException("%s is not a known column".formatted(label));
    }
}
