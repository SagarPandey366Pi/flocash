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

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import org.floj.core.FLOSerializer;
import org.floj.core.Block;
import org.floj.core.Coin;
import org.floj.core.NetworkParameters;
import org.floj.core.StoredBlock;
import org.floj.core.Transaction;
import org.floj.core.Utils;
import org.floj.core.VerificationException;
import org.floj.store.BlockStore;
import org.floj.store.BlockStoreException;
import org.floj.utils.MonetaryFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

/**
 * Parameters for FLO-like networks.
 */
public abstract class AbstractFLONetParams extends NetworkParameters {
    /**
     * Scheme part for FLO URIs.
     */
    public static final String BITCOIN_SCHEME = "flo";

    private static final Logger log = LoggerFactory.getLogger(AbstractFLONetParams.class);

    public AbstractFLONetParams() {
        super();
    }

    /**
     * Checks if we are at a difficulty transition point.
     * @param storedPrev The previous stored block
     * @return If this is a difficulty transition point
     */
    protected boolean isDifficultyTransitionPoint(StoredBlock storedPrev) {
    	//bitcoin where interval is fixed to 2016
    	//return ((storedPrev.getHeight() + 1) % this.getInterval()) == 0;

    	//see if this is an adjustment interval
    	int height=storedPrev.getHeight() + 1;
    	return (height % this.DifficultyAdjustmentInterval(height)) == 0;
    }

    @Override
    public void checkDifficultyTransitions(final StoredBlock storedPrev, final Block nextBlock,
    	final BlockStore blockStore) throws VerificationException, BlockStoreException {
        Block prev = storedPrev.getHeader();

        // Is this supposed to be a difficulty transition point?
        if (!isDifficultyTransitionPoint(storedPrev)) {

            // No ... so check the difficulty didn't actually change.
        	//changes by Sagar
            /*if (nextBlock.getDifficultyTarget() != prev.getDifficultyTarget())
                throw new VerificationException("Unexpected change in difficulty at height " + storedPrev.getHeight() +
                        ": " + Long.toHexString(nextBlock.getDifficultyTarget()) + " vs " +
                        Long.toHexString(prev.getDifficultyTarget()));*/
            return;
        }

        int nextHeight = storedPrev.getHeight()+1;

        // We need to find a block far back in the chain. It's OK that this is expensive because it only occurs every
        // two weeks after the initial block chain download.
        final Stopwatch watch = Stopwatch.createStarted();
        StoredBlock cursor = blockStore.get(prev.getHash());
        for (int i = 0; i < this.AveragingInterval(nextHeight); i++) {
            if (cursor == null) {
                // This should never happen. If it does, it means we are following an incorrect or busted chain.
                throw new VerificationException(
                        "Difficulty transition point but we did not find a way back to the genesis block.");
            }
            cursor = blockStore.get(cursor.getHeader().getPrevBlockHash());
        }
        watch.stop();
        if (watch.elapsed(TimeUnit.MILLISECONDS) > 50)
            log.info("Difficulty transition traversal took {}", watch);

        Block blockIntervalAgo = cursor.getHeader();
        long timespan = prev.getTimeSeconds() - blockIntervalAgo.getTimeSeconds();
        // Limit the adjustment step.
        final long minActualTimespan = this.MinActualTimespan(nextHeight);
        final long maxActualTimespan = this.MaxActualTimespan(nextHeight);
        if (timespan < minActualTimespan)
            timespan = minActualTimespan;
        if (timespan > maxActualTimespan)
            timespan = maxActualTimespan;

        BigInteger newTarget = Utils.decodeCompactBits(prev.getDifficultyTarget());
        newTarget = newTarget.multiply(BigInteger.valueOf(timespan));
        newTarget = newTarget.divide(BigInteger.valueOf(this.TargetTimespan(nextHeight)));

        if (newTarget.compareTo(this.getMaxTarget()) > 0) {
            log.info("Difficulty hit proof of work limit: {}", newTarget.toString(16));
            newTarget = this.getMaxTarget();
        }

        int accuracyBytes = (int) (nextBlock.getDifficultyTarget() >>> 24) - 3;
        long receivedTargetCompact = nextBlock.getDifficultyTarget();

        // The calculated difficulty is to a higher precision than received, so reduce here.
        BigInteger mask = BigInteger.valueOf(0xFFFFFFL).shiftLeft(accuracyBytes * 8);
        newTarget = newTarget.and(mask);
        long newTargetCompact = Utils.encodeCompactBits(newTarget);

        if (newTargetCompact != receivedTargetCompact)
            throw new VerificationException("Network provided difficulty bits do not match what was calculated: " +
                    Long.toHexString(newTargetCompact) + " vs " + Long.toHexString(receivedTargetCompact));
    }

    @Override
    public Coin getMaxMoney() {
        return MAX_MONEY;
    }

    @Override
    public Coin getMinNonDustOutput() {
        return Transaction.MIN_NONDUST_OUTPUT;
    }

    @Override
    public MonetaryFormat getMonetaryFormat() {
        return new MonetaryFormat();
    }

    @Override
    public int getProtocolVersionNum(final ProtocolVersion version) {
        return version.getFLOProtocolVersion();
    }

    @Override
    public FLOSerializer getSerializer(boolean parseRetain) {
        return new FLOSerializer(this, parseRetain);
    }

    @Override
    public String getUriScheme() {
        return BITCOIN_SCHEME;
    }

    @Override
    public boolean hasMaxMoney() {
        return true;
    }
}
