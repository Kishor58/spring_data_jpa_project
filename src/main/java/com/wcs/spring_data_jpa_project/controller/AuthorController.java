package com.wcs.spring_data_jpa_project.controller;

import com.wcs.spring_data_jpa_project.model.Author;
import com.wcs.spring_data_jpa_project.model.Book;
import com.wcs.spring_data_jpa_project.service.core.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

        @Autowired
        private AuthorService authorService;


        @PostMapping
        public Author saveAuthorWithBooks(@RequestBody Author author) {
            return authorService.saveAuthorWithBooks(author);
        }


        @GetMapping
        public List<Author> getAllAuthors() {
            return authorService.getAllAuthors();
        }


        @GetMapping("/{id}")
        public Author getAuthorById(@PathVariable Long id) {
            return authorService.getAuthorById(id);
        }


        @PutMapping("/{id}")
        public Author updateAuthor(@PathVariable Long id, @RequestBody Author author) {
            return authorService.updateAuthor(id, author);
        }


        @DeleteMapping("/{id}")
        public String deleteAuthor(@PathVariable Long id) {
            return authorService.deleteAuthor(id);
        }


        @PostMapping("/{authorId}/books")
        public Book saveBookForAuthor(@PathVariable Long authorId, @RequestBody Book book) {
            return authorService.saveBookForAuthor(authorId, book);
        }


        @PutMapping("/books/{bookId}/author/{authorId}")
        public Book updateBookAuthor(@PathVariable Long bookId, @PathVariable Long authorId) {
            return authorService.updateBookAuthor(bookId, authorId);
        }


        @GetMapping("/{authorId}/books")
        public List<Book> getBooksByAuthor(@PathVariable Long authorId) {
            return authorService.getBooksByAuthor(authorId);
        }


        @DeleteMapping("/books/{bookId}")
        public String deleteBook(@PathVariable Long bookId) {
            return authorService.deleteBook(bookId);
        }

}
