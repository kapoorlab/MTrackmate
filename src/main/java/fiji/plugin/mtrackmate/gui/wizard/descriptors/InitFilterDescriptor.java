package fiji.plugin.mtrackmate.gui.wizard.descriptors;

import java.util.function.Function;

import fiji.plugin.mtrackmate.Logger;
import fiji.plugin.mtrackmate.Spot;
import fiji.plugin.mtrackmate.TrackMate;
import fiji.plugin.mtrackmate.features.FeatureFilter;
import fiji.plugin.mtrackmate.features.FeatureUtils;
import fiji.plugin.mtrackmate.gui.components.InitFilterPanel;
import fiji.plugin.mtrackmate.gui.displaysettings.DisplaySettings.TrackMateObject;
import fiji.plugin.mtrackmate.gui.wizard.WizardPanelDescriptor;

public class InitFilterDescriptor extends WizardPanelDescriptor
{

	public static final String KEY = "InitialFiltering";

	private final TrackMate mtrackmate;

	public InitFilterDescriptor( final TrackMate mtrackmate, final FeatureFilter filter )
	{
		super( KEY );
		this.mtrackmate = mtrackmate;
		final Function< String, double[] > valuesCollector = key -> FeatureUtils.collectFeatureValues(
				Spot.QUALITY, TrackMateObject.SPOTS, mtrackmate.getModel(), mtrackmate.getSettings(), false );
		this.targetPanel = new InitFilterPanel( filter, valuesCollector );
	}

	@Override
	public Runnable getForwardRunnable()
	{
		return new Runnable()
		{

			@Override
			public void run()
			{
				mtrackmate.getModel().getLogger().log( "\nComputing spot quality histogram...\n", Logger.BLUE_COLOR );
				final InitFilterPanel component = ( InitFilterPanel ) targetPanel;
				component.refresh();
			}
		};
	}

	@Override
	public void aboutToHidePanel()
	{
		final InitFilterPanel component = ( InitFilterPanel ) targetPanel;
		mtrackmate.getSettings().initialSpotFilterValue = component.getFeatureThreshold().value;
	}
}
