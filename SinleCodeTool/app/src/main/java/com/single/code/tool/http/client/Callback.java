package com.single.code.tool.http.client;


import com.single.code.tool.http.response.XpResponse;

/**
 * 
 * @author yao.guoju
 *
 */
public abstract class Callback {
	private int tag;
	public abstract void onResponse(XpResponse rsp) throws Exception;
	public abstract void onFailure(String errorMsg);
	
	protected void addTag(int t) {
		tag = t;
	}
	
	public int getTag() {
		return tag;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return tag;
	}
	
}
