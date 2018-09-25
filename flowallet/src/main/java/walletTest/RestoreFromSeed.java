package walletTest;

import java.io.File;

import org.floj.core.BlockChain;
import org.floj.core.NetworkParameters;
import org.floj.core.PeerGroup;
import org.floj.core.listeners.DownloadProgressTracker;
import org.floj.net.discovery.DnsDiscovery;
import org.floj.store.SPVBlockStore;
import org.floj.wallet.DeterministicSeed;
import org.floj.wallet.Wallet;

public class RestoreFromSeed {

    public void restore(NetworkParameters params, WalletTest seed, long creationtime) throws Exception
    {
    	String seedCode = seed.getMnemonic();
        String passphrase = "";

        DeterministicSeed seed1 = new DeterministicSeed(seedCode, null, passphrase, creationtime);

        // The wallet class provides a easy fromSeed() function that loads a new wallet from a given seed.
        Wallet wallet = Wallet.fromSeed(params, seed1);

        // Because we are importing an existing wallet which might already have transactions we must re-download the blockchain to make the wallet picks up these transactions
        System.out.println(wallet.toString());
        wallet.clearTransactions(0);
        File chainFile = new File("restore-from-seed.spvchain");
        if (chainFile.exists()) {
            chainFile.delete();
        }

        // Setting up the BlochChain, the BlocksStore and connecting to the network.
        SPVBlockStore chainStore = new SPVBlockStore(params, chainFile);
        BlockChain chain = new BlockChain(params, chainStore);
        PeerGroup peerGroup = new PeerGroup(params, chain);
        peerGroup.addPeerDiscovery(new DnsDiscovery(params));

        // Now we need to hook the wallet up to the blockchain and the peers. This registers event listeners that notify our wallet about new transactions.
        chain.addWallet(wallet);
        peerGroup.addWallet(wallet);

        DownloadProgressTracker bListener = new DownloadProgressTracker() {
            @Override
            public void doneDownload() {
                System.out.println("blockchain downloaded");
            }
        };

        // Now we re-download the blockchain. This replays the chain into the wallet. Once this is completed our wallet should know of all its transactions and print the correct balance.
        peerGroup.start();
        peerGroup.startBlockChainDownload(bListener);

        bListener.await();

        // Print a debug message with the details about the wallet. The correct balance should now be displayed.
        System.out.println(wallet.toString());

        // shutting down again
        peerGroup.stop();
    }
}
