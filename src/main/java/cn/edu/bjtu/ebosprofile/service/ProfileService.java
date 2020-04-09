package cn.edu.bjtu.ebosprofile.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface ProfileService {
    JSONObject stamp2Time(JSONObject jsonObject);
    JSONArray getProfiles(JSONArray output, String ip);
}
