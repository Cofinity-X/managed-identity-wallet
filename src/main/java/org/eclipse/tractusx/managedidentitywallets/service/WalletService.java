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

import com.smartsensesolutions.java.commons.FilterRequest;
import com.smartsensesolutions.java.commons.base.repository.BaseRepository;
import com.smartsensesolutions.java.commons.base.service.BaseService;
import com.smartsensesolutions.java.commons.sort.Sort;
import com.smartsensesolutions.java.commons.sort.SortType;
import com.smartsensesolutions.java.commons.specification.SpecificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.tractusx.managedidentitywallets.config.MIWSettings;
import org.eclipse.tractusx.managedidentitywallets.constant.StringPool;
import org.eclipse.tractusx.managedidentitywallets.dao.entity.HoldersCredential;
import org.eclipse.tractusx.managedidentitywallets.dao.entity.Wallet;
import org.eclipse.tractusx.managedidentitywallets.dao.entity.WalletKey;
import org.eclipse.tractusx.managedidentitywallets.dao.repository.HoldersCredentialRepository;
import org.eclipse.tractusx.managedidentitywallets.dao.repository.WalletRepository;
import org.eclipse.tractusx.managedidentitywallets.domain.BPN;
import org.eclipse.tractusx.managedidentitywallets.domain.Identifier;
import org.eclipse.tractusx.managedidentitywallets.domain.WalletAggregate;
import org.eclipse.tractusx.managedidentitywallets.domain.command.CreateWalletCommand;
import org.eclipse.tractusx.managedidentitywallets.domain.command.GetWalletCommand;
import org.eclipse.tractusx.managedidentitywallets.domain.command.StoreCredentialCommand;
import org.eclipse.tractusx.managedidentitywallets.exception.BadDataException;
import org.eclipse.tractusx.managedidentitywallets.exception.DuplicateWalletProblem;
import org.eclipse.tractusx.managedidentitywallets.exception.ForbiddenException;
import org.eclipse.tractusx.managedidentitywallets.utils.EncryptionUtils;
import org.eclipse.tractusx.managedidentitywallets.utils.Validate;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Wallet service.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WalletService extends BaseService<Wallet, Long> {

    /**
     * The constant BASE_WALLET_BPN_IS_NOT_MATCHING_WITH_REQUEST_BPN_FROM_TOKEN.
     */
    public static final String BASE_WALLET_BPN_IS_NOT_MATCHING_WITH_REQUEST_BPN_FROM_TOKEN = "Base wallet BPN is not matching with request BPN(from token)";

    private final WalletRepository walletRepository;

    private final MIWSettings miwSettings;

    private final EncryptionUtils encryptionUtils;

    private final WalletKeyService walletKeyService;

    private final HoldersCredentialRepository holdersCredentialRepository;

    private final SpecificationUtil<Wallet> walletSpecificationUtil;

    private final IssuersCredentialService issuersCredentialService;

    private final CommonService commonService;

    @Override
    protected BaseRepository<Wallet, Long> getRepository() {
        return walletRepository;
    }

    @Override
    protected SpecificationUtil<Wallet> getSpecificationUtil() {
        return walletSpecificationUtil;
    }

    /**
     * Store credential map.
     *
     * @param cmd the command containing all necessary data
     * @return the map
     */
    public Map<String, String> storeCredential(StoreCredentialCommand cmd) {
        Wallet wallet;

        if (commonService.checkIfDid(cmd.identifier().value())) {
            wallet = commonService.getWalletByDid(cmd.identifier().value());
        } else {
            wallet = commonService.getWalletByBPN(cmd.identifier().value());
        }
        // validate BPN access
        Validate.isFalse(cmd.caller().value().equalsIgnoreCase(wallet.getBpn()))
                .launch(new ForbiddenException("Wallet BPN is not matching with request BPN(from the token)"));

        // check type
        Validate.isTrue(cmd.verifiableCredential().getTypes().isEmpty())
                .launch(new BadDataException("Invalid types provided in credentials"));

        List<String> cloneTypes = new ArrayList<>(cmd.verifiableCredential().getTypes());
        cloneTypes.remove(VerifiableCredentialType.VERIFIABLE_CREDENTIAL);

        holdersCredentialRepository.save(HoldersCredential.builder()
                                                          .holderDid(wallet.getDid())
                                                          .issuerDid(cmd.verifiableCredential().getIssuer().toString())
                                                          .type(String.join(",", cloneTypes))
                                                          .data(cmd.verifiableCredential())
                                                          .selfIssued(false)
                                                          .stored(true) // credential is stored(not issued by MIW)
                                                          .credentialId(cmd.verifiableCredential().getId().toString())
                                                          .build());
        log.debug("VC type of {} stored for bpn ->{} with id-{}", cloneTypes, cmd.caller(), cmd.verifiableCredential().getId());
        return Map.of(
                "message",
                String.format("Credential with id %s has been successfully stored", cmd.verifiableCredential().getId())
        );
    }

    /**
     * Gets wallet by identifier.
     *
     * @param didOrBpn      the identifier (a BPN or DID)
     * @param withCredentials the with credentials
     * @param callerBpn       the caller bpn
     * @return the wallet by identifier
     */
    public Wallet getWalletByIdentifier(Identifier didOrBpn, boolean withCredentials, BPN callerBpn) {
        Wallet wallet;
        if (commonService.checkIfDid(didOrBpn.value())) {
            wallet = commonService.getWalletByDid(didOrBpn.value());
        } else {
            wallet = commonService.getWalletByBPN(didOrBpn.value());
        }
        // authority wallet can see all wallets
        if (!miwSettings.authorityWalletBpn().value().equals(callerBpn.value())) {
            // validate BPN access
            Validate.isFalse(callerBpn.value().equalsIgnoreCase(wallet.getBpn()))
                    .launch(new ForbiddenException("Wallet BPN is not matching with request BPN(from the token)"));
        }

        if (withCredentials) {
            wallet.setVerifiableCredentials(holdersCredentialRepository.getCredentialsByHolder(wallet.getDid()));
        }
        return wallet;
    }

    /**
     * Gets wallets.
     *
     * @param cmd the command containing all necessary data
     * @return the wallets
     */
    public Page<Wallet> getWallets(GetWalletCommand cmd) {
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setSize(cmd.size());
        filterRequest.setPage(cmd.pageNumber());

        Sort sort = new Sort();
        sort.setColumn(cmd.sortColumn().value);
        sort.setSortType(SortType.valueOf(cmd.sortType().getValue().toUpperCase()));
        filterRequest.setSort(sort);
        return filter(filterRequest);
    }

    /**
     * Create wallet wallet.
     *
     * @param cmd the command containing all necessary data
     * @return the wallet
     */
    @SneakyThrows
    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED)
    public Wallet createWallet(CreateWalletCommand cmd) {
        return createWallet(cmd.bpn(), cmd.name(), false, cmd.caller());
    }

    /**
     * Create wallet.
     *
     * @param bpn the bpn for which the wallet is created
     * @param callerBpn the BPN of the entity creating the wallet
     * @param name the name of the created wallet
     * @param authority whether the holders credential is self-issued
     * @return the wallet
     */
    @SneakyThrows
    private Wallet createWallet(BPN bpn, String name, boolean authority, BPN callerBpn) {
        validateCreateWallet(bpn, callerBpn);

        WalletAggregate walletAggregate = WalletAggregate.builder()
                                                         .withBpn(bpn)
                                                         .withHost(miwSettings.host())
                                                         .withContextUrls(miwSettings.didDocumentContextUrls())
                                                         .build();

        log.debug("did document created for bpn ->{}", StringEscapeUtils.escapeJava(bpn.value()));
        String encryptedPrivateKey = walletAggregate.getEncryptedPrivateKey(encryptionUtils);
        String encryptedPublicKey = walletAggregate.getEncryptedPublicKey(encryptionUtils);

        // Save wallet
        Wallet wallet = create(Wallet.builder()
                                     .didDocument(walletAggregate.getDocument())
                                     .bpn(bpn.value())
                                     .name(name)
                                     .did(walletAggregate.getDid().toUri().toString())
                                     .algorithm(StringPool.ED_25519)
                                     .build());

        // Save key
        walletKeyService.getRepository().save(WalletKey.builder()
                                                       .walletId(wallet.getId())
                                                       .keyId(walletAggregate.getKeyId())
                                                       .referenceKey("dummy ref key, removed once vault setup is ready")
                                                       .vaultAccessToken(
                                                               "dummy vault access token, removed once vault setup is ready")
                                                       .privateKey(encryptedPrivateKey)
                                                       .publicKey(encryptedPublicKey)
                                                       .build());

        log.debug("Wallet created for bpn ->{}", StringEscapeUtils.escapeJava(bpn.value()));

        Wallet issuerWallet = walletRepository.getByBpn(miwSettings.authorityWalletBpn().value());

        // issue BPN credentials
        issuersCredentialService.issueBpnCredential(issuerWallet, wallet, authority);

        return wallet;
    }

    /**
     * Create authority wallet on application start up, skip if already created.
     */

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED)
    public void createAuthorityWallet() {

        if (!walletRepository.existsByBpn(miwSettings.authorityWalletBpn().value())) {
            createWallet(
                    miwSettings.authorityWalletBpn(),
                    miwSettings.authorityWalletName(),
                    true,
                    miwSettings.authorityWalletBpn()
            );
            log.info(
                    "Authority wallet created with bpn {}",
                    StringEscapeUtils.escapeJava(miwSettings.authorityWalletBpn().value())
            );
        } else {
            log.info(
                    "Authority wallet exists with bpn {}",
                    StringEscapeUtils.escapeJava(miwSettings.authorityWalletBpn().value())
            );
        }

    }

    private void validateCreateWallet(BPN bpn, BPN callerBpn) {
        // check base wallet
        Validate.isFalse(callerBpn.value().equalsIgnoreCase(miwSettings.authorityWalletBpn().value()))
                .launch(new ForbiddenException(BASE_WALLET_BPN_IS_NOT_MATCHING_WITH_REQUEST_BPN_FROM_TOKEN));

        // check wallet already exists
        boolean exist = walletRepository.existsByBpn(bpn.value());
        if (exist) {
            throw new DuplicateWalletProblem("Wallet is already exists for bpn " + bpn.value());
        }
    }
}
