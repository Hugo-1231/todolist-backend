package com.todolist.service;

import com.todolist.common.BusinessException;
import com.todolist.dto.UserProfileResponse;
import com.todolist.entity.User;
import com.todolist.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户个人中心业务：资料、头像、手机号、邮箱、改密码。
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileResponse getProfile() {
        return toProfile(currentUser());
    }

    @Transactional
    public UserProfileResponse updateProfile(String nickname, String avatar) {
        User user = currentUser();
        if (StringUtils.hasText(nickname)) {
            user.setNickname(nickname);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        user.setUpdatedAt(LocalDateTime.now());
        return toProfile(userRepository.save(user));
    }

    @Transactional
    public UserProfileResponse bindPhone(String phone) {
        User user = currentUser();
        if (!phone.matches("^1\\d{10}$")) {
            throw new BusinessException("手机号格式不正确");
        }
        if (userRepository.existsByPhoneAndIdNot(phone, user.getId())) {
            throw new BusinessException("该手机号已被其他账号绑定");
        }
        user.setPhone(phone);
        user.setUpdatedAt(LocalDateTime.now());
        return toProfile(userRepository.save(user));
    }

    @Transactional
    public UserProfileResponse bindEmail(String email) {
        User user = currentUser();
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$")) {
            throw new BusinessException("邮箱格式不正确");
        }
        if (userRepository.existsByEmailAndIdNot(email, user.getId())) {
            throw new BusinessException("该邮箱已被其他账号绑定");
        }
        user.setEmail(email);
        user.setUpdatedAt(LocalDateTime.now());
        return toProfile(userRepository.save(user));
    }

    @Transactional
    public void changePassword(String phone, String newPassword) {
        User user = currentUser();
        if (!StringUtils.hasText(user.getPhone())) {
            throw new BusinessException("请先在个人中心绑定手机号");
        }
        if (!user.getPhone().equals(phone)) {
            throw new BusinessException("手机号与绑定的手机号不一致");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("新密码至少 6 位");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 获取当前登录用户（托管实体，便于保存）。
     */
    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            Long id = ((User) authentication.getPrincipal()).getId();
            return userRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(401, "用户不存在"));
        }
        throw new BusinessException(401, "未登录");
    }

    private UserProfileResponse toProfile(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar(),
                user.getPhone(),
                user.getEmail()
        );
    }
}
