package fiji.plugin.mtrackmate.gui.wizard.descriptors;

import java.util.Map;

import fiji.plugin.mtrackmate.TrackMate;
import fiji.plugin.mtrackmate.detection.LabeImageDetectorFactory;
import fiji.plugin.mtrackmate.detection.SpotDetectorFactoryBase;
import fiji.plugin.mtrackmate.gui.components.ModuleChooserPanel;
import fiji.plugin.mtrackmate.gui.wizard.WizardPanelDescriptor;
import fiji.plugin.mtrackmate.providers.DetectorProvider;

public class ChooseDetectorDescriptor extends WizardPanelDescriptor
{

	private static final String KEY = "ChooseDetector";

	private final TrackMate mtrackmate;

	private final DetectorProvider detectorProvider;
	

	public ChooseDetectorDescriptor( final DetectorProvider detectorProvider, final TrackMate mtrackmate )
	{
		super( KEY );
		this.mtrackmate = mtrackmate;
		this.detectorProvider = detectorProvider;
		String selectedDetector = LabeImageDetectorFactory.DETECTOR_KEY; // default
		if ( null != mtrackmate.getSettings().detectorFactory )
			selectedDetector = mtrackmate.getSettings().detectorFactory.getKey();

		this.targetPanel = new ModuleChooserPanel<>( detectorProvider, "detector", selectedDetector );
			this.targetPanel.setEnabled(false);
	}

	private void setCurrentChoiceFromPlugin()
	{
		String key = LabeImageDetectorFactory.DETECTOR_KEY; // back to default
		if ( null != mtrackmate.getSettings().detectorFactory )
			key = mtrackmate.getSettings().detectorFactory.getKey();

		@SuppressWarnings( { "rawtypes", "unchecked" } )
		final ModuleChooserPanel< SpotDetectorFactoryBase > component = (fiji.plugin.mtrackmate.gui.components.ModuleChooserPanel< SpotDetectorFactoryBase > ) targetPanel;
		component.setSelectedModuleKey( key );
	}

	@Override
	public void displayingPanel()
	{
		setCurrentChoiceFromPlugin();
	}

	@Override
	public void aboutToHidePanel()
	{
		// Configure the detector provider with choice made in panel
		@SuppressWarnings( { "rawtypes", "unchecked" } )
		final ModuleChooserPanel< SpotDetectorFactoryBase > component = (fiji.plugin.mtrackmate.gui.components.ModuleChooserPanel< SpotDetectorFactoryBase > ) targetPanel;
		final String detectorKey = component.getSelectedModuleKey();

		// Configure mtrackmate settings with selected detector
		final SpotDetectorFactoryBase< ? > factory = detectorProvider.getFactory( detectorKey );

		if ( null == factory )
		{
			mtrackmate.getModel().getLogger().error( "[ChooseDetectorDescriptor] Cannot find detector named "
					+ detectorKey
					+ " in current TrackMate modules." );
			return;
		}
		mtrackmate.getSettings().detectorFactory = factory;

		/*
		 * Compare current settings with default ones, and substitute default
		 * ones only if the old ones are absent or not compatible with it.
		 */
		final Map< String, Object > currentSettings = mtrackmate.getSettings().detectorSettings;
		if ( !factory.checkSettings( currentSettings ) )
		{
			final Map< String, Object > defaultSettings = factory.getDefaultSettings();
			mtrackmate.getSettings().detectorSettings = defaultSettings;
		}
	}
}
