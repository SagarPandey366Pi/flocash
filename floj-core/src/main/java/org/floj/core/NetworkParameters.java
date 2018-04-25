/*
 * Copyright 2011 Google Inc.
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

package org.floj.core;

import com.google.common.base.Objects;

import javax.annotation.*;

import static org.floj.core.Coin.*;

import java.io.*;
import java.math.*;
import java.util.*;

import org.floj.core.Block;
import org.floj.core.StoredBlock;
import org.floj.core.VerificationException;
import org.floj.net.discovery.*;
import org.floj.params.*;
import org.floj.script.*;
import org.floj.store.BlockStore;
import org.floj.store.BlockStoreException;
import org.floj.utils.MonetaryFormat;
import org.floj.utils.VersionTally;

/**
 * <p>NetworkParameters contains the data needed for working with an instantiation of a FLO chain.</p>
 *
 * <p>This is an abstract class, concrete instantiations can be found in the params package. There are four:
 * one for the main network ({@link MainNetParams}), one for the public test network, and two others that are
 * intended for unit testing and local app development purposes. Although this class contains some aliases for
 * them, you are encouraged to call the static get() methods on each specific params class directly.</p>
 */
public abstract class NetworkParameters {
    /**
     * The alert signing key originally owned by Satoshi, and now passed on to Gavin along with a few others.
     */
    public static final byte[] SATOSHI_KEY = Utils.HEX.decode("0416ca41786113574984664acedc7d338cc10a29edec0adae32f3fa0317fee95c9790150f38544204fac805ac58bca435df5021233d8e347f6127832a9e0207e1b");

    /** The string returned by getId() for the main, production network where people trade things. */
    public static final String ID_MAINNET = "cash.flo.production";
    /** The string returned by getId() for the testnet. */
    public static final String ID_TESTNET = "cash.flo.test";
    /** The string returned by getId() for regtest mode. */
    public static final String ID_REGTEST = "cash.flo.regtest";
    /** Unit test network. */
    public static final String ID_UNITTESTNET = "cash.floj.unittest";

    /** The string used by the payment protocol to represent the main net. */
    public static final String PAYMENT_PROTOCOL_ID_MAINNET = "main";
    /** The string used by the payment protocol to represent the test net. */
    public static final String PAYMENT_PROTOCOL_ID_TESTNET = "test";
    /** The string used by the payment protocol to represent unit testing (note that this is non-standard). */
    public static final String PAYMENT_PROTOCOL_ID_UNIT_TESTS = "unittest";
    public static final String PAYMENT_PROTOCOL_ID_REGTEST = "regtest";

    // TODO: Seed nodes should be here as well.

    protected Block genesisBlock;
    protected BigInteger maxTarget;
    protected int port;
    protected long packetMagic;  // Indicates message origin network and is used to seek to the next message when stream state is unknown.
    protected int addressHeader;
    protected int p2shHeader;
    protected int dumpedPrivateKeyHeader;
    protected int interval;
    protected int targetTimespan;
    protected byte[] alertSigningKey;
    protected int bip32HeaderPub;
    protected int bip32HeaderPriv;

    /** Used to check majorities for block version upgrade */
    protected int majorityEnforceBlockUpgrade;
    protected int majorityRejectBlockOutdated;
    protected int majorityWindow;
    
    /** Used to adjust difficulty levels */
    protected boolean fPowAllowMinDifficultyBlocks;
    protected boolean fPowNoRetargeting;
    protected long nPowTargetSpacing;
    // V1
    protected long nTargetTimespan_Version1;
    protected long nInterval_Version1;
    protected long nMaxAdjustUp_Version1;
    protected long nMaxAdjustDown_Version1;
    protected long nAveragingInterval_Version1;

    // V2
    protected long nHeight_Difficulty_Version2;
    protected long nInterval_Version2;
    protected long nMaxAdjustUp_Version2;
    protected long nMaxAdjustDown_Version2;
    protected long nAveragingInterval_Version2;

