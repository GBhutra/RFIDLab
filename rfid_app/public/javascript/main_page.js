window.onload = function() {

    var messages = [];
    var socket = io.connect('http://localhost:3700');
    var start = document.getElementById("start");
    var layout = document.getElementById("layout");
    var save = document.getElementById("save");
    var Exit = document.getElementById("exit");

    var content = document.getElementById("disp");

    socket.on('message', function (data) {
        if(data.message) {
            messages.push(data.message);
            var html = '';
            for(var i=0; i<messages.length; i++) {
                html += messages[i] + '<br />';
            }
            content.innerHTML = html;
        } else {
            //console.log("There is a problem:", data);
            if(data=="Change the layout")
               location.reload();
            content.innerHTML = data;
        }

    });

    start.onclick = function() {
        if (start.innerHTML == 'Start') {
            start.innerHTML = 'Stop';
            socket.emit('start','Start the reader');
        }
        else    {
            start.innerHTML = 'Start';
            socket.emit('stop', 'Stop the reader');
        }
    };

    layout.onclick = function() {
        socket.emit('layout','Change the layout');
    };

    save.onclick = function() {
        socket.emit('save','save the file');
    };

    exit.onclick = function() {
        socket.emit('exit', 'Exit the applicaiton');
    };
}
