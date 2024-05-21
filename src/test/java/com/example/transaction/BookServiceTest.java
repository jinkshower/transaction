package com.example.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    @Transactional
    @Sql("/sql/test-data.sql")
    void transaction_test() {
        Long id = 1L;
        Book book = bookService.get(id);
        System.out.println("book.getId() = " + book.getId());
        System.out.println("book.getTitle() = " + book.getTitle());
        System.out.println("book.getAuthor() = " + book.getAuthor());
        System.out.println("book.getPrice() = " + book.getPrice());
        assertThat(book).isNotNull();
    }

    @Test
    @Transactional
    @Sql(scripts = "/sql/test-data.sql",
        config = @SqlConfig(transactionMode = TransactionMode.ISOLATED))
    void multi_thread_test() {
        boolean outerTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        System.out.println("outerTransactionActive = " + outerTransactionActive);
        Long id = 1L;
        Book book = bookService.get(id);
        System.out.println("book.getId() = " + book.getId());
        System.out.println("book.getTitle() = " + book.getTitle());
        System.out.println("book.getAuthor() = " + book.getAuthor());
        System.out.println("book.getPrice() = " + book.getPrice());

        int count = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(16);
        CountDownLatch latch = new CountDownLatch(5);

        for (int i = 0; i < count; i++) {
            executorService.execute(() -> {
                boolean innerTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
                System.out.println("innerTransactionActive = " + innerTransactionActive);
                Book book1 = bookService.get(id);
                System.out.println("book1.getId() = " + book1.getId());
                System.out.println("book1.getTitle() = " + book1.getTitle());
                System.out.println("book1.getAuthor() = " + book1.getAuthor());
                System.out.println("book1.getPrice() = " + book1.getPrice());

                latch.countDown();
            });
        }
    }
}
