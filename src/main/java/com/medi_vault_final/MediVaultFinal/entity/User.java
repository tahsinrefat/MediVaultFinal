package com.medi_vault_final.MediVaultFinal.entity;

import com.medi_vault_final.MediVaultFinal.enums.Gender;
import com.medi_vault_final.MediVaultFinal.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "user_table")
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role cannot be null")
    private Role role;

    @Column
    @NotNull(message = "Age cannot be blank")
    private Integer age;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Gender cannot be null")
    private Gender gender;

    @Column
    @NotBlank(message = "Username cannot be null")
    private String username;

    @Column
    @NotBlank(message = "Password cannot be blank")
    private String password;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Prescription> prescriptionsAsDoctor;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Prescription> prescriptionsAsPatient;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+ role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
