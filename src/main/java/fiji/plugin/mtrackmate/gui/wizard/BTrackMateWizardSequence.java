package fiji.plugin.mtrackmate.gui.wizard;

import static fiji.plugin.mtrackmate.gui.Icons.SPOT_TABLE_ICON;
import static fiji.plugin.mtrackmate.gui.Icons.TRACK_SCHEME_ICON_16x16;
import static fiji.plugin.mtrackmate.gui.Icons.TRACK_TABLES_ICON;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import fiji.plugin.mtrackmate.Logger;
import fiji.plugin.mtrackmate.Model;
import fiji.plugin.mtrackmate.SelectionModel;
import fiji.plugin.mtrackmate.Settings;
import fiji.plugin.mtrackmate.Spot;
import fiji.plugin.mtrackmate.SpotCollection;
import fiji.plugin.mtrackmate.TrackMate;
import fiji.plugin.mtrackmate.action.AbstractTMAction;
import fiji.plugin.mtrackmate.action.ExportAllSpotsStatsAction;
import fiji.plugin.mtrackmate.action.ExportStatsTablesAction;
import fiji.plugin.mtrackmate.detection.ManualDetectorFactory;
import fiji.plugin.mtrackmate.detection.SpotDetectorFactoryBase;
import fiji.plugin.mtrackmate.features.FeatureFilter;
import fiji.plugin.mtrackmate.gui.components.ConfigurationPanel;
import fiji.plugin.mtrackmate.gui.components.FeatureDisplaySelector;
import fiji.plugin.mtrackmate.gui.components.LogPanel;
import fiji.plugin.mtrackmate.gui.displaysettings.DisplaySettings;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.ActionChooserDescriptor;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.ChooseDetectorDescriptor;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.ChooseTrackerDescriptor;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.ConfigureViewsDescriptor;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.ExecuteDetectionDescriptor;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.ExecuteTrackingDescriptor;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.GrapherDescriptor;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.InitFilterDescriptor;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.LogPanelDescriptor2;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.SaveDescriptor;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.SpotDetectorDescriptor;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.SpotFilterDescriptor;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.SpotTrackerDescriptor;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.StartDialogDescriptor;
import fiji.plugin.mtrackmate.gui.wizard.descriptors.TrackFilterDescriptor;
import fiji.plugin.mtrackmate.providers.ActionProvider;
import fiji.plugin.mtrackmate.providers.DetectorProvider;
import fiji.plugin.mtrackmate.providers.TrackerProvider;
import fiji.plugin.mtrackmate.tracking.ManualTrackerFactory;
import fiji.plugin.mtrackmate.tracking.SpotTrackerFactory;
import fiji.plugin.mtrackmate.visualization.TrackMateModelView;
import fiji.plugin.mtrackmate.visualization.hyperstack.HyperStackDisplayer;
import fiji.plugin.mtrackmate.visualization.trackscheme.SpotImageUpdater;
import fiji.plugin.mtrackmate.visualization.trackscheme.TrackScheme;

public class BTrackMateWizardSequence implements WizardSequence
{

	private final TrackMate mtrackmate;

	private final SelectionModel selectionModel;

	private final DisplaySettings displaySettings;

	private WizardPanelDescriptor current;

	private final StartDialogDescriptor startDialogDescriptor;

	private final Map< WizardPanelDescriptor, WizardPanelDescriptor > next;

	private final Map< WizardPanelDescriptor, WizardPanelDescriptor > previous;

	private final LogPanelDescriptor2 logDescriptor;

	private final ChooseDetectorDescriptor chooseDetectorDescriptor;

	private final ExecuteDetectionDescriptor executeDetectionDescriptor;

	private final InitFilterDescriptor initFilterDescriptor;

	private final SpotFilterDescriptor spotFilterDescriptor;

	private final ChooseTrackerDescriptor chooseTrackerDescriptor;

