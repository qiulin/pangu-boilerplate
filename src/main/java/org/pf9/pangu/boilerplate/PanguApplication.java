package org.pf9.pangu.boilerplate;


import org.pf9.pangu.boilerplate.ApplicationProperties;
import org.pf9.pangu.framework.common.config.constants.ProfileConstants;
import org.pf9.pangu.framework.common.util.DefaultProfileUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

@EnableWebMvc
@ComponentScan(basePackages = {"org.pf9.pangu"})
//@EnableAutoConfiguration则表示让Spring Boot根据类路径中的jar包依赖为当前项目进行自动配置
@EnableAutoConfiguration
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
//这是个组合注解
//@SpringBootApplication(scanBasePackages = {"org.pf9.pangu"})
@MapperScan(basePackages = "org.pf9.pangu.**.mapper")
public class PanguApplication {

    private static final Logger log = LoggerFactory.getLogger(PanguApplication.class);

    private final Environment env;

    private final ApplicationProperties applicationProperties;

    public PanguApplication(Environment env, ApplicationProperties applicationProperties) {
        this.env = env;
        this.applicationProperties = applicationProperties;
    }

    @PostConstruct
    public void initApplication() {
        log.debug("upload path" + applicationProperties.getUploadPath());

        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());

        if (activeProfiles.contains(ProfileConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(ProfileConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                    "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(ProfileConstants.SPRING_PROFILE_JWT_AUTH) && activeProfiles.contains(ProfileConstants.SPRING_PROFILE_SESSION_AUTH)) {
            log.error("You have misconfigured your application! It should not run " +
                    "with both the 'jwt' and 'session' profiles at the same time.");
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(PanguApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\t{}://localhost:{}\n\t" +
                        "External: \t{}://{}:{}\n\t" +
                        "Profile(s): \t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                protocol,
                env.getProperty("server.port"),
                protocol,
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                env.getActiveProfiles());
    }
}
