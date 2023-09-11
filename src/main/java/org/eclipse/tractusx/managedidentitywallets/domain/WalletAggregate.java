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

package org.eclipse.tractusx.managedidentitywallets.domain;

import lombok.SneakyThrows;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.eclipse.tractusx.managedidentitywallets.utils.EncryptionUtils;
import org.eclipse.tractusx.ssi.lib.crypt.IKeyGenerator;
import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.crypt.jwk.JsonWebKey;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559Generator;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebFactory;
import org.eclipse.tractusx.ssi.lib.exception.KeyGenerationException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocumentBuilder;
import org.eclipse.tractusx.ssi.lib.model.did.JWKVerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.did.JWKVerificationMethodBuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class WalletAggregate {


    private final String host;

    private final String bpn;

    private final List<URI> contextUrls;

    private final KeyPair keyPair;

    private final String keyId = UUID.randomUUID().toString();

    private final Did did;


    private WalletAggregate(Builder builder) {
        host = builder.host;
        bpn = builder.bpn;
        contextUrls = builder.contextUrls;
        keyPair = getKeyPair();
        did = DidWebFactory.fromHostnameAndPath(host, bpn);
    }

    public String getEncryptedPublicKey(EncryptionUtils encryptionUtils) {
        return encryptionUtils.encrypt(getPublicKeyString(keyPair.getPublicKey().asByte()));
    }


    public String getEncryptedPrivateKey(EncryptionUtils encryptionUtils) {
        return encryptionUtils.encrypt(getPrivateKeyString(keyPair.getPrivateKey().asByte()));
    }

    public Did getDid() {
        return did;
    }

    public String getKeyId() {
        return keyId;
    }


    public DidDocument getDocument() {
        JsonWebKey jwk = null;

        try {
            jwk = new JsonWebKey(keyId, keyPair.getPublicKey(), keyPair.getPrivateKey());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JWKVerificationMethod jwkVerificationMethod =
                new JWKVerificationMethodBuilder().did(did).jwk(jwk).build();

        DidDocumentBuilder didDocumentBuilder = new DidDocumentBuilder();
        didDocumentBuilder.id(did.toUri());
        didDocumentBuilder.verificationMethods(List.of(jwkVerificationMethod));
        DidDocument didDocument = didDocumentBuilder.build();
        //modify context URLs
        List<URI> context = didDocument.getContext();
        List<URI> mutableContext = new ArrayList<>(context);
        contextUrls.forEach(uri -> {
            if (!mutableContext.contains(uri)) {
                mutableContext.add(uri);
            }
        });
        didDocument.put("@context", mutableContext);
        didDocument = DidDocument.fromJson(didDocument.toJson());
        return didDocument;
    }

    @SneakyThrows
    private String getPrivateKeyString(byte[] privateKeyBytes) {
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        pemWriter.writeObject(new PemObject("PRIVATE KEY", privateKeyBytes));
        pemWriter.flush();
        pemWriter.close();
        return stringWriter.toString();
    }

    @SneakyThrows
    private String getPublicKeyString(byte[] publicKeyBytes) {
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        pemWriter.writeObject(new PemObject("PUBLIC KEY", publicKeyBytes));
        pemWriter.flush();
        pemWriter.close();
        return stringWriter.toString();
    }

    private KeyPair getKeyPair() {
        IKeyGenerator keyGenerator = new x21559Generator();
        try {
            return keyGenerator.generateKey();
        } catch (KeyGenerationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String host;

        private String bpn;

        private List<URI> contextUrls;


        public WalletAggregate build() throws IllegalStateException {
            validate();

            return new WalletAggregate(this);
        }

        public Builder withHost(final String host) {
            this.host = Objects.requireNonNull(host);
            return this;
        }

        public Builder withBpn(final String bpn) {
            this.bpn = Objects.requireNonNull(bpn);
            return this;
        }

        public Builder withContextUrls(final List<URI> contextUrls) {
            this.contextUrls = Objects.requireNonNull(contextUrls);
            return this;
        }

        private void validate() throws IllegalStateException {
            try {
                Objects.requireNonNull(host);
                Objects.requireNonNull(bpn);
                Objects.requireNonNull(contextUrls);
            } catch (NullPointerException e) {
                throw new IllegalStateException(e);
            }

            if (contextUrls.isEmpty()) {
                throw new IllegalStateException("contextUrls must not be empty");
            }

        }
    }
}
