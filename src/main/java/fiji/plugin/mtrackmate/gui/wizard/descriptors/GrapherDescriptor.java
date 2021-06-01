package fiji.plugin.mtrackmate.gui.wizard.descriptors;

import fiji.plugin.mtrackmate.TrackMate;
import fiji.plugin.mtrackmate.gui.components.GrapherPanel;
import fiji.plugin.mtrackmate.gui.displaysettings.DisplaySettings;
import fiji.plugin.mtrackmate.gui.wizard.WizardPanelDescriptor;

public class GrapherDescriptor extends WizardPanelDescriptor
{

	private static final String KEY = "GraphFeatures";

	public GrapherDescriptor( final TrackMate mtrackmate, final DisplaySettings displaySettings )
	{
		super( KEY );
		this.targetPanel = new GrapherPanel( mtrackmate, displaySettings );
	}

	@Override
	public void aboutToDisplayPanel()
	{
		final GrapherPanel panel = ( GrapherPanel ) targetPanel;
		panel.refresh();
	}
}
