# Script to generate 8 category posters + 1 editor canvas poster
import os

os.makedirs('assets/posters', exist_ok=True)

# 1. Christmas Poster
christmas_svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 840" width="600" height="840">
  <defs>
    <linearGradient id="c_bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#0F2D1F"/>
      <stop offset="50%" stop-color="#081C13"/>
      <stop offset="100%" stop-color="#1A0A0E"/>
    </linearGradient>
    <linearGradient id="c_gold" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#FDE047"/>
      <stop offset="50%" stop-color="#F59E0B"/>
      <stop offset="100%" stop-color="#D97706"/>
    </linearGradient>
    <linearGradient id="c_red" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#EF4444"/>
      <stop offset="100%" stop-color="#991B1B"/>
    </linearGradient>
    <filter id="glow" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="6" result="blur"/>
      <feComposite in="SourceGraphic" in2="blur" operator="over"/>
    </filter>
  </defs>
  <rect width="600" height="840" rx="24" fill="url(#c_bg)"/>
  
  <!-- Decorative Border -->
  <rect x="25" y="25" width="550" height="790" rx="16" fill="none" stroke="url(#c_gold)" stroke-width="2" opacity="0.6" stroke-dasharray="8 6"/>
  <rect x="35" y="35" width="530" height="770" rx="12" fill="none" stroke="url(#c_gold)" stroke-width="1" opacity="0.3"/>

  <!-- Ornaments and Baubles -->
  <line x1="160" y1="0" x2="160" y2="150" stroke="#F59E0B" stroke-width="2" opacity="0.7"/>
  <circle cx="160" cy="180" r="30" fill="url(#c_gold)" filter="url(#glow)"/>
  <circle cx="160" cy="180" r="22" fill="#FEF08A" opacity="0.6"/>

  <line x1="440" y1="0" x2="440" y2="200" stroke="#EF4444" stroke-width="2" opacity="0.7"/>
  <circle cx="440" cy="235" r="35" fill="url(#c_red)" filter="url(#glow)"/>
  <circle cx="440" cy="235" r="25" fill="#FCA5A5" opacity="0.5"/>

  <line x1="300" y1="0" x2="300" y2="110" stroke="#F59E0B" stroke-width="1.5" opacity="0.5"/>
  <circle cx="300" cy="130" r="20" fill="url(#c_gold)"/>

  <!-- Snowflakes -->
  <g fill="#FFFFFF" opacity="0.7" font-size="20" font-family="sans-serif">
    <text x="80" y="120">❄</text>
    <text x="500" y="140">❄</text>
    <text x="100" y="420" font-size="28" opacity="0.4">❄</text>
    <text x="490" y="450" font-size="24" opacity="0.4">❄</text>
    <text x="70" y="680" font-size="18">❄</text>
    <text x="520" y="700" font-size="22">❄</text>
  </g>

  <!-- Stars -->
  <g fill="#FDE047" opacity="0.9">
    <path d="M 300 240 L 305 255 L 320 260 L 305 265 L 300 280 L 295 265 L 280 260 L 295 255 Z"/>
    <path d="M 120 280 L 123 290 L 133 293 L 123 296 L 120 306 L 117 296 L 107 293 L 117 290 Z" opacity="0.6"/>
    <path d="M 480 320 L 483 330 L 493 333 L 483 336 L 480 346 L 477 336 L 467 333 L 477 330 Z" opacity="0.6"/>
  </g>

  <!-- Tree silhouette / Pine lines in background -->
  <polygon points="300,310 380,430 340,430 410,540 360,540 440,660 160,660 240,540 190,540 260,430 220,430" fill="#14532D" opacity="0.45"/>

  <!-- Badge -->
  <rect x="200" y="295" width="200" height="36" rx="18" fill="#B91C1C" stroke="url(#c_gold)" stroke-width="1.5"/>
  <text x="300" y="318" text-anchor="middle" fill="#FEF08A" font-family="'Poppins', sans-serif" font-weight="700" font-size="13" letter-spacing="3">FESTIVE GALA</text>

  <!-- Typography -->
  <text x="300" y="390" text-anchor="middle" fill="#FFFFFF" font-family="'Cinzel', 'Playfair Display', serif" font-weight="400" font-size="34" letter-spacing="4">MERRY</text>
  <text x="300" y="465" text-anchor="middle" fill="url(#c_gold)" font-family="'Playfair Display', 'Cinzel', serif" font-weight="900" font-size="58" letter-spacing="2" filter="url(#glow)">CHRISTMAS</text>
  <text x="300" y="515" text-anchor="middle" fill="#E2E8F0" font-family="'Poppins', sans-serif" font-weight="300" font-size="16" letter-spacing="6">&amp; HAPPY NEW YEAR</text>

  <line x1="180" y1="540" x2="420" y2="540" stroke="url(#c_gold)" stroke-width="1.5" opacity="0.7"/>

  <text x="300" y="585" text-anchor="middle" fill="#FEF08A" font-family="'Poppins', sans-serif" font-weight="600" font-size="18" letter-spacing="2">DECEMBER 24 • 25, 2026</text>
  <text x="300" y="618" text-anchor="middle" fill="#CBD5E1" font-family="'Poppins', sans-serif" font-weight="400" font-size="14">GRAND CITY AUDITORIUM • 7 PM</text>

  <!-- CTA Box -->
  <g transform="translate(180, 680)">
    <rect width="240" height="52" rx="26" fill="url(#c_gold)"/>
    <text x="120" y="33" text-anchor="middle" fill="#1E293B" font-family="'Poppins', sans-serif" font-weight="800" font-size="16" letter-spacing="2">JOIN CELEBRATION</text>
  </g>
