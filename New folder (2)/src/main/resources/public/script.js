async function getJSON(url, opts){
  const r = await fetch(url, opts);
  const ct = r.headers.get('content-type')||'';
  if (ct.includes('application/json')) return r.json();
  return r.text();
}

async function refreshStudents(){
  const data = await getJSON('/api/students');
  const tbl = document.getElementById('tbl');
  tbl.innerHTML = '';
  if (!data || data.length===0){ tbl.innerHTML = '<tr><td>No data</td></tr>'; return; }
  const subjects = new Set();
  data.forEach(s=>Object.keys(s.marks||{}).forEach(k=>subjects.add(k)));
  const sub = Array.from(subjects);
  const header = document.createElement('tr');
  ;['id','name',...sub,'total','average','grade'].forEach(h=>{const th=document.createElement('th');th.textContent=h;header.appendChild(th);});
  tbl.appendChild(header);
  data.forEach(s=>{
    const tr = document.createElement('tr');
    const cells = [s.id,s.name,...sub.map(k=>s.marks?.[k]??''),s.total,s.average.toFixed(2),s.grade];
    cells.forEach(v=>{const td=document.createElement('td');td.textContent=v;tr.appendChild(td);});
    tbl.appendChild(tr);
  });
}

async function refreshMetrics(){
  const m = await getJSON('/api/metrics');
  document.getElementById('metrics').textContent = JSON.stringify(m,null,2);
}

setInterval(refreshMetrics, 2000);
setInterval(refreshStudents, 5000);

document.getElementById('btn-generate').onclick = async () => {
  const count = parseInt(document.getElementById('count').value||'50',10);
  const subjects = document.getElementById('subjects').value.split(',').map(s=>s.trim()).filter(Boolean);
  await getJSON('/api/generate', {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({count, subjects})});
  await refreshStudents();
};

document.getElementById('btn-import').onclick = async () => {
  const csv = document.getElementById('csv').value;
  await getJSON('/api/import', {method:'POST', headers:{'Content-Type':'text/plain'}, body: csv});
  await refreshStudents();
};

document.getElementById('btn-export').onclick = async () => {
  const r = await fetch('/api/export');
  const blob = await r.blob();
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url; a.download = 'results.xlsx';
  document.body.appendChild(a); a.click(); a.remove();
  URL.revokeObjectURL(url);
};

refreshStudents();
refreshMetrics();
