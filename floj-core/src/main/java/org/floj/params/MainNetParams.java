/*
 * Copyright 2013 Google Inc.
 * Copyright 2015 Andreas Schildbach
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

import org.floj.core.*;
import org.floj.net.discovery.*;

import java.math.BigInteger;
import java.net.*;

import static com.google.common.base.Preconditions.*;

/**
 * Parameters for the main production network on which people trade goods and services.
 */
public class MainNetParams extends AbstractFLONetParams {
    public static final int MAINNET_MAJORITY_WINDOW = 1000;
    public static final int MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED = 950;
    public static final int MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE = 750;

    public MainNetParams() {
        super();
//        interval = INTERVAL;
//        targetTimespan = TARGET_TIMESPAN;
        maxTarget = new BigInteger("00000fffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",16);
        // can also use this to initialize same value
        // maxTarget = Utils.decodeCompactBits(0x1e0fffff); // flo 0x1d00ffffL);

        // long maxTargetLong = Utils.encodeCompactBits(maxTarget);
        // System.out.println("maxTargetLong:"+maxTargetLong);
        dumpedPrivateKeyHeader = 163; 	//R    flo 128; SECRET_KEY
        addressHeader = 35; 			//F    flo 0; PUBKEY_ADDRESS
        p2shHeader = 8; 				//4    flo 5; SCRIPT_ADDRESS 
        acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        port = 7312;      			// flo 8333;
        packetMagic = 0xfdc0a5f1L;  	//flo 0xf9beb4d9L;
        bip32HeaderPub = 0x0134406b; //The 4 byte header that serializes in base58 to "Fpub". flo 0x0488B21E; //The 4 byte header that serializes in base58 to "xpub".
        bip32HeaderPriv = 0x01343c31; //The 4 byte header that serializes in base58 to "Fprv". flo 0x0488ADE4; //The 4 byte header that serializes in base58 to "xprv"

        majorityEnforceBlockUpgrade = MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = MAINNET_MAJORITY_WINDOW;
        
        // Difficulty adjustments
        fPowAllowMinDifficultyBlocks=false;
        fPowNoRetargeting=false;
        nPowTargetSpacing = 40; // 40s block time
        // V1
        nTargetTimespan_Version1 = 60 * 60;
        nInterval_Version1 = nTargetTimespan_Version1 / nPowTargetSpacing;
        nMaxAdjustUp_Version1 = 75;
        nMaxAdjustDown_Version1 = 300;
        nAveragingInterval_Version1 = nInterval_Version1;
        // V2
        nHeight_Difficulty_Version2 = 208440;
        nInterval_Version2 = 15;
        nMaxAdjustUp_Version2 = 75;
        nMaxAdjustDown_Version2 = 300;
        nAveragingInterval_Version2 = nInterval_Version2;
        // V3
        nHeight_Difficulty_Version3 = 426000;
        nInterval_Version3 = 1;
        nMaxAdjustUp_Version3 = 2;
        nMaxAdjustDown_Version3 = 3;
        nAveragingInterval_Version3 = 6;

        genesisBlock.setDifficultyTarget(0x1e0ffff0L); // flo 0x1d00ffffL);
        genesisBlock.setTime(1371488396L); // flo 1231006505L);
        genesisBlock.setNonce(1000112548); // flo 2083236893);
        id = ID_MAINNET;
        subsidyDecreaseBlockCount = 800000; // flo 210000;
        spendableCoinbaseDepth = 100; //same as flo
        String genesisHash = genesisBlock.getHashAsString();
        
        //merkle root needs to be 730f0c8ddc5a592d5512566890e2a73e45feaa6748b24b849d1c29a7ab2b2300
        checkState(genesisHash.equals("09c7781c9df90708e278c35d38ea5c9041d7ecfcdd1c56ba67274b7cff3e1cea"), // flo 000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f"),
                genesisHash);

        // This contains (at a minimum) the blocks which are not BIP30 compliant. BIP30 changed how duplicate
        // transactions are handled. Duplicated transactions could occur in the case where a coinbase had the same
        // extraNonce and the same outputs but appeared at different heights, and greatly complicated re-org handling.
        // Having these here simplifies block connection logic considerably.
        
        //flo
		checkpoints.put(      0, Sha256Hash.wrap("09c7781c9df90708e278c35d38ea5c9041d7ecfcdd1c56ba67274b7cff3e1cea"));
		checkpoints.put(   8002, Sha256Hash.wrap("73bc3b16d99bbf797f396c9532f80c3b73bb21304280de2efbc5edcb75739234"));
		checkpoints.put(  18001, Sha256Hash.wrap("5a7a4821aa4fc7ee3dea2f8319e9fa4d991a8c6762e79cb624c64e4cf1031582"));
		checkpoints.put(  38002, Sha256Hash.wrap("4962437c6d0a450f44c1e40cd38ff220f8122af1517e1329f1abd07fb7791e40"));
		checkpoints.put( 160002, Sha256Hash.wrap("478d381c92298614c3a05fb934a4fffc4d3e5b573efbba9b3e8b2ce8d26a0f8f"));
		checkpoints.put( 208001, Sha256Hash.wrap("2bb3f8b2d5081aefa0af9f5d8de42bd73a5d89eebf78aa7421cd63dc40a56d4c"));
		checkpoints.put( 270001, Sha256Hash.wrap("74988a3179ae6bbc5986e63f71bafc855202502b07e4d9331015eee82df80860"));
		checkpoints.put( 290036, Sha256Hash.wrap("145994381e5e4f0e5674adc1ace9a03b670838792f6bd6b650c80466453c2da3"));
		checkpoints.put( 344665, Sha256Hash.wrap("40fe36d8dec357aa529b6b1d99b2989a37ed8c7b065a0e3345cd15a751b9c1ad"));
		checkpoints.put( 400236, Sha256Hash.wrap("f9a4b8e21d410539e45ff3f11c28dee8966de7edffc45fd02dd1a5f4e7d4ef38"));
		checkpoints.put( 415000, Sha256Hash.wrap("16ef8ab98a7300039a5755d5bdc00e31dada9d2f1c440ff7928f43c4ea41c0a8"));
		checkpoints.put( 420937, Sha256Hash.wrap("48a75e4687021ec0dda2031439de50b61933e197a4e1a1185d131cc2b59b8444"));
		checkpoints.put( 425606, Sha256Hash.wrap("62c8d811b1a49f6fdaffded704dc48b1c98d6f8dd736d8afb96c9b097774a85e"));
		checkpoints.put( 508694, Sha256Hash.wrap("65cde197e9118e5164c4dcdcdc6fcfaf8c0de605d569cefd56aa220e7739da6a"));
		checkpoints.put( 696454, Sha256Hash.wrap("8cfb75684405e22f8f69522ec11f1e5206758e37f25db13880548f69fe6f1976"));
		checkpoints.put( 955000, Sha256Hash.wrap("b5517a50aee6af59eb0ab4ee3262bcbaf3f6672b9301cdd3302e4bab491e7526"));
		checkpoints.put(1505017, Sha256Hash.wrap("d38b306850bb26a5c98400df747d4391bb4e359e95e20dc79b50063ed3c5bfa7"));
		checkpoints.put(1678879, Sha256Hash.wrap("1e874e2852e8dfb3553f0e6c02dcf70e9f5697effa31385d25a3c88fe26676fc"));
		checkpoints.put(1678909, Sha256Hash.wrap("4c5a1040e337a542e6717904c8346bd72151fc34c390dff7b5cf23dcedc5058a"));
		checkpoints.put(1679162, Sha256Hash.wrap("b32c64fb80a4196ff3e1be883db10629e1d7cd27c00ef0b5d1fe54af481fc10f"));
		checkpoints.put(1796633, Sha256Hash.wrap("c2da8b936a7f2c0de02aa0c6c45f3d971ebad78655255a945b0e60b62f27d445"));
		checkpoints.put(2094558, Sha256Hash.wrap("946616c88286f32bfac15868456d87a86f8611e1f9b56594b81e46831ce43f81"));
		checkpoints.put(2532181, Sha256Hash.wrap("cacd5149aaed1088ae1db997a741210b0525e941356104120f182f3159931c79"));

        dnsSeeds = new String[] {
        		"193.70.122.58",
				"nyc2.entertheblockchain.com",
				"sf1.entertheblockchain.com",
				"am2.entertheblockchain.com",
				"sgp.entertheblockchain.com",
				"ind.entertheblockchain.com",
				"de.entertheblockchain.com",
				"node.oip.fun",
				"snowflake.oip.fun"
        };
        
//        httpSeeds = new HttpDiscovery.Details[] {
//                // Andreas Schildbach
//                new HttpDiscovery.Details(
//                        ECKey.fromPublicOnly(Utils.HEX.decode("0238746c59d46d5408bf8b1d0af5740fe1a6e1703fcb56b2953f0b965c740d256f")),
//                        URI.create("http://httpseed.flo.schildbach.de/peers")
//                )
//        };

        /**
         *  chainparams.h seed example for 127.0.0.1 from static SeedSpec6 pnSeed6_main[]={
         *  	{{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xff,0xff,0x7f,0x00,0x00,0x01}, 7312}
         *  }
         *  needs to be converted to this format for addrSeeds: 
         *  [
         *  	0x0100007f
         *  ]
         */
//        addrSeeds = new int[] {
//        		0x3a7a46c1, 0x6e3b09b0, 0x0366a6bc, 0x706bf3a2, 0x13c9f1c0, 0xd4df51c0, 0x7774c780, 0xa6463b8b,
//        		0x94ef9acf
//        };
    }

    private static MainNetParams instance;
    public static synchronized MainNetParams get() {
        if (instance == null) {
            instance = new MainNetParams();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return PAYMENT_PROTOCOL_ID_MAINNET;
    }
}
