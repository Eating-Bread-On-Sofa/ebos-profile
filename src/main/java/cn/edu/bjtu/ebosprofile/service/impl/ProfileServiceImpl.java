package cn.edu.bjtu.ebosprofile.service.impl;

import cn.edu.bjtu.ebosprofile.service.ProfileService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class ProfileServiceImpl implements ProfileService {

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
}
