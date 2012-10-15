package com.hoccer.client;


import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

public class HoccerHttp {

	/* keep connections alive for 30 seconds */
	private static final int KEEP_ALIVE_DURATION = 30;
	
	private HttpClient mClient;
	
	public HoccerHttp() {
	}
	
	public synchronized HttpClient getClient() {
		if(mClient == null) {
			HttpParams params = makeParams();
			ClientConnectionManager cm = makeConnectionManager(params);
			mClient = makeClient(params, cm);
		}
		return mClient;
	}
	
	public synchronized void dropClient() {
		mClient = null;
	}
	
	private HttpClient makeClient(HttpParams params, ClientConnectionManager cm) {
		DefaultHttpClient client = new DefaultHttpClient(cm, params);
		client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
		client.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				return KEEP_ALIVE_DURATION;
			}
		});
		return client;
	}
	
	private ClientConnectionManager makeConnectionManager(HttpParams params) {
        // support http and https
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        
        return new ThreadSafeClientConnManager(params, schemeRegistry);
	}
	
	private HttpParams makeParams() {
		// create parameter holder
        BasicHttpParams httpParams = new BasicHttpParams();
        
        // keep sockets in idle state for up to 70 seconds
        HttpConnectionParams.setSoTimeout(httpParams, 70 * 1000);
        // when connecting, try for up to 10 seconds
        HttpConnectionParams.setConnectionTimeout(httpParams, 10 * 1000);
        
        // XXX: after 1000 connections, the client will stop working
        ConnManagerParams.setMaxTotalConnections(httpParams, 1000);
        
        // don't open more than 5 connections per route at a time
        ConnPerRoute connPerRoute = new ConnPerRouteBean(5);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
        
        // always use HTTP 1.1
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        // always encode as UTF-8
        HttpProtocolParams.setContentCharset(httpParams, "UTF-8");

        return httpParams;
	}
	
}
