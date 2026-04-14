const municipalitySelect = document.getElementById("municipalitySelect");
const barangayTable = document.getElementById("barangayTable");
const barangayTableBody = barangayTable.querySelector("tbody");
const printBtn = document.getElementById("printBtn");
const statusEl = document.getElementById("status");

const MUNICIPALITY_API = "http://localhost:8080/municipality";
const BARANGAY_API_BASE = "http://localhost:8080/barangay";
const GENERATE_API_BASE = "http://localhost:8080/generate/barangay";
const PROVINCE = "Bataan";

document.addEventListener("DOMContentLoaded", loadMunicipalities);

// -------- helpers --------
function setStatus(msg, type = "") {
  statusEl.textContent = msg || "";
  statusEl.className = `status ${type}`.trim();
}

function getSelectedDocType() {
  const checked = document.querySelector('input[name="doctype"]:checked');
  return checked ? checked.value : "";
}

function updatePrintButtonState() {
  const muni = municipalitySelect.value;
  const dt = getSelectedDocType();
  printBtn.disabled = !(muni && dt);
}

// -------- load municipalities --------
async function loadMunicipalities() {
  try {
    setStatus("Loading municipalities...");
    const response = await fetch(MUNICIPALITY_API);
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    const municipalities = await response.json();

    municipalities.forEach((municipality) => {
      const option = document.createElement("option");
      option.value = municipality.municipality;
      option.textContent = municipality.municipality;
      municipalitySelect.appendChild(option);
    });

    setStatus("");
  } catch (error) {
    console.error(error);
    setStatus("Failed to load municipalities. Check backend/CORS.", "error");
  } finally {
    updatePrintButtonState();
  }
}

// -------- when municipality changes, load barangays --------
municipalitySelect.addEventListener("change", async () => {
  const selectedMunicipality = municipalitySelect.value;

  barangayTable.hidden = true;
  barangayTableBody.innerHTML = "";

  if (!selectedMunicipality) {
    setStatus("");
    updatePrintButtonState();
    return;
  }

  try {
    setStatus(`Loading barangays for ${selectedMunicipality}...`);
    const url = `${BARANGAY_API_BASE}?municipality=${encodeURIComponent(selectedMunicipality)}&province=${encodeURIComponent(PROVINCE)}`;
    const response = await fetch(url);
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    const barangays = await response.json();

    renderBarangayTable(barangays, selectedMunicipality);
    setStatus("");
  } catch (error) {
    console.error(error);
    setStatus("Failed to load barangays. Check backend/CORS.", "error");
  } finally {
    updatePrintButtonState();
  }
});

// -------- doc type changes enable print --------
document.querySelectorAll('input[name="doctype"]').forEach((rb) => {
  rb.addEventListener("change", updatePrintButtonState);
});

// -------- render table (3 columns) --------
function renderBarangayTable(barangays, municipality) {
  barangayTableBody.innerHTML = "";

  barangays.forEach((barangay) => {
    const row = document.createElement("tr");

    const barangayCell = document.createElement("td");
    barangayCell.textContent = barangay.barangay;

    const municipalityCell = document.createElement("td");
    municipalityCell.textContent = barangay.municipality;

    const provinceCell = document.createElement("td");
    provinceCell.textContent = barangay.province;

    row.appendChild(barangayCell);
    row.appendChild(municipalityCell);
    row.appendChild(provinceCell);

    barangayTableBody.appendChild(row);
  });

  barangayTable.hidden = false;
}

// -------- print/download generated file --------
printBtn.addEventListener("click", async () => {
  const municipality = municipalitySelect.value;
  const doctype = getSelectedDocType(); // PDF or CSV

  if (!municipality || !doctype) return;

  try {
    setStatus(`Generating ${doctype}...`);
    printBtn.disabled = true;

    const url = `${GENERATE_API_BASE}?municipality=${encodeURIComponent(municipality)}&province=${encodeURIComponent(PROVINCE)}&doctype=${encodeURIComponent(doctype)}`;
    const response = await fetch(url);

    if (!response.ok) throw new Error(`HTTP ${response.status}`);

    const blob = await response.blob();

    // Use filename from response header if available; otherwise fallback
    const cd = response.headers.get("Content-Disposition") || "";
    const filenameMatch = cd.match(/filename\*?=(?:UTF-8'')?["']?([^"';]+)["']?/i);
    const fallbackName = `barangays_${municipality}_${PROVINCE}.${doctype.toLowerCase()}`;
    const filename = filenameMatch ? decodeURIComponent(filenameMatch[1]) : fallbackName;

    const blobUrl = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = blobUrl;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(blobUrl);

    setStatus(`Downloaded: ${filename}`, "success");
  } catch (error) {
    console.error(error);
    setStatus("Failed to generate/download file. Check backend/CORS.", "error");
  } finally {
    updatePrintButtonState();
  }
});