	private final ExecuteTrackingDescriptor executeTrackingDescriptor;

	private final TrackFilterDescriptor trackFilterDescriptor;

	private final ConfigureViewsDescriptor configureViewsDescriptor;

	private final GrapherDescriptor grapherDescriptor;

	private final ActionChooserDescriptor actionChooserDescriptor;

	private final SaveDescriptor saveDescriptor;
	

	public BTrackMateWizardSequence( final TrackMate mtrackmate, final SelectionModel selectionModel, final DisplaySettings displaySettings)
	{
		this.mtrackmate = mtrackmate;
		this.selectionModel = selectionModel;
		this.displaySettings = displaySettings;
		final Settings settings = mtrackmate.getSettings();
		final Model model = mtrackmate.getModel();

		final LogPanel logPanel = new LogPanel();
		final Logger logger = logPanel.getLogger();
        model.setLogger(logger);
		final FeatureDisplaySelector featureSelector = new FeatureDisplaySelector( model, settings, displaySettings );
		final FeatureFilter initialFilter = new FeatureFilter( Spot.QUALITY, settings.initialSpotFilterValue.doubleValue(), true );
		final List< FeatureFilter > spotFilters = settings.getSpotFilters();
		final List< FeatureFilter > trackFilters = settings.getTrackFilters();

		logDescriptor = new LogPanelDescriptor2( logPanel );
		startDialogDescriptor = new StartDialogDescriptor( model, settings, logger );
		chooseDetectorDescriptor = new ChooseDetectorDescriptor( new DetectorProvider(), mtrackmate );
		executeDetectionDescriptor = new ExecuteDetectionDescriptor( mtrackmate, logPanel );
		initFilterDescriptor = new InitFilterDescriptor( mtrackmate, initialFilter );
		spotFilterDescriptor = new SpotFilterDescriptor( mtrackmate, spotFilters, featureSelector );
		chooseTrackerDescriptor = new ChooseTrackerDescriptor( new TrackerProvider(), mtrackmate );
		executeTrackingDescriptor = new ExecuteTrackingDescriptor( mtrackmate, logPanel );
		trackFilterDescriptor = new TrackFilterDescriptor( mtrackmate, trackFilters, featureSelector );
		configureViewsDescriptor = new ConfigureViewsDescriptor( displaySettings, featureSelector, new LaunchTrackSchemeAction(), new ShowTrackTablesAction(), new ShowSpotTableAction(), model.getSpaceUnits() );
		grapherDescriptor = new GrapherDescriptor( mtrackmate, displaySettings );
		actionChooserDescriptor = new ActionChooserDescriptor( new ActionProvider(), mtrackmate, selectionModel, displaySettings );
		saveDescriptor = new SaveDescriptor( mtrackmate, displaySettings, this );

		this.next = getForwardSequence();
		this.previous = getBackwardSequence();
		current = startDialogDescriptor;
	}

	@Override
	public WizardPanelDescriptor next()
	{
		if ( current == chooseDetectorDescriptor )
			getDetectorConfigDescriptor();

		if ( current == chooseTrackerDescriptor )
			getTrackerConfigDescriptor();

		current = next.get( current );
		return current;
	}


	@Override
	public WizardPanelDescriptor previous()
	{
		if ( current == trackFilterDescriptor )
			getTrackerConfigDescriptor();

		if ( current == spotFilterDescriptor )
			getDetectorConfigDescriptor();

		current = previous.get( current );
		return current;
	}

	@Override
	public boolean hasNext()
	{
		return current != actionChooserDescriptor;
	}

	@Override
	public WizardPanelDescriptor current()
	{
		return current;
	}


	@Override
	public WizardPanelDescriptor logDescriptor()
	{
		return logDescriptor;
	}

	@Override
	public WizardPanelDescriptor configDescriptor()
	{
		return configureViewsDescriptor;
	}

	@Override
	public WizardPanelDescriptor save()
	{
		return saveDescriptor;
	}

