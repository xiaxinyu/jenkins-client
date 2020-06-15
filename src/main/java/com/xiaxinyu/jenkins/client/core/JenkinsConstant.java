package com.xiaxinyu.jenkins.client.core;

/**
 * @author XIAXINYU3
 * @date 2019.3.21
 * @description JenkinsConstant
 */
public class JenkinsConstant {
    public static final String GLOBAL_PARAMETER_CI_HOST = "${Default_Devops_CI_Host}";

    public static final String REQUIRE_APPLICATION_ID = "applicationId";

    /* Request parameter name */
    public static final String REQ_PARAM_APPLICATION_ID = "projectId";
    public static final String REQ_PARAM_JOB_ID = "jobId";
    public static final String REQ_PARAM_GROUP_ID = "groupId";
    public static final String REQ_PARAM_STEAM_PROJECT_ID = "steamProjectId";
    public static final String REQ_PARAM_TOKEN = "token";

    /**
     * Jenkins 凭证，用于Gitlab交互
     */
    public static final String CREDENTIAL_ID = "credentialId";

    public static final String JOB_TYPE_BUILD = "build";
    public static final String JOB_TYPE_PIPELINE = "pipeline";
    public static final String JOB_TYPE_QUALITYCHECK = "qualityCheck";
    public static final String JOB_TYPE_QUALITYCHECK_DB = "sonarqube";

    public static final String JOB_NAME = "jobName";
    public static final String REMOTE_JOB_NAME = "remoteName";
    public static final String REMOTE_JOB_FULL_NAME = "remoteJobName";
    public static final String NEW_REMOTE_JOB_FULL_NAME = "newRemoteJobName";

    //job的git仓库
    public static final String JOB_GIT_ADDRESS = "gitAddress";
    //job的git分支
    public static final String JOB_GIT_BRANCH = "branch";

    //构建编译命令字段
    public static final String BUILD_COMMAND = "buildCommand";
    //sonar项目名称
    public static final String SONAR_PROJECT_NAME = "projectName";
    //sonar项目key
    public static final String SONAR_PROJECT_KEY = "projectKey";
    //sonar全部扫描参数
    public static final String SONAR_SCAN_PARAMETERS = "scanParameter";
    //sonar参数表单
    public static final String SONAR_SCAN_PARAMETERS_FORM = "form";
    //sonar规则集
    public static final String SONAR_PROFILE = "sonarProfile";
    //sonar源码位置
    public static final String SONAR_SOURCES = "sources";
    //sonar排除扫描项
    public static final String SONAR_EXCLUDE = "exclude";

    //JOB任务最后一次运行id
    public static final String JOB_RUN_LASTID = "lastRunId";
    //JOB任务运行状态
    public static final String JOB_RUN_RESULT = "result";
    //JOB任务运行状态
    public static final String JOB_RUN_DURATION = "duration";


    /* Email Ext Plugin */
    public static final String PUBLISHERS = "publishers";

    public static final String EMAIL_EXT = "email_ext";

    public static final String EMAIL = "email";

    public static final String EMAIL_EXT_TRIGGER = "configuredTriggers";

    public static final String EMAIL_EXT_TRIGGER_TYPE = "type";

    public static final String EMAIL_EXT_TRIGGER_TYPE_ALWAYS = "always";

    public static final String EMAIL_EXT_TRIGGER_TYPE_FAILURE = "failure";

    public static final String EMAIL_EXT_TRIGGER_TYPE_SUCCESS = "success";

    public static final String EMAIL_EXT_TPL = "email-ext.xml";

    public static final String EMAIL_EXT_TRIGGER_TPL_ALWAYS = "email-ext-always-trigger.xml";

    public static final String EMAIL_EXT_TRIGGER_TPL_FAILURE = "email-ext-failure-trigger.xml";

    public static final String EMAIL_EXT_TRIGGER_TPL_SUCCESS = "email-ext-success-trigger.xml";

