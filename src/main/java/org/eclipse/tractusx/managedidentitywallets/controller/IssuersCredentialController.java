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

package org.eclipse.tractusx.managedidentitywallets.controller;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.managedidentitywallets.dto.IssueDismantlerCredentialRequest;
import org.eclipse.tractusx.managedidentitywallets.dto.IssueFrameworkCredentialRequest;
import org.eclipse.tractusx.managedidentitywallets.dto.IssueMembershipCredentialRequest;
import org.eclipse.tractusx.managedidentitywallets.service.IssuersCredentialService;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * The type Issuers credential controller.
 */

// TODO refactor requestParams as pojo
@RestController
@RequiredArgsConstructor
public class IssuersCredentialController extends BaseController implements IssuersCredentialServiceControllerApi {


    private final IssuersCredentialService issuersCredentialService;


    /**
     * Gets credentials.
     *
     * @param credentialId     the credential id
     * @param holderIdentifier the holder identifier
     * @param type             the type
     * @param pageNumber       the page number
     * @param size             the size
     * @param sortColumn       the sort column
     * @param sortTpe          the sort tpe
     * @param principal        the principal
     * @return the credentials
     */
    @Override
    public ResponseEntity<PageImpl<VerifiableCredential>> getCredentials(
            String credentialId,
            String holderIdentifier,
            List<String> type,
            int pageNumber,
            int size,
            String sortColumn,
            String sortTpe,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(issuersCredentialService.getCredentials(
                                     credentialId,
                                     holderIdentifier,
                                     type,
                                     sortColumn,
                                     sortTpe,
                                     pageNumber,
                                     size,
                                     getBPNFromToken(principal)
                             ));
    }

    /**
     * Issue membership credential response entity.
     *
     * @param issueMembershipCredentialRequest the issue membership credential request
     * @param principal                        the principal
     * @return the response entity
     */
    @Override
    public ResponseEntity<VerifiableCredential> issueMembershipCredential(
            IssueMembershipCredentialRequest issueMembershipCredentialRequest,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(issuersCredentialService.issueMembershipCredential(
                                     issueMembershipCredentialRequest,
                                     getBPNFromToken(principal)
                             ));
    }

    /**
     * Issue dismantler credential response entity.
     *
     * @param request   the request
     * @param principal the principal
     * @return the response entity
     */
    @Override
    public ResponseEntity<VerifiableCredential> issueDismantlerCredential(
            IssueDismantlerCredentialRequest request,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(issuersCredentialService.issueDismantlerCredential(
                                     request,
                                     getBPNFromToken(principal)
                             ));
    }

    /**
     * Issue framework credential response entity.
     *
     * @param request   the request
     * @param principal the principal
     * @return the response entity
     */
    public ResponseEntity<VerifiableCredential> issueFrameworkCredential(
            IssueFrameworkCredentialRequest request,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(issuersCredentialService.issueFrameworkCredential(
                                     request,
                                     getBPNFromToken(principal)
                             ));
    }

    /**
     * Credentials validation response entity.
     *
     * @param data                     the data
     * @param withCredentialExpiryDate the with credential expiry date
     * @return the response entity
     */
    @Override
    public ResponseEntity<Map<String, Object>> credentialsValidation(
            Map<String, Object> data,
            boolean withCredentialExpiryDate
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(issuersCredentialService.credentialsValidation(data, withCredentialExpiryDate));
    }

    /**
     * Issue credential response entity.
     *
     * @param holderDid the holder did
     * @param data      the data
     * @param principal the principal
     * @return the response entity
     */
    @Override
    public ResponseEntity<VerifiableCredential> issueCredentialUsingBaseWallet(
            String holderDid,
            Map<String, Object> data,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(issuersCredentialService.issueCredentialUsingBaseWallet(
                                     holderDid,
                                     data,
                                     getBPNFromToken(principal)
                             ));
    }
}