    // V3
    protected long nHeight_Difficulty_Version3;
    protected long nInterval_Version3;
    protected long nMaxAdjustUp_Version3;
    protected long nMaxAdjustDown_Version3;
    protected long nAveragingInterval_Version3;

    /**
     * See getId(). This may be null for old deserialized wallets. In that case we derive it heuristically
     * by looking at the port number.
     */
    protected String id;

    /**
     * The depth of blocks required for a coinbase transaction to be spendable.
     */
    protected int spendableCoinbaseDepth;
    protected int subsidyDecreaseBlockCount;
    
    protected int[] acceptableAddressCodes;
    protected String[] dnsSeeds;
    protected int[] addrSeeds;
    protected HttpDiscovery.Details[] httpSeeds = {};
    protected Map<Integer, Sha256Hash> checkpoints = new HashMap<Integer, Sha256Hash>();
    protected transient MessageSerializer defaultSerializer = null;

    protected NetworkParameters() {
        alertSigningKey = SATOSHI_KEY;
        genesisBlock = createGenesis(this);
    }

    private static Block createGenesis(NetworkParameters n) {
        Block genesisBlock = new Block(n, Block.BLOCK_VERSION_GENESIS);
        Transaction t = new Transaction(n);
        try {
            // A script containing the difficulty bits and the following message:
            //
            //   "The Times 03/Jan/2009 Chancellor on brink of second bailout for banks" (0x45 bytes, 69 decimal)
        	//  04 PUSH 4 BYTES (ff,ff,00,1d)  01 PUSH 1 BYTE (04) PUSH 45 BYTES (5468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73)
        	//   "Slashdot - 17 June 2013 - Saudi Arabia Set To Ban WhatsApp, Skype"  (65 decimal, 0x41 bytes)
        	//  04 PUSH 4 BYTES (ff,ff,00,1d)  01 PUSH 1 BYTE (04) PUSH 41 BYTES (536C617368646F74202D203137204A756E652032303133202D205361756469204172616269612053657420546F2042616E2057686174734170702C20536B797065)
        	//
        	// https://blockchain.info/tx/4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b
        	// https://blockchain.info/tx/d5ada064c6417ca25c4308bd158c34b77e1c0eca2a73cda16c737e7424afba2f
        	
        	t.setFloData("text:Florincoin genesis block");

//            byte[] bytes = Utils.HEX.decode
//                    ("04 ffff001d 01 04 45 5468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73");
        	byte[] bytes = Utils.HEX.decode
            		("04ffff001d010441536c617368646f74202d203137204a756e652032303133202d205361756469204172616269612053657420546f2042616e2057686174734170702c20536b797065");
            t.addInput(new TransactionInput(n, t, bytes));
            ByteArrayOutputStream scriptPubKeyBytes = new ByteArrayOutputStream();
            Script.writeBytes(scriptPubKeyBytes, Utils.HEX.decode
                    ("040184710fa689ad5023690c80f3a49c8f13f8d45b8c857fbcbc8bc4a8e4d3eb4b10f4d4604fa08dce601aaf0f470216fe1b51850b4acf21b179c45070ac7b03a9"));
            scriptPubKeyBytes.write(ScriptOpCodes.OP_CHECKSIG);
            t.addOutput(new TransactionOutput(n, t, HUNDRED_COINS, scriptPubKeyBytes.toByteArray()));
        } catch (Exception e) {
            // Cannot happen.
            throw new RuntimeException(e);
        }
        genesisBlock.addTransaction(t);
        return genesisBlock;
    }

//    public static final int TARGET_TIMESPAN = 14 * 24 * 60 * 60;  // 2 weeks per difficulty cycle, on average.
//    public static final int TARGET_SPACING = 10 * 60;  // 10 minutes per block.
//    public static final int INTERVAL = TARGET_TIMESPAN / TARGET_SPACING;    public static final int TARGET_SPACING = 40;  // 40s per block.

    // FLO: Difficulty adjustment forks.
    public long TargetTimespan(int height) {
        // V1
        if (height < nHeight_Difficulty_Version2)
            return nTargetTimespan_Version1;
        // V2
        if (height < nHeight_Difficulty_Version3)
            return nInterval_Version2 * nPowTargetSpacing;
        // V3
        return nInterval_Version3 * nPowTargetSpacing;
    }

