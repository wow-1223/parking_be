package com.parking.util.tool;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;

@Slf4j
public class HttpClientUtil {

    public static void closeQuietly(CloseableHttpClient httpClient) {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (Exception e) {
                log.error("关闭HttpClient失败", e);
            }
        }
    }

    public static void closeQuietly(CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (Exception e) {
                log.error("关闭HttpResponse失败", e);
            }
        }
    }
}