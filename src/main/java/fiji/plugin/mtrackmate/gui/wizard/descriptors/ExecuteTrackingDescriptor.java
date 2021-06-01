package fiji.plugin.mtrackmate.gui.wizard.descriptors;

import java.util.IntSummaryStatistics;

import org.scijava.Cancelable;

import fiji.plugin.mtrackmate.Logger;
import fiji.plugin.mtrackmate.TrackMate;
import fiji.plugin.mtrackmate.TrackModel;
import fiji.plugin.mtrackmate.gui.components.LogPanel;
import fiji.plugin.mtrackmate.gui.wizard.WizardPanelDescriptor;

public class ExecuteTrackingDescriptor extends WizardPanelDescriptor
{

	public static final String KEY = "ExecuteTracking";

	private final TrackMate mtrackmate;

	public ExecuteTrackingDescriptor( final TrackMate mtrackmate, final LogPanel logPanel )
	{
		super( KEY );
		this.mtrackmate = mtrackmate;
		this.targetPanel = logPanel;
	}

	@Override
	public Runnable getForwardRunnable()
	{
		return () -> {
			final long start = System.currentTimeMillis();
			mtrackmate.execTracking();
			final long end = System.currentTimeMillis();

			final Logger logger = mtrackmate.getModel().getLogger();
			logger.log( String.format( "Tracking done in %.1f s.\n", ( end - start ) / 1e3f ) );
			final TrackModel trackModel = mtrackmate.getModel().getTrackModel();
			final int nTracks = trackModel.nTracks( false );
			final IntSummaryStatistics stats = trackModel.unsortedTrackIDs( false ).stream()
					.mapToInt( id -> trackModel.trackSpots( id ).size() )
					.summaryStatistics();
			logger.log( "Found " + nTracks + " tracks.\n" );
			logger.log( String.format( "  - avg size: %.1f spots.\n", stats.getAverage() ) );
			logger.log( String.format( "  - min size: %d spots.\n", stats.getMin() ) );
			logger.log( String.format( "  - max size: %d spots.\n", stats.getMax() ) );
		};
	}

	@Override
	public Cancelable getCancelable()
	{
		return mtrackmate;
	}
}
