import os

os.makedirs('assets/icons', exist_ok=True)

icons = {
    # Tool 1: Text
    'tool-text.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <polyline points="4 7 4 4 20 4 20 7"/>
  <line x1="9" y1="20" x2="15" y2="20"/>
  <line x1="12" y1="4" x2="12" y2="20"/>
</svg>''',

    # Tool 2: Image
    'tool-image.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
  <circle cx="8.5" cy="8.5" r="1.5"/>
  <polyline points="21 15 16 10 5 21"/>
</svg>''',

    # Tool 3: Logo
    'tool-logo.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <polygon points="12 2 2 7 12 12 22 7 12 2"/>
  <polyline points="2 17 12 22 22 17"/>
  <polyline points="2 12 12 17 22 12"/>
</svg>''',

    # Tool 4: Sticker
    'tool-sticker.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
</svg>''',

    # Tool 5: Background
    'tool-background.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
</svg>''',

    # Tool 6: Color
    'tool-color.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <circle cx="12" cy="12" r="10"/>
  <path d="M12 2a10 10 0 0 1 10 10c0 5.523-4.477 10-10 10"/>
  <circle cx="8" cy="10" r="1.5" fill="currentColor"/>
  <circle cx="12" cy="7" r="1.5" fill="currentColor"/>
  <circle cx="16" cy="10" r="1.5" fill="currentColor"/>
  <circle cx="10" cy="15" r="1.5" fill="currentColor"/>
</svg>''',

    # Tool 7: Font
    'tool-font.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <path d="M4 19L8.5 6h2L15 19"/>
  <line x1="6" y1="14" x2="13" y2="14"/>
  <path d="M16 11c1.5-1 4-1 4 1.5v6.5"/>
  <path d="M16 15c0-1.5 1.5-2 3-2 1 0 1 .5 1 1v5"/>
  <circle cx="18" cy="16" r="2.5"/>
</svg>''',

    # Tool 8: Resize
    'tool-resize.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <polyline points="15 3 21 3 21 9"/>
  <polyline points="9 21 3 21 3 15"/>
  <line x1="21" y1="3" x2="14" y2="10"/>
  <line x1="3" y1="21" x2="10" y2="14"/>
</svg>''',

    # Bottom feature: Easy Editing
    'feature-easy.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/>
</svg>''',

    # Bottom feature: Ready Templates
    'feature-templates.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <rect x="3" y="3" width="7" height="7" rx="1"/>
  <rect x="14" y="3" width="7" height="7" rx="1"/>
  <rect x="14" y="14" width="7" height="7" rx="1"/>
  <rect x="3" y="14" width="7" height="7" rx="1"/>
</svg>''',

    # Bottom feature: High-Quality Download
    'feature-download.svg': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
  <polyline points="7 10 12 15 17 10"/>
  <line x1="12" y1="15" x2="12" y2="3"/>
</svg>'''
}

for name, svg in icons.items():
    with open(os.path.join('assets/icons', name), 'w', encoding='utf-8') as f:
        f.write(svg)

# Google Play Badge SVG
google_play_badge = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 260 76" width="260" height="76">
  <defs>
    <linearGradient id="gp_p1" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#00A0FF"/>
      <stop offset="100%" stop-color="#00E5FF"/>
    </linearGradient>
    <linearGradient id="gp_p2" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#FF3A44"/>
      <stop offset="100%" stop-color="#C01B2B"/>
    </linearGradient>
    <linearGradient id="gp_p3" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#00E676"/>
      <stop offset="100%" stop-color="#00C853"/>
    </linearGradient>
    <linearGradient id="gp_p4" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#FFC400"/>
      <stop offset="100%" stop-color="#FF8F00"/>
    </linearGradient>
  </defs>
  <!-- Background Badge Container -->
  <rect width="260" height="76" rx="18" fill="#0A0A0E" stroke="#2D2B3D" stroke-width="1.5"/>

  <!-- Google Play Color Triangle Icon -->
  <g transform="translate(22, 16)">
    <!-- Base Blue triangle -->
    <path d="M 4 2 L 28 22 L 4 42 Z" fill="#00C8FF"/>
    <!-- Yellow side triangle -->
    <path d="M 28 22 L 36 29 L 28 36 Z" fill="#FFC400"/>
    <!-- Green top part -->
    <path d="M 4 2 L 28 22 L 36 15 L 12 0 Z" fill="#00E676"/>
    <!-- Red bottom part -->
    <path d="M 4 42 L 28 22 L 36 29 L 12 44 Z" fill="#FF3A44"/>
    <!-- Highlight overlay -->
    <path d="M 4 2 L 28 22 L 4 24 Z" fill="#FFFFFF" opacity="0.15"/>
  </g>

  <!-- Typography -->
  <text x="76" y="29" fill="#A1A1AA" font-family="'Poppins', sans-serif" font-weight="500" font-size="11" letter-spacing="1">GET IT ON</text>
  <text x="76" y="55" fill="#FFFFFF" font-family="'Poppins', 'Roboto', sans-serif" font-weight="700" font-size="22" letter-spacing="0.5">Google Play</text>
</svg>'''

with open('assets/google-play-badge.svg', 'w', encoding='utf-8') as f:
    f.write(google_play_badge)

print("Icons and Google Play badge created successfully!")
