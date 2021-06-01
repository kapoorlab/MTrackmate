package fiji.plugin.mtrackmate.action;

import static fiji.plugin.mtrackmate.gui.Icons.CALCULATOR_ICON;

import java.awt.Frame;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import fiji.plugin.mtrackmate.Logger;
import fiji.plugin.mtrackmate.Model;
import fiji.plugin.mtrackmate.SelectionModel;
import fiji.plugin.mtrackmate.Settings;
import fiji.plugin.mtrackmate.TrackMate;
import fiji.plugin.mtrackmate.gui.displaysettings.DisplaySettings;

public class RecomputeFeatureAction extends AbstractTMAction
{

	public static final String NAME = "Recompute all features";

	public static final String KEY = "RECOMPUTE_FEATURES";

	public static final String INFO_TEXT = "<html>" +
			"Calling this action causes the model to recompute all the features values " +
			"for all spots, edges and tracks. All the feature analyzers discovered when "
			+ "running this action are added and computed. " +
			"</html>";


	@Override
	public void execute( final TrackMate mtrackmate, final SelectionModel selectionModel, final DisplaySettings displaySettings, final Frame parent )
	{
		recompute( mtrackmate, logger );
	}

	@Plugin( type = TrackMateActionFactory.class )
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
			return CALCULATOR_ICON;
		}

		@Override
		public TrackMateAction create()
		{
			return new RecomputeFeatureAction();
		}
	}

	public static void recompute( final TrackMate mtrackmate, final Logger logger )
	{
		logger.log( "Recalculating all features.\n" );
		final Model model = mtrackmate.getModel();
		final Logger oldLogger = model.getLogger();
		model.setLogger( logger );

		final Settings settings = mtrackmate.getSettings();

		/*
		 * Configure settings object with spot, edge and track analyzers as
		 * specified in the providers.
		 */

		settings.addAllAnalyzers();
		mtrackmate.computeSpotFeatures( true );
		mtrackmate.computeEdgeFeatures( true );
		mtrackmate.computeTrackFeatures( true );

		model.setLogger( oldLogger );
		logger.log( "Done.\n" );
	}
}
