package com.nerd.herd.cards.config;

import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class SslConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(SslConfiguration.class);

	protected static SSLContext getSslContext() throws KeyManagementException, KeyStoreException,
			NoSuchAlgorithmException {
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
		sslContext.init(null,
						new TrustManager[] { new X509TrustManager() {
							public java.security.cert.X509Certificate[] getAcceptedIssuers() {
								return null;
							}

							public void checkClientTrusted(X509Certificate[] certs, String authType) {
							}

							public void checkServerTrusted(X509Certificate[] certs, String authType) {
							}
						} }
				, null);
		return sslContext;
	}

	static {
		try {
			SSLContext sc = getSslContext();
			sc.init(null, new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} }, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
}
