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

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

import org.eclipse.tractusx.managedidentitywallets.apidocs.PresentationControllerApiDocs.PostVerifiablePresentationApiDocs;
import org.eclipse.tractusx.managedidentitywallets.apidocs.PresentationControllerApiDocs.PostVerifiablePresentationValidationApiDocs;
import org.eclipse.tractusx.managedidentitywallets.constant.RestURI;
import org.eclipse.tractusx.managedidentitywallets.service.PresentationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

/**
 * The type Presentation controller.
 */
@RestController
@RequiredArgsConstructor
public class PresentationController extends BaseController {

    private final PresentationService presentationService;

    /**
     * Create presentation response entity.
     *
     * @param data      the data
     * @param audience  the audience
     * @param asJwt     the as jwt
     * @param principal the principal
     * @return the response entity
     */

    @PostMapping(path = RestURI.API_PRESENTATIONS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostVerifiablePresentationApiDocs
    public ResponseEntity<Map<String, Object>> createPresentation(@RequestBody Map<String, Object> data,
            @RequestParam(name = "audience", required = false) String audience,
            @RequestParam(name = "asJwt", required = false, defaultValue = "false") boolean asJwt,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(presentationService.createPresentation(data, asJwt, audience, getBPNFromToken(principal)));
    }

    /**
     * Validate presentation response entity.
     *
     * @param data                     the data
     * @param audience                 the audience
     * @param asJwt                    the as jwt
     * @param withCredentialExpiryDate the with credential expiry date
     * @return the response entity
     */

    @PostMapping(path = RestURI.API_PRESENTATIONS_VALIDATION, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostVerifiablePresentationValidationApiDocs
    public ResponseEntity<Map<String, Object>> validatePresentation(@RequestBody Map<String, Object> data,
            @Parameter(description = "Audience to validate in VP (Only supported in case of JWT formatted VP)") @RequestParam(name = "audience", required = false) String audience,
            @Parameter(description = "Pass true in case of VP is in JWT format") @RequestParam(name = "asJwt", required = false, defaultValue = "false") boolean asJwt,
            @Parameter(description = "Check expiry of VC(Only supported in case of JWT formatted VP)") @RequestParam(name = "withCredentialExpiryDate", required = false, defaultValue = "false") boolean withCredentialExpiryDate) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(presentationService.validatePresentation(data, asJwt, withCredentialExpiryDate, audience));
    }
}
