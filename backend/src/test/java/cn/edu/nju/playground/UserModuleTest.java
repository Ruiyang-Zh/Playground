package cn.edu.nju.playground;

import cn.edu.nju.playground.model.dto.user.*;
import cn.edu.nju.playground.model.po.User;
import cn.edu.nju.playground.model.po.Wallet;
import cn.edu.nju.playground.repository.UserRepository;
import cn.edu.nju.playground.repository.WalletRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserModuleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static String authToken;
    private static Long userId;
    private static final String TEST_PHONE = "13812345678";
    private static final String TEST_EMAIL = "testuser@example.com";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";

    @Test
    @Order(1)
    @DisplayName("1. 用户注册功能测试")
    void testUserRegistration() throws Exception {
        // 准备注册数据
        MockMultipartFile avatarFile = new MockMultipartFile(
                "avatar",
                "avatar.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // 执行注册请求
        MvcResult result = mockMvc.perform(multipart("/api/auth/register")
                        .file(avatarFile)
                        .param("phone", TEST_PHONE)
                        .param("email", TEST_EMAIL)
                        .param("username", TEST_USERNAME)
                        .param("password", TEST_PASSWORD)
                        .param("sportsPreference", "BASKETBALL,RUNNING")
                        .param("description", "我是一个热爱运动的测试用户"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        // 验证数据库中用户信息
        Optional<User> userOpt = userRepository.findByPhone(TEST_PHONE);
        assertTrue(userOpt.isPresent(), "用户应该已成功创建");

        User user = userOpt.get();
        userId = user.getId();

        assertEquals(TEST_PHONE, user.getPhone());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals(TEST_USERNAME, user.getUsername());
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, user.getPassword()));
        assertNotNull(user.getAvatar(), "头像应该已上传");

        assertEquals("我是一个热爱运动的测试用户", user.getDescription());

        // 验证钱包是否创建
        Optional<Wallet> walletOpt = walletRepository.findByUserId(userId);
        assertTrue(walletOpt.isPresent(), "用户钱包应该已创建");
        assertEquals(new BigDecimal("0.00"), new BigDecimal(String.valueOf(walletOpt.get().getBalance())));

        System.out.println("✅ 用户注册测试通过 - 用户ID: " + userId);
    }

    @Test
    @Order(2)
    @DisplayName("2. 重复注册验证测试")
    void testDuplicateRegistration() throws Exception {
        // 尝试使用相同手机号注册
        mockMvc.perform(multipart("/api/auth/register")
                        .param("phone", TEST_PHONE)
                        .param("username", "anothername")
                        .param("password", "password456"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("手机号已被注册"));

        // 尝试使用相同邮箱注册
        mockMvc.perform(multipart("/api/auth/register")
                        .param("phone", "13987654321")
                        .param("email", TEST_EMAIL)
                        .param("username", "anothername")
                        .param("password", "password456"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("邮箱已被注册"));

        System.out.println("✅ 重复注册验证测试通过");
    }

    @Test
    @Order(3)
    @DisplayName("3. 用户登录功能测试")
    void testUserLogin() throws Exception {
        // 使用手机号登录
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount(TEST_PHONE);
        loginRequest.setPassword(TEST_PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(responseContent).get("data").asText();

        assertNotNull(authToken, "应该返回JWT token");
        assertFalse(authToken.isEmpty(), "JWT token不应为空");

        System.out.println("✅ 手机号登录测试通过 - Token: " + authToken.substring(0, 20) + "...");

        // 使用邮箱登录
        loginRequest.setAccount(TEST_EMAIL);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());

        System.out.println("✅ 邮箱登录测试通过");
    }

    @Test
    @Order(4)
    @DisplayName("4. 登录失败场景测试")
    void testLoginFailure() throws Exception {
        // 错误密码
        UserLoginRequest wrongPasswordRequest = new UserLoginRequest();
        wrongPasswordRequest.setAccount(TEST_PHONE);
        wrongPasswordRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("账号或密码错误"));

        // 不存在的用户
        UserLoginRequest nonExistentUserRequest = new UserLoginRequest();
        nonExistentUserRequest.setAccount("13999999999");
        nonExistentUserRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentUserRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").exists());

        System.out.println("✅ 登录失败场景测试通过");
    }

    @Test
    @Order(5)
    @DisplayName("5. 获取当前用户信息测试")
    void testGetCurrentUserInfo() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/info")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.phone").value(TEST_PHONE))
                .andExpect(jsonPath("$.data.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.data.avatar").exists())
                .andExpect(jsonPath("$.data.description").value("我是一个热爱运动的测试用户"))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andReturn();

        System.out.println("✅ 获取当前用户信息测试通过");
    }

    @Test
    @Order(6)
    @DisplayName("6. 获取用户公开信息测试")
    void testGetUserPublicInfo() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/" + userId + "/info").header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.data.avatar").exists())
                .andExpect(jsonPath("$.data.description").exists())
                // 公开信息不包含手机号和邮箱
                .andExpect(jsonPath("$.data.phone").doesNotExist())
                .andExpect(jsonPath("$.data.email").doesNotExist())
                .andReturn();

        System.out.println("✅ 获取用户公开信息测试通过");
    }

    @Test
    @Order(7)
    @DisplayName("7. 未授权访问测试")
    void testUnauthorizedAccess() throws Exception {
        // 无token访问
        mockMvc.perform(get("/api/users/info"))
                .andExpect(status().isUnauthorized());

        // 错误token访问
        mockMvc.perform(get("/api/users/info")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());

        System.out.println("✅ 未授权访问测试通过");
    }

    @Test
    @Order(8)
    @DisplayName("8. 更新用户信息测试")
    void testUpdateUserInfo() throws Exception {
        MockMultipartFile newAvatarFile = new MockMultipartFile(
                "avatar",
                "new-avatar.jpg",
                "image/jpeg",
                "new test image content".getBytes()
        );

        MvcResult result = mockMvc.perform(multipart("/api/users/info")
                        .file(newAvatarFile)
                        .param("username", "updateduser")
                        .param("phone", "13987654321")
                        .param("email", "updated@example.com")
                        .param("sportsPreference", "SWIMMING,TENNIS")
                        .param("description", "我是更新后的用户信息")
                        .header("Authorization", "Bearer " + authToken)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("updateduser"))
                .andExpect(jsonPath("$.data.phone").value("13987654321"))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"))
                .andExpect(jsonPath("$.data.description").value("我是更新后的用户信息"))
                .andReturn();

        // 验证数据库中的更新
        User updatedUser = userRepository.findById(userId).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals("13987654321", updatedUser.getPhone());
        assertEquals("updated@example.com", updatedUser.getEmail());

        assertEquals("我是更新后的用户信息", updatedUser.getDescription());

        System.out.println("✅ 更新用户信息测试通过");
    }

    @Test
    @Order(9)
    @DisplayName("9. 更新冲突检测测试")
    void testUpdateConflictDetection() throws Exception {
        // 创建另一个用户
        User anotherUser = User.builder()
                .phone("13111111111")
                .email("another@example.com")
                .username("anotheruser")
                .password(passwordEncoder.encode("password123"))
                .build();
        userRepository.save(anotherUser);

        // 尝试更新为已存在的手机号
        mockMvc.perform(multipart("/api/users/info")
                        .param("phone", "13111111111")
                        .header("Authorization", "Bearer " + authToken)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("手机号已被注册"));

        // 尝试更新为已存在的邮箱
        mockMvc.perform(multipart("/api/users/info")
                        .param("email", "another@example.com")
                        .header("Authorization", "Bearer " + authToken)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("邮箱已被注册"));

        System.out.println("✅ 更新冲突检测测试通过");
    }

    @Test
    @Order(10)
    @DisplayName("10. 修改密码测试")
    void testChangePassword() throws Exception {
        PasswordChangeRequest changeRequest = new PasswordChangeRequest();
        changeRequest.setOldPassword(TEST_PASSWORD);
        changeRequest.setNewPassword("newpassword123");

        mockMvc.perform(put("/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeRequest))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证密码已更改
        User user = userRepository.findById(userId).orElse(null);
        assertNotNull(user);
        assertTrue(passwordEncoder.matches("newpassword123", user.getPassword()));
        assertFalse(passwordEncoder.matches(TEST_PASSWORD, user.getPassword()));

        System.out.println("✅ 修改密码测试通过");
    }

    @Test
    @Order(11)
    @DisplayName("12. 使用新密码重新登录测试")
    void testLoginWithNewPassword() throws Exception {
        // 使用新密码登录
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount("13987654321"); // 使用更新后的手机号
        loginRequest.setPassword("newpassword123"); // 使用新密码

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andReturn();

        String newToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("data").asText();

        assertNotNull(newToken);
        assertNotEquals(authToken, newToken); // 新token应该与旧token不同

        authToken = newToken; // 更新authToken为新token

        // 使用新token获取用户信息
        MvcResult userInfoResult = mockMvc.perform(get("/api/users/info")
                        .header("Authorization", "Bearer " + newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("updateduser"))
                .andReturn();

        // 旧密码不能登录
        loginRequest.setPassword(TEST_PASSWORD);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("账号或密码错误"));

        System.out.println("✅ 使用新密码重新登录测试通过");
    }

    @Test
    @Order(12)
    @DisplayName("11. 修改密码失败场景测试")
    void testChangePasswordFailure() throws Exception {
        // 错误的原密码
        PasswordChangeRequest wrongOldPasswordRequest = new PasswordChangeRequest();
        wrongOldPasswordRequest.setOldPassword("wrongoldpassword");
        wrongOldPasswordRequest.setNewPassword("anothernewpassword");

        mockMvc.perform(put("/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongOldPasswordRequest))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("原密码错误"));

        System.out.println("✅ 修改密码失败场景测试通过");
    }

    @Test
    @Order(13)
    @DisplayName("13. 数据完整性验证测试")
    void testDataIntegrityValidation() throws Exception {
        // 验证最终用户数据状态
        User finalUser = userRepository.findById(userId).orElse(null);
        assertNotNull(finalUser, "用户应该存在");

        assertEquals("updateduser", finalUser.getUsername());
        assertEquals("13987654321", finalUser.getPhone());
        assertEquals("updated@example.com", finalUser.getEmail());
        assertTrue(passwordEncoder.matches("newpassword123", finalUser.getPassword()));
        assertNotNull(finalUser.getAvatar());

        assertEquals("我是更新后的用户信息", finalUser.getDescription());

        // 验证钱包仍然存在
        Optional<Wallet> walletOpt = walletRepository.findByUserId(userId);
        assertTrue(walletOpt.isPresent(), "用户钱包应该仍然存在");

        System.out.println("✅ 数据完整性验证测试通过");
    }

    @Test
    @Order(14)
    @DisplayName("14. 参数验证测试")
    void testParameterValidation() throws Exception {
        // 注册时参数验证
        mockMvc.perform(multipart("/api/auth/register")
                        .param("phone", "invalid-phone")
                        .param("username", "")
                        .param("password", "123")) // 密码太短
                .andExpect(status().isBadRequest());

        // 登录时参数验证
        UserLoginRequest emptyLoginRequest = new UserLoginRequest();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyLoginRequest)))
                .andExpect(status().isBadRequest());

        // 修改密码时参数验证
        PasswordChangeRequest invalidChangeRequest = new PasswordChangeRequest();
        invalidChangeRequest.setOldPassword("");
        invalidChangeRequest.setNewPassword("123"); // 密码太短

        mockMvc.perform(put("/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidChangeRequest))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().is4xxClientError());

        System.out.println("✅ 参数验证测试通过");
    }

    @AfterAll
    static void printTestSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎉 用户模块功能测试全部完成！");
        System.out.println("📊 测试覆盖的完整流程：");
        System.out.println("   1. ✅ 用户注册（包含头像上传）");
        System.out.println("   2. ✅ 重复注册验证");
        System.out.println("   3. ✅ 用户登录（手机号/邮箱）");
        System.out.println("   4. ✅ 登录失败场景");
        System.out.println("   5. ✅ 获取用户信息");
        System.out.println("   6. ✅ 获取公开信息");
        System.out.println("   7. ✅ 权限验证");
        System.out.println("   8. ✅ 更新用户信息");
        System.out.println("   9. ✅ 更新冲突检测");
        System.out.println("  10. ✅ 修改密码");
        System.out.println("  11. ✅ 修改密码失败场景");
        System.out.println("  12. ✅ 新密码登录验证");
        System.out.println("  13. ✅ 数据完整性验证");
        System.out.println("  14. ✅ 参数验证");
        System.out.println("=".repeat(60));
    }
}