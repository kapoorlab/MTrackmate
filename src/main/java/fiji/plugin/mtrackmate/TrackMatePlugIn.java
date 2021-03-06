package fiji.plugin.mtrackmate;

import static fiji.plugin.mtrackmate.gui.Icons.TRACKMATE_ICON;
import static fiji.plugin.mtrackmate.gui.Icons.TRACKMATE_ICON;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fiji.plugin.mtrackmate.gui.GuiUtils;
import fiji.plugin.mtrackmate.gui.displaysettings.DisplaySettings;
import fiji.plugin.mtrackmate.gui.displaysettings.DisplaySettingsIO;
import fiji.plugin.mtrackmate.gui.wizard.BTrackMateWizardSequence;
import fiji.plugin.mtrackmate.gui.wizard.WizardSequence;
import fiji.plugin.mtrackmate.visualization.TrackMateModelView;
import fiji.plugin.mtrackmate.visualization.hyperstack.HyperStackDisplayer;
import fiji.plugin.mtrackmate.Model;
import fiji.plugin.mtrackmate.SelectionModel;
import fiji.plugin.mtrackmate.Settings;
import fiji.plugin.mtrackmate.TrackMate;
import fiji.plugin.mtrackmate.TrackMatePlugIn;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;
import net.imglib2.img.display.imagej.ImageJFunctions;

public class TrackMatePlugIn implements PlugIn
{

	
	public static TrackMateModelView displayer;
	
	public static Settings settings;
	public static Model model;
	public static TrackMate mtrackmate;
	public static JFrame frame;
	public static WizardSequence sequence;
	
	@Override
	public void run( final String imagePath )
	{
		GuiUtils.setSystemLookAndFeel();
		final ImagePlus imp;
		if ( imagePath != null && imagePath.length() > 0 )
		{
			imp = IJ.openImage( imagePath );
			if ( null == imp.getOriginalFileInfo() )
			{
				IJ.error( TrackMate.PLUGIN_NAME_STR + " v" + TrackMate.PLUGIN_NAME_VERSION, "Could not load image with path " + imagePath + "." );
				return;
			}
		}
		else
		{
			imp = WindowManager.getCurrentImage();
			if ( null == imp )
			{
				IJ.error( TrackMate.PLUGIN_NAME_STR + " v" + TrackMate.PLUGIN_NAME_VERSION,
						"Please open an image before running TrackMate." );
				return;
			}
			else if ( imp.getType() == ImagePlus.COLOR_RGB )
			{
				IJ.error( TrackMate.PLUGIN_NAME_STR + " v" + TrackMate.PLUGIN_NAME_VERSION,
						"TrackMate does not work on RGB images." );
				return;
			}
		}

		imp.setOpenAsHyperStack( true );
		imp.setDisplayMode( IJ.COMPOSITE );
		if ( !imp.isVisible() )
			imp.show();

		GuiUtils.userCheckImpDimensions( imp );

		// Main objects.
		
		final SelectionModel selectionModel = new SelectionModel( model );
		final DisplaySettings displaySettings = createDisplaySettings();
		 settings = createSettings( imp );
		 model = createModel( imp );
		 mtrackmate = createTrackMate( model, settings );
		// Main view.
		// Main view.
				
		 displayer = new HyperStackDisplayer( model, selectionModel, imp, PseudocreateDisplaySettings() );
		 displayer.render();
		// Wizard.
		 sequence = createSequence( mtrackmate, selectionModel, displaySettings );
		frame = sequence.run( "MTrackMate");
		frame.setIconImage( TRACKMATE_ICON.getImage() );
		GuiUtils.positionWindow( frame, imp.getWindow() );
		frame.setVisible( true );
		//Call pack on the JFrame to have panels sized with preferred size
		frame.pack();
	}

	/**
	 * Hook for subclassers: <br>
	 * Will create and position the sequence that will be played by the wizard
	 * launched by this plugin.
	 * 
	 * @param trackmate
	 * @param selectionModel
	 * @param displaySettings
	 * @return
	 */
	protected WizardSequence createSequence( final TrackMate mtrackmate, final SelectionModel selectionModel, final DisplaySettings displaySettings)
	{
		return new BTrackMateWizardSequence( mtrackmate, selectionModel, displaySettings);
	}

