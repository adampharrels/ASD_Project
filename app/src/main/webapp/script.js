const form = document.getElementById('loginForm');
const email = document.getElementById('email');
const pwd = document.getElementById('password');
const emailErr = document.getElementById('emailErr');
const pwdErr = document.getElementById('pwdErr');

// Show/Hide password
const toggle = document.getElementById('togglePwd');
let revealed = false;
toggle.addEventListener('click', ()=>{
  revealed = !revealed;
  pwd.type = revealed ? 'text' : 'password';
  toggle.textContent = revealed ? '🙈' : '👁️';
});

// Simple validation
form.addEventListener('submit', (e)=>{
  e.preventDefault();
  let ok = true;
  if(!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email.value)){
    emailErr.style.display='block'; ok=false;
  } else { emailErr.style.display='none'; }

  if((pwd.value||'').length < 8){
    pwdErr.style.display='block'; ok=false;
  } else { pwdErr.style.display='none'; }

  if(ok){
    fetch('/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ email: email.value, password: pwd.value })
    })
    .then(res => res.json())
    .then(result => {
      if(result.success){
        window.location.href = 'home.html';
      } else {
        pwdErr.textContent = result.message || 'Login failed.';
        pwdErr.style.display = 'block';
      }
    })
    .catch(() => {
      pwdErr.textContent = 'Network error. Please try again.';
      pwdErr.style.display = 'block';
    });
  }
});