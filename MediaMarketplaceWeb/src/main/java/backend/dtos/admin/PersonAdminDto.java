package backend.dtos.admin;

import backend.dtos.PersonDto;

public class PersonAdminDto extends PersonDto {
	
	/**
	 * The unique media identifier for the person.
	 */
	private String personMediaID;

	/**
	 * Gets the unique media identifier for the person.
	 * 
	 * @return the unique media identifier of the person
	 */
	public String getPersonMediaID() {
		return personMediaID;
	}

	/**
	 * Sets the unique media identifier for the person.
	 * 
	 * @param personMediaID the unique media identifier of the person
	 */
	public void setPersonMediaID(String personMediaID) {
		this.personMediaID = personMediaID;
	}

}
