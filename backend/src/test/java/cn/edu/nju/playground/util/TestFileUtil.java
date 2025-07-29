package cn.edu.nju.playground.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Profile("test")
@Component
@Slf4j
public class TestFileUtil extends FileUtil {
    @Override
    public String upload(long userId, MultipartFile file) {
        // 返回假数据，绕过 OSS
        log.info("TestFileUtil upload called with userId: {}, file: {}", userId, file.getOriginalFilename());
        return "https://test.local/" + UUID.randomUUID().toString().replace("-", "") + "/";
    }

    @Override
    public void delete(String path) {
        // 不执行删除操作，绕过 OSS
        log.info("TestFileUtil delete called with path: {}", path);
    }
}