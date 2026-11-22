package com.opensource.antigravity.webview

object MobileBridgeScript {

    const val MOBILE_POLISH_CSS: String = """
:root, html, body {
  color-scheme: dark;
  --background: #000000;
  --color-background: #000000;
  --content: #000000;
  --focus-ring-offset-color: #000000;
  --onyx-frame: #000000;
  --onyx-well: #131314;
  --sidebar: #0E0E10;
  --color-sidebar: #0E0E10;
  --shell: #131314;
  --card: #1E1F20;
  --color-card: #1E1F20;
  --glass: #1E1F20;
  --onyx-raised: #282A2C;
  --onyx-selected: #333537;
  --onyx-key: #282A2C;
  --muted: rgba(255, 255, 255, 0.06);
  --color-muted: rgba(255, 255, 255, 0.06);
  --border: rgba(255, 255, 255, 0.06);
  --color-border: rgba(255, 255, 255, 0.06);
  --card-border: rgba(255, 255, 255, 0.05);
  --color-card-border: rgba(255, 255, 255, 0.05);
  --primary: #1A73E8;
  --color-primary: #1A73E8;
  --cta: #1A73E8;
  --nux-composer-radius: 28px;
  --nux-composer-frame-radius: 28px;
  --nux-radius-lg: 16px;
  --nux-radius-xl: 24px;
  --nux-radius-2xl: 28px;
  --ga-control-height-xs: 34px;
  --ga-control-height-sm: 40px;
  --ga-control-height-md: 44px;
  --ga-radius-md: 12px;
  --ga-radius-lg: 16px;
  --ga-radius-xl: 24px;
}
*, *::before, *::after {
  backdrop-filter: none !important;
  -webkit-backdrop-filter: none !important;
}
html, body, div.app-container, div.iframe-panel {
  background-color: #000000 !important;
}
html, body {
  color: #E3E3E3;
  max-width: 100vw;
  overflow-x: clip;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
  overscroll-behavior-y: contain;
}
pre, table {
  max-width: 100%;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}
pre {
  background-color: #131314 !important;
  border-radius: 14px !important;
  border: 1px solid rgba(255, 255, 255, 0.06) !important;
}
code {
  word-break: break-word;
}
p code, li code {
  background-color: #1E1F20 !important;
  padding: 2px 6px;
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.06);
}
pre code {
  word-break: normal;
  background: transparent !important;
  border: none !important;
  padding: 0 !important;
}
p, li {
  line-height: 1.6;
}
li + li {
  margin-top: 5px;
}
input, textarea, [contenteditable="true"] {
  font-size: 16px;
}
/* Hide Google Account & Feedback on top host while viewing a conversation */
html.agy-in-conversation button.header-feedback-btn,
html.agy-in-conversation button.header-feedback-btn--fullscreen,
html.agy-in-conversation .boqOnegoogleliteOgbOneGoogleBar,
html.agy-in-conversation .gb_Za {
  display: none !important;
}
/* Gemini-style minimal topbar: Home vs Conversation */
html body div.flex.items-center.justify-between.w-full.pl-2.gap-2.border-b {
  background-color: #000000 !important;
  border-bottom-color: transparent !important;
  min-height: 52px;
}
html body div.flex.items-center.justify-between.w-full.pl-2.gap-2.border-b:has(button[aria-label="Back to home"]) {
  background-color: #0E0F12 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.07) !important;
  min-height: 60px !important;
  padding: 8px 14px !important;
  gap: 12px !important;
}
html body div.flex.items-center.justify-between.w-full.pl-2.gap-2.border-b:has(button[aria-label="Back to home"]) > div.flex.items-center.shrink-0 {
  display: none !important;
}
html body button[aria-label="Back to home"] {
  width: 44px !important;
  height: 44px !important;
  min-width: 44px !important;
  min-height: 44px !important;
  border-radius: 9999px !important;
  background-color: #1E1F20 !important;
  color: #F1F3F4 !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  flex-shrink: 0 !important;
}
html body button[aria-label="Back to home"] svg {
  width: 22px !important;
  height: 22px !important;
}
html body div.relative.w-full.py-1 > button.flex.items-center.justify-between.w-full.rounded-lg {
  background-color: transparent !important;
  border-color: transparent !important;
  border-radius: 9999px !important;
  padding: 8px 10px !important;
  min-height: 40px;
  font-size: 16px;
  font-weight: 500;
  color: #E3E3E3 !important;
}
html body nav[aria-label="Breadcrumb"] {
  font-size: 19px !important;
  font-weight: 500 !important;
  color: #F1F3F4 !important;
  letter-spacing: -0.01em;
  line-height: 1.3;
  padding-left: 4px;
  flex: 1 1 auto;
  max-width: none !important;
}
html body nav[aria-label="Breadcrumb"] * {
  font-size: inherit !important;
  color: inherit !important;
}
html body button.agy-button-icon-touch {
  width: 38px;
  height: 38px;
  min-width: 38px;
  min-height: 38px;
  border-radius: 9999px;
  color: #C4C7C5;
}
html body div.border-b > div.flex.items-center.shrink-0 > button:last-of-type {
  margin-right: 10px;
}
html body button.header-feedback-btn.header-feedback-btn--fullscreen {
  width: 34px;
  height: 34px;
  min-width: 34px;
  min-height: 34px;
  border-radius: 9999px;
  margin-top: -3px;
  transform: translateX(-6px);
  color: #C4C7C5;
}
/* Chat message action buttons & scroll-to-bottom pill */
html body div.flex.shrink-0.items-center {
  gap: 6px;
}
html body button.agy-button.agy-button-icon:not(.agy-button-icon-sm):not(.agy-button-icon-touch) {
  width: 36px;
  height: 36px;
  min-width: 36px;
  min-height: 36px;
  border-radius: 9999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #9AA0A6;
}
html body button.agy-button.agy-button-icon:not(.agy-button-icon-sm):not(.agy-button-icon-touch) svg {
  width: 18px;
  height: 18px;
}
html body button[aria-label="Scroll to Bottom"] {
  width: 40px;
  height: 40px;
  min-width: 40px;
  min-height: 40px;
  border-radius: 9999px;
  background-color: #282A2C !important;
  border: 1px solid rgba(255, 255, 255, 0.10) !important;
  color: #E3E3E3 !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.45);
}
/* Floating Gemini Material 3 pill composer (strips nested p-px wireframe wrapper) */
html body div.surface-composer {
  background: transparent !important;
  padding: 0 2px 10px 2px !important;
  border-radius: 28px !important;
}
html body div.surface-composer-card {
  background-color: #1E1F20 !important;
  border-radius: 28px !important;
  padding: 10px 12px 10px 14px !important;
  border: 1px solid rgba(255, 255, 255, 0.05) !important;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.5) !important;
}
html body div.surface-composer-card [aria-label="Message input"] {
  font-size: 16px !important;
  line-height: 1.5 !important;
  padding: 6px 6px 10px 6px !important;
  color: #E3E3E3 !important;
}
html body div.surface-composer-card button[aria-label="Add context"],
html body div.surface-composer-card button[aria-label="Record voice memo"],
html body div.surface-composer-card button[aria-label*="Agent permissions"] {
  width: 40px;
  height: 40px;
  min-width: 40px;
  min-height: 40px;
  padding: 0;
  border-radius: 9999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #C4C7C5;
  flex-shrink: 0;
}
html body div.surface-composer-card button[aria-label="Add context"] svg,
html body div.surface-composer-card button[aria-label="Record voice memo"] svg,
html body div.surface-composer-card button[aria-label*="Agent permissions"] svg {
  width: 20px;
  height: 20px;
}
/* Attached image thumbnails & compact remove X badge inside composer */
html body div.surface-composer-card img {
  min-width: 64px;
  min-height: 64px;
  max-width: 84px;
  max-height: 84px;
  object-fit: cover;
  border-radius: 12px;
}
html body div.surface-composer-card button[aria-label*="Remove" i],
html body div.surface-composer-card button[aria-label*="Delete" i],
html body div.surface-composer-card button[aria-label*="Close" i],
html body div.surface-composer-card button[aria-label*="Dismiss" i] {
  width: 22px !important;
  height: 22px !important;
  min-width: 22px !important;
  min-height: 22px !important;
  padding: 2px !important;
  border-radius: 9999px !important;
  background-color: rgba(0, 0, 0, 0.78) !important;
  color: #FFFFFF !important;
  border: 1px solid rgba(255, 255, 255, 0.22) !important;
}
html body div.surface-composer-card button[aria-label*="Remove" i] svg,
html body div.surface-composer-card button[aria-label*="Delete" i] svg,
html body div.surface-composer-card button[aria-label*="Close" i] svg,
html body div.surface-composer-card button[aria-label*="Dismiss" i] svg {
  width: 13px !important;
  height: 13px !important;
}
/* Elevated Gemini Material 3 bottom sheets, model picker & context menus */
html body [role="dialog"],
html body [vaul-drawer],
html body [data-vaul-drawer],
html body [role="menu"],
html body [role="listbox"],
html body [data-radix-popper-content-wrapper] > div,
html body div.fixed.inset-x-0.bottom-0 {
  background-color: #1E1F20 !important;
  border-color: rgba(255, 255, 255, 0.08) !important;
  color: #E3E3E3 !important;
}
html body [role="dialog"],
html body [vaul-drawer],
html body [data-vaul-drawer],
html body div.fixed.inset-x-0.bottom-0 {
  border-top-left-radius: 28px !important;
  border-top-right-radius: 28px !important;
  box-shadow: 0 -8px 32px rgba(0, 0, 0, 0.65) !important;
}
html body [role="menu"],
html body [role="listbox"] {
  border-radius: 20px !important;
  padding: 8px !important;
  box-shadow: 0 12px 36px rgba(0, 0, 0, 0.65) !important;
}
html body [role="dialog"] button:not([aria-label*="Close" i]):not([aria-label*="Dismiss" i]),
html body [role="dialog"] [role="menuitem"],
html body [role="dialog"] [role="option"],
html body [role="menu"] [role="menuitem"],
html body [role="menu"] button,
html body [role="listbox"] [role="option"],
html body [role="listbox"] button,
html body div.fixed.inset-x-0.bottom-0 button:not([aria-label*="Close" i]):not([aria-label*="Dismiss" i]) {
  min-height: 48px !important;
  padding: 10px 14px !important;
  border-radius: 14px !important;
  font-size: 15.5px !important;
  gap: 14px !important;
}
html body [role="dialog"] button:not([aria-label*="Close" i]):not([aria-label*="Dismiss" i]) svg,
html body [role="dialog"] [role="menuitem"] svg,
html body [role="dialog"] [role="option"] svg,
html body [role="menu"] svg,
html body [role="listbox"] svg,
html body div.fixed.inset-x-0.bottom-0 button:not([aria-label*="Close" i]):not([aria-label*="Dismiss" i]) svg {
  width: 22px !important;
  height: 22px !important;
  min-width: 22px !important;
  min-height: 22px !important;
  flex-shrink: 0 !important;
}
html body div.surface-composer-card button[aria-label*="Select model"] {
  min-height: 38px;
  height: auto;
  padding: 6px 10px;
  font-size: 13.5px;
  font-weight: 500;
  color: #C4C7C5;
  gap: 5px;
  border-radius: 9999px;
  background-color: rgba(255, 255, 255, 0.04);
  border: none;
  min-width: 0;
  flex-shrink: 1;
  max-width: 48vw;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
html body button[aria-label*="Select project"] {
  min-height: 36px;
  padding: 6px 12px;
  border-radius: 9999px;
  font-size: 13px;
  font-weight: 500;
}
html body button.agy-send-button {
  width: 42px;
  height: 42px;
  min-width: 42px;
  min-height: 42px;
  padding: 0;
  border-radius: 9999px;
  border: none;
  background-color: #1A73E8;
  color: #FFFFFF;
  flex-shrink: 0;
}
html body button.agy-send-button:disabled,
html body button.agy-send-button[aria-disabled="true"],
html body button.agy-send-button.text-muted-foreground {
  background-color: #1B2C48;
  color: #A8C7FA;
}
html body button.agy-send-button svg {
  width: 20px;
  height: 20px;
}
/* Spacious home rows & section headers */
html body div.flex.flex-col.px-2 {
  gap: 8px;
  padding-top: 8px;
  padding-bottom: 6px;
}
html body div.flex.flex-col.px-2 > .agy-button-custom {
  min-height: 48px;
  padding: 12px 16px;
  border-radius: 16px;
  background-color: #131314;
  color: #E3E3E3;
  font-size: 15px;
  font-weight: 500;
  gap: 12px;
}
html body [class*="section-header"] {
  background-color: #000000 !important;
  padding: 12px 12px 8px 16px;
}
html body [class*="section-header"] h2 {
  opacity: 1 !important;
  color: #9AA0A6 !important;
  font-size: 14px;
}
html body button.agy-button-icon-sm,
html body .agy-button-icon-sm {
  width: 36px;
  height: 36px;
  min-width: 36px;
  min-height: 36px;
  border-radius: 9999px;
  background-color: rgba(255, 255, 255, 0.05);
  color: #C4C7C5;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
html body button.agy-button-icon-sm svg,
html body .agy-button-icon-sm svg {
  width: 18px;
  height: 18px;
}
html body .agy-button-custom:not(.agy-button-icon-sm),
html body button.agy-button:not(.agy-button-icon-sm):not(.agy-button-icon-touch) {
  min-height: 40px;
  border-radius: 12px;
}
html body button,
html body [role="button"],
html body .agy-button {
  transition: transform 80ms ease, opacity 80ms ease;
}
html body button:active,
html body [role="button"]:active,
html body .agy-button:active {
  opacity: 0.76;
  transform: scale(0.96);
}
::-webkit-scrollbar {
  width: 3px;
  height: 3px;
}
::-webkit-scrollbar-thumb {
  background: rgba(138, 143, 158, 0.28);
  border-radius: 3px;
}
"""

