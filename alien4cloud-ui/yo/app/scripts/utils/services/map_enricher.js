/* global UTILS */

'use strict';

angular.module('alienUiApp').factory('orderedMapEnricher',
function() {
  return {
    /**
    * This method is used in order to inject a map field from an array of objects with key and value fields.
    * In JSON you need, in order to keep ordering, to use array rather than map representation, however you loose the easy lookup capability of the map
    * representation.
    * The goal of this utility is to add a map field with name "propertyName_map" for a given property that is under an array of map entry representaiton.
    *
    * @param Object that contains the property to convert.
    * @param Name of the property to convert.
    */
    process: function(object, propertyName) {
      var entryArray = object[propertyName];
      if(UTILS.isDefinedAndNotNull(entryArray)) {
        var mapField = propertyName+'Map';
        var map = {};
        for(var i=0; i< entryArray.length; i++) {
          map[entryArray[i].key] = entryArray[i];
        }
        object[mapField] = map;
      }
    },
    processMap: function(map, propertyName) {
      for (var key in map) {
        if (map.hasOwnProperty(key)) {
          this.process(map[key], propertyName);
        }
      }
    },
    processArray: function(array, propertyName) {
      for(var i=0; i<array.length; i++) {
        this.process(array[i], propertyName);
      }
    }
  };
});