</svg>'''

# 2. Halloween Poster
halloween_svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 840" width="600" height="840">
  <defs>
    <linearGradient id="h_bg" x1="0%" y1="0%" x2="0%" y2="100%">
      <stop offset="0%" stop-color="#17062B"/>
      <stop offset="60%" stop-color="#2D0B48"/>
      <stop offset="100%" stop-color="#0B0214"/>
    </linearGradient>
    <linearGradient id="h_orange" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#FF5400"/>
      <stop offset="50%" stop-color="#FF9E00"/>
      <stop offset="100%" stop-color="#FF5400"/>
    </linearGradient>
    <radialGradient id="h_moon" cx="50%" cy="50%" r="50%">
      <stop offset="0%" stop-color="#FFFBEB"/>
      <stop offset="60%" stop-color="#FDE68A"/>
      <stop offset="100%" stop-color="#F59E0B" stop-opacity="0"/>
    </radialGradient>
    <filter id="h_glow" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="8" result="blur"/>
      <feComposite in="SourceGraphic" in2="blur" operator="over"/>
    </filter>
  </defs>
  <rect width="600" height="840" rx="24" fill="url(#h_bg)"/>

  <!-- Glowing Moon -->
  <circle cx="300" cy="240" r="140" fill="url(#h_moon)" opacity="0.85"/>
  <circle cx="300" cy="240" r="100" fill="#FEF3C7" opacity="0.95"/>

  <!-- Flying Bats -->
  <g fill="#17062B" opacity="0.9">
    <path d="M 230 180 Q 245 160 260 175 Q 275 160 290 180 Q 260 195 230 180 Z"/>
    <path d="M 330 150 Q 340 135 352 145 Q 365 135 375 150 Q 352 162 330 150 Z" transform="scale(0.8) translate(80, 20)"/>
    <path d="M 120 120 Q 135 105 150 118 Q 165 105 180 120 Q 150 132 120 120 Z"/>
    <path d="M 450 110 Q 465 95 480 108 Q 495 95 510 110 Q 480 122 450 110 Z"/>
  </g>

  <!-- Spiderwebs in corners -->
  <path d="M 0 0 L 120 0 M 0 30 L 100 0 M 0 60 L 80 0 M 0 90 L 60 0 M 0 120 L 0 0" stroke="#A855F7" stroke-width="1.5" opacity="0.4"/>
  <path d="M 600 0 L 480 0 M 600 30 L 500 0 M 600 60 L 520 0 M 600 90 L 540 0 M 600 120 L 600 0" stroke="#A855F7" stroke-width="1.5" opacity="0.4"/>

  <!-- Spooky Castle / Graveyard silhouettes -->
  <path d="M 0 540 Q 150 510 300 530 Q 450 500 600 540 L 600 840 L 0 840 Z" fill="#090110"/>
  
  <!-- Jack O Lantern Pumpkin -->
  <g transform="translate(225, 330)">
    <!-- Pumpkin Body -->
    <ellipse cx="75" cy="80" rx="72" ry="58" fill="#EA580C"/>
    <ellipse cx="75" cy="80" rx="52" ry="58" fill="#F97316"/>
    <ellipse cx="75" cy="80" rx="30" ry="58" fill="#FB923C"/>
    <!-- Stem -->
    <path d="M 72 24 Q 75 8 90 12 Q 82 25 78 24 Z" fill="#15803D"/>
    <!-- Glowing Face -->
    <polygon points="50,65 62,78 40,78" fill="#FEF08A" filter="url(#h_glow)"/>
    <polygon points="100,65 110,78 88,78" fill="#FEF08A" filter="url(#h_glow)"/>
    <polygon points="75,82 82,92 68,92" fill="#FEF08A"/>
    <path d="M 45 105 Q 75 130 105 105 Q 95 118 75 120 Q 55 118 45 105 Z" fill="#FEF08A" filter="url(#h_glow)"/>
  </g>

  <!-- Typography -->
  <text x="300" y="520" text-anchor="middle" fill="#FDE047" font-family="'Poppins', sans-serif" font-weight="800" font-size="16" letter-spacing="8">SPOOKY COSTUME NIGHT</text>
  <text x="300" y="595" text-anchor="middle" fill="url(#h_orange)" font-family="'Creepster', 'Impact', sans-serif" font-weight="900" font-size="64" letter-spacing="4" filter="url(#h_glow)">HALLOWEEN</text>
  <text x="300" y="640" text-anchor="middle" fill="#FFFFFF" font-family="'Poppins', sans-serif" font-weight="600" font-size="22" letter-spacing="4">PARTY &amp; HAUNTED DJ</text>

  <line x1="160" y1="665" x2="440" y2="665" stroke="#F97316" stroke-width="2" opacity="0.8"/>

  <text x="300" y="700" text-anchor="middle" fill="#E9D5FF" font-family="'Poppins', sans-serif" font-weight="500" font-size="15" letter-spacing="2">OCTOBER 31 | 9:00 PM TILL LATE</text>
  <text x="300" y="726" text-anchor="middle" fill="#FB923C" font-family="'Poppins', sans-serif" font-weight="700" font-size="14">$1,000 BEST COSTUME PRIZE</text>

  <g transform="translate(180, 750)">
    <rect width="240" height="50" rx="25" fill="url(#h_orange)"/>
    <text x="120" y="32" text-anchor="middle" fill="#180A2A" font-family="'Poppins', sans-serif" font-weight="900" font-size="16" letter-spacing="2">GET TICKETS NOW</text>
  </g>
</svg>'''

