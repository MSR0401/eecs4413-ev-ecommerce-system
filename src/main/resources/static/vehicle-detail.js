function getVehicleIdFromUrl() { const urlParams = new URLSearchParams(window.location.search); return urlParams.get('id'); }
const vehicleId = getVehicleIdFromUrl();

async function fetchVehicleDetails(id) {
    const titleElement = document.getElementById('vehicleTitle');
    const stockSpan = document.getElementById('vehicleStock');
    const quantityInput = document.getElementById('quantity');
    const addToCartButton = document.getElementById('addToCartButton');
    const addToWishlistButton = document.getElementById('addToWishlistButton');

    try {
        const response = await fetch(`/api/vehicles/${id}`);
        if (!response.ok) { throw new Error(`HTTP error! status: ${response.status}`); }
        const vehicle = await response.json();

        // Populate details
        titleElement.textContent = `${vehicle.year || ''} ${vehicle.make || ''} ${vehicle.model || ''}`;
        document.getElementById('vehicleImage').src = vehicle.imageUrl || 'placeholder.jpg';
        document.getElementById('vehicleImage').alt = `${vehicle.make || ''} ${vehicle.model || ''}`;
        document.getElementById('vehicleMake').textContent = vehicle.make || 'N/A';
        document.getElementById('vehicleModel').textContent = vehicle.model || 'N/A';
        document.getElementById('vehicleYear').textContent = vehicle.year || 'N/A';
        document.getElementById('vehiclePrice').textContent = vehicle.price ? vehicle.price.toFixed(2) : 'N/A';
        document.getElementById('vehicleType').textContent = vehicle.type || 'N/A';
        document.getElementById('vehicleBattery').textContent = vehicle.batteryCapacity || 'N/A';
        document.getElementById('vehicleRange').textContent = vehicle.rangeKm || 'N/A';

        // Update stock and enable/disable buttons
        const stock = vehicle.availableStock !== null ? vehicle.availableStock : 0;
        stockSpan.textContent = stock;
        if (quantityInput) quantityInput.max = stock;

        if (stock <= 0) {
            if (addToCartButton) { addToCartButton.disabled = true; addToCartButton.textContent = 'Out of Stock'; }
            if (quantityInput) { quantityInput.disabled = true; quantityInput.value = 0; }
            if (addToWishlistButton) addToWishlistButton.disabled = false;
        } else {
            if (addToCartButton) { addToCartButton.disabled = false; addToCartButton.textContent = 'Add to Cart'; }
            if (addToWishlistButton) addToWishlistButton.disabled = false;
            if (quantityInput) {
                quantityInput.disabled = false;
                if (parseInt(quantityInput.value) > stock) quantityInput.value = stock;
                if (parseInt(quantityInput.value) < 1) quantityInput.value = 1;
            }
        }

    } catch (error) {
        console.error("Error fetching vehicle details:", error);
        titleElement.textContent = 'Error Loading Vehicle';
        document.getElementById('vehicleDetailsSection').innerHTML += `<div class="alert alert-danger">${error.message || 'Could not load vehicle details.'}</div>`;
        if (addToCartButton) addToCartButton.disabled = true;
        if (addToWishlistButton) addToWishlistButton.disabled = true;
        if (quantityInput) quantityInput.disabled = true;
    }
}

async function checkLoginStatusAndSetupForm() {
    const reviewerNameGroup = document.getElementById('reviewerNameGroup');
    reviewerNameGroup.style.display = 'none'; document.getElementById('reviewerName').required = false;
    try {
        const response = await fetch('/api/users/me');
        if (response.ok) {
            const userData = await response.json();
            if (!userData.authenticated) { reviewerNameGroup.style.display = 'block'; document.getElementById('reviewerName').required = true; }
        } else { reviewerNameGroup.style.display = 'block'; document.getElementById('reviewerName').required = true; }
    } catch (error) { console.error('Error checking auth status:', error); reviewerNameGroup.style.display = 'block'; document.getElementById('reviewerName').required = true; }
}

