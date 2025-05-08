package com.wcs.spring_data_jpa_project.service.core;

import com.wcs.spring_data_jpa_project.model.Book;
import com.wcs.spring_data_jpa_project.repository.AuthorRepository;
import com.wcs.spring_data_jpa_project.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;


    public Book saveBook(Book book) {
        book.setAuthor(
                authorRepository.findById(book.getAuthor().getAuthorId())
                        .orElseThrow(() -> new RuntimeException("Author not found"))
        );
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }


    public Book updateBook(Long id, Book updatedBook) {
        Book book = bookRepository.findById(id).orElseThrow();
        book.setTittle(updatedBook.getTittle());
        book.setPrice(updatedBook.getPrice());
        return bookRepository.save(book);
    }

    public String deleteBook(Long id) {
        bookRepository.deleteById(id);
        return "Book deleted";
    }

    public Page<Book> getBooksByAuthor(String authorName, int page, int size, String sortField, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.trim());//Here it is taking one input from user asc or desc that convert for Assending or Dessending order

        Sort sort = Sort.by(direction, sortField); // Sort.by method are used to check the direct means asc or desc as well as which column we want to sort that also it took.

        Pageable pageable = PageRequest.of(page, size, sort);//
        return bookRepository.findByAuthorAuthorName(authorName, pageable);
    }


}
