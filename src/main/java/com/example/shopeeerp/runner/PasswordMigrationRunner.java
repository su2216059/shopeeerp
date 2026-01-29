package com.example.shopeeerp.runner;

import com.example.shopeeerp.mapper.UserMapper;
import com.example.shopeeerp.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.password-migration", name = "enabled", havingValue = "true")
public class PasswordMigrationRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(PasswordMigrationRunner.class);

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final boolean exitAfterRun;

    public PasswordMigrationRunner(
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            @org.springframework.beans.factory.annotation.Value("${app.password-migration.exit-after-run:true}")
            boolean exitAfterRun) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.exitAfterRun = exitAfterRun;
    }

    @Override
    public void run(String... args) {
        List<User> users = userMapper.selectAll();
        int updated = 0;
        for (User user : users) {
            String raw = user.getPassword();
            if (raw == null || isBcryptHash(raw)) {
                continue;
            }
            String encoded = passwordEncoder.encode(raw);
            userMapper.updatePasswordById(user.getUserId(), encoded, LocalDateTime.now());
            updated++;
        }
        logger.info("Password migration completed. Updated {}", updated);
        if (exitAfterRun) {
            logger.info("Password migration exit-after-run is enabled. Exiting application.");
            System.exit(0);
        }
    }

    private boolean isBcryptHash(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }
}
