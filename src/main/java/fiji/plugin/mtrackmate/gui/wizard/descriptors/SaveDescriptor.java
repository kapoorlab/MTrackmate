package fiji.plugin.mtrackmate.gui.wizard.descriptors;

import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.SwingUtilities;

import fiji.plugin.mtrackmate.Logger;
import fiji.plugin.mtrackmate.TrackMate;
import fiji.plugin.mtrackmate.gui.components.LogPanel;
import fiji.plugin.mtrackmate.gui.displaysettings.DisplaySettings;
import fiji.plugin.mtrackmate.gui.wizard.WizardSequence;
import fiji.plugin.mtrackmate.io.IOUtils;
import fiji.plugin.mtrackmate.io.TmXmlWriter;
import fiji.plugin.mtrackmate.util.TMUtils;

public class SaveDescriptor extends SomeDialogDescriptor
{

	private static final String KEY = "Saving";

	private final TrackMate mtrackmate;

	private final DisplaySettings displaySettings;

	private final WizardSequence sequence;

	public SaveDescriptor( final TrackMate mtrackmate, final DisplaySettings displaySettings, final WizardSequence sequence )
	{
		super( KEY, ( LogPanel ) sequence.logDescriptor().getPanelComponent() );
		this.mtrackmate = mtrackmate;
		this.displaySettings = displaySettings;
		this.sequence = sequence;
	}

	@Override
	public void displayingPanel()
	{
		final LogPanel logPanel = ( LogPanel ) targetPanel;
		final Logger logger = logPanel.getLogger();
		logger.log( "Saving data...\n", Logger.BLUE_COLOR );
		if ( null == file )
			file = TMUtils.proposeTrackMateSaveFile( mtrackmate.getSettings(), logger );

		/*
		 * If we are to save tracks, we better ensures that track and edge
		 * features are there, even if we have to enforce it.
		 */
		if ( mtrackmate.getModel().getTrackModel().nTracks( false ) > 0 )
		{
			mtrackmate.computeEdgeFeatures( true );
			mtrackmate.computeTrackFeatures( true );
		}

		final File tmpFile = IOUtils.askForFileForSaving( file, ( Frame ) SwingUtilities.getWindowAncestor( logPanel ), logger );
		if ( null == tmpFile )
			return;
		file = tmpFile;

		/*
		 * Write model, settings and GUI state
		 */

		final TmXmlWriter writer = new TmXmlWriter( file, logger );

		writer.appendLog( logPanel.getTextContent() );
		writer.appendModel( mtrackmate.getModel() );
		writer.appendSettings( mtrackmate.getSettings() );
		writer.appendGUIState( sequence.current().getPanelDescriptorIdentifier() );
		writer.appendDisplaySettings( displaySettings );

		try
		{
			writer.writeToFile();
			logger.log( "Data saved to: " + file.toString() + '\n' );
		}
		catch ( final FileNotFoundException e )
		{
			logger.error( "File not found:\n" + e.getMessage() + '\n' );
			return;
		}
		catch ( final IOException e )
		{
			logger.error( "Input/Output error:\n" + e.getMessage() + '\n' );
			return;
		}
	}
}
