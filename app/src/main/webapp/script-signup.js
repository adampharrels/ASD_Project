const email = document.getElementById('email');
const emailErr = document.getElementById('emailErr');
const pwd = document.getElementById('pwd');
const pwd2 = document.getElementById('pwd2');
const matchErr = document.getElementById('matchErr');
const meterBar = document.getElementById('meterBar');
const togglePwd = document.getElementById('togglePwd');
const togglePwd2 = document.getElementById('togglePwd2');

function utsEmailValid(v){
  return /@student\.uts\.edu\.au$/i.test(v.trim());
}

email.addEventListener('blur', ()=>{
  emailErr.style.display = utsEmailValid(email.value) ? 'none' : 'block';
});

// Password strength meter
function strength(v){
  let s = 0; if(v.length>=8) s+=25; if(/[A-Z]/.test(v)) s+=20; if(/[0-9]/.test(v)) s+=20; if(/[^A-Za-z0-9]/.test(v)) s+=35; return s;
}
pwd.addEventListener('input', ()=>{
  const val = strength(pwd.value); meterBar.style.width = val+'%';
  meterBar.style.background = val>80? 'var(--ok)': val>50? 'var(--warn)' : 'var(--bad)';
});

// Show/hide
const flip = (input,btn)=>{ let shown=false; btn.addEventListener('click',()=>{shown=!shown; input.type = shown? 'text':'password'; btn.textContent = shown? '🙈':'👁️';}); };
flip(pwd, togglePwd); flip(pwd2, togglePwd2);

// Match check
function checkMatch(){
  const ok = pwd.value && pwd.value === pwd2.value; matchErr.style.display = ok? 'none':'block'; return ok;
}
pwd2.addEventListener('input', checkMatch);

document.getElementById('signupForm').addEventListener('submit', (e)=>{
  e.preventDefault();
  const okEmail = utsEmailValid(email.value);
  const okMatch = checkMatch();
  emailErr.style.display = okEmail? 'none':'block';
  if(okEmail && okMatch){
    // Gather form data
    const data = {
      first: document.getElementById('first').value,
      last: document.getElementById('last').value,
      email: email.value,
      sid: document.getElementById('sid').value,
      password: pwd.value
    };
    fetch('/api/signup', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    })
    .then(res => res.json())
    .then(result => {
      if(result.success){
        alert('Account created! Redirecting to login…');
        window.location.href = 'index.html';
      } else {
        alert('Signup failed: ' + (result.message || 'Unknown error'));
      }
    })
    .catch(() => alert('Network error. Please try again.'));
  }
});