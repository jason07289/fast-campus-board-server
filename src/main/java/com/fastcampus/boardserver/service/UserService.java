package com.fastcampus.boardserver.service;

import com.fastcampus.boardserver.dto.UserDTO;

public interface UserService {
    void register(UserDTO userProfile);

    UserDTO login(String id, String password);

    boolean isDuplicated(String id);

    UserDTO getUserInfo(String id);

    void updatePassword(String id, String previousPassword, String newPassword);

    void deleteId(String id, String password);

}