	@Override
	public boolean hasPrevious()
	{
		return current != startDialogDescriptor;
	}

	private Map< WizardPanelDescriptor, WizardPanelDescriptor > getBackwardSequence()
	{
		final Map< WizardPanelDescriptor, WizardPanelDescriptor > map = new HashMap<>();
		map.put( startDialogDescriptor, null );
		map.put( chooseDetectorDescriptor, startDialogDescriptor );
		map.put( chooseTrackerDescriptor, spotFilterDescriptor );
		map.put( configureViewsDescriptor, trackFilterDescriptor );
		map.put( grapherDescriptor, configureViewsDescriptor );
		map.put( actionChooserDescriptor, grapherDescriptor );
		return map;
	}

	private Map< WizardPanelDescriptor, WizardPanelDescriptor > getForwardSequence()
	{
		final Map< WizardPanelDescriptor, WizardPanelDescriptor > map = new HashMap<>();
		map.put( startDialogDescriptor, chooseDetectorDescriptor );
		map.put( executeDetectionDescriptor, initFilterDescriptor );
		map.put( initFilterDescriptor, spotFilterDescriptor );
		map.put( spotFilterDescriptor, chooseTrackerDescriptor );
		map.put( executeTrackingDescriptor, trackFilterDescriptor );
		map.put( trackFilterDescriptor, configureViewsDescriptor );
		map.put( configureViewsDescriptor, grapherDescriptor );
		map.put( grapherDescriptor, actionChooserDescriptor );
		return map;
	}

	@Override
	public void setCurrent( final String panelIdentifier )
	{
		if ( panelIdentifier.equals( SpotDetectorDescriptor.KEY ) )
		{
			current = getDetectorConfigDescriptor();
			return;
		}

		if ( panelIdentifier.equals( SpotTrackerDescriptor.KEY ) )
		{
			current = getTrackerConfigDescriptor();
			return;
		}

		if ( panelIdentifier.equals( InitFilterDescriptor.KEY ) )
		{
			getDetectorConfigDescriptor();
			current = initFilterDescriptor;
			return;
		}

		final List< WizardPanelDescriptor > descriptors = Arrays.asList( new WizardPanelDescriptor[] {
				logDescriptor,
				chooseDetectorDescriptor,
				executeDetectionDescriptor,
				initFilterDescriptor,
				spotFilterDescriptor,
				chooseTrackerDescriptor,
				executeTrackingDescriptor,
				trackFilterDescriptor,
				configureViewsDescriptor,
				grapherDescriptor,
				actionChooserDescriptor,
				saveDescriptor
		} );
		for ( final WizardPanelDescriptor w : descriptors )
		{
			if ( w.getPanelDescriptorIdentifier().equals( panelIdentifier ) )
			{
				current = w;
				break;
			}
		}
	}

