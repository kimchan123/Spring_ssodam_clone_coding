package ssodam.ssodam.domain;

import lombok.*;
import org.apache.tomcat.jni.Local;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import java.util.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member implements UserDetails {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String username;


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    private String email;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<Scrap> scraps = new HashSet<>();

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    private LocalDateTime createDate;

    public Member(String username, String password, String email, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password =password;
        this.email = email;
        this.authorities = authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
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
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
