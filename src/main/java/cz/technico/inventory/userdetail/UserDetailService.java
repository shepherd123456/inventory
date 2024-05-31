package cz.technico.inventory.userdetail;

import cz.technico.inventory.user.User;

public interface UserDetailService {
    UserDetail save(UserDetail userDetail);
    UserDetail findByEmail(String email);
    String getProfileImgFilename(String email);
    boolean existsByUser(User user);
}
