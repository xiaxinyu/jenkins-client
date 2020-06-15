package com.xiaxinyu.jenkins.client.api;

import com.alibaba.fastjson.JSONObject;
import com.crc.devops.devcloud.api.jenkins.connect.JenkinsConnectionManager;
import com.crc.devops.devcloud.api.jenkins.model.JenkinsDomain;
import com.crc.devops.devcloud.common.exception.AppException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XIAXINYU3
 */
public class JenkinsDomainsApi {
    private static final Logger logger = LoggerFactory.getLogger(JenkinsDomainsApi.class);
    private static final String URL_CREATE_DOMAIN = "credentials/store/system/createDomain";
    private static final String URL_EXIST_DOMAIN = "credentials/store/system/domain/%s/";

    public static boolean createDomain(JenkinsDomain domain) {
        logger.info("创建Jenkins域，domain={}", JSONObject.toJSONString(domain));
        int httpStatusCode;
        try {
            JenkinsHttpClientExtend clientExtend = JenkinsConnectionManager.getClient();
            List<NameValuePair> data = new ArrayList<>();
            NameValuePair pair = new BasicNameValuePair("json", JSONObject.toJSONString(domain));
            data.add(pair);
            httpStatusCode = clientExtend.postFormReturnStatusCode(URL_CREATE_DOMAIN, data);
        } catch (Exception e) {
            throw new AppException(String.format("创建Jenkins域出现错误，参数=%s", domain));
        }
        logger.info("完成创建Jenkins域，httpStatusCode={}", httpStatusCode);
        if (HttpStatus.SC_OK == httpStatusCode) {
            return true;
        }
        return false;
    }

    public static boolean existDomain(String domainName) {
        logger.info("检查Jenkins域，domainName={}", domainName);
        int httpStatusCode;
        try {
            JenkinsHttpClientExtend clientExtend = JenkinsConnectionManager.getClient();
            String url = String.format(URL_EXIST_DOMAIN, domainName);
            httpStatusCode = clientExtend.getFormReturnStatusCode(url);
            logger.info("检查Jenkins域，httpStatusCode={}", httpStatusCode);
        } catch (Exception e) {
            throw new AppException(String.format("检查域是否存在出现错误. domainName = %s", domainName));
        }

        if (HttpStatus.SC_OK == httpStatusCode) {
            return true;
        }else if (HttpStatus.SC_NOT_FOUND == httpStatusCode){
            return false;
        }else{
            throw new AppException(String.format("检查Jenkins域请求出现异常，domainName=%s", domainName));
        }
    }
}