package ui;

import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import java.util.Properties;

import api.BookService;
import model.Book;

public class BookRental extends ViewPart {
	public static final String ID = "ui.bookRental";
	private Composite parent;

	private BookService bookService;

	public void createPartControl(Composite parent) {
		this.parent = parent;

		setupBookService();

		Composite buttonComposite = setupGridLayout(parent);

		setupAddBookButton(parent, buttonComposite);

		setupRemoveBookButton(parent, buttonComposite);

		setupChangeBookButton(parent, buttonComposite);

	}

	public void setFocus() {
		parent.setFocus();
	}

	private void setupBookService() {
		try {
			Properties properties = new Properties();
			properties.load(getClass().getResourceAsStream("../resources/config.properties"));
			String bookServiceClass = properties.getProperty("bookServiceClass");
			this.bookService = (BookService) Class.forName(bookServiceClass).getMethod("getInstance").invoke(null);
		} catch (Exception e) { // This is handled in BibliotekaView
		}
	}

	private Composite setupGridLayout(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		GridLayout buttonLayout = new GridLayout(3, false);
		buttonComposite.setLayout(buttonLayout);
		GridData buttonGridData = new GridData(GridData.FILL_HORIZONTAL);
		buttonGridData.horizontalAlignment = SWT.CENTER;
		buttonComposite.setLayoutData(buttonGridData);
		return buttonComposite;
	}

