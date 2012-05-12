package com.hoccer.client;


import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class HoccerHttp {

	private HttpClient mClient;
	private HttpParams mParams;
	private ClientConnectionManager mConnManager;
	
	public HoccerHttp() {
		makeParams();
		makeConnectionManager();
		makeClient();
	}
	
	public HttpClient getClient() {
		return mClient;
	}
	
	private void makeClient() {
		mClient = new DefaultHttpClient(mConnManager, mParams);
	}
	
	private void makeConnectionManager() {
        // support http and https
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        
        mConnManager = new ThreadSafeClientConnManager(mParams, schemeRegistry);
        
	}
	
	private void makeParams() {
		// create parameter holder
        BasicHttpParams httpParams = new BasicHttpParams();
        
        // keep sockets in idle state for up to 70 seconds
        HttpConnectionParams.setSoTimeout(httpParams, 70 * 1000);
        // when connecting, try for up to 10 seconds
        HttpConnectionParams.setConnectionTimeout(httpParams, 10 * 1000);
        
        // don't open more than 20 connections at the same time
        ConnManagerParams.setMaxTotalConnections(httpParams, 20);
        
        // don't open more than 10 connections per route
        ConnPerRoute connPerRoute = new ConnPerRouteBean(10);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
        
        // always use HTTP 1.1
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        // always encode as UTF-8
        HttpProtocolParams.setContentCharset(httpParams, "UTF-8");

        mParams = httpParams;
	}
	
}
