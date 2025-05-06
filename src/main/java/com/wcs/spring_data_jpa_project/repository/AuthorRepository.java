package com.wcs.spring_data_jpa_project.repository;

import com.wcs.spring_data_jpa_project.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author,Long> {

}
