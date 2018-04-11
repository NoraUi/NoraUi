/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package cucumber.metrics.core;

/**
 * An object which maintains mean and exponentially-weighted rate.
 */
public interface Metered {

    /**
     * Returns the number of events which have been marked.
     *
     * @return the number of events which have been marked
     */
    long getCount();

}