    public long DifficultyAdjustmentInterval(int height) {
        // V1
        if (height < nHeight_Difficulty_Version2)
            return nInterval_Version1;
        // V2
        if (height < nHeight_Difficulty_Version3)
            return nInterval_Version2;
        // V3
        return nInterval_Version3;
    }

    public long MaxActualTimespan(int height) {
        long averagingTargetTimespan = AveragingInterval(height) * nPowTargetSpacing;
        // V1
        if (height < nHeight_Difficulty_Version2)
            return averagingTargetTimespan * (100 + nMaxAdjustDown_Version1) / 100;
        // V2
        if (height < nHeight_Difficulty_Version3)
            return averagingTargetTimespan * (100 + nMaxAdjustDown_Version2) / 100;
        // V3
        return averagingTargetTimespan * (100 + nMaxAdjustDown_Version3) / 100;
    }

    public long MinActualTimespan(int height) {
        long averagingTargetTimespan = AveragingInterval(height) * nPowTargetSpacing;
        // V1
        if (height < nHeight_Difficulty_Version2)
            return averagingTargetTimespan * (100 - nMaxAdjustUp_Version1) / 100;
        // V2
        if (height < nHeight_Difficulty_Version3)
            return averagingTargetTimespan * (100 - nMaxAdjustUp_Version2) / 100;
        // V3
        return averagingTargetTimespan * (100 - nMaxAdjustUp_Version3) / 100;
    }

    public long AveragingInterval(int height) {
        // V1
        if (height < nHeight_Difficulty_Version2)
            return nAveragingInterval_Version1;
        // V2
        if (height < nHeight_Difficulty_Version3)
            return nAveragingInterval_Version2;
        // V3
        return nAveragingInterval_Version3;
    }
    
    long GetNextWorkRequired(final StoredBlock storedPrev, final Block nextBlock, 
    		final BlockStore blockStore) throws BlockStoreException {
    	Block prev = storedPrev.getHeader();
    	
        long nProofOfWorkLimit=Utils.encodeCompactBits(maxTarget); // UintToArith256(params.powLimit).GetCompact();

        // Only change once per difficulty adjustment interval
    	int height=storedPrev.getHeight()+1;
        if((height % DifficultyAdjustmentInterval(height))!=0) {
        	
        	
            if(fPowAllowMinDifficultyBlocks) {
                // Special difficulty rule for testnet:
                // If the new block's timestamp is more than 2* 10 minutes
                // then allow mining of a min-difficulty block.
            	
                if(nextBlock.getTimeSeconds() > (prev.getTimeSeconds()+TargetTimespan(height)*2)) {
                    return nProofOfWorkLimit;
                } else {
                    // Return the last non-special-min-difficulty-rules-block
                	StoredBlock cursor = blockStore.get(prev.getHash());
                    while (cursor!=null && (cursor.getHeight() % DifficultyAdjustmentInterval(cursor.getHeight()+1))!=0 && cursor.getHeader().getDifficultyTarget()==nProofOfWorkLimit)
                    	cursor=blockStore.get(cursor.getHeader().getPrevBlockHash());
                    return cursor.getHeader().getDifficultyTarget();
                }
            }
            return storedPrev.getHeader().getDifficultyTarget();
        }

        long averagingInterval = AveragingInterval(height);
        // Go back by what we want to be 14 days worth of blocks
        // FLO: This fixes an issue where a 51% attack can change difficulty at will.
        // Go back the full period unless it's the first retarget after genesis. Code courtesy of Art Forz
        long blockstogoback = averagingInterval-1;
        if (height != averagingInterval)
            blockstogoback = averagingInterval;

        // Go back by what we want to be 14 days worth of blocks
        StoredBlock cursor = blockStore.get(prev.getHash()); //const CBlockIndex* pindexFirst = storedPrev;
        for(int i=0; cursor!=null && i<blockstogoback; i++)
        	cursor=blockStore.get(cursor.getHeader().getPrevBlockHash()); //pindexFirst = pindexFirst->pprev;

        assert(cursor!=null);

        return CalculateNextWorkRequired(storedPrev, cursor.getHeader().getTimeSeconds());
    }

