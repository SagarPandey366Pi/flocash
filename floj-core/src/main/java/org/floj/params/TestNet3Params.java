/*
 * Copyright 2013 Google Inc.
 * Copyright 2014 Andreas Schildbach
 * Copyright 2018 DeSoto Inc
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

import java.math.BigInteger;
import java.util.Date;

import org.floj.core.Block;
import org.floj.core.NetworkParameters;
import org.floj.core.StoredBlock;
import org.floj.core.Utils;
import org.floj.core.VerificationException;
import org.floj.store.BlockStore;
import org.floj.store.BlockStoreException;

import static com.google.common.base.Preconditions.checkState;

/**
 * Parameters for the testnet, a separate public instance of FLO that has relaxed rules suitable for development
 * and testing of applications and new FLO versions.
 */
public class TestNet3Params extends AbstractFLONetParams {
    public TestNet3Params() {
        super();
        // Genesis hash is 000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943
//        interval = INTERVAL;
//        targetTimespan = TARGET_TIMESPAN;
        maxTarget = Utils.decodeCompactBits(0x1e0fffff); // flo 0x1d00ffffL);
        dumpedPrivateKeyHeader = 239; //SECRET_KEY
        addressHeader = 115;  //PUBKEY_ADDRESS
        p2shHeader = 198;  //SCRIPT_ADDRESS
        acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        port = 17312;  //flo 18333;
        packetMagic = 0xfdc05af2; //flo 0x0b110907;
        bip32HeaderPub = 0x013440e2; //flo 0x043587CF;
        bip32HeaderPriv = 0x01343c23; //flo 0x04358394;

        majorityEnforceBlockUpgrade = TestNet2Params.TESTNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = TestNet2Params.TESTNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = TestNet2Params.TESTNET_MAJORITY_WINDOW;
        
        // Difficulty adjustments
        fPowAllowMinDifficultyBlocks=true;
        fPowNoRetargeting=false;
        nPowTargetSpacing = 40; // 40s block time
        // V1
        nTargetTimespan_Version1 = 60 * 60;
        nInterval_Version1 = nTargetTimespan_Version1 / nPowTargetSpacing;
        nMaxAdjustUp_Version1 = 75;
        nMaxAdjustDown_Version1 = 300;
        nAveragingInterval_Version1 = nInterval_Version1;
        // V2
        nHeight_Difficulty_Version2 = 50000;
        nInterval_Version2 = 15;
        nMaxAdjustUp_Version2 = 75;
        nMaxAdjustDown_Version2 = 300;
        nAveragingInterval_Version2 = nInterval_Version2;
        // V3
        nHeight_Difficulty_Version3 = 60000;
        nInterval_Version3 = 1;
        nMaxAdjustUp_Version3 = 2;
        nMaxAdjustDown_Version3 = 3;
        nAveragingInterval_Version3 = 6;

        genesisBlock.setDifficultyTarget(0x1e0ffff0L); //flo 0x1d00ffffL);
        genesisBlock.setTime(1371387277L); //flo 1296688602L);
        genesisBlock.setNonce(1000580675); //flo 414098458);
        id = ID_TESTNET;
        subsidyDecreaseBlockCount = 800000; ; // flo 210000;
        spendableCoinbaseDepth = 100; //same as flo
        String genesisHash = genesisBlock.getHashAsString();
        checkState(genesisHash.equals("9b7bc86236c34b5e3a39367c036b7fe8807a966c22a7a1f0da2a198a27e03731")); //flo 000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943"));
        alertSigningKey = Utils.HEX.decode("0456f0d9f60a0a7d9b92f2366c75106c6ce8430b76d49186e41866e3bcbbae0161a04cd423bfb055ae749c6847369d73b26ca16b9e82d0f99fc4611e0fb2251cb4"); //flo 04302390343f91cc401d56d68b123028bf52e5fca1939df127f63c6467cdf9c8e2c14b61104cf817d0b780da337893ecc4aaff1309e536162dabbdb45200ca2b0a");

        dnsSeeds = new String[] {
        		"testnet.oip.fun"
//                "testnet-seed.flo.jonasschnelli.ch", // Jonas Schnelli
//                "testnet-seed.bluematt.me",              // Matt Corallo
//                "testnet-seed.flo.petertodd.org",    // Peter Todd
//                "testnet-seed.flo.schildbach.de",    // Andreas Schildbach
//                "flo-testnet.bloqseeds.net",         // Bloq
        };
        addrSeeds = null;

    }

    private static TestNet3Params instance;
    public static synchronized TestNet3Params get() {
        if (instance == null) {
            instance = new TestNet3Params();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return PAYMENT_PROTOCOL_ID_TESTNET;
    }

//    // February 16th 2012
//    private static final Date testnetDiffDate = new Date(1329264000000L);
//
//    @Override
//    public void checkDifficultyTransitions(final StoredBlock storedPrev, final Block nextBlock,
//        final BlockStore blockStore) throws VerificationException, BlockStoreException {
//        if (!isDifficultyTransitionPoint(storedPrev) && nextBlock.getTime().after(testnetDiffDate)) {
//            Block prev = storedPrev.getHeader();
//
//            // After 15th February 2012 the rules on the testnet change to avoid people running up the difficulty
//            // and then leaving, making it too hard to mine a block. On non-difficulty transition points, easy
//            // blocks are allowed if there has been a span of 20 minutes without one.
//            final long timeDelta = nextBlock.getTimeSeconds() - prev.getTimeSeconds();
//            // There is an integer underflow bug in flo-qt that means mindiff blocks are accepted when time
//            // goes backwards.
//            if (timeDelta >= 0 && timeDelta <= NetworkParameters.TARGET_SPACING * 2) {
//        	// Walk backwards until we find a block that doesn't have the easiest proof of work, then check
//        	// that difficulty is equal to that one.
//        	StoredBlock cursor = storedPrev;
//        	while (!cursor.getHeader().equals(getGenesisBlock()) &&
//                       cursor.getHeight() % getInterval() != 0 &&
//                       cursor.getHeader().getDifficultyTargetAsInteger().equals(getMaxTarget()))
//                    cursor = cursor.getPrev(blockStore);
//        	BigInteger cursorTarget = cursor.getHeader().getDifficultyTargetAsInteger();
//        	BigInteger newTarget = nextBlock.getDifficultyTargetAsInteger();
//        	if (!cursorTarget.equals(newTarget))
//                    throw new VerificationException("Testnet block transition that is not allowed: " +
//                	Long.toHexString(cursor.getHeader().getDifficultyTarget()) + " vs " +
//                	Long.toHexString(nextBlock.getDifficultyTarget()));
//            }
//        } else {
//            super.checkDifficultyTransitions(storedPrev, nextBlock, blockStore);
//        }
//    }
}
