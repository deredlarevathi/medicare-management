// ---------- helpers ----------
const $ = (id) => document.getElementById(id);
let role = "";
let bills = [];

const money = (n) => "₹" + Number(n).toFixed(2);

function esc(text) {
  const d = document.createElement("div");
  d.textContent = text == null ? "" : text;
  return d.innerHTML;
}

function show(message, isError) {
  const box = $("msg");
  box.textContent = message;
  box.className = "msg" + (isError ? " error" : "");
  box.hidden = false;
  setTimeout(() => (box.hidden = true), 3500);
}

async function api(url, method, formData) {
  const options = { method: method || "GET" };
  if (formData) {
    options.body = new URLSearchParams(formData);
    options.headers = { "Content-Type": "application/x-www-form-urlencoded" };
  }
  const res = await fetch(url, options);
  if (res.status === 401) {
    location.href = "/login.html";
    throw new Error("Please log in");
  }
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Request failed");
  return data;
}

// ---------- navigation ----------
document.querySelectorAll("nav button").forEach((btn) => {
  btn.addEventListener("click", () => {
    document.querySelectorAll("nav button").forEach((b) => b.classList.remove("active"));
    btn.classList.add("active");
    document.querySelectorAll(".view").forEach((v) => (v.hidden = true));
    $("view-" + btn.dataset.view).hidden = false;
  });
});

$("logout").addEventListener("click", async () => {
  await fetch("/api/logout", { method: "POST" });
  location.href = "/login.html";
});

// ---------- rendering ----------
function emptyRow(cols, text) {
  return `<tr><td class="empty" colspan="${cols}">${text}</td></tr>`;
}

async function loadAll() {
  try {
    const canBill = role !== "DOCTOR";
    const [patients, doctors, appts, billList] = await Promise.all([
      api("/api/patients"), api("/api/doctors"), api("/api/appointments"),
      canBill ? api("/api/bills") : Promise.resolve([])
    ]);
    bills = billList;
    renderPatients(patients);
    renderDoctors(doctors);
    renderAppointments(appts);
    renderBills(billList);
    fillSelects(patients, doctors);
    $("c-patients").textContent = patients.length;
    $("c-doctors").textContent = doctors.length;
    $("c-appts").textContent = appts.length;
    $("c-bills").textContent = billList.length;
  } catch (e) {
    show("Cannot reach the server. Is MediCareServer running and MySQL started? " + e.message, true);
  }
}

function renderPatients(list) {
  $("t-patients").innerHTML = list.length ? list.map((p) =>
    `<tr><td>${p.id}</td><td>${esc(p.name)}</td><td>${p.age}</td><td>${esc(p.gender)}</td>
     <td>${esc(p.phone)}</td><td>${esc(p.address)}</td>
     <td><button class="link admin-only" onclick="removeItem('patients',${p.id})">Delete</button></td></tr>`
  ).join("") : emptyRow(7, "No patients yet. Register the first one above.");
}

function renderDoctors(list) {
  $("t-doctors").innerHTML = list.length ? list.map((d) =>
    `<tr><td>${d.id}</td><td>${esc(d.name)}</td><td>${esc(d.specialization)}</td><td>${esc(d.phone)}</td>
     <td><button class="link admin-only" onclick="removeItem('doctors',${d.id})">Delete</button></td></tr>`
  ).join("") : emptyRow(5, "No doctors yet. Add one above.");
}

function apptRow(a, full) {
  const actions = a.status === "Scheduled"
    ? `<button class="link ok" onclick="setStatus(${a.id},'Completed')">Complete</button>
       <button class="link" onclick="setStatus(${a.id},'Cancelled')">Cancel</button>`
    : "";
  return `<tr>${full ? `<td>${esc(a.date)}</td>` : ""}<td>${esc(a.time)}</td>
    <td>${esc(a.patientName)}</td><td>${esc(a.doctorName)}</td>
    <td class="status ${esc(a.status)}">${esc(a.status)}</td>
    ${full ? `<td>${actions}<button class="link admin-only" onclick="removeItem('appointments',${a.id})">Delete</button></td>` : ""}</tr>`;
}

function renderAppointments(list) {
  $("t-appts").innerHTML = list.length
    ? list.map((a) => apptRow(a, true)).join("")
    : emptyRow(6, "No appointments yet. Book one above.");

  const today = new Date().toLocaleDateString("en-CA"); // local date as YYYY-MM-DD
  const todays = list.filter((a) => a.date === today).reverse();
  $("c-today").textContent = todays.length;
  $("t-today").innerHTML = todays.length
    ? todays.map((a) => apptRow(a, false)).join("")
    : emptyRow(4, "Nothing scheduled for today.");
}

