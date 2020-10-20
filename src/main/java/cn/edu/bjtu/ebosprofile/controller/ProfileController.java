package cn.edu.bjtu.ebosprofile.controller;

import cn.edu.bjtu.ebosprofile.entity.ProfileYML;
import cn.edu.bjtu.ebosprofile.service.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Api(tags = "模板管理")
@RequestMapping("/api/profile")
@RestController
public class ProfileController {
    @Autowired
    ProfileService profileService;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    LogService logService;
    @Autowired
    MqFactory mqFactory;
    @Autowired
    SubscribeService subscribeService;

    public static final List<RawSubscribe> status = new LinkedList<>();
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 50,3, TimeUnit.SECONDS,new SynchronousQueue<>());

    @ApiOperation(value = "查看模板库中模板")
    @CrossOrigin
    @GetMapping()
    public JSONArray getRepoProfiles(){
        return profileService.getRepoProfiles();
    }

    @ApiOperation(value = "向库中添加模板")
    @CrossOrigin
    @PostMapping()
    public boolean saveRepoProfile( @RequestBody String ymlStr){
        ProfileYML profileYML = new ProfileYML(ymlStr);
        logService.info("create","向库中添加模板"+ymlStr+"成功！");
        return profileService.saveYML(profileYML);
    }

    @ApiOperation(value = "查看指定模板（目前先不用了，用下面那种调用方式）")
    @CrossOrigin
    @GetMapping("/name/yml/{name}")
    public String getRepoProfile(@PathVariable String name) {
        ProfileYML yml = profileService.getYML(name);
        return yml.getInfo();
    }

    @ApiOperation(value = "查看指定模板")
    @CrossOrigin
    @GetMapping("/name/json/{name}")
    public JSONObject getJsonProfile(@PathVariable String name) {
        return profileService.getJson(name);
    }

    @ApiOperation(value = "删除指定模板")
    @CrossOrigin
    @DeleteMapping("/name/{name}")
    public boolean deleteRepoProfile(@PathVariable String name) {
        logService.info("delete","删除模板"+name+"成功！");
        return profileService.deleteYML(name);
    }

    @ApiOperation(value = "查看指定网关下的模板")
    @CrossOrigin
    @GetMapping("/gateway/{ip}")
    public JSONArray getProducts(@PathVariable String ip) {
        JSONArray result = new JSONArray();
        result = profileService.getProfiles(result,ip);
        System.out.println("查看网关"+ip+"设备模板"+result);
        return result;
    }

    @ApiOperation(value = "向指定网关下发模板")
    @CrossOrigin
    @PostMapping("/gateway/{ip}/{name}")
    public String addProduct(@PathVariable String ip,@PathVariable String name) {
        ProfileYML yml = profileService.getYML(name);
        if(yml != null){
        System.out.println(yml.getInfo());
        String url = "http://" + ip + ":48081/api/v1/deviceprofile/upload";
        String product = yml.getInfo();
        String result = restTemplate.postForObject(url,product,String.class);
        logService.info("create","向网关" + ip + "添加了新设备模板：" + name);
        return result;
        }else {
            return "模板库中无此模板";
        }
    }

    @ApiOperation(value = "删除指定网关下的模板")
    @CrossOrigin
    @DeleteMapping("/gateway/{ip}/{name}")
    public String deleteProduct(@PathVariable String ip,@PathVariable String name) {
        String url = "http://" + ip + ":48081/api/v1/deviceprofile/name/" + name;
        try {
            restTemplate.delete(url);
            logService.info("delete","删除了网关"+ip+"的设备模板："+name);
            return "done";
        } catch (Exception e) {
            return e.toString();
        }
    }

    @ApiOperation(value = "指定网关已有的模板列表")
    @CrossOrigin
    @GetMapping("/gateway/{ip}/list")
    public JSONArray getBriefInfo(@PathVariable String ip) {
        JSONArray result = new JSONArray();
        result = profileService.getProfilesName(result,ip);
        return result;
    }

    @ApiOperation(value = "微服务订阅mq的主题")
    @CrossOrigin
    @PostMapping("/subscribe")
    public String newSubscribe(RawSubscribe rawSubscribe){
        if(!ProfileController.check(rawSubscribe.getSubTopic())){
            try{
                status.add(rawSubscribe);
                subscribeService.save(rawSubscribe.getSubTopic());
                threadPoolExecutor.execute(rawSubscribe);
                logService.info("create","模板管理成功订阅主题"+ rawSubscribe.getSubTopic());
                return "订阅成功";
            }catch (Exception e) {
                e.printStackTrace();
                logService.error("create","模板管理订阅主题"+rawSubscribe.getSubTopic()+"时，参数设定有误。");
                return "参数错误!";
            }
        }else {
            logService.error("create","模板管理已订阅主题"+rawSubscribe.getSubTopic()+",再次订阅失败");
            return "订阅主题重复";
        }
    }

    public static boolean check(String subTopic){
        boolean flag = false;
        for (RawSubscribe rawSubscribe : status) {
            if(subTopic.equals(rawSubscribe.getSubTopic())){
                flag=true;
                break;
            }
        }
        return flag;
    }

    @ApiOperation(value = "删除微服务订阅mq的主题")
    @CrossOrigin
    @DeleteMapping("/subscribe/{subTopic}")
    public boolean delete(@PathVariable String subTopic){
        boolean flag;
        synchronized (status){
            flag = status.remove(search(subTopic));
        }
        return flag;
    }

    public static RawSubscribe search(String subTopic){
        for (RawSubscribe rawSubscribe : status) {
            if(subTopic.equals(rawSubscribe.getSubTopic())){
                return rawSubscribe;
            }
        }
        return null;
    }

    @ApiOperation(value = "微服务向mq的某主题发布消息")
    @CrossOrigin
    @PostMapping("/publish")
    public String publish(@RequestParam(value = "topic") String topic,@RequestParam(value = "message") String message){
        MqProducer mqProducer = mqFactory.createProducer();
        mqProducer.publish(topic,message);
        return "发布成功";
    }

    @ApiOperation(value = "微服务健康检测")
    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
}
