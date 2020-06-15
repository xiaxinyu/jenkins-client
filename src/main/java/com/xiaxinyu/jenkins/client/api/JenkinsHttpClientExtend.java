package com.xiaxinyu.jenkins.client.api;

import com.crc.devops.devcloud.common.exception.AppException;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.client.util.RequestReleasingInputStream;
import com.offbytwo.jenkins.client.util.ResponseUtils;
import com.offbytwo.jenkins.client.validator.HttpResponseValidator;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.applet.AppletIOException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;

public class JenkinsHttpClientExtend extends JenkinsHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(JenkinsHttpClientExtend.class);

    private HttpResponseValidator httpResponseValidator;

    public JenkinsHttpClientExtend(URI uri, String username, String password) {
        super(uri, username, password);
        httpResponseValidator = new HttpResponseValidator();
    }

    @Override
    public InputStream getFile(URI path) throws IOException {
        HttpGet getMethod = null;
        CloseableHttpResponse response = null;
        InputStream in = null;
        try {
            getMethod = new HttpGet(path);
            response = getClient().execute(getMethod, this.getLocalContext());
            setJenkinsVersion(ResponseUtils.getJenkinsVersion(response));
            httpResponseValidator.validateResponse(response);
            in = new RequestReleasingInputStream(response.getEntity().getContent(), getMethod);
            return in;
        } catch (Exception ex) {
            if (response != null) {
                response.close();
                getMethod.releaseConnection();
            }
            logger.error("JenkinsHttpClientExtend.getFile()请求发生异常: ", ex);
            throw new AppletIOException(ex.getMessage());
        }

    }

    private CloseableHttpClient getClient() {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("client");
            field.setAccessible(true);
            return (CloseableHttpClient) field.get(this);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setJenkinsVersion(String version) {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("jenkinsVersion");
            field.setAccessible(true);
            field.set(this, version);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public URI getURI() {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("uri");
            field.setAccessible(true);
            return (URI) field.get(this);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 发送GET请求，请求结果以文本形式返回
     */
    public String getFormReturnContent(String url) throws IOException {
        CloseableHttpResponse response = getForm(url);
        httpResponseValidator.validateResponse(response);
        try {
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            EntityUtils.consume(response.getEntity());
            return result;
        } catch (Exception e) {
            response.close();
            logger.error("发送Get请求发生异常: ", e);
            throw new AppletIOException(e.getMessage());
        }
        //return IOUtils.toString(response.getEntity().getContent(), "UTF-8");
    }

    /**
     * 发送GET请求，请求结果返回Http Status Code
     */
    public int getFormReturnStatusCode(String url) throws IOException {
        CloseableHttpResponse response = getForm(url);
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            logger.debug("方法getFormReturnStatusCode的请求状态：{}", statusCode);
            if (statusCode >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                throw new AppException(String.format("请求出现错误：StatusCode=%d, Reason=%s", statusCode, response.getStatusLine().getReasonPhrase()));
            }
            logger.debug("[getFormReturnStatusCode]关闭流操作前");
            EntityUtils.consume(response.getEntity());
            logger.debug("[getFormReturnStatusCode]关闭流操作后");
            return statusCode;
        } catch (AppException e) {
            response.close();
            logger.error("发送Get请求发生异常: ", e);
            throw new AppletIOException(e.getMessage());
        }
    }

    public CloseableHttpResponse getForm(String url) throws IOException {
        HttpGet getMethod = null;
        try {
            url = handleUrl(url);
            logger.debug("Jenkins客户端请求URL：{}", url);
            getMethod = new HttpGet(url);
            getMethod.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            CloseableHttpResponse response = this.getClient().execute(getMethod, this.getLocalContext());
            return response;
        } catch (Exception e) {
            logger.error("执行Jenkins查询报错", e);
            throw new AppException(e.getMessage());
        }
    }

    public String getFormReturnHtmlContent(String url) throws Exception {
        String result;

        HttpGet getMethod = null;
        InputStream inputStream = null;
        try {
            url = handleUrl(url);
            logger.debug("客户端请求URL：{}", url);
            getMethod = new HttpGet(url);
            getMethod.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            CloseableHttpResponse response = this.getClient().execute(getMethod, this.getLocalContext());

            int status = response.getStatusLine().getStatusCode();
            if (status < HttpStatus.SC_OK || status >= HttpStatus.SC_BAD_REQUEST) {
                if (status == HttpStatus.SC_NOT_FOUND) {
                    return StringUtils.EMPTY;
                } else {
                    throw new HttpResponseException(status, response.getStatusLine().getReasonPhrase());
                }
            }
            //inputStream = response.getEntity().getContent();
            //result = IOUtils.toString(inputStream, "UTF-8");
            result = EntityUtils.toString(response.getEntity(), "UTF-8");
            EntityUtils.consume(response.getEntity());
            return result;
        } catch (Exception e) {
            logger.error("getFormReturnHtmlContent请求方法发生异常: ", e);
            throw new AppletIOException(e.getMessage());
        }
    }

    /**
     * 发送POST请求，请求结果以文本形式返回
     */
    public String postFormReturnContent(String url, List<NameValuePair> formData) throws IOException {
        CloseableHttpResponse response = postForm(url, formData);
        httpResponseValidator.validateResponse(response);
        try {
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            EntityUtils.consume(response.getEntity());
            return result;
        } catch (Exception e) {
            response.close();
            logger.error("发送POST请求发生异常: ", e);
            throw new AppletIOException(e.getMessage());
        }

        //return IOUtils.toString(response.getEntity().getContent(), "UTF-8");
    }

    /**
     * 发送POST请求，请求结果返回Http Status Code
     */
    public int postFormReturnStatusCode(String url, List<NameValuePair> formData) throws IOException {
        CloseableHttpResponse response = postForm(url, formData);
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            logger.info("方法postFormReturnStatusCode的请求状态：{}", statusCode);
            if (statusCode >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                throw new AppException(String.format("请求出现错误：StatusCode=%d, Reason=%s", statusCode, response.getStatusLine().getReasonPhrase()));
            }
            logger.debug("[postFormReturnStatusCode]关闭流操作前");
            EntityUtils.consume(response.getEntity());
            logger.debug("[postFormReturnStatusCode]关闭流操作后");
            return statusCode;
        } catch (AppException e) {
            response.close();
            logger.error("发送POST请求发生异常: ", e);
            throw new AppletIOException(e.getMessage());
        }
    }

    public CloseableHttpResponse postForm(String url, List<NameValuePair> formData) throws IOException {
        HttpPost postMethod = null;
        url = handleUrl(url);
        logger.debug("Jenkins客户端请求URL：{}", url);
        postMethod = new HttpPost(url);
        postMethod.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        HttpEntity entity = new UrlEncodedFormEntity(formData, "UTF-8");
        postMethod.setEntity(entity);
        CloseableHttpResponse response = this.getClient().execute(postMethod, this.getLocalContext());
        return response;

    }

    public String handleUrl(String url) {
        url = url.trim();
        if (!url.trim().startsWith("http")) {
            if (url.startsWith("/")) {
                url = getURI() + url;
            } else {
                url = getURI() + "/" + url;
            }
        }
        return url;
    }
}
