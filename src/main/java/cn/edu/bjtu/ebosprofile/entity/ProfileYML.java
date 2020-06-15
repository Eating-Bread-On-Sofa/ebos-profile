package cn.edu.bjtu.ebosprofile.entity;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

@Document
public class ProfileYML {
    @Id
    private String name;
    private String info;

    public ProfileYML(String info) {
        this.info = info;
        Yaml yaml = new Yaml();
        Map<String,Object> map = (Map<String, Object>) yaml.load(info);
        JSONObject jsonObject = new JSONObject(map);
        this.name = jsonObject.getString("name");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
