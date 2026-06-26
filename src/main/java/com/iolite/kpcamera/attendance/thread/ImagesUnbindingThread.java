package com.iolite.kpcamera.attendance.thread;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.iolite.kpcamera.attendance.model.responses.RecongnizedStudentResDto;

@Component
@Scope("prototype")
public class ImagesUnbindingThread implements Runnable {

    private List<String> duplicateStudentList;
    private String megBoxIp;

    // ← CHANGED: Map<String, String> instead of Map<Integer, String>
    private Map<String, String> studentTokenDetails;

    private static String megBoxProtocal = "http://";
    private static String megBoxUri = "/v1/MEGBOX/";

    // ← CHANGED: constructor parameter Map<String, String>
    public ImagesUnbindingThread(
            List<String> duplicateStudentList,
            String megBoxIp,
            Map<String, String> studentTokenDetails) {
        this.duplicateStudentList = duplicateStudentList;
        this.megBoxIp = megBoxIp;
        this.studentTokenDetails = studentTokenDetails;
    }

    @Override
    public void run() {
        System.out.println("Starting of Unbinding Thread");
        PoolingHttpClientConnectionManager cm = null;

        for (String data : duplicateStudentList) {
            try {
                // ← CHANGED: split by "|" instead of "-"
                // because UUID contains "-" which breaks split!
                // Key format: "UUID|cameraIp"
                // Example: "018f4e3d-6f2c-7c1a-b8d5|192.168.1.5"
                String[] arr = data.split("\\|");

                // arr[0] = UUID (studentId)
                // arr[1] = cameraIp (group name)

                if (arr.length < 2) {
                    System.out.println(
                        "Invalid key format: " + data);
                    continue;
                }

                String studentId = arr[0]; // ← UUID String
                String cameraIp  = arr[1]; // ← group name

                // ← CHANGED: String key lookup
                //   (no Integer.parseInt!)
                String faceToken =
                        studentTokenDetails.get(studentId);

                if (faceToken == null
                        || faceToken.isEmpty()) {
                    System.out.println(
                        "No face token for student: "
                            + studentId);
                    continue;
                }

                // SSL setup (unchanged)
                SSLContextBuilder builder =
                        new SSLContextBuilder();
                try {
                    builder.loadTrustMaterial(
                            null,
                            new TrustSelfSignedStrategy());
                } catch (NoSuchAlgorithmException
                        | KeyStoreException e) {
                    // ignore
                }

                SSLConnectionSocketFactory
                        sslConnectionSocketFactory = null;
                try {
                    sslConnectionSocketFactory =
                            new SSLConnectionSocketFactory(
                                    builder.build(),
                                    NoopHostnameVerifier.INSTANCE);
                } catch (KeyManagementException
                        | NoSuchAlgorithmException e) {
                    // ignore
                }

                Registry<ConnectionSocketFactory> registry =
                        RegistryBuilder
                        .<ConnectionSocketFactory>create()
                        .register("http",
                                new PlainConnectionSocketFactory())
                        .register("https",
                                sslConnectionSocketFactory)
                        .build();

                cm = new PoolingHttpClientConnectionManager(
                        registry);
                cm.setMaxTotal(100);

                CloseableHttpClient httpclient =
                        HttpClients.custom()
                        .setSSLSocketFactory(
                                sslConnectionSocketFactory)
                        .setRedirectStrategy(
                                new LaxRedirectStrategy())
                        .setConnectionManager(cm)
                        .build();

                HttpComponentsClientHttpRequestFactory
                        requestFactory =
                        new HttpComponentsClientHttpRequestFactory();
                requestFactory.setHttpClient(httpclient);

                RestTemplate restTemplate =
                        new RestTemplate(requestFactory);

                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> entityforGet =
                        new HttpEntity<>(headers);

                // ← cameraIp used as group (arr[1])
                // Unbind API call (unchanged logic)
                restTemplate.exchange(
                        megBoxProtocal +
                        megBoxIp +
                        megBoxUri +
                        "faceGroups/" +
                        cameraIp +    // ← arr[1] = cameraIp
                        "/" +
                        faceToken,
                        HttpMethod.DELETE,
                        entityforGet,
                        String.class);

                cm.close();

            } catch (Exception e) {
                System.out.println(
                    "Error unbinding student: " +
                    data + " | " + e.getMessage());
            }
        }
        System.out.println("End of Unbinding Thread");
    }
}