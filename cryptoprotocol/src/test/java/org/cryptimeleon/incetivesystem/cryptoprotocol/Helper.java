package org.cryptimeleon.incetivesystem.cryptoprotocol;

import org.cryptimeleon.craco.sig.sps.eq.SPSEQSignature;
import org.cryptimeleon.incentivesystem.cryptoprotocol.model.IncentivePublicParameters;
import org.cryptimeleon.incentivesystem.cryptoprotocol.model.Token;
import org.cryptimeleon.incentivesystem.cryptoprotocol.model.keys.provider.ProviderKeyPair;
import org.cryptimeleon.incentivesystem.cryptoprotocol.model.keys.user.UserKeyPair;

public class Helper {
    static Token generateToken(IncentivePublicParameters pp, UserKeyPair userKeyPair, ProviderKeyPair providerKeyPair) {
        var vectorH = providerKeyPair.getPk().getH();
        var zp = pp.getBg().getZn();
        // Manually create a token since issue-join is not yet implemented
        var encryptionSecretKey = zp.getUniformlyRandomNonzeroElement();
        var dsrd1 = zp.getUniformlyRandomElement();
        var dsrd2 = zp.getUniformlyRandomElement();
        var z = zp.getUniformlyRandomElement();
        var t = zp.getUniformlyRandomElement();
        var points = 0;
        var pointsZp = zp.valueOf(points);
        var c1 = vectorH.get(0).pow(userKeyPair.getSk().getUsk())
                .op(vectorH.get(1).pow(encryptionSecretKey))
                .op(vectorH.get(2).pow(dsrd1))
                .op(vectorH.get(3).pow(dsrd2))
                .op(vectorH.get(4).pow(pointsZp))
                .op(vectorH.get(5).pow(z))
                .op(pp.getH7().pow(t)).compute();
        var c2 = pp.getG1();

        return new Token(
                c1,
                c2,
                encryptionSecretKey,
                dsrd1,
                dsrd2,
                z,
                t,
                pointsZp,
                (SPSEQSignature) pp.getSpsEq().sign(
                        providerKeyPair.getSk().getSkSpsEq(),
                        c1,
                        c2
                )
        );

    }

}
