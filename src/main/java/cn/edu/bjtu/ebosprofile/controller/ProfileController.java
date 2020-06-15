package cn.edu.bjtu.ebosprofile.controller;

import cn.edu.bjtu.ebosprofile.entity.ProfileYML;
import cn.edu.bjtu.ebosprofile.service.LogService;
import cn.edu.bjtu.ebosprofile.service.ProfileService;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
        return profileService.saveYML(profileYML);
    }

    @ApiOperation(value = "查看指定模板")
    @CrossOrigin
    @GetMapping("/name/{name}")
    public String getRepoProfile(@PathVariable String name) {
        ProfileYML yml = profileService.getYML(name);
        return yml.getInfo();
    }

    @ApiOperation(value = "删除指定模板")
    @CrossOrigin
    @DeleteMapping("/name/{name}")
    public boolean deleteRepoProfile(@PathVariable String name) {
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
        logService.info(null,"向网关" + ip + "添加了新设备模板：" + name);
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
            logService.info(null,"删除了网关"+ip+"的设备模板："+name);
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

    @ApiOperation(value = "微服务健康检测")
    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
}
