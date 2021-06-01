package fiji.plugin.mtrackmate.visualization;

import fiji.plugin.mtrackmate.Model;
import fiji.plugin.mtrackmate.SelectionModel;
import fiji.plugin.mtrackmate.Settings;
import fiji.plugin.mtrackmate.TrackMateModule;
import fiji.plugin.mtrackmate.gui.displaysettings.DisplaySettings;

public interface ViewFactory extends TrackMateModule
{

	/**
	 * Returns a new instance of the concrete view.
	 *
	 * @param model
	 *            the model to display in the view.
	 * @param settings
	 *            a {@link Settings} object, which specific implementation might
	 *            use to display the model.
	 * @param selectionModel
	 *            the {@link SelectionModel} model to share in the created view.
	 * @param displaySettings
	 *            the display settings to use to paint the view.
	 * @return a new view of the specified model.
	 */
	public TrackMateModelView create( final Model model, final Settings settings, final SelectionModel selectionModel, final DisplaySettings displaySettings );

}
