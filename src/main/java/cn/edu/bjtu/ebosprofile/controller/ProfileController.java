package cn.edu.bjtu.ebosprofile.controller;

import cn.edu.bjtu.ebosprofile.entity.ProfileYML;
import cn.edu.bjtu.ebosprofile.service.LogService;
import cn.edu.bjtu.ebosprofile.service.ProfileService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

@Api(tags = "设备模板")
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
    @PostMapping("/name/{name}")
    public boolean saveRepoProfile(@PathVariable String name, @RequestBody String ymlStr){
        ProfileYML profileYML = new ProfileYML();
        profileYML.setName(name);
        profileYML.setInfo(ymlStr);
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
    @PostMapping("/gateway/{ip}")
    public String addProduct(@PathVariable String ip,@RequestBody String name) {
        ProfileYML yml = profileService.getYML(name);
        System.out.println("收到\n"+yml.toString());
        String url = "http://" + ip + ":48081/api/v1/deviceprofile/upload";
        String product = yml.getInfo();
        String result = restTemplate.postForObject(url,product,String.class);
        logService.info("向网关" + ip + "添加了新设备模板：" + name);
        return result;
    }

    @CrossOrigin
    @DeleteMapping("/gateway/{ip}")
    public String deleteProduct(@PathVariable String ip,@RequestBody String name) {
        String url = "http://" + ip + ":48081/api/v1/deviceprofile/name/" + name;
        try {
            restTemplate.delete(url);
            logService.info("删除了网关"+ip+"的设备模板："+name);
            return "done";
        } catch (Exception e) {
            return e.toString();
        }
    }

    @CrossOrigin
    @GetMapping("/gateway/{ip}/list")
    public JSONArray getBriedInfo(@PathVariable String ip) {
        JSONArray result = new JSONArray();
        result = profileService.getProfilesName(result,ip);
        return result;
    }


//    @ApiImplicitParam(name = "ipset",value = "所有网关的ip，按英文逗号分隔，无空格",required = true,dataTypeClass = String.class)
//    @CrossOrigin
//    @GetMapping("/ip")
//    public JSONArray getAllProducts(@RequestParam String ipset){
//        JSONArray result = new JSONArray();
//        String[] ips = ipset.split(",");
//        for (String ip : ips) {
//            result = profileService.getProfiles(result,ip);
//        }
//        return result;
//    }
//
//
//    @CrossOrigin
//    @GetMapping("/ip/{ip}/id/{id}")
//    public JSONObject getThisProduct(@PathVariable String ip,@PathVariable String id){
//        String url = "http://"+ip+":48081/api/v1/deviceprofile/"+id;
//        return profileService.stamp2Time(restTemplate.getForObject(url,JSONObject.class));
//    }
//
//    @CrossOrigin
//    @PostMapping("/ip/{ip}/yml")
//    public String addProduct(@PathVariable String ip, @RequestBody String product) {
//        System.out.println("收到\n" + product);
//        Yaml yaml = new Yaml();
//        Map<String, Object> map = (Map<String, Object>) yaml.load(product);
//        ProfileYML profileYML = new ProfileYML();
//        String url = "http://" + ip + ":48081/api/v1/deviceprofile/upload";
//        String result = restTemplate.postForObject(url, product, String.class);
//        logService.info("向网关" + ip + "添加了新设备模板" + product);
//        profileYML.setName(result);
//        profileYML.setInfo(product);
//        profileService.saveYML(profileYML);
//        return result;
//    }
//
//    @CrossOrigin
//    @GetMapping("/yml/{id}")
//    public String getYML(@PathVariable String id){
//        return profileService.getYML(id).getInfo();
//    }

    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
}
