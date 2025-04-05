window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('logout')) {
        document.getElementById('logoutMessage').style.display = 'block';
    }
    if (urlParams.has('error')) { 
        document.getElementById('errorMessage').style.display = 'block';
    }
};
