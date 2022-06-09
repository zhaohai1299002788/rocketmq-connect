package org.apache.rocketmq.connect.http.sink;

import com.alibaba.fastjson.JSONObject;
import io.openmessaging.KeyValue;
import io.openmessaging.connector.api.component.task.sink.SinkTask;
import io.openmessaging.connector.api.component.task.sink.SinkTaskContext;
import io.openmessaging.connector.api.data.ConnectRecord;
import io.openmessaging.connector.api.errors.ConnectException;
import org.apache.rocketmq.connect.http.sink.auth.AuthFactory;
import org.apache.rocketmq.connect.http.sink.client.AbstractHttpClient;
import org.apache.rocketmq.connect.http.sink.client.ApacheHttpClientImpl;
import org.apache.rocketmq.connect.http.sink.common.ClientConfig;
import org.apache.rocketmq.connect.http.sink.constant.HttpConstant;
import org.apache.rocketmq.connect.http.sink.entity.HttpRequest;
import org.apache.rocketmq.connect.http.sink.enums.AuthTypeEnum;
import org.apache.rocketmq.connect.http.sink.thread.OAuthTokenRunnable;
import org.apache.rocketmq.connect.http.sink.utils.CheckUtils;
import org.apache.rocketmq.connect.http.sink.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HttpSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(HttpSinkTask.class);
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    protected String urlPattern;
    protected String method;
    protected String queryStringParameters;
    protected String headerParameters;
    protected String bodys;
    protected String authType;
    protected String basicUser;
    protected String basicPassword;
    protected String oauth2Endpoint;
    protected String oauth2ClientId;
    protected String oauth2ClientSecret;
    protected String oauth2HttpMethod;
    protected String proxyType;
    protected String proxyHost;
    protected String proxyPort;
    protected String proxyUser;
    protected String proxyPassword;
    protected String apiKeyName;
    protected String apiKeyValue;
    protected String timeout;
    private AbstractHttpClient httpClient;

    @Override
    public void put(List<ConnectRecord> sinkRecords) throws ConnectException {
        try {
            sinkRecords.forEach(connectRecord -> {
                ClientConfig clientConfig = new ClientConfig();
                clientConfig.setUrlPattern(urlPattern);
                clientConfig.setMethod(CheckUtils.checkNull(method) ? connectRecord.getExtension(HttpConstant.HTTP_METHOD) : method);
                clientConfig.setAuthType(authType);
                clientConfig.setHttpPathValue(connectRecord.getExtension(HttpConstant.HTTP_PATH_VALUE));
                clientConfig.setQueryStringParameters(JsonUtils.mergeJson(JSONObject.parseObject(connectRecord.getExtension(HttpConstant.HTTP_QUERY_VALUE)), JSONObject.parseObject(queryStringParameters)) == null ? null : JsonUtils.mergeJson(JSONObject.parseObject(connectRecord.getExtension(HttpConstant.HTTP_QUERY_VALUE)), JSONObject.parseObject(queryStringParameters)).toJSONString());
                clientConfig.setHeaderParameters(JsonUtils.mergeJson(JSONObject.parseObject(connectRecord.getExtension(HttpConstant.HTTP_HEADER)), JSONObject.parseObject(headerParameters)) == null ? null : JsonUtils.mergeJson(JSONObject.parseObject(connectRecord.getExtension(HttpConstant.HTTP_HEADER)), JSONObject.parseObject(headerParameters)).toJSONString());
                clientConfig.setBodys(JsonUtils.mergeJson(JSONObject.parseObject(connectRecord.getData().toString()), JSONObject.parseObject(bodys)).toJSONString());
                clientConfig.setProxyUser(proxyUser);
                clientConfig.setProxyPassword(proxyPassword);
                clientConfig.setProxyType(proxyType);
                clientConfig.setProxyPort(proxyPort);
                clientConfig.setProxyHost(proxyHost);
                clientConfig.setOauth2ClientId(oauth2ClientId);
                clientConfig.setOauth2ClientSecret(oauth2ClientSecret);
                clientConfig.setTimeout(timeout);
                clientConfig.setOauth2HttpMethod(oauth2HttpMethod);
                clientConfig.setOauth2Endpoint(oauth2Endpoint);
                clientConfig.setBasicUser(basicUser);
                clientConfig.setBasicPassword(basicPassword);
                clientConfig.setApiKeyName(apiKeyName);
                clientConfig.setApiKeyValue(apiKeyValue);
                final Map<String, String> headerMap = AuthFactory.auth(clientConfig);
                HttpRequest httpRequest = new HttpRequest();
                httpRequest.setBody(clientConfig.getBodys());
                httpRequest.setHeaderMap(headerMap);
                httpRequest.setMethod(clientConfig.getMethod());
                httpRequest.setTimeout(clientConfig.getTimeout());
                httpRequest.setUrl(JsonUtils.queryStringAndPathValue(clientConfig.getUrlPattern(), clientConfig.getQueryStringParameters(), connectRecord.getExtension(HttpConstant.HTTP_PATH_VALUE)));
                try {
                    httpClient.execute(httpRequest);
                } catch (IOException e) {
                    log.error("HttpSinkTask | put | execute | error => ", e);
                }
            });
        } catch (Exception e) {
            log.error("HttpSinkTask | put | error => ", e);
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void validate(KeyValue config) {
        if (CheckUtils.checkNull(config.getString(HttpConstant.URL_PATTERN_CONSTANT))
                || CheckUtils.checkNull(config.getString(HttpConstant.AUTH_TYPE_CONSTANT))
                || CheckUtils.checkNull(config.getString(HttpConstant.BODYS_CONSTANT))) {
            throw new RuntimeException("http required parameter is null !");
        }
        final List<AuthTypeEnum> collect = Arrays.stream(AuthTypeEnum.values()).filter(authTypeEnum -> authTypeEnum.getAuthType().equals(config.getString(HttpConstant.AUTH_TYPE_CONSTANT))).collect(Collectors.toList());
        if (collect.isEmpty()) {
            throw new RuntimeException("authType required parameter check is fail !");
        }
    }

    @Override
    public void init(KeyValue config) {
        urlPattern = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.URL_PATTERN_CONSTANT));
        method = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.METHOD_CONSTANT));
        queryStringParameters = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.QUERY_STRING_PARAMETERS_CONSTANT));
        headerParameters = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.HEADER_PARAMETERS_CONSTANT));
        bodys = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.BODYS_CONSTANT));
        authType = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.AUTH_TYPE_CONSTANT));
        basicUser = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.BASIC_USER_CONSTANT));
        basicPassword = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.BASIC_PASSWORD_CONSTANT));
        oauth2Endpoint = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.OAUTH2_ENDPOINT_CONSTANT));
        oauth2ClientId = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.OAUTH2_CLIENTID_CONSTANT));
        oauth2ClientSecret = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.OAUTH2_CLIENTSECRET_CONSTANT));
        oauth2HttpMethod = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.OAUTH2_HTTP_METHOD_CONSTANT));
        proxyType = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.PROXY_TYPE_CONSTANT));
        proxyHost = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.PROXY_HOST_CONSTANT));
        proxyPort = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.PROXY_PORT_CONSTANT));
        proxyUser = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.PROXY_USER_CONSTANT));
        proxyPassword = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.PROXY_PASSWORD_CONSTANT));
        timeout = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.TIMEOUT_CONSTANT));
        apiKeyName = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.API_KEY_NAME));
        apiKeyValue = CheckUtils.checkNullReturnDefault(config.getString(HttpConstant.API_KEY_VALUE));
    }

    @Override
    public void start(SinkTaskContext sinkTaskContext) {
        super.start(sinkTaskContext);
        try {
            httpClient = new ApacheHttpClientImpl();
            ClientConfig config = new ClientConfig();
            config.setProxyHost(proxyHost);
            config.setProxyPort(proxyPort);
            config.setProxyType(proxyType);
            config.setProxyUser(proxyUser);
            config.setProxyPassword(proxyPassword);
            httpClient.init(config);
            scheduledExecutorService.scheduleAtFixedRate(new OAuthTokenRunnable(), 1, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("HttpSinkTask | start | error => ", e);
        }
    }

    @Override
    public void stop() {
        httpClient.close();
    }
}
