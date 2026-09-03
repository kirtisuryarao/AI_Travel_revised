let travelData = {
    destination: '',
    duration: '',
    budget: '',
    interests: '',
    weather: '',
    itinerary: ''
};

let heroImageUrl = '';
let savedTripId = null;

const TRAVEL_FALLBACKS = [
    'https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=1200&q=80',
    'https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=1200&q=80',
    'https://images.unsplash.com/photo-1476514525535-07fb3b4eae5f?auto=format&fit=crop&w=1200&q=80',
    'https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=1200&q=80',
    'https://images.unsplash.com/photo-1502920917128-1aa500764cbd?auto=format&fit=crop&w=1200&q=80',
    'https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1200&q=80'
];

async function initDashboard() {
    const params = new URLSearchParams(window.location.search);
    const savedId = params.get('id');

    if (savedId) {
        await loadSavedItinerary(savedId);
        return;
    }

    const data = sessionStorage.getItem('travelData');
    if (data) {
        travelData = JSON.parse(data);
        renderDashboard();
    } else {
        showNotification('No itinerary found. Redirecting…', 'error');
        setTimeout(() => window.location.href = '/', 1800);
    }
}

async function loadSavedItinerary(id) {
    if (!isLoggedIn()) {
        window.location.href = '/login.html';
        return;
    }

    try {
        const saved = await apiFetch(`/api/itineraries/${id}`);
        travelData = {
            destination: saved.destination,
            duration: saved.duration,
            budget: saved.budget,
            interests: saved.interests,
            weather: saved.weather,
            itinerary: saved.itinerary
        };
        heroImageUrl = saved.heroImageUrl || '';
        savedTripId = saved.id;
        renderDashboard();
        setSaveStatus('Saved to your account');
        document.getElementById('saveTripBtn').textContent = 'Saved';
        document.getElementById('saveTripBtn').disabled = true;
    } catch (err) {
        showNotification(err.message, 'error');
        setTimeout(() => window.location.href = '/my-trips.html', 1600);
    }
}

function renderDashboard() {
    const destination = cleanDestination(travelData.destination);

    document.getElementById('heroDestination').textContent = destination;
    document.getElementById('heroMeta').textContent = [
        travelData.duration || 'Flexible dates',
        travelData.budget ? `Budget ${travelData.budget}` : null,
        travelData.interests || 'Open exploration'
    ].filter(Boolean).join(' · ');

    document.getElementById('factWeather').textContent = travelData.weather || '—';
    document.getElementById('factDuration').textContent = travelData.duration || '—';
    document.getElementById('factBudget').textContent = travelData.budget || '—';
    document.getElementById('factInterests').textContent = travelData.interests || '—';

    loadDestinationPhotos(destination);
    setupSaveButton();

    if (window.marked) {
        marked.setOptions({ breaks: true, gfm: true });
    }

    renderDayCards();
    parseAndRenderCards();
    renderBudgetRecommendation();
    renderBudgetChart();
    initTabs();
}

function cleanDestination(value) {
    return (value || 'Your destination').split('(')[0].trim();
}

async function loadDestinationPhotos(destination) {
    const heroImg = document.getElementById('heroImage');
    const placeholder = document.getElementById('heroPlaceholder');
    const thumbRow = document.getElementById('thumbRow');

    if (heroImageUrl) {
        setHeroImage(heroImageUrl);
    }

    let gallery = [];

    try {
        const photos = await fetch(`/api/destinations/photos?destination=${encodeURIComponent(destination)}`)
            .then(r => r.json());
        if (photos.hero) heroImageUrl = photos.hero;
        gallery = photos.gallery || [];
    } catch (err) {
        console.warn('Photo fetch failed', err);
    }

    if (!gallery.length) {
        const seed = Math.abs(destination.split('').reduce((a, c) => a + c.charCodeAt(0), 0));
        gallery = TRAVEL_FALLBACKS.map((url, i) => url.replace('w=1200', `w=1200&sig=${seed + i}`));
        if (!heroImageUrl) heroImageUrl = gallery[0];
    }

    setHeroImage(heroImageUrl || gallery[0]);

    thumbRow.innerHTML = gallery.slice(0, 5).map((url, i) => `
        <button type="button" class="it-thumb ${i === 0 ? 'active' : ''}" data-url="${url}" aria-label="Photo ${i + 1}">
            <img src="${url}" alt="" loading="lazy">
        </button>
    `).join('');

    thumbRow.querySelectorAll('.it-thumb').forEach(btn => {
        btn.addEventListener('click', () => {
            thumbRow.querySelectorAll('.it-thumb').forEach(t => t.classList.remove('active'));
            btn.classList.add('active');
            setHeroImage(btn.dataset.url);
        });
    });
}

