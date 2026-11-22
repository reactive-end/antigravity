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
    """
}