    public static final String EMAIL_EXT_TRIGGER_SUBJECT = "subject";

    public static final String EMAIL_EXT_TRIGGER_BODY = "body";

    public static final String EMAIL_EXT_TRIGGER_RECIPIENTLIST = "recipientList";

    public static final String EMAIL_EXT_LOG_URI_PATTERN = "/#/%d/build/codeci/info/%d";

    /* Time Trigger */
    public static final String TIME_TRIGGER_TIP_OK = "ok";

    public static final String TIME_TRIGGER_TIP_ERROR = "error";

    public static final String TIME_TRIGGER_TIP_WARNING = "warning";

    public static final String TIME_TRIGGER_TIP_WARNING_S1 = "warning_s1";

    public static final String TIME_TRIGGER_TIP_WARNING_S2 = "warning_s2";

    /* Jenkins File */
    public static final String JENKINS_FILE = "jenkinsfile";

    public static final String PIPELINE_JSON = "json";

    public static final String PIPELINE = "pipeline";

    public static final String PIPELINE_STAGES = "stages";

    public static final String PIPELINE_STAGES_BRANCHES = "branches";

    public static final String PIPELINE_STAGES_PARALLEL = "parallel";

    public static final String PIPELINE_STAGES_STEPS = "steps";

    public static final String PIPELINE_STAGES_NAME = "name";

    public static final String PIPELINE_STAGES_STEPS_ARGUMENTS = "arguments";

    public static final String PIPELINE_STAGES_STEPS_ARGUMENTS_KEY = "key";

    public static final String PIPELINE_STAGES_STEPS_ARGUMENTS_JOB = "job";

    public static final String PIPELINE_STAGES_STEPS_ARGUMENTS_VALUE = "value";

    /**
     * 后构建属性
     */
    public static final String AFTER_BUILD = "afterBuild";

    /**
     * tomcat 构建后动作
     */
    public static final String AFTER_BUILD_TOMCAT = "tomcat";

    /**
     * xshell 构建后动作
     */
    public static final String AFTER_BUILD_XSHELL = "xshell";

    /**
     * docker 构建后动作
     */
    public static final String AFTER_BUILD_DOCKER = "docker";

    /**
     * helm 构建后动作
     */
    public static final String AFTER_BUILD_HELM = "helm";

    /**
     * email ext 构建后动作
     */
    public static final String AFTER_BUILD_EMAIL_EXT = "email_ext";

    /**
     * kubernates 构建后动作
     */
    public static final String AFTER_BUILD_KUBERNATES = "kubernates";

    /**
     * 合并请求 构建后动作
     */
    public static final String AFTER_BUILD_MERGE_REQUEST = "mergeRequest";

    /**
     * ssh 构建后动作
     */
    public static final String AFTER_BUILD_SSH = "ssh";

    /**
     * 包上传构建后动作
     */
    public static final String AFTER_BUILD_PACKAGE_UPLOAD = "packageUpload";

    /**
     * 部署构建后动作
     */
    public static final String AFTER_BUILD_DEPLOYMENT = "deployment";

    /**
     * 后构建类型属性
     */
    public static final String AFTER_BUILD_TYPE = "type";

    /**
     * 合并请求分支默认值
     */
    public static final String MR_SOURCH_BRANCH_DEFAULT_VALUE = "${gitlabSourceBranch}";

    /**
     * 定时任务名称默认值
     */
    public static final String MR_CRON_TABLE_DEFAULT_VALUE = "H/5 * * * *";
    /**
     * 执行计划
     */
    public static final String EXEC_PLAN = "execPlan";
    /**
     * 执行计划默认方法
     */
    public static final String EXEC_PLAN_METHOD_DEFAULT = "input";
    /**
     * 执行计划类型默认值
     */
    public static final Integer EXEC_PLAN_TYPE_DEFAULT = 1;
    /**
     * 任务参数
     */
    public static final String PARAMETER = "parameter";
}