# 3. Birthday Poster
birthday_svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 840" width="600" height="840">
  <defs>
    <linearGradient id="b_bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#FCE7F3"/>
      <stop offset="50%" stop-color="#F3E8FF"/>
      <stop offset="100%" stop-color="#E0E7FF"/>
    </linearGradient>
    <linearGradient id="b_pink" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#EC4899"/>
      <stop offset="100%" stop-color="#8B5CF6"/>
    </linearGradient>
    <linearGradient id="b_gold" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#FBBF24"/>
      <stop offset="100%" stop-color="#D97706"/>
    </linearGradient>
    <filter id="b_shadow" x="-10%" y="-10%" width="120%" height="120%">
      <feDropShadow dx="0" dy="8" stdDeviation="12" flood-color="#EC4899" flood-opacity="0.25"/>
    </filter>
  </defs>
  <rect width="600" height="840" rx="24" fill="url(#b_bg)"/>

  <!-- Confetti Elements -->
  <g opacity="0.75">
    <rect x="80" y="80" width="12" height="24" rx="3" fill="#EC4899" transform="rotate(25 80 80)"/>
    <rect x="500" y="90" width="10" height="22" rx="3" fill="#8B5CF6" transform="rotate(-35 500 90)"/>
    <rect x="120" y="240" width="14" height="8" rx="2" fill="#F59E0B" transform="rotate(45 120 240)"/>
    <rect x="470" y="260" width="16" height="10" rx="2" fill="#10B981" transform="rotate(-20 470 260)"/>
    <circle cx="160" cy="140" r="7" fill="#F43F5E"/>
    <circle cx="430" cy="120" r="8" fill="#3B82F6"/>
    <circle cx="200" cy="300" r="6" fill="#F59E0B"/>
    <circle cx="480" cy="400" r="7" fill="#EC4899"/>
    <path d="M 60 380 Q 90 350 70 320" stroke="#8B5CF6" stroke-width="3" fill="none"/>
    <path d="M 520 460 Q 550 430 530 400" stroke="#EC4899" stroke-width="3" fill="none"/>
  </g>

  <!-- 3D Balloons -->
  <!-- Left Balloon (Pink) -->
  <g transform="translate(140, 160)">
    <ellipse cx="60" cy="80" rx="55" ry="70" fill="url(#b_pink)" filter="url(#b_shadow)"/>
    <ellipse cx="45" cy="55" rx="14" ry="24" fill="#FFFFFF" opacity="0.45" transform="rotate(-25 45 55)"/>
    <polygon points="56,150 64,150 60,158" fill="#DB2777"/>
    <path d="M 60 158 Q 50 200 65 240" stroke="#CBD5E1" stroke-width="1.5" fill="none"/>
  </g>

  <!-- Right Balloon (Gold) -->
  <g transform="translate(340, 140)">
    <ellipse cx="60" cy="80" rx="58" ry="74" fill="url(#b_gold)" filter="url(#b_shadow)"/>
    <ellipse cx="44" cy="52" rx="15" ry="26" fill="#FFFFFF" opacity="0.5" transform="rotate(-25 44 52)"/>
    <polygon points="56,154 64,154 60,162" fill="#B45309"/>
    <path d="M 60 162 Q 70 210 55 250" stroke="#CBD5E1" stroke-width="1.5" fill="none"/>
  </g>

  <!-- Center Balloon (Purple Accent) -->
  <g transform="translate(240, 90)">
    <ellipse cx="60" cy="85" rx="64" ry="80" fill="#A855F7" filter="url(#b_shadow)"/>
    <ellipse cx="45" cy="55" rx="16" ry="28" fill="#FFFFFF" opacity="0.5" transform="rotate(-25 45 55)"/>
    <polygon points="56,165 64,165 60,173" fill="#7E22CE"/>
    <path d="M 60 173 Q 65 220 58 280" stroke="#CBD5E1" stroke-width="1.5" fill="none"/>
  </g>

  <!-- Ribbon Pill Badge -->
  <rect x="180" y="370" width="240" height="36" rx="18" fill="#FFFFFF" filter="url(#b_shadow)"/>
  <text x="300" y="393" text-anchor="middle" fill="#8B5CF6" font-family="'Poppins', sans-serif" font-weight="700" font-size="13" letter-spacing="3">LET'S CELEBRATE</text>

  <!-- Typography -->
  <text x="300" y="460" text-anchor="middle" fill="#1E293B" font-family="'Poppins', sans-serif" font-weight="800" font-size="34" letter-spacing="2">HAPPY</text>
  <text x="300" y="535" text-anchor="middle" fill="url(#b_pink)" font-family="'Playfair Display', serif" font-weight="900" font-size="64" font-style="italic">Birthday</text>

  <!-- Age Circle -->
  <g transform="translate(260, 560)">
    <circle cx="40" cy="40" r="38" fill="url(#b_gold)" filter="url(#b_shadow)"/>
    <circle cx="40" cy="40" r="32" fill="#FFFFFF"/>
    <text x="40" y="49" text-anchor="middle" fill="#D97706" font-family="'Poppins', sans-serif" font-weight="900" font-size="28">25</text>
  </g>

  <text x="300" y="680" text-anchor="middle" fill="#334155" font-family="'Poppins', sans-serif" font-weight="700" font-size="20">SOPHIA'S BIRTHDAY BASH</text>
  <text x="300" y="710" text-anchor="middle" fill="#64748B" font-family="'Poppins', sans-serif" font-weight="500" font-size="14">SATURDAY, AUGUST 22 • 8:00 PM</text>
  <text x="300" y="734" text-anchor="middle" fill="#94A3B8" font-family="'Poppins', sans-serif" font-weight="400" font-size="13">SKYLINE ROOFTOP LOUNGE</text>

  <g transform="translate(180, 760)">
    <rect width="240" height="48" rx="24" fill="url(#b_pink)"/>
    <text x="120" y="30" text-anchor="middle" fill="#FFFFFF" font-family="'Poppins', sans-serif" font-weight="700" font-size="15" letter-spacing="1">RSVP TODAY</text>
  </g>
