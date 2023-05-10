package service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import api.BookService;
import model.Book;

public class XmlBookService implements BookService {

	private static final XmlBookService INSTANCE = new XmlBookService();

	private List<Book> books;

	private XmlBookService() {
		loadBooksFromXml();
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
		saveBooksToXml();
	}

	@Override
	public void deleteBook(int id) {
		Book book = getBookById(id);
		if (book != null) {
			books.remove(book);
			saveBooksToXml();
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
			saveBooksToXml();
		}
	}

	public void refresh() {
		loadBooksFromXml();
	}
	
	private void loadBooksFromXml() {

		try {
			File xmlFile = new File("resources/books.xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("book");
			books = new ArrayList<>();

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					int id = Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent());
					String title = eElement.getElementsByTagName("title").item(0).getTextContent();
					String author = eElement.getElementsByTagName("author").item(0).getTextContent();
					int year = Integer.parseInt(eElement.getElementsByTagName("year").item(0).getTextContent());
					String isbn = eElement.getElementsByTagName("isbn").item(0).getTextContent();
					String status = eElement.getElementsByTagName("status").item(0).getTextContent();

					Book book = new Book(id, title, author, year, isbn, status);
					books.add(book);
				}
			}
		}

		catch (ParserConfigurationException | SAXException | IOException e) {

			e.printStackTrace();
		}
	}

	private void saveBooksToXml() {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();

			Element rootElement = doc.createElement("books");
			doc.appendChild(rootElement);

			for (Book book : books) {
				Element bookElement = doc.createElement("book");
				rootElement.appendChild(bookElement);

				Element id = doc.createElement("id");
				id.appendChild(doc.createTextNode(Integer.toString(book.getId())));
				bookElement.appendChild(id);

				Element title = doc.createElement("title");
				title.appendChild(doc.createTextNode(book.getTitle()));
				bookElement.appendChild(title);

				Element author = doc.createElement("author");
				author.appendChild(doc.createTextNode(book.getAuthor()));
				bookElement.appendChild(author);

				Element year = doc.createElement("year");
				year.appendChild(doc.createTextNode(Integer.toString(book.getYear())));
				bookElement.appendChild(year);

				Element isbn = doc.createElement("isbn");
				isbn.appendChild(doc.createTextNode(book.getIsbn()));
				bookElement.appendChild(isbn);

				Element status = doc.createElement("status");
				status.appendChild(doc.createTextNode(book.getStatus()));
				bookElement.appendChild(status);
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("resources/books.xml"));
			transformer.transform(source, result);
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}

}