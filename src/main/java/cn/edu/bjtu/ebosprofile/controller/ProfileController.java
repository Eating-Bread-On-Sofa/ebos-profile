package cn.edu.bjtu.ebosprofile.controller;

import cn.edu.bjtu.ebosprofile.service.ProfileService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RequestMapping("/api/profile")
@RestController
public class ProfileController {
    @Autowired
    ProfileService profileService;
    @Autowired
    RestTemplate restTemplate;

    @CrossOrigin
    @GetMapping("/ip/{ip}")
    public JSONArray getProducts(@PathVariable String ip) {
        String url = "http://"+ip+":48081/api/v1/deviceprofile";
        JSONArray products = new JSONArray(restTemplate.getForObject(url,JSONArray.class));
        JSONArray result = new JSONArray();
        for(int i=0;i<products.size();i++){
            JSONObject jo = products.getJSONObject(i);
            jo = profileService.stamp2Time(jo);
            jo.put("gateway-ip",ip);
            result.add(jo);
        }
        System.out.println("查看所有设备模板"+result);
        return result;
    }

    @CrossOrigin
    @GetMapping("/ip/{ip}/id/{id}")
    public JSONObject getThisProduct(@PathVariable String ip,@PathVariable String id){
        String url = "http://"+ip+":48081/api/v1/deviceprofile/"+id;
        return profileService.stamp2Time(restTemplate.getForObject(url,JSONObject.class));
    }

    @CrossOrigin
    @PostMapping("/ip/{ip}")
    public String addProduct(@PathVariable String ip,@RequestBody JSONObject product) {
        System.out.println("收到\n"+product.toString());
        String url = "http://"+ip+":48081/api/v1/deviceprofile";
        String result = restTemplate.postForObject(url,product,String.class);
        return result;
    }

    @CrossOrigin
    @PostMapping("/ip/{ip}/yml")
    public String addProduct(@PathVariable String ip, @RequestBody String product) {
        System.out.println("收到\n"+product);
        String url = "http://"+ip+":48081/api/v1/deviceprofile/upload";
        String result = restTemplate.postForObject(url,product,String.class);
        return result;
    }

    @CrossOrigin
    @DeleteMapping("/ip/{ip}/id/{id}")
    public String deleteProduct(@PathVariable String ip, @PathVariable String id) {
        String url = "http://" + ip + ":48081/api/v1/deviceprofile/id/" + id;
        try {
            restTemplate.delete(url);
            return "done";
        } catch (Exception e) {
            return e.toString();
        }
    }

    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
}
