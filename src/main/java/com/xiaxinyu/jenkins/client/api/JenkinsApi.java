package com.xiaxinyu.jenkins.client.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;
import com.offbytwo.jenkins.model.Queue;
import com.xiaxinyu.jenkins.client.core.JenkinsConstant;
import com.xiaxinyu.jenkins.client.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.*;

@Component
@Slf4j
public class JenkinsApi {

    @Autowired
    JenkinsServer jenkins;

    @Autowired
    JenkinsHttpClientExtend clientExtend;

    public  String toJenkinsFile(JSONObject json) {
        try {

            String url = "/pipeline-model-converter/toJenkinsfile";

            List<NameValuePair> data = new ArrayList<NameValuePair>();
            NameValuePair nv = new BasicNameValuePair("json", json.toJSONString());
            data.add(nv);

            String result = clientExtend.postFormReturnContent(url, data);
            JSONObject temp = JSON.parseObject(result);
            JSONObject retData = JSONUtils.checkNullAndType(temp, "data", JSONObject.class);

            if ("failure".equals(retData.getString("result"))) {
                JSONArray errors = JSONUtils.checkNullAndType(retData, "errors", JSONArray.class);
                throw new RuntimeException("生成jenkins文件错误:" + errors.getJSONObject(0).getString("error"));
            }

            return JSONUtils.checkNullAndType(retData, "jenkinsfile", String.class);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            throw new RuntimeException("生成jenkins文件错误，请联系管理员!error:" + ex.getMessage());
        }
    }

