package cn.edu.bjtu.ebosprofile.service;

import cn.edu.bjtu.ebosprofile.entity.ProfileYML;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface ProfileService {
    ProfileYML getYML(String name);
    void saveYML(ProfileYML profileYML);
    JSONObject stamp2Time(JSONObject jsonObject);
    JSONArray getProfiles(JSONArray output, String ip);
}
