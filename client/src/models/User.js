//Object that contains user's attributes

import dayjs from "dayjs"

class Profile {

    /**
     * Create a new user
     * @param {string} email email of the user / unique identifier
     * @param {string} name first name of the user
     * @param {string} surname last name of the user
     * @param {string} birthdate birth date of the user
     * @param {string} phonenumber phone number of the user
     */
     constructor(email, name, surname, birthdate, phonenumber) {
         this.email = email
         this.name = name
         this.surname = surname
         this.birthdate = dayjs(birthdate)
         this.phonenumber = phonenumber
     }
 
     /**
      * Create a new user profile form a json object
      * @param {JSON} json object 
      * @returns {Profile} new profile object
      */
     static from(json) {
         return new Profile(json.email, json.name, json.surname, json.birthdate, json.phonenumber);
     }
 }
 
 export default Profile