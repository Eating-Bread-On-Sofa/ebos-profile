package cn.edu.bjtu.ebosprofile.controller;

import cn.edu.bjtu.ebosprofile.service.ProfileService;
import cn.edu.bjtu.ebosprofile.util.LayuiTableResultUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RequestMapping("/api/profile")
@RestController
public class ProfileController {
    @Autowired
    ProfileService profileService;
    @Autowired
    RestTemplate restTemplate;
    @Value("${server.edgex}")
    private String ip;

    @CrossOrigin
    @GetMapping("/list")
    public LayuiTableResultUtil<JSONArray> getProducts(@RequestParam int page, @RequestParam int limit) {
        String url = "http://"+ip+":48081/api/v1/deviceprofile";
        JSONArray products = new JSONArray(restTemplate.getForObject(url,JSONArray.class));
        JSONArray result = new JSONArray();
        for(int i=0;i<products.size();i++){
            JSONObject jo = products.getJSONObject(i);
            jo = profileService.stamp2Time(jo);
            result.add(jo);
        }
        System.out.println("查看所有设备模板"+result);
        return new LayuiTableResultUtil<>("",result,0,products.size());
    }

    @CrossOrigin
    @GetMapping("/{id}")
    public JSONObject getThisProduct(@PathVariable String id){
        String url = "http://"+ip+":48081/api/v1/deviceprofile/"+id;
        return profileService.stamp2Time(restTemplate.getForObject(url,JSONObject.class));
    }

    @CrossOrigin
    @PostMapping("/yml")
    public String addProduct(@RequestBody String product) {
        System.out.println("收到\n"+product);
        String url = "http://"+ip+":48081/api/v1/deviceprofile/upload";
        String result = restTemplate.postForObject(url,product,String.class);
        return result;
    }

    @CrossOrigin
    @DeleteMapping()
    public void deleteProduct(@RequestBody String id){
        String url = "http://"+ip+":48081/api/v1/deviceprofile/id/"+id;
        System.out.println(url);
        restTemplate.delete(url);
    }

    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
}
