package com.example.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book save() {
        Book book = Book.builder()
            .title("Spring Boot")
            .author("Spring")
            .price(100)
            .build();

        return bookRepository.save(book);
    }
}
