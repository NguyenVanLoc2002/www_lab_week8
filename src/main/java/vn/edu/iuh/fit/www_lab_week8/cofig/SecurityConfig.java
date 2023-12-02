package vn.edu.iuh.fit.www_lab_week8.cofig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    public void globalConfig(AuthenticationManagerBuilder auth, PasswordEncoder encoder, DataSource dataSource) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource).withDefaultSchema()
                .withUser((User.withUsername("admin")
                        .password(encoder.encode("admin"))
                        .roles("ADMIN")
                        .build()
                ))
                .withUser(User.withUsername("guest")
                        .password(encoder.encode("guest"))
                        .roles("GUEST")
                        .build()
                )
                .withUser(User.withUsername("locnguyen")
                        .password(encoder.encode("0986045812"))
                        .roles("USER")
                        .build()
                )
        ;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/index").permitAll()//nhung links nay khong can authenticate
                .requestMatchers("/api/**").hasAnyRole("ADMIN", "USER", "GUEST")//nhung uri bat dau bang / api can phai dang nhap voi cac role admin / user / teo
                .requestMatchers(("/admin/**")).hasRole("ADMIN")//uri bat dau bang/ admin thi phai dang nhap voi quyen admin
                .requestMatchers(("/h2-console/**")).permitAll()
                .anyRequest().authenticated()//cac uri khac can dang nhap duoi bat ky role nao
        );
        http.httpBasic(Customizer.withDefaults());//cac thiet lap con lai thi theo mac dinh
        http.csrf(csrf->csrf.ignoringRequestMatchers("/h2-console/**"));
        http.headers(headers ->headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
