package wallettemplate;

import javax.xml.bind.ValidationException;

import org.spongycastle.util.Arrays;

/*
 * Changes are done by Sagar. This is included to implement Deterministic feature and generate Public key and private key*/
public class DeterministicPublicKey implements Key 
{
	private byte[] pub;
	private boolean compressed;

	public DeterministicPublicKey (byte[] pub, boolean compressed)
	{
		this.pub = pub;
		this.compressed = compressed;
	}
	
	@Override
	public boolean isCompressed() {
		// TODO Auto-generated method stub
		return compressed;
	}
	
	public void setCompressed(boolean compressed) 
	{
		this.compressed = compressed;
	}

	@Override
	public Key getReadOnly() {
		return this;
	}

	@Override
	public DeterministicPublicKey clone() throws CloneNotSupportedException 
	{
		DeterministicPublicKey c = (DeterministicPublicKey)super.clone();
		c.pub = Arrays.clone(pub);
		return c;
	}

	@Override
	public byte[] getPrivate() 
	{
		return null;
	}
	
	@Override
	public byte[] getPublic() 
	{
		return Arrays.clone(pub);
	}
	@Override
	public byte[] sign(byte[] data) throws ValidationException 
	{
		throw new ValidationException ("Can not sign with public key");
	}

	@Override
	public boolean verify(byte[] data, byte[] signature) 
	{
		return false;
	}
	
}
