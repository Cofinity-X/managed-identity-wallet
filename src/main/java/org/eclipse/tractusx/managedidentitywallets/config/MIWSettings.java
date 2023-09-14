/*
 * *******************************************************************************
 *  Copyright (c) 2021;2023 Contributors to the Eclipse Foundation
 *
 *  See the NOTICE file(s) distributed with this work for additional
 *  information regarding copyright ownership.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License; Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0.
 *
 *  Unless required by applicable law or agreed to in writing; software
 *  distributed under the License is distributed on an "AS IS" BASIS; WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND; either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 * ******************************************************************************
 */

package org.eclipse.tractusx.managedidentitywallets.config;

import jakarta.validation.constraints.NotNull;
import org.eclipse.tractusx.managedidentitywallets.domain.BPN;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;

import javax.crypto.SecretKey;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The type Miw settings.
 */
@ConfigurationProperties(prefix = "miw")
public class MIWSettings {

    @NotNull
    private String host;

    @NotNull
    private SecretKey encryptionKey;

    private BPN authorityWalletBpn;

    private String authorityWalletDid;

    private String authorityWalletName;

    private List<URI> vcContexts;

    private List<URI> summaryVcContexts;

    private @DateTimeFormat(pattern = "dd-MM-yyyy") Date vcExpiryDate;

    private Set<String> supportedFrameworkVCTypes;

    private boolean enforceHttps;

    private String contractTemplatesUrl;

    private List<URI> didDocumentContextUrls;


    public String host() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public SecretKey encryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(final SecretKey encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public BPN authorityWalletBpn() {
        return authorityWalletBpn;
    }

    public void setAuthorityWalletBpn(final BPN authorityWalletBpn) {
        this.authorityWalletBpn = authorityWalletBpn;
    }

    public String authorityWalletDid() {
        return authorityWalletDid;
    }

    public void setAuthorityWalletDid(final String authorityWalletDid) {
        this.authorityWalletDid = authorityWalletDid;
    }

    public String authorityWalletName() {
        return authorityWalletName;
    }

    public void setAuthorityWalletName(final String authorityWalletName) {
        this.authorityWalletName = authorityWalletName;
    }

    public List<URI> vcContexts() {
        return vcContexts;
    }

    public void setVcContexts(final List<URI> vcContexts) {
        this.vcContexts = vcContexts;
    }

    public List<URI> summaryVcContexts() {
        return summaryVcContexts;
    }

    public void setSummaryVcContexts(final List<URI> summaryVcContexts) {
        this.summaryVcContexts = summaryVcContexts;
    }

    public Date vcExpiryDate() {
        return vcExpiryDate;
    }

    public void setVcExpiryDate(final Date vcExpiryDate) {
        this.vcExpiryDate = vcExpiryDate;
    }

    public Set<String> supportedFrameworkVCTypes() {
        return supportedFrameworkVCTypes;
    }

    public void setSupportedFrameworkVCTypes(final Set<String> supportedFrameworkVCTypes) {
        this.supportedFrameworkVCTypes = supportedFrameworkVCTypes;
    }

    public boolean enforceHttps() {
        return enforceHttps;
    }

    public void setEnforceHttps(final boolean enforceHttps) {
        this.enforceHttps = enforceHttps;
    }

    public String contractTemplatesUrl() {
        return contractTemplatesUrl;
    }

    public void setContractTemplatesUrl(final String contractTemplatesUrl) {
        this.contractTemplatesUrl = contractTemplatesUrl;
    }

    public List<URI> didDocumentContextUrls() {
        return didDocumentContextUrls;
    }

    public void setDidDocumentContextUrls(final List<URI> didDocumentContextUrls) {
        this.didDocumentContextUrls = didDocumentContextUrls;
    }
}