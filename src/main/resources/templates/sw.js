//IndexedDB
var db = {};
var dataStore = null;

db.open = function (callback) {
    var request = indexedDB.open("pwebDB");
    request.onerror = function(event) {alert("Index DB error: " + event.target.errorCode)};
    request.onsuccess = function(event) {dataStore = request.result; callback();};

    request.onupgradeneeded = function(event) {
        var db = event.target.result;

        // Crea un almacén de objetos (objectStore) para esta base de datos
        var objectStore  = db.createObjectStore("encuestas", { autoIncrement : true });
        objectStore.createIndex("ip", "ip", { unique: false });
        objectStore.createIndex("nivel_escolar", "nivel_escolar", { unique: false });
        objectStore.createIndex("nombre", "nombre", { unique: false });
        objectStore.createIndex("sector", "sector", { unique: false });
        objectStore.createIndex("subido", "subido", { unique: false });
    };
}

db.fetchEncuestas = function(db, callback) {
    var transaction = db.transaction(['encuestas'], 'readwrite');
    var objStore = transaction.objectStore('encuestas');

    var keyRange = IDBKeyRange.lowerBound(0);
    var cursorRequest = objStore.openCursor(keyRange);

    var encuestas = [];

    transaction.oncomplete = function(e) {
        console.log(encuestas)
        // Execute the callback function.
        callback(encuestas);
    };

    cursorRequest.onsuccess = function(e) {
        var result = e.target.result;

        if (!!result == false) {
            return;
        }

        encuestas.push({key: result.primaryKey, value: result.value});
        result.continue();
    };
};

db.deleteEncuesta = function(db, id, callback) {
    var transaction = db.transaction(['encuestas'], 'readwrite');
    var objStore = transaction.objectStore('encuestas');
    var request = objStore.delete(id);

    request.onsuccess = function(e) {callback()}
    request.onerror = function(e) {console.log(e)}
};

//
db.insertEncuesta = function(db, encuesta, callback) {
    var transaction = db.transaction(['encuestas'], 'readwrite');
    var objStore = transaction.objectStore("encuestas");
    if(encuesta.key)
        var request = objStore.put(encuesta, encuesta.key);
    else
        var request = objStore.put(encuesta);
    request.onsuccess = function(e) {callback(encuesta)};
};
//
db.open(refrescarEncuestas)
//
// //Service Worker
self.addEventListener('install', function(e) {
    e.waitUntil(
        caches.open('pweb-v1').then(function(cache) {
            return cache.addAll([
                '/',
                '/encuesta',
                '/index.html',
                '/ver.html',
                '/encuesta.html',
                '/public/css/main.css',
                '/public/js/jquery.min.js',
                '/public/js/jquery.dropotron.min.js',
                '/public/js/browser.min.js',
                '/public/js/breakpoints.min.js',
                '/public/js/util.js',
                '/public/js/main.js',
                'sw.js'
            ]);
        })
    );
});
//
//
self.addEventListener('message', function (event) {
    switch (event.data.tipo) {
        case 'delete':
            db.deleteEncuesta(dataStore, event.data.key, () => refrescarEncuestas)
            break;
        case 'update':
            console.log(event.data.encuesta)
            db.insertEncuesta(dataStore, event.data.encuesta, encuesta => refrescarEncuestas)
            break;
        case 'subir':
            db.deleteEncuesta(dataStore, event.data.key, () => refrescarEncuestas)
            break;
        default:
            console.log('form data', event.data)
            let params = event.data;
            db.insertEncuesta(dataStore, params, encuesta => refrescarEncuestas)
            break;
    }
})

