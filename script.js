/**
 * Poster Maker - Promotional Creative
 * Responsive Viewport Scaling, Interactive Controls & 1080x1920 PNG Export
 */

(function () {
  'use strict';

  // DOM Elements
  const creativeCanvas = document.getElementById('creativeCanvas');
  const canvasWrapper = document.getElementById('canvasWrapper');
  const viewportStage = document.getElementById('viewportStage');
  
  // Toolbar Buttons
  const btnZoomFit = document.getElementById('btnZoomFit');
  const btnZoom50 = document.getElementById('btnZoom50');
  const btnZoom75 = document.getElementById('btnZoom75');
  const btnZoom100 = document.getElementById('btnZoom100');
  const zoomButtons = [btnZoomFit, btnZoom50, btnZoom75, btnZoom100];

  const btnToggleAnim = document.getElementById('btnToggleAnim');
  const btnToggleTheme = document.getElementById('btnToggleTheme');
  const btnExportPng = document.getElementById('btnExportPng');
  const ctaMainBtn = document.getElementById('ctaMainBtn');

  // Toast Notification
  const toast = document.getElementById('toastNotification');
  const toastMsg = document.getElementById('toastMsg');

  // State
  let currentZoomMode = 'fit'; // 'fit', '50', '75', '100'
  let animationsActive = true;
  let currentScale = 1;

  const CANVAS_WIDTH = 1080;
  const CANVAS_HEIGHT = 1920;

  /**
   * Show Toast Notification
   */
  let toastTimer = null;
  function showToast(message, isSuccess = true, duration = 3500) {
    if (toastTimer) clearTimeout(toastTimer);
    toastMsg.textContent = message;
    toast.className = 'toast-notification show' + (isSuccess ? '' : ' error');
    toastTimer = setTimeout(() => {
      toast.className = 'toast-notification';
    }, duration);
  }

  /**
   * Apply Scale to Canvas via CSS Custom Property
   */
  function applyScale(scale) {
    currentScale = scale;
    // Single source of truth: CSS handles wrapper sizing and canvas transform cleanly
    document.documentElement.style.setProperty('--preview-scale', scale);
  }

  /**
   * Calculate Best-Fit Scale for Responsive Preview
   */
  function calculateFitScale() {
    const stageWidth = viewportStage.clientWidth - 40; // padding
    const stageHeight = window.innerHeight - 64 - 80; // toolbar + padding
    
    const scaleX = stageWidth / CANVAS_WIDTH;
    const scaleY = stageHeight / CANVAS_HEIGHT;
    
    // Take the smaller scale to ensure no clipping vertically or horizontally
    let fitScale = Math.min(scaleX, scaleY);
    
    // Clamp scale so it doesn't get uncomfortably small or larger than 1.0 on large displays
    fitScale = Math.min(Math.max(fitScale, 0.25), 1.0);
    
    return fitScale;
  }

  /**
   * Update Viewport Scaling
   */
  function updateViewportScale() {
    if (currentZoomMode === 'fit') {
      const fitScale = calculateFitScale();
      applyScale(fitScale);
    } else if (currentZoomMode === '50') {
      applyScale(0.5);
    } else if (currentZoomMode === '75') {
      applyScale(0.75);
    } else if (currentZoomMode === '100') {
      applyScale(1.0);
    }
  }

  /**
   * Set Active Zoom Button
   */
  function setActiveZoomBtn(activeBtn) {
    zoomButtons.forEach(btn => btn.classList.remove('active'));
    activeBtn.classList.add('active');
  }

  // --- Zoom Event Listeners ---
  btnZoomFit.addEventListener('click', () => {
    currentZoomMode = 'fit';
    setActiveZoomBtn(btnZoomFit);
    updateViewportScale();
    showToast(`Fitted to screen (${Math.round(currentScale * 100)}%)`);
  });

  btnZoom50.addEventListener('click', () => {
    currentZoomMode = '50';
    setActiveZoomBtn(btnZoom50);
    updateViewportScale();
    showToast('Zoom set to 50%');
  });

  btnZoom75.addEventListener('click', () => {
    currentZoomMode = '75';
    setActiveZoomBtn(btnZoom75);
    updateViewportScale();
    showToast('Zoom set to 75%');
  });

  btnZoom100.addEventListener('click', () => {
    currentZoomMode = '100';
    setActiveZoomBtn(btnZoom100);
    updateViewportScale();
    showToast('Actual Size 1080 × 1920 px (100%)');
  });

  // --- Animation Toggle ---
  btnToggleAnim.addEventListener('click', () => {
    animationsActive = !animationsActive;
    if (animationsActive) {
      document.body.classList.remove('no-anim');
      btnToggleAnim.classList.add('active');
      btnToggleAnim.innerHTML = '<span class="indicator"></span> Animations: ON';
      showToast('Animations enabled');
    } else {
      document.body.classList.add('no-anim');
      btnToggleAnim.classList.remove('active');
      btnToggleAnim.innerHTML = '<span class="indicator"></span> Animations: PAUSED';
      showToast('Animations paused');
    }
  });

  // --- Studio Backdrop Theme Toggle ---
  btnToggleTheme.addEventListener('click', () => {
    document.body.classList.toggle('light-stage');
    const isLight = document.body.classList.contains('light-stage');
    showToast(isLight ? 'Studio Backdrop: Light Mode' : 'Studio Backdrop: Dark Mode');
  });

  // --- CTA Button Action ---
  ctaMainBtn.addEventListener('click', (e) => {
    e.preventDefault();
    showToast('Redirecting to Google Play Store: Poster Maker...', true, 2500);
    window.open('https://play.google.com/store/apps/details?id=com.online.flyer.design.postermaker', '_blank');
  });

  // --- Tool Items Micro-Interaction ---
  const toolItems = document.querySelectorAll('.tool-item, .floating-tool-badge');
  toolItems.forEach(item => {
    item.addEventListener('click', () => {
      const toolName = item.querySelector('.tool-name, .tool-label')?.textContent || 'Tool';
      showToast(`Selected Tool: ${toolName}`, true, 1500);
    });
  });

  // --- 8 Surrounding Posters Click / Preview Interaction ---
  const posterCards = document.querySelectorAll('.poster-card');
  posterCards.forEach(card => {
    card.addEventListener('click', () => {
      const cat = card.querySelector('.category-pill')?.textContent.trim() || 'Template';
      showToast(`Template preview: ${cat}`, true, 1800);
    });
  });

  /**
   * High-Resolution 1080 × 1920 PNG Export Function
   */
  async function exportCreativeToPng() {
    showToast('Generating 1080 × 1920 PNG... Please wait.', true, 8000);
    btnExportPng.disabled = true;
    btnExportPng.style.opacity = '0.7';

    // Save previous states
    const previousScale = currentScale;
    const wasAnimActive = animationsActive;

    // Temporarily pause animations and ensure scale is 1 for exact 1080x1920 export
    document.body.classList.add('no-anim');
    document.documentElement.style.setProperty('--preview-scale', '1');

    try {
      let dataUrl = null;

      // Method 1: Using html-to-image (High precision SVG & CSS support)
      if (typeof htmlToImage !== 'undefined') {
        dataUrl = await htmlToImage.toPng(creativeCanvas, {
          width: CANVAS_WIDTH,
          height: CANVAS_HEIGHT,
          pixelRatio: 1,
          style: {
            transform: 'none',
            transformOrigin: 'top left',
            margin: '0',
            left: '0',
            top: '0'
          }
        });
      } 
      // Method 2: Fallback to html2canvas
      else if (typeof html2canvas !== 'undefined') {
        const canvas = await html2canvas(creativeCanvas, {
          width: CANVAS_WIDTH,
          height: CANVAS_HEIGHT,
          scale: 1,
          useCORS: true,
          allowTaint: true,
          backgroundColor: null
        });
        dataUrl = canvas.toDataURL('image/png');
      }

      if (dataUrl) {
        // Trigger download
        const link = document.createElement('a');
        link.download = `Poster-Maker-Promo-1080x1920-${Date.now()}.png`;
        link.href = dataUrl;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        showToast('✓ 1080 × 1920 PNG exported successfully!', true, 4000);
      } else {
        throw new Error('Canvas render returned empty result');
      }

    } catch (err) {
      console.error('Export Error:', err);
      // Fallback: draw directly to canvas using SVG XML serialization
      showToast('Export failed. Check browser console.', false, 4000);
    } finally {
      // Restore states
      btnExportPng.disabled = false;
      btnExportPng.style.opacity = '1';
      if (wasAnimActive) {
        document.body.classList.remove('no-anim');
      }
      applyScale(previousScale);
    }
  }

  btnExportPng.addEventListener('click', exportCreativeToPng);

  // --- Window Resize Handler with Debounce ---
  let resizeDebounce = null;
  window.addEventListener('resize', () => {
    clearTimeout(resizeDebounce);
    resizeDebounce = setTimeout(() => {
      if (currentZoomMode === 'fit') {
        updateViewportScale();
      }
    }, 100);
  });

  // Initial setup on DOM ready
  window.addEventListener('DOMContentLoaded', () => {
    updateViewportScale();
  });

  // Run initial fit immediately
  updateViewportScale();

})();
