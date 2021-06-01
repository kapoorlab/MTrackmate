/**
 *
 */
package fiji.plugin.mtrackmate.action;

import static fiji.plugin.mtrackmate.gui.Icons.TIME_ICON;

import java.awt.Frame;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import fiji.plugin.mtrackmate.SelectionModel;
import fiji.plugin.mtrackmate.Spot;
import fiji.plugin.mtrackmate.SpotCollection;
import fiji.plugin.mtrackmate.TrackMate;
import fiji.plugin.mtrackmate.gui.displaysettings.DisplaySettings;

public class ResetSpotTimeFeatureAction extends AbstractTMAction {


	public static final String NAME = "Reset spot time";
	public static final String INFO_TEXT = "<html>" +
			"Reset the time feature of all spots: it is set to the frame number "  +
			"times the frame interval. " +
			"</html>";

	private static final String KEY = "RESET_SPOT_TIME";


	@Override
	public void execute( final TrackMate mtrackmate, final SelectionModel selectionModel, final DisplaySettings displaySettings, final Frame parent )
	{
		logger.log("Reset spot time.\n");
		double dt = mtrackmate.getSettings().dt;
		if (dt == 0) {
			dt = 1;
		}
		final SpotCollection spots = mtrackmate.getModel().getSpots();
		final Set<Integer> frames = spots.keySet();
		for(final int frame : frames) {
			for (final Iterator<Spot> iterator = spots.iterator(frame, true); iterator.hasNext();) {
				iterator.next().putFeature(Spot.POSITION_T, frame * dt);
			}
			logger.setProgress((double) (frame + 1) / frames.size());
		}
		logger.log("Done.\n");
		logger.setProgress(0);
	}

	@Plugin( type = TrackMateActionFactory.class, visible = false )
	public static class Factory implements TrackMateActionFactory
	{

		@Override
		public String getInfoText()
		{
			return INFO_TEXT;
		}

		@Override
		public String getName()
		{
			return NAME;
		}

		@Override
		public String getKey()
		{
			return KEY;
		}

		@Override
		public ImageIcon getIcon()
		{
			return TIME_ICON;
		}

		@Override
		public TrackMateAction create()
		{
			return new ResetSpotTimeFeatureAction();
		}
	}
}
