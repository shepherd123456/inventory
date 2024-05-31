package cz.technico.inventory.userdetail;

import cz.technico.inventory.user.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailService{
    private UserDetailRepository userDetailRepository;
    @Override
    public UserDetail save(UserDetail userDetail) {
        return userDetailRepository.save(userDetail);
    }

    @Override
    public UserDetail findByEmail(String email) {
        return userDetailRepository.findByEmail(email);
    }

    @Override
    public String getProfileImgFilename(String email) {
        return userDetailRepository.getProfileImgFilename(email);
    }

    @Override
    @Transactional
    public boolean existsByUser(User user) {
        return userDetailRepository.existsByUser(user);
    }
}
