package org.apache.rocketmq.connect.http.sink;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import io.openmessaging.KeyValue;
import io.openmessaging.connector.api.component.task.sink.SinkTaskContext;
import io.openmessaging.connector.api.data.ConnectRecord;
import io.openmessaging.connector.api.data.RecordOffset;
import io.openmessaging.connector.api.data.RecordPartition;
import io.openmessaging.internal.DefaultKeyValue;
import org.apache.rocketmq.connect.http.sink.constant.HttpConstant;
import org.apache.rocketmq.connect.http.sink.enums.AuthTypeEnum;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HttpSinkConnectorTest {

    private final HttpSinkConnector httpSinkConnector = new HttpSinkConnector();

    @Test
    public void testTaskConfigs() {
        Assert.assertEquals(httpSinkConnector.taskConfigs(1).size(), 1);
    }

    @Test
    public void testBasicPut() throws InterruptedException {
        HttpSinkTask httpSinkTask = new HttpSinkTask();
        KeyValue keyValue = new DefaultKeyValue();
        // test basic
        keyValue.put(HttpConstant.URL_PATTERN_CONSTANT, "http://localhost:8080/putEventsByBasicAuth");
        keyValue.put(HttpConstant.METHOD_CONSTANT, HttpConstant.POST_METHOD);
        keyValue.put(HttpConstant.AUTH_TYPE_CONSTANT, AuthTypeEnum.BASIC.getAuthType());
        keyValue.put(HttpConstant.TIMEOUT_CONSTANT, 60000);
        keyValue.put(HttpConstant.BASIC_USER_CONSTANT, "xxxx");
        keyValue.put(HttpConstant.BASIC_PASSWORD_CONSTANT, "xxxx");
        Map<String, String> bodyMap = Maps.newHashMap();
        bodyMap.put("id", "123");
        keyValue.put(HttpConstant.BODYS_CONSTANT, JSONObject.toJSONString(bodyMap));
        httpSinkTask.validate(keyValue);
        httpSinkTask.init(keyValue);
        List<ConnectRecord> connectRecordList = Lists.newArrayList();
        ConnectRecord connectRecord = new ConnectRecord(null, null, System.currentTimeMillis());
        connectRecord.addExtension(HttpConstant.HTTP_QUERY_VALUE, JSONObject.toJSONString(bodyMap));
        connectRecord.setData(JSONObject.toJSONString(new HashMap<>()));
        connectRecordList.add(connectRecord);
        httpSinkTask.start(new SinkTaskContext() {
            @Override
            public String getConnectorName() {
                return null;
            }

            @Override
            public String getTaskName() {
                return null;
            }

            @Override
            public void resetOffset(RecordPartition recordPartition, RecordOffset recordOffset) {

            }

            @Override
            public void resetOffset(Map<RecordPartition, RecordOffset> offsets) {

            }

            @Override
            public void pause(List<RecordPartition> partitions) {

            }

            @Override
            public void resume(List<RecordPartition> partitions) {

            }

            @Override
            public Set<RecordPartition> assignment() {
                return null;
            }
        });
        httpSinkTask.put(connectRecordList);
    }

    @Test
    public void testApiKeyPut() throws InterruptedException {
        HttpSinkTask httpSinkTask = new HttpSinkTask();
        KeyValue keyValue = new DefaultKeyValue();
        // test api key
        keyValue.put(HttpConstant.URL_PATTERN_CONSTANT, "http://localhost:8080/putEventsByAPiKey");
        keyValue.put(HttpConstant.METHOD_CONSTANT, HttpConstant.POST_METHOD);
        keyValue.put(HttpConstant.AUTH_TYPE_CONSTANT, AuthTypeEnum.API_KEY.getAuthType());
        keyValue.put(HttpConstant.TIMEOUT_CONSTANT, 60000);
        keyValue.put(HttpConstant.API_KEY_NAME, "Token");
        keyValue.put(HttpConstant.API_KEY_VALUE, "xxxx");
        Map<String, String> bodyMap = Maps.newHashMap();
        bodyMap.put("id", "123");
        keyValue.put(HttpConstant.BODYS_CONSTANT, JSONObject.toJSONString(bodyMap));
        httpSinkTask.validate(keyValue);
        httpSinkTask.init(keyValue);
        List<ConnectRecord> connectRecordList = Lists.newArrayList();
        ConnectRecord connectRecord = new ConnectRecord(null, null, System.currentTimeMillis());
        connectRecord.addExtension(HttpConstant.HTTP_QUERY_VALUE, JSONObject.toJSONString(bodyMap));
        connectRecord.setData(JSONObject.toJSONString(new HashMap<>()));
        connectRecordList.add(connectRecord);
        httpSinkTask.start(new SinkTaskContext() {
            @Override
            public String getConnectorName() {
                return null;
            }

            @Override
            public String getTaskName() {
                return null;
            }

            @Override
            public void resetOffset(RecordPartition recordPartition, RecordOffset recordOffset) {

            }

            @Override
            public void resetOffset(Map<RecordPartition, RecordOffset> offsets) {

            }

            @Override
            public void pause(List<RecordPartition> partitions) {

            }

            @Override
            public void resume(List<RecordPartition> partitions) {

            }

            @Override
            public Set<RecordPartition> assignment() {
                return null;
            }
        });
        httpSinkTask.put(connectRecordList);
    }

    @Test
    public void testOAuthPut() throws InterruptedException {
        HttpSinkTask httpSinkTask = new HttpSinkTask();
        KeyValue keyValue = new DefaultKeyValue();
        // test OAuth
        keyValue.put(HttpConstant.URL_PATTERN_CONSTANT, "http://localhost:8080/putEventsByOAuth2");
        keyValue.put(HttpConstant.METHOD_CONSTANT, HttpConstant.POST_METHOD);
        keyValue.put(HttpConstant.AUTH_TYPE_CONSTANT, AuthTypeEnum.OAUTH_CLIENT_CREDENTIALS.getAuthType());
        keyValue.put(HttpConstant.TIMEOUT_CONSTANT, 60000);
        keyValue.put(HttpConstant.OAUTH2_HTTP_METHOD_CONSTANT, HttpConstant.POST_METHOD);
        keyValue.put(HttpConstant.OAUTH2_ENDPOINT_CONSTANT, "http://localhost:8080/oauth/token");
        Map<String, String> queryStringMap = Maps.newHashMap();
        queryStringMap.put("grant_type", "xxxx");
        queryStringMap.put("scope", "xxxx");
        keyValue.put(HttpConstant.QUERY_STRING_PARAMETERS_CONSTANT, JSONObject.toJSONString(queryStringMap));
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put(HttpConstant.OAUTH_BASIC_KEY, "xxxx");
        headerMap.put(HttpConstant.OAUTH_BASIC_VALUE, "xxxx");
        keyValue.put(HttpConstant.HEADER_PARAMETERS_CONSTANT, JSONObject.toJSONString(headerMap));
        Map<String, String> bodyMap = Maps.newHashMap();
        bodyMap.put("id", "234");
        keyValue.put(HttpConstant.BODYS_CONSTANT, JSONObject.toJSONString(bodyMap));
        httpSinkTask.validate(keyValue);
        httpSinkTask.init(keyValue);
        List<ConnectRecord> connectRecordList = Lists.newArrayList();
        ConnectRecord connectRecord = new ConnectRecord(null, null, System.currentTimeMillis());
        connectRecord.addExtension(HttpConstant.HTTP_QUERY_VALUE, JSONObject.toJSONString(bodyMap));
        connectRecord.setData(JSONObject.toJSONString(new HashMap<>()));
        connectRecordList.add(connectRecord);
        httpSinkTask.start(new SinkTaskContext() {
            @Override
            public String getConnectorName() {
                return null;
            }

            @Override
            public String getTaskName() {
                return null;
            }

            @Override
            public void resetOffset(RecordPartition recordPartition, RecordOffset recordOffset) {

            }

            @Override
            public void resetOffset(Map<RecordPartition, RecordOffset> offsets) {

            }

            @Override
            public void pause(List<RecordPartition> partitions) {

            }

            @Override
            public void resume(List<RecordPartition> partitions) {

            }

            @Override
            public Set<RecordPartition> assignment() {
                return null;
            }
        });
        httpSinkTask.put(connectRecordList);
    }

}
