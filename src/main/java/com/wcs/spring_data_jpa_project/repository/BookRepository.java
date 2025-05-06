package com.wcs.spring_data_jpa_project.repository;

import com.wcs.spring_data_jpa_project.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Long> {

    List<Book> findByAuthorAuthorId(Long authorId);
    Page<Book> findByAuthorAuthorName(String authorName, Pageable pageable);

}
