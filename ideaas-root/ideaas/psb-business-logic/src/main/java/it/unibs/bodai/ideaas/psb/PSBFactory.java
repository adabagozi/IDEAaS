package it.unibs.bodai.ideaas.psb;

import java.io.IOException;
import java.util.Optional;

import it.unibs.bodai.ideaas.psb.implementation.PSBBusinessLogicImpl;

public class PSBFactory {
	 private static final PSBFactory factory = new PSBFactory();
	    private static Optional<IPSBBusinessLogic> psbBusinessLogicInstance = Optional.empty(); 
	    
	    private PSBFactory() {
	        
	    }
	    
	    public static final PSBFactory getPSBFactory(){
	        return factory;
	    }
	    
	    public IPSBBusinessLogic getPSBBusinessLogic() throws IOException {
	        if (!psbBusinessLogicInstance.isPresent()) {
	        	psbBusinessLogicInstance = Optional.of(new PSBBusinessLogicImpl());
	        }
	        return psbBusinessLogicInstance.get();
	    }


}
