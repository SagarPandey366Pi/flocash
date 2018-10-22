package walletSeed;

import org.floj.core.NetworkParameters;
import org.floj.crypto.DeterministicKey;
import org.floj.crypto.HDKeyDerivation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Chain {

    private DeterministicKey cKey = null;
    private boolean isReceive;

    private String strPath = null;
    private NetworkParameters params = null;

    @SuppressWarnings("unused")
	private Chain() { }

    /**
     * Constructor for a chain.
     *
     * @param NetworkParameters params
     * @param DeterministicKey aKey deterministic key for this chain
     * @param boolean isReceive this is the receive chain
     *
     */
    public Chain(NetworkParameters params, DeterministicKey aKey, boolean isReceive) {

        this.params = params;
        this.isReceive = isReceive;
        int chain = isReceive ? 0 : 1;
        cKey = HDKeyDerivation.deriveChildKey(aKey, chain);

        strPath = cKey.getPathAsString();
    }

    /**
     * Test if this is the receive chain.
     *
     * @return boolean
     */
    public boolean isReceive() {
        return isReceive;
    }

    /**
     * Return Address at provided index into chain.
     *
     * @return Address
	 *
     */
    public Addresss getAddressAt(int addrIdx) {
        return new Addresss(params, cKey, addrIdx);
    }

    /**
     * Return BIP44 path for this chain (m / purpose' / coin_type' / account' / chain).
     *
     * @return String
     *
     */
    public String getPath() {
        return strPath;
    }

    /**
     * Write chain to JSONObject.
     * For debugging only.
     *
     * @return JSONObject
     *
     */
    public JSONObject toJSON() {
        try {
            JSONObject obj = new JSONObject();

            obj.put("path", getPath());

            JSONArray addresses = new JSONArray();
            for(int i = 0; i < 2; i++) {
                Addresss addr = new Addresss(params, cKey, i);
                addresses.put(addr.toJSON());
            }
            obj.put("addresses", addresses);

            return obj;
        }
        catch(JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

}
