package com.single.code.tool.rxjava.http.client;


import com.single.code.tool.rxjava.http.request.XpRequest;

/**
 * 
 * @author yao.guoju
 *
 */
public interface  BaseClient {
	 void newCall(XpRequest req, Callback cb);
	 void newCall(XpRequest req, Callback cb, boolean rspOnUi);
}
