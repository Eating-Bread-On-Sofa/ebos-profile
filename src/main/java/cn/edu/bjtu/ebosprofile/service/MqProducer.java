package cn.edu.bjtu.ebosprofile.service;

public interface MqProducer {
    void publish(String topic, String message);
}