</svg>'''

# 4. Business Poster
business_svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 840" width="600" height="840">
  <defs>
    <linearGradient id="bus_bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#0F172A"/>
      <stop offset="60%" stop-color="#1E293B"/>
      <stop offset="100%" stop-color="#0284C7"/>
    </linearGradient>
    <linearGradient id="bus_cyan" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#38BDF8"/>
      <stop offset="100%" stop-color="#818CF8"/>
    </linearGradient>
    <linearGradient id="bus_card" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#FFFFFF" stop-opacity="0.12"/>
      <stop offset="100%" stop-color="#FFFFFF" stop-opacity="0.03"/>
    </linearGradient>
  </defs>
  <rect width="600" height="840" rx="24" fill="url(#bus_bg)"/>

  <!-- High-tech Grid Lines -->
  <g stroke="#38BDF8" stroke-width="1" opacity="0.15">
    <line x1="0" y1="140" x2="600" y2="140"/>
    <line x1="0" y1="280" x2="600" y2="280"/>
    <line x1="0" y1="420" x2="600" y2="420"/>
    <line x1="0" y1="560" x2="600" y2="560"/>
    <line x1="0" y1="700" x2="600" y2="700"/>
    <line x1="150" y1="0" x2="150" y2="840"/>
    <line x1="300" y1="0" x2="300" y2="840"/>
    <line x1="450" y1="0" x2="450" y2="840"/>
  </g>

  <!-- Geometric Abstract 3D Cube / Shapes -->
  <g transform="translate(200, 110)">
    <polygon points="100,20 180,65 100,110 20,65" fill="#38BDF8" opacity="0.85"/>
    <polygon points="20,65 100,110 100,195 20,150" fill="#0284C7" opacity="0.9"/>
    <polygon points="180,65 100,110 100,195 180,150" fill="#0369A1" opacity="0.95"/>
    <circle cx="100" cy="110" r="40" fill="none" stroke="#F0F9FF" stroke-width="3" opacity="0.7"/>
  </g>

  <!-- Pill Tag -->
  <rect x="170" y="330" width="260" height="34" rx="17" fill="url(#bus_card)" stroke="#38BDF8" stroke-width="1.5"/>
  <text x="300" y="352" text-anchor="middle" fill="#38BDF8" font-family="'Poppins', sans-serif" font-weight="700" font-size="12" letter-spacing="4">GLOBAL TECH SUMMIT</text>

  <!-- Typography -->
  <text x="300" y="420" text-anchor="middle" fill="#F8FAFC" font-family="'Poppins', sans-serif" font-weight="900" font-size="44" letter-spacing="2">BUSINESS</text>
  <text x="300" y="475" text-anchor="middle" fill="url(#bus_cyan)" font-family="'Poppins', sans-serif" font-weight="900" font-size="44" letter-spacing="2">CONFERENCE</text>
  
  <text x="300" y="525" text-anchor="middle" fill="#94A3B8" font-family="'Poppins', sans-serif" font-weight="400" font-size="16" letter-spacing="4">INNOVATION • LEADERSHIP • AI</text>

  <!-- Glass Card with Speaker Stats -->
  <rect x="50" y="560" width="500" height="130" rx="16" fill="url(#bus_card)" stroke="#FFFFFF" stroke-width="1" stroke-opacity="0.2"/>
  
  <g transform="translate(85, 595)">
    <text x="50" y="35" text-anchor="middle" fill="#38BDF8" font-family="'Poppins', sans-serif" font-weight="800" font-size="28">50+</text>
    <text x="50" y="60" text-anchor="middle" fill="#94A3B8" font-family="'Poppins', sans-serif" font-weight="500" font-size="12">SPEAKERS</text>

    <line x1="125" y1="10" x2="125" y2="70" stroke="#475569" stroke-width="1"/>

    <text x="215" y="35" text-anchor="middle" fill="#38BDF8" font-family="'Poppins', sans-serif" font-weight="800" font-size="28">120+</text>
    <text x="215" y="60" text-anchor="middle" fill="#94A3B8" font-family="'Poppins', sans-serif" font-weight="500" font-size="12">COUNTRIES</text>

    <line x1="305" y1="10" x2="305" y2="70" stroke="#475569" stroke-width="1"/>

    <text x="380" y="35" text-anchor="middle" fill="#38BDF8" font-family="'Poppins', sans-serif" font-weight="800" font-size="28">5,000</text>
    <text x="380" y="60" text-anchor="middle" fill="#94A3B8" font-family="'Poppins', sans-serif" font-weight="500" font-size="12">ATTENDEES</text>
  </g>

  <g transform="translate(170, 730)">
    <rect width="260" height="52" rx="26" fill="url(#bus_cyan)"/>
    <text x="130" y="33" text-anchor="middle" fill="#0F172A" font-family="'Poppins', sans-serif" font-weight="800" font-size="16" letter-spacing="1">REGISTER NOW</text>
  </g>
</svg>'''

