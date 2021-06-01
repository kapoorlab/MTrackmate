package fiji.plugin.mtrackmate.visualization.trackscheme;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import fiji.plugin.mtrackmate.Model;
import fiji.plugin.mtrackmate.SelectionModel;
import fiji.plugin.mtrackmate.Settings;
import fiji.plugin.mtrackmate.gui.displaysettings.DisplaySettings;
import fiji.plugin.mtrackmate.visualization.TrackMateModelView;
import fiji.plugin.mtrackmate.visualization.ViewFactory;

/*
 * We annotate the TrackScheme factory to be NOT visible,
 * because we do not want it to show in the GUI menu.
 */
@Plugin( type = ViewFactory.class, visible = false )
public class TrackSchemeFactory implements ViewFactory
{

	@Override
	public TrackMateModelView create( final Model model, final Settings settings, final SelectionModel selectionModel, final DisplaySettings displaySettings )
	{
		return new TrackScheme( model, selectionModel, displaySettings );
	}

	@Override
	public String getName()
	{
		return "TrackScheme";
	}

	@Override
	public String getKey()
	{
		return TrackScheme.KEY;
	}

	@Override
	public ImageIcon getIcon()
	{
		return null;
	}

	@Override
	public String getInfoText()
	{
		return "<html>Not redacted!</html>";
	}
}
