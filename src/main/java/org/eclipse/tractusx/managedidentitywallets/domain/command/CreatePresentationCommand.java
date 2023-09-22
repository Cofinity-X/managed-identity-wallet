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
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentials;

import java.util.List;
import java.util.Objects;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class CreatePresentationCommand {

    private final List<VerifiableCredential> verifiableCredentials;

    private final boolean asJwt;

    private final String audience;

    private final BPN caller;

    private CreatePresentationCommand(Builder builder) {
        verifiableCredentials = builder.verifiableCredentials;
        asJwt = builder.asJwt;
        audience = builder.audience;
        caller = builder.caller;
    }


    public List<VerifiableCredential> verifiableCredentials() {
        return verifiableCredentials;
    }

    public boolean asJwt() {
        return asJwt;
    }

    public String audience() {
        return audience;
    }

    public BPN caller() {
        return caller;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<VerifiableCredential> verifiableCredentials;
        private boolean asJwt;
        private String audience;
        private BPN caller;

        public CreatePresentationCommand build() throws IllegalStateException {
            validate();

            return new CreatePresentationCommand(this);
        }

        public Builder setVerifiableCredentials(final List<VerifiableCredential> verifiableCredentials) {
            this.verifiableCredentials = Objects.requireNonNull(verifiableCredentials);
            return this;
        }

        public Builder setAsJwt(final boolean asJwt) {
            this.asJwt = asJwt;
            return this;
        }

        public Builder setAudience(final String audience) {
            this.audience = Objects.requireNonNull(audience);
            return this;
        }

        public Builder setCaller(final BPN caller) {
            this.caller = Objects.requireNonNull(caller);
            return this;
        }

        private void validate() throws IllegalStateException {
            try {
                Objects.requireNonNull(verifiableCredentials);
                Objects.requireNonNull(caller);
            } catch (NullPointerException e) {
                throw new IllegalStateException(e);
            }
        }
    }

}
