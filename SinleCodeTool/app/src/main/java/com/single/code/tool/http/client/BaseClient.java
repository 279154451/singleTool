package com.single.code.tool.http.client;



import com.single.code.tool.http.request.XpRequest;

import okhttp3.Request;

/**
 * 
 * @author yao.guoju
 *
 */
public interface  BaseClient {
	 void newCall(XpRequest req, Callback cb);
	 void newCall(XpRequest req, Callback cb, boolean rspOnUi);
	 void newCall(Request req, Callback cb);
}
