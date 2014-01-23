/**
 * Parts of http://www.jquery4u.com/snippets/create-jquery-digital-clock-jquery4u/ used
 */

var timeoffset = 0;
$.get("http://www.timeapi.org/utc/now.json", function(data) {
  timeoffset = new Date() - new Date(data.dateString);
}, "jsonp");

function updateClock( ) {
  var currentTime = new Date(new Date() - timeoffset);
  var currentHours = currentTime.getHours( );
  var currentMinutes = currentTime.getMinutes( );
  var currentSeconds = currentTime.getSeconds( );

  currentMinutes = (currentMinutes < 10 ? "0" : "") + currentMinutes;
  currentSeconds = (currentSeconds < 10 ? "0" : "") + currentSeconds;

  $("#clock").html(currentHours + ":" + currentMinutes + ":" + currentSeconds);
}
