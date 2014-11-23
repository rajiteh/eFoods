/**
 * 
 */

//for debuggin shiz
window.test = {};

//Define the app scope
var eFoods = {};
//Utility functions
eFoods.util = {};
//Standard errors -- todo:
eFoods.util.errors = {};
//Core app code
eFoods.app = {};
//App vars
eFoods.vars = {
	MAIN_URL : "backend/category",
	CONTENT_DIV : "main-content"
};

// Returns the baseURL, ex. http://localhost:4413
eFoods.util.baseURL = function() {
	path = window.location.href.split('/');
	protocol = path[0];
	host = path[2];
	return protocol + "//" + host;
}

//Ajax helper. Takes url and 2 callbacks.
eFoods.util.doAjax = function(url, data, method, onSuccess, onFailure) {

	//Default callbacks when not supplied
	if (!onSuccess) {
		onSuccess = function(request) {
			console.log("Request successful");
			console.log(request);
		}
	}
	if (!onFailure) {
		onFailure = function(request) {
			console.log("Request failed");
			console.log(request);
		}
	}
	//End default callbacks

	//Handler for xmlHttpRequest
	var handler = function(request) {
		if ((request.readyState == 4)) {
			if (request.status == 200) {
				onSuccess(request);
			} else {
				onFailure(request)
			}

		}
	}

	var request = new XMLHttpRequest();
	var postData = null;
	if (method.trim().toUpperCase() == "GET") { //If GET we append data with a '?' mark
		url += data.length > 0 ? "?" + data : ""
		request.open(method, url, true);
	} else if (method.trim().toUpperCase() == "POST") { //If POST we set the content type and assign data as PostData
		postData = data;
		request.open(method, url, true);
		request.setRequestHeader('Content-type',
				'application/x-www-form-urlencoded');
	} else {
		console.log(this, method);
		throw "dont know about that http method";
	}

	request.onreadystatechange = function() {
		handler(request);
	};
	request.send(postData);
};

//Convert values in a form in to a query string for ajax submission
eFoods.util.formToQueryString = function(form) {
	var elem = form.elements;
	var params = "";
	for (var i = 0; i < elem.length; i++) {
		if (elem[i].tagName == "SELECT") {
			value = elem[i].options[elem[i].selectedIndex].value;
		} else {
			value = elem[i].value;
		}
		params += elem[i].name + "=" + encodeURIComponent(value) + "&";
	}
	console.log('Form query string', params);
	return params;
}

//Get current app state from hashbang
eFoods.util.getState = function() {
	var state = "";
	if (window.location.hash && window.location.hash.substring(0, 2) == "#!") {
		state = window.location.hash.substring(2);
	}
	return state.length < 1 ? null : state
}

//Set hashbang state (or default state when empty) to app state 
eFoods.util.setState = function() {
	var state = this.getState();
	if (state) {
		url = state
	} else {
		url = eFoods.vars.MAIN_URL;
	}
	var ele = document.getElementById(eFoods.vars.CONTENT_DIV);
	ele.setAttribute('data-ajaxify', url)
}

//Ajaxify, looks for  the tag data-ajaxify and populates the inside via ajax usisng "GET"
eFoods.app.ajaxify = function(element) {
	var url = element.getAttribute("data-ajaxify");
	
	// Check if we're changing app state
	if (element.getAttribute("id") == eFoods.vars.CONTENT_DIV) {
		window.location.hash = "#!" + url;
	}

	eFoods.util.doAjax(url, "", "GET", function(request) {
		console.log("successfully loaded", url);
		element.innerHTML = request.responseText;
	});
};

//Ajaxify everything with the tag!
eFoods.app.ajaxifyAll = function() {
	var elements = document.querySelectorAll('[data-ajaxify]');
	for (i = 0; i < elements.length; i++) {
		(function(target) {
			eFoods.app.ajaxify(target)
		})(elements[i]); //This a little bit of js sorcery called self-invoking closure scoping.
	}
}

//Invoke this to call a hyperlink. See the dom element for the required tags. This will also refresh given dom elements on success (used to refresh cart badge and user badge) 
//ajaxifyTarget = on true, instead of refreshing the target, perform the ajax call and put the response in to the target dom element (used to change pages - category, item, car)
eFoods.app.handleHref = function(href, ajaxifyTarget) {
	try {
		var url = href.getAttribute("href");
		var refreshDoms = href.getAttribute("data-refresh-ids").split(",");
		if (ajaxifyTarget) {
			for (i = 0; i < refreshDoms.length; i++) {
				(function(target) {
					target.setAttribute("data-ajaxify", url);
					eFoods.app.ajaxify(target);
				}(document.getElementById(refreshDoms[i].trim())));
			}
		} else {
			eFoods.util.doAjax(url, "", "GET", function(request) {
				console.log("Request success!" + url);
				for (i = 0; i < refreshDoms.length; i++) {
					(function(target) {
						eFoods.app.ajaxify(target);
					})(document.getElementById(refreshDoms[i].trim()))
				}
			});
		}

	} catch (e) {
		//throw e
		console.log(e);
	}
	return false;
};

//Ajax form submission
eFoods.app.handleForm = function(form) {
	try {
		console.log(form);
		var method = form.method;
		var url = form.action;
		var refreshDoms = form.getAttribute("data-refresh-ids").split(",");
		var formData = eFoods.util.formToQueryString(form);
		console.log(method, url, refreshDoms, formData);
		eFoods.util.doAjax(url, formData, method, function(request) {
			console.log("Request success!" + url);
			for (i = 0; i < refreshDoms.length; i++) {
				(function(target) {
					eFoods.app.ajaxify(target);
				})(document.getElementById(refreshDoms[i].trim()))
			}
		});
	} catch (e) {
		console.log(e, e.stack);
	}
	return false;
};

//Watch for any changes in the hash bang, update app state if needed
eFoods.app.watchState = function() {
	var ele = document.getElementById(eFoods.vars.CONTENT_DIV);
	var interval = setInterval(function() {
	var appState = ele.getAttribute('data-ajaxify');
		if (appState != eFoods.util.getState()) {
			eFoods.util.setState();
			eFoods.app.ajaxify(ele);
		}
	}, 250)
}

//Initialization code. We'll call this at the bottom of the file.
eFoods.app.init = function() {
	eFoods.util.setState();
	eFoods.app.ajaxifyAll();
	eFoods.app.watchState();
};

eFoods.app.init();

console.log(eFoods.util.baseURL());

/* The idea for this module is as the user is typing
 * the main page is updated with foods matching the search.
 * 
 * We use a timeout to avoid sending requests for every keystroke.
 */			
var searchBar = document.getElementById('item-search');
var searchForm = document.getElementById('search-form');

console.log(searchBar);
	
var searchTimeout;
searchBar.addEventListener('keypress', function() {
	// an ajax request will be only sent only after 400ms
	if (searchTimeout) {
		clearTimeout(searchTimeout);
	}
	searchTimeout = setTimeout(function() {
		// do ajax here
		console.log('keypress');
		eFoods.app.handleForm(searchForm);
	}, 400)
})
	
