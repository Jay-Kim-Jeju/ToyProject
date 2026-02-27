<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<style>
  .locale-switcher {
    position: fixed;
    top: 10px;
    right: 10px;
    z-index: 2147483000;
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 3px;
    border: 1px solid #d0d5dd;
    border-radius: 14px;
    background: #ffffff;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.12);
    font-size: 11px;
    line-height: 1;
  }

  .locale-switcher button {
    min-width: 34px;
    height: 22px;
    padding: 0 8px;
    border: 0;
    border-radius: 11px;
    background: transparent;
    color: #5f6672;
    cursor: pointer;
    font-weight: 700;
  }

  .locale-switcher button.is-active {
    background: #263238;
    color: #ffffff;
  }
</style>

<script>
  (function() {
    var currentLang = "<c:out value='${pageContext.request.locale.language}'/>";
    var storageKey = "toy_admin_lang";

    function normalizeLang(v) {
      if (!v) return "";
      v = String(v).toLowerCase();
      if (v.indexOf("en") === 0) return "en";
      if (v.indexOf("ko") === 0) return "ko";
      return "";
    }

    function resolveCurrentLang() {
      var fromQuery = "";
      try {
        fromQuery = normalizeLang(new URL(window.location.href).searchParams.get("lang"));
      } catch (e) {}
      if (fromQuery) return fromQuery;

      var fromStorage = normalizeLang(sessionStorage.getItem(storageKey));
      if (fromStorage) return fromStorage;

      var fromServer = normalizeLang(currentLang);
      return fromServer || "ko";
    }

    function isPopupPage() {
      return !!document.querySelector(".win-popup");
    }

    function buildButton(lang, text, active) {
      var btn = document.createElement("button");
      btn.type = "button";
      btn.setAttribute("data-lang", lang);
      btn.textContent = text;
      btn.setAttribute("aria-label", "Switch language to " + text);
      if (active) {
        btn.className = "is-active";
      }
      return btn;
    }

    function switchLang(nextLang) {
      sessionStorage.setItem(storageKey, nextLang);
      var url = new URL(window.location.href);
      url.searchParams.set("lang", nextLang);
      window.location.href = url.toString();
    }

    document.addEventListener("DOMContentLoaded", function() {
      if (!document.body || isPopupPage()) {
        return;
      }

      var normalized = resolveCurrentLang();

      var root = document.createElement("div");
      root.className = "locale-switcher";

      var koBtn = buildButton("ko", "KO", normalized === "ko");
      var enBtn = buildButton("en", "EN", normalized === "en");

      koBtn.addEventListener("click", function() {
        switchLang("ko");
      });
      enBtn.addEventListener("click", function() {
        switchLang("en");
      });

      root.appendChild(koBtn);
      root.appendChild(enBtn);
      document.body.appendChild(root);
    });
  })();
</script>
