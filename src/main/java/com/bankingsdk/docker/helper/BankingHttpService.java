package com.bankingsdk.docker.helper;

import com.bankingsdk.docker.exceptions.ApiCallException;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @serial exclude
 */
public class BankingHttpService {
    private static final int DEFAULT_REQUEST_TIMEOUT = 10_000;

    private Map<String, String> headers;
    private Map<String, String> parameters;
    private HttpResponse response;
    private HttpEntity payload;
    private Charset charset;
    private SSLContext sslContext;
    private RequestConfig requestConfig;
    private int connectTimeout;
    private int socketTimeout;
    private int connectionRequestTimeout;
    private HttpRequestRetryHandler retryHandler;
    private List<HttpRequestInterceptor> requestInterceptors;
    private List<HttpResponseInterceptor> responseInterceptors;
    private String uri;
    private String fragment;

    public BankingHttpService() {
        headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.put(HttpHeaders.ACCEPT, "application/json");
        parameters = new HashMap<>();
        charset = Charset.forName("UTF-8");
        connectTimeout = DEFAULT_REQUEST_TIMEOUT;
        socketTimeout = DEFAULT_REQUEST_TIMEOUT;
        connectionRequestTimeout = DEFAULT_REQUEST_TIMEOUT;
        requestInterceptors = new ArrayList<>();
        responseInterceptors = new ArrayList<>();
    }

    /*
     * Building part
     */

    public BankingHttpService setRequestPayload(StringEntity payload) {
        this.payload = payload;
        return this;
    }

    public BankingHttpService addHeader(String name, String value) {
        if (name != null && value != null) {
            headers.put(name, value);
        }

        return this;
    }

    public BankingHttpService removeHeader(String name) {
        if (name != null) {
            headers.remove(name);
        }

        return this;
    }

    public BankingHttpService addParameter(String name, String unescapedValue) {
        if (name != null && unescapedValue != null) {
            parameters.put(name, unescapedValue);
        }

        return this;
    }

    public BankingHttpService setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;

        return this;
    }

    public BankingHttpService setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;

        return this;
    }

    public BankingHttpService setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;

        return this;
    }

    public BankingHttpService setHeaders(Map<String, String> headers) {
        headers.forEach((k, v) -> this.headers.put(k, v));

        return this;
    }

    public BankingHttpService setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;

        return this;
    }

    public BankingHttpService setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;

        return this;
    }

    public BankingHttpService setCharset(Charset charset) {
        this.charset = charset;

        return this;
    }

    public BankingHttpService setPayload(String payload) {
        this.payload = new StringEntity(payload, charset);

        return this;
    }

    public BankingHttpService setPayload(HttpEntity payload) {
        this.payload = payload;

        return this;
    }

    public BankingHttpService setRequestInterceptors(List<HttpRequestInterceptor> requestInterceptors) {
        if (requestInterceptors != null) {
            this.requestInterceptors = requestInterceptors;
        }

        return this;
    }

    public BankingHttpService setResponseInterceptors(List<HttpResponseInterceptor> responseInterceptors) {
        if (responseInterceptors != null) {
            this.responseInterceptors = responseInterceptors;
        }

        return this;
    }

    public BankingHttpService addRequestInterceptors(HttpRequestInterceptor requestInterceptor) {
        this.requestInterceptors.add(requestInterceptor);

        return this;
    }

    public BankingHttpService addResponseInterceptors(HttpResponseInterceptor responseInterceptor) {
        this.responseInterceptors.add(responseInterceptor);

        return this;
    }

    public BankingHttpService setUri(String uri) {
        this.uri = uri;

        return this;
    }

    public String getFragment() {
        return fragment;
    }

    public BankingHttpService setFragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    /*
     * Requesting part
     */
    private RequestConfig getRequestConfig() {
        return requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();
    }

    private List<Header> getHeaders() {
        List<Header> headersBuilder = new ArrayList<>();

        headers.forEach((k, v) -> {
            if ((v != null) && !v.isEmpty()) {
                headersBuilder.add(new BasicHeader(k, v));
            }
        });

        return headersBuilder;
    }

    public BankingHttpService setSSLContext(SSLContext sslContext) {
        this.sslContext = sslContext;

        return this;
    }

    private SSLContext getSSLContext() {
        return sslContext;
    }

    private HttpRequestRetryHandler getRetryHandler() {
        return retryHandler;
    }

    private List<HttpRequestInterceptor> getRequestInterceptors() {
        return requestInterceptors;
    }

    private List<HttpResponseInterceptor> getResponseInterceptors() {
        return responseInterceptors;
    }

    private HttpClient getClient() {

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        if (getSSLContext() != null) {
            httpClientBuilder.setSSLContext(getSSLContext());
            httpClientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext));
        }
        httpClientBuilder.setDefaultRequestConfig(getRequestConfig());
        httpClientBuilder.setDefaultHeaders(getHeaders());
        if (getRetryHandler() != null) {
            httpClientBuilder.setRetryHandler(getRetryHandler()); // TODO : new CustomHttpRequestRetryHandler(RETRY_COUNTS, true)
        }

        // TODO : new IdempotencyInterceptor()
        getRequestInterceptors().forEach(httpClientBuilder::addInterceptorLast);
        getResponseInterceptors().forEach(httpClientBuilder::addInterceptorLast);


        httpClientBuilder.setConnectionReuseStrategy(new DefaultClientConnectionReuseStrategy());

        return httpClientBuilder.build();
    }

    private URIBuilder buildUrl(String uri) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(uri);

        if (parameters.size() > 0) {
            parameters.forEach(builder::addParameter);
        }

        builder.setFragment(fragment);

        return builder;
    }

    public BankingHttpService get() throws ApiCallException {
        return get(uri);
    }

    public BankingHttpService get(String uri) throws ApiCallException {
        try {
            URIBuilder builder = buildUrl(uri);

            HttpGet request = new HttpGet(builder.build().toString());

            HttpClient client = getClient();
            response = client.execute(request);
        } catch (IOException | URISyntaxException e) {
            throw new ApiCallException(e);
        }

        return this;
    }

    public BankingHttpService post() throws ApiCallException {
        return post(uri);
    }

    public BankingHttpService post(String uri) throws ApiCallException {
        try {
            URIBuilder builder = buildUrl(uri);
            HttpPost request = new HttpPost(builder.build().toString());
            if (payload != null) {
                request.setEntity(payload);
            }
            HttpClient client = getClient();
            response = client.execute(request);
        } catch (URISyntaxException | IOException e) {
            throw new ApiCallException(e);
        }

        return this;
    }

    public BankingHttpService delete() throws ApiCallException {
        return delete(uri);
    }

    public BankingHttpService delete(String uri) throws ApiCallException {
        try {
            URIBuilder builder = buildUrl(uri);
            HttpDelete request = new HttpDelete(builder.build().toString());
            HttpClient client = getClient();
            response = client.execute(request);
        } catch (IOException | URISyntaxException e) {
            throw new ApiCallException(e);
        }

        return this;
    }

    /*
     * Response part
     */

    public HttpResponse getResponse() {
        return response;
    }

    public HttpEntity getResponsePayload() {
        if (response == null) {
            return null;
        }

        return response.getEntity();
    }

    public String getResponsePayloadAsString() throws ApiCallException {
        if (response == null) {
            return null;
        }

        try {
            return EntityUtils.toString(getResponsePayload());
        } catch (IOException e) {
            throw new ApiCallException(e);
        }
    }

    public int getRequestStatus() {
        if (response == null) {
            return -1;
        }

        return response.getStatusLine().getStatusCode();
    }
}
