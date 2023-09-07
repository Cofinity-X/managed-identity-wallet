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

package org.eclipse.tractusx.managedidentitywallets.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class IssuerVerifiableCredentialSearch extends AbstractVerifiableCredentialSearch {

    @Parameter(name = "holderIdentifier", description = "Holder identifier(did of BPN)", examples = {@ExampleObject(name = "bpn", value = "BPNL000000000001", description = "bpn"), @ExampleObject(description = "did", name = "did", value = "did:web:localhost:BPNL000000000001")})
    private String holderIdentifier;

    @Parameter(name = "sortColumn", description = "Sort column name",
            examples = {
                    @ExampleObject(value = "createdAt", name = "creation date"),
                    @ExampleObject(value = "holderDid", name = "Holder did"),
                    @ExampleObject(value = "type", name = "Credential type"),
                    @ExampleObject(value = "credentialId", name = "Credential id")
            }
    )
    private String sortColumn = "createdAt";

    public String getHolderIdentifier() {
        return holderIdentifier;
    }

    public void setHolderIdentifier(final String identifier) {
        this.holderIdentifier = identifier;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(final String sortColumn) {
        this.sortColumn = sortColumn;
    }
}
