package com.fastcampus.boardserver.service.impl;

import com.fastcampus.boardserver.dto.UserDTO;
import com.fastcampus.boardserver.exception.DuplicateIdException;
import com.fastcampus.boardserver.mapper.UserProfileMapper;
import com.fastcampus.boardserver.service.UserService;
import com.fastcampus.boardserver.util.SHA256Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserProfileMapper userProfileMapper;
    @Override
    public void register(UserDTO userProfile) {
        boolean isDuplicated = this.isDuplicated(userProfile.getUserId());
        if(isDuplicated) {
            throw new DuplicateIdException("중복된 아이디입니다.");
        }
        userProfile.setCreateTime(new Date());
        userProfile.setPassword(SHA256Util.encryptSHA256(userProfile.getPassword()));

        int count = userProfileMapper.register(userProfile);
        if (count != 1) {
            log.error("insert User ERROR {}", userProfile);
            throw new RuntimeException("회원 가입 중 오류 발생 \n" +
                    " params: " + userProfile);
        }
    }

    @Override
    public UserDTO login(String id, String password) {
        String encryptedPassword = SHA256Util.encryptSHA256(password);
        UserDTO userInfo = userProfileMapper.findByUserIdAndPassword(id, encryptedPassword);

        return userInfo;
    }

    @Override
    public boolean isDuplicated(String id) {
        return userProfileMapper.idCheck(id) == 1;
    }

    @Override
    public UserDTO getUserInfo(String id) {
        return null;
    }

    @Override
    public void updatePassword(String id, String previousPassword, String newPassword) {
        String encryptedPassword = SHA256Util.encryptSHA256(previousPassword);
        UserDTO userInfo = userProfileMapper.findByUserIdAndPassword(id, encryptedPassword);
        if(userInfo != null) {
            userInfo.setPassword(SHA256Util.encryptSHA256(newPassword));
            int updated = userProfileMapper.updatePassword(userInfo);
        } else {
            log.error("회원정보 없음. {}", id);
            throw new RuntimeException("id, password에 해당하는 회원을 찾지 못했습니다.");
        }

    }

    @Override
    public void deleteId(String id, String password) {
        String cryptoPassword = SHA256Util.encryptSHA256(password);
        UserDTO memberInfo = userProfileMapper.findByIdAndPassword(id, cryptoPassword);

        if (memberInfo != null) {
            userProfileMapper.deleteUserProfile(memberInfo.getUserId());
        } else {
            log.error("deleteId ERROR! {}", memberInfo);
            throw new RuntimeException("deleteId ERROR! id 삭제 메서드를 확인해주세요\n" + "Params : " + memberInfo);
        }

    }
}
