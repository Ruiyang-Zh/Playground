package cn.edu.nju.playground.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Component
public class JsonUtil {

    @Autowired
    private ObjectMapper objectMapper;

    public void assertJsonEquals(String expected, String actual) {
        try {
            String processedActual = actual;

            // 处理多层转义：持续反转义直到不再是转义的字符串
            while (processedActual.startsWith("\"") && processedActual.endsWith("\"")) {
                try {
                    String temp = objectMapper.readValue(processedActual, String.class);
                    if (temp.equals(processedActual)) {
                        // 如果反转义后没有变化，说明不是转义字符串，跳出循环
                        break;
                    }
                    processedActual = temp;
                } catch (Exception e) {
                    // 反转义失败，跳出循环
                    break;
                }
            }

            JsonNode expectedNode = objectMapper.readTree(expected);
            JsonNode actualNode = objectMapper.readTree(processedActual);
            assertEquals(expectedNode, actualNode);
        } catch (Exception e) {
            fail("JSON比较失败: " + e.getMessage() + "\n期望: " + expected + "\n实际: " + actual);
        }
    }

    /**
     * 更强大的字符串或JSON比较方法
     */
    public void assertStringOrJsonEquals(String expected, String actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null || actual == null) {
            assertEquals(expected, actual);
            return;
        }

        // 处理实际值的转义
        String cleanActual = actual;
        if (actual.startsWith("\"") && actual.endsWith("\"")) {
            try {
                cleanActual = objectMapper.readValue(actual, String.class);
            } catch (Exception e) {
                // 转义失败，保持原值
            }
        }

        // 判断是否是JSON格式
        boolean expectedIsJson = (expected.trim().startsWith("{") && expected.trim().endsWith("}")) ||
                (expected.trim().startsWith("[") && expected.trim().endsWith("]"));
        boolean actualIsJson = (cleanActual.trim().startsWith("{") && cleanActual.trim().endsWith("}")) ||
                (cleanActual.trim().startsWith("[") && cleanActual.trim().endsWith("]"));

        if (expectedIsJson && actualIsJson) {
            // 都是JSON，使用JSON比较
            try {
                JsonNode expectedNode = objectMapper.readTree(expected);
                JsonNode actualNode = objectMapper.readTree(cleanActual);
                assertEquals(expectedNode, actualNode);
            } catch (Exception e) {
                // JSON解析失败，按字符串比较
                assertEquals(expected, cleanActual);
            }
        } else {
            // 按字符串比较
            assertEquals(expected, cleanActual);
        }
    }
}
