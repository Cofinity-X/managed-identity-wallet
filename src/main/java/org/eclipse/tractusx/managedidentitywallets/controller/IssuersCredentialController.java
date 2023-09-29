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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.managedidentitywallets.apidocs.IssuersCredentialControllerApiDocs.GetCredentialsApiDocs;
import org.eclipse.tractusx.managedidentitywallets.apidocs.IssuersCredentialControllerApiDocs.IssueDismantlerCredentialApiDoc;
import org.eclipse.tractusx.managedidentitywallets.apidocs.IssuersCredentialControllerApiDocs.IssueFrameworkCredentialApiDocs;
import org.eclipse.tractusx.managedidentitywallets.apidocs.IssuersCredentialControllerApiDocs.IssueMembershipCredentialApiDoc;
import org.eclipse.tractusx.managedidentitywallets.apidocs.IssuersCredentialControllerApiDocs.IssueVerifiableCredentialUsingBaseWalletApiDocs;
import org.eclipse.tractusx.managedidentitywallets.apidocs.IssuersCredentialControllerApiDocs.ValidateVerifiableCredentialApiDocs;
import org.eclipse.tractusx.managedidentitywallets.constant.RestURI;
import org.eclipse.tractusx.managedidentitywallets.domain.ActivityType;
import org.eclipse.tractusx.managedidentitywallets.domain.BPN;
import org.eclipse.tractusx.managedidentitywallets.domain.CredentialId;
import org.eclipse.tractusx.managedidentitywallets.domain.Identifier;
import org.eclipse.tractusx.managedidentitywallets.domain.CredentialSortColumn;
import org.eclipse.tractusx.managedidentitywallets.domain.TypeToSearch;
import org.eclipse.tractusx.managedidentitywallets.domain.command.CredentialSearchCommand;
import org.eclipse.tractusx.managedidentitywallets.domain.command.IssueDismantlerCredentialCommand;
import org.eclipse.tractusx.managedidentitywallets.domain.command.IssueFrameworkCredentialCommand;
import org.eclipse.tractusx.managedidentitywallets.domain.command.IssueMembershipCredentialCommand;
import org.eclipse.tractusx.managedidentitywallets.dto.IssueDismantlerCredentialRequest;
import org.eclipse.tractusx.managedidentitywallets.dto.IssueFrameworkCredentialRequest;
import org.eclipse.tractusx.managedidentitywallets.dto.IssueMembershipCredentialRequest;
import org.eclipse.tractusx.managedidentitywallets.dto.IssuerVerifiableCredentialSearch;
import org.eclipse.tractusx.managedidentitywallets.service.IssuersCredentialService;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The type Issuers credential controller.
 */

@RestController
@RequiredArgsConstructor
@Slf4j
public class IssuersCredentialController extends BaseController {

    private final IssuersCredentialService issuersCredentialService;

    /**
     * Gets credentials.
     *
     * @param credentialSearch object holding all query parameters including default values
     * @param principal        the principal
     * @return the credentials
     */
    @GetMapping(path = RestURI.ISSUERS_CREDENTIALS, produces = MediaType.APPLICATION_JSON_VALUE)
    @GetCredentialsApiDocs
    public ResponseEntity<PageImpl<VerifiableCredential>> getCredentials(
            IssuerVerifiableCredentialSearch credentialSearch,
            Principal principal
    ) {

        CredentialSearchCommand.Builder searchBuilder = CredentialSearchCommand.builder();
        Optional.ofNullable(credentialSearch.getCredentialId())
                .ifPresent(c -> searchBuilder.withCredentialId(new CredentialId(c)));
        Optional.ofNullable(credentialSearch.getHolderIdentifier())
                .ifPresent(hi -> searchBuilder.withIdentifier(new Identifier(hi)));
        Optional.ofNullable(credentialSearch.getType())
                .ifPresent(t -> {
                    List<TypeToSearch> l = t.stream().map(TypeToSearch::valueOfType).toList();
                    searchBuilder.withTypesToSearch(l);
                });

        searchBuilder.withSort(
                             CredentialSortColumn.valueOfColumn(credentialSearch.getSortColumn()),
                             SortType.valueOf(credentialSearch.getSortType().toUpperCase())
                     )
                     .withPageNumber(credentialSearch.getPageNumber())
                     .withPageSize(credentialSearch.getSize())
                     .withCallerBpn(getBPNFromToken(principal));

        return ResponseEntity.status(HttpStatus.OK)
                             .body(issuersCredentialService.getCredentials(
                                     searchBuilder.build()));
    }

