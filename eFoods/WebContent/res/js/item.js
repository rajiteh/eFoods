
function setupItemsListener() {
	var items = document.getElementsByName('itemAdd');
	for (var i = 0; i < items.length; i++) {
		items[i].addEventListener('click', addItem, false);
	}
}

// Increment the cart Count
function cartCountInc() {
	var cart = document.getElementsByName('cartCount')[0];
	var countStr = cart.innerHTML;
	var count = parseInt(countStr.trim());
	count += 1;
	cart.innerHTML = count;
}

// Decrement the cart count
function cartCountDec() {
	var cart = document.getElementsByName('cartCount')[0];
	var countStr = cart.innerHTML;
	var count = parseInt(countStr.trim());
	count -= 1;
	cart.innerHTML = count;
}

// Checks the quantity is valid.
// qty is the quantity in string form
function isQuantityValid(qty) {
	if (isNaN(qty))
		return false;
	var q = parseInt(qty);
	return q > 0;
}

// Adds the item to the cart on the backend
function addItem() {
	console.log('Adding item to the cart');
	
	var pEl = this.parentElement;
	console.log(pEl);
	a = pEl;
	var qty = pEl.children[2].innerHTML.trim();
	var id = pEl.children[4].innerHTML.trim();
	
	// valid quantity
	if ( !isQuantityValid(qty) ) {
		console.log('Quantity:', qty, 'not valid, must be > 0!');
		return;
	}
	
	var data = "";
	data += ("qty=" + qty);
	data += "&";
	data += ("item=" + id);
	
	data = encodeURI(data);
	
	console.log('Sending encoded data', data);
	// Ajax to add the item into cart
	makeCartReq("http://localhost:4413/eFoods/backend/cart", 
			data, cartCountInc);
}

setupItemsListener();