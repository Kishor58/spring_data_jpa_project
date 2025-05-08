package com.wcs.spring_data_jpa_project.service.core;

import com.wcs.spring_data_jpa_project.model.Author;
import com.wcs.spring_data_jpa_project.model.Book;
import com.wcs.spring_data_jpa_project.repository.AuthorRepository;
import com.wcs.spring_data_jpa_project.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

        @Autowired
        private AuthorRepository authorRepository;

        @Autowired
        private BookRepository bookRepository;


        public Author saveAuthorWithBooks(Author author) {
            return authorRepository.save(author);
        }

        public List<Author> getAllAuthors() {
            return authorRepository.findAll();
        }


        public Author getAuthorById(Long authorId) {
            return authorRepository.findById(authorId)
                    .orElseThrow(() -> new RuntimeException("Author not found"));
        }


        public Author updateAuthor(Long authorId, Author updatedAuthor) {
            Author author = getAuthorById(authorId);
            author.setAuthorName(updatedAuthor.getAuthorName());
            return authorRepository.save(author);
        }

        public String deleteAuthor(Long authorId) {
            Author author = getAuthorById(authorId);
            authorRepository.delete(author);
            return "Author and associated books deleted";
        }

        public Book saveBookForAuthor(Long authorId, Book book) {
            Author author = getAuthorById(authorId);
            book.setAuthor(author);
            return bookRepository.save(book);
        }

        public Book updateBookAuthor(Long bookId, Long authorId) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            Author author = getAuthorById(authorId);
            book.setAuthor(author);
            return bookRepository.save(book);
        }


        public List<Book> getBooksByAuthor(Long authorId) {
            return bookRepository.findByAuthorAuthorId(authorId);
        }


        public String deleteBook(Long bookId) {
            bookRepository.deleteById(bookId);
            return "Book deleted";
        }

}
