self.addEventListener('install', function(e) {
    e.waitUntil(
        caches.open('pweb-v1').then(function(cache) {
            return cache.addAll([
                '/',
                '/encuesta',
                '/index.html',
                '/login.html',
                '/encuesta.html',
                '/public/css/main.css',
                '/public/js/jquery.min.js',
                '/public/js/jquery.dropotron.min.js',
                '/public/js/browser.min.js',
                '/public/js/breakpoints.min.js',
                '/public/js/util.js',
                '/public/js/main.js',
            ]);
        })
    );
});

// self.addEventListener('activate', () => {self.clients.claim(); console.log("ACTIVADO")});

self.addEventListener('fetch', function(event) {
    event.respondWith(
        fetch(event.request) //Trata de cargar online
            .catch(() => caches.match(event.request) //No? busca en cache
                .then(function(response) {return response})
            )
    );
});