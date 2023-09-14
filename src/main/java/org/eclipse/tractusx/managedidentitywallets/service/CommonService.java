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

package org.eclipse.tractusx.managedidentitywallets.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.managedidentitywallets.constant.StringPool;
import org.eclipse.tractusx.managedidentitywallets.dao.entity.Wallet;
import org.eclipse.tractusx.managedidentitywallets.dao.repository.WalletRepository;
import org.eclipse.tractusx.managedidentitywallets.domain.BPN;
import org.eclipse.tractusx.managedidentitywallets.domain.Identifier;
import org.eclipse.tractusx.managedidentitywallets.exception.WalletNotFoundProblem;
import org.eclipse.tractusx.managedidentitywallets.utils.Validate;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommonService {

    private final WalletRepository walletRepository;

    /**
     * Gets wallet by bpn.
     * 
     * @param bpn
     * @return
     */
    public Wallet getWalletByBPN(BPN bpn) {
        Wallet wallet = walletRepository.getByBpn(bpn.value());
        Validate.isNull(wallet).launch(new WalletNotFoundProblem("Wallet not found for identifier " + bpn.value()));
        return wallet;
    }

    /**
     * get wallet by did
     * 
     * @param did
     * @return
     */
    public Wallet getWalletByDid(Identifier did) {
        Wallet wallet = walletRepository.getByDid(did.value());
        Validate.isNull(wallet).launch(new WalletNotFoundProblem("Wallet not found for identifier " + did));
        return wallet;
    }

    public static boolean validateExpiry(boolean withCredentialExpiryDate, VerifiableCredential verifiableCredential,
            Map<String, Object> response) {
        // validate expiry date
        boolean dateValidation = true;
        if (withCredentialExpiryDate) {
            Instant expirationDate = verifiableCredential.getExpirationDate();
            if (expirationDate.isBefore(Instant.now())) {
                dateValidation = false;
                response.put(StringPool.VALIDATE_EXPIRY_DATE, false);
            } else {
                response.put(StringPool.VALIDATE_EXPIRY_DATE, true);
            }
        }
        return dateValidation;
    }

    public boolean checkIfDid(String str) {
        return str.toLowerCase().startsWith("did:");

    }

}
