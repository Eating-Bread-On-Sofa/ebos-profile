package cn.edu.bjtu.ebosprofile.dao;

import cn.edu.bjtu.ebosprofile.entity.ProfileYML;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileYMLRepo extends MongoRepository<ProfileYML,String> {
    List<ProfileYML> findAll();
    ProfileYML findByName(String name);
    void deleteByName(String name);
}
