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

import com.smartsensesolutions.java.commons.sort.SortType;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.managedidentitywallets.apidocs.HoldersCredentialControllerApiDocs;
import org.eclipse.tractusx.managedidentitywallets.constant.RestURI;
import org.eclipse.tractusx.managedidentitywallets.domain.BPN;
import org.eclipse.tractusx.managedidentitywallets.domain.CredentialId;
import org.eclipse.tractusx.managedidentitywallets.domain.CredentialSearch;
import org.eclipse.tractusx.managedidentitywallets.domain.Identifier;
import org.eclipse.tractusx.managedidentitywallets.domain.SortColumn;
import org.eclipse.tractusx.managedidentitywallets.domain.TypeToSearch;
import org.eclipse.tractusx.managedidentitywallets.dto.HolderVerifiableCredentialSearch;
import org.eclipse.tractusx.managedidentitywallets.service.HoldersCredentialService;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@Slf4j
public class HoldersCredentialController extends BaseController {

    private final HoldersCredentialService holdersCredentialService;

    /**
     * Gets credentials.
     *
     * @param credentialSearch object holding all query parameters including default
     *                         values
     * @param principal        the principal
     * @return the credentials
     */
    @GetMapping(path = RestURI.CREDENTIALS, produces = MediaType.APPLICATION_JSON_VALUE)
    @HoldersCredentialControllerApiDocs.GetCredentialsApiDocs
    public ResponseEntity<PageImpl<VerifiableCredential>> getCredentials(
            HolderVerifiableCredentialSearch credentialSearch,
            final Principal principal) {

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

        searchBuilder
                .withSort(SortColumn.valueOfColumn(credentialSearch.getSortColumn()),
                        SortType.valueOf(credentialSearch.getSortType().toUpperCase()))
                .withPageNumber(credentialSearch.getPageNumber())
                .withPageSize(credentialSearch.getSize())
                .withCallerBpn(new BPN(getBPNFromToken(principal)));

        return ResponseEntity.status(HttpStatus.OK)
                .body(holdersCredentialService.getCredentials(
                        searchBuilder.build()));
    }

    /**
     * Issue credential response entity.
     *
     * @param data      the data
     * @param principal the principal
     * @return the response entity`
     */
    @PostMapping(path = RestURI.CREDENTIALS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @HoldersCredentialControllerApiDocs.IssueCredentialApiDoc
    public ResponseEntity<VerifiableCredential> issueCredential(
            @RequestBody Map<String, Object> data,
            Principal principal) {
        VerifiableCredential verifiableCredential = null;
        try {
            // validates the input (hopefully), then pass to domain
            verifiableCredential = new VerifiableCredential(data);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        // TODO verfiy values of verifiableCredential

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(holdersCredentialService.issueCredential(
                        verifiableCredential,
                        getBPNFromToken(principal)));
    }

}
