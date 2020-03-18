package cn.edu.bjtu.ebosprofile.service;

public interface MqFactory {
    MqProducer createProducer();
    MqConsumer createConsumer(String topic);
}
