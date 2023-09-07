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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class AbstractVerifiableCredentialSearch {
    @Parameter(name = "credentialId", description = "Credential Id", examples = {@ExampleObject(name = "Credential Id", value = "did:web:localhost:BPNL000000000000#12528899-160a-48bd-ba15-f396c3959ae9")})
    private String credentialId;

    @Min(0)
    @Max(Integer.MAX_VALUE)
    @Parameter(description = "Page number, Page number start with zero", example = "0")
    private int pageNumber = 0;

    @Min(0)
    @Max(Integer.MAX_VALUE)
    @Parameter(description = "Number of records per page", example = "42")
    private int size = Integer.MAX_VALUE;

    @Parameter(name = "type", description = "Type of VC", examples = {@ExampleObject(name = "SummaryCredential", value = "SummaryCredential", description = "SummaryCredential"), @ExampleObject(description = "BpnCredential", name = "BpnCredential", value = "BpnCredential")})
    private List<String> type;



    @Parameter(name = "sortType", description = "Sort order", examples = {@ExampleObject(value = "desc", name = "Descending order"), @ExampleObject(value = "asc", name = "Ascending order")})
    private String sortType = "desc";


    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(final String credentialId) {
        this.credentialId = credentialId;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(final List<String> type) {
        this.type = type;
    }


    public String getSortType() {
        return sortType;
    }

    public void setSortType(final String sortType) {
        this.sortType = sortType;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }


    public int getSize() {
        return size;
    }

    public void setSize(final int size) {
        this.size = size;
    }
}
