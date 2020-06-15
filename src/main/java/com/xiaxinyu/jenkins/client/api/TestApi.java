package com.xiaxinyu.jenkins.client.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Artifact;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @ClassName: TestApi
 * @Description: dd
 * @author: dushaohua5
 * @date: 2018年9月17日 下午7:21:32
 */
public class TestApi {

    public static JSONArray retriveFiles(String html) {

//        org.jsoup.nodes.Document doc = Jsoup.parse(html);
//        Elements els = doc.select(".fileList tbody tr");
//        JSONArray files = new JSONArray();
//        JSONObject file = null;
//        Element imgEl = null;
//        String type = null;
//        String name = null;
//        for (Element el : els) {
//            imgEl = el.selectFirst("img");
//            if (imgEl.attr("src").endsWith("package.png")) {
//                continue;
//            }
//            if (imgEl.attr("src").endsWith("folder.png")) {
//                type = "folder";
//            } else {
//                type = "text";
//            }
//
//            name = el.child(1).select("a").text();
//            file = new JSONObject();
//            file.put("fileName", name);
//            file.put("fileType", type);
//            files.add(file);
//        }
//        return files;
        return null;
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) throws URISyntaxException, IOException {
//        JenkinsServer jenkins = new JenkinsServer(new URI("http://10.0.49.90/jenkins"), "root",
//                "115268ac0aa429fcf601010b37c4f6a62d");
//
//        JenkinsHttpClientExtend client = new JenkinsHttpClientExtend(
//                new URI("http://10.0.49.90/jenkins"), "root",
//                "115268ac0aa429fcf601010b37c4f6a62d");

        JenkinsServer jenkins = new JenkinsServer(new URI("http://jenkins.devops.crc.com.cn"), "admin",
                "11d57b10802d214cfac83f2ad320dc50cc");
        JobWithDetails detail = jenkins.getJob("crcsoft-devcloud_devops-ci_testpipe");
        List<Build> builds = detail.getBuilds();
        BuildWithDetails buildDetail = null;
        for (Build build : builds) {
            buildDetail = build.details();

            //组装构件信息
            JSONArray artifacts = new JSONArray();
            JSONObject temp = null;
            for (Artifact artifact : buildDetail.getArtifacts()) {
                temp = new JSONObject();
                temp.put("displayPath", artifact.getDisplayPath());
                temp.put("fileName", artifact.getFileName());
                temp.put("relativePath", artifact.getRelativePath());
                artifacts.add(temp);
                System.out.println(JSON.toJSONString(temp));
            }

        }

//      List<NameValuePair> data = new ArrayList<>();
//       String ret1 = client.postFormReturnContent("job/devcloud-devops-ci_devops-test1_test1/23/stop",data);
//        JobWithDetails ret =  jenkins.getJob("devcloud-devops-ci_devops-test1_test1");
//        List<Build> builds = ret.getBuilds();
//        System.out.println("inqueue: "+ret.isInQueue());
//        builds.forEach(b->{
//            try {
//                System.out.println(b.getNumber()+"~"+b.details().getNumber()+"~"+b.details().getResult()+"~"+b.details().isBuilding());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });

        //System.out.println(ret1);

    }


}
