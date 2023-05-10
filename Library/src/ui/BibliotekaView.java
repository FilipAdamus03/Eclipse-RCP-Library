package ui;

import api.BookService;
import model.Book;
import service.XmlBookService;

import java.util.List;
import java.util.Properties;

import java.util.TimerTask;
import java.util.Timer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class BibliotekaView extends Composite {
	private Table table;
	private BookService bookService;
	private Composite parent;

	public BibliotekaView(Composite parent, int swt) {
		super(parent, swt);
		this.parent = parent;

		setupBookService();
		if (bookService == null) {
			System.exit(0);
		}

		loadData();

		startRefreshThreadForXmlBookService();
	}

	public void loadData() {
		String[] titles = { "ID", "Tytuł", "Autor", "Rok wydania", "ISBN", "Status" };

		if (table == null) {
			setLayout(new GridLayout(1, false));

			Label label = new Label(this, SWT.NONE);
			label.setText("Lista książek:");

			table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
			table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			table.setHeaderVisible(true);

			for (int i = 0; i < titles.length; i++) {
				TableColumn column = new TableColumn(table, SWT.NONE);
				column.setText(titles[i]);
			}
		}

		try {

			List<Book> books = bookService.getBooks();
			for (Book book : books) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, String.valueOf(book.getId()));
				item.setText(1, book.getTitle());
				item.setText(2, book.getAuthor());
				item.setText(3, String.valueOf(book.getYear()));
				item.setText(4, book.getIsbn());
				item.setText(5, book.getStatus());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}
	}

	public void refresh() {
		if (bookService instanceof XmlBookService) {
			((XmlBookService) bookService).refresh();
		}
		table.removeAll();
		loadData();
		table.redraw();
	}

	public void reload() {
		getParent().layout();
		redraw();
	}

	public Table getTable() {
		return table;
	}

	private void setupBookService() {
		try {
			Properties properties = new Properties();
			properties.load(getClass().getResourceAsStream("../resources/config.properties"));
			String bookServiceClass = properties.getProperty("bookServiceClass");
			this.bookService = (BookService) Class.forName(bookServiceClass).getMethod("getInstance").invoke(null);
		} catch (Exception e) {
			Shell dialogShell = new Shell(parent.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			MessageBox messageBox = new MessageBox(dialogShell, SWT.ICON_ERROR | SWT.OK);
			messageBox.setText("Błąd");
			messageBox.setMessage("Błąd w konfiguracji aplikacji!");
			int response = messageBox.open();

			if (response == SWT.YES) {
				System.exit(1);
			}
		}
	}

	private void startRefreshThreadForXmlBookService() {
		if (bookService instanceof XmlBookService) {
			Timer timer = new Timer();
			timer.schedule(new RefreshListTask(this), 0, 5000);
		}
	}

	private class RefreshListTask extends TimerTask {
		private BibliotekaView bibliotekaView;

		public RefreshListTask(BibliotekaView bibliotekaView) {
			this.bibliotekaView = bibliotekaView;
		}

		@Override
		public void run() {
			Display.getDefault().asyncExec(() -> {
				bibliotekaView.refresh();
			});

		}

	}

}