async function fetchAndDisplayReviews() {
    const reviewsListDiv = document.getElementById('reviewsList'); reviewsListDiv.innerHTML = '<p>Loading reviews...</p>'; if (!vehicleId) { reviewsListDiv.innerHTML = '<p class="text-danger">Could not determine vehicle ID.</p>'; return; } try { const response = await fetch(`/api/vehicles/${vehicleId}/reviews`); if (!response.ok) { if(response.status === 404) { reviewsListDiv.innerHTML = '<p>No reviews yet. Be the first!</p>'; } else { throw new Error(`HTTP error! status: ${response.status}`); } return; } const reviews = await response.json(); if (reviews.length === 0) { reviewsListDiv.innerHTML = '<p>No reviews yet. Be the first!</p>'; return; } reviewsListDiv.innerHTML = ''; reviews.forEach(review => { const reviewElement = document.createElement('div'); reviewElement.classList.add('card', 'mb-2'); let reviewerDisplayName = 'Anonymous'; if (review.userName) { reviewerDisplayName = review.userName; } else if (review.reviewerName) { reviewerDisplayName = review.reviewerName; } reviewElement.innerHTML = ` <div class="card-body"> <h5 class="card-title">Rating: ${review.rating || 'N/A'}/5</h5> <p class="card-text">${review.comment || ''}</p> <p class="card-subtitle text-muted small"> By: ${reviewerDisplayName} on ${review.createdAt ? new Date(review.createdAt).toLocaleDateString() : 'Unknown date'} </p> </div>`; reviewsListDiv.appendChild(reviewElement); }); } catch (error) { console.error('Error fetching reviews:', error); reviewsListDiv.innerHTML = '<p class="text-danger">Could not load reviews.</p>'; }
}

// Review form submission
document.getElementById('reviewForm').addEventListener('submit', async function(event) {
    event.preventDefault();
    const messageDiv = document.getElementById('reviewMessage');
    const form = event.target;
    messageDiv.style.display = 'none';
    messageDiv.classList.remove('alert-success', 'alert-danger');
    if (!vehicleId) { messageDiv.textContent = 'Error: Vehicle ID not found.'; messageDiv.className = 'alert alert-danger'; messageDiv.style.display = 'block'; return; }
    const ratingInput = document.getElementById('rating');
    const commentInput = document.getElementById('comment');
    const reviewerNameInput = document.getElementById('reviewerName');
    const reviewData = { vehicle: { id: vehicleId }, rating: ratingInput.value, comment: commentInput.value, reviewerName: reviewerNameInput.required ? reviewerNameInput.value : null };
    if (!reviewData.rating || (reviewerNameInput.required && !reviewData.reviewerName)) { messageDiv.textContent = 'Please fill in all required fields (Rating and Name if not logged in).'; messageDiv.className = 'alert alert-warning'; messageDiv.style.display = 'block'; return; }
    try {
        const response = await fetch(`/api/vehicles/${vehicleId}/reviews`, { method: 'POST', headers: { 'Content-Type': 'application/json'  }, body: JSON.stringify(reviewData) });
        const responseBody = await response.text();
        if (response.ok || response.status === 201) {
            messageDiv.textContent = 'Review submitted successfully!'; messageDiv.className = 'alert alert-success'; form.reset(); checkLoginStatusAndSetupForm(); fetchAndDisplayReviews(); setTimeout(() => { messageDiv.style.display = 'none'; }, 3000);
        } else {
            let errorMsg = `Error: ${response.statusText}`; try { const d=JSON.parse(responseBody); errorMsg = d.message||d.error||responseBody; } catch(e){} messageDiv.textContent = errorMsg; messageDiv.className = 'alert alert-danger';
        }
    } catch (error) { console.error('Review submission error:', error); messageDiv.textContent = 'An error occurred during submission.'; messageDiv.className = 'alert alert-danger'; }
    finally { messageDiv.style.display = 'block'; }
});


function showActionMessage(message, type, messageElementId) {
    const messageDiv = document.getElementById(messageElementId);
    if (!messageDiv) return;
    messageDiv.innerHTML = message;
    messageDiv.className = `alert alert-${type} action-message`;
    messageDiv.style.display = message ? 'block' : 'none';
}

const addToCartButton = document.getElementById('addToCartButton');
const quantityInput = document.getElementById('quantity');

