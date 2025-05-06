package com.wcs.spring_data_jpa_project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorId;

    @NotBlank(message = "Author name is required !")
    private String authorName;

    @OneToMany(mappedBy = "author" ,cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Book> book;

}
