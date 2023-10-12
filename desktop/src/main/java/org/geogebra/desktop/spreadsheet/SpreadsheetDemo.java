package org.geogebra.desktop.spreadsheet;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.stream.IntStream;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.OverlayLayout;
import javax.swing.border.BevelBorder;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.spreadsheet.core.ClipboardInterface;
import org.geogebra.common.spreadsheet.core.Modifiers;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellEditor;
import org.geogebra.common.spreadsheet.core.SpreadsheetControlsDelegate;
import org.geogebra.common.spreadsheet.kernel.GeoElementCellRendererFactory;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.common.spreadsheet.kernel.SpreadsheetEditorListener;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.factories.AwtFactoryD;

import com.himamis.retex.editor.desktop.MathFieldD;
import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;

public class SpreadsheetDemo {

	/**
	 * @param args commandline arguments
	 */
	public static void main(String[] args) {
		try {
			JFrame frame = new JFrame("spreadsheet");
			Dimension preferredSize = new Dimension(800, 600);
			frame.setPreferredSize(preferredSize);
			AppCommon appCommon = new AppCommon(new LocalizationCommon(3), new AwtFactoryD());
			KernelTabularDataAdapter adapter = new KernelTabularDataAdapter();
			Spreadsheet spreadsheet = new Spreadsheet(adapter,
					new GeoElementCellRendererFactory());

			FactoryProviderDesktop.setInstance(new FactoryProviderDesktop());
			spreadsheet.setWidthForColumns(60, IntStream.range(0, 10).toArray());
			spreadsheet.setHeightForRows(20, IntStream.range(0, 10).toArray());

			spreadsheet.setWidthForColumns(90, IntStream.range(2, 4).toArray());
			spreadsheet.setHeightForRows(40, IntStream.range(3, 5).toArray());
			SpreadsheetPanel spreadsheetPanel = new SpreadsheetPanel(spreadsheet, appCommon, frame);
			appCommon.getKernel().attach(adapter);
			appCommon.getGgbApi().evalCommand(String.join("\n", "C4=7", "C5=8",
					"A1=4", "B2=true", "B3=Button()", "B4=sqrt(x)"));

			spreadsheetPanel.setPreferredSize(preferredSize);
			initParentPanel(frame, spreadsheetPanel);
			spreadsheet.setViewport(spreadsheetPanel.getViewport());

			frame.setVisible(true);
			frame.setSize(preferredSize);
		} catch (Throwable t) {
			Log.debug(t);
		}
	}

