package com.opensource.antigravity.webview

object MobileBridgeScript {

    fun build(mobileOptimization: Boolean, notificationsEnabled: Boolean): String {
        val optFlag = if (mobileOptimization) "true" else "false"
        val notifFlag = if (notificationsEnabled) "true" else "false"
        return """
            (function() {
              try {
                window.__ANVIL_MOBILE_OPT = $optFlag;
                window.__ANVIL_NOTIF_ENABLED = $notifFlag;

                if (window.__ANVIL_MOBILE_OPT) {
                  var vp = document.querySelector('meta[name="viewport"]');
                  var content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, viewport-fit=cover';
                  if (!vp) {
                    vp = document.createElement('meta');
                    vp.name = 'viewport';
                    vp.content = content;
                    document.head && document.head.appendChild(vp);
                  } else if (vp.content.indexOf('viewport-fit=cover') === -1) {
                    vp.content = content;
                  }

                  var styleId = 'antigravity-native-mobile-style';
                  if (!document.getElementById(styleId) && document.head) {
                    var style = document.createElement('style');
                    style.id = styleId;
                    style.textContent = [
                      'html, body { overscroll-behavior-y: contain; -webkit-tap-highlight-color: transparent; }',
                      'body { padding-bottom: env(safe-area-inset-bottom, 0px); }',
                      'input, textarea, [contenteditable="true"] { font-size: 16px; }',
                      '::-webkit-scrollbar { width: 4px; height: 4px; }',
                      '::-webkit-scrollbar-thumb { background: rgba(141,152,176,0.35); border-radius: 4px; }'
                    ].join('\n');
                    document.head.appendChild(style);
                  }
                } else {
                  var oldStyle = document.getElementById('antigravity-native-mobile-style');
                  if (oldStyle && oldStyle.parentNode) oldStyle.parentNode.removeChild(oldStyle);
                }

                if (!window.__ANVIL_NOTIF_PATCHED && window.AntigravityNativeBridge) {
                  window.__ANVIL_NOTIF_PATCHED = true;
                  var NativeNotification = function(title, options) {
                    var body = (options && options.body) ? String(options.body) : '';
                    if (window.__ANVIL_NOTIF_ENABLED) {
                      window.AntigravityNativeBridge.notify(String(title || 'Antigravity'), body);
                    }
                  };
                  Object.defineProperty(NativeNotification, 'permission', {
                    get: function() { return window.__ANVIL_NOTIF_ENABLED ? 'granted' : 'denied'; }
                  });
                  NativeNotification.requestPermission = function(cb) {
                    var res = window.__ANVIL_NOTIF_ENABLED ? 'granted' : 'denied';
                    if (typeof cb === 'function') { try { cb(res); } catch (_) {} }
                    return Promise.resolve(res);
                  };
                  try { window.Notification = NativeNotification; } catch (_) {}
                }
              } catch (_) {}
            })();
        """.trimIndent()
    }
}
