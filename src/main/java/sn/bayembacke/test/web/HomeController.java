package sn.bayembacke.test.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import sn.bayembacke.test.be.AuthenticateResponse;
import sn.bayembacke.test.be.Users;
import sn.bayembacke.test.dao.UserRepository;
import sn.bayembacke.test.security.jwt.JwtUtil;
import sn.bayembacke.test.service.UserDetailsServiceImpl;
import sn.bayembacke.test.service.UserSevice;

import javax.xml.bind.ValidationException;

@RestController
public class HomeController {
    private AuthenticationManager authenticationManager;

    UserSevice userDetailsService;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;


    @Autowired
    UserRepository repositoryUser;

    private JwtUtil jwtUtil;

    public HomeController(AuthenticationManager authenticationManager,
                          UserSevice userDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping({"/hello"})
    public String hello(){
        return "<h1>Hello</h1>";
    }


    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody Users request
    ) throws Exception {
        try{
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ;
        final UserDetails userDetails = userDetailsServiceImpl
                .loadUserByUsername(request.getUsername());

        final  String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticateResponse(jwt));

    }
    @PostMapping("/register")
    public Users register(@RequestBody Users request) throws ValidationException {
         return userDetailsService.saveUser(request);
    }
}
