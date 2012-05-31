package se.gustavkarlsson.snap.gui.pages.send.advancedoptions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;

import se.gustavkarlsson.snap.resources.PropertyManager;
import se.gustavkarlsson.snap.util.NetworkUtils;
import se.gustavkarlsson.snap.util.PasswordUtils;
import se.gustavkarlsson.snap.util.PasswordUtils.Strength;

public class AdvancedOptionsPage extends WizardPage {
	private ComboViewer listeningAddressComboViewer;
	private ComboViewer compressionRateComboViewer;
	private Button enableCompressionButton;
	private Label compressionRateLabel;
	private Combo compressionRateCombo;
	private Combo listeningAddressCombo;
	private Group encryptionGroup;
	private Button enableEncryptionButton;
	private Text encryptionKeyText;
	private Label encryptionKeyLabel;
	private Button enableUpnpPortMappingButton;
	private Button enableNatpmpPortMappingButton;
	private Text portText;
	private Text encryptionKeyStrengthText;

	/**
	 * Create the wizard.
	 */
	public AdvancedOptionsPage() {
		super(AdvancedOptionsPage.class.getName());
		setTitle("Advanced Options");
		setDescription("Set advanced options.");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, false));
		setControl(container);

		createNetworkGroup(container);
		createCompressionGroup(container);
		createEncryptionGroup(container);

		bindComponents();
	}
	
	private void bindComponents() {
		DataBindingContext bindingContext = new DataBindingContext(
				SWTObservables.getRealm(getShell().getDisplay()));

		// Compression
		IObservableValue enableCompressionButtonSelection = SWTObservables
				.observeSelection(enableCompressionButton);
		IObservableValue compressionRateLabelEnabled = SWTObservables
				.observeEnabled(compressionRateLabel);
		IObservableValue compressionRateComboEnabled = SWTObservables
				.observeEnabled(compressionRateCombo);
		bindingContext.bindValue(enableCompressionButtonSelection, compressionRateLabelEnabled);
		bindingContext.bindValue(enableCompressionButtonSelection, compressionRateComboEnabled);
		
		// Encryption
		IObservableValue enableEncryptionButtonSelection = SWTObservables
				.observeSelection(enableEncryptionButton);
		IObservableValue encryptionKeyLabelEnabled = SWTObservables
				.observeEnabled(encryptionKeyLabel);
		IObservableValue encryptionKeyTextEnabled = SWTObservables
				.observeEnabled(encryptionKeyText);
		IObservableValue encryptionKeyStrengthTextEnabled = SWTObservables
				.observeEnabled(encryptionKeyStrengthText);
		bindingContext.bindValue(enableEncryptionButtonSelection, encryptionKeyLabelEnabled);
		bindingContext.bindValue(enableEncryptionButtonSelection, encryptionKeyTextEnabled);
		bindingContext.bindValue(enableEncryptionButtonSelection, encryptionKeyStrengthTextEnabled);
		
		// FIXME Clear encryptionKeyStrengthText when disabled.
	}

	private void createNetworkGroup(Composite container) {
		Group networkGroup = new Group(container, SWT.NONE);
		networkGroup.setLayout(new GridLayout(2, false));
		networkGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		networkGroup.setText("Network");

		Label listeningAddressLabel = new Label(networkGroup, SWT.NONE);
		listeningAddressLabel.setText("Listening address:");

		listeningAddressComboViewer = new ComboViewer(networkGroup, SWT.NONE);
		listeningAddressComboViewer
				.setContentProvider(new ArrayContentProvider());
		listeningAddressComboViewer
				.setLabelProvider(new Inet4AddressLabelProvider());

		listeningAddressComboViewer.setInput(NetworkUtils.listInetAddresses());
		listeningAddressCombo = listeningAddressComboViewer.getCombo();
		listeningAddressCombo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				true, false, 1, 1));
		listeningAddressCombo.select(0);

		Label portLabel = new Label(networkGroup, SWT.NONE);
		portLabel.setText("Port:");

		portText = new Text(networkGroup, SWT.BORDER);
		GridData gd_portText = new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1);
		gd_portText.widthHint = 50;
		portText.setLayoutData(gd_portText);
		portText.addVerifyListener(new PortVerifyListener());
		portText.setText(Integer.toString(PropertyManager.getListeningPort()));

		enableUpnpPortMappingButton = new Button(networkGroup, SWT.CHECK);
		enableUpnpPortMappingButton.setLayoutData(new GridData(SWT.LEFT,
				SWT.CENTER, false, false, 2, 1));
		enableUpnpPortMappingButton.setText("Enable UPnP port mapping");
		enableUpnpPortMappingButton.setSelection(PropertyManager.isUsingUpnp());

		enableNatpmpPortMappingButton = new Button(networkGroup, SWT.CHECK);
		enableNatpmpPortMappingButton.setLayoutData(new GridData(SWT.LEFT,
				SWT.CENTER, false, false, 2, 1));
		enableNatpmpPortMappingButton.setText("Enable NAT-PMP port mapping");
		enableNatpmpPortMappingButton.setSelection(PropertyManager.isUsingNatPmp());
	}
	
	private void createCompressionGroup(Composite container) {
		Group compressionGroup = new Group(container, SWT.NONE);
		compressionGroup.setText("Compression");
		compressionGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		compressionGroup.setLayout(new GridLayout(3, false));

		enableCompressionButton = new Button(compressionGroup, SWT.CHECK);
		enableCompressionButton.setText("Enable");

		compressionRateLabel = new Label(compressionGroup, SWT.NONE);
		compressionRateLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				true, false, 1, 1));
		compressionRateLabel.setText("Rate:");

		compressionRateComboViewer = new ComboViewer(compressionGroup, SWT.NONE);
		compressionRateComboViewer.setLabelProvider(new CompressionRateLabelProvider());
		compressionRateComboViewer.setContentProvider(new ArrayContentProvider());
		compressionRateComboViewer.setInput(new Integer[] { 1, 5, 9 });
		compressionRateCombo = compressionRateComboViewer.getCombo();
		compressionRateCombo.select(1);
	}
	
	private void createEncryptionGroup(Composite container) {
		encryptionGroup = new Group(container, SWT.NONE);
		encryptionGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		encryptionGroup.setText("Encryption");
		encryptionGroup.setLayout(new GridLayout(4, false));

		enableEncryptionButton = new Button(encryptionGroup, SWT.CHECK);
		enableEncryptionButton.setText("Enable");

		encryptionKeyLabel = new Label(encryptionGroup, SWT.NONE);
		encryptionKeyLabel.setLayoutData(new GridData(SWT.RIGHT,
				SWT.CENTER, true, false, 1, 1));
		encryptionKeyLabel.setText("Encryption key:");

		encryptionKeyText = new Text(encryptionGroup, SWT.BORDER | SWT.PASSWORD);
		GridData gd_encryptionKeyText = new GridData(SWT.RIGHT,
				SWT.CENTER, false, false, 1, 1);
		gd_encryptionKeyText.widthHint = 150;
		encryptionKeyText.setLayoutData(gd_encryptionKeyText);
		encryptionKeyText
				.addVerifyListener(new EncryptionKeyVerifyListener());
		encryptionKeyText
				.addModifyListener(new EncryptionKeyModifyListener());

		encryptionKeyStrengthText = new Text(encryptionGroup, SWT.BORDER
				| SWT.READ_ONLY | SWT.CENTER);
		GridData gd_encryptionKeyStrengthText = new GridData(SWT.RIGHT,
				SWT.CENTER, false, false, 1, 1);
		gd_encryptionKeyStrengthText.widthHint = 100;
		encryptionKeyStrengthText
				.setLayoutData(gd_encryptionKeyStrengthText);
	}

	private void updateEncryptionKeyStrengthText() {
		if (!enableEncryptionButton.getSelection()) {
			encryptionKeyStrengthText.setText("");
			return;
		}
		Strength encryptionKeyStrength = PasswordUtils
				.checkStrength(encryptionKeyText.getText());
	
		Color textColor;
		switch (encryptionKeyStrength) {
		case EXCEPTIONAL:
			textColor = new Color(Display.getCurrent(), 0, 0, 255);
			break;
		case VERY_STRONG:
			textColor = new Color(Display.getCurrent(), 0, 85, 255);
			break;
		case STRONG:
			textColor = new Color(Display.getCurrent(), 0, 170, 255);
			break;
		case MODERATE:
			textColor = new Color(Display.getCurrent(), 127, 170, 127);
			break;
		case WEAK:
			textColor = new Color(Display.getCurrent(), 255, 170, 0);
			break;
		case VERY_WEAK:
			textColor = new Color(Display.getCurrent(), 255, 85, 0);
			break;
		case INADEQUATE:
			textColor = new Color(Display.getCurrent(), 255, 0, 0);
			break;
		default:
			textColor = null;
			// TODO log error
			break;
		}
		encryptionKeyStrengthText.setForeground(textColor);
		encryptionKeyStrengthText.setText(encryptionKeyStrength.toString());
	}

	private class PortVerifyListener implements VerifyListener {

		private final ToolTip balloon = new ToolTip(getShell(), SWT.BALLOON
				| SWT.ICON_ERROR);

		public PortVerifyListener() {
			balloon.setMessage("Valid ports range from 0 to 65535");
		}

		@Override
		public void verifyText(VerifyEvent event) {
			event.doit = false;
			boolean valid = false;

			String newText = portText.getText().substring(0, event.start)
					+ event.text + portText.getText().substring(event.end);

			if (newText.equals("")) {
				// Let user empty the field, but don't accept it as valid
				event.doit = true;
			} else {
				try {
					int portNumber = Integer.parseInt(newText);
					if (0 <= portNumber && portNumber <= 65535) {
						event.doit = true;
						valid = true;
					}
				} catch (NumberFormatException e) {
				}
			}

			balloon.setLocation(portText.toDisplay(portText.getSize()));
			balloon.setVisible(!valid);
		}
	}

	private class EncryptionKeyVerifyListener implements VerifyListener {

		private final ToolTip balloon = new ToolTip(getShell(), SWT.BALLOON
				| SWT.ICON_ERROR);

		public EncryptionKeyVerifyListener() {
			balloon.setMessage("Invalid character input");
		}

		@Override
		public void verifyText(VerifyEvent event) {
			String newText = encryptionKeyText.getText().substring(0,
					event.start)
					+ event.text
					+ encryptionKeyText.getText().substring(event.end);

			event.doit = PasswordUtils.checkValidity(newText);

			balloon.setLocation(encryptionKeyText
					.toDisplay(encryptionKeyText.getSize()));
			balloon.setVisible(!event.doit);
		}
	}

	private class EncryptionKeyModifyListener implements ModifyListener {

		@Override
		public void modifyText(ModifyEvent event) {
			updateEncryptionKeyStrengthText();
		}
	}
}
