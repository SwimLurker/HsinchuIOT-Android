package org.slstudio.hsinchuiot.responseparser;

import org.slstudio.hsinchuiot.service.IOTException;
import org.w3c.dom.Document;

public class PushDeviceBindingParser extends XmlResponseParser<String>  {

	@Override
	public String parse(Document doc) throws IOTException {
		// TODO Auto-generated method stub
		return doc.toString();
	}

}
