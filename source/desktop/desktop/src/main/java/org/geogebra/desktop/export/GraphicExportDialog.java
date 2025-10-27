/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.export;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.AbstractVectorGraphicsIO;
import org.freehep.graphicsio.FontConstants;
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.freehep.graphicsio.emf.EMFPlusGraphics2D;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.freehep.graphicsio.svg.SVGGraphics2D;
import org.freehep.util.UserProperties;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.euclidian.EuclidianViewD;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.gui.dialog.Dialog;
import org.geogebra.desktop.gui.util.FileTransferable;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.FontManagerD;
import org.geogebra.desktop.main.GeoGebraPreferencesD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.UtilD;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * @author Markus Hohenwarter
 */
public class GraphicExportDialog extends Dialog implements KeyListener {

	// #4979
	private static final int EXTRA_MARGIN = 4;

	private static final long serialVersionUID = 1L;

	private static final String CREATOR = "GeoGebra / FreeHEP Graphics2D Driver";

	private final AppD app;
	@SuppressWarnings("rawtypes")
	private JComboBox cbFormat;

	/**
	 * Combobox for DPI
	 */
	@SuppressWarnings("rawtypes")
	JComboBox cbDPI;
	private JLabel sizeLabel;

	private double exportScale;
	private int pixelWidth;
	private int pixelHeight;
	private double cmWidth;
	private double cmHeight;
	private final NumberFormat sizeLabelFormat;

	/** convert text to shapes (eps, pdf, svg) */
	boolean textAsShapes = true;
	/** true for transparent images (png) */
	boolean transparent = true;
	/** set true if Braille font installed */
	boolean braille = false;
	/** whether EMF+ is used or EMF */
	boolean EMFPlus = true;

	private enum Format {
		PNG, PDF, SVG, EMF
	}

	private final EuclidianViewD specifiedEuclidianView;

	/** print scale or pixel size settings */
	PrintScalePanel psp;

	private final LocalizationD loc;

	/**
	 * Creates a dialog for exporting an image of the active EuclidianView
	 * 
	 * @param app
	 *            application
	 */
	public GraphicExportDialog(AppD app) {
		this(app, null);

	}

	/**
	 * Creates a dialog for exporting an image of the EuclidianView given as a
	 * parameter.
	 * 
	 * @param app
	 *            application
	 * @param specifiedEuclidianView
	 *            EV
	 */
	public GraphicExportDialog(AppD app,
			EuclidianViewD specifiedEuclidianView) {
		super(app.getFrame(), false);
		this.app = app;
		this.loc = app.getLocalization();
		this.specifiedEuclidianView = specifiedEuclidianView;

		sizeLabelFormat = NumberFormat.getInstance(Locale.ENGLISH);
		sizeLabelFormat.setGroupingUsed(false);
		sizeLabelFormat.setMaximumFractionDigits(2);

		checkBrailleFont();

		initGUI();
	}

	private Font brailleFont = null;

	/**
	 * check if a Braille font is installed
	 */
	private void checkBrailleFont() {
		final String preferredBrailleFont = "IDV ComputerBraille (ANSI)";

		GraphicsEnvironment e = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		Font[] fonts = e.getAllFonts(); // Get the fonts

		// check preferred font first
		for (Font f : fonts) {
			if (f.getFontName().equals(preferredBrailleFont)) {
				Log.debug("found Braille font: " + f.getFontName());
				brailleFont = f;
				braille = true;
				return;
			}
		}

		// preferred font not found, check for any that contain "braille"
		for (Font f : fonts) {
			if (StringUtil.toLowerCaseUS(f.getFontName()).contains("braille")) {
				Log.debug("found Braille font: " + f.getFontName());
				brailleFont = f;
				braille = true;
				return;
			}
		}

	}

	private EuclidianViewInterfaceD getEuclidianView() {
		if (specifiedEuclidianView != null) {
			return specifiedEuclidianView;
		}
		return (EuclidianViewInterfaceD) app.getActiveEuclidianView();
	}

