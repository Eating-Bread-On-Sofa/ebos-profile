package cn.edu.bjtu.ebosprofile.service;

import cn.edu.bjtu.ebosprofile.entity.ProfileYML;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface ProfileService {
    ProfileYML getYML(String name);
    JSONObject getJson(String name);
    boolean saveYML(ProfileYML profileYML);
    boolean deleteYML(String name);
    JSONObject stamp2Time(JSONObject jsonObject);
    JSONArray getProfiles(JSONArray output, String ip);
    JSONArray getProfilesName(JSONArray output, String ip);
    JSONArray getRepoProfiles();
}