# 5. Sale Poster
sale_svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 840" width="600" height="840">
  <defs>
    <linearGradient id="s_bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#FFE000"/>
      <stop offset="60%" stop-color="#FF5E00"/>
      <stop offset="100%" stop-color="#E11D48"/>
    </linearGradient>
    <linearGradient id="s_text" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#FFFFFF"/>
      <stop offset="100%" stop-color="#FFF176"/>
    </linearGradient>
    <filter id="s_shadow" x="-10%" y="-10%" width="120%" height="120%">
      <feDropShadow dx="0" dy="10" stdDeviation="10" flood-color="#000000" flood-opacity="0.35"/>
    </filter>
  </defs>
  <rect width="600" height="840" rx="24" fill="url(#s_bg)"/>

  <!-- High-energy Dynamic Rays -->
  <g fill="#FFFFFF" opacity="0.12">
    <polygon points="300,420 0,0 120,0"/>
    <polygon points="300,420 240,0 360,0"/>
    <polygon points="300,420 480,0 600,0"/>
    <polygon points="300,420 600,150 600,270"/>
    <polygon points="300,420 600,400 600,520"/>
    <polygon points="300,420 600,680 600,800"/>
    <polygon points="300,420 480,840 360,840"/>
    <polygon points="300,420 240,840 120,840"/>
    <polygon points="300,420 0,840 0,700"/>
    <polygon points="300,420 0,550 0,420"/>
    <polygon points="300,420 0,280 0,140"/>
  </g>

  <!-- Big Sale Badge -->
  <g transform="translate(140, 70)">
    <rect width="320" height="44" rx="22" fill="#000000" filter="url(#s_shadow)"/>
    <text x="160" y="28" text-anchor="middle" fill="#FFE000" font-family="'Poppins', sans-serif" font-weight="900" font-size="16" letter-spacing="4">★ SPECIAL OFFER ★</text>
  </g>

  <!-- Typography -->
  <text x="300" y="240" text-anchor="middle" fill="#111827" font-family="'Impact', 'Arial Black', sans-serif" font-size="95" letter-spacing="6">SUPER</text>
  <text x="300" y="360" text-anchor="middle" fill="#FFFFFF" font-family="'Impact', 'Arial Black', sans-serif" font-size="120" letter-spacing="4" filter="url(#s_shadow)">SALE</text>

  <!-- Big Discount Pill / Banner -->
  <g transform="translate(70, 410)">
    <rect width="460" height="150" rx="30" fill="#000000" filter="url(#s_shadow)"/>
    <text x="230" y="80" text-anchor="middle" fill="#FFE000" font-family="'Impact', 'Arial Black', sans-serif" font-size="75">UP TO 70%</text>
    <text x="230" y="125" text-anchor="middle" fill="#FFFFFF" font-family="'Poppins', sans-serif" font-weight="800" font-size="28" letter-spacing="6">OFF EVERYTHING</text>
  </g>

  <!-- Feature Tags -->
  <g transform="translate(80, 600)">
    <rect x="0" y="0" width="135" height="46" rx="23" fill="#FFFFFF"/>
    <text x="67" y="28" text-anchor="middle" fill="#111827" font-family="'Poppins', sans-serif" font-weight="800" font-size="13">FREE SHIPPING</text>

    <rect x="150" y="0" width="140" height="46" rx="23" fill="#FFFFFF"/>
    <text x="220" y="28" text-anchor="middle" fill="#111827" font-family="'Poppins', sans-serif" font-weight="800" font-size="13">NEW ARRIVALS</text>

    <rect x="305" y="0" width="135" height="46" rx="23" fill="#FFFFFF"/>
    <text x="372" y="28" text-anchor="middle" fill="#111827" font-family="'Poppins', sans-serif" font-weight="800" font-size="13">24H ONLY</text>
  </g>

  <!-- CTA -->
  <g transform="translate(160, 710)">
    <rect width="280" height="60" rx="30" fill="#111827" filter="url(#s_shadow)"/>
    <text x="140" y="38" text-anchor="middle" fill="#FFE000" font-family="'Poppins', sans-serif" font-weight="900" font-size="20" letter-spacing="2">SHOP NOW →</text>
  </g>
