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

import java.util.List;
import java.util.Objects;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class CredentialSearch {

    private final CredentialId credentialId;

    private final Identifier identifier;

    private final List<TypeToSearch> typesToSearch;

    private final SortColumn sortColumn;

    private final SortType sortType;

    private final int pageNumber;

    private final int pageSize;

    private final Identifier callerBpn;

    private CredentialSearch(Builder builder) {
        credentialId = builder.id;
        identifier = builder.identifier;
        typesToSearch = builder.typesToSearch;
        sortColumn = builder.sortColumn;
        sortType = builder.sortType;
        pageNumber = builder.pageNumber;
        pageSize = builder.pageSize;
        callerBpn = builder.callerBpn;
    }

    public CredentialId credentialId() {
        return credentialId;
    }

    public Identifier identifier() {
        return identifier;
    }

    public List<TypeToSearch> typeToSearch() {
        return typesToSearch;
    }

    public SortColumn sortColumn() {
        return sortColumn;
    }

    public SortType sortType() {
        return sortType;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public int pageSize() {
        return pageSize;
    }

    public Identifier callerBpn() {
        return callerBpn;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private CredentialId id;

        private Identifier identifier;

        private List<TypeToSearch> typesToSearch;

        private SortColumn sortColumn;

        private SortType sortType;

        private int pageNumber;

        private int pageSize;

        private Identifier callerBpn;


        public CredentialSearch build() throws IllegalStateException {
            validate();

            return new CredentialSearch(this);
        }

        public Builder withCredentialId(final CredentialId id) throws NullPointerException {
            this.id = Objects.requireNonNull(id);

            return this;
        }

        public Builder withIdentifier(final Identifier identifier) {
            this.identifier = Objects.requireNonNull(identifier);
            return this;
        }

        public Builder withTypesToSearch(final List<TypeToSearch> typesToSearch) {
            this.typesToSearch = Objects.requireNonNull(typesToSearch);
            return this;
        }

        public Builder withSortColumn(final SortColumn sortColumn) {
            this.sortColumn = Objects.requireNonNull(sortColumn);
            return this;
        }

        public Builder withSortType(SortType sortType) {
            this.sortType = Objects.requireNonNull(sortType);
            return this;
        }

        public Builder withPageNumber(int pageNumber) {
            if (pageNumber < 0)
                throw new IllegalArgumentException("page number must be >=0");
            this.pageNumber = pageNumber;
            return this;
        }

        public Builder withPageSize(int pageSize) {
            if (pageSize < 0)
                throw new IllegalArgumentException("page size must be >=0");
            this.pageSize = pageSize;
            return this;
        }

        public Builder withCallerBpn(Identifier identifier) {
            this.callerBpn = identifier;
            return this;
        }

        private void validate() throws IllegalStateException {
            try {
                Objects.requireNonNull(sortColumn);
                Objects.requireNonNull(sortType);
                Objects.requireNonNull(callerBpn);
            } catch (NullPointerException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
