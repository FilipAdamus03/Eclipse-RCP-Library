package service;

import api.BookService;
import model.Book;
import java.util.ArrayList;
import java.util.List;

public class MockBookService implements BookService {
	private List<Book> books;

	private static final MockBookService INSTANCE = new MockBookService();

	private MockBookService() {
		books = new ArrayList<>();
		books.add(new Book(1, "Władca Pierścieni", "J.R.R. Tolkien", 1954, "9788380082172", "Na stanie"));
		books.add(new Book(2, "Hobbit", "J.R.R. Tolkien", 1937, "9788380082189", "Wypożyczona"));
	}

	public static BookService getInstance() {
		return INSTANCE;
	}

	@Override
	public List<Book> getBooks() {
		return books;
	}

	@Override
	public Book getBookById(int id) {
		for (Book book : books) {
			if (book.getId() == (id)) {
				return book;
			}
		}
		return null;
	}

	public void addNewBook(Book book) {
		book.setId(getMaxId(books) + 1);
		book.setStatus("Na stanie");
		books.add(book);
	}

	@Override
	public void deleteBook(int id) {
		Book book = getBookById(id);
		if (book != null) {
			books.remove(book);
		}
	}

	@Override
	public void changeBookStatus(int id) {
		Book book = getBookById(id);
		if (book != null) {
			if (book.getStatus().equals("Na stanie")) {
				book.setStatus("Wypożyczona");
			} else {
				book.setStatus("Na stanie");
			}
		}
	}

}