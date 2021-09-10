/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events;


/**
 * Class to persist events in the database.
 * @author Fernando
 *
 */
public interface EventPersistor {

    /**
     * Persist the reading (or printing) event in the database.
     * @param tagReport
     * @param isFillingStation
     * @return
     */
	boolean persistEvent(ExtendedTagReport tagReport, boolean isFillingStation);

}