	private void setupAddBookButton(Composite parent, Composite buttonComposite) {
		Button addBookButton = new Button(buttonComposite, SWT.PUSH);
		addBookButton.setText("Dodaj książkę");
		addBookButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		addBookButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell dialogShell = new Shell(parent.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				dialogShell.setText("Dodaj książkę");
				dialogShell.setLayout(new GridLayout(2, false));

				Label titleLabel = new Label(dialogShell, SWT.NONE);
				titleLabel.setText("Tytuł:");
				Text titleText = new Text(dialogShell, SWT.BORDER);

				Label authorLabel = new Label(dialogShell, SWT.NONE);
				authorLabel.setText("Autor:");
				Text authorText = new Text(dialogShell, SWT.BORDER);

				Label yearLabel = new Label(dialogShell, SWT.NONE);
				yearLabel.setText("Rok:");
				Text yearText = new Text(dialogShell, SWT.BORDER);

				Label isbnLabel = new Label(dialogShell, SWT.NONE);
				isbnLabel.setText("ISBN:");
				Text isbnText = new Text(dialogShell, SWT.BORDER);

				Button okButton = new Button(dialogShell, SWT.PUSH);
				okButton.setText("OK");
				okButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							String title = titleText.getText().trim();
							String author = authorText.getText().trim();
							String yearString = yearText.getText().trim();
							String isbn = isbnText.getText().trim();
							if (title.isEmpty() || author.isEmpty() || yearString.isEmpty() || isbn.isEmpty()) {
								throw new IllegalArgumentException("Pola nie mogą być puste.");
							}
							int year = Integer.parseInt(yearString);
							if (year < 1000 || year > 2023) {
								throw new IllegalArgumentException("Rok musi być z przedziału 1000-2023.");
							}
							if (isbn.isEmpty()) {
								throw new IllegalArgumentException("Pole ISBN nie może być puste.");
							} else if (!isbn.matches("\\d+")) {
								throw new IllegalArgumentException("ISBN musi składać się tylko z cyfr.");
							} else if (isbn.length() != 13) {
								throw new IllegalArgumentException("ISBN musi składać się z 13 cyfr.");
							}
							Book book = new Book();
							book.setTitle(title);
							book.setAuthor(author);
							book.setYear(year);
							book.setIsbn(isbn);
							bookService.addNewBook(book);
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							IViewPart viewPart = page.findView("ui.biblioteka");
							Biblioteka view = (Biblioteka) viewPart;
							view.refresh();
							dialogShell.dispose();
						} catch (NumberFormatException ex) {
							MessageBox messageBox = new MessageBox(dialogShell, SWT.ICON_ERROR | SWT.OK);
							messageBox.setText("Błąd");
							messageBox.setMessage("Nieprawidłowy format roku.");
							messageBox.open();
						} catch (IllegalArgumentException ex) {
							MessageBox messageBox = new MessageBox(dialogShell, SWT.ICON_ERROR | SWT.OK);
							messageBox.setText("Błąd");
							messageBox.setMessage(ex.getMessage());
							messageBox.open();
						}
					}
				});

				Button cancelButton = new Button(dialogShell, SWT.PUSH);
				cancelButton.setText("Anuluj");
				cancelButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						dialogShell.dispose();
					}
				});

				dialogShell.pack();
				dialogShell.open();
			}

		});
	}

	private void setupRemoveBookButton(Composite parent, Composite buttonComposite) {
		Button removeBookButton = new Button(buttonComposite, SWT.PUSH);
		removeBookButton.setText("Usuń książkę");
		removeBookButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		removeBookButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = new Shell(parent.getShell(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
				shell.setText("Usuń książkę");
				shell.setLayout(new GridLayout(2, false));

				Label idLabel = new Label(shell, SWT.NONE);
				idLabel.setText("ID:");

				Text idText = new Text(shell, SWT.BORDER);
				idText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

				Button removeButton = new Button(shell, SWT.PUSH);
				removeButton.setText("Usuń");
				removeButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
				removeButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						String idString = idText.getText().trim();
						try {
							int id = Integer.parseInt(idString);
							if (bookService.getBookById(id) == null) {
								MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
								messageBox.setText("Błąd");
								messageBox.setMessage("Książka o podanym ID nie istnieje!");
								messageBox.open();
							} else {
								bookService.deleteBook(id);
								IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
										.getActivePage();
								IViewPart viewPart = page.findView("ui.biblioteka");
								Biblioteka view = (Biblioteka) viewPart;
								view.refresh();
								shell.close();
							}
						} catch (NumberFormatException ex) {
							MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
							messageBox.setText("Błąd");
							messageBox.setMessage("Wprowadź poprawne ID!");
							messageBox.open();
						}
					}
				});

				Button cancelButton = new Button(shell, SWT.PUSH);
				cancelButton.setText("Anuluj");
				cancelButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
				cancelButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						shell.close();
					}
				});

				shell.pack();
				shell.open();
			}
		});
	}

	private void setupChangeBookButton(Composite parent, Composite buttonComposite) {
		Button changeBookStatusButton = new Button(buttonComposite, SWT.PUSH);
		changeBookStatusButton.setText("Zmień status książki");
		changeBookStatusButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		changeBookStatusButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell dialog = new Shell(parent.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				dialog.setText("Zmień status książki");

				GridLayout layout = new GridLayout(2, false);
				dialog.setLayout(layout);

				Label idLabel = new Label(dialog, SWT.NONE);
				idLabel.setText("ID:");

				Text idText = new Text(dialog, SWT.BORDER);
				idText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

				Button okButton = new Button(dialog, SWT.PUSH);
				okButton.setText("OK");
				okButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
				okButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						String idString = idText.getText().trim();
						if (idString.isEmpty()) {
							MessageBox messageBox = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
							messageBox.setMessage("ID nie może być puste.");
							messageBox.open();
							return;
						}

						int id = Integer.parseInt(idString);
						Book book = bookService.getBookById(id);
						if (book == null) {
							MessageBox messageBox = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
							messageBox.setMessage("Książka o podanym ID nie istnieje.");
							messageBox.open();
							return;
						}

						bookService.changeBookStatus(id);
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						IViewPart viewPart = page.findView("ui.biblioteka");
						Biblioteka view = (Biblioteka) viewPart;
						view.refresh();
						view.setFocus();
						dialog.close();
					}
				});

				Button cancelButton = new Button(dialog, SWT.PUSH);
				cancelButton.setText("Anuluj");
				cancelButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
				cancelButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						dialog.close();
					}
				});

				dialog.pack();
				dialog.open();
			}
		});
	}

	@SuppressWarnings("unused")
	private class DialogHelper {
		private Shell shell;

		public DialogHelper(Shell shell) {
			this.shell = shell;
		}
	}
}