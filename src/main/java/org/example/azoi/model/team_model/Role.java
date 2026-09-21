package org.example.azoi.model.team_model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles", schema = "azoi")
public class Role {

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_NORMAL = "normal";
    public static final Long ROLE_NORMAL_id = 1L;
    public static final Long ROLE_ADMIN_id = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, length = 32)
    private String code;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    public Role() {
    }

    public Role(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}