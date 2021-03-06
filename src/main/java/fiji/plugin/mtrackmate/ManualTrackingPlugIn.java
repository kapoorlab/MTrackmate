package fiji.plugin.mtrackmate;

import fiji.plugin.mtrackmate.detection.ManualDetectorFactory;
import fiji.plugin.mtrackmate.gui.displaysettings.DisplaySettings;
import fiji.plugin.mtrackmate.gui.wizard.WizardSequence;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.ConfigureViewsDescriptor;
import fiji.plugin.mtrackmate.tracking.ManualTrackerFactory;
import ij.ImageJ;
import ij.ImagePlus;

public class ManualTrackingPlugIn extends TrackMatePlugIn
{

	@Override
	protected WizardSequence createSequence( final TrackMate trackmate, final SelectionModel selectionModel, final DisplaySettings displaySettings )
	{
		final WizardSequence sequence = super.createSequence( trackmate, selectionModel, displaySettings );
		sequence.setCurrent( ConfigureViewsDescriptor.KEY );
		return sequence;
	}

	@SuppressWarnings( "rawtypes" )
	@Override
	protected Settings createSettings( final ImagePlus imp )
	{
		final Settings lSettings = super.createSettings( imp );
		// Manual detection
		lSettings.detectorFactory = new ManualDetectorFactory();
		lSettings.detectorSettings = lSettings.detectorFactory.getDefaultSettings();
		// Manual tracker
		lSettings.trackerFactory = new ManualTrackerFactory();
		lSettings.trackerSettings = lSettings.trackerFactory.getDefaultSettings();
		return lSettings;
	}

	public static void main( final String[] args )
	{
		ImageJ.main( args );
		new ManualTrackingPlugIn().run( "samples/Merged.tif" );
	}
}