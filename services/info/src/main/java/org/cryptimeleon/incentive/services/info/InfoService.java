package org.cryptimeleon.incentive.services.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cryptimeleon.incentive.crypto.Setup;
import org.cryptimeleon.incentive.crypto.model.IncentivePublicParameters;
import org.cryptimeleon.incentive.crypto.model.keys.provider.ProviderKeyPair;
import org.cryptimeleon.math.serialization.converter.JSONConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Service
public class InfoService {
    Logger logger = LoggerFactory.getLogger(InfoService.class);

    @Getter
    private String serializedPublicParameters;
    @Getter
    private String serializedProviderPublicKey;
    @Getter
    private String serializedProviderSecretKey;

    private IncentivePublicParameters pp;
    private ProviderKeyPair providerKeyPair;

    @Value("${provider.shared-secret}")
    private String sharedSecret;

    @Value("${info.use-mcl}")
    private boolean useMcl;

    @PostConstruct
    public void init() {
        // Make sure shared secret is set
        if (sharedSecret.equals("")) {
            throw new IllegalArgumentException("Shared secret is not set.");
        }

        logger.info("Setting up a new incentive-system");
        if (useMcl) {
            logger.info("Generate pp using mcl");
            this.pp = Setup.trustedSetup(128, Setup.BilinearGroupChoice.Herumi_MCL);
        } else {
            logger.info("Generate pp using debug group");
            this.pp = Setup.trustedSetup(128, Setup.BilinearGroupChoice.Debug);
        }
        logger.info("Generate provider keypair");
        this.providerKeyPair = Setup.providerKeyGen(pp);
        logger.info("Serializing pp and keypair");
        JSONConverter jsonConverter = new JSONConverter();
        serializedPublicParameters = jsonConverter.serialize(pp.getRepresentation());
        serializedProviderPublicKey = jsonConverter.serialize(providerKeyPair.getPk().getRepresentation());
        serializedProviderSecretKey = jsonConverter.serialize(providerKeyPair.getSk().getRepresentation());
        logger.info("Setup finished");
    }

    public boolean verifyProviderSharedSecret(String providerSharedSecret) {
        return providerSharedSecret.equals(sharedSecret);
    }
}
