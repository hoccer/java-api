package com.hoccer.http;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.logging.Logger;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import com.hoccer.util.HoccerLoggers;

/**
 * An HTTP client using a globally configured keystore with a certificate chain. Use static methods to configure the
 * keystore.
 * 
 * @author Arne Handt, it@handtwerk.de
 */
public class HttpClientWithKeystore extends DefaultHttpClient {

    // Constants ---------------------------------------------------------

    private static final String LOG_TAG = HttpClientWithKeystore.class.getSimpleName();

    private static final Logger LOG = HoccerLoggers.getLogger(LOG_TAG);

    // Static Variables --------------------------------------------------

    private static SchemeRegistry sRegistry;

    // Static Methods ----------------------------------------------------

    /* currently not used */
    private static synchronized void initializeSsl(KeyStore pTrustStore)
            throws GeneralSecurityException {

        LOG.fine("initializeSsl");

        LOG.finest("aliases:");
        Enumeration<String> aliases = pTrustStore.aliases();
        while (aliases.hasMoreElements()) {

            LOG.finest(" - " + aliases.nextElement());
        }

        LOG.finest("creating socket factory");
        SSLSocketFactory socketFactory = new SSLSocketFactory(pTrustStore);
        // {
        //
        // @Override
        // public Socket connectSocket(Socket pSock, String pHost, int pPort, InetAddress pLocalAddress,
        // int pLocalPort, HttpParams pParams) throws IOException {
        //
        // LOG.fine("connect socket");
        // return super.connectSocket(pSock, pHost, pPort, pLocalAddress, pLocalPort, pParams);
        // }
        //
        // @Override
        // public Socket createSocket() throws IOException {
        //
        // LOG.fine("create socket #1");
        // return super.createSocket();
        // }
        //
        // @Override
        // public Socket createSocket(Socket pSocket, String pHost, int pPort, boolean pAutoClose) throws IOException,
        // UnknownHostException {
        //
        // LOG.fine("create socket #2");
        // return super.createSocket(pSocket, pHost, pPort, pAutoClose);
        // }
        // };
        socketFactory.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);

        sRegistry = new SchemeRegistry();
        sRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        sRegistry.register(new Scheme("https", socketFactory, 443));
        sRegistry.register(new Scheme("http", socketFactory, 443)); // appears to be necessary...whatever...
    }

    public static SchemeRegistry getSchemeRegistry() {

        return sRegistry;
    }

    // Constructors ------------------------------------------------------

    public HttpClientWithKeystore() {

        super();
    }

    public HttpClientWithKeystore(ClientConnectionManager pConman, HttpParams pParams) {

        super(pConman, pParams);
    }

    public HttpClientWithKeystore(HttpParams pParams) {

        super(pParams);
    }

    // Protected Instance Methods ----------------------------------------

    // @Override
    // protected ClientConnectionManager createClientConnectionManager() {
    //
    // LOG.finer("createClientConnectionManager");
    //
    // if (sRegistry == null) {
    //
    // LOG.finer("using default connection manager");
    // // no certificate - revert to default implementation
    // return super.createClientConnectionManager();
    // }
    //
    // LOG.finer("using connection manager with SSL socket factory");
    // return new ThreadSafeClientConnManager(getParams(), sRegistry);
    // }
    //
    //
}
