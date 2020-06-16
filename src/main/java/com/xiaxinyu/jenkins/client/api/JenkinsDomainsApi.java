package com.xiaxinyu.jenkins.client.api;

import com.alibaba.fastjson.JSONObject;
import com.xiaxinyu.jenkins.client.api.model.JenkinsDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XIAXINYU3
 */
@Component
@Slf4j
public class JenkinsDomainsApi {

    @Autowired
    JenkinsHttpClientExtend clientExtend;

    private static final String URL_CREATE_DOMAIN = "credentials/store/system/createDomain";
    private static final String URL_EXIST_DOMAIN = "credentials/store/system/domain/%s/";

    public boolean createDomain(JenkinsDomain domain) {
        log.info("创建Jenkins域，domain={}", JSONObject.toJSONString(domain));
        int httpStatusCode;
        try {
            List<NameValuePair> data = new ArrayList<>();
            NameValuePair pair = new BasicNameValuePair("json", JSONObject.toJSONString(domain));
            data.add(pair);
            httpStatusCode = clientExtend.postFormReturnStatusCode(URL_CREATE_DOMAIN, data);
        } catch (Exception e) {
            throw new RuntimeException(String.format("创建Jenkins域出现错误，参数=%s", domain));
        }
        log.info("完成创建Jenkins域，httpStatusCode={}", httpStatusCode);
        if (HttpStatus.SC_OK == httpStatusCode) {
            return true;
        }
        return false;
    }

    public boolean existDomain(String domainName) {
        log.info("检查Jenkins域，domainName={}", domainName);
        int httpStatusCode;
        try {
            String url = String.format(URL_EXIST_DOMAIN, domainName);
            httpStatusCode = clientExtend.getFormReturnStatusCode(url);
            log.info("检查Jenkins域，httpStatusCode={}", httpStatusCode);
        } catch (Exception e) {
            throw new RuntimeException(String.format("检查域是否存在出现错误. domainName = %s", domainName));
        }

        if (HttpStatus.SC_OK == httpStatusCode) {
            return true;
        } else if (HttpStatus.SC_NOT_FOUND == httpStatusCode) {
            return false;
        } else {
            throw new RuntimeException(String.format("检查Jenkins域请求出现异常，domainName=%s", domainName));
        }
    }
}