function setHeroImage(url) {
    heroImageUrl = url;
    const heroImg = document.getElementById('heroImage');
    const placeholder = document.getElementById('heroPlaceholder');

    heroImg.src = url;
    heroImg.alt = cleanDestination(travelData.destination);
    heroImg.hidden = false;
    placeholder.style.display = 'none';

    heroImg.onerror = () => {
        const seed = Math.abs(cleanDestination(travelData.destination).length);
        heroImg.src = TRAVEL_FALLBACKS[seed % TRAVEL_FALLBACKS.length];
    };
}

function setupSaveButton() {
    const btn = document.getElementById('saveTripBtn');
    if (!btn || savedTripId) return;

    const freshBtn = btn.cloneNode(true);
    btn.parentNode.replaceChild(freshBtn, btn);

    if (!isLoggedIn()) {
        freshBtn.textContent = 'Sign in to save';
        freshBtn.addEventListener('click', () => {
            sessionStorage.setItem('travelData', JSON.stringify(travelData));
            window.location.href = '/login.html';
        });
        return;
    }

    freshBtn.addEventListener('click', saveTrip);
}

async function saveTrip() {
    const btn = document.getElementById('saveTripBtn');
    btn.disabled = true;
    btn.textContent = 'Saving…';

    try {
        const saved = await apiFetch('/api/itineraries', {
            method: 'POST',
            body: JSON.stringify({
                destination: travelData.destination,
                duration: travelData.duration,
                budget: travelData.budget,
                interests: travelData.interests,
                weather: travelData.weather,
                itinerary: travelData.itinerary,
                heroImageUrl
            })
        });
        savedTripId = saved.id;
        window.history.replaceState({}, '', `${window.location.pathname}?id=${saved.id}`);
        setSaveStatus('Saved to your account');
        showNotification('Trip saved!', 'success');
        btn.textContent = 'Saved';
    } catch (err) {
        showNotification(err.message, 'error');
        btn.disabled = false;
        btn.textContent = 'Save trip';
    }
}

function setSaveStatus(msg) {
    const el = document.getElementById('saveStatus');
    if (el) el.textContent = msg;
}

function initTabs() {
    document.querySelectorAll('.it-tab').forEach(tab => {
        tab.addEventListener('click', () => {
            document.querySelectorAll('.it-tab').forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.it-tab-panel').forEach(p => p.classList.remove('active'));
            tab.classList.add('active');
            document.getElementById(tab.dataset.panel)?.classList.add('active');
        });
    });
}

function generateMapsLink(text) {
    const query = encodeURIComponent(`${text} ${cleanDestination(travelData.destination)}`);
    return `<a href="https://www.google.com/maps/search/?api=1&query=${query}" target="_blank" rel="noopener" class="maps-btn">Map</a>`;
}

function renderMarkdown(text) {
    if (!text || !window.marked) return text || '';
    return wrapTables(marked.parse(enhanceMarkdown(text)));
}

function wrapTables(html) {
    const div = document.createElement('div');
    div.innerHTML = html;
    div.querySelectorAll('table').forEach(table => {
        const colCount = table.querySelectorAll('thead th').length
            || table.querySelectorAll('tr:first-child th').length
            || table.querySelectorAll('tr:first-child td').length;
        table.classList.add('it-data-table', `it-cols-${colCount}`);

        if (!table.parentElement?.classList.contains('it-table-wrap')) {
            const wrap = document.createElement('div');
            wrap.className = 'it-table-wrap';
            table.parentNode.insertBefore(wrap, table);
            wrap.appendChild(table);
        }
    });
    return div.innerHTML;
}

