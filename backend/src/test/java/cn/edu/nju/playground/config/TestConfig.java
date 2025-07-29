package cn.edu.nju.playground.config;

import cn.edu.nju.playground.util.FileUtil;
import cn.edu.nju.playground.util.TestFileUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public FileUtil fileUtil() {
        return new TestFileUtil();
    }
}
