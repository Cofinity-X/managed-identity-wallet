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

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.managedidentitywallets.service.HoldersCredentialService;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * The type Holders credential controller.
 */

// TODO refactor openapi annotations to interface
// TODO refactor requestParams as pojo
@RestController
@RequiredArgsConstructor
@Tag(name = "Verifiable Credential - Holder")
public class HoldersCredentialController extends BaseController implements HoldersCredentialControllerApi {

    private final HoldersCredentialService holdersCredentialService;


    /**
     * Gets credentials.
     *
     * @param credentialId     the credential id
     * @param issuerIdentifier the issuer identifier
     * @param type             the type
     * @param sortColumn       the sort column
     * @param sortTpe          the sort tpe
     * @param principal        the principal
     * @return the credentials
     */
    @Override
    public ResponseEntity<PageImpl<VerifiableCredential>> getCredentials(
            final String credentialId,
            final String issuerIdentifier,
            final List<String> type,
            final String sortColumn,
            final String sortTpe,
            final int pageNumber,
            final int size,
            final Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(holdersCredentialService.getCredentials(
                                     credentialId,
                                     issuerIdentifier,
                                     type,
                                     sortColumn,
                                     sortTpe,
                                     pageNumber,
                                     size,
                                     getBPNFromToken(principal)
                             ));
    }

    /**
     * Issue credential response entity.
     *
     * @param data      the data
     * @param principal the principal
     * @return the response entity
     */
    @Override
    public ResponseEntity<VerifiableCredential> issueCredential(
            Map<String, Object> data,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(holdersCredentialService.issueCredential(data, getBPNFromToken(principal)));
    }
}
