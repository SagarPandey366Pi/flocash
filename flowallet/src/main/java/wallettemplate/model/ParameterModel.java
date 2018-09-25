package wallettemplate.model;

import org.floj.core.NetworkParameters;
import org.floj.params.MainNetParams;
import org.floj.params.TestNet3Params;

public class ParameterModel {

	private NetworkParameters parameters;
	private static ParameterModel object;
	
	private ParameterModel()
	{
		parameters = TestNet3Params.get();
		//parameters = MainNetParams.get();
	}
	
	public static ParameterModel getInstance() {
		if (object == null) {
			object = new ParameterModel();
		}
		return object;
	}

	public NetworkParameters getParameters() {
		return parameters;
	}

	public void setParameters(NetworkParameters parameters) {
		this.parameters = parameters;
	}
}