    val INJECT_SCRIPT: String = run {
        val escapedCss = MOBILE_POLISH_CSS
            .replace("\\", "\\\\")
            .replace("`", "\\`")
            .replace("\${", "\\\${")
        """
        (function() {
          try {
            var ensureViewport = function() {
              try {
                var vpTarget = document.head || document.documentElement;
                if (!vpTarget) return;
                var vp = document.querySelector('meta[name="viewport"]');
                var content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, viewport-fit=cover';
                if (!vp) {
                  vp = document.createElement('meta');
                  vp.name = 'viewport';
                  vp.content = content;
                  vpTarget.appendChild(vp);
                } else if (vp.content.indexOf('viewport-fit=cover') === -1) {
                  vp.content = content;
                }
              } catch (_) {}
            };

            var styleId = 'antigravity-mobile-polish';
            var cssText = `$escapedCss`;
            var upsertPolish = function(targetRoot) {
              if (!targetRoot) return;
              var st = (targetRoot === document.head || targetRoot === document.documentElement)
                ? document.getElementById(styleId)
                : (targetRoot.getElementById ? targetRoot.getElementById(styleId) : targetRoot.querySelector('#' + styleId));
              if (!st) {
                st = document.createElement('style');
                st.id = styleId;
              }
              if (st.textContent !== cssText) {
                st.textContent = cssText;
              }
              if (st.parentNode !== targetRoot || targetRoot.lastElementChild !== st) {
                targetRoot.appendChild(st);
              }
            };
            var isConvUrl = function() {
              try {
                var p = location.pathname || '';
                var s = location.search || '';
                return p.indexOf('/c/') !== -1 || s.indexOf('p=c%2F') !== -1 || s.indexOf('p=c/') !== -1;
              } catch (_) { return false; }
            };
            var syncConversationMode = function() {
              try {
                if (window === window.top && document.querySelector('iframe[src*="usercontent.goog"]')) {
                  if (window.__AGY_IFRAME_CONV_MODE !== undefined) {
                    if (document.documentElement) {
                      document.documentElement.classList.toggle('agy-in-conversation', !!window.__AGY_IFRAME_CONV_MODE);
                    }
                    return;
                  }
                }
                var hasBack = !!document.querySelector('button[aria-label="Back to home"]');
                var inConv = isConvUrl() || hasBack;
                if (document.documentElement) {
                  document.documentElement.classList.toggle('agy-in-conversation', inConv);
                }
                if (window !== window.top && window.top) {
                  try { window.top.postMessage({ __agyCmd: 'CONV_MODE', active: inConv }, '*'); } catch (_) {}
                }
              } catch (_) {}
            };
            var applyAllPolish = function() {
              ensureViewport();
              var rootTarget = document.head || document.documentElement;
              if (rootTarget) upsertPolish(rootTarget);
              syncConversationMode();
              try {
                var allEls = document.getElementsByTagName('*');
                for (var sIdx = 0; sIdx < allEls.length; sIdx++) {
                  if (allEls[sIdx].shadowRoot) upsertPolish(allEls[sIdx].shadowRoot);
                }
              } catch (_) {}
            };
            applyAllPolish();

            if (!window.__AGY_POLISH_OBSERVING) {
              window.__AGY_POLISH_OBSERVING = true;
              var polishScheduled = false;
              var schedulePolish = function() {
                if (polishScheduled) return;
                polishScheduled = true;
                var run = function() {
                  polishScheduled = false;
                  applyAllPolish();
                };
                if (typeof requestAnimationFrame === 'function') {
                  requestAnimationFrame(run);
                } else {
                  setTimeout(run, 16);
                }
              };
              try {
                document.addEventListener('DOMContentLoaded', applyAllPolish, true);
                window.addEventListener('load', applyAllPolish, true);
                window.addEventListener('popstate', function() { setTimeout(applyAllPolish, 60); }, true);
                window.addEventListener('message', function(e) {
                  if (window !== window.top || !e || !e.data || e.data.__agyCmd !== 'CONV_MODE') return;
                  window.__AGY_IFRAME_CONV_MODE = !!e.data.active;
                  if (document.documentElement) {
                    document.documentElement.classList.toggle('agy-in-conversation', !!e.data.active);
                  }
                }, false);
                if (window.history) {
                  var origPush = history.pushState;
                  if (typeof origPush === 'function') {
                    history.pushState = function() {
                      var res = origPush.apply(this, arguments);
                      setTimeout(syncConversationMode, 60);
                      return res;
                    };
                  }
                  var origReplace = history.replaceState;
                  if (typeof origReplace === 'function') {
                    history.replaceState = function() {
                      var res = origReplace.apply(this, arguments);
                      setTimeout(syncConversationMode, 60);
                      return res;
                    };
                  }
                }
                setInterval(syncConversationMode, 800);
              } catch (_) {}
              try {
                var headObserved = null;
                var ensureHeadObserver = function() {
                  if (document.head && headObserved !== document.head && typeof MutationObserver !== 'undefined') {
                    headObserved = document.head;
                    new MutationObserver(function(muts) {
                      for (var m = 0; m < muts.length; m++) {
                        if (muts[m].addedNodes && muts[m].addedNodes.length) {
                          var last = document.head.lastElementChild;
                          if (!last || last.id !== styleId) {
                            schedulePolish();
                            return;
                          }
                        }
                      }
                    }).observe(document.head, { childList: true });
                  }
                };
                ensureHeadObserver();
                var docTarget = document.documentElement || document;
                if (docTarget && typeof MutationObserver !== 'undefined') {
                  var bootObs = new MutationObserver(function() {
                    if (document.head) {
                      ensureHeadObserver();
                      schedulePolish();
                      bootObs.disconnect();
                    }
                  });
                  bootObs.observe(docTarget, { childList: true, subtree: true });
                  setTimeout(function() { try { bootObs.disconnect(); } catch (_) {} }, 10000);
                }
              } catch (_) {}
            }

            if (!window.AntigravityNativeBridge) return;

            var emitNative = function(title, body) {
              try {
                window.AntigravityNativeBridge.notify(
                  String(title || 'Antigravity'),
                  String(body || 'El agente terminó de responder.')
                );
              } catch (_) {}
            };

            if (!window.__AGY_VISIBILITY_LOCKED) {
              window.__AGY_VISIBILITY_LOCKED = true;
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

              var NativeNotification = function(title, options) {
                var body = (options && options.body) ? String(options.body) : '';
                this.title = String(title || 'Antigravity');
                this.body = body;
                this.tag = (options && options.tag) ? String(options.tag) : '';
                this.data = (options && options.data !== undefined) ? options.data : null;
                this.onclick = null;
                this.onclose = null;
                this.onerror = null;
                this.onshow = null;
                window.__AGY_CHANGED_IN_BG = false;
                window.__AGY_WAS_BUSY_IN_BG = false;
                emitNative(this.title, body);
              };
              NativeNotification.prototype.close = function() {};
              NativeNotification.prototype.addEventListener = function() {};
              NativeNotification.prototype.removeEventListener = function() {};
              Object.defineProperty(NativeNotification, 'permission', {
                get: function() { return 'granted'; }
              });
              NativeNotification.requestPermission = function(cb) {
                if (typeof cb === 'function') { try { cb('granted'); } catch (_) {} }
                return Promise.resolve('granted');
              };
              try { window.Notification = NativeNotification; } catch (_) {}

              if (window.ServiceWorkerRegistration && window.ServiceWorkerRegistration.prototype) {
                window.ServiceWorkerRegistration.prototype.showNotification = function(title, options) {
                  var body = (options && options.body) ? String(options.body) : '';
                  window.__AGY_CHANGED_IN_BG = false;
                  window.__AGY_WAS_BUSY_IN_BG = false;
                  emitNative(title, body);
                  return Promise.resolve();
                };
              }

              if (navigator.permissions && typeof navigator.permissions.query === 'function') {
                var origQuery = navigator.permissions.query.bind(navigator.permissions);
                navigator.permissions.query = function(desc) {
                  if (desc && desc.name === 'notifications') {
                    return Promise.resolve({ state: 'granted', onchange: null, addEventListener: function(){}, removeEventListener: function(){} });
                  }
                  return origQuery(desc);
                };
              }
            }

            // Deep walker that traverses both regular DOM and all open Shadow Roots
            var readDeepConversationState = function() {
              try {
                if (!document.body) {
                  return { sig: '', count: 0, preview: 'El agente terminó de responder.', busy: false, lostConnection: false, skip: true };
                }
                if (window === window.top && document.querySelector('iframe[src*="usercontent.goog"]')) {
                  return { sig: '', count: 0, preview: '', busy: false, lostConnection: false, skip: true };
                }
                var chunks = [];
                var sawBusy = false;
                var lostConn = false;

                var walk = function(node) {
                  if (!node) return;
                  if (node.nodeType === 3) {
                    var t = (node.nodeValue || '').trim();
                    if (t) {
                      var low = t.toLowerCase();
                      if (low === 'working' || low === 'thinking' || low === 'working...' || low === 'thinking...' || low === 'generating' || low === 'generating...') {
                        sawBusy = true;
                      } else if (t.indexOf('Lost connection to the remote instance') !== -1) {
                        lostConn = true;
                      } else if (t.length >= 2 &&
                                 !/^\d{1,2}:\d{2}/.test(t) &&
                                 !/^\d+[smhd]\s+ago$/i.test(t) &&
                                 !/^\d+\s+(sec|second|min|minute|hr|hour|day)s?\s+ago$/i.test(t) &&
                                 !/^\d+[smh]$/i.test(t) &&
                                 low !== 'now' &&
                                 low !== 'just now' &&
                                 low !== 'a moment ago' &&
                                 low !== 'feedback' &&
                                 t.indexOf('Ask anything, @ to mention') === -1) {
                        chunks.push(t);
                      }
                    }
                    return;
                  }
                  if (node.nodeType !== 1 && node.nodeType !== 11) return;
                  var tag = (node.tagName || '').toUpperCase();
                  if (tag === 'SCRIPT' || tag === 'STYLE' || tag === 'NOSCRIPT' || tag === 'SVG' || tag === 'HEADER' || tag === 'NAV' || tag === 'FOOTER') return;
                  if (node.getAttribute) {
                    var role = node.getAttribute('role');
                    if (role === 'dialog' || role === 'menu' || role === 'listbox') return;
                    var al = node.getAttribute('aria-label') || '';
                    if (al === 'Message input') return;
                  }
                  var cn = (typeof node.className === 'string') ? node.className : '';
                  if (cn.indexOf('surface-composer') !== -1 || cn.indexOf('boqOnegoogleliteOgbOneGoogleBar') !== -1) return;

                  if (tag === 'BUTTON' || (node.getAttribute && node.getAttribute('role') === 'button')) {
                    var lbl = (((node.getAttribute && node.getAttribute('aria-label')) || '') + ' ' +
                               ((node.getAttribute && node.getAttribute('title')) || '')).toLowerCase();
                    if (lbl.indexOf('stop') !== -1 || lbl.indexOf('detener') !== -1 || lbl.indexOf('cancel') !== -1) {
                      sawBusy = true;
                    }
                  }

                  if (node.shadowRoot) {
                    walk(node.shadowRoot);
                  }
                  var children = node.childNodes;
                  if (children) {
                    for (var i = 0; i < children.length; i++) {
                      walk(children[i]);
                    }
                  }
                };

                walk(document.body);

                var tail = chunks.slice(-14);
                var sig = chunks.length + ':' + tail.join('|');
                var preview = 'El agente terminó de responder.';
                for (var k = tail.length - 1; k >= 0; k--) {
                  var candidate = tail[k];
                  if (candidate.length > 4 &&
                      candidate !== 'Review' &&
                      candidate.indexOf('Gemini ') !== 0 &&
                      candidate.indexOf('files changed') === -1) {
                    preview = candidate;
                    break;
                  }
                }
                if (preview.length > 140) preview = preview.slice(0, 140) + '…';
                return {
                  sig: sig,
                  count: chunks.length,
                  preview: preview,
                  busy: sawBusy,
                  lostConnection: lostConn,
                  skip: false
                };
              } catch (_) {
                return { sig: '', count: 0, preview: 'El agente terminó de responder.', busy: false, lostConnection: false, skip: true };
              }
            };

            var dispatchBgToFrames = function(payload) {
              try {
                if (window === window.top && window.frames) {
                  for (var fi = 0; fi < window.frames.length; fi++) {
                    try { window.frames[fi].postMessage(payload, '*'); } catch (_) {}
                  }
                }
              } catch (_) {}
            };

            if (!window.__AGY_MSG_BRIDGE_BOUND) {
              window.__AGY_MSG_BRIDGE_BOUND = true;
              try {
                window.addEventListener('message', function(e) {
                  if (!e || !e.data || !e.data.__agyCmd) return;
                  if (e.data.__agyCmd === 'CONV_MODE' && window === window.top) {
                    if (document.documentElement) {
                      document.documentElement.classList.toggle('agy-in-conversation', !!e.data.active);
                    }
                    return;
                  }
                  if (window === window.top) return;
                  if (e.data.__agyCmd === 'SET_BG' && window.__AGY_SET_BG) {
                    window.__AGY_SET_BG(e.data.val);
                  } else if (e.data.__agyCmd === 'BG_TICK' && window.__AGY_BG_TICK) {
                    window.__AGY_BG_TICK();
                  }
                }, false);
              } catch (_) {}
            }

            window.__AGY_SET_BG = function(isBg) {
              window.__AGY_BG = !!isBg;
              dispatchBgToFrames({ __agyCmd: 'SET_BG', val: !!isBg });
              var st = readDeepConversationState();
              window.__AGY_BASE_SIG = st.sig;
              window.__AGY_BASE_COUNT = st.count;
              window.__AGY_PREV_SIG = st.sig;
              window.__AGY_WAS_BUSY_IN_BG = st.busy;
              window.__AGY_CHANGED_IN_BG = false;
              window.__AGY_LAST_SNIPPET = st.preview;
            };

            window.__AGY_BG_TICK = function() {
              dispatchBgToFrames({ __agyCmd: 'BG_TICK' });
              if (!window.__AGY_BG) return;
              var st = readDeepConversationState();
              if (st.skip) return;

              if (st.busy) {
                window.__AGY_WAS_BUSY_IN_BG = true;
                window.__AGY_PREV_SIG = st.sig;
                window.__AGY_LAST_SNIPPET = st.preview;
                return;
              }

              if (st.sig !== window.__AGY_PREV_SIG) {
                if (st.count > (window.__AGY_BASE_COUNT || 0) && st.sig !== window.__AGY_BASE_SIG) {
                  window.__AGY_CHANGED_IN_BG = true;
                  window.__AGY_LAST_SNIPPET = st.preview;
                }
                window.__AGY_PREV_SIG = st.sig;
                return;
              }

              if ((window.__AGY_WAS_BUSY_IN_BG || window.__AGY_CHANGED_IN_BG) && !st.busy && !st.lostConnection) {
                if (st.count > (window.__AGY_BASE_COUNT || 0) || window.__AGY_WAS_BUSY_IN_BG) {
                  window.__AGY_WAS_BUSY_IN_BG = false;
                  window.__AGY_CHANGED_IN_BG = false;
                  window.__AGY_BASE_SIG = st.sig;
                  window.__AGY_BASE_COUNT = st.count;
                  emitNative('Antigravity', window.__AGY_LAST_SNIPPET || st.preview);
                }
              }
            };
          } catch (_) {}
        })();
        """.trimIndent()
    }
}
