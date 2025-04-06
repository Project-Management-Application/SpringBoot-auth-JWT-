package com.midou.tutorial.user.entities;

import com.midou.tutorial.user.enums.Role;
import com.midou.tutorial.user.enums.Subscription;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.midou.tutorial.Workspace.entities.Workspace;
import com.midou.tutorial.Workspace.entities.WorkspaceMember;
import com.midou.tutorial.Projects.entities.Project;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;



import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phoneNumber;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String googleId;

    private boolean isGoogleUser = false;

    private String otp;

    private boolean enabled = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subscription subscription = Subscription.STARTER;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return true ;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean getEnabled() {
        return enabled;
    }

    @OneToOne(mappedBy = "owner")
    @JsonBackReference
    private Workspace workspace;


    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<WorkspaceMember> workspaceMemberships;

    @OneToMany(mappedBy = "owner")
    private List<Project> ownedProjects;

    @ManyToMany(mappedBy = "members")
    private List<Project> projects;
}
