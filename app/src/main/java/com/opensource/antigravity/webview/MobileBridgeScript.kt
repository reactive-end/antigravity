package com.opensource.antigravity.webview

object MobileBridgeScript {

    const val INJECT_SCRIPT: String = """
        (function() {
          try {
            var vp = document.querySelector('meta[name="viewport"]');
            var content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, viewport-fit=cover';
            if (!vp && document.head) {
              vp = document.createElement('meta');
              vp.name = 'viewport';
              vp.content = content;
              document.head.appendChild(vp);
            } else if (vp && vp.content.indexOf('viewport-fit=cover') === -1) {
              vp.content = content;
            }

            var styleId = 'antigravity-mobile-polish';
            if (!document.getElementById(styleId) && document.head) {
              var style = document.createElement('style');
              style.id = styleId;
              style.textContent = [
                ':root { color-scheme: dark; }',
                '*, *::before, *::after { backdrop-filter: none !important; -webkit-backdrop-filter: none !important; }',
                'html, body { max-width: 100vw; overflow-x: clip; -webkit-tap-highlight-color: transparent; touch-action: manipulation; overscroll-behavior-y: contain; }',
                'pre, table { max-width: 100%; overflow-x: auto; -webkit-overflow-scrolling: touch; }',
                'code { word-break: break-word; }',
                'pre code { word-break: normal; }',
                'input, textarea, [contenteditable="true"] { font-size: 16px; }',
                '::-webkit-scrollbar { width: 3px; height: 3px; }',
                '::-webkit-scrollbar-thumb { background: rgba(138,143,158,0.28); border-radius: 3px; }'
              ].join('\n');
              document.head.appendChild(style);
            }

            if (!window.__AGY_NOTIF_BRIDGE && window.AntigravityNativeBridge) {
              window.__AGY_NOTIF_BRIDGE = true;
              window.__AGY_BG = false;
              var baselineSig = '';
              var prevTickSig = '';
              var hasChangedInBg = false;
              var lastSnippet = '';

              var emitNative = function(title, body) {
                try {
                  window.AntigravityNativeBridge.notify(
                    String(title || 'Antigravity'),
                    String(body || 'El agente terminó de responder.')
                  );
                } catch (_) {}
              };

              // 1. Intercept window.Notification API
              var NativeNotification = function(title, options) {
                var body = (options && options.body) ? String(options.body) : '';
                hasChangedInBg = false;
                emitNative(title, body);
              };
              NativeNotification.prototype.close = function() {};
              Object.defineProperty(NativeNotification, 'permission', {
                get: function() { return 'granted'; }
              });
              NativeNotification.requestPermission = function(cb) {
                if (typeof cb === 'function') { try { cb('granted'); } catch (_) {} }
                return Promise.resolve('granted');
              };
              try { window.Notification = NativeNotification; } catch (_) {}

              // 2. Intercept ServiceWorkerRegistration.showNotification
              if (window.ServiceWorkerRegistration && window.ServiceWorkerRegistration.prototype) {
                window.ServiceWorkerRegistration.prototype.showNotification = function(title, options) {
                  var body = (options && options.body) ? String(options.body) : '';
                  hasChangedInBg = false;
                  emitNative(title, body);
                  return Promise.resolve();
                };
              }

              // 3. Report notifications permission as granted via Permissions API
              if (navigator.permissions && typeof navigator.permissions.query === 'function') {
                var origQuery = navigator.permissions.query.bind(navigator.permissions);
                navigator.permissions.query = function(desc) {
                  if (desc && desc.name === 'notifications') {
                    return Promise.resolve({ state: 'granted', onchange: null, addEventListener: function(){}, removeEventListener: function(){} });
                  }
                  return origQuery(desc);
                };
              }

              // Helper: detect if an agent turn is currently streaming/running
              var isAgentBusy = function() {
                try {
                  var btns = document.querySelectorAll('button, [role="button"]');
                  for (var i = 0; i < btns.length; i++) {
                    var b = btns[i];
                    if (b.disabled) continue;
                    var label = ((b.getAttribute('aria-label') || '') + ' ' + (b.getAttribute('title') || '') + ' ' + (b.textContent || '')).toLowerCase();
                    if (label.indexOf('stop') !== -1 || label.indexOf('detener') !== -1 || label.indexOf('cancel generating') !== -1) {
                      return true;
                    }
                  }
                } catch (_) {}
                return false;
              };

              // Helper: compute a clean signature and preview of the latest conversation text
              var readConversationState = function() {
                try {
                  var raw = (document.body && document.body.innerText) ? document.body.innerText : '';
                  var lines = raw.split('\n');
                  var cleanLines = [];
                  for (var i = 0; i < lines.length; i++) {
                    var ln = lines[i].trim();
                    if (ln.length < 3) continue;
                    if (/^\d{1,2}:\d{2}/.test(ln) || /^\d+s$/.test(ln)) continue;
                    cleanLines.push(ln);
                  }
                  var tail = cleanLines.slice(-12);
                  var sig = cleanLines.length + ':' + tail.join('|').replace(/\d+\s*(s|ms|sec|min)\b/gi, '');
                  var preview = tail.length > 0 ? tail[tail.length - 1] : 'El agente terminó de responder.';
                  if (preview.length > 140) preview = preview.slice(0, 140) + '…';
                  return { sig: sig, preview: preview, busy: isAgentBusy() };
                } catch (_) {
                  return { sig: '', preview: 'El agente terminó de responder.', busy: false };
                }
              };

              // 4. Called from Android native onStart/onStop
              window.__AGY_SET_BG = function(isBg) {
                window.__AGY_BG = !!isBg;
                var st = readConversationState();
                baselineSig = st.sig;
                prevTickSig = st.sig;
                hasChangedInBg = st.busy;
                lastSnippet = st.preview;
                try {
                  Object.defineProperty(document, 'hidden', { configurable: true, get: function() { return !!isBg; } });
                  Object.defineProperty(document, 'visibilityState', { configurable: true, get: function() { return isBg ? 'hidden' : 'visible'; } });
                  document.dispatchEvent(new Event('visibilitychange'));
                } catch (_) {}
              };

              // 5. Pumped every 2.0s by Android native Handler while app is in background
              window.__AGY_BG_TICK = function() {
                if (!window.__AGY_BG) return;
                var st = readConversationState();
                if (st.busy) {
                  hasChangedInBg = true;
                  prevTickSig = st.sig;
                  lastSnippet = st.preview;
                  return;
                }
                if (st.sig !== prevTickSig) {
                  if (st.sig !== baselineSig) {
                    hasChangedInBg = true;
                    lastSnippet = st.preview;
                  }
                  prevTickSig = st.sig;
                  return;
                }
                // Text changed since leaving the app, agent is no longer busy, and text settled across ticks!
                if (hasChangedInBg && st.sig !== baselineSig) {
                  hasChangedInBg = false;
                  baselineSig = st.sig;
                  emitNative('Antigravity', lastSnippet || st.preview);
                }
              };
            }
          } catch (_) {}
        })();
    """
}
