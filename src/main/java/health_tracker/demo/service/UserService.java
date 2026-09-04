package health_tracker.demo.service;

import health_tracker.demo.model.Role;
import health_tracker.demo.model.User;
import health_tracker.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Transactional
    public void register(User user) {

        System.out.println("SAVING USER IN DB");

        // 🔐 Encrypt password
        user.setPassword(encoder.encode(user.getPassword()));

        // 👤 DEFAULT ROLE


        // ✅ Enable account
        user.setEnabled(true);

        userRepository.save(user);

        System.out.println("USER SAVED SUCCESSFULLY");
    }
}
