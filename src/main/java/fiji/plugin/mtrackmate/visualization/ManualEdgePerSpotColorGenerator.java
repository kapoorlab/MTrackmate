package fiji.plugin.mtrackmate.visualization;


import java.awt.Color;

import org.jgrapht.graph.DefaultWeightedEdge;

import fiji.plugin.mtrackmate.Model;
import fiji.plugin.mtrackmate.Spot;

/**
 * Color an edge by the manual color of its target spot.
 */
public class ManualEdgePerSpotColorGenerator implements TrackColorGenerator
{
	private final Model model;

	private final ManualSpotColorGenerator manualSpotColorGenerator;

	public ManualEdgePerSpotColorGenerator( final Model model, final Color missingValueColor )
	{
		this.model = model;
		manualSpotColorGenerator = new ManualSpotColorGenerator( missingValueColor );
	}

	@Override
	public Color color( final DefaultWeightedEdge  edge)
	{
		final Spot spot = model.getTrackModel().getEdgeTarget( edge );
		return manualSpotColorGenerator.color( spot );
	}
}
