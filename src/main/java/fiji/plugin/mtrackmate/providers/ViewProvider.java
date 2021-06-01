package fiji.plugin.mtrackmate.providers;

import fiji.plugin.mtrackmate.visualization.ViewFactory;

public class ViewProvider extends AbstractProvider< ViewFactory >
{

	public ViewProvider()
	{
		super( ViewFactory.class );
	}

	public static void main( final String[] args )
	{
		final ViewProvider provider = new ViewProvider();
		System.out.println( provider.echo() );
	}
}
