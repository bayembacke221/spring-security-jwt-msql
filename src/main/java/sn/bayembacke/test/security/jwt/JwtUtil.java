package sn.bayembacke.test.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sn.bayembacke.test.be.Users;
import sn.bayembacke.test.security.SecurityParams;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);

    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SecurityParams.SECRET).parseClaimsJws(token).getBody();
    }
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims,userDetails.getUsername());
    }
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims)
                .setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + SecurityParams.EXPIRATION))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SecurityParams.SECRET).compact();
    }
    public Boolean validateToken(String token, String username) {
        final String usernameFromToken = extractUsername(token);
        return (usernameFromToken.equals(username) && !isTokenExpired(token));
    }

}
