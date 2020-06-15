package com.xiaxinyu.jenkins.client.api;

import com.alibaba.fastjson.JSONObject;
import com.xiaxinyu.jenkins.client.api.model.JenkinsCredential;
import com.xiaxinyu.jenkins.client.api.model.JenkinsCredentialVO;
import com.xiaxinyu.jenkins.client.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author XIAXINYU3
 */
@Component
@Slf4j
public class JenkinsCredentialsApi {
    private static final String URL_CREATE_CREDENTIAL = "credentials/store/system/domain/%s/createCredentials";
    private static final String URL_GET_CREDENTIALS = "credentials/store/system/domain/%s/api/json?pretty=true&depth=1";
    private static final String URL_FIND_CREDENTIAL = "credentials/store/system/domain/%s/credential/%s/api/json";
    private static final String URL_DELETE_CREDENTIAL = "credentials/store/system/domain/%s/credential/%s/doDelete";

    @Autowired
    JenkinsHttpClientExtend clientExtend;

    public boolean createCredential(String domainName, JenkinsCredential credential) {
        log.info("创建Jenkins凭证，domainName={}，credential={}", domainName, credential.getJSONObject().toJSONString());
        int httpStatusCode;
        try {

            List<NameValuePair> data = new ArrayList<>();
            data.add(new BasicNameValuePair("json", credential.getJSONObject().toJSONString()));
            String url = String.format(URL_CREATE_CREDENTIAL, domainName);
            httpStatusCode = clientExtend.postFormReturnStatusCode(url, data);
        } catch (Exception e) {
            throw new RuntimeException(String.format("创建凭据发生错误，domainName=%s, errorMessage=%s", domainName, e.getMessage()));
        }
        log.info("完成创建Jenkins凭证，httpStatusCode={}", httpStatusCode);
        if (HttpStatus.SC_MOVED_TEMPORARILY == httpStatusCode || HttpStatus.SC_OK == httpStatusCode) {
            return true;
        }
        return false;
    }

    public List<JenkinsCredentialVO> getCredentialsByDomain(String domainName) {
        log.info("根据Jenkins域名查询Jenkins凭据，domainName={}", domainName);
        //考虑数据量会很大，只获取id和displayName
        String url = String.format(URL_GET_CREDENTIALS, domainName).concat("&tree=credentials[id,displayName]");

        List<JenkinsCredentialVO> credentials = new ArrayList<>();
        try {

            String credentialStr = clientExtend.getFormReturnContent(url);
            if (StringUtils.isNotBlank(credentialStr)) {
                JSONObject object = JSONObject.parseObject(credentialStr);
                object.getJSONArray("credentials").forEach(c -> {
                    JenkinsCredentialVO vo = new JenkinsCredentialVO();
                    vo.setCredentialId(JSONUtils.checkNullAndType((JSONObject) c, "id", String.class));
                    vo.setName(JSONUtils.checkNullAndType((JSONObject) c, "displayName", String.class));
                    credentials.add(vo);
                });
            }
        } catch (HttpResponseException e) {
            if (!e.getMessage().contains("Not Found")) {
                log.error("根据Jenkins域名查询Jenkins凭据出现Http错误, domainName={}", domainName, e);
                throw new RuntimeException(String.format("查询Jenkins凭据出现Http错误，domainName=%s", domainName));
            }
        } catch (Exception e) {
            log.error("根据Jenkins域名查询Jenkins凭据出现错误, domainName={}", domainName, e);
            throw new RuntimeException(String.format("查询Jenkins凭据出现错误，domainName=%s", domainName));
        }
        return credentials;
    }

    /**
     * Jenkins没有提供查询凭据的API, 通过解析html来获取凭据
     *
     * @param htmlContent 凭据html内容
     * @param domainName  Jenkins域名
     * @return Jenkins凭据
     * @deprecated 不再使用，调用api查询
     */
    @Deprecated
    private static List<JenkinsCredentialVO> parseCredentialHtml(String htmlContent, String domainName) {
        List<JenkinsCredentialVO> credentialVOs = new ArrayList<>();

        Document html = Jsoup.parse(htmlContent);
        //选择数据表格
        Elements table = html.select("table.bigtable");
        if (null == table) {
            return credentialVOs;
        }
        //选择数据项
        Elements items = table.select("a[tooltip]");
        if (null == items || items.isEmpty()) {
            return credentialVOs;
        }

        Iterator<Element> iterator = items.iterator();
        while (iterator.hasNext()) {
            try {
                Element a = iterator.next();
                String credentialId = a.attr("href");
                credentialId = credentialId.replaceAll("credential/", StringUtils.EMPTY);
                String name = a.text();

                JenkinsCredentialVO vo = new JenkinsCredentialVO(name, credentialId);
                log.info("域[{}]下的Jenkins凭据：{}", domainName, vo);
                credentialVOs.add(vo);
            } catch (Exception e) {
                log.warn("解析Jenkins凭据出现错误：{}", e.getMessage());
            }
        }

        if (CollectionUtils.isEmpty(credentialVOs)) {
            throw new RuntimeException(String.format("没有解析到Jenkins凭据, domainName=%s", domainName));
        }

        return credentialVOs;
    }

    public JenkinsCredentialVO findJenkinsCredential(String domainName, String credentialId) {
        try {

            String url = String.format(URL_FIND_CREDENTIAL, domainName, credentialId);
            //判断Credential是否存在
            int httpCode = clientExtend.getFormReturnStatusCode(url);
            if (HttpStatus.SC_NOT_FOUND == httpCode) {
                log.warn("不存在对应Jenkins凭据，domainName={}，credentialId={}", domainName, credentialId);
                return null;
            }
            //获取Credential内容
            String response = clientExtend.get(url);
            if (StringUtils.isNotBlank(response)) {
                JSONObject object = JSONObject.parseObject(response);

                JenkinsCredentialVO vo = new JenkinsCredentialVO();
                vo.setCredentialId(JSONUtils.checkNullAndType(object, "id", String.class));
                vo.setName(JSONUtils.checkNullAndType(object, "displayName", String.class));
                return vo;
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("解析查询Jenkins凭据的报文出现错误，domainName=%s, credentialId=%s, errorMessage=%s", domainName, credentialId, e.getMessage()));
        }
        return null;
    }

    public boolean deleteCredential(String domainName, String credentialId) {
        log.info("准备删除Jenkins凭证，domainName={}，credentialId={}", domainName, credentialId);

        int httpStatusCode;
        try {

            String url = String.format(URL_DELETE_CREDENTIAL, domainName, credentialId);
            log.info("删除凭据URL：{}", url);

            List<NameValuePair> data = new ArrayList<>();
            data.add(new BasicNameValuePair("json", JSONObject.parseObject("{}").toJSONString()));
            data.add(new BasicNameValuePair("Submit", "Yes"));

            httpStatusCode = clientExtend.postFormReturnStatusCode(url, data);
        } catch (Exception e) {
            throw new RuntimeException(String.format("删除凭据发生错误，domainName=%s, credentialId=%s, errorMessage=%s", domainName, credentialId, e.getMessage()));
        }

        log.info("完成删除Jenkins凭证，httpStatusCode={}", httpStatusCode);
        if (HttpStatus.SC_MOVED_TEMPORARILY == httpStatusCode || HttpStatus.SC_OK == httpStatusCode) {
            return true;
        }
        return false;
    }
}
