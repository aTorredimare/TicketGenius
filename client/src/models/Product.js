//Object that contains product's attributes

class Product {

    /**
     * Create a new product
     * @param {number} ean unique identifier
     * @param {string} name name of the product
     * @param {string} brand brand of the product
     */
     constructor(ean, name, brand) {
         this.ean = ean;
         this.name = name;
         this.brand = brand;
     }
 
     /**
      * Create a new product form a json object
      * @param {JSON} json object 
      * @returns {Product} new product object
      */
     static from(json) {
         return new Product(json.ean, json.name, json.brand);
     }
 }
 
 export default Product