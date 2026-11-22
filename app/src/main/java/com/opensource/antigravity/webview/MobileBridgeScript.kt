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
                'html, body { max-width: 100vw; overflow-x: clip; -webkit-tap-highlight-color: transparent; touch-action: manipulation; }',
                'pre, table { max-width: 100%; overflow-x: auto; -webkit-overflow-scrolling: touch; }',
                'code { word-break: break-word; }',
                'pre code { word-break: normal; }',
                'input, textarea, [contenteditable="true"] { font-size: 16px; }',
                '::-webkit-scrollbar { width: 4px; height: 4px; }',
                '::-webkit-scrollbar-thumb { background: rgba(138,143,158,0.32); border-radius: 4px; }'
              ].join('\n');
              document.head.appendChild(style);
            }

            if (!window.__AGY_NOTIF_BRIDGE && window.AntigravityNativeBridge) {
              window.__AGY_NOTIF_BRIDGE = true;
              var lastExplicitNotifMs = 0;

              var emitNative = function(title, body) {
                lastExplicitNotifMs = Date.now();
                try {
                  window.AntigravityNativeBridge.notify(String(title || 'Antigravity'), String(body || ''));
                } catch (_) {}
              };

              // 1. Intercept window.Notification API
              var NativeNotification = function(title, options) {
                var body = (options && options.body) ? String(options.body) : '';
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

              // 2. Intercept ServiceWorkerRegistration.showNotification (used by Google PWAs)
              if (window.ServiceWorkerRegistration && window.ServiceWorkerRegistration.prototype) {
                var origSwShow = window.ServiceWorkerRegistration.prototype.showNotification;
                window.ServiceWorkerRegistration.prototype.showNotification = function(title, options) {
                  var body = (options && options.body) ? String(options.body) : '';
                  emitNative(title, body);
                  if (typeof origSwShow === 'function') {
                    return Promise.resolve();
                  }
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

              // 4. Fallback: when app is in background, catch completion chimes or title alerts
              if (window.HTMLMediaElement && window.HTMLMediaElement.prototype) {
                var origPlay = window.HTMLMediaElement.prototype.play;
                window.HTMLMediaElement.prototype.play = function() {
                  try {
                    if (window.AntigravityNativeBridge.isInBackground() && Date.now() - lastExplicitNotifMs > 3000) {
                      emitNative('Antigravity', 'Una tarea terminó o requiere tu atención.');
                    }
                  } catch (_) {}
                  return origPlay.apply(this, arguments);
                };
              }

              var lastTitle = document.title || '';
              if (typeof MutationObserver === 'function' && document.head) {
                var titleObserver = new MutationObserver(function() {
                  var nextTitle = document.title || '';
                  if (nextTitle && nextTitle !== lastTitle) {
                    lastTitle = nextTitle;
                    try {
                      if (window.AntigravityNativeBridge.isInBackground() &&
                          Date.now() - lastExplicitNotifMs > 3000 &&
                          nextTitle !== 'Google Antigravity' &&
                          nextTitle !== 'Antigravity') {
                        emitNative('Antigravity', nextTitle);
                      }
                    } catch (_) {}
                  }
                });
                titleObserver.observe(document.head, { subtree: true, characterData: true, childList: true });
              }
            }
          } catch (_) {}
        })();
    """
}
