// Profile dropdown logic for HTML-based menu (match home.js)
document.addEventListener('DOMContentLoaded', function () {
  const trigger = document.getElementById('profileBtn');
  const dropdown = document.getElementById('profileDropdown');
  if (!trigger || !dropdown) return;

  function openDropdown() {
    dropdown.classList.remove('hidden');
    trigger.setAttribute('aria-expanded', 'true');
    dropdown.querySelector('[role="menuitem"]')?.focus();
  }
  function closeDropdown() {
    dropdown.classList.add('hidden');
    trigger.setAttribute('aria-expanded', 'false');
  }
  trigger.addEventListener('click', function (e) {
    e.stopPropagation();
    dropdown.classList.contains('hidden') ? openDropdown() : closeDropdown();
  });
  document.addEventListener('click', function (e) {
    if (!dropdown.classList.contains('hidden') && !dropdown.contains(e.target) && e.target !== trigger) {
      closeDropdown();
    }
  });
  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') closeDropdown();
  });
});
// Theme toggle logic with persistence
const darkToggle = document.getElementById('toggleDark');
function applyTheme() {
  const theme = localStorage.getItem('theme') || 'light';
  const isDark = theme === 'dark';
  document.documentElement.classList.toggle('dark-mode', isDark);
  if (darkToggle) darkToggle.checked = isDark;
}
applyTheme();
if (darkToggle) {
  darkToggle.addEventListener('change', function() {
    const isDark = darkToggle.checked;
  document.documentElement.classList.toggle('dark-mode', isDark);
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
  });
}
const $ = (q, r=document) => r.querySelector(q);
const $$ = (q, r=document) => [...r.querySelectorAll(q)];

function toast(msg){
  const host = $("#toastHost");
  const el = document.createElement("div");
  el.className = "toast";
  el.textContent = msg;
  host.append(el);
  setTimeout(()=> el.remove(), 2500);
}

/* ---------- Password show/hide ---------- */
$$(".eye").forEach(btn => {
  const input = $(btn.dataset.target);
  btn.addEventListener("click", () => {
    input.type = input.type === "password" ? "text" : "password";
    input.focus();
  });
});

/* ---------- Password rules live validation ---------- */
const pwNew = $("#pwNew");
if (pwNew){
  pwNew.addEventListener("input", () => {
    const v = pwNew.value;
    $("#pwRules [data-rule='len']").classList.toggle("valid", v.length >= 8);
    $("#pwRules [data-rule='mix']").classList.toggle("valid", /[a-z]/.test(v) && /[A-Z]/.test(v));
    $("#pwRules [data-rule='num']").classList.toggle("valid", /\d/.test(v));
  });
}

/* ---------- Submit change password ---------- */
$("#pwForm")?.addEventListener("submit", (e) => {
  e.preventDefault();
  const cur = $("#pwCurrent").value.trim();
  const nw  = $("#pwNew").value.trim();
  const cf  = $("#pwConfirm").value.trim();
  if (!cur || !nw || !cf) return toast("Please fill all password fields");
  if (nw !== cf) return toast("New passwords do not match");
  if (nw.length < 8 || !/[a-z]/.test(nw) || !/[A-Z]/.test(nw) || !/\d/.test(nw))
    return toast("Password does not meet the requirements");
  // Send password change to backend
  fetch("/changePassword", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ current: cur, newpw: nw })
  })
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        toast("Password updated");
        e.target.reset();
      } else {
        toast(data.message || "Password update failed");
      }
    })
    .catch(() => toast("Server error"));
});

/* ---------- Preferences toggles ---------- */
const toggleDark = $("#toggleDark");
const toggleNotify = $("#toggleNotify");

// restore saved prefs
try{
  const savedDark = localStorage.getItem("pref.dark");
  if (savedDark !== null) toggleDark.checked = savedDark === "1";
  const savedNotify = localStorage.getItem("pref.notify");
  if (savedNotify !== null) toggleNotify.checked = savedNotify === "1";
}catch{}

function applyDarkMode(on){
  document.documentElement.style.colorScheme = on ? "dark" : "light";
  document.body.dataset.theme = on ? "dark" : "light";
}
applyDarkMode(toggleDark?.checked);

toggleDark?.addEventListener("change", e => {
  const on = e.target.checked;
  applyDarkMode(on);
  try{ localStorage.setItem("pref.dark", on ? "1" : "0"); }catch{}
  toast(on ? "Dark mode enabled" : "Dark mode disabled");
});

toggleNotify?.addEventListener("change", e => {
  try{ localStorage.setItem("pref.notify", e.target.checked ? "1" : "0"); }catch{}
  toast(e.target.checked ? "Notifications on" : "Notifications off");
});
