package com.xiaxinyu.jenkins.client.core;

import java.util.HashMap;
import java.util.Map;


public class Constants {
    public static final String HTTP_AUTHORIZATION_TOKEN = "token";
    // 项目ID
    @Deprecated
    public static final String PROJECT_ID = "project-id";
    // 用户最大权限ID
    public static final String USER_MAX_PRIVILEGE = "user-max-privilege";
    //用户权限换存key格式
    public static final String USER_PRIVILEGE_CACHE_KEY_FORMAT = "Access#%s#%d";
    // 用户账号
    public static final String SESSION_USER = "session-user";
    // 用户显示名称
    public static final String SESSION_USER_NAME = "session-user-name";
    // 处理成功
    public static final Integer SUCCESS = 20000;
    // 处理失败
    public static final Integer FAILURE = 30000;
    // 无权限
    public static final Integer UNAUTHORIZED = 40000;

    // gitNewAPI
    public static final String GITLAB_NEW_API = "gitlab-new-api";
    public static final String GITLAB_API_CLIENT = "gitlab-api-client";
    // jenkinsAPI
    public static final String JENKINS_API = "jenkins-api";
    // jenkinsAPI client
    public static final String JENKINS_API_CLIENT = "jenkins-api-client";

    // xdiamond API
    public static final String XDIAMOND_API = "xdiamond-api";
    // xdiamond 未登录
    public static final String XDIAMOND_NOT_LOGIN_ERROR = "xdiamond does not login!";
    // xdiamond 重试登录标记
    public static final String XDIAMOND_API_RECONNECT = "xdiamond-api-reconnect-times";
    // xdiamond 重试登录次数
    public static final Integer XDIAMOND_API_RECONNECT_MAX_TIMES = 3;

    // Nexus API
    public static final String NEXUS_API = "nexus-api";// xdiamond 未登录

    public static final String ARTIFACTORY_API = "artifactory-api";

    public static final int ENABLE = 1;

    public static final int DISABLE = 0;

    /**
     * SONAR字段对照
     */
    public final static Map<String, String> CONTRAST_MAP = new HashMap<String, String>() {
        private static final long serialVersionUID = -7975549190522508770L;

        {
            put("reliability_rating", "可靠性等级");
            put("reliability_remediation_effort", "可靠性修复工作");
            put("security_remediation_effort", "安全性修复工作");
            put("new_security_remediation_effort", "安全性修复工作");
            put("sqale_index", "技术债务");
            put("effort_to_reach_maintainability_rating_a", "达到可维护性A级所需要的工作");
            put("duplicated_blocks", "重复块");
            put("duplicated_lines", "重复行");
            put("duplicated_files", "重复文件");
            put("lines", "行");
            put("statements", "语句");
            put("functions", "方法");
            put("classes", "类");
            put("files", "文件");
            put("directories", "目录");
            put("comment_lines_density", "注释%");
            put("comment_lines", "注释行");
            put("function_complexity", "复杂度/方法");
            put("file_complexity", "复杂度/文件");
            put("class_complexity", "复杂度/类");
            put("open_issues", "开启问题");
            put("reopened_issues", "重开问题");
            put("confirmed_issues", "确认问题");
            put("false_positive_issues", "误判问题");
            put("wont_fix_issues", "不修复问题");
            put("duplicated_lines_density", "重复行");
            put("nclocvalue", "代码函数");
            put("sqale_debt_ratiovalue", "技术债务比");
            put("sqale_debt_ratio", "技术债务");
            put("new_sqale_debt_ratio", "技术债务");
            put("major_violations", "主要违规");
            put("new_major_violations", "新主要违规");
            put("info_violations", "未知违规信息");
            put("new_info_violations", "新未知违规信息");
            put("code_smells", "坏味道");
            put("vulnerabilitie", "漏洞");
            put("blocker_violations", "阻断违规");
            put("new_blocker_violations", "新阻断违规");
            put("comment_lines_densityvalue", "注释稠密度");
            put("complexityvalue", "复杂度");
            put("violation", "违规");
            put("sqale_ratingvalue", "可维护性等级");
            put("alert_status", "报警状态");
            put("security_ratingvalue", "安全性等级");
            put("effort_to_reach_maintainability_rating_avalue", "到达最高可维护性级所需的工作");
            put("new_maintainability_rating", "新可维护性级别");
            put("file_complexityvalue", "文件复杂度");
            put("minor_violations", "次要违规");
            put("new_minor_violations", "新次要违规");
            put("critical_violations", "严重违规");
            put("new_critical_violations", "新严重违规");
            put("statements", "表现");
            put("bugs", "bugs");
            put("reliability", "可靠性");
            put("complexity", "复杂性");
            put("violations", "违规");
            put("reliability_rating", "可靠性等级");
            put("new_reliability_rating", "新可靠性等级");
            put("security", "安全性");
            put("security_rating", "安全等级");
            put("new_security_rating", "新安全等级");
            put("sqale", "可维护");
            put("sqale_rating", "可维护等级");
            put("coverage", "覆盖率");
            put("duplicated_lines_density", "重复");
            put("ncloc", "代码量");
            put("vulnerabilities", "漏洞");
            put("new_bugs", "新增Bugs");
            put("new_vulnerabilities", "新增漏洞");
            put("new_code_smells", "新增坏味道");
            put("new_violations", "新违规");
            put("new_reliability_remediation_effort", "修复新缺陷问题成本");
            put("new_technical_debt", "新技术债务");
            put("new_reliability_rating ", "新代码可靠率");
            put("new_reliability_remediation_effort", "新代码的可靠性修复工作");
            put("reliability_remediation_effort", "可靠性修复工作");
            put("security_remediation_effort", "安全修复工作");
            put("new_security_remediation_effort", "新代码的安全修复工作");
            put("new_security_rating", "新代码安全率");
            put("new_technical_debt", "新代码的技术债务");
            put("sqale_debt_ratio", "技术债务比率");
            put("new_sqale_debt_ratio", "新代码技术债务比率");
            put("new_maintainability_rating", "新代码可维护率");

        }
    };
    /**
     * jenkins 编译任务类型
     */
    public static final String JOB_TYPE_BUILD = "build";
    /**
     * jenkins 代码扫描任务类型
     */
    public static final String JOB_TYPE_QUBE = "sonarqube";
    /**
     * jenkins pipeline 任务类型
     */
    public static final String JOB_TYPE_PIPELINE = "pipeline";
}
