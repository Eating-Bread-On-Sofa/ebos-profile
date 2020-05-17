package cn.edu.bjtu.ebosprofile.service.impl;

import cn.edu.bjtu.ebosprofile.dao.ProfileYMLRepo;
import cn.edu.bjtu.ebosprofile.entity.ProfileYML;
import cn.edu.bjtu.ebosprofile.service.ProfileService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ProfileYMLRepo profileYMLRepo;

    @Override
    public ProfileYML getYML(String name){
        return profileYMLRepo.findByName(name);
    }

    @Override
    public boolean saveYML(ProfileYML profileYML){
        ProfileYML profileYML1 = profileYMLRepo.findByName(profileYML.getName());
        if (profileYML1 != null) {
            return false;
        } else {
            profileYMLRepo.save(profileYML);
            return true;
        }
    }

    @Override
    public boolean deleteYML(String name) {
        ProfileYML profileYML = profileYMLRepo.findByName(name);
        if (profileYML == null) {
            return false;
        } else {
            profileYMLRepo.deleteByName(name);
            return true;
        }
    }

    @Override
    public JSONObject stamp2Time(JSONObject jsonObject){
        String createdStamp = jsonObject.getString("created");
        String modifiedStamp = jsonObject.getString("modified");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long created = Long.parseLong(createdStamp);
        long modified = Long.parseLong(modifiedStamp);
        Date createdTime = new Date(created);
        Date modifiedTime = new Date(modified);
        jsonObject.remove("created");
        jsonObject.remove("modified");
        jsonObject.put("created",simpleDateFormat.format(createdTime));
        jsonObject.put("modified",simpleDateFormat.format(modifiedTime));
        return jsonObject;
    }

    @Override
    public JSONArray getProfiles(JSONArray output, String ip){
        String url = "http://"+ip+":48081/api/v1/deviceprofile";
        JSONArray profiles = new JSONArray(restTemplate.getForObject(url,JSONArray.class));
        for(int i=0;i<profiles.size();i++){
            JSONObject jo = profiles.getJSONObject(i);
            jo = stamp2Time(jo);
            jo.put("ip",ip);
            output.add(jo);
        }
        return output;
    }

    @Override
    public JSONArray getProfilesName(JSONArray output, String ip) {
        String url = "http://"+ip+":48081/api/v1/deviceprofile";
        JSONArray profiles = new JSONArray(restTemplate.getForObject(url,JSONArray.class));
        for(int i=0;i<profiles.size();i++){
            JSONObject jo = profiles.getJSONObject(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", jo.getString("name"));
            output.add(jsonObject);
        }
        return output;
    }

    @Override
    public JSONArray getRepoProfiles() {
        List<ProfileYML> profileYMLRepoAll = profileYMLRepo.findAll();
        JSONArray result = new JSONArray();
        for (ProfileYML profileYML : profileYMLRepoAll) {
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(profileYML);
            result.add(jsonObject);
        }
        return result;
    }
}