    /**
     * Issue membership credential response entity.
     *
     * @param issueMembershipCredentialRequest the issue membership credential request
     * @param principal                        the principal
     * @return the response entity
     */
    @PostMapping(path = RestURI.CREDENTIALS_ISSUER_MEMBERSHIP, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @IssueMembershipCredentialApiDoc
    public ResponseEntity<VerifiableCredential> issueMembershipCredential(
            @Valid @RequestBody IssueMembershipCredentialRequest issueMembershipCredentialRequest,
            Principal principal
    ) {

        IssueMembershipCredentialCommand cmd = new IssueMembershipCredentialCommand(
                new BPN(issueMembershipCredentialRequest.getBpn()), getBPNFromToken(principal));

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(issuersCredentialService.issueMembershipCredential(cmd));
    }

    /**
     * Issue dismantler credential response entity.
     *
     * @param request   the request
     * @param principal the principal
     * @return the response entity
     */
    @PostMapping(path = RestURI.CREDENTIALS_ISSUER_DISMANTLER, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @IssueDismantlerCredentialApiDoc
    public ResponseEntity<VerifiableCredential> issueDismantlerCredential(
            @Valid @RequestBody IssueDismantlerCredentialRequest request,
            Principal principal
    ) {

        IssueDismantlerCredentialCommand cmd = IssueDismantlerCredentialCommand
                .builder()
                .withActivityType(ActivityType.valueOfActivity(
                        request.getActivityType()))
                .withBpn(new BPN(request.getBpn()))
                .withCaller(getBPNFromToken(
                        principal))
                .withAllowedVehicleBrands(request.getAllowedVehicleBrands())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(issuersCredentialService.issueDismantlerCredential(cmd));
    }

    /**
     * Issue framework credential response entity.
     *
     * @param request   the request
     * @param principal the principal
     * @return the response entity
     */
    @PostMapping(path = RestURI.API_CREDENTIALS_ISSUER_FRAMEWORK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @IssueFrameworkCredentialApiDocs
    public ResponseEntity<VerifiableCredential> issueFrameworkCredential(
            @Valid @RequestBody IssueFrameworkCredentialRequest request,
            Principal principal
    ) {

        IssueFrameworkCredentialCommand cmd;
        try {
            cmd = IssueFrameworkCredentialCommand.builder()
                                                 .setContractTemplate(request.getContractTemplate())
                                                 .setContractVersion(request.getContractVersion())
                                                 .setHolderIdentifier(new BPN(request.getHolderIdentifier()))
                                                 .setType(
                                                         IssueFrameworkCredentialCommand.FrameworkCredentialType.valueOfType(
                                                                 request.getType())

                                                 )
                                                 .setCaller(getBPNFromToken(principal))
                                                 .build();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }


        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(issuersCredentialService.issueFrameworkCredential(cmd));
    }

    /**
     * Credentials validation response entity.
     *
     * @param data                     the data
     * @param withCredentialExpiryDate the with credential expiry date
     * @return the response entity
     */
    @PostMapping(path = RestURI.CREDENTIALS_VALIDATION, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ValidateVerifiableCredentialApiDocs
    public ResponseEntity<Map<String, Object>> credentialsValidation(
            @RequestBody Map<String, Object> data,
            @RequestParam(name = "withCredentialExpiryDate", defaultValue = "false", required = false) boolean withCredentialExpiryDate
    ) {

        VerifiableCredential verifiableCredential = new VerifiableCredential(data);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(issuersCredentialService.credentialsValidation(
                                     verifiableCredential,
                                     withCredentialExpiryDate
                             ));
    }

    /**
     * Issue credential response entity.
     *
     * @param holderDid the holder did
     * @param data      the data
     * @param principal the principal
     * @return the response entity
     */
    @PostMapping(path = RestURI.ISSUERS_CREDENTIALS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @IssueVerifiableCredentialUsingBaseWalletApiDocs
    public ResponseEntity<VerifiableCredential> issueCredentialUsingBaseWallet(
            @RequestParam(name = "holderDid") String holderDid,
            @RequestBody Map<String, Object> data,
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