    public Object getFile(String jobName, String path, String fileType) {
        InputStream in = null;
        try {
            Job job = jenkins.getJob(jobName);
            String url = job.getUrl() + "/ws/" + path;
            in = job.getClient().getFile(URI.create(url));

            if ("text".equals(fileType)) {
                return in;
            } else {
                try {
                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    String html = result.toString("UTF-8");
                    JSONArray files = retriveFiles(html);
                    return files;
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            try {
                if (ex instanceof HttpResponseException) {
                    HttpResponseException te = (HttpResponseException) ex;
                    if (te.getStatusCode() == 404) {
                        throw new RuntimeException("文件不存在!");
                    }
                }
                if (ex instanceof RuntimeException) {
                    throw (RuntimeException) ex;
                }
                throw new RuntimeException("获取Jenkins工作目录失败，请联系管理员!error:" + ex.getMessage());
            } finally {
                IOUtils.closeQuietly(in);
            }
        }
    }

    /**
     * 从html中提取工作空间文件目录信息
     *
     * @param html
     * @return
     */
    public static JSONArray retriveFiles(String html) {

        org.jsoup.nodes.Document doc = Jsoup.parse(html);
        Elements els = doc.select(".fileList tbody tr");
        JSONArray files = new JSONArray();
        JSONObject file = null;
        Element imgEl = null;
        String type = null;
        String name = null;
        for (Element el : els) {
            imgEl = el.selectFirst("img");
            if (imgEl.attr("src").endsWith("package.png")) {
                continue;
            }
            if (imgEl.attr("src").endsWith("folder.png")) {
                type = "folder";
            } else {
                type = "text";
            }

            name = el.child(1).select("a").text();
            file = new JSONObject();
            file.put("fileName", name);
            file.put("fileType", type);
            files.add(file);
        }
        return files;
    }


    public void createJob(Document doc, String jobName) throws Exception {
        log.debug("构建任务xml：" + doc.asXML());

        if (jenkins.getJob(jobName) == null) {
            jenkins.createJob(jobName, doc.asXML());
        } else {
            jenkins.updateJob(jobName, doc.asXML());
        }
    }


    public  JSONObject getJob(JSONObject job) throws Exception {
        JobWithDetails jobDetails = getJobDetails(job.getString("remoteJobName"));
        if (jobDetails == null) {
            return null;
        }

        //返回最后一次构建的信息
        BuildWithDetails buildDetail = jobDetails.getLastBuild().details();
        JSONObject jobInfo = new JSONObject();

        jobInfo.put("fullName", jobDetails.getFullName());
        jobInfo.put("lastBuildTime", buildDetail.getTimestamp());
        jobInfo.put("lastBuildResult", buildDetail.getResult());
        jobInfo.put("lastBuildDisplayName", buildDetail.getDisplayName());

        return jobInfo;
    }


    public  JobWithDetails getJobDetails(String remoteJobName) throws Exception {

        JobWithDetails jobDetails = jenkins.getJob(remoteJobName);
        return jobDetails;
    }


    public  JSONObject runJob(String jobName, Map<String, String> map) {
        try {
            JobWithDetails jobDetails = getJobDetails(jobName);

            log.info("运行CI任务，请求执行参数：jobName={}, executeParams={}", jobName, JSONObject.toJSONString(map));

            if (jobDetails.getQueueItem() != null) {
                throw new RuntimeException("任务正在运行，请稍后操作");
            }
            int num = jobDetails.getNextBuildNumber();
            QueueReference queue = jobDetails.build(map);
            JSONObject retObj = new JSONObject();
            retObj.put("runId", num + "");
            retObj.put("displayName", "#" + num);
            retObj.put("startDate", Calendar.getInstance().getTime());
            retObj.put("result", BuildResult.BUILDING.name());

            //持续时长
            retObj.put("duration", 0);

            return retObj;
        } catch (Exception ex) {
            if (ex instanceof HttpResponseException) {
                HttpResponseException ex1 = (HttpResponseException) ex;
                log.error("errorCode:" + ex1.getStatusCode());
            }
            log.error(ex.getMessage(), ex);
            throw new RuntimeException("运行job任务出错，请联系管理员！error:" + ex.getMessage());
        }
    }

    /**
     * @return
     * @throws IOException
     * @description 查看Job执行历史
     */
    public  JSONArray getJobRunHis(String jobName, String runId) {
        log.info("开始查询任务的运行历史：jobName={}，runId={}", jobName, runId);
        long start = System.currentTimeMillis();
        JSONArray hisArray = new JSONArray();
        boolean hasRunId = runId != null && !runId.trim().equals("");
        try {
            JobWithDetails jobDetails = getJobDetails(jobName);
            List<Build> builds = jobDetails.getBuilds();

            //获取排队中的任务
            JSONObject his = getWaitBuild(jobDetails, builds);
            if (his != null) {
                hisArray.add(his);
            }


            BuildWithDetails buildDetail = null;
            for (Build build : builds) {
                buildDetail = build.details();
                if (hasRunId && !buildDetail.getId().equals(runId)) {
                    continue;
                }

                his = getRunJson(buildDetail);

                //组装构件信息
                JSONArray artifacts = new JSONArray();
                JSONObject temp = null;
                for (Artifact artifact : buildDetail.getArtifacts()) {
                    temp = new JSONObject();
                    temp.put("displayPath", artifact.getDisplayPath());
                    temp.put("fileName", artifact.getFileName());
                    temp.put("relativePath", artifact.getRelativePath());
                    artifacts.add(temp);
                }

                his.put("artifacts", artifacts);

                hisArray.add(his);
            }


        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException("获取任务运行历史信息错误,请联系管理员!error:" + ex.getMessage());
        }
        if (hasRunId && hisArray.size() == 0) {
            throw new RuntimeException("无效的runId:" + runId);
        }

        long cost = (System.currentTimeMillis() - start) / 1000;
        log.info("结束查询任务的运行历史：jobName={}，runId={}, cost={}", jobName, runId, cost);
        return hisArray;
    }

    /**
     * 获取排队中的任务
     *
     * @param jobDetails
     * @param builds
     * @return
     * @throws IOException
     */
    private static JSONObject getWaitBuild(JobWithDetails jobDetails, List<Build> builds) throws IOException {
        if (jobDetails.getQueueItem() == null || jobDetails.getQueueItem().getId() <= 0) {
            return null;
        }
        int maxId = builds.size() == 0 ? 1 : (builds.get(0).details().getNumber() + 1);

        JSONObject buildInfo = new JSONObject();
        buildInfo.put("runId", maxId);
        buildInfo.put("queueId", jobDetails.getQueueItem().getId());
        buildInfo.put("displayName", "#" + maxId);
        buildInfo.put("startDate", "");
        buildInfo.put("result", "WAITING");
        //持续时长
        buildInfo.put("duration", 0);
        buildInfo.put("artifacts", new JSONArray());
        return buildInfo;
    }


    public  String getJobLastLog(String jobName) throws Exception {

        JobWithDetails jobDetails = jenkins.getJob(jobName);

        Build build = jobDetails.getLastBuild();
        if (build == null || build.details().getId() == null) {
            throw new RuntimeException("任务还没有运行过");
        } else {
            return build.details().getConsoleOutputText();
        }
    }


    public  JSONObject getJobLastRun(String jobName) {
        try {
            JobWithDetails jobDetails = getJobDetails(jobName);


            //获取最后一次构建信息，并返回
            BuildWithDetails buildDetail = jobDetails.getLastBuild().details();
            return getRunJson(buildDetail);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException("获取jenkins任务出错，请联系管理员!error:" + ex.getMessage());
        }
    }


    private static JSONObject getRunJson(BuildWithDetails buildDetail) {
        JSONObject buildInfo = new JSONObject();
        buildInfo.put("runId", buildDetail.getId());
        buildInfo.put("displayName", buildDetail.getDisplayName());
        buildInfo.put("startDate", new Date(buildDetail.getTimestamp()));
        buildInfo.put("result", getResult(buildDetail));
        //持续时长
        buildInfo.put("duration", buildDetail.getDuration());
        return buildInfo;
    }


    private static String getResult(BuildWithDetails buildDetail) {
        if (buildDetail.isBuilding()) {
            return BuildResult.BUILDING.name();
        }
        if (buildDetail.getResult() != null) {
            return buildDetail.getResult().name();
        }
        return "WAITING";
    }


    public  JSONObject getRunById(String jobName, String runId) throws Exception {
        JSONArray runs = getJobRunHis(jobName, null);
        JSONObject temp = null;
        for (int i = 0; i < runs.size(); i++) {
            temp = runs.getJSONObject(i);
            if (runId.equals(temp.getString("runId"))) {
                return temp;
            }
        }
        throw new RuntimeException("获取jenkins运行记录失败，无效的runid:" + runId);
    }

    public  String getRunLogById(String jobName, String runId) {
        try {
            JobWithDetails jobDetails = getJobDetails(jobName);
            List<Build> builds = jobDetails.getBuilds();
            BuildWithDetails buildDetail = null;
            for (Build build : builds) {
                buildDetail = build.details();
                if (buildDetail.getId().equals(runId)) {
                    return buildDetail.getConsoleOutputText();
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException("获取任务运行历史信息错误,请联系管理员!error:" + ex.getMessage());
        }
        throw new RuntimeException("获取jenkins运行日志失败，无效的runid:" + runId);
    }


    public  void disableJob(JSONObject job) throws Exception {

        jenkins.disableJob(job.getString("remoteJobName"));
    }


    public  void deleteJob(JSONObject job) {
        try {

            jenkins.deleteJob(job.getString("remoteJobName"));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException("删除jenkins任务失败，请联系管理员!error:" + ex.getMessage());
        }
    }


    public  void deleteJob(String remoteJobName) throws Exception {
        try {
            jenkins.deleteJob(remoteJobName);
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() != 404) {
                log.error(ex.getMessage(), ex);
                throw new RuntimeException("删除jenkins任务出错,请联系管理员!error:" + ex.getMessage());
            }
        }
    }

    /**
     * @param job
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @description 下载归档文件
     */
    public  InputStream downArtifact(JSONObject job) throws Exception {
        String buildId = job.getString("buildId");
        String jobName = job.getString("remoteJobName");

        //构建下载参数
        Artifact artifact = new Artifact();
        artifact.setDisplayPath(job.getString("displayPath"));
        artifact.setFileName(job.getString("fileName"));
        artifact.setRelativePath(job.getString("relativePath"));

        JobWithDetails jobDetails = getJobDetails(jobName);
        List<Build> lsBuild = jobDetails.getBuilds();
        BuildWithDetails buildDetail = null;
        for (Build build : lsBuild) {
            buildDetail = build.details();
            if (buildDetail.getId().equals(buildId)) {
                //下载构件
                return buildDetail.downloadArtifact(artifact);
            }
        }
        return null;
    }


    public  String queryLog(String pipelinesName, Integer runId, Integer nodeId, String step) {
        try {

            String s = "blue/rest/organizations/jenkins/pipelines/" + pipelinesName + "/runs/" + runId + "/nodes/" + nodeId + "/steps/" + step + "/log/";
            String reJson = clientExtend.getFormReturnContent(s);
            return reJson;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public  String runStep(String pipelinesName, Integer runId) {
        try {
            String s = "/blue/rest/organizations/jenkins/pipelines/" + pipelinesName + "/runs/" + runId + "/";
            String reJson = clientExtend.getFormReturnContent(s);
            return reJson;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public  String nodesList(Integer runId, String pipelinesName, Integer limit) {
        try {
            String s = "/blue/rest/organizations/jenkins/pipelines/" + pipelinesName + "/runs/" + runId + "/nodes/?limit=" + limit;
            String reJson = clientExtend.getFormReturnContent(s);
            return reJson;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public  String taskStatus(Integer runId, String pipelinesName) {
        try {
            String s = "blue/rest/organizations/jenkins/pipelines/" + pipelinesName + "/runs/" + runId + "/";
            String reJson = clientExtend.getFormReturnContent(s);
            return reJson;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public  String restartStep(Integer runId, String pipelinesName, Integer nodeId) {
        try {

            String s = "blue/rest/organizations/jenkins/pipelines/" + pipelinesName + "/runs/" + runId + "/nodes/" + nodeId + "/restart/";
            String reJson = clientExtend.getFormReturnContent(s);
            return reJson;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public  String validateJson(String json) {
        try {
            String s = "/pipeline-model-converter/validateJson";
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("json", json));
            String reJson = clientExtend.postFormReturnContent(s, nvps);
            return reJson;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public  String querySteps(String pipelinesName, Integer runId, Integer nodeId) {
        try {
            String s = "blue/rest/organizations/jenkins/pipelines/" + pipelinesName + "/runs/" + runId + "/nodes/" + nodeId + "/steps/";
            String reJson = clientExtend.getFormReturnContent(s);
            return reJson;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public  String queryBuildLog(String remoteJobName, String runId) {
        try {
            String url = clientExtend.handleUrl(String.format("/job/%s/%s/logText/progressiveText?start=0", remoteJobName, runId));
            log.info("查询构建日志：remoteJobName= {}, runId={}, url={}", remoteJobName, runId, url);
            return clientExtend.getFormReturnContent(url);
        } catch (Exception e) {
            log.error("查询构建日志出现错误： remoteJobName= {}, runId={}", remoteJobName, runId);
            throw new RuntimeException("error.log.query.build");
        }
    }

    public  JSONObject querylogBySteps(String urls) {
        JSONObject jsonObject = new JSONObject();

        String url[] = urls.split(",");
        try {

            String reJson = null;
            List<String> logs = new ArrayList<>();

            for (int i = 0; i < url.length; i++) {
                if (i == (url.length - 1)) {
                    reJson = clientExtend.getFormReturnContent(url[i]);
                    jsonObject = JSONObject.parseObject(reJson);
                }
                String log = clientExtend.getFormReturnContent(url[i] + "/log");
                logs.add(log);

            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < logs.size(); i++) {
                sb.append(logs.get(i)).append("\n\n");
            }
            jsonObject.put("log", sb.toString());
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            jsonObject = JSONObject.parseObject(clientExtend.getFormReturnContent(url[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonObject.put("log", "");
        return jsonObject;
    }


    public  void renameJob(String oldJobName, String newJobName) throws Exception {

        if (jenkins.getJob(oldJobName) != null) {
            jenkins.renameJob(oldJobName, newJobName);
        }
    }

    public  List<JSONObject> getJobRunningJobs() throws Exception {
        List<JSONObject> array = new ArrayList<>();


        //获取在队列中的任务
        Queue queue = jenkins.getQueue();
        queue.getItems().forEach(task -> {
            JSONObject job = new JSONObject();
            String jobName = task.getTask().getName();
            if (StringUtils.isNotBlank(jobName)) {
                job.put(JenkinsConstant.REMOTE_JOB_NAME, jobName);
                try {
                    job.put(JenkinsConstant.JOB_RUN_LASTID, String.valueOf(getJobDetails(jobName).getNextBuildNumber()));
                } catch (Exception e) {
                    log.error("获取jenkins下一次构建Number失败！", e);
                    throw new RuntimeException(e.getMessage());
                }
                job.put(JenkinsConstant.JOB_RUN_RESULT, "WAITING");
                array.add(job);
                log.debug("获取到Jenkins等待执行任务：" + task.getTask().getName());
            }
        });

        String s = "computer/api/json?depth=1";
        String reJson = clientExtend.getFormReturnContent(s);
        JSONObject jsonObject = JSONObject.parseObject(reJson);
        JSONArray computers = jsonObject.getJSONArray("computer");
        for (int i = 0; i < computers.size(); i++) {
            JSONArray runningJobs = computers.getJSONObject(i).getJSONArray("executors");
            for (int j = 0; j < runningJobs.size(); j++) {
                JSONObject runningJob = runningJobs.getJSONObject(j).getJSONObject("currentExecutable");
                if (runningJob != null) {
                    String jobName = getJobNameFromUrl(runningJob.getString("url"));
                    Integer buildId = runningJob.getInteger("number");
                    JSONObject job = new JSONObject();
                    job.put(JenkinsConstant.REMOTE_JOB_NAME, jobName);
                    job.put(JenkinsConstant.JOB_RUN_LASTID, String.valueOf(buildId));
                    job.put(JenkinsConstant.JOB_RUN_RESULT, BuildResult.BUILDING.name());
                    array.add(job);
                    log.debug("获取到Jenkins正在执行任务：" + jobName + "~" + buildId);
                }
            }
        }
        return array;
    }

    public  void reloadJob(String remoteJobName) {
        try {
            String url = clientExtend.handleUrl(String.format("/job/%s/reload", remoteJobName));
            log.info("Reloading {} : {}", remoteJobName, url);
            clientExtend.post(url);
        } catch (Exception e) {
            log.error("Reloading job[{}] has error.", remoteJobName, e);
            throw new RuntimeException("Reloading job[" + remoteJobName + "] has error.");
        }
    }

    private  String getJobNameFromUrl(String url) {
        return url.substring(url.indexOf("/job/") + 5, url.indexOf("/", url.indexOf("/job/") + 5));
    }

    public  void abortJob(String jobName, String runId) throws Exception {
        JSONArray runs = getJobRunHis(jobName, runId);
        if (runs.size() > 0) {
            JSONObject run = runs.getJSONObject(0);

            String s;
            if ("WAITING".equalsIgnoreCase(run.getString("result"))) {
                String queueId = run.getString("queueId");
                log.debug("停止队列中的job：{}/{}", jobName, queueId);
                s = String.format("queue/cancelItem?id=%s", queueId);
            } else {
                log.debug("停止运行的job：{}/{}", jobName, runId);
                s = String.format("job/%s/%s/stop", jobName, runId);
            }

            List<NameValuePair> data = new ArrayList<>();
            clientExtend.postFormReturnContent(s, data);
        }
    }

    public  JSONObject checkTimeTrigger(String value) throws Exception {
      
        //init 为Jenkins默认初始化内置项目，不得删除
        String url = clientExtend.handleUrl(String.format("/job/init/descriptorByName/hudson.triggers.TimerTrigger/checkSpec?value=%s", value));
        String response = clientExtend.get(url);

        log.info("Invoking {}, response ={}", url, response);

        String result = StringUtils.EMPTY, description = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(response)) {
            if (response.contains(JenkinsConstant.TIME_TRIGGER_TIP_OK) && !response.contains(JenkinsConstant.TIME_TRIGGER_TIP_WARNING)) {
                result = JenkinsConstant.TIME_TRIGGER_TIP_OK;
                description = filterCheckingTimeTriggerText(response, JenkinsConstant.TIME_TRIGGER_TIP_OK);
            } else if (response.contains(JenkinsConstant.TIME_TRIGGER_TIP_WARNING)) {
                result = JenkinsConstant.TIME_TRIGGER_TIP_WARNING;
                if (response.contains(JenkinsConstant.TIME_TRIGGER_TIP_OK)) {
                    description = filterCheckingTimeTriggerText(response, JenkinsConstant.TIME_TRIGGER_TIP_WARNING_S1);
                } else {
                    description = filterCheckingTimeTriggerText(response, JenkinsConstant.TIME_TRIGGER_TIP_WARNING_S2);
                }
            } else if (response.contains(JenkinsConstant.TIME_TRIGGER_TIP_ERROR)) {
                result = JenkinsConstant.TIME_TRIGGER_TIP_ERROR;
                description = filterCheckingTimeTriggerText(response, JenkinsConstant.TIME_TRIGGER_TIP_ERROR);
            } else {
                log.error("Invoke url [{}] has error: {}", url, response);
            }
        }

        if (StringUtils.isBlank(result)) {
            throw new RuntimeException("Checking time trigger has error.");
        }

        JSONObject obj = new JSONObject();
        obj.put("result", result);
        obj.put("description", description);

        return obj;
    }

    private static String filterCheckingTimeTriggerText(String response, String type) throws Exception {
        String result = "";

        org.jsoup.nodes.Document snippet = Jsoup.parse(response);
        if (JenkinsConstant.TIME_TRIGGER_TIP_OK.equals(type)) {
            result = snippet.select("div.ok").first().text();
        } else if (JenkinsConstant.TIME_TRIGGER_TIP_WARNING_S1.equals(type)) {
            result = snippet.select("div.warning").first().text();
        } else if (JenkinsConstant.TIME_TRIGGER_TIP_WARNING_S2.equals(type)) {
            Elements elements = snippet.select("div.warning");
            Iterator<org.jsoup.nodes.Element> iterator = elements.iterator();
            while (iterator.hasNext()) {
                result += iterator.next().text() + "   ";
            }
        } else if (JenkinsConstant.TIME_TRIGGER_TIP_ERROR.equals(type)) {
            result = snippet.select("div.error").first().text();
        }

        return result;
    }

    public  String toPipelineJson(String jenkinsFile) throws Exception {

        String url = clientExtend.handleUrl("/pipeline-model-converter/toJson");

        List<NameValuePair> datas = new ArrayList<>();
        datas.add(new BasicNameValuePair(JenkinsConstant.JENKINS_FILE, jenkinsFile));

        String result = clientExtend.postFormReturnContent(url, datas);
        log.info("Accessed url: {}, Parameter[{} = {}], Response: {}", url, JenkinsConstant.JENKINS_FILE, jenkinsFile, (result == null ? "null" : result));

        String returnResult = "";
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonResult = JSONObject.parseObject(result);
            if ("success".equals(jsonResult.getJSONObject("data").getString("result"))) {
                returnResult = jsonResult.getJSONObject("data").getString("json");
            } else {
                throw new RuntimeException("jenkinsfile is invalid.");
            }
        }

        if (StringUtils.isBlank(returnResult)) {
            throw new RuntimeException("Converting jenkinsfile to pipeline has error.");
        }

        return returnResult;
    }

    public  String loadEnvVars() {
        final String JENKINS_ENV_VARS_URL = "env-vars.html";
        try {

            String envVarsStr = clientExtend.getFormReturnContent(JENKINS_ENV_VARS_URL);
            return envVarsStr;
        } catch (Exception e) {
            log.error("load jenkins env vars throw exception: ", e);
        }
        return null;
    }
}
