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

import com.nimbusds.jwt.JWT;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;

import java.util.Objects;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class ValidatePresentationCommand {

    private final boolean asJwt;

    private final String audience;

    private final VerifiablePresentation vpJson;

    private final JWT vpJwt;

    private final boolean withCredentialExpiryDate;

    private ValidatePresentationCommand(Builder builder) {
        asJwt = builder.asJwt;
        audience = builder.audience;
        vpJson = builder.vpJson;
        vpJwt = builder.vpJwt;
        withCredentialExpiryDate = builder.withCredentialExpiryDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean asJwt() {
        return asJwt;
    }

    public String audience() {
        return audience;
    }

    public VerifiablePresentation vpJson() {
        return vpJson;
    }

    public JWT vpJwt() {
        return vpJwt;
    }

    public boolean withCredentialExpiryDate() {
        return withCredentialExpiryDate;
    }

    public static class Builder {

        private boolean asJwt;

        private String audience;

        private VerifiablePresentation vpJson;

        private JWT vpJwt;

        private boolean withCredentialExpiryDate;

        public ValidatePresentationCommand build() throws IllegalStateException {
            validate();

            return new ValidatePresentationCommand(this);
        }

        public Builder setAsJwt(final boolean asJwt) {
            this.asJwt = asJwt;
            return this;
        }

        public Builder setAudience(final String audience) {
            this.audience = Objects.requireNonNull(audience);
            return this;
        }



        public Builder setWithCredentialExpiryDate(final boolean withCredentialExpiryDate) {
            this.withCredentialExpiryDate = withCredentialExpiryDate;
            return this;
        }

        public Builder setVpJson(final VerifiablePresentation vpJson) {
            this.vpJson = Objects.requireNonNull(vpJson);
            return this;
        }

        public Builder setVpJwt(final JWT vpJwt) {
            this.vpJwt = Objects.requireNonNull(vpJwt);
            return this;
        }

        private void validate() throws IllegalStateException {
            try {
                if(asJwt){
                    Objects.requireNonNull(vpJwt);
                }else{
                    Objects.requireNonNull(vpJson);
                }
            } catch (NullPointerException e) {
                throw new IllegalStateException(e);
            }
        }
    }

}