	/**
	 * Determines and registers the descriptor used to configure the detector
	 * chosen in the {@link ChooseDetectorDescriptor}.
	 *
	 * @return a suitable {@link SpotDetectorDescriptor}.
	 */
	private SpotDetectorDescriptor getDetectorConfigDescriptor()
	{
		final SpotDetectorFactoryBase< ? > detectorFactory = mtrackmate.getSettings().detectorFactory;

		/*
		 * Special case: are we dealing with the manual detector? If yes, no
		 * config, no detection.
		 */
		if ( detectorFactory.getKey().equals( ManualDetectorFactory.DETECTOR_KEY ) )
		{
			// Position sequence next and previous.
			next.put( chooseDetectorDescriptor, spotFilterDescriptor );
			previous.put( spotFilterDescriptor, chooseDetectorDescriptor );
			previous.put( executeDetectionDescriptor, chooseDetectorDescriptor );
			previous.put( initFilterDescriptor, chooseDetectorDescriptor );
			return null;
		}

		/*
		 * Copy as much settings as we can to the potentially new config
		 * descriptor.
		 */
		// From settings.
		final Map< String, Object > oldSettings1 = new HashMap<>( mtrackmate.getSettings().detectorSettings );
		// From previous panel.
		final Map< String, Object > oldSettings2 = new HashMap<>();
		final WizardPanelDescriptor previousDescriptor = next.get( chooseDetectorDescriptor );
		if ( previousDescriptor != null && previousDescriptor instanceof SpotDetectorDescriptor )
		{
			final SpotDetectorDescriptor previousSpotDetectorDescriptor = ( SpotDetectorDescriptor ) previousDescriptor;
			final ConfigurationPanel detectorConfigPanel = ( ConfigurationPanel ) previousSpotDetectorDescriptor.targetPanel;
			oldSettings2.putAll( detectorConfigPanel.getSettings() );
		}

		final Map< String, Object > defaultSettings = detectorFactory.getDefaultSettings();
		for ( final String skey : defaultSettings.keySet() )
		{
			Object previousValue = oldSettings2.get( skey );
			if ( previousValue == null )
				previousValue = oldSettings1.get( skey );

			defaultSettings.put( skey, previousValue );
		}

		final ConfigurationPanel detectorConfigurationPanel = detectorFactory.getDetectorConfigurationPanel( mtrackmate.getSettings(), mtrackmate.getModel() );
		detectorConfigurationPanel.setSettings( defaultSettings );
		mtrackmate.getSettings().detectorSettings = defaultSettings;
		final SpotDetectorDescriptor configDescriptor = new SpotDetectorDescriptor( mtrackmate.getSettings(), detectorConfigurationPanel, mtrackmate.getModel().getLogger() );

		// Position sequence next and previous.
		next.put( chooseDetectorDescriptor, configDescriptor );
		next.put( configDescriptor, executeDetectionDescriptor );
		previous.put( configDescriptor, chooseDetectorDescriptor );
		previous.put( executeDetectionDescriptor, configDescriptor );
		previous.put( initFilterDescriptor, configDescriptor );
		previous.put( spotFilterDescriptor, configDescriptor );

		return configDescriptor;
	}

	/**
	 * Determines and registers the descriptor used to configure the tracker
	 * chosen in the {@link ChooseTrackerDescriptor}.
	 *
	 * @return a suitable {@link SpotTrackerDescriptor}.
	 */
	private SpotTrackerDescriptor getTrackerConfigDescriptor()
	{
		final SpotTrackerFactory trackerFactory = mtrackmate.getSettings().trackerFactory;

		/*
		 * Special case: are we dealing with the manual tracker? If yes, no
		 * config, no detection.
		 */
		if ( trackerFactory.getKey().equals( ManualTrackerFactory.TRACKER_KEY ) )
		{
			// Position sequence next and previous.
			next.put( chooseTrackerDescriptor, trackFilterDescriptor );
			previous.put( executeTrackingDescriptor, chooseTrackerDescriptor );
			previous.put( trackFilterDescriptor, chooseTrackerDescriptor );
			return null;
		}
		/*
		 * Copy as much settings as we can to the potentially new config
		 * descriptor.
		 */
		// From settings.
		final Map< String, Object > oldSettings1 = new HashMap<>( mtrackmate.getSettings().trackerSettings );
		// From previous panel.
		final Map< String, Object > oldSettings2 = new HashMap<>();
		final WizardPanelDescriptor previousDescriptor = next.get( chooseTrackerDescriptor );
		if ( previousDescriptor != null && previousDescriptor instanceof SpotTrackerDescriptor )
		{
			final SpotTrackerDescriptor previousTrackerDetectorDescriptor = ( SpotTrackerDescriptor ) previousDescriptor;
			final ConfigurationPanel detectorConfigPanel = ( ConfigurationPanel ) previousTrackerDetectorDescriptor.targetPanel;
			oldSettings2.putAll( detectorConfigPanel.getSettings() );
		}

		final Map< String, Object > defaultSettings = trackerFactory.getDefaultSettings();
		for ( final String skey : defaultSettings.keySet() )
		{
			Object previousValue = oldSettings2.get( skey );
			if ( previousValue == null )
				previousValue = oldSettings1.get( skey );

			defaultSettings.put( skey, previousValue );
		}

		final ConfigurationPanel trackerConfigurationPanel = trackerFactory.getTrackerConfigurationPanel( mtrackmate.getModel() );
		trackerConfigurationPanel.setSettings( defaultSettings );
		mtrackmate.getSettings().trackerSettings = defaultSettings;
		final SpotTrackerDescriptor configDescriptor = new SpotTrackerDescriptor( mtrackmate.getSettings(), trackerConfigurationPanel, mtrackmate.getModel().getLogger() );

		// Position sequence next and previous.
		next.put( chooseTrackerDescriptor, configDescriptor );
		next.put( configDescriptor, executeTrackingDescriptor );
		previous.put( configDescriptor, chooseTrackerDescriptor );
		previous.put( executeTrackingDescriptor, configDescriptor );
		previous.put( trackFilterDescriptor, configDescriptor );
		
		return configDescriptor;
	}

