package ie.ucd.cobweb.cobwebsocial;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import buildingblocks.DiscoveryReceiver;

public class Activator implements BundleActivator {

	private DiscoveryReceiver discoveryReceiver;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		discoveryReceiver = new DiscoveryReceiver(context);
		new App_Flooding(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		discoveryReceiver.unregister();
	}

}
