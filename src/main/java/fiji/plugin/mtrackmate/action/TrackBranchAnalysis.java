package fiji.plugin.mtrackmate.action;

import static fiji.plugin.mtrackmate.gui.Icons.BRANCH_ICON_16x16;

import java.awt.Frame;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import fiji.plugin.mtrackmate.Model;
import fiji.plugin.mtrackmate.SelectionModel;
import fiji.plugin.mtrackmate.TrackMate;
import fiji.plugin.mtrackmate.gui.displaysettings.DisplaySettings;
import fiji.plugin.mtrackmate.visualization.table.BranchTableView;

public class TrackBranchAnalysis extends AbstractTMAction
{

	private static final String INFO_TEXT = "<html>"
			+ "This action analyzes each branch of all "
			+ "tracks, and outputs in an ImageJ results "
			+ "table the number of its predecessors, of "
			+ "successors, and its duration."
			+ "<p>"
			+ "The results table is in sync with the selection. "
			+ "Clicking on a line will select the target branch."
			+ "</html>";

	private static final String KEY = "TRACK_BRANCH_ANALYSIS";

	private static final String NAME = "Branch hierarchy analysis";

	@Override
	public void execute( final TrackMate mtrackmate, final SelectionModel selectionModel, final DisplaySettings displaySettings, final Frame parent )
	{
		createBranchTable( mtrackmate.getModel(), selectionModel ).render();
	}

	public static final BranchTableView createBranchTable( final Model model, final SelectionModel selectionModel )
	{
		return new BranchTableView( model, selectionModel );
	}
	
	@Plugin( type = TrackMateActionFactory.class, enabled = true )
	public static class Factory implements TrackMateActionFactory
	{

		@Override
		public String getInfoText()
		{
			return INFO_TEXT;
		}

		@Override
		public String getName()
		{
			return NAME;
		}

		@Override
		public String getKey()
		{
			return KEY;
		}

		@Override
		public TrackMateAction create()
		{
			return new TrackBranchAnalysis();
		}

		@Override
		public ImageIcon getIcon()
		{
			return BRANCH_ICON_16x16;
		}
	}
}
