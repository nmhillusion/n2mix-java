package tech.nmhillusion.n2mix.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.model.Book;

class CloneHelperTest {
    @Test
    void testClone() {
        Assertions.assertDoesNotThrow(() -> {
            final Book book1 = new Book()
                    .setTitle("The Code")
                    .setAuthor("Bob")
                    .setPrice(100);
            
            final Book book2 = CloneHelper.clone(book1);
            
            Assertions.assertEquals(book1.getTitle(), book2.getTitle());
            Assertions.assertEquals(book1.getAuthor(), book2.getAuthor());
            Assertions.assertEquals(book1.getPrice(), book2.getPrice());
            
            book2.setTitle("Clean Coder");
            Assertions.assertNotEquals(book1.getTitle(), book2.getTitle());
            Assertions.assertEquals(book1.getAuthor(), book2.getAuthor());
            Assertions.assertEquals(book1.getPrice(), book2.getPrice());
            
            book2.setPrice(101);
            Assertions.assertNotEquals(book1.getTitle(), book2.getTitle());
            Assertions.assertEquals(book1.getAuthor(), book2.getAuthor());
            Assertions.assertNotEquals(book1.getPrice(), book2.getPrice());
            
            book2.setPrice(100);
            Assertions.assertNotEquals(book1.getTitle(), book2.getTitle());
            Assertions.assertEquals(book1.getAuthor(), book2.getAuthor());
            Assertions.assertEquals(book1.getPrice(), book2.getPrice());
            
            book2.setTitle("The Code");
            Assertions.assertEquals(book1.getTitle(), book2.getTitle());
            Assertions.assertEquals(book1.getAuthor(), book2.getAuthor());
            Assertions.assertEquals(book1.getPrice(), book2.getPrice());
        });
    }
}