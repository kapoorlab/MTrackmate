package fiji.plugin.mtrackmate.gui.wizard.descriptors;

import fiji.plugin.mtrackmate.SelectionModel;
import fiji.plugin.mtrackmate.TrackMate;
import fiji.plugin.mtrackmate.gui.components.ActionChooserPanel;
import fiji.plugin.mtrackmate.gui.displaysettings.DisplaySettings;
import fiji.plugin.mtrackmate.gui.wizard.WizardPanelDescriptor;
import fiji.plugin.mtrackmate.providers.ActionProvider;

public class ActionChooserDescriptor extends WizardPanelDescriptor
{

	private static final String KEY = "Actions";

	public ActionChooserDescriptor( final ActionProvider actionProvider, final TrackMate mtrackmate, final SelectionModel selectionModel, final DisplaySettings displaySettings )
	{
		super( KEY );
		this.targetPanel = new ActionChooserPanel( actionProvider, mtrackmate, selectionModel, displaySettings );
	}
}
