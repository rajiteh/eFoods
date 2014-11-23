/* The idea for this module is as the user types
 * the main page is updated with foods matching the search.
 * 
 * We use a timeout to avoid sending requests for every keystroke.
 */
(function(window) {
	
	console.log("Loading search module");
	var eFoods = window.eFoods;
		
	var searchBar = document.getElementById('home-search-bar');
	
	searchBar.addEventListener('keypress', function() {
		window.setTimeout(function() {
			// do ajax here
			console.log('keypress');
		}, 200)
	})
		
}(window))();