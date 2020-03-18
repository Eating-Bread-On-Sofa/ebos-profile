package cn.edu.bjtu.ebosprofile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class EbosProfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbosProfileApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(5000);
        simpleClientHttpRequestFactory.setReadTimeout(3000);
        RestTemplate restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
        return restTemplate;
    }

}
