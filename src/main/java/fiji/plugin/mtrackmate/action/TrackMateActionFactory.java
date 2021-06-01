package fiji.plugin.mtrackmate.action;

import fiji.plugin.mtrackmate.TrackMateModule;

public interface TrackMateActionFactory extends TrackMateModule
{
	public TrackMateAction create();
}
