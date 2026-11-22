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
              var NativeNotification = function(title, options) {
                var body = (options && options.body) ? String(options.body) : '';
                window.AntigravityNativeBridge.notify(String(title || 'Antigravity'), body);
              };
              Object.defineProperty(NativeNotification, 'permission', {
                get: function() { return 'granted'; }
              });
              NativeNotification.requestPermission = function(cb) {
                if (typeof cb === 'function') { try { cb('granted'); } catch (_) {} }
                return Promise.resolve('granted');
              };
              try { window.Notification = NativeNotification; } catch (_) {}
            }
          } catch (_) {}
        })();
    """
}
