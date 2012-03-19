package se.gustavkarlsson.snap.gui.pages.send.choosesession;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import se.gustavkarlsson.snap.gui.pages.send.choosefiles.FileNameComparator;
import se.gustavkarlsson.snap.service.session.SessionManager;

public class ChooseSessionPage extends WizardPage {

	private final SessionManager sessionManager;
	private ListViewer sessionListViewer;
	private Button deleteButton;

	/**
	 * Create the wizard.
	 */
	public ChooseSessionPage(SessionManager sessionManager) {
		super(ChooseSessionPage.class.getName());
		setTitle("Choose Session");
		setDescription("Select a session to resume.");

		this.sessionManager = sessionManager;
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		sessionListViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL);
		sessionListViewer.setContentProvider(new ArrayContentProvider());
		sessionListViewer.setLabelProvider(new SessionLabelProvider());
		sessionListViewer.setComparator(new ViewerComparator(
				new FileNameComparator()));
		org.eclipse.swt.widgets.List sessionList = sessionListViewer.getList();
		sessionList.addSelectionListener(new SessionSelectedListener());
		sessionList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				2, 1));

		Button refreshButton = new Button(container, SWT.NONE);
		refreshButton.addSelectionListener(new RefreshSessionsListener());
		refreshButton.setText("&Refresh");

		deleteButton = new Button(container, SWT.NONE);
		deleteButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		deleteButton.addSelectionListener(new DeleteSessionListener());
		deleteButton.setText("&Delete");
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			refreshSessions();
			updateSelectedSession();
		}
		super.setVisible(visible);
	}

	@Override
	public boolean isPageComplete() {
		return (sessionManager.getCurrentSession() != null);
	}

	private void refreshSessions() {
		sessionManager.update();
		List<File> newSessions = sessionManager.getSessions();
		if (!newSessions.equals(sessionListViewer.getInput())) {
			sessionListViewer.setInput(newSessions);
		}
	}

	private void updateSelectedSession() {
		int selectedIndex = sessionListViewer.getList().getSelectionIndex();
		File selectedSession = selectedIndex == -1 ? null : sessionManager
				.getSessions().get(selectedIndex);
		sessionManager.setCurrentSession(selectedSession);
		sessionManager.setSessionChanged(true);

		deleteButton.setEnabled(sessionManager.hasCurrentSession());

		getWizard().getContainer().updateButtons();
	}

	private class RefreshSessionsListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			refreshSessions();
			updateSelectedSession();
		}
	}

	private class DeleteSessionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			boolean answeredYes = MessageDialog.openQuestion(getShell(),
					"Delete session",
					"Are you sure you want to delete this session?");

			if (answeredYes) {
				boolean deletedFile = sessionManager.getCurrentSession()
						.delete();
				if (deletedFile) {
					refreshSessions();
					updateSelectedSession();
				} else {
					MessageDialog.openError(getShell(), "Delete session",
							"Failed to delete session. You can try deleting it manually from:\n\n"
									+ sessionManager.getCurrentSession()
											.getAbsolutePath());
				}
			}
		}
	}

	private class SessionSelectedListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			updateSelectedSession();
		}
	}
}