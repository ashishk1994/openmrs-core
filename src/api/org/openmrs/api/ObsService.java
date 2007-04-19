package org.openmrs.api;

import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.MimeType;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.ObsDAO;
import org.openmrs.logic.Aggregation;
import org.openmrs.logic.Constraint;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ObsService {
	
	public static final Integer PERSON = 1;
	public static final Integer PATIENT = 2;
	public static final Integer USER = 4;
	
	public void setObsDAO(ObsDAO dao);

	/**
	 * Create an observation
	 * 
	 * @param Obs
	 * @throws APIException
	 */
	public void createObs(Obs obs) throws APIException;

	/**
	 * Create a grouping of observations (observations linked by
	 * obs.obs_group_id)
	 * 
	 * @param obs -
	 *            array of observations to be grouped
	 * @throws APIException
	 */
	public void createObsGroup(Obs[] obs) throws APIException;

	/**
	 * Get an observation
	 * 
	 * @param integer
	 *            obsId of observation desired
	 * @return matching Obs
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	public Obs getObs(Integer obsId) throws APIException;

	/**
	 * Save changes to observation
	 * 
	 * @param Obs
	 * @throws APIException
	 */
	public void updateObs(Obs obs) throws APIException;

	/**
	 * Equivalent to deleting an observation
	 * 
	 * @param Obs
	 *            obs to void
	 * @param String
	 *            reason
	 * @throws APIException
	 */
	public void voidObs(Obs obs, String reason) throws APIException;

	/**
	 * Revive an observation (pull a Lazarus)
	 * 
	 * @param Obs
	 * @throws APIException
	 */
	public void unvoidObs(Obs obs) throws APIException;

	/**
	 * Delete an observation. SHOULD NOT BE CALLED unless caller is lower-level.
	 * 
	 * @param Obs
	 * @throws APIException
	 * @see voidObs(Obs)
	 */
	public void deleteObs(Obs obs) throws APIException;

	/**
	 * Get all mime types
	 * 
	 * @return mime types list
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	public List<MimeType> getMimeTypes() throws APIException;

	/**
	 * Get mimeType by internal identifier
	 * 
	 * @param mimeType
	 *            id
	 * @return mimeType with given internal identifier
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	public MimeType getMimeType(Integer mimeTypeId) throws APIException;

	/**
	 * Get all Observations for a person
	 * 
	 * @param who
	 * @return
	 */
	@Transactional(readOnly = true)
	public Set<Obs> getObservations(Person who);

	/**
	 * Get all Observations for this concept/location Sort is optional
	 * 
	 * @param concept
	 * @param location
	 * @param sort
	 * @param personType
	 * @return list of obs for a location
	 */
	@Transactional(readOnly = true)
	public List<Obs> getObservations(Concept c, Location loc, String sort, Integer persontType);

	/**
	 * e.g. get all CD4 counts for a person
	 * 
	 * @param who
	 * @param question
	 * @param personType
	 * @return
	 */
	@Transactional(readOnly = true)
	public Set<Obs> getObservations(Person who, Concept question);

	/**
	 * e.g. get last 'n' number of observations for a person for given concept
	 * 
	 * @param n
	 *            number of concepts to retrieve
	 * @param who
	 * @param question
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Obs> getLastNObservations(Integer n, Person who,
			Concept question);

	/**
	 * e.g. get all observations referring to RETURN VISIT DATE
	 * 
	 * @param question
	 *            (Concept: RETURN VISIT DATE)
	 * @param sort
	 * 			  (obsId, obsDatetime, etc) if null, defaults to obsId
	 * @param personType
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Obs> getObservations(Concept question, String sort, Integer personType);

	/**
	 * Return all observations that have the given concept as an answer
	 * (<code>answer.getConceptId()</code> == value_coded)
	 * 
	 * @param concept
	 * @param personType
	 * @return list of obs
	 */
	@Transactional(readOnly = true)
	public List<Obs> getObservationsAnsweredByConcept(Concept answer, Integer personType);
	
	/**
	 * Return all numeric answer values for the given concept ordered by value
	 * numeric low to high
	 * 
	 * personType should be one of PATIENT, PERSON, or USER;
	 * 
	 * @param concept
	 * @param sortByValue true/false if sorting by valueNumeric.  If false, will sort by obsDatetime
	 * @param personType
	 * 
	 * @return List<Object[]> [0]=<code>obsId</code>, [1]=<code>obsDatetime</code>, [2]=<code>valueNumeric</code>s
	 */
	@Transactional(readOnly = true)
	public List<Object[]> getNumericAnswersForConcept(Concept answer, Boolean sortByValue, Integer personType);
	
	/**
	 * Get all observations from a specific encounter
	 * 
	 * @param whichEncounter
	 * @return Set of Obs
	 */
	@Transactional(readOnly = true)
	public Set<Obs> getObservations(Encounter whichEncounter);

	/**
	 * Get all observations that have been voided Observations are ordered by
	 * descending voidedDate
	 * 
	 * @return List of Obs
	 */
	@Transactional(readOnly = true)
	public List<Obs> getVoidedObservations();

	/**
	 * Find observations matching the search string "matching" is defined as
	 * either the obsId or the person identifier
	 * 
	 * @param search
	 * @param includeVoided
	 * @param personType
	 * @return list of matched observations
	 */
	@Transactional(readOnly = true)
	public List<Obs> findObservations(String search, boolean includeVoided, Integer personType);

	/**
	 * 
	 * @param question
	 * @param personType
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<String> getDistinctObservationValues(Concept question, Integer personType);

	/**
	 * @param obsGroupId
	 * @return All obs that share obsGroupId
	 */
	@Transactional(readOnly = true)
	public List<Obs> findObsByGroupId(Integer obsGroupId);

	@Transactional(readOnly = true)
	@Authorized( { "View Person" })
	public List<Obs> getObservations(Person who, Aggregation aggregation,
			Concept question, Constraint constraint);
}