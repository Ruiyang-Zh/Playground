package cn.edu.nju.playground.service;

import cn.edu.nju.playground.exception.PlaygroundException;
import cn.edu.nju.playground.model.dto.user.*;
import cn.edu.nju.playground.model.po.User;
import cn.edu.nju.playground.model.po.Wallet;
import cn.edu.nju.playground.repository.UserRepository;
import cn.edu.nju.playground.repository.WalletRepository;
import cn.edu.nju.playground.util.FileUtil;
import cn.edu.nju.playground.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TokenUtil tokenUtil;
    private final FileUtil fileUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    @Transactional
    public void register(UserRegisterRequest request) {
        // 检查手机号是否已存在
        if (userRepository.existsByPhone(request.getPhone())) {
            throw PlaygroundException.userAlreadyExists("手机号已被注册");
        }

        String email = (request.getEmail() != null && request.getEmail().isEmpty()) ? null : request.getEmail();

        // 检查邮箱是否已存在
        if (email != null && userRepository.existsByEmail(request.getEmail())) {
            throw PlaygroundException.userAlreadyExists("邮箱已被注册");
        }

        // 创建用户
        User user = User.builder()
                .phone(request.getPhone())
                .email(email)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .sportsPreference(request.getSportsPreference())
                .description(request.getDescription())
                .build();

        user = userRepository.save(user);

        if (request.getAvatar() != null) {
            // 上传头像
            String avatarUrl = fileUtil.upload(user.getId(), request.getAvatar());
            user.setAvatar(avatarUrl);
        }

        user = userRepository.save(user);

        // 创建用户钱包
        Wallet wallet = Wallet.builder()
                .user(user)
                .build();

        walletRepository.save(wallet);

        log.info("用户注册成功，用户ID: {}, 手机号: {}", user.getId(), user.getPhone());
    }

    /**
     * 用户登录
     */
    public String login(UserLoginRequest request) {
        // 根据账号类型查找用户
        User user = findUserByAccount(request.getAccount())
                .orElseThrow(PlaygroundException::userNotFound);

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw PlaygroundException.invalidCredentials("账号或密码错误");
        }

        // 返回token
        return tokenUtil.generateToken(user);
    }

    /**
     * 获取当前用户信息
     */
    public UserInfoResponse getCurrentUserInfo() {
        return new UserInfoResponse(tokenUtil.getCurrentUser());
    }

    /**
     * 根据ID获取用户公开信息
     */
    public UserPublicInfoResponse getUserPublicInfo(Long userId) {
        return new UserPublicInfoResponse(userRepository.findById(userId).orElseThrow(PlaygroundException::userNotFound));
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserInfoResponse updateUserInfo(UserUpdateRequest request) {
        User user = tokenUtil.getCurrentUser();

        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            // 检查手机号是否已存在
            if (userRepository.existsByPhone(request.getPhone())) {
                throw PlaygroundException.userAlreadyExists("手机号已被注册");
            }
            user.setPhone(request.getPhone());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // 检查邮箱是否已存在
            if (userRepository.existsByEmail(request.getEmail())) {
                throw PlaygroundException.userAlreadyExists("邮箱已被注册");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getAvatar() != null) {
            if (user.getAvatar() != null) {
                // 删除旧头像
                fileUtil.delete(user.getAvatar());
            }
            user.setAvatar(fileUtil.upload(user.getId(), request.getAvatar()));
        }
        if (request.getSportsPreference() != null) {
            user.setSportsPreference(request.getSportsPreference());
        }
        if (request.getDescription() != null) {
            user.setDescription(request.getDescription());
        }

        User updatedUser = userRepository.save(user);

        return new UserInfoResponse(updatedUser);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(PasswordChangeRequest request) {
        User user = tokenUtil.getCurrentUser();

        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw PlaygroundException.invalidCredentials("原密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * 根据账号查找用户
     */
    private Optional<User> findUserByAccount(String account) {
        // 判断是手机号还是邮箱
        if (account.matches("^1[3-9]\\d{9}$")) {
            return userRepository.findByPhone(account);
        } else if (account.contains("@")) {
            return userRepository.findByEmail(account);
        } else {
            return Optional.empty();
        }
    }
}