	private static void initParentPanel(JFrame frame, SpreadsheetPanel sp) {
		JScrollBar vertical = new JScrollBar();
		JScrollBar horizontal = new JScrollBar(JScrollBar.HORIZONTAL);
		JPanel scrollPanel = new JPanel();
		scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		top.add(sp);
		top.add(vertical);
		scrollPanel.add(top);
		scrollPanel.add(horizontal);
		Container contentPane = frame.getContentPane();
		contentPane.setPreferredSize(new Dimension(800, 600));
		contentPane.setLayout(new OverlayLayout(contentPane));

		contentPane.add(scrollPanel);
		sp.editorOverlay = new JPanel();
		sp.editorOverlay.setPreferredSize(new Dimension(800, 600));
		sp.editorOverlay.setLayout(null);
		contentPane.add(sp.editorOverlay);
		sp.editorOverlay.add(sp.editorBox);

		vertical.addAdjustmentListener(evt -> {
			sp.scrollY = evt.getValue() * 10;
			sp.spreadsheet.setViewport(sp.getViewport());
			frame.repaint();
		});
		horizontal.addAdjustmentListener(evt -> {
			sp.scrollX = evt.getValue() * 10;
			sp.spreadsheet.setViewport(sp.getViewport());
			frame.repaint();
		});
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	private static class SpreadsheetPanel extends JPanel {
		private final Spreadsheet spreadsheet;
		private final MathFieldD mathField;
		private final Box editorBox = Box.createHorizontalBox();
		private final JPopupMenu contextMenu = new JPopupMenu();
		public JPanel editorOverlay;

		private int scrollX;
		private int scrollY;

		public SpreadsheetPanel(Spreadsheet spreadsheet, AppCommon app, JFrame frame) {
			this.spreadsheet = spreadsheet;
			this.mathField = new MathFieldD(new SyntaxAdapterImpl(app.getKernel()),
					editorBox::repaint);
			editorBox.setBorder(new BevelBorder(BevelBorder.RAISED));
			editorBox.add(mathField);
			mathField.setBounds(0, 0, 200, 200);
			mathField.requestViewFocus();
			editorBox.setAlignmentX(0);
			editorBox.setAlignmentY(0);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {
					spreadsheet.handlePointerUp(event.getX(), event.getY(),
							getModifiers(event));
					repaint();
				}

				@Override
				public void mousePressed(MouseEvent event) {
					spreadsheet.handlePointerDown(event.getX(), event.getY(),
							getModifiers(event));
					repaint();
				}
			});
			final SpreadsheetCellEditor editor = new DesktopSpreadsheetCellEditor(frame, app);
			spreadsheet.setControlsDelegate(new SpreadsheetControlsDelegate() {

				private ClipboardInterface clipboard = new ClipboardD();

				@Override
				public SpreadsheetCellEditor getCellEditor() {
					return editor;
				}

				@Override
				public void showContextMenu(Map<String, Runnable> actions, GPoint position) {
					contextMenu.show(editorOverlay, position.x, position.y);
					contextMenu.removeAll();
					for (Map.Entry<String, Runnable> action: actions.entrySet()) {
						JMenuItem btn = new JMenuItem(action.getKey());
						btn.setAction(new AbstractAction(action.getKey()) {
							@Override
							public void actionPerformed(ActionEvent e) {
								action.getValue().run();
							}
						});
						contextMenu.add(btn);
					}
					contextMenu.setVisible(true);
					frame.revalidate();
				}

				@Override
				public void hideCellEditor() {
					editorBox.setVisible(false);
				}

				@Override
				public void hideContextMenu() {
					contextMenu.setVisible(false);
				}

				@Override
				public ClipboardInterface getClipboard() {
					return clipboard;
				}
			});
		}

		private Modifiers getModifiers(MouseEvent event) {
			return new Modifiers(event.isAltDown(), event.isControlDown(),
					event.getButton() == 3);
		}

		public Rectangle getViewport() {
			return new Rectangle(scrollX, scrollX + 500, scrollY, scrollY + 400);
		}

		@Override
		public void paint(Graphics graphics) {
			super.paint(graphics);
			GGraphics2DD graphics1 = new GGraphics2DD((Graphics2D) graphics);
			spreadsheet.draw(graphics1);
		}

		private class DesktopSpreadsheetCellEditor implements SpreadsheetCellEditor {

			private final JFrame frame;
			private final AppCommon app;

			DesktopSpreadsheetCellEditor(JFrame frame, AppCommon app) {
				this.frame = frame;
				this.app = app;
			}

			@Override
			public void setBounds(Rectangle bounds) {
				if (!frame.getContentPane().isAncestorOf(editorBox)) {
					frame.getContentPane().add(editorBox);
				}
				editorBox.setBounds((int) bounds.getMinX(), (int) bounds.getMinY(),
						(int) bounds.getWidth(), (int) bounds.getHeight());
				mathField.setBounds(0, 0,
						(int) bounds.getWidth(), (int) bounds.getHeight());

				editorBox.setBackground(Color.BLUE);
				editorBox.setVisible(true);
				frame.revalidate();
				mathField.requestViewFocus();
			}

			@Override
			public void setTargetCell(int row, int column) {
				mathField.getInternal().setFieldListener(new SpreadsheetEditorListener(
						mathField.getInternal(), app.getKernel(), row, column));
			}

			@Override
			public void setContent(String content) {
				mathField.parse(content);
			}
		}
	}
}
