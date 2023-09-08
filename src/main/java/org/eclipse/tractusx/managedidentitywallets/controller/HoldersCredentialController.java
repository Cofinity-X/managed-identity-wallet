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
import org.eclipse.tractusx.managedidentitywallets.domain.CredentialId;
import org.eclipse.tractusx.managedidentitywallets.domain.CredentialSearch;
import org.eclipse.tractusx.managedidentitywallets.domain.Identifier;
import org.eclipse.tractusx.managedidentitywallets.domain.SortColumn;
import org.eclipse.tractusx.managedidentitywallets.domain.SortType;
import org.eclipse.tractusx.managedidentitywallets.domain.TypeToSearch;
import org.eclipse.tractusx.managedidentitywallets.dto.HolderVerifiableCredentialSearch;
import org.eclipse.tractusx.managedidentitywallets.service.HoldersCredentialService;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The type Holders credential controller.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Verifiable Credential - Holder")
public class HoldersCredentialController extends BaseController implements HoldersCredentialControllerApi {

    private final HoldersCredentialService holdersCredentialService;


    /**
     * Gets credentials.
     *
     * @param credentialSearch Pojo holding all query params, if any
     * @param principal        the principal
     * @return the credentials
     */
    @Override
    public ResponseEntity<PageImpl<VerifiableCredential>> getCredentials(
            HolderVerifiableCredentialSearch credentialSearch,
            final Principal principal
    ) {

        CredentialSearch.Builder searchBuilder = CredentialSearch.builder();
        Optional.ofNullable(credentialSearch.getCredentialId())
                .ifPresent(c -> searchBuilder.withCredentialId(new CredentialId(c)));
        Optional.ofNullable(credentialSearch.getIssuerIdentifier())
                .ifPresent(hi -> searchBuilder.withIdentifier(new Identifier(hi)));
        Optional.ofNullable(credentialSearch.getType())
                .ifPresent(t -> {
                    List<TypeToSearch> l = t.stream().map(TypeToSearch::valueOfType).toList();
                    searchBuilder.withTypesToSearch(l);
                });

        searchBuilder.withSortColumn(SortColumn.valueOfColumn(credentialSearch.getSortColumn()))
                     .withSortType(SortType.valueOf(credentialSearch.getSortType()))
                     .withPageNumber(credentialSearch.getPageNumber())
                     .withPageSize(credentialSearch.getSize())
                     .withCallerBpn(new Identifier(getBPNFromToken(principal)));
        
        return ResponseEntity.status(HttpStatus.OK)
                             .body(holdersCredentialService.getCredentials(
                                     searchBuilder.build()
                             ));
    }

    /**
     * Issue credential response entity.
     *
     * @param data      the data
     * @param principal the principal
     * @return the response entity
     */

    // TODO no, no, no, real DTO not just map, even though VerifiableCredential must be constructed with map, wtf
    // !!! never pass input straight into domain !!!
    @Override
    public ResponseEntity<VerifiableCredential> issueCredential(
            Map<String, Object> data,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(holdersCredentialService.issueCredential(data, getBPNFromToken(principal)));
    }


}
