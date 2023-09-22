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

import org.eclipse.tractusx.managedidentitywallets.domain.BPN;

import java.util.Objects;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class IssueFrameworkCredentialCommand {

    private final BPN holderIdentifier;

    private final BPN caller;

    private final String contractTemplate;

    private final String contractVersion;

    private final FrameworkCredentialType type;

    private IssueFrameworkCredentialCommand(Builder builder) {
        holderIdentifier = builder.holderIdentifier;
        caller = builder.caller;
        contractTemplate = builder.contractTemplate;
        contractVersion = builder.contractVersion;
        type = builder.type;
    }

    public BPN caller() {
        return caller;
    }

    public BPN holderIdentifier() {
        return holderIdentifier;
    }

    public String contractTemplate() {
        return contractTemplate;
    }

    public String contractVersion() {
        return contractVersion;
    }

    public FrameworkCredentialType type() {
        return type;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private BPN holderIdentifier;

        private BPN caller;

        private String contractTemplate;

        private String contractVersion;

        private FrameworkCredentialType type;

        public IssueFrameworkCredentialCommand build() throws IllegalStateException {
            validate();

            return new IssueFrameworkCredentialCommand(this);
        }

        public Builder setHolderIdentifier(final BPN holderIdentifier) {
            this.holderIdentifier = Objects.requireNonNull(holderIdentifier);
            return this;
        }

        public Builder setContractTemplate(final String contractTemplate) {
            this.contractTemplate = Objects.requireNonNull(contractTemplate);
            return this;
        }

        public Builder setContractVersion(final String contractVersion) {
            this.contractVersion = Objects.requireNonNull(contractVersion);
            return this;
        }

        public Builder setType(final FrameworkCredentialType type) {
            this.type = Objects.requireNonNull(type);
            return this;
        }

        public Builder setCaller(final BPN caller) {
            this.caller = Objects.requireNonNull(caller);
            return this;
        }

        private void validate() throws IllegalStateException {
            try {
                Objects.requireNonNull(holderIdentifier);
                Objects.requireNonNull(type);
                Objects.requireNonNull(contractTemplate);
                Objects.requireNonNull(contractVersion);
                Objects.requireNonNull(caller);
            } catch (NullPointerException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public enum FrameworkCredentialType {
        RESILIENCY("ResiliencyCredential"),
        BEHAVIOUR_TWIN("BehaviorTwinCredential"),
        PCF("PcfCredential"),
        SUSTAINABILITY("SustainabilityCredential"),
        QUALITY("QualityCredential"),
        TRACEABILITY("TraceabilityCredential");

        public final String value;

        FrameworkCredentialType(final String value) {
            this.value = value;
        }

        public static FrameworkCredentialType valueOfType(String label) {
            for (FrameworkCredentialType e : values()) {
                if (e.value.equals(label)) {
                    return e;
                }
            }
            throw new IllegalArgumentException("%s is not a known type".formatted(label));
        }
    }

}