	private static final String TRACK_TABLES_BUTTON_TOOLTIP = "<html>"
			+ "Export the features of all tracks, edges and all <br>"
			+ "spots belonging to a track to ImageJ tables."
			+ "</html>";

	private static final String SPOT_TABLE_BUTTON_TOOLTIP = "Export the features of all spots to ImageJ tables.";

	private static final String TRACKSCHEME_BUTTON_TOOLTIP = "<html>Launch a new instance of TrackScheme.</html>";

	private class LaunchTrackSchemeAction extends AbstractAction
	{
		private static final long serialVersionUID = 1L;

		private LaunchTrackSchemeAction()
		{
			super( "TrackScheme", TRACK_SCHEME_ICON_16x16 );
			putValue( SHORT_DESCRIPTION, TRACKSCHEME_BUTTON_TOOLTIP );
		}

		@Override
		public void actionPerformed( final ActionEvent e )
		{
			new Thread( "Launching TrackScheme thread" )
			{
				@Override
				public void run()
				{
					final TrackScheme trackscheme = new TrackScheme( mtrackmate.getModel(), selectionModel, displaySettings );
					final SpotImageUpdater thumbnailUpdater = new SpotImageUpdater( mtrackmate.getSettings() );
					trackscheme.setSpotImageUpdater( thumbnailUpdater );
					trackscheme.render();
				}
			}.start();
		}
	}

	private class ShowTrackTablesAction extends AbstractAction
	{
		private static final long serialVersionUID = 1L;

		private ShowTrackTablesAction()
		{
			super( "Tracks", TRACK_TABLES_ICON );
			putValue( SHORT_DESCRIPTION, TRACK_TABLES_BUTTON_TOOLTIP );
		}

		@Override
		public void actionPerformed( final ActionEvent e )
		{
			showTables( false );
		}
	}

	private class ShowSpotTableAction extends AbstractAction
	{
		private static final long serialVersionUID = 1L;

		private ShowSpotTableAction()
		{
			super( "Spots", SPOT_TABLE_ICON );
			putValue( SHORT_DESCRIPTION, SPOT_TABLE_BUTTON_TOOLTIP );
		}

		@Override
		public void actionPerformed( final ActionEvent e )
		{
			showTables( true );
		}
	}

	private void showTables( final boolean showSpotTable )
	{
		new Thread( "TrackMate table thread." )
		{
			@Override
			public void run()
			{
				AbstractTMAction action;
				if ( showSpotTable )
					action = new ExportAllSpotsStatsAction();
				else
					action = new ExportStatsTablesAction();

				action.execute( mtrackmate, selectionModel, displaySettings, null );
			}
		}.start();
	}
}
