package fiji.plugin.mtrackmate.gui.wizard.descriptors;

import org.scijava.Cancelable;

import fiji.plugin.mtrackmate.TrackMate;
import fiji.plugin.mtrackmate.gui.components.LogPanel;
import fiji.plugin.mtrackmate.gui.wizard.WizardPanelDescriptor;
import fiji.plugin.mtrackmate.util.TMUtils;

public class ExecuteDetectionDescriptor extends WizardPanelDescriptor
{

	public static final String KEY = "ExecuteDetection";

	private final TrackMate mtrackmate;
	

	public ExecuteDetectionDescriptor( final TrackMate mtrackmate, final LogPanel logPanel )
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
			mtrackmate.execDetection();
			final long end = System.currentTimeMillis();
			mtrackmate.getModel().getLogger().log( String.format( "Detection done in %.1f s.\n", ( end - start ) / 1e3f ) );
		};
		
	}

	@Override
	public Cancelable getCancelable()
	{
		return mtrackmate;
	}
}
