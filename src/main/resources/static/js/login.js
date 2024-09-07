import axios from 'axios';

function login(){
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const requestBody = {
        email: email,
        password: password
    };
     try {
        const response = await axios.post('/api/v1/auth/login', requestBody);
        const token = response.data.token;
        localStorage.setItem('jwtToken', token);
        alert('Login successful!');
        window.location.href = '/api/v1/templates/auth/home-page';
     } catch (error) {
        alert('Login failed: ' + error.response.data.message);
     }

}