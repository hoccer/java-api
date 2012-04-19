package com.hoccer.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.logging.Logger;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
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

    private static SSLSocketFactory sSocketFactory = null;

    // Static Methods ----------------------------------------------------

    public static synchronized void initializeSsl(KeyStore pKeyStore, String pKeyStorePass, KeyStore pTrustStore)
            throws GeneralSecurityException {

        LOG.fine("initializeSsl");
        sSocketFactory = new SSLSocketFactory(pKeyStore, pKeyStorePass, pTrustStore) {

            @Override
            public Socket connectSocket(Socket pSock, String pHost, int pPort, InetAddress pLocalAddress,
                    int pLocalPort, HttpParams pParams) throws IOException {

                LOG.fine("connect socket");
                return super.connectSocket(pSock, pHost, pPort, pLocalAddress, pLocalPort, pParams);
            }
        };
        sSocketFactory.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
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

    @Override
    protected ClientConnectionManager createClientConnectionManager() {

        LOG.finer("createClientConnectionManager");

        if (sSocketFactory == null) {

            LOG.finer("using default connection manager");
            // no certificate - revert to default implementation
            return super.createClientConnectionManager();
        }

        LOG.finer("using connection manager with SSL socket factory");

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", sSocketFactory, 443));

        return new SingleClientConnManager(getParams(), registry);
    }



}
