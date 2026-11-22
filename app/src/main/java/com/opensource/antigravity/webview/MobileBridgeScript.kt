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
              var wasBusyInBg = false;
              var hasChangedInBg = false;
              var lastSnippet = '';

              // CRITICAL: Never let Antigravity Remote know the tab went to background,
              // otherwise its JS client disconnects from the remote PC instance!
              try {
                Object.defineProperty(document, 'hidden', { configurable: true, get: function() { return false; } });
                Object.defineProperty(document, 'visibilityState', { configurable: true, get: function() { return 'visible'; } });
                Object.defineProperty(document, 'webkitHidden', { configurable: true, get: function() { return false; } });
                document.hasFocus = function() { return true; };

                var blockedEvents = { visibilitychange: true, webkitvisibilitychange: true, pagehide: true, freeze: true, blur: true };
                var origDocAdd = document.addEventListener.bind(document);
                document.addEventListener = function(type, listener, options) {
                  if (blockedEvents[String(type).toLowerCase()]) return;
                  return origDocAdd(type, listener, options);
                };
                var origWinAdd = window.addEventListener.bind(window);
                window.addEventListener = function(type, listener, options) {
                  if (blockedEvents[String(type).toLowerCase()]) return;
                  return origWinAdd(type, listener, options);
                };
                window.addEventListener('visibilitychange', function(e) { e.stopImmediatePropagation(); }, true);
                document.addEventListener('visibilitychange', function(e) { e.stopImmediatePropagation(); }, true);
              } catch (_) {}

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
                wasBusyInBg = false;
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
                  wasBusyInBg = false;
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

              // Helper: detect if the agent is currently working ("Working" label or red stop button)
              var readConversationState = function() {
                try {
                  var raw = (document.body && document.body.innerText) ? document.body.innerText : '';
                  var lines = raw.split('\n');
                  var cleanLines = [];
                  var sawWorkingLine = false;
                  var lostConnection = raw.indexOf('Lost connection to the remote instance') !== -1;

                  for (var i = 0; i < lines.length; i++) {
                    var ln = lines[i].trim();
                    if (!ln) continue;
                    if (ln === 'Working' || ln === 'Thinking' || ln === 'Thinking...' || ln === 'Working...') {
                      sawWorkingLine = true;
                      continue;
                    }
                    if (ln.indexOf('Lost connection to the remote instance') !== -1 ||
                        ln.indexOf('Please wait a minute or refresh') !== -1 ||
                        ln.indexOf('Ask anything, @ to mention') !== -1) {
                      continue;
                    }
                    if (/^\d{1,2}:\d{2}/.test(ln) || /^\d+[smh]\s+ago$/i.test(ln) || ln === 'now') continue;
                    if (ln.length >= 2) cleanLines.push(ln);
                  }

                  var hasStopBtn = false;
                  var btns = document.querySelectorAll('button, [role="button"]');
                  for (var j = 0; j < btns.length; j++) {
                    var b = btns[j];
                    if (b.disabled) continue;
                    var label = ((b.getAttribute('aria-label') || '') + ' ' + (b.getAttribute('title') || '') + ' ' + (b.textContent || '')).toLowerCase();
                    if (label.indexOf('stop') !== -1 || label.indexOf('detener') !== -1 || label.indexOf('cancel') !== -1) {
                      hasStopBtn = true;
                      break;
                    }
                  }

                  var tail = cleanLines.slice(-10);
                  var sig = cleanLines.length + ':' + tail.join('|');
                  var preview = tail.length > 0 ? tail[tail.length - 1] : 'El agente terminó de responder.';
                  if (preview.length > 140) preview = preview.slice(0, 140) + '…';
                  return {
                    sig: sig,
                    preview: preview,
                    busy: sawWorkingLine || hasStopBtn,
                    lostConnection: lostConnection
                  };
                } catch (_) {
                  return { sig: '', preview: 'El agente terminó de responder.', busy: false, lostConnection: false };
                }
              };

              // 4. Called from Android native onStart/onStop
              window.__AGY_SET_BG = function(isBg) {
                window.__AGY_BG = !!isBg;
                var st = readConversationState();
                baselineSig = st.sig;
                prevTickSig = st.sig;
                wasBusyInBg = st.busy;
                hasChangedInBg = false;
                lastSnippet = st.preview;
              };

              // 5. Pumped every 1.5s by Android native Handler while app is in background
              window.__AGY_BG_TICK = function() {
                if (!window.__AGY_BG) return;
                var st = readConversationState();

                if (st.busy) {
                  wasBusyInBg = true;
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

                // Trigger notification when either:
                // A) The agent was in "Working" state while in background and just finished (!st.busy), OR
                // B) New conversation text arrived in background and settled across ticks
                if ((wasBusyInBg || hasChangedInBg) && !st.busy && !st.lostConnection) {
                  if (st.sig !== baselineSig || wasBusyInBg) {
                    wasBusyInBg = false;
                    hasChangedInBg = false;
                    baselineSig = st.sig;
                    emitNative('Antigravity', lastSnippet || st.preview);
                  }
                }
              };
            }
          } catch (_) {}
        })();
    """
}
