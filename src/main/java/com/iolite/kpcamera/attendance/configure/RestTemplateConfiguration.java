package com.iolite.kpcamera.attendance.configure;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.NoHttpResponseException;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestTemplateConfiguration {

	public static RestTemplate getRestTemplate() {
	    try {
	        // Trust all certificates (insecure, only for internal or testing use)
	        SSLContext sslContext = SSLContexts.custom()
	            .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
	            .build();

	        // Connection manager with pooling
	        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
	            .register("http", PlainConnectionSocketFactory.getSocketFactory())
	            .register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
	            .build();

	        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
	        connManager.setMaxTotal(100); // total max connections
	        connManager.setDefaultMaxPerRoute(20); // max per route (target IP)

	        // HttpClient with retry logic
	        CloseableHttpClient httpClient = HttpClients.custom()
	            .setConnectionManager(connManager)
	            .setRetryHandler((exception, executionCount, context) -> {
	                // Retry up to 3 times on transient errors (not timeouts)
	                if (executionCount >= 3) return false;
	                return exception instanceof NoHttpResponseException;
	            })
	            .build();

	        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
	        factory.setConnectTimeout(20_000);              // time to establish connection
	        factory.setReadTimeout(60_000);                 // time to wait for response
	        factory.setConnectionRequestTimeout(10_000);    // time to get connection from pool

	        RestTemplate restTemplate = new RestTemplate(factory);
	        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

	        return restTemplate;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

}
