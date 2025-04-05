async function fetchVehicles() {
    document.getElementById('globalMessage').style.display = 'none';
    let url = '/api/vehicles?';
    const type = document.getElementById('typeFilter').value;
    const sortBy = document.getElementById('sortSelect').value;
    const hotDeals = document.getElementById('hotDeals').checked;
    const make = document.getElementById('makeFilter').value;
    const model = document.getElementById('modelFilter').value;
    const minPrice = document.getElementById('minPrice').value;
    const maxPrice = document.getElementById('maxPrice').value;

    if (type) url += `type=${encodeURIComponent(type)}&`;
    if (sortBy) url += `sortBy=${encodeURIComponent(sortBy)}&`;
    if (hotDeals) url += `hotDeals=true&`;
    if (make) url += `make=${encodeURIComponent(make)}&`;
    if (model) url += `model=${encodeURIComponent(model)}&`;
    if (minPrice) url += `minPrice=${encodeURIComponent(minPrice)}&`;
    if (maxPrice) url += `maxPrice=${encodeURIComponent(maxPrice)}&`;

    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        const vehicles = await response.json();
        displayVehicles(vehicles);
    } catch (error) {
        console.error("Error fetching vehicles:", error);
        const tableBody = document.querySelector('tbody');
        tableBody.innerHTML = `<tr><td colspan="7" class="text-danger">Error loading vehicles: ${error.message}. Please try again later.</td></tr>`;
    }
}

// --- displayVehicles function ---
function displayVehicles(vehicles) {
    const tableBody = document.querySelector('tbody');
    tableBody.innerHTML = '';

    if (vehicles.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="7">No vehicles found matching your criteria.</td></tr>';
        return;
    }

    vehicles.forEach(vehicle => {
        const row = document.createElement('tr');
        row.setAttribute('data-vehicle-id', vehicle.id);

        row.innerHTML = `
            <td>${vehicle.make || 'N/A'}</td>
            <td>
                <a href="vehicle-detail.html?id=${vehicle.id}">
                    ${vehicle.model || 'N/A'}
                </a>
            </td>
            <td>${vehicle.year || 'N/A'}</td>
            <td>$${vehicle.price ? vehicle.price.toFixed(2) : 'N/A'}</td>
            <td>${vehicle.type || 'N/A'}</td>
            <td><img src="${vehicle.imageUrl || 'placeholder.jpg'}" alt="${vehicle.make || ''} ${vehicle.model || ''}" width="100"></td>
            <td>
                <button class="btn btn-success btn-sm mb-1" onclick="addToCart(${vehicle.id}, this)">
                    Add to Cart
                </button>
                <span class="action-feedback feedback-success" id="cart-feedback-success-${vehicle.id}"></span>
                <span class="action-feedback feedback-error" id="cart-feedback-error-${vehicle.id}"></span>
                <br>
                <button class="btn btn-outline-primary btn-sm" onclick="addToWishlist(${vehicle.id}, this)">
                    Add to Wishlist
                </button>
                <span class="action-feedback feedback-success" id="wishlist-feedback-success-${vehicle.id}"></span>
                <span class="action-feedback feedback-error" id="wishlist-feedback-error-${vehicle.id}"></span>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

async function performApiAction(url, vehicleId, buttonElement, actionType) {
    const successFeedback = document.getElementById(`${actionType}-feedback-success-${vehicleId}`);
    const errorFeedback = document.getElementById(`${actionType}-feedback-error-${vehicleId}`);

    if (successFeedback) successFeedback.style.display = 'none';
    if (errorFeedback) errorFeedback.style.display = 'none';
    buttonElement.disabled = true;
    const originalButtonText = buttonElement.textContent;
    buttonElement.textContent = 'Processing...';
    document.getElementById('globalMessage').style.display = 'none';

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ vehicleId: vehicleId, quantity: 1 }) 
        });

        const responseBodyText = await response.text();

        if (response.ok || response.status === 201) { 
           
            if (actionType === 'cart') {
                alert("Vehicle has been added to the cart. Visit home to view it");
            } else if (actionType === 'wishlist') {
                alert("Vehicle has been added to the wishlist. Visit home to view it");
            }

            if (successFeedback) {
                successFeedback.textContent = 'Added!';
                successFeedback.style.display = 'inline';
                setTimeout(() => { successFeedback.style.display = 'none'; }, 2000);
            }
          
        } else {
           
            let errorMessage = `Error: ${response.statusText}`;
            if (response.status === 401) {
                errorMessage = 'Please log in first.';
                showGlobalMessage('Please <a href="login.html">log in</a> to perform this action.', 'warning');
            } else if (response.status === 409) {
                errorMessage = (actionType === 'wishlist') ? 'Already in Wishlist' : 'Conflict';
            } else {
                try { const d=JSON.parse(responseBodyText); errorMessage = d.error||d.message||responseBodyText;}
                catch(e){ errorMessage = responseBodyText||`Error code: ${response.status}`; }
            }
            if (errorFeedback) {
                errorFeedback.textContent = `Failed! ${errorMessage}`;
                errorFeedback.style.display = 'inline';
                setTimeout(() => { errorFeedback.style.display = 'none'; }, 3000);
            }
        }

    } catch (error) {
        console.error(`Error adding to ${actionType}:`, error);
        if (errorFeedback) {
            errorFeedback.textContent = 'Network error!';
            errorFeedback.style.display = 'inline';
            setTimeout(() => { errorFeedback.style.display = 'none'; }, 3000);
        }
    } finally {
        setTimeout(() => {
            buttonElement.disabled = false;
            buttonElement.textContent = originalButtonText;
        }, 500);
    }
}

// --- Specific Action Callers ---
function addToCart(vehicleId, buttonElement) {
    performApiAction('/api/cart/items', vehicleId, buttonElement, 'cart');
}

function addToWishlist(vehicleId, buttonElement) {
    performApiAction('/api/wishlist/items', vehicleId, buttonElement, 'wishlist');
}


// --- Function to show global messages ---
function showGlobalMessage(message, type = 'danger') {
    const globalMsgDiv = document.getElementById('globalMessage');
    globalMsgDiv.innerHTML = message;
    globalMsgDiv.className = `alert alert-${type}`;
    globalMsgDiv.style.display = 'block';
}

window.onload = fetchVehicles;
