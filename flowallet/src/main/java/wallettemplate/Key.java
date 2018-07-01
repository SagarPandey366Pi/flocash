package wallettemplate;

import javax.xml.bind.ValidationException;

/*
 * Changes are done by Sagar. This is included to implement Deterministic feature and generate Public key and private key*/
public interface Key extends Cloneable
{
	public byte[] getPrivate();
	
	public byte[] getPublic();
	
	public boolean isCompressed();
	
	public Key getReadOnly();
	
	public Key clone() throws CloneNotSupportedException;
	
	public byte[] sign(byte[] data) throws ValidationException;
	
	public boolean verify(byte[] data,  byte[] signature);
}