</svg>'''

# 6. Wedding Poster
wedding_svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 840" width="600" height="840">
  <defs>
    <linearGradient id="w_bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#FAF5EF"/>
      <stop offset="50%" stop-color="#F5EBE1"/>
      <stop offset="100%" stop-color="#EFE2D3"/>
    </linearGradient>
    <linearGradient id="w_gold" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#D4AF37"/>
      <stop offset="50%" stop-color="#AA7C11"/>
      <stop offset="100%" stop-color="#D4AF37"/>
    </linearGradient>
  </defs>
  <rect width="600" height="840" rx="24" fill="url(#w_bg)"/>

  <!-- Delicate Gold Double Border -->
  <rect x="30" y="30" width="540" height="780" rx="16" fill="none" stroke="url(#w_gold)" stroke-width="1.5" opacity="0.6"/>
  <rect x="42" y="42" width="516" height="756" rx="12" fill="none" stroke="url(#w_gold)" stroke-width="0.8" opacity="0.4"/>

  <!-- Floral / Botanical branch motifs -->
  <g fill="none" stroke="url(#w_gold)" stroke-width="1.5" opacity="0.8">
    <!-- Top wreath -->
    <path d="M 220 120 Q 300 80 380 120"/>
    <path d="M 240 100 Q 255 85 260 105"/>
    <path d="M 280 90 Q 295 75 300 95"/>
    <path d="M 320 90 Q 335 75 340 95"/>
    <path d="M 355 100 Q 370 85 375 105"/>
    <!-- Intertwined Rings -->
    <circle cx="285" cy="180" r="24" stroke="url(#w_gold)" stroke-width="2.5"/>
    <circle cx="315" cy="180" r="24" stroke="url(#w_gold)" stroke-width="2.5"/>
  </g>

  <!-- Typography -->
  <text x="300" y="270" text-anchor="middle" fill="#8C6D37" font-family="'Cinzel', 'Playfair Display', serif" font-weight="400" font-size="18" letter-spacing="8">SAVE THE DATE</text>
  <text x="300" y="320" text-anchor="middle" fill="#71717A" font-family="'Playfair Display', serif" font-weight="400" font-size="16" font-style="italic">FOR THE WEDDING OF</text>

  <text x="300" y="410" text-anchor="middle" fill="#27272A" font-family="'Playfair Display', serif" font-weight="700" font-size="52">Elena</text>
  <text x="300" y="465" text-anchor="middle" fill="url(#w_gold)" font-family="'Great Vibes', cursive, serif" font-weight="400" font-size="44">&amp;</text>
  <text x="300" y="535" text-anchor="middle" fill="#27272A" font-family="'Playfair Display', serif" font-weight="700" font-size="52">Lucas</text>

  <line x1="200" y1="575" x2="400" y2="575" stroke="url(#w_gold)" stroke-width="1" opacity="0.6"/>

  <text x="300" y="620" text-anchor="middle" fill="#8C6D37" font-family="'Cinzel', serif" font-weight="600" font-size="18" letter-spacing="4">OCTOBER 14, 2026</text>
  <text x="300" y="655" text-anchor="middle" fill="#52525B" font-family="'Playfair Display', serif" font-weight="400" font-size="16">AT FOUR O'CLOCK IN THE AFTERNOON</text>
  <text x="300" y="690" text-anchor="middle" fill="#71717A" font-family="'Playfair Display', serif" font-weight="400" font-size="14">VILLA BELLA VISTA • FLORENCE, ITALY</text>

  <g transform="translate(200, 730)">
    <rect width="200" height="42" rx="21" fill="none" stroke="url(#w_gold)" stroke-width="1.5"/>
    <text x="100" y="26" text-anchor="middle" fill="#8C6D37" font-family="'Cinzel', serif" font-weight="600" font-size="13" letter-spacing="3">FORMAL INVITATION</text>
  </g>
</svg>'''

# 7. Travel Poster
travel_svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 840" width="600" height="840">
  <defs>
    <linearGradient id="t_bg" x1="0%" y1="0%" x2="0%" y2="100%">
      <stop offset="0%" stop-color="#0284C7"/>
      <stop offset="40%" stop-color="#06B6D4"/>
      <stop offset="70%" stop-color="#F59E0B"/>
      <stop offset="100%" stop-color="#EA580C"/>
    </linearGradient>
    <radialGradient id="t_sun" cx="50%" cy="50%" r="50%">
      <stop offset="0%" stop-color="#FEF08A"/>
      <stop offset="100%" stop-color="#F59E0B" stop-opacity="0.2"/>
    </radialGradient>
  </defs>
  <rect width="600" height="840" rx="24" fill="url(#t_bg)"/>

  <!-- Golden Sun -->
  <circle cx="300" cy="400" r="130" fill="url(#t_sun)"/>

  <!-- Tropical Mountains / Ocean Horizon -->
  <polygon points="0,520 180,440 340,510 500,430 600,500 600,600 0,600" fill="#0C4A6E" opacity="0.6"/>
  <rect x="0" y="550" width="600" height="290" fill="#0F172A" opacity="0.85"/>

  <!-- Ocean Waves -->
  <path d="M 0 570 Q 150 560 300 570 Q 450 580 600 570" stroke="#38BDF8" stroke-width="2" fill="none" opacity="0.5"/>
  <path d="M 0 600 Q 150 590 300 600 Q 450 610 600 600" stroke="#38BDF8" stroke-width="1.5" fill="none" opacity="0.3"/>

  <!-- Palm Tree Silhouettes -->
  <g fill="#020617">
    <!-- Left Palm Trunk -->
    <path d="M 60 840 Q 90 600 130 420 Q 115 600 100 840 Z"/>
    <!-- Left Palm Leaves -->
    <path d="M 130 420 Q 80 370 0 380 Q 70 410 130 420 Z"/>
    <path d="M 130 420 Q 110 330 40 310 Q 100 370 130 420 Z"/>
    <path d="M 130 420 Q 160 310 220 330 Q 170 380 130 420 Z"/>
    <path d="M 130 420 Q 200 370 250 420 Q 180 430 130 420 Z"/>
  </g>

  <!-- Flight Path -->
  <path d="M 100 200 Q 300 120 500 160" stroke="#FFFFFF" stroke-width="2" stroke-dasharray="6 6" fill="none" opacity="0.7"/>
  <!-- Airplane icon -->
  <g transform="translate(500, 160) rotate(15)">
    <path d="M 0 0 L -18 -8 L -12 0 L -24 3 L -12 3 L -18 8 Z" fill="#FFFFFF"/>
  </g>

  <!-- Pill Badge -->
  <rect x="180" y="60" width="240" height="36" rx="18" fill="#FFFFFF" opacity="0.9"/>
  <text x="300" y="83" text-anchor="middle" fill="#0284C7" font-family="'Poppins', sans-serif" font-weight="800" font-size="13" letter-spacing="4">PARADISE ESCAPE</text>

  <!-- Typography -->
  <text x="300" y="160" text-anchor="middle" fill="#FFFFFF" font-family="'Poppins', sans-serif" font-weight="900" font-size="34" letter-spacing="4">DISCOVER</text>
  <text x="300" y="250" text-anchor="middle" fill="#FEF08A" font-family="'Impact', 'Arial Black', sans-serif" font-size="82" letter-spacing="6">BALI</text>

  <text x="300" y="660" text-anchor="middle" fill="#FFFFFF" font-family="'Poppins', sans-serif" font-weight="700" font-size="22" letter-spacing="2">7 DAYS TROPICAL TOUR</text>
  <text x="300" y="695" text-anchor="middle" fill="#94A3B8" font-family="'Poppins', sans-serif" font-weight="500" font-size="14">VILLAS • SURFING • TEMPLES • WATERFALLS</text>

  <g transform="translate(180, 740)">
    <rect width="240" height="52" rx="26" fill="#F59E0B"/>
    <text x="120" y="33" text-anchor="middle" fill="#0F172A" font-family="'Poppins', sans-serif" font-weight="800" font-size="16" letter-spacing="2">BOOK VACATION</text>
  </g>
