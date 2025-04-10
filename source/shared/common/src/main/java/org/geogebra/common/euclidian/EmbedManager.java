package org.geogebra.common.euclidian;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.euclidian.draw.DrawWidget;
import org.geogebra.common.io.file.ZipFile;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.plugin.ActionType;

/**
 * Updates, adds and removes embedded applets.
 * 
 * @author Zbynek
 */
public interface EmbedManager {
	/**
	 * Add new applet.
	 * 
	 * @param drawEmbed
	 *            embedded applet
	 */
	void add(DrawEmbed drawEmbed);

	/**
	 * Update an embedded applet.
	 * 
	 * @param drawEmbed
	 *            embedded applet
	 */
	void update(DrawEmbed drawEmbed);

	/**
	 * Remove all widgets.
	 */
	void removeAll();

	/**
	 * @return unused ID for new embed
	 */
	int nextID();

	/**
	 * Add base64 of embedded files into an archive
	 * 
	 * @param construction
	 *            construction
	 * 
	 * @param f
	 *            archive
	 */
	void writeEmbeds(Construction construction, ZipFile f);

	/**
	 * Load all embeds for a slide
	 * 
	 * @param archive
	 *            slide
	 */
	void loadEmbeds(ZipFile archive);

	/**
	 * Save state of all widgets.
	 */
	void persist();

	/**
	 * Move all embeds to background.
	 */
	void backgroundAll();

	/**
	 * Activates embedded applet
	 * 
	 * @param embed
	 *            active embed
	 */
	void play(GeoEmbed embed);

	/**
	 * Removes embedded applet
	 * 
	 * @param drawEmbed
	 *            drawable
	 */
	void remove(DrawEmbed drawEmbed);

	/**
	 * Add new embedded applet and store undo info.
	 * 
	 * @param material
	 *            online material
	 */
	void embed(Material material);

	/**
	 * @param drawEmbed applet drawable
	 * @param width width in pixels
	 * @param height height in pixels
	 */
	void drawPreview(GGraphics2D g2, DrawEmbed drawEmbed, int width, int height, double angle);

	/**
	 * Executes an action in all embedded elements.
	 *
	 * @param action
	 *            event type
	 */
	void executeAction(ActionType action);

	/**
	 * Move embeds to cache so that they don't need rebuilding during undo
	 */
	void storeEmbeds();

	/**
	 * Permanently remove cached embeds
	 */
	void clearStoredEmbeds();

	/**
	 * opens the  Graspable math tool
	 */
	void openGraspableMTool();

	void initAppEmbed(GeoEmbed ge);

	/**
	 * @param embed drawable
	 * @param layer z-index
	 */
	void setLayer(DrawWidget embed, int layer);

	/**
	 * @param embedID embed ID
	 * @return embed content as JSON
	 */
	String getContent(int embedID);

	/**
	 * @param embedID embed ID
	 * @param content embed content as JSON
	 */
	void setContent(int embedID, String content);

	void setContentSync(String label, String base64);

	void sendCommand(GeoEmbed chart, String cmd);

	void setGraphAxis(GeoEmbed chart, int axis, double crossing);

	/**
	 * @param embed calculator
	 * @return application for embedded calculator, null during slide switch
	 */
	@CheckForNull App getEmbedApp(GeoEmbed embed);

	/**
	 * Add embedded suite calc
	 * @param appCode top level code
	 * @param subApp - preselected app
	 */
	void addCalcWithPreselectedApp(String appCode, String subApp);

	/**
	 * Adds a resolving function for specific embedded element type. The function gets an ID of the
	 * embed and returns a promise that resolves to a HTML string.
	 *
	 * @param type the embed type
	 * @param callback the resolving callback
	 */
	void registerEmbedResolver(String type, Object callback);

	/**
	 * Inserts embedded element with specific type and id.
	 *
	 * @param type the embed type.
	 * @param id the embed id.
	 * @return if embed was successful.
	 */
	boolean insertEmbed(String type, String id);
}
