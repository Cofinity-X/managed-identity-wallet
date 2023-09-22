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

import com.nimbusds.jwt.JWTParser;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.managedidentitywallets.apidocs.PresentationControllerApiDocs.PostVerifiablePresentationApiDocs;
import org.eclipse.tractusx.managedidentitywallets.apidocs.PresentationControllerApiDocs.PostVerifiablePresentationValidationApiDocs;
import org.eclipse.tractusx.managedidentitywallets.constant.RestURI;
import org.eclipse.tractusx.managedidentitywallets.constant.StringPool;
import org.eclipse.tractusx.managedidentitywallets.domain.command.CreatePresentationCommand;
import org.eclipse.tractusx.managedidentitywallets.domain.command.ValidatePresentationCommand;
import org.eclipse.tractusx.managedidentitywallets.service.PresentationService;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentials;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The type Presentation controller.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
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
    public ResponseEntity<Map<String, Object>> createPresentation(
            @RequestBody Map<String, Object> data,
            @RequestParam(name = "audience", required = false) String audience,
            @RequestParam(name = "asJwt", required = false, defaultValue = "false") boolean asJwt,
            Principal principal
    ) {
        Object mayBeList = data.get(StringPool.VERIFIABLE_CREDENTIALS);

        // ensure that verifiableCredentials is a list
        if (!(mayBeList instanceof Collection<?>)) {
            log.error("VerifiableCredentials are not an array");
            return ResponseEntity.badRequest().build();
        }

        List<Object> credentialList = new ArrayList<>((Collection<?>) mayBeList);
        VerifiableCredentials credentials = new VerifiableCredentials();

        // ensure that list contains json payload
        for (final Object o : credentialList) {
            Map<String, Object> m = new HashMap<>();
            if (o instanceof Map<?, ?> map) {
                map.forEach((k, v) -> {
                    if ((k instanceof String key)) {
                        m.put(key, v);
                    }
                });
                credentials.add(new VerifiableCredential(m));
            }
        }

        CreatePresentationCommand.Builder builder = CreatePresentationCommand.builder()
                                                                             .setAsJwt(asJwt)
                                                                             .setVerifiableCredentials(credentials)
                                                                             .setCaller(getBPNFromToken(principal));
        Optional.ofNullable(audience).ifPresent(builder::setAudience);

        CreatePresentationCommand cmd = builder.build();
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(presentationService.createPresentation(cmd));
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
    public ResponseEntity<Map<String, Object>> validatePresentation(
            @RequestBody Map<String, Object> data,
            @Parameter(description = "Audience to validate in VP (Only supported in case of JWT formatted VP)") @RequestParam(name = "audience", required = false) String audience,
            @Parameter(description = "Pass true in case of VP is in JWT format") @RequestParam(name = "asJwt", required = false, defaultValue = "false") boolean asJwt,
            @Parameter(description = "Check expiry of VC(Only supported in case of JWT formatted VP)") @RequestParam(name = "withCredentialExpiryDate", required = false, defaultValue = "false") boolean withCredentialExpiryDate
    ) {

        ValidatePresentationCommand.Builder builder = ValidatePresentationCommand
                .builder()
                .setWithCredentialExpiryDate(withCredentialExpiryDate)
                .setAsJwt(asJwt);

        Optional.ofNullable(audience).ifPresent(builder::setAudience);


        Optional.ofNullable(data.get("vp"))
                .filter(String.class::isInstance)
                .ifPresentOrElse(s -> {
                    try {
                        builder.setVpJwt(JWTParser.parse((String) s));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }, () -> {
                    Map<String, Object> m = new HashMap<>();
                    if (data.get("vp") instanceof Map<?, ?> map) {
                        map.forEach((k, v) -> {
                            if ((k instanceof String key)) {
                                m.put(key, v);
                            }
                        });
                        builder.setVpJson(new VerifiablePresentation(m));
                    }
                });

        ValidatePresentationCommand cmd;
        try{
            cmd = builder.build();
        }catch(IllegalStateException e){
            log.error(null, e);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.OK)
                             .body(presentationService.validatePresentation(cmd));
    }
}