function enhanceTables(root) {
    root.querySelectorAll('table:not(.it-data-table)').forEach(table => {
        const colCount = table.querySelectorAll('thead th').length
            || table.querySelectorAll('tr:first-child th').length
            || table.querySelectorAll('tr:first-child td').length;
        table.classList.add('it-data-table', `it-cols-${colCount}`);
        if (!table.parentElement?.classList.contains('it-table-wrap')) {
            const wrap = document.createElement('div');
            wrap.className = 'it-table-wrap';
            table.parentNode.insertBefore(wrap, table);
            wrap.appendChild(table);
        }
    });
}

const TIME_ICONS = { morning: '🌅', afternoon: '☀️', evening: '🌆', night: '🌙' };

function enhanceMarkdown(text) {
    if (!text) return '';
    const lines = text.split('\n');
    let inTable = false;

    return lines.map(line => {
        if (/^\s*\|/.test(line)) {
            inTable = true;
            return line;
        }
        if (inTable && !line.trim()) {
            inTable = false;
        }
        if (inTable) return line;

        return line.replace(/\*\*(.*?)\*\*/g, (match, label) => {
            const lower = label.toLowerCase();
            if (/morning|afternoon|evening|night|day|cost|budget|time|total|₹|rs\.?|category|mode|example|reason|approx|how to use|per night|star|hotel|airbnb|transport|accommodation/.test(lower)) {
                return `**${label}**`;
            }
            return `**${label}** ${generateMapsLink(label)}`;
        });
    }).join('\n');
}

function linkifyPlaceNames(text) {
    return text.replace(/\*\*(.*?)\*\*/g, (match, label) => {
        const lower = label.toLowerCase();
        if (/morning|afternoon|evening|night|day|cost|budget|time|total|₹|rs\.?/.test(lower)) {
            return `<strong>${label}</strong>`;
        }
        return `<strong>${label}</strong>${generateMapsLink(label)}`;
    });
}