self.addEventListener('fetch', function(event) {
    event.respondWith(
        caches.match(event.request) //To match current request with cached request it
            .then(function(response) {
                //If response found return it, else fetch again.
                return response || fetch(event.request);
            })
            .catch(function(error) {
                console.error("Error: ", error);
            })
    );
});
//
function getParams(url){
    let params = {}
    // var url = event.request.url;
    let dataString = url.split('?')[1]
    // console.log(dataString)
    let paramsString = dataString.split('&')
    // console.log(paramsString)
    paramsString.map(p => {params[p.split('=')[0]] = p.split('=')[1]})
    params["subido"] = 0
    return params;
}
//
// //Refrescar Encuestas
function refrescarEncuestas(){
    db.fetchEncuestas(dataStore, async encuestas => {
        console.log(`${encuestas.length} en la base de datos`)
        var pendientes = document.getElementById('pendientes');
        if(navigator.onLine == true){
            let rowsHtml = ""
            for (let i=0; i<encuestas.length; i++){
                encuestaJSON = JSON.stringify(encuestas[i]).replace(/"/g, "'");
                await $.get("https://api.ipify.org?format=json",function (data) {
                    rowsHtml += `
                    <h5>${i+1}.</h5>
                    <div class="row" style="margin-bottom: 10px;">
                        <input type="text" style="display: none" name = "ip" id="${"ip"+encuestas[i].key}" value="${data.ip}"/>                        
                        <div class="col" style="width: 33%;">
                            <input type="text" name="nombre" id="${'nombre'+encuestas[i].key}" value="${encuestas[i].value.nombre}" placeholder="Nombre">
                        </div>
                            <div class="col" style="width: 33%;">
                            <input type="text" name="sector" id="${'sector'+encuestas[i].key}" value="${encuestas[i].value.sector}" placeholder="Sector">
                        </div>
                            <div class="col" style="width: 33%;">
                            <select name="nivel_escolar" id="${'nivel_escolar'+encuestas[i].key}">
                                <option value="none" ${encuestas[i].value.nivel_escolar === "none" ? "selected" : ""} hidden>Nivel Escolar</option>
                                <option value="Básico" ${encuestas[i].value.nivel_escolar === "Básico" ? "selected" : ""}>Básico</option>
                                <option value="Medio" ${encuestas[i].value.nivel_escolar === "Medio" ? "selected" : ""}>Medio</option>
                                <option value="Universitario" ${encuestas[i].value.nivel_escolar === "Universitario" ? "selected" : ""}>Grado Universitario</option>
                                <option value="Postgrado" ${encuestas[i].value.nivel_escolar === "Postgrado" ? "selected" : ""}>Postgrado</option>
                                <option value="Doctorado" ${encuestas[i].value.nivel_escolar === "Doctorado" ? "selected" : ""}>Doctorado</option>
                            </select>
                        </div>
                    </div>
                    <div class="row" style="border-bottom: 1px;border-style: solid;border-color: #cacaca;">
                        <div class="col" style="width: 33%;"><a onclick="navigator.serviceWorker.controller.postMessage({tipo: 'delete', key: ${encuestas[i].key}});window.location.replace('/')" class="button" style="background-color: #ff0b61"><i class="fas fa-trash"></i> BORRAR</a></div>
                        <div class="col" style="width: 33%;"><a onclick="navigator.serviceWorker.controller.postMessage(
                            {tipo: 'update', encuesta: {key: ${encuestas[i].key}, nombre: document.getElementById('nombre'+${encuestas[i].key}).value, sector: document.getElementById('sector'+${encuestas[i].key}).value, nivel_escolar: document.getElementById('nivel_escolar'+${encuestas[i].key}).value, ip: '${data.ip}'}});window.location.replace('/')" class="button" style="background-color: #0090c5"><i class="fas fa-save"></i> MODIFICAR</a></div>
                        <div class="col" style="width: 33%;"><a onclick="
                        $.post( '/encuesta', {nombre: document.getElementById('nombre'+${encuestas[i].key}).value, sector: document.getElementById('sector'+${encuestas[i].key}).value, nivel_escolar: document.getElementById('nivel_escolar'+${encuestas[i].key}).value, ip: '${data.ip}'}, ()=>{})
                        .done(()=>{navigator.serviceWorker.controller.postMessage({tipo:'subir', key: ${encuestas[i].key}});window.location.replace('/')}).catch(()=>{alert('Error al subir encuesta, revise su conexión')})" class="button" style="background-color: #1b1"><i class="fas fa-upload"></i> SUBIR</a></div>                                                                                    
                    </div>`
                })
            }
            pendientes.innerHTML = `
            <div id="banner" class="box container" style="margin-top: 10px;">
                <div class="row">
                    <p id="pendientes" style="color: red;">${encuestas.length} Encuestas Pendientes</p>
                </div>                
                <form>
                    ${rowsHtml}
                </form>    
            </div>
            `;
        }
    })
}
//
// //Cambio de Conexión
onlineStyle = "width: fit-content;color: white;background-color: green;border-radius: 10px;padding: 5px;"
offlineStyle = "width: fit-content;color: white;background-color: red;border-radius: 10px;padding: 5px;"

self.addEventListener('load', function() {
    var status = document.getElementById("status");

    function updateOnlineStatus(event) {
        var condition = navigator.onLine ? "online" : "offline";
        if(condition == "online"){
            // db.fetchEncuestas(dataStore, encuestas => {
            //     console.log(encuestas)
            //     for(let i=0; i<encuestas.length; i++){
            //         $.get("https://api.ipify.org?format=json",function (data) {
            //             encuestas[i].value.ip = data.ip;
            //             $.post( "/encuesta", encuestas[i].value, function() {})
            //                 .done(function() {alert( "Encuesta subida al servidor!" )})
            //         });
            //     }
            // })
        }
        refrescarEncuestas()
        status.style = condition == "online" ? onlineStyle : offlineStyle
        status.innerHTML = condition.toUpperCase();
    }

    window.addEventListener('online',  updateOnlineStatus);
    window.addEventListener('offline', updateOnlineStatus);
});

// document.getElementById("status").innerHTML = navigator.onLine ? "ONLINE" : "OFFLINE"
// document.getElementById("status").style = navigator.onLine ? onlineStyle : offlineStyle
