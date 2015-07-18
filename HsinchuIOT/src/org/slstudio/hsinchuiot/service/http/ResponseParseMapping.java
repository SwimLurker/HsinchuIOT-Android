package org.slstudio.hsinchuiot.service.http;

import java.util.HashMap;
import java.util.Map;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.responseparser.DeviceListParser;
import org.slstudio.hsinchuiot.responseparser.IOTAggregationDataParser;
import org.slstudio.hsinchuiot.responseparser.IOTMonitorDataParser;
import org.slstudio.hsinchuiot.responseparser.IOTReportDataParser;
import org.slstudio.hsinchuiot.responseparser.SessionIDParser;
import org.slstudio.hsinchuiot.responseparser.SiteListJSONParser;
import org.slstudio.hsinchuiot.responseparser.UserParser;

public class ResponseParseMapping {
	public static Map<String, ResponseParser> parserMapping = new HashMap<String, ResponseParser>();
	
	private static ResponseParser defaultParser = new DummyResponseParser();
	
	static{
		parserMapping.put(Constants.ServerAPIURI.GET_SESSION_ID, new SessionIDParser());
		parserMapping.put(Constants.ServerAPIURI.LOGIN, new UserParser());
		parserMapping.put(Constants.ServerAPIURI.DEVICE_LIST, new DeviceListParser());
		parserMapping.put(Constants.ServerAPIURI.GET_REALTIME_DATA, new IOTMonitorDataParser());
		parserMapping.put(Constants.ServerAPIURI.GET_HOUR_AGG_DATA, new IOTAggregationDataParser());
		parserMapping.put(Constants.ServerAPIURI.GET_HOURS_AGG_DATA, new IOTAggregationDataParser());
		parserMapping.put(Constants.ServerAPIURI.GET_DAY_AGG_DATA, new IOTAggregationDataParser());
		parserMapping.put(Constants.ServerAPIURI.GET_WEEK_AGG_DATA, new IOTAggregationDataParser());
		parserMapping.put(Constants.ServerAPIURI.GET_MONTH_AGG_DATA, new IOTAggregationDataParser());
		parserMapping.put(Constants.ServerAPIURI.GET_REPORT_DATA, new IOTReportDataParser());
		//parserMapping.put(Constants.ServerAPIURI.GET_DEVICE_LIST_WITH_AGG_DATA, new DeviceListWithAggregationDataParser());
		parserMapping.put(Constants.ServerAPIURI.GET_SITE_LIST_WITH_AGG_DATA, new SiteListJSONParser());
	}
	
	public static ResponseParser getResponseParser(String requestURI){
		if (parserMapping.containsKey(requestURI)){
			return parserMapping.get(requestURI);
		}else{
			return defaultParser;
		}
	}
	
}