    long CalculateNextWorkRequired(final StoredBlock storedPrev, long nFirstBlockTime) {
        if (fPowNoRetargeting)
            return storedPrev.getHeader().getDifficultyTarget();
        
        int height=storedPrev.getHeight()+1;
        final long nMinActualTimespan = MinActualTimespan(height);
        final long nMaxActualTimespan = MaxActualTimespan(height);

        // Limit adjustment step
        long nActualTimespan = storedPrev.getHeader().getTimeSeconds() - nFirstBlockTime;
        if (nActualTimespan < nMinActualTimespan)
            nActualTimespan = nMinActualTimespan;
        if (nActualTimespan > nMaxActualTimespan)
            nActualTimespan = nMaxActualTimespan;

        // Retarget
        BigInteger bnNew;
//        BigInteger bnOld;
        bnNew=Utils.decodeCompactBits(storedPrev.getHeader().getDifficultyTarget()); //.SetCompact(storedPrev.getHeader().getDifficultyTarget());
//        bnOld = bnNew;
        // FLO: intermediate uint256 can overflow by 1 bit
        final BigInteger bnPowLimit=maxTarget;  // const arith_uint256 bnPowLimit = UintToArith256(params.powLimit);
        boolean fShift = bnNew.compareTo(bnPowLimit.subtract(BigInteger.ONE))>0; //bnNew.bits() > bnPowLimit.bits() - 1;
        if(fShift)
            bnNew.shiftRight(1); // bnNew >>= 1;
        bnNew=bnNew.multiply(BigInteger.valueOf(nActualTimespan)); // bnNew *= nActualTimespan;
        bnNew=bnNew.divide(BigInteger.valueOf(AveragingInterval(height)*nPowTargetSpacing)); // bnNew /= params.AveragingInterval(storedPrev.getHeight()+1) * params.nPowTargetSpacing;
        if(fShift)
        	bnNew.shiftLeft(1); // bnNew <<= 1;

        if(bnNew.compareTo(bnPowLimit)>0) // (bnNew > bnPowLimit)
            bnNew = bnPowLimit;

//        /// debug print
//        LogPrintf("GetNextWorkRequired RETARGET\n");
//        LogPrintf("Params().TargetTimespan() = %d    nActualTimespan = %d\n", params.TargetTimespan(storedPrev.getHeight()+1), nActualTimespan);
//        LogPrintf("Before: %08x  %s\n", storedPrev.getHeader().getDifficultyTarget(), bnOld.ToString());
//        LogPrintf("After:  %08x  %s\n", bnNew.GetCompact(), bnNew.ToString());

        return Utils.encodeCompactBits(bnNew); //bnNew.GetCompact();
    }

    /**
     * Blocks with a timestamp after this should enforce BIP 16, aka "Pay to script hash". This BIP changed the
     * network rules in a soft-forking manner, that is, blocks that don't follow the rules are accepted but not
     * mined upon and thus will be quickly re-orged out as long as the majority are enforcing the rule.
     * BIP16 didn't become active until Oct 1 2012
     */
    public static final int BIP16_ENFORCE_TIME = 1349049600; //1333238400;
    
    /**
     * The maximum number of coins to be generated
     */
    public static final long MAX_COINS = 160000000;

    /**
     * The maximum money to be generated
     */
    public static final Coin MAX_MONEY = COIN.multiply(MAX_COINS);

    /** Alias for TestNet3Params.get(), use that instead. */
    @Deprecated
    public static NetworkParameters testNet() {
        return TestNet3Params.get();
    }

    /** Alias for TestNet2Params.get(), use that instead. */
    @Deprecated
    public static NetworkParameters testNet2() {
        return TestNet2Params.get();
    }

    /** Alias for TestNet3Params.get(), use that instead. */
    @Deprecated
    public static NetworkParameters testNet3() {
        return TestNet3Params.get();
    }