	@Override
	public void setVisible(boolean flag) {
		if (flag) {
			loadPreferences();
			super.setVisible(true);
		} else {
			savePreferences();
			super.setVisible(false);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initGUI() {
		setResizable(false);
		setTitle(loc.getMenu("ExportAsPicture"));
		JPanel cp = new JPanel(new BorderLayout(5, 5));
		cp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(cp);

		// format list
		JPanel formatPanel = new JPanel(new FlowLayout(5));
		String[] formats;

		if (getEuclidianView().isEuclidianView3D()) {
			formats = new String[] {
					loc.getMenu("png") + " (" + FileExtensions.PNG + ")" };
		} else {
			formats = new String[] {
					loc.getMenu("png") + " (" + FileExtensions.PNG + ")",
					loc.getMenu("pdf"),
					loc.getMenu("svg") + " (" + FileExtensions.SVG + ")",
					loc.getMenu("emf") + " (" + FileExtensions.EMF + ")" };
		}

		cbFormat = new JComboBox(formats);
		formatPanel.add(new JLabel(loc.getMenu("Format") + ":"));
		formatPanel.add(cbFormat);
		cp.add(formatPanel, BorderLayout.NORTH);

		// panel with fields to enter
		// scale of image, dpi and
		// width and height of picture
		final JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		// scale
		EuclidianView ev = (EuclidianView) getEuclidianView();

		psp = new PrintScalePanel(app, ev);
		psp.addActionListener(e -> updateSizeLabel());
		p.add(psp);

		// dpi combo box
		final JPanel dpiPanel = new JPanel(new FlowLayout(5));

		String[] dpiStr = { "72", "96", "150", "300", "600" };
		cbDPI = new JComboBox(dpiStr);
		cbDPI.setSelectedItem("300");
		final JLabel resolutionInDPILabel = new JLabel(
				loc.getMenu("ResolutionInDPI") + ":");
		final JCheckBox cbTransparent = new JCheckBox(
				loc.getMenu("Transparent"), transparent);
		final JCheckBox cbBraille = new JCheckBox(loc.getMenu("Braille"),
				braille);
		final JCheckBox cbEMFPlus = new JCheckBox("EMF+", EMFPlus);

		final JCheckBox textAsShapesCB = new JCheckBox(
				loc.getMenu("ExportTextAsShapes"), textAsShapes);

		// make sure panel is wide enough
		if (selectedFormat() == Format.PNG) {
			dpiPanel.add(resolutionInDPILabel);
			dpiPanel.add(cbDPI);
			if (brailleFont != null) {
				// GGB-766
				dpiPanel.add(cbBraille);
			}

			dpiPanel.add(cbTransparent);

		} else if (selectedFormat() == Format.SVG) {
			dpiPanel.add(cbTransparent);
		} else if (selectedFormat() == Format.EMF) {
			dpiPanel.add(cbEMFPlus);
		} else {
			dpiPanel.add(textAsShapesCB);
		}

		p.add(dpiPanel);
		cbDPI.addActionListener(ae -> updateSizeLabel());

		cbTransparent.addActionListener(arg0 -> transparent = cbTransparent.isSelected());

		cbBraille.addActionListener(arg0 -> braille = cbBraille.isSelected());

		cbEMFPlus.addActionListener(arg0 -> EMFPlus = cbEMFPlus.isSelected());

		textAsShapesCB.addActionListener(arg0 -> textAsShapes = textAsShapesCB.isSelected());

		cbFormat.addActionListener(arg0 -> {
			switch (selectedFormat()) {
			case SVG:
				dpiPanel.remove(resolutionInDPILabel);
				dpiPanel.remove(cbDPI);
				dpiPanel.remove(cbEMFPlus);
				dpiPanel.add(cbTransparent);
				dpiPanel.add(textAsShapesCB);
				psp.enableAbsoluteSize(true);
				break;
			case PDF:
				dpiPanel.remove(resolutionInDPILabel);
				dpiPanel.remove(cbDPI);
				dpiPanel.remove(cbEMFPlus);
				dpiPanel.remove(cbTransparent);
				dpiPanel.remove(cbBraille);
				dpiPanel.add(textAsShapesCB);
				textAsShapesCB.setSelected(true);
				psp.enableAbsoluteSize(false);
				break;
			case EMF:
				dpiPanel.add(cbEMFPlus);
				dpiPanel.remove(resolutionInDPILabel);
				dpiPanel.remove(cbDPI);
				dpiPanel.remove(cbTransparent);
				dpiPanel.remove(textAsShapesCB);
				dpiPanel.remove(cbBraille);
				psp.enableAbsoluteSize(false);
				break;
			default: // PNG
				dpiPanel.add(resolutionInDPILabel);
				dpiPanel.add(cbDPI);
				dpiPanel.remove(cbEMFPlus);
				dpiPanel.add(cbTransparent);
				if (braille) {
					// GGB-766
					dpiPanel.add(cbBraille);
				}
				dpiPanel.remove(textAsShapesCB);
				cbDPI.setSelectedItem("300");
				cbDPI.setEnabled(true);
				psp.enableAbsoluteSize(true);
				break;
			}
			updateSizeLabel();
			SwingUtilities.updateComponentTreeUI(p);
		});

		// width and height of picture
		JPanel sizePanel = new JPanel(new FlowLayout(5));
		sizePanel.add(new JLabel(loc.getMenu("Size") + ":"));
		sizeLabel = new JLabel();
		sizePanel.add(sizeLabel);
		p.add(sizePanel);
		cp.add(p, BorderLayout.CENTER);

		// Cancel and Export Button
		JButton cancelButton = new JButton(loc.getMenu("Cancel"));
		cancelButton.addActionListener(e -> setVisible(false));
		JButton exportButton = new JButton(loc.getMenu("Save"));
		exportButton.addActionListener(e -> {
			Thread runner = new Thread(() -> {
				setVisible(false);

				doExport(false);
			});
			runner.start();
		});

		JButton exportClipboardButton = new JButton(loc.getMenu("Clipboard"));
		exportClipboardButton.addActionListener(e -> {
			Thread runner = new Thread(() -> {
				setVisible(false);

				doExport(true);
			});
			runner.start();
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(exportButton);
		/*
		 * This is done via copying a file to the clipboard. This is not
		 * supported on Mac.
		 * Toolkit.getDefaultToolkit().getSystemClipboard().setContents
		 * (geogebra.gui.util.ImageSelection, null) would be OK (via
		 * AppD.simpleExportToClipboard(EuclidianView)), but this code here
		 * wants to use
		 * Toolkit.getDefaultToolkit().getSystemClipboard().setContents
		 * (FileTransferable, null) via sendToClipboard(File). So we simply not
		 * show the "Clipboard" button here on Mac.
		 */
		if (!app.isMacOS()) {
			buttonPanel.add(exportClipboardButton);
		}
		buttonPanel.add(cancelButton);
		cp.add(buttonPanel, BorderLayout.SOUTH);

		UtilD.addKeyListenerToAll(this, this);

		updateSizeLabel();
		setPreferredSize(new Dimension(
				getPreferredSize().width + app.getFontSize() * EXTRA_MARGIN,
				getPreferredSize().height + app.getFontSize() * EXTRA_MARGIN));
		centerOnScreen();

	}

	/**
	 * Creates the picture of the correct format
	 * 
	 * @param toClipboard
	 *            whether output is a file or clipboard
	 */
	void doExport(boolean toClipboard) {
		Format index = selectedFormat();
		switch (index) {
		case PNG:

			FontManagerD fm = app.getFontManager();
			int fontSize = fm.getFontSize();
			File pngDestination = toClipboard ? getTmpPNG()
					: getPNGDestination();
			if (pngDestination != null) {
				if (braille) {
					// GGB-766
					fm.updateDefaultFonts(fontSize, brailleFont.getFontName(),
							brailleFont.getFontName());

					getEuclidianView().updateFonts();
				}

				exportPNGSilent(pngDestination, toClipboard, transparent,
						getDPI(), exportScale, app, getEuclidianView(),
						braille ? ExportType.PNG_BRAILLE : ExportType.PNG);

				if (braille) {
					// GGB-766
					fm.updateDefaultFonts(fontSize, "SansSerif", "Serif");

					getEuclidianView().updateFonts();
				}
			}
			break;

		case EMF: // EMF
			exportEMF(toClipboard, EMFPlus);
			break;

		case PDF: // PDF
			exportPDF(toClipboard);
			break;

		case SVG: // SVG
			exportSVG(toClipboard);
			break;

		}
	}

	private int getDPI() {
		if (selectedFormat() == Format.SVG) {
			return 72;
		}

		return Integer.parseInt((String) cbDPI.getSelectedItem());
	}

	private void loadPreferences() {
		try {
			// format
			Format formatID = Format.PNG;
			String format = GeoGebraPreferencesD.getPref().loadPreference(
					GeoGebraPreferencesD.EXPORT_PIC_FORMAT, "png");
			if ("svg".equals(format)) {
				formatID = Format.SVG;
			}
			if (formatID.ordinal() < cbFormat.getItemCount()) {
				cbFormat.setSelectedIndex(formatID.ordinal());
			}

			// dpi
			if (cbDPI.isEnabled()) {
				String strDPI = GeoGebraPreferencesD.getPref().loadPreference(
						GeoGebraPreferencesD.EXPORT_PIC_DPI, "300");
				for (int i = 0; i < cbDPI.getItemCount(); i++) {
					String dpi = cbDPI.getItemAt(i).toString();
					if (dpi.equals(strDPI)) {
						cbDPI.setSelectedIndex(i);
					}
				}
			}

			/*
			 * // scale in cm double scale =
			 * Double.parseDouble(GeoGebraPreferences.loadPreference(
			 * GeoGebraPreferences.EXPORT_PIC_SCALE, "1"));
			 * app.getEuclidianView().setPrintingScale(scale);
			 */

			updateSizeLabel();
		} catch (Exception e) {
			Log.debug(e);
		}
	}

	private void savePreferences() {
		// dpi
		GeoGebraPreferencesD.getPref().savePreference(
				GeoGebraPreferencesD.EXPORT_PIC_DPI,
				cbDPI.getSelectedItem().toString());

		// format
		String format;
		if (selectedFormat() == Format.SVG) {
			format = "svg";
		} else {
			format = "png";
		}
		GeoGebraPreferencesD.getPref()
				.savePreference(GeoGebraPreferencesD.EXPORT_PIC_FORMAT, format);

		/*
		 * // scale in cm
		 * GeoGebraPreferences.savePreference(GeoGebraPreferences.
		 * EXPORT_PIC_SCALE,
		 * Double.toString(app.getEuclidianView().getPrintingScale()));
		 */
	}

	/**
	 * Update the pixel size
	 */
	void updateSizeLabel() {
		EuclidianView ev = (EuclidianView) getEuclidianView();
		double printingScale = ev.getPrintingScale();
		// takes dpi into account (note: eps has 72dpi)

		StringBuilder sb = new StringBuilder();
		switch (psp.getMode()) {
		case SIZEINPX:
			pixelWidth = psp.getPixelWidth();
			pixelHeight = psp.getPixelHeight();
			cmWidth = (pixelWidth * 2.54) / getDPI();
			cmHeight = (pixelHeight * 2.54) / getDPI();
			exportScale = pixelWidth / ((double) ev.getExportWidth());
			break;

		case FIXED_SIZE:

			// what the user typed in the "100 screen pixels = x cm" textfield
			double screenPixels = DoubleUtil.checkDecimalFraction(
					100 * ev.getPrintingScale() / ev.getXscale());

			// double screenPixelsY = 100 * ev.getPrintingScale() /
			// ev.getYscale();

			cmWidth = ev.getExportWidth() / 100.0 * screenPixels;
			// not screenPixelsY
			// eg
			// https://help.geogebra.org/topic/picture-export-adds-huge-margin-when-axes-ratio-not-1-1
			cmHeight = ev.getExportHeight() / 100.0 * screenPixels;

			pixelWidth = (int) (cmWidth / 2.54 * getDPI());
			pixelHeight = (int) (cmHeight / 2.54 * getDPI());

			exportScale = pixelWidth / ((double) ev.getExportWidth());

			break;

		case SIZEINCM:
			exportScale = (printingScale * getDPI()) / 2.54 / ev.getXscale();
			// cm size
			cmWidth = printingScale * (ev.getExportWidth() / ev.getXscale());

			// getXscale() is not a typo, see #2894
			// #4185 changed back to getYscale()
			// * ev.getYscale() / ev.getXscale() added for when x:y ratio is not
			// 1:1
			// https://help.geogebra.org/topic/picture-export-adds-huge-margin-when-axes-ratio-not-1-1
			cmHeight = printingScale * (ev.getExportHeight() / ev.getYscale())
					* ev.getYscale() / ev.getXscale();

			pixelWidth = (int) Math.floor(ev.getExportWidth() * exportScale);
			pixelHeight = (int) Math.floor(ev.getExportHeight() * exportScale);
			break;
		}

		sb.append(sizeLabelFormat.format(cmWidth));
		sb.append(" cm ");
		sb.append(Unicode.MULTIPLY);
		sb.append(' ');
		sb.append(sizeLabelFormat.format(cmHeight));
		sb.append(" cm");

		Format index = selectedFormat();
		if (index == Format.PNG || index == Format.SVG) {
			// pixel size
			sb.append(", ");
			sb.append(pixelWidth);
			sb.append(' ');
			sb.append(Unicode.MULTIPLY);
			sb.append(' ');
			sb.append(pixelHeight);
			sb.append(" pixels");
			sb.append(Unicode.SUPERSCRIPT_2);
		}

		sizeLabel.setText(sb.toString());
	}

	/**
	 * @return currently selected format
	 */
	Format selectedFormat() {
		return Format.values()[cbFormat.getSelectedIndex()];
	}

	private void centerOnScreen() {
		// center on screen
		pack();
		setLocationRelativeTo(app.getMainComponent());
	}

	/**
	 * Exports drawing as emf
	 */
	private void exportEMF(boolean exportToClipboard,
			boolean useEMFplus) {

		File file;
		if (exportToClipboard) {
			String tempDir = UtilD.getTempDir();
			file = new File(tempDir + "geogebra.emf");
		} else {
			file = app.getGuiManager().showSaveDialog(FileExtensions.EMF, null,
					loc.getMenu("emf") + " " + loc.getMenu("Files"), true,
					false);

		}

		if (file == null) {
			return;
		}
		try {
			exportEMF((EuclidianViewD) getEuclidianView(), file,
					useEMFplus, pixelWidth, pixelHeight, exportScale);

			if (exportToClipboard) {
				sendToClipboard(file);
			}

		} catch (RuntimeException | Error ex) {
			app.showError(Errors.SaveFileFailed);
			Log.debug(ex.toString());
		}
	}

	/**
	 * Exports drawing as pdf
	 */
	private void exportPDF(boolean exportToClipboard) {

		File file;
		String tempDir = UtilD.getTempDir();
		if (exportToClipboard) {
			file = new File(tempDir + "geogebra.pdf");
		} else {

			file = app.getGuiManager().showSaveDialog(FileExtensions.PDF, null,
					loc.getMenu("pdf") + " " + loc.getMenu("Files"), true,
					false);
		}

		if (file == null) {
			return;
		}
		try {

			exportPDF((EuclidianViewD) getEuclidianView(), file,
					textAsShapes, pixelWidth, pixelHeight, exportScale);

			if (exportToClipboard) {
				sendToClipboard(file);
			}

		} catch (RuntimeException | Error ex) {
			Log.debug(ex);
			app.showError(Errors.SaveFileFailed);

		}
	}

	/**
	 * Exports drawing as SVG
	 */
	private void exportSVG(boolean exportToClipboard) {

		EuclidianView ev = (EuclidianView) getEuclidianView();

		File file;
		String tempDir = UtilD.getTempDir();
		if (exportToClipboard) {
			file = new File(tempDir + "geogebra.svg");
		} else {
			file = app.getGuiManager().showSaveDialog(FileExtensions.SVG, null,
					loc.getMenu("svg") + " " + loc.getMenu("Files"), true,
					false);
		}

		if (file == null) {
			return;
		}

		try {
			exportSVG(app, ev, file, textAsShapes, pixelWidth,
					pixelHeight, cmWidth, cmHeight, exportScale, transparent);

			if (exportToClipboard) {
				// note this *doesn't* copy as text
				// so ctrl-V in notepad won't show anything
				sendToClipboard(file);
			}
		} catch (Throwable t) {
			app.showError(Errors.SaveFileFailed);
			Log.trace(t.getMessage());
		}
	}

	/**
	 * Exports drawing as png with given resolution in dpi
	 */
	public static void exportPNGClipboard(boolean transparent0,
			int dpi, double exportScale0, AppD app,
			EuclidianViewInterfaceD ev) {
		File file = getTmpPNG();

		exportPNGSilent(file, true, transparent0, dpi, exportScale0, app,
				ev, ExportType.PNG);
	}

	private static File getTmpPNG() {
		String tempDir = UtilD.getTempDir();
		return new File(tempDir + "geogebra.png");
	}

	private File getPNGDestination() {

		return app.getGuiManager().showSaveDialog(FileExtensions.PNG, null,
				loc.getMenu("png") + " " + loc.getMenu("Files"), true, false);
	}

	private static void exportPNGSilent(File file, boolean exportToClipboard,
			boolean transparent0, int dpi, double exportScale0, AppD app,
			EuclidianViewInterfaceD ev, ExportType exportType) {
		if (file == null) {
			return;
		}

		try {
			// draw graphics view into image
			// EuclidianViewInterfaceD ev = getEuclidianView();

			exportPNG(ev, file, transparent0, dpi, exportScale0,
					exportToClipboard, exportType);

		} catch (RuntimeException | Error ex) {
			app.showError(Errors.SaveFileFailed);
			Log.debug(ex);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_ESCAPE) {
			setVisible(false);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//
	}

	/**
	 * @param file file to be copied
	 */
	public static void sendToClipboard(File file) {
		FileTransferable ft = new FileTransferable(file);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, null);
	}

	/**
	 * 
	 * @param app
	 *            application
	 * @param ev
	 *            view
	 * @param file
	 *            target file
	 * @param textAsShapes
	 *            whether to convert text to curves
	 * @param pixelWidth
	 *            width in pixels
	 * @param pixelHeight
	 *            height in pixels
	 * @param cmWidth
	 *            width in cm
	 * @param cmHeight
	 *            height in cm
	 * @param exportScale
	 *            scale units / cm
	 * @param transparent0
	 *            transparent?
	 */
	public static void exportSVG(App app, EuclidianView ev, File file,
			boolean textAsShapes, int pixelWidth, int pixelHeight,
			double cmWidth, double cmHeight, double exportScale,
			boolean transparent0) {
		try {
			exportSVG(app, ev, new FileOutputStream(file), textAsShapes,
					pixelWidth, pixelHeight, cmWidth, cmHeight, exportScale,
					transparent0);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.debug(e);
		}
	}

	/**
	 * 
	 * @param app
	 *            application
	 * @param ev
	 *            view
	 * @param file
	 *            target file
	 * @param textAsShapes
	 *            whether to convert text to curves
	 * @param pixelWidth
	 *            width in pixels
	 * @param pixelHeight
	 *            height in pixels
	 * @param cmWidth
	 *            width in cm
	 * @param cmHeight
	 *            height in cm
	 * @param exportScale
	 *            scale units / cm
	 * @param transparent0
	 *            transparent?
	 */
	public static void exportSVG(App app, EuclidianView ev, OutputStream file,
			boolean textAsShapes, int pixelWidth, int pixelHeight,
			double cmWidth, double cmHeight, double exportScale,
			boolean transparent0) {
		UserProperties props = (UserProperties) SVGGraphics2D
				.getDefaultProperties();
		props.setProperty(SVGGraphics2D.EMBED_FONTS, !textAsShapes);
		props.setProperty(AbstractVectorGraphicsIO.TEXT_AS_SHAPES,
				textAsShapes);
		SVGGraphics2D.setDefaultProperties(props);

		// added SVGExtensions to support grouped objects in layers
		SVGExtensions g;
		try {
			g = new SVGExtensions(file,
					new Dimension((int) (pixelWidth / exportScale),
							(int) (pixelHeight / exportScale)),
					cmWidth, cmHeight);
			g.setCreator(CREATOR);

			// make sure LaTeX exported at hi res
			app.setExporting(ExportType.SVG, exportScale);

			g.startExport();
			// export scale = 1 (change for SVG export in cm)
			// and avoid lots of these in the file
			// <g
			// transform="matrix(.56693, 0.0000, 0.0000, .56693, 0.0000,
			// 0.0000)">
			GGraphics2D expGraphics = new GGraphics2DD(g);
			ev.exportPaintPre(expGraphics, 1, transparent0);

			g.startGroup("misc");
			ev.drawActionObjects(expGraphics);
			g.endGroup("misc");

			int currentLayer = 0;

			g.startGroup("layer" + currentLayer);
			for (Drawable d : ev.getAllDrawableList()) {
				if (d.getGeoElement().getLayer() != currentLayer) {
					g.endGroup("layer" + currentLayer);
					currentLayer = d.getGeoElement().getLayer();
					g.startGroup("layer" + currentLayer);
				}

				g.draw(d, new GGraphics2DD(g));
			}
			g.endGroup("layer" + currentLayer);

			g.endExport();
			expGraphics.resetClip();
		} catch (IOException e) {
			Log.debug(e);
		} finally {
			app.setExporting(ExportType.NONE, 1);
		}
	}

	/**
	 * 
	 * @param ev
	 *            view
	 * @param file
	 *            target file
	 * @param useEMFplus
	 *            whether to use EMF+
	 * @param pixelWidth
	 *            width in pixels
	 * @param pixelHeight
	 *            height in pixels
	 * @param exportScale
	 *            scale units / cm
	 */
	public static void exportEMF(EuclidianViewD ev, File file,
			boolean useEMFplus, int pixelWidth, int pixelHeight,
			double exportScale) {

		VectorGraphics g;
		try {
			if (useEMFplus) {
				g = new EMFPlusGraphics2D(file,
						new Dimension(pixelWidth, pixelHeight));

			} else {
				g = new EMFGraphics2D(file,
						new Dimension(pixelWidth, pixelHeight));
			}

			g.setCreator(CREATOR);

			// fix problem with aspect ratio in eg MS Word
			// https://community.oracle.com/thread/1264130
			g.setDeviceIndependent(true);

			g.startExport();
			ev.exportPaint(g, exportScale, ExportType.EMF);

			g.endExport();

		} catch (FileNotFoundException e) {
			Log.debug(e);
		}

	}

	/**
	 * 
	 * @param view
	 *            view
	 * @param file
	 *            target file
	 * @param textAsShapes
	 *            whether to convert text to curves
	 * @param pixelWidth
	 *            width in pixels
	 * @param pixelHeight
	 *            height in pixels
	 * @param exportScale
	 *            scale units / cm
	 */
	public static void exportPDF(EuclidianView view, File file,
			boolean textAsShapes, int pixelWidth, int pixelHeight,
			double exportScale) {

		ImageIO.scanForPlugins();
		// export text as shapes or plaintext
		// shapes: better representation
		// text: smaller file size, but some unicode symbols don't export eg
		// Upsilon
		UserProperties props = (UserProperties) PDFGraphics2D
				.getDefaultProperties();

		// #TRAC-5292
		props.setProperty(PDFGraphics2D.EMBED_FONTS, !textAsShapes);
		props.setProperty(PDFGraphics2D.EMBED_FONTS_AS,
				FontConstants.EMBED_FONTS_TYPE1);
		props.setProperty(AbstractVectorGraphicsIO.TEXT_AS_SHAPES,
				textAsShapes);
		PDFGraphics2D.setDefaultProperties(props);

		PDFGraphics2D g;
		try {

			double printingScale = view.getPrintingScale();

			// TODO: why do we need this to make correct size in cm?
			double factor = view.getXscale() * 2.54 / 72;

			Dimension size = new Dimension(
					(int) (view.getExportWidth() * printingScale / factor),
					(int) (view.getExportHeight() * printingScale / factor));

			g = new PDFGraphics2D(file, size);
			g.setCreator(CREATOR);

			// g.
			//
			// PDFRedundanceTracker tracker = new PDFRedundanceTracker(pdf);
			// Font font = new Font("Impact", Font.PLAIN, 1000);
			// FontRenderContext context = new FontRenderContext(null, true,
			// true);
			// FontEmbedder fe = PDFFontEmbedderType1.create(context, pdf,
			// "MyFont", tracker);
			// fe.includeFont(font, Lookup.getInstance().getTable("PDFLatin"),
			// "F1");

			g.setPageSize(size);

			g.startExport();
			((EuclidianViewD) view).exportPaint(g, printingScale / factor, textAsShapes
					? ExportType.PDF_TEXTASSHAPES : ExportType.PDF_EMBEDFONTS);
			g.endExport();
		} catch (FileNotFoundException e) {
			Log.debug(e);
		}
	}

	/**
	 * 
	 * @param ev
	 *            view
	 * @param file
	 *            target file
	 * @param transparent
	 *            whether result should be transparent
	 * @param dpi
	 *            dpi
	 * @param exportScale
	 *            scale units / cm
	 * @param exportToClipboard
	 *            says if exports to clipboard
	 * 
	 */
	public static void exportPNG(EuclidianViewInterfaceD ev, File file,
			boolean transparent, int dpi, double exportScale,
			boolean exportToClipboard, ExportType exportType) {

		ev.exportImagePNG(exportScale, transparent, dpi, file,
				exportToClipboard, exportType);
	}

	/**
	 * @param extension file extension
	 * @param ev view
	 * @param file output file
	 * @param transparent use alpha?
	 * @param dpi DPI
	 * @param exportScale scale
	 * @param textAsShapes convert font to shapes?
	 * @param useEMFplus whether to use EMF plus
	 * @param pixelWidth width
	 * @param pixelHeight height
	 * @param app application
	 */
	public static void export(String extension, EuclidianViewInterfaceD ev,
			File file, boolean transparent, int dpi, double exportScale,
			boolean textAsShapes, boolean useEMFplus, int pixelWidth,
			int pixelHeight, AppD app) {
		if ("png".equals(extension)) {
			// can be 3D View
			GraphicExportDialog.exportPNG(ev, file, transparent, dpi,
					exportScale, false, ExportType.PNG);

		} else if ("pdf".equals(extension)) {
			GraphicExportDialog.exportPDF((EuclidianViewD) ev, file,
					textAsShapes, pixelWidth, pixelHeight, exportScale);

		} else if ("emf".equals(extension)) {
			GraphicExportDialog.exportEMF((EuclidianViewD) ev, file,
					useEMFplus, pixelWidth, pixelHeight, exportScale);

		} else if ("svg".equals(extension)) {
			GraphicExportDialog.exportSVG(app, (EuclidianViewD) ev, file,
					textAsShapes, pixelWidth, pixelHeight, -1, -1, exportScale,
					transparent);
		}
	}

}
