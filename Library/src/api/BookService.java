package api;

import java.util.List;
import model.Book;

public interface BookService {
	public static BookService getInstance() {
		throw new UnsupportedOperationException();
	}

	List<Book> getBooks();

	Book getBookById(int id);

	void addNewBook(Book book);

	void deleteBook(int id);

	void changeBookStatus(int id);

	default int getMaxId(List<Book> books) {
		int maxId = 0;
		for (Book book : books) {
			if (book.getId() > maxId) {
				maxId = book.getId();
			}
		}
		return maxId;
	}

}