/** Turn raw day markdown into spaced time-blocks with proper bullet lists */
function structureDayContent(raw) {
    if (!raw?.trim()) return '';

    const lines = raw.split('\n');
    const intro = [];
    const blocks = [];
    let current = null;
    let looseLines = [];

    const flushLoose = () => {
        if (looseLines.length && current) {
            looseLines.forEach(l => current.items.push(l));
            looseLines = [];
        }
    };

    const startBlock = (period, inlineText) => {
        if (current) blocks.push(current);
        current = { period, items: [] };
        if (inlineText) current.items.push(inlineText);
    };

    const timeOnly = /^(?:#{1,4}\s*)?\*{0,2}\s*(Morning|Afternoon|Evening|Night)\s*\*{0,2}\s*:?\s*$/i;
    const timeInline = /^(?:#{1,4}\s*)?\*{0,2}\s*(Morning|Afternoon|Evening|Night)\s*\*{0,2}\s*:\s*(.+)$/i;
    const bullet = /^[-*•]\s+(.+)$/;
    const numbered = /^\d+[.)]\s+(.+)$/;
    const tableRow = /^\|/;
    const heading = /^#{1,4}\s+/;

    for (const line of lines) {
        const trimmed = line.trim();
        if (!trimmed) continue;

        if (tableRow.test(trimmed)) {
            flushLoose();
            if (!current) intro.push(trimmed);
            else current.items.push(trimmed);
            continue;
        }

        const inlineMatch = trimmed.match(timeInline);
        if (inlineMatch) {
            flushLoose();
            startBlock(inlineMatch[1], inlineMatch[2].replace(/\*\*/g, '').trim());
            continue;
        }

        if (timeOnly.test(trimmed)) {
            flushLoose();
            const period = trimmed.match(/(Morning|Afternoon|Evening|Night)/i)[1];
            startBlock(period, null);
            continue;
        }

        if (heading.test(trimmed) && !timeInline.test(trimmed)) {
            flushLoose();
            if (current) blocks.push(current);
            current = null;
            intro.push(trimmed.replace(/^#+\s*/, ''));
            continue;
        }

        const bulletMatch = trimmed.match(bullet) || trimmed.match(numbered);
        if (bulletMatch) {
            const item = bulletMatch[1].trim();
            if (current) current.items.push(item);
            else intro.push(item);
            continue;
        }

        if (current) {
            looseLines.push(trimmed);
        } else {
            intro.push(trimmed);
        }
    }

    flushLoose();
    if (current) blocks.push(current);

    // Fallback: let marked handle complex markdown
    if (blocks.length === 0) {
        return `<div class="it-day-markdown">${renderMarkdown(raw)}</div>`;
    }

    let html = '';

    if (intro.length) {
        const introHtml = intro
            .filter(l => !l.startsWith('|'))
            .map(l => `<p>${linkifyPlaceNames(l)}</p>`)
            .join('');
        if (introHtml) html += `<div class="it-day-intro">${introHtml}</div>`;
    }

    for (const block of blocks) {
        const key = block.period.toLowerCase();
        const icon = TIME_ICONS[key] || '🕐';
        const items = block.items.filter(i => i && !i.startsWith('|'));

        html += `
            <section class="it-time-block">
                <h4 class="it-time-label">
                    <span class="it-time-icon" aria-hidden="true">${icon}</span>
                    ${block.period}
                </h4>
                ${items.length
                    ? `<ul class="it-list">${items.map(i => `<li>${linkifyPlaceNames(i)}</li>`).join('')}</ul>`
                    : '<p class="it-time-empty">Free time — explore at your own pace.</p>'
                }
            </section>
        `;
    }

    return html;
}

function renderDayCards() {
    const container = document.getElementById('itineraryContent');
    const itinerary = travelData.itinerary || '';
    const dayRegex = /(?=^(?:### |## |\*\*|)Day \d+)/im;
    let parts = itinerary.split(dayRegex);

    if (parts.length <= 1) {
        container.innerHTML = `<div class="it-intro it-rich-content">${renderMarkdown(itinerary)}</div>`;
        enhanceTables(container);
        return;
    }

    let html = '';
    if (parts[0] && !parts[0].toLowerCase().includes('day 1')) {
        html += `<div class="it-intro it-rich-content">${renderMarkdown(parts[0])}</div>`;
        parts.shift();
    }

    parts.forEach((part, index) => {
        if (!part.trim()) return;
        const lines = part.split('\n');
        const title = lines[0].replace(/[#*]/g, '').trim() || `Day ${index + 1}`;
        const dayNum = title.match(/\d+/)?.[0] || String(index + 1);
        const content = lines.slice(1).join('\n').trim();
        const parsed = structureDayContent(content);
        const isOpen = index === 0 ? 'is-open' : '';

        html += `
            <article class="it-day ${isOpen}">
                <button type="button" class="it-day-toggle" aria-expanded="${index === 0}">
                    <span class="it-day-num">${dayNum}</span>
                    <span class="it-day-title">${title}</span>
                    <span class="it-day-chevron" aria-hidden="true">⌄</span>
                </button>
                <div class="it-day-body">${parsed}</div>
            </article>
        `;
    });

    container.innerHTML = html;
    enhanceTables(container);

    container.querySelectorAll('.it-day-toggle').forEach(btn => {
        btn.addEventListener('click', () => {
            const day = btn.closest('.it-day');
            const open = day.classList.toggle('is-open');
            btn.setAttribute('aria-expanded', String(open));
        });
    });
}

function parseAndRenderCards() {
    const itinerary = travelData.itinerary || '';
    const fallback = '<p class="muted">See the daily plan for details.</p>';

    const sections = [
        { id: 'hiddenGemsContent', regex: /(?:Hidden Gems|Off the Beaten Path|Secrets)[\s\S]*?(?=(?:###|##|Day \d+|$))/i },
        { id: 'foodContent', regex: /(?:Local Food|Dining|Where to Eat|Cuisine|Food Guide|Foods|Famous local)[\s\S]*?(?=(?:###|##|Day \d+|$))/i },
        { id: 'accommodationContent', regex: /(?:Accommodation|Hotel|Stay|Where to Stay|Lodging)[\s\S]*?(?=(?:###|##|Day \d+|$))/i },
        { id: 'transportationContent', regex: /(?:Transportation|Getting Around|Transport|Travel Tips)[\s\S]*?(?=(?:###|##|Day \d+|$))/i },
        { id: 'packingContent', regex: /(?:Packing|What to Bring|Checklist|Essentials)[\s\S]*?(?=(?:###|##|Day \d+|$))/i },
        { id: 'emergencyContent', regex: /(?:Emergency|Safety|Important Info|Warnings)[\s\S]*?(?=(?:###|##|Day \d+|$))/i }
    ];

    sections.forEach(sec => {
        const match = itinerary.match(sec.regex);
        let text = match ? match[0] : fallback;
        const lines = text.split('\n');
        if (lines.length > 0 && lines[0].match(/^(#|\*)/)) {
            lines.shift();
            text = lines.join('\n').trim();
        }
        const el = document.getElementById(sec.id);
        if (el) {
            el.innerHTML = renderMarkdown(text);
            enhanceTables(el);
        }
    });
}

function renderBudgetRecommendation() {
    const budgetVal = parseInt((travelData.budget || '').replace(/[^0-9]/g, ''), 10) || 25000;
    const estimated = Math.floor(budgetVal * 1.12);
    const recommended = Math.floor(estimated * 1.08);

    document.getElementById('recEnteredBudget').textContent = `₹${budgetVal.toLocaleString('en-IN')}`;
    document.getElementById('recEstimatedCost').textContent = `₹${estimated.toLocaleString('en-IN')}`;
    document.getElementById('recRecommendedBudget').textContent = `₹${recommended.toLocaleString('en-IN')}`;

    const el = document.getElementById('recComfortRating');
    if (budgetVal < estimated) {
        el.textContent = 'Tight — trim optional activities.';
        el.className = 'it-comfort tight';
    } else if (budgetVal > recommended * 1.15) {
        el.textContent = 'Comfortable with room to spare.';
        el.className = 'it-comfort relaxed';
    } else {
        el.textContent = 'Well balanced for this trip.';
        el.className = 'it-comfort';
    }
}

function renderBudgetChart() {
    const ctx = document.getElementById('budgetChart');
    if (!ctx || !window.Chart) return;

    const isDark = document.documentElement.classList.contains('dark-mode');
    if (window.budgetChartInstance) window.budgetChartInstance.destroy();

    window.budgetChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Stay', 'Food', 'Activities', 'Transport'],
            datasets: [{
                data: [38, 28, 22, 12],
                backgroundColor: ['#2A7A6A', '#E07A3A', '#1E4035', '#C9B8A8'],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: isDark ? '#B8B0A6' : '#5C5248',
                        boxWidth: 10,
                        font: { family: "'DM Sans', sans-serif", size: 11 }
                    }
                }
            },
            cutout: '65%'
        }
    });
}

function exportToPdf() {
    const destination = cleanDestination(travelData.destination);
    if (!travelData.itinerary) {
        showNotification('Nothing to export', 'error');
        return;
    }

    try {
        const doc = new window.jspdf.jsPDF();
        const pw = doc.internal.pageSize.getWidth();
        const ph = doc.internal.pageSize.getHeight();
        const m = 20;
        let y = m;

        doc.setFont('helvetica', 'bold');
        doc.setFontSize(22);
        doc.text(`${destination} — Itinerary`, m, y);
        y += 12;
        doc.setFont('helvetica', 'normal');
        doc.setFontSize(11);
        doc.text(`${travelData.duration || ''} · ${travelData.budget || ''}`, m, y);
        y += 10;

        for (const raw of travelData.itinerary.split('\n')) {
            const line = raw.trim();
            if (!line) continue;
            if (y > ph - m - 12) { doc.addPage(); y = m; }
            const clean = line.replace(/\*\*/g, '').replace(/^#+\s*/, '');
            const wrapped = doc.splitTextToSize(clean, pw - m * 2);
            doc.text(wrapped, m, y);
            y += wrapped.length * 5.5 + 2;
        }

        doc.save(`${destination.replace(/\s+/g, '-')}-itinerary.pdf`);
        showNotification('PDF downloaded', 'success');
    } catch (e) {
        showNotification('PDF export failed', 'error');
    }
}

document.getElementById('exportPdfBtn').addEventListener('click', exportToPdf);

function showNotification(message, type = 'info') {
    const n = document.createElement('div');
    n.className = `toast toast-${type}`;
    n.textContent = message;
    document.body.appendChild(n);
    setTimeout(() => n.remove(), 2800);
}

window.addEventListener('load', initDashboard);
