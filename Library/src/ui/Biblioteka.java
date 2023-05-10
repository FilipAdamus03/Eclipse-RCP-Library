package ui;

import javax.inject.Inject;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

public class Biblioteka extends ViewPart {

	@Inject
	IWorkbench workbench;

	private TableViewer viewer;
	Composite parent;
	BibliotekaView biblioteka;

	@SuppressWarnings("unused")
	private class StringLabelProvider extends ColumnLabelProvider {

		@Override
		public Image getImage(Object obj) {
			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}

	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		this.biblioteka = new BibliotekaView(parent, SWT.NONE);
	}

	public void refresh() {
		biblioteka.refresh();
	}

	@Override
	public void setFocus() {
		viewer = new TableViewer(biblioteka.getTable());
		viewer.getControl().setFocus();
	}

}