if (addToCartButton) {
    addToCartButton.addEventListener('click', async () => {
        showActionMessage('', '', 'cartActionMessage');
        if (!vehicleId) { showActionMessage('Error: Vehicle ID missing.', 'danger', 'cartActionMessage'); return; }
        const quantity = parseInt(quantityInput.value);
        const maxStock = parseInt(quantityInput.max || '0');

        if (isNaN(quantity) || quantity < 1) { showActionMessage('Please enter a valid quantity.', 'warning', 'cartActionMessage'); return; }
        if (quantity > maxStock) { showActionMessage(`Quantity exceeds available stock (${maxStock}).`, 'warning', 'cartActionMessage'); return; }

        addToCartButton.disabled = true;
        addToCartButton.textContent = 'Adding...';

        try {
            const response = await fetch('/api/cart/items', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' /* Add CSRF if needed */ },
                body: JSON.stringify({ vehicleId: vehicleId, quantity: quantity })
            });
            const responseBodyText = await response.text();

            if (response.ok || response.status === 201) {
                alert("Vehicle has been added to the cart. Visit Home to view it");
                showActionMessage(`Added ${quantity} item(s)!`, 'success', 'cartActionMessage');
                setTimeout(() => { showActionMessage('', '', 'cartActionMessage'); }, 2000);
            } else {
                // Error handling
                let errorMessage = `Error: ${response.statusText}`;
                if (response.status === 401) { errorMessage = 'Please <a href="login.html">log in</a> to add items.'; }
                else { try { const d=JSON.parse(responseBodyText); errorMessage = d.error||d.message||responseBodyText;} catch(e){ errorMessage = responseBodyText||`Code: ${response.status}`; } }
                showActionMessage(`Failed: ${errorMessage}`, 'danger', 'cartActionMessage');
            }
        } catch (error) {
            console.error("Error adding to cart:", error);
            showActionMessage('Network error. Please try again.', 'danger', 'cartActionMessage');
        } finally {
            const currentStock = parseInt(document.getElementById('quantity').max || '0');
            if (currentStock > 0) { addToCartButton.disabled = false; addToCartButton.textContent = 'Add to Cart'; }
            else { addToCartButton.textContent = 'Out of Stock'; }
        }
    });
}

// --- Handle Add to Wishlist with Specific Alert ---
const addToWishlistButton = document.getElementById('addToWishlistButton');
if (addToWishlistButton) {
    addToWishlistButton.addEventListener('click', async () => {
        showActionMessage('', '', 'wishlistActionMessage');
        if (!vehicleId) { showActionMessage('Error: Vehicle ID missing.', 'danger', 'wishlistActionMessage'); return; }

        addToWishlistButton.disabled = true;
        const originalText = addToWishlistButton.textContent;
        addToWishlistButton.textContent = 'Adding...';

        try {
            const response = await fetch('/api/wishlist/items', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json'  },
                body: JSON.stringify({ vehicleId: vehicleId })
            });
            const responseBodyText = await response.text();

            if (response.ok || response.status === 201) {
                alert("Vehicle has been added to the wishlist and visit Home to view it");
                showActionMessage('Added to Wishlist!', 'success', 'wishlistActionMessage');
                addToWishlistButton.textContent = 'In Wishlist';
                setTimeout(() => { showActionMessage('', '', 'wishlistActionMessage'); }, 2000);
            } else {
                let errorMessage = `Error: ${response.statusText}`;
                if (response.status === 401) { errorMessage = 'Please <a href="login.html">log in</a> first.'; }
                else if (response.status === 409) { errorMessage = 'Already in wishlist.'; addToWishlistButton.textContent = 'In Wishlist'; }
                else { try { const d=JSON.parse(responseBodyText); errorMessage = d.error||d.message||responseBodyText;} catch(e){ errorMessage = responseBodyText||`Code: ${response.status}`; } }
                showActionMessage(`Failed: ${errorMessage}`, 'danger', 'wishlistActionMessage');
                addToWishlistButton.disabled = (response.status === 409);
                if(response.status !== 409) addToWishlistButton.textContent = originalText;
            }
        } catch (error) {
            console.error("Error adding to wishlist:", error);
            showActionMessage('Network error. Please try again.', 'danger', 'wishlistActionMessage');
            addToWishlistButton.disabled = false;
            addToWishlistButton.textContent = originalText;
        }
    });
}

// --- Initial Load Actions ---
document.addEventListener('DOMContentLoaded', () => {
    if (vehicleId) {
        fetchVehicleDetails(vehicleId);
        checkLoginStatusAndSetupForm();
        fetchAndDisplayReviews();
    } else {
        document.body.innerHTML = '<div class="alert alert-danger container mt-5">Error: No vehicle specified. <a href="vehicles.html">Go Back</a></div>';
    }
});
