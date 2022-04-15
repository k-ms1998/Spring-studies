package jpabook.jpashop.Domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Book extends Item{
    private String author;
    private String isbn;

    static public Book createBook(String name, int price, int quantity, String author, String isbn) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setQuantity(quantity);
        book.setAuthor(author);
        book.setIsbn(isbn);

        return book;
    }
}
