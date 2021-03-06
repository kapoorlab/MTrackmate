package fiji.plugin.mtrackmate.gui.wizard.descriptors;

import java.util.Map;

import fiji.plugin.mtrackmate.TrackMate;
import fiji.plugin.mtrackmate.gui.components.ModuleChooserPanel;
import fiji.plugin.mtrackmate.gui.wizard.WizardPanelDescriptor;
import fiji.plugin.mtrackmate.providers.TrackerProvider;
import fiji.plugin.mtrackmate.tracking.SpotTrackerFactory;
import fiji.plugin.mtrackmate.tracking.sparselap.SimpleSparseLAPTrackerFactory;
import fiji.plugin.mtrackmate.tracking.sparselap.SparseLAPTrackerFactory;

public class ChooseTrackerDescriptor extends WizardPanelDescriptor
{

	private static final String KEY = "ChooseTracker";

	private final TrackMate mtrackmate;

	private final TrackerProvider trackerProvider;

	public ChooseTrackerDescriptor( final TrackerProvider trackerProvider, final TrackMate mtrackmate )
	{
		super( KEY );
		this.mtrackmate = mtrackmate;
		this.trackerProvider = trackerProvider;

		String selectedTracker = SparseLAPTrackerFactory.THIS_TRACKER_KEY; // default
		if ( null != mtrackmate.getSettings().trackerFactory )
			selectedTracker = mtrackmate.getSettings().trackerFactory.getKey();

		this.targetPanel = new ModuleChooserPanel<>( trackerProvider, "tracker", selectedTracker );
	}

	private void setCurrentChoiceFromPlugin()
	{
		String key = SimpleSparseLAPTrackerFactory.THIS2_TRACKER_KEY; // default
		if ( null != mtrackmate.getSettings().trackerFactory )
			key = mtrackmate.getSettings().trackerFactory.getKey();

		@SuppressWarnings( "unchecked" )
		final ModuleChooserPanel< SpotTrackerFactory > component = (fiji.plugin.mtrackmate.gui.components.ModuleChooserPanel< SpotTrackerFactory > ) targetPanel;
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
		@SuppressWarnings( "unchecked" )
		final ModuleChooserPanel< SpotTrackerFactory > component = (fiji.plugin.mtrackmate.gui.components.ModuleChooserPanel< SpotTrackerFactory > ) targetPanel;
		final String trackerKey = component.getSelectedModuleKey();

		// Configure mtrackmate settings with selected detector
		final SpotTrackerFactory factory = trackerProvider.getFactory( trackerKey );

		if ( null == factory )
		{
			mtrackmate.getModel().getLogger().error( "[ChooseTrackerDescriptor] Cannot find tracker named "
					+ trackerKey
					+ " in current TrackMate modules." );
			return;
		}
		mtrackmate.getSettings().trackerFactory = factory;

		/*
		 * Compare current settings with default ones, and substitute default
		 * ones only if the old ones are absent or not compatible with it.
		 */
		final Map< String, Object > currentSettings = mtrackmate.getSettings().trackerSettings;
		if ( !factory.checkSettingsValidity( currentSettings ) )
		{
			final Map< String, Object > defaultSettings = factory.getDefaultSettings();
			mtrackmate.getSettings().trackerSettings = defaultSettings;
		}
	}
}
