package org.example.azoi.model;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@NullMarked
@Table(name = "roles", schema = "azoi")
public class Role {
    @Id
    private Long id = 0L;

    private String code = "";

    private String name = "";

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