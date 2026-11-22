package com.opensource.antigravity.webview

object MobileBridgeScript {

    const val MOBILE_POLISH_CSS: String = """
:root, html, body {
  color-scheme: dark;
  --background: #050507;
  --onyx-frame: #0A0A0E;
  --onyx-well: #101015;
  --card: #14141A;
  --onyx-raised: #181821;
  --onyx-selected: #21222B;
  --onyx-key: #272933;
  --border: rgba(255, 255, 255, 0.08);
  --card-border: rgba(255, 255, 255, 0.13);
  --ga-control-height-xs: 34px;
  --ga-control-height-sm: 40px;
  --ga-control-height-md: 44px;
  --ga-radius-md: 10px;
  --ga-radius-lg: 12px;
  --ga-radius-xl: 14px;
}
*, *::before, *::after {
  backdrop-filter: none !important;
  -webkit-backdrop-filter: none !important;
}
html, body {
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
code {
  word-break: break-word;
}
p code, li code {
  padding: 2px 6px;
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}
pre code {
  word-break: normal;
}
p, li {
  line-height: 1.55;
}
li + li {
  margin-top: 4px;
}
input, textarea, [contenteditable="true"] {
  font-size: 16px;
}
/* Comfortable touch targets for tiny Antigravity icon buttons (20-28px -> 38-40px) */
html body button.agy-button-icon-sm,
html body .agy-button-icon-sm,
html body button.header-feedback-btn,
html body button[aria-label="Add context"],
html body button[aria-label*="Agent permissions"],
html body button.agy-send-button,
html body button.w-7.h-7,
html body button.w-6.h-6,
html body button.p-1\.5.rounded-full,
html body button.p-1.rounded-full {
  min-width: 40px;
  min-height: 40px;
  width: 40px;
  height: 40px;
  padding: 8px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
html body button.agy-button-icon-sm,
html body .agy-button-icon-sm,
html body button.header-feedback-btn {
  min-width: 38px;
  width: 38px;
}
html body button.agy-button-icon-sm svg,
html body .agy-button-icon-sm svg,
html body button.header-feedback-btn svg,
html body button[aria-label="Add context"] svg,
html body button[aria-label*="Agent permissions"] svg,
html body button.agy-send-button svg,
html body button.w-7.h-7 svg,
html body button.w-6.h-6 svg,
html body button.p-1\.5.rounded-full svg,
html body button.p-1.rounded-full svg,
html body button.agy-button-icon-sm .mat-icon,
html body button[aria-label="Add context"] .mat-icon {
  width: 20px;
  height: 20px;
  font-size: 20px;
}
/* High-contrast visible send/stop and action capsules */
html body button.agy-send-button {
  border: 1px solid rgba(255, 255, 255, 0.14);
}
html body button[aria-label*="Select model"] {
  min-height: 40px;
  height: auto;
  padding: 6px 12px;
  font-size: 14px;
  gap: 6px;
  border-radius: 9999px;
  background-color: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.10);
  min-width: 0;
  flex-shrink: 1;
  max-width: 44vw;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
html body .agy-button-custom,
html body a.agy-button-custom,
html body button.agy-button:not(.agy-button-icon-sm) {
  min-height: 40px;
  padding-left: 12px;
  padding-right: 12px;
  border-radius: 10px;
}
html body a.gb_C {
  width: 36px;
  height: 36px;
  min-width: 36px;
  min-height: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
/* Header icon spacing on narrow mobile viewports */
html body header button,
html body header a[role="button"] {
  min-height: 38px;
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
            var cssText = `$escapedCss`;
            var upsertPolish = function(targetRoot) {
              if (!targetRoot) return;
              var st = targetRoot.getElementById ? targetRoot.getElementById(styleId) : targetRoot.querySelector('#' + styleId);
              if (!st) {
                st = document.createElement('style');
                st.id = styleId;
              }
              if (st.textContent !== cssText) {
                st.textContent = cssText;
              }
              targetRoot.appendChild(st);
            };
            if (document.head) upsertPolish(document.head);
            try {
              var allEls = document.getElementsByTagName('*');
              for (var sIdx = 0; sIdx < allEls.length; sIdx++) {
                if (allEls[sIdx].shadowRoot) upsertPolish(allEls[sIdx].shadowRoot);
              }
            } catch (_) {}

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
                window.__AGY_CHANGED_IN_BG = false;
                window.__AGY_WAS_BUSY_IN_BG = false;
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
                var chunks = [];
                var sawBusy = false;
                var lostConn = false;

                var walk = function(node) {
                  if (!node) return;
                  if (node.nodeType === 3) {
                    var t = (node.nodeValue || '').trim();
                    if (t) {
                      if (t === 'Working' || t === 'Thinking' || t === 'Working...' || t === 'Thinking...') {
                        sawBusy = true;
                      } else if (t.indexOf('Lost connection to the remote instance') !== -1) {
                        lostConn = true;
                      } else if (t.length >= 2 &&
                                 !/^\d{1,2}:\d{2}/.test(t) &&
                                 !/^\d+[smh]\s+ago$/i.test(t) &&
                                 t !== 'now' &&
                                 t.indexOf('Ask anything, @ to mention') === -1) {
                        chunks.push(t);
                      }
                    }
                    return;
                  }
                  if (node.nodeType !== 1 && node.nodeType !== 11) return;
                  var tag = (node.tagName || '').toUpperCase();
                  if (tag === 'SCRIPT' || tag === 'STYLE' || tag === 'NOSCRIPT' || tag === 'SVG') return;

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
                  preview: preview,
                  busy: sawBusy,
                  lostConnection: lostConn
                };
              } catch (_) {
                return { sig: '', preview: 'El agente terminó de responder.', busy: false, lostConnection: false };
              }
            };

            window.__AGY_SET_BG = function(isBg) {
              window.__AGY_BG = !!isBg;
              var st = readDeepConversationState();
              window.__AGY_BASE_SIG = st.sig;
              window.__AGY_PREV_SIG = st.sig;
              window.__AGY_WAS_BUSY_IN_BG = st.busy;
              window.__AGY_CHANGED_IN_BG = false;
              window.__AGY_LAST_SNIPPET = st.preview;
            };

            window.__AGY_BG_TICK = function() {
              if (!window.__AGY_BG) return;
              var st = readDeepConversationState();

              if (st.busy) {
                window.__AGY_WAS_BUSY_IN_BG = true;
                window.__AGY_PREV_SIG = st.sig;
                window.__AGY_LAST_SNIPPET = st.preview;
                return;
              }

              if (st.sig !== window.__AGY_PREV_SIG) {
                if (st.sig !== window.__AGY_BASE_SIG) {
                  window.__AGY_CHANGED_IN_BG = true;
                  window.__AGY_LAST_SNIPPET = st.preview;
                }
                window.__AGY_PREV_SIG = st.sig;
                return;
              }

              if ((window.__AGY_WAS_BUSY_IN_BG || window.__AGY_CHANGED_IN_BG) && !st.busy && !st.lostConnection) {
                if (st.sig !== window.__AGY_BASE_SIG || window.__AGY_WAS_BUSY_IN_BG) {
                  window.__AGY_WAS_BUSY_IN_BG = false;
                  window.__AGY_CHANGED_IN_BG = false;
                  window.__AGY_BASE_SIG = st.sig;
                  emitNative('Antigravity', window.__AGY_LAST_SNIPPET || st.preview);
                }
              }
            };
          } catch (_) {}
        })();
        """.trimIndent()
    }
}