	/**
	 * Hook for subclassers: <br>
	 * Creates the {@link Model} instance that will be used to store data in the
	 * {@link TrackMate} instance.
	 * 
	 * @param imp
	 *
	 * @return a new {@link Model} instance.
	 */
	protected Model createModel( final ImagePlus imp )
	{
		final Model model = new Model();
		model.setPhysicalUnits(
				imp.getCalibration().getUnit(),
				imp.getCalibration().getTimeUnit() );
		return model;
	}
	public static Model localcreateModel( final ImagePlus imp )
	{
		final Model model = new Model();
		model.setPhysicalUnits(
				imp.getCalibration().getUnit(),
				imp.getCalibration().getTimeUnit() );
		return model;
	}
	
	public static void ModelUpdate(final Logger logger,  final ImagePlus localimp) 
	
	{
		
		
		 settings = localcreateSettings( localimp );
		 model = localcreateModel( localimp );
		 
		 if (TrackMate.CsvSpots!=null) {
		        model.setSpots(TrackMate.CsvSpots, true);
				}
		        model.setLogger( logger );
		        
		        
		 mtrackmate = localcreateTrackMate( model, settings );
		 settings.setFrom(localimp); 
		
		 
        TrackMate updatedmtrackmate = new TrackMate(model, settings);
        final WizardSequence sequence = PseudocreateSequence( updatedmtrackmate,  new SelectionModel( model ), PseudocreateDisplaySettings());
        sequence.run( "MTrackMate");
        
		
	}
	
	
	
	protected static DisplaySettings PseudocreateDisplaySettings()
	{
		return DisplaySettingsIO.readUserDefault().copy( "CurrentDisplaySettings" );
	}
	protected  static WizardSequence PseudocreateSequence( final TrackMate mtrackmate, final SelectionModel selectionModel, final DisplaySettings displaySettings)
	{
		return new BTrackMateWizardSequence( mtrackmate, selectionModel, displaySettings);
	}
	/**
	 * Hook for subclassers: <br>
	 * Creates the {@link Settings} instance that will be used to tune the
	 * {@link TrackMate} instance. It is initialized by default with values
	 * taken from the current {@link ImagePlus}.
	 *
	 * @param imp
	 *            the {@link ImagePlus} to operate on.
	 * @return a new {@link Settings} instance.
	 */
	protected Settings createSettings( final ImagePlus imp )
	{
		final Settings ls = new Settings();
		ls.setFrom( imp );
		ls.addAllAnalyzers();
		return ls;
	}
	
	public static Settings localcreateSettings( final ImagePlus imp )
	{
		final Settings ls = new Settings();
		ls.setFrom( imp );
		ls.addAllAnalyzers();
		return ls;
	}

	/**
	 * Hook for subclassers: <br>
	 * Creates the TrackMate instance that will be controlled in the GUI.
	 *
	 * @return a new {@link TrackMate} instance.
	 */
	protected TrackMate createTrackMate( final Model model, final Settings settings )
	{
		/*
		 * Since we are now sure that we will be working on this model with this
		 * settings, we need to pass to the model the units from the settings.
		 */
		final String spaceUnits = settings.imp.getCalibration().getXUnit();
		final String timeUnits = settings.imp.getCalibration().getTimeUnit();
		model.setPhysicalUnits( spaceUnits, timeUnits );

		return new TrackMate( model, settings );
	}
	
	public static TrackMate localcreateTrackMate( final Model model, final Settings settings )
	{
		/*
		 * Since we are now sure that we will be working on this model with this
		 * settings, we need to pass to the model the units from the settings.
		 */
		final String spaceUnits = settings.imp.getCalibration().getXUnit();
		final String timeUnits = settings.imp.getCalibration().getTimeUnit();
		model.setPhysicalUnits( spaceUnits, timeUnits );

		return new TrackMate( model, settings );
	}

	protected DisplaySettings createDisplaySettings()
	{
		return DisplaySettingsIO.readUserDefault().copy( "CurrentDisplaySettings" );
	}

	public static void main( final String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		ImageJ.main( args );
//		new TrackMatePlugIn().run( "samples/Stack.tif" );
//		new TrackMatePlugIn().run( "samples/Merged.tif" );
		new TrackMatePlugIn().run("/Users/aimachine/Downloads/CellTracking/SEG-1.tif");
//		new TrackMatePlugIn().run( "samples/Mask.tif" );
//		new TrackMatePlugIn().run( "samples/FakeTracks.tif" );
	}
}