</svg>'''

# 8. Fitness Poster
fitness_svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 840" width="600" height="840">
  <defs>
    <linearGradient id="f_bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#18181B"/>
      <stop offset="50%" stop-color="#09090B"/>
      <stop offset="100%" stop-color="#18181B"/>
    </linearGradient>
    <linearGradient id="f_neon" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#22C55E"/>
      <stop offset="100%" stop-color="#A3E635"/>
    </linearGradient>
    <filter id="f_glow" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="6" result="blur"/>
      <feComposite in="SourceGraphic" in2="blur" operator="over"/>
    </filter>
  </defs>
  <rect width="600" height="840" rx="24" fill="url(#f_bg)"/>

  <!-- Dynamic Slash Accents -->
  <polygon points="520,0 600,0 480,840 400,840" fill="#22C55E" opacity="0.15"/>
  <polygon points="460,0 500,0 360,840 320,840" fill="#A3E635" opacity="0.1"/>

  <!-- Dumbbell Silhouette Visual -->
  <g transform="translate(180, 140)" fill="url(#f_neon)">
    <!-- Dumbbell Bar -->
    <rect x="70" y="45" width="100" height="10" rx="5"/>
    <!-- Left Weight Plates -->
    <rect x="50" y="25" width="14" height="50" rx="4"/>
    <rect x="30" y="15" width="14" height="70" rx="4"/>
    <!-- Right Weight Plates -->
    <rect x="176" y="25" width="14" height="50" rx="4"/>
    <rect x="196" y="15" width="14" height="70" rx="4"/>
  </g>

  <!-- Tag Pill -->
  <rect x="170" y="270" width="260" height="36" rx="18" fill="#27272A" stroke="#22C55E" stroke-width="1.5"/>
  <text x="300" y="293" text-anchor="middle" fill="#A3E635" font-family="'Poppins', sans-serif" font-weight="800" font-size="13" letter-spacing="4">NO PAIN NO GAIN</text>

  <!-- Typography -->
  <text x="300" y="380" text-anchor="middle" fill="#FFFFFF" font-family="'Impact', 'Arial Black', sans-serif" font-size="75" letter-spacing="4">FITNESS</text>
  <text x="300" y="460" text-anchor="middle" fill="url(#f_neon)" font-family="'Impact', 'Arial Black', sans-serif" font-size="82" letter-spacing="4" filter="url(#f_glow)">CLUB</text>
  
  <text x="300" y="520" text-anchor="middle" fill="#E4E4E7" font-family="'Poppins', sans-serif" font-weight="700" font-size="22" letter-spacing="3">UNLEASH YOUR POWER</text>
  
  <!-- Stats Grid -->
  <g transform="translate(60, 560)">
    <rect x="0" y="0" width="145" height="90" rx="16" fill="#27272A"/>
    <text x="72" y="40" text-anchor="middle" fill="#22C55E" font-family="'Poppins', sans-serif" font-weight="900" font-size="24">24/7</text>
    <text x="72" y="65" text-anchor="middle" fill="#A1A1AA" font-family="'Poppins', sans-serif" font-weight="600" font-size="12">OPEN ACCESS</text>

    <rect x="165" y="0" width="145" height="90" rx="16" fill="#27272A"/>
    <text x="237" y="40" text-anchor="middle" fill="#22C55E" font-family="'Poppins', sans-serif" font-weight="900" font-size="24">PRO</text>
    <text x="237" y="65" text-anchor="middle" fill="#A1A1AA" font-family="'Poppins', sans-serif" font-weight="600" font-size="12">COACHING</text>

    <rect x="330" y="0" width="145" height="90" rx="16" fill="#27272A"/>
    <text x="402" y="40" text-anchor="middle" fill="#22C55E" font-family="'Poppins', sans-serif" font-weight="900" font-size="24">FREE</text>
    <text x="402" y="65" text-anchor="middle" fill="#A1A1AA" font-family="'Poppins', sans-serif" font-weight="600" font-size="12">DAY TRIAL</text>
  </g>

  <g transform="translate(170, 710)">
    <rect width="260" height="54" rx="27" fill="url(#f_neon)"/>
    <text x="130" y="34" text-anchor="middle" fill="#09090B" font-family="'Poppins', sans-serif" font-weight="900" font-size="17" letter-spacing="2">START WORKOUT</text>
  </g>
</svg>'''

