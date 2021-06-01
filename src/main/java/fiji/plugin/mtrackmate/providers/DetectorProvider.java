package fiji.plugin.mtrackmate.providers;

import fiji.plugin.mtrackmate.detection.SpotDetectorFactoryBase;

@SuppressWarnings( "rawtypes" )
public class DetectorProvider extends AbstractProvider< SpotDetectorFactoryBase >
{

	public DetectorProvider()
	{
		super( SpotDetectorFactoryBase.class );
	}

	public static void main( final String[] args )
	{
		final DetectorProvider provider = new DetectorProvider();
		System.out.println( provider.echo() );
	}

}
