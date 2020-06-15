package cn.edu.bjtu.ebosprofile.controller;

import cn.edu.bjtu.ebosprofile.entity.ProfileYML;
import cn.edu.bjtu.ebosprofile.service.LogService;
import cn.edu.bjtu.ebosprofile.service.ProfileService;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


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

    @CrossOrigin
    @GetMapping()
    public JSONArray getRepoProfiles(){
        return profileService.getRepoProfiles();
    }

    @CrossOrigin
    @PostMapping()
    public boolean saveRepoProfile( @RequestBody String ymlStr){
        ProfileYML profileYML = new ProfileYML(ymlStr);
        return profileService.saveYML(profileYML);
    }

    @CrossOrigin
    @GetMapping("/name/{name}")
    public String getRepoProfile(@PathVariable String name) {
        ProfileYML yml = profileService.getYML(name);
        return yml.getInfo();
    }

    @CrossOrigin
    @DeleteMapping("/name/{name}")
    public boolean deleteRepoProfile(@PathVariable String name) {
        return profileService.deleteYML(name);
    }

    @CrossOrigin
    @GetMapping("/gateway/{ip}")
    public JSONArray getProducts(@PathVariable String ip) {
        JSONArray result = new JSONArray();
        result = profileService.getProfiles(result,ip);
        System.out.println("查看网关"+ip+"设备模板"+result);
        return result;
    }

    @CrossOrigin
    @PostMapping("/gateway/{ip}/{name}")
    public String addProduct(@PathVariable String ip,@PathVariable String name) {
        ProfileYML yml = profileService.getYML(name);
        System.out.println("收到\n"+yml.toString());
        String url = "http://" + ip + ":48081/api/v1/deviceprofile/upload";
        String product = yml.getInfo();
        String result = restTemplate.postForObject(url,product,String.class);
        logService.info(null,"向网关" + ip + "添加了新设备模板：" + name);
        return result;
    }

    @CrossOrigin
    @DeleteMapping("/gateway/{ip}/{name}")
    public String deleteProduct(@PathVariable String ip,@PathVariable String name) {
        String url = "http://" + ip + ":48081/api/v1/deviceprofile/name/" + name;
        try {
            restTemplate.delete(url);
            logService.info(null,"删除了网关"+ip+"的设备模板："+name);
            return "done";
        } catch (Exception e) {
            return e.toString();
        }
    }

    @CrossOrigin
    @GetMapping("/gateway/{ip}/list")
    public JSONArray getBriefInfo(@PathVariable String ip) {
        JSONArray result = new JSONArray();
        result = profileService.getProfilesName(result,ip);
        return result;
    }

    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
}
