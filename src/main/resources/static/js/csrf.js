(function () {
    if (!window.__SCA_CSRF_SETUP) {
        const tokenMeta = document.querySelector('meta[name="_csrf"]');
        const headerMeta = document.querySelector('meta[name="_csrf_header"]');
        window.csrfToken = tokenMeta ? tokenMeta.getAttribute('content') : null;
        window.csrfHeader = headerMeta ? headerMeta.getAttribute('content') : 'X-CSRF-TOKEN';
        window.fetchWithCSRF = function (url, opts = {}) {
            opts.headers = opts.headers || {};
            if (window.csrfToken) opts.headers[window.csrfHeader] = window.csrfToken;
            return fetch(url, opts);
        };
        window.__SCA_CSRF_SETUP = true;
    }
})();
