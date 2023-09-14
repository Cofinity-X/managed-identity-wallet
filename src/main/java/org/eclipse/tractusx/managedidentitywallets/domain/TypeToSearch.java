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

public enum TypeToSearch {

    BPN_CREDENTIAL("BpnCredential"),
    SUMMARY_CREDENTIAL("SummaryCredential"),
    MEMBERSHIP_CREDENTIAL("MembershipCredential");

    public final String value;

    TypeToSearch(final String value) {
        this.value = value;
    }

    public static TypeToSearch valueOfType(String label) {
        for (TypeToSearch e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        throw new IllegalArgumentException("%s is not a known type".formatted(label));
    }
}
