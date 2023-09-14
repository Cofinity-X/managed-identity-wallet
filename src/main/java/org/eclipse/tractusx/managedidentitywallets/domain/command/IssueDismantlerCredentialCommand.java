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

import org.eclipse.tractusx.managedidentitywallets.domain.ActivityType;
import org.eclipse.tractusx.managedidentitywallets.domain.BPN;

import java.util.Objects;
import java.util.Set;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class IssueDismantlerCredentialCommand extends IssueCredentialCommand {

    private final ActivityType activityType;

    private final Set<String> allowedVehicleBrands;

    private IssueDismantlerCredentialCommand(Builder builder) {
        super(builder.bpn, builder.caller);
        activityType = builder.activityType;
        allowedVehicleBrands = builder.allowedVehicleBrands;
    }

    public ActivityType activityType() {
        return activityType;
    }

    public Set<String> allowedVehicleBrands() {
        return allowedVehicleBrands;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private BPN bpn;

        private BPN caller;

        private ActivityType activityType;

        private Set<String> allowedVehicleBrands;

        public IssueDismantlerCredentialCommand build() throws IllegalStateException {
            validate();

            return new IssueDismantlerCredentialCommand(this);
        }

        public Builder withBpn(final BPN bpn) {
            this.bpn = Objects.requireNonNull(bpn);
            return this;
        }

        public Builder withCaller(final BPN caller) {
            this.caller = Objects.requireNonNull(caller);
            return this;
        }

        public Builder withActivityType(final ActivityType activityType) {
            this.activityType = Objects.requireNonNull(activityType);
            return this;
        }

        public Builder withAllowedVehicleBrands(final Set<String> allowedVehicleBrands) {
            this.allowedVehicleBrands = Objects.requireNonNull(allowedVehicleBrands);
            return this;
        }

        private void validate() throws IllegalStateException {
            try {
                Objects.requireNonNull(bpn);
                Objects.requireNonNull(caller);
                Objects.requireNonNull(activityType);

            } catch (NullPointerException e) {
                throw new IllegalStateException(e);
            }
        }
    }

}
