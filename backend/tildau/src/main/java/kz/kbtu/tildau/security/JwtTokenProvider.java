package kz.kbtu.tildau.security;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtTokenProvider {
    String generateToken(UserDetails userDetails);
    boolean validateToken(String token);
    String getUsername(String token);
}