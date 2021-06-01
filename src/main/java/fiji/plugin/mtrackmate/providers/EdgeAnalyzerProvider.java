package fiji.plugin.mtrackmate.providers;

import fiji.plugin.mtrackmate.features.edges.EdgeAnalyzer;

/**
 * A provider for the edge analyzers provided in the GUI.
 */
public class EdgeAnalyzerProvider extends AbstractProvider< EdgeAnalyzer >
{

	public EdgeAnalyzerProvider()
	{
		super( EdgeAnalyzer.class );
	}

	public static void main( final String[] args )
	{
		final EdgeAnalyzerProvider provider = new EdgeAnalyzerProvider();
		System.out.println( provider.echo() );
	}
}
