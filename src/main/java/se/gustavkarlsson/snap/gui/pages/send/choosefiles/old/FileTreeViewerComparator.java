package se.gustavkarlsson.snap.gui.pages.send.choosefiles.old;

import java.util.Comparator;

import org.eclipse.jface.viewers.ViewerComparator;

import se.gustavkarlsson.snap.domain.old.FileNode;
import se.gustavkarlsson.snap.domain.old.FolderNode;

public class FileTreeViewerComparator extends ViewerComparator {

	public FileTreeViewerComparator() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public FileTreeViewerComparator(Comparator comparator) {
		super(comparator);
	}

	@Override
	public int category(Object element) {
		if (element instanceof FolderNode) {
			return 1;
		}
		if (element instanceof FileNode) {
			return 2;
		}
		return super.category(element);
	}
}
