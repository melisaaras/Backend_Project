package com.tpe.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//1.ADIM: KULLANDIĞIMIZ USERLARI SECURİTYE ANLATABİLMEK İÇİN USERDETAİLSE İMPLEMENT EDİYORUZ.
//kimlik doprulama ve yetkilendirme

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;

    private String username;

    private String name;

    private Boolean isAdvisor;

    @JsonIgnore //clienta bu bilgi gitmesin
    private String password;

    private String ssn;

    private Collection<? extends GrantedAuthority> authorities; //securityede roller enum type olarak tutulmuyordu. GrantedAuthority olarak tutuluyordu.


    public UserDetailsImpl(Long id, String username, String name, Boolean isAdvisor,
                           String password, String role, String ssn){ //parametreli consa kendi api tarafındaki userların bilgilerini verdiğimiz zaman, UserDetailsImpl türüne çevirmesini sağlarız.
        this.id = id;
        this.username=username;
        this.name= name;
        this.isAdvisor=isAdvisor;
        this.password=password;
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(role));
        this.authorities=grantedAuthorities;
        this.ssn=ssn;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { //hesap kilitliliği
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() { //aktiflik
        return true;
    }
}
