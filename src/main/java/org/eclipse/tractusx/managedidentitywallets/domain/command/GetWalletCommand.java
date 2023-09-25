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

package org.eclipse.tractusx.managedidentitywallets.domain.command;

import com.smartsensesolutions.java.commons.sort.SortType;
import org.eclipse.tractusx.managedidentitywallets.domain.WalletSortColumn;

import java.util.Objects;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class GetWalletCommand {

    private final int size;

    private final int pageNumber;

    private final WalletSortColumn sortColumn;

    private final SortType sortType;

    private GetWalletCommand(Builder builder) {
        size = builder.size;
        pageNumber = builder.pageNumber;
        sortType = builder.sortType;
        sortColumn = builder.sortColumn;
    }

    public int size() {
        return size;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public WalletSortColumn sortColumn() {
        return sortColumn;
    }

    public SortType sortType() {
        return sortType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int size;

        private int pageNumber;

        private WalletSortColumn sortColumn;

        private SortType sortType;

        public Builder setSize(final int size) {
            if (size <= 0)
                throw new IllegalArgumentException("size must be > 0");
            this.size = size;
            return this;
        }

        public Builder setPageNumber(final int pageNumber) {
            if (pageNumber < 0)
                throw new IllegalArgumentException("page number must be >= 0");
            this.pageNumber = pageNumber;
            return this;
        }

        public Builder setSortColumn(final WalletSortColumn sortColumn) {
            this.sortColumn = Objects.requireNonNull(sortColumn);
            return this;
        }

        public Builder setSortType(final SortType sortType) {
            this.sortType = Objects.requireNonNull(sortType);
            return this;
        }

        public GetWalletCommand build() throws IllegalStateException {
            validate();

            return new GetWalletCommand(this);
        }

        private void validate() throws IllegalStateException {
            try {
                Objects.requireNonNull(sortType);
                Objects.requireNonNull(sortColumn);
                if(size <= 0)
                    throw new IllegalStateException("size must be > 0");
            } catch (NullPointerException e) {
                throw new IllegalStateException(e);
            }
        }
    }

}
