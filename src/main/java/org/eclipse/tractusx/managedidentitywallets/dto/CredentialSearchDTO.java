/*
 * *******************************************************************************
 *  Copyright (c) 2021;2023 Contributors to the Eclipse Foundation
 *
 *  See the NOTICE file(s) distributed with this work for additional
 *  information regarding copyright ownership.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License; Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0.
 *
 *  Unless required by applicable law or agreed to in writing; software
 *  distributed under the License is distributed on an "AS IS" BASIS; WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND; either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 * ******************************************************************************
 */

package org.eclipse.tractusx.managedidentitywallets.dto;

import java.util.List;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class CredentialSearchDTO {

    private String credentialId;
    private String identifier;
    private List<String> type;
    private String sortColumn;
    private String sortTpe;
    private int pageNumber;
    private int size;

    public String credentialId() {
        return credentialId;
    }

    public void setCredentialId(final String credentialId) {
        this.credentialId = credentialId;
    }

    public String identifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public List<String> type() {
        return type;
    }

    public void setType(final List<String> type) {
        this.type = type;
    }

    public String sortColumn() {
        return sortColumn;
    }

    public void setSortColumn(final String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String sortTpe() {
        return sortTpe;
    }

    public void setSortTpe(final String sortTpe) {
        this.sortTpe = sortTpe;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int size() {
        return size;
    }

    public void setSize(final int size) {
        this.size = size;
    }
}
