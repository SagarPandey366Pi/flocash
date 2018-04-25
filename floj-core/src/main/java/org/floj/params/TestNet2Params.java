/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.floj.params;

import static com.google.common.base.Preconditions.checkState;

import org.floj.core.Utils;

/**
 * Parameters for the old version 2 testnet. This is not useful to you - it exists only because some unit tests are
 * based on it.
 */
public class TestNet2Params extends AbstractFLONetParams {
    public static final int TESTNET_MAJORITY_WINDOW = 100;
    public static final int TESTNET_MAJORITY_REJECT_BLOCK_OUTDATED = 75;
    public static final int TESTNET_MAJORITY_ENFORCE_BLOCK_UPGRADE = 51;

    public TestNet2Params() {
        super();
        maxTarget = Utils.decodeCompactBits(0x1e0fffff);
        dumpedPrivateKeyHeader = 239; //SECRET_KEY
        addressHeader = 115; //PUBKEY_ADDRESS 111;
        p2shHeader = 198;  //SCRIPT_ADDRESS 196;
        acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        port = 17312;  //flo 18333;
        packetMagic = 0xfdc05af2; //flo 0x0b110907;
        bip32HeaderPub = 0x013440e2; //flo 0x043587CF;
        bip32HeaderPriv = 0x01343c23; //flo 0x04358394;

        majorityEnforceBlockUpgrade = TESTNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = TESTNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = TESTNET_MAJORITY_WINDOW;

//        interval = INTERVAL;
//        targetTimespan = TARGET_TIMESPAN;
        genesisBlock.setDifficultyTarget(0x1e0ffff0L); //flo 0x1d00ffffL);
        genesisBlock.setTime(1371387277L); //flo 1296688602L);
        genesisBlock.setNonce(1000580675); //flo 414098458);
        id = ID_TESTNET;
        subsidyDecreaseBlockCount = 800000; ; // flo 210000;
        spendableCoinbaseDepth = 100; //same as flo
        String genesisHash = genesisBlock.getHashAsString();
        checkState(genesisHash.equals("9b7bc86236c34b5e3a39367c036b7fe8807a966c22a7a1f0da2a198a27e03731")); //flo 000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943"));
        dnsSeeds = null;
        addrSeeds = null;
    }

    private static TestNet2Params instance;
    public static synchronized TestNet2Params get() {
        if (instance == null) {
            instance = new TestNet2Params();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return null;
    }
}