# 9. Editor Poster (for the phone screen)
editor_svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 540 860" width="540" height="860">
  <defs>
    <linearGradient id="ed_bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#4F46E5"/>
      <stop offset="50%" stop-color="#7C3AED"/>
      <stop offset="100%" stop-color="#EC4899"/>
    </linearGradient>
    <radialGradient id="ed_glow" cx="50%" cy="30%" r="50%">
      <stop offset="0%" stop-color="#FDE047"/>
      <stop offset="60%" stop-color="#FB923C"/>
      <stop offset="100%" stop-color="#EC4899" stop-opacity="0"/>
    </radialGradient>
  </defs>
  <rect width="540" height="860" rx="18" fill="url(#ed_bg)"/>

  <!-- Music Festival Visuals -->
  <circle cx="270" cy="280" r="130" fill="url(#ed_glow)" opacity="0.8"/>

  <!-- Equalizer / Wave Bars -->
  <g fill="#FFFFFF" opacity="0.35">
    <rect x="70" y="520" width="12" height="60" rx="6"/>
    <rect x="95" y="490" width="12" height="90" rx="6"/>
    <rect x="120" y="460" width="12" height="120" rx="6"/>
    <rect x="145" y="510" width="12" height="70" rx="6"/>
    <rect x="170" y="480" width="12" height="100" rx="6"/>
    <rect x="195" y="440" width="12" height="140" rx="6"/>
    <rect x="220" y="500" width="12" height="80" rx="6"/>
    <rect x="245" y="470" width="12" height="110" rx="6"/>
    <rect x="270" y="430" width="12" height="150" rx="6"/>
    <rect x="295" y="460" width="12" height="120" rx="6"/>
    <rect x="320" y="490" width="12" height="90" rx="6"/>
    <rect x="345" y="450" width="12" height="130" rx="6"/>
    <rect x="370" y="510" width="12" height="70" rx="6"/>
    <rect x="395" y="470" width="12" height="110" rx="6"/>
    <rect x="420" y="520" width="12" height="60" rx="6"/>
    <rect x="445" y="490" width="12" height="90" rx="6"/>
  </g>

  <!-- Palm silhouette at bottom -->
  <path d="M 0 740 Q 150 710 300 730 Q 450 710 540 740 L 540 860 L 0 860 Z" fill="#1E1B4B" opacity="0.8"/>

  <!-- Header text -->
  <text x="270" y="110" text-anchor="middle" fill="#FEF08A" font-family="'Poppins', sans-serif" font-weight="800" font-size="14" letter-spacing="6">SUMMER VIBES 2026</text>
  <text x="270" y="160" text-anchor="middle" fill="#FFFFFF" font-family="'Poppins', sans-serif" font-weight="900" font-size="32" letter-spacing="2">NEON NIGHTS</text>

  <!-- Main Active Selected Element (Bounding Box will be drawn around this in CSS) -->
  <text x="270" y="290" text-anchor="middle" fill="#FFFFFF" font-family="'Impact', 'Arial Black', sans-serif" font-size="64" letter-spacing="3">MUSIC</text>
  <text x="270" y="360" text-anchor="middle" fill="#FDE047" font-family="'Impact', 'Arial Black', sans-serif" font-size="64" letter-spacing="3">FESTIVAL</text>

  <!-- Sub details -->
  <text x="270" y="650" text-anchor="middle" fill="#FFFFFF" font-family="'Poppins', sans-serif" font-weight="700" font-size="18" letter-spacing="2">JULY 18 - 20 • MIAMI BEACH</text>
  <text x="270" y="685" text-anchor="middle" fill="#CBD5E1" font-family="'Poppins', sans-serif" font-weight="500" font-size="13">25+ INTERNATIONAL DJS &amp; ARTISTS</text>

  <!-- Button -->
  <g transform="translate(170, 770)">
    <rect width="200" height="44" rx="22" fill="#FDE047"/>
    <text x="100" y="28" text-anchor="middle" fill="#1E1B4B" font-family="'Poppins', sans-serif" font-weight="800" font-size="14" letter-spacing="1">GET PASSES</text>
  </g>
</svg>'''

posters = {
    'christmas.svg': christmas_svg,
    'halloween.svg': halloween_svg,
    'birthday.svg': birthday_svg,
    'business.svg': business_svg,
    'sale.svg': sale_svg,
    'wedding.svg': wedding_svg,
    'travel.svg': travel_svg,
    'fitness.svg': fitness_svg,
    'editor-poster.svg': editor_svg,
}

for name, content in posters.items():
    path = os.path.join('assets/posters', name)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Written: {path}")

print("All 9 posters created successfully!")