function renderBills(list) {
  let paid = 0, due = 0;
  list.forEach((b) => { if (b.status === "Paid") paid += b.total; else due += b.total; });
  $("sum-paid").textContent = money(paid);
  $("sum-due").textContent = money(due);

  $("t-bills").innerHTML = list.length ? list.map((b) => {
    const pay = b.status === "Unpaid"
      ? `<select id="pm-${b.id}"><option>Cash</option><option>Card</option><option>UPI</option></select>
         <button class="link ok" onclick="markPaid(${b.id})">Mark paid</button>`
      : "";
    const status = b.status === "Paid" ? `Paid (${esc(b.method)})` : "Unpaid";
    return `<tr><td>#${b.id}</td><td>${esc(b.date)}</td><td>${esc(b.patientName)}</td>
      <td>${money(b.consultation)}</td><td>${money(b.medicine)}</td><td>${money(b.other)}</td>
      <td><strong>${money(b.total)}</strong></td>
      <td class="status ${b.status}">${status}</td>
      <td>${pay}<button class="link ok" onclick="printBill(${b.id})">Print</button>
      <button class="link admin-only" onclick="removeItem('bills',${b.id})">Delete</button></td></tr>`;
  }).join("") : emptyRow(9, "No bills yet. Generate the first one above.");
}

function fillSelects(patients, doctors) {
  const patientOptions = '<option value="">Select patient</option>' +
    patients.map((p) => `<option value="${p.id}">${esc(p.name)}</option>`).join("");
  $("s-patient").innerHTML = patientOptions;
  $("s-bill-patient").innerHTML = patientOptions;
  $("s-doctor").innerHTML = '<option value="">Select doctor</option>' +
    doctors.map((d) => `<option value="${d.id}">${esc(d.name)} (${esc(d.specialization)})</option>`).join("");
}

// ---------- actions ----------
async function removeItem(type, id) {
  if (!confirm("Delete this record? Related records will be removed too.")) return;
  try {
    await api(`/api/${type}?id=${id}`, "DELETE");
    show("Deleted.");
    loadAll();
  } catch (e) { show(e.message, true); }
}

async function setStatus(id, status) {
  try {
    await api("/api/appointments", "POST", { id, status });
    show("Appointment marked " + status.toLowerCase() + ".");
    loadAll();
  } catch (e) { show(e.message, true); }
}

async function markPaid(id) {
  try {
    await api("/api/bills", "POST", { id, method: $("pm-" + id).value });
    show("Payment recorded.");
    loadAll();
  } catch (e) { show(e.message, true); }
}

function printBill(id) {
  const b = bills.find((x) => x.id === id);
  if (!b) return;
  const w = window.open("", "_blank");
  w.document.write(`<!DOCTYPE html><html><head><meta charset="UTF-8"><title>Bill #${b.id}</title>
    <style>
      body{font-family:Segoe UI,Arial,sans-serif;max-width:560px;margin:40px auto;color:#1b2b34}
      h1{margin:0}.sub{color:#5d6f78;margin:2px 0 24px}
      table{width:100%;border-collapse:collapse;margin-top:16px}
      td{padding:8px 0;border-bottom:1px solid #d6dfdc}td:last-child{text-align:right}
      .total td{font-weight:bold;font-size:1.1em;border-bottom:0}
    </style></head><body>
    <h1>MediCare Hospital</h1><p class="sub">Invoice #${b.id} · ${esc(b.date)}</p>
    <p><strong>Patient:</strong> ${esc(b.patientName)}</p>
    <table>
      <tr><td>Consultation fee</td><td>${money(b.consultation)}</td></tr>
      <tr><td>Medicine charges</td><td>${money(b.medicine)}</td></tr>
      <tr><td>Other charges</td><td>${money(b.other)}</td></tr>
      <tr class="total"><td>Total</td><td>${money(b.total)}</td></tr>
    </table>
    <p>Status: ${b.status}${b.status === "Paid" ? " (" + esc(b.method) + ")" : ""}</p>
    </body></html>`);
  w.document.close();
  w.focus();
  w.print();
}

function bindForm(formId, url, okText) {
  $(formId).addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
      await api(url, "POST", new FormData(event.target));
      event.target.reset();
      show(okText);
      loadAll();
    } catch (e) { show(e.message, true); }
  });
}

bindForm("f-patient", "/api/patients", "Patient registered.");
bindForm("f-doctor", "/api/doctors", "Doctor added.");
bindForm("f-appt", "/api/appointments", "Appointment booked.");
bindForm("f-bill", "/api/bills", "Bill generated.");

// ---------- start: check login first ----------
(async function init() {
  const res = await fetch("/api/me");
  if (res.status === 401) { location.href = "/login.html"; return; }
  const me = await res.json();
  role = me.role;
  document.body.dataset.role = role;
  $("who-name").textContent = me.name;
  $("who-role").textContent = role.charAt(0) + role.slice(1).toLowerCase();
  loadAll();
})();
