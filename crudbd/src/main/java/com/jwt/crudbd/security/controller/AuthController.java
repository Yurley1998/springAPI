package com.jwt.crudbd.security.controller;

import com.jwt.crudbd.dto.Mensaje;
import com.jwt.crudbd.security.dto.JwtDto;
import com.jwt.crudbd.security.dto.LoginUsuario;
import com.jwt.crudbd.security.dto.NuevoUsuario;
import com.jwt.crudbd.security.entity.Rol;
import com.jwt.crudbd.security.entity.Usuario;
import com.jwt.crudbd.security.enums.RolNombre;
import com.jwt.crudbd.security.jwt.JwtProvider;
import com.jwt.crudbd.security.service.RolService;
import com.jwt.crudbd.security.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    RolService rolService;
    @Autowired
    JwtProvider jwtProvider;
    @PostMapping("")
    public ResponseEntity<Mensaje> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<Mensaje>(new Mensaje("Verifique los datos introducidos"), HttpStatus.BAD_REQUEST);
        }
        if (usuarioService.existsByNombreUsuario(nuevoUsuario.getNombreUsuario())) {
            return new ResponseEntity<Mensaje>(new Mensaje("El nombre " + nuevoUsuario.getNombre() + " ya se encuentra registrado"), HttpStatus.BAD_REQUEST);
        }
        if (usuarioService.existsByEmail(nuevoUsuario.getEmail())) {
            return new ResponseEntity<Mensaje>(new Mensaje("El email " + nuevoUsuario.getEmail() + "ya se encuentra registrado"), HttpStatus.BAD_REQUEST);
        }
        Usuario usuario =
                new Usuario(nuevoUsuario.getNombre(), nuevoUsuario.getNombreUsuario(),
                        nuevoUsuario.getEmail(), passwordEncoder.encode(nuevoUsuario.getPassword()));
        Set<Rol> roles = new HashSet<>();
        if (!nuevoUsuario.getRoles().contains("admin"))
            roles.add(rolService.getByRolNombre(RolNombre.ROLE_USER).get());
        else
            roles.add(rolService.getByRolNombre(RolNombre.ROLE_ADMIN).get());
        usuario.setRoles(roles);
        usuarioService.save(usuario);
        return new ResponseEntity<Mensaje>(new Mensaje("Usuario creado con éxito"), HttpStatus.CREATED);
    }
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<Mensaje>(new Mensaje("Usuario inválido"), HttpStatus.UNAUTHORIZED);
        }
        Optional<Usuario> user = usuarioService.getByNombreUsuario(loginUsuario.getNombreUsuario());
        if (!user.isPresent())
            return new ResponseEntity<Mensaje>(new Mensaje("Usuario inválido"), HttpStatus.UNAUTHORIZED);

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getNombreUsuario(), loginUsuario.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        JwtDto jwtDto = new JwtDto(jwt);
        return new ResponseEntity<JwtDto>(jwtDto, HttpStatus.ACCEPTED);
    }
    @PostMapping(value = "/refresh")
    public ResponseEntity<JwtDto> refresh(@RequestBody JwtDto jwtDto) throws ParseException{
        String token = jwtProvider.refreshToken(jwtDto);
        JwtDto jwt = new JwtDto(token);
        return new ResponseEntity<JwtDto>(jwt, HttpStatus.OK);
    }
}