    /** Alias for MainNetParams.get(), use that instead */
    @Deprecated
    public static NetworkParameters prodNet() {
        return MainNetParams.get();
    }

    /** Returns a testnet params modified to allow any difficulty target. */
    @Deprecated
    public static NetworkParameters unitTests() {
        return UnitTestParams.get();
    }

    /** Returns a standard regression test params (similar to unitTests) */
    @Deprecated
    public static NetworkParameters regTests() {
        return RegTestParams.get();
    }

    /**
     * A Java package style string acting as unique ID for these parameters
     */
    public String getId() {
        return id;
    }

    public abstract String getPaymentProtocolId();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return getId().equals(((NetworkParameters)o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    /** Returns the network parameters for the given string ID or NULL if not recognized. */
    @Nullable
    public static NetworkParameters fromID(String id) {
        if (id.equals(ID_MAINNET)) {
            return MainNetParams.get();
        } else if (id.equals(ID_TESTNET)) {
            return TestNet3Params.get();
        } else if (id.equals(ID_UNITTESTNET)) {
            return UnitTestParams.get();
        } else if (id.equals(ID_REGTEST)) {
            return RegTestParams.get();
        } else {
            return null;
        }
    }

    /** Returns the network parameters for the given string paymentProtocolID or NULL if not recognized. */
    @Nullable
    public static NetworkParameters fromPmtProtocolID(String pmtProtocolId) {
        if (pmtProtocolId.equals(PAYMENT_PROTOCOL_ID_MAINNET)) {
            return MainNetParams.get();
        } else if (pmtProtocolId.equals(PAYMENT_PROTOCOL_ID_TESTNET)) {
            return TestNet3Params.get();
        } else if (pmtProtocolId.equals(PAYMENT_PROTOCOL_ID_UNIT_TESTS)) {
            return UnitTestParams.get();
        } else if (pmtProtocolId.equals(PAYMENT_PROTOCOL_ID_REGTEST)) {
            return RegTestParams.get();
        } else {
            return null;
        }
    }

    public int getSpendableCoinbaseDepth() {
        return spendableCoinbaseDepth;
    }

    /**
     * Throws an exception if the block's difficulty is not correct.
     *
     * @throws VerificationException if the block's difficulty is not correct.
     */
    public abstract void checkDifficultyTransitions(StoredBlock storedPrev, Block next, final BlockStore blockStore) throws VerificationException, BlockStoreException;

    /**
     * Returns true if the block height is either not a checkpoint, or is a checkpoint and the hash matches.
     */
    public boolean passesCheckpoint(int height, Sha256Hash hash) {
        Sha256Hash checkpointHash = checkpoints.get(height);
        return checkpointHash == null || checkpointHash.equals(hash);
    }

    /**
     * Returns true if the given height has a recorded checkpoint.
     */
    public boolean isCheckpoint(int height) {
        Sha256Hash checkpointHash = checkpoints.get(height);
        return checkpointHash != null;
    }

    public int getSubsidyDecreaseBlockCount() {
        return subsidyDecreaseBlockCount;
    }

    /** Returns DNS names that when resolved, give IP addresses of active peers. */
    public String[] getDnsSeeds() {
        return dnsSeeds;
    }

    /** Returns IP address of active peers. */
    public int[] getAddrSeeds() {
        return addrSeeds;
    }

    /** Returns discovery objects for seeds implementing the Cartographer protocol. See {@link org.floj.net.discovery.HttpDiscovery} for more info. */
    public HttpDiscovery.Details[] getHttpSeeds() {
        return httpSeeds;
    }

    /**
     * <p>Genesis block for this chain.</p>
     *
     * <p>The first block in every chain is a well known constant shared between all FLO implemenetations. For a
     * block to be valid, it must be eventually possible to work backwards to the genesis block by following the
     * prevBlockHash pointers in the block headers.</p>
     *
     * <p>The genesis blocks for both test and main networks contain the timestamp of when they were created,
     * and a message in the coinbase transaction. It says, <i>"The Times 03/Jan/2009 Chancellor on brink of second
     * bailout for banks"</i>.</p>
     */
    public Block getGenesisBlock() {
        return genesisBlock;
    }

    /** Default TCP port on which to connect to nodes. */
    public int getPort() {
        return port;
    }

    /** The header bytes that identify the start of a packet on this network. */
    public long getPacketMagic() {
        return packetMagic;
    }

    /**
     * First byte of a base58 encoded address. See {@link org.floj.core.Address}. This is the same as acceptableAddressCodes[0] and
     * is the one used for "normal" addresses. Other types of address may be encountered with version codes found in
     * the acceptableAddressCodes array.
     */
    public int getAddressHeader() {
        return addressHeader;
    }

    /**
     * First byte of a base58 encoded P2SH address.  P2SH addresses are defined as part of BIP0013.
     */
    public int getP2SHHeader() {
        return p2shHeader;
    }

    /** First byte of a base58 encoded dumped private key. See {@link org.floj.core.DumpedPrivateKey}. */
    public int getDumpedPrivateKeyHeader() {
        return dumpedPrivateKeyHeader;
    }

    /**
     * How much time in seconds is supposed to pass between "interval" blocks. If the actual elapsed time is
     * significantly different from this value, the network difficulty formula will produce a different value. Both
     * test and main flo networks use 2 weeks (1209600 seconds).
     */
    public int getTargetTimespan() {
        return targetTimespan;
    }

    /**
     * The version codes that prefix addresses which are acceptable on this network. Although Satoshi intended these to
     * be used for "versioning", in fact they are today used to discriminate what kind of data is contained in the
     * address and to prevent accidentally sending coins across chains which would destroy them.
     */
    public int[] getAcceptableAddressCodes() {
        return acceptableAddressCodes;
    }

    /**
     * If we are running in testnet-in-a-box mode, we allow connections to nodes with 0 non-genesis blocks.
     */
    public boolean allowEmptyPeerChain() {
        return true;
    }

    /** How many blocks pass between difficulty adjustment periods. FLO standardises this to be 2015. */
    public int getInterval() {
        return interval;
    }

    /** Maximum target represents the easiest allowable proof of work. */
    public BigInteger getMaxTarget() {
        return maxTarget;
    }
    

    /**
     * The key used to sign {@link org.floj.core.AlertMessage}s. You can use {@link org.floj.core.ECKey#verify(byte[], byte[], byte[])} to verify
     * signatures using it.
     */
    public byte[] getAlertSigningKey() {
        return alertSigningKey;
    }

    /** Returns the 4 byte header for BIP32 (HD) wallet - public key part. */
    public int getBip32HeaderPub() {
        return bip32HeaderPub;
    }

    /** Returns the 4 byte header for BIP32 (HD) wallet - private key part. */
    public int getBip32HeaderPriv() {
        return bip32HeaderPriv;
    }

    /**
     * Returns the number of coins that will be produced in total, on this
     * network. Where not applicable, a very large number of coins is returned
     * instead (i.e. the main coin issue for Dogecoin).
     */
    public abstract Coin getMaxMoney();

    /**
     * Any standard (ie pay-to-address) output smaller than this value will
     * most likely be rejected by the network.
     */
    public abstract Coin getMinNonDustOutput();

    /**
     * The monetary object for this currency.
     */
    public abstract MonetaryFormat getMonetaryFormat();

    /**
     * Scheme part for URIs, for example "flo".
     */
    public abstract String getUriScheme();

    /**
     * Returns whether this network has a maximum number of coins (finite supply) or
     * not. Always returns true for FLO, but exists to be overriden for other
     * networks.
     */
    public abstract boolean hasMaxMoney();

    /**
     * Return the default serializer for this network. This is a shared serializer.
     * @return 
     */
    public final MessageSerializer getDefaultSerializer() {
        // Construct a default serializer if we don't have one
        if (null == this.defaultSerializer) {
            // Don't grab a lock unless we absolutely need it
            synchronized(this) {
                // Now we have a lock, double check there's still no serializer
                // and create one if so.
                if (null == this.defaultSerializer) {
                    // As the serializers are intended to be immutable, creating
                    // two due to a race condition should not be a problem, however
                    // to be safe we ensure only one exists for each network.
                    this.defaultSerializer = getSerializer(false);
                }
            }
        }
        return defaultSerializer;
    }

    /**
     * Construct and return a custom serializer.
     */
    public abstract FLOSerializer getSerializer(boolean parseRetain);

    /**
     * The number of blocks in the last {@link getMajorityWindow()} blocks
     * at which to trigger a notice to the user to upgrade their client, where
     * the client does not understand those blocks.
     */
    public int getMajorityEnforceBlockUpgrade() {
        return majorityEnforceBlockUpgrade;
    }

    /**
     * The number of blocks in the last {@link getMajorityWindow()} blocks
     * at which to enforce the requirement that all new blocks are of the
     * newer type (i.e. outdated blocks are rejected).
     */
    public int getMajorityRejectBlockOutdated() {
        return majorityRejectBlockOutdated;
    }

    /**
     * The sampling window from which the version numbers of blocks are taken
     * in order to determine if a new block version is now the majority.
     */
    public int getMajorityWindow() {
        return majorityWindow;
    }

    /**
     * The flags indicating which block validation tests should be applied to
     * the given block. Enables support for alternative blockchains which enable
     * tests based on different criteria.
     * 
     * @param block block to determine flags for.
     * @param height height of the block, if known, null otherwise. Returned
     * tests should be a safe subset if block height is unknown.
     */
    public EnumSet<Block.VerifyFlag> getBlockVerificationFlags(final Block block,
            final VersionTally tally, final Integer height) {
        final EnumSet<Block.VerifyFlag> flags = EnumSet.noneOf(Block.VerifyFlag.class);

        if (block.isBIP34()) {
            final Integer count = tally.getCountAtOrAbove(Block.BLOCK_VERSION_BIP34);
            if (null != count && count >= getMajorityEnforceBlockUpgrade()) {
                flags.add(Block.VerifyFlag.HEIGHT_IN_COINBASE);
            }
        }
        return flags;
    }

    /**
     * The flags indicating which script validation tests should be applied to
     * the given transaction. Enables support for alternative blockchains which enable
     * tests based on different criteria.
     *
     * @param block block the transaction belongs to.
     * @param transaction to determine flags for.
     * @param height height of the block, if known, null otherwise. Returned
     * tests should be a safe subset if block height is unknown.
     */
    public EnumSet<Script.VerifyFlag> getTransactionVerificationFlags(final Block block,
            final Transaction transaction, final VersionTally tally, final Integer height) {
        final EnumSet<Script.VerifyFlag> verifyFlags = EnumSet.noneOf(Script.VerifyFlag.class);
        if (block.getTimeSeconds() >= NetworkParameters.BIP16_ENFORCE_TIME)
            verifyFlags.add(Script.VerifyFlag.P2SH);

        // Start enforcing CHECKLOCKTIMEVERIFY, (BIP65) for block.nVersion=4
        // blocks, when 75% of the network has upgraded:
        if (block.getVersion() >= Block.BLOCK_VERSION_BIP65 &&
            tally.getCountAtOrAbove(Block.BLOCK_VERSION_BIP65) > this.getMajorityEnforceBlockUpgrade()) {
            verifyFlags.add(Script.VerifyFlag.CHECKLOCKTIMEVERIFY);
        }

        return verifyFlags;
    }

    public abstract int getProtocolVersionNum(final ProtocolVersion version);

    public static enum ProtocolVersion {
//        MINIMUM(70000),
//        PONG(60001),
//        BLOOM_FILTER(70000),
//        CURRENT(70001);
        MINIMUM(70002),
        PONG(60001),
        BLOOM_FILTER(70000),
        CURRENT(70002);

        private final int bitcoinProtocol;

        ProtocolVersion(final int bitcoinProtocol) {
            this.bitcoinProtocol = bitcoinProtocol;
        }

        public int getFLOProtocolVersion() {
            return bitcoinProtocol;
        }
